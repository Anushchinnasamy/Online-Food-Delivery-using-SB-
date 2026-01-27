package com.fooddelivery.auth.service;

import com.fooddelivery.auth.dto.request.LoginRequestDTO;
import com.fooddelivery.auth.dto.request.RegisterRequestDTO;
import com.fooddelivery.auth.dto.response.AuthResponseDTO;
import com.fooddelivery.auth.dto.response.UserResponseDTO;
import com.fooddelivery.auth.entity.RefreshToken;
import com.fooddelivery.auth.entity.User;
import com.fooddelivery.auth.mapper.UserMapper;
import com.fooddelivery.auth.repository.RefreshTokenRepository;
import com.fooddelivery.auth.repository.UserRepository;
import com.fooddelivery.auth.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service for handling authentication operations
 */
@Service
@Transactional
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int LOCK_TIME_MINUTES = 30;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserMapper userMapper;

    /**
     * Register a new user
     */
    public AuthResponseDTO register(RegisterRequestDTO request) {
        logger.info("Registering new user with email: {}", request.getEmail());

        // Check if user already exists
        if (userRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        if (userRepository.existsByPhone(request.getPhone())) {
            throw new RuntimeException("Phone number already exists");
        }

        // Create new user
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail().toLowerCase());
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setIsActive(true);
        user.setIsVerified(false);

        // Save user
        user = userRepository.save(user);
        logger.info("User registered successfully with ID: {}", user.getId());

        // Generate tokens
        String accessToken = jwtUtil.generateToken(user);
        String refreshTokenString = jwtUtil.generateRefreshToken(user);

        // Save refresh token
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(refreshTokenString);
        refreshToken.setUser(user);
        refreshToken.setExpiresAt(LocalDateTime.now().plusDays(7));
        refreshToken.setDeviceInfo(request.getDeviceInfo());
        refreshToken.setIpAddress(request.getIpAddress());
        refreshTokenRepository.save(refreshToken);

        // Create response
        UserResponseDTO userDTO = userMapper.toResponseDTO(user);
        return new AuthResponseDTO(accessToken, refreshTokenString, jwtUtil.getExpirationTime(), userDTO);
    }

    /**
     * Authenticate user and generate tokens
     */
    public AuthResponseDTO login(LoginRequestDTO request) {
        logger.info("Authenticating user with email: {}", request.getEmail());

        try {
            // Find user
            User user = userRepository.findActiveUserByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

            // Check if account is locked
            if (user.isAccountLocked()) {
                throw new RuntimeException("Account is temporarily locked due to multiple failed login attempts");
            }

            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getEmail().toLowerCase(),
                    request.getPassword()
                )
            );

            // Reset failed login attempts on successful login
            user.resetFailedLoginAttempts();
            user.setLastLoginAt(LocalDateTime.now());
            userRepository.save(user);

            logger.info("User authenticated successfully: {}", user.getEmail());

            // Generate tokens
            String accessToken = jwtUtil.generateToken(user);
            String refreshTokenString = jwtUtil.generateRefreshToken(user);

            // Save refresh token
            RefreshToken refreshToken = new RefreshToken();
            refreshToken.setToken(refreshTokenString);
            refreshToken.setUser(user);
            refreshToken.setExpiresAt(LocalDateTime.now().plusDays(7));
            refreshToken.setDeviceInfo(request.getDeviceInfo());
            refreshToken.setIpAddress(request.getIpAddress());
            refreshTokenRepository.save(refreshToken);

            // Create response
            UserResponseDTO userDTO = userMapper.toResponseDTO(user);
            return new AuthResponseDTO(accessToken, refreshTokenString, jwtUtil.getExpirationTime(), userDTO);

        } catch (BadCredentialsException e) {
            // Handle failed login attempt
            handleFailedLogin(request.getEmail());
            logger.warn("Authentication failed for email: {}", request.getEmail());
            throw new RuntimeException("Invalid email or password");
        }
    }

    /**
     * Refresh access token using refresh token
     */
    public AuthResponseDTO refreshToken(String refreshTokenString) {
        logger.info("Refreshing access token");

        try {
            // Validate refresh token
            if (!jwtUtil.validateToken(refreshTokenString) || !jwtUtil.isRefreshToken(refreshTokenString)) {
                throw new RuntimeException("Invalid refresh token");
            }

            // Find refresh token in database
            RefreshToken refreshToken = refreshTokenRepository.findValidToken(refreshTokenString, LocalDateTime.now())
                .orElseThrow(() -> new RuntimeException("Refresh token not found or expired"));

            User user = refreshToken.getUser();

            // Check if user is still active
            if (!user.getIsActive() || user.isDeleted()) {
                throw new RuntimeException("User account is not active");
            }

            // Mark old refresh token as used
            refreshToken.markAsUsed();
            refreshTokenRepository.save(refreshToken);

            // Generate new tokens
            String newAccessToken = jwtUtil.generateToken(user);
            String newRefreshTokenString = jwtUtil.generateRefreshToken(user);

            // Save new refresh token
            RefreshToken newRefreshToken = new RefreshToken();
            newRefreshToken.setToken(newRefreshTokenString);
            newRefreshToken.setUser(user);
            newRefreshToken.setExpiresAt(LocalDateTime.now().plusDays(7));
            newRefreshToken.setDeviceInfo(refreshToken.getDeviceInfo());
            newRefreshToken.setIpAddress(refreshToken.getIpAddress());
            refreshTokenRepository.save(newRefreshToken);

            UserResponseDTO userDTO = userMapper.toResponseDTO(user);
            return new AuthResponseDTO(newAccessToken, newRefreshTokenString, jwtUtil.getExpirationTime(), userDTO);

        } catch (Exception e) {
            logger.error("Token refresh failed: {}", e.getMessage());
            throw new RuntimeException("Token refresh failed");
        }
    }

    /**
     * Validate token and return user details
     */
    @Transactional(readOnly = true)
    public UserResponseDTO validateToken(String token) {
        try {
            if (!jwtUtil.validateToken(token)) {
                throw new RuntimeException("Invalid token");
            }

            String username = jwtUtil.extractUsername(token);
            User user = userRepository.findActiveUserByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

            return userMapper.toResponseDTO(user);

        } catch (Exception e) {
            logger.error("Token validation failed: {}", e.getMessage());
            throw new RuntimeException("Token validation failed");
        }
    }

    /**
     * Logout user by revoking refresh token
     */
    public void logout(String refreshTokenString) {
        try {
            refreshTokenRepository.revokeToken(refreshTokenString);
            logger.info("User logged out successfully");
        } catch (Exception e) {
            logger.error("Logout failed: {}", e.getMessage());
        }
    }

    /**
     * Logout user from all devices
     */
    public void logoutFromAllDevices(Long userId) {
        try {
            refreshTokenRepository.revokeAllTokensForUserId(userId);
            logger.info("User logged out from all devices: {}", userId);
        } catch (Exception e) {
            logger.error("Logout from all devices failed: {}", e.getMessage());
        }
    }

    /**
     * Change user password
     */
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        logger.info("Changing password for user ID: {}", userId);

        User user = userRepository.findById(userId)
            .filter(u -> u.getIsActive() && !u.isDeleted())
            .orElseThrow(() -> new RuntimeException("User not found"));

        // Verify current password
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordChangedAt(LocalDateTime.now());
        userRepository.save(user);

        // Revoke all refresh tokens to force re-login
        refreshTokenRepository.revokeAllTokensForUser(user);

        logger.info("Password changed successfully for user ID: {}", userId);
    }

    /**
     * Handle failed login attempt
     */
    private void handleFailedLogin(String email) {
        try {
            User user = userRepository.findByEmailIgnoreCase(email).orElse(null);
            if (user != null) {
                user.incrementFailedLoginAttempts();
                
                if (user.getFailedLoginAttempts() >= MAX_FAILED_ATTEMPTS) {
                    user.lockAccount(LOCK_TIME_MINUTES);
                    logger.warn("Account locked for user: {} due to {} failed attempts", email, MAX_FAILED_ATTEMPTS);
                }
                
                userRepository.save(user);
            }
        } catch (Exception e) {
            logger.error("Error handling failed login for email: {}", email, e);
        }
    }

    /**
     * Clean up expired refresh tokens
     */
    @Transactional
    public void cleanupExpiredTokens() {
        try {
            refreshTokenRepository.deleteExpiredTokens(LocalDateTime.now());
            refreshTokenRepository.deleteOldRevokedTokens(LocalDateTime.now().minusDays(30));
            logger.info("Expired tokens cleaned up successfully");
        } catch (Exception e) {
            logger.error("Error cleaning up expired tokens", e);
        }
    }
}
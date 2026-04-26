package com.fooddelivery.service;

import com.fooddelivery.dto.AuthRequest;
import com.fooddelivery.dto.AuthResponse;
import com.fooddelivery.dto.RegisterRequest;
import com.fooddelivery.dto.UserDTO;
import com.fooddelivery.entity.User;
import com.fooddelivery.entity.UserRole;
import com.fooddelivery.repository.UserRepository;
import com.fooddelivery.security.CustomUserDetails;
import com.fooddelivery.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for handling authentication operations
 */
@Service
@Transactional
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Register a new user
     */
    public AuthResponse register(RegisterRequest request) {
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
        user.setAddress(request.getAddress());
        user.setCity(request.getCity());
        user.setPincode(request.getPincode());
        user.setLatitude(request.getLatitude());
        user.setLongitude(request.getLongitude());
        user.setIsActive(true);
        user.setIsVerified(false);

        // Save user
        user = userRepository.save(user);
        logger.info("User registered successfully with ID: {}", user.getId());

        // Generate tokens
        CustomUserDetails userDetails = new CustomUserDetails(user);
        String accessToken = jwtUtil.generateToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        // Create response
        UserDTO userDTO = convertToDTO(user);
        return new AuthResponse(accessToken, refreshToken, jwtUtil.getExpirationTime(), userDTO);
    }

    /**
     * Authenticate user and generate tokens
     */
    public AuthResponse login(AuthRequest request) {
        logger.info("Authenticating user with email: {}", request.getEmail());

        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getEmail().toLowerCase(),
                    request.getPassword()
                )
            );

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            User user = userDetails.getUser();

            logger.info("User authenticated successfully: {}", user.getEmail());

            // Generate tokens
            String accessToken = jwtUtil.generateToken(userDetails);
            String refreshToken = jwtUtil.generateRefreshToken(userDetails);

            // Create response
            UserDTO userDTO = convertToDTO(user);
            return new AuthResponse(accessToken, refreshToken, jwtUtil.getExpirationTime(), userDTO);

        } catch (BadCredentialsException e) {
            logger.warn("Authentication failed for email: {}", request.getEmail());
            throw new RuntimeException("Invalid email or password");
        }
    }

    /**
     * Refresh access token using refresh token
     */
    public AuthResponse refreshToken(String refreshToken) {
        logger.info("Refreshing access token");

        try {
            if (!jwtUtil.validateToken(refreshToken) || !jwtUtil.isRefreshToken(refreshToken)) {
                throw new RuntimeException("Invalid refresh token");
            }

            String username = jwtUtil.extractUsername(refreshToken);
            User user = userRepository.findActiveUserByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

            CustomUserDetails userDetails = new CustomUserDetails(user);
            String newAccessToken = jwtUtil.generateToken(userDetails);
            String newRefreshToken = jwtUtil.generateRefreshToken(userDetails);

            UserDTO userDTO = convertToDTO(user);
            return new AuthResponse(newAccessToken, newRefreshToken, jwtUtil.getExpirationTime(), userDTO);

        } catch (Exception e) {
            logger.error("Token refresh failed: {}", e.getMessage());
            throw new RuntimeException("Token refresh failed");
        }
    }

    /**
     * Validate token and return user details
     */
    @Transactional(readOnly = true)
    public UserDTO validateToken(String token) {
        try {
            if (!jwtUtil.validateToken(token)) {
                throw new RuntimeException("Invalid token");
            }

            String username = jwtUtil.extractUsername(token);
            User user = userRepository.findActiveUserByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

            return convertToDTO(user);

        } catch (Exception e) {
            logger.error("Token validation failed: {}", e.getMessage());
            throw new RuntimeException("Token validation failed");
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
        userRepository.save(user);

        logger.info("Password changed successfully for user ID: {}", userId);
    }

    /**
     * Reset password (simplified - in real app would involve email verification)
     */
    public void resetPassword(String email, String newPassword) {
        logger.info("Resetting password for email: {}", email);

        User user = userRepository.findActiveUserByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        logger.info("Password reset successfully for email: {}", email);
    }

    /**
     * Verify user account (simplified)
     */
    public void verifyAccount(Long userId) {
        logger.info("Verifying account for user ID: {}", userId);

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        user.setIsVerified(true);
        userRepository.save(user);

        logger.info("Account verified successfully for user ID: {}", userId);
    }

    /**
     * Convert User entity to UserDTO
     */
    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setRole(user.getRole());
        dto.setIsActive(user.getIsActive());
        dto.setIsVerified(user.getIsVerified());
        dto.setAddress(user.getAddress());
        dto.setCity(user.getCity());
        dto.setPincode(user.getPincode());
        dto.setLatitude(user.getLatitude());
        dto.setLongitude(user.getLongitude());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }
}
package com.fooddelivery.controller;

import com.fooddelivery.dto.AuthRequest;
import com.fooddelivery.dto.AuthResponse;
import com.fooddelivery.dto.RegisterRequest;
import com.fooddelivery.dto.UserDTO;
import com.fooddelivery.entity.User;
import com.fooddelivery.entity.UserRole;
import com.fooddelivery.repository.UserRepository;
import com.fooddelivery.service.AuthService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST controller for authentication operations
 */
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Register a new user
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            logger.info("Registration request received for email: {}", request.getEmail());
            AuthResponse response = authService.register(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Registration failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Authenticate user (login)
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequest request) {
        try {
            logger.info("Login request received for email: {}", request.getEmail());
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Login failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Refresh access token
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
        try {
            String refreshToken = request.get("refreshToken");
            if (refreshToken == null || refreshToken.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Refresh token is required"));
            }

            AuthResponse response = authService.refreshToken(refreshToken);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Token refresh failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Validate token and get user info
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7); // Remove "Bearer " prefix
            UserDTO user = authService.validateToken(token);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            logger.error("Token validation failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Change password
     */
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> request,
                                          @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            UserDTO user = authService.validateToken(token);
            
            String currentPassword = request.get("currentPassword");
            String newPassword = request.get("newPassword");
            
            if (currentPassword == null || newPassword == null) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Current password and new password are required"));
            }

            authService.changePassword(user.getId(), currentPassword, newPassword);
            return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
        } catch (Exception e) {
            logger.error("Password change failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Reset password (simplified)
     */
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String newPassword = request.get("newPassword");
            
            if (email == null || newPassword == null) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Email and new password are required"));
            }

            authService.resetPassword(email, newPassword);
            return ResponseEntity.ok(Map.of("message", "Password reset successfully"));
        } catch (Exception e) {
            logger.error("Password reset failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Verify account
     */
    @PostMapping("/verify/{userId}")
    public ResponseEntity<?> verifyAccount(@PathVariable Long userId) {
        try {
            authService.verifyAccount(userId);
            return ResponseEntity.ok(Map.of("message", "Account verified successfully"));
        } catch (Exception e) {
            logger.error("Account verification failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

}
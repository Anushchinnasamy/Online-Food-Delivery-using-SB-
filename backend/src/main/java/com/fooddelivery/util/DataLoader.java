package com.fooddelivery.util;

import com.fooddelivery.entity.User;
import com.fooddelivery.entity.UserRole;
import com.fooddelivery.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Data loader to ensure test users have properly encoded passwords
 * This runs on application startup and fixes any password encoding issues
 */
@Component
public class DataLoader implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataLoader.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        logger.info("=".repeat(80));
        logger.info("Running DataLoader - Verifying user passwords");
        logger.info("=".repeat(80));

        // Check and fix passwords for all test users
        fixUserPassword("admin@fooddelivery.com", "password123");
        fixUserPassword("raj@spicepalace.com", "password123");
        fixUserPassword("priya@pizzacorner.com", "password123");
        fixUserPassword("ahmed@burgerking.com", "password123");
        fixUserPassword("ravi.delivery@gmail.com", "password123");
        fixUserPassword("suresh.delivery@gmail.com", "password123");
        fixUserPassword("vikram.delivery@gmail.com", "password123");
        fixUserPassword("john.doe@gmail.com", "password123");
        fixUserPassword("jane.smith@gmail.com", "password123");
        fixUserPassword("mike.johnson@gmail.com", "password123");

        logger.info("=".repeat(80));
        logger.info("DataLoader completed - All passwords verified");
        logger.info("=".repeat(80));
        
        // Print test user credentials
        printTestCredentials();
    }

    private void fixUserPassword(String email, String plainPassword) {
        try {
            User user = userRepository.findByEmailIgnoreCase(email).orElse(null);
            
            if (user == null) {
                logger.warn("User not found: {}", email);
                return;
            }

            // Check if password is already properly encoded
            boolean isValidHash = user.getPassword() != null && 
                                 user.getPassword().startsWith("$2a$") &&
                                 passwordEncoder.matches(plainPassword, user.getPassword());

            if (isValidHash) {
                logger.info("✓ Password OK for: {} ({})", user.getName(), email);
            } else {
                // Re-encode the password
                String encodedPassword = passwordEncoder.encode(plainPassword);
                user.setPassword(encodedPassword);
                userRepository.save(user);
                logger.info("✓ Password FIXED for: {} ({})", user.getName(), email);
            }
        } catch (Exception e) {
            logger.error("Error fixing password for {}: {}", email, e.getMessage());
        }
    }

    private void printTestCredentials() {
        logger.info("\n" + "=".repeat(80));
        logger.info("TEST USER CREDENTIALS");
        logger.info("=".repeat(80));
        logger.info("All users have password: password123\n");
        
        logger.info("PLATFORM_ADMIN:");
        logger.info("  Email: admin@fooddelivery.com");
        logger.info("  Password: password123\n");
        
        logger.info("RESTAURANT_ADMIN:");
        logger.info("  Email: raj@spicepalace.com");
        logger.info("  Email: priya@pizzacorner.com");
        logger.info("  Email: ahmed@burgerking.com");
        logger.info("  Password: password123\n");
        
        logger.info("DELIVERY_PARTNER:");
        logger.info("  Email: ravi.delivery@gmail.com");
        logger.info("  Email: suresh.delivery@gmail.com");
        logger.info("  Email: vikram.delivery@gmail.com");
        logger.info("  Password: password123\n");
        
        logger.info("CUSTOMER:");
        logger.info("  Email: john.doe@gmail.com");
        logger.info("  Email: jane.smith@gmail.com");
        logger.info("  Email: mike.johnson@gmail.com");
        logger.info("  Password: password123");
        
        logger.info("=".repeat(80) + "\n");
    }
}

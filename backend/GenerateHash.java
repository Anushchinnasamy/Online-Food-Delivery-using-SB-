import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Utility to generate BCrypt password hashes
 * Run this to generate hashes for test users
 */
public class GenerateHash {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
        
        // Generate hash for password123
        String password = "password123";
        String hash = encoder.encode(password);
        
        System.out.println("=".repeat(80));
        System.out.println("BCrypt Password Hash Generator");
        System.out.println("=".repeat(80));
        System.out.println("\nPassword: " + password);
        System.out.println("BCrypt Hash: " + hash);
        
        // Verify it works
        boolean matches = encoder.matches(password, hash);
        System.out.println("Verification: " + (matches ? "✓ SUCCESS" : "✗ FAILED"));
        
        // Test with the hash from data.sql
        String existingHash = "$2a$12$LQv3c1yqBwWFcDkz2jANQOdP8mJxdvz3h8pl1HL.vQOqZqjqb4qOa";
        boolean existingMatches = encoder.matches(password, existingHash);
        System.out.println("\nTesting existing hash from data.sql:");
        System.out.println("Hash: " + existingHash);
        System.out.println("Matches 'password123': " + (existingMatches ? "✓ YES" : "✗ NO"));
        
        System.out.println("\n" + "=".repeat(80));
        System.out.println("Use this hash in your SQL updates or data.sql file");
        System.out.println("=".repeat(80));
    }
}

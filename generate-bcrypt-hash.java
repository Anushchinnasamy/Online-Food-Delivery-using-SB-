import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GenerateBCryptHash {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
        String rawPassword = "password123";
        
        // Generate 3 different hashes to see the pattern
        for (int i = 1; i <= 3; i++) {
            String hash = encoder.encode(rawPassword);
            System.out.println("Hash " + i + ": " + hash);
            System.out.println("Verification " + i + ": " + encoder.matches(rawPassword, hash));
        }
        
        // Test the existing hash from data.sql
        String existingHash = "$2a$12$LQv3c1yqBwWFcDkz2jANQOdP8mJxdvz3h8pl1HL.vQOqZqjqb4qOa";
        System.out.println("\nExisting hash verification: " + encoder.matches(rawPassword, existingHash));
    }
}
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TestBCrypt {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "password123";
        String hash = encoder.encode(password);
        System.out.println("BCrypt hash for 'password123': " + hash);
        
        // Test the existing hash
        String existingHash = "$2a$12$LQv3c1yqBwWFcDkz2jANQOdP8mJxdvz3h8pl1HL.vQOqZqjqb4qOa";
        boolean matches = encoder.matches(password, existingHash);
        System.out.println("Existing hash matches 'password123': " + matches);
    }
}
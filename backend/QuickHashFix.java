import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class QuickHashFix {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "password123";
        String hash = encoder.encode(password);
        
        System.out.println("Generated BCrypt hash for 'password123':");
        System.out.println(hash);
        
        // Verify it works
        boolean matches = encoder.matches(password, hash);
        System.out.println("\nVerification: " + (matches ? "SUCCESS" : "FAILED"));
        
        // Test with existing hash from database
        String dbHash = "$2a$10$N9qo8uLOickgx2ZMRZoMye1J8JqMQOqZqjqb4qOa2qOa2qOa2";
        System.out.println("\nTesting database hash:");
        System.out.println("Hash: " + dbHash);
        try {
            boolean dbMatches = encoder.matches(password, dbHash);
            System.out.println("Matches 'password123': " + (dbMatches ? "YES" : "NO"));
        } catch (Exception e) {
            System.out.println("ERROR: Invalid hash format - " + e.getMessage());
        }
        
        // Generate SQL update statement
        System.out.println("\n" + "=".repeat(80));
        System.out.println("SQL UPDATE STATEMENT:");
        System.out.println("=".repeat(80));
        System.out.println("UPDATE users SET password = '" + hash + "' WHERE email IN (");
        System.out.println("  'admin@fooddelivery.com',");
        System.out.println("  'raj@spicepalace.com',");
        System.out.println("  'priya@pizzacorner.com',");
        System.out.println("  'ahmed@burgerking.com',");
        System.out.println("  'ravi.delivery@gmail.com',");
        System.out.println("  'suresh.delivery@gmail.com',");
        System.out.println("  'vikram.delivery@gmail.com',");
        System.out.println("  'john.doe@gmail.com',");
        System.out.println("  'jane.smith@gmail.com',");
        System.out.println("  'mike.johnson@gmail.com'");
        System.out.println(");");
    }
}

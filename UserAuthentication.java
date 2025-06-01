import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.regex.Pattern;

public class UserAuthentication {
    // Hash PIN using SHA-256
    private String hashPin(String pin) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(pin.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hashing algorithm not available", e);
        }
    }

    // Validate user inputs
    private void validateInput(String name, String email, String number, String pin) {
        if (name == null || name.trim().isEmpty()) 
            throw new IllegalArgumentException("Name cannot be empty");
        
        if (!Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$").matcher(email).matches())
            throw new IllegalArgumentException("Invalid email format");
        
        if (!Pattern.compile("^09\\d{9}$").matcher(number).matches())
            throw new IllegalArgumentException("Number must be 11 digits starting with 09");
        
        if (!Pattern.compile("^\\d{4}$").matcher(pin).matches())
            throw new IllegalArgumentException("PIN must be 4 digits");
    }

    // 1. Registration
    public int register(String name, String email, String number, String pin) {
        validateInput(name, email, number, pin);
        String hashedPin = hashPin(pin);
        
        String sql = "INSERT INTO users (Name, Email, Number, PIN) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, number);
            pstmt.setString(4, hashedPin);
            
            pstmt.executeUpdate();
            
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                if (e.getMessage().contains("Email")) 
                    throw new IllegalArgumentException("Email already registered");
                if (e.getMessage().contains("Number")) 
                    throw new IllegalArgumentException("Number already registered");
            }
            throw new RuntimeException("Registration failed", e);
        }
        return -1;
    }

    // 2. Login
    public int login(String credential, String pin) {
        String hashedPin = hashPin(pin);
        String sql = "SELECT ID FROM users WHERE (Email = ? OR Number = ?) AND PIN = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, credential);
            pstmt.setString(2, credential);
            pstmt.setString(3, hashedPin);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt("ID");
                throw new SecurityException("Invalid credentials");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Login failed", e);
        }
    }

    // 3. Change PIN
    public void changePin(int userId, String oldPin, String newPin) {
        if (!newPin.matches("^\\d{4}$")) 
            throw new IllegalArgumentException("New PIN must be 4 digits");
        
        String oldHashed = hashPin(oldPin);
        String newHashed = hashPin(newPin);
        
        // Verify old PIN first
        String verifySql = "SELECT ID FROM users WHERE ID = ? AND PIN = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement vstmt = conn.prepareStatement(verifySql)) {
            
            vstmt.setInt(1, userId);
            vstmt.setString(2, oldHashed);
            
            try (ResultSet rs = vstmt.executeQuery()) {
                if (!rs.next()) throw new SecurityException("Incorrect old PIN");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Verification failed", e);
        }

        // Update PIN
        String updateSql = "UPDATE users SET PIN = ? WHERE ID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
            
            pstmt.setString(1, newHashed);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            throw new RuntimeException("PIN update failed", e);
        }
    }

    // 4. Logout
    public void logout() {
        // Invalidate session/tokens in real implementation
        System.out.println("User logged out successfully");
    }
    
    // 5. Get User Access Code (Secure)
    public String getAccessCode(int userId) {
        String sql = "SELECT CONCAT(LEFT(Number, 3), '***', RIGHT(Number, 3)) AS AccessCode FROM users WHERE ID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getString("AccessCode");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Access code retrieval failed", e);
        }
        return null;
    }
}
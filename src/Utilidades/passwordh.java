package Utilidades;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class passwordh {
    
    // Método para encriptar contraseña (MD5, SHA-256, etc.)
    public static String encriptar(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    
    // Método para verificar contraseña
    public static boolean verificar(String passwordIngresada, String passwordHash) {
        return encriptar(passwordIngresada).equals(passwordHash);
    }
}

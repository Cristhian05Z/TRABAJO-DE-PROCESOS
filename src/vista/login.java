package vista;

import javax.swing.*;

import Utilidades.passwordh;
import modelo.Usuario;
import database.conexion;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class login extends JFrame {
    
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    
    public login() {
        setTitle("Alquiler de Playa - Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Panel principal con gradiente
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth(), h = getHeight();
                Color color1 = new Color(52, 152, 219);
                Color color2 = new Color(41, 128, 185);
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, h, color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        mainPanel.setLayout(null);
        
        // Título
        JLabel lblTitle = new JLabel("Sistema de Alquiler de Playa");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBounds(70, 30, 300, 30);
        mainPanel.add(lblTitle);
        
        // Panel de login
        JPanel loginPanel = new JPanel();
        loginPanel.setBackground(Color.WHITE);
        loginPanel.setBounds(50, 80, 300, 150);
        loginPanel.setLayout(null);
        loginPanel.setBorder(BorderFactory.createLineBorder(new Color(52, 152, 219), 2));
        
        JLabel lblUser = new JLabel("Usuario:");
        lblUser.setBounds(30, 20, 80, 25);
        loginPanel.add(lblUser);
        
        txtUsername = new JTextField();
        txtUsername.setBounds(30, 45, 240, 30);
        loginPanel.add(txtUsername);
        
        JLabel lblPass = new JLabel("Contraseña:");
        lblPass.setBounds(30, 80, 80, 25);
        loginPanel.add(lblPass);
        
        txtPassword = new JPasswordField();
        txtPassword.setBounds(30, 105, 240, 30);
        loginPanel.add(txtPassword);
        
        mainPanel.add(loginPanel);
        
        // Botón de login
        btnLogin = new JButton("Iniciar Sesión");
        btnLogin.setBounds(125, 240, 150, 35);
        btnLogin.setBackground(new Color(46, 204, 113));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setFont(new Font("Arial", Font.BOLD, 14));
        btnLogin.addActionListener(e -> loginuser());
        mainPanel.add(btnLogin);
        
        add(mainPanel);
        
        // Enter para login
        txtPassword.addActionListener(e -> loginuser());
    }
    
    private void loginuser() {
    String username = txtUsername.getText().trim();
    String password = new String(txtPassword.getPassword()).trim();

    System.out.println("====== DEBUG LOGIN ======");
    System.out.println("Usuario ingresado: [" + username + "]");
    System.out.println("Contraseña ingresada: [" + password + "]");
    System.out.println("Longitud contraseña: " + password.length());

    if (username.isEmpty() || password.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Complete todos los campos.");
        return;
    }

    try (Connection conn = conexion.getConnection()) {
        String sql = "SELECT * FROM USUARIO WHERE IDUsuario = ? OR UPPER(Nombre) = UPPER(?)";
        PreparedStatement pst = conn.prepareStatement(sql);
        pst.setString(1, username);
        pst.setString(2, username);

        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            String idUsuario = rs.getString("IDUsuario");
            String nombre = rs.getString("Nombre");
            String passwordBD = rs.getString("Contraseña");
            
            System.out.println("\n--- USUARIO ENCONTRADO ---");
            System.out.println("IDUsuario BD: [" + idUsuario + "]");
            System.out.println("Nombre BD: [" + nombre + "]");
            System.out.println("Contraseña BD: [" + passwordBD + "]");
            System.out.println("Longitud contraseña BD: " + passwordBD.length());
            
            // Comparación byte por byte
            System.out.println("\n--- COMPARACIÓN ---");
            System.out.println("¿Son iguales? " + password.equals(passwordBD));
            System.out.println("¿Iguales ignorando mayúsculas? " + password.equalsIgnoreCase(passwordBD));
            
            // Mostrar en hexadecimal
            System.out.println("\nIngresada en hex: " + bytesToHex(password.getBytes()));
            System.out.println("BD en hex: " + bytesToHex(passwordBD.getBytes()));
            
            // COMPARACIÓN SIMPLE
            if (password.equals(passwordBD)) {
                System.out.println("\n✅ LOGIN EXITOSO!");
                
                Usuario user = new Usuario(
                    rs.getString("IDUsuario"),
                    rs.getString("TipoDeUsuario"),
                    rs.getString("Nombre"),
                    rs.getString("Contraseña")
                );
                
                JOptionPane.showMessageDialog(this, 
                    "¡Bienvenido " + nombre + "!",
                    "Login exitoso",
                    JOptionPane.INFORMATION_MESSAGE);
                
                dispose();
                
                if (user.esAdmin()) {
                    new adminframe(user).setVisible(true);
                } else if (user.esEmpleado()) {
                    new vendedorframe(user).setVisible(true);
                } else if (user.esTurista()) {
                    new turistaframe(user).setVisible(true);
                }
            } else {
                System.out.println("\n❌ CONTRASEÑA INCORRECTA");
                JOptionPane.showMessageDialog(this, 
                    "Contraseña incorrecta\n\n" +
                    "Ingresada: " + password + "\n" +
                    "En BD: " + passwordBD,
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } else {
            System.out.println("\n❌ USUARIO NO ENCONTRADO");
            JOptionPane.showMessageDialog(this,
                "Usuario no encontrado: " + username,
                "Error", JOptionPane.ERROR_MESSAGE);
        }

    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this,
            "Error al conectar: " + e.getMessage(),
            "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    System.out.println("========================\n");
}
private String bytesToHex(byte[] bytes) {
    StringBuilder sb = new StringBuilder();
    for (byte b : bytes) {
        sb.append(String.format("%02X ", b));
    }
    return sb.toString();
}

    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new login().setVisible(true);
        });
    }
}


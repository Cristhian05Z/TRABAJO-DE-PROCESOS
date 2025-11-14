package vista;

import javax.swing.*;
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
        setTitle("Beach Rental - Login");
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
        JLabel lblTitle = new JLabel("Beach Rental System");
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
        btnLogin.addActionListener(e -> login());
        mainPanel.add(btnLogin);
        
        add(mainPanel);
        
        // Enter para login
        txtPassword.addActionListener(e -> login());
    }
    
    private void login() {
        String username = txtUsername.getText();
        String password = new String(txtPassword.getPassword());
        
        try (Connection conn = conexion.getConnection()) {
            String sql = "SELECT * FROM usuarios WHERE username = ? AND password = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, username);
            pst.setString(2, password);
            
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                Usuario user = new Usuario(
                    rs.getInt("id"),
                    rs.getString("Nombre"),
                    rs.getString("contraseña"),
                    rs.getString("tipo de usuario")
                );
                
                dispose();
                
                // Abrir la interfaz correspondiente según el rol
                switch (user.getTipoDeUsuario().toLowerCase()) {
                    case "admin":
                        new admin(user).setVisible(true);
                        break;
                    case "vendedor":
                        new vendedor(username).setVisible(true);
                        break;
                    case "turista":
                        new tursita(username).setVisible(true);
                        break;
                }
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Usuario o contraseña incorrectos", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error de conexión: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new login().setVisible(true);
        });
    }
}


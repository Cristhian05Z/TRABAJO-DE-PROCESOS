package vista;

import javax.swing.*;

import database.conexion;
import modelo.Usuario;

import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class login extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;

    public login() {
        setTitle("Login - Alquiler de Playa");
        setSize(700, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        setLayout(new BorderLayout());

        // PANEL IZQUIERDO CON IMAGEN
        JLabel lblImagen = new JLabel();
        lblImagen.setIcon(new ImageIcon("src/Imagen/playa.png")); 
        lblImagen.setHorizontalAlignment(JLabel.CENTER);
        lblImagen.setVerticalAlignment(JLabel.CENTER);
        add(lblImagen, BorderLayout.WEST);

        lblImagen.setPreferredSize(new Dimension(350, 400));

        // PANEL DERECHO (CARD)
        JPanel rightPanel = new RoundedPanel(30);
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setLayout(new GridBagLayout());
        add(rightPanel, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // TÍTULO
        JLabel lblTitle = new JLabel("Iniciar Sesión");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 26));
        lblTitle.setHorizontalAlignment(JLabel.CENTER);
        gbc.gridx = 0; gbc.gridy = 0;
        rightPanel.add(lblTitle, gbc);

        // USUARIO
        JLabel lblUser = new JLabel("Usuario");
        lblUser.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridy = 1;
        rightPanel.add(lblUser, gbc);

        txtUsername = new JTextField();
        txtUsername.setPreferredSize(new Dimension(200, 35));
        txtUsername.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridy = 2;
        rightPanel.add(txtUsername, gbc);

        // CONTRASEÑA
        JLabel lblPass = new JLabel("Contraseña");
        lblPass.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridy = 3;
        rightPanel.add(lblPass, gbc);

        txtPassword = new JPasswordField();
        txtPassword.setPreferredSize(new Dimension(200, 35));
        txtPassword.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridy = 4;
        rightPanel.add(txtPassword, gbc);

        // BOTÓN LOGIN
        btnLogin = new JButton("Ingresar");
        btnLogin.setFont(new Font("Arial", Font.BOLD, 16));
        btnLogin.setBackground(new Color(46, 204, 113));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setPreferredSize(new Dimension(120, 40));
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover
        btnLogin.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnLogin.setBackground(new Color(39, 174, 96));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btnLogin.setBackground(new Color(46, 204, 113));
            }
        });

        gbc.gridy = 5;
        rightPanel.add(btnLogin, gbc);

        // Evento Enter
        txtPassword.addActionListener(e -> loginuser());
        btnLogin.addActionListener(e -> loginuser());
    }


    // PANEL REDONDEADO PERSONALIZADO
    class RoundedPanel extends JPanel {
        private int radius;

        public RoundedPanel(int radius) {
            this.radius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                 RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        }
    }

    private void loginuser() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

    // Validación básica
    if (username.isEmpty() || password.isEmpty()) {
        JOptionPane.showMessageDialog(this,
            "Complete todos los campos.",
            "Advertencia", JOptionPane.WARNING_MESSAGE);
        return;
    }

    try (Connection conn = conexion.getConnection()) {

        String sql = "SELECT * FROM USUARIO WHERE IDUsuario = ? OR UPPER(Nombre) = UPPER(?)";
        PreparedStatement pst = conn.prepareStatement(sql);
        pst.setString(1, username);
        pst.setString(2, username);

        ResultSet rs = pst.executeQuery();

        if (!rs.next()) {
            JOptionPane.showMessageDialog(this,
                "Usuario no encontrado.",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String userBD = rs.getString("IDUsuario");
        String nombreBD = rs.getString("Nombre");
        String passBD = rs.getString("Contraseña");
        String tipoBD = rs.getString("TipoDeUsuario");

        // Comparación directa (tu BD almacena texto plano)
        if (!password.equals(passBD)) {
            JOptionPane.showMessageDialog(this,
                "Contraseña incorrecta.",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // ---- LOGIN EXITOSO ----
        Usuario user = new Usuario(
            rs.getString("IDUsuario"),
            tipoBD,
            nombreBD,
            passBD
        );

        JOptionPane.showMessageDialog(this,
            "Bienvenido " + nombreBD,
            "Ingreso correcto",
            JOptionPane.INFORMATION_MESSAGE);

        dispose(); 

        // Redirección según el tipo de usuario
        switch (tipoBD.toLowerCase()) {
            case "administrador":
                new adminframe(user).setVisible(true);
                break;

            case "empleado":
                new vendedorframe(user).setVisible(true);
                break;

            case "turista":
                new turistaframe(user).setVisible(true);
                break;

            default:
                JOptionPane.showMessageDialog(this,
                    "Tipo de usuario desconocido: " + tipoBD,
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this,
            "Error de conexión: " + e.getMessage(),
            "SQL Error", JOptionPane.ERROR_MESSAGE);
    }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new login().setVisible(true));
    }
}



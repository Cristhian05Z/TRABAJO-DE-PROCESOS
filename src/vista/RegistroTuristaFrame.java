package vista;

import database.conexion;
import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class RegistroTuristaFrame extends JFrame {

    private JTextField txtUsuario, txtNombre, txtApellido, txtDNI, txtNacionalidad, txtTelefono, txtCorreo;
    private JPasswordField txtPassword, txtConfirmar;

    public RegistroTuristaFrame() {
        setTitle("Registro de Turista");
        setSize(420, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initUI();
    }

    private void initUI() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        txtUsuario = new JTextField();
        txtPassword = new JPasswordField();
        txtConfirmar = new JPasswordField();
        txtNombre = new JTextField();
        txtApellido = new JTextField();
        txtDNI = new JTextField();
        txtNacionalidad = new JTextField();
        txtTelefono = new JTextField();
        txtCorreo = new JTextField();

        panel.add(new JLabel("Usuario:"));
        panel.add(txtUsuario);

        panel.add(new JLabel("Contraseña:"));
        panel.add(txtPassword);

        panel.add(new JLabel("Confirmar contraseña:"));
        panel.add(txtConfirmar);

        panel.add(new JLabel("Nombre:"));
        panel.add(txtNombre);

        panel.add(new JLabel("Apellido:"));
        panel.add(txtApellido);

        panel.add(new JLabel("DNI:"));
        panel.add(txtDNI);

        panel.add(new JLabel("Nacionalidad:"));
        panel.add(txtNacionalidad);

        panel.add(new JLabel("Teléfono:"));
        panel.add(txtTelefono);

        panel.add(new JLabel("Correo:"));
        panel.add(txtCorreo);

        JButton btnRegistrar = new JButton("Registrarse");
        btnRegistrar.addActionListener(e -> registrarTurista());

        add(panel, BorderLayout.CENTER);
        add(btnRegistrar, BorderLayout.SOUTH);
    }

    private void registrarTurista() {

        if (txtUsuario.getText().isEmpty() ||
            txtPassword.getPassword().length == 0 ||
            txtConfirmar.getPassword().length == 0 ||
            txtNombre.getText().isEmpty() ||
            txtApellido.getText().isEmpty() ||
            txtDNI.getText().isEmpty() ||
            txtNacionalidad.getText().isEmpty()) {

            JOptionPane.showMessageDialog(this, "Completa todos los campos obligatorios");
            return;
        }

        if (!String.valueOf(txtPassword.getPassword())
                .equals(String.valueOf(txtConfirmar.getPassword()))) {

            JOptionPane.showMessageDialog(this, "Las contraseñas no coinciden");
            return;
        }

        try (Connection conn = conexion.getConnection()) {
            conn.setAutoCommit(false);

            // Verificar usuario duplicado
            String checkUser = "SELECT 1 FROM USUARIO WHERE Nombre = ?";
            try (PreparedStatement pstCheck = conn.prepareStatement(checkUser)) {
                pstCheck.setString(1, txtUsuario.getText());
                if (pstCheck.executeQuery().next()) {
                    JOptionPane.showMessageDialog(this, "El usuario ya existe");
                    conn.rollback();
                    return;
                }
            }

            // Generar IDs
            String idUsuario = generarNuevoID(conn, "USUARIO", "IDUsuario", "U");
            String idTurista = generarNuevoID(conn, "TURISTAA", "IDTurista", "T");

            // INSERT USUARIO
            String sqlUsuario = """
                INSERT INTO USUARIO (IDUsuario, TipoDeUsuario, Nombre, Contraseña)
                VALUES (?, ?, ?, ?)
            """;

            try (PreparedStatement pstUsuario = conn.prepareStatement(sqlUsuario)) {
                pstUsuario.setString(1, idUsuario);
                pstUsuario.setString(2, "TURISTA");
                pstUsuario.setString(3, txtUsuario.getText());
                pstUsuario.setString(4, String.valueOf(txtPassword.getPassword()));
                pstUsuario.executeUpdate();
            }

            // INSERT TURISTAA (SIN IDUSUARIO)
            String sqlTurista = """
                INSERT INTO TURISTAA
                (IDTurista, Nombre, Apellido, DNI, Nacionalidad, Telefono, Email)
                VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

            try (PreparedStatement pstTurista = conn.prepareStatement(sqlTurista)) {
                pstTurista.setString(1, idTurista);
                pstTurista.setString(2, txtNombre.getText());
                pstTurista.setString(3, txtApellido.getText());
                pstTurista.setString(4, txtDNI.getText());
                pstTurista.setString(5, txtNacionalidad.getText());
                pstTurista.setString(6, txtTelefono.getText());
                pstTurista.setString(7, txtCorreo.getText());
                pstTurista.executeUpdate();
            }

            conn.commit();

            JOptionPane.showMessageDialog(this,
                "🎉 Registro exitoso\nYa puedes iniciar sesión",
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE);

            dispose();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error al registrar:\n" + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private String generarNuevoID(Connection conn, String tabla, String columna, String prefijo) throws SQLException {
        String sql = "SELECT TOP 1 " + columna + " FROM " + tabla +
                     " WHERE " + columna + " LIKE ? ORDER BY " + columna + " DESC";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, prefijo + "%");
            ResultSet rs = pst.executeQuery();
            int numero = 1;
            if (rs.next()) {
                numero = Integer.parseInt(rs.getString(1).substring(1)) + 1;
            }
            return String.format("%s%03d", prefijo, numero);
        }
    }
}

package vista;

import javax.swing.*;
import javax.swing.table.*;
import modelo.Usuario;
import database.conexion;
import java.awt.*;
import java.sql.*;

public class adminframe extends JFrame {
    
    private Usuario currentUser;
    private JTable tablaRecursos;
    private DefaultTableModel modelRecursos;
    private JTable tablaUsuarios;
    private DefaultTableModel modelUsuarios;
    private JTable tablaTuristas;
    private DefaultTableModel modelTuristas;
    private JTable tablaAlquileres;
    private DefaultTableModel modelAlquileres;
    private JTable tablaPromociones;
    private DefaultTableModel modelPromociones;
    private JTabbedPane tabbedPane;
    
    public adminframe(Usuario user) {
        this.currentUser = user;
        
        setTitle("Panel Administrador - " + user.getNombre());
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Panel superior
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(52, 152, 219));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel lblWelcome = new JLabel("Bienvenido, " + user.getNombre() + " (Administrador)");
        lblWelcome.setForeground(Color.WHITE);
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 18));
        
        JButton btnLogout = new JButton("Cerrar Sesión");
        btnLogout.setBackground(new Color(231, 76, 60));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.addActionListener(e -> logout());
        
        topPanel.add(lblWelcome, BorderLayout.WEST);
        topPanel.add(btnLogout, BorderLayout.EAST);
        
        // Tabs
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("🏖️ Recursos", createRecursosPanel());
        tabbedPane.addTab("👥 Usuarios", createUsuariosPanel());
        tabbedPane.addTab("🧳 Turistas", createTuristasPanel());
        tabbedPane.addTab("📋 Alquileres", createAlquileresPanel());
        tabbedPane.addTab("🎁 Promociones", createPromocionesPanel());
        
        add(topPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
        
        loadData();
    }
    
    // ============================================
    // PANEL DE RECURSOS
    // ============================================
    private JPanel createRecursosPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Tabla: IDRecursos, Recurso, Descripcion, TarifaPorHora, Estado
        modelRecursos = new DefaultTableModel(
            new String[]{"ID", "Recurso", "Descripción", "Tarifa/Hora", "Estado"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaRecursos = new JTable(modelRecursos);
        tablaRecursos.setRowHeight(30);
        JScrollPane scrollPane = new JScrollPane(tablaRecursos);
        
        // Botones
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAdd = new JButton("Agregar Recurso");
        JButton btnEdit = new JButton("Editar");
        JButton btnDelete = new JButton("Eliminar");
        JButton btnRefresh = new JButton("Actualizar");
        
        btnAdd.setBackground(new Color(46, 204, 113));
        btnAdd.setForeground(Color.WHITE);
        btnEdit.setBackground(new Color(52, 152, 219));
        btnEdit.setForeground(Color.WHITE);
        btnDelete.setBackground(new Color(231, 76, 60));
        btnDelete.setForeground(Color.WHITE);
        
        btnAdd.addActionListener(e -> addRecurso());
        btnEdit.addActionListener(e -> editRecurso());
        btnDelete.addActionListener(e -> deleteRecurso());
        btnRefresh.addActionListener(e -> loadRecursos());
        
        btnPanel.add(btnAdd);
        btnPanel.add(btnEdit);
        btnPanel.add(btnDelete);
        btnPanel.add(btnRefresh);
        
        panel.add(btnPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void loadRecursos() {
        modelRecursos.setRowCount(0);
        try (Connection conn = conexion.getConnection()) {
            String sql = "SELECT * FROM RECURSOS ORDER BY Recurso";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                modelRecursos.addRow(new Object[]{
                    rs.getInt("IDRecursos"),
                    rs.getString("Recurso"),
                    rs.getString("Descripcion"),
                    String.format("S/ %.2f", rs.getDouble("TarifaPorHora")),
                    rs.getString("Estado")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar recursos: " + e.getMessage());
        }
    }
    
    private void addRecurso() {
        JTextField txtRecurso = new JTextField();
        JTextField txtDescripcion = new JTextField();
        JTextField txtTarifa = new JTextField();
        JComboBox<String> cmbEstado = new JComboBox<>(new String[]{"disponible", "alquilado", "mantenimiento"});
        
        Object[] message = {
            "Recurso:", txtRecurso,
            "Descripción:", txtDescripcion,
            "Tarifa por Hora:", txtTarifa,
            "Estado:", cmbEstado
        };
        
        int option = JOptionPane.showConfirmDialog(this, message, "Agregar Recurso", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try (Connection conn = conexion.getConnection()) {
                String sql = "INSERT INTO RECURSOS (Recurso, Descripcion, TarifaPorHora, Estado) VALUES (?, ?, ?, ?)";
                PreparedStatement pst = conn.prepareStatement(sql);
                pst.setString(1, txtRecurso.getText());
                pst.setString(2, txtDescripcion.getText());
                pst.setDouble(3, Double.parseDouble(txtTarifa.getText()));
                pst.setString(4, cmbEstado.getSelectedItem().toString());
                pst.executeUpdate();
                
                JOptionPane.showMessageDialog(this, "Recurso agregado exitosamente");
                loadRecursos();
            } catch (SQLException | NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }
    
    private void editRecurso() {
        int selectedRow = tablaRecursos.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un recurso");
            return;
        }
        
        int id = (int) modelRecursos.getValueAt(selectedRow, 0);
        JTextField txtRecurso = new JTextField(modelRecursos.getValueAt(selectedRow, 1).toString());
        JTextField txtDescripcion = new JTextField(modelRecursos.getValueAt(selectedRow, 2).toString());
        String tarifaStr = modelRecursos.getValueAt(selectedRow, 3).toString().replace("S/ ", "");
        JTextField txtTarifa = new JTextField(tarifaStr);
        JComboBox<String> cmbEstado = new JComboBox<>(new String[]{"disponible", "alquilado", "mantenimiento"});
        cmbEstado.setSelectedItem(modelRecursos.getValueAt(selectedRow, 4).toString());
        
        Object[] message = {
            "Recurso:", txtRecurso,
            "Descripción:", txtDescripcion,
            "Tarifa por Hora:", txtTarifa,
            "Estado:", cmbEstado
        };
        
        int option = JOptionPane.showConfirmDialog(this, message, "Editar Recurso", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try (Connection conn = conexion.getConnection()) {
                String sql = "UPDATE RECURSOS SET Recurso=?, Descripcion=?, TarifaPorHora=?, Estado=? WHERE IDRecursos=?";
                PreparedStatement pst = conn.prepareStatement(sql);
                pst.setString(1, txtRecurso.getText());
                pst.setString(2, txtDescripcion.getText());
                pst.setDouble(3, Double.parseDouble(txtTarifa.getText()));
                pst.setString(4, cmbEstado.getSelectedItem().toString());
                pst.setInt(5, id);
                pst.executeUpdate();
                
                JOptionPane.showMessageDialog(this, "Recurso actualizado");
                loadRecursos();
            } catch (SQLException | NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }
    
    private void deleteRecurso() {
        int selectedRow = tablaRecursos.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un recurso");
            return;
        }
        
        int id = (int) modelRecursos.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, 
            "¿Eliminar este recurso?", 
            "Confirmar", 
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = conexion.getConnection()) {
                String sql = "DELETE FROM RECURSOS WHERE IDRecursos=?";
                PreparedStatement pst = conn.prepareStatement(sql);
                pst.setInt(1, id);
                pst.executeUpdate();
                
                JOptionPane.showMessageDialog(this, "Recurso eliminado");
                loadRecursos();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }
    
    // ============================================
    // PANEL DE USUARIOS
    // ============================================
    private JPanel createUsuariosPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Tabla: IDUsuario, TipoDeUsuario, Nombre
        modelUsuarios = new DefaultTableModel(
            new String[]{"ID", "Tipo", "Nombre"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaUsuarios = new JTable(modelUsuarios);
        tablaUsuarios.setRowHeight(30);
        JScrollPane scrollPane = new JScrollPane(tablaUsuarios);
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAdd = new JButton("Agregar Usuario");
        JButton btnDelete = new JButton("Eliminar");
        JButton btnRefresh = new JButton("Actualizar");
        
        btnAdd.setBackground(new Color(46, 204, 113));
        btnAdd.setForeground(Color.WHITE);
        btnDelete.setBackground(new Color(231, 76, 60));
        btnDelete.setForeground(Color.WHITE);
        
        btnAdd.addActionListener(e -> addUsuario());
        btnDelete.addActionListener(e -> deleteUsuario());
        btnRefresh.addActionListener(e -> loadUsuarios());
        
        btnPanel.add(btnAdd);
        btnPanel.add(btnDelete);
        btnPanel.add(btnRefresh);
        
        panel.add(btnPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void loadUsuarios() {
        modelUsuarios.setRowCount(0);
        try (Connection conn = conexion.getConnection()) {
            String sql = "SELECT IDUsuario, TipoDeUsuario, Nombre FROM USUARIO ORDER BY Nombre";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                modelUsuarios.addRow(new Object[]{
                    rs.getString("IDUsuario"),
                    rs.getString("TipoDeUsuario"),
                    rs.getString("Nombre")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void addUsuario() {
        JTextField txtID = new JTextField();
        JTextField txtNombre = new JTextField();
        JPasswordField txtPassword = new JPasswordField();
        JComboBox<String> cmbTipo = new JComboBox<>(new String[]{"ADMINISTRADOR", "EMPLEADO", "TURISTA"});
        
        Object[] message = {
            "ID Usuario:", txtID,
            "Nombre:", txtNombre,
            "Contraseña:", txtPassword,
            "Tipo:", cmbTipo
        };
        
        int option = JOptionPane.showConfirmDialog(this, message, "Agregar Usuario", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try (Connection conn = conexion.getConnection()) {
                String sql = "INSERT INTO USUARIO (IDUsuario, TipoDeUsuario, Nombre, Contrasena) VALUES (?, ?, ?, ?)";
                PreparedStatement pst = conn.prepareStatement(sql);
                pst.setString(1, txtID.getText());
                pst.setString(2, cmbTipo.getSelectedItem().toString());
                pst.setString(3, txtNombre.getText());
                pst.setString(4, new String(txtPassword.getPassword()));
                pst.executeUpdate();
                
                JOptionPane.showMessageDialog(this, "Usuario agregado");
                loadUsuarios();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }
    
    private void deleteUsuario() {
        int selectedRow = tablaUsuarios.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un usuario");
            return;
        }
        
        String id = modelUsuarios.getValueAt(selectedRow, 0).toString();
        int confirm = JOptionPane.showConfirmDialog(this, 
            "¿Eliminar este usuario?", 
            "Confirmar", 
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = conexion.getConnection()) {
                String sql = "DELETE FROM USUARIO WHERE IDUsuario=?";
                PreparedStatement pst = conn.prepareStatement(sql);
                pst.setString(1, id);
                pst.executeUpdate();
                
                JOptionPane.showMessageDialog(this, "Usuario eliminado");
                loadUsuarios();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }
    
    // ============================================
    // PANEL DE TURISTAS
    // ============================================
    private JPanel createTuristasPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Tabla: IDTurista, Nombre, Apellido, DNI, Nacionalidad, Telefono, Email
        modelTuristas = new DefaultTableModel(
            new String[]{"ID", "Nombre", "Apellido", "DNI", "Nacionalidad", "Teléfono", "Email"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaTuristas = new JTable(modelTuristas);
        tablaTuristas.setRowHeight(30);
        JScrollPane scrollPane = new JScrollPane(tablaTuristas);
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAdd = new JButton("Agregar Turista");
        JButton btnEdit = new JButton("Editar");
        JButton btnDelete = new JButton("Eliminar");
        JButton btnRefresh = new JButton("Actualizar");
        
        btnAdd.setBackground(new Color(46, 204, 113));
        btnAdd.setForeground(Color.WHITE);
        btnEdit.setBackground(new Color(52, 152, 219));
        btnEdit.setForeground(Color.WHITE);
        btnDelete.setBackground(new Color(231, 76, 60));
        btnDelete.setForeground(Color.WHITE);
        
        btnAdd.addActionListener(e -> addTurista());
        btnEdit.addActionListener(e -> editTurista());
        btnDelete.addActionListener(e -> deleteTurista());
        btnRefresh.addActionListener(e -> loadTuristas());
        
        btnPanel.add(btnAdd);
        btnPanel.add(btnEdit);
        btnPanel.add(btnDelete);
        btnPanel.add(btnRefresh);
        
        panel.add(btnPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void loadTuristas() {
        modelTuristas.setRowCount(0);
        try (Connection conn = conexion.getConnection()) {
            String sql = "SELECT * FROM TURISTAA ORDER BY Nombre, Apellido";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                modelTuristas.addRow(new Object[]{
                    rs.getInt("IDTurista"),
                    rs.getString("Nombre"),
                    rs.getString("Apellido"),
                    rs.getString("DNI"),
                    rs.getString("Nacionalidad"),
                    rs.getString("Telefono"),
                    rs.getString("Email")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void addTurista() {
        JTextField txtNombre = new JTextField();
        JTextField txtApellido = new JTextField();
        JTextField txtDNI = new JTextField();
        JTextField txtNacionalidad = new JTextField();
        JTextField txtTelefono = new JTextField();
        JTextField txtEmail = new JTextField();
        
        Object[] message = {
            "Nombre:", txtNombre,
            "Apellido:", txtApellido,
            "DNI:", txtDNI,
            "Nacionalidad:", txtNacionalidad,
            "Teléfono:", txtTelefono,
            "Email:", txtEmail
        };
        
        int option = JOptionPane.showConfirmDialog(this, message, "Agregar Turista", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try (Connection conn = conexion.getConnection()) {
                String sql = "INSERT INTO TURISTAA (Nombre, Apellido, DNI, Nacionalidad, Telefono, Email) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement pst = conn.prepareStatement(sql);
                pst.setString(1, txtNombre.getText());
                pst.setString(2, txtApellido.getText());
                pst.setString(3, txtDNI.getText());
                pst.setString(4, txtNacionalidad.getText());
                pst.setString(5, txtTelefono.getText());
                pst.setString(6, txtEmail.getText());
                pst.executeUpdate();
                
                JOptionPane.showMessageDialog(this, "Turista agregado");
                loadTuristas();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }
    
    private void editTurista() {
        int selectedRow = tablaTuristas.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un turista");
            return;
        }
        
        int id = (int) modelTuristas.getValueAt(selectedRow, 0);
        JTextField txtNombre = new JTextField(modelTuristas.getValueAt(selectedRow, 1).toString());
        JTextField txtApellido = new JTextField(modelTuristas.getValueAt(selectedRow, 2).toString());
        JTextField txtDNI = new JTextField(modelTuristas.getValueAt(selectedRow, 3).toString());
        JTextField txtNacionalidad = new JTextField(modelTuristas.getValueAt(selectedRow, 4).toString());
        JTextField txtTelefono = new JTextField(modelTuristas.getValueAt(selectedRow, 5).toString());
        JTextField txtEmail = new JTextField(modelTuristas.getValueAt(selectedRow, 6).toString());
        
        Object[] message = {
            "Nombre:", txtNombre,
            "Apellido:", txtApellido,
            "DNI:", txtDNI,
            "Nacionalidad:", txtNacionalidad,
            "Teléfono:", txtTelefono,
            "Email:", txtEmail
        };
        
        int option = JOptionPane.showConfirmDialog(this, message, "Editar Turista", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try (Connection conn = conexion.getConnection()) {
                String sql = "UPDATE TURISTAA SET Nombre=?, Apellido=?, DNI=?, Nacionalidad=?, Telefono=?, Email=? WHERE IDTurista=?";
                PreparedStatement pst = conn.prepareStatement(sql);
                pst.setString(1, txtNombre.getText());
                pst.setString(2, txtApellido.getText());
                pst.setString(3, txtDNI.getText());
                pst.setString(4, txtNacionalidad.getText());
                pst.setString(5, txtTelefono.getText());
                pst.setString(6, txtEmail.getText());
                pst.setInt(7, id);
                pst.executeUpdate();
                
                JOptionPane.showMessageDialog(this, "Turista actualizado");
                loadTuristas();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }
    
    private void deleteTurista() {
        int selectedRow = tablaTuristas.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un turista");
            return;
        }
        
        int id = (int) modelTuristas.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, 
            "¿Eliminar este turista?", 
            "Confirmar", 
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = conexion.getConnection()) {
                String sql = "DELETE FROM TURISTAA WHERE IDTurista=?";
                PreparedStatement pst = conn.prepareStatement(sql);
                pst.setInt(1, id);
                pst.executeUpdate();
                
                JOptionPane.showMessageDialog(this, "Turista eliminado");
                loadTuristas();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }
    
    // ============================================
    // PANEL DE ALQUILERES
    // ============================================
    private JPanel createAlquileresPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Tabla: IDAlquiler, FechaDeInicio, HoraDeInicio, Duracion
        modelAlquileres = new DefaultTableModel(
            new String[]{"ID", "Fecha Inicio", "Hora Inicio", "Duración (hrs)", "Total Aprox."}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaAlquileres = new JTable(modelAlquileres);
        tablaAlquileres.setRowHeight(30);
        JScrollPane scrollPane = new JScrollPane(tablaAlquileres);
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnRefresh = new JButton("Actualizar");
        JButton btnDetails = new JButton("Ver Detalles");
        
        btnDetails.setBackground(new Color(52, 152, 219));
        btnDetails.setForeground(Color.WHITE);
        
        btnDetails.addActionListener(e -> verDetallesAlquiler());
        btnRefresh.addActionListener(e -> loadAlquileres());
        
        btnPanel.add(btnRefresh);
        btnPanel.add(btnDetails);
        
        panel.add(btnPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void loadAlquileres() {
        modelAlquileres.setRowCount(0);
        try (Connection conn = conexion.getConnection()) {
            String sql = "SELECT a.IDAlquiler, a.FechaDeInicio, a.HoraDeInicio, a.Duracion, " +
                        "SUM(r.TarifaPorHora * a.Duracion) as TotalAprox " +
                        "FROM Alquiler a " +
                        "LEFT JOIN DETALLEALQUILER da ON a.IDAlquiler = da.IDAlquiler " +
                        "LEFT JOIN RECURSOS r ON da.IDRecurso = r.IDRecursos " +
                        "GROUP BY a.IDAlquiler, a.FechaDeInicio, a.HoraDeInicio, a.Duracion " +
                        "ORDER BY a.FechaDeInicio DESC, a.HoraDeInicio DESC";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                modelAlquileres.addRow(new Object[]{
                    rs.getInt("IDAlquiler"),
                    rs.getDate("FechaDeInicio"),
                    rs.getTime("HoraDeInicio"),
                    rs.getInt("Duracion"),
                    String.format("S/ %.2f", rs.getDouble("TotalAprox"))
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void verDetallesAlquiler() {
        int selectedRow = tablaAlquileres.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un alquiler");
            return;
        }
        
        int idAlquiler = (int) modelAlquileres.getValueAt(selectedRow, 0);
        
        StringBuilder detalles = new StringBuilder();
        detalles.append("═══════════════════════════════════\n");
        detalles.append("  DETALLES DEL ALQUILER #").append(idAlquiler).append("\n");
        detalles.append("═══════════════════════════════════\n\n");
        
        try (Connection conn = conexion.getConnection()) {
            String sql = "SELECT da.*, t.Nombre, t.Apellido, r.Recurso, r.TarifaPorHora, " +
                        "p.PorcentajeDescuento, a.Duracion " +
                        "FROM DETALLEALQUILER da " +
                        "JOIN TURISTAA t ON da.IDTurista = t.IDTurista " +
                        "JOIN RECURSOS r ON da.IDRecurso = r.IDRecursos " +
                        "JOIN Alquiler a ON da.IDAlquiler = a.IDAlquiler " +
                        "LEFT JOIN PROMOCION p ON da.IDPromocion = p.IDPromocion " +
                        "WHERE da.IDAlquiler = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, idAlquiler);
            ResultSet rs = pst.executeQuery();
            
            double totalGeneral = 0;
            int count = 1;
            
            while (rs.next()) {
                String cliente = rs.getString("Nombre") + " " + rs.getString("Apellido");
                String recurso = rs.getString("Recurso");
                double tarifa = rs.getDouble("TarifaPorHora");
                int duracion = rs.getInt("Duracion");
                double subtotal = tarifa * duracion;
                
                detalles.append(count++).append(". ").append(recurso).append("\n");
                detalles.append("   Cliente: ").append(cliente).append("\n");
                detalles.append("   Tarifa: S/ ").append(String.format("%.2f", tarifa)).append(" x ").append(duracion).append(" hrs\n");
                detalles.append("   Subtotal: S/ ").append(String.format("%.2f", subtotal)).append("\n");
                
                if (rs.getObject("PorcentajeDescuento") != null) {
                    double descuento = rs.getDouble("PorcentajeDescuento");
                    double montoDescuento = subtotal * (descuento / 100);
                    subtotal -= montoDescuento;
                    detalles.append("   Descuento: ").append(descuento).append("% (-S/ ").append(String.format("%.2f", montoDescuento)).append(")\n");
                    detalles.append("   Total con descuento: S/ ").append(String.format("%.2f", subtotal)).append("\n");
                }
                detalles.append("\n");
                totalGeneral += subtotal;
            }
            
            detalles.append("═══════════════════════════════════\n");
            detalles.append("TOTAL GENERAL: S/ ").append(String.format("%.2f", totalGeneral)).append("\n");
            detalles.append("═══════════════════════════════════");
            
            JTextArea textArea = new JTextArea(detalles.toString());
            textArea.setEditable(false);
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(450, 350));
            
            JOptionPane.showMessageDialog(this, scrollPane, "Detalles del Alquiler", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
    
    // ============================================
    // PANEL DE PROMOCIONES
    // ============================================
    private JPanel createPromocionesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        modelPromociones = new DefaultTableModel(
            new String[]{"ID", "% Descuento", "Condiciones"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaPromociones = new JTable(modelPromociones);
        tablaPromociones.setRowHeight(30);
        JScrollPane scrollPane = new JScrollPane(tablaPromociones);
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAdd = new JButton("Agregar Promoción");
        JButton btnEdit = new JButton("Editar");
        JButton btnDelete = new JButton("Eliminar");
        JButton btnRefresh = new JButton("Actualizar");
        
        btnAdd.setBackground(new Color(46, 204, 113));
        btnAdd.setForeground(Color.WHITE);
        btnEdit.setBackground(new Color(52, 152, 219));
        btnEdit.setForeground(Color.WHITE);
        btnDelete.setBackground(new Color(231, 76, 60));
        btnDelete.setForeground(Color.WHITE);
        
        btnAdd.addActionListener(e -> addPromocion());
        btnEdit.addActionListener(e -> editPromocion());
        btnDelete.addActionListener(e -> deletePromocion());
        btnRefresh.addActionListener(e -> loadPromociones());
        
        btnPanel.add(btnAdd);
        btnPanel.add(btnEdit);
        btnPanel.add(btnDelete);
        btnPanel.add(btnRefresh);
        
        panel.add(btnPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void loadPromociones() {
        modelPromociones.setRowCount(0);
        try (Connection conn = conexion.getConnection()) {
            String sql = "SELECT * FROM PROMOCION ORDER BY PorcentajeDescuento DESC";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                modelPromociones.addRow(new Object[]{
                    rs.getInt("IDPromocion"),
                    rs.getDouble("PorcentajeDescuento") + "%",
                    rs.getString("Condiciones")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void addPromocion() {
        JTextField txtDescuento = new JTextField();
        JTextField txtCondiciones = new JTextField();
        
        Object[] message = {
            "% Descuento:", txtDescuento,
            "Condiciones:", txtCondiciones
        };
        
        int option = JOptionPane.showConfirmDialog(this, message, "Agregar Promoción", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try (Connection conn = conexion.getConnection()) {
                String sql = "INSERT INTO PROMOCION (PorcentajeDescuento, Condiciones) VALUES (?, ?)";
                PreparedStatement pst = conn.prepareStatement(sql);
                pst.setDouble(1, Double.parseDouble(txtDescuento.getText()));
                pst.setString(2, txtCondiciones.getText());
                pst.executeUpdate();
                
                JOptionPane.showMessageDialog(this, "Promoción agregada");
                loadPromociones();
            } catch (SQLException | NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }
    
    private void editPromocion() {
        int selectedRow = tablaPromociones.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona una promoción");
            return;
        }
        
        int id = (int) modelPromociones.getValueAt(selectedRow, 0);
        String descuentoStr = modelPromociones.getValueAt(selectedRow, 1).toString().replace("%", "");
        JTextField txtDescuento = new JTextField(descuentoStr);
        JTextField txtCondiciones = new JTextField(modelPromociones.getValueAt(selectedRow, 2).toString());
        
        Object[] message = {
            "% Descuento:", txtDescuento,
            "Condiciones:", txtCondiciones
        };
        
        int option = JOptionPane.showConfirmDialog(this, message, "Editar Promoción", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try (Connection conn = conexion.getConnection()) {
                String sql = "UPDATE PROMOCION SET PorcentajeDescuento=?, Condiciones=? WHERE IDPromocion=?";
                PreparedStatement pst = conn.prepareStatement(sql);
                pst.setDouble(1, Double.parseDouble(txtDescuento.getText()));
                pst.setString(2, txtCondiciones.getText());
                pst.setInt(3, id);
                pst.executeUpdate();
                
                JOptionPane.showMessageDialog(this, "Promoción actualizada");
                loadPromociones();
            } catch (SQLException | NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }
    
    private void deletePromocion() {
        int selectedRow = tablaPromociones.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona una promoción");
            return;
        }
        
        int id = (int) modelPromociones.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, 
            "¿Eliminar esta promoción?", 
            "Confirmar", 
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = conexion.getConnection()) {
                String sql = "DELETE FROM PROMOCION WHERE IDPromocion=?";
                PreparedStatement pst = conn.prepareStatement(sql);
                pst.setInt(1, id);
                pst.executeUpdate();
                
                JOptionPane.showMessageDialog(this, "Promoción eliminada");
                loadPromociones();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }
    
    // ============================================
    // MÉTODOS AUXILIARES
    // ============================================
    private void loadData() {
        loadRecursos();
        loadUsuarios();
        loadTuristas();
        loadAlquileres();
        loadPromociones();
    }
    
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Seguro que deseas cerrar sesión?",
            "Confirmar",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new login().setVisible(true);
        }
    }
}


package vista;
import javax.swing.*;
import javax.swing.table.*;
import modelo.Usuario;
import database.conexion;
import java.awt.*;
import java.sql.*;

public class adminframe extends JFrame {
    
    // Paleta de colores moderna
    private static final Color PRIMARY = new Color(99, 102, 241); 
    private static final Color PRIMARY_DARK = new Color(79, 70, 229);
    private static final Color SECONDARY = new Color(236, 72, 153); // Rosa
    private static final Color SUCCESS = new Color(34, 197, 94); // Verde
    private static final Color DANGER = new Color(239, 68, 68); // Rojo
    private static final Color WARNING = new Color(251, 146, 60); // Naranja
    private static final Color INFO = new Color(59, 130, 246); // Azul
    private static final Color BG_LIGHT = new Color(248, 250, 252);
    private static final Color BG_CARD = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(15, 23, 42);
    private static final Color TEXT_SECONDARY = new Color(100, 116, 139);
    private static final Color BORDER_COLOR = new Color(226, 232, 240);
    
    private Usuario currentUser;
    private JTable tablaRecursos, tablaUsuarios, tablaTuristas, tablaAlquileres, tablaPromociones;
    private DefaultTableModel modelRecursos, modelUsuarios, modelTuristas, modelAlquileres, modelPromociones;
    private JTabbedPane tabbedPane;
    private JLabel lblTotalIngresos;
    private JTable tablaIngresosDia, tablaControlAlquileres;
    private DefaultTableModel modelIngresosDia, modelControlAlquileres;
    private JPanel graficaPanel;
    
    public adminframe(Usuario user) {
        this.currentUser = user;
        
        setTitle("Sistema de Gestión - Panel Administrador");
        setSize(1200, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_LIGHT);
        
        // Layout principal
        setLayout(new BorderLayout(0, 0));
        
        // Header moderno
        add(createModernHeader(), BorderLayout.NORTH);
        
        // Contenido con tabs
        add(createModernTabbedPane(), BorderLayout.CENTER);
        
        loadData();
    }
    
    // ============================================
    // HEADER MODERNO
    // ============================================
    private JPanel createModernHeader() {
        JPanel header = new JPanel(new BorderLayout(20, 0));
        header.setBackground(BG_CARD);
        header.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
        BorderFactory.createEmptyBorder(20, 30, 20, 30)
        ));

    // =========================
    // LADO IZQUIERDO (TÍTULO)
    // =========================
    JLabel titleLabel = new JLabel("Panel de Administración");
    titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
    titleLabel.setForeground(TEXT_PRIMARY);

    header.add(titleLabel, BorderLayout.WEST);

    // =========================
    // LADO DERECHO (BIENVENIDA + FOTO)
    // =========================
    JPanel rightPanel = new JPanel(new BorderLayout(10, 5));
    rightPanel.setOpaque(false);

    JLabel lblBienvenido = new JLabel(
        "BIENVENIDO " + currentUser.getNombre().toUpperCase()
    );
    lblBienvenido.setFont(new Font("Segoe UI", Font.BOLD, 14));
    lblBienvenido.setForeground(TEXT_PRIMARY);
    lblBienvenido.setHorizontalAlignment(SwingConstants.RIGHT);

    // Cargar imagen
    ImageIcon icon = new ImageIcon(
        getClass().getResource("/Imagen/foto.jpg")
    );
    Image img = icon.getImage().getScaledInstance(45, 45, Image.SCALE_SMOOTH);
    JLabel lblFoto = new JLabel(new ImageIcon(img));

    JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
    userPanel.setOpaque(false);
    userPanel.add(lblBienvenido);
    userPanel.add(lblFoto);

    // =========================
    // BOTÓN CERRAR SESIÓN (ABAJO)
    // =========================
    JButton btnLogout = createModernButton("Cerrar Sesión", DANGER, "⎋");
    btnLogout.addActionListener(e -> logout());

    JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
    logoutPanel.setOpaque(false);
    logoutPanel.add(btnLogout);

    rightPanel.add(userPanel, BorderLayout.NORTH);
    rightPanel.add(logoutPanel, BorderLayout.SOUTH);

    header.add(rightPanel, BorderLayout.EAST);

    return header;
    }
    
    // ============================================
    // TABS MODERNOS
    // ============================================
    private JTabbedPane createModernTabbedPane() {
        tabbedPane = new JTabbedPane(JTabbedPane.LEFT);
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabbedPane.setBackground(BG_LIGHT);
        tabbedPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Personalizar UI de tabs
        UIManager.put("TabbedPane.selected", PRIMARY);
        UIManager.put("TabbedPane.contentAreaColor", BG_LIGHT);
        
        tabbedPane.addTab("  🏖️  Recursos       ", createRecursosPanel());
        tabbedPane.addTab("  👥  Usuarios       ", createUsuariosPanel());
        tabbedPane.addTab("  🧳  Turistas       ", createTuristasPanel());
        tabbedPane.addTab("  📋  Alquileres     ", createAlquileresPanel());
        tabbedPane.addTab("  🎁  Promociones    ", createPromocionesPanel());
        tabbedPane.addTab("  📊  Reportes       ", createReportesPanel());
        return tabbedPane;
    }
    
    // ============================================
    // PANEL DE RECURSOS MODERNO
    // ============================================
    private JPanel createRecursosPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(BG_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Header del panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Gestión de Recursos");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(TEXT_PRIMARY);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Botones de acción
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);
        
        JButton btnAdd = createModernButton("Agregar Recurso", SUCCESS, "+");
        JButton btnEdit = createModernButton("Editar", INFO, "✎");
        JButton btnDelete = createModernButton("Eliminar", DANGER, "🗑");
        JButton btnRefresh = createModernButton("Actualizar", PRIMARY, "↻");
        
        btnAdd.addActionListener(e -> addRecurso());
        btnEdit.addActionListener(e -> editRecurso());
        btnDelete.addActionListener(e -> deleteRecurso());
        btnRefresh.addActionListener(e -> loadRecursos());
        
        btnPanel.add(btnAdd);
        btnPanel.add(btnEdit);
        btnPanel.add(btnDelete);
        btnPanel.add(btnRefresh);
        
        headerPanel.add(btnPanel, BorderLayout.EAST);
        
        // Tabla moderna
        modelRecursos = new DefaultTableModel(
            new String[]{"ID", "Recurso", "Descripción", "Tarifa/Hora", "Estado"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaRecursos = createModernTable(modelRecursos);
        JScrollPane scrollPane = createModernScrollPane(tablaRecursos);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    // ============================================
    // PANEL DE USUARIOS MODERNO
    // ============================================
    private JPanel createUsuariosPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(BG_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Gestión de Usuarios");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(TEXT_PRIMARY);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);
        
        JButton btnAdd = createModernButton("Nuevo Usuario", SUCCESS, "+");
        JButton btnDelete = createModernButton("Eliminar", DANGER, "🗑");
        JButton btnRefresh = createModernButton("Actualizar", PRIMARY, "↻");
        
        btnAdd.addActionListener(e -> addUsuario());
        btnDelete.addActionListener(e -> deleteUsuario());
        btnRefresh.addActionListener(e -> loadUsuarios());
        
        btnPanel.add(btnAdd);
        btnPanel.add(btnDelete);
        btnPanel.add(btnRefresh);
        
        headerPanel.add(btnPanel, BorderLayout.EAST);
        
        modelUsuarios = new DefaultTableModel(
            new String[]{"ID", "Tipo", "Nombre"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaUsuarios = createModernTable(modelUsuarios);
        JScrollPane scrollPane = createModernScrollPane(tablaUsuarios);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    // ============================================
    // PANEL DE TURISTAS MODERNO
    // ============================================
    private JPanel createTuristasPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(BG_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Gestión de Turistas");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(TEXT_PRIMARY);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);
        
        JButton btnAdd = createModernButton("Nuevo Turista", SUCCESS, "+");
        JButton btnEdit = createModernButton("Editar", INFO, "✎");
        JButton btnDelete = createModernButton("Eliminar", DANGER, "🗑");
        JButton btnRefresh = createModernButton("Actualizar", PRIMARY, "↻");
        
        btnAdd.addActionListener(e -> addTurista());
        btnEdit.addActionListener(e -> editTurista());
        btnDelete.addActionListener(e -> {

            int fila = tablaTuristas.getSelectedRow();

            if (fila == -1) {
                JOptionPane.showMessageDialog(
                    this,
                    "Seleccione un turista",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            String idTurista =
            tablaTuristas.getValueAt(fila, 0).toString();

            deleteTurista(idTurista);
        });
        btnRefresh.addActionListener(e -> loadTuristas());
        
        btnPanel.add(btnAdd);
        btnPanel.add(btnEdit);
        btnPanel.add(btnDelete);
        btnPanel.add(btnRefresh);
        
        headerPanel.add(btnPanel, BorderLayout.EAST);
        
        modelTuristas = new DefaultTableModel(
            new String[]{"ID", "Nombre", "Apellido", "DNI", "Nacionalidad", "Teléfono", "Email"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaTuristas = createModernTable(modelTuristas);
        JScrollPane scrollPane = createModernScrollPane(tablaTuristas);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    // ============================================
    // PANEL DE ALQUILERES MODERNO
    // ============================================
   private JPanel createAlquileresPanel() {
    JPanel panel = new JPanel(new BorderLayout(15, 15));
    panel.setBackground(BG_LIGHT);
    panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

    JPanel headerPanel = new JPanel(new BorderLayout());
    headerPanel.setOpaque(false);

    JLabel titleLabel = new JLabel("Gestión de Alquileres");
    titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
    titleLabel.setForeground(TEXT_PRIMARY);
    headerPanel.add(titleLabel, BorderLayout.WEST);

    
    lblTotalIngresos = new JLabel("Total Ingresos: S/ 0.00", SwingConstants.CENTER);
    lblTotalIngresos.setFont(new Font("Segoe UI", Font.BOLD, 16));
    lblTotalIngresos.setForeground(SUCCESS);


    headerPanel.add(lblTotalIngresos, BorderLayout.CENTER);

    JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
    btnPanel.setOpaque(false);

    JButton btnDetails = createModernButton("Ver Detalles", INFO, "👁");
    JButton btnRefresh = createModernButton("Actualizar", PRIMARY, "↻");

    btnDetails.addActionListener(e -> verDetallesAlquiler());
    btnRefresh.addActionListener(e -> {
        loadAlquileres();
        cargarTotalIngresos(); 
    });

    btnPanel.add(btnDetails);
    btnPanel.add(btnRefresh);

    headerPanel.add(btnPanel, BorderLayout.EAST);

    modelAlquileres = new DefaultTableModel(
        new String[]{"ID", "Turista", "Recurso", "Fecha Inicio", "Hora Inicio", "Duración (hrs)", "Total Aprox."}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    tablaAlquileres = createModernTable(modelAlquileres);
    JScrollPane scrollPane = createModernScrollPane(tablaAlquileres);

    panel.add(headerPanel, BorderLayout.NORTH);
    panel.add(scrollPane, BorderLayout.CENTER);

    return panel;
    }

    
    // ============================================
    // PANEL DE PROMOCIONES MODERNO
    // ============================================
    private JPanel createPromocionesPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(BG_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Gestión de Promociones");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(TEXT_PRIMARY);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);
        
        JButton btnAdd = createModernButton("Nueva Promoción", SUCCESS, "+");
        JButton btnEdit = createModernButton("Editar", INFO, "✎");
        JButton btnDelete = createModernButton("Eliminar", DANGER, "🗑");
        JButton btnRefresh = createModernButton("Actualizar", PRIMARY, "↻");
        
        btnAdd.addActionListener(e -> addPromocion());
        btnEdit.addActionListener(e -> editPromocion());
        btnDelete.addActionListener(e -> deletePromocion());
        btnRefresh.addActionListener(e -> loadPromociones());
        
        btnPanel.add(btnAdd);
        btnPanel.add(btnEdit);
        btnPanel.add(btnDelete);
        btnPanel.add(btnRefresh);
        
        headerPanel.add(btnPanel, BorderLayout.EAST);
        
        modelPromociones = new DefaultTableModel(
            new String[]{"ID", "% Descuento", "Condiciones"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaPromociones = createModernTable(modelPromociones);
        JScrollPane scrollPane = createModernScrollPane(tablaPromociones);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    // ============================================
    // COMPONENTES REUTILIZABLES MODERNOS
    // ============================================
    private JButton createModernButton(String text, Color bgColor, String icon) {
        JButton btn = new JButton(icon + "  " + text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(150, 38));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        
        // Efecto hover
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(bgColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(bgColor);
            }
        });
        
        return btn;
    }
    
    private JTable createModernTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(45);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(224, 231, 255));
        table.setSelectionForeground(TEXT_PRIMARY);
        table.setBackground(BG_CARD);
        
        // Header personalizado
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(BG_LIGHT);
        header.setForeground(TEXT_SECONDARY);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, PRIMARY));
        header.setPreferredSize(new Dimension(header.getWidth(), 45));
        
        // Renderer para celdas con padding
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
                    BorderFactory.createEmptyBorder(10, 15, 10, 15)
                ));
                return c;
            }
        };
        
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }
        
        return table;
    }
    
    private JScrollPane createModernScrollPane(JTable table) {
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder()
        ));
        scrollPane.setBackground(BG_CARD);
        scrollPane.getViewport().setBackground(BG_CARD);
        return scrollPane;
    }
    
    // ============================================
    // MÉTODOS DE LÓGICA (SIN CAMBIOS)
    // ============================================
    private void loadRecursos() {
        modelRecursos.setRowCount(0);
        try (Connection conn = conexion.getConnection()) {
            String sql = "SELECT * FROM RECURSOS ORDER BY IDRecurso";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
String estadoBD = rs.getString("Estado");
    String estadoMostrar;
    
    // Solo cambiar visualización a "Disponible" o "No Disponible"
    if (estadoBD.equalsIgnoreCase("disponible")) {
        estadoMostrar = "Disponible";
    } else {
        estadoMostrar = "No Disponible";  
    }

                modelRecursos.addRow(new Object[]{
                    rs.getString("IDRecurso"),
                    rs.getString("Recurso"),
                    rs.getString("Descripcion"),
                    String.format("S/ %.2f", rs.getDouble("TarifaPorHora")),
                    estadoMostrar
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar recursos: " + e.getMessage());
        }
    }
    
    private void addRecurso() {
        JTextField txtRecurso = new JTextField();
        JTextArea txtDescripcion = new JTextArea(3, 20);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        JScrollPane scrollDesc = new JScrollPane(txtDescripcion);
        
        JTextField txtTarifa = new JTextField();
        JComboBox<String> cmbEstado = new JComboBox<>(new String[]{"disponible", "alquilado", "mantenimiento"});
        
        Object[] message = {
            "Recurso:", txtRecurso,
            "Descripción:", scrollDesc,
            "Tarifa por Hora:", txtTarifa,
            "Estado:", cmbEstado
        };
        
        int option = JOptionPane.showConfirmDialog(this, message, "Agregar Recurso", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try (Connection conn = conexion.getConnection()) {
                if (txtRecurso.getText().trim().isEmpty() || txtDescripcion.getText().trim().isEmpty() || txtTarifa.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Por favor completa todos los campos");
                    return;
                }
                
                String descripcion = txtDescripcion.getText().trim();
                if (descripcion.length() > 255) {
                    int confirm = JOptionPane.showConfirmDialog(this,
                        "La descripción es muy larga (" + descripcion.length() + " caracteres).\n" +
                        "Se recortará a 255 caracteres. ¿Continuar?",
                        "Advertencia",
                        JOptionPane.YES_NO_OPTION);
                    
                    if (confirm != JOptionPane.YES_OPTION) {
                        return;
                    }
                    descripcion = descripcion.substring(0, 255);
                }
                
                String nuevoID = generarNuevoIDRecurso(conn);
                
                String sql = "INSERT INTO RECURSOS (IDRecurso, Recurso, Descripcion, TarifaPorHora, Estado) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement pst = conn.prepareStatement(sql);
                pst.setString(1, nuevoID);
                pst.setString(2, txtRecurso.getText().trim());
                pst.setString(3, descripcion);
                pst.setDouble(4, Double.parseDouble(txtTarifa.getText().trim()));
                pst.setString(5, cmbEstado.getSelectedItem().toString());
                pst.executeUpdate();
                
                JOptionPane.showMessageDialog(this, "Recurso agregado exitosamente con ID: " + nuevoID);
                loadRecursos();
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error de BD: " + e.getMessage());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Error: La tarifa debe ser un número válido");
            }
        }
    }
    
    private String generarNuevoIDRecurso(Connection conn) throws SQLException {
        String prefijo = "R";
        int siguienteNumero = 1;
        
        String sql = "SELECT TOP 1 IDRecurso FROM RECURSOS WHERE IDRecurso LIKE ? ORDER BY IDRecurso DESC";
        PreparedStatement pst = conn.prepareStatement(sql);
        pst.setString(1, prefijo + "%");
        ResultSet rs = pst.executeQuery();
        
        if (rs.next()) {
            String ultimoID = rs.getString("IDRecurso");
            String numeroStr = ultimoID.substring(prefijo.length());
            int numero = Integer.parseInt(numeroStr);
            siguienteNumero = numero + 1;
        }
        
        return String.format("%s%03d", prefijo, siguienteNumero);
    }
    
    private void editRecurso() {
        int selectedRow = tablaRecursos.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un recurso");
            return;
        }
        
        String id = modelRecursos.getValueAt(selectedRow, 0).toString();
        JTextField txtRecurso = new JTextField(modelRecursos.getValueAt(selectedRow, 1).toString());
        
        JTextArea txtDescripcion = new JTextArea(3, 20);
        txtDescripcion.setText(modelRecursos.getValueAt(selectedRow, 2).toString());
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        JScrollPane scrollDesc = new JScrollPane(txtDescripcion);
        
        String tarifaStr = modelRecursos.getValueAt(selectedRow, 3).toString().replace("S/ ", "");
        JTextField txtTarifa = new JTextField(tarifaStr);
        JComboBox<String> cmbEstado = new JComboBox<>(new String[]{"DISPONIBLE", "ALQUILADO", "MANTENIMIENTO"});
        cmbEstado.setSelectedItem(modelRecursos.getValueAt(selectedRow, 4).toString());
        
        Object[] message = {
            "Recurso:", txtRecurso,
            "Descripción:", scrollDesc,
            "Tarifa por Hora:", txtTarifa,
            "Estado:", cmbEstado
        };
        
        int option = JOptionPane.showConfirmDialog(this, message, "Editar Recurso", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try (Connection conn = conexion.getConnection()) {
                String descripcion = txtDescripcion.getText().trim();
                if (descripcion.length() > 255) {
                    int confirm = JOptionPane.showConfirmDialog(this,
                        "La descripción es muy larga (" + descripcion.length() + " caracteres).\n" +
                        "Se recortará a 255 caracteres. ¿Continuar?",
                        "Advertencia",
                        JOptionPane.YES_NO_OPTION);
                    
                    if (confirm != JOptionPane.YES_OPTION) {
                        return;
                    }
                    descripcion = descripcion.substring(0, 255);
                }
                
                String sql = "UPDATE RECURSOS SET Recurso=?, Descripcion=?, TarifaPorHora=?, Estado=? WHERE IDRecurso=?";
                PreparedStatement pst = conn.prepareStatement(sql);
                pst.setString(1, txtRecurso.getText().trim());
                pst.setString(2, descripcion);
                pst.setDouble(3, Double.parseDouble(txtTarifa.getText().trim()));
                pst.setString(4, cmbEstado.getSelectedItem().toString());
                pst.setString(5, id);
                pst.executeUpdate();
                
                JOptionPane.showMessageDialog(this, "Recurso actualizado");
                loadRecursos();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error de BD: " + e.getMessage());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Error: La tarifa debe ser un número válido");
            }
        }
    }
    
    private void deleteRecurso() {
        int selectedRow = tablaRecursos.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un recurso");
            return;
        }
        
        String id = modelRecursos.getValueAt(selectedRow, 0).toString();
        int confirm = JOptionPane.showConfirmDialog(this, 
            "¿Eliminar este recurso?", 
            "Confirmar", 
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = conexion.getConnection()) {
                String sql = "DELETE FROM RECURSOS WHERE IDRecurso=?";
                PreparedStatement pst = conn.prepareStatement(sql);
                pst.setString(1, id);
                pst.executeUpdate();
                
                JOptionPane.showMessageDialog(this, "Recurso eliminado");
                loadRecursos();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }
    
    private void loadUsuarios() {
        modelUsuarios.setRowCount(0);
        try (Connection conn = conexion.getConnection()) {
            String sql = "SELECT IDUsuario, TipoDeUsuario, Nombre FROM USUARIO ORDER BY IDUsuario";
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
                String sql = "INSERT INTO USUARIO (IDUsuario, TipoDeUsuario, Nombre, Contraseña) VALUES (?, ?, ?, ?)";
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
    
    private void loadTuristas() {
        modelTuristas.setRowCount(0);
        try (Connection conn = conexion.getConnection()) {
            String sql = "SELECT * FROM TURISTAA ORDER BY IDTurista";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                modelTuristas.addRow(new Object[]{
                    rs.getString("IDTurista"),
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
            JOptionPane.showMessageDialog(this, "Error al cargar turistas: " + e.getMessage());
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
                String nuevoID = generarNuevoIDTurista(conn);
                
                String sql = "INSERT INTO TURISTAA (IDTurista, Nombre, Apellido, DNI, Nacionalidad, Telefono, Email) VALUES (?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement pst = conn.prepareStatement(sql);
                pst.setString(1, nuevoID);
                pst.setString(2, txtNombre.getText());
                pst.setString(3, txtApellido.getText());
                pst.setString(4, txtDNI.getText());
                pst.setString(5, txtNacionalidad.getText());
                pst.setString(6, txtTelefono.getText());
                pst.setString(7, txtEmail.getText());
                pst.executeUpdate();
                
                JOptionPane.showMessageDialog(this, "Turista agregado con ID: " + nuevoID);
                loadTuristas();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }
    
    private String generarNuevoIDTurista(Connection conn) throws SQLException {
        String prefijo = "T";
        int siguienteNumero = 1;
        
        String sql = "SELECT TOP 1 IDTurista FROM TURISTAA WHERE IDTurista LIKE ? ORDER BY IDTurista DESC";
        PreparedStatement pst = conn.prepareStatement(sql);
        pst.setString(1, prefijo + "%");
        ResultSet rs = pst.executeQuery();
        
        if (rs.next()) {
            String ultimoID = rs.getString("IDTurista");
            String numeroStr = ultimoID.substring(prefijo.length());
            int numero = Integer.parseInt(numeroStr);
            siguienteNumero = numero + 1;
        }
        
        return String.format("%s%03d", prefijo, siguienteNumero);
    }
    
    private void editTurista() {
        int selectedRow = tablaTuristas.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un turista");
            return;
        }
        
        String id = modelTuristas.getValueAt(selectedRow, 0).toString();
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
                pst.setString(7, id);
                pst.executeUpdate();
                
                JOptionPane.showMessageDialog(this, "Turista actualizado");
                loadTuristas();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }
    
    private void deleteTurista(String idTurista) {

    String sqlCheck = "SELECT COUNT(*) FROM DETALLEALQUILER WHERE IDTurista = ?";
    String sqlDelete = "DELETE FROM TURISTAA WHERE IDTurista = ?";

    try (Connection con = conexion.getConnection()) {

        // 1️⃣ Verificar si el turista tiene alquileres
        try (PreparedStatement psCheck = con.prepareStatement(sqlCheck)) {
            psCheck.setString(1, idTurista);
            ResultSet rs = psCheck.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(this,
                    "No se puede eliminar a un turista que ya haya hecho un alquiler");
                return;
            }
        }

        // 2️⃣ Eliminar turista
        try (PreparedStatement psDelete = con.prepareStatement(sqlDelete)) {
            psDelete.setString(1, idTurista);
            psDelete.executeUpdate();
        }

        JOptionPane.showMessageDialog(this, "Turista eliminado correctamente");

    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error al eliminar turista");
    }
}
    
    private void loadAlquileres() {
        modelAlquileres.setRowCount(0);
        try (Connection conn = conexion.getConnection()) {
            String sql =
                    "SELECT a.IDAlquiler, " +
                    "t.Nombre AS Turista, " +
                    "r.Recurso, " +
                    "a.FechaDeInicio, " +
                    "a.HoraDeInicio, " +
                    "a.Duracion, " +
                    "r.TarifaPorHora * a.Duracion AS Total " +
                    "FROM ALQUILER a " +
                    "LEFT JOIN DETALLEALQUILER d ON a.IDAlquiler = d.IDAlquiler " +
                    "LEFT JOIN TURISTAA t ON d.IDTurista = t.IDTurista " +
                    "LEFT JOIN RECURSOS r ON d.IDRecurso = r.IDRecurso " +
                    "ORDER BY a.IDAlquiler";

            PreparedStatement pst = conn.prepareStatement(sql); 
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                modelAlquileres.addRow(new Object[]{
                    rs.getString("IDAlquiler"),
                    rs.getString("Turista"),
                    rs.getString("Recurso"),
                    rs.getDate("FechaDeInicio"),
                    rs.getTime("HoraDeInicio"),
                    rs.getInt("Duracion"),
                    String.format("S/ %.2f", rs.getDouble("Total"))
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

        String idAlquiler = modelAlquileres.getValueAt(selectedRow, 0).toString();

        StringBuilder detalles = new StringBuilder();
        detalles.append("═══════════════════════════════════\n");
        detalles.append("  DETALLES DEL ALQUILER #").append(idAlquiler).append("\n");
        detalles.append("═══════════════════════════════════\n\n");

        try (Connection conn = conexion.getConnection()) {
            String sql =
                "SELECT t.Nombre, t.Apellido, r.Recurso, r.TarifaPorHora, " +
                "a.Duracion, p.PorcentajeDescuento " +
                "FROM DETALLEALQUILER da " +
                "JOIN TURISTAA t ON da.IDTurista = t.IDTurista " +
                "JOIN RECURSOS r ON da.IDRecurso = r.IDRecurso " +
                "JOIN ALQUILER a ON da.IDAlquiler = a.IDAlquiler " +
                "LEFT JOIN PROMOCION p ON da.IDPromocion = p.IDPromocion " +
                "WHERE da.IDAlquiler = ?";

            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, idAlquiler);
            ResultSet rs = pst.executeQuery();

            double totalGeneral = 0;
            int i = 1;

            while (rs.next()) {
                String cliente = rs.getString("Nombre") + " " + rs.getString("Apellido");
                String recurso = rs.getString("Recurso");
                double tarifa = rs.getDouble("TarifaPorHora");
                int duracion = rs.getInt("Duracion");

                double subtotal = tarifa * duracion;

                detalles.append(i++).append(". ").append(recurso).append("\n");
                detalles.append("   Cliente: ").append(cliente).append("\n");
                detalles.append("   Tarifa: S/ ").append(String.format("%.2f", tarifa))
                        .append(" x ").append(duracion).append(" hrs\n");

                if (rs.getObject("PorcentajeDescuento") != null) {
                    double desc = rs.getDouble("PorcentajeDescuento");
                    double montoDesc = subtotal * desc / 100;
                    subtotal -= montoDesc;
                    detalles.append("   Descuento: ").append(desc)
                            .append("% (-S/ ").append(String.format("%.2f", montoDesc)).append(")\n");
                }

                detalles.append("   Subtotal: S/ ")
                        .append(String.format("%.2f", subtotal)).append("\n\n");

                totalGeneral += subtotal;
            }

            detalles.append("═══════════════════════════════════\n");
            detalles.append("TOTAL GENERAL: S/ ")
                    .append(String.format("%.2f", totalGeneral)).append("\n");
            detalles.append("═══════════════════════════════════");

            JTextArea area = new JTextArea(detalles.toString());
            area.setEditable(false);
            area.setFont(new Font("Monospaced", Font.PLAIN, 12));

            JOptionPane.showMessageDialog(
                this,
                new JScrollPane(area),
                "Detalles del Alquiler",
                JOptionPane.INFORMATION_MESSAGE
            );
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
    
    private void loadPromociones() {
        modelPromociones.setRowCount(0);
        try (Connection conn = conexion.getConnection()) {
            String sql =
                "SELECT * FROM PROMOCION " +
                "ORDER BY CAST(SUBSTRING(IDPromocion, 2, LEN(IDPromocion)) AS INT) ASC";

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                modelPromociones.addRow(new Object[]{
                    rs.getString("IDPromocion"),
                    rs.getString("PorcentajeDescuento") + "%",
                    rs.getString("Condiciones")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error al cargar promociones: " + e.getMessage());
        }
    }
    
    private void addPromocion() {
        JTextField txtDescuento = new JTextField();
        JTextField txtCondiciones = new JTextField();

        Object[] message = {
            "% Descuento:", txtDescuento,
            "Condiciones:", txtCondiciones
        };

        int option = JOptionPane.showConfirmDialog(
                this,
                message,
                "Agregar Promoción",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (option == JOptionPane.OK_OPTION) {
            if (txtDescuento.getText().trim().isEmpty() ||
                txtCondiciones.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Completa todos los campos");
                return;
            }

            try (Connection conn = conexion.getConnection()) {
                String nuevoID = generarNuevoIDPromocion(conn);

                String sql = """
                    INSERT INTO PROMOCION (IDPromocion, PorcentajeDescuento, Condiciones)
                    VALUES (?, ?, ?)
                """;

                PreparedStatement pst = conn.prepareStatement(sql);
                pst.setString(1, nuevoID);
                pst.setDouble(2, Double.parseDouble(txtDescuento.getText()));
                pst.setString(3, txtCondiciones.getText());
                pst.executeUpdate();

                JOptionPane.showMessageDialog(this,
                    "Promoción agregada con ID " + nuevoID);

                loadPromociones();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "El descuento debe ser un número");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error SQL: " + e.getMessage());
            }
        }
    }

    private String generarNuevoIDPromocion(Connection conn) throws SQLException {
        String prefijo = "P";
        int siguienteNumero = 1;
        
        String sql = "SELECT TOP 1 IDPromocion FROM PROMOCION WHERE IDPromocion LIKE ? ORDER BY IDPromocion DESC";
        PreparedStatement pst = conn.prepareStatement(sql);
        pst.setString(1, prefijo + "%");
        ResultSet rs = pst.executeQuery();
        
        if (rs.next()) {
            String ultimoID = rs.getString("IDPromocion");
            String numeroStr = ultimoID.substring(prefijo.length());
            int numero = Integer.parseInt(numeroStr);
            siguienteNumero = numero + 1;
        }
        
        return String.format("%s%03d", prefijo, siguienteNumero);
    }
    
    private void editPromocion() {
        int selectedRow = tablaPromociones.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecciona una promoción de la tabla para editar.");
            return;
        }

        String id = modelPromociones.getValueAt(selectedRow, 0).toString();
        String descuentoActual = modelPromociones.getValueAt(selectedRow, 1).toString().replace("%", "").trim();
        String condicionesActuales = modelPromociones.getValueAt(selectedRow, 2).toString();

        JTextField txtDescuento = new JTextField(descuentoActual);
        JTextField txtCondiciones = new JTextField(condicionesActuales);

        Object[] message = {
            "ID Promoción: " + id,
            "\n% Nuevo Descuento:", txtDescuento,
            "Nuevas Condiciones:", txtCondiciones
        };

        int option = JOptionPane.showConfirmDialog(
            this, 
            message, 
            "Editar Promoción " + id, 
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        if (option == JOptionPane.OK_OPTION) {
            if (txtDescuento.getText().isEmpty() || txtCondiciones.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.");
                return;
            }

            try (Connection conn = conexion.getConnection()) {
                String sql = "UPDATE PROMOCION SET PorcentajeDescuento = ?, Condiciones = ? WHERE IDPromocion = ?";
                PreparedStatement pst = conn.prepareStatement(sql);
                
                double nuevoDesc = Double.parseDouble(txtDescuento.getText());
                
                pst.setDouble(1, nuevoDesc);
                pst.setString(2, txtCondiciones.getText());
                pst.setString(3, id);

                int resultado = pst.executeUpdate();

                if (resultado > 0) {
                    JOptionPane.showMessageDialog(this, "¡Promoción actualizada con éxito!");
                    loadPromociones();
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Error: El descuento debe ser un valor numérico (ej: 15.5)");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error de base de datos: " + e.getMessage());
            }
        }
    }
    
    private void deletePromocion() {
        int selectedRow = tablaPromociones.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona una promoción");
            return;
        }
        
        String id = modelPromociones.getValueAt(selectedRow, 0).toString();
        int confirm = JOptionPane.showConfirmDialog(this, 
            "¿Eliminar esta promoción?", 
            "Confirmar", 
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = conexion.getConnection()) {
                String sql = "DELETE FROM PROMOCION WHERE IDPromocion=?";
                PreparedStatement pst = conn.prepareStatement(sql);
                pst.setString(1, id);
                pst.executeUpdate();
                
                JOptionPane.showMessageDialog(this, "Promoción eliminada");
                loadPromociones();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }
    private JPanel createReportesPanel() {

    JPanel panel = new JPanel(new BorderLayout(15, 15));
    panel.setBackground(BG_LIGHT);
    panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

    // =============================
    // PARTE SUPERIOR: INGRESOS POR DÍA
    // =============================
    JPanel ingresosPanel = new JPanel(new BorderLayout(10, 10));
    ingresosPanel.setBackground(BG_CARD);
    ingresosPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

    JLabel lblIngresos = new JLabel("📅 Ingresos por Día");
    lblIngresos.setFont(new Font("Segoe UI", Font.BOLD, 18));

    modelIngresosDia = new DefaultTableModel(
        new String[]{"Fecha", "Total Ingresos (S/)"}, 0
    ) {
        public boolean isCellEditable(int r, int c) { return false; }
    };

    tablaIngresosDia = createModernTable(modelIngresosDia);

    ingresosPanel.add(lblIngresos, BorderLayout.NORTH);
    graficaPanel = new GraficaIngresosPanel();
    graficaPanel.setPreferredSize(new Dimension(600, 250));

    JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
    centerPanel.setOpaque(false);
    centerPanel.add(createModernScrollPane(tablaIngresosDia), BorderLayout.CENTER);
    centerPanel.add(graficaPanel, BorderLayout.SOUTH);

    ingresosPanel.add(centerPanel, BorderLayout.CENTER);

    // =============================
    // PARTE INFERIOR: CONTROL DE ALQUILERES
    // =============================
    JPanel controlPanel = new JPanel(new BorderLayout(10, 10));
    controlPanel.setBackground(BG_CARD);
    controlPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

    JLabel lblControl = new JLabel("🛠 Control de Alquileres por Día");
    lblControl.setFont(new Font("Segoe UI", Font.BOLD, 18));

    JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    btnPanel.setOpaque(false);

    JButton btnEditar = createModernButton("Editar", INFO, "✎");
    JButton btnEliminar = createModernButton("Eliminar", DANGER, "🗑");
    JButton btnActualizar = createModernButton("Actualizar", PRIMARY, "↻");

    btnEditar.addActionListener(e -> editarAlquilerReporte());
    btnEliminar.addActionListener(e -> eliminarAlquilerReporte());
    btnActualizar.addActionListener(e -> {
        loadIngresosPorDia();
        loadControlAlquileres();
        graficaPanel.repaint();
    });

    btnPanel.add(btnEditar);
    btnPanel.add(btnEliminar);
    btnPanel.add(btnActualizar);

    modelControlAlquileres = new DefaultTableModel(
        new String[]{"ID", "Fecha", "Turista", "Recurso", "Duración", "Total (S/)"}, 0
    ) {
        public boolean isCellEditable(int r, int c) { return false; }
    };

    tablaControlAlquileres = createModernTable(modelControlAlquileres);

    controlPanel.add(lblControl, BorderLayout.NORTH);
    controlPanel.add(btnPanel, BorderLayout.SOUTH);
    controlPanel.add(createModernScrollPane(tablaControlAlquileres), BorderLayout.CENTER);

    // =============================
    // DISTRIBUCIÓN FINAL
    // =============================
    panel.add(ingresosPanel, BorderLayout.NORTH);
    panel.add(controlPanel, BorderLayout.CENTER);

    loadIngresosPorDia();
    loadControlAlquileres();
    
    return panel;
    }
    private void loadIngresosPorDia() {

    modelIngresosDia.setRowCount(0);

    try (Connection conn = conexion.getConnection()) {

        String sql = """
            SELECT a.FechaDeInicio AS Fecha,
                   SUM(r.TarifaPorHora * a.Duracion) AS Total
            FROM ALQUILER a
            JOIN DETALLEALQUILER d ON a.IDAlquiler = d.IDAlquiler
            JOIN RECURSOS r ON d.IDRecurso = r.IDRecurso
            GROUP BY a.FechaDeInicio
            ORDER BY a.FechaDeInicio
        """;

        PreparedStatement pst = conn.prepareStatement(sql);
        ResultSet rs = pst.executeQuery();

        while (rs.next()) {
            modelIngresosDia.addRow(new Object[]{
                rs.getDate("Fecha"),
                String.format("S/ %.2f", rs.getDouble("Total"))
            });
        }

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error en reporte de ingresos");
    }
    }
    private void loadControlAlquileres() {

    modelControlAlquileres.setRowCount(0);

    try (Connection conn = conexion.getConnection()) {

        String sql = """
            SELECT a.IDAlquiler,
                   a.FechaDeInicio,
                   t.Nombre AS Turista,
                   r.Recurso,
                   a.Duracion,
                   r.TarifaPorHora * a.Duracion AS Total
            FROM ALQUILER a
            JOIN DETALLEALQUILER d ON a.IDAlquiler = d.IDAlquiler
            JOIN TURISTAA t ON d.IDTurista = t.IDTurista
            JOIN RECURSOS r ON d.IDRecurso = r.IDRecurso
            ORDER BY a.FechaDeInicio DESC
        """;

        PreparedStatement pst = conn.prepareStatement(sql);
        ResultSet rs = pst.executeQuery();

        while (rs.next()) {
            modelControlAlquileres.addRow(new Object[]{
                rs.getString("IDAlquiler"),
                rs.getDate("FechaDeInicio"),
                rs.getString("Turista"),
                rs.getString("Recurso"),
                rs.getInt("Duracion"),
                String.format("S/ %.2f", rs.getDouble("Total"))
            });
        }

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error al cargar control de alquileres");
    }
    }
    private void editarAlquilerReporte() {

    int row = tablaControlAlquileres.getSelectedRow();

    if (row == -1) {
        JOptionPane.showMessageDialog(this, "Selecciona un alquiler");
        return;
    }

    String idAlquiler = modelControlAlquileres.getValueAt(row, 0).toString();
    Date fechaActual = (Date) modelControlAlquileres.getValueAt(row, 1);
    int duracionActual = Integer.parseInt(
        modelControlAlquileres.getValueAt(row, 4).toString()
    );

    // =============================
    // FORMULARIO
    // =============================
    JTextField txtFecha = new JTextField(fechaActual.toString());
    JTextField txtDuracion = new JTextField(String.valueOf(duracionActual));

    JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
    panel.add(new JLabel("Fecha (YYYY-MM-DD):"));
    panel.add(txtFecha);
    panel.add(new JLabel("Duración (horas):"));
    panel.add(txtDuracion);

    int result = JOptionPane.showConfirmDialog(
        this,
        panel,
        "Editar Alquiler " + idAlquiler,
        JOptionPane.OK_CANCEL_OPTION,
        JOptionPane.PLAIN_MESSAGE
    );

    if (result != JOptionPane.OK_OPTION) return;

    try {
        Date nuevaFecha = Date.valueOf(txtFecha.getText());
        int nuevaDuracion = Integer.parseInt(txtDuracion.getText());

        if (nuevaDuracion <= 0) {
            JOptionPane.showMessageDialog(this, "Duración inválida");
            return;
        }

        // =============================
        // UPDATE EN BD
        // =============================
        try (Connection conn = conexion.getConnection()) {

            String sql = """
                UPDATE ALQUILER
                SET FechaDeInicio = ?, Duracion = ?
                WHERE IDAlquiler = ?
            """;

            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setDate(1, nuevaFecha);
            pst.setInt(2, nuevaDuracion);
            pst.setString(3, idAlquiler);
            pst.executeUpdate();

            JOptionPane.showMessageDialog(this, "Alquiler actualizado");

            // Refrescar reportes
            loadControlAlquileres();
            loadIngresosPorDia();
        }

    } catch (IllegalArgumentException e) {
        JOptionPane.showMessageDialog(this, "Formato de fecha incorrecto");
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error al actualizar alquiler");
    }
    }
    private void eliminarAlquilerReporte() {

    int row = tablaControlAlquileres.getSelectedRow();
    if (row == -1) {
        JOptionPane.showMessageDialog(this, "Selecciona un alquiler");
        return;
    }

    String id = modelControlAlquileres.getValueAt(row, 0).toString();

    int confirm = JOptionPane.showConfirmDialog(
        this,
        "¿Eliminar el alquiler " + id + "?",
        "Confirmar",
        JOptionPane.YES_NO_OPTION
    );

    if (confirm == JOptionPane.YES_OPTION) {
        try (Connection conn = conexion.getConnection()) {
            PreparedStatement pst1 =
                conn.prepareStatement("DELETE FROM DETALLEALQUILER WHERE IDAlquiler=?");
            pst1.setString(1, id);
            pst1.executeUpdate();

            PreparedStatement pst2 =
                conn.prepareStatement("DELETE FROM ALQUILER WHERE IDAlquiler=?");
            pst2.setString(1, id);
            pst2.executeUpdate();

            loadControlAlquileres();
            loadIngresosPorDia();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al eliminar");
        }
    }
    }
    class GraficaIngresosPanel extends JPanel {

    public GraficaIngresosPanel() {
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createTitledBorder("📊 Ingresos por Día"));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        int width = getWidth();
        int height = getHeight();
        int padding = 40;

        int rowCount = modelIngresosDia.getRowCount();
        if (rowCount == 0) return;

        // Obtener el máximo
        double max = 0;
        for (int i = 0; i < rowCount; i++) {
            String val = modelIngresosDia.getValueAt(i, 1).toString()
                    .replace("S/", "").trim();
            double value = Double.parseDouble(val);
            max = Math.max(max, value);
        }

        int barWidth = (width - 2 * padding) / rowCount;

        for (int i = 0; i < rowCount; i++) {

            String fecha = modelIngresosDia.getValueAt(i, 0).toString();
            String val = modelIngresosDia.getValueAt(i, 1).toString()
                    .replace("S/", "").trim();

            double ingreso = Double.parseDouble(val);
            int barHeight = (int) ((ingreso / max) * (height - 2 * padding));

            int x = padding + i * barWidth;
            int y = height - padding - barHeight;

            // Barra
            g2.setColor(PRIMARY);
            g2.fillRect(x + 5, y, barWidth - 10, barHeight);

            // Valor
            g2.setColor(Color.BLACK);
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            g2.drawString("S/ " + String.format("%.0f", ingreso), x + 10, y - 5);

            // Fecha
            g2.drawString(fecha, x + 10, height - 15);
        }
    }
    }
    private void cargarTotalIngresos() {

    try (Connection conn = conexion.getConnection()) {

        String sql = """
            SELECT 
                SUM(
                    r.TarifaPorHora * a.Duracion *
                    (1 - ISNULL(p.PorcentajeDescuento, 0) / 100.0)
                ) AS IngresoReal
            FROM ALQUILER a
            JOIN DETALLEALQUILER da ON a.IDAlquiler = da.IDAlquiler
            JOIN RECURSOS r ON da.IDRecurso = r.IDRecurso
            LEFT JOIN PROMOCION p ON da.IDPromocion = p.IDPromocion
        """;

        PreparedStatement pst = conn.prepareStatement(sql);
        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            double total = rs.getDouble("IngresoReal");
            lblTotalIngresos.setText(String.format("S/ %.2f", total));
        }

    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this,
            "Error al calcular ingresos",
            "Error",
            JOptionPane.ERROR_MESSAGE);
    }
    } 



    private void loadData() {
        loadRecursos();
        loadUsuarios();
        loadTuristas();
        loadAlquileres();
        loadPromociones();
        cargarTotalIngresos();

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
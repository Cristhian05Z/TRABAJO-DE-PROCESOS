package vista;

import javax.swing.*;
import javax.swing.table.*;


import DAO.DetalleAlquilerDAO;
import database.conexion;
import modelo.Usuario;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class turistaframe extends JFrame {
    
    // Paleta de colores oscuros elegantes
    private static final Color PRIMARY = new Color(59, 130, 246); // Azul brillante
    private static final Color PRIMARY_DARK = new Color(37, 99, 235);
    private static final Color SECONDARY = new Color(16, 185, 129); // Verde esmeralda
    private static final Color SUCCESS = new Color(34, 197, 94);
    private static final Color DANGER = new Color(239, 68, 68);
    private static final Color WARNING = new Color(251, 146, 60);
    private static final Color INFO = new Color(14, 165, 233);
    
    // Fondos oscuros
    private static final Color BG_DARK = new Color(15, 23, 42); // Slate oscuro
    private static final Color BG_CARD = new Color(30, 41, 59); // Slate medio
    private static final Color BG_HOVER = new Color(51, 65, 85);
    
    // Textos
    private static final Color TEXT_PRIMARY = new Color(248, 250, 252);
    private static final Color TEXT_SECONDARY = new Color(148, 163, 184);
    private static final Color BORDER_COLOR = new Color(51, 65, 85);
    
    private Usuario currentUser;
    private JTable tablaRecursos;
    private DefaultTableModel modelRecursos;
    private JTable tableMyAlquileres;
    private DefaultTableModel modelMyAlquileres;
    private JTable tableCart;
    private DefaultTableModel modelCart;
    private JLabel lblTotal;
    private ArrayList<CartItem> cart = new ArrayList<>();
    private JTabbedPane tabbedPane;
    private String idTurista = "";
    
    class CartItem {
        String idRecurso;
        String nombre;
        double tarifaPorHora;
        int horas;
        
        CartItem(String id, String nombre, double tarifa, int horas) {
            this.idRecurso = id;
            this.nombre = nombre;
            this.tarifaPorHora = tarifa;
            this.horas = horas;
        }
    }
    
    public turistaframe(Usuario user) {
        this.currentUser = user;
        this.idTurista = obtenerIDTuristaPorNombre(user.getNombre());
        
        if (idTurista == null) {
            JOptionPane.showMessageDialog(this,
                "No se encontró el turista en la base de datos",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        setTitle("Portal Turista - Sistema de Alquiler");
        setSize(1200, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_DARK);
        
        setLayout(new BorderLayout(0, 0));
        
        // Header
        add(createModernHeader(), BorderLayout.NORTH);
        
        // Tabs
        add(createModernTabs(), BorderLayout.CENTER);
        
        loadRecursos();
        loadMyAlquileres();
        initTableClick();
    }
    
    // ============================================
    // HEADER MODERNO
    // ============================================
    private JPanel createModernHeader() {
        JPanel header = new JPanel(new BorderLayout(20, 0));
        header.setBackground(BG_CARD);
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, PRIMARY),
            BorderFactory.createEmptyBorder(20, 30, 20, 30)
        ));
        
        // Lado izquierdo
        JPanel leftPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        leftPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("🏖️ Portal de Turista");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_PRIMARY);
        
        JLabel welcomeLabel = new JLabel("Bienvenido, " + currentUser.getNombre());
        welcomeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        welcomeLabel.setForeground(TEXT_SECONDARY);
        
        leftPanel.add(titleLabel);
        leftPanel.add(welcomeLabel);
        
        // Botón cerrar sesión
        JButton btnLogout = createModernButton("Cerrar Sesión", DANGER, "⎋");
        btnLogout.addActionListener(e -> logout());
        
        header.add(leftPanel, BorderLayout.WEST);
        header.add(btnLogout, BorderLayout.EAST);
        
        return header;
    }
    
    // ============================================
    // TABS MODERNOS
    // ============================================
    private JTabbedPane createModernTabs() {
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabbedPane.setBackground(BG_DARK);
        tabbedPane.setForeground(TEXT_PRIMARY);
        tabbedPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Personalizar apariencia de tabs
        UIManager.put("TabbedPane.selected", PRIMARY);
        UIManager.put("TabbedPane.contentAreaColor", BG_DARK);
        
        tabbedPane.addTab("  🛒  Alquilar Recursos  ", createAlquilerPanel());
        tabbedPane.addTab("  📋  Mis Alquileres  ", createMyAlquileresPanel());
        
        return tabbedPane;
    }
    
    // ============================================
    // PANEL DE ALQUILER
    // ============================================
    private JPanel createAlquilerPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(0, 15));
        mainPanel.setBackground(BG_DARK);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Panel superior - Recursos disponibles
        JPanel recursosPanel = createRecursosDisponiblesPanel();
        
        // Panel inferior - Carrito
        JPanel carritoPanel = createCarritoPanel();
        
        // Dividir verticalmente
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, recursosPanel, carritoPanel);
        splitPane.setDividerLocation(320);
        splitPane.setDividerSize(8);
        splitPane.setBackground(BG_DARK);
        splitPane.setBorder(null);
        
        mainPanel.add(splitPane, BorderLayout.CENTER);
        
        return mainPanel;
    }
    
    // ============================================
    // PANEL DE RECURSOS DISPONIBLES
    // ============================================
    private JPanel createRecursosDisponiblesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BG_CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("🏖️ Recursos Disponibles");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(TEXT_PRIMARY);
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);
        
        JButton btnRefresh = createModernButton("Actualizar", INFO, "↻");
        btnRefresh.setPreferredSize(new Dimension(130, 36));
        btnRefresh.addActionListener(e -> loadRecursos());
        
        btnPanel.add(btnRefresh);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(btnPanel, BorderLayout.EAST);
        
        // Tabla
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
        
        // Botón agregar
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        JButton btnAddToCart = createModernButton("➕ Agregar al Carrito", PRIMARY, "");
        btnAddToCart.setPreferredSize(new Dimension(200, 42));
        btnAddToCart.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAddToCart.addActionListener(e -> addToCart());
        
        bottomPanel.add(btnAddToCart);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    // ============================================
    // PANEL DE CARRITO
    // ============================================
    private JPanel createCarritoPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BG_CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(SECONDARY, 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("🛒 Mi Carrito de Compras");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(TEXT_PRIMARY);
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);
        
        JButton btnRemove = createModernButton("Quitar", DANGER, "✕");
        JButton btnClear = createModernButton("Limpiar", WARNING, "🗑");
        
        btnRemove.setPreferredSize(new Dimension(110, 36));
        btnClear.setPreferredSize(new Dimension(110, 36));
        
        btnRemove.addActionListener(e -> removeFromCart());
        btnClear.addActionListener(e -> clearCart());
        
        btnPanel.add(btnRemove);
        btnPanel.add(btnClear);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(btnPanel, BorderLayout.EAST);
        
        // Tabla
        modelCart = new DefaultTableModel(
            new String[]{"Recurso", "Tarifa", "Horas", "Subtotal"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableCart = createModernTable(modelCart);
        JScrollPane scrollPane = createModernScrollPane(tableCart);
        
        // Panel de total y confirmar
        JPanel bottomPanel = new JPanel(new BorderLayout(0, 15));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        
        // Total
        JPanel totalPanel = new JPanel(new BorderLayout());
        totalPanel.setBackground(BG_HOVER);
        totalPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(SECONDARY, 2),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        JLabel lblTotalText = new JLabel("TOTAL A PAGAR:");
        lblTotalText.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTotalText.setForeground(TEXT_SECONDARY);
        
        lblTotal = new JLabel("S/ 0.00");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTotal.setForeground(SECONDARY);
        lblTotal.setHorizontalAlignment(SwingConstants.RIGHT);
        
        totalPanel.add(lblTotalText, BorderLayout.WEST);
        totalPanel.add(lblTotal, BorderLayout.EAST);
        
        // Botón confirmar
        JButton btnConfirm = createModernButton("✓ Confirmar Alquiler", SUCCESS, "");
        btnConfirm.setPreferredSize(new Dimension(250, 50));
        btnConfirm.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnConfirm.addActionListener(e -> processAlquiler());
        
        JPanel btnConfirmPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnConfirmPanel.setOpaque(false);
        btnConfirmPanel.add(btnConfirm);
        
        bottomPanel.add(totalPanel, BorderLayout.NORTH);
        bottomPanel.add(btnConfirmPanel, BorderLayout.SOUTH);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    // ============================================
    // PANEL DE MIS ALQUILERES
    // ============================================
    private JPanel createMyAlquileresPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(BG_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Card contenedor
        JPanel cardPanel = new JPanel(new BorderLayout(10, 10));
        cardPanel.setBackground(BG_CARD);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("📋 Historial de Alquileres");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(TEXT_PRIMARY);
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);
        
        JButton btnRefresh = createModernButton("Actualizar", INFO, "↻");
        JButton btnDevolver = createModernButton("Devolver Recurso", PRIMARY, "↩");
        
        btnRefresh.setPreferredSize(new Dimension(130, 36));
        btnDevolver.setPreferredSize(new Dimension(180, 36));
        
        btnRefresh.addActionListener(e -> loadMyAlquileres());
        btnDevolver.addActionListener(e -> returnProduct());
        
        btnPanel.add(btnRefresh);
        btnPanel.add(btnDevolver);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(btnPanel, BorderLayout.EAST);
        
        // Tabla
        modelMyAlquileres = new DefaultTableModel(
            new String[]{"ID Alquiler", "ID Recurso", "Fecha", "Hora", "Duración", "Recurso", "Tarifa"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableMyAlquileres = createModernTable(modelMyAlquileres);
        JScrollPane scrollPane = createModernScrollPane(tableMyAlquileres);
        
        cardPanel.add(headerPanel, BorderLayout.NORTH);
        cardPanel.add(scrollPane, BorderLayout.CENTER);
        
        panel.add(cardPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    // ============================================
    // COMPONENTES REUTILIZABLES
    // ============================================
    private JButton createModernButton(String text, Color bgColor, String icon) {
        JButton btn = new JButton(icon.isEmpty() ? text : icon + "  " + text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        
        // Efecto hover
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(bgColor.brighter());
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
        table.setRowHeight(40);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(PRIMARY_DARK);
        table.setSelectionForeground(Color.WHITE);
        table.setBackground(BG_CARD);
        table.setForeground(TEXT_PRIMARY);
        
        // Header
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(BG_DARK);
        header.setForeground(TEXT_SECONDARY);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, PRIMARY));
        header.setPreferredSize(new Dimension(header.getWidth(), 40));
        
        // Renderer personalizado
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    setBackground(row % 2 == 0 ? BG_CARD : BG_HOVER);
                    setForeground(TEXT_PRIMARY);
                }
                
                setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
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
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        scrollPane.setBackground(BG_CARD);
        scrollPane.getViewport().setBackground(BG_CARD);
        
        scrollPane.getVerticalScrollBar().setBackground(BG_CARD);
        scrollPane.getHorizontalScrollBar().setBackground(BG_CARD);
        
        return scrollPane;
    }
    
    // ============================================
    // LÓGICA DE NEGOCIO
    // ============================================
    private void loadRecursos() {
        modelRecursos.setRowCount(0);
        try (Connection conn = conexion.getConnection()) {
            String sql = "SELECT * FROM RECURSOS WHERE Estado = 'disponible'";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                modelRecursos.addRow(new Object[]{
                    rs.getString("IDRecurso"),
                    rs.getString("Recurso"),
                    rs.getString("Descripcion"),
                    String.format("S/ %.2f", rs.getDouble("TarifaPorHora")),
                    rs.getString("Estado")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error al cargar recursos: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadMyAlquileres() {
        modelMyAlquileres.setRowCount(0);
        if (idTurista == null || idTurista.isEmpty()) return;
        
        try (Connection conn = conexion.getConnection()) {
            String sql = "SELECT a.IDAlquiler, da.IDRecurso, a.FechaDeInicio, a.HoraDeInicio, a.Duracion, " +
                        "r.Recurso, r.TarifaPorHora, r.Estado " +
                        "FROM Alquiler a " +
                        "JOIN DETALLEALQUILER da ON a.IDAlquiler = da.IDAlquiler " +
                        "JOIN RECURSOS r ON da.IDRecurso = r.IDRecurso " +
                        "WHERE da.IDTurista = ? " +
                        "ORDER BY a.FechaDeInicio DESC";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, idTurista);
            ResultSet rs = pst.executeQuery();
            
            while (rs.next()) {
                modelMyAlquileres.addRow(new Object[]{
                    rs.getString("IDAlquiler"),
                    rs.getString("IDRecurso"),
                    rs.getDate("FechaDeInicio"),
                    rs.getTime("HoraDeInicio"),
                    rs.getString("Duracion") + " hrs",
                    rs.getString("Recurso"),
                    String.format("S/ %.2f", rs.getDouble("TarifaPorHora"))
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error al cargar alquileres: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    private void initTableClick() {

    tablaRecursos.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {

            if (e.getClickCount() == 2) {

                int row = tablaRecursos.getSelectedRow();

                String idRecurso = tablaRecursos.getValueAt(row, 0).toString();
                String nombre = tablaRecursos.getValueAt(row, 1).toString();
                double tarifa = Double.parseDouble(
                        tablaRecursos.getValueAt(row, 2).toString()
                );

                String rutaImagen = "/imagenes/" +
                        nombre.toLowerCase().replace(" ", "_") + ".jpg";

                mostrarImagenRecurso(nombre, tarifa, rutaImagen);
            }
        }
    });
}
private void mostrarImagenRecurso(String nombre, double tarifa, String rutaImagen) {

    ImageIcon icon = new ImageIcon(
            getClass().getResource(rutaImagen)
    );

    JLabel lblImagen = new JLabel(icon);
    lblImagen.setHorizontalAlignment(JLabel.CENTER);

    JOptionPane.showMessageDialog(
            this,
            lblImagen,
            nombre + " - S/ " + tarifa + " por hora",
            JOptionPane.PLAIN_MESSAGE
    );
}


    
    private void addToCart() {
        int selectedRow = tablaRecursos.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Por favor selecciona un recurso de la tabla",
                "Aviso",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String id = modelRecursos.getValueAt(selectedRow, 0).toString();
        String nombre = (String) modelRecursos.getValueAt(selectedRow, 1);
        String tarifaStr = (String) modelRecursos.getValueAt(selectedRow, 3);
        double tarifa = Double.parseDouble(tarifaStr.replace("S/ ", ""));
        
        String horasStr = JOptionPane.showInputDialog(this,
            "¿Cuántas horas desea alquilar?",
            "Horas de Alquiler",
            JOptionPane.QUESTION_MESSAGE);
        
        if (horasStr == null || horasStr.trim().isEmpty()) return;
        
        try {
            int horas = Integer.parseInt(horasStr);
            
            if (horas <= 0) {
                JOptionPane.showMessageDialog(this,
                    "Las horas deben ser mayor a 0",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            cart.add(new CartItem(id, nombre, tarifa, horas));
            updateCartTable();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "Por favor ingresa un número válido",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void removeFromCart() {
        int selectedRow = tableCart.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Selecciona un item del carrito",
                "Aviso",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        cart.remove(selectedRow);
        updateCartTable();
    }
    
    private void clearCart() {
        if (cart.isEmpty()) return;
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Estás seguro de limpiar el carrito?",
            "Confirmar",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            cart.clear();
            updateCartTable();
        }
    }
    
    private void updateCartTable() {
        modelCart.setRowCount(0);
        double total = 0;
        
        for (CartItem item : cart) {
            double subtotal = item.tarifaPorHora * item.horas;
            total += subtotal;
            modelCart.addRow(new Object[]{
                item.nombre,
                String.format("S/ %.2f", item.tarifaPorHora),
                item.horas + " hrs",
                String.format("S/ %.2f", subtotal)
            });
        }
        
        lblTotal.setText(String.format("S/ %.2f", total));
    }
    
    private String generarNuevoIDAlquiler(Connection conn) throws SQLException {
        String prefijo = "A";
        int siguienteNumero = 1;
        
        String sql = "SELECT TOP 1 IDAlquiler FROM ALQUILER WHERE IDAlquiler LIKE ? ORDER BY IDAlquiler DESC";
        PreparedStatement pst = conn.prepareStatement(sql);
        pst.setString(1, prefijo + "%");
        ResultSet rs = pst.executeQuery();
        
        if (rs.next()) {
            String ultimoID = rs.getString("IDAlquiler");
            String numeroStr = ultimoID.substring(prefijo.length());
            int numero = Integer.parseInt(numeroStr);
            siguienteNumero = numero + 1;
        }
        
        return String.format("%s%03d", prefijo, siguienteNumero);
    }
    
    private void processAlquiler() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "El carrito está vacío",
                "Aviso",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (idTurista == null || idTurista.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "No se pudo identificar al turista",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        double totalPagar = 0;
        for (CartItem item : cart)
            totalPagar += item.tarifaPorHora * item.horas;

        int confirm = JOptionPane.showConfirmDialog(this,
            String.format("¿Confirmar alquiler por un total de S/ %.2f?", totalPagar),
            "Confirmar Alquiler",
            JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        try (Connection conn = conexion.getConnection()) {
            conn.setAutoCommit(false);

            int duracionTotal = 0;
            for (CartItem item : cart) duracionTotal += item.horas;
            int duracionPromedio = duracionTotal / cart.size();

            String idAlquiler = generarNuevoIDAlquiler(conn);

            String sqlAlquiler = """
                INSERT INTO ALQUILER (IDAlquiler, FechaDeInicio, HoraDeInicio, Duracion)
                VALUES (?, ?, ?, ?)
            """;

            PreparedStatement pstAlquiler = conn.prepareStatement(sqlAlquiler);
            pstAlquiler.setString(1, idAlquiler);
            pstAlquiler.setDate(2, Date.valueOf(LocalDate.now()));
            pstAlquiler.setTime(3, Time.valueOf(LocalTime.now()));
            pstAlquiler.setInt(4, duracionPromedio);
            pstAlquiler.executeUpdate();

            for (CartItem item : cart) {
                String idDetalle = generarNuevoIDDetalleAlquiler(conn);

                String sqlDetalle = """
                    INSERT INTO DETALLEALQUILER
                    (IDDetalleAlquiler, IDRecurso, IDTurista, IDAlquiler, IDPromocion)
                    VALUES (?, ?, ?, ?, NULL)
                """;

                PreparedStatement pstDetalle = conn.prepareStatement(sqlDetalle);
                pstDetalle.setString(1, idDetalle);
                pstDetalle.setString(2, item.idRecurso);
                pstDetalle.setString(3, idTurista);
                pstDetalle.setString(4, idAlquiler);
                pstDetalle.executeUpdate();
            }

            conn.commit();

            JOptionPane.showMessageDialog(this,
                String.format(
                    "✅ ¡Alquiler confirmado exitosamente!\n\n" +
                    "ID Alquiler: %s\n" +
                    "Total: S/ %.2f\n" +
                    "Items: %d recurso(s)\n\n" +
                    "¡Disfruta tu experiencia!",
                    idAlquiler, totalPagar, cart.size()
                ),
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE);

            clearCart();
            loadRecursos();
            loadMyAlquileres();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error al procesar el alquiler:\n" + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private String obtenerIDTuristaPorNombre(String nombre) {
        try (Connection conn = conexion.getConnection()) {
            String sql = "SELECT IDTurista FROM TURISTAA WHERE UPPER(Nombre) = UPPER(?)";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, nombre);

            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getString("IDTurista");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private void returnProduct() {
        int fila = tableMyAlquileres.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this,
                "Por favor selecciona un alquiler de la tabla",
                "Aviso",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        String idAlquiler = tableMyAlquileres.getValueAt(fila, 0).toString();
        String idRecurso = tableMyAlquileres.getValueAt(fila, 1).toString();
        String recursoNombre = tableMyAlquileres.getValueAt(fila, 5).toString();

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "¿Deseas devolver el recurso: " + recursoNombre + "?",
            "Confirmar Devolución",
            JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        DetalleAlquilerDAO dao = new DetalleAlquilerDAO();

        if (dao.devolverRecurso(idAlquiler, idRecurso)) {
            JOptionPane.showMessageDialog(this,
                "✅ Recurso devuelto correctamente",
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE);
            loadMyAlquileres();
            loadRecursos();
        } else {
            JOptionPane.showMessageDialog(this,
                "Error al devolver el recurso",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private String generarNuevoIDDetalleAlquiler(Connection conn) throws SQLException {
        String prefijo = "D";
        int siguienteNumero = 1;

        String sql = """
            SELECT TOP 1 IDDetalleAlquiler
            FROM DETALLEALQUILER
            WHERE IDDetalleAlquiler LIKE ?
            ORDER BY IDDetalleAlquiler DESC
        """;

        PreparedStatement pst = conn.prepareStatement(sql);
        pst.setString(1, prefijo + "%");
        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            String ultimoID = rs.getString("IDDetalleAlquiler");
            String numeroStr = ultimoID.substring(prefijo.length());
            siguienteNumero = Integer.parseInt(numeroStr) + 1;
        }

        return String.format("%s%03d", prefijo, siguienteNumero);
    }
    
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Deseas cerrar sesión?",
            "Confirmar",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new login().setVisible(true);
        }
    }
}
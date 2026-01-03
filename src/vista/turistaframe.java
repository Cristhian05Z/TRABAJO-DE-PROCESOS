package vista;

import javax.swing.*;
import javax.swing.table.*;
import DAO.DetalleAlquilerDAO;
import DAO.PromocionDAO;
import database.conexion;
import modelo.Promocion;
import modelo.Usuario;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class turistaframe extends JFrame {
    
    // Paleta de colores claros y modernos
    private static final Color PRIMARY = new Color(59, 130, 246); // Azul principal
    private static final Color PRIMARY_DARK = new Color(37, 99, 235);
    private static final Color SECONDARY = new Color(16, 185, 129); // Verde
    private static final Color SUCCESS = new Color(34, 197, 94);
    private static final Color DANGER = new Color(239, 68, 68);
    private static final Color WARNING = new Color(251, 146, 60);
    private static final Color INFO = new Color(14, 165, 233);
    
    // Fondos CLAROS
    private static final Color BG_LIGHT = new Color(248, 250, 252); // Fondo claro
    private static final Color BG_CARD = new Color(255, 255, 255); // Cards blancas
    private static final Color BG_HOVER = new Color(243, 244, 246); // Hover claro
    
    // Textos uniformes
    private static final Color TEXT_PRIMARY = new Color(17, 24, 39); // Texto oscuro
    private static final Color TEXT_SECONDARY = new Color(107, 114, 128); // Texto secundario
    private static final Color BORDER_COLOR = new Color(229, 231, 235); // Bordes claros
    
    // FUENTE UNIFORME PARA TODO
    private static final Font FONT_MAIN = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 12);
    
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
    
    // Fuente consistente para toda la aplicación
    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 12);
    private static final Font FONT_TABLE = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 12);
    
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
        setSize(1400, 800); // ✅ AUMENTADO para mejor espacio
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_LIGHT);
        
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
    // HEADER MODERNO (MODO CLARO)
    // ============================================
    private JPanel createModernHeader() {
        JPanel header = new JPanel(new BorderLayout(20, 0));
        header.setBackground(BG_CARD);
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, PRIMARY),
            BorderFactory.createEmptyBorder(15, 25, 15, 25) // ✅ Reducido padding
        ));

        // LADO IZQUIERDO (TÍTULO)
        JPanel leftPanel = new JPanel(new GridLayout(2, 1, 0, 3)); // ✅ Menos espacio
        leftPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("🏖️ Portal de Turista");
        titleLabel.setFont(FONT_MAIN);
        titleLabel.setForeground(TEXT_PRIMARY);

        JLabel subtitle = new JLabel("Sistema de Alquiler Turístico");
        subtitle.setFont(FONT_SMALL);
        subtitle.setForeground(TEXT_SECONDARY);

        leftPanel.add(titleLabel);
        leftPanel.add(subtitle);

        // LADO DERECHO (USUARIO + FOTO)
        JPanel rightPanel = new JPanel(new BorderLayout(10, 6)); // ✅ Menos espacio
        rightPanel.setOpaque(false);

        JLabel lblBienvenido = new JLabel(
                "BIENVENIDO " + currentUser.getNombre().toUpperCase()
        );
        lblBienvenido.setFont(FONT_REGULAR);
        lblBienvenido.setForeground(TEXT_PRIMARY);
        lblBienvenido.setHorizontalAlignment(SwingConstants.RIGHT);

        // Imagen del usuario
        ImageIcon icon = new ImageIcon(
                getClass().getResource("/Imagen/foto.jpg")
        );
        Image img = icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH); // ✅ Más pequeña
        JLabel lblFoto = new JLabel(new ImageIcon(img));

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        userPanel.setOpaque(false);
        userPanel.add(lblBienvenido);
        userPanel.add(lblFoto);

        // BOTONES (ABAJO)
        JButton btnCatalogo = createModernButton("Catálogo", INFO, "🖼");
        btnCatalogo.addActionListener(e -> new CatalogoFrame());

        JButton btnLogout = createModernButton("Cerrar Sesión", DANGER, "⎋");
        btnLogout.addActionListener(e -> logout());

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttonsPanel.setOpaque(false);
        buttonsPanel.add(btnCatalogo);
        buttonsPanel.add(btnLogout);

        rightPanel.add(userPanel, BorderLayout.NORTH);
        rightPanel.add(buttonsPanel, BorderLayout.SOUTH);

        header.add(leftPanel, BorderLayout.WEST);
        header.add(rightPanel, BorderLayout.EAST);

        return header;
    }
    
    // ============================================
    // TABS MODERNOS (MODO CLARO)
    // ============================================
    private JTabbedPane createModernTabs() {
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setFont(FONT_REGULAR);
        tabbedPane.setBackground(BG_LIGHT);
        tabbedPane.setForeground(TEXT_PRIMARY);
        tabbedPane.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8)); // ✅ Menos margen
        
        // Personalizar tabs claros
        UIManager.put("TabbedPane.selected", PRIMARY);
        UIManager.put("TabbedPane.contentAreaColor", BG_LIGHT);
        
        tabbedPane.addTab("  🛒  Alquilar Recursos  ", createAlquilerPanel());
        tabbedPane.addTab("  📋  Mis Alquileres  ", createMyAlquileresPanel());
        
        return tabbedPane;
    }
    
    // ============================================
    // PANEL DE ALQUILER ✅ LAYOUT OPTIMIZADO
    // ============================================
    private JPanel createAlquilerPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(12, 8)); // ✅ Espaciado uniforme
        mainPanel.setBackground(BG_LIGHT);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        
        // ✅ SplitPane HORIZONTAL para mejor distribución
        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplit.setDividerLocation(950); // ✅ Recursos 70%, Carrito+Promo 30%
        mainSplit.setDividerSize(6);
        mainSplit.setBackground(BG_LIGHT);
        mainSplit.setBorder(null);
        
        // Panel IZQUIERDO: Recursos (70%)
        JPanel recursosPanel = createRecursosDisponiblesPanel();
        
        // Panel DERECHO: Carrito + Promociones (30%)
        JPanel rightPanel = new JPanel(new BorderLayout(8, 8));
        rightPanel.setOpaque(false);
        rightPanel.add(createCarritoPanel(), BorderLayout.CENTER);
        rightPanel.add(createPromocionesPanel(), BorderLayout.SOUTH);
        
        mainSplit.setLeftComponent(recursosPanel);
        mainSplit.setRightComponent(rightPanel);
        
        mainPanel.add(mainSplit, BorderLayout.CENTER);
        return mainPanel;
    }
    
    // ============================================
    // RECURSOS DISPONIBLES (TAMAÑO UNIFORME)
    // ============================================
    private JPanel createRecursosDisponiblesPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(BG_CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("🏖️ Recursos Disponibles");
        titleLabel.setFont(FONT_MAIN);
        titleLabel.setForeground(TEXT_PRIMARY);
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnPanel.setOpaque(false);
        
        JButton btnRefresh = createModernButton("Actualizar", INFO, "↻");
        btnRefresh.setPreferredSize(new Dimension(120, 38)); // ✅ Tamaño uniforme
        btnRefresh.addActionListener(e -> loadRecursos());
        
        btnPanel.add(btnRefresh);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(btnPanel, BorderLayout.EAST);
        
        // Tabla ✅ Tamaño uniforme
        modelRecursos = new DefaultTableModel(
            new String[]{"ID", "Recurso", "Descripción", "Tarifa/H", "Estado"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        tablaRecursos = createModernTable(modelRecursos);
        JScrollPane scrollPane = createModernScrollPane(tablaRecursos);
        
        // Botón agregar
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setOpaque(false);
        
        JButton btnAddToCart = createModernButton("➕ Agregar al Carrito", PRIMARY, "");
        btnAddToCart.setPreferredSize(new Dimension(180, 42)); // ✅ Tamaño uniforme
        btnAddToCart.addActionListener(e -> addToCart());
        
        bottomPanel.add(btnAddToCart);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    // ============================================
    // CARRITO ✅ OPTIMIZADO Y SIEMPRE VISIBLE
    // ============================================
    private JPanel createCarritoPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(BG_CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(SECONDARY, 2),
            BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("🛒 Mi Carrito");
        titleLabel.setFont(FONT_MAIN);
        titleLabel.setForeground(TEXT_PRIMARY);
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        btnPanel.setOpaque(false);
        
        JButton btnRemove = createModernButton("Quitar", DANGER, "✕");
        JButton btnClear = createModernButton("Limpiar", WARNING, "🗑");
        
        btnRemove.setPreferredSize(new Dimension(90, 36)); // ✅ Tamaños uniformes
        btnClear.setPreferredSize(new Dimension(90, 36));
        
        btnRemove.addActionListener(e -> removeFromCart());
        btnClear.addActionListener(e -> clearCart());
        
        btnPanel.add(btnRemove);
        btnPanel.add(btnClear);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(btnPanel, BorderLayout.EAST);
        
        // Tabla carrito ✅ Columnas optimizadas
        modelCart = new DefaultTableModel(
            new String[]{"Recurso", "Tarifa", "Horas", "Subtotal"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        tableCart = createModernTable(modelCart);
        JScrollPane scrollPane = createModernScrollPane(tableCart);
        
        // Total y confirmar ✅ Compacto
        JPanel bottomPanel = new JPanel(new BorderLayout(0, 10));
        bottomPanel.setOpaque(false);
        
        // Total
        JPanel totalPanel = new JPanel(new BorderLayout());
        totalPanel.setBackground(BG_HOVER);
        totalPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(SECONDARY, 2),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        JLabel lblTotalText = new JLabel("TOTAL A PAGAR:");
        lblTotalText.setFont(FONT_REGULAR);
        lblTotalText.setForeground(TEXT_SECONDARY);
        
        lblTotal = new JLabel("S/ 0.00");
        lblTotal.setFont(FONT_MAIN);
        lblTotal.setForeground(SECONDARY);
        lblTotal.setHorizontalAlignment(SwingConstants.RIGHT);
        
        totalPanel.add(lblTotalText, BorderLayout.WEST);
        totalPanel.add(lblTotal, BorderLayout.EAST);
        
        // Botón confirmar
        JButton btnConfirm = createModernButton("✓ Confirmar", SUCCESS, "");
        btnConfirm.setPreferredSize(new Dimension(140, 44)); // ✅ Tamaño uniforme
        btnConfirm.addActionListener(e -> processAlquiler());
        
        bottomPanel.add(totalPanel, BorderLayout.NORTH);
        bottomPanel.add(btnConfirm, BorderLayout.CENTER);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
   // ============================================
    // MIS ALQUILERES (TAMAÑO UNIFORME)
    // ============================================
    private JPanel createMyAlquileresPanel() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBackground(BG_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        
        // Card contenedor
        JPanel cardPanel = new JPanel(new BorderLayout(8, 8));
        cardPanel.setBackground(BG_CARD);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("📋 Historial de Alquileres");
        titleLabel.setFont(FONT_MAIN);
        titleLabel.setForeground(TEXT_PRIMARY);
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnPanel.setOpaque(false);
        
        JButton btnRefresh = createModernButton("Actualizar", INFO, "↻");
        JButton btnDevolver = createModernButton("Devolver", PRIMARY, "↩");
        
        btnRefresh.setPreferredSize(new Dimension(110, 38)); // ✅ Uniforme
        btnDevolver.setPreferredSize(new Dimension(110, 38));
        
        btnRefresh.addActionListener(e -> loadMyAlquileres());
        btnDevolver.addActionListener(e -> returnProduct());
        
        btnPanel.add(btnRefresh);
        btnPanel.add(btnDevolver);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(btnPanel, BorderLayout.EAST);
        
        // Tabla
        modelMyAlquileres = new DefaultTableModel(
            new String[]{"ID", "Rec.", "Fecha", "Hora", "Hrs", "Recurso", "Tarifa"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        tableMyAlquileres = createModernTable(modelMyAlquileres);
        JScrollPane scrollPane = createModernScrollPane(tableMyAlquileres);
        
        cardPanel.add(headerPanel, BorderLayout.NORTH);
        cardPanel.add(scrollPane, BorderLayout.CENTER);
        
        panel.add(cardPanel, BorderLayout.CENTER);
        return panel;
    }
    
    // ============================================
    // PROMOCIONES COMPACTAS
    // ============================================
    private JPanel createPromocionesPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BG_CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(INFO, 2),
            BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
        panel.setPreferredSize(new Dimension(280, 200)); // ✅ Tamaño fijo compacto

        JLabel title = new JLabel("🎁 Promociones");
        title.setFont(FONT_MAIN);
        title.setForeground(TEXT_PRIMARY);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(title);
        panel.add(Box.createVerticalStrut(10));

        panel.add(crearPromoLabel("⏱️ Por horas"));
        panel.add(crearPromoLabel("• 3+ hrs → 10% OFF"));
        panel.add(crearPromoLabel("• 5+ hrs → 15% OFF"));
        panel.add(crearPromoLabel("• 8+ hrs → 20% OFF"));

        panel.add(Box.createVerticalStrut(10));
        panel.add(crearPromoLabel("🚜 Por cantidad"));
        panel.add(crearPromoLabel("• 4+ rec. → 15% OFF"));
        panel.add(crearPromoLabel("• 6+ rec. → 25% OFF"));

        return panel;
    }

    private JLabel crearPromoLabel(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(FONT_SMALL);
        lbl.setForeground(TEXT_SECONDARY);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }
    
    // ============================================
    // BOTONES UNIFORMES
    // ============================================
    private JButton createModernButton(String text, Color bgColor, String icon) {
        JButton btn = new JButton(icon.isEmpty() ? text : icon + " " + text);
        btn.setFont(FONT_REGULAR);
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14)); // ✅ Padding uniforme
        
        // Efecto hover claro
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
    
    // ============================================
    // TABLAS UNIFORMES (RowHeight 38px)
    // ============================================
    private JTable createModernTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFont(FONT_REGULAR);
        table.setRowHeight(38); // ✅ Altura uniforme
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(PRIMARY_DARK);
        table.setSelectionForeground(Color.WHITE);
        table.setBackground(BG_CARD);
        table.setForeground(TEXT_PRIMARY);
        
        // Header uniforme
        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_MAIN);
        header.setBackground(BG_LIGHT);
        header.setForeground(TEXT_PRIMARY);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, PRIMARY));
        header.setPreferredSize(new Dimension(header.getWidth(), 42));
        
        // Renderer uniforme
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
                    BorderFactory.createEmptyBorder(6, 10, 6, 10) // ✅ Padding uniforme
                ));
                
                return c;
            }
        };
        
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
            table.getColumnModel().getColumn(i).setPreferredWidth(120); // ✅ Anchos uniformes
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
    
    private void loadRecursos() {
        modelRecursos.setRowCount(0);
        try (Connection conn = conexion.getConnection()) {
            String sql = "SELECT * FROM RECURSOS WHERE Estado = 'disponible'";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                String estadoBD = rs.getString("Estado");
                String estadoMostrar = estadoBD.equalsIgnoreCase("disponible") ? "Disponible" : "No Disponible";

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
                    String tarifaStr = tablaRecursos.getValueAt(row, 3).toString();
                    double tarifa = Double.parseDouble(tarifaStr.replace("S/ ", ""));

                    String rutaImagen = "/imagenes/" +
                            nombre.toLowerCase().replace(" ", "_") + ".jpg";

                    mostrarImagenRecurso(nombre, tarifa, rutaImagen);
                }
            }
        });
    }

    private void mostrarImagenRecurso(String nombre, double tarifa, String rutaImagen) {
        ImageIcon icon = new ImageIcon(getClass().getResource(rutaImagen));
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
        double tarifa = Double.parseDouble(tablaRecursos.getValueAt(selectedRow, 3).toString().replace("S/ ", ""));
        
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

    private Promocion determinarPromocion(int horasTotales, int cantidadRecursos) {
    try {
        PromocionDAO dao = new PromocionDAO();

        if (cantidadRecursos >= 6)
            return dao.obtenerPorId("P005");
        if (cantidadRecursos >= 4)
            return dao.obtenerPorId("P004");
        if (horasTotales >= 8)
            return dao.obtenerPorId("P003");
        if (horasTotales >= 5)
            return dao.obtenerPorId("P002");
        if (horasTotales >= 3)
            return dao.obtenerPorId("P001");

    } catch (SQLException e) {
        e.printStackTrace();
    }
    return null;
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

        int horasTotales = 0;
        double totalBruto = 0;

        for (CartItem item : cart) {
                horasTotales += item.horas;
                totalBruto += item.tarifaPorHora * item.horas;
            }

        int cantidadRecursos = cart.size();

// 👉 AQUÍ SÍ SE USA TU MÉTODO
        Promocion promocionAplicada =
        determinarPromocion(horasTotales, cantidadRecursos);

            double descuento = 0;
               if (promocionAplicada != null) {
                descuento = totalBruto * (promocionAplicada.getPorcentajeDescuento() / 100.0);
            }

        double totalPagar = totalBruto - descuento;

            String mensajeConfirmacion =
        "Total bruto: S/ " + String.format("%.2f", totalBruto);

if (promocionAplicada != null) {
    mensajeConfirmacion +=
            "\nPromoción aplicada: " + promocionAplicada.getCondiciones() +
            "\nDescuento: " + promocionAplicada.getPorcentajeDescuento() + "%";
}

mensajeConfirmacion +=
        "\n\nTotal a pagar: S/ " + String.format("%.2f", totalPagar) +
        "\n\n¿Confirmar alquiler?";

int confirm = JOptionPane.showConfirmDialog(
        this,
        mensajeConfirmacion,
        "Confirmar Alquiler",
        JOptionPane.YES_NO_OPTION
);

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
                    VALUES (?, ?, ?, ?, ?)
                """;

                PreparedStatement pstDetalle = conn.prepareStatement(sqlDetalle);
                pstDetalle.setString(1, idDetalle);
                pstDetalle.setString(2, item.idRecurso);
                pstDetalle.setString(3, idTurista);
                pstDetalle.setString(4, idAlquiler);
                pstDetalle.setString(5,promocionAplicada != null? promocionAplicada.getIDPromocion(): null
);
                pstDetalle.executeUpdate();
          // Actualizar estado del recurso a "alquilado"
String sqlUpdateEstado = "UPDATE RECURSOS SET Estado = 'alquilado' WHERE IDRecurso = ?";
PreparedStatement pstUpdateEstado = conn.prepareStatement(sqlUpdateEstado);
pstUpdateEstado.setString(1, item.idRecurso);
pstUpdateEstado.executeUpdate();

            }

            conn.commit();

            String mensajeFinal =
        "✅ ¡Alquiler confirmado exitosamente!\n\n" +
        "ID Alquiler: " + idAlquiler +
        "\nItems: " + cart.size() +
        "\nTotal bruto: S/ " + String.format("%.2f", totalBruto);

if (promocionAplicada != null) {
    mensajeFinal +=
            "\nPromoción aplicada: " + promocionAplicada.getCondiciones() +
            "\nDescuento: " + promocionAplicada.getPorcentajeDescuento() + "%";
}

mensajeFinal +=
        "\n\nTotal pagado: S/ " + String.format("%.2f", totalPagar) +
        "\n\n¡Disfruta tu experiencia!";

JOptionPane.showMessageDialog(
        this,
        mensajeFinal,
        "Éxito",
        JOptionPane.INFORMATION_MESSAGE
);


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
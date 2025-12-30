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
    
    // ============================================
    // PALETA DE COLORES CLARA Y MODERNA
    // ============================================
    private static final Color PRIMARY = new Color(59, 130, 246);      // Azul vibrante
    private static final Color PRIMARY_LIGHT = new Color(147, 197, 253);
    private static final Color SECONDARY = new Color(16, 185, 129);    // Verde esmeralda
    private static final Color SUCCESS = new Color(34, 197, 94);
    private static final Color DANGER = new Color(239, 68, 68);
    private static final Color WARNING = new Color(251, 146, 60);
    private static final Color INFO = new Color(14, 165, 233);
    
    // Fondos claros
    private static final Color BG_PRIMARY = new Color(249, 250, 251);  // Gris muy claro
    private static final Color BG_SECONDARY = Color.WHITE;
    private static final Color BG_HOVER = new Color(243, 244, 246);
    
    // Textos
    private static final Color TEXT_PRIMARY = new Color(17, 24, 39);
    private static final Color TEXT_SECONDARY = new Color(107, 114, 128);
    private static final Color BORDER_COLOR = new Color(229, 231, 235);
    
    // Fuente estándar
    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font FONT_NORMAL = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 12);
    
    // ============================================
    // VARIABLES DE INSTANCIA
    // ============================================
    private Usuario currentUser;
    private String idTurista;
    private ArrayList<CartItem> cart = new ArrayList<>();
    
    // Componentes UI
    private JTable tablaRecursos;
    private DefaultTableModel modelRecursos;
    private JTable tableMyAlquileres;
    private DefaultTableModel modelMyAlquileres;
    private JTable tableCart;
    private DefaultTableModel modelCart;
    private JLabel lblTotal;
    private JTabbedPane tabbedPane;
    
    // ============================================
    // CLASE INTERNA: CART ITEM
    // ============================================
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
    
    // ============================================
    // CONSTRUCTOR
    // ============================================
    public turistaframe(Usuario user) {
        this.currentUser = user;
        this.idTurista = obtenerIDTuristaPorNombre(user.getNombre());
        
        if (idTurista == null) {
            mostrarError("No se encontró el turista en la base de datos");
            return;
        }
        
        configurarVentana();
        construirInterfaz();
        cargarDatosIniciales();
    }
    
    // ============================================
    // CONFIGURACIÓN DE VENTANA
    // ============================================
    private void configurarVentana() {
        setTitle("Portal Turista - Sistema de Alquiler");
        setSize(1400, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_PRIMARY);
        setLayout(new BorderLayout(0, 0));
    }
    
    private void construirInterfaz() {
        add(crearHeader(), BorderLayout.NORTH);
        add(crearTabs(), BorderLayout.CENTER);
    }
    
    private void cargarDatosIniciales() {
        loadRecursos();
        loadMyAlquileres();
        initTableClick();
    }
    
    // ============================================
    // HEADER
    // ============================================
<<<<<<< HEAD
    private JPanel crearHeader() {
        JPanel header = new JPanel(new BorderLayout(20, 0));
        header.setBackground(BG_SECONDARY);
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 3, 0, PRIMARY),
            BorderFactory.createEmptyBorder(25, 35, 25, 35)
        ));
        
        // Panel izquierdo - Título y bienvenida
        JPanel leftPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        leftPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("🏖️ Portal de Turista");
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setForeground(TEXT_PRIMARY);
        
        JLabel welcomeLabel = new JLabel("Bienvenido, " + currentUser.getNombre());
        welcomeLabel.setFont(FONT_NORMAL);
        welcomeLabel.setForeground(TEXT_SECONDARY);
        
        leftPanel.add(titleLabel);
        leftPanel.add(welcomeLabel);
        
        // Panel derecho - Botones
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setOpaque(false);
        
        JButton btnCatalogo = crearBoton("📋 Catálogo", INFO);
        btnCatalogo.addActionListener(e -> new CatalogoFrame());
        
        JButton btnLogout = crearBoton("⎋ Cerrar Sesión", DANGER);
        btnLogout.addActionListener(e -> logout());
        
        rightPanel.add(btnCatalogo);
        rightPanel.add(btnLogout);
        
        header.add(leftPanel, BorderLayout.WEST);
        header.add(rightPanel, BorderLayout.EAST);
        
        return header;
    }
=======
    private JPanel createModernHeader() {

    JPanel header = new JPanel(new BorderLayout(20, 0));
    header.setBackground(BG_CARD);
    header.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createMatteBorder(0, 0, 2, 0, PRIMARY),
        BorderFactory.createEmptyBorder(20, 30, 20, 30)
    ));

    // =========================
    // LADO IZQUIERDO (TÍTULO)
    // =========================
    JPanel leftPanel = new JPanel(new GridLayout(2, 1, 0, 5));
    leftPanel.setOpaque(false);

    JLabel titleLabel = new JLabel("🏖️ Portal de Turista");
    titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
    titleLabel.setForeground(TEXT_PRIMARY);

    JLabel subtitle = new JLabel("Sistema de Alquiler Turístico");
    subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    subtitle.setForeground(TEXT_SECONDARY);

    leftPanel.add(titleLabel);
    leftPanel.add(subtitle);

    // =========================
    // LADO DERECHO (USUARIO + FOTO)
    // =========================
    JPanel rightPanel = new JPanel(new BorderLayout(10, 8));
    rightPanel.setOpaque(false);

    JLabel lblBienvenido = new JLabel(
            "BIENVENIDO " + currentUser.getNombre().toUpperCase()
    );
    lblBienvenido.setFont(new Font("Segoe UI", Font.BOLD, 14));
    lblBienvenido.setForeground(TEXT_PRIMARY);
    lblBienvenido.setHorizontalAlignment(SwingConstants.RIGHT);

    // Imagen del usuario
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
    // BOTONES (ABAJO)
    // =========================
    JButton btnCatalogo = createModernButton("Catálogo", INFO, "🖼");
    btnCatalogo.addActionListener(e -> new CatalogoFrame());

    JButton btnLogout = createModernButton("Cerrar Sesión", DANGER, "⎋");
    btnLogout.addActionListener(e -> logout());

    JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
    buttonsPanel.setOpaque(false);
    buttonsPanel.add(btnCatalogo);
    buttonsPanel.add(btnLogout);

    rightPanel.add(userPanel, BorderLayout.NORTH);
    rightPanel.add(buttonsPanel, BorderLayout.SOUTH);

    header.add(leftPanel, BorderLayout.WEST);
    header.add(rightPanel, BorderLayout.EAST);

    return header;
}
>>>>>>> 38b3c0186d81551f9d2476cb895bdee46e2def6b
    
    // ============================================
    // TABS
    // ============================================
    private JTabbedPane crearTabs() {
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setFont(FONT_BUTTON);
        tabbedPane.setBackground(BG_PRIMARY);
        tabbedPane.setForeground(TEXT_PRIMARY);
        tabbedPane.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        tabbedPane.addTab("  🛒  Alquilar Recursos  ", crearPanelAlquiler());
        tabbedPane.addTab("  📋  Mis Alquileres  ", crearPanelMisAlquileres());
        
        return tabbedPane;
    }
    
    // ============================================
    // PANEL DE ALQUILER
    // ============================================
    private JPanel crearPanelAlquiler() {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(BG_PRIMARY);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        
        // Panel de recursos y carrito (izquierda)
        JPanel recursosYCarrito = new JPanel(new BorderLayout(0, 15));
        recursosYCarrito.setOpaque(false);
        
        JPanel recursosPanel = crearPanelRecursosDisponibles();
        JPanel carritoPanel = crearPanelCarrito();
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, recursosPanel, carritoPanel);
        splitPane.setDividerLocation(450);
        splitPane.setDividerSize(6);
        splitPane.setBackground(BG_PRIMARY);
        splitPane.setBorder(null);
        
        recursosYCarrito.add(splitPane, BorderLayout.CENTER);
        
        // Panel de promociones (derecha)
        JPanel promoPanel = crearPanelPromociones();
        promoPanel.setPreferredSize(new Dimension(320, 0));
        
        mainPanel.add(recursosYCarrito, BorderLayout.CENTER);
        mainPanel.add(promoPanel, BorderLayout.EAST);
        
        return mainPanel;
    }
    
    // ============================================
    // PANEL RECURSOS DISPONIBLES
    // ============================================
    private JPanel crearPanelRecursosDisponibles() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BG_SECONDARY);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("🏖️ Recursos Disponibles");
        titleLabel.setFont(FONT_SUBTITLE);
        titleLabel.setForeground(TEXT_PRIMARY);
        
        JButton btnRefresh = crearBoton("↻ Actualizar", INFO);
        btnRefresh.addActionListener(e -> loadRecursos());
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(btnRefresh, BorderLayout.EAST);
        
        // Tabla
        modelRecursos = new DefaultTableModel(
            new String[]{"ID", "Recurso", "Descripción", "Tarifa/Hora", "Estado"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaRecursos = crearTabla(modelRecursos);
        JScrollPane scrollPane = crearScrollPane(tablaRecursos);
        
        // Botón agregar
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        bottomPanel.setOpaque(false);
        
        JButton btnAddToCart = crearBotonGrande("➕ Agregar al Carrito", PRIMARY);
        btnAddToCart.addActionListener(e -> addToCart());
        
        bottomPanel.add(btnAddToCart);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    // ============================================
    // PANEL CARRITO
    // ============================================
    private JPanel crearPanelCarrito() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BG_SECONDARY);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(SECONDARY, 1),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("🛒 Mi Carrito");
        titleLabel.setFont(FONT_SUBTITLE);
        titleLabel.setForeground(TEXT_PRIMARY);
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);
        
        JButton btnRemove = crearBoton("✕ Quitar", DANGER);
        JButton btnClear = crearBoton("🗑 Limpiar", WARNING);
        
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
        
        tableCart = crearTabla(modelCart);
        JScrollPane scrollPane = crearScrollPane(tableCart);
        
        // Panel de total y confirmar
        JPanel bottomPanel = new JPanel(new BorderLayout(0, 10));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        // Total
        JPanel totalPanel = new JPanel(new BorderLayout(15, 0));
        totalPanel.setBackground(new Color(240, 253, 244));
        totalPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(SECONDARY, 1),
            BorderFactory.createEmptyBorder(12, 20, 12, 20)
        ));
        
        JLabel lblTotalText = new JLabel("TOTAL:");
        lblTotalText.setFont(FONT_BUTTON);
        lblTotalText.setForeground(TEXT_SECONDARY);
        
        lblTotal = new JLabel("S/ 0.00");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTotal.setForeground(SECONDARY);
        lblTotal.setHorizontalAlignment(SwingConstants.RIGHT);
        
        totalPanel.add(lblTotalText, BorderLayout.WEST);
        totalPanel.add(lblTotal, BorderLayout.EAST);
        
        // Botón confirmar
        JButton btnConfirm = crearBotonGrande("✓ Confirmar Alquiler", SUCCESS);
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
    // PANEL PROMOCIONES
    // ============================================
    private JPanel crearPanelPromociones() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BG_SECONDARY);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(INFO, 2),
            BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));
        
        JLabel title = new JLabel("🎁 Promociones");
        title.setFont(FONT_SUBTITLE);
        title.setForeground(TEXT_PRIMARY);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panel.add(title);
        panel.add(Box.createVerticalStrut(25));
        
        // Sección por horas
        panel.add(crearLabelPromo("⏱️ Por Horas", true));
        panel.add(Box.createVerticalStrut(10));
        panel.add(crearLabelPromo("3+ horas → 10% OFF", false));
        panel.add(crearLabelPromo("5+ horas → 15% OFF", false));
        panel.add(crearLabelPromo("8+ horas → 20% OFF", false));
        
        panel.add(Box.createVerticalStrut(25));
        
        // Sección por cantidad
        panel.add(crearLabelPromo("🚜 Por Cantidad", true));
        panel.add(Box.createVerticalStrut(10));
        panel.add(crearLabelPromo("4+ recursos → 15% OFF", false));
        panel.add(crearLabelPromo("6+ recursos → 25% OFF", false));
        
        return panel;
    }
    
    private JLabel crearLabelPromo(String texto, boolean esTitulo) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(esTitulo ? new Font("Segoe UI", Font.BOLD, 15) : FONT_NORMAL);
        lbl.setForeground(esTitulo ? TEXT_PRIMARY : TEXT_SECONDARY);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        return lbl;
    }
    
    // ============================================
    // PANEL MIS ALQUILERES
    // ============================================
    private JPanel crearPanelMisAlquileres() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(BG_PRIMARY);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        
        JPanel cardPanel = new JPanel(new BorderLayout(15, 15));
        cardPanel.setBackground(BG_SECONDARY);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 2),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("📋 Historial de Alquileres");
        titleLabel.setFont(FONT_SUBTITLE);
        titleLabel.setForeground(TEXT_PRIMARY);
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);
        
        JButton btnRefresh = crearBoton("↻ Actualizar", INFO);
        JButton btnDevolver = crearBoton("↩ Devolver Recurso", PRIMARY);
        
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
        
        tableMyAlquileres = crearTabla(modelMyAlquileres);
        JScrollPane scrollPane = crearScrollPane(tableMyAlquileres);
        
        cardPanel.add(headerPanel, BorderLayout.NORTH);
        cardPanel.add(scrollPane, BorderLayout.CENTER);
        
        panel.add(cardPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    // ============================================
    // COMPONENTES REUTILIZABLES
    // ============================================
    private JButton crearBoton(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setFont(FONT_BUTTON);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        // Efecto hover
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                btn.setBackground(color.brighter());
            }
            public void mouseExited(MouseEvent evt) {
                btn.setBackground(color);
            }
        });
        
        return btn;
    }
    
    private JButton crearBotonGrande(String texto, Color color) {
        JButton btn = crearBoton(texto, color);
        btn.setPreferredSize(new Dimension(220, 48));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        return btn;
    }
    
    private JTable crearTabla(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFont(FONT_NORMAL);
        table.setRowHeight(45);
        table.setShowGrid(true);
        table.setGridColor(BORDER_COLOR);
        table.setIntercellSpacing(new Dimension(1, 1));
        table.setSelectionBackground(PRIMARY_LIGHT);
        table.setSelectionForeground(TEXT_PRIMARY);
        table.setBackground(BG_SECONDARY);
        table.setForeground(TEXT_PRIMARY);
        
        // Header
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(PRIMARY);
        header.setForeground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder());
        header.setPreferredSize(new Dimension(header.getWidth(), 45));
        
        // Renderer personalizado
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    setBackground(row % 2 == 0 ? BG_SECONDARY : BG_HOVER);
                    setForeground(TEXT_PRIMARY);
                }
                
                setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
                
                return c;
            }
        };
        
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }
        
        return table;
    }
    
    private JScrollPane crearScrollPane(JTable table) {
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        scrollPane.setBackground(BG_SECONDARY);
        scrollPane.getViewport().setBackground(BG_SECONDARY);
        
        return scrollPane;
    }
    
    // ============================================
    // LÓGICA DE NEGOCIO - CARGAR DATOS
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
            mostrarError("Error al cargar recursos: " + e.getMessage());
        }
    }
    
    private void loadMyAlquileres() {
        modelMyAlquileres.setRowCount(0);
        if (idTurista == null || idTurista.isEmpty()) return;
        
        try (Connection conn = conexion.getConnection()) {
            String sql = "SELECT a.IDAlquiler, da.IDRecurso, a.FechaDeInicio, a.HoraDeInicio, a.Duracion, " +
                        "r.Recurso, r.TarifaPorHora " +
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
            mostrarError("Error al cargar alquileres: " + e.getMessage());
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
    
    // ============================================
    // LÓGICA DEL CARRITO
    // ============================================
    private void initTableClick() {
        tablaRecursos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = tablaRecursos.getSelectedRow();
                    if (row == -1) return;
                    
                    String nombre = tablaRecursos.getValueAt(row, 1).toString();
                    String tarifaStr = tablaRecursos.getValueAt(row, 3).toString();
                    double tarifa = Double.parseDouble(tarifaStr.replace("S/ ", ""));
                    
                    String rutaImagen = "/imagenes/" + nombre.toLowerCase().replace(" ", "_") + ".jpg";
                    mostrarImagenRecurso(nombre, tarifa, rutaImagen);
                }
            }
        });
    }
    
    private void mostrarImagenRecurso(String nombre, double tarifa, String rutaImagen) {
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(rutaImagen));
            JLabel lblImagen = new JLabel(icon);
            lblImagen.setHorizontalAlignment(JLabel.CENTER);
            
            JOptionPane.showMessageDialog(this, lblImagen,
                nombre + " - S/ " + String.format("%.2f", tarifa) + " por hora",
                JOptionPane.PLAIN_MESSAGE);
        } catch (Exception e) {
            mostrarError("No se pudo cargar la imagen del recurso");
        }
    }
    
    private void addToCart() {
        int selectedRow = tablaRecursos.getSelectedRow();
        if (selectedRow == -1) {
            mostrarAdvertencia("Por favor selecciona un recurso de la tabla");
            return;
        }
        
        String id = modelRecursos.getValueAt(selectedRow, 0).toString();
        String nombre = modelRecursos.getValueAt(selectedRow, 1).toString();
        String tarifaStr = modelRecursos.getValueAt(selectedRow, 3).toString();
        double tarifa = Double.parseDouble(tarifaStr.replace("S/ ", ""));
        
        String horasStr = JOptionPane.showInputDialog(this,
            "¿Cuántas horas desea alquilar?",
            "Horas de Alquiler",
            JOptionPane.QUESTION_MESSAGE);
        
        if (horasStr == null || horasStr.trim().isEmpty()) return;
        
        try {
            int horas = Integer.parseInt(horasStr);
            
            if (horas <= 0) {
                mostrarError("Las horas deben ser mayor a 0");
                return;
            }
            
            cart.add(new CartItem(id, nombre, tarifa, horas));
            updateCartTable();
        } catch (NumberFormatException e) {
            mostrarError("Por favor ingresa un número válido");
        }
    }
    
    private void removeFromCart() {
        int selectedRow = tableCart.getSelectedRow();
        if (selectedRow == -1) {
            mostrarAdvertencia("Selecciona un item del carrito");
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
    
    // ============================================
    // PROCESAMIENTO DE ALQUILER
    // ============================================
    private void processAlquiler() {
        if (cart.isEmpty()) {
            mostrarAdvertencia("El carrito está vacío");
            return;
        }
        
        if (idTurista == null || idTurista.isEmpty()) {
            mostrarError("No se pudo identificar al turista");
            return;
        }
        
        // Calcular totales
        int horasTotales = 0;
        double totalBruto = 0;
        
        for (CartItem item : cart) {
            horasTotales += item.horas;
            totalBruto += item.tarifaPorHora * item.horas;
        }
        
        int cantidadRecursos = cart.size();
        
        // Determinar promoción
        Promocion promocionAplicada = determinarPromocion(horasTotales, cantidadRecursos);
        
        double descuento = 0;
        if (promocionAplicada != null) {
            descuento = totalBruto * (promocionAplicada.getPorcentajeDescuento() / 100.0);
        }
        
        double totalPagar = totalBruto - descuento;
        
        // Mensaje de confirmación
        String mensajeConfirmacion = "Total bruto: S/ " + String.format("%.2f", totalBruto);
        
        if (promocionAplicada != null) {
            mensajeConfirmacion += "\nPromoción aplicada: " + promocionAplicada.getCondiciones() +
                "\nDescuento: " + promocionAplicada.getPorcentajeDescuento() + "%";
        }
        
        mensajeConfirmacion += "\n\nTotal a pagar: S/ " + String.format("%.2f", totalPagar) +
            "\n\n¿Confirmar alquiler?";
        
        int confirm = JOptionPane.showConfirmDialog(this, mensajeConfirmacion,
            "Confirmar Alquiler", JOptionPane.YES_NO_OPTION);
        
        if (confirm != JOptionPane.YES_OPTION) return;
        
        // Procesar en base de datos
        try (Connection conn = conexion.getConnection()) {
            conn.setAutoCommit(false);
            
            int duracionTotal = 0;
            for (CartItem item : cart) duracionTotal += item.horas;
            int duracionPromedio = duracionTotal / cart.size();
            
            String idAlquiler = generarNuevoIDAlquiler(conn);
            
            // Insertar alquiler
            String sqlAlquiler = "INSERT INTO ALQUILER (IDAlquiler, FechaDeInicio, HoraDeInicio, Duracion) " +
                "VALUES (?, ?, ?, ?)";
            
            PreparedStatement pstAlquiler = conn.prepareStatement(sqlAlquiler);
            pstAlquiler.setString(1, idAlquiler);
            pstAlquiler.setDate(2, Date.valueOf(LocalDate.now()));
            pstAlquiler.setTime(3, Time.valueOf(LocalTime.now()));
            pstAlquiler.setInt(4, duracionPromedio);
            pstAlquiler.executeUpdate();
            
            // Insertar detalles
            for (CartItem item : cart) {
                String idDetalle = generarNuevoIDDetalleAlquiler(conn);
                
                String sqlDetalle = "INSERT INTO DETALLEALQUILER " +
                    "(IDDetalleAlquiler, IDRecurso, IDTurista, IDAlquiler, IDPromocion) " +
                    "VALUES (?, ?, ?, ?, ?)";
                
                PreparedStatement pstDetalle = conn.prepareStatement(sqlDetalle);
                pstDetalle.setString(1, idDetalle);
                pstDetalle.setString(2, item.idRecurso);
                pstDetalle.setString(3, idTurista);
                pstDetalle.setString(4, idAlquiler);
                pstDetalle.setString(5, promocionAplicada != null ? 
                    promocionAplicada.getIDPromocion() : null);
                pstDetalle.executeUpdate();
                
                // Actualizar estado del recurso
                String sqlEstado = "UPDATE RECURSOS SET Estado = 'alquilado' WHERE IDRecurso = ?";
                PreparedStatement pstEstado = conn.prepareStatement(sqlEstado);
                pstEstado.setString(1, item.idRecurso);
                pstEstado.executeUpdate();
            }
            
            conn.commit();
            
            // Mensaje de éxito
            String mensajeFinal = "✅ ¡Alquiler confirmado exitosamente!\n\n" +
                "ID Alquiler: " + idAlquiler +
                "\nItems: " + cart.size() +
                "\nTotal bruto: S/ " + String.format("%.2f", totalBruto);
            
            if (promocionAplicada != null) {
                mensajeFinal += "\nPromoción aplicada: " + promocionAplicada.getCondiciones() +
                    "\nDescuento: " + promocionAplicada.getPorcentajeDescuento() + "%";
            }
            
            mensajeFinal += "\n\nTotal pagado: S/ " + String.format("%.2f", totalPagar) +
                "\n\n¡Disfruta tu experiencia!";
            
            JOptionPane.showMessageDialog(this, mensajeFinal, "Éxito", 
                JOptionPane.INFORMATION_MESSAGE);
            
            clearCart();
            loadRecursos();
            loadMyAlquileres();
            
        } catch (SQLException e) {
            mostrarError("Error al procesar el alquiler:\n" + e.getMessage());
        }
    }
    
    private Promocion determinarPromocion(int horasTotales, int cantidadRecursos) {
        try {
            PromocionDAO dao = new PromocionDAO();
            
            if (cantidadRecursos >= 6) return dao.obtenerPorId("P005");
            if (cantidadRecursos >= 4) return dao.obtenerPorId("P004");
            if (horasTotales >= 8) return dao.obtenerPorId("P003");
            if (horasTotales >= 5) return dao.obtenerPorId("P002");
            if (horasTotales >= 3) return dao.obtenerPorId("P001");
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // ============================================
    // DEVOLUCIÓN DE RECURSOS
    // ============================================
    private void returnProduct() {
        int fila = tableMyAlquileres.getSelectedRow();
        if (fila == -1) {
            mostrarAdvertencia("Por favor selecciona un alquiler de la tabla");
            return;
        }
        
        String idAlquiler = tableMyAlquileres.getValueAt(fila, 0).toString();
        String idRecurso = tableMyAlquileres.getValueAt(fila, 1).toString();
        String recursoNombre = tableMyAlquileres.getValueAt(fila, 5).toString();
        
        int confirm = JOptionPane.showConfirmDialog(this,
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
            mostrarError("Error al devolver el recurso");
        }
    }
    
    // ============================================
    // GENERADORES DE ID
    // ============================================
    private String generarNuevoIDAlquiler(Connection conn) throws SQLException {
        String prefijo = "A";
        int siguienteNumero = 1;
        
        String sql = "SELECT TOP 1 IDAlquiler FROM ALQUILER WHERE IDAlquiler LIKE ? " +
            "ORDER BY IDAlquiler DESC";
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
    
    private String generarNuevoIDDetalleAlquiler(Connection conn) throws SQLException {
        String prefijo = "D";
        int siguienteNumero = 1;
        
        String sql = "SELECT TOP 1 IDDetalleAlquiler FROM DETALLEALQUILER " +
            "WHERE IDDetalleAlquiler LIKE ? ORDER BY IDDetalleAlquiler DESC";
        
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
    
    // ============================================
    // UTILIDADES
    // ============================================
    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    private void mostrarAdvertencia(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Aviso", JOptionPane.WARNING_MESSAGE);
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
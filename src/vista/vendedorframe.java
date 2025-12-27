package vista;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;
import database.conexion;
import modelo.Usuario;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class vendedorframe extends JFrame {
    
    // Paleta de colores oscuros moderna
    private static final Color PRIMARY = new Color(139, 92, 246); // Violeta
    private static final Color PRIMARY_DARK = new Color(109, 40, 217);
    private static final Color SECONDARY = new Color(236, 72, 153); // Rosa
    private static final Color SUCCESS = new Color(34, 197, 94); // Verde
    private static final Color DANGER = new Color(239, 68, 68); // Rojo
    private static final Color WARNING = new Color(251, 146, 60); // Naranja
    private static final Color INFO = new Color(59, 130, 246); // Azul
    
    // Colores de fondo oscuros
    private static final Color BG_DARK = new Color(17, 24, 39); // Gris muy oscuro
    private static final Color BG_CARD = new Color(31, 41, 55); // Gris oscuro para tarjetas
    private static final Color BG_HOVER = new Color(45, 55, 72); // Gris medio para hover
    
    // Colores de texto
    private static final Color TEXT_PRIMARY = new Color(243, 244, 246); // Blanco suave
    private static final Color TEXT_SECONDARY = new Color(156, 163, 175); // Gris claro
    private static final Color BORDER_COLOR = new Color(55, 65, 81); // Borde gris oscuro
    
    private Usuario currentUser;
    private JTable tablaRecursos;
    private DefaultTableModel modelRecursos;
    private JTable tableCart;
    private DefaultTableModel modelCart;
    private JLabel lblTotal;
    private ArrayList<CartItem> cart = new ArrayList<>();
    
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
    
    public vendedorframe(Usuario user) {
        this.currentUser = user;
        
        setTitle("Sistema de Alquiler - Panel Vendedor");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_DARK);
        
        setLayout(new BorderLayout(0, 0));
        
        // Header moderno oscuro
        add(createModernHeader(), BorderLayout.NORTH);
        
        // Contenido principal
        add(createMainContent(), BorderLayout.CENTER);
        
        loadProducts();
    }
    
    // ============================================
    // HEADER MODERNO OSCURO
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
        
        JLabel titleLabel = new JLabel("💼 Panel de Ventas");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_PRIMARY);
        
        JLabel welcomeLabel = new JLabel("Vendedor: " + currentUser.getNombre());
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
    // CONTENIDO PRINCIPAL
    // ============================================
    private JPanel createMainContent() {
        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        mainPanel.setBackground(BG_DARK);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Panel izquierdo - Recursos
        mainPanel.add(createRecursosPanel());
        
        // Panel derecho - Carrito
        mainPanel.add(createCarritoPanel());
        
        return mainPanel;
    }
    
    // ============================================
    // PANEL DE RECURSOS
    // ============================================
    private JPanel createRecursosPanel() {
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
        btnRefresh.addActionListener(e -> loadProducts());
        
        btnPanel.add(btnRefresh);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(btnPanel, BorderLayout.EAST);
        
        // Tabla
        modelRecursos = new DefaultTableModel(
            new String[]{"ID", "Recurso", "Tarifa/Hora", "Estado"}, 0
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
            BorderFactory.createLineBorder(PRIMARY, 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("🛒 Carrito de Compras");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(TEXT_PRIMARY);
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);
        
        JButton btnRemove = createModernButton("Eliminar", DANGER, "✕");
        JButton btnClear = createModernButton("Limpiar", WARNING, "🗑");
        
        btnRemove.setPreferredSize(new Dimension(110, 36));
        btnClear.setPreferredSize(new Dimension(110, 36));
        
        btnRemove.addActionListener(e -> removeFromCart());
        btnClear.addActionListener(e -> clearCart());
        
        btnPanel.add(btnRemove);
        btnPanel.add(btnClear);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(btnPanel, BorderLayout.EAST);
        
        // Tabla del carrito
        modelCart = new DefaultTableModel(
            new String[]{"Recurso", "Tarifa/Hora", "Horas", "Subtotal"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableCart = createModernTable(modelCart);
        JScrollPane scrollPane = createModernScrollPane(tableCart);
        
        // Panel inferior con total y procesar
        JPanel bottomPanel = new JPanel(new BorderLayout(0, 15));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        
        // Total
        JPanel totalPanel = new JPanel(new BorderLayout());
        totalPanel.setBackground(new Color(45, 55, 72));
        totalPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY, 2),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        JLabel lblTotalText = new JLabel("TOTAL:");
        lblTotalText.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTotalText.setForeground(TEXT_SECONDARY);
        
        lblTotal = new JLabel("S/ 0.00");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTotal.setForeground(SUCCESS);
        lblTotal.setHorizontalAlignment(SwingConstants.RIGHT);
        
        totalPanel.add(lblTotalText, BorderLayout.WEST);
        totalPanel.add(lblTotal, BorderLayout.EAST);
        
        // Botón procesar
        JButton btnProcess = createModernButton("💳 Procesar Alquiler", SUCCESS, "");
        btnProcess.setPreferredSize(new Dimension(250, 50));
        btnProcess.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnProcess.addActionListener(e -> processRental());
        
        JPanel btnProcessPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnProcessPanel.setOpaque(false);
        btnProcessPanel.add(btnProcess);
        
        bottomPanel.add(totalPanel, BorderLayout.NORTH);
        bottomPanel.add(btnProcessPanel, BorderLayout.SOUTH);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
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
        
        // Personalizar scrollbars
        scrollPane.getVerticalScrollBar().setBackground(BG_CARD);
        scrollPane.getHorizontalScrollBar().setBackground(BG_CARD);
        
        return scrollPane;
    }
    
    // ============================================
    // LÓGICA DE NEGOCIO
    // ============================================
    private void loadProducts() {
        modelRecursos.setRowCount(0);
        try (Connection conn = conexion.getConnection()) {
            String sql = "SELECT * FROM RECURSOS WHERE Estado = 'DISPONIBLE' OR Estado = 'disponible'";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                modelRecursos.addRow(new Object[]{
                    rs.getString("IDRecurso"),
                    rs.getString("Recurso"),
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
        String tarifaStr = modelRecursos.getValueAt(selectedRow, 2).toString();

        tarifaStr = tarifaStr.replace("S/", "").replace("$", "").trim();
        double tarifa = Double.parseDouble(tarifaStr);

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

            boolean found = false;
            for (CartItem item : cart) {
                if (item.idRecurso.equals(id)) {
                    item.horas += horas;
                    found = true;
                    break;
                }
            }

            if (!found) {
                cart.add(new CartItem(id, nombre, tarifa, horas));
            }

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
    
    private void processRental() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "El carrito está vacío",
                "Aviso",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        String dni = JOptionPane.showInputDialog(this, 
            "Ingrese el DNI del Turista:",
            "Datos del Cliente",
            JOptionPane.QUESTION_MESSAGE);
        
        if (dni == null || dni.trim().isEmpty()) return;

        try (Connection conn = conexion.getConnection()) {
            conn.setAutoCommit(false);

            String sqlTurista = "SELECT IDTurista FROM TURISTAA WHERE DNI = ?";
            PreparedStatement pstTurista = conn.prepareStatement(sqlTurista);
            pstTurista.setString(1, dni);
            ResultSet rsTurista = pstTurista.executeQuery();

            if (!rsTurista.next()) {
                JOptionPane.showMessageDialog(this, 
                    "Turista no encontrado con DNI: " + dni,
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                conn.rollback();
                return;
            }

            String idTurista = rsTurista.getString("IDTurista");

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
            pstAlquiler.setDate(2, java.sql.Date.valueOf(LocalDate.now()));
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

                String sqlEstado = "UPDATE RECURSOS SET Estado='alquilado' WHERE IDRecurso=?";
                PreparedStatement pstEstado = conn.prepareStatement(sqlEstado);
                pstEstado.setString(1, item.idRecurso);
                pstEstado.executeUpdate();
            }

            conn.commit();

            double total = 0;
            for (CartItem item : cart) {
                total += item.tarifaPorHora * item.horas;
            }

            JOptionPane.showMessageDialog(this,
                String.format(
                    "✅ ¡Alquiler procesado exitosamente!\n\n" +
                    "ID Alquiler: %s\n" +
                    "Cliente: DNI %s\n" +
                    "Total: S/ %.2f\n" +
                    "Items: %d recurso(s)",
                    idAlquiler, dni, total, cart.size()
                ),
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE);

            clearCart();
            loadProducts();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error al procesar el alquiler:\n" + e.getMessage(),
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
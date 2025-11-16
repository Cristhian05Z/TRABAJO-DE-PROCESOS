package vista;

import javax.swing.*;
import javax.swing.table.*;
import database.conexion;
import modelo.Usuario;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;



public class turistaframe extends JFrame {
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
    private int idTurista = -1; 
    
    class CartItem {
        int idRecurso;
        String nombre;
        double tarifaPorHora;
        int horas;
        
        CartItem(int id, String nombre, double tarifa, int horas) {
            this.idRecurso = id;
            this.nombre = nombre;
            this.tarifaPorHora = tarifa;
            this.horas = horas;
        }
    }
    
    public turistaframe(Usuario user) {
        this.currentUser = user;
        
        setTitle("Panel Turista - " + user.getNombre());
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Panel superior
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(241, 196, 15));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel lblWelcome = new JLabel("Bienvenido, " + user.getNombre());
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
        tabbedPane.addTab("🏖️ Alquilar", createAlquilerPanel());
        tabbedPane.addTab("📋 Mis Alquileres", createMyAlquileresPanel());
        
        add(topPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
        
        loadRecursos();
        loadMyAlquileres();
    }
    
    private JPanel createAlquilerPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel superior - Recursos
        JPanel topSection = new JPanel(new BorderLayout(10, 10));
        topSection.setBorder(BorderFactory.createTitledBorder("Recursos Disponibles"));
        
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
        JScrollPane scrollProducts = new JScrollPane(tablaRecursos);
        scrollProducts.setPreferredSize(new Dimension(0, 250));
        
        JPanel btnPanelTop = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAddToCart = new JButton("Agregar al Carrito");
        JButton btnRefresh = new JButton("Actualizar");
        
        btnAddToCart.setBackground(new Color(52, 152, 219));
        btnAddToCart.setForeground(Color.WHITE);
        btnAddToCart.setFont(new Font("Arial", Font.BOLD, 12));
        btnAddToCart.addActionListener(e -> addToCart());
        btnRefresh.addActionListener(e -> loadRecursos());
        
        btnPanelTop.add(btnAddToCart);
        btnPanelTop.add(btnRefresh);
        
        topSection.add(btnPanelTop, BorderLayout.NORTH);
        topSection.add(scrollProducts, BorderLayout.CENTER);
        
        // Panel inferior - Carrito
        JPanel bottomSection = new JPanel(new BorderLayout(10, 10));
        bottomSection.setBorder(BorderFactory.createTitledBorder("Mi Carrito"));
        
        modelCart = new DefaultTableModel(
            new String[]{"Recurso", "Tarifa", "Horas", "Subtotal"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableCart = new JTable(modelCart);
        tableCart.setRowHeight(30);
        JScrollPane scrollCart = new JScrollPane(tableCart);
        
        JPanel btnPanelBottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnRemove = new JButton("Quitar");
        JButton btnClear = new JButton("Limpiar");
        
        btnRemove.setBackground(new Color(231, 76, 60));
        btnRemove.setForeground(Color.WHITE);
        btnRemove.addActionListener(e -> removeFromCart());
        btnClear.addActionListener(e -> clearCart());
        
        btnPanelBottom.add(btnRemove);
        btnPanelBottom.add(btnClear);
        
        // Panel de total
        JPanel totalPanel = new JPanel(new BorderLayout());
        totalPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        lblTotal = new JLabel("Total: $0.00");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 24));
        lblTotal.setHorizontalAlignment(SwingConstants.RIGHT);
        
        JButton btnRent = new JButton("Confirmar Alquiler");
        btnRent.setBackground(new Color(46, 204, 113));
        btnRent.setForeground(Color.WHITE);
        btnRent.setFont(new Font("Arial", Font.BOLD, 16));
        btnRent.setPreferredSize(new Dimension(200, 45));
        btnRent.addActionListener(e -> processAlquiler());
        
        JPanel btnContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnContainer.add(btnRent);
        
        totalPanel.add(lblTotal, BorderLayout.NORTH);
        totalPanel.add(btnContainer, BorderLayout.SOUTH);
        
        bottomSection.add(btnPanelBottom, BorderLayout.NORTH);
        bottomSection.add(scrollCart, BorderLayout.CENTER);
        bottomSection.add(totalPanel, BorderLayout.SOUTH);
        
        // Dividir con JSplitPane
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topSection, bottomSection);
        splitPane.setDividerLocation(300);
        
        mainPanel.add(splitPane, BorderLayout.CENTER);
        
        return mainPanel;
    }
    
    private JPanel createMyAlquileresPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        modelMyAlquileres = new DefaultTableModel(
            new String[]{"ID Alquiler", "Fecha", "Hora", "Duración", "Recurso", "Tarifa"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableMyAlquileres = new JTable(modelMyAlquileres);
        tableMyAlquileres.setRowHeight(30);
        JScrollPane scrollPane = new JScrollPane(tableMyAlquileres);
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnRefresh = new JButton("Actualizar");
        JButton btnDevolver = new JButton("Devolver Recurso");

        btnDevolver.setBackground(new Color(52, 152, 219));
        btnDevolver.setForeground(Color.WHITE);
        btnDevolver.addActionListener(e -> returnProduct());
        btnRefresh.addActionListener(e -> loadMyAlquileres());
        
        btnPanel.add(btnRefresh);
        btnPanel.add(btnDevolver);
        
        panel.add(btnPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
     private void loadRecursos() {
        modelRecursos.setRowCount(0);
        try (Connection conn = conexion.getConnection()) {
            String sql = "SELECT * FROM RECURSOS WHERE Estado = 'disponible'";
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
        }
    }
    
     private void loadMyAlquileres() {
        modelMyAlquileres.setRowCount(0);
        if (idTurista == -1) return;
        
        try (Connection conn = conexion.getConnection()) {
            String sql = "SELECT a.IDAlquiler, a.FechaDeInicio, a.HoraDeInicio, a.Duracion, " +
                        "r.Recurso, r.TarifaPorHora, r.Estado " +
                        "FROM Alquiler a " +
                        "JOIN DETALLEALQUILER da ON a.IDAlquiler = da.IDAlquiler " +
                        "JOIN RECURSOS r ON da.IDRecurso = r.IDRecursos " +
                        "WHERE da.IDTurista = ? " +
                        "ORDER BY a.FechaDeInicio DESC";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, idTurista);
            ResultSet rs = pst.executeQuery();
            
            while (rs.next()) {
                modelMyAlquileres.addRow(new Object[]{
                    rs.getInt("IDAlquiler"),
                    rs.getDate("FechaDeInicio"),
                    rs.getTime("HoraDeInicio"),
                    rs.getInt("Duracion") + " hrs",
                    rs.getString("Recurso"),
                    String.format("S/ %.2f", rs.getDouble("TarifaPorHora"))
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void addToCart() {
        int selectedRow = tablaRecursos.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un recurso");
            return;
        }
        
        int id = (int) modelRecursos.getValueAt(selectedRow, 0);
        String nombre = (String) modelRecursos.getValueAt(selectedRow, 1);
        String tarifaStr = (String) modelRecursos.getValueAt(selectedRow, 3);
        double tarifa = Double.parseDouble(tarifaStr.replace("S/ ", ""));
        
        String horasStr = JOptionPane.showInputDialog(this, "¿Cuántas horas?", "1");
        if (horasStr == null || horasStr.isEmpty()) return;
        
        int horas = Integer.parseInt(horasStr);
        
        cart.add(new CartItem(id, nombre, tarifa, horas));
        updateCartTable();
    }
    
    private void removeFromCart() {
        int selectedRow = tableCart.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un item del carrito");
            return;
        }
        
        cart.remove(selectedRow);
        updateCartTable();
    }
    
    private void clearCart() {
        cart.clear();
        updateCartTable();
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
        
        lblTotal.setText(String.format("Total: S/ %.2f", total));
    }
    
    private void processAlquiler() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El carrito está vacío");
            return;
        }
        
        if (idTurista == -1) {
            JOptionPane.showMessageDialog(this, "No se pudo identificar al turista");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Confirmar alquiler?",
            "Confirmar",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm != JOptionPane.YES_OPTION) return;
        
        try (Connection conn = conexion.getConnection()) {
            conn.setAutoCommit(false);
            
            // Calcular duración promedio
            int duracionTotal = 0;
            for (CartItem item : cart) {
                duracionTotal += item.horas;
            }
            int duracionPromedio = duracionTotal / cart.size();
            
            // Insertar alquiler
            String sqlAlquiler = "INSERT INTO Alquiler (FechaDeInicio, HoraDeInicio, Duracion) VALUES (?, ?, ?)";
            PreparedStatement pstAlquiler = conn.prepareStatement(sqlAlquiler, Statement.RETURN_GENERATED_KEYS);
            pstAlquiler.setDate(1, Date.valueOf(LocalDate.now()));
            pstAlquiler.setTime(2, Time.valueOf(LocalTime.now()));
            pstAlquiler.setInt(3, duracionPromedio);
            pstAlquiler.executeUpdate();
            
            ResultSet rsAlquiler = pstAlquiler.getGeneratedKeys();
            int idAlquiler = 0;
            if (rsAlquiler.next()) {
                idAlquiler = rsAlquiler.getInt(1);
            }
            
            // Insertar detalles
            for (CartItem item : cart) {
                String sqlDetalle = "INSERT INTO DETALLEALQUILER (IDRecurso, IDTurista, IDAlquiler, IDPromocion) VALUES (?, ?, ?, NULL)";
                PreparedStatement pstDetalle = conn.prepareStatement(sqlDetalle);
                pstDetalle.setInt(1, item.idRecurso);
                pstDetalle.setInt(2, idTurista);
                pstDetalle.setInt(3, idAlquiler);
                pstDetalle.executeUpdate();
            
            conn.commit();
            
            JOptionPane.showMessageDialog(this,
                String.format("¡Alquiler confirmado!\nTotal: $%.2f\nID: %d", duracionTotal, idAlquiler),
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE);
            
            clearCart();
            loadRecursos();
            loadMyAlquileres();
            
        }
     } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
    
    private void returnProduct() {
        int selectedRow = tableMyAlquileres.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un alquiler");
            return;
        }
        
        String estado = (String) modelMyAlquileres.getValueAt(selectedRow, 5);
        if (estado.equals("devuelto")) {
            JOptionPane.showMessageDialog(this, "Este producto ya fue devuelto");
            return;
        }
        
        int rentaId = (int) modelMyAlquileres.getValueAt(selectedRow, 0);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Devolver este producto?",
            "Confirmar",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm != JOptionPane.YES_OPTION) return;
        
        try (Connection conn = conexion.getConnection()) {
            conn.setAutoCommit(false);
            
            // Actualizar estado
            String sql = "UPDATE rentas SET estado = 'devuelto' WHERE id = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, rentaId);
            pst.executeUpdate();
            
            // Restaurar stock
            String sqlStock = "UPDATE productos p " +
                             "JOIN renta_detalles rd ON p.id = rd.producto_id " +
                             "SET p.stock = p.stock + rd.cantidad " +
                             "WHERE rd.renta_id = ?";
            PreparedStatement pstStock = conn.prepareStatement(sqlStock);
            pstStock.setInt(1, rentaId);
            pstStock.executeUpdate();
            
            conn.commit();
            
            JOptionPane.showMessageDialog(this, "Producto devuelto exitosamente");
            loadMyAlquileres();
            loadRecursos();
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
    
    private void logout() {
        dispose();
        new login().setVisible(true);
    }
}

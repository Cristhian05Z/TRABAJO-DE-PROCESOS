package vista;

import javax.swing.*;
import javax.swing.table.*;
import database.conexion;
import modelo.Usuario;

import java.awt.*;
import java.sql.*;
import java.util.*;
public class tursita extends JFrame {
    private Usuario currentUser;
    private JTable tableProducts;
    private DefaultTableModel modelProducts;
    private JTable tableMyRentals;
    private DefaultTableModel modelMyRentals;
    private JTable tableCart;
    private DefaultTableModel modelCart;
    private JLabel lblTotal;
    private ArrayList<CartItem> cart = new ArrayList<>();
    private JTabbedPane tabbedPane;
    
    class CartItem {
        int productId;
        String nombre;
        double precio;
        int cantidad;
        
        CartItem(int id, String nombre, double precio, int cantidad) {
            this.productId = id;
            this.nombre = nombre;
            this.precio = precio;
            this.cantidad = cantidad;
        }
    }
    
    public tursita(Usuario user) {
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
        tabbedPane.addTab("🏖️ Alquilar", createRentalPanel());
        tabbedPane.addTab("📋 Mis Alquileres", createMyRentalsPanel());
        
        add(topPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
        
        loadProducts();
        loadMyRentals();
    }
    
    private JPanel createRentalPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel superior - Productos
        JPanel topSection = new JPanel(new BorderLayout(10, 10));
        topSection.setBorder(BorderFactory.createTitledBorder("Productos Disponibles"));
        
        modelProducts = new DefaultTableModel(
            new String[]{"ID", "Nombre", "Precio/Día", "Stock", "Categoría"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableProducts = new JTable(modelProducts);
        tableProducts.setRowHeight(30);
        JScrollPane scrollProducts = new JScrollPane(tableProducts);
        scrollProducts.setPreferredSize(new Dimension(0, 250));
        
        JPanel btnPanelTop = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAddToCart = new JButton("Agregar al Carrito");
        JButton btnRefresh = new JButton("Actualizar");
        
        btnAddToCart.setBackground(new Color(52, 152, 219));
        btnAddToCart.setForeground(Color.WHITE);
        btnAddToCart.setFont(new Font("Arial", Font.BOLD, 12));
        btnAddToCart.addActionListener(e -> addToCart());
        btnRefresh.addActionListener(e -> loadProducts());
        
        btnPanelTop.add(btnAddToCart);
        btnPanelTop.add(btnRefresh);
        
        topSection.add(btnPanelTop, BorderLayout.NORTH);
        topSection.add(scrollProducts, BorderLayout.CENTER);
        
        // Panel inferior - Carrito
        JPanel bottomSection = new JPanel(new BorderLayout(10, 10));
        bottomSection.setBorder(BorderFactory.createTitledBorder("Mi Carrito"));
        
        modelCart = new DefaultTableModel(
            new String[]{"Producto", "Precio", "Días", "Subtotal"}, 0
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
        btnRent.addActionListener(e -> processRental());
        
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
    
    private JPanel createMyRentalsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        modelMyRentals = new DefaultTableModel(
            new String[]{"ID", "Fecha", "Producto", "Cantidad", "Total", "Estado"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableMyRentals = new JTable(modelMyRentals);
        tableMyRentals.setRowHeight(30);
        JScrollPane scrollPane = new JScrollPane(tableMyRentals);
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnRefresh = new JButton("Actualizar");
        JButton btnReturn = new JButton("Devolver Producto");
        
        btnReturn.setBackground(new Color(52, 152, 219));
        btnReturn.setForeground(Color.WHITE);
        btnReturn.addActionListener(e -> returnProduct());
        btnRefresh.addActionListener(e -> loadMyRentals());
        
        btnPanel.add(btnRefresh);
        btnPanel.add(btnReturn);
        
        panel.add(btnPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void loadProducts() {
        modelProducts.setRowCount(0);
        try (Connection conn = conexion.getConnection()) {
            String sql = "SELECT * FROM productos WHERE stock > 0";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                modelProducts.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    String.format("$%.2f", rs.getDouble("precio")),
                    rs.getInt("stock"),
                    rs.getString("categoria")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void loadMyRentals() {
        modelMyRentals.setRowCount(0);
        try (Connection conn = conexion.getConnection()) {
            String sql = "SELECT r.id, r.fecha, p.nombre, rd.cantidad, rd.precio_unitario * rd.cantidad as total, r.estado " +
                        "FROM rentas r " +
                        "JOIN renta_detalles rd ON r.id = rd.renta_id " +
                        "JOIN productos p ON rd.producto_id = p.id " +
                        "WHERE r.usuario_id = ? " +
                        "ORDER BY r.fecha DESC";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, currentUser.getIDUsuario());
            ResultSet rs = pst.executeQuery();
            
            while (rs.next()) {
                modelMyRentals.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("fecha"),
                    rs.getString("nombre"),
                    rs.getInt("cantidad"),
                    String.format("$%.2f", rs.getDouble("total")),
                    rs.getString("estado")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void addToCart() {
        int selectedRow = tableProducts.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un producto");
            return;
        }
        
        int id = (int) modelProducts.getValueAt(selectedRow, 0);
        String nombre = (String) modelProducts.getValueAt(selectedRow, 1);
        String precioStr = (String) modelProducts.getValueAt(selectedRow, 2);
        double precio = Double.parseDouble(precioStr.replace("$", ""));
        int stockDisponible = (int) modelProducts.getValueAt(selectedRow, 3);
        
        String diasStr = JOptionPane.showInputDialog(this, "¿Cuántos días?", "1");
        if (diasStr == null || diasStr.isEmpty()) return;
        
        int dias = Integer.parseInt(diasStr);
        
        if (dias > stockDisponible) {
            JOptionPane.showMessageDialog(this, "Stock insuficiente");
            return;
        }
        
        // Verificar si ya está en el carrito
        boolean found = false;
        for (CartItem item : cart) {
            if (item.productId == id) {
                item.cantidad += dias;
                found = true;
                break;
            }
        }
        
        if (!found) {
            cart.add(new CartItem(id, nombre, precio, dias));
        }
        
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
            double subtotal = item.precio * item.cantidad;
            total += subtotal;
            modelCart.addRow(new Object[]{
                item.nombre,
                String.format("$%.2f", item.precio),
                item.cantidad + " días",
                String.format("$%.2f", subtotal)
            });
        }
        
        lblTotal.setText(String.format("Total: $%.2f", total));
    }
    
    private void processRental() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El carrito está vacío");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Confirmar alquiler?",
            "Confirmar",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm != JOptionPane.YES_OPTION) return;
        
        double total = 0;
        for (CartItem item : cart) {
            total += item.precio * item.cantidad;
        }
        
        try (Connection conn = conexion.getConnection()) {
            conn.setAutoCommit(false);
            
            // Insertar la renta
            String sql = "INSERT INTO rentas (usuario_id, fecha, total, estado) VALUES (?, CURDATE(), ?, 'activo')";
            PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pst.setInt(1, currentUser.getIDUsuario());
            pst.setDouble(2, total);
            pst.executeUpdate();
            
            ResultSet rs = pst.getGeneratedKeys();
            int rentaId = 0;
            if (rs.next()) {
                rentaId = rs.getInt(1);
            }
            
            // Insertar detalles
            for (CartItem item : cart) {
                String sqlDetail = "INSERT INTO renta_detalles (renta_id, producto_id, cantidad, precio_unitario) VALUES (?, ?, ?, ?)";
                PreparedStatement pstDetail = conn.prepareStatement(sqlDetail);
                pstDetail.setInt(1, rentaId);
                pstDetail.setInt(2, item.productId);
                pstDetail.setInt(3, item.cantidad);
                pstDetail.setDouble(4, item.precio);
                pstDetail.executeUpdate();
                
                // Actualizar stock
                String sqlStock = "UPDATE productos SET stock = stock - ? WHERE id = ?";
                PreparedStatement pstStock = conn.prepareStatement(sqlStock);
                pstStock.setInt(1, item.cantidad);
                pstStock.setInt(2, item.productId);
                pstStock.executeUpdate();
            }
            
            conn.commit();
            
            JOptionPane.showMessageDialog(this,
                String.format("¡Alquiler confirmado!\nTotal: $%.2f\nID: %d", total, rentaId),
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE);
            
            clearCart();
            loadProducts();
            loadMyRentals();
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
    
    private void returnProduct() {
        int selectedRow = tableMyRentals.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un alquiler");
            return;
        }
        
        String estado = (String) modelMyRentals.getValueAt(selectedRow, 5);
        if (estado.equals("devuelto")) {
            JOptionPane.showMessageDialog(this, "Este producto ya fue devuelto");
            return;
        }
        
        int rentaId = (int) modelMyRentals.getValueAt(selectedRow, 0);
        
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
            loadMyRentals();
            loadProducts();
            
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

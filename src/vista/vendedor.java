package vista;
import javax.swing.*;
import javax.swing.table.*;

import database.conexion;
import modelo.Usuario;

import java.awt.*;
import java.sql.*;
import java.util.*;

public class vendedor extends JFrame {
    private Usuario currentUser;
    private JTable tableProducts;
    private DefaultTableModel modelProducts;
    private JTable tableCart;
    private DefaultTableModel modelCart;
    private JLabel lblTotal;
    private ArrayList<CartItem> cart = new ArrayList<>();
    
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
    
    public vendedor(Usuario user) {
        this.currentUser = user;
        
        setTitle("Panel Vendedor - " + user.getNombre());
        setSize(1100, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Panel superior
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(46, 204, 113));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel lblWelcome = new JLabel("Vendedor: " + user.getNombre());
        lblWelcome.setForeground(Color.WHITE);
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 18));
        
        JButton btnLogout = new JButton("Cerrar Sesión");
        btnLogout.setBackground(new Color(231, 76, 60));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.addActionListener(e -> logout());
        
        topPanel.add(lblWelcome, BorderLayout.WEST);
        topPanel.add(btnLogout, BorderLayout.EAST);
        
        // Panel principal dividido
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(650);
        
        // Panel izquierdo - Productos
        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setBorder(BorderFactory.createTitledBorder("Productos Disponibles"));
        
        modelProducts = new DefaultTableModel(
            new String[]{"ID", "Nombre", "Precio", "Stock", "Categoría"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableProducts = new JTable(modelProducts);
        JScrollPane scrollProducts = new JScrollPane(tableProducts);
        
        JPanel btnPanelLeft = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAddToCart = new JButton("Agregar al Carrito");
        JButton btnRefresh = new JButton("Actualizar");
        
        btnAddToCart.setBackground(new Color(52, 152, 219));
        btnAddToCart.setForeground(Color.WHITE);
        btnAddToCart.addActionListener(e -> addToCart());
        btnRefresh.addActionListener(e -> loadProducts());
        
        btnPanelLeft.add(btnAddToCart);
        btnPanelLeft.add(btnRefresh);
        
        leftPanel.add(btnPanelLeft, BorderLayout.NORTH);
        leftPanel.add(scrollProducts, BorderLayout.CENTER);
        
        // Panel derecho - Carrito
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setBorder(BorderFactory.createTitledBorder("Carrito de Compras"));
        
        modelCart = new DefaultTableModel(
            new String[]{"Producto", "Precio", "Cantidad", "Subtotal"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableCart = new JTable(modelCart);
        JScrollPane scrollCart = new JScrollPane(tableCart);
        
        JPanel btnPanelRight = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnRemove = new JButton("Quitar");
        JButton btnClear = new JButton("Limpiar");
        
        btnRemove.setBackground(new Color(231, 76, 60));
        btnRemove.setForeground(Color.WHITE);
        btnRemove.addActionListener(e -> removeFromCart());
        btnClear.addActionListener(e -> clearCart());
        
        btnPanelRight.add(btnRemove);
        btnPanelRight.add(btnClear);
        
        // Panel de total y procesar
        JPanel bottomRightPanel = new JPanel(new BorderLayout());
        bottomRightPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        lblTotal = new JLabel("Total: $0.00");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 20));
        lblTotal.setHorizontalAlignment(SwingConstants.RIGHT);
        
        JButton btnProcess = new JButton("Procesar Alquiler");
        btnProcess.setBackground(new Color(46, 204, 113));
        btnProcess.setForeground(Color.WHITE);
        btnProcess.setFont(new Font("Arial", Font.BOLD, 16));
        btnProcess.setPreferredSize(new Dimension(200, 40));
        btnProcess.addActionListener(e -> processRental());
        
        bottomRightPanel.add(lblTotal, BorderLayout.NORTH);
        bottomRightPanel.add(btnProcess, BorderLayout.SOUTH);
        
        rightPanel.add(btnPanelRight, BorderLayout.NORTH);
        rightPanel.add(scrollCart, BorderLayout.CENTER);
        rightPanel.add(bottomRightPanel, BorderLayout.SOUTH);
        
        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);
        
        add(topPanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
        
        loadProducts();
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
        
        String cantidadStr = JOptionPane.showInputDialog(this, "Cantidad:", "1");
        if (cantidadStr == null || cantidadStr.isEmpty()) return;
        
        int cantidad = Integer.parseInt(cantidadStr);
        
        // Verificar si ya está en el carrito
        boolean found = false;
        for (CartItem item : cart) {
            if (item.productId == id) {
                item.cantidad += cantidad;
                found = true;
                break;
            }
        }
        
        if (!found) {
            cart.add(new CartItem(id, nombre, precio, cantidad));
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
                item.cantidad,
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
        
        // Solicitar ID del cliente
        String clienteIdStr = JOptionPane.showInputDialog(this, "ID del Cliente (Turista):");
        if (clienteIdStr == null || clienteIdStr.isEmpty()) return;
        
        int clienteId = Integer.parseInt(clienteIdStr);
        double total = 0;
        for (CartItem item : cart) {
            total += item.precio * item.cantidad;
        }
        
        try (Connection conn = conexion.getConnection()) {
            conn.setAutoCommit(false);
            
            // Insertar la renta principal
            String sql = "INSERT INTO rentas (usuario_id, vendedor_id, fecha, total, estado) VALUES (?, ?, CURDATE(), ?, 'activo')";
            PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pst.setInt(1, clienteId);
            pst.setInt(2, currentUser.getIdusuario());
            pst.setDouble(3, total);
            pst.executeUpdate();
            
            ResultSet rs = pst.getGeneratedKeys();
            int rentaId = 0;
            if (rs.next()) {
                rentaId = rs.getInt(1);
            }
            
            // Insertar detalles y actualizar stock
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
                String.format("Alquiler procesado exitosamente\nTotal: $%.2f\nRenta ID: %d", total, rentaId),
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE);
            
            clearCart();
            loadProducts();
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al procesar: " + e.getMessage());
        }
    }
    
    private void logout() {
        dispose();
        new login().setVisible(true);
    }
}

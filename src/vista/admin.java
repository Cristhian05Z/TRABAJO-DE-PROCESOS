package vista;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;
public class admin extends JFrame{

    private User currentUser;
    private JTable tableProducts;
    private DefaultTableModel modelProducts;
    private JTable tableUsers;
    private DefaultTableModel modelUsers;
    private JTable tableSales;
    private DefaultTableModel modelSales;
    private JTabbedPane tabbedPane;
    
    public admin(User user) {
        this.currentUser = user;
        
        setTitle("Panel Administrador - " + user.getNombre());
        setSize(1000, 600);
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
        tabbedPane.addTab("📦 Productos", createProductsPanel());
        tabbedPane.addTab("👥 Usuarios", createUsersPanel());
        tabbedPane.addTab("💰 Ventas", createSalesPanel());
        tabbedPane.addTab("📊 Reportes", createReportsPanel());
        
        add(topPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
        
        loadData();
    }
    
    private JPanel createProductsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Tabla
        modelProducts = new DefaultTableModel(
            new String[]{"ID", "Nombre", "Precio", "Stock", "Categoría"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableProducts = new JTable(modelProducts);
        JScrollPane scrollPane = new JScrollPane(tableProducts);
        
        // Botones
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAdd = new JButton("Agregar");
        JButton btnEdit = new JButton("Editar");
        JButton btnDelete = new JButton("Eliminar");
        JButton btnRefresh = new JButton("Actualizar");
        
        btnAdd.setBackground(new Color(46, 204, 113));
        btnAdd.setForeground(Color.WHITE);
        btnEdit.setBackground(new Color(52, 152, 219));
        btnEdit.setForeground(Color.WHITE);
        btnDelete.setBackground(new Color(231, 76, 60));
        btnDelete.setForeground(Color.WHITE);
        
        btnAdd.addActionListener(e -> addProduct());
        btnEdit.addActionListener(e -> editProduct());
        btnDelete.addActionListener(e -> deleteProduct());
        btnRefresh.addActionListener(e -> loadProducts());
        
        btnPanel.add(btnAdd);
        btnPanel.add(btnEdit);
        btnPanel.add(btnDelete);
        btnPanel.add(btnRefresh);
        
        panel.add(btnPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createUsersPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        modelUsers = new DefaultTableModel(
            new String[]{"ID", "Usuario", "Nombre", "Rol"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableUsers = new JTable(modelUsers);
        JScrollPane scrollPane = new JScrollPane(tableUsers);
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAdd = new JButton("Agregar Usuario");
        JButton btnDelete = new JButton("Eliminar");
        JButton btnRefresh = new JButton("Actualizar");
        
        btnAdd.setBackground(new Color(46, 204, 113));
        btnAdd.setForeground(Color.WHITE);
        btnDelete.setBackground(new Color(231, 76, 60));
        btnDelete.setForeground(Color.WHITE);
        
        btnAdd.addActionListener(e -> addUser());
        btnDelete.addActionListener(e -> deleteUser());
        btnRefresh.addActionListener(e -> loadUsers());
        
        btnPanel.add(btnAdd);
        btnPanel.add(btnDelete);
        btnPanel.add(btnRefresh);
        
        panel.add(btnPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createSalesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        modelSales = new DefaultTableModel(
            new String[]{"ID", "Cliente", "Fecha", "Total", "Estado"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableSales = new JTable(modelSales);
        JScrollPane scrollPane = new JScrollPane(tableSales);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createReportsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Tarjetas de estadísticas
        panel.add(createStatCard("Total Productos", "0", new Color(52, 152, 219)));
        panel.add(createStatCard("Total Usuarios", "0", new Color(46, 204, 113)));
        panel.add(createStatCard("Ventas Hoy", "$0", new Color(155, 89, 182)));
        panel.add(createStatCard("Ingresos Totales", "$0", new Color(241, 196, 15)));
        
        return panel;
    }
    
    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(color);
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel lblTitle = new JLabel(title);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        
        JLabel lblValue = new JLabel(value);
        lblValue.setForeground(Color.WHITE);
        lblValue.setFont(new Font("Arial", Font.BOLD, 32));
        
        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblValue, BorderLayout.CENTER);
        
        return card;
    }
    
    private void loadData() {
        loadProducts();
        loadUsers();
        loadSales();
    }
    
    private void loadProducts() {
        modelProducts.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM productos";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                modelProducts.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getDouble("precio"),
                    rs.getInt("stock"),
                    rs.getString("categoria")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void loadUsers() {
        modelUsers.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT id, username, nombre, role FROM usuarios";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                modelUsers.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("nombre"),
                    rs.getString("role")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void loadSales() {
        modelSales.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT r.id, u.nombre, r.fecha, r.total, r.estado " +
                        "FROM rentas r JOIN usuarios u ON r.usuario_id = u.id";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                modelSales.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("fecha"),
                    rs.getDouble("total"),
                    rs.getString("estado")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void addProduct() {
        JTextField txtNombre = new JTextField();
        JTextField txtPrecio = new JTextField();
        JTextField txtStock = new JTextField();
        JTextField txtCategoria = new JTextField();
        
        Object[] message = {
            "Nombre:", txtNombre,
            "Precio:", txtPrecio,
            "Stock:", txtStock,
            "Categoría:", txtCategoria
        };
        
        int option = JOptionPane.showConfirmDialog(this, message, "Agregar Producto", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                String sql = "INSERT INTO productos (nombre, precio, stock, categoria) VALUES (?, ?, ?, ?)";
                PreparedStatement pst = conn.prepareStatement(sql);
                pst.setString(1, txtNombre.getText());
                pst.setDouble(2, Double.parseDouble(txtPrecio.getText()));
                pst.setInt(3, Integer.parseInt(txtStock.getText()));
                pst.setString(4, txtCategoria.getText());
                pst.executeUpdate();
                
                JOptionPane.showMessageDialog(this, "Producto agregado exitosamente");
                loadProducts();
            } catch (SQLException | NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }
    
    private void editProduct() {
        int selectedRow = tableProducts.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un producto");
            return;
        }
        
        int id = (int) modelProducts.getValueAt(selectedRow, 0);
        JTextField txtNombre = new JTextField(modelProducts.getValueAt(selectedRow, 1).toString());
        JTextField txtPrecio = new JTextField(modelProducts.getValueAt(selectedRow, 2).toString());
        JTextField txtStock = new JTextField(modelProducts.getValueAt(selectedRow, 3).toString());
        JTextField txtCategoria = new JTextField(modelProducts.getValueAt(selectedRow, 4).toString());
        
        Object[] message = {
            "Nombre:", txtNombre,
            "Precio:", txtPrecio,
            "Stock:", txtStock,
            "Categoría:", txtCategoria
        };
        
        int option = JOptionPane.showConfirmDialog(this, message, "Editar Producto", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                String sql = "UPDATE productos SET nombre=?, precio=?, stock=?, categoria=? WHERE id=?";
                PreparedStatement pst = conn.prepareStatement(sql);
                pst.setString(1, txtNombre.getText());
                pst.setDouble(2, Double.parseDouble(txtPrecio.getText()));
                pst.setInt(3, Integer.parseInt(txtStock.getText()));
                pst.setString(4, txtCategoria.getText());
                pst.setInt(5, id);
                pst.executeUpdate();
                
                JOptionPane.showMessageDialog(this, "Producto actualizado");
                loadProducts();
            } catch (SQLException | NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }
    
    private void deleteProduct() {
        int selectedRow = tableProducts.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un producto");
            return;
        }
        
        int id = (int) modelProducts.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, 
            "¿Eliminar este producto?", 
            "Confirmar", 
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                String sql = "DELETE FROM productos WHERE id=?";
                PreparedStatement pst = conn.prepareStatement(sql);
                pst.setInt(1, id);
                pst.executeUpdate();
                
                JOptionPane.showMessageDialog(this, "Producto eliminado");
                loadProducts();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }
    
    private void addUser() {
        // Similar al addProduct
        JOptionPane.showMessageDialog(this, "Función de agregar usuario (implementar)");
    }
    
    private void deleteUser() {
        // Similar al deleteProduct
        JOptionPane.showMessageDialog(this, "Función de eliminar usuario (implementar)");
    }
    
    private void logout() {
        dispose();
        new login().setVisible(true);
    }
}


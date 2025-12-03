package vista;


import javax.swing.*;
import javax.swing.table.*;
import database.conexion;
import modelo.Usuario;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.sql.Date;

public class vendedorframe extends JFrame {
    private Usuario currentUser;
    private JTable tablaRecursos;
    private DefaultTableModel modelRecursos ;
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
        leftPanel.setBorder(BorderFactory.createTitledBorder("Recursos Disponibles"));
        
        modelRecursos = new DefaultTableModel(
            new String[]{"ID", "Recurso", "Tarifa/Hora", "Estado"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaRecursos = new JTable(modelRecursos);
        JScrollPane scrollProducts = new JScrollPane(tablaRecursos);
        
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
            new String[]{"Recurso", "Tarifa/Hora", "Horas", "Subtotal"}, 0
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
        modelRecursos.setRowCount(0);
        try (Connection conn = conexion.getConnection()) {
            String sql = "SELECT * FROM RECURSOS WHERE Estado = 'DISPONIBLE'";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                modelRecursos.addRow(new Object[]{
                    rs.getString("IDRecurso"),
                    rs.getString("Recurso"),
                    String.format("$%.2f", rs.getDouble("TarifaPorHora")),
                    rs.getString("Estado")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void addToCart() {
        int selectedRow = tablaRecursos.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un Recurso");
            return;
        }
        
        String id = modelRecursos.getValueAt(selectedRow, 0).toString();
        String nombre = (String) modelRecursos.getValueAt(selectedRow, 1);
        String tarifaStr = modelRecursos.getValueAt(selectedRow, 2).toString();

    // Normalizar el string de tarifa: "S/ 12.34" -> "12.34"
    tarifaStr = tarifaStr.replace("S/", "").replace("$", "").trim();
    double tarifa = Double.parseDouble(tarifaStr);

    String horasStr  = JOptionPane.showInputDialog(this, "Cuántas Horas:", "1");
    if (horasStr  == null || horasStr.isEmpty()) return;

    int horas = Integer.parseInt(horasStr);

    // Verificar si ya está en el carrito (usar equals para strings)
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
                item.horas + "hrs",
                String.format("S/ %.2f", subtotal)
            });
        }
        
        lblTotal.setText(String.format("Total: S/ %.2f", total));
    }
    private String generarNuevoIDAlquiler(Connection conn) throws SQLException {
    String prefijo = "A";  // Prefijo para alquileres
    int siguienteNumero = 1;
    
    // Obtener el último IDAlquiler existente
    String sql = "SELECT TOP 1 IDAlquiler FROM ALQUILER WHERE IDAlquiler LIKE ? ORDER BY IDAlquiler DESC";
    PreparedStatement pst = conn.prepareStatement(sql);
    pst.setString(1, prefijo + "%");
    ResultSet rs = pst.executeQuery();
    
    if (rs.next()) {
        String ultimoID = rs.getString("IDAlquiler");
        
        // Extraer número: "A005" → "005" → 5
        String numeroStr = ultimoID.substring(prefijo.length());
        int numero = Integer.parseInt(numeroStr);
        siguienteNumero = numero + 1;
    }
    
    // Formatear con ceros: 6 → "006", 12 → "012"
    return String.format("%s%03d", prefijo, siguienteNumero);
}
    
    private void processRental() {
        if (cart.isEmpty()) {
        JOptionPane.showMessageDialog(this, "El carrito está vacío");
        return;
    }

    String dni = JOptionPane.showInputDialog(this, "DNI del Turista:");
    if (dni == null || dni.isEmpty()) return;

    try (Connection conn = conexion.getConnection()) {
        conn.setAutoCommit(false);

        // Buscar turista por DNI (ajusta el nombre de la tabla si es necesario)
        String sqlTurista = "SELECT IDTurista FROM TURISTAA WHERE DNI = ?";
        PreparedStatement pstTurista = conn.prepareStatement(sqlTurista);
        pstTurista.setString(1, dni);
        ResultSet rsTurista = pstTurista.executeQuery();

        if (!rsTurista.next()) {
            JOptionPane.showMessageDialog(this, "Turista no encontrado. Debe estar registrado primero.");
            conn.rollback();
            return;
        }

        int idTurista = rsTurista.getInt("IDTurista");

        // Calcular duración promedio
        int duracionTotal = 0;
        for (CartItem item : cart) {
            duracionTotal += item.horas;
        }
        int duracionPromedio = duracionTotal / cart.size();

        // Generar IDAlquiler (si usas IDs tipo "A001")
        String idAlquiler = generarNuevoIDAlquiler(conn); 

        // Insertar alquiler incluyendo IDAlquiler
        String sqlAlquiler = "INSERT INTO Alquiler (IDAlquiler, FechaDeInicio, HoraDeInicio, Duracion) VALUES (?, ?, ?, ?)";
        PreparedStatement pstAlquiler = conn.prepareStatement(sqlAlquiler);
        pstAlquiler.setString(1, idAlquiler);
        pstAlquiler.setDate(2, Date.valueOf(LocalDate.now()));
        pstAlquiler.setTime(3, Time.valueOf(LocalTime.now()));
        pstAlquiler.setInt(4, duracionPromedio);
        pstAlquiler.executeUpdate();

        // Insertar detalles y actualizar estado de recursos
        for (CartItem item : cart) {
            String sqlDetalle = "INSERT INTO DETALLEALQUILER (IDRecurso, IDTurista, IDAlquiler, IDPromocion) VALUES (?, ?, ?, NULL)";
            PreparedStatement pstDetalle = conn.prepareStatement(sqlDetalle);
            pstDetalle.setString(1, item.idRecurso);
            pstDetalle.setInt(2, idTurista);
            pstDetalle.setString(3, idAlquiler);
            pstDetalle.executeUpdate();

            // Actualizar estado del recurso (usar columna IDRecurso)
            String sqlEstado = "UPDATE RECURSOS SET Estado = 'alquilado' WHERE IDRecurso = ?";
            PreparedStatement pstEstado = conn.prepareStatement(sqlEstado);
            pstEstado.setString(1, item.idRecurso);
            pstEstado.executeUpdate();
        }

        conn.commit();

        double total = 0;
        for (CartItem item : cart) total += item.tarifaPorHora * item.horas;

        JOptionPane.showMessageDialog(this,
            String.format("Alquiler procesado exitosamente\nTotal: S/ %.2f\nAlquiler ID: %s", total, idAlquiler),
            "Éxito",
            JOptionPane.INFORMATION_MESSAGE);
            
            clearCart();

    
            
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

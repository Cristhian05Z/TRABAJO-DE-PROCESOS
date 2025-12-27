package vista;
import javax.swing.*;
import java.awt.*;



public class imagenesdialog  extends JDialog {

    public imagenesdialog(
            JFrame parent,
            String nombre,
            double tarifa,
            String rutaImagen,
            Runnable onAddToCart
    ) {
        super(parent, "Detalle del Recurso", true);
        setSize(420, 420);
        setLayout(new BorderLayout(10, 10));
        setLocationRelativeTo(parent);

        // ===== TÍTULO =====
        JLabel lblTitulo = new JLabel(nombre, JLabel.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        add(lblTitulo, BorderLayout.NORTH);

        // ===== IMAGEN =====
        ImageIcon icon = new ImageIcon(
            getClass().getResource(rutaImagen)
        );

        Image img = icon.getImage().getScaledInstance(
            350, 220, Image.SCALE_SMOOTH
        );

        JLabel lblImagen = new JLabel(new ImageIcon(img));
        lblImagen.setHorizontalAlignment(JLabel.CENTER);
        add(lblImagen, BorderLayout.CENTER);

        // ===== PANEL INFERIOR =====
        JPanel panelBottom = new JPanel(new GridLayout(3, 1, 5, 5));

        JLabel lblTarifa = new JLabel(
            "Tarifa por hora: S/ " + String.format("%.2f", tarifa),
            JLabel.CENTER
        );
        lblTarifa.setFont(new Font("Segoe UI", Font.PLAIN, 15));

        JButton btnAgregar = new JButton("Agregar al carrito");
        btnAgregar.setFont(new Font("Segoe UI", Font.BOLD, 14));

        btnAgregar.addActionListener(e -> {
            onAddToCart.run();
            dispose();
        });

        panelBottom.add(lblTarifa);
        panelBottom.add(new JLabel()); // espacio
        panelBottom.add(btnAgregar);

        add(panelBottom, BorderLayout.SOUTH);
    }


}

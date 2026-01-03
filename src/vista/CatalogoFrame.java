package vista;

import javax.swing.*;
import java.awt.*;

public class CatalogoFrame extends JFrame {

    private static final Color BG_DARK = new Color(15, 23, 42);
    private static final Color BG_CARD = new Color(30, 41, 59);
    private static final Color TEXT_PRIMARY = new Color(248, 250, 252);
    private static final Color TEXT_SECONDARY = new Color(148, 163, 184);
    private static final Color BORDER_COLOR = new Color(51, 65, 85);

    private final String[][] recursos = {
        {"cuatrimotoestandar", "Cuatrimoto Estándar"},
        {"cuatrimotodeportiva", "Cuatrimoto Deportiva"},
        {"motoacuatica", "Moto Acuática"},
        {"biciletaelectrica", "Bicicleta Eléctrica"},
        {"kitsurf", "Kitsurf"},
        {"tabladesurf", "Tabla de Surf"},
        {"bicicletadearena", "Bicicleta de Arena"},
        {"motocross", "Motocross de Arena"},
        {"buggy", "Buggy de Playa"},
        {"kayak", "Kayak"}
    };

    public CatalogoFrame() {
        setTitle("📸 Catálogo de Recursos");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(BG_DARK);

        add(createContent());

        setVisible(true);
    }

    private JPanel createContent() {
        JPanel main = new JPanel(new BorderLayout(20, 20));
        main.setBackground(BG_DARK);
        main.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("📦 Catálogo de Recursos");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(TEXT_PRIMARY);

        JPanel grid = new JPanel(new GridLayout(0, 3, 20, 20));
        grid.setBackground(BG_DARK);

        for (String[] r : recursos) {
            grid.add(createCard(r[0], r[1]));
        }

        JScrollPane scroll = new JScrollPane(grid);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG_DARK);

        main.add(title, BorderLayout.NORTH);
        main.add(scroll, BorderLayout.CENTER);

        return main;
    }

    private JPanel createCard(String imgName, String displayName) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        ImageIcon icon = new ImageIcon(
        getClass().getResource("/Imagen/" + imgName + ".jpg")
        );

        Image img = icon.getImage().getScaledInstance(200, 140, Image.SCALE_SMOOTH);
        JLabel lblImg = new JLabel(new ImageIcon(img));
        lblImg.setHorizontalAlignment(JLabel.CENTER);

        JLabel lblName = new JLabel(displayName);
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblName.setForeground(TEXT_SECONDARY);
        lblName.setHorizontalAlignment(JLabel.CENTER);

        card.add(lblImg, BorderLayout.CENTER);
        card.add(lblName, BorderLayout.SOUTH);

        return card;
    }
}
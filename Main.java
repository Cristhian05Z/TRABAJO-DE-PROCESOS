import javax.swing.*;
import java.sql.*;

public class Main {
    
    public static void main(String[] args) {
        // Configurar el Look and Feel del sistema operativo
        try {
            // Intenta usar el Look and Feel del sistema
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            // Alternativas modernas:
            // UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel"); // Nimbus
            // UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel"); // Windows
            
        } catch (Exception e) {
            System.err.println("No se pudo configurar el Look and Feel: " + e.getMessage());
        }
        
        // Verificar conexión a la base de datos antes de iniciar
        if (!verificarConexionBD()) {
            mostrarErrorConexion();
            return;
        }
        
        // Iniciar la aplicación en el Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                // Crear y mostrar la ventana de login
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
                
                // Mensaje de bienvenida en consola
                System.out.println("===========================================");
                System.out.println("   BEACH RENTAL SYSTEM - INICIADO");
                System.out.println("===========================================");
                System.out.println("Sistema de alquiler de equipos de playa");
                System.out.println("Versión: 1.0");
                System.out.println("===========================================\n");
                
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                    "Error al iniciar la aplicación:\n" + e.getMessage(),
                    "Error Fatal",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }
    
    /**
     * Verifica que la conexión a la base de datos esté disponible
     * @return true si la conexión es exitosa, false en caso contrario
     */
    private static boolean verificarConexionBD() {
        try {
            // Intentar establecer conexión
            Connection conn = DatabaseConnection.getConnection();
            
            if (conn != null && !conn.isClosed()) {
                System.out.println("✓ Conexión a la base de datos establecida correctamente");
                
                // Verificar que las tablas existan
                DatabaseMetaData metaData = conn.getMetaData();
                ResultSet tables = metaData.getTables(null, null, "usuario", null);
                
                if (tables.next()) {
                    System.out.println("✓ Tablas de base de datos verificadas");
                } else {
                    System.err.println("⚠ Advertencia: No se encontraron las tablas necesarias");
                    System.err.println("  Por favor ejecuta el script SQL de inicialización");
                }
                
                conn.close();
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("✗ Error de conexión a la base de datos:");
            System.err.println("  " + e.getMessage());
            return false;
        }
        
        return false;
    }
    
    /**
     * Muestra un diálogo de error cuando no se puede conectar a la BD
     */
    private static void mostrarErrorConexion() {
        String mensaje = "No se pudo conectar a la base de datos.\n\n" +
                        "Verifica lo siguiente:\n" +
                        "1. SQL Server está ejecutándose\n" +
                        "2. La base de datos 'beach_rental' existe\n" +
                        "3. Las credenciales en DatabaseConnection.java son correctas\n" +
                        "4. El driver JDBC está en la carpeta lib/\n\n" +
                        "Consulta la consola para más detalles.";
        
        JOptionPane.showMessageDialog(null,
            mensaje,
            "Error de Conexión",
            JOptionPane.ERROR_MESSAGE);
    }
}
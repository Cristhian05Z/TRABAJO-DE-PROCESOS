package DAO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import modelo.Turista;

public class TuristaDAO{

public Turista obtenerPorId(int id) throws SQLException {
        String sql = "SELECT * FROM TURISTAA WHERE IDTurista = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                return mapearTurista(rs);
            }
        }
        return null;
    }
    
    // Obtener turista por DNI
    public Turista obtenerPorDNI(String dni) throws SQLException {
        String sql = "SELECT * FROM TURISTAA WHERE DNI = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, dni);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                return mapearTurista(rs);
            }
        }
        return null;
    }
    
    // Obtener todos los turistas
    public List<Turista> obtenerTodos() throws SQLException {
        List<Turista> turistas = new ArrayList<>();
        String sql = "SELECT * FROM TURISTAA ORDER BY Nombre, Apellido";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                turistas.add(mapearTurista(rs));
            }
        }
        return turistas;
    }
    
    // Insertar nuevo turista
    public int insertar(Turista turista) throws SQLException {
        String sql = "INSERT INTO TURISTAA (Nombre, Apellido, DNI, Nacionalidad, Telefono, Email) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pst.setString(1, turista.getNombre());
            pst.setString(2, turista.getApellido());
            pst.setString(3, turista.getDNI());
            pst.setString(4, turista.getNacionalidad());
            pst.setString(5, turista.getTelefono());
            pst.setString(6, turista.getEmail());
            
            int filasAfectadas = pst.executeUpdate();
            
            if (filasAfectadas > 0) {
                ResultSet rs = pst.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }
    
    // Actualizar turista
    public boolean actualizar(Turista turista) throws SQLException {
        String sql = "UPDATE TURISTAA SET Nombre = ?, Apellido = ?, DNI = ?, " +
                     "Nacionalidad = ?, Telefono = ?, Email = ? WHERE IDTurista = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setString(1, turista.getNombre());
            pst.setString(2, turista.getApellido());
            pst.setString(3, turista.getDNI());
            pst.setString(4, turista.getNacionalidad());
            pst.setString(5, turista.getTelefono());
            pst.setString(6, turista.getEmail());
            pst.setInt(7, turista.getIDTurista());
            
            return pst.executeUpdate() > 0;
        }
    }
    
    // Eliminar turista
    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM TURISTAA WHERE IDTurista = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setInt(1, id);
            return pst.executeUpdate() > 0;
        }
    }
    
    // Buscar turistas por nombre
    public List<Turista> buscarPorNombre(String nombre) throws SQLException {
        List<Turista> turistas = new ArrayList<>();
        String sql = "SELECT * FROM TURISTAA WHERE Nombre LIKE ? OR Apellido LIKE ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            String patron = "%" + nombre + "%";
            pst.setString(1, patron);
            pst.setString(2, patron);
            
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                turistas.add(mapearTurista(rs));
            }
        }
        return turistas;
    }
    
    // Mapear ResultSet a Turista
    private Turista mapearTurista(ResultSet rs) throws SQLException {
        Turista turista = new Turista();
        turista.setIDTurista(rs.getInt("IDTurista"));
        turista.setNombre(rs.getString("Nombre"));
        turista.setApellido(rs.getString("Apellido"));
        turista.setDNI(rs.getString("DNI"));
        turista.setNacionalidad(rs.getString("Nacionalidad"));
        turista.setTelefono(rs.getString("Telefono"));
        turista.setEmail(rs.getString("Email"));
        return turista;
    }

    
}
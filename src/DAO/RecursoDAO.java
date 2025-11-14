package DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import database.conexion;
import modelo.Recurso;

public class RecursoDAO {
    public Recurso obtenerPorId(String id) throws SQLException {
        String sql = "SELECT * FROM RECURSOS WHERE IDRecursos = ?";
        try (Connection conn = conexion.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, id);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                return mapearRecurso(rs);
            }
        }
        return null;
    }
    
    // Obtener todos los recursos
    public List<Recurso> obtenerTodos() throws SQLException {
        List<Recurso> recursos = new ArrayList<>();
        String sql = "SELECT * FROM RECURSOS ORDER BY Recurso";
        
        try (Connection conn = conexion.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                recursos.add(mapearRecurso(rs));
            }
        }
        return recursos;
    }
    
    // Obtener recursos disponibles
    public List<Recurso> obtenerDisponibles() throws SQLException {
        List<Recurso> recursos = new ArrayList<>();
        String sql = "SELECT * FROM RECURSOS WHERE Estado = 'disponible' ORDER BY Recurso";
        
        try (Connection conn = conexion.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                recursos.add(mapearRecurso(rs));
            }
        }
        return recursos;
    }
    
    // Insertar nuevo recurso
    public int insertar(Recurso recurso) throws SQLException {
        String sql = "INSERT INTO RECURSOS (Recurso, Descripcion, TarifaPorHora, Estado) " +
                     "VALUES (?, ?, ?, ?)";
        
        try (Connection conn = conexion.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pst.setString(1, recurso.getRecurso());
            pst.setString(2, recurso.getDescripcion());
            pst.setDouble(3, recurso.getTarifaPorHora());
            pst.setString(4, recurso.getEstado());
            
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
    
    // Actualizar recurso
    public boolean actualizar(Recurso recurso) throws SQLException {
        String sql = "UPDATE RECURSOS SET Recurso = ?, Descripcion = ?, " +
                     "TarifaPorHora = ?, Estado = ? WHERE IDRecursos = ?";
        
        try (Connection conn = conexion.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setString(1, recurso.getRecurso());
            pst.setString(2, recurso.getDescripcion());
            pst.setDouble(3, recurso.getTarifaPorHora());
            pst.setString(4, recurso.getEstado());
            pst.setString(5, recurso.getIDRecursos());
            
            return pst.executeUpdate() > 0;
        }
    }
    
    // Actualizar solo el estado
    public boolean actualizarEstado(int id, String nuevoEstado) throws SQLException {
        String sql = "UPDATE RECURSOS SET Estado = ? WHERE IDRecursos = ?";
        
        try (Connection conn = conexion.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setString(1, nuevoEstado);
            pst.setInt(2, id);
            
            return pst.executeUpdate() > 0;
        }
    }
    
    // Eliminar recurso
    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM RECURSOS WHERE IDRecursos = ?";
        
        try (Connection conn = conexion.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setInt(1, id);
            return pst.executeUpdate() > 0;
        }
    }
    
    // Contar recursos por estado
    public int contarPorEstado(String estado) throws SQLException {
        String sql = "SELECT COUNT(*) as total FROM RECURSOS WHERE Estado = ?";
        
        try (Connection conn = conexion.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setString(1, estado);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("total");
            }
        }
        return 0;
    }
    
    // Mapear ResultSet a Recurso
    private Recurso mapearRecurso(ResultSet rs) throws SQLException {
        Recurso recurso = new Recurso();
        recurso.setIDRecursos(rs.getString("IDRecursos"));
        recurso.setRecurso(rs.getString("Recurso"));
        recurso.setDescripcion(rs.getString("Descripcion"));
        recurso.setTarifaPorHora(rs.getDouble("TarifaPorHora"));
        recurso.setEstado(rs.getString("Estado"));
        return recurso;
    }
}

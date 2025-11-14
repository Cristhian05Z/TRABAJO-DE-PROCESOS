package DAO;


import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import database.conexion;
import modelo.Alquiler;


public class AlquilerDAO {
    
    // Obtener alquiler por ID
    public Alquiler obtenerPorId(int id) throws SQLException {
        String sql = "SELECT * FROM Alquiler WHERE IDAlquiler = ?";
        try (Connection conn = conexion.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                Alquiler alquiler = mapearAlquiler(rs);
                // Cargar detalles
                DetalleAlquilerDAO detalleDAO = new DetalleAlquilerDAO();
                alquiler.setDetalles(detalleDAO.obtenerPorAlquiler(id));
                return alquiler;
            }
        }
        return null;
    }
    
    // Obtener todos los alquileres
    public List<Alquiler> obtenerTodos() throws SQLException {
        List<Alquiler> alquileres = new ArrayList<>();
        String sql = "SELECT * FROM Alquiler ORDER BY FechaDeInicio DESC, HoraDeInicio DESC";
        
        try (Connection conn = conexion.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                alquileres.add(mapearAlquiler(rs));
            }
        }
        return alquileres;
    }
    
    // Obtener alquileres por fecha
    public List<Alquiler> obtenerPorFecha(LocalDate fecha) throws SQLException {
        List<Alquiler> alquileres = new ArrayList<>();
        String sql = "SELECT * FROM Alquiler WHERE FechaDeInicio = ? ORDER BY HoraDeInicio";
        
        try (Connection conn = conexion.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setDate(1, Date.valueOf(fecha));
            ResultSet rs = pst.executeQuery();
            
            while (rs.next()) {
                alquileres.add(mapearAlquiler(rs));
            }
        }
        return alquileres;
    }
    
    // Crear nuevo alquiler
    public int crear(Alquiler alquiler) throws SQLException {
        String sql = "INSERT INTO Alquiler (FechaDeInicio, HoraDeInicio, Duracion) VALUES (?, ?, ?)";
        
        try (Connection conn = conexion.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pst.setDate(1, Date.valueOf(alquiler.getFechaDeInicio()));
            pst.setTime(2, Time.valueOf(alquiler.getHoraDeInicio()));
            pst.setInt(3, alquiler.getDuracion());
            
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
    
    // Actualizar alquiler
    public boolean actualizar(Alquiler alquiler) throws SQLException {
        String sql = "UPDATE Alquiler SET FechaDeInicio = ?, HoraDeInicio = ?, Duracion = ? " +
                     "WHERE IDAlquiler = ?";
        
        try (Connection conn = conexion.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setDate(1, Date.valueOf(alquiler.getFechaDeInicio()));
            pst.setTime(2, Time.valueOf(alquiler.getHoraDeInicio()));
            pst.setInt(3, alquiler.getDuracion());
            pst.setInt(4, alquiler.getIDAlquiler());
            
            return pst.executeUpdate() > 0;
        }
    }
    
    // Eliminar alquiler
    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM Alquiler WHERE IDAlquiler = ?";
        
        try (Connection conn = conexion.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setInt(1, id);
            return pst.executeUpdate() > 0;
        }
    }
    
    // Contar alquileres del día
    public int contarDelDia() throws SQLException {
        String sql = "SELECT COUNT(*) as total FROM Alquiler WHERE FechaDeInicio = CONVERT(date, GETDATE())";
        
        try (Connection conn = conexion.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt("total");
            }
        }
        return 0;
    }
    
    // Mapear ResultSet a Alquiler
    private Alquiler mapearAlquiler(ResultSet rs) throws SQLException {
        Alquiler alquiler = new Alquiler();
        alquiler.setIDAlquiler(rs.getInt("IDAlquiler"));
        alquiler.setFechaDeInicio(rs.getDate("FechaDeInicio").toLocalDate());
        alquiler.setHoraDeInicio(rs.getTime("HoraDeInicio").toLocalTime());
        alquiler.setDuracion(rs.getInt("Duracion"));
        return alquiler;
    }
}

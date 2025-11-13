package DAO;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import modelo.Promocion;


public class PromocionDAO {
        public Promocion obtenerPorId(int id) throws SQLException {
        String sql = "SELECT * FROM PROMOCION WHERE IDPromocion = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                return mapearPromocion(rs);
            }
        }
        return null;
    }
    
    // Obtener todas las promociones
    public List<Promocion> obtenerTodas() throws SQLException {
        List<Promocion> promociones = new ArrayList<>();
        String sql = "SELECT * FROM PROMOCION ORDER BY PorcentajeDescuento DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                promociones.add(mapearPromocion(rs));
            }
        }
        return promociones;
    }
    
    // Insertar nueva promoción
    public int insertar(Promocion promocion) throws SQLException {
        String sql = "INSERT INTO PROMOCION (PorcentajeDescuento, Condiciones) VALUES (?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pst.setDouble(1, promocion.getPorcentajeDescuento());
            pst.setString(2, promocion.getCondiciones());
            
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
    
    // Actualizar promoción
    public boolean actualizar(Promocion promocion) throws SQLException {
        String sql = "UPDATE PROMOCION SET PorcentajeDescuento = ?, Condiciones = ? " +
                     "WHERE IDPromocion = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setDouble(1, promocion.getPorcentajeDescuento());
            pst.setString(2, promocion.getCondiciones());
            pst.setInt(3, promocion.getIdPromocion());
            
            return pst.executeUpdate() > 0;
        }
    }
    
    // Eliminar promoción
    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM PROMOCION WHERE IDPromocion = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setInt(1, id);
            return pst.executeUpdate() > 0;
        }
    }
    
    // Mapear ResultSet a Promocion
    private Promocion mapearPromocion(ResultSet rs) throws SQLException {
        Promocion promocion = new Promocion();
        promocion.setIdPromocion(rs.getInt("IDPromocion"));
        promocion.setPorcentajeDescuento(rs.getDouble("PorcentajeDescuento"));
        promocion.setCondiciones(rs.getString("Condiciones"));
        return promocion;
    }
}

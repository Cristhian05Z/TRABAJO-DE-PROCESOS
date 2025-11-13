package DAO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import modelo.DetalleAlquiler;

public class DetalleAlquilerDAO {
    // Obtener detalle por ID
    public DetalleAlquiler obtenerPorId(int id) throws SQLException {
        String sql = "SELECT * FROM DETALLEALQUILER WHERE IDDetalleAlquiler = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                return mapearDetalle(rs);
            }
        }
        return null;
    }
    
    // Obtener detalles de un alquiler
    public List<DetalleAlquiler> obtenerPorAlquiler(int idAlquiler) throws SQLException {
        List<DetalleAlquiler> detalles = new ArrayList<>();
        String sql = "SELECT * FROM DETALLEALQUILER WHERE IDAlquiler = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, idAlquiler);
            ResultSet rs = pst.executeQuery();
            
            RecursoDAO recursoDAO = new RecursoDAO();
            TuristaDAO turistaDAO = new TuristaDAO();
            PromocionDAO promocionDAO = new PromocionDAO();
            
            while (rs.next()) {
                DetalleAlquiler detalle = mapearDetalle(rs);
                
                // Cargar objetos relacionados
                detalle.setRecursoObj(recursoDAO.obtenerPorId(detalle.getIDRecurso()));
                detalle.setTuristaObj(turistaDAO.obtenerPorId(detalle.getIDTurista()));
                
                if (detalle.getIDPromocion() != null) {
                    detalle.setPromocionObj(promocionDAO.obtenerPorId(detalle.getIDPromocion()));
                }
                
                detalles.add(detalle);
            }
        }
        return detalles;
    }
    
    // Insertar nuevo detalle
    public int insertar(DetalleAlquiler detalle) throws SQLException {
        String sql = "INSERT INTO DETALLEALQUILER (IDRecurso, IDTurista, IDAlquiler, IDPromocion) " +
                     "VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pst.setInt(1, detalle.getIDRecurso());
            pst.setInt(2, detalle.getIDTurista());
            pst.setInt(3, detalle.getIDAlquiler());
            
            if (detalle.getIDPromocion() != null) {
                pst.setInt(4, detalle.getIDPromocion());
            } else {
                pst.setNull(4, Types.INTEGER);
            }
            
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
    
    // Actualizar detalle
    public boolean actualizar(DetalleAlquiler detalle) throws SQLException {
        String sql = "UPDATE DETALLEALQUILER SET IDRecurso = ?, IDTurista = ?, " +
                     "IDPromocion = ? WHERE IDDetalleAlquiler = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setInt(1, detalle.getIDRecurso());
            pst.setInt(2, detalle.getIDTurista());
            
            if (detalle.getIDPromocion() != null) {
                pst.setInt(3, detalle.getIDPromocion());
            } else {
                pst.setNull(3, Types.INTEGER);
            }
            
            pst.setInt(4, detalle.getIDDetalleAlquiler());
            
            return pst.executeUpdate() > 0;
        }
    }
    
    // Eliminar detalle
    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM DETALLEALQUILER WHERE IDDetalleAlquiler = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setInt(1, id);
            return pst.executeUpdate() > 0;
        }
    }
    private DetalleAlquiler mapearDetalle(ResultSet rs) throws SQLException{
        DetalleAlquiler detalleAlquiler = new DetalleAlquiler();
        detalleAlquiler.setIDDetalleAlquiler(rs.getInt("IDDetalleAlquiler"));
        detalleAlquiler.setIDRecurso(rs.getInt("IDRecurso"));
        detalleAlquiler.setIDTurista(rs.getInt("IDTurista"));
        detalleAlquiler.setIDAlquiler(rs.getInt("IDAlquiler"));
        detalleAlquiler.setIDPromocion(rs.getInt("IDPromocion"));
        return detalleAlquiler;
    }

    
}

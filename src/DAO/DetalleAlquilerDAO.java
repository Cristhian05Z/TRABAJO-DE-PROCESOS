package DAO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import database.conexion;
import modelo.DetalleAlquiler;

public class DetalleAlquilerDAO {
    // Obtener detalle por ID
    public DetalleAlquiler obtenerPorId(String id) throws SQLException {
        String sql = "SELECT * FROM DETALLEALQUILER WHERE IDDetalleAlquiler = ?";
        try (Connection conn = conexion.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, id);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                return mapearDetalle(rs);
            }
        }
        return null;
    }
    
    // Obtener detalles de un alquiler
    public List<DetalleAlquiler> obtenerPorAlquiler(String idAlquiler) throws SQLException {
        List<DetalleAlquiler> detalles = new ArrayList<>();
        String sql = "SELECT * FROM DETALLEALQUILER WHERE IDAlquiler = ?";
        
        try (Connection conn = conexion.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, idAlquiler);
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
        
        try (Connection conn = conexion.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pst.setString(1, detalle.getIDRecurso());
            pst.setString(2, detalle.getIDTurista());
            pst.setString(3, detalle.getIDAlquiler());
            
            if (detalle.getIDPromocion() != null) {
                pst.setString(4, detalle.getIDPromocion());
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
        
        try (Connection conn = conexion.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setString(1, detalle.getIDRecurso());
            pst.setString(2, detalle.getIDTurista());
            
            if (detalle.getIDPromocion() != null) {
                pst.setString(3, detalle.getIDPromocion());
            } else {
                pst.setNull(3, Types.INTEGER);
            }
            
            pst.setString(4, detalle.getIDDetalleAlquiler());
            
            return pst.executeUpdate() > 0;
        }
    }
    
    // Eliminar detalle
    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM DETALLEALQUILER WHERE IDDetalleAlquiler = ?";
        
        try (Connection conn = conexion.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setInt(1, id);
            return pst.executeUpdate() > 0;
        }
    }
     private DetalleAlquiler mapearDetalle(ResultSet rs) throws SQLException {
        DetalleAlquiler detalle = new DetalleAlquiler();
        detalle.setIDDetalleAlquiler(rs.getString("iDDetalleAlquiler"));
        detalle.setIDAlquiler(rs.getString("iDAlquiler"));
        detalle.setIDTurista(rs.getString("iDTurista"));
        detalle.setIDRecurso(rs.getString("iDRecurso"));
        
        String idPromocion = rs.getString("idPromocion");
        if (!rs.wasNull()) {
            detalle.setIDPromocion(idPromocion);
        }
        
        detalle.setFormatodePago(rs.getString("FormatodePago"));
        return detalle;
    }
    public boolean devolverRecurso(String idAlquiler, String idRecurso) {

    String sqlDetalle =
        "DELETE FROM DETALLEALQUILER WHERE IDAlquiler = ? AND IDRecurso = ?";

    String sqlRecurso =
        "UPDATE RECURSOS SET Estado = 'disponible' WHERE IDRecurso = ?";

    try (Connection conn = conexion.getConnection()) {
        conn.setAutoCommit(false);

        PreparedStatement pst1 = conn.prepareStatement(sqlDetalle);
        pst1.setString(1, idAlquiler);
        pst1.setString(2, idRecurso);
        pst1.executeUpdate();

        PreparedStatement pst2 = conn.prepareStatement(sqlRecurso);
        pst2.setString(1, idRecurso);
        pst2.executeUpdate();

        conn.commit();
        return true;

    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}

    
}

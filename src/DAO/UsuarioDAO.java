package DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import database.conexion;
import modelo.Usuario;


public class UsuarioDAO {
    public Usuario login(String nombre, String contrasena) throws SQLException {
        String sql = "SELECT * FROM USUARIO WHERE Nombre = ? AND Contrasena = ?";
        try (Connection conn = conexion.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, nombre);
            pst.setString(2, contrasena);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                return mapearUsuario(rs);
            }
        }
        return null;
    }
    
    // Obtener usuario por ID
    public Usuario obtenerPorId(int id) throws SQLException {
        String sql = "SELECT * FROM USUARIO WHERE IDUsuario = ?";
        try (Connection conn = conexion.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                return mapearUsuario(rs);
            }
        }
        return null;
    }
    
    // Obtener todos los usuarios
    public List<Usuario> obtenerTodos() throws SQLException {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM USUARIO ORDER BY Nombre";
        
        try (Connection conn = conexion.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                usuarios.add(mapearUsuario(rs));
            }
        }
        return usuarios;
    }
    
    // Insertar nuevo usuario
    public int insertar(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO USUARIO (TipoDeUsuario, Nombre, Contrasena) VALUES (?, ?, ?)";
        
        try (Connection conn = conexion.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pst.setString(1, usuario.getTipoDeUsuario());
            pst.setString(2, usuario.getNombre());
            pst.setString(3, usuario.getContraseña());
            
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
    
    // Actualizar usuario
    public boolean actualizar(Usuario usuario) throws SQLException {
        String sql = "UPDATE USUARIO SET TipoDeUsuario = ?, Nombre = ?, Contrasena = ? WHERE IDUsuario = ?";
        
        try (Connection conn = conexion.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setString(1, usuario.getTipoDeUsuario());
            pst.setString(2, usuario.getNombre());
            pst.setString(3, usuario.getContraseña());
            pst.setString(4, usuario.getIDUsuario());
            
            return pst.executeUpdate() > 0;
        }
    }
    
    // Eliminar usuario
    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM USUARIO WHERE IDUsuario = ?";
        
        try (Connection conn = conexion.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setInt(1, id);
            return pst.executeUpdate() > 0;
        }
    }
    
    // Mapear ResultSet a Usuario
    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setIDUsuario(rs.getString("IDUsuario"));
        usuario.setTipoDeUsuario(rs.getString("TipoDeUsuario"));
        usuario.setNombre(rs.getString("Nombre"));
        usuario.setContraseña(rs.getString("Contraseña"));
        return usuario;
    }
}
    


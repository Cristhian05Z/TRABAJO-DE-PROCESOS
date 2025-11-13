package database;

import java.sql.*;
public class conexion {
    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=Alquiler de Playa;encrypt=true;trustServerCertificate=true";
    private static final String USER = "sa"; 
    private static final String PASSWORD = "13142485";
     public static Connection getConnection() throws SQLException {
        try {
            // Cargar el driver de SQL Server
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver de SQL Server no encontrado", e);
        }
    }

}
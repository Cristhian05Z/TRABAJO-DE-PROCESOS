package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class conexion {

    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=Alquiler De Playa;encrypt=false;";
    private static final String USER = "sa"; // o el usuario que uses
    private static final String PASS = "13142485";

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (SQLException e) {
            System.out.println("❌ Error al conectar a SQL Server: " + e.getMessage());
            return null;
        }
    }
}

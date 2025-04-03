package org.hugo.dbconnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {

    // Datos de conexión a la base de datos
    private static final String URL = "jdbc:mysql://localhost:3306/pokedb";
    private static final String USER = "profesor";
    private static final String PASS = "12345678";

    // Método para obtener una conexión
    public static Connection getConnection() throws SQLException {
        // Crear una nueva conexión cada vez que se solicite
        return DriverManager.getConnection(URL, USER, PASS);
    }

    // Método para cerrar una conexión
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }
    }
}

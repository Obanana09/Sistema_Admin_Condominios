/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author bjsmg
 */
public class ConexionDB {
    private static Connection conexion = null;
    // 
    private static final String URL = "jdbc:sqlite:condominio_vistaverde.db";

    // Constructor privado para evitar que creen instancias con 'new'
    private ConexionDB() {}

    /**
     * Método estático para obtener la conexión
     * @return Connection objeto de conexión a SQLite
     */
    public static Connection getConexion() {
        try {
            // Si la conexión no existe o se cerró, la creamos
            if (conexion == null || conexion.isClosed()) {
                // Esto busca el driver de SQLite 
                conexion = DriverManager.getConnection(URL);
                System.out.println("LOG: Conexión establecida con SQLite.");
            }
        } catch (SQLException e) {
            System.err.println("ERROR: No se pudo conectar a la base de datos: " + e.getMessage());
        }
        return conexion;
    }
}

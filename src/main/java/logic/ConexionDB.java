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
    
    private static ConexionDB instancia;
    
    private Connection Conexion;
    
    private final String URL = "jdbc:sqlite:condominio.db";
    
    private ConexionDB() {
        
        try {
            Conexion = DriverManager.getConnection(URL);
            System.out.println("C onexión a la DB exitosa");
            
        } catch(SQLException e) {
            System.out.println("Error de conexión");
        }
    }
    
    public static ConexionDB getInstancia() {
        
        if(instancia == null) {
            instancia = new ConexionDB();
            
        }
        return instancia;
    }
    
    public Connection getConexion() {
        return Conexion;
    }
    
}

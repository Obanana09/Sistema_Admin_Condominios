package com.mycompany.registropropietario;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {

    private static final String URL = "jdbc:sqlite:BD/condominio_vistaverde.db";

    public static Connection getConexion() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}

package com.mycompany.registropropietario;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase ConexionDB.
 *
 * Esta clase se encarga de administrar la conexión
 * entre la aplicación y la base de datos SQLite.
 *
 * Su función principal es proporcionar una conexión
 * lista para ser utilizada por los distintos módulos
 * del sistema, como propietarios, casas, pagos y morosidad.
 *
 * La base de datos utilizada se encuentra en:
 * BD/condominio_vistaverde.db
 *
 * Al centralizar la conexión en una sola clase,
 * se facilita el mantenimiento y la reutilización
 * del código en todo el proyecto.
 *
 * @author Brian Sanchez
 * @version 1.0
 * @since 2026
 */
public class ConexionDB {

    /**
     * Ruta de la base de datos SQLite.
     *
     * Esta constante almacena la ubicación
     * del archivo donde se guardan los datos
     * del sistema.
     */
    private static final String URL = "jdbc:sqlite:BD/condominio_vistaverde.db";

    /**
     * Obtiene una conexión activa con la base de datos.
     *
     * Este método utiliza DriverManager para establecer
     * la comunicación con el archivo SQLite indicado
     * en la constante URL.
     *
     * @return Objeto Connection que permite realizar
     *         consultas, inserciones, modificaciones
     *         y eliminaciones en la base de datos.
     *
     * @throws SQLException Se produce cuando ocurre
     *         un error durante la conexión.
     */
    public static Connection getConexion() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}

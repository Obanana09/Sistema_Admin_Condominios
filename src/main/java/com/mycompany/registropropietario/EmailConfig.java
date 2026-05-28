package com.mycompany.registropropietario;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Configuracion SMTP para el envio de correos del condominio.
 *
 * Las credenciales NO estan en el codigo: se leen de "email.properties" en el
 * classpath (src/main/resources/email.properties). Ese archivo esta ignorado
 * por git. Para configurar en una maquina nueva, copia "email.properties.example"
 * a "email.properties" y rellena tus credenciales reales de Brevo.
 */
public class EmailConfig {

    private static final Properties PROPS = cargar();

    public static final String HOST = PROPS.getProperty("email.host");
    public static final String PUERTO = PROPS.getProperty("email.puerto");
    public static final String SMTP_USUARIO = PROPS.getProperty("email.smtp.usuario");
    public static final String SMTP_CLAVE = PROPS.getProperty("email.smtp.clave");
    public static final String REMITENTE = PROPS.getProperty("email.remitente");
    public static final String NOMBRE_REMITENTE = PROPS.getProperty("email.nombre.remitente");

    private static Properties cargar() {
        Properties p = new Properties();
        try (InputStream in = EmailConfig.class.getResourceAsStream("/email.properties")) {
            if (in == null) {
                throw new IllegalStateException(
                        "No se encontro email.properties en el classpath. "
                        + "Copia src/main/resources/email.properties.example a "
                        + "email.properties y rellena tus credenciales.");
            }
            p.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("Error leyendo email.properties", e);
        }
        return p;
    }

    private EmailConfig() {
    }
}

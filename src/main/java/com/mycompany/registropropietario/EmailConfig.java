package com.mycompany.registropropietario;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


/**
 * Clase EmailConfig.
 *
 * Se encarga de cargar y proporcionar la configuración
 * necesaria para el envío de correos electrónicos
 * desde el sistema de administración residencial.
 *
 * La configuración es obtenida desde el archivo
 * email.properties ubicado en los recursos del proyecto.
 *
 * Entre los datos cargados se encuentran:
 * - Servidor SMTP.
 * - Puerto de conexión.
 * - Usuario SMTP.
 * - Contraseña SMTP.
 * - Dirección de correo remitente.
 * - Nombre del remitente.
 *
 * Este diseño permite mantener las credenciales
 * fuera del código fuente, mejorando la seguridad
 * y facilitando la configuración del sistema.
 *
 * @author Brian Sanchez
 * @version 1.0
 * @since 2026
 */
/**
 * Configuracion SMTP para el envio de correos del condominio.
 *
 * Las credenciales NO estan en el codigo: se leen de "email.properties" en el
 * classpath (src/main/resources/email.properties). Ese archivo esta ignorado
 * por git. Para configurar en una maquina nueva, copia "email.properties.example"
 * a "email.properties" y rellena tus credenciales reales de Brevo.
 */
public class EmailConfig {

   /**
 * Contenedor de propiedades cargadas desde
 * el archivo email.properties.
 *
 * Al iniciar la clase se ejecuta automáticamente
 * el método cargar() para recuperar toda la
 * configuración de correo electrónico.
 */
    private static final Properties PROPS = cargar();

   /**
 * Dirección del servidor SMTP.
 */
    public static final String HOST = PROPS.getProperty("email.host");
    
    /**
 * Puerto utilizado para la conexión SMTP.
 */
    public static final String PUERTO = PROPS.getProperty("email.puerto");
    
    /**
 * Usuario autorizado para autenticarse
 * en el servidor SMTP.
 */
    public static final String SMTP_USUARIO = PROPS.getProperty("email.smtp.usuario");
    
    /**
 * Contraseña utilizada para autenticarse
 * en el servidor SMTP.
 */
    public static final String SMTP_CLAVE = PROPS.getProperty("email.smtp.clave");
    
    /**
 * Dirección de correo que aparecerá
 * como remitente de los mensajes.
 */
    public static final String REMITENTE = PROPS.getProperty("email.remitente");
    
    /**
 * Nombre descriptivo mostrado como remitente.
 */
    public static final String NOMBRE_REMITENTE = PROPS.getProperty("email.nombre.remitente");

    /**
 * Carga la configuración de correo electrónico.
 *
 * Busca el archivo email.properties dentro
 * de los recursos del proyecto y carga todos
 * sus valores en un objeto Properties.
 *
 * Si el archivo no existe o presenta errores
 * de lectura, se genera una excepción para
 * impedir que el sistema continúe sin una
 * configuración válida.
 *
 * @return Objeto Properties con la configuración
 *         completa del servicio de correo.
 */
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

    /**
 * Constructor privado.
 *
 * Impide la creación de objetos de esta clase.
 *
 * EmailConfig funciona únicamente como una clase
 * de configuración estática, por lo que no es
 * necesario crear instancias.
 */
    private EmailConfig() {
    }
}

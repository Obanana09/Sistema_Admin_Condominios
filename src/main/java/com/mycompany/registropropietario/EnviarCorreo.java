package com.mycompany.registropropietario;

import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * Envío de correos vía SMTP (Gmail / Google Workspace) usando la configuración
 * de {@link EmailConfig}.
 */
public class EnviarCorreo {

    /** Datos del propietario asociado a una casa. */
    public static class Propietario {
        public final String nombre;
        public final String correo;

        public Propietario(String nombre, String correo) {
            this.nombre = nombre;
            this.correo = correo;
        }
    }

    private EnviarCorreo() {
    }

    /**
     * Envía un correo con cuerpo en texto plano y su versión HTML
     * (multipart/alternative), más cabeceras que mejoran la entregabilidad y
     * reducen la probabilidad de caer en spam. El parámetro {@code cuerpo} es el
     * texto plano; el HTML se genera a partir de él.
     * Propaga la excepción para que el llamador decida cómo manejar el fallo.
     */
    public static void enviar(String destinatario, String asunto, String cuerpo)
            throws MessagingException {

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", EmailConfig.HOST);
        props.put("mail.smtp.port", EmailConfig.PUERTO);

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(
                        EmailConfig.SMTP_USUARIO, EmailConfig.SMTP_CLAVE);
            }
        });

        try {
            MimeMessage mensaje = new MimeMessage(session);
            mensaje.setFrom(new InternetAddress(
                    EmailConfig.REMITENTE, EmailConfig.NOMBRE_REMITENTE));
            mensaje.setReplyTo(new InternetAddress[]{
                    new InternetAddress(EmailConfig.REMITENTE, EmailConfig.NOMBRE_REMITENTE)});
            mensaje.setRecipients(
                    Message.RecipientType.TO, InternetAddress.parse(destinatario));
            mensaje.setSubject(asunto, "UTF-8");

            // Cabeceras que ayudan a los filtros a clasificar el correo como
            // legítimo/transaccional en lugar de spam masivo.
            mensaje.addHeader("List-Unsubscribe",
                    "<mailto:" + EmailConfig.REMITENTE + "?subject=Baja>");
            mensaje.addHeader("Auto-Submitted", "auto-generated");
            mensaje.addHeader("X-Auto-Response-Suppress", "All");

            // multipart/alternative: el cliente muestra HTML si puede, o el texto
            // plano como respaldo.
            MimeBodyPart parteTexto = new MimeBodyPart();
            parteTexto.setText(cuerpo, "UTF-8");

            MimeBodyPart parteHtml = new MimeBodyPart();
            parteHtml.setContent(textoAHtml(cuerpo), "text/html; charset=UTF-8");

            MimeMultipart multipart = new MimeMultipart("alternative");
            multipart.addBodyPart(parteTexto);
            multipart.addBodyPart(parteHtml);
            mensaje.setContent(multipart);

            Transport.send(mensaje);
        } catch (java.io.UnsupportedEncodingException e) {
            throw new MessagingException("Nombre de remitente inválido", e);
        }
    }

    /**
     * Convierte un cuerpo de texto plano en un HTML simple y limpio,
     * escapando caracteres especiales y respetando los saltos de línea.
     */
    private static String textoAHtml(String texto) {
        String escapado = texto
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\n", "<br>");
        return "<!DOCTYPE html><html lang=\"es\"><body style=\"margin:0;padding:0;"
             + "background:#f4f4f4;\">"
             + "<div style=\"max-width:600px;margin:0 auto;padding:24px;"
             + "font-family:Arial,Helvetica,sans-serif;color:#333333;"
             + "background:#ffffff;border:1px solid #e0e0e0;border-radius:8px;\">"
             + "<h2 style=\"color:#2e7d32;margin-top:0;\">"
             + EmailConfig.NOMBRE_REMITENTE + "</h2>"
             + "<p style=\"font-size:15px;line-height:1.5;\">" + escapado + "</p>"
             + "<hr style=\"border:none;border-top:1px solid #e0e0e0;margin:24px 0;\">"
             + "<p style=\"font-size:12px;color:#999999;\">Este es un correo "
             + "automático de notificación. Si tiene dudas, responda a este mensaje.</p>"
             + "</div></body></html>";
    }

    /**
     * Obtiene el nombre y correo del propietario de una casa.
     * Los correos viven en la tabla Propietarios.
     *
     * @return datos del propietario, o {@code null} si la casa no tiene
     *         propietario o no tiene correo registrado.
     */
    public static Propietario propietarioPorCasa(int numeroCasa) {
        String sql = "SELECT p.nombre, p.correo "
                   + "FROM Casas c "
                   + "JOIN Propietarios p ON c.id_propietario = p.id_propietario "
                   + "WHERE c.numero_casa = ?";
        try (java.sql.Connection con = ConexionDB.getConexion();
             java.sql.PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, numeroCasa);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String correo = rs.getString("correo");
                    if (correo != null && !correo.trim().isEmpty()) {
                        return new Propietario(rs.getString("nombre"), correo.trim());
                    }
                }
            }
        } catch (java.sql.SQLException e) {
            return null;
        }
        return null;
    }
}

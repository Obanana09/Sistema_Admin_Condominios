/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.registropropietario;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;
import javax.swing.table.DefaultTableModel;


/**
 * Clase CasasM.
 *
 * Esta ventana muestra el listado de casas con morosidad
 * dentro del sistema de administración residencial.
 *
 * Funciones principales:
 * - Mostrar el período actual consultado.
 * - Consultar las casas registradas en la base de datos.
 * - Mostrar información de propietarios morosos.
 * - Mostrar estadísticas de morosidad.
 * - Permitir regresar al menú principal.
 *
 * La información es obtenida desde la base de datos SQLite
 * mediante consultas SQL y presentada en una tabla.
 *
 * @author Jose Tot
 * @version 1.0
 * @since 2026
 */
public class CasasM extends javax.swing.JFrame {
    

  /**
 * Constructor de la ventana CasasM.
 *
 * Inicializa todos los componentes gráficos,
 * centra la ventana en pantalla y ejecuta
 * automáticamente la carga de información
 * relacionada con la morosidad.
 */
    public CasasM() {
        initComponents();
        this.setLocationRelativeTo(null);
        // Ejecución automática de los requerimientos al abrir la ventana
        mostrarPeriodoActual();
        cargarCasasMorosas();
    }

    /**
 * Obtiene y muestra el período actual.
 *
 * Utiliza la fecha del sistema para obtener
 * el mes y año vigentes, mostrando el resultado
 * en formato legible para el usuario.
 *
 * Ejemplo:
 * "Periodo Consultado: Mayo 2026"
 */
    private void mostrarPeriodoActual() {
        // Obtiene el mes y año actual de forma automática según la fecha del sistema
        LocalDate fechaActual = LocalDate.now();
        String mes = fechaActual.getMonth().getDisplayName(TextStyle.FULL, new Locale("es", "ES"));
        int anio = fechaActual.getYear();
        
        // Convierte la primera letra del mes en mayúscula (ej: Mayo 2026)
        String periodoFormateado = mes.substring(0, 1).toUpperCase() + mes.substring(1) + " " + anio;
        lblperiodo.setText("Periodo Consultado: " + periodoFormateado);
    }
   
    /**
 * Obtiene y muestra el período actual.
 *
 * Utiliza la fecha del sistema para obtener
 * el mes y año vigentes, mostrando el resultado
 * en formato legible para el usuario.
 *
 * Ejemplo:
 * "Periodo Consultado: Mayo 2026"
 */
    private java.sql.Connection conectar() {
    try {
        return ConexionDB.getConexion();
    } catch (java.sql.SQLException e) {
        System.out.println("Error de conexión en módulo morosos: " + e.getMessage());
        return null;
    }
}
/**
 * Carga la información de las casas morosas.
 *
 * Este método realiza las siguientes tareas:
 *
 * 1. Limpia la tabla actual.
 * 2. Verifica que la estructura de la base de datos
 *    contenga la columna de correo electrónico.
 * 3. Consulta la información de propietarios.
 * 4. Llena la tabla con los resultados obtenidos.
 * 5. Calcula el total de casas morosas.
 * 6. Actualiza los indicadores visuales del sistema.
 *
 * En caso de error se muestra un mensaje
 * informativo al usuario.
 */
    private void cargarCasasMorosas() {
          DefaultTableModel modelo = (DefaultTableModel) tablaMorosos.getModel();
        modelo.setRowCount(0); 
        
        int contadorMorosos = 0;
        int totalCasasCondominio = 30; 

        // 1. CONTROL DE SEGURIDAD: Creamos la columna 'correo' automáticamente si no existe
        try (Connection conn = conectar()) {
            if (conn != null) {
                boolean existeColumna = false;
                try (ResultSet rsCol = conn.getMetaData().getColumns(null, null, "Casas", "correo")) {
                    if (rsCol.next()) {
                        existeColumna = true;
                    }
                }
                // Si no existe en el archivo .db actual, la creamos en este instante
                if (!existeColumna) {
                    try (java.sql.Statement stmt = conn.createStatement()) {
                        stmt.execute("ALTER TABLE Casas ADD COLUMN correo TEXT");
                        System.out.println("Columna 'correo' creada con éxito.");
                    }
                }
            }
            } catch (SQLException e) {
            System.out.println("Aviso en verificación de estructura: " + e.getMessage());
        }

        // 2. CONSULTA COMPLETA: Ahora sí podemos pedir el correo con total seguridad
        String sql = "SELECT c.numero_casa, p.nombre, p.telefono, p.correo " +
             "FROM Casas c " +
             "LEFT JOIN Propietarios p ON c.id_propietario = p.id_propietario " +
             "WHERE c.id_propietario IS NOT NULL";
        
        try (Connection conn = conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                contadorMorosos++;
                int numCasa = rs.getInt("numero_casa");
                String propietario = rs.getString("nombre");
                String telefono = rs.getString("telefono");
                String correo = rs.getString("correo"); // Leerá el correo real

                    // Control de textos vacíos y eliminación del guion
                if (telefono == null || telefono.trim().isEmpty()) {
                    telefono = "No registrado";
                } else {
                    telefono = telefono.replace("-", "").trim();
                }

                if (correo == null || correo.trim().isEmpty()) {
                    correo = "No registrado";
                }

                modelo.addRow(new Object[]{numCasa, propietario, telefono, correo});
            }

            // 3. ACTUALIZACIÓN DE BANNERS EN INTERFAZ
            if (contadorMorosos == 0) {
                lblconteo.setBackground(new java.awt.Color(204, 255, 204)); 
                lblconteo.setForeground(new java.awt.Color(0, 102, 0));       
                lblconteo.setText(" Todas las casas están al día");
                jLabel3.setText("Casas Morosas: 0");
            } else {
                lblconteo.setBackground(new java.awt.Color(255, 51, 0)); 
                lblconteo.setForeground(java.awt.Color.WHITE);       
                lblconteo.setText(" Resumen de Morosidad: " + contadorMorosos + " casas morosas de " + totalCasasCondominio + " totales");
                jLabel3.setText("Casas Morosas: " + contadorMorosos);
            }
        } catch (SQLException e) {
            javax.swing.JOptionPane.showMessageDialog(this, "Error al cargar datos: " + e.getMessage());
        }
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblperiodo = new javax.swing.JLabel();
        lblconteo = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaMorosos = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        lblperiodo.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblperiodo.setText("Periodo Consulatdo:");

        lblconteo.setBackground(new java.awt.Color(255, 51, 0));
        lblconteo.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        lblconteo.setText("Resumen de Morosidad:");
        lblconteo.setOpaque(true);

        tablaMorosos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "No. Casa", "Propietario", "Telefono", "Correo"
            }
        ));
        jScrollPane1.setViewportView(tablaMorosos);

        jLabel3.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        jLabel3.setText("Casas Morosas:");

        jButton1.setBackground(new java.awt.Color(255, 0, 102));
        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jButton1.setText("Volver");
        jButton1.addActionListener(this::jButton1ActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(lblconteo, javax.swing.GroupLayout.DEFAULT_SIZE, 490, Short.MAX_VALUE)
                            .addComponent(lblperiodo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 452, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(15, 15, 15)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1)))
                .addContainerGap(33, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblperiodo, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblconteo, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 321, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addContainerGap(13, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
/**
 * Evento ejecutado al presionar el botón "Volver".
 *
 * Cierra la ventana actual y abre nuevamente
 * el menú principal del sistema.
 *
 * @param evt Evento generado por el botón.
 */
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        new MenuInicio().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
 * Método principal de ejecución.
 *
 * Inicializa el tema visual FlatLaf Light
 * y muestra la ventana de consulta
 * de casas morosas.
 *
 * @param args Argumentos enviados desde la línea
 *             de comandos.
 */
    public static void main(String args[]) {
          try {
        com.formdev.flatlaf.FlatLightLaf.setup();
        } catch (Exception e) { }
        java.awt.EventQueue.invokeLater(() -> new CasasM().setVisible(true));
        }

    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblconteo;
    private javax.swing.JLabel lblperiodo;
    private javax.swing.JTable tablaMorosos;
    // End of variables declaration//GEN-END:variables
}

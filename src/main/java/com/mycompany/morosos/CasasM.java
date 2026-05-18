/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.morosos;
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
 
 * @author deleo
 */
public class CasasM extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(CasasM.class.getName());

    /**
     * Creates new form CasasM
     */
    public CasasM() {
        initComponents();
        this.setLocationRelativeTo(null);
        // Ejecución automática de los requerimientos al abrir la ventana
        mostrarPeriodoActual();
        cargarCasasMorosas();
    }

    private void mostrarPeriodoActual() {
        // Obtiene el mes y año actual de forma automática según la fecha del sistema
        LocalDate fechaActual = LocalDate.now();
        String mes = fechaActual.getMonth().getDisplayName(TextStyle.FULL, new Locale("es", "ES"));
        int anio = fechaActual.getYear();
        
        // Convierte la primera letra del mes en mayúscula (ej: Mayo 2026)
        String periodoFormateado = mes.substring(0, 1).toUpperCase() + mes.substring(1) + " " + anio;
        lblperiodo.setText("Periodo Consultado: " + periodoFormateado);
    }
    private Connection conectar() {
        try {
            // Conexión directa a tu base de datos SQLite local
            String url = "jdbc:sqlite:condominio_vistaverde.db"; 
            return DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println("Error de conexión en módulo morosos: " + e.getMessage());
            return null;
        }
    }

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
        String sql = "SELECT numero_casa, nombre_encargado, telefono, correo FROM Casas WHERE estado_pago = 'Pendiente'";

        try (Connection conn = conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                contadorMorosos++;
                int numCasa = rs.getInt("numero_casa");
                String propietario = rs.getString("nombre_encargado");
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

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        lblperiodo.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblperiodo.setText("Periodo Consulatdo:");

        lblconteo.setBackground(new java.awt.Color(255, 51, 0));
        lblconteo.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N
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

        jLabel3.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel3.setText("Casas Morosas:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(lblconteo, javax.swing.GroupLayout.DEFAULT_SIZE, 490, Short.MAX_VALUE)
                            .addComponent(lblperiodo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 452, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(15, 15, 15)))
                        .addGap(0, 112, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblperiodo, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblconteo, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 321, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addContainerGap(19, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
         try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }

        /* Ejecuta la ventana */
        java.awt.EventQueue.invokeLater(() -> {
            new CasasM().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblconteo;
    private javax.swing.JLabel lblperiodo;
    private javax.swing.JTable tablaMorosos;
    // End of variables declaration//GEN-END:variables
}

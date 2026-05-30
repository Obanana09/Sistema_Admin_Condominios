/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.registropropietario;

/**
 * Clase Estadodecuenta.
 *
 * Esta ventana permite consultar el estado de cuenta
 * de las viviendas registradas en el sistema.
 *
 * Funciones principales:
 * - Seleccionar una casa del residencial.
 * - Mostrar la información del propietario.
 * - Consultar el historial de pagos mensuales.
 * - Identificar cuotas pagadas y pendientes.
 * - Calcular los montos totales pagados y adeudados.
 *
 * La información es obtenida directamente desde
 * la base de datos del sistema.
 *
 * @author Gerardo Silvestre
 * @version 1.0
 * @since 2026
 */
public class Estadodecuenta extends javax.swing.JFrame {
    
    /**
 * Constructor de la ventana Estado de Cuenta.
 *
 * Inicializa los componentes gráficos,
 * configura la ventana y carga automáticamente
 * el listado de casas disponibles para consulta.
 */
    public Estadodecuenta() {
    initComponents();
    configurarVentana();
    cargarCasas();
    jComboBox1.addActionListener(this::jComboBox1ActionPerformed);
}

    /**
 * Configura las propiedades generales de la ventana.
 *
 * - Centra la ventana en la pantalla.
 * - Impide que el usuario modifique su tamaño.
 */
private void configurarVentana() {
    setLocationRelativeTo(null);
    setResizable(false);
}

/**
 * Carga el listado de casas disponibles.
 *
 * Agrega al ComboBox las viviendas del residencial,
 * permitiendo al usuario seleccionar la casa
 * que desea consultar.
 *
 * Actualmente se cargan las casas del 1 al 30.
 */
private void cargarCasas() {
    jComboBox1.removeAllItems();
    jComboBox1.addItem("Seleccione una casa");
    for (int i = 1; i <= 30; i++) {
        jComboBox1.addItem(String.valueOf(i));
    }
}

/**
 * Evento ejecutado cuando el usuario selecciona
 * una casa en el listado.
 *
 * Si existe una selección válida, se cargan
 * los datos correspondientes a la vivienda.
 *
 * @param evt Evento generado por el ComboBox.
 */
private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {
    String seleccion = (String) jComboBox1.getSelectedItem();
    if (seleccion == null || seleccion.equals("Seleccione una casa")) {
        jLabel3.setText("Selecciona una casa para ver la información.");
        limpiarTabla();
        return;
    }
    int numeroCasa = Integer.parseInt(seleccion);
    cargarDatosCasa(numeroCasa);
}

/**
 * Obtiene y muestra toda la información
 * relacionada con una vivienda.
 *
 * Realiza las siguientes tareas:
 *
 * 1. Consulta los datos del propietario.
 * 2. Obtiene la cuota mensual vigente.
 * 3. Verifica los pagos realizados durante el año.
 * 4. Muestra el estado de cada mes.
 * 5. Calcula el total pagado.
 * 6. Calcula el total pendiente.
 * 7. Actualiza la tabla y los indicadores visuales.
 *
 * @param numeroCasa Número de vivienda seleccionada.
 */
private void cargarDatosCasa(int numeroCasa) {
    // Consultar propietario
    String sqlPropietario = 
        "SELECT p.nombre, p.telefono, p.correo " +
        "FROM Casas c LEFT JOIN Propietarios p ON c.id_propietario = p.id_propietario " +
        "WHERE c.numero_casa = ?";

    try (java.sql.Connection con = ConexionDB.getConexion();
         java.sql.PreparedStatement ps = con.prepareStatement(sqlPropietario)) {
        ps.setInt(1, numeroCasa);
        java.sql.ResultSet rs = ps.executeQuery();

        if (rs.next() && rs.getString("nombre") != null) {
            jLabel3.setText("<html>Nombre: " + rs.getString("nombre") +
                    "<br>Teléfono: " + rs.getString("telefono") +
                    "<br>Correo: " + rs.getString("correo") + "</html>");
        } else {
            jLabel3.setText("Esta casa no tiene propietario registrado.");
            limpiarTabla();
            return;
        }
    } catch (java.sql.SQLException e) {
        jLabel3.setText("Error al cargar propietario.");
        return;
    }

    // Consultar pagos
    String[] meses = {"Enero","Febrero","Marzo","Abril","Mayo","Junio",
                      "Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre"};

    javax.swing.table.DefaultTableModel modelo = 
        (javax.swing.table.DefaultTableModel) jTable1.getModel();
    modelo.setRowCount(0);

    double totalPagado = 0;
    double totalPendiente = 0;

    String sqlCuota = "SELECT cuota FROM Configuracion WHERE id = 1";
    double cuota = 1500.00;
    try (java.sql.Connection con = ConexionDB.getConexion();
         java.sql.Statement st = con.createStatement();
         java.sql.ResultSet rs = st.executeQuery(sqlCuota)) {
        if (rs.next()) cuota = rs.getDouble("cuota");
    } catch (java.sql.SQLException e) { }

    for (int i = 0; i < 12; i++) {
        String sqlPago = "SELECT COUNT(*) FROM Pagos WHERE numero_casa = ? AND mes = ?";
        try (java.sql.Connection con = ConexionDB.getConexion();
             java.sql.PreparedStatement ps = con.prepareStatement(sqlPago)) {
            ps.setInt(1, numeroCasa);
            ps.setInt(2, i + 1);
            java.sql.ResultSet rs = ps.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                modelo.addRow(new Object[]{meses[i], "✅ Pagado"});
                totalPagado += cuota;
            } else {
                modelo.addRow(new Object[]{meses[i], "❌ Pendiente"});
                totalPendiente += cuota;
            }
        } catch (java.sql.SQLException e) {
            modelo.addRow(new Object[]{meses[i], "Error"});
        }
    }

    jLabel5.setText("Total Pagado: Q " + String.format("%,.2f", totalPagado));
    jLabel6.setText("Total Pendiente: Q " + String.format("%,.2f", totalPendiente));
}

/**
 * Limpia la información mostrada en la tabla.
 *
 * Además reinicia los totales pagados y pendientes
 * cuando no existe una casa seleccionada
 * o cuando ocurre algún error de consulta.
 */
private void limpiarTabla() {
    javax.swing.table.DefaultTableModel modelo = 
        (javax.swing.table.DefaultTableModel) jTable1.getModel();
    modelo.setRowCount(0);
    jLabel5.setText("Total Pagado: Q 0.00");
    jLabel6.setText("Total Pendiente: Q 0.00");
}
    


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel1.setText("Estado de cuentas");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel2.setText("Seleccionar casa:");

        jComboBox1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jTable1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Mes", "Estado"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel4.setText("Detalle de pagos mensuales");

        jButton1.setBackground(new java.awt.Color(255, 51, 102));
        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jButton1.setText("Volver");
        jButton1.addActionListener(this::jButton1ActionPerformed);

        jLabel5.setBackground(new java.awt.Color(51, 255, 51));
        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(51, 255, 51));
        jLabel5.setText("Total pagado del año:");
        jLabel5.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, new java.awt.Color(51, 255, 51), new java.awt.Color(51, 255, 51), new java.awt.Color(102, 255, 102), null));

        jLabel6.setBackground(new java.awt.Color(255, 51, 51));
        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 51, 51));
        jLabel6.setText("Total pendiente del año:");
        jLabel6.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, null, new java.awt.Color(255, 51, 51)));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(100, 100, 100)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(jLabel3)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(52, 52, 52)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(271, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel6))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton1)))
                .addGap(111, 111, 111))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jButton1, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jLabel5))
                .addGap(86, 86, 86))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
 * Evento ejecutado al presionar el botón "Volver".
 *
 * Regresa al menú principal del sistema y
 * cierra la ventana actual.
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
 * Inicializa la apariencia visual de la aplicación
 * y muestra la ventana de Estado de Cuenta.
 *
 * @param args Argumentos enviados desde la línea
 *             de comandos.
 */
    public static void main(String args[]) {
    try {
        com.formdev.flatlaf.FlatLightLaf.setup();
    } catch (Exception e) { }
    java.awt.EventQueue.invokeLater(() -> new Estadodecuenta().setVisible(true));

        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new Estadodecuenta().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.registropropietario;

import com.formdev.flatlaf.FlatDarculaLaf;

/**
 * Clase principal del sistema de administración del Condominio Vista Verde.
 *
 * Esta clase actúa como punto de entrada de la aplicación. Se encarga de:
 * <ul>
 *   <li>Configurar el tema visual FlatDarculaLaf.</li>
 *   <li>Inicializar el entorno gráfico de Swing.</li>
 *   <li>Mostrar la ventana de inicio de sesión.</li>
 * </ul>
 *
 * Desde la ventana de inicio de sesión, el usuario puede acceder a las
 * diferentes funcionalidades del sistema, como el registro de propietarios,
 * registro de pagos, consulta de estados de cuenta y generación de reportes.
 *
 * @author deleo
 */
public class RegistroPropietario {

    /**
 * Método principal que inicia la ejecución del sistema.
 *
 * Configura el tema visual FlatDarculaLaf para mejorar la apariencia
 * de la interfaz gráfica y posteriormente abre la ventana de inicio
 * de sesión del sistema.
 *
 * @param args argumentos recibidos desde la línea de comandos.
 */
    public static void main(String[] args) {
        FlatDarculaLaf.setup();
        java.awt.EventQueue.invokeLater(() -> new Login().setVisible(true));
    }
}

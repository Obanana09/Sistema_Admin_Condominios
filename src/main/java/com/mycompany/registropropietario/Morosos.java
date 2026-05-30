/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.registropropietario;

/**
 * Clase de inicio para el módulo de consulta de casas morosas.
 *
 * Esta clase actúa como punto de entrada independiente para ejecutar
 * directamente la ventana CasasM sin necesidad de pasar por el menú principal.
 *
 * Al iniciar la aplicación, se crea una instancia de la ventana CasasM
 * y se muestra al usuario.
 *
 * @author deleo
 */
public class Morosos {

    /**
 * Método principal que inicia el módulo de casas morosas.
 *
 * Utiliza EventQueue.invokeLater para garantizar que la creación
 * y visualización de la interfaz gráfica se realice de forma segura
 * dentro del hilo de eventos de Swing.
 *
 * @param args argumentos enviados desde la línea de comandos
 */
    public static void main(String[] args) {
       java.awt.EventQueue.invokeLater(() -> {
            new CasasM().setVisible(true);
        });
    }
}

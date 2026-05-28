/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.registropropietario;

import com.formdev.flatlaf.FlatDarculaLaf;

public class RegistroPropietario {

    public static void main(String[] args) {
        FlatDarculaLaf.setup();
        java.awt.EventQueue.invokeLater(() -> new Login().setVisible(true));
    }
}

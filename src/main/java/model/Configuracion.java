/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author laske
 */
public class Configuracion {
    
    private static double CuotaMantenimiento = 1500.00;
    
    public static double getCuotaMante() {
        return CuotaMantenimiento;
    }
    
    public static void getCuotamante(double NuevaCuota) {
        CuotaMantenimiento = NuevaCuota;
        
    }
    
    public static double CalcularTotalEsperado() {
        return CuotaMantenimiento * 30;
        
    }
    
}

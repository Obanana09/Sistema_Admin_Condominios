/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.time.LocalDate;
/**
 *
 * @author laske
 */


public class Pago {
    
    //Atributos
    
    private int Mes;
    private int Año;
    private double Monto;
    private int NumeroCasa;
    private final LocalDate FechaRegistro; 
    
    //Constructor
    
    public Pago(int Mes, int Año, int NumeroCasa) {
        this.Mes = Mes;
        this.Año = Año;
        this.NumeroCasa = NumeroCasa;
        this.Monto = Configuracion.getCuotaMante();
        this.FechaRegistro = LocalDate.now();
        
    }
    
    //Getters
    
    public int getMes() {
        return Mes;
    }
    
    public int getAño() {
        return Año;
        
    }
    
    public double getMonto() {
        return Monto;
        
    }
    
    public int getNumeroCasa() {
        return NumeroCasa;
    }
    
    public LocalDate getFechaReistro() {
        return FechaRegistro;
    }
    
    //Setters
    
    public void setMes(int Mes) {
        this.Mes = Mes;
        
    }
    
    public void setAño(int Año) {
        this.Año = Año;
        
    }
    
    public void setMonto(double monto) {
        this.Monto = monto;
    }
    
    public void setNumeroCasa(int NumeroCasa) {
        this.NumeroCasa = NumeroCasa;
        
    }
    
    @Override
    
    public String toString() {
        return "Pago{" +
                "Mes=" + Mes +
                ", Año=" + Año +
                ", NumeroCasa=" + NumeroCasa +
                ", FechaRegistro=" + FechaRegistro +
                '}';
    }
}

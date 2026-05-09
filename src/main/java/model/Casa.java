/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;
import java.util.ArrayList;
/**
 *
 * @author laske
 */
public class Casa {
    // Atributos
    
    private int numero_casa;
    private Propietario propietario;
    private ArrayList<Pago> listaPagos;
    
    // Constructor
    
    public Casa(int numero_casa) {
        this.numero_casa = numero_casa;
        this.listaPagos = new ArrayList<>();
        
    }
    
    // Getters y Setters
    
    public int getNumero() {
        return numero_casa;
    }
    
    public void setNumero(int numero_casa) {
        this.numero_casa = numero_casa;
    }
    
    public Propietario getPropietario() {
        return propietario;
    }
    
    public void setPropietario(Propietario propietario) {
        this.propietario = propietario;
    }
    
    public ArrayList<Pago> getlistaPagos() {
        return listaPagos;
        
    }
    
    public void setListaPagos(ArrayList<Pago> listaPagos) {
        this.listaPagos = listaPagos;
        
    }
    
    // Metodo para agregar un pago
    
    public void agregarPago(Pago pago) {
        listaPagos.add(pago);
        
    }
    
    //Verificacion si ya existe un pago para el mes y año indicado 
    
    public boolean PagoDelMes(int mes, int año) {
        
        for(Pago pago : listaPagos) {
            
            if(pago.getMes() == mes &&
                    pago.getAño() == año) {
                return true;
            }
        }
        
        return false;
    }
    
    //Obtiene los meses pendietnes de pago en el año actual
    
    public ArrayList<Integer> MesesPendientes(int AñoAcutal) {
        
        ArrayList<Integer> pendientes = new ArrayList<>();
        
        for(int Mes = 1; Mes <= 12; Mes++) {
            
            boolean pagado = false;
            
            for(Pago pago : listaPagos) {
                
                if(pago.getMes() == Mes &&
                        pago.getAño() == Mes &&
                        pago.getAño() == AñoAcutal) {
                        
                    pagado = true;
                    break;
                }
            }
            
            if(!pagado) {
                pendientes.add(Mes);
                
            }
        }
            
        return pendientes;
    }
        // Calcular el total pagado por la cas
        
        public double calcularTotalPagado() {
        
        double total = 0;

        for (Pago pago : listaPagos) {
        total += pago.getMonto();
        }

        return total;
    }

        // Verifica si la casa es morosa
        public boolean esMorosa(int MesActual, int AñoActual) {

            return !PagoDelMes(MesActual, AñoActual);
        
    }
}

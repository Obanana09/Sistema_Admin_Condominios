/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author laske
 */       
public class Propietario {
    //Artributos

private String Nombre;
private String Telefono;
private String Correo;
private int NumeroCasa;

//Constructor

public Propietario(String Nombre, String Telefono, String Correo, int NumeroCasa) {
    this.Nombre = Nombre;
    this.Telefono = Telefono;
    this.Correo = Correo;
    this.NumeroCasa = NumeroCasa;
    
}

//Getters

public String getNombre() {
    return Nombre;
}

public String getTelefono() {
    return Telefono;
    
}

public String getCorreo() {
    return Correo;
    
}

//Setters

public void getNombre(String Nombre) {
    this.Nombre = Nombre;
}

public void setTelefono(String Telefono) {
    this.Telefono = Telefono;
}

public void setCorreo(String Correo) {
    this.Correo = Correo;
}

public void setNumeroCasa(int NumeroCasa) {
    this.NumeroCasa = NumeroCasa;
}

@Override

public String toString() {
    return "Propietario{" +
            "Nombre='" + Nombre + '\'' +
            ", Telefono='" + Telefono + '\'' +
            ", Correo='" + Correo + '\'' +
            ", NumeroCasa=" + NumeroCasa +
            '}';
}

    public int getNumeroCasa() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
 
}

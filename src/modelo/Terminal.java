package modelo;

import java.io.Serializable;

public class Terminal implements Serializable {

    private String nombre;
    private Direccion direccion;

    public Terminal(String nombre, Direccion direccion) {
        this.nombre = nombre;
        this.direccion = direccion;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Direccion getDireccion() { return direccion; }
    public void setDireccion(Direccion direccion) { this.direccion = direccion; }

    public String getComuna() { return direccion.getComuna(); }

    @Override
    public String toString() {
        return nombre + " - " + direccion.toString();
    }
}
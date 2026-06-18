package modelo;

//Autores: Juan Bustos- Yamilet Arias

import java.io.Serializable;

public class Terminal implements Serializable {

    private String nombre;
    private String comuna;

    public Terminal(String nombre, String comuna) {
        this.nombre = nombre;
        this.comuna = comuna;
    }

    public String getNombre() {
        return nombre;
    }

    public String getComuna() {
        return comuna;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setComuna(String comuna) {
        this.comuna = comuna;
    }

    @Override
    public String toString() {
        return nombre + " - " + comuna;
    }
}
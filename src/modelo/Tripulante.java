package modelo;

import utilidades.IdPersona;
import utilidades.Nombre;

import java.io.Serializable;

public abstract class Tripulante extends Persona implements Serializable {
    private Direccion direccion;

    public Tripulante(IdPersona id, Nombre nom, Direccion dir){
        super(id, nom);
        this.direccion = dir;
    }

    public Direccion getDireccion() {
        return direccion;
    }

    public void setDireccion(Direccion direccion){
        this.direccion = direccion;
    }

    public abstract void addViaje(Viaje viaje);

    public abstract int getNroViajes();
}

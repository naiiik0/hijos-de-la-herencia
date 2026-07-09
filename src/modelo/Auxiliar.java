package modelo;

import utilidades.IdPersona;
import utilidades.Nombre;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Auxiliar extends Tripulante implements Serializable {

    private List<Viaje> viajes = new ArrayList<>();

    public Auxiliar(IdPersona id, Nombre nom, Direccion dir) {
        super(id, nom, dir);
    }

    @Override
    public void addViaje(Viaje viaje) {
        viajes.add(viaje);
    }

    @Override
    public int getNroViajes() {
        return viajes.size();
    }
}

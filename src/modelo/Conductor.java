package modelo;

import utilidades.IdPersona;
import utilidades.Nombre;

import java.util.ArrayList;
import java.util.List;

public class Conductor extends Tripulante{
    private final List<Viaje> viajes = new ArrayList<>();

    public Conductor(IdPersona id, Nombre nom, Direccion dir) {
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

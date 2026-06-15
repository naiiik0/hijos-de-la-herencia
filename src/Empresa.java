package modelo;

import java.io.Serializable;
import java.util.ArrayList;

public class Empresa implements Serializable {

    private String rut;
    private String nombre;
    private String url;

    private ArrayList<Bus> buses;
    private ArrayList<Conductor> conductores;
    private ArrayList<Auxiliar> auxiliares;

    public Empresa(String rut, String nombre, String url) {
        this.rut = rut;
        this.nombre = nombre;
        this.url = url;

        buses = new ArrayList<>();
        conductores = new ArrayList<>();
        auxiliares = new ArrayList<>();
    }

    public String getRut() {
        return rut;
    }

    public String getNombre() {
        return nombre;
    }

    public String getUrl() {
        return url;
    }

    public void addBus(Bus bus) {
        buses.add(bus);
    }

    public Bus[] getBuses() {
        return buses.toArray(new Bus[0]);
    }

    public boolean addConductor(String id, Nombre nombre,
                                String telefono, int aniosExperiencia) {

        for (Conductor conductor : conductores) {
            if (conductor.getIdPersona().equals(id)) {
                return false;
            }
        }

        for (Auxiliar auxiliar : auxiliares) {
            if (auxiliar.getIdPersona().equals(id)) {
                return false;
            }
        }

        conductores.add(new Conductor(id, nombre,
                telefono, aniosExperiencia));

        return true;
    }

    public boolean addAuxiliar(String id, Nombre nombre,
                               String telefono) {

        for (Conductor conductor : conductores) {
            if (conductor.getIdPersona().equals(id)) {
                return false;
            }
        }

        for (Auxiliar auxiliar : auxiliares) {
            if (auxiliar.getIdPersona().equals(id)) {
                return false;
            }
        }

        auxiliares.add(new Auxiliar(id, nombre, telefono));

        return true;
    }

    public Tripulante[] getTripulantes() {

        Tripulante[] lista =
                new Tripulante[conductores.size() + auxiliares.size()];

        int i = 0;

        for (Conductor conductor : conductores) {
            lista[i++] = conductor;
        }

        for (Auxiliar auxiliar : auxiliares) {
            lista[i++] = auxiliar;
        }

        return lista;
    }

    public Venta[] getVentas() {

        ArrayList<Venta> ventas = new ArrayList<>();

        for (Bus bus : buses) {
            for (Viaje viaje : bus.getViajes()) {
                for (Venta venta : viaje.getVentas()) {
                    if (!ventas.contains(venta)) {
                        ventas.add(venta);
                    }
                }
            }
        }

        return ventas.toArray(new Venta[0]);
    }
}
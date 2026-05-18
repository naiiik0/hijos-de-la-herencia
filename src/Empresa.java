import java.util.ArrayList;
 // Autor: Yamilet Arias
public class Empresa {
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

    public boolean addConductor(String id, Nombre nombre, String telefono, int añosExperiencia) {

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

        Conductor nuevo = new Conductor(id, nombre, telefono, añosExperiencia);

        conductores.add(nuevo);

        return true;
    }

    public boolean addAuxiliar(String id, Nombre nombre, String telefono) {

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

        Auxiliar nuevo =
                new Auxiliar(id,
                        nombre,
                        telefono);

        auxiliares.add(nuevo);

        return true;
    }

    public Tripulante[] getTripulantes() {

        Tripulante[] lista =
                new Tripulante[conductores.size()
                        + auxiliares.size()];

        int i = 0;

        for (Conductor conductor : conductores) {

            lista[i] = conductor;
            i++;
        }

        for (Auxiliar auxiliar : auxiliares) {

            lista[i] = auxiliar;
            i++;
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

        Venta[] arreglo = new Venta[ventas.size()];

        for (int i = 0; i < ventas.size(); i++) {

            arreglo[i] = ventas.get(i);
        }

        return arreglo;
    }
} }

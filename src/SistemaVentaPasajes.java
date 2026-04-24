import java.util.ArrayList;

public class SistemaVentaPasajes {

    private ArrayList<Bus> buses;
    private ArrayList<Viaje> viajes;

    public SistemaVentaPasajes() {
        buses = new ArrayList<>();
        viajes = new ArrayList<>();
    }

   
    public boolean createBus(String patente, String marca, String modelo, int asientos) {
        for (Bus b : buses) {
            if (b.getPatente().equalsIgnoreCase(patente)) {
                return false;
            }
        }

        buses.add(new Bus(patente, marca, modelo, asientos));
        return true;
    }

  
    public Bus findBus(String patente) {
        for (Bus b : buses) {
            if (b.getPatente().equalsIgnoreCase(patente)) {
                return b;
            }
        }
        return null;
    }

  
    public boolean createViaje(String fecha, String hora, double precio, String patenteBus) {

        Bus bus = findBus(patenteBus);

        if (bus == null) return false;

     
        for (Viaje v : viajes) {
            if (v.getFecha().equals(fecha) &&
                    v.getHora().equals(hora) &&
                    v.getBus().getPatente().equalsIgnoreCase(patenteBus)) {
                return false;
            }
        }

        Viaje nuevo = new Viaje(fecha, hora, precio, bus);
        viajes.add(nuevo);
        bus.addViaje(nuevo);

        return true;
    }

    
    public void listarViajes() {
        if (viajes.isEmpty()) {
            System.out.println("No hay viajes registrados.");
            return;
        }

        for (Viaje v : viajes) {
            System.out.println(v);
        }
    }

}

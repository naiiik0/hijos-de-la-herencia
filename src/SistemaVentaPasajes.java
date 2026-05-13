import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
//Autores: Juan Bustos - Nicolás Figueroa
public class SistemaVentaPasajes {

    private ArrayList<Cliente> clientes;
    private ArrayList<Pasajero> pasajeros;
    private ArrayList<Bus> buses;
    private ArrayList<Viaje> viajes;
    private ArrayList<Venta> ventas;

    public SistemaVentaPasajes() {
        clientes = new ArrayList<>();
        pasajeros = new ArrayList<>();
        buses = new ArrayList<>();
        viajes = new ArrayList<>();
        ventas = new ArrayList<>();
    }

    public boolean createCliente(IdPersona idPersona, Nombre nom, String fono, String email) {
        if (findCliente(idPersona) != null) return false;
        Cliente c = new Cliente(idPersona, nom, email);
        c.setTelefono(fono);
        clientes.add(c);
        return true;
    }

    public boolean createPasajero(IdPersona idPersona, Nombre nom, String fono,
                                  Nombre nomContacto, String fonoContacto) {
        if (findPasajero(idPersona) != null) return false;
        Pasajero p = new Pasajero(idPersona, nom);
        p.setTelefono(fono);
        p.setNomContacto(nomContacto);
        p.setFonoContacto(fonoContacto);
        pasajeros.add(p);
        return true;
    }

    public boolean createBus(String patente, String marca, String modelo, int nroAsientos) {
        if (findBusPatente(patente) != null) return false;
        buses.add(new Bus(patente, marca, modelo, nroAsientos));
        return true;
    }

    public boolean createViaje(LocalDate fecha, LocalTime hora, int precio, String patBus) {
        Bus bus = findBusPatente(patBus);
        if (bus == null) return false;
        if (findViaje(fecha, hora, patBus) != null) return false;
        Viaje v = new Viaje(fecha, hora, precio, bus);
        viajes.add(v);
        return true;
    }

    public String[][] getHorariosDisponibles(LocalDate fechaViaje) {
        ArrayList<Viaje> viajesFecha = new ArrayList<>();
        for (Viaje v : viajes) {
            if (v.getFecha().equals(fechaViaje)) {
                viajesFecha.add(v);
            }
        }
        String[][] resultado = new String[viajesFecha.size()][4];
        for (int i = 0; i < viajesFecha.size(); i++) {
            Viaje v = viajesFecha.get(i);
            resultado[i][0] = v.getBus().getPatente();
            resultado[i][1] = v.getHora().toString();
            resultado[i][2] = String.valueOf(v.getPrecio());
            resultado[i][3] = String.valueOf(v.getNroAsientosDisponibles());
        }
        return resultado;
    }

    public String[] listAsientosDeViaje(LocalDate fecha, LocalTime hora, String patBus) {
        Viaje v = findViaje(fecha, hora, patBus);
        if (v == null) return new String[0];
        int[][] asientos = v.getAsientos();
        String[] resultado = new String[asientos.length];
        for (int i = 0; i < asientos.length; i++) {
            resultado[i] = (asientos[i][1] == 0)
                    ? String.valueOf(asientos[i][0])
                    : "*";
        }
        return resultado;
    }

    public boolean iniciaVenta(String idDoc, TipoDocumento tipo, LocalDate fechaVenta, IdPersona idCliente) {
        if (findVentaIdDocumento(idDoc, tipo) != null) return false;
        Cliente c = findCliente(idCliente);
        if (c == null) return false;
        Venta v = new Venta(idDoc, tipo, fechaVenta, c);
        ventas.add(v);
        return true;
    }

    public int getMontoVenta(String idDocumento, TipoDocumento tipo) {
        Venta v = findVentaIdDocumento(idDocumento, tipo);
        if (v == null) return 0;
        return v.getMonto();
    }

    public String getNombrePasajero(IdPersona idPasajero) {
        Pasajero p = findPasajero(idPasajero);
        if (p == null) return null;
        return p.getNombreCompleto().toString();
    }

    public boolean vendePasaje(String idDoc, TipoDocumento tipo, LocalDate fecha, LocalTime hora, String patBus, int asiento, IdPersona idPasajero) {
        Venta venta = findVentaIdDocumento(idDoc, tipo);
        if (venta == null) return false;
        Viaje viaje = findViaje(fecha, hora, patBus);
        if (viaje == null) return false;
        Pasajero pasajero = findPasajero(idPasajero);
        if (pasajero == null) return false;
        venta.createPasaje(asiento, viaje, pasajero);
        return true;
    }

    public String[][] listVentas(LocalDate fecha) {
        ArrayList<Venta> ventasFecha = new ArrayList<>();
        for (Venta v : ventas) {
            if (v.getFecha().equals(fecha)) ventasFecha.add(v);
        }
        String[][] resultado = new String[ventasFecha.size()][7];
        for (int i = 0; i < ventasFecha.size(); i++) {
            Venta v = ventasFecha.get(i);
            resultado[i][0] = v.getIdDocumento();
            resultado[i][1] = v.getTipo().toString();
            resultado[i][2] = formatearFecha(v.getFecha());
            resultado[i][3] = v.getCliente().getIdPersona().toString();
            resultado[i][4] = v.getCliente().getNombreCompleto().toString();
            resultado[i][5] = String.valueOf(v.getPasajes().length);
            resultado[i][6] = "$" + v.getMonto();
        }
        return resultado;
    }

    public String[][] listViajes() {
        String[][] resultado = new String[viajes.size()][5];
        for (int i = 0; i < viajes.size(); i++) {
            Viaje v = viajes.get(i);
            resultado[i][0] = formatearFecha(v.getFecha());
            resultado[i][1] = v.getHora().toString();
            resultado[i][2] = "$" + v.getPrecio();
            resultado[i][3] = String.valueOf(v.getNroAsientosDisponibles());
            resultado[i][4] = v.getBus().getPatente();
        }
        return resultado;
    }
    public String[][] listPasajeros(LocalDate fecha, LocalTime hora, String patenteBus) {
        Viaje v = findViaje(fecha, hora, patenteBus);
        if (v == null) return new String[0][0];
        ArrayList<Pasaje> pasajes = v.getPasajes();
        String[][] resultado = new String[pasajes.size()][5];
        for (int i = 0; i < pasajes.size(); i++) {
            Pasaje p = pasajes.get(i);
            Pasajero pasajero = p.getPasajero();
            resultado[i][0] = String.valueOf(p.getAsiento());
            resultado[i][1] = pasajero.getIdPersona().toString();
            resultado[i][2] = pasajero.getNombreCompleto().toString();
            resultado[i][3] = (pasajero.getNomContacto() != null)
                    ? pasajero.getNomContacto().toString() : "";
            resultado[i][4] = (pasajero.getFonoContacto() != null)
                    ? pasajero.getFonoContacto() : "";
        }
        return resultado;
    }
    public Cliente findCliente(IdPersona id) {
        for (Cliente c : clientes) {
            if (c.getIdPersona().equals(id)) return c;
        }
        return null;
    }

    public Venta findVentaIdDocumento(String idDocumento, TipoDocumento tipo) {
        for (Venta v : ventas) {
            if (v.getIdDocumento().equals(idDocumento) && v.getTipo() == tipo) return v;
        }
        return null;
    }

    public Bus findBusPatente(String patente) {
        for (Bus b : buses) {
            if (b.getPatente().equalsIgnoreCase(patente)) return b;
        }
        return null;
    }

    public Viaje findViaje(LocalDate fecha, LocalTime hora, String patBus) {
        for (Viaje v : viajes) {
            if (v.getFecha().equals(fecha)
                    && v.getHora().equals(hora)
                    && v.getBus().getPatente().equalsIgnoreCase(patBus)) {
                return v;
            }
        }
        return null;
    }

    public Pasajero findPasajero(IdPersona id) {
        for (Pasajero p : pasajeros) {
            if (p.getIdPersona().equals(id)) return p;
        }
        return null;
    }

    //METODO CREADO PARA SIMPLIFICAR EL TRABAJO
    private String formatearFecha(LocalDate fecha) {
        return String.format("%02d/%02d/%04d",
                fecha.getDayOfMonth(), fecha.getMonthValue(), fecha.getYear());
    }
}

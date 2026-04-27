import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
//Autores: Sofia Lagos - Nicolás Figueroa
public class Viaje {
    private LocalDate fecha;
    private LocalTime hora;
    private int precio;
    private Bus bus;
    private ArrayList<Pasaje> pasajes;

    public Viaje (LocalDate fecha, LocalTime hora, int precio, Bus bus){
        this.fecha = fecha;
        this.hora = hora;
        this.precio = precio;
        this.bus = bus;
        this.pasajes = new ArrayList<>();
        bus.addViaje(this);
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public LocalTime getHora() {
        return hora;
    }

    public int getPrecio() {
        return precio;
    }

    public void setPrecio(int precio) {
        this.precio = precio;
    }
    public Bus getBus(){
        return bus;
    }
    public int[][] getAsientos() {
        int totalAsientos = bus.getNroAsientos();
        int[][] asientos = new int[totalAsientos][2];
        for (int i = 0; i < totalAsientos; i++) {
            asientos[i][0] = i + 1;
            asientos[i][1] = 0;
        }
        for (Pasaje p : pasajes) {
            int asiento = p.getAsiento();
            if (asiento >= 1 && asiento <= totalAsientos) {
                asientos[asiento - 1][1] = 1;
            }
        }
        return asientos;
    }

    public void addPasaje (Pasaje pasaje){
        pasajes.add(pasaje);
    }
    public String[][] getListaPasajeros(){
        String[][] lista = new String[pasajes.size()][5];
        for (int i = 0; i < pasajes.size(); i++) {
            Pasaje p = pasajes.get(i);
            Pasajero pasajero =p.getPasajero();
            lista[i][0] = String.valueOf(p.getAsiento());
            lista[i][1] = pasajero.getIdPersona().toString();
            lista[i][2] = pasajero.getNombreCompleto().toString();
            lista[i][3] = (pasajero.getNomContacto() != null) ? pasajero.getNomContacto().toString() : "";
            lista[i][4] = (pasajero.getFonoContacto() != null) ? pasajero.getFonoContacto() : "";
        }
        return lista;
    }
    public boolean existeDisponibilidad(){
        return getNroAsientosDisponibles() > 0;
    }
    public int getNroAsientosDisponibles(){
        return bus.getNroAsientos() - pasajes.size();
    }
    public ArrayList<Pasaje> getPasajes() {
        return pasajes;
    }
}

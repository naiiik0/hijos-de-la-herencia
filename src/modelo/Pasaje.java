package modelo;

import java.io.Serializable;
import java.util.UUID;
//Autor: Nicolás Figueroa
public class Pasaje implements Serializable {
    private long numero;
    private int asiento;
    private Viaje viaje;
    private Pasajero pasajero;
    private Venta venta;

    public Pasaje(int asiento, Viaje viaje, Pasajero pasajero, Venta venta) {
        this.asiento = asiento;
        this.viaje = viaje;
        this.pasajero = pasajero;
        this.venta = venta;
        this.numero = Math.abs(UUID.randomUUID().getMostSignificantBits());
        viaje.addPasaje(this);
    }

    public long getNumero() {
        return numero;
    }

    public int getAsiento() {
        return asiento;
    }

    public Viaje getViaje() {
        return viaje;
    }

    public Pasajero getPasajero() {
        return pasajero;
    }

    public Venta getVenta() {
        return venta;
    }

    @Override
    public String toString() {
        String nombreEmpresa = viaje.getBus().getEmpresa() != null
                ? viaje.getBus().getEmpresa().getNombre().toUpperCase()
                : "";
        String nombrePasajero = pasajero.getNombreCompleto().toString().toUpperCase();
        String rutPasajero = pasajero.getIdPersona().toString();
        String patente = viaje.getBus().getPatente();
        String terminalOrigen = viaje.getTerminalSalida() != null
                ? viaje.getTerminalSalida().getNombre().toUpperCase() : "";
        String terminalDestino = viaje.getTerminalLlegada() != null
                ? viaje.getTerminalLlegada().getNombre().toUpperCase() : "";
        String fecha = String.format("%02d/%02d/%04d",
                viaje.getFecha().getDayOfMonth(),
                viaje.getFecha().getMonthValue(),
                viaje.getFecha().getYear());
        String hora = viaje.getHora().toString();
        int nroPasajes = venta.getPasajes().length;
        int valorPagado = (venta.getMontoPagado() > 0 && nroPasajes > 0)
                ? venta.getMontoPagado() / nroPasajes
                : viaje.getPrecio();

        StringBuilder sb = new StringBuilder();
        sb.append("-------------------- PASAJE ELECTRÓNICO --------------------\n");
        sb.append(String.format("%-20s %s%n", "Nombre Empresa", "Número de pasaje"));
        sb.append(String.format("%-20s %d%n", nombreEmpresa, numero));
        sb.append(String.format("%-35s %s%n", "Nombre Pasajero", "RUT/Pasaporte"));
        sb.append(String.format("%-35s %s%n", nombrePasajero, rutPasajero));
        sb.append(String.format("%-20s %-20s %s%n", "Patente bus", "Asiento", "Valor Pagado"));
        sb.append(String.format("%-20s %-20d %d%n", patente, asiento, valorPagado));
        sb.append(String.format("%-20s %-20s %-15s %s%n", "Terminal origen", "Terminal destino", "Fecha", "Hora"));
        sb.append(String.format("%-20s %-20s %-15s %s%n", terminalOrigen, terminalDestino, fecha, hora));
        sb.append("------------------------------------------------------------");
        return sb.toString();
    }
}


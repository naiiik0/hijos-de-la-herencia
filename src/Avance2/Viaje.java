package Avance2;

//Autor: Juan Bustos

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

public class Viaje {

    private LocalDate fecha;
    private LocalTime hora;
    private int precio;
    private int duracionMinutos;

    private Bus bus;

    private Terminal terminalSalida;
    private Terminal terminalLlegada;

    private Auxiliar auxiliar;

    private ArrayList<Conductor> conductores;
    private ArrayList<Pasaje> pasajes;

    public Viaje(LocalDate fecha,
                 LocalTime hora,
                 int precio,
                 int duracionMinutos,
                 Bus bus,
                 Terminal terminalSalida,
                 Terminal terminalLlegada,
                 Auxiliar auxiliar) {

        this.fecha = fecha;
        this.hora = hora;
        this.precio = precio;
        this.duracionMinutos = duracionMinutos;

        this.bus = bus;

        this.terminalSalida = terminalSalida;
        this.terminalLlegada = terminalLlegada;

        this.auxiliar = auxiliar;

        conductores = new ArrayList<>();
        pasajes = new ArrayList<>();
    }

    public void addConductor(Conductor conductor) {
        conductores.add(conductor);
    }

    public void addPasaje(Pasaje pasaje) {
        pasajes.add(pasaje);
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

    public int getDuracionMinutos() {
        return duracionMinutos;
    }

    public Bus getBus() {
        return bus;
    }

    public Terminal getTerminalSalida() {
        return terminalSalida;
    }

    public Terminal getTerminalLlegada() {
        return terminalLlegada;
    }

    public Auxiliar getAuxiliar() {
        return auxiliar;
    }

    public ArrayList<Conductor> getConductores() {
        return conductores;
    }

    public ArrayList<Pasaje> getPasajes() {
        return pasajes;
    }

    public int getNroAsientosDisponibles() {

        return bus.getNroAsientos() - pasajes.size();
    }

    public boolean existeDisponibilidad(int cantidad) {

        return getNroAsientosDisponibles() >= cantidad;
    }

    public String[] getAsientos() {

        String[] resultado = new String[bus.getNroAsientos()];

        for (int i = 0; i < resultado.length; i++) {

            resultado[i] = String.valueOf(i + 1);
        }

        for (Pasaje p : pasajes) {

            resultado[p.getAsiento() - 1] = "*";
        }

        return resultado;
    }

    public LocalDateTime getFechaHoraTermino() {

        LocalDateTime salida =
                LocalDateTime.of(fecha, hora);

        return salida.plusMinutes(duracionMinutos);
    }
}
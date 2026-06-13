package modelo;

import java.util.ArrayList;
//Autores: Juan Bustos - Nicolás Figueroa
public class Bus {
    private String patente;
    private String marca;
    private String modelo;
    private int nroAsientos;
    private Empresa empresa;
    private ArrayList<Viaje> viajes;

    public Bus(String patente, String marca, String modelo, int nroAsientos, Empresa empresa) {
        this.patente = patente;
        this.marca = marca;
        this.modelo = modelo;
        this.nroAsientos = nroAsientos;
        this.empresa = empresa;
        this.viajes = new ArrayList<>();
    }

    public String getPatente() {
        return patente;
    }

    public Empresa getEmpresa() {
        return empresa;
    }
    public String getMarca() {
        return marca;
    }
    public void setMarca(String marca) {
        this.marca = marca;
    }
    public String getModelo() {
        return modelo;
    }
    public void setModelo(String modelo) {
        this.modelo = modelo;
    }
    public int getNroAsientos() {
        return nroAsientos;
    }
    public void addViaje(Viaje viaje) {
        viajes.add(viaje);
    }
    public ArrayList<Viaje> getViajes() {
        return viajes;
    }

}

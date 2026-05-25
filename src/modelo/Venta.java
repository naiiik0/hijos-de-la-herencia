package modelo;

import java.time.LocalDate;
import java.util.ArrayList;
//Autor: Yamilet Arias
public class Venta {

    private String idDocumento;
    private TipoDocumento tipo;
    private LocalDate fecha;
    private Cliente cliente;
    private ArrayList<Pasaje> pasajes;
    private Pago pago;

    public Venta(String id, TipoDocumento tipo, LocalDate fec, Cliente cli) {
        this.idDocumento = id;
        this.tipo = tipo;
        this.fecha = fec;
        this.cliente = cli;
        this.pasajes = new ArrayList<>();
        cli.addVenta(this);
        pago = null;
    }
    public String getIdDocumento (){
        return idDocumento;
    }

    public TipoDocumento getTipo() {
        return tipo;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public Cliente getCliente() {
        return cliente;
    }
    public void createPasaje(int asiento, Viaje viaje, Pasajero pasajero) {
        Pasaje p = new Pasaje(asiento, viaje, pasajero, this);
        pasajes.add(p);
    }
    public Pasaje[] getPasajes() {
        return pasajes.toArray(new Pasaje[0]);
    }
    public int getMonto() {
        int total = 0;
        for (Pasaje p : pasajes) {
            total += p.getViaje().getPrecio();
        }
        return total;
    }
    public boolean pagaMonto() {

        if (pago != null) {

            return false;
        }

        pago = new PagoEfectivo(getMonto());

        return true;
    }
    public boolean pagaMonto(String nroTarjeta) {

        if (pago != null) {

            return false;
        }

        pago = new PagoTarjeta(
                getMonto(),
                nroTarjeta
        );

        return true;
    }

    public String getTipoPago() {

        if (pago == null) {

            return null;
        }

        return pago.getTipoPago();
    }

}

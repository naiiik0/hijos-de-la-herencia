package Avance3;

import Avance3.Pago;

import java.io.Serializable;

public class PagoTarjeta extends Pago implements Serializable {

    private String numeroTarjeta;
    private String banco;

    public PagoTarjeta(double monto, String numeroTarjeta, String banco) {
        super(monto);
        this.numeroTarjeta = numeroTarjeta;
        this.banco = banco;
    }

    public String getNumeroTarjeta() {
        return numeroTarjeta;
    }

    public void setNumeroTarjeta(String numeroTarjeta) {
        this.numeroTarjeta = numeroTarjeta;
    }

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    @Override
    public String toString() {
        return super.toString() +
                "\nBanco: " + banco +
                "\nNúmero de Tarjeta: " + numeroTarjeta;
    }
}
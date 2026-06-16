package modelo;
//Autor : Juan Bustos
public class PagoTarjeta extends Pago {

    private long nroTarjeta;

    public PagoTarjeta(int monto, long nroTarjeta) {
        super(monto);
        this.nroTarjeta = nroTarjeta;
    }

    public long getNroTarjeta() {
        return nroTarjeta;
    }

    @Override
    public String getTipoPago() {
        return "Pago Tarjeta";
    }

    public void setNroTarjeta(long nroTarjeta) {
        this.nroTarjeta = nroTarjeta;
    }
}
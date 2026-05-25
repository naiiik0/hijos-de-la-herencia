package Avance2;
//Autor : Juan Bustos
public class PagoTarjeta extends Pago {

    private String nroTarjeta;

    public PagoTarjeta(int monto, String nroTarjeta) {
        super(monto);
        this.nroTarjeta = nroTarjeta;
    }

    public String getNroTarjeta() {
        return nroTarjeta;
    }

    public void setNroTarjeta(String nroTarjeta) {
        this.nroTarjeta = nroTarjeta;
    }
}
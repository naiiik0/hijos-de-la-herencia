package modelo;
import java.io.Serializable;

//Autor: Juan bustos
public class PagoEfectivo extends Pago implements Serializable {
    public PagoEfectivo(int monto) {
        super(monto);
    }
    @Override
    public String getTipoPago() {
        return "Pago Efectivo";
    }
}
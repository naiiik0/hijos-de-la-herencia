package modelo;

//Autor: Juan Bustos

public class PagoEfectivo extends Pago {

    public PagoEfectivo(int monto) {
        super(monto);
    }
    @Override
    public String getTipoPago() {
        return "Pago Efectivo";
    }
}
package Avance3;

public class PagoEfectivo extends Pago {
    // Juan Bustos Segura
    private double montoEntregado;

    public PagoEfectivo(double monto, double montoEntregado) {
        super(monto);
        this.montoEntregado = montoEntregado;
    }

    public double getMontoEntregado() {
        return montoEntregado;
    }

    public void setMontoEntregado(double montoEntregado) {
        this.montoEntregado = montoEntregado;
    }

    public double calcularVuelto() {
        return montoEntregado - getMonto();
    }

    @Override
    public String toString() {
        return super.toString() +
                "\nPago en efectivo" +
                "\nEntregado: $" + montoEntregado +
                "\nVuelto: $" + calcularVuelto();
    }
}
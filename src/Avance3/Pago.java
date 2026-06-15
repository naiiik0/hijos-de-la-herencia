package Avance3;

import java.io.Serializable;
// Juan Bustos Segura
    public abstract class Pago implements Serializable {

        private double monto;

        public Pago(double monto) {
            this.monto = monto;
        }

        public double getMonto() {
            return monto;
        }

        public void setMonto(double monto) {
            this.monto = monto;
        }

        @Override
        public String toString() {
            return "Monto: $" + monto;
        }
    }


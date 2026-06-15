package Avance3;
// Juan Bustos Segura
import java.io.Serializable;

    public class Bus implements Serializable {

        private String patente;
        private String marca;
        private String modelo;
        private int numeroAsientos;

        public Bus(String patente, String marca, String modelo, int numeroAsientos) {
            this.patente = patente;
            this.marca = marca;
            this.modelo = modelo;
            this.numeroAsientos = numeroAsientos;
        }

        public String getPatente() {
            return patente;
        }

        public String getMarca() {
            return marca;
        }

        public String getModelo() {
            return modelo;
        }

        public int getNumeroAsientos() {
            return numeroAsientos;
        }

        public void setMarca(String marca) {
            this.marca = marca;
        }

        public void setModelo(String modelo) {
            this.modelo = modelo;
        }

        public void setNumeroAsientos(int numeroAsientos) {
            this.numeroAsientos = numeroAsientos;
        }

        @Override
        public String toString() {
            return patente + " - " + marca + " " + modelo;
        }
    }


package modelo;

import java.io.Serializable;
// Juan Bustos Segura
public abstract class Pago implements Serializable {
    private int monto;
    public Pago(int monto){
        this.monto = monto;
    }
    public int getMonto(){
        return monto;
    }
    public void setMonto(int monto){
        this.monto = monto;
    }
    public abstract String getTipoPago();
}


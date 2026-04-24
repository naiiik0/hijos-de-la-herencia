public class Bus {
    private String patente;
    private String marca;
    private String modelo;
    private int nroAsientos;
    public Bus (String patente, String marca, String modelo, int nroAsientos){
        this.patente=patente;
        this.marca=marca;
        this.modelo=modelo;
        this.nroAsientos=nroAsientos;
    }
    public String getPatente(){
        return patente;
    }


    @Override
    public String toString() {
        return "patente"+ patente + " Marca "+ marca + "modelo "+ modelo + "Asientos" + nroAsientos;


    }
}

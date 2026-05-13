import java.util.ArrayList;
import java.util.List;
//Autores: Sofia Lagos - Nicolás Figueroa
public class Cliente extends Persona{
    private String email;
    private List<Venta> ventas;

    public Cliente (IdPersona id, Nombre nom, String email){
        super(id, nom);
        this.email = email;
        this.ventas = new ArrayList<>();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public void addVenta(Venta venta) {
        ventas.add(venta);
    }
    public Venta[] getVentas() {
        return ventas.toArray(new Venta[0]);
    }
}

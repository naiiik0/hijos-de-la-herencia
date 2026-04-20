
public class Pasaporte {

    private String numero;
    private String nacionalidad;

    public Pasaporte(String num, String nacionalidad) {
        this.numero = num;
        this.nacionalidad = nacionalidad;
    }

    public String getNumero() {
        return numero;
    }

    public String getNacionalidad() {
        return nacionalidad;
    }


    public String toString() {
        return numero + " " + nacionalidad;
    }


    public boolean equals(Object obj) {
        if (obj instanceof Pasaporte) {
            Pasaporte p = (Pasaporte) obj;
            return numero.equals(p.numero) && nacionalidad.equals(p.nacionalidad);
        }
        return false;
    }

    public static Pasaporte of(String num, String nacionalidad) {
        if (num == null || nacionalidad == null) {
            return null;
        }
        return new Pasaporte(num, nacionalidad);
    }
}
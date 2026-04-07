import java.util.Objects;

public class Nombre {
private Tratamiento tratamiento;
private String nombres;
private String apellidoPaterno;
private String apellidoMaterno;

    public Tratamiento getTratamiento() {
        return tratamiento;
    }

    public void setTratamiento(Tratamiento tratamiento) {
        this.tratamiento = tratamiento;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidoPaterno() {
        return apellidoPaterno;
    }

    public void setApellidoPaterno(String apellidoPaterno) {
        this.apellidoPaterno = apellidoPaterno;
    }

    public String getApellidoMaterno() {
        return apellidoMaterno;
    }

    public void setApellidoMaterno(String apellidoMaterno) {
        this.apellidoMaterno = apellidoMaterno;
    }

    @Override
    public String toString() {
        return "Nombre{" +
                "tratamiento=" + tratamiento +
                ", nombres='" + nombres + '\'' +
                ", apellidoPaterno='" + apellidoPaterno + '\'' +
                ", apellidoMaterno='" + apellidoMaterno + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Nombre nombre = (Nombre) o;
        return tratamiento == nombre.tratamiento && Objects.equals(nombres, nombre.nombres) && Objects.equals(apellidoPaterno, nombre.apellidoPaterno) && Objects.equals(apellidoMaterno, nombre.apellidoMaterno);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tratamiento, nombres, apellidoPaterno, apellidoMaterno);
    }
}

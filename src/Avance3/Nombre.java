package Avance3;

import java.io.Serializable;
// Juan Bustos Segura
public class Nombre implements Serializable {

    private String tratamiento;
    private String nombres;
    private String apellidoPaterno;
    private String apellidoMaterno;

    public Nombre(String tratamiento, String nombres, String apellidoPaterno, String apellidoMaterno) {

        this.tratamiento = tratamiento;
        this.nombres = nombres;
        this.apellidoPaterno = apellidoPaterno;
        this.apellidoMaterno = apellidoMaterno;
    }

    public String getTratamiento() {
        return tratamiento;
    }

    public String getNombres() {
        return nombres;
    }

    public String getApellidoPaterno() {
        return apellidoPaterno;
    }

    public String getApellidoMaterno() {
        return apellidoMaterno;
    }

    public void setTratamiento(String tratamiento) {
        this.tratamiento = tratamiento;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public void setApellidoPaterno(String apellidoPaterno) {
        this.apellidoPaterno = apellidoPaterno;
    }

    public void setApellidoMaterno(String apellidoMaterno) {
        this.apellidoMaterno = apellidoMaterno;
    }

    @Override
    public String toString() {
        return tratamiento + " " + nombres + " "
                + apellidoPaterno + " " + apellidoMaterno;
    }
}
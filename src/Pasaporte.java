
public class Pasaporte  {

    private String Numero ;
    private String nacionalidad ;
    public Pasaporte (String num , String nacionalidad){
        this.Numero = num ;
        this.nacionalidad = nacionalidad ;
    }

    public String getNacionalidad() {
        return nacionalidad;
    }

    public String getNumero(){
        return Numero ;
    }

    public static  Pasaporte of (String num , String nacionalidad){
        return new Pasaporte (num, nacionalidad);
    }



}
public class Rut {
    private int numero;
    private char dv ;

    public Rut (int num, char dv){
        this.numero = num;
        this.dv = dv ;

    }

   public String toString (){
        return numero +""+ dv ;

   }
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Rut)) return false;

        Rut otro = (Rut) obj;
        return this.numero == otro.numero && this.dv == otro.dv;
    }

    public static Rut of(String rutStr) {
        try {
            rutStr = rutStr.replace(".", "");
            String[] partes = rutStr.split("-");

            if (partes.length != 2) return null;

            int numero = Integer.parseInt(partes[0]);
            char dv = partes[1].toUpperCase().charAt(0);

            if (calcularDV(numero) == dv) {
                return new Rut(numero, dv);
            } else {
                return null;
            }

        } catch (Exception e) {
            return null;
        }
    }
    private static char calcularDV(int numero) {
        int suma = 0;
        int multiplo = 2;

        while (numero > 0) {
            suma += (numero % 10) * multiplo;
            numero /= 10;

            multiplo++;
            if (multiplo > 7) multiplo = 2;
        }

        int resto = 11 - (suma % 11);

        if (resto == 11) return '0';
        if (resto == 10) return 'K';
        return (char) (resto + '0');
    }
}

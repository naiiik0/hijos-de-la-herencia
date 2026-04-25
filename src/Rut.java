public class Rut implements IdPersona {
    private int numero;
    private char dv ;

    public Rut (int num, char dv){
        this.numero = num;
        this.dv = dv ;
    }

    public int getNumero() {
        return numero;
    }

    public char getDv() {
        return dv;
    }

    @Override
    public String toString() {
        String numStr = String.valueOf(numero);
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (int i = numStr.length() - 1; i >= 0; i--) {
            if (count > 0 && count % 3 == 0) {
                sb.insert(0, '.');
            }
            sb.insert(0, numStr.charAt(i));
            count++;
        }
        sb.append('-');
        sb.append(dv);
        return sb.toString();
    }

    @Override
    public boolean equals(Object otro) {
        if (this == otro) return true;
        if (!(otro instanceof Rut)) return false;
        Rut r = (Rut) otro;
        return this.numero == r.numero && this.dv == r.dv;
    }

    public static Rut of(String rutStr) {
        if (rutStr == null || rutStr.isBlank()) return null;
        try {
            String limpio = rutStr.replace(".", "");
            String[] partes = limpio.split("-");
            if (partes.length != 2 || partes[0].isEmpty() || partes[1].isEmpty()) return null;
            int numero = Integer.parseInt(partes[0]);
            char dvIngresado = Character.toUpperCase(partes[1].charAt(0));
            if (calcularDV(numero) != dvIngresado) return null;
            return new Rut(numero, dvIngresado);
        } catch (Exception e) {
            return null;
        }
    }

    private static char calcularDV(int numero) {
        int suma = 0;
        int multiplo = 2;
        int num = numero;
        while (num > 0) {
            suma += (num % 10) * multiplo;
            num /= 10;
            multiplo++;
            if (multiplo > 7) multiplo = 2;
        }
        int resto = 11 - (suma % 11);
        if (resto == 11) return '0';
        if (resto == 10) return 'K';
        return (char) (resto + '0');
    }
}

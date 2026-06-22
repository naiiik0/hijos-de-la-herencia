package vista;

import controlador.ControladorEmpresas;
import controlador.SistemaVentaPasajes;
import java.util.Scanner;

public class UISVP {
    private static UISVP instancia;
    private static Scanner teclado = new Scanner(System.in);

    private static SistemaVentaPasajes sistema   = SistemaVentaPasajes.getInstance();
    private static ControladorEmpresas ce         = ControladorEmpresas.getInstance();

    private UISVP() {}

    public static UISVP getInstance() {
        if (instancia == null) {
            instancia = new UISVP();
        }
        return instancia;
    }
}

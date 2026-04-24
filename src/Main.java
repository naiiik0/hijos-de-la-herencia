import java.util.Scanner;

public class Main {

    private static SistemaVentaPasajes sistema = new SistemaVentaPasajes();
    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {

        int opcion;

        do {
            System.out.println("   MENU   ");
            System.out.println("1. Crear Bus");
            System.out.println("2. Crear Viaje");
            System.out.println("3. Listar Viajes");
            System.out.println("4. Salir");
            System.out.print("Seleccione opción: ");

            opcion = sc.nextInt();
            sc.nextLine();

            switch (opcion) {
                case 1:
                    crearBus();
                    break;
                case 2:
                    crearViaje();
                    break;
                case 3:
                    sistema.listarViajes();
                    break;
                case 4:
                    System.out.println("Saliendo...");
                    break;
                default:
                    System.out.println("Opción inválida");
            }

        } while (opcion != 4);
    }


    public static void crearBus() {
        System.out.println("Crear Bus");

        System.out.print("Patente: ");
        String patente = sc.nextLine();

        System.out.print("Marca: ");
        String marca = sc.nextLine();

        System.out.print("Modelo: ");
        String modelo = sc.nextLine();

        System.out.print("Asientos: ");
        int asientos = sc.nextInt();
        sc.nextLine();

        if (sistema.createBus(patente, marca, modelo, asientos)) {
            System.out.println("Bus creado correctamente");
        } else {
            System.out.println("Error: patente repetida");
        }
    }


    public static void crearViaje() {
        System.out.println("Crear Viaje ");

        System.out.print("Fecha (dd/mm/aaaa): ");
        String fecha = sc.nextLine();

        System.out.print("Hora (hh:mm): ");
        String hora = sc.nextLine();

        System.out.print("Precio: ");
        double precio = sc.nextDouble();
        sc.nextLine();

        System.out.print("Patente del bus: ");
        String patente = sc.nextLine();

        if (sistema.createViaje(fecha, hora, precio, patente)) {
            System.out.println("Viaje creado correctamente");
        } else {
            System.out.println("Error al crear viaje");
        }
    }
}
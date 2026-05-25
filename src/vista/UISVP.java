package vista;

import controlador.SistemaVentaPasajes;
import excepciones.SistemaVentaPasajesException;
import modelo.TipoDocumento;
import utilidades.IdPersona;
import utilidades.Nombre;
import utilidades.Rut;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Scanner;

public class UISVP {

    private static UISVP instancia;

    private Scanner sc;
    private SistemaVentaPasajes svp;

    private UISVP() {

        sc = new Scanner(System.in);
        svp = SistemaVentaPasajes.getInstance();

    }

    public static UISVP getInstancia() {

        if (instancia == null) {
            instancia = new UISVP();
        }

        return instancia;
    }

    public void menu() {

        int op;

        do {

            System.out.println("\n===== MENU =====");
            System.out.println("1. Crear Cliente");
            System.out.println("2. Crear Pasajero");
            System.out.println("3. Crear Viaje");
            System.out.println("4. Vender Pasaje");
            System.out.println("5. Pagar Venta");
            System.out.println("6. Listar Viajes");
            System.out.println("7. Listar Ventas");
            System.out.println("8. Listar Pasajeros Viaje");
            System.out.println("0. Salir");

            System.out.print("Ingrese opcion: ");

            if (sc.hasNextInt()) {

                op = sc.nextInt();
                sc.nextLine();

                switch (op) {

                    case 1:
                        createCliente();
                        break;

                    case 2:
                        createPasajero();
                        break;

                    case 3:
                        createViaje();
                        break;

                    case 4:
                        vendePasajes();
                        break;

                    case 5:
                        pagaVentaPasajes();
                        break;

                    case 6:
                        listViajes();
                        break;

                    case 7:
                        listVentas();
                        break;

                    case 8:
                        listPasajerosViaje();
                        break;

                    case 0:
                        System.out.println("Fin programa");
                        break;

                    default:
                        System.out.println("Opcion invalida");
                }

            } else {

                System.out.println("Debe ingresar un numero");
                sc.nextLine();
                op = -1;
            }

        } while (op != 0);

    }

    private void createCliente() {

        try {

            System.out.print("Rut: ");
            String rut = sc.nextLine();

            System.out.print("Nombre: ");
            String nombre = sc.nextLine();

            System.out.print("Apellido: ");
            String apellido = sc.nextLine();

            System.out.print("Telefono: ");
            String telefono = sc.nextLine();

            System.out.print("Email: ");
            String email = sc.nextLine();

            if (nombre.isBlank() || apellido.isBlank()) {

                System.out.println("Nombre invalido");
                return;
            }

            IdPersona id = Rut.of(rut);

            Nombre nom = new Nombre(nombre, apellido);

            svp.createCliente(id, nom, telefono, email);

            System.out.println("Cliente creado correctamente");

        } catch (SistemaVentaPasajesException e) {

            System.out.println(e.getMessage());

        } catch (Exception e) {

            System.out.println("Datos invalidos");
        }

    }

    private void createPasajero() {

        try {

            System.out.print("Rut pasajero: ");
            String rut = sc.nextLine();

            System.out.print("Nombre: ");
            String nombre = sc.nextLine();

            System.out.print("Apellido: ");
            String apellido = sc.nextLine();

            System.out.print("Telefono: ");
            String telefono = sc.nextLine();

            System.out.print("Nombre contacto: ");
            String nombreContacto = sc.nextLine();

            System.out.print("Apellido contacto: ");
            String apellidoContacto = sc.nextLine();

            System.out.print("Telefono contacto: ");
            String telefonoContacto = sc.nextLine();

            IdPersona id = Rut.of(rut);

            Nombre nom = new Nombre(nombre, apellido);

            Nombre nomContacto = new Nombre(nombreContacto, apellidoContacto);

            svp.createPasajero(id, nom, telefono, nomContacto, telefonoContacto);

            System.out.println("Pasajero creado correctamente");

        } catch (SistemaVentaPasajesException e) {

            System.out.println(e.getMessage());

        } catch (Exception e) {

            System.out.println("Datos invalidos");
        }

    }

    private void createViaje() {

        try {

            System.out.print("Fecha (AAAA-MM-DD): ");
            LocalDate fecha = LocalDate.parse(sc.nextLine());

            System.out.print("Hora (HH:MM): ");
            LocalTime hora = LocalTime.parse(sc.nextLine());

            System.out.print("Precio: ");
            int precio = Integer.parseInt(sc.nextLine());

            System.out.print("Duracion: ");
            int duracion = Integer.parseInt(sc.nextLine());

            System.out.print("Patente bus: ");
            String patente = sc.nextLine();

            String[] tripulantes = new String[2];

            System.out.print("Rut auxiliar: ");
            tripulantes[0] = sc.nextLine();

            System.out.print("Rut conductor: ");
            tripulantes[1] = sc.nextLine();

            String[] comunas = new String[2];

            System.out.print("Comuna salida: ");
            comunas[0] = sc.nextLine();

            System.out.print("Comuna llegada: ");
            comunas[1] = sc.nextLine();

            svp.createViaje(fecha, hora, precio, duracion,
                    patente, tripulantes, comunas);

            System.out.println("Viaje creado correctamente");

        } catch (SistemaVentaPasajesException e) {

            System.out.println(e.getMessage());

        } catch (Exception e) {

            System.out.println("Datos invalidos");
        }

    }

    private void vendePasajes() {

        try {

            System.out.print("Id documento: ");
            String idDoc = sc.nextLine();

            System.out.print("Tipo documento (BOLETA/FACTURA): ");
            TipoDocumento tipo = TipoDocumento.valueOf(sc.nextLine().toUpperCase());

            System.out.print("Fecha viaje (AAAA-MM-DD): ");
            LocalDate fecha = LocalDate.parse(sc.nextLine());

            System.out.print("Hora viaje (HH:MM): ");
            LocalTime hora = LocalTime.parse(sc.nextLine());

            System.out.print("Patente bus: ");
            String patente = sc.nextLine();

            System.out.print("Numero asiento: ");
            int asiento = Integer.parseInt(sc.nextLine());

            System.out.print("Rut pasajero: ");
            IdPersona idPasajero = Rut.of(sc.nextLine());

            svp.vendePasaje(idDoc, tipo, fecha, hora,
                    patente, asiento, idPasajero);

            System.out.println("Pasaje vendido correctamente");

        } catch (SistemaVentaPasajesException e) {

            System.out.println(e.getMessage());

        } catch (Exception e) {

            System.out.println("Datos invalidos");
        }

    }

    private void pagaVentaPasajes() {

        try {

            System.out.print("Id documento: ");
            String idDoc = sc.nextLine();

            System.out.print("Tipo documento (BOLETA/FACTURA): ");
            TipoDocumento tipo = TipoDocumento.valueOf(sc.nextLine().toUpperCase());

            svp.pagaVenta(idDoc, tipo);

            System.out.println("Venta pagada correctamente");

        } catch (SistemaVentaPasajesException e) {

            System.out.println(e.getMessage());

        } catch (Exception e) {

            System.out.println("Datos invalidos");
        }

    }

    private void listViajes() {

        String[][] viajes = svp.listViajes();

        for (int i = 0; i < viajes.length; i++) {

            for (int j = 0; j < viajes[i].length; j++) {

                System.out.print(viajes[i][j] + " ");
            }

            System.out.println();
        }

    }

    private void listVentas() {

        try {

            System.out.print("Fecha (AAAA-MM-DD): ");
            LocalDate fecha = LocalDate.parse(sc.nextLine());

            String[][] ventas = svp.listVentas(fecha);

            for (int i = 0; i < ventas.length; i++) {

                for (int j = 0; j < ventas[i].length; j++) {

                    System.out.print(ventas[i][j] + " ");
                }

                System.out.println();
            }

        } catch (Exception e) {

            System.out.println("Fecha invalida");
        }

    }

    private void listPasajerosViaje() {

        try {

            System.out.print("Fecha (AAAA-MM-DD): ");
            LocalDate fecha = LocalDate.parse(sc.nextLine());

            System.out.print("Hora (HH:MM): ");
            LocalTime hora = LocalTime.parse(sc.nextLine());

            System.out.print("Patente bus: ");
            String patente = sc.nextLine();

            String[][] pasajeros = svp.listPasajerosViaje(fecha, hora, patente);

            for (int i = 0; i < pasajeros.length; i++) {

                for (int j = 0; j < pasajeros[i].length; j++) {

                    System.out.print(pasajeros[i][j] + " ");
                }

                System.out.println();
            }

        } catch (SistemaVentaPasajesException e) {

            System.out.println(e.getMessage());

        } catch (Exception e) {

            System.out.println("Datos invalidos");
        }

    }

}


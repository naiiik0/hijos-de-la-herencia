import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;
public class Main {

    private static Scanner sc = new Scanner(System.in);
    private static SistemaVentaPasajes sistema = new SistemaVentaPasajes();

    public static void main(String[] args) {
        menu();
    }
    private static void menu() {
        int opcion = 0;
        do {
            System.out.println("==========================================");
            System.out.println("       ...:::: Menú principal ::::....   ");
            System.out.println();
            System.out.println("  1) Crear cliente");
            System.out.println("  2) Crear bus");
            System.out.println("  3) Crear viaje");
            System.out.println("  4) Vender pasaje");
            System.out.println("  5) Lista de pasajeros");
            System.out.println("  6) Lista de ventas");
            System.out.println("  7) Lista de viajes");
            System.out.println("  8) Consulta Viajes disponibles por fecha");
            System.out.println("  9) Salir");
            System.out.println("------------------------------------------");
            System.out.print("..:: Ingrese número de opción: ");
            try {
                opcion = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                opcion = -1;
            }
            switch (opcion) {
                case 1: createCliente();
                break;
                case 2: createBus();
                break;
                case 3: createViaje();
                break;
                case 4: vendePasajes();
                break;
                case 5: listPasajerosViaje();
                break;
                case 6: listVentas();
                break;
                case 7: listViajes();
                break;
                case 8: consultaViajesDisponibles();
                break;
                case 9: System.out.println("...:::: Hasta pronto ::::....");
                break;
                default: System.out.println("  [!] Opción inválida. Ingrese un número entre 1 y 9.");
            }
            System.out.println();
        } while (opcion != 9);
    }
    private static void createCliente() {
        System.out.println("...::::: Crear un nuevo Cliente :::::....");
        System.out.println();

        System.out.print("  Rut[1] o Pasaporte[2] : ");
        int tipoId = leerEnteroRango(1, 2);
        IdPersona idPersona = leerIdPersona(tipoId);
        if (idPersona == null) {
            System.out.println("  [!] Identificador inválido.");
            return;
        }

        System.out.print("       Sr.[1] o Sra. [2] : ");
        int selTrat = leerEnteroRango(1, 2);
        Tratamiento trat = (selTrat == 1) ? Tratamiento.SR : Tratamiento.SRA;

        System.out.print("               Nombres : ");
        String nombres = sc.nextLine().trim();
        System.out.print("      Apellido Paterno : ");
        String apPaterno = sc.nextLine().trim();
        System.out.print("      Apellido Materno : ");
        String apMaterno = sc.nextLine().trim();
        System.out.print("        Telefono movil : ");
        String telefono = sc.nextLine().trim();
        System.out.print("                Email : ");
        String email = sc.nextLine().trim();

        Nombre nombre = new Nombre(trat, nombres, apPaterno, apMaterno);
        boolean creado = sistema.createCliente(idPersona, nombre, telefono, email);
        System.out.println();
        if (creado) {
            System.out.println("...::::: Cliente guardado exitosamente :::::....");
        } else {
            System.out.println("  [!] Ya existe un cliente con ese identificador.");
        }
    }

    private static void createBus() {
        System.out.println("...::::: Creación de un nuevo BUS :::::....");
        System.out.println();

        System.out.print("           Patente : ");
        String patente = sc.nextLine().trim();
        System.out.print("             Marca : ");
        String marca = sc.nextLine().trim();
        System.out.print("            Modelo : ");
        String modelo = sc.nextLine().trim();
        System.out.print("  Número de asientos : ");
        int nroAsientos;
        try {
            nroAsientos = Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("  [!] Número de asientos inválido.");
            return;
        }

        boolean creado = sistema.createBus(patente, marca, modelo, nroAsientos);
        System.out.println();
        if (creado) {
            System.out.println("...::::: Bus guardado exitosamente :::::....");
        } else {
            System.out.println("  [!] Ya existe un bus con esa patente.");
        }
    }

    private static void createViaje() {
        System.out.println("...::::: Creación de un nuevo Viaje :::::....");
        System.out.println();
        System.out.print("  Fecha[dd/mm/yyyy] : ");
        LocalDate fecha = parsearFecha(sc.nextLine().trim());
        if (fecha == null) { System.out.println("  [!] Formato de fecha inválido.");
            return; }
        System.out.print("       Hora[hh:mm] : ");
        LocalTime hora = parsearHora(sc.nextLine().trim());
        if (hora == null) { System.out.println("  [!] Formato de hora inválido.");
            return; }
        System.out.print("           Precio : ");
        int precio;
        try {
            precio = Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("  [!] Precio inválido.");
            return;
        }
        System.out.print("      Patente Bus : ");
        String patente = sc.nextLine().trim();
        boolean creado = sistema.createViaje(fecha, hora, precio, patente);
        System.out.println();
        if (creado) {
            System.out.println("...::::: Viaje guardado exitosamente :::::....");
        } else {
            System.out.println("  [!] No existe el bus con esa patente, o ya existe un viaje para esa fecha, hora y bus.");
        }
    }
    private static void vendePasajes() {
        System.out.println("       ...::::: Venta de pasajes :::::....");
        System.out.println();
        System.out.println(":::: Datos de la Venta");
        System.out.print("            ID Documento : ");
        String idDoc = sc.nextLine().trim();
        System.out.print("Tipo documento: [1] Boleta [2] Factura : ");
        int selTipo = leerEnteroRango(1, 2);
        TipoDocumento tipo = (selTipo == 1) ? TipoDocumento.BOLETA : TipoDocumento.FACTURA;
        System.out.print("Fecha de venta[dd/mm/yyyy] : ");
        LocalDate fechaVenta = parsearFecha(sc.nextLine().trim());
        if (fechaVenta == null) {
            System.out.println("  [!] Formato de fecha inválido. Venta no concretada.");
            return;
        }
        if (sistema.findVentaIdDocumento(idDoc, tipo) != null) {
            System.out.println("  [!] Ya existe una venta con ese ID de documento. Venta no concretada.");
            return;
        }
        System.out.println();
        System.out.println(":::: Datos del cliente");
        System.out.print("    Rut[1] o Pasaporte[2] : ");
        int tipoIdCliente = leerEnteroRango(1, 2);
        IdPersona idCliente = leerIdPersona(tipoIdCliente);
        if (idCliente == null) {
            System.out.println("  [!] Identificador inválido. Venta no concretada.");
            return;
        }
        Cliente cliente = sistema.findCliente(idCliente);
        if (cliente == null) {
            System.out.println("  [!] No existe un cliente con ese identificador. Venta no concretada.");
            return;
        }
        System.out.println("        Nombre Cliente : " + cliente.getNombreCompleto().toString());
        sistema.iniciaVenta(idDoc, tipo, fechaVenta, idCliente);
        Venta ventaActual = sistema.findVentaIdDocumento(idDoc, tipo);
        System.out.println();
        System.out.println(":::: Pasajes a vender");
        System.out.print("     Cantidad de pasajes : ");
        int cantPasajes;
        try {
            cantPasajes = Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("  [!] Cantidad inválida.");
            return;
        }
        System.out.print("Fecha de viaje[dd/mm/yyyy] : ");
        LocalDate fechaViaje = parsearFecha(sc.nextLine().trim());
        if (fechaViaje == null) {
            System.out.println("  [!] Formato de fecha inválido.");
            return;
        }
        String[][] horarios = sistema.getHorariosDisponibles(fechaViaje);
        if (horarios.length == 0) {
            System.out.println("  [!] No existen viajes para esa fecha. Venta no concretada.");
            return;
        }
        ArrayList<String[]> disponibles = new ArrayList<>();
        for (String[] h : horarios) {
            if (Integer.parseInt(h[3]) >= cantPasajes) disponibles.add(h);
        }
        if (disponibles.isEmpty()) {
            System.out.println("  [!] No hay viajes con disponibilidad para " + cantPasajes + " pasajes. Venta no concretada.");
            return;
        }
        System.out.println();
        System.out.println(":::: Listado de horarios disponibles");
        System.out.println("     *----------*----------*----------*----------*");
        System.out.printf("     | %-8s | %-8s | %-8s | %-8s |%n", "BUS", "SALIDA", "VALOR", "ASIENTOS");
        System.out.println("     |----------+----------+----------+----------|");
        for (int i = 0; i < disponibles.size(); i++) {
            String[] h = disponibles.get(i);
            System.out.printf("  %2d | %-8s | %-8s | $%-7s | %-8s |%n",
                    i + 1, h[0], h[1], h[2], h[3]);
            System.out.println("     |----------+----------+----------+----------|");
        }
        System.out.println("     *----------*----------*----------*----------*");
        System.out.print("  Seleccione viaje en [1.." + disponibles.size() + "] : ");
        int selViaje = leerEnteroRango(1, disponibles.size());
        String[] viajeElegido = disponibles.get(selViaje - 1);
        LocalTime horaViaje = parsearHora(viajeElegido[1]);
        Viaje viaje = sistema.findViaje(fechaViaje, horaViaje, viajeElegido[0]);
        if (viaje == null) {
            System.out.println("  [!] Error al encontrar el viaje.");
            return;
        }
        System.out.println();
        System.out.println(":::: Asientos disponibles para el viaje seleccionado");
        mostrarAsientos(viaje);
        System.out.print("  Seleccione sus asientos [separe por ,] : ");
        String[] asientosStr = sc.nextLine().trim().split(",");
        if (asientosStr.length != cantPasajes) {
            System.out.println("  [!] Debe seleccionar exactamente " + cantPasajes + " asientos.");
            return;
        }
        int[] asientosElegidos = new int[cantPasajes];
        try {
            for (int i = 0; i < cantPasajes; i++) {
                asientosElegidos[i] = Integer.parseInt(asientosStr[i].trim());
            }
        } catch (NumberFormatException e) {
            System.out.println("  [!] Asientos inválidos.");
            return;
        }
        for (int i = 0; i < cantPasajes; i++) {
            System.out.println();
            System.out.println(":::: Datos pasajeros " + (i + 1));
            System.out.print("     Rut[1] o Pasaporte[2] : ");
            int tipoIdPas = leerEnteroRango(1, 2);
            IdPersona idPasajero = leerIdPersona(tipoIdPas);
            if (idPasajero == null) {
                System.out.println("  [!] Identificador inválido.");
                return;
            }
            if (sistema.findPasajero(idPasajero) == null) {
                System.out.println("  Pasajero no encontrado. Ingrese sus datos:");
                System.out.print("       Sr.[1] o Sra.[2] : ");
                int tratPas = leerEnteroRango(1, 2);
                Tratamiento tPas = (tratPas == 1) ? Tratamiento.SR : Tratamiento.SRA;
                System.out.print("              Nombres : ");
                String nomPas = sc.nextLine().trim();
                System.out.print("     Apellido Paterno : ");
                String apPPas = sc.nextLine().trim();
                System.out.print("     Apellido Materno : ");
                String apMPas = sc.nextLine().trim();
                System.out.print("             Telefono : ");
                String telPas = sc.nextLine().trim();
                System.out.print("      Nombre Contacto : ");
                String nomCont = sc.nextLine().trim();
                System.out.print("  Ap. Paterno Contacto : ");
                String apPCont = sc.nextLine().trim();
                System.out.print("  Ap. Materno Contacto : ");
                String apMCont = sc.nextLine().trim();
                System.out.print("   Sr.[1] o Sra.[2] (Contacto) : ");
                int tratCont = leerEnteroRango(1, 2);
                Tratamiento tCont = (tratCont == 1) ? Tratamiento.SR : Tratamiento.SRA;
                System.out.print("   Telefono Contacto : ");
                String telCont = sc.nextLine().trim();
                Nombre nombrePas = new Nombre(tPas, nomPas, apPPas, apMPas);
                Nombre nombreCont = new Nombre(tCont, nomCont, apPCont, apMCont);
                sistema.createPasajero(idPasajero, nombrePas, telPas, nombreCont, telCont);
            }
            boolean vendido = sistema.vendePasaje(idDoc, tipo, fechaViaje, horaViaje, viajeElegido[0], asientosElegidos[i], idPasajero);
            if (vendido) {
                System.out.println(":::: Pasaje agregado exitosamente");
            } else {
                System.out.println("  [!] No se pudo agregar el pasaje.");
            }
        }
        System.out.println();
        System.out.println(":::: Monto total de la venta: $" + ventaActual.getMonto());
        System.out.println();
        System.out.println(":::: Imprimiendo los pasajes");
        for (Pasaje p : ventaActual.getPasajes()) {
            imprimirPasaje(p);
        }
        System.out.println("   ...::::: Venta generada exitosamente :::::....");
    }
    private static void listPasajerosViaje() {
        System.out.println("...::::: Listado de pasajeros de un viaje :::::....");
        System.out.println();
        System.out.print("  Fecha del viaje[dd/mm/yyyy] : ");
        LocalDate fecha = parsearFecha(sc.nextLine().trim());
        if (fecha == null) { System.out.println("  [!] Formato de fecha inválido.");
            return; }
        System.out.print("     Hora del viaje[hh:mm] : ");
        LocalTime hora = parsearHora(sc.nextLine().trim());
        if (hora == null) { System.out.println("  [!] Formato de hora inválido.");
            return; }
        System.out.print("              Patente bus : ");
        String patente = sc.nextLine().trim();
        String[][] pasajeros = sistema.listPasajeros(fecha, hora, patente);
        if (pasajeros.length == 0) {
            System.out.println("  [!] No existe el bus o un viaje con los datos indicados.");
            return;
        }
        String sep = "*----------*------------------*--------------------------------*--------------------------------*--------------------*";
        System.out.println(sep);
        System.out.printf("| %-8s | %-16s | %-30s | %-30s | %-18s |%n",
                "ASIENTO", "RUT/PASS", "PASAJERO", "CONTACTO", "TELEFONO CONTACTO");
        System.out.println("|----------+------------------+--------------------------------+--------------------------------+--------------------|");
        for (String[] fila : pasajeros) {
            System.out.printf("| %-8s | %-16s | %-30s | %-30s | %-18s |%n",
                    fila[0], fila[1], fila[2], fila[3], fila[4]);
            System.out.println("|----------+------------------+--------------------------------+--------------------------------+--------------------|");
        }
        System.out.println(sep);
    }
    private static void listVentas() {
        System.out.println("       ...::::: Listado de ventas :::::....");
        System.out.println();
        System.out.print("  Fecha[dd/mm/yyyy] : ");
        LocalDate fecha = parsearFecha(sc.nextLine().trim());
        if (fecha == null) { System.out.println("  [!] Formato de fecha inválido.");
            return; }
        String[][] ventas = sistema.listVentas(fecha);
        if (ventas.length == 0) {
            System.out.println("  [!] No existen ventas registradas para esa fecha.");
            return;
        }
        String sep = "*-------------*-----------*--------------*----------------------------------*---------------*---------------*";
        System.out.println(sep);
        System.out.printf("| %-11s | %-9s | %-12s | %-16s %-15s | %-13s | %-13s |%n", "ID DOCUMENT", "TIPO DOCU", "FECHA", "RUT/PASAPORTE", "| CLIENTE", "CANT BOLETOS", "TOTAL VENTA");
        System.out.println("|-------------+-----------+--------------+----------------------------------+---------------+---------------|");
        for (String[] f : ventas) {
            System.out.printf("| %-11s | %-9s | %-12s | %-14s | %-16s | %-13s | %-13s |%n",
                    f[0], f[1], f[2], f[3], f[4], f[5], f[6]);
            System.out.println("|-------------+-----------+--------------+----------------------------------+---------------+---------------|");
        }
        System.out.println(sep);
    }
    private static void listViajes() {
        System.out.println("       ...::::: Listado de viajes :::::....");
        System.out.println();
        String[][] viajes = sistema.listViajes();
        if (viajes.length == 0) {
            System.out.println("  [!] No existen viajes registrados en el sistema.");
            return;
        }
        String sep = "*---------------*---------------*-----------*-----------*---------------*";
        System.out.println(sep);
        System.out.printf("| %-13s | %-13s | %-9s | %-11s | %-13s |%n",
                "FECHA", "HORA", "PRECIO", "DISPONIBLES", "PATENTE");
        System.out.println("|---------------+---------------+-----------+-----------+---------------|");
        for (String[] f : viajes) {
            System.out.printf("| %-13s | %-13s | %-9s | %-11s | %-13s |%n",
                    f[0], f[1], f[2], f[3], f[4]);
            System.out.println("|---------------+---------------+-----------+-----------+---------------|");
        }
        System.out.println(sep);
    }

    private static void consultaViajesDisponibles() {
        System.out.println("...::::: Consulta Viajes disponibles por fecha :::::....");
        System.out.println();
        System.out.print("  Fecha[dd/mm/yyyy] : ");
        LocalDate fecha = parsearFecha(sc.nextLine().trim());
        if (fecha == null) { System.out.println("  [!] Formato de fecha inválido.");
            return; }
        String[][] horarios = sistema.getHorariosDisponibles(fecha);
        if (horarios.length == 0) {
            System.out.println("  [!] No existen viajes disponibles para esa fecha.");
            return;
        }
        System.out.println("  Total de viajes: " + horarios.length);
        System.out.println();
        String sep = "*-----------*-----------*-----------*-----------*";
        System.out.println(sep);
        System.out.printf("| %-9s | %-9s | %-9s | %-9s |%n",
                "PATENTE", "HORA", "PRECIO", "ASIENTOS");
        System.out.println("|-----------+-----------+-----------+-----------|");
        for (String[] h : horarios) {
            System.out.printf("| %-9s | %-9s | $%-8s | %-9s |%n",
                    h[0], h[1], h[2], h[3]);
            System.out.println("|-----------+-----------+-----------+-----------|");
        }
        System.out.println(sep);
    }
    // METODOS AUXILIARES

    private static IdPersona leerIdPersona(int tipo) {
        if (tipo == 1) {
            System.out.print("           R.U.T : ");
            String rutStr = sc.nextLine().trim();
            return Rut.of(rutStr);
        } else {
            System.out.print("  Número Pasaporte : ");
            String num = sc.nextLine().trim();
            System.out.print("       Nacionalidad : ");
            String nac = sc.nextLine().trim();
            return Pasaporte.of(num, nac);
        }
    }

    private static int leerEnteroRango(int min, int max) {
        int valor = -1;
        while (valor < min || valor > max) {
            try {
                valor = Integer.parseInt(sc.nextLine().trim());
                if (valor < min || valor > max) {
                    System.out.print("  [!] Ingrese un valor entre " + min + " y " + max + ": ");
                }
            } catch (NumberFormatException e) {
                System.out.print("  [!] Valor inválido. Ingrese entre " + min + " y " + max + ": ");
            }
        }
        return valor;
    }

    private static LocalDate parsearFecha(String fechaStr) {
        try {
            return LocalDate.parse(fechaStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private static LocalTime parsearHora(String horaStr) {
        try {
            return LocalTime.parse(horaStr, DateTimeFormatter.ofPattern("HH:mm"));
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private static void mostrarAsientos(Viaje viaje) {
        int[][] asientos = viaje.getAsientos();
        System.out.println("  *---*---*---*---*");
        int col = 0;
        StringBuilder fila = new StringBuilder("  |");
        for (int i = 0; i < asientos.length; i++) {
            String celda = (asientos[i][1] == 0)
                    ? String.format("%2d", asientos[i][0])
                    : " *";
            fila.append(celda).append("|");
            col++;
            if (col == 4) {
                System.out.println(fila.toString());
                System.out.println("  |---+---+---+---|");
                fila = new StringBuilder("  |");
                col = 0;
            }
        }
        if (col > 0) {
            while (col < 4) { fila.append("   |"); col++; }
            System.out.println(fila.toString());
        }
        System.out.println("  *---*---*---*---*");
    }

    private static void imprimirPasaje(Pasaje p) {
        System.out.println();
        System.out.println("  ------------------ PASAJE --------------------");
        System.out.printf("  %-22s : %d%n",  "NUMERO DE PASAJE", p.getNumero());
        System.out.printf("  %-22s : %s%n",  "FECHA DE VIAJE",   formatearFecha(p.getViaje().getFecha()));
        System.out.printf("  %-22s : %s%n",  "HORA DE VIAJE",    p.getViaje().getHora().toString());
        System.out.printf("  %-22s : %s%n",  "PATENTE BUS",      p.getViaje().getBus().getPatente());
        System.out.printf("  %-22s : %d%n",  "ASIENTO",          p.getAsiento());
        System.out.printf("  %-22s : %s%n",  "RUT/PASAPORTE",    p.getPasajero().getIdPersona().toString());
        System.out.printf("  %-22s : %s%n",  "NOMBRE PASAJERO",  p.getPasajero().getNombreCompleto().toString());
        System.out.println("  -----------------------------------------------");
    }

    private static String formatearFecha(LocalDate fecha) {
        return String.format("%02d/%02d/%04d",
                fecha.getDayOfMonth(), fecha.getMonthValue(), fecha.getYear());
    }
}
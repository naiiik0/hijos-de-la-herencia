package vista;
import controlador.ControladorEmpresas;
import controlador.SistemaVentaPasajes;
import excepciones.SistemaVentaPasajesException;
import modelo.Cliente;
import modelo.TipoDocumento;
import utilidades.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import java.util.Optional;

//Autores: Nicolás Figueroa - Juan Bustos
public class Main {
    private static SistemaVentaPasajes sistema = SistemaVentaPasajes.getInstance();
    private static ControladorEmpresas controladorEmpresas = ControladorEmpresas.getInstance();
    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        menu();
    }
    private static void menu() {
        int opcion = 0;
        do {
            System.out.println("==========================================");
            System.out.println("       ...:::: Menú principal ::::....   ");
            System.out.println();
            System.out.println("   1) Crear empresa");
            System.out.println("   2) Contratar tripulante");
            System.out.println("   3) Crear terminal");
            System.out.println("   4) Crear cliente");
            System.out.println("   5) Crear bus");
            System.out.println("   6) Crear viaje");
            System.out.println("   7) Vender pasajes");
            System.out.println("   8) Listar ventas");
            System.out.println("   9) Listar viajes");
            System.out.println("  10) Listar pasajeros de viaje");
            System.out.println("  11) Listar empresas");
            System.out.println("  12) Listar llegadas/salidas de terminal");
            System.out.println("  13) Listar ventas de empresa");
            System.out.println("  14) Salir");
            System.out.println("------------------------------------------");
            System.out.print("..:: Ingrese número de opción: ");
            try {
                opcion = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                opcion = -1;
            }
            switch (opcion) {
                case 1: crearEmpresa();
                break;
                case 2: contratarTripulante();
                break;
                case 3: crearTerminal();
                break;
                case 4: createCliente();
                break;
                case 5: createBus();
                break;
                case 6: createViaje();
                break;
                case 7: vendePasajes();
                break;
                case 8: listVentas();
                break;
                case 9: listViajes();
                break;
                case 10: listPasajerosViaje();
                break;
                case 11: listEmpresas();
                break;
                case 12: listLlegadasSalidasTerminal();
                break;
                case 13: listVentasEmpresa();
                break;
                case 14: System.out.println("...:::: Hasta pronto ::::...."); break;
                default: System.out.println("  [!] Opción inválida. Ingrese un número entre 1 y 14.");
            }
            System.out.println();
        } while (opcion != 14);
    }
    private static void crearEmpresa() {
        System.out.println("...::::: Creando una nueva Empresa :::::....");
        System.out.println();
        System.out.print("         R.U.T : ");
        Rut rut = Rut.of(sc.nextLine().trim());
        if (rut == null) { System.out.println("  [!] RUT inválido."); return; }
        System.out.print("        Nombre : ");
        String nombre = sc.nextLine().trim();
        if (nombre.isBlank()) { System.out.println("  [!] Nombre inválido."); return; }
        System.out.print("           url : ");
        String url = sc.nextLine().trim();
        try {
            controladorEmpresas.createEmpresa(rut, nombre, url);
            System.out.println();
            System.out.println("...::::: Empresa guardada exitosamente :::::....");
        } catch (SistemaVentaPasajesException e) {
            System.out.println("  [!] " + e.getMessage());
        }
    }
    private static void contratarTripulante() {
        System.out.println("...::::: Contratando un nuevo modelo.Tripulante :::::....");
        System.out.println();
        System.out.println(":::: Dato de la Empresa");
        System.out.print("         R.U.T : ");
        Rut rutEmp = Rut.of(sc.nextLine().trim());
        if (rutEmp == null) { System.out.println("  [!] RUT inválido."); return; }
        System.out.println();
        System.out.println(":::: Datos tripulante");
        System.out.print("modelo.Auxiliar[1] o modelo.Conductor[2] : ");
        int tipoTrip = leerEnteroRango(1, 2);
        System.out.print("   Rut[1] o Pasaporte[2] : ");
        int tipoId = leerEnteroRango(1, 2);
        IdPersona id = leerIdPersona(tipoId);
        if (id == null) { System.out.println("  [!] Identificador inválido."); return; }
        System.out.print("       Sr.[1] o Sra.[2] : ");
        int selTrat = leerEnteroRango(1, 2);
        Tratamiento trat = (selTrat == 1) ? Tratamiento.SR : Tratamiento.SRA;
        System.out.print("            Nombres : ");
        String nombres = sc.nextLine().trim();
        System.out.print("   Apellido Paterno : ");
        String apP = sc.nextLine().trim();
        System.out.print("   Apellido Materno : ");
        String apM = sc.nextLine().trim();
        System.out.print("              Calle : ");
        String calle = sc.nextLine().trim();
        System.out.print("             Numero : ");
        int numero;
        try {
            numero = Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("  [!] Número de calle inválido."); return;
        }
        System.out.print("             Comuna : ");
        String comuna = sc.nextLine().trim();
        Nombre nom = new Nombre(trat, nombres, apP, apM);
        Direccion dir = new Direccion(calle, numero, comuna);
        try {
            if (tipoTrip == 1) {
                controladorEmpresas.hireAuxiliarForEmpresa(rutEmp, id, nom, dir);
                System.out.println();
                System.out.println("...::::: modelo.Auxiliar contratado exitosamente :::::....");
            } else {
                controladorEmpresas.hireConductorForEmpresa(rutEmp, id, nom, dir);
                System.out.println();
                System.out.println("...::::: modelo.Conductor contratado exitosamente :::::....");
            }
        } catch (SistemaVentaPasajesException e) {
            System.out.println("  [!] " + e.getMessage());
        }
    }
    private static void crearTerminal() {
        System.out.println("...::::: Creando un nuevo Terminal :::::....");
        System.out.println();
        System.out.print("    Nombre : ");
        String nombre = sc.nextLine().trim();
        if (nombre.isBlank()) { System.out.println("  [!] Nombre inválido."); return; }
        System.out.print("     Calle : ");
        String calle = sc.nextLine().trim();
        System.out.print("    Numero : ");
        int numero;
        try {
            numero = Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("  [!] Número inválido."); return;
        }
        System.out.print("    Comuna : ");
        String comuna = sc.nextLine().trim();
        Direccion dir = new Direccion(calle, numero, comuna);
        try {
            controladorEmpresas.createTerminal(nombre, dir);
            System.out.println();
            System.out.println("...::::: Terminal guardado exitosamente :::::....");
        } catch (SistemaVentaPasajesException e) {
            System.out.println("  [!] " + e.getMessage());
        }
    }
    private static void createCliente() {
        System.out.println("...::::: Crear un nuevo Cliente :::::....");
        System.out.println();
        System.out.print("  Rut[1] o Pasaporte[2] : ");
        int tipoId = leerEnteroRango(1, 2);
        IdPersona idPersona = leerIdPersona(tipoId);
        if (idPersona == null) { System.out.println("  [!] Identificador inválido."); return; }
        System.out.print("    Sr.[1] o Sra. [2] : ");
        int selTrat = leerEnteroRango(1, 2);
        Tratamiento trat = (selTrat == 1) ? Tratamiento.SR : Tratamiento.SRA;
        System.out.print("           Nombres : ");
        String nombres = sc.nextLine().trim();
        System.out.print("  Apellido Paterno : ");
        String apP = sc.nextLine().trim();
        System.out.print("  Apellido Materno : ");
        String apM = sc.nextLine().trim();
        System.out.print("    Telefono movil : ");
        String telefono = sc.nextLine().trim();
        System.out.print("             Email : ");
        String email = sc.nextLine().trim();
        Nombre nombre = new Nombre(trat, nombres, apP, apM);
        try {
            sistema.createCliente(idPersona, nombre, telefono, email);
            System.out.println();
            System.out.println("...::::: Cliente guardado exitosamente :::::....");
        } catch (SistemaVentaPasajesException e) {
            System.out.println("  [!] " + e.getMessage());
        }
    }
    private static void createBus() {
        System.out.println("...::::: Creando un nuevo Bus :::::....");
        System.out.println();
        System.out.print("            Patente : ");
        String patente = sc.nextLine().trim();
        System.out.print("              Marca : ");
        String marca = sc.nextLine().trim();
        System.out.print("             Modelo : ");
        String modelo = sc.nextLine().trim();
        System.out.print("  Número de asientos : ");
        int nroAsientos;
        try {
            nroAsientos = Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("  [!] Número de asientos inválido."); return;
        }
        System.out.println(":::: Dato de la empresa");
        System.out.print("         R.U.T : ");
        Rut rutEmp = Rut.of(sc.nextLine().trim());
        if (rutEmp == null) { System.out.println("  [!] RUT inválido."); return; }
        try {
            controladorEmpresas.createBus(patente, marca, modelo, nroAsientos, rutEmp);
            System.out.println();
            System.out.println("...::::: Bus guardado exitosamente :::::....");
        } catch (SistemaVentaPasajesException e) {
            System.out.println("  [!] " + e.getMessage());
        }
    }
    private static void listEmpresas() {
        System.out.println("       ...::::: Listado de empresas :::::....");
        System.out.println();
        String[][] empresas = controladorEmpresas.listEmpresas();
        if (empresas.length == 0) {
            System.out.println("  [!] No existen empresas registradas en el sistema.");
            return;
        }
        String sep = "*-------------*------------------------------*------------------------------*------------------*-----------*--------------*";
        System.out.println(sep);
        System.out.printf("| %-11s | %-28s | %-28s | %-16s | %-9s | %-12s |%n",
                "RUT EMPRESA", "NOMBRE", "URL", "NRO. TRIPULANTES", "NRO. BUSES", "NRO. VENTAS");
        System.out.println("|-------------+------------------------------+------------------------------+------------------+-----------+--------------|");
        for (String[] f : empresas) {
            System.out.printf("| %-11s | %-28s | %-28s | %-16s | %-9s | %-12s |%n",
                    f[0], f[1], f[2], f[3], f[4], "0");
            System.out.println("|-------------+------------------------------+------------------------------+------------------+-----------+--------------|");
        }
        System.out.println(sep);
    }

    private static void listLlegadasSalidasTerminal() {
        System.out.println("...::::: Listado de llegadas y salidas de un terminal :::::....");
        System.out.println();
        System.out.print("       Nombre terminal : ");
        String nombre = sc.nextLine().trim();
        System.out.print("  Fecha[dd/mm/yyyy] : ");
        LocalDate fecha = parsearFecha(sc.nextLine().trim());
        if (fecha == null) { System.out.println("  [!] Formato de fecha inválido."); return; }
        try {
            String[][] datos = controladorEmpresas.listLlegadasSalidasTerminal(nombre, fecha);
            if (datos.length == 0) {
                System.out.println("  [!] No hay llegadas ni salidas para esa fecha.");
                return;
            }
            String sep = "*----------------*--------*-------------*------------------------------*-----------------*";
            System.out.println(sep);
            System.out.printf("| %-14s | %-6s | %-11s | %-28s | %-15s |%n",
                    "LLEGADA/SALIDA", "HORA", "PATENTE BUS", "NOMBRE EMPRESA", "NRO. PASAJEROS");
            System.out.println("|----------------+--------+-------------+------------------------------+-----------------|");
            for (String[] f : datos) {
                System.out.printf("| %-14s | %-6s | %-11s | %-28s | %-15s |%n",
                        f[0], f[1], f[2], f[3], f[4]);
                System.out.println("|----------------+--------+-------------+------------------------------+-----------------|");
            }
            System.out.println(sep);
        } catch (SistemaVentaPasajesException e) {
            System.out.println("*** Error: " + e.getMessage() + " ***");
        }
    }
    private static void listVentasEmpresa() {
        System.out.println("...::::: Listado de ventas de una empresa :::::....");
        System.out.println();
        System.out.print("  R.U.T : ");
        Rut rut = Rut.of(sc.nextLine().trim());
        if (rut == null) { System.out.println("  [!] RUT inválido."); return; }
        try {
            String[][] ventas = controladorEmpresas.listVentasEmpresa(rut);
            if (ventas.length == 0) {
                System.out.println("  [!] La empresa no registra ventas.");
                return;
            }
            String sep = "*----------*---------*--------------*---------------*";
            System.out.println(sep);
            System.out.printf("| %-8s | %-7s | %-12s | %-13s |%n",
                    "FECHA", "TIPO", "MONTO PAGADO", "TIPO PAGO");
            System.out.println("|----------+---------+--------------+---------------|");
            for (String[] f : ventas) {
                System.out.printf("| %-8s | %-7s | %-12s | %-13s |%n",
                        f[0], f[1], f[2], f[3]);
                System.out.println("|----------+---------+--------------+---------------|");
            }
            System.out.println(sep);
        } catch (SistemaVentaPasajesException e) {
            System.out.println("  [!] " + e.getMessage());
        }
    }
    private static void createViaje() {
        System.out.println("...::::: Creación de un nuevo Viaje :::::....");
        System.out.println();
        System.out.print("      Fecha[dd/mm/yyyy] : ");
        LocalDate fecha = parsearFecha(sc.nextLine().trim());
        if (fecha == null) { System.out.println("  [!] Formato de fecha inválido."); return; }
        System.out.print("           Hora[hh:mm] : ");
        LocalTime hora = parsearHora(sc.nextLine().trim());
        if (hora == null) { System.out.println("  [!] Formato de hora inválido."); return; }
        System.out.print("               Precio : ");
        int precio;
        try {
            precio = Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("  [!] Precio inválido."); return;
        }
        System.out.print("  Duración (minutos) : ");
        int duracion;
        try {
            duracion = Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("  [!] Duración inválida."); return;
        }
        System.out.print("          Patente Bus : ");
        String patente = sc.nextLine().trim();
        System.out.print("  Nro. de conductores : ");
        int nroConductores = leerEnteroRango(1, 2);
        int totalTripulantes = 1 + nroConductores;
        String[] idTripulantes = new String[totalTripulantes];
        System.out.println("     :: Id modelo.Auxiliar ::");
        System.out.print("  Rut[1] o Pasaporte[2] : ");
        int tipoAux = leerEnteroRango(1, 2);
        IdPersona idAux = leerIdPersona(tipoAux);
        if (idAux == null) { System.out.println("  [!] Identificador inválido."); return; }
        idTripulantes[0] = idAux.toString();
        for (int i = 1; i <= nroConductores; i++) {
            System.out.println("     :: Id modelo.Conductor ::");
            System.out.print("  Rut[1] o Pasaporte[2] : ");
            int tipoCond = leerEnteroRango(1, 2);
            IdPersona idCond = leerIdPersona(tipoCond);
            if (idCond == null) { System.out.println("  [!] Identificador inválido."); return; }
            idTripulantes[i] = idCond.toString();
        }
        System.out.print("  Nombre comuna salida : ");
        String comunaSalida = sc.nextLine().trim();
        System.out.print("  Nombre comuna llegada : ");
        String comunaLlegada = sc.nextLine().trim();
        String[] comunas = {comunaSalida, comunaLlegada};
        try {
            sistema.createViaje(fecha, hora, precio, duracion, patente, idTripulantes, comunas);
            System.out.println();
            System.out.println("...::::: Viaje guardado exitosamente :::::....");
        } catch (SistemaVentaPasajesException e) {
            System.out.println("  [!] " + e.getMessage());
        }
    }
    private static void vendePasajes() {
        System.out.println("       ...::::: Venta de pasajes :::::....");
        System.out.println();
        System.out.println(":::: Datos de la Venta");
        System.out.print("         ID Documento : ");
        String idDoc = sc.nextLine().trim();
        System.out.print("Tipo documento: [1] Boleta [2] Factura : ");
        int selTipo = leerEnteroRango(1, 2);
        TipoDocumento tipo = (selTipo == 1) ? TipoDocumento.BOLETA : TipoDocumento.FACTURA;
        System.out.print("  Fecha de viaje[dd/mm/yyyy] : ");
        LocalDate fechaViaje = parsearFecha(sc.nextLine().trim());
        if (fechaViaje == null) { System.out.println("  [!] Fecha inválida."); return; }
        System.out.print("        Origen (comuna) : ");
        String comunaSalida = sc.nextLine().trim();
        System.out.print("       Destino (comuna) : ");
        String comunaLlegada = sc.nextLine().trim();
        System.out.println();
        System.out.println(":::: Datos del cliente");
        System.out.print("  Rut[1] o Pasaporte[2] : ");
        int tipoIdCliente = leerEnteroRango(1, 2);
        IdPersona idCliente = leerIdPersona(tipoIdCliente);
        if (idCliente == null) { System.out.println("  [!] Identificador inválido."); return; }
        System.out.println();
        System.out.println(":::: Pasajes a vender");
        System.out.print("  Cantidad de pasajes : ");
        int cantPasajes;
        try {
            cantPasajes = Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("  [!] Cantidad inválida."); return;
        }
        LocalDate fechaVenta = LocalDate.now();
        try {
            sistema.iniciaVenta(idDoc, tipo, fechaVenta, idCliente, comunaSalida, comunaLlegada, cantPasajes);
        } catch (SistemaVentaPasajesException e) {
            System.out.println("  [!] " + e.getMessage());
            return;
        }
        Optional<Cliente> cliente = sistema.findCliente(idCliente);
        cliente.ifPresent(c -> System.out.println("  Nombre Cliente : " + c.getNombreCompleto()));
        String[][] horarios = sistema.getHorariosDisponibles(fechaViaje, comunaSalida, comunaLlegada, cantPasajes);
        if (horarios.length == 0) {
            System.out.println("  [!] No hay viajes disponibles para esa fecha y ruta.");
            return;
        }
        System.out.println();
        System.out.println(":::: Listado de horarios disponibles");
        System.out.println("     *----------*----------*----------*----------*");
        System.out.printf("     | %-8s | %-8s | %-8s | %-8s |%n", "BUS", "SALIDA", "VALOR", "ASIENTOS");
        System.out.println("     |----------+----------+----------+----------|");
        for (int i = 0; i < horarios.length; i++) {
            String[] h = horarios[i];
            System.out.printf("  %2d | %-8s | %-8s | $%-7s | %-8s |%n",
                    i + 1, h[0], h[1], h[2], h[3]);
            System.out.println("     |----------+----------+----------+----------|");
        }
        System.out.println("     *----------*----------*----------*----------*");
        System.out.print("  Seleccione viaje en [1.." + horarios.length + "] : ");
        int selViaje = leerEnteroRango(1, horarios.length);
        String[] viajeElegido = horarios[selViaje - 1];
        LocalTime horaViaje = parsearHora(viajeElegido[1]);
        String[] asientosDisp = sistema.listAsientosDeViaje(fechaViaje, horaViaje, viajeElegido[0]);
        System.out.println();
        System.out.println(":::: Asientos disponibles para el viaje seleccionado");
        mostrarAsientos(asientosDisp);
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
            System.out.println("  [!] Asientos inválidos."); return;
        }
        for (int i = 0; i < cantPasajes; i++) {
            System.out.println();
            System.out.println(":::: Datos pasajeros " + (i + 1));
            System.out.print("  Rut[1] o Pasaporte[2] : ");
            int tipoIdPas = leerEnteroRango(1, 2);
            IdPersona idPasajero = leerIdPersona(tipoIdPas);
            if (idPasajero == null) { System.out.println("  [!] Identificador inválido."); return; }
            if (sistema.findPasajero(idPasajero).isEmpty()) {
                System.out.println("  Pasajero no encontrado. Ingrese sus datos:");
                System.out.print("     Sr.[1] o Sra.[2] : ");
                int tratPas = leerEnteroRango(1, 2);
                Tratamiento tPas = (tratPas == 1) ? Tratamiento.SR : Tratamiento.SRA;
                System.out.print("           Nombres : ");
                String nomPas = sc.nextLine().trim();
                System.out.print("  Apellido Paterno : ");
                String apPPas = sc.nextLine().trim();
                System.out.print("  Apellido Materno : ");
                String apMPas = sc.nextLine().trim();
                System.out.print("          Telefono : ");
                String telPas = sc.nextLine().trim();
                System.out.print("   Nombre Contacto : ");
                String nomCont = sc.nextLine().trim();
                System.out.print("  Ap. Pat. Contacto : ");
                String apPCont = sc.nextLine().trim();
                System.out.print("  Ap. Mat. Contacto : ");
                String apMCont = sc.nextLine().trim();
                System.out.print("  Sr.[1] o Sra.[2] (Contacto) : ");
                int tratCont = leerEnteroRango(1, 2);
                Tratamiento tCont = (tratCont == 1) ? Tratamiento.SR : Tratamiento.SRA;
                System.out.print("  Telefono Contacto : ");
                String telCont = sc.nextLine().trim();
                Nombre nombrePas = new Nombre(tPas, nomPas, apPPas, apMPas);
                Nombre nombreCont = new Nombre(tCont, nomCont, apPCont, apMCont);
                try {
                    sistema.createPasajero(idPasajero, nombrePas, telPas, nombreCont, telCont);
                } catch (SistemaVentaPasajesException e) {
                    System.out.println("  [!] " + e.getMessage()); return;
                }
            }
            try {
                sistema.vendePasaje(idDoc, tipo, fechaViaje, horaViaje,
                        viajeElegido[0], asientosElegidos[i], idPasajero);
                System.out.println(":::: Pasaje agregado exitosamente");
            } catch (SistemaVentaPasajesException e) {
                System.out.println("  [!] " + e.getMessage()); return;
            }
        }
        Optional<Integer> monto = sistema.getMontoVenta(idDoc, tipo);
        monto.ifPresent(m -> System.out.println("\n:::: Monto total de la venta: $" + m));
        System.out.println();
        System.out.println(":::: Pago de la venta");
        System.out.print("  Efectivo[1] o Tarjeta[2] : ");
        int tipoPago = leerEnteroRango(1, 2);
        try {
            if (tipoPago == 1) {
                sistema.pagaVenta(idDoc, tipo);
            } else {
                System.out.print("  Número de tarjeta : ");
                long nroTarjeta;
                try {
                    nroTarjeta = Long.parseLong(sc.nextLine().trim());
                } catch (NumberFormatException e) {
                    System.out.println("  [!] Número de tarjeta inválido."); return;
                }
                sistema.pagaVenta(idDoc, tipo, nroTarjeta);
            }
            System.out.println();
            System.out.println("...::::: Venta realizada exitosamente :::::....");
        } catch (SistemaVentaPasajesException e) {
            System.out.println("  [!] " + e.getMessage());
        }
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
        String[][] pasajeros = sistema.listPasajerosViaje(fecha, hora, patente);
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
        if (fecha == null) { System.out.println("  [!] Formato de fecha inválido."); return; }
        String[][] ventas = sistema.listVentas(fecha);
        if (ventas.length == 0) {
            System.out.println("  [!] No existen ventas registradas para esa fecha.");
            return;
        }
        String sep = "*-------------*-----------*--------------*----------------------------------*---------------*---------------*";
        System.out.println(sep);
        System.out.printf("| %-11s | %-9s | %-12s | %-32s | %-13s | %-13s |%n",
                "ID DOCUMENT", "TIPO DOCU", "FECHA", "RUT/PASAPORTE | CLIENTE", "CANT BOLETOS", "TOTAL VENTA");
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
        String sep = "*------------*----------*----------*---------*-----------*----------*----------*----------*";
        System.out.println(sep);
        System.out.printf("| %-10s | %-8s | %-8s | %-7s | %-9s | %-8s | %-8s | %-8s |%n",
                "FECHA", "HORA SALE", "HORA LLEGA", "PRECIO", "ASIENTOS D.", "PATENTE", "ORIGEN", "DESTINO");
        System.out.println("|------------+----------+----------+---------+-----------+----------+----------+----------|");
        for (String[] f : viajes) {
            System.out.printf("| %-10s | %-8s | %-10s | %-7s | %-11s | %-8s | %-8s | %-8s |%n",
                    f[0], f[1], f[2], f[3], f[4], f[5], f[6], f[7]);
            System.out.println("|------------+----------+----------+---------+-----------+----------+----------+----------|");
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

    private static void mostrarAsientos(String[] asientos) {
        int total = asientos.length;
        System.out.println("  *---*---*---*---*");
        int col = 0;
        StringBuilder fila = new StringBuilder("  |");
        for (int i = 0; i < total; i++) {
            String celda = asientos[i].equals("*")
                    ? " *"
                    : String.format("%2s", asientos[i]);

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
            while (col < 4) {
                fila.append("   |");
                col++;
            }
            System.out.println(fila.toString());
        }
        System.out.println("  *---*---*---*---*");
    }

    private static String formatearFecha(LocalDate fecha) {
        return String.format("%02d/%02d/%04d",
                fecha.getDayOfMonth(), fecha.getMonthValue(), fecha.getYear());
    }
}
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Optional;

//Autores: Juan Bustos - Nicolás Figueroa
public class SistemaVentaPasajes {
    private static SistemaVentaPasajes instancia;
    private ArrayList<Cliente> clientes;
    private ArrayList<Pasajero> pasajeros;
    private ArrayList<Viaje> viajes;
    private ArrayList<Venta> ventas;

    private SistemaVentaPasajes() {
        clientes = new ArrayList<>();
        pasajeros = new ArrayList<>();
        viajes = new ArrayList<>();
        ventas = new ArrayList<>();
    }
    public static SistemaVentaPasajes getInstance() {
        if (instancia == null) {
            instancia = new SistemaVentaPasajes();
        }
        return instancia;
    }

    public void createCliente(IdPersona idPersona, Nombre nom, String fono, String email) {
        if (findCliente(idPersona).isPresent()) {
            throw new SistemaVentaPasajesException("Ya existe cliente con el id indicado");
        }
        Cliente c = new Cliente(idPersona, nom, email);
        c.setTelefono(fono);
        clientes.add(c);
    }

    public void createPasajero(IdPersona idPersona, Nombre nom, String fono,
                               Nombre nomContacto, String fonoContacto) {
        if (findPasajero(idPersona).isPresent()) {
            throw new SistemaVentaPasajesException("Ya existe pasajero con el id indicado");
        }
        Pasajero p = new Pasajero(idPersona, nom);
        p.setTelefono(fono);
        p.setNomContacto(nomContacto);
        p.setFonoContacto(fonoContacto);
        pasajeros.add(p);
    }

    public void createViaje(LocalDate fecha, LocalTime hora, int precio, int duracion,
                            String patBus, String[] idTripulantes, String[] comunas) {
        ControladorEmpresas ce = ControladorEmpresas.getInstance();

        Optional<Bus> bus = ce.findBus(patBus);
        if (bus.isEmpty()) {
            throw new SistemaVentaPasajesException("No existe bus con la patente indicada");
        }

        if (findViaje(fecha, hora, patBus).isPresent()) {
            throw new SistemaVentaPasajesException("Ya existe viaje con fecha, hora y patente de bus indicados");
        }

        Optional<Terminal> termSalida = ce.findTerminalComuna(comunas[0]);
        if (termSalida.isEmpty()) {
            throw new SistemaVentaPasajesException("No existe terminal de salida en la comuna indicada");
        }

        Optional<Terminal> termLlegada = ce.findTerminalComuna(comunas[1]);
        if (termLlegada.isEmpty()) {
            throw new SistemaVentaPasajesException("No existe terminal de llegada en la comuna indicada");
        }

        Rut rutEmp = bus.get().getEmpresa().getRut();

        Optional<Auxiliar> auxiliar = ce.findAuxiliar(Rut.of(idTripulantes[0]), rutEmp);
        if (auxiliar.isEmpty()) {
            throw new SistemaVentaPasajesException("No existe auxiliar con el id indicado en la empresa con el rut indicado");
        }

        Viaje v = new Viaje(fecha, hora, precio, duracion, bus.get(),
                termSalida.get(), termLlegada.get(), auxiliar.get());

        for (int i = 1; i < idTripulantes.length; i++) {
            Optional<Conductor> conductor = ce.findConductor(Rut.of(idTripulantes[i]), rutEmp);
            if (conductor.isEmpty()) {
                throw new SistemaVentaPasajesException("No existe conductor con el id indicado en la empresa con el rut indicado");
            }
            v.addConductor(conductor.get());
            conductor.get().addViaje(v);
        }

        auxiliar.get().addViaje(v);
        bus.get().addViaje(v);
        viajes.add(v);
    }

    public String[][] getHorariosDisponibles(LocalDate fecha, String comunaSalida,
                                             String comunaLlegada, int nroPasajes) {
        ArrayList<Viaje> disponibles = new ArrayList<>();
        for (Viaje v : viajes) {
            if (v.getFecha().equals(fecha)
                    && v.getTerminalSalida() != null
                    && v.getTerminalLlegada() != null
                    && v.getTerminalSalida().getComuna().equalsIgnoreCase(comunaSalida)
                    && v.getTerminalLlegada().getComuna().equalsIgnoreCase(comunaLlegada)
                    && v.existeDisponibilidad(nroPasajes)) {
                disponibles.add(v);
            }
        }
        String[][] resultado = new String[disponibles.size()][4];
        for (int i = 0; i < disponibles.size(); i++) {
            Viaje v = disponibles.get(i);
            resultado[i][0] = v.getBus().getPatente();
            resultado[i][1] = v.getHora().toString();
            resultado[i][2] = String.valueOf(v.getPrecio());
            resultado[i][3] = String.valueOf(v.getNroAsientosDisponibles());
        }
        return resultado;
    }

    public String[] listAsientosDeViaje(LocalDate fecha, LocalTime hora, String patBus) {
        Optional<Viaje> v = findViaje(fecha, hora, patBus);
        if (v.isEmpty()) return new String[0];
        return v.get().getAsientos();
    }

    public void iniciaVenta(String idDoc, TipoDocumento tipo, LocalDate fechaVenta,
                            IdPersona idCliente, String comunaSalida, String comunaLlegada,
                            int nroPasajes) {
        if (findVentaIdDocumento(idDoc, tipo).isPresent()) {
            throw new SistemaVentaPasajesException("Ya existe venta con el id y tipo de documento indicados");
        }

        Optional<Cliente> cliente = findCliente(idCliente);
        if (cliente.isEmpty()) {
            throw new SistemaVentaPasajesException("No existe cliente con id indicado");
        }

        boolean hayDisponibilidad = false;
        for (Viaje v : viajes) {
            if (v.getTerminalSalida() != null && v.getTerminalLlegada() != null
                    && v.getTerminalSalida().getComuna().equalsIgnoreCase(comunaSalida)
                    && v.getTerminalLlegada().getComuna().equalsIgnoreCase(comunaLlegada)
                    && v.existeDisponibilidad(nroPasajes)) {
                hayDisponibilidad = true;
                break;
            }
        }
        if (!hayDisponibilidad) {
            throw new SistemaVentaPasajesException("No existen viajes disponibles en la fecha y con terminales en las comunas de salida y llegada indicados");
        }

        Venta venta = new Venta(idDoc, tipo, fechaVenta, cliente.get());
        ventas.add(venta);
    }
    public void pagaVenta(String idDoc, TipoDocumento tipo) {
        Optional<Venta> venta = findVentaIdDocumento(idDoc, tipo);
        if (venta.isEmpty()) {
            throw new SistemaVentaPasajesException("No existe venta con el id y tipo de documento indicados");
        }
        boolean pagado = venta.get().pagaMonto();
        if (!pagado) {
            throw new SistemaVentaPasajesException("La venta ya fue pagada");
        }
    }

    public void pagaVenta(String idDoc, TipoDocumento tipo, long nroTarjeta) {
        Optional<Venta> venta = findVentaIdDocumento(idDoc, tipo);
        if (venta.isEmpty()) {
            throw new SistemaVentaPasajesException("No existe venta con el id y tipo de documento indicados");
        }
        boolean pagado = venta.get().pagaMonto(nroTarjeta);
        if (!pagado) {
            throw new SistemaVentaPasajesException("La venta ya fue pagada");
        }
    }

    public Optional<Integer> getMontoVenta(String idDocumento, TipoDocumento tipo) {
        Optional<Venta> v = findVentaIdDocumento(idDocumento, tipo);
        return v.map(Venta::getMonto);
    }

    public Optional<String> getNombrePasajero(IdPersona idPasajero) {
        Optional<Pasajero> p = findPasajero(idPasajero);
        return p.map(pasajero -> pasajero.getNombreCompleto().toString());
    }

    public void vendePasaje(String idDoc, TipoDocumento tipo, LocalDate fecha,
                            LocalTime hora, String patBus, int asiento, IdPersona idPasajero) {
        Optional<Venta> venta = findVentaIdDocumento(idDoc, tipo);
        if (venta.isEmpty()) {
            throw new SistemaVentaPasajesException("No existe venta con el id y tipo de documento indicados");
        }
        Optional<Pasajero> pasajero = findPasajero(idPasajero);
        if (pasajero.isEmpty()) {
            throw new SistemaVentaPasajesException("No existe pasajero con el id indicado");
        }
        Optional<Viaje> viaje = findViaje(fecha, hora, patBus);
        if (viaje.isEmpty()) {
            throw new SistemaVentaPasajesException("No existe viaje con la fecha, hora y patente de bus indicados");
        }
        venta.get().createPasaje(asiento, viaje.get(), pasajero.get());
    }

    public String[][] listVentas(LocalDate fecha) {
        ArrayList<Venta> ventasFecha = new ArrayList<>();
        for (Venta v : ventas) {
            if (v.getFecha().equals(fecha)) ventasFecha.add(v);
        }
        String[][] resultado = new String[ventasFecha.size()][7];
        for (int i = 0; i < ventasFecha.size(); i++) {
            Venta v = ventasFecha.get(i);
            resultado[i][0] = v.getIdDocumento();
            resultado[i][1] = v.getTipo().toString();
            resultado[i][2] = formatearFecha(v.getFecha());
            resultado[i][3] = v.getCliente().getIdPersona().toString();
            resultado[i][4] = v.getCliente().getNombreCompleto().toString();
            resultado[i][5] = String.valueOf(v.getPasajes().length);
            resultado[i][6] = "$" + v.getMonto();
        }
        return resultado;
    }

    public String[][] listViajes() {
        String[][] resultado = new String[viajes.size()][8];
        for (int i = 0; i < viajes.size(); i++) {
            Viaje v = viajes.get(i);
            resultado[i][0] = formatearFecha(v.getFecha());
            resultado[i][1] = v.getHora().toString();
            resultado[i][2] = v.getFechaHoraTermino().toLocalTime().toString();
            resultado[i][3] = "$" + v.getPrecio();
            resultado[i][4] = String.valueOf(v.getNroAsientosDisponibles());
            resultado[i][5] = v.getBus().getPatente();
            resultado[i][6] = v.getTerminalSalida() != null ? v.getTerminalSalida().getComuna() : "";
            resultado[i][7] = v.getTerminalLlegada() != null ? v.getTerminalLlegada().getComuna() : "";
        }
        return resultado;
    }
    public String[][] listPasajerosViaje(LocalDate fecha, LocalTime hora, String patenteBus) {
        Optional<Viaje> v = findViaje(fecha, hora, patenteBus);
        if (v.isEmpty()) {
            throw new SistemaVentaPasajesException("No existe viaje con la fecha, hora y patente de bus indicados");
        }
        ArrayList<Pasaje> pasajes = v.get().getPasajes();
        String[][] resultado = new String[pasajes.size()][5];
        for (int i = 0; i < pasajes.size(); i++) {
            Pasaje p = pasajes.get(i);
            Pasajero pasajero = p.getPasajero();
            resultado[i][0] = String.valueOf(p.getAsiento());
            resultado[i][1] = pasajero.getIdPersona().toString();
            resultado[i][2] = pasajero.getNombreCompleto().toString();
            resultado[i][3] = pasajero.getNomContacto() != null ? pasajero.getNomContacto().toString() : "";
            resultado[i][4] = pasajero.getFonoContacto() != null ? pasajero.getFonoContacto() : "";
        }
        return resultado;
    }
    public Optional<Cliente> findCliente(IdPersona id) {
        for (Cliente c : clientes) {
            if (c.getIdPersona().equals(id)) return Optional.of(c);
        }
        return Optional.empty();
    }

    public Optional<Venta> findVentaIdDocumento(String idDocumento, TipoDocumento tipo) {
        for (Venta v : ventas) {
            if (v.getIdDocumento().equals(idDocumento) && v.getTipo() == tipo) return Optional.of(v);
        }
        return Optional.empty();
    }

    public Optional<Viaje> findViaje(LocalDate fecha, LocalTime hora, String patBus) {
        for (Viaje v : viajes) {
            if (v.getFecha().equals(fecha)
                    && v.getHora().equals(hora)
                    && v.getBus().getPatente().equalsIgnoreCase(patBus)) {
                return Optional.of(v);
            }
        }
        return Optional.empty();
    }

    public Optional<Pasajero> findPasajero(IdPersona id) {
        for (Pasajero p : pasajeros) {
            if (p.getIdPersona().equals(id)) return Optional.of(p);
        }
        return Optional.empty();
    }
    // METODO CREADO PARA SIMPLIFICAR EL TRABAJO
    private String formatearFecha(LocalDate fecha) {
        return String.format("%02d/%02d/%04d",
                fecha.getDayOfMonth(), fecha.getMonthValue(), fecha.getYear());
    }
}

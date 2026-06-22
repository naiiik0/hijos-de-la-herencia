package controlador;

import excepciones.SVPException;
import modelo.*;
import utilidades.IdPersona;
import utilidades.Nombre;
import utilidades.Rut;

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
            throw new SVPException("Ya existe cliente con el id indicado");
        }
        Cliente c = new Cliente(idPersona, nom, email);
        c.setTelefono(fono);
        clientes.add(c);
    }

    public void createPasajero(IdPersona idPersona, Nombre nom, String fono,
                               Nombre nomContacto, String fonoContacto) {
        if (findPasajero(idPersona).isPresent()) {
            throw new SVPException("Ya existe pasajero con el id indicado");
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
            throw new SVPException("No existe bus con la patente indicada");
        }

        if (findViaje(fecha, hora, patBus).isPresent()) {
            throw new SVPException("Ya existe viaje con fecha, hora y patente de bus indicados");
        }

        Optional<Terminal> termSalida = ce.findTerminalComuna(comunas[0]);
        if (termSalida.isEmpty()) {
            throw new SVPException("No existe terminal de salida en la comuna indicada");
        }

        Optional<Terminal> termLlegada = ce.findTerminalComuna(comunas[1]);
        if (termLlegada.isEmpty()) {
            throw new SVPException("No existe terminal de llegada en la comuna indicada");
        }

        Rut rutEmp = bus.get().getEmpresa().getRut();

        Optional<Auxiliar> auxiliar = ce.findAuxiliar(Rut.of(idTripulantes[0]), rutEmp);
        if (auxiliar.isEmpty()) {
            throw new SVPException("No existe auxiliar con el id indicado en la empresa con el rut indicado");
        }

        Viaje v = new Viaje(fecha, hora, precio, duracion, bus.get(),
                termSalida.get(), termLlegada.get(), auxiliar.get());

        for (int i = 1; i < idTripulantes.length; i++) {
            Optional<Conductor> conductor = ce.findConductor(Rut.of(idTripulantes[i]), rutEmp);
            if (conductor.isEmpty()) {
                throw new SVPException("No existe conductor con el id indicado en la empresa con el rut indicado");
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
        return viajes.stream()
                .filter(v -> v.getFecha().equals(fecha)
                        && v.getTerminalSalida() != null
                        && v.getTerminalLlegada() != null
                        && v.getTerminalSalida().getComuna().equalsIgnoreCase(comunaSalida)
                        && v.getTerminalLlegada().getComuna().equalsIgnoreCase(comunaLlegada)
                        && v.existeDisponibilidad(nroPasajes))
                .map(v -> new String[]{
                        v.getBus().getPatente(),
                        v.getHora().toString(),
                        String.valueOf(v.getPrecio()),
                        String.valueOf(v.getNroAsientosDisponibles())
                })
                .toArray(String[][]::new);
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
            throw new SVPException("Ya existe venta con el id y tipo de documento indicados");
        }

        Optional<Cliente> cliente = findCliente(idCliente);
        if (cliente.isEmpty()) {
            throw new SVPException("No existe cliente con id indicado");
        }

        boolean hayDisponibilidad = viajes.stream()
                .anyMatch(v -> v.getTerminalSalida() != null
                        && v.getTerminalLlegada() != null
                        && v.getTerminalSalida().getComuna().equalsIgnoreCase(comunaSalida)
                        && v.getTerminalLlegada().getComuna().equalsIgnoreCase(comunaLlegada)
                        && v.existeDisponibilidad(nroPasajes));
        if (!hayDisponibilidad) {
            throw new SVPException("No existen viajes disponibles en la fecha y con terminales en las comunas de salida y llegada indicados");
        }

        Venta venta = new Venta(idDoc, tipo, fechaVenta, cliente.get());
        ventas.add(venta);
    }
    public void pagaVenta(String idDoc, TipoDocumento tipo) {
        Optional<Venta> venta = findVentaIdDocumento(idDoc, tipo);
        if (venta.isEmpty()) {
            throw new SVPException("No existe venta con el id y tipo de documento indicados");
        }
        boolean pagado = venta.get().pagaMonto();
        if (!pagado) {
            throw new SVPException("La venta ya fue pagada");
        }
    }

    public void pagaVenta(String idDoc, TipoDocumento tipo, long nroTarjeta) {
        Optional<Venta> venta = findVentaIdDocumento(idDoc, tipo);
        if (venta.isEmpty()) {
            throw new SVPException("No existe venta con el id y tipo de documento indicados");
        }
        boolean pagado = venta.get().pagaMonto(nroTarjeta);
        if (!pagado) {
            throw new SVPException("La venta ya fue pagada");
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
            throw new SVPException("No existe venta con el id y tipo de documento indicados");
        }
        Optional<Pasajero> pasajero = findPasajero(idPasajero);
        if (pasajero.isEmpty()) {
            throw new SVPException("No existe pasajero con el id indicado");
        }
        Optional<Viaje> viaje = findViaje(fecha, hora, patBus);
        if (viaje.isEmpty()) {
            throw new SVPException("No existe viaje con la fecha, hora y patente de bus indicados");
        }
        venta.get().createPasaje(asiento, viaje.get(), pasajero.get());
    }

    public String[][] listVentas(LocalDate fecha) {
        return ventas.stream()
                .filter(v -> v.getFecha().equals(fecha))
                .map(v -> new String[]{
                        v.getIdDocumento(),
                        v.getTipo().toString(),
                        formatearFecha(v.getFecha()),
                        v.getCliente().getIdPersona().toString(),
                        v.getCliente().getNombreCompleto().toString(),
                        String.valueOf(v.getPasajes().length),
                        "$" + v.getMonto()
                })
                .toArray(String[][]::new);
    }

    public String[][] listViajes() {
        return viajes.stream()
                .map(v -> new String[]{
                        formatearFecha(v.getFecha()),
                        v.getHora().toString(),
                        v.getFechaHoraTermino().toLocalTime().toString(),
                        "$" + v.getPrecio(),
                        String.valueOf(v.getNroAsientosDisponibles()),
                        v.getBus().getPatente(),
                        v.getTerminalSalida() != null ? v.getTerminalSalida().getComuna() : "",
                        v.getTerminalLlegada() != null ? v.getTerminalLlegada().getComuna() : ""
                })
                .toArray(String[][]::new);
    }
    public String[][] listPasajerosViaje(LocalDate fecha, LocalTime hora, String patenteBus) {
        Optional<Viaje> v = findViaje(fecha, hora, patenteBus);
        if (v.isEmpty()) {
            throw new SVPException("No existe viaje con la fecha, hora y patente de bus indicados");
        }
        return v.get().getPasajes().stream()
                .map(p -> new String[]{
                        String.valueOf(p.getAsiento()),
                        p.getPasajero().getIdPersona().toString(),
                        p.getPasajero().getNombreCompleto().toString(),
                        p.getPasajero().getNomContacto() != null ? p.getPasajero().getNomContacto().toString() : "",
                        p.getPasajero().getFonoContacto() != null ? p.getPasajero().getFonoContacto() : ""
                })
                .toArray(String[][]::new);
    }
    public Optional<Cliente> findCliente(IdPersona id) {
        return clientes.stream().filter(c -> c.getIdPersona().equals(id)).findFirst();
    }

    public Optional<Venta> findVentaIdDocumento(String idDocumento, TipoDocumento tipo) {
        return ventas.stream()
                .filter(v -> v.getIdDocumento().equals(idDocumento) && v.getTipo() == tipo)
                .findFirst();
    }

    public Optional<Viaje> findViaje(LocalDate fecha, LocalTime hora, String patBus) {
        return viajes.stream()
                .filter(v -> v.getFecha().equals(fecha)
                        && v.getHora().equals(hora)
                        && v.getBus().getPatente().equalsIgnoreCase(patBus))
                .findFirst();
    }

    public Optional<Pasajero> findPasajero(IdPersona id) {
        return pasajeros.stream().filter(p -> p.getIdPersona().equals(id)).findFirst();
    }

    public void readDatosIniciales() throws SVPException {
        Object[] objetos = persistencia.IOSVP.getInstance().readDatosIniciales();

        this.clientes.clear();
        this.pasajeros.clear();
        this.viajes.clear();

        for (Object obj : objetos) {
            if (obj instanceof modelo.Cliente) {
                this.clientes.add((modelo.Cliente) obj);
            } else if (obj instanceof modelo.Pasajero) {
                this.pasajeros.add((modelo.Pasajero) obj);
            } else if (obj instanceof modelo.Viaje) {
                this.viajes.add((modelo.Viaje) obj);
            }
        }
        ControladorEmpresas.getInstance().setDatosIniciales(objetos);
    }

    public void generatePasajesVenta(String idDocumento, TipoDocumento tipo) throws SVPException {
        Optional<Venta> venta = findVentaIdDocumento(idDocumento, tipo);
        if (venta.isEmpty()) {
            throw new SVPException("No existe venta con el id y tipo de documento indicados");
        }
        String nombreArchivo = idDocumento + tipo.toString().toLowerCase() + ".txt";
        persistencia.IOSVP.getInstance().savePasajesDeVenta(venta.get().getPasajes(), nombreArchivo);
    }

    public void saveDatosSistema() throws SVPException {
        Object[] controladores = new Object[]{ControladorEmpresas.getInstance(), this};
        persistencia.IOSVP.getInstance().saveControladores(controladores);
    }

    public void readDatosSistema() throws SVPException {
        Object[] controladores = persistencia.IOSVP.getInstance().readControladores();
        instancia = (SistemaVentaPasajes) controladores[1];
        ControladorEmpresas instEmpresas = (ControladorEmpresas) controladores[0];
        instEmpresas.setInstanciaPersistente(instEmpresas);
    }

    // METODO CREADO PARA SIMPLIFICAR EL TRABAJO
    private String formatearFecha(LocalDate fecha) {
        return String.format("%02d/%02d/%04d",
                fecha.getDayOfMonth(), fecha.getMonthValue(), fecha.getYear());
    }
}

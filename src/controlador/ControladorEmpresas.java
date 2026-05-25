package controlador;
import excepciones.SistemaVentaPasajesException;
import modelo.*;
import utilidades.Rut;
import utilidades.IdPersona;
import utilidades.Nombre;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

public class ControladorEmpresas {

    private static ControladorEmpresas instancia;

    private ArrayList<Empresa> empresas;
    private ArrayList<Bus> buses;
    private ArrayList<Terminal> terminales;

    private ControladorEmpresas() {
        empresas = new ArrayList<>();
        buses = new ArrayList<>();
        terminales = new ArrayList<>();
    }

    public static ControladorEmpresas getInstance() {
        if (instancia == null) {
            instancia = new ControladorEmpresas();
        }
        return instancia;
    }

    public void createEmpresa(Rut rut, String nombre, String url) {
        if (findEmpresa(rut).isPresent()) {
            throw new SistemaVentaPasajesException("Ya existe empresa con el rut indicado");
        }
        empresas.add(new Empresa(rut, nombre, url));
    }

    public void createBus(String patente, String marca, String modelo, int nroAsientos, Rut rutEmp) {
        Optional<Empresa> emp = findEmpresa(rutEmp);
        if (emp.isEmpty()) {
            throw new SistemaVentaPasajesException("No existe empresa con el rut indicado");
        }
        if (findBus(patente).isPresent()) {
            throw new SistemaVentaPasajesException("Ya existe bus con la patente indicada");
        }
        Bus b = new Bus(patente, marca, modelo, nroAsientos, emp.get());
        buses.add(b);
        emp.get().addBus(b);
    }

    public void createTerminal(String nombre, Direccion direccion) {
        if (findTerminalNombre(nombre).isPresent()) {
            throw new SistemaVentaPasajesException("Ya existe terminal con el nombre indicado");
        }
        if (findTerminalComuna(direccion.getComuna()).isPresent()) {
            throw new SistemaVentaPasajesException("Ya existe terminal en la comuna indicada");
        }
        terminales.add(new Terminal(nombre, direccion));
    }

    public void hireConductorForEmpresa(Rut rutEmp, IdPersona id, Nombre nom, Direccion dir) {
        Optional<Empresa> emp = findEmpresa(rutEmp);
        if (emp.isEmpty()) {
            throw new SistemaVentaPasajesException("No existe empresa con el rut indicado");
        }
        boolean contratado = emp.get().addConductor(id, nom, dir);
        if (!contratado) {
            throw new SistemaVentaPasajesException("Ya está contratado conductor/auxiliar con el id dado en la empresa señalada");
        }
    }

    public void hireAuxiliarForEmpresa(Rut rutEmp, IdPersona id, Nombre nom, Direccion dir) {
        Optional<Empresa> emp = findEmpresa(rutEmp);
        if (emp.isEmpty()) {
            throw new SistemaVentaPasajesException("No existe empresa con el rut indicado");
        }
        boolean contratado = emp.get().addAuxiliar(id, nom, dir);
        if (!contratado) {
            throw new SistemaVentaPasajesException("Ya está contratado auxiliar/conductor con el id dado en la empresa señalada");
        }
    }

    public String[][] listEmpresas() {
        String[][] resultado = new String[empresas.size()][5];
        for (int i = 0; i < empresas.size(); i++) {
            Empresa e = empresas.get(i);
            resultado[i][0] = e.getRut().toString();
            resultado[i][1] = e.getNombre();
            resultado[i][2] = e.getUrl();
            resultado[i][3] = String.valueOf(e.getTripulantes().length);
            resultado[i][4] = String.valueOf(e.getBuses().length);
        }
        return resultado;
    }

    public String[][] listLlegadasSalidasTerminal(String nombreTerminal, LocalDate fecha) {
        Optional<Terminal> terminal = findTerminalNombre(nombreTerminal);
        if (terminal.isEmpty()) {
            throw new SistemaVentaPasajesException("No existe terminal con el nombre indicado");
        }
        Terminal t = terminal.get();
        ArrayList<String[]> filas = new ArrayList<>();
        for (Bus b : buses) {
            for (Viaje v : b.getViajes()) {
                boolean esSalida = v.getTerminalSalida() != null
                        && v.getTerminalSalida().getNombre().equalsIgnoreCase(t.getNombre())
                        && v.getFecha().equals(fecha);
                boolean esLlegada = v.getTerminalLlegada() != null
                        && v.getTerminalLlegada().getNombre().equalsIgnoreCase(t.getNombre())
                        && v.getFechaHoraTermino().toLocalDate().equals(fecha);
                if (esSalida) {
                    String[] fila = new String[5];
                    fila[0] = "Salida";
                    fila[1] = v.getHora().toString();
                    fila[2] = v.getBus().getPatente();
                    fila[3] = b.getEmpresa() != null ? b.getEmpresa().getNombre() : "";
                    fila[4] = String.valueOf(v.getPasajes().size());
                    filas.add(fila);
                }
                if (esLlegada) {
                    String[] fila = new String[5];
                    fila[0] = "Llegada";
                    fila[1] = v.getFechaHoraTermino().toLocalTime().toString();
                    fila[2] = v.getBus().getPatente();
                    fila[3] = b.getEmpresa() != null ? b.getEmpresa().getNombre() : "";
                    fila[4] = String.valueOf(v.getPasajes().size());
                    filas.add(fila);
                }
            }
        }
        return filas.toArray(new String[0][]);
    }

    public String[][] listVentasEmpresa(Rut rutEmp) {
        Optional<Empresa> emp = findEmpresa(rutEmp);
        if (emp.isEmpty()) {
            throw new SistemaVentaPasajesException("No existe empresa con el rut indicado");
        }
        Venta[] ventas = emp.get().getVentas();
        String[][] resultado = new String[ventas.length][4];
        for (int i = 0; i < ventas.length; i++) {
            Venta v = ventas[i];
            resultado[i][0] = formatearFecha(v.getFecha());
            resultado[i][1] = v.getTipo().toString().toLowerCase();
            resultado[i][2] = "$" + v.getMontoPagado();
            resultado[i][3] = v.getTipoPago() != null ? v.getTipoPago() : "-";
        }
        return resultado;
    }

    public Optional<Empresa> findEmpresa(Rut rut) {
        for (Empresa e : empresas) {
            if (e.getRut().equals(rut)) return Optional.of(e);
        }
        return Optional.empty();
    }

    public Optional<Bus> findBus(String patente) {
        for (Bus b : buses) {
            if (b.getPatente().equalsIgnoreCase(patente)) return Optional.of(b);
        }
        return Optional.empty();
    }

    public Optional<Terminal> findTerminalNombre(String nombre) {
        for (Terminal t : terminales) {
            if (t.getNombre().equalsIgnoreCase(nombre)) return Optional.of(t);
        }
        return Optional.empty();
    }

    public Optional<Terminal> findTerminalComuna(String comuna) {
        for (Terminal t : terminales) {
            if (t.getComuna().equalsIgnoreCase(comuna)) return Optional.of(t);
        }
        return Optional.empty();
    }

    public Optional<Conductor> findConductor(IdPersona id, Rut rutEmp) {
        Optional<Empresa> emp = findEmpresa(rutEmp);
        if (emp.isEmpty()) return Optional.empty();
        for (Tripulante t : emp.get().getTripulantes()) {
            if (t instanceof Conductor && t.getIdPersona().equals(id)) {
                return Optional.of((Conductor) t);
            }
        }
        return Optional.empty();
    }

    public Optional<Auxiliar> findAuxiliar(IdPersona id, Rut rutEmp) {
        Optional<Empresa> emp = findEmpresa(rutEmp);
        if (emp.isEmpty()) return Optional.empty();
        for (Tripulante t : emp.get().getTripulantes()) {
            if (t instanceof Auxiliar && t.getIdPersona().equals(id)) {
                return Optional.of((Auxiliar) t);
            }
        }
        return Optional.empty();
    }

    private String formatearFecha(LocalDate fecha) {
        return String.format("%02d/%02d/%04d",
                fecha.getDayOfMonth(), fecha.getMonthValue(), fecha.getYear());
    }
}
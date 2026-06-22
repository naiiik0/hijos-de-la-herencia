package controlador;
import excepciones.SVPException;
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
            throw new SVPException("Ya existe empresa con el rut indicado");
        }
        empresas.add(new Empresa(rut, nombre, url));
    }

    public void createBus(String patente, String marca, String modelo, int nroAsientos, Rut rutEmp) {
        Optional<Empresa> emp = findEmpresa(rutEmp);
        if (emp.isEmpty()) {
            throw new SVPException("No existe empresa con el rut indicado");
        }
        if (findBus(patente).isPresent()) {
            throw new SVPException("Ya existe bus con la patente indicada");
        }
        Bus b = new Bus(patente, marca, modelo, nroAsientos, emp.get());
        buses.add(b);
        emp.get().addBus(b);
    }

    public void createTerminal(String nombre, Direccion direccion) {
        if (findTerminalNombre(nombre).isPresent()) {
            throw new SVPException("Ya existe terminal con el nombre indicado");
        }
        if (findTerminalComuna(direccion.getComuna()).isPresent()) {
            throw new SVPException("Ya existe terminal en la comuna indicada");
        }
        terminales.add(new Terminal(nombre, direccion));
    }

    public void hireConductorForEmpresa(Rut rutEmp, IdPersona id, Nombre nom, Direccion dir) {
        Optional<Empresa> emp = findEmpresa(rutEmp);
        if (emp.isEmpty()) {
            throw new SVPException("No existe empresa con el rut indicado");
        }
        boolean contratado = emp.get().addConductor(id, nom, dir);
        if (!contratado) {
            throw new SVPException("Ya está contratado conductor con el id indicado en la empresa señalada");
        }
    }

    public void hireAuxiliarForEmpresa(Rut rutEmp, IdPersona id, Nombre nom, Direccion dir) {
        Optional<Empresa> emp = findEmpresa(rutEmp);
        if (emp.isEmpty()) {
            throw new SVPException("No existe empresa con el rut indicado");
        }
        boolean contratado = emp.get().addAuxiliar(id, nom, dir);
        if (!contratado) {
            throw new SVPException("Ya está contratado auxiliar con el id indicado en la empresa señalada");
        }
    }

    public String[][] listEmpresas() {
        String[][] resultado = new String[empresas.size()][6];
        for (int i = 0; i < empresas.size(); i++) {
            Empresa e = empresas.get(i);
            resultado[i][0] = e.getRut().toString();
            resultado[i][1] = e.getNombre();
            resultado[i][2] = e.getUrl();
            resultado[i][3] = String.valueOf(e.getTripulantes().length);
            resultado[i][4] = String.valueOf(e.getBuses().length);
            resultado[i][5] = String.valueOf(e.getVentas().length);
        }
        return resultado;
    }

    public String[][] listLlegadasSalidasTerminal(String nombreTerminal, LocalDate fecha) {
        Optional<Terminal> terminal = findTerminalNombre(nombreTerminal);
        if (terminal.isEmpty()) {
            throw new SVPException("No existe terminal con el nombre indicado");
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
            throw new SVPException("No existe empresa con el rut indicado");
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
        return empresas.stream()
                .filter(e -> e.getRut().equals(rut))
                .findFirst();
    }

    public Optional<Bus> findBus(String patente) {
        return buses.stream()
                .filter(b -> b.getPatente().equalsIgnoreCase(patente))
                .findFirst();
    }

    public Optional<Terminal> findTerminalNombre(String nombre) {
        return terminales.stream()
                .filter(t -> t.getNombre().equalsIgnoreCase(nombre))
                .findFirst();
    }

    public Optional<Terminal> findTerminalComuna(String comuna) {
        return terminales.stream()
                .filter(t -> t.getComuna().equalsIgnoreCase(comuna))
                .findFirst();
    }

    public Optional<Conductor> findConductor(IdPersona id, Rut rutEmp) {
        return findEmpresa(rutEmp)
                .stream()
                .flatMap(emp -> java.util.Arrays.stream(emp.getTripulantes()))
                .filter(t -> t instanceof Conductor)
                .filter(t -> t.getIdPersona().equals(id))
                .map(t -> (Conductor) t)
                .findFirst();
    }


    public Optional<Auxiliar> findAuxiliar(IdPersona id, Rut rutEmp) {
        return findEmpresa(rutEmp)
                .stream()
                .flatMap(emp -> java.util.Arrays.stream(emp.getTripulantes()))
                .filter(t -> t instanceof Auxiliar)
                .filter(t -> t.getIdPersona().equals(id))
                .map(t -> (Auxiliar) t)
                .findFirst();
    }


    private String formatearFecha(LocalDate fecha) {
        return String.format("%02d/%02d/%04d",
                fecha.getDayOfMonth(), fecha.getMonthValue(), fecha.getYear());
    }

    public void setDatosIniciales(Object[] objetos) {
        this.empresas.clear();
        this.terminales.clear();
        this.buses.clear();

        for (Object obj : objetos) {
            if (obj instanceof modelo.Empresa) {
                this.empresas.add((modelo.Empresa) obj);
            } else if (obj instanceof modelo.Terminal) {
                this.terminales.add((modelo.Terminal) obj);
            } else if (obj instanceof modelo.Bus) {
                this.buses.add((modelo.Bus) obj);
            }
        }
    }

    public void setInstanciaPersistente(ControladorEmpresas inst) {
        instancia = inst;
    }
}
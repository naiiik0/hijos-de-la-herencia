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
        return empresas.stream()
                .map(e -> new String[]{
                        e.getRut().toString(),
                        e.getNombre(),
                        e.getUrl(),
                        String.valueOf(e.getTripulantes().length),
                        String.valueOf(e.getBuses().length),
                        String.valueOf(e.getVentas().length)
                })
                .toArray(String[][]::new);
    }

    public String[][] listLlegadasSalidasTerminal(String nombreTerminal, LocalDate fecha) {
        Optional<Terminal> terminal = findTerminalNombre(nombreTerminal);
        if (terminal.isEmpty()) {
            throw new SVPException("No existe terminal con el nombre indicado");
        }
        Terminal t = terminal.get();
        return buses.stream()
                .flatMap(b -> b.getViajes().stream()
                        .flatMap(v -> {
                            java.util.List<String[]> filas = new java.util.ArrayList<>();
                            if (v.getTerminalSalida() != null
                                    && v.getTerminalSalida().getNombre().equalsIgnoreCase(t.getNombre())
                                    && v.getFecha().equals(fecha)) {
                                filas.add(new String[]{
                                        "Salida",
                                        v.getHora().toString(),
                                        v.getBus().getPatente(),
                                        b.getEmpresa() != null ? b.getEmpresa().getNombre() : "",
                                        String.valueOf(v.getPasajes().size())
                                });
                            }
                            if (v.getTerminalLlegada() != null
                                    && v.getTerminalLlegada().getNombre().equalsIgnoreCase(t.getNombre())
                                    && v.getFechaHoraTermino().toLocalDate().equals(fecha)) {
                                filas.add(new String[]{
                                        "Llegada",
                                        v.getFechaHoraTermino().toLocalTime().toString(),
                                        v.getBus().getPatente(),
                                        b.getEmpresa() != null ? b.getEmpresa().getNombre() : "",
                                        String.valueOf(v.getPasajes().size())
                                });
                            }
                            return filas.stream();
                        })
                )
                .toArray(String[][]::new);
    }

    public String[][] listVentasEmpresa(Rut rutEmp) {
        Optional<Empresa> emp = findEmpresa(rutEmp);
        if (emp.isEmpty()) {
            throw new SVPException("No existe empresa con el rut indicado");
        }
        return java.util.Arrays.stream(emp.get().getVentas())
                .map(v -> new String[]{
                        formatearFecha(v.getFecha()),
                        v.getTipo().toString().toLowerCase(),
                        "$" + v.getMontoPagado(),
                        v.getTipoPago() != null ? v.getTipoPago() : "-"
                })
                .toArray(String[][]::new);
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
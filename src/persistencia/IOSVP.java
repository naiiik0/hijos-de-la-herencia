package persistencia;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.time.*;
import java.util.Optional;
import excepciones.SVPException;
import modelo.*;
import utilidades.Nombre;
import utilidades.Rut;
import utilidades.Tratamiento;
import static java.lang.Integer.parseInt;

//Nicolas Figueroa, Sofia Lagos

public class IOSVP {

    private static IOSVP instancia;

    private IOSVP() {
    }
    public static IOSVP getInstance() {
        if (instancia == null) {
            instancia = new IOSVP();
        }
        return instancia;
    }
    public Object[] readDatosIniciales() throws SVPException {
        ArrayList<Object> objetos = new ArrayList<>();
        ArrayList<Empresa> empresas = new ArrayList<>();
        ArrayList<Terminal> terminales = new ArrayList<>();
        ArrayList<Bus> buses = new ArrayList<>();

        try (BufferedReader lector = new BufferedReader(new FileReader("SVPDatosIniciales.txt"))) {
            String linea;

            //pasajeros o clientes o ambos
            while ((linea = lector.readLine()) != null && !linea.equals("+")) {
                String[] p = linea.split(";");
                String tipo = p[0];
                Rut rut = Rut.of(p[1]);
                Tratamiento trat;
                if (p[2].equals("SR")) {
                    trat = Tratamiento.SR;
                } else {
                    trat = Tratamiento.SRA;
                }
                Nombre nombre = new Nombre(trat, p[3], p[4], p[5]);
                String fono = p[6];

                if (tipo.equals("C")) {
                    Cliente c = new Cliente(rut, nombre, p[7]);
                    objetos.add(c);

                } else if (tipo.equals("P")) {
                    Tratamiento tratC;
                    if (p[7].equals("SR")) {
                        tratC = Tratamiento.SR;
                    } else {
                        tratC = Tratamiento.SRA;
                    }
                    Nombre nomContacto = new Nombre(tratC, p[8], p[9], p[10]);
                    Pasajero pas = new Pasajero(rut, nombre);
                    pas.setFonoContacto(fono);
                    pas.setNomContacto(nomContacto);
                    objetos.add(pas);

                } else if (tipo.equals("CP")) {
                    Cliente c = new Cliente(rut, nombre, p[7]);
                    Tratamiento tratCP;
                    if (p[8].equals("SR")) {
                        tratCP = Tratamiento.SR;
                    } else {
                        tratCP = Tratamiento.SRA;
                    }
                    Nombre nomContacto = new Nombre(tratCP, p[9], p[10], p[11]);
                    Pasajero pas = new Pasajero(rut, nombre);
                    pas.setFonoContacto(fono);
                    pas.setNomContacto(nomContacto);
                    objetos.add(c);
                    objetos.add(pas);
                }
            }
            //empresa
            while ((linea = lector.readLine()) != null && !linea.equals("+")) {
                String[] p = linea.split(";");
                String[] partes = p[0].split("-");
                int num = parseInt(partes[0].replace(".", ""));
                char dv = partes[1].charAt(0);

                Empresa e = new Empresa(new Rut(num, dv), p[1], p[2]);
                empresas.add(e);
                objetos.add(e);
            }

            //tripulantes
            while ((linea = lector.readLine()) != null && !linea.equals("+")) {
                String[] p = linea.split(";");
                String[] partesRut = p[1].split("-");
                int num = parseInt(partesRut[0].replace(".", ""));
                char dv = partesRut[1].charAt(0);
                Rut id = new Rut(num, dv);

                Tratamiento trat;
                if (p[2].equals("SR")) {
                    trat = Tratamiento.SR;
                } else {
                    trat = Tratamiento.SRA;
                }
                Nombre nombre = new Nombre(trat, p[3], p[4], p[5]);
                Direccion dir = new Direccion(p[6], parseInt(p[7]), p[8]);

                String[] partesRutEmp = p[9].split("-");
                int numEmp = parseInt(partesRutEmp[0].replace(".", ""));
                char dvEmp = partesRutEmp[1].charAt(0);
                Empresa emp = findEmpresa(empresas, new Rut(numEmp, dvEmp)).orElse(null);

                if (p[0].equals("A")) {
                    emp.addAuxiliar(id, nombre, dir);
                } else {
                    emp.addConductor(id, nombre, dir);
                }
            }

            //terminal
            while ((linea = lector.readLine()) != null && !linea.equals("+")) {
                String[] p = linea.split(";");
                Direccion dir = new Direccion(p[1], parseInt(p[2]), p[3]);
                Terminal t = new Terminal(p[0], dir);
                terminales.add(t);
                objetos.add(t);
            }
            //buses
            while ((linea = lector.readLine()) != null && !linea.equals("+")) {
                String[] p = linea.split(";");
                Empresa emp = findEmpresa(empresas, Rut.of(p[4])).orElse(null);
                Bus b = new Bus(p[0], p[1], p[2], parseInt(p[3]), emp);
                if (emp != null) {
                    emp.addBus(b);
                }
                buses.add(b);
                objetos.add(b);
            }

            //viaje
            while ((linea = lector.readLine()) != null) {
                String[] p = linea.split(";");
                String[] dma = p[0].split("-");
                LocalDate fecha = LocalDate.of(parseInt(dma[2]), parseInt(dma[1]), parseInt(dma[0]));
                LocalTime hora  = LocalTime.parse(p[1]);
                int precio = parseInt(p[2]);
                int duracion = parseInt(p[3]);

                Bus bus        = findBus(buses, p[4]).orElse(null);
                Auxiliar aux   = (Auxiliar)  findTripulante(bus.getEmpresa(), Rut.of(p[5]), "Auxiliar").orElse(null);
                Conductor cond = (Conductor) findTripulante(bus.getEmpresa(), Rut.of(p[6]), "Conductor").orElse(null);
                Terminal sal   = findTerminal(terminales, p[7]).orElse(null);
                Terminal lleg  = findTerminal(terminales, p[8]).orElse(null);

                Viaje v = new Viaje(fecha, hora, precio, duracion, bus, sal, lleg, aux);
                if (cond != null) {
                    v.addConductor(cond);
                    cond.addViaje(v);
                }
                if (aux != null) {
                    aux.addViaje(v);
                }
                if (bus != null) {
                    bus.addViaje(v);
                }
                objetos.add(v);
            }

        } catch(FileNotFoundException e){
            throw new SVPException("No existe o no se puede abrir SVPDatosIniciales.txt");
        } catch (IOException e) {
            throw new SVPException("Error al leer SVPDatosIniciales.txt");
        }
        return objetos.toArray();

    }

    public void saveControladores(Object[] controladores) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("SVPObjetos.obj"))) {
            out.writeObject(controladores);
        } catch (IOException e) {
            throw new SVPException("No se puede abrir o crear el archivo SVPObjetos.obj o no se puede grabar en él");
        }
    }

    public Object[] readControladores() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("SVPObjetos.obj"))) {
            return (Object[]) in.readObject();
        } catch (FileNotFoundException e) {
            throw new SVPException("No existe o no se puede abrir el archivo SVPObjetos.obj");
        } catch (IOException | ClassNotFoundException e) {
            throw new SVPException("No se puede leer el archivo SVPObjetos.obj");
        }
    }

    public void savePasajesDeVenta(Pasaje[] pasajes, String nombreArchivo) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nombreArchivo))) {
            for (Pasaje p : pasajes) {
                writer.write(p.toString());
                writer.newLine();
                writer.newLine();
            }
        } catch (IOException e) {
            throw new SVPException("No se puede abrir o crear el archivo " + nombreArchivo);
        }
    }

    private Optional<Empresa> findEmpresa(List<Empresa> list, Rut rut) {
        return list.stream().filter(e -> e.getRut().equals(rut)).findFirst();
    }
    private Optional<Bus> findBus(List<Bus> buses, String patente) {
        return buses.stream()
                .filter(b -> b.getPatente().equals(patente))
                .findFirst();
    }

    private Optional<Terminal> findTerminal(List<Terminal> terminales, String nombre) {
        return terminales.stream()
                .filter(t -> t.getNombre().equals(nombre))
                .findFirst();
    }

    private Optional<Tripulante> findTripulante(Empresa empresa, Rut id, String rol) {
        return Arrays.stream(empresa.getTripulantes())
                .filter(t -> t.getIdPersona().equals(id))
                .filter(t -> {
                    if (rol.equals("Auxiliar")) {
                        return t instanceof Auxiliar;
                    } else {
                        return t instanceof Conductor;
                    }
                })
                .findFirst();
    }


}
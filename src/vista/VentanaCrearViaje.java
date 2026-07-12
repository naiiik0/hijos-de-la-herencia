package vista;

import controlador.ControladorEmpresas;
import excepciones.SVPException;
import modelo.*;
import utilidades.Rut;

import javax.swing.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

public class VentanaCrearViaje extends JFrame{
    private JButton crearButton;
    private JButton cancelarButton;
    private JTextField txtFecha;
    private JTextField txtHora;
    private JTextField txtPrecio;
    private JTextField txtDuracion;
    private JComboBox comboAuxiliar;
    private JComboBox comboTerSalida;
    private JComboBox comboTerLlegada;
    private JComboBox comboConductor;
    private JComboBox comboBus;
    private JPanel panelPrincipal;

    private static ControladorEmpresas controlador;

    public VentanaCrearViaje() {
        this.controlador = ControladorEmpresas.getInstance();
        setContentPane(panelPrincipal);
        setTitle("B.4. VENTANA DE CREACIÓN DE UN VIAJE");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        ListasDesplegables();
    }

    private void ListasDesplegables() {
        comboBus.removeAllItems();
        comboTerSalida.removeAllItems();
        comboTerLlegada.removeAllItems();
        comboAuxiliar.removeAllItems();
        comboConductor.removeAllItems();

        // poblar combo de Bus
        ArrayList<Bus> busesExistentes = controlador.getBuses();
        for (Bus b : busesExistentes) {
            comboBus.addItem(b.getPatente() + " - " + b.getModelo());
        }

        // poblar combos de Terminales
        ArrayList<Terminal> terminalesExistentes = controlador.getTerminales();
        for (Terminal t : terminalesExistentes) {
            comboTerSalida.addItem(t.getNombre());
            comboTerLlegada.addItem(t.getNombre());
        }

        // 4poblar combo de Auxiliares
        for (Bus b : busesExistentes) {
            Empresa emp = b.getEmpresa();
            if (emp != null && emp.getTripulantes() != null) {
                for (Tripulante t : emp.getTripulantes()) {
                    if (t instanceof Auxiliar) {
                        String itemAuxiliar = t.getIdPersona().toString() + " - " + t.getNombre().toString();
                        boolean yaExiste = false;
                        for (int i = 0; i < comboAuxiliar.getItemCount(); i++) {
                            if (comboAuxiliar.getItemAt(i).equals(itemAuxiliar)) {
                                yaExiste = true;
                                break;
                            }
                        }
                        if (!yaExiste) {
                            comboAuxiliar.addItem(itemAuxiliar);
                        }
                    }
                }
            }
        }
    }

    private void registrarViaje() {
        try {

            String busSeleccionado = (String) comboBus.getSelectedItem();
            if (busSeleccionado == null) throw new SVPException("Debe seleccionar un bus.");
            String patente = busSeleccionado.split(" - ")[0];

            String termSalida = (String) comboTerSalida.getSelectedItem();
            String termLlegada = (String) comboTerLlegada.getSelectedItem();

            String auxiliarSeleccionado = (String) comboAuxiliar.getSelectedItem();
            if (auxiliarSeleccionado == null) throw new SVPException("Debe seleccionar un auxiliar.");
            String rutAuxStr = auxiliarSeleccionado.split(" - ")[0];

            Bus busReal = controlador.findBus(patente)
                    .orElseThrow(() -> new SVPException("Bus no encontrado en el sistema."));

            Terminal salidaReal = controlador.findTerminalNombre(termSalida)
                    .orElseThrow(() -> new SVPException("Terminal de salida inválido."));

            Terminal llegadaReal = controlador.findTerminalNombre(termLlegada)
                    .orElseThrow(() -> new SVPException("Terminal de llegada inválido."));

            Rut rutAux = Rut.of(rutAuxStr);

            Auxiliar auxiliarReal = (Auxiliar) controlador.findAuxiliar(rutAux, busReal.getEmpresa().getRut())
                    .orElseThrow(() -> new SVPException("El auxiliar no pertenece a la empresa de este bus."));

            LocalDate fecha = LocalDate.parse(txtFecha.getText().trim());
            LocalTime hora = LocalTime.parse(txtHora.getText().trim());
            int precio = Integer.parseInt(txtPrecio.getText().trim());
            int duracion = Integer.parseInt(txtDuracion.getText().trim());

            Viaje nuevoViaje = new Viaje(fecha, hora, precio, duracion, busReal, salidaReal, llegadaReal, auxiliarReal);

            busReal.addViaje(nuevoViaje);

            JOptionPane.showMessageDialog(this, "Viaje registrado de forma exitosa.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            dispose();

        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Formato erróneo. Use AAAA-MM-DD para fecha y HH:MM para hora.", "Error de entrada", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Precio y duración deben ser enteros.", "Error de entrada", JOptionPane.ERROR_MESSAGE);
        } catch (SVPException ex) {
            // Manejo controlado de excepciones lanzado a través de tu clase de excepciones
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error de validación", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Detalle del error: " + ex.getMessage(), "Error inesperado", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        try {
            Object[] datos = persistencia.IOSVP.getInstance().readDatosIniciales();
            ControladorEmpresas.getInstance().setDatosIniciales(datos);
        } catch (Exception e) {
            System.out.println("Aviso: No se pudieron precargar datos de texto: " + e.getMessage());
        }

        VentanaCrearViaje v = new VentanaCrearViaje();
        v.setVisible(true);
    }



}

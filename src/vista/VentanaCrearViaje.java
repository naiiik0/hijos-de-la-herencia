package vista;

import controlador.ControladorEmpresas;
import controlador.SistemaVentaPasajes;
import excepciones.SVPException;
import modelo.*;
import utilidades.Rut;

import javax.swing.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

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

    private ControladorEmpresas controlador;
    private SistemaVentaPasajes sistema;

    public VentanaCrearViaje(JFrame parent) {
        controlador = ControladorEmpresas.getInstance();
        sistema = SistemaVentaPasajes.getInstance();
        setContentPane(panelPrincipal);
        setTitle("B.4. VENTANA DE CREACIÓN DE UN VIAJE");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(parent);
        ListasDesplegables();
        asignarAcciones();
    }

    private void ListasDesplegables() {
        comboBus.removeAllItems();
        comboTerSalida.removeAllItems();
        comboTerLlegada.removeAllItems();
        comboAuxiliar.removeAllItems();
        comboConductor.removeAllItems();

        String[][] empresas = controlador.listEmpresas();
        if (empresas == null) return;

        for (String[] emp : empresas) {
            try {
                Rut rut = Rut.of(emp[0]);
                controlador.findEmpresa(rut).ifPresent(empresa -> {
                    for (Bus b : empresa.getBuses()) {
                        comboBus.addItem(b.getPatente() + " - " + b.getModelo()
                                + " (" + empresa.getNombre() + ")");
                    }
                    for (Tripulante t : empresa.getTripulantes()) {
                        String item = t.getIdPersona().toString() + " - "
                                + t.getNombreCompleto().toString()
                                + " (" + empresa.getNombre() + ")";
                        if (t instanceof Auxiliar) comboAuxiliar.addItem(item);
                        else if (t instanceof Conductor) comboConductor.addItem(item);
                    }
                });
            } catch (Exception ignored) {}
        }

        String[][] viajes = sistema.listViajes();
        if (viajes != null) {
            for (String[] v : viajes) {
                agregarSiNoExiste(comboTerSalida, v[6]);
                agregarSiNoExiste(comboTerLlegada, v[6]);
                agregarSiNoExiste(comboTerSalida, v[7]);
                agregarSiNoExiste(comboTerLlegada, v[7]);
            }
        }
    }

    private void asignarAcciones() {
        crearButton.addActionListener(e -> registrarViaje());
        cancelarButton.addActionListener(e -> dispose());
    }

    private void agregarSiNoExiste(JComboBox<String> combo, String item) {
        if (item == null || item.isEmpty()) return;
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (combo.getItemAt(i).equals(item)) return;
        }
        combo.addItem(item);
    }

    private void registrarViaje() {
        try {
            String fechaStr = txtFecha.getText().trim();
            String horaStr = txtHora.getText().trim();
            String precioStr = txtPrecio.getText().trim();
            String duracionStr = txtDuracion.getText().trim();

            if (fechaStr.isEmpty() || horaStr.isEmpty() || precioStr.isEmpty() || duracionStr.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Todos los campos son obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            LocalDate fecha = LocalDate.parse(fechaStr);
            LocalTime hora = LocalTime.parse(horaStr);
            int precio = Integer.parseInt(precioStr);
            int duracion = Integer.parseInt(duracionStr);

            if (precio <= 0 || duracion <= 0) {
                JOptionPane.showMessageDialog(this,
                        "Precio y duración deben ser mayores a 0.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String busSeleccionado  = (String) comboBus.getSelectedItem();
            String auxSeleccionado  = (String) comboAuxiliar.getSelectedItem();
            String condSeleccionado = (String) comboConductor.getSelectedItem();
            String termSalida       = (String) comboTerSalida.getSelectedItem();
            String termLlegada      = (String) comboTerLlegada.getSelectedItem();

            if (busSeleccionado == null || auxSeleccionado == null
                    || condSeleccionado == null || termSalida == null || termLlegada == null) {
                JOptionPane.showMessageDialog(this,
                        "Debe seleccionar bus, auxiliar, conductor y terminales.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String patente    = busSeleccionado.split(" - ")[0];
            String rutAuxStr  = auxSeleccionado.split(" - ")[0];
            String rutCondStr = condSeleccionado.split(" - ")[0];

            sistema.createViaje(fecha, hora, precio, duracion, patente,
                    new String[]{rutAuxStr, rutCondStr},
                    new String[]{termSalida, termLlegada});

            JOptionPane.showMessageDialog(this, "Viaje registrado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            dispose();

        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this,
                    "Formato de fecha: AAAA-MM-DD  |  Hora: HH:MM", "Error de formato", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Precio y duración deben ser números enteros.", "Error de formato", JOptionPane.ERROR_MESSAGE);
        } catch (SVPException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(), "Error de validación", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error inesperado: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        try {
            Object[] datos = persistencia.IOSVP.getInstance().readDatosIniciales();
            ControladorEmpresas.getInstance().setDatosIniciales(datos);
        } catch (Exception e) {
            System.out.println("Aviso: No se pudieron precargar datos de texto: " + e.getMessage());
        }

        VentanaCrearViaje v = new VentanaCrearViaje(null);
        v.setVisible(true);
    }



}

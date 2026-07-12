package vista;

import controlador.SistemaVentaPasajes;
import excepciones.SVPException;
import modelo.TipoDocumento;
import utilidades.Rut;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.time.LocalDate;
import java.time.LocalTime;
// autor:Yamilet Arias
public class VentaPasaje extends JFrame {
    private JPanel panelPrincipal;
    private JTextField txtIDDocumento;
    private JComboBox<TipoDocumento> cmbTipoDocumento;
    private JTextField txtFecha;
    private JTextField txtIDCliente;
    private JTextField textComunaSalida;
    private JTextField textComunaLlegada;
    private JSpinner spCantidad;
    private JTable tblViajes;
    private JTable tblAsientos;
    private JTextField txtIDPasajero;
    private JTextField txtAsiento;
    private JLabel lblMonto;
    private JComboBox<String> cmbTipoPago;
    private JTextField txtNumeroTarjeta;
    private JButton btnIniciarVenta;
    private JButton btnSelecionarViaje;
    private JButton btnAgregarPasajero;
    private JButton btnPagarVenta;
    private JButton btnGenerarPasaje;
    private JButton btnCerrar;
    private SistemaVentaPasajes sistema;
    private DefaultTableModel modeloViajes;
    private DefaultTableModel modeloAsientos;

    // Datos de la venta
    private String idDocumentoActual;
    private TipoDocumento tipoDocumentoActual;
    private LocalDate fechaViaje;
    private LocalTime horaViaje;
    private String patenteBus;

    public VentaPasaje() {
        sistema = SistemaVentaPasajes.getInstance();
        setContentPane(panelPrincipal);
        setTitle("Venta de Pasajes");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        inicializarCombos();
        inicializarTablas();
        bloquearControles();
        btnIniciarVenta.addActionListener(e -> iniciarVenta());
        btnSelecionarViaje.addActionListener(e -> seleccionarViaje());
        btnAgregarPasajero.addActionListener(e -> agregarPasajero());
        btnPagarVenta.addActionListener(e -> pagarVenta());
        btnGenerarPasaje.addActionListener(e -> generarPasajes());
        btnCerrar.addActionListener(e -> dispose());
    }
    private void inicializarCombos() {
        cmbTipoDocumento.removeAllItems();
        cmbTipoDocumento.addItem(TipoDocumento.BOLETA);
        cmbTipoDocumento.addItem(TipoDocumento.FACTURA);
        cmbTipoPago.removeAllItems();
        cmbTipoPago.addItem("Efectivo");
        cmbTipoPago.addItem("Tarjeta");
        cmbTipoPago.addActionListener(e -> {
            boolean tarjeta = cmbTipoPago.getSelectedItem().equals("Tarjeta");
            txtNumeroTarjeta.setEnabled(tarjeta);});
    }
    private void inicializarTablas() {
        modeloViajes = new DefaultTableModel();
        modeloViajes.addColumn("Patente");
        modeloViajes.addColumn("Hora");
        modeloViajes.addColumn("Precio");
        modeloViajes.addColumn("Disponibles");
        tblViajes.setModel(modeloViajes);
        modeloAsientos = new DefaultTableModel();
        modeloAsientos.addColumn("Asientos");
        tblAsientos.setModel(modeloAsientos);
    }
    private void bloquearControles() {
        btnSelecionarViaje.setEnabled(false);
        btnAgregarPasajero.setEnabled(false);
        btnPagarVenta.setEnabled(false);
        btnGenerarPasaje.setEnabled(false);
        txtIDPasajero.setEnabled(false);
        txtAsiento.setEnabled(false);
        cmbTipoPago.setEnabled(false);
        txtNumeroTarjeta.setEnabled(false);
        lblMonto.setText("$0");
    }
    private void iniciarVenta() {
        try {
            idDocumentoActual = txtIDDocumento.getText().trim();
            tipoDocumentoActual =
                    (TipoDocumento) cmbTipoDocumento.getSelectedItem();
            fechaViaje =
                    LocalDate.parse(txtFecha.getText().trim());
            Rut rutCliente =
                    Rut.of(txtIDCliente.getText().trim());
            String comunaSalida =
                    textComunaSalida.getText().trim();
            String comunaLlegada =
                    textComunaLlegada.getText().trim();
            int cantidad =
                    (Integer) spCantidad.getValue();
            sistema.iniciaVenta(idDocumentoActual, tipoDocumentoActual, fechaViaje, rutCliente, comunaSalida, comunaLlegada, cantidad);
            cargarViajes(fechaViaje, comunaSalida, comunaLlegada, cantidad);
            btnSelecionarViaje.setEnabled(true);
            JOptionPane.showMessageDialog(
                    this,
                    "Venta iniciada correctamente.");

        } catch (SVPException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Datos inválidos.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void cargarViajes(LocalDate fecha, String salida, String llegada, int cantidad) {
        modeloViajes.setRowCount(0);
        String[][] viajes =
                sistema.getHorariosDisponibles(
                        fecha,
                        salida,
                        llegada,
                        cantidad);
        for (String[] fila : viajes) {
            modeloViajes.addRow(fila);
        }
        if (modeloViajes.getRowCount() == 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "No existen viajes disponibles.");
        }
    }
    private void seleccionarViaje() {
        try {
            int fila = tblViajes.getSelectedRow();
            if (fila == -1) {
                JOptionPane.showMessageDialog(
                        this,
                        "Debe seleccionar un viaje.");
                return;
            }
            patenteBus = modeloViajes.getValueAt(fila, 0).toString();
            horaViaje = LocalTime.parse(
                    modeloViajes.getValueAt(fila, 1).toString());
            cargarAsientos();
            btnAgregarPasajero.setEnabled(true);
            txtIDPasajero.setEnabled(true);
            txtAsiento.setEnabled(true);
            JOptionPane.showMessageDialog(
                    this,
                    "Viaje seleccionado correctamente."
            );
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void cargarAsientos() {
        modeloAsientos.setRowCount(0);
        String[] asientos = sistema.listAsientosDeViaje(
                fechaViaje,
                horaViaje,
                patenteBus);
        for (String asiento : asientos) {
            modeloAsientos.addRow(new Object[]{asiento});
        }
    }
    private void agregarPasajero() {
        try {
            int asiento = Integer.parseInt(
                    txtAsiento.getText().trim());
            Rut rutPasajero = Rut.of(txtIDPasajero.getText().trim());
            sistema.vendePasaje(
                    idDocumentoActual,
                    tipoDocumentoActual,
                    fechaViaje,
                    horaViaje,
                    patenteBus,
                    asiento,
                    rutPasajero);
            Integer monto = sistema.getMontoVenta(
                    idDocumentoActual,
                    tipoDocumentoActual
            ).orElse(0);
            lblMonto.setText("$" + monto);
            cargarAsientos();
            txtIDPasajero.setText("");
            txtAsiento.setText("");
            btnPagarVenta.setEnabled(true);
            cmbTipoPago.setEnabled(true);
            JOptionPane.showMessageDialog(
                    this,
                    "Pasaje agregado correctamente.");
        } catch (SVPException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Datos inválidos.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void pagarVenta() {
        try {
            if (cmbTipoPago.getSelectedItem().equals("Efectivo")) {
                sistema.pagaVenta(
                        idDocumentoActual,
                        tipoDocumentoActual
                );
            } else {

                long numeroTarjeta = Long.parseLong(
                        txtNumeroTarjeta.getText().trim()
                );

                sistema.pagaVenta(
                        idDocumentoActual,
                        tipoDocumentoActual,
                        numeroTarjeta
                );
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Venta pagada correctamente.");
            btnPagarVenta.setEnabled(false);
            btnAgregarPasajero.setEnabled(false);
            cmbTipoPago.setEnabled(false);
            txtNumeroTarjeta.setEnabled(false);
            btnGenerarPasaje.setEnabled(true);

        }
        catch (SVPException ex){
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
        catch (Exception ex){

            JOptionPane.showMessageDialog(
                    this,
                    "Error al pagar la venta.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    private void generarPasajes() {
        try {
            JOptionPane.showMessageDialog(
                    this,
                    "Pasajes generados correctamente."
            );

            btnGenerarPasaje.setEnabled(false);

        }
        catch (SVPException ex){
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
        catch (Exception ex){

            JOptionPane.showMessageDialog(
                    this,
                    "Error al generar los pasajes.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}

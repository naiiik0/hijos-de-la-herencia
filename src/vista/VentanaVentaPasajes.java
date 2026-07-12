// Autor: (tu nombre)
package vista;

import controlador.SistemaVentaPasajes;
import excepciones.SVPException;
import modelo.TipoDocumento;
import utilidades.Rut;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

public class VentanaVentaPasajes extends JFrame {

    private JPanel panelPrincipal;
    private JTextField txtIdDoc;
    private JComboBox<String> comboTipoDoc;
    private JTextField txtFechaVenta;
    private JTextField txtIdCliente;
    private JTextField txtComunaSalida;
    private JTextField txtComunaLlegada;
    private JTextField txtNroPasajes;
    private JButton btnIniciarVenta;
    private JTable tablaViajes;
    private JTextField txtIdPasajero;
    private JComboBox<String> comboAsiento;
    private JButton btnVenderPasaje;
    private JComboBox<String> comboTipoPago;
    private JTextField txtNroTarjeta;
    private JButton btnPagar;
    private JButton btnGenerarPasajes;
    private JButton btnCerrar;
    private JLabel lblEstado;

    private SistemaVentaPasajes sistema;

    // Estado de la venta activa
    private String idDocActivo;
    private TipoDocumento tipoDocActivo;
    private String patenteViajeSeleccionado;
    private LocalTime horaViajeSeleccionado;
    private LocalDate fechaViajeSeleccionado;

    public VentanaVentaPasajes(JFrame parent) {
        sistema = SistemaVentaPasajes.getInstance();
        setContentPane(panelPrincipal);
        setTitle("Venta de Pasajes");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 650);
        setLocationRelativeTo(parent);
        inicializarCombos();
        configurarEstadoInicial();
        asignarAcciones();
    }

    private void inicializarCombos() {
        comboTipoDoc.addItem("BOLETA");
        comboTipoDoc.addItem("FACTURA");
        comboTipoPago.addItem("Efectivo");
        comboTipoPago.addItem("Tarjeta");
        // Tarjeta solo activa si se selecciona
        comboTipoPago.addActionListener(e -> {
            txtNroTarjeta.setEnabled("Tarjeta".equals(comboTipoPago.getSelectedItem()));
        });
        txtNroTarjeta.setEnabled(false);
    }

    private void configurarEstadoInicial() {
        // Al inicio solo se puede iniciar venta
        btnVenderPasaje.setEnabled(false);
        btnPagar.setEnabled(false);
        btnGenerarPasajes.setEnabled(false);
        comboAsiento.setEnabled(false);
        txtIdPasajero.setEnabled(false);
        comboTipoPago.setEnabled(false);
        txtNroTarjeta.setEnabled(false);
    }

    private void asignarAcciones() {
        btnIniciarVenta.addActionListener(e -> iniciarVenta());
        btnVenderPasaje.addActionListener(e -> venderPasaje());
        btnPagar.addActionListener(e -> pagarVenta());
        btnGenerarPasajes.addActionListener(e -> generarPasajes());
        btnCerrar.addActionListener(e -> dispose());

        // Al seleccionar viaje en tabla, cargar asientos
        tablaViajes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) seleccionarViaje();
        });
    }

    private void iniciarVenta() {
        try {
            String idDoc = txtIdDoc.getText().trim();
            String idCliente = txtIdCliente.getText().trim();
            String comunaSalida = txtComunaSalida.getText().trim();
            String comunaLlegada = txtComunaLlegada.getText().trim();
            String nroPasajesStr = txtNroPasajes.getText().trim();
            String fechaStr = txtFechaVenta.getText().trim();

            if (idDoc.isEmpty() || idCliente.isEmpty() || comunaSalida.isEmpty()
                    || comunaLlegada.isEmpty() || nroPasajesStr.isEmpty() || fechaStr.isEmpty()) {
                mostrarEstado("Complete todos los campos.", true);
                return;
            }

            int nroPasajes = Integer.parseInt(nroPasajesStr);
            LocalDate fecha = LocalDate.parse(fechaStr);
            TipoDocumento tipo = TipoDocumento.valueOf(
                    (String) comboTipoDoc.getSelectedItem());
            Rut rutCliente = Rut.of(idCliente);

            // Buscar viajes disponibles primero
            String[][] viajes = sistema.getHorariosDisponibles(
                    fecha, comunaSalida, comunaLlegada, nroPasajes);

            if (viajes == null || viajes.length == 0) {
                mostrarEstado("No hay viajes disponibles para esos parámetros.", true);
                return;
            }

            // Iniciar la venta en el controlador
            sistema.iniciaVenta(idDoc, tipo, fecha, rutCliente,
                    comunaSalida, comunaLlegada, nroPasajes);

            // Guardar estado activo
            idDocActivo = idDoc;
            tipoDocActivo = tipo;
            fechaViajeSeleccionado = fecha;

            // Mostrar viajes en tabla
            String[] columnas = {"Patente Bus", "Hora", "Precio", "Asientos Disp."};
            DefaultTableModel modelo = new DefaultTableModel(viajes, columnas) {
                @Override
                public boolean isCellEditable(int row, int col) { return false; }
            };
            tablaViajes.setModel(modelo);

            // Habilitar siguiente paso
            btnIniciarVenta.setEnabled(false);
            mostrarEstado("✔ Venta iniciada. Seleccione un viaje de la tabla.", false);

        } catch (NumberFormatException ex) {
            mostrarEstado("Nro. pasajes debe ser un número entero.", true);
        } catch (DateTimeParseException ex) {
            mostrarEstado("Formato de fecha: AAAA-MM-DD", true);
        } catch (SVPException ex) {
            mostrarEstado("Error: " + ex.getMessage(), true);
        }
    }

    private void seleccionarViaje() {
        int fila = tablaViajes.getSelectedRow();
        if (fila < 0) return;

        patenteViajeSeleccionado = (String) tablaViajes.getValueAt(fila, 0);
        horaViajeSeleccionado = LocalTime.parse(
                (String) tablaViajes.getValueAt(fila, 1));

        // Cargar asientos disponibles
        String[] asientos = sistema.listAsientosDeViaje(
                fechaViajeSeleccionado, horaViajeSeleccionado, patenteViajeSeleccionado);

        comboAsiento.removeAllItems();
        for (String a : asientos) comboAsiento.addItem(a);

        // Habilitar venta de pasaje
        btnVenderPasaje.setEnabled(true);
        comboAsiento.setEnabled(true);
        txtIdPasajero.setEnabled(true);
        mostrarEstado("✔ Viaje seleccionado. Ingrese RUT pasajero y asiento.", false);
    }

    private void venderPasaje() {
        try {
            String idPasajero = txtIdPasajero.getText().trim();
            String asientoStr = (String) comboAsiento.getSelectedItem();

            if (idPasajero.isEmpty() || asientoStr == null) {
                mostrarEstado("Ingrese RUT del pasajero y seleccione asiento.", true);
                return;
            }

            int asiento = Integer.parseInt(asientoStr.trim());
            Rut rutPasajero = Rut.of(idPasajero);

            sistema.vendePasaje(idDocActivo, tipoDocActivo,
                    fechaViajeSeleccionado, horaViajeSeleccionado,
                    patenteViajeSeleccionado, asiento, rutPasajero);

            // Refrescar asientos
            String[] asientos = sistema.listAsientosDeViaje(
                    fechaViajeSeleccionado, horaViajeSeleccionado, patenteViajeSeleccionado);
            comboAsiento.removeAllItems();
            for (String a : asientos) comboAsiento.addItem(a);

            txtIdPasajero.setText("");
            btnPagar.setEnabled(true);
            comboTipoPago.setEnabled(true);
            mostrarEstado("✔ Pasaje vendido. Puede agregar más o proceder a pagar.", false);

        } catch (NumberFormatException ex) {
            mostrarEstado("Asiento inválido.", true);
        } catch (SVPException ex) {
            mostrarEstado("Error: " + ex.getMessage(), true);
        }
    }

    private void pagarVenta() {
        try {
            String tipoPago = (String) comboTipoPago.getSelectedItem();

            if ("Tarjeta".equals(tipoPago)) {
                String nroTarjetaStr = txtNroTarjeta.getText().trim();
                if (nroTarjetaStr.isEmpty()) {
                    mostrarEstado("Ingrese número de tarjeta.", true);
                    return;
                }
                long nroTarjeta = Long.parseLong(nroTarjetaStr);
                sistema.pagaVenta(idDocActivo, tipoDocActivo, nroTarjeta);
            } else {
                sistema.pagaVenta(idDocActivo, tipoDocActivo);
            }

            btnPagar.setEnabled(false);
            btnVenderPasaje.setEnabled(false);
            comboTipoPago.setEnabled(false);
            txtNroTarjeta.setEnabled(false);
            btnGenerarPasajes.setEnabled(true);
            mostrarEstado("✔ Venta pagada. Puede generar los pasajes electrónicos.", false);

        } catch (NumberFormatException ex) {
            mostrarEstado("Número de tarjeta inválido.", true);
        } catch (SVPException ex) {
            mostrarEstado("Error: " + ex.getMessage(), true);
        }
    }

    private void generarPasajes() {
        try {
            sistema.generatePasajesVenta(idDocActivo, tipoDocActivo);
            mostrarEstado("✔ Pasajes generados correctamente.", false);
            btnGenerarPasajes.setEnabled(false);
        } catch (SVPException ex) {
            mostrarEstado("Error: " + ex.getMessage(), true);
        }
    }

    private void mostrarEstado(String mensaje, boolean esError) {
        lblEstado.setText(mensaje);
        lblEstado.setForeground(esError ?
                java.awt.Color.RED : new java.awt.Color(0, 128, 0));
    }
}
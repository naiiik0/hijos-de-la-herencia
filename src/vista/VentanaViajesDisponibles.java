package vista;

import controlador.ControladorEmpresas;
import modelo.Viaje;
import persistencia.IOSVP;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;

public class VentanaViajesDisponibles extends JFrame {
    private JTable tablaViajes;
    private JPanel panelPrincipal;
    private JButton cerrarButton;

    private ControladorEmpresas controlador;

    public VentanaViajesDisponibles() {

        this.controlador = ControladorEmpresas.getInstance();
        setContentPane(panelPrincipal);
        setTitle("SVP - Listado de Viajes Disponibles");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        configurarTabla();

        cerrarButton.addActionListener(e -> dispose());
        pack();
        setSize(750, 450);
        setLocationRelativeTo(null);
    }

    private void configurarTabla() {
        try {
            String[] columnas = {"Fecha", "Hora", "Precio", "Duración", "Bus (Patente)", "Origen", "Destino", "Asientos Libres"};
            ArrayList<String[]> filasViajes = new ArrayList<>();

            Object[] datosIniciales = IOSVP.getInstance().readDatosIniciales();

            for (Object obj : datosIniciales) {
                if (obj instanceof Viaje) {
                    Viaje v = (Viaje) obj;

                    String[] fila = new String[]{
                            v.getFecha().toString(),
                            v.getHora().toString(),
                            "$" + v.getPrecio(),
                            v.getDuracionMinutos() + " min",
                            v.getBus() != null ? v.getBus().getPatente() : "-",
                            v.getTerminalSalida() != null ? v.getTerminalSalida().getNombre() : "-",
                            v.getTerminalLlegada() != null ? v.getTerminalLlegada().getNombre() : "-",
                            String.valueOf(v.getNroAsientosDisponibles())
                    };
                    filasViajes.add(fila);
                }
            }
            if (filasViajes.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No existen viajes registrados en el sistema actualmente.", "Sin Resultados", JOptionPane.INFORMATION_MESSAGE);
                tablaViajes.setModel(new DefaultTableModel(new Object[][]{}, columnas));
                return;
            }

            String[][] datos = filasViajes.toArray(new String[0][0]);

            DefaultTableModel modelo = new DefaultTableModel(datos, columnas) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false; // Bloquear edición de celdas
                }
            };

            tablaViajes.setModel(modelo);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al procesar el listado de viajes: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        try {
            Object[] datos = IOSVP.getInstance().readDatosIniciales();
            ControladorEmpresas.getInstance().setDatosIniciales(datos);
        } catch (Exception e) {
            System.out.println("Aviso: No se pudieron precargar datos en el entorno de prueba: " + e.getMessage());
        }

        SwingUtilities.invokeLater(() -> {
            VentanaViajesDisponibles ventana = new VentanaViajesDisponibles();
            ventana.setVisible(true);
        });
    }
}

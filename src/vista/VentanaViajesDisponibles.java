package vista;

import controlador.SistemaVentaPasajes;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;


public class VentanaViajesDisponibles extends JFrame {
    private JTable tablaViajes;
    private JPanel panelPrincipal;
    private JButton cerrarButton;

    public VentanaViajesDisponibles(JFrame parent) {
        setContentPane(panelPrincipal);
        setTitle("SVP - Listado de Viajes Disponibles");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        configurarTabla();
        setLocationRelativeTo(parent);
        cerrarButton.addActionListener(e -> dispose());
        pack();
        setSize(750, 450);
        setLocationRelativeTo(null);
    }

    private void configurarTabla() {
        try {
            String[] columnas = {"Fecha", "Hora", "Precio", "Duración",
                    "Bus (Patente)", "Origen", "Destino", "Asientos Libres"};

            String[][] datos = SistemaVentaPasajes.getInstance().listViajes();

            if (datos == null || datos.length == 0) {
                JOptionPane.showMessageDialog(this,
                        "No existen viajes registrados en el sistema.",
                        "Sin Resultados", JOptionPane.INFORMATION_MESSAGE);
                tablaViajes.setModel(new DefaultTableModel(new Object[][]{}, columnas));
                return;
            }

            DefaultTableModel modelo = new DefaultTableModel(datos, columnas) {
                @Override
                public boolean isCellEditable(int row, int column) { return false; }
            };
            tablaViajes.setModel(modelo);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al procesar el listado de viajes: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

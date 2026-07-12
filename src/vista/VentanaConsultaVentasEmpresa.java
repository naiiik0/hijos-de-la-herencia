package vista;

import controlador.ControladorEmpresas;
import excepciones.SVPException;
import utilidades.Rut;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class VentanaConsultaVentasEmpresa extends JFrame {

    private JPanel panelPrincipal;
    private JComboBox<String> comboEmpresas;
    private JButton buscarButton;
    private JTable tablaVentas;
    private JButton cerrarButton;

    private ControladorEmpresas controlador;

    public VentanaConsultaVentasEmpresa(JFrame parent) {
        controlador = ControladorEmpresas.getInstance();
        setContentPane(panelPrincipal);
        setTitle("Ventas por Empresa");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(700, 450);
        setLocationRelativeTo(parent);
        poblarComboEmpresas();
        asignarAcciones();
    }

    private void poblarComboEmpresas() {
        comboEmpresas.removeAllItems();
        String[][] empresas = controlador.listEmpresas();
        if (empresas == null || empresas.length == 0) {
            JOptionPane.showMessageDialog(this,
                    "No hay empresas registradas.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        for (String[] emp : empresas) {
            comboEmpresas.addItem(emp[0] + " - " + emp[1]);
        }
    }

    private void asignarAcciones() {
        buscarButton.addActionListener(e -> buscarVentas());
        cerrarButton.addActionListener(e -> dispose());
    }

    private void buscarVentas() {
        String[] columnas = {"Fecha", "Tipo Doc.", "Monto Pagado", "Tipo Pago"};

        String seleccion = (String) comboEmpresas.getSelectedItem();
        if (seleccion == null) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione una empresa.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Rut rut = Rut.of(seleccion.split(" - ")[0]);
            String[][] datos = controlador.listVentasEmpresa(rut);

            if (datos == null || datos.length == 0) {
                JOptionPane.showMessageDialog(this,
                        "La empresa no tiene ventas registradas.",
                        "Sin Resultados", JOptionPane.INFORMATION_MESSAGE);
                tablaVentas.setModel(new DefaultTableModel(new Object[][]{}, columnas));
                return;
            }

            DefaultTableModel modelo = new DefaultTableModel(datos, columnas) {
                @Override
                public boolean isCellEditable(int row, int column) { return false; }
            };
            tablaVentas.setModel(modelo);

        } catch (SVPException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error inesperado: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
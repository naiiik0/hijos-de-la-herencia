package vista;

import controlador.ControladorEmpresas;
import excepciones.SVPException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class VentanaConsultaEmpresas extends JFrame{
    private JPanel panelPrincipal;
    private JTable tablaEmpresas;
    private JButton cerrarButton;

    private ControladorEmpresas controlador;

    public VentanaConsultaEmpresas(JFrame parent) {
        this.controlador = ControladorEmpresas.getInstance();

        setContentPane(panelPrincipal);
        setTitle("Consulta: Listado de Empresas");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        configurarTabla();

        cerrarButton.addActionListener(e -> dispose());

        pack();
        setSize(700, 400);
        setLocationRelativeTo(null);
    }

    private void configurarTabla() {
        try {
            String[] columnas = {"RUT", "Nombre", "URL", "N° Tripulantes", "N° Buses", "N° Ventas"};

            String[][] datos = controlador.listEmpresas();

            if (datos == null || datos.length == 0) {
                JOptionPane.showMessageDialog(this, "No existen empresas registradas en el sistema actualmente.", "Sin Resultados", JOptionPane.INFORMATION_MESSAGE);
                DefaultTableModel modeloVacio = new DefaultTableModel(new Object[][]{}, columnas);
                tablaEmpresas.setModel(modeloVacio);
                return;
            }

            DefaultTableModel modelo = new DefaultTableModel(datos, columnas) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            tablaEmpresas.setModel(modelo);

        } catch (SVPException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error en Consulta", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error inesperado: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}

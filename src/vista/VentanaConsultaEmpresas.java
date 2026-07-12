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

    public VentanaConsultaEmpresas() {
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
            //títulos de las columnas
            String[] columnas = {"RUT", "Nombre", "URL", "N° Tripulantes", "N° Buses", "N° Ventas"};

            // matriz de datos String[][] desde tu controlador
            String[][] datos = controlador.listEmpresas();

            if (datos == null || datos.length == 0) {
                JOptionPane.showMessageDialog(this, "No existen empresas registradas en el sistema actualmente.", "Sin Resultados", JOptionPane.INFORMATION_MESSAGE);
                DefaultTableModel modeloVacio = new DefaultTableModel(new Object[][]{}, columnas);
                tablaEmpresas.setModel(modeloVacio);
                return;
            }

            // crear el modelo de la tabla pasándole los datos y las columnas
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

    public static void main(String[] args) {
        try {

            Object[] datosIniciales = persistencia.IOSVP.getInstance().readDatosIniciales();

            ControladorEmpresas.getInstance().setDatosIniciales(datosIniciales);

            javax.swing.SwingUtilities.invokeLater(() -> {
                vista.VentanaConsultaEmpresas ventana = new vista.VentanaConsultaEmpresas();
                ventana.setVisible(true);
            });

        } catch (Exception e) {
            System.out.println("Error al cargar los datos iniciales: " + e.getMessage());
        }
    }

}

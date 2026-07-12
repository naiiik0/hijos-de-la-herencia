package vista;

import controlador.ControladorEmpresas;
import modelo.Conductor;
import modelo.Tripulante;
import utilidades.Rut;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class VentanaListaEmpleados extends JFrame {
    private JComboBox comboEmpresas;
    private JButton buscarButton;
    private JTable tablaPersonal;
    private JButton cerrarButton;
    private JPanel panelPrincipal;

    public VentanaListaEmpleados(JFrame parent) {
        setContentPane(panelPrincipal);
        setTitle("Listado de Personal por Empresa");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        poblarComboEmpresas();
        configurarEventos();
        pack();
        setSize(700, 450);
        setLocationRelativeTo(null);
    }

    private void poblarComboEmpresas() {
        comboEmpresas.removeAllItems();
        String[][] empresas = ControladorEmpresas.getInstance().listEmpresas();
        if (empresas == null || empresas.length == 0) {
            JOptionPane.showMessageDialog(this,
                    "No hay empresas registradas.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        for (String[] emp : empresas) {
            comboEmpresas.addItem(emp[0] + " - " + emp[1]);
        }
    }

    private void configurarEventos() {
        buscarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cargarTablaPersonal();
            }
        });

        cerrarButton.addActionListener(e -> dispose());
    }

    private void cargarTablaPersonal() {
        String[] columnas = {"RUT / ID", "Nombre Completo", "Cargo / Rol", "N° Viajes Realizados"};

        String seleccion = (String) comboEmpresas.getSelectedItem();
        if (seleccion == null) {
            JOptionPane.showMessageDialog(this,
                    "Por favor, seleccione una empresa.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Rut rutEmpresa = Rut.of(seleccion.split(" - ")[0]);

            ControladorEmpresas.getInstance().findEmpresa(rutEmpresa).ifPresentOrElse(empresa -> {
                Tripulante[] tripulantes = empresa.getTripulantes();

                if (tripulantes == null || tripulantes.length == 0) {
                    JOptionPane.showMessageDialog(this,
                            "La empresa no registra personal contratado.",
                            "Sin Resultados", JOptionPane.INFORMATION_MESSAGE);
                    tablaPersonal.setModel(new DefaultTableModel(new Object[][]{}, columnas));
                    return;
                }

                String[][] datos = new String[tripulantes.length][4];
                for (int i = 0; i < tripulantes.length; i++) {
                    Tripulante t = tripulantes[i];
                    datos[i][0] = t.getIdPersona().toString();
                    datos[i][1] = t.getNombreCompleto().toString();
                    datos[i][2] = (t instanceof Conductor) ? "Conductor" : "Auxiliar";
                    datos[i][3] = String.valueOf(t.getNroViajes());
                }

                DefaultTableModel modelo = new DefaultTableModel(datos, columnas) {
                    @Override
                    public boolean isCellEditable(int row, int column) { return false; }
                };
                tablaPersonal.setModel(modelo);

            }, () -> JOptionPane.showMessageDialog(this,
                    "No se encontró la empresa seleccionada.", "Error", JOptionPane.ERROR_MESSAGE));

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

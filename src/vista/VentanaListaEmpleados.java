package vista;

import controlador.ControladorEmpresas;
import modelo.Conductor;
import modelo.Empresa;
import modelo.Tripulante;
import persistencia.IOSVP;
import utilidades.Rut;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class VentanaListaEmpleados extends JFrame {
    private JComboBox comboEmpresas;
    private JButton buscarButton;
    private JTable tablaPersonal;
    private JButton cerrarButton;
    private JPanel panelPrincipal;

    public VentanaListaEmpleados() {
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
        try {
            Object[] datosIniciales = IOSVP.getInstance().readDatosIniciales();
            for (Object obj : datosIniciales) {
                if (obj instanceof Empresa) {
                    Empresa e = (Empresa) obj;
                    // Guardamos "RUT - Nombre" en el combo
                    comboEmpresas.addItem(e.getRut().toString() + " - " + e.getNombre());
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar las empresas: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
        try {
            String[] columnas = {"RUT / ID", "Nombre Completo", "Cargo / Rol", "N° Viajes Realizados"};
            ArrayList<String[]> filasPersonal = new ArrayList<>();

            String seleccion = (String) comboEmpresas.getSelectedItem();
            if (seleccion == null) {
                JOptionPane.showMessageDialog(this, "Por favor, seleccione una empresa.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String rutEmpresaStr = seleccion.split(" - ")[0];
            Rut rutEmpresaTarget = Rut.of(rutEmpresaStr);

            Object[] datosIniciales = IOSVP.getInstance().readDatosIniciales();
            Empresa empresaSeleccionada = null;

            for (Object obj : datosIniciales) {
                if (obj instanceof Empresa) {
                    Empresa e = (Empresa) obj;
                    if (e.getRut().equals(rutEmpresaTarget)) {
                        empresaSeleccionada = e;
                        break;
                    }
                }
            }

            if (empresaSeleccionada != null && empresaSeleccionada.getTripulantes() != null) {
                for (Tripulante t : empresaSeleccionada.getTripulantes()) {
                    String rol = (t instanceof Conductor) ? "Conductor" : "Auxiliar";

                    String[] fila = new String[]{
                            t.getIdPersona().toString(),
                            t.getNombre().toString(),
                            rol,
                            String.valueOf(t.getNroViajes())
                    };
                    filasPersonal.add(fila);
                }
            }

            if (filasPersonal.isEmpty()) {
                JOptionPane.showMessageDialog(this, "La empresa seleccionada no registra personal contratado actualmente.", "Sin Resultados", JOptionPane.INFORMATION_MESSAGE);
                tablaPersonal.setModel(new DefaultTableModel(new Object[][]{}, columnas));
                return;
            }

            String[][] datos = filasPersonal.toArray(new String[0][0]);
            DefaultTableModel modelo = new DefaultTableModel(datos, columnas) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            tablaPersonal.setModel(modelo);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al procesar el personal: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        try {
            Object[] datos = IOSVP.getInstance().readDatosIniciales();
            ControladorEmpresas.getInstance().setDatosIniciales(datos);
        } catch (Exception e) {
            System.out.println("Error precarga: " + e.getMessage());
        }

        SwingUtilities.invokeLater(() -> {
            VentanaListaEmpleados ventana = new VentanaListaEmpleados();
            ventana.setVisible(true);
        });
    }
}

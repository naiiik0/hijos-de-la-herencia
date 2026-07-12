package vista;

import controlador.ControladorEmpresas;
import controlador.SistemaVentaPasajes;
import modelo.Venta;
import utilidades.Rut;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class VentanaConsultas extends JFrame {
// Juan Bustos
    private VentanaPrincipal ventanaPrincipal;
    private JTabbedPane tabbedPane;

    public VentanaConsultas(VentanaPrincipal principal) {
        this.ventanaPrincipal = principal;
        setTitle("Consultas del Sistema");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(null);
        initComponents();
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                volver();
            }
        });
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        tabbedPane = new JTabbedPane();

        // Pestaña 1: Viajes
        tabbedPane.addTab("Listado de Viajes", crearPanelViajes());

        // Pestaña 2: Ventas
        tabbedPane.addTab("Listado de Ventas", crearPanelVentas());

        // Pestaña 3: Empresas
        tabbedPane.addTab("Listado de Empresas", crearPanelEmpresas());

        add(tabbedPane, BorderLayout.CENTER);

        JButton btnVolver = new JButton("Volver al Menú");
        btnVolver.addActionListener(e -> volver());

        JPanel pnlInferior = new JPanel();
        pnlInferior.add(btnVolver);
        add(pnlInferior, BorderLayout.SOUTH);
    }

    private JPanel crearPanelViajes() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] columnas = {"FECHA", "HORA SALIDA", "HORA LLEGADA", "PRECIO", "ASIENTOS DISP.", "PATENTE BUS", "TERMINAL SALIDA", "TERMINAL LLEGADA"};
        String[][] datos = SistemaVentaPasajes.getInstance().listViajes();

        JTable tabla = new JTable(new DefaultTableModel(datos, columnas) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });

        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelVentas() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] columnas = {"ID DOC.", "TIPO", "FECHA", "CLIENTE", "MONTO", "TIPO PAGO"};
        String [][]datos= ControladorEmpresas.getInstance().listVentas;

        JTable tabla = new JTable(new DefaultTableModel(datos, columnas) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });

        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelEmpresas() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] columnas = {"RUT", "NOMBRE", "URL", "NRO. TRIPULANTES", "NRO. BUSES", "NRO. VENTAS"};
        String[][] datos = ControladorEmpresas.getInstance().listEmpresas();

        JTable tabla = new JTable(new DefaultTableModel(datos, columnas) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });

        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        return panel;
    }

    private void volver() {
        this.setVisible(false);
        ventanaPrincipal.setVisible(true);
        this.dispose();
    }
}

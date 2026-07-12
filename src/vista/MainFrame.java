// Autor: Nicolás Figueroa
package vista;

import controlador.ControladorEmpresas;
import controlador.SistemaVentaPasajes;
import excepciones.SVPException;
import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private SistemaVentaPasajes sistema;
    private ControladorEmpresas controladorEmpresas;

    private JPanel panelPrincipal;
    private JButton btnVenta;
    private JButton btnViaje;
    private JButton btnConsulta1;
    private JButton btnConsulta2;
    private JButton btnConsulta3;
    private JButton btnLeerInicial;
    private JButton btnGuardar;
    private JButton btnCargar;
    private JButton btnSalir;
    private JLabel lblEstado;

    public MainFrame() {
        sistema = SistemaVentaPasajes.getInstance();
        controladorEmpresas = ControladorEmpresas.getInstance();
        setTitle("Sistema Venta Pasajes");
        setContentPane(panelPrincipal);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 580);
        setLocationRelativeTo(null);
        asignarAcciones();
    }

    private void asignarAcciones() {
        btnLeerInicial.addActionListener(e -> leerDatosIniciales());
        btnGuardar.addActionListener(e -> guardarDatos());
        btnCargar.addActionListener(e -> cargarDatos());
        btnSalir.addActionListener(e -> System.exit(0));

        // Descomentar cuando crees las otras ventanas:
        // btnVenta.addActionListener(e -> new VentaPasajesFrame(this).setVisible(true));
        btnViaje.addActionListener(e -> new VentanaCrearViaje(this).setVisible(true));
        // btnConsulta1.addActionListener(e -> new ListarViajesFrame(this).setVisible(true));
        // btnConsulta2.addActionListener(e -> new ListarEmpresasFrame(this).setVisible(true));
        // btnConsulta3.addActionListener(e -> new VentasEmpresaFrame(this).setVisible(true));
    }

    private void leerDatosIniciales() {
        try {
            sistema.readDatosIniciales();
            mostrarEstado("✔ Datos iniciales cargados correctamente.", false);
        } catch (SVPException e) {
            mostrarEstado("Error: " + e.getMessage(), true);
        }
    }

    private void guardarDatos() {
        try {
            sistema.saveDatosSistema();
            mostrarEstado("✔ Datos guardados correctamente.", false);
        } catch (SVPException e) {
            mostrarEstado("Error: " + e.getMessage(), true);
        }
    }

    private void cargarDatos() {
        try {
            sistema.readDatosSistema();
            sistema = SistemaVentaPasajes.getInstance();
            controladorEmpresas = ControladorEmpresas.getInstance();
            mostrarEstado("✔ Datos recuperados correctamente.", false);
        } catch (SVPException e) {
            mostrarEstado("Error: " + e.getMessage(), true);
        }
    }

    private void mostrarEstado(String mensaje, boolean esError) {
        lblEstado.setText(mensaje);
        lblEstado.setForeground(esError ? Color.RED : new Color(0, 128, 0));
    }
}
package vista;

import javax.swing.*;

//Autores: Nicolás Figueroa - Juan Bustos
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
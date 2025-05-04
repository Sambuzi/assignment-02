package gui;

import javax.swing.*;
import gui.components.GraphPanel;

/**
 * Main class to launch the GUI.
 */
public class LauncherGUI {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Dependency Analyser");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);

            // Aggiungi il pannello grafico alla finestra principale
            GraphPanel graphPanel = new GraphPanel();
            frame.add(graphPanel);

            // Mostra la finestra
            frame.setVisible(true);
        });
    }
}
package gui;

import javax.swing.*;

import gui.components.DependencyAnalyserPanel;

/**
 * MainGUI is the entry point of the application.
 * It initializes the main window (JFrame) and sets up the DependencyAnalyserPanel
 * to provide the user interface for analyzing dependencies in a Java project.
 */
public class MainGUI {

    /**
     * The main method that launches the application.
     *
     * @param args Command-line arguments (not used in this application).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Create the main application window
            JFrame frame = new JFrame("Reactive Dependency Analyser");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1000, 800); // Set the size of the window
            frame.setLocationRelativeTo(null); // Center the window on the screen

            // Add the DependencyAnalyserPanel to the frame
            DependencyAnalyserPanel analyserPanel = new DependencyAnalyserPanel();
            frame.setContentPane(analyserPanel);

            // Make the window visible
            frame.setVisible(true);
        });
    }
}
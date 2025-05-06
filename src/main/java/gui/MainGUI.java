package gui;

import javax.swing.*;

import gui.components.DependencyAnalyserPanel;

public class MainGUI {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Reactive Dependency Analyser");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(950, 700);
            frame.setLocationRelativeTo(null);

            DependencyAnalyserPanel analyserPanel = new DependencyAnalyserPanel();
            frame.setContentPane(analyserPanel);
            frame.setVisible(true);
        });
    }
}

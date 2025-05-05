package gui.components;

import gui.components.utils.ReactiveDependencyAnalyser;
import io.reactivex.rxjava3.schedulers.Schedulers;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DependencyAnalyserPanel extends JPanel {

    private final JTextArea outputArea;
    private final JButton startButton;
    private final JTextField folderField;

    public DependencyAnalyserPanel() {
        this.setLayout(new BorderLayout());

        folderField = new JTextField("src/main/java"); // Default path
        startButton = new JButton("Analizza");
        outputArea = new JTextArea();
        outputArea.setEditable(false);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(new JLabel("Folder:"), BorderLayout.WEST);
        topPanel.add(folderField, BorderLayout.CENTER);
        topPanel.add(startButton, BorderLayout.EAST);

        this.add(topPanel, BorderLayout.NORTH);
        this.add(new JScrollPane(outputArea), BorderLayout.CENTER);

        startButton.addActionListener(e -> startAnalysis());
    }

    private void startAnalysis() {
        outputArea.setText("Analisi in corso...\n");

        Path path = Paths.get(folderField.getText());
        ReactiveDependencyAnalyser analyser = new ReactiveDependencyAnalyser();

        analyser.analyzeDependencies(path)
                .subscribeOn(Schedulers.io())
                .observeOn(io.reactivex.rxjava3.schedulers.Schedulers.trampoline())
                .subscribe(
                        deps -> SwingUtilities.invokeLater(() -> outputArea.append(String.join(", ", deps) + "\n")),
                        error -> SwingUtilities.invokeLater(() -> outputArea.append("Errore: " + error.getMessage() + "\n")),
                        () -> SwingUtilities.invokeLater(() -> outputArea.append("Analisi completata.\n"))
                );
    }
}

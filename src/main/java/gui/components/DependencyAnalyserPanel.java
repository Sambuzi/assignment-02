package gui.components;

import gui.components.utils.ReactiveDependencyAnalyser;
import io.reactivex.rxjava3.schedulers.Schedulers;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicInteger;

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

        JButton folderButton = new JButton("Seleziona Cartella");
        folderButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int result = chooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                folderField.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        });
        topPanel.add(folderButton, BorderLayout.EAST);

        topPanel.add(startButton, BorderLayout.SOUTH);
        this.add(topPanel, BorderLayout.NORTH);

        this.add(new JScrollPane(outputArea), BorderLayout.CENTER);

        JPanel statsPanel = new JPanel(new GridLayout(1, 2));
        JLabel classesLabel = new JLabel("Classi/Interfacce Analizzate:");
        JLabel dependenciesLabel = new JLabel("Dipendenze Trovate:");
        statsPanel.add(classesLabel);
        statsPanel.add(dependenciesLabel);
        this.add(statsPanel, BorderLayout.SOUTH);

        GraphPanel graphPanel = new GraphPanel();
        this.add(graphPanel, BorderLayout.EAST);

        startButton.addActionListener(e -> startAnalysis(classesLabel, dependenciesLabel, graphPanel));
    }

    private void startAnalysis(JLabel classesLabel, JLabel dependenciesLabel, GraphPanel graphPanel) {
        outputArea.setText("Analisi in corso...\n");
    
        Path path = Paths.get(folderField.getText());
        ReactiveDependencyAnalyser analyser = new ReactiveDependencyAnalyser();
    
        AtomicInteger classCount = new AtomicInteger(0);
        AtomicInteger dependencyCount = new AtomicInteger(0);
    
        analyser.analyzeDependencies(path)
                .subscribeOn(Schedulers.io())
                .observeOn(io.reactivex.rxjava3.schedulers.Schedulers.trampoline())
                .subscribe(
                        deps -> SwingUtilities.invokeLater(() -> {
                            // Formattazione dell'output
                            StringBuilder formattedOutput = new StringBuilder();
                            formattedOutput.append("Classe: ").append(deps[0]).append("\n");
                            formattedOutput.append("Dipendenze:\n");
                            for (int i = 1; i < deps.length; i++) {
                                formattedOutput.append("  - ").append(deps[i]).append("\n");
                            }
                            formattedOutput.append("\n");
                            outputArea.append(formattedOutput.toString());
    
                            // Aggiornamento del grafo
                            graphPanel.addNode(deps[0]); // Nome della classe
                            for (int i = 1; i < deps.length; i++) {
                                graphPanel.addEdge(deps[0], deps[i]); // Dipendenze
                            }
    
                            // Aggiornamento dei conteggi
                            classCount.incrementAndGet();
                            dependencyCount.addAndGet(deps.length - 1);
                            classesLabel.setText("Classi/Interfacce Analizzate: " + classCount.get());
                            dependenciesLabel.setText("Dipendenze Trovate: " + dependencyCount.get());
                        }),
                        error -> SwingUtilities.invokeLater(() -> outputArea.append("Errore: " + error.getMessage() + "\n")),
                        () -> SwingUtilities.invokeLater(() -> outputArea.append("Analisi completata.\n"))
                );
    }
}
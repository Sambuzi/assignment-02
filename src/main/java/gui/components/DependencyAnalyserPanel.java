package gui.components;

import gui.components.utils.ReactiveDependencyAnalyser;
import io.reactivex.rxjava3.schedulers.Schedulers;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;

public class DependencyAnalyserPanel extends JPanel {
    private final SourceSelector sourceSelector;
    private final OutputBox outputBox;
    private final GraphPanel graphPanel;

    public DependencyAnalyserPanel() {
        this.setLayout(new BorderLayout());

        sourceSelector = new SourceSelector();
        outputBox = new OutputBox();
        graphPanel = new GraphPanel();

        JPanel topPanel = new JPanel(new BorderLayout());
        JButton startButton = new JButton("Analizza");
        topPanel.add(sourceSelector, BorderLayout.CENTER);
        topPanel.add(startButton, BorderLayout.SOUTH);

        this.add(topPanel, BorderLayout.NORTH);
        this.add(outputBox, BorderLayout.CENTER);

        // Avvolgi il GraphPanel in uno JScrollPane
        JScrollPane scrollPane = new JScrollPane(graphPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Incremento per lo scrolling fluido
        this.add(scrollPane, BorderLayout.EAST);

        JPanel statsPanel = new JPanel(new GridLayout(1, 2));
        JLabel classesLabel = new JLabel("Classi/Interfacce Analizzate:");
        JLabel dependenciesLabel = new JLabel("Dipendenze Trovate:");
        statsPanel.add(classesLabel);
        statsPanel.add(dependenciesLabel);
        this.add(statsPanel, BorderLayout.SOUTH);

        startButton.addActionListener(e -> startAnalysis(classesLabel, dependenciesLabel));
    }

    private void startAnalysis(JLabel classesLabel, JLabel dependenciesLabel) {
        outputBox.setText("Analisi in corso...\n");

        Path path = Path.of(sourceSelector.getSelectedPath());
        ReactiveDependencyAnalyser analyser = new ReactiveDependencyAnalyser();

        AtomicInteger classCount = new AtomicInteger(0);
        AtomicInteger dependencyCount = new AtomicInteger(0);

        analyser.analyzeDependencies(path)
                .subscribeOn(Schedulers.io())
                .observeOn(io.reactivex.rxjava3.schedulers.Schedulers.trampoline())
                .subscribe(
                        deps -> SwingUtilities.invokeLater(() -> {
                            StringBuilder formattedOutput = new StringBuilder();
                            formattedOutput.append("Classe: ").append(deps[0]).append("\n");
                            formattedOutput.append("Dipendenze:\n");
                            for (int i = 1; i < deps.length; i++) {
                                formattedOutput.append("  - ").append(deps[i]).append("\n");
                            }
                            formattedOutput.append("\n");
                            outputBox.appendText(formattedOutput.toString());

                            graphPanel.addNode(deps[0]);
                            for (int i = 1; i < deps.length; i++) {
                                graphPanel.addEdge(deps[0], deps[i]);
                            }

                            classCount.incrementAndGet();
                            dependencyCount.addAndGet(deps.length - 1);
                            classesLabel.setText("Classi/Interfacce Analizzate: " + classCount.get());
                            dependenciesLabel.setText("Dipendenze Trovate: " + dependencyCount.get());
                        }),
                        error -> SwingUtilities.invokeLater(() -> {
                            outputBox.appendText("Errore: " + error.getMessage() + "\n");
                            JOptionPane.showMessageDialog(this, "Errore durante l'analisi: " + error.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
                        }),
                        () -> SwingUtilities.invokeLater(() -> outputBox.appendText("Analisi completata.\n"))
                );
    }
}
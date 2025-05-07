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

        // Pannello superiore con il selettore e il pulsante
        JPanel topPanel = new JPanel(new BorderLayout());
        JButton startButton = new JButton("Analizza");
        topPanel.add(sourceSelector, BorderLayout.CENTER);
        topPanel.add(startButton, BorderLayout.SOUTH);
        this.add(topPanel, BorderLayout.NORTH);

        // Pannello sinistro con l'output
        JScrollPane outputScrollPane = new JScrollPane(outputBox);
        outputScrollPane.setPreferredSize(new Dimension(300, getHeight())); // Dimensione preferita per il pannello di output
        this.add(outputScrollPane, BorderLayout.WEST);

        // Avvolgi il GraphPanel in uno JScrollPane
        JScrollPane scrollPane = new JScrollPane(graphPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Incremento per lo scrolling fluido
        this.add(scrollPane, BorderLayout.CENTER);

        // Pannello inferiore con la legenda
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        legendPanel.add(new JLabel("Legenda:"));
        JLabel packageLabel = new JLabel("Package");
        packageLabel.setOpaque(true);
        packageLabel.setBackground(new Color(173, 216, 230)); // Light blue
        legendPanel.add(packageLabel);

        JLabel classLabel = new JLabel("Classe");
        classLabel.setOpaque(true);
        classLabel.setBackground(Color.LIGHT_GRAY); // Light gray
        legendPanel.add(classLabel);

        // Aggiungi etichette per il conteggio di classi e dipendenze
        JLabel classesLabel = new JLabel("Classi/Interfacce Analizzate: 0");
        JLabel dependenciesLabel = new JLabel("Dipendenze Trovate: 0");
        legendPanel.add(Box.createHorizontalStrut(20)); // Spazio tra la legenda e le statistiche
        legendPanel.add(classesLabel);
        legendPanel.add(Box.createHorizontalStrut(10)); // Spazio tra le statistiche
        legendPanel.add(dependenciesLabel);

        this.add(legendPanel, BorderLayout.SOUTH);

        // Aggiungi azione al pulsante
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
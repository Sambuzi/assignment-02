package gui.components;

import gui.components.utils.ReactiveDependencyAnalyser;
import io.reactivex.rxjava3.schedulers.Schedulers;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * DependencyAnalyserPanel is a custom JPanel that provides a user interface
 * for analyzing dependencies in a project. It includes a source selector,
 * an output box for displaying results, and a graph panel for visualizing
 * the dependency graph.
 */
public class DependencyAnalyserPanel extends JPanel {
    private final SourceSelector sourceSelector;
    private final OutputBox outputBox;
    private final GraphPanel graphPanel;

    /**
     * Constructs a DependencyAnalyserPanel with all its components.
     */
    public DependencyAnalyserPanel() {
        this.setLayout(new BorderLayout());

        sourceSelector = new SourceSelector();
        outputBox = new OutputBox();
        graphPanel = new GraphPanel();

        // Top panel with the source selector and the analyze button
        JPanel topPanel = new JPanel(new BorderLayout());
        JButton startButton = new JButton("Analyze");
        startButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        startButton.setBackground(new Color(60, 179, 113)); // Light green
        startButton.setForeground(Color.WHITE);
        startButton.setFocusPainted(false);

        JButton exitButton = new JButton("Exit");
        exitButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        exitButton.setBackground(new Color(255, 69, 0)); // Red
        exitButton.setForeground(Color.WHITE);
        exitButton.setFocusPainted(false);
        exitButton.addActionListener(e -> System.exit(0)); // Exit the application

        topPanel.add(sourceSelector, BorderLayout.CENTER);
        topPanel.add(startButton, BorderLayout.SOUTH);
        this.add(topPanel, BorderLayout.NORTH);
        topPanel.add(exitButton, BorderLayout.EAST); // Add exit button to the top panel
        // Left panel with the output box
        JPanel outputPanel = new JPanel(new BorderLayout());
        JLabel outputHeader = new JLabel("Analysis Output");
        outputHeader.setFont(new Font("SansSerif", Font.BOLD, 14));
        outputHeader.setHorizontalAlignment(SwingConstants.CENTER);
        outputHeader.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        outputPanel.add(outputHeader, BorderLayout.NORTH);

        JScrollPane outputScrollPane = new JScrollPane(outputBox);
        outputScrollPane.setPreferredSize(new Dimension(300, getHeight())); // Preferred size for the output panel
        outputPanel.add(outputScrollPane, BorderLayout.CENTER);
        this.add(outputPanel, BorderLayout.WEST);

        // Center panel with the graph panel (no scroll)
        this.add(graphPanel, BorderLayout.CENTER);

        // Bottom panel with the legend and statistics
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        legendPanel.add(new JLabel("Legend:"));
        JLabel packageLabel = new JLabel("Package");
        packageLabel.setOpaque(true);
        packageLabel.setBackground(new Color(173, 216, 230)); // Light blue
        legendPanel.add(packageLabel);

        JLabel classLabel = new JLabel("Class");
        classLabel.setOpaque(true);
        classLabel.setBackground(Color.LIGHT_GRAY); // Light gray
        legendPanel.add(classLabel);

        // Add labels for class and dependency counts
        JLabel classesLabel = new JLabel("Classes/Interfaces Analyzed: 0");
        JLabel dependenciesLabel = new JLabel("Dependencies Found: 0");
        legendPanel.add(Box.createHorizontalStrut(20)); // Space between legend and statistics
        legendPanel.add(classesLabel);
        legendPanel.add(Box.createHorizontalStrut(10)); // Space between statistics
        legendPanel.add(dependenciesLabel);

        this.add(legendPanel, BorderLayout.SOUTH);

        // Add action to the analyze button
        startButton.addActionListener(e -> startAnalysis(classesLabel, dependenciesLabel));
    }

    /**
     * Starts the dependency analysis process.
     *
     * @param classesLabel      The label to update with the number of analyzed classes.
     * @param dependenciesLabel The label to update with the number of found dependencies.
     */
    private void startAnalysis(JLabel classesLabel, JLabel dependenciesLabel) {
        outputBox.setText("Analysis in progress...\n");
    
        Path path = Path.of(sourceSelector.getSelectedPath());
        ReactiveDependencyAnalyser analyser = new ReactiveDependencyAnalyser();
    
        AtomicInteger classCount = new AtomicInteger(0);
        AtomicInteger dependencyCount = new AtomicInteger(0);
    
        analyser.analyzeDependencies(path)
                .subscribeOn(Schedulers.io())
                .observeOn(io.reactivex.rxjava3.schedulers.Schedulers.trampoline())
                .subscribe(
                        deps -> SwingUtilities.invokeLater(() -> {
                            // Clear the output box for new results
                            outputBox.appendText("\n");
    
                            // Draw "Class:" in red
                            outputBox.appendColoredText("Class: ", Color.RED);
                            outputBox.appendText(deps[0] + "\n");
    
                            // Draw "Dependencies:" in red
                            outputBox.appendColoredText("Dependencies:\n", Color.RED);
                            for (int i = 1; i < deps.length; i++) {
                                outputBox.appendText("  - " + deps[i] + "\n");
                            }
    
                            // Add a blank line for separation
                            outputBox.appendText("\n");
    
                            graphPanel.addNode(deps[0]);
                            for (int i = 1; i < deps.length; i++) {
                                graphPanel.addEdge(deps[0], deps[i]);
                            }
    
                            classCount.incrementAndGet();
                            dependencyCount.addAndGet(deps.length - 1);
                            classesLabel.setText("Classes/Interfaces Analyzed: " + classCount.get());
                            dependenciesLabel.setText("Dependencies Found: " + dependencyCount.get());
                        }),
                        error -> SwingUtilities.invokeLater(() -> {
                            outputBox.appendColoredText("Error: " + error.getMessage() + "\n", Color.RED);
                            JOptionPane.showMessageDialog(this, "Error during analysis: " + error.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        }),
                        () -> SwingUtilities.invokeLater(() -> outputBox.appendText("Analysis completed.\n"))
                );
    }
}
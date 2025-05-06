package gui.components;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A panel for displaying graphical representations of dependencies.
 */
public class GraphPanel extends JPanel {
    private final List<String> nodes = new ArrayList<>();
    private final List<String[]> edges = new ArrayList<>();
    private final Map<String, Point> nodePositions = new HashMap<>();

    public GraphPanel() {
        setPreferredSize(new Dimension(600, 600));
        setBackground(Color.WHITE);
    }

    /**
     * Adds a node to the graph.
     *
     * @param node The name of the node (e.g., class name).
     */
    public void addNode(String node) {
        if (!nodes.contains(node)) {
            nodes.add(node);
            calculateNodePositions(); // Recalculate positions when a new node is added
            repaint();
        }
    }

    /**
     * Adds an edge between two nodes.
     *
     * @param from The starting node.
     * @param to   The ending node.
     */
    public void addEdge(String from, String to) {
        edges.add(new String[]{from, to});
        repaint();
    }

    /**
     * Calculates positions for nodes in a circular layout.
     */
    private void calculateNodePositions() {
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        int radius = Math.min(getWidth(), getHeight()) / 3 + nodes.size() * 10; // Adjust radius based on number of nodes

        int totalNodes = nodes.size();
        for (int i = 0; i < totalNodes; i++) {
            double angle = 2 * Math.PI * i / totalNodes;
            int x = (int) (centerX + radius * Math.cos(angle));
            int y = (int) (centerY + radius * Math.sin(angle));
            nodePositions.put(nodes.get(i), new Point(x, y));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw edges
        g2d.setColor(Color.GRAY);
        for (String[] edge : edges) {
            Point from = nodePositions.get(edge[0]);
            Point to = nodePositions.get(edge[1]);
            if (from != null && to != null) {
                g2d.drawLine(from.x, from.y, to.x, to.y);
            }
        }

        // Draw nodes
        g2d.setColor(Color.LIGHT_GRAY);
        for (Map.Entry<String, Point> entry : nodePositions.entrySet()) {
            Point position = entry.getValue();
            String node = entry.getKey();

            // Draw node circle
            int nodeSize = 50;
            g2d.fillOval(position.x - nodeSize / 2, position.y - nodeSize / 2, nodeSize, nodeSize);
            g2d.setColor(Color.BLACK);
            g2d.drawOval(position.x - nodeSize / 2, position.y - nodeSize / 2, nodeSize, nodeSize);

            // Draw node label
            FontMetrics fm = g2d.getFontMetrics();
            int labelWidth = fm.stringWidth(node);
            g2d.drawString(node, position.x - labelWidth / 2, position.y + fm.getAscent() / 2);

            g2d.setColor(Color.LIGHT_GRAY);
        }
    }
}
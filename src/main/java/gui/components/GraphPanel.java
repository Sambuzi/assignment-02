package gui.components;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class GraphPanel extends JPanel {
    private final List<String> nodes = new ArrayList<>();
    private final List<String[]> edges = new ArrayList<>();
    private final Map<String, Point> nodePositions = new HashMap<>();
    private final Set<String> packageNodes = new HashSet<>();

    public GraphPanel() {
        setPreferredSize(new Dimension(500, 500));
        setBackground(Color.WHITE);
    }

    public void addNode(String node) {
        String packageName = node.contains(".") ? node.substring(0, node.lastIndexOf('.')) : "default";
        if (!nodes.contains(packageName)) {
            nodes.add(packageName);
            packageNodes.add(packageName);
        }
        if (!nodes.contains(node)) {
            nodes.add(node);
            edges.add(new String[]{packageName, node});
        }
        calculateNodePositions();
        repaint();
    }

    public void addEdge(String from, String to) {
        edges.add(new String[]{from, to});
        calculateNodePositions();
        repaint();
    }

    private void calculateNodePositions() {
        int yOffset = 150; // Distanza verticale tra i grafi
        int packageX = getWidth() / 2; // Posizione orizzontale centrale
        int packageY = yOffset; // Posizione verticale iniziale

        int classRadius = 100; // Raggio per le classi all'interno di un package

        for (String packageName : packageNodes) {
            // Posizionamento del package
            nodePositions.put(packageName, new Point(packageX, packageY));

            // Posizionamento delle classi all'interno del package
            List<String> classNodes = new ArrayList<>();
            for (String node : nodes) {
                if (!packageNodes.contains(node) && node.startsWith(packageName)) {
                    classNodes.add(node);
                }
            }

            int totalClasses = classNodes.size();
            for (int i = 0; i < totalClasses; i++) {
                double angle = 2 * Math.PI * i / totalClasses;
                int x = (int) (packageX + classRadius * Math.cos(angle));
                int y = (int) (packageY + classRadius * Math.sin(angle));
                nodePositions.put(classNodes.get(i), new Point(x, y));
            }

            // Incrementa la posizione verticale per il prossimo package
            packageY += yOffset + classRadius * 2;
        }

        // Aggiorna la dimensione preferita del pannello per abilitare lo scrolling
        setPreferredSize(new Dimension(getWidth(), packageY));
        revalidate(); // Assicurati che il pannello venga aggiornato
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
        for (String node : nodes) {
            Point position = nodePositions.get(node);
            if (position == null) continue;

            int size = 50;

            if (packageNodes.contains(node)) {
                g2d.setColor(new Color(173, 216, 230)); // Light blue for packages
                g2d.fillRect(position.x - size / 2, position.y - size / 2, size, size);
                g2d.setColor(Color.BLUE);
                g2d.drawRect(position.x - size / 2, position.y - size / 2, size, size);
            } else {
                g2d.setColor(Color.LIGHT_GRAY); // Light gray for classes
                g2d.fillOval(position.x - size / 2, position.y - size / 2, size, size);
                g2d.setColor(Color.BLACK);
                g2d.drawOval(position.x - size / 2, position.y - size / 2, size, size);
            }

            g2d.setColor(Color.BLACK);
            FontMetrics fm = g2d.getFontMetrics();
            int labelWidth = fm.stringWidth(node);
            int labelHeight = fm.getHeight();

            // Posiziona il testo leggermente sotto il nodo
            g2d.drawString(node, position.x - labelWidth / 2, position.y + size / 2 + labelHeight);
        }
    }
}
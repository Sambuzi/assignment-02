package gui.components;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A panel for displaying graphical representations of dependencies.
 */
public class GraphPanel extends JPanel {
    private final List<String> nodes = new ArrayList<>();
    private final List<String[]> edges = new ArrayList<>();

    public GraphPanel() {
        setPreferredSize(new Dimension(400, 400));
        setBackground(Color.WHITE);
    }

    /**
     * Aggiunge un nodo al grafo.
     *
     * @param node Nome del nodo (es. nome della classe).
     */
    public void addNode(String node) {
        nodes.add(node);
        repaint(); // Aggiorna il pannello
    }

    /**
     * Aggiunge un arco tra due nodi.
     *
     * @param from Nodo di partenza.
     * @param to   Nodo di destinazione.
     */
    public void addEdge(String from, String to) {
        edges.add(new String[]{from, to});
        repaint(); // Aggiorna il pannello
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        int x = 50, y = 50;
        int nodeSpacing = 100;

        // Disegna i nodi
        for (String node : nodes) {
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.fillOval(x, y, 100, 50);
            g2d.setColor(Color.BLACK);
            g2d.drawOval(x, y, 100, 50);
            g2d.drawString(node, x + 10, y + 30);
            y += nodeSpacing;
        }

        // Disegna gli archi
        for (String[] edge : edges) {
            String from = edge[0];
            String to = edge[1];
            int fromIndex = nodes.indexOf(from);
            int toIndex = nodes.indexOf(to);

            if (fromIndex != -1 && toIndex != -1) {
                int fromY = 75 + fromIndex * nodeSpacing;
                int toY = 75 + toIndex * nodeSpacing;
                g2d.drawLine(100, fromY, 100, toY);
            }
        }
    }
}
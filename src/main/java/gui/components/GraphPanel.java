package gui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.QuadCurve2D;
import java.util.*;
import java.util.List;

/**
 * GraphPanel is a custom JPanel that visualizes a graph with nodes and edges.
 * It supports zooming, panning, and drawing curved edges with arrows.
 */
public class GraphPanel extends JPanel {
    private final List<String> nodes = new ArrayList<>();
    private final List<String[]> edges = new ArrayList<>();
    private final Map<String, Point> nodePositions = new HashMap<>();
    private final Set<String> packageNodes = new HashSet<>();
    private double zoomFactor = 1.0;
    private int offsetX = 0;
    private int offsetY = 0;
    private Point lastDragPoint = null;

    /**
     * Constructs a GraphPanel with default settings.
     */
    public GraphPanel() {
        setBackground(Color.WHITE);

        // Add mouse wheel listener for zooming
        addMouseWheelListener(e -> {
            if (e.getPreciseWheelRotation() < 0) {
                zoomFactor = Math.min(zoomFactor + 0.1, 2.0);
            } else {
                zoomFactor = Math.max(zoomFactor - 0.1, 0.5);
            }
            repaint();
        });

        // Add mouse listener for panning
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lastDragPoint = e.getPoint();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (lastDragPoint != null) {
                    int dx = e.getX() - lastDragPoint.x;
                    int dy = e.getY() - lastDragPoint.y;
                    offsetX += dx;
                    offsetY += dy;
                    lastDragPoint = e.getPoint();
                    repaint();
                }
            }
        });
    }

    /**
     * Adds a node to the graph.
     *
     * @param node The name of the node to add.
     */
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

    /**
     * Adds an edge between two nodes in the graph.
     *
     * @param from The starting node of the edge.
     * @param to   The ending node of the edge.
     */
    public void addEdge(String from, String to) {
        edges.add(new String[]{from, to});
        calculateNodePositions();
        repaint();
    }

    /**
     * Calculates the positions of nodes in the graph.
     */
    private void calculateNodePositions() {
        nodePositions.clear();
        int width = getWidth();
        int height = getHeight();

        int packageCount = packageNodes.size();
        int columns = (int) Math.ceil(Math.sqrt(packageCount));
        int rows = (int) Math.ceil((double) packageCount / columns);
        int cellWidth = width / columns;
        int cellHeight = height / rows;

        List<String> packageList = new ArrayList<>(packageNodes);
        for (int i = 0; i < packageList.size(); i++) {
            int row = i / columns;
            int col = i % columns;
            int x = col * cellWidth + cellWidth / 2;
            int y = row * cellHeight + cellHeight / 4;
            nodePositions.put(packageList.get(i), new Point(x, y));
        }

        for (String pkg : packageNodes) {
            List<String> classNodes = new ArrayList<>();
            for (String node : nodes) {
                if (!packageNodes.contains(node) && node.startsWith(pkg)) {
                    classNodes.add(node);
                }
            }

            int classCount = classNodes.size();
            if (classCount == 0) continue;

            Point center = nodePositions.get(pkg);
            int gridSize = (int) Math.ceil(Math.sqrt(classCount));
            int spacing = 80;
            int startX = center.x - (gridSize * spacing) / 2;
            int startY = center.y + 100;

            for (int i = 0; i < classCount; i++) {
                int row = i / gridSize;
                int col = i % gridSize;
                int x = startX + col * spacing;
                int y = startY + row * spacing;
                nodePositions.put(classNodes.get(i), new Point(x, y));
            }
        }
    }

    /**
     * Paints the graph, including nodes and edges.
     *
     * @param g The Graphics object used for painting.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.translate(offsetX, offsetY);
        g2d.scale(zoomFactor, zoomFactor);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw curved edges with arrows
        g2d.setColor(Color.GRAY);
        for (String[] edge : edges) {
            Point from = nodePositions.get(edge[0]);
            Point to = nodePositions.get(edge[1]);
            if (from != null && to != null) {
                // Draw curved edge
                int controlX = (from.x + to.x) / 2;
                int controlY = (from.y + to.y) / 2 - 50;
                QuadCurve2D curve = new QuadCurve2D.Float(from.x, from.y, controlX, controlY, to.x, to.y);
                g2d.draw(curve);

                // Draw arrow at the end of the edge
                drawArrow(g2d, to.x, to.y, from.x, from.y);
            }
        }

        int circleSize = 50;

        // Draw nodes
        for (String node : nodes) {
            Point pos = nodePositions.get(node);
            if (pos == null) continue;

            if (packageNodes.contains(node)) {
                // Draw package (rectangle)
                g2d.setColor(new Color(173, 216, 230));
                g2d.fillRect(pos.x - 30, pos.y - 30, 60, 60);
                g2d.setColor(Color.BLUE);
                g2d.drawRect(pos.x - 30, pos.y - 30, 60, 60);

                // Draw package name
                g2d.setColor(Color.BLACK);
                Font font = new Font("SansSerif", Font.PLAIN, 10);
                g2d.setFont(font);
                FontMetrics fm = g2d.getFontMetrics();
                int labelWidth = fm.stringWidth(node);
                g2d.drawString(node, pos.x - labelWidth / 2, pos.y + 45);
            } else {
                // Draw class (circle)
                g2d.setColor(Color.LIGHT_GRAY);
                g2d.fillOval(pos.x - circleSize / 2, pos.y - circleSize / 2, circleSize, circleSize);
                g2d.setColor(Color.BLACK);
                g2d.drawOval(pos.x - circleSize / 2, pos.y - circleSize / 2, circleSize, circleSize);

                // Draw class name centered
                String simpleName = node.contains(".") ? node.substring(node.lastIndexOf('.') + 1) : node;
                int fontSize = 12;
                Font font;
                FontMetrics fm;
                int labelWidth;

                do {
                    font = new Font("SansSerif", Font.PLAIN, fontSize);
                    g2d.setFont(font);
                    fm = g2d.getFontMetrics();
                    labelWidth = fm.stringWidth(simpleName);
                    fontSize--;
                } while (labelWidth > circleSize - 10 && fontSize > 6);

                g2d.drawString(simpleName, pos.x - labelWidth / 2, pos.y + fm.getAscent() / 2 - 2);
            }
        }
    }

    /**
     * Draws an arrow at the specified position.
     *
     * @param g2d The Graphics2D object used for drawing.
     * @param x2  The x-coordinate of the arrow tip.
     * @param y2  The y-coordinate of the arrow tip.
     * @param x1  The x-coordinate of the arrow base.
     * @param y1  The y-coordinate of the arrow base.
     */
    private void drawArrow(Graphics2D g2d, int x2, int y2, int x1, int y1) {
        int arrowSize = 15; // Arrow size
        g2d.setColor(Color.BLACK); // Arrow color
        double angle = Math.atan2(y2 - y1, x2 - x1);

        // Calculate arrow points
        int xArrow1 = (int) (x2 - arrowSize * Math.cos(angle - Math.PI / 6));
        int yArrow1 = (int) (y2 - arrowSize * Math.sin(angle - Math.PI / 6));
        int xArrow2 = (int) (x2 - arrowSize * Math.cos(angle + Math.PI / 6));
        int yArrow2 = (int) (y2 - arrowSize * Math.sin(angle + Math.PI / 6));

        // Draw arrow
        g2d.drawLine(x2, y2, xArrow1, yArrow1);
        g2d.drawLine(x2, y2, xArrow2, yArrow2);
        g2d.fillPolygon(new int[]{x2, xArrow1, xArrow2}, new int[]{y2, yArrow1, yArrow2}, 3);
    }
}
package gui.components;

import javax.swing.*;
import java.awt.*;

public class OutputBox extends JPanel {
    private final JTextArea outputArea;

    public OutputBox() {
        this.setLayout(new BorderLayout());
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        this.add(new JScrollPane(outputArea), BorderLayout.CENTER);
    }

    public void appendText(String text) {
        outputArea.append(text);
    }

    public void setText(String text) {
        outputArea.setText(text);
    }
}
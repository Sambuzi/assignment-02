package gui.components;

import javax.swing.*;

/**
 * A text area for displaying output or reports.
 */
public class OutputBox extends JTextArea {
    public OutputBox() {
        setEditable(false);
        setLineWrap(true);
        setWrapStyleWord(true);
    }
}
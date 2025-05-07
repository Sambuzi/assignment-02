package gui.components;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

/**
 * OutputBox is a custom JTextPane for displaying formatted and colored text.
 */
public class OutputBox extends JTextPane {

    /**
     * Constructs an OutputBox with default settings.
     */
    public OutputBox() {
        this.setEditable(false);
        this.setFont(new Font("Monospaced", Font.PLAIN, 12));
    }

    /**
     * Appends plain text to the OutputBox.
     *
     * @param text The text to append.
     */
    public void appendText(String text) {
        appendColoredText(text, Color.BLACK);
    }

    /**
     * Appends colored text to the OutputBox.
     *
     * @param text  The text to append.
     * @param color The color of the text.
     */
    public void appendColoredText(String text, Color color) {
        StyledDocument doc = this.getStyledDocument();
        Style style = this.addStyle("Style", null);
        StyleConstants.setForeground(style, color);
        try {
            doc.insertString(doc.getLength(), text, style);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Clears the content of the OutputBox.
     */
    public void clear() {
        this.setText("");
    }
}
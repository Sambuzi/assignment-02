package gui.components;

import javax.swing.*;
import java.awt.event.ActionListener;

/**
 * A component for selecting the source directory for analysis.
 */
public class SourceSelector extends JPanel {
    private final JTextField sourceField;
    private final JButton browseButton;

    public SourceSelector(ActionListener browseAction) {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        sourceField = new JTextField(30);
        browseButton = new JButton("Browse");
        browseButton.addActionListener(browseAction);

        add(sourceField);
        add(browseButton);
    }

    public String getSourcePath() {
        return sourceField.getText();
    }
}
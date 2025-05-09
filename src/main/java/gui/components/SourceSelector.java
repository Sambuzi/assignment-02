package gui.components;

import javax.swing.*;
import java.awt.*;

/**
 * SourceSelector is a custom JPanel that provides a user interface
 * for selecting a source folder. It includes a text field to display
 * the selected folder path and a button to open a folder chooser dialog.
 */
public class SourceSelector extends JPanel {
    private final JTextField folderField;

    /**
     * Constructs a SourceSelector panel with a text field and a folder selection button.
     * The default folder path is set to "src/main/java".
     */
    public SourceSelector() {
        this.setLayout(new BorderLayout());

        folderField = new JTextField("src/main/java"); // Default path
        JButton folderButton = new JButton("Seleziona Cartella");

        folderButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int result = chooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                folderField.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        });

        this.add(new JLabel("Folder:"), BorderLayout.WEST);
        this.add(folderField, BorderLayout.CENTER);
        this.add(folderButton, BorderLayout.EAST);
    }

    /**
     * Returns the currently selected folder path.
     *
     * @return The selected folder path as a string.
     */
    public String getSelectedPath() {
        return folderField.getText();
    }
}
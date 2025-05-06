package gui.components;

import javax.swing.*;
import java.awt.*;

public class SourceSelector extends JPanel {
    private final JTextField folderField;

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

    public String getSelectedPath() {
        return folderField.getText();
    }
}
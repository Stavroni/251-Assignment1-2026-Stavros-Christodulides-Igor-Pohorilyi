// Wraps the JTextPane (the actual editable text area) plus file New/Open/Save logic
// Using JTextPane instead of JTextArea because it supports StyledDocument,
// which is what you'll need later for syntax highlighting.

package a1.texteditor;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class EditorPanel extends JPanel {

    private final JTextPane textPane;
    private final JFileChooser fileChooser;
    private File currentFile; // null until the user opens or saves a file

    public EditorPanel() {
        setLayout(new BorderLayout());

        textPane = new JTextPane();
        textPane.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));

        JScrollPane scrollPane = new JScrollPane(textPane);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(scrollPane, BorderLayout.CENTER);

        fileChooser = new JFileChooser();
    }

    // Exposed in case you want to attach a DocumentListener/UndoManager
    // from Main later, or hook in a highlighter.
    public JTextPane getTextPane() {
        return textPane;
    }

    public void newFile() {
        textPane.setText("");
        currentFile = null;
    }

    public void openFile() {
        int result = fileChooser.showOpenDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File file = fileChooser.getSelectedFile();
        try {
            String content = Files.readString(file.toPath());
            textPane.setText(content);
            textPane.setCaretPosition(0);
            currentFile = file;
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Could not open file:\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void saveFile() {
        if (currentFile == null) {
            saveFileAs();
            return;
        }
        writeToFile(currentFile);
    }

    public void saveFileAs() {
        int result = fileChooser.showSaveDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File file = fileChooser.getSelectedFile();
        writeToFile(file);
        currentFile = file;
    }

    private void writeToFile(File file) {
        try {
            Files.writeString(file.toPath(), textPane.getText());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Could not save file:\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

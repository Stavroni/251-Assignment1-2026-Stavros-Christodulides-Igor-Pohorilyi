// Wraps the JTextPane (the actual editable text area) plus file New/Open/Save logic
// Using JTextPane instead of JTextArea because it supports StyledDocument,
// which is what you'll need later for syntax highlighting.
//
// Open/Save now understands two formats:
//   - .txt  -> plain text, read/written with java.nio.file
//   - .odt  -> OpenDocument Text, read/written with Apache ODFToolkit (simple-odf)
// Anything else falls back to plain text handling.

package a1.texteditor;

import java.awt.BorderLayout;
import java.awt.Font;
import java.io.File;
import java.nio.file.Files;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.common.TextExtractor;

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
        fileChooser.setAcceptAllFileFilterUsed(true);
        fileChooser.addChoosableFileFilter(
                new FileNameExtensionFilter("Text files (*.txt)", "txt"));
        fileChooser.addChoosableFileFilter(
                new FileNameExtensionFilter("OpenDocument Text (*.odt)", "odt"));
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
            String content = isOdt(file) ? readOdt(file) : Files.readString(file.toPath());
            textPane.setText(content);
            textPane.setCaretPosition(0);
            currentFile = file;
        } catch (Exception e) {
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

        // If the user didn't type an extension, default to .txt so we know
        // which writer to use later.
        if (!isOdt(file) && !isTxt(file)) {
            file = new File(file.getParentFile(), file.getName() + ".txt");
        }

        writeToFile(file);
        currentFile = file;
    }

    private void writeToFile(File file) {
        try {
            if (isOdt(file)) {
                writeOdt(file);
            } else {
                Files.writeString(file.toPath(), textPane.getText());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Could not save file:\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ---- format helpers -----------------------------------------------

    private boolean isOdt(File file) {
        return file.getName().toLowerCase().endsWith(".odt");
    }

    private boolean isTxt(File file) {
        return file.getName().toLowerCase().endsWith(".txt");
    }

    // Reads an .odt file and returns its plain text content, paragraphs
    // separated by newlines. Uses ODFToolkit's TextExtractor, which walks
    // the document's content root and pulls out display text without any
    // ODF/XML markup.
    private String readOdt(File file) throws Exception {
        TextDocument doc = TextDocument.loadDocument(file);
        try {
            return TextExtractor.getText(doc.getContentRoot());
        } finally {
            doc.close();
        }
    }

    // Writes the current textPane contents out as a new .odt file, one
    // paragraph per line of text. This intentionally keeps formatting
    // simple (no styles/fonts carried over) since the editor itself is
    // plain text for now.
    private void writeOdt(File file) throws Exception {
        TextDocument doc = TextDocument.newTextDocument();
        try {
            // newTextDocument() ships with a single empty paragraph already
            // in it, so remove that placeholder before adding real content.
            doc.getContentRoot().removeChild(doc.getContentRoot().getFirstChild());

            String[] lines = textPane.getText().split("\n", -1);
            for (String line : lines) {
                doc.addParagraph(line);
            }
            doc.save(file);
        } finally {
            doc.close();
        }
    }

    //------------------------------------------------------------
    //helpfull textpane documentation; https://www.geeksforgeeks.org/java/java-jtextpane/

    public void selectText() {
        textPane.selectAll();
    }

    public void cutText() {
        textPane.cut();
    }

    public void copyText() {
        textPane.copy();
    }

    public void pasteText() {
        textPane.paste();
    }

    //getters to implement action listeners for sccp




}

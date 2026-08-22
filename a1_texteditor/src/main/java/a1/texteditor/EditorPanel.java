// Wraps the JTextPane (the actual editable text area) plus file New/Open/Save logic
// Using JTextPane instead of JTextArea because it supports StyledDocument,
// which is what you'll need later for syntax highlighting.
//
// Open/Save now understands three formats:
//   - .txt  -> plain text, read/written with java.nio.file
//   - .odt  -> OpenDocument Text, read/written with Apache ODFToolkit (simple-odf)
//   - .rtf  -> Rich Text Format, read/written with the JDK's built-in RTFEditorKit
// Anything else falls back to plain text handling.

package a1.texteditor;

import java.awt.BorderLayout;
import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.rtf.RTFEditorKit;

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
        fileChooser.addChoosableFileFilter(
                new FileNameExtensionFilter("Rich Text Format (*.rtf)", "rtf"));
    }

    // Exposed in case you want to attach a DocumentListener/UndoManager
    // from Main later, or hook in a highlighter.
    public JTextPane getTextPane() {
        return textPane;
    }

    private final Highlighter.HighlightPainter searchPainter =
            new DefaultHighlighter.DefaultHighlightPainter(java.awt.Color.YELLOW);

    public int searchText(String query) {
        Highlighter highlighter = textPane.getHighlighter();
        highlighter.removeAllHighlights(); // clear previous search's highlights

        if (query == null || query.isEmpty()) {
            return 0;
        }

        String content = textPane.getText();
        String lowerContent = content.toLowerCase();
        String lowerQuery = query.toLowerCase();

        int index = 0;
        int matches = 0;
        while ((index = lowerContent.indexOf(lowerQuery, index)) != -1) {
            try {
                highlighter.addHighlight(index, index + query.length(), searchPainter);
            } catch (BadLocationException e) {
                // shouldn't happen since index comes from the text itself
            }
            index += query.length();
            matches++;
        }

        if (matches > 0) {
            // jump the caret to the first match so the user sees it immediately
            int firstMatch = lowerContent.indexOf(lowerQuery);
            textPane.setCaretPosition(firstMatch);
        }
        return matches;
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
            if (isOdt(file)) {
                textPane.setText(readOdt(file));
            } else if (isRtf(file)) {
                readRtf(file);
            } else {
                textPane.setText(Files.readString(file.toPath()));
            }
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
        if (!isOdt(file) && !isRtf(file) && !isTxt(file)) {
            file = new File(file.getParentFile(), file.getName() + ".txt");
        }

        writeToFile(file);
        currentFile = file;
    }

    private void writeToFile(File file) {
        try {
            if (isOdt(file)) {
                writeOdt(file);
            } else if (isRtf(file)) {
                writeRtf(file);
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

    private boolean isRtf(File file) {
        return file.getName().toLowerCase().endsWith(".rtf");
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

    // Reads an .rtf file straight into the textPane using the JDK's
    // built-in RTFEditorKit, which understands basic fonts/styles/colors.
    // This replaces the textPane's underlying Document with the one the
    // kit builds while parsing, so formatting from the RTF file survives.
    private void readRtf(File file) throws Exception {
        RTFEditorKit rtfKit = new RTFEditorKit();
        javax.swing.text.Document rtfDoc = rtfKit.createDefaultDocument();
        try (FileInputStream in = new FileInputStream(file)) {
            rtfKit.read(in, rtfDoc, 0);
        }
        textPane.setDocument(rtfDoc);
    }

    // Writes the textPane's current document out as RTF, preserving
    // whatever character/paragraph formatting is present in the styled
    // document (bold, italic, fonts, etc.).
    private void writeRtf(File file) throws Exception {
        RTFEditorKit rtfKit = new RTFEditorKit();
        try (FileOutputStream out = new FileOutputStream(file)) {
            rtfKit.write(out, textPane.getDocument(), 0, textPane.getDocument().getLength());
        }
    }

    //------------------------------------------------------------
    //helpfull textpane documentation; https://www.geeksforgeeks.org/java/java-jtextpane/

    //idk what select text could otherwise imply cuz you have to hover over text to select it anyway, thus making a
    //  select text which saves the text as e.g a string to be used by copy/cut redundant
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

    //print textpane
    public void printPane() {
        try {
            textPane.print();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Could not print file:\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

    }




}

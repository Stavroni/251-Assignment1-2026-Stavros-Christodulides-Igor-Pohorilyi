// program entry
package a1.texteditor;

import java.awt.BorderLayout;
import java.awt.Container;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.JFrame;
import javax.swing.JMenuItem;

public class Main extends JFrame {

    //edit to change window dimentions
    private static final int FRAME_WIDTH  = 600;
    private static final int PANEL_HEIGHT = 600;
    private static final int FRAME_HEIGHT = PANEL_HEIGHT + 160;


    private JMenuItem openItem, quitItem, aboutItem;

    public Main() {
        super("Text editor");

        // properties of jframe
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        this.setResizable(false);

        //add in panels
        menuPanel menuBar = new menuPanel();
        this.setJMenuBar(menuBar);

        EditorPanel editorPanel = new EditorPanel();

        // BorderLayout instead of null: with null layout nothing gets
        // sized/positioned unless you setBounds() on it yourself, and it
        // won't respond properly to the frame's size. BorderLayout.CENTER
        // makes the editor fill all remaining space automatically.
        Container content = this.getContentPane();
        content.setLayout(new BorderLayout());
        content.add(editorPanel, BorderLayout.CENTER);

        // Wire up File menu actions to the editor panel
        menuBar.getNewItem().addActionListener(e -> editorPanel.newFile());
        menuBar.getOpenItem().addActionListener(e -> editorPanel.openFile());
        menuBar.getSaveItem().addActionListener(e -> editorPanel.saveFile());

        menuBar.getviewTime().addActionListener(e -> {
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Calendar cal = Calendar.getInstance();
            String timeString = dateFormat.format(cal.getTime()) + "\n";

            try {
                editorPanel.getTextPane().getDocument().insertString(0, timeString, null);
                editorPanel.getTextPane().setCaretPosition(0);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        //ADD action listeners for sccp:
        menuBar.getSelectText().addActionListener(e -> {editorPanel.selectText(); });
        menuBar.getCutText().addActionListener(e -> {editorPanel.cutText(); });
        menuBar.getCopyText().addActionListener(e -> {editorPanel.copyText(); });
        menuBar.getPasteText().addActionListener(e -> {editorPanel.pasteText(); });

        //-----------------------------

        // By making our JFrame visible, the JVM will call the
        // paintComponent() method of any JPanels registered to the
        // JFrame
        this.setVisible(true);

    }

    public static void main(String[] args) {new Main();}
}

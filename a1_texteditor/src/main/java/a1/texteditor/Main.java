
package a1.texteditor;

import java.awt.Container;

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


        // This allows panels to be displayed properly
        Container content = this.getContentPane();
        content.setLayout(null);

        // By making our JFrame visible, the JVM will call the
        // paintComponent() method of any JPanels registered to the
        // JFrame
        this.setVisible(true);

    }

    public static void main(String[] args) {new Main();}
}

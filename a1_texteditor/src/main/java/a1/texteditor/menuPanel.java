//file to handle addition of menuitems for e.g file, view search etc. Also handles menuitem functionality

package a1.texteditor;

import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class menuPanel extends JMenuBar{
    //create filemenu items
    private JMenuItem newItem, openItem, saveItem, exitItem, viewTime, selectText, cutText, copyText, pastetext;
    private JTextField searchfeild;

    public menuPanel () {
    //--------------------------------------------------------------------------------
    JMenu fileMenu = new JMenu("File");
    newItem = new JMenuItem("New");
    openItem = new JMenuItem("Open");
    saveItem = new JMenuItem("Save");
    exitItem = new JMenuItem("Exit");

    exitItem.addActionListener(e -> System.exit(0));//First action listener to test if will work

    fileMenu.add(newItem);
    fileMenu.add(openItem);
    fileMenu.add(saveItem);
    fileMenu.add(exitItem);
    
    //-------------------------------------------------------------------------------
    JMenu searchMenu = new JMenu("Search");

    JPanel searchPanel = new JPanel();

    JTextField searchField = new JTextField(8);
    JButton searchButton = new JButton("Search");

    searchPanel.add(searchField);
    searchPanel.add(searchButton);


    searchMenu.add(searchPanel);

    //create logic for searching
        /*
        searchbutton.addActionListener(e -> {
    
    });
     */


    //--------------------------------------------------------------------------------
    JMenu viewMenu = new JMenu("View");
    viewTime = new JMenuItem("Time & date");

    viewMenu.add(viewTime);
    //incase t&d has to be real time updated: https://stackoverflow.com/questions/13811224/java-display-current-time

    //--------------------------------------------------------------------------------
    JMenu manageMenu = new JMenu("Manage");
    selectText = new JMenuItem("Select");
    cutText = new JMenuItem("Cut");
    copyText = new JMenuItem("Copy");
    pastetext = new JMenuItem("Paste");

    manageMenu.add(selectText);
    manageMenu.add(cutText);
    manageMenu.add(copyText);
    manageMenu.add(pastetext);

    //--------------------------------------------------------------------------------
    JMenu helpMenu = new JMenu("Help");


    this.add(fileMenu);
    this.add(searchMenu);
    this.add(viewMenu);
    this.add(manageMenu);
    this.add(helpMenu);


    }

    // Getters so Main can attach the actual functionality
    public JMenuItem getNewItem() { return newItem; }
    public JMenuItem getOpenItem() { return openItem; }
    public JMenuItem getSaveItem() { return saveItem; }
    public JMenuItem getExitItem() { return exitItem; }

    public JMenuItem getviewTime() { return viewTime; }
    
    //SCCP items
    public JMenuItem getSelectText() {return selectText; }
    public JMenuItem getCutText() {return cutText; }
    public JMenuItem getCopyText() {return copyText; }
    public JMenuItem getPasteText() {return pastetext; }
}
//file to handle addition of menuitems for e.g file, view search etc. Also handles menuitem functionality

package a1.texteditor;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class menuPanel extends JMenuBar{
    //create filemenu items
    private JMenuItem newItem, openItem, saveItem, exitItem;

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
    
    //--------------------------------------------------------------------------------
    JMenu viewMenu = new JMenu("view");

    //--------------------------------------------------------------------------------
    JMenu manageMenu = new JMenu("Manage");

    //--------------------------------------------------------------------------------
    JMenu helpMenu = new JMenu("Help");


    this.add(fileMenu);
    this.add(searchMenu);
    this.add(manageMenu);
    this.add(helpMenu);


    }

}
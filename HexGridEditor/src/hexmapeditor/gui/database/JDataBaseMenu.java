package hexmapeditor.gui.database;

import core.HexGridEditorMain;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/**
 *
 * @author roah
 */
public final class JDataBaseMenu extends JMenu {
    private final JDataBaseHolder holder;

    public JDataBaseMenu(HexGridEditorMain main) {
        super("Database");
        holder = new JDataBaseHolder(main);
        addMenu("HexGrid Data", null);
    }
    

//    private static void createTabs(){
//        tabbedPane = new JTabbedPane();
//       
//        canvasPanel1 = new JPanel();
//        canvasPanel1.setLayout(new BorderLayout());
//        tabbedPane.addTab("jME3 Canvas 1", canvasPanel1);
//       
//        canvasPanel2 = new JPanel();
//        canvasPanel2.setLayout(new BorderLayout());
//        tabbedPane.addTab("jME3 Canvas 2", canvasPanel2);
//       
//        frame.getContentPane().add(tabbedPane);
//       
//        currentPanel = canvasPanel1;
//    }

    public void addMenu(String dockName, JPanel dockPanel) {
        holder.add(dockName, dockPanel);
        add(new AbstractAction(dockName) {
            @Override
            public void actionPerformed(ActionEvent e) {
                holder.setVisible(e.getActionCommand(), true);
            }
        });
    }
    
    private void onAction(ActionEvent e) {
        switch(e.getActionCommand()){
//            case "root"
        }
    }
}

package hexmapeditor.gui.database;

import core.HexGridEditorMain;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

/**
 *
 * @author roah
 */
public class JDataBaseHolder {

    private final HexGridEditorMain main;
    private final HashMap<String, JPanel> docks = new HashMap<>();
    private final JDialog rootPanel;
    private final JPanel dockTabs;
    private final ButtonGroup dockBtnGroup;

    public JDataBaseHolder(HexGridEditorMain main) {
        this.main = main;
        /**
         * Panel who hold everything.
         */
        rootPanel = new JDialog(main.getRootWindow(), "Database", true);

        rootPanel.setMaximumSize(new Dimension(
                (int)(main.getRootWindow().getWidth()*0.1f), 
                (int)(main.getRootWindow().getHeight()*0.8f)));
        rootPanel.setLayout(new BorderLayout());
        rootPanel.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        rootPanel.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                rootPanel.setVisible(false);
            }
        });

        /**
         * Panel containing all tabs for navigation.
         */
        dockTabs = new JPanel(new GridLayout(0, 10));
//        dockTabs.setBorder(BorderFactory.createBevelBorder(-1));//createLineBorder(Color.BLACK));
//        dockTabs.setLayout(new BoxLayout(dockTabs, BoxLayout.LINE_AXIS));
//        dockTabs.setAlignmentX(0);
//        dockTabs.setPreferredSize(new Dimension(Integer.MAX_VALUE, 30));
        rootPanel.add(dockTabs, BorderLayout.NORTH);
        dockBtnGroup = new ButtonGroup();
        JToggleButton btn = new JToggleButton(new AbstractAction("hello world") {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.err.println(e.getActionCommand());
            }
        });
        btn.setPreferredSize(new Dimension(75, 25));
        dockBtnGroup.add(btn);
        dockTabs.add(btn);
        btn = new JToggleButton(new AbstractAction("test world") {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.err.println(e.getActionCommand());
            }
        });
        dockBtnGroup.add(btn);
        dockTabs.add(btn);
        btn = new JToggleButton(new AbstractAction("test dadada") {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.err.println(e.getActionCommand());
            }
        });
        dockBtnGroup.add(btn);
        dockTabs.add(btn);
        buildDataPan();
        rootPanel.pack();
    }

    private void buildDataPan() {
        docks.put("HexGrid Data", new JPanel());
        JButton btn = new JButton(new AbstractAction("hello world") {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.err.println(e.getActionCommand());
            }
        });
        docks.get("HexGrid Data").add(btn);
        rootPanel.add(btn, BorderLayout.CENTER);
    }

    public void add(String dockName, JPanel dockPanel) {
        if (!dockName.equals("HexGrid Data")) {
            docks.put(dockName, dockPanel);
        }
    }

    public void setVisible(String dockToShow, boolean show) {
        rootPanel.setLocationRelativeTo(null);
        rootPanel.setVisible(show);
    }
}

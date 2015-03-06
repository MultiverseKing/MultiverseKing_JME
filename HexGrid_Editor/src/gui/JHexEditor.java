package gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import test.EditorMain;

/**
 *
 * @author roah
 */
public final class JHexEditor extends AbstractAction {
    private final EditorMain editorMain;

    public JHexEditor(String name, EditorMain editorMain) {
        super(name);
        this.editorMain = editorMain;
    }

    public JPanel buildMenu() {
        JPanel innerPan = new JPanel();
        innerPan.setLayout(new BoxLayout(innerPan, BoxLayout.PAGE_AXIS));
        innerPan.setAlignmentX(0);
        innerPan.setBorder(BorderFactory.createTitledBorder("Map Property"));
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        addComp(innerPan, separator);

        JCheckBox box = new JCheckBox(new AbstractAction("Show ghost") {
            @Override
            public void actionPerformed(ActionEvent e) {
                onAction(e);
            }
        });//todo
        box.setSelected(true);
        box.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        addComp(innerPan, box);
        addComp(innerPan, new JButton(new AbstractAction("Test Component") {
            @Override
            public void actionPerformed(ActionEvent e) {
                onAction(e);
            }
        }));

        return innerPan;
    }

    private void addComp(JPanel pan, Component comp) {
        pan.add(Box.createRigidArea(new Dimension(0, 3)));
        pan.add(comp);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        onAction(e);
    }

    private void onAction(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "New Map":
                addHexGridMenu();
                editorMain.startEditor();
                break;
            case "Show ghost":
                //TODO
                System.err.println("TODO : " + e.getActionCommand());
                break;
            default:
                System.err.println("No associated action for : " + e.getActionCommand());
        }
    }

    private void addHexGridMenu() {
        GridBagConstraints bagConstraints = new GridBagConstraints();
        bagConstraints.gridx = 1;
        bagConstraints.gridy = 1;
        bagConstraints.gridwidth = 1;
        bagConstraints.gridheight = 1;
        bagConstraints.weightx = 0;
        bagConstraints.weighty = 0;
        bagConstraints.insets = new Insets(2, 0, 2, 5);
        bagConstraints.anchor = GridBagConstraints.CENTER;
        bagConstraints.fill = GridBagConstraints.BOTH;


        editorMain.getRootPanel().add(buildMenu(), bagConstraints);
        editorMain.getRootWindow().revalidate();
    }
}

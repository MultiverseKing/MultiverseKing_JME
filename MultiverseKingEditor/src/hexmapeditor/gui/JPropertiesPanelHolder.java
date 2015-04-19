package hexmapeditor.gui;

import hexmapeditor.gui.hexmap.JHexPropertiesPanel;
import hexmapeditor.gui.entitysystem.JESPropertiesPanel;
import gui.JPropertiesPanel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import core.EditorMain;

/**
 *
 * @author roah
 */
public final class JPropertiesPanelHolder extends JPanel {

    private final JPanel iconPan;
    private final ButtonGroup iconGrp = new ButtonGroup();
    private HashMap<String, JPropertiesPanel> panels = new HashMap<>();
    private boolean initialized = false;

    public JPropertiesPanelHolder(EditorMain editorMain) {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setAlignmentX(0);
        setPreferredSize(new Dimension(170, Integer.MAX_VALUE));

        iconPan = new JPanel();
        iconPan.setBorder(BorderFactory.createBevelBorder(-1));//createLineBorder(Color.BLACK));
        iconPan.setLayout(new BoxLayout(iconPan, BoxLayout.LINE_AXIS));
        iconPan.setAlignmentX(0);
        iconPan.setPreferredSize(new Dimension(Integer.MAX_VALUE, 30));
        super.add(iconPan);

        add(Box.createRigidArea(new Dimension(0, 2)));
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        add(separator);
        add(Box.createRigidArea(new Dimension(0, 3)));
        
        add(new JHexPropertiesPanel(editorMain));
        add(new JESPropertiesPanel(editorMain));
    }
    
    public void add(JPropertiesPanel panel) {
        if (!panels.containsKey(panel.getName())) {
            JButton b = new JButton(panel.getIcon());
            b.setName(panel.getName());
            b.addActionListener(new AbstractAction(panel.getName()) {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (!iconGrp.getSelection().equals(((JButton) e.getSource()).getModel())) {
                        updatePanel(getValue(Action.NAME).toString());
                    }
                }
            });
            b.setPreferredSize(new Dimension(15, 15));
            b.setMaximumSize(new Dimension(20, 20));
            b.setBorder(BorderFactory.createEmptyBorder());
            b.setContentAreaFilled(false);
            iconPan.add(b);
            iconGrp.add(b);
            panels.put(panel.getName(), panel);
            if(!initialized){
                super.add(panel);
                iconGrp.setSelected(b.getModel(), true);
                b.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                validate();
                iconPan.validate();
                initialized = true;
            } else {
                iconPan.revalidate();
                iconPan.repaint();
            }
        }
    }

    private void updatePanel(String newPan) {
        for (Component c : this.getComponents()) {
            if (c instanceof JPanel && !((JPanel) c).equals(iconPan)) {
                this.remove(panels.get(c.getName()));
                break;
            }
        }
        super.add(panels.get(newPan));
        panels.get(newPan).isShow();

        for (Component b : iconPan.getComponents()) {
            if (((JButton) b).getName().equals(newPan)) {
                ((JButton) b).setBorder(BorderFactory.createLineBorder(Color.BLACK));
                iconGrp.setSelected(((JButton) b).getModel(), true);
            } else {
                ((JButton) b).setBorder(BorderFactory.createEmptyBorder());
            }
        }
        revalidate();
        repaint();
    }
}

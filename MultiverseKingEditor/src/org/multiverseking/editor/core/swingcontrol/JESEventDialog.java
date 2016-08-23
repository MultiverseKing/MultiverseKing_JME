package org.multiverseking.editor.core.swingcontrol;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.multiverseking.field.exploration.component.AreaEventComponent;

/**
 *
 * @author roah
 */
public class JESEventDialog extends JDialog {

    JPanel dataPan = new JPanel(new GridBagLayout());
    AreaEventComponent.Event validatedEvent;

    public JESEventDialog(Frame owner, String title) {
        super(owner, title, true);
        setResizable(false);

        buildDataPan();
        add(dataPan);
        pack();
    }

    private void buildDataPan() {
        addComp(new JLabel("Event Type : "), 0, 0, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE);
        final JComboBox combo = new JComboBox(AreaEventComponent.Event.values());
        combo.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switch ((AreaEventComponent.Event) ((JComboBox) e.getSource()).getSelectedItem()) {
                    case Start:
                        break;
                    case Event:
                        buildEventMenu();
                        break;
                    default:
                        throw new UnsupportedOperationException(
                                (AreaEventComponent.Event) ((JComboBox) e.getSource())
                                .getSelectedItem() + " is not an implemented type.");
                }
            }
        });
        addComp(combo, 1, 0, 3, 1, GridBagConstraints.EAST, GridBagConstraints.BOTH);

        JButton okBtn = new JButton(new AbstractAction("Ok") {
            @Override
            public void actionPerformed(ActionEvent e) {
                validatedEvent = (AreaEventComponent.Event) combo.getSelectedItem();
                setVisible(false);
            }
        });
        addComp(okBtn, 2, 3, 1, 1, GridBagConstraints.EAST, GridBagConstraints.BOTH);

        JButton cancelBtn = new JButton(new AbstractAction("cancel") {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        addComp(cancelBtn, 3, 3, 1, 1, GridBagConstraints.EAST, GridBagConstraints.BOTH);
    }

    private void buildEventMenu() {
        System.err.println("TODO : Set Trigger Menu");
        JPanel innerPan = new JPanel();
        
    }

    // Sets the rules for a component destined for a GridBagLayout
    // and then adds it
    private void addComp(JComponent comp, int xPos, int yPos, int compWidth, int compHeight, int place, int stretch) {

        GridBagConstraints gridConstraints = new GridBagConstraints();

        gridConstraints.gridx = xPos;
        gridConstraints.gridy = yPos;
        gridConstraints.gridwidth = compWidth;
        gridConstraints.gridheight = compHeight;
        gridConstraints.weightx = 0;
        gridConstraints.weighty = 0;
        gridConstraints.insets = new Insets(5, 5, 5, 5);
        gridConstraints.anchor = place;
        gridConstraints.fill = stretch;

        dataPan.add(comp, gridConstraints);

    }

    public AreaEventComponent.Event getValidatedEvent() {
        return validatedEvent;
    }
}

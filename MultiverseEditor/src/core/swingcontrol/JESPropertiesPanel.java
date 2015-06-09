package core.swingcontrol;

import gui.JPanelTab;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import org.hexgridapi.core.appstate.MouseControlSystem;
import core.HexGridEditorMain;
import hexmap.gui.JCursorPositionPanel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Callable;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.hexgridapi.events.TileSelectionListener;
import org.hexgridapi.core.geometry.builder.coordinate.HexCoordinate;
import core.debug.DebugSystemState;
import org.multiversekingesapi.field.component.AreaEventComponent;

/**
 *
 * @author roah
 */
public class JESPropertiesPanel extends JPanelTab {

    private final HexGridEditorMain editorMain;
    private JCursorPositionPanel cursorPan;
    private HashMap<String, Component> comps = new HashMap<>();
    private final DebugSystemState system;
    private JPanel eventPan = new JPanel();
    private HexCoordinate inspectedPos;

    public JESPropertiesPanel(HexGridEditorMain editorMain, MouseControlSystem mouseSystem) {
        super(editorMain.getAssetManager().loadTexture(
                "Textures/Icons/Buttons/closeChest.png").getImage(), "Entity System");
        this.editorMain = editorMain;

        cursorPan = new JCursorPositionPanel(mouseSystem);
        add(cursorPan);
        mouseSystem.getSelectionControl().registerTileListener(selectionListener);
        system = editorMain.getStateManager().getState(DebugSystemState.class);
    }
    private TileSelectionListener selectionListener = new TileSelectionListener() {
        @Override
        public void onTileSelectionUpdate(HexCoordinate currentSelection, ArrayList<HexCoordinate> selectedList) {
            if (inspectedPos == null) {
                initialize();
            } else if (!inspectedPos.equals(currentSelection)) {
                updateMenu();
            }
        }
    };

    private void initialize() {
        JButton addEvent = new JButton(new AbstractAction("Add Event") {
            @Override
            public void actionPerformed(ActionEvent e) {
                //open event menu
                JESEventDialog dial = new JESEventDialog(editorMain.getRootFrame(), "Add Event");
                dial.setLocationRelativeTo(editorMain.getRootFrame());
                dial.setVisible(true);

                final AreaEventComponent.Event event = dial.getValidatedEvent();
                if (event != null) {
                    if (event.equals(AreaEventComponent.Event.Start)
                            && system.getStartPosition() != null
                            && system.getStartPosition().equals(cursorPan.getPosition())) {
                        return;
                    }
                    editorMain.enqueue(new Callable<Void>() {
                        @Override
                        public Void call() throws Exception {
                            system.updateEvent(cursorPan.getPosition(), event, true);
                            return null;
                        }
                    });
                    eventPan.add(getButtonEvent(event));
                    revalidate();
                }
            }
        });
        addEvent.setPreferredSize(new Dimension(Integer.MAX_VALUE, 30));
        addEvent.setAlignmentX(0);
        add(addEvent);
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        addComp(separator);

        eventPan.setLayout(new BoxLayout(eventPan, BoxLayout.PAGE_AXIS));
        add(eventPan);

        inspectedPos = cursorPan.getPosition();
    }

    private void updateMenu() {
        AreaEventComponent events = system.getValue(cursorPan.getPosition());
        if (events != null) {
            if (eventPan.getComponents().length != 0) {
                eventPan.removeAll();
            }
            for (AreaEventComponent.Event event : events.getEvents()) {
                eventPan.add(getButtonEvent(event));
            }
        } else {
            eventPan.removeAll();
        }
        inspectedPos = cursorPan.getPosition();
        revalidate();
        repaint();
    }

    private JPanel getButtonEvent(AreaEventComponent.Event event) {
        JPanel result = new JPanel(new BorderLayout());
        result.add(new JLabel(event.toString()), BorderLayout.WEST);
        result.add(Box.createRigidArea(new Dimension(5, 0)));
        final AreaEventComponent.Event passEvent = event;
        JButton removeEvent = new JButton(new AbstractAction("Remove") {
            @Override
            public void actionPerformed(ActionEvent e) {
                editorMain.enqueue(new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        editorMain.getStateManager().getState(DebugSystemState.class)
                                .updateEvent(cursorPan.getPosition(), passEvent, false);
                        return null;
                    }
                });
                eventPan.remove(((Component) e.getSource()).getParent());
                eventPan.revalidate();
            }
        });
        result.setAlignmentX(0);
        result.add(removeEvent, BorderLayout.EAST);
        result.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        return result;
    }

    @Override
    public void isShow() {
    }

    @Override
    public void isHidden() {
    }
}

package org.multiverseking.editor.core.swingcontrol;

import com.jme3.texture.Image;
import java.awt.Dimension;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import org.hexgridapi.editor.core.HexGridEditorMain;
import org.hexgridapi.editor.hexmap.gui.JCursorPositionPanel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
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
import org.hexgridapi.core.camera.RTSCamera;
import org.hexgridapi.core.mousepicking.GridMouseControlAppState;
import org.hexgridapi.core.coordinate.HexCoordinate;
import org.hexgridapi.editor.hexmap.gui.HexGridPropertiesPan;
import org.hexgridapi.editor.utility.ImageConverter;
import org.hexgridapi.events.TileSelectionListener;
import org.multiverseking.debug.DebugSystemState;
import org.multiverseking.editor.core.JPlayEditorMenu;
import org.multiverseking.field.component.AreaEventComponent;

/**
 *
 * @author roah
 */
public class JESPropertiesPanel extends HexGridPropertiesPan {

    private final HexGridEditorMain editorMain;
    private final DebugSystemState system;
    private final JPlayEditorMenu playEditorMenu;
    private JCursorPositionPanel cursorPan;
    private JPanel eventPan = new JPanel();
    private HexCoordinate inspectedPos;

    public JESPropertiesPanel(HexGridEditorMain editorMain, GridMouseControlAppState mouseSystem) {
        super(editorMain.getAssetManager().loadTexture(
                "org/multiverseking/assets/Textures/Icons/Buttons/closeChest.png").getImage(), "Entity System");
        this.editorMain = editorMain;

        playEditorMenu = new JPlayEditorMenu(editorMain);
        editorMain.getRootFrame().getJMenuBar().add(playEditorMenu);

        cursorPan = new JCursorPositionPanel(mouseSystem);
        add(cursorPan);
        mouseSystem.getSelectionControl().register(selectionListener);
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
        JPanel holder = new JPanel(new FlowLayout(FlowLayout.TRAILING));
        holder.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        holder.setAlignmentX(0);
        
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
        addEvent.setPreferredSize(new Dimension(120, 26));
        holder.add(addEvent);
        //------- Center to Start Position ------
        Image img = editorMain.getAssetManager().loadTexture(
                      "org/multiverseking/assets/Textures/Icons/Buttons/centerToStart.png").getImage();
        JButton center = new JButton(new AbstractAction("", ImageConverter.convertToIcon(img, 20, 20)) {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(system.getStartPosition() != null) {
                    editorMain.getStateManager().getState(RTSCamera.class)
                            .setCenter(system.getStartPosition().toWorldPosition());
                }
            }
        });
        center.setPreferredSize(new Dimension(30, 26));
        holder.add(center);
        add(holder);
        
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        addWithSpace(separator);

        eventPan.setLayout(new BoxLayout(eventPan, BoxLayout.PAGE_AXIS));
        add(eventPan);

        inspectedPos = cursorPan.getPosition();
        editorMain.getRootFrame().pack();
        editorMain.getRootFrame().revalidate();
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

    @Override
    public void onMapLoaded() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void onMapRemoved() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void onMapReset() {
//        editorMain.enqueue(new Callable<Void>() {
//            @Override
//            public Void call() throws Exception {
//                editorMain.getStateManager().getState(DebugSystemState.class).cleanup();
//                return null;
//            }
//        });
    }
}

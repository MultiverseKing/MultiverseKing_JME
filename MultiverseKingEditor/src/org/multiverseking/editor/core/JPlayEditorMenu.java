package org.multiverseking.editor.core;

import org.hexgridapi.editor.core.HexGridEditorMain;
import java.awt.event.ActionEvent;
import java.util.concurrent.Callable;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.border.BevelBorder;
import org.hexgridapi.core.AbstractHexGridAppState;
import org.hexgridapi.core.camera.RTSCamera;
import org.multiverseking.debug.DebugSystemState;
import org.multiverseking.debug.RenderDebugSystem;
import org.multiverseking.field.exploration.ExplorationSystem;

/**
 *
 * @author roah
 */
public class JPlayEditorMenu extends JMenu {

    private final HexGridEditorMain editorMain;
    private JButton stopBtn;

    public JPlayEditorMenu(final HexGridEditorMain editorMain) {
        super("Play");
        this.editorMain = editorMain;
        stopBtn = new JButton(new AbstractAction(" Stop") {
            @Override
            public void actionPerformed(ActionEvent e) {
                editorMain.enqueue(new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        stopExploration();
                        return null;
                    }
                });
            }
        });
        stopBtn.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

        add(new AbstractAction("Play Exploration") {
            @Override
            public void actionPerformed(ActionEvent e) {
                onAction(e);
            }
        });
        add(new AbstractAction("Play Battle") {
            @Override
            public void actionPerformed(ActionEvent e) {
                onAction(e);
            }
        });
    }

    public void onAction(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "Play Battle":
                JOptionPane.showMessageDialog(editorMain.getRootFrame(), "TODO...");
                break;
            case "Play Exploration":
                editorMain.enqueue(new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        if (editorMain.getStateManager().getState(ExplorationSystem.class) != null) {
                            int result = JOptionPane.showConfirmDialog(editorMain.getRootFrame(), "Exploration mode already running, "
                                    + "want to shut it off ?", "Exploration", JOptionPane.OK_CANCEL_OPTION);
                            if (result == 0) {
                                stopExploration();
                            }
                        } else if (editorMain.getStateManager().getState(DebugSystemState.class).getStartPosition() != null) {
                            editorMain.getStateManager().attach(new ExplorationSystem());
                            editorMain.getStateManager().getState(RenderDebugSystem.class).setEnabled(false);
                            editorMain.getRootFrame().getJMenuBar().add(stopBtn);
                            editorMain.getHexGridModule().collapseProperties(true);
                            editorMain.getRootFrame().revalidate();
                            editorMain.getRootFrame().repaint();
                        } else {
                            JOptionPane.showMessageDialog(editorMain.getRootFrame(), "A start position need to be set.");
                        }
                        return null;
                    }
                });
                break;
        }
    }

    private void stopExploration() {
        editorMain.getStateManager().getState(AbstractHexGridAppState.class)
                .setBufferPositionProvider(editorMain.getStateManager().getState(RTSCamera.class));
        editorMain.getStateManager().detach(editorMain.getStateManager().getState(ExplorationSystem.class));
        editorMain.getStateManager().getState(RenderDebugSystem.class).setEnabled(true);
        editorMain.getRootFrame().getJMenuBar().remove(stopBtn);
        editorMain.getHexGridModule().collapseProperties(false);
        editorMain.getRootFrame().revalidate();
        editorMain.getRootFrame().repaint();
    }
}

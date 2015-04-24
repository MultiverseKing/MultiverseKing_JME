package core;

import java.awt.event.ActionEvent;
import java.util.concurrent.Callable;
import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import org.multiversekingesapi.field.AreaEventSystem;
import org.multiversekingesapi.field.exploration.ExplorationSystem;

/**
 *
 * @author roah
 */
public class JPlayEditorMenu extends JMenu {

    private final HexGridEditorMain main;

    public JPlayEditorMenu(HexGridEditorMain main) {
        super("Play");
        this.main = main;

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
                JOptionPane.showMessageDialog(main.getRootWindow(), "TODO...");
                break;
            case "Play Exploration":
                main.enqueue(new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        if (main.getStateManager().getState(AreaEventSystem.class).getStartPosition() != null) {
                            main.getStateManager().attach(new ExplorationSystem());
                        } else {
                            JOptionPane.showMessageDialog(main.getRootWindow(), "A start position need to be set.");
                        }
                        return null;
                    }
                });
                break;
        }
    }
}

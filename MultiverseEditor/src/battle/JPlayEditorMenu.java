package battle;

import core.Editor;
import core.HexGridEditorMain;
import java.awt.event.ActionEvent;
import java.util.concurrent.Callable;
import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import org.multiversekingesapi.battle.BattleSystem;
import org.multiversekingesapi.field.AreaEventSystem;

/**
 *
 * @author roah
 */
public class JPlayEditorMenu extends JMenu {

    private final HexGridEditorMain main;

    public JPlayEditorMenu(HexGridEditorMain main) {
        super("Play");
        this.main = main;

        add(new AbstractAction("Battle Test") {
            @Override
            public void actionPerformed(ActionEvent e) {
                onAction(e);
            }
        });
        add(new AbstractAction("Exploration Test") {
            @Override
            public void actionPerformed(ActionEvent e) {
                onAction(e);
            }
        });
    }

    public void onAction(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "Battle Test":
                main.enqueue(new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        if (main.getStateManager().getState(AreaEventSystem.class).getStartPosition() != null) {
                            System.err.println("Start Battle");
                            main.getStateManager().attach(new BattleSystem(((Editor) main).getScreen()));
                        } else {
                            JOptionPane.showMessageDialog(main.getRootWindow(), "A start position need to be set.");
                        }
                        return null;
                    }
                });
                break;
            case "Exploration Test":
                JOptionPane.showMessageDialog(main.getRootWindow(), "TODO...");
                break;
        }
    }
}

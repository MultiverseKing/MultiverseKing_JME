package battle;

import core.EditorMain;
import java.awt.event.ActionEvent;
import java.util.concurrent.Callable;
import javax.swing.AbstractAction;
import javax.swing.JMenu;
import org.multiversekingesapi.field.AreaEventSystem;

/**
 *
 * @author roah
 */
public class JPlayEditorMenu extends JMenu {

    private final EditorMain main;

    public JPlayEditorMenu(EditorMain main) {
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
                        } else {
                            System.err.println("there is no start");
                        }
                        return null;
                    }
                });
                break;
            case "Exploration Test":
                System.err.println("todo");
                break;
        }
    }
}

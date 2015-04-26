package core;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.concurrent.Callable;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.border.BevelBorder;
import org.multiversekingesapi.field.AreaEventSystem;
import org.multiversekingesapi.field.exploration.ExplorationSystem;

/**
 *
 * @author roah
 */
public class JPlayEditorMenu extends JMenu {

    private final HexGridEditorMain main;
    private JButton stopBtn;

    public JPlayEditorMenu(final HexGridEditorMain main) {
        super("Play");
        this.main = main;
        stopBtn = new JButton(new AbstractAction(" Stop") {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.enqueue(new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        main.getStateManager().detach(main.getStateManager().getState(ExplorationSystem.class));
                        main.getRootWindow().getJMenuBar().remove(stopBtn);
                        main.getRootWindow().revalidate();
                        main.getRootWindow().repaint();
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
                JOptionPane.showMessageDialog(main.getRootWindow(), "TODO...");
                break;
            case "Play Exploration":
                main.enqueue(new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        if (main.getStateManager().getState(AreaEventSystem.class).getStartPosition() != null) {
                            main.getStateManager().attach(new ExplorationSystem());
                            main.getRootWindow().getJMenuBar().add(stopBtn);
                            main.getRootWindow().revalidate();
                            main.getRootWindow().repaint();
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

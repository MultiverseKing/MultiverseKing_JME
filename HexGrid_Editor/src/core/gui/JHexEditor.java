package core.gui;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
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
    
    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "New Map":
                if(!editorMain.isStart()){
                    editorMain.startEditor();
                }
                break;
            default:
                System.err.println("No associated action for : " + e.getActionCommand());
        }
    }
}

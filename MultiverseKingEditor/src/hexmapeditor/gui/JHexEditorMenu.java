package hexmapeditor.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.concurrent.Callable;
import javax.swing.AbstractAction;
import javax.swing.JMenu;
import org.hexgridapi.core.appstate.MapDataAppState;
import core.EditorMain;

/**
 *
 * @author roah
 */
public final class JHexEditorMenu extends JMenu {

    private final EditorMain editorMain;

    public JHexEditorMenu(EditorMain editorMain) {
        super("Hex Editor");
        this.editorMain = editorMain;
    }
    
    public void setAction(HexMenuAction action){
        String name = action.toString() + " Map";
        add(new AbstractAction(name) {
            @Override
            public void actionPerformed(ActionEvent e) {
                onAction(e);
            }
        });
    }
    
    public void onAction(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "New Map":
                if(!editorMain.isStart()){
                    editorMain.enqueue(new Callable<Void>() {
                        @Override
                        public Void call() throws Exception {
                            editorMain.startAreaEditor();
                            editorMain.getRootWindow().add(new JPropertiesPanelHolder(editorMain), BorderLayout.EAST);
                            editorMain.getRootWindow().revalidate();
                            return null;
                        }
                    });
                    setAction(HexMenuAction.Save);
                } else {
                    //@todo create a new map from a new seed
                    //@todo show warning popup
                }
                break;
            case "Load Map":
                JLoaderDialog loadDialog = new JLoaderDialog(editorMain.getRootWindow(), false);
                loadDialog.setLocationRelativeTo(editorMain.getRootWindow());
                loadDialog.setVisible(true);

                String loadName = loadDialog.getValidatedText();
                if (loadName != null) {
                    //The text is valid.
                    System.err.println("@todo Load Map " + loadName);
                }
                loadDialog.dispose();
                break;
            case "Save Map":
                JLoaderDialog saveDialog = new JLoaderDialog(editorMain.getRootWindow(), 
                        editorMain.getStateManager().getState(MapDataAppState.class).getMapData().getMapName());
                saveDialog.setLocationRelativeTo(editorMain.getRootWindow());
                saveDialog.setModal(true);
                saveDialog.setVisible(true);
                String saveName = saveDialog.getValidatedText();
                if (saveName != null) {
                    //The text is valid.
                    editorMain.enqueue(new Callable<Void>() {
                        @Override
                        public Void call() throws Exception {
//                            editorSystem.save(null);
                            System.err.println("@todo Save Map");
                            return null;
                        }
                    });
                }
                saveDialog.dispose();
                break;
            default:
                System.err.println("No associated action for : " + e.getActionCommand());
        }
    }
    
    public enum HexMenuAction {
        New,
        Load,
        Save;
    }
}

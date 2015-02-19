package editor.area;

import gui.EditorWindow;
import tonegod.gui.core.Element;
import tonegod.gui.core.Screen;

/**
 * Option used to customize the area.
 *
 * @author roah
 */
public class AreaEditorGUI extends EditorWindow {

    private final AreaEditorSystem system;

    public AreaEditorGUI(Screen screen, Element parent, AreaEditorSystem system) {
        super(screen, parent, "Area Properties");
        this.system = system;
        /**
         * Part used to set the Activation range of the ability.
         */
        addLabelPropertieField("MapSize", system.getChunkMapSize(), HAlign.left);
        addLabelPropertieField("tile Counts", system.getTileMapSize(), HAlign.left);
//        addButtonList("Size X", new String[]{"Add X", "Remove X"}, HAlign.left, 1, 0, true);
//        addButtonList("Size Y", new String[]{"Add Y", "Remove Y"}, HAlign.left, 1, 0, true);
        showConstrainToParent(VAlign.bottom, HAlign.left);
        window.setUseCloseButton(false);
        window.setUseCollapseButton(true);
    }

    @Override
    public void onPressCloseAndHide() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void onButtonTrigger(String label) {
//        switch (label) {
//            case "Add X":
//                system.updateMapSize(new Vector2Int(1, 0));
//                break;
//            case "Remove X":
//                system.updateMapSize(new Vector2Int(-1, 0));
//                break;
//            case "Add Y":
//                system.updateMapSize(new Vector2Int(0, 1));
//                break;
//            case "Remove Y":
//                system.updateMapSize(new Vector2Int(0, -1));
//                break;
//        }
    }
}

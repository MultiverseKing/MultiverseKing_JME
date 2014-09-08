package gamemode.editor.map;

import gamemode.editor.EditorMenu;
import gamemode.editor.map.MapEditorSystem.MapEditorMode;
import tonegod.gui.core.ElementManager;

/**
 *
 * @author roah
 */
class MapEditorMenu extends EditorMenu {

    public MapEditorMenu(ElementManager screen, MapEditorSystem system) {
        super(screen, "MapEditorGui", "Map Editor", system);
        addAdditionalField("Edit World");
        addAdditionalField("Edit Area");
        addAdditionalField("Edit Room");
        populateEditor();
    }

    @Override
    protected void onAdditionalFieldTrigger(int value) {
        switch(value){
            case 0:
                /**
                 * Open the World Editor mode.
                 * Run globeHex.
                 */
                ((MapEditorSystem)system).switchGui(MapEditorMode.WORLD);
                break;
            case 1:
                /**
                 * Open the Area Editor mode.
                 */
                ((MapEditorSystem)system).switchGui(MapEditorMode.AREA);
                break;
            case 2:
                /**
                 * Open the Room Editor mode.
                 */
                ((MapEditorSystem)system).switchGui(MapEditorMode.ROOM);
                break;
        }
    }

    @Override
    protected void onSelectedItemChange(int index) {
    }    

    @Override
    public void update(float tpf) {
        
    }
}

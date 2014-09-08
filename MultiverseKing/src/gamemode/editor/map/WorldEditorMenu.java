package gamemode.editor.map;

import entitysystem.EntitySystemAppState;
import gamemode.editor.EditorMenu;
import tonegod.gui.core.ElementManager;

/**
 *
 * @author roah
 */
class WorldEditorMenu extends EditorMenu {

    WorldEditorMenu(ElementManager screen, EntitySystemAppState system) {
        super(screen, "WorldEditorMenu", "World Editor", system);
    }

    
    @Override
    protected void onAdditionalFieldTrigger(int value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void onSelectedItemChange(int index) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void update(float tpf) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}

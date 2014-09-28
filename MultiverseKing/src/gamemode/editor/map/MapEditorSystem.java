package gamemode.editor.map;

import entitysystem.EntitySystemAppState;

/**
 *
 * @author roah
 */
public abstract class MapEditorSystem extends EntitySystemAppState {
    
    public abstract boolean load(String name);
    public abstract boolean save(String name);
}

package gamemode.editor.map;

import gamemode.gui.LoadingPopup;
import entitysystem.EntitySystemAppState;

/**
 *
 * @author roah
 */
public abstract class MapEditorSystem extends EntitySystemAppState {

    public abstract boolean load(LoadingPopup popup);
    public abstract boolean save(LoadingPopup popup);
    public abstract void generateEmptyArea();
    public abstract void reloadSystem();
}

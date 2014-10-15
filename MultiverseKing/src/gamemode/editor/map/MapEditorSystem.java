package gamemode.editor.map;

import gamemode.gui.LoadingPopup;
import entitysystem.EntitySystemAppState;

/**
 *
 * @author roah
 */
public abstract class MapEditorSystem extends EntitySystemAppState {

    public abstract void reloadSystem(LoadingPopup popup);
    public abstract void reloadSystem();
    public abstract void save(LoadingPopup popup);
    public abstract void generateEmptyArea();
}

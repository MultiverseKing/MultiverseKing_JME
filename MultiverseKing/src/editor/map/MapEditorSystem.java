package editor.map;

import entitysystem.EntitySystemAppState;
import gui.LoadingPopup;

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

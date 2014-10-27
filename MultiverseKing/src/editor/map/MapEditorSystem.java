package editor.map;

import entitysystem.EntitySystemAppState;
import gui.FileManagerPopup;
import gui.LoadingPopup;

/**
 *
 * @author roah
 */
public abstract class MapEditorSystem extends EntitySystemAppState {
    public abstract void reloadSystem(FileManagerPopup popup);
    public abstract void reloadSystem();
    public abstract void save(FileManagerPopup popup);
    public abstract void generateEmptyArea();
}

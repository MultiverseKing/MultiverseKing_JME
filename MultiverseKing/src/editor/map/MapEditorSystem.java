package editor.map;

import org.multiversekingesapi.EntitySystemAppState;
import gui.FileManagerPopup;

/**
 *
 * @author roah
 */
public abstract class MapEditorSystem extends EntitySystemAppState {
    public abstract void loadFromFile(FileManagerPopup popup);
    public abstract void reloadSystem();
    public abstract void save(FileManagerPopup popup);
    public abstract void generateEmptyArea();
}

package kingofmultiverse.Application;

import exploration.ExplorationSystemGUI;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.MouseButtonTrigger;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hexgridapi.core.data.MapData;
import org.hexgridapi.core.appstate.HexGridDefaultApplication;
import org.hexgridapi.core.appstate.MapDataAppState;
import org.hexgridapi.core.appstate.MouseControlSystem;
import org.hexgridapi.core.geometry.builder.GridParam;
import org.hexgridapi.core.geometry.builder.coordinate.HexCoordinate;
import org.hexgridapi.core.geometry.builder.coordinate.SquareCoordinate;
import org.multiversekingesapi.EntityDataAppState;
import org.multiversekingesapi.MultiverCoreGUI;
import org.multiversekingesapi.field.exploration.ExplorationSystem;
import org.multiversekingesapi.field.position.HexPositionSystem;
import org.multiversekingesapi.render.RenderSystem;
import tonegod.gui.core.Screen;

/**
 *
 * @author roah
 */
public class KingOfMultiverseMain extends HexGridDefaultApplication implements MultiverCoreGUI {

    public static void main(String[] args) {
        KingOfMultiverseMain app = new KingOfMultiverseMain();
        Logger.getLogger("").setLevel(Level.SEVERE);
        app.start();
    }
    private Screen screen;

    @Override
    public Screen getScreen() {
        return screen;
    }

    @Override
    public void initApp() {
        this.screen = new Screen(this);
        guiNode.addControl(screen);

        inputManager.addMapping("Confirm", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addMapping("Cancel", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));

        GridParam param = new GridParam(new String[]{"EARTH", "ICE", "NATURE", "VOLT"}, SquareCoordinate.class, true, false, false, true);
        MapData mapData = new MapData(assetManager, param);
        stateManager.attachAll(
                /**
                 * HexGrid State
                 */
                new MapDataAppState(mapData),
                new MouseControlSystem(),
                new HexGridSystem(mapData),
                /**
                 * Entity system State
                 */
                new EntityDataAppState(),
                new RenderSystem(),
                new HexPositionSystem(),
                new ExplorationSystem(),
                new ExplorationSystemGUI());
    }
}

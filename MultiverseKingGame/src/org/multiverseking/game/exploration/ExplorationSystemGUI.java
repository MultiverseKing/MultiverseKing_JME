package org.multiverseking.game.exploration;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.math.Vector2f;
import org.hexgridapi.core.AbstractHexGridAppState;
import org.multiverseking.game.core.KingOfMultiverseMain;
import tonegod.gui.controls.text.Label;
import tonegod.gui.controls.windows.Window;
import tonegod.gui.core.Screen;

/**
 *
 * @author roah
 */
public class ExplorationSystemGUI extends AbstractAppState {
    private KingOfMultiverseMain app;
    private Screen screen;
    private Window debug;
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.app = ((KingOfMultiverseMain)app);
        screen = ((KingOfMultiverseMain)app).getScreen();
        
//        initialiseDebug();
        
        super.initialize(stateManager, app);
    }

    private void initialiseDebug() {
        debug = new Window(screen, Vector2f.ZERO);
        Label label = new Label(screen, Vector2f.ZERO, new Vector2f(225, 35));
        label.setText(Integer.toString(app.getStateManager()
                .getState(AbstractHexGridAppState.class).getMapData().getGenerator().getSeed()));
        debug.addWindowContent(label);
        screen.addElement(debug);
    }
    
}

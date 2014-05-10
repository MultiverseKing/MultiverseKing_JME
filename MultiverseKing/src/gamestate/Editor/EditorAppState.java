package gamestate.Editor;

import hexsystem.events.HexMapInputListener;
import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import gamestate.HexMapMouseInput;
import hexsystem.MapData;
import hexsystem.events.HexMapInputEvent;
import hexsystem.events.TileChangeEvent;
import hexsystem.events.TileChangeListener;
import kingofmultiverse.MultiverseMain;

/**
 *
 * @author Eike Foede, Roah
 */
public class EditorAppState extends AbstractAppState implements TileChangeListener, HexMapInputListener {

    
    private EditorGUI editorGUI;
    
    private Node camTarget = new Node("camFocus");      //for chase cam
    
    private HexMapMouseInput mouseSystem;
    private MultiverseMain main;
    private MapData mapData;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        main = (MultiverseMain) app;
        mouseSystem = app.getStateManager().getState(HexMapMouseInput.class);
        mapData = app.getStateManager().getState(HexMapMouseInput.class).getMapData();
        editorGUI = new EditorGUI(mapData);
        app.getStateManager().attach(editorGUI);
        mouseSystem.getMapData().registerTileChangeListener(this);
    }
    
    /**
     * Method called each time a left mouse action is done.
     */
    public void leftMouseActionResult(HexMapInputEvent event) {
        
    }
    
    public void rightMouseActionResult(HexMapInputEvent event) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public void tileChange(TileChangeEvent event) {
        
    }

    @Override
    public void update(float tpf) {
        super.update(tpf); //To change body of generated methods, choose Tools | Templates.
        
    }

    /**
     * Change the camera focus to the selected position.
     *
     * @param position where the focus should be.
     */
    public void moveCameraFocus(Vector3f position) {
        camTarget.setLocalTranslation(position);
    }
}

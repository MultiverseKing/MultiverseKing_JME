/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector2f;
import kingofmultiverse.MultiverseMain;
import utility.attribut.GameState;
import tonegod.gui.controls.buttons.ButtonAdapter;
import tonegod.gui.controls.windows.Window;

/**
 * @todo User should not be able to move windows.
 * @author roah
 */
public class MainGUI extends AbstractAppState{

    private final MultiverseMain main;
    
    public MainGUI(MultiverseMain main) {
        this.main = main;
        main.setGameState(GameState.MAIN);
    }
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app); //To change body of generated methods, choose Tools | Templates.
        
        Window win = new Window(main.getScreen(), "mainWin", new Vector2f(15f, 15f));
        win.setWindowTitle("Main Windows");
        win.setMinDimensions(new Vector2f(130, 100));
        win.setWidth(new Float(50));
//        win.setIsMovable(false);
        win.setIgnoreMouse(true);
        main.getScreen().addElement(win);
        main.getScreen().setUseToolTips(true);

        ButtonAdapter editorConfig = new ButtonAdapter( main.getScreen(), "Btn1", new Vector2f(15, 40) ) {
            @Override
            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                startEditorConfig();
            }
            private void startEditorConfig(){
                main.setGameState(GameState.EDITOR);
                main.generateHexMap();
            }
        };
        ButtonAdapter battleTest = new ButtonAdapter( main.getScreen(), "Btn2", new Vector2f(15, 80) ) {
            @Override
            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                startBattleTest();
            }
            private void startBattleTest(){
                
            }
        };
        editorConfig.setText("Editor Config");
        battleTest.setText("Battle Test");
        battleTest.setToolTipText("Not implemented");

        // Add it to out initial window
        win.addChild(editorConfig);
        win.addChild(battleTest);
    }
    
    @Override
    public void cleanup() {
        super.cleanup();
        //TODO: clean up what you initialized in the initialize method,
        //e.g. remove all spatials from rootNode
        //this is called on the OpenGL thread after the AppState has been detached
        main.getScreen().removeElement(main.getScreen().getElementById("mainWin"));
    }
}
    

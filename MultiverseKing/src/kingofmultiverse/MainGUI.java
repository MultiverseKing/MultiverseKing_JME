package kingofmultiverse;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector2f;
import gamestate.Editor.CardEditorAppState;
import gamestate.Editor.MapEditorAppState;
import tonegod.gui.controls.buttons.ButtonAdapter;
import tonegod.gui.controls.windows.Window;

/**
 * @todo User should not be able to move windows.
 * @author roah
 */
public class MainGUI extends AbstractAppState {

    private final MultiverseMain main;

    /**
     *
     * @param main
     */
    public MainGUI(MultiverseMain main) {
        this.main = main;
    }

    /**
     *
     * @param stateManager
     * @param app
     */
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app); //To change body of generated methods, choose Tools | Templates.

        Window win = new Window(main.getScreen(), "mainWin", new Vector2f(15f, 15f));
        win.setMinDimensions(Vector2f.ZERO);
        win.setWidth(130);
        win.setHeight(4 * 40);
        win.getDragBar().setPosition(8, win.getHeight()-35);
        win.getDragBar().setWidth(win.getWidth()-14);
        win.setWindowTitle("  Editor Menu");
//        win.setIsVisible(); //used to resolve the dragbar issue with tonegodGUI
        win.setIgnoreMouse(true);
        main.getScreen().addElement(win);

        ButtonAdapter editorConfig = new ButtonAdapter(main.getScreen(), "Btn1", new Vector2f(15, 40)) {
            @Override
            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                main.getStateManager().attach(new MapEditorAppState());
                main.getStateManager().detach(main.getStateManager().getState(MainGUI.class));
            }
        };
        editorConfig.setText("Map Editor");
        win.addChild(editorConfig);
        
        ButtonAdapter cardEditor = new ButtonAdapter(main.getScreen(), "Btn2", new Vector2f(15, 80)) {
            @Override
            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                main.getStateManager().attach(new CardEditorAppState());
                main.getStateManager().detach(main.getStateManager().getState(MainGUI.class));
            }
        };
        cardEditor.setText("Card Editor");
        cardEditor.setToolTipText("Not implemented");
        win.addChild(cardEditor);
        
        ButtonAdapter test = new ButtonAdapter(main.getScreen(), "Btn3", new Vector2f(15, 120));
        test.setText("Test Button");
        win.addChild(test);
    }

    @Override
    public void cleanup() {
        super.cleanup();
        main.getScreen().removeElement(main.getScreen().getElementById("mainWin"));
    }
}

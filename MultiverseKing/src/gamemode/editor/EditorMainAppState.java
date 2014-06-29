package gamemode.editor;

import gamemode.editor.card.CardEditorSystem;
import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector2f;
import gamemode.editor.battle.BattleFieldTestSystem;
import gamemode.editor.map.MapEditorSystem;
import kingofmultiverse.MultiverseMain;
import tonegod.gui.controls.buttons.ButtonAdapter;
import tonegod.gui.controls.windows.Window;

/**
 *
 * @author roah
 */
public class EditorMainAppState extends AbstractAppState {

    private MultiverseMain main;
    private Window mainMenu;

    public Window getMainMenu() {
        return mainMenu;
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        main = (MultiverseMain) app;
        CreateMainWin();
    }

    private void CreateMainWin() {
        if (main.getScreen().getElementById("mainWin") != null) {
            main.getScreen().removeElement(main.getScreen().getElementById("mainWin"));
        }
        mainMenu = new Window(main.getScreen(), "mainWin", new Vector2f(15f, 15f));
        mainMenu.setMinDimensions(Vector2f.ZERO);
        mainMenu.setWidth(130);
        mainMenu.setHeight(4 * 40);
        mainMenu.getDragBar().setPosition(8, mainMenu.getHeight() - 35);
        mainMenu.getDragBar().setWidth(mainMenu.getWidth() - 14);
        mainMenu.setWindowTitle("   Editor Menu");
        mainMenu.setIgnoreMouse(true);
        main.getScreen().addElement(mainMenu);

        populateMainWindow();
    }

    private void populateMainWindow() {
        /**
         * Button used to open the MapEditor.
         */
        ButtonAdapter editorConfig = new ButtonAdapter(main.getScreen(), "MapEditorBtn", new Vector2f(15, 40)) {
            @Override
            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                remove();
                main.getStateManager().attach(new MapEditorSystem(main));
            }
        };
        editorConfig.setText("Map Editor");
        mainMenu.addChild(editorConfig);

        /**
         * Button used to open the CardEditor.
         */
        ButtonAdapter cardEditor = new ButtonAdapter(main.getScreen(), "CardEditorBtn", new Vector2f(15, 80)) {
            @Override
            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                remove();
                main.getStateManager().attach(new CardEditorSystem());
            }
        };
        cardEditor.setText("Card Editor");
        mainMenu.addChild(cardEditor);

        /**
         * A random testing Button.
         */
        ButtonAdapter test = new ButtonAdapter(main.getScreen(), "BattleFieldTest", new Vector2f(15, 120)){
            @Override
            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                remove();
                main.getStateManager().attach(new BattleFieldTestSystem());
            }
        };
        test.setText("Battle Test");
        mainMenu.addChild(test);
    }

    private void remove() {
        main.getStateManager().detach(this);
    }

    @Override
    public void cleanup() {
        super.cleanup();
        main.getScreen().removeElement(main.getScreen().getElementById("mainWin"));
    }
}

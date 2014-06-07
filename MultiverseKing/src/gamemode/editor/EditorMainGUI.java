package gamemode.editor;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector2f;
import entitysystem.card.CardSystem;
import kingofmultiverse.MultiverseMain;
import tonegod.gui.controls.buttons.Button;
import tonegod.gui.controls.buttons.ButtonAdapter;
import tonegod.gui.controls.windows.Window;

/**
 * @todo User should not be able to move windows.
 * @author roah
 */
public class EditorMainGUI extends AbstractAppState {

    private final MultiverseMain main;
    private CardEditor cardEditorMenu = null;
    private MapEditor mapEditorMenu = null;
    private Window mainMenu;

    public EditorMainGUI(MultiverseMain main) {
        this.main = main;
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        populateMainWindow();
    }

    private void populateMainWindow() {
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

        ButtonAdapter editorConfig = new ButtonAdapter(main.getScreen(), "Btn1", new Vector2f(15, 40)) {
            @Override
            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                mapEditorMenu = new MapEditor(main);
            }
        };
        editorConfig.setText("Map Editor");
        mainMenu.addChild(editorConfig);

        ButtonAdapter cardEditor = new ButtonAdapter(main.getScreen(), "Btn2", new Vector2f(15, 80)) {
            @Override
            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                if (main.getStateManager().getState(CardSystem.class) == null) {
                    main.getStateManager().attach(new CardSystem());
                }
                cardEditorMenu = new CardEditor(main);
            }
        };
        cardEditor.setText("Card Editor");
        mainMenu.addChild(cardEditor);

        ButtonAdapter test = new ButtonAdapter(main.getScreen(), "Btn3", new Vector2f(15, 120));
        test.setText("Test Button");
        mainMenu.addChild(test);
    }

    /**
     * Button to return back to the main menu.
     */
    public void populateReturnEditorMain(Window menu) {
        Window closeButtonWin = new Window(main.getScreen(), "CloseButtonWin",
                new Vector2f(0, menu.getHeight()), new Vector2f(180, 50));
        closeButtonWin.removeAllChildren();
        closeButtonWin.setIsResizable(false);
        closeButtonWin.setIsMovable(false);

        Button close = new ButtonAdapter(main.getScreen(), "returnMain",
                new Vector2f(15, 10), new Vector2f(150, 30)) {
            @Override
            public void onButtonMouseLeftDown(MouseButtonEvent evt, boolean toggled) {
                super.onButtonMouseLeftDown(evt, toggled);
                main.getStateManager().getState(EditorMainGUI.class).detachAll();
            }
        };
        close.setText("Return Main");
        closeButtonWin.addChild(close);
        menu.addChild(closeButtonWin);
    }

    public void detachAll() {
        if (cardEditorMenu != null) {
            cardEditorMenu.cleanup();
            cardEditorMenu = null;
        } else if (mapEditorMenu != null) {
            mapEditorMenu.cleanup();
            mapEditorMenu = null;
        }
        populateMainWindow();
    }

    @Override
    public void cleanup() {
        super.cleanup();
        main.getScreen().removeElement(main.getScreen().getElementById("mainWin"));
    }
}

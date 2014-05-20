package gamestate.Editor;

import hexsystem.events.HexMapInputListener;
import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import entitysystem.attribut.CardRenderPosition;
import entitysystem.card.CardRenderComponent;
import entitysystem.card.CardRenderSystem;
import entitysystem.render.RenderComponent;
import gamestate.GameDataAppState;
import gamestate.HexMapMouseInput;
import hexsystem.MapData;
import hexsystem.events.HexMapInputEvent;
import hexsystem.events.TileChangeEvent;
import hexsystem.events.TileChangeListener;
import kingofmultiverse.MainGUI;
import kingofmultiverse.MultiverseMain;
import tonegod.gui.controls.buttons.Button;
import tonegod.gui.controls.buttons.ButtonAdapter;
import tonegod.gui.controls.windows.Window;

/**
 *
 * @author Eike Foede, Roah
 */
public class CardEditorAppState extends AbstractAppState implements TileChangeListener, HexMapInputListener {
    
    private MultiverseMain main;
    private MapData mapData;
    private Window mainWin;
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        main = (MultiverseMain) app;
        HexMapMouseInput mouseSystem = app.getStateManager().getState(HexMapMouseInput.class);
        mouseSystem.registerTileInputListener(this);
        mapData = app.getStateManager().getState(HexMapMouseInput.class).getMapData();
        if(mapData.getMapName() == null || !mapData.getMapName().equalsIgnoreCase("reset")){
            if(!mapData.loadMap("Reset")){
                System.err.println("Cannot load the card editor map.");
            } else {
                initCardGUI();
            }
        } else {
            initCardGUI();
        }
    }
    
    public void leftMouseActionResult(HexMapInputEvent event) {

    }
    
    public void rightMouseActionResult(HexMapInputEvent event) {

    }
    
    public void tileChange(TileChangeEvent event) {
        
    }

    @Override
    public void update(float tpf) {
        super.update(tpf); //To change body of generated methods, choose Tools | Templates.
        
    }
    private void initCardGUI() {
        mainWin = new Window(main.getScreen(), "mainWin", new Vector2f(15f, 15f), new Vector2f(130, 40 * 5));
        mainWin.setWindowTitle("Map Editor");
        mainWin.setMinDimensions(new Vector2f(130, 130));
        mainWin.setIsResizable(false);
        mainWin.getDragBar().setIsMovable(false);
//        mainWin.setIsVisible(); //used to resolve the dragbar issue with tonegodGUI
        main.getScreen().addElement(mainWin);
//        Window cardEditor = new Window(main.getScreen(), "cardEditor", new Vector2f(15, 10), new Vector2f(150, 30));
//        cardEditor.setText("Card Editor");
//        main.getScreen().addElement(cardEditor);
        
        Window addRemoveCard = new Window(main.getScreen(), "addRemoveCard", new Vector2f(0f, 42f * 6), new Vector2f(180, 50));
        addRemoveCard.getDragBar().setIsVisible(false);
        addRemoveCard.setIgnoreMouse(true);
        mainWin.addChild(addRemoveCard);

        Button addCard = new ButtonAdapter(main.getScreen(), "addCard", new Vector2f(15, 10), new Vector2f(70, 30)) {
            @Override
            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                super.onButtonMouseLeftUp(evt, toggled);
                EntityData ed = app.getStateManager().getState(GameDataAppState.class).getEntityData();
                EntityId cardId = ed.createEntity();
                ed.setComponent(cardId, new RenderComponent("Cendrea"));
                ed.setComponent(cardId, new CardRenderComponent(CardRenderPosition.HAND));
            }
        };
        addCard.setText("Add");
        addRemoveCard.addChild(addCard);

        Button removeCard = new ButtonAdapter(main.getScreen(), "removeCard", new Vector2f(94, 10), new Vector2f(70, 30)) {
            @Override
            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                super.onButtonMouseLeftUp(evt, toggled);
                EntityData ed = app.getStateManager().getState(GameDataAppState.class).getEntityData();
                Object[] cards = app.getStateManager().getState(CardRenderSystem.class).getCardsKeyset().toArray();
                EntityId id = (EntityId) cards[FastMath.nextRandomInt(0, cards.length - 1)];
                ed.removeComponent(id, CardRenderComponent.class);
            }
        };
        removeCard.setText("Del");
        addRemoveCard.addChild(removeCard);
        
        /**
         * Button to return back to the main menu.
         */
        Window closeButtonWin = new Window(main.getScreen(), "CloseButtonWin", new Vector2f(0, 40f * 5), new Vector2f(180, 50));
        closeButtonWin.removeAllChildren();
        closeButtonWin.setIsResizable(false);
        closeButtonWin.setIsMovable(false);

        Button close = new ButtonAdapter(main.getScreen(), "returnMain", new Vector2f(15, 10), new Vector2f(150, 30)) {
            @Override
            public void onButtonMouseLeftDown(MouseButtonEvent evt, boolean toggled) {
                super.onButtonMouseLeftDown(evt, toggled);
                main.getStateManager().attach(new MainGUI(main));
                main.getStateManager().detach(main.getStateManager().getState(CardEditorAppState.class));
            }
        };
        close.setText("Return Main");
        closeButtonWin.addChild(close);
        mainWin.addChild(closeButtonWin);
    }

    @Override
    public void cleanup() {
        super.cleanup();
        main.getScreen().removeElement(main.getScreen().getElementById("mainWin"));
    }
    
}

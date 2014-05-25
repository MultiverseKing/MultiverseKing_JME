package gamestate.editormode;

import hexsystem.events.HexMapInputListener;
import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import entitysystem.EntityDataAppState;
import entitysystem.attribut.CardRenderPosition;
import entitysystem.card.CardProperties;
import entitysystem.card.CardRenderComponent;
import entitysystem.card.CardRenderSystem;
import entitysystem.loader.EntityLoader;
import entitysystem.render.RenderComponent;
import gamestate.EntitySystemAppState;
import gamestate.HexMapMouseInput;
import hexsystem.MapData;
import hexsystem.events.HexMapInputEvent;
import hexsystem.events.TileChangeEvent;
import hexsystem.events.TileChangeListener;
import java.io.File;
import java.io.FilenameFilter;
import kingofmultiverse.MainGUI;
import kingofmultiverse.MultiverseMain;
import tonegod.gui.controls.buttons.Button;
import tonegod.gui.controls.buttons.ButtonAdapter;
import tonegod.gui.controls.menuing.Menu;
import tonegod.gui.controls.windows.Window;

/**
 *
 * @author Eike Foede, Roah
 */
public class CardEditorAppState extends AbstractAppState implements TileChangeListener, HexMapInputListener {
    
    private MultiverseMain main;
    private MapData mapData;
    private Window mainWin;
//    private Menu loadCategory = null;
    
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
        mainWin.setWindowTitle("Card Editor");
        mainWin.setMinDimensions(new Vector2f(130, 130));
        mainWin.setIsResizable(false);
        mainWin.getDragBar().setIsMovable(false);
        main.getScreen().addElement(mainWin);
        
        /**
         * Button used to open the card generator menu.
         */
        Button mapElement = new ButtonAdapter(main.getScreen(), "cardGeneratorB", new Vector2f(15, 40)) {
            @Override
            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                super.onButtonMouseLeftUp(evt, toggled);
                if (!mainWin.getElementsAsMap().containsKey("cardGeneratorW")) {
                    generatorMenu();
                    if(mainWin.getElementsAsMap().containsKey("addRemoveCard")){
                        mainWin.getElementsAsMap().get("addRemoveCard").hide();
                    }
                } else {
                    mainWin.removeChild(mainWin.getElementsAsMap().get("cardGeneratorW"));
                    if(mainWin.getElementsAsMap().containsKey("addRemoveCard")){
                        mainWin.getElementsAsMap().get("addRemoveCard").show();
                    } else {
                        genCardAddRemoveWin();
                    }
                } 
            }
        };
        mapElement.setText("Generator");
        mainWin.addChild(mapElement);
        
        
        genCardAddRemoveWin();
        
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

    private void genCardAddRemoveWin() {
        /**
         * Window used to add or remove card from the GUI.
         */
        Window addRemoveCard = new Window(main.getScreen(), "addRemoveCard", new Vector2f(0f, 42f * 6), new Vector2f(180, 50));
        addRemoveCard.removeAllChildren();
//        addRemoveCard.getDragBar().setIsVisible(false);
        addRemoveCard.setIgnoreMouse(true);
        mainWin.addChild(addRemoveCard);

        Button addCard = new ButtonAdapter(main.getScreen(), "addCard", new Vector2f(15, 10), new Vector2f(70, 30)) {
            @Override
            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                super.onButtonMouseLeftUp(evt, toggled);
                EntityData ed = app.getStateManager().getState(EntitySystemAppState.class).getEntityData();
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
                EntityData ed = app.getStateManager().getState(EntitySystemAppState.class).getEntityData();
                Object[] cards = app.getStateManager().getState(CardRenderSystem.class).getCardsKeyset().toArray();
                EntityId id = (EntityId) cards[FastMath.nextRandomInt(0, cards.length - 1)];
                ed.removeComponent(id, CardRenderComponent.class);
            }
        };
        removeCard.setText("Del");
        addRemoveCard.addChild(removeCard);
    }

    private void generatorMenu() {
        /**
         * Window used to show card Properties.
         */
        Window genMenu = new Window(main.getScreen(), "cardGeneratorW", new Vector2f(130, 0), 
                new Vector2f(main.getScreen().getWidth()*0.5f, mainWin.getHeight()));
        genMenu.removeAllChildren();
        genMenu.setIsResizable(false);
        genMenu.setIsMovable(false);
        mainWin.addChild(genMenu);
        
        /**
         * Window used to show a preview of a card.
         */
        Window preview = new Window(main.getScreen(), "genPreview", new Vector2f(genMenu.getWidth(), 0), 
                new Vector2f(140,200), new Vector4f(), "Textures/Cards/cardEmpty.png");
        preview.removeAllChildren();
        preview.setIsResizable(false);
        preview.setIsMovable(false);
        genMenu.addChild(preview);
        
        /**
         * Window used to load and save card to/from file.
         */
        Window cardLoader = new Window(main.getScreen(), "cardLoader", new Vector2f(55, genMenu.getHeight()), new Vector2f(225, 50));
        cardLoader.removeAllChildren();
        cardLoader.setIgnoreMouse(true);
        genMenu.addChild(cardLoader);
        
        ButtonAdapter load = new ButtonAdapter(main.getScreen(), "loadCardButton", new Vector2f(10, 10)){
            @Override
            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                super.onButtonMouseLeftUp(evt, toggled);
                if(!main.getScreen().getElementsAsMap().containsKey("loadCategory")){
                    loadMenu();
                }
                Menu loadMenu = (Menu) main.getScreen().getElementById("loadCategory");
                loadMenu.showMenu(null, getAbsoluteX(), getAbsoluteY()-loadMenu.getHeight());
            }
        };
        load.setText("Load");
        cardLoader.addChild(load);
        
        ButtonAdapter save = new ButtonAdapter(main.getScreen(), "saveCardButton", new Vector2f(115, 10)){
            @Override
            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                super.onButtonMouseLeftUp(evt, toggled);
                System.err.println("Not implemented");
            }
        };
        save.setText("Save");
        cardLoader.addChild(save);
    }
    
    private void loadMenu(){
        /**
         * Some variable to get file.
         */
        File folder;
        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return (name.endsWith(".card"));
            }
        };
        
        /**
         * Menu listing all saved card.
         */
        folder = new File(System.getProperty("user.dir") + "/assets/Data/CardData/");
        Menu categoryAll = new Menu(main.getScreen(), "categoryAll", new Vector2f(0,0), false) {
            @Override
            public void onMenuItemClicked(int index, Object value, boolean isToggled) {
                EntityLoader loader = new EntityLoader();
                openCardWin(loader.loadCardProperties((String) value));
            }
        };
        
        File[] fList = folder.listFiles();
        for(File f : fList){
            if(!f.isDirectory()){
                int index = f.getName().lastIndexOf('.');
                categoryAll.addMenuItem(f.getName().substring(0, index), f.getName().substring(0, index), null);
            } else {
                String[] subF = f.list(filter);
                for(String fi : subF){
                    int index = fi.lastIndexOf('.');
                    categoryAll.addMenuItem(fi.substring(0, index), fi.substring(0, index), null);
                }
            }
        }
        main.getScreen().addElement(categoryAll);
        
        /**
         * Menu listing all saved Ability card.
         */
        folder = new File(System.getProperty("user.dir") + "/assets/Data/CardData/Ability");
        Menu categoryAbility = new Menu(main.getScreen(), "categoryAbility", new Vector2f(0,0), false) {
            @Override
            public void onMenuItemClicked(int index, Object value, boolean isToggled) {
            }
        };
        
        String[] abilityList = folder.list(filter);
        for(String s : abilityList){
            int index = s.lastIndexOf('.');
            categoryAbility.addMenuItem(s.substring(0, index), s.substring(0, index), null);
        }        
        main.getScreen().addElement(categoryAbility);
        
        /**
         * Menu listing all saved unit card.
         */
        folder = new File(System.getProperty("user.dir") + "/assets/Data/CardData/");
        Menu categoryUnit = new Menu(main.getScreen(), "categoryUnit", new Vector2f(0,0), false) {
            @Override
            public void onMenuItemClicked(int index, Object value, boolean isToggled) {
            }
        };
        
        String[] unitList = folder.list(filter);
        for(String s : unitList){
            int index = s.lastIndexOf('.');
            categoryUnit.addMenuItem(s.substring(0, index), s.substring(0, index), null);
        }        
        main.getScreen().addElement(categoryUnit);
        
        /**
         * Root menu.
         */
        final Menu loadCategory = new Menu(main.getScreen(), "loadCategory", new Vector2f(0,0), false) {
            @Override
            public void onMenuItemClicked(int index, Object value, boolean isToggled) { }
        };
        loadCategory.addMenuItem("All", null, categoryAll);
        loadCategory.addMenuItem("Unit", null, categoryUnit);
        loadCategory.addMenuItem("Ability", null, categoryAbility);

        main.getScreen().addElement(loadCategory);
    }

    private void openCardWin(CardProperties loadCardProperties) {
        
    }

    @Override
    public void cleanup() {
        super.cleanup();
        main.getScreen().removeElement(mainWin);
        main.getScreen().removeElement(main.getScreen().getElementById("categoryAll"));
        main.getScreen().removeElement(main.getScreen().getElementById("categoryAbility"));
        main.getScreen().removeElement(main.getScreen().getElementById("categoryUnit"));
        main.getScreen().removeElement(main.getScreen().getElementById("loadCategory"));
        mainWin = null;
        //@todo remove all card from the card system 
        // by getting all entity with the card renderer component and remove it
        //@todo remove all card from the field.
//        EntitySet comps = main.getStateManager().getState(EntityDataAppState.class).getEntityData().getEntities(CardRenderComponent.class);
    }
}

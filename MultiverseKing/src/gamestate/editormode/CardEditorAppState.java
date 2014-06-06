package gamestate.editormode;

import hexsystem.events.HexMapInputListener;
import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.KeyInput;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import entitysystem.attribut.Animation;
import entitysystem.attribut.CardRenderPosition;
import entitysystem.attribut.CardType;
import static entitysystem.attribut.CardType.ABILITY;
import static entitysystem.attribut.CardType.EQUIPEMENT;
import static entitysystem.attribut.CardType.SPELL;
import static entitysystem.attribut.CardType.SUMMON;
import static entitysystem.attribut.CardType.TRAP;
import entitysystem.attribut.Faction;
import entitysystem.attribut.Rarity;
import entitysystem.card.CardProperties;
import entitysystem.card.CardRenderComponent;
import entitysystem.card.CardRenderSystem;
import entitysystem.card.Hover;
import entitysystem.loader.EntityLoader;
import entitysystem.field.position.HexPositionComponent;
import entitysystem.field.render.AnimationComponent;
import entitysystem.field.render.RenderComponent;
import entitysystem.field.CollisionComponent;
import gamestate.EntityDataAppState;
import gamestate.HexMapMouseInput;
import gamestate.HexSystemAppState;
import hexsystem.HexSettings;
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
import tonegod.gui.controls.lists.Spinner;
import tonegod.gui.controls.menuing.Menu;
import tonegod.gui.controls.text.Label;
import tonegod.gui.controls.text.TextField;
import tonegod.gui.controls.windows.Window;
import tonegod.gui.core.Element;
import tonegod.gui.effects.Effect;
import utility.ElementalAttribut;
import utility.HexCoordinate;
import utility.Rotation;
import utility.Vector2Int;

/**
 * TODO : The GUI resolution should be consistant, 
 * gui scale should not depend on the resolution screen, 
 * have an option to change it's resolution.
 * @author Eike Foede, Roah
 */
public class CardEditorAppState extends AbstractAppState implements TileChangeListener, HexMapInputListener {

    private MultiverseMain main;
    private EntityData entityData;
    private MapData mapData;
    private Window mainWin;
//    private Menu loadCategory = null;
    private EntityId tuxDoll = null;
    private float rightMenu = 245;
    private Hover hover;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        main = (MultiverseMain) app;
        entityData = app.getStateManager().getState(EntityDataAppState.class).getEntityData();
        HexMapMouseInput mouseSystem = app.getStateManager().getState(HexMapMouseInput.class);
        mouseSystem.registerTileInputListener(this);
        mapData = app.getStateManager().getState(HexSystemAppState.class).getMapData();
        Vector3f center = mapData.getTileWorldPosition(
                new HexCoordinate(HexCoordinate.OFFSET, 
                new Vector2Int(HexSettings.CHUNK_SIZE/2, HexSettings.CHUNK_SIZE/2)));
        main.getRtsCam().setCenter(new Vector3f(center.x+3, 15, center.z+3));
        if (mapData.getMapName() == null || !mapData.getMapName().equalsIgnoreCase("reset")) {
            if (!mapData.loadMap("Reset")) {
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

    private float timer = 0.5f;
    private float current = 0;
    //Tux doll is instanced there to leave some delay
    @Override
    public void update(float tpf) {
        super.update(tpf);
        if(timer < current && tuxDoll == null){
        tuxDoll = entityData.createEntity();
        entityData.setComponents(tuxDoll, new RenderComponent("TuxDoll"),
                    new HexPositionComponent(new HexCoordinate(HexCoordinate.OFFSET, 
                    new Vector2Int(HexSettings.CHUNK_SIZE/2, HexSettings.CHUNK_SIZE/2)), Rotation.A),
                    new AnimationComponent(Animation.SUMMON), 
                    new CollisionComponent((byte)0));
        } else if (tuxDoll == null){
            current += tpf;
        }

    }
    
    private void initCardGUI() {
        mainWin = new Window(main.getScreen(), "mainWin", new Vector2f(15f, 15f),
                new Vector2f(130, 40 * 5));
        mainWin.setWindowTitle("Card Editor");
        mainWin.setMinDimensions(new Vector2f(130, 130));
        mainWin.setIsResizable(false);
        mainWin.getDragBar().setIsMovable(false);
        main.getScreen().addElement(mainWin);

        /**
         * Button used to open the card generator menu.
         */
        Button mapElement = new ButtonAdapter(main.getScreen(), "cardGeneratorB",
                new Vector2f(15, 40)) {
            @Override
            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                super.onButtonMouseLeftUp(evt, toggled);
                if (!mainWin.getElementsAsMap().containsKey("cardGeneratorW")) {
                    generatorMenu();
                    if (mainWin.getElementsAsMap().containsKey("addRemoveCard")) {
                        mainWin.getElementsAsMap().get("addRemoveCard").hide();
                    }
                } else {
                    mainWin.removeChild(mainWin.getElementsAsMap().get("cardGeneratorW"));
                    if (mainWin.getElementsAsMap().containsKey("addRemoveCard")) {
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
        Window closeButtonWin = new Window(main.getScreen(), "CloseButtonWin",
                new Vector2f(0, 40f * 5), new Vector2f(180, 50));
        closeButtonWin.removeAllChildren();
        closeButtonWin.setIsResizable(false);
        closeButtonWin.setIsMovable(false);

        Button close = new ButtonAdapter(main.getScreen(), "returnMain",
                new Vector2f(15, 10), new Vector2f(150, 30)) {
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
        Window addRemoveCard = new Window(main.getScreen(), "addRemoveCard",
                new Vector2f(0f, 42f * 6), new Vector2f(180, 50));
        addRemoveCard.removeAllChildren();
        addRemoveCard.setIgnoreMouse(true);
        mainWin.addChild(addRemoveCard);

        Button addCard = new ButtonAdapter(main.getScreen(), "addCard",
                new Vector2f(15, 10), new Vector2f(70, 30)) {
            @Override
            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                super.onButtonMouseLeftUp(evt, toggled);
                EntityData ed = app.getStateManager().getState(EntityDataAppState.class).getEntityData();
                EntityId cardId = ed.createEntity();
                ed.setComponent(cardId, new RenderComponent("Cendrea"));
                ed.setComponent(cardId, new CardRenderComponent(CardRenderPosition.HAND));
            }
        };
        addCard.setText("Add");
        addRemoveCard.addChild(addCard);

        Button removeCard = new ButtonAdapter(main.getScreen(), "removeCard",
                new Vector2f(94, 10), new Vector2f(70, 30)) {
            @Override
            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                super.onButtonMouseLeftUp(evt, toggled);
                Object[] cards = app.getStateManager().getState(CardRenderSystem.class).getCardsKeyset().toArray();
                if (cards.length != 0) {
                    EntityData ed = app.getStateManager().getState(EntityDataAppState.class).getEntityData();
                    EntityId id = (EntityId) cards[FastMath.nextRandomInt(0, cards.length - 1)];
                    ed.removeComponent(id, CardRenderComponent.class);
                }
            }
        };
        removeCard.setText("Del");
        addRemoveCard.addChild(removeCard);
    }

    private void generatorMenu() {
        /**
         * Window used to show card Properties.
         */
        cleanupTestCard();
        Window genMenu = new Window(main.getScreen(), "cardGeneratorW", new Vector2f(130, 0),
                new Vector2f(main.getScreen().getWidth() * 0.5f, mainWin.getHeight()));
        genMenu.removeAllChildren();
        genMenu.setIsResizable(false);
        genMenu.setIsMovable(false);
        mainWin.addChild(genMenu);
        
        /**
         * Configure all button of the menu.
         */
        initGeneratorMenu(genMenu);
        
        /**
         * Window used to show a preview of a card.
         */
        ButtonAdapter preview = new ButtonAdapter(main.getScreen(), "geneneratorImgPreview", new Vector2f(genMenu.getWidth(), 0),
                new Vector2f(140, 200), new Vector4f(), "Textures/Cards/Artworks/undefined.png"){
            @Override
            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                super.onButtonMouseLeftUp(evt, toggled);
                if (!main.getScreen().getElementsAsMap().containsKey("loadImgMenu")) {
                    loadImgMenu();
                }
                Menu loadImgMenu = (Menu) main.getScreen().getElementById("loadImgMenu");
                loadImgMenu.showMenu(null, getAbsoluteX()+getDimensions().x, getAbsoluteY());
            }

            private void loadImgMenu() {
                Menu imgMenu = new Menu(screen, "loadImgMenu", new Vector2f(), false) {
                    @Override
                    public void onMenuItemClicked(int index, Object value, boolean isToggled) {
                        screen.getElementById("geneneratorImgPreview").setColorMap("Textures/Cards/Artworks/"+value);
                    }
                };
                FilenameFilter filter = new FilenameFilter() {
                    public boolean accept(File dir, String name) {
                        return (name.endsWith(".png"));
                    }
                };
                File folder = new File(System.getProperty("user.dir") + "/assets/Textures/Cards/Artworks");
                File[] fList = folder.listFiles(filter);
                for (File f : fList) {
                    if (!f.isDirectory()) {
                        int index = f.getName().lastIndexOf('.');
                        imgMenu.addMenuItem(f.getName().substring(0, index), f.getName(), null);
                    }
                }
                screen.addElement(imgMenu);
            }
        };
        preview.removeEffect(Effect.EffectEvent.Hover);
        preview.removeEffect(Effect.EffectEvent.Press);
        preview.removeAllChildren();
        preview.setIsResizable(false);
        preview.setIsMovable(false);
        hover = new Hover(main.getScreen(), new Vector2f(), preview.getDimensions());
        CardProperties cardProperties = new CardProperties(0, Faction.NEUTRAL, CardType.SUMMON, 
                Rarity.COMMON, ElementalAttribut.NULL, "This is a Testing unit");
        hover.setProperties(cardProperties, "TuxDoll");
        preview.addChild(hover);
        genMenu.addChild(preview);
        
        /**
         * Window used to load and save card to/from file.
         */
        Window cardLoader = new Window(main.getScreen(), "cardLoader",
                new Vector2f(55, genMenu.getHeight()), new Vector2f(225, 50));
        cardLoader.removeAllChildren();
        cardLoader.setIgnoreMouse(true);
        genMenu.addChild(cardLoader);
        
        ButtonAdapter load = new ButtonAdapter(main.getScreen(), "loadCardButton", new Vector2f(10, 10)) {
            @Override
            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                super.onButtonMouseLeftUp(evt, toggled);
                if (!main.getScreen().getElementsAsMap().containsKey("loadCategory")) {
                    openLoadingMenu();
                }
                Menu loadMenu = (Menu) main.getScreen().getElementById("loadCategory");
                loadMenu.showMenu(null, getAbsoluteX(), getAbsoluteY() - loadMenu.getHeight());
            }
        };
        load.setText("Load");
        cardLoader.addChild(load);

        ButtonAdapter save = new ButtonAdapter(main.getScreen(), "saveCardButton", new Vector2f(115, 10)) {
            @Override
            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                super.onButtonMouseLeftUp(evt, toggled);
                System.err.println("Not implemented");
            }
        };
        save.setText("Save");
        cardLoader.addChild(save);
    }
    
    private void initGeneratorMenu(Window callerMenu) {
        /**
         * Part used to show/set the card Name.
         */
        Label nameLabel = new Label(main.getScreen(), "generatorNameLabel",
                new Vector2f(10, 5), new Vector2f(60, 35));
        nameLabel.setText("Name : ");
        callerMenu.addChild(nameLabel);

        ButtonAdapter nameButton = new ButtonAdapter(main.getScreen(), "generatorFieldButton",
                new Vector2f(nameLabel.getDimensions().x + 5, 12)){
            @Override
            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                super.onButtonMouseLeftUp(evt, toggled);
                TextField field = new TextField(main.getScreen(), "generatorNameField",
                        new Vector2f(getElementParent().getElementsAsMap().get("generatorNameLabel").getDimensions().x+5, 12), 
                        new Vector2f(100, 30)){
                    @Override
                    public void onKeyRelease(KeyInputEvent evt) {
                        super.onKeyRelease(evt);
                        if(evt.getKeyCode() == KeyInput.KEY_RETURN){
                            screen.getElementById("generatorFieldButton").setText(getText());
                            hover.setCardName(getText());
                            getElementParent().removeChild(this);
                        }
                    }
                };
                field.setMaxLength(15);
                field.setText(getText());
                getElementParent().addChild(field);
            }            
        };
        nameButton.setText("TuxDoll");
        callerMenu.addChild(nameButton);
        
        /**
         * Part used to show/choose the card Type.
         */
        Label subTypeLabel = new Label(main.getScreen(), "generatorCardTypeLabel", new Vector2f(10, 45), new Vector2f(95, 35));
        subTypeLabel.setText("Card Type : ");
        callerMenu.addChild(subTypeLabel);
        
        ButtonAdapter subTypeButton = new ButtonAdapter(main.getScreen(), "generatorCardTypeButton",
                new Vector2f(subTypeLabel.getDimensions().x , 48)) {
            @Override
            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                if (!main.getScreen().getElementsAsMap().containsKey("generatorCardTypeMenu")) {
                    loadSubTypeMenu();
                }
                Menu SubTypeMenu = (Menu) main.getScreen().getElementById("generatorCardTypeMenu");
                SubTypeMenu.showMenu(null, getAbsoluteX(), getAbsoluteY() - SubTypeMenu.getHeight());
            }
        };
        subTypeButton.setText(CardType.SUMMON.name());
        callerMenu.addChild(subTypeButton);
        
        /**
         * Part used to show/set the card Description text.
         */
        Label descriptionLabel = new Label(main.getScreen(), "generatorDescriptionLabel",
                new Vector2f(10, 85), new Vector2f(150, 35));
        descriptionLabel.setText("Description : ");
        callerMenu.addChild(descriptionLabel);
        
        ButtonAdapter descriptionButton = new ButtonAdapter(main.getScreen(), "generatorDescriptionButton",
                new Vector2f(10, 115), new Vector2f(main.getScreen().getElementById("cardGeneratorW").getWidth()*0.9f, 30)){
            @Override
            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                super.onButtonMouseLeftUp(evt, toggled);
                TextField field = new TextField(main.getScreen(), "generatorDescriptionField",
                        new Vector2f(10,115), new Vector2f(screen.getElementById("cardGeneratorW").getWidth()*0.9f, 30)){
                    @Override
                    public void onKeyRelease(KeyInputEvent evt) {
                        super.onKeyRelease(evt);
                        if(evt.getKeyCode() == KeyInput.KEY_RETURN){
                            screen.getElementById("generatorDescriptionButton").setText(getText());
                            getElementParent().removeChild(this);
                        }
                    }
                };
                field.setText(getText());
                getElementParent().addChild(field);
            }            
        };
        descriptionButton.setText("This is a Testing unit");
        callerMenu.addChild(descriptionButton);
        
        /**
         * Part used to show/choose the card faction.
         */
        Label factionLabel = new Label(main.getScreen(), "generatorFactionLabel", new Vector2f(rightMenu, 5), new Vector2f(75, 35));
        factionLabel.setText("Faction : ");
        callerMenu.addChild(factionLabel);
        
        ButtonAdapter factionButton = new ButtonAdapter(main.getScreen(), "generatorFactionButton",
                new Vector2f(rightMenu + factionLabel.getDimensions().x, 11)) {
            @Override
            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                if (!main.getScreen().getElementsAsMap().containsKey("generatorFactionMenu")) {
                    OpenFactionMenu();
                }
                Menu factionMenu = (Menu) main.getScreen().getElementById("generatorFactionMenu");
                factionMenu.showMenu(null, getAbsoluteX(), getAbsoluteY() - factionMenu.getHeight());
            }
        };
        factionButton.setText(Faction.NEUTRAL.name());
        callerMenu.addChild(factionButton);
        
        /**
         * Part used to show/choose the card Element Attribut.
         */
        Label elementTypeLabel = new Label(main.getScreen(), "generatorEAttributLabel", new Vector2f(rightMenu, 45), new Vector2f(80, 35));
        elementTypeLabel.setText("E.Attribut : ");
        callerMenu.addChild(elementTypeLabel);
        
        ButtonAdapter elementTypeButton = new ButtonAdapter(main.getScreen(), "generatorEAttributButton",
                new Vector2f(rightMenu + elementTypeLabel.getDimensions().x +5 , 48)) {
            @Override
            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                if (!main.getScreen().getElementsAsMap().containsKey("generatorEAttributMenu")) {
                    loadElementalMenu();
                }
                Menu SubTypeMenu = (Menu) main.getScreen().getElementById("generatorEAttributMenu");
                SubTypeMenu.showMenu(null, getAbsoluteX(), getAbsoluteY() - SubTypeMenu.getHeight());
            }
        };
        elementTypeButton.setText(ElementalAttribut.NULL.name());
        callerMenu.addChild(elementTypeButton);
        
        /**
         * Part used to show/choose the cost needed to use the card.
         */
        Label costLabel = new Label(main.getScreen(), "generatorCostLabel", new Vector2f(rightMenu, 80), new Vector2f(75, 35));
        costLabel.setText("Cost : ");
        callerMenu.addChild(costLabel);
        
        Spinner spinner = new Spinner(main.getScreen(), "generatorCostSpinner", 
                        new Vector2f(rightMenu + costLabel.getDimensions().x -22 , 85), Spinner.Orientation.HORIZONTAL, true){
            @Override
            public void onChange(int selectedIndex, String value) {
                hover.setCastCost(selectedIndex);
            }
        };
        spinner.setStepIntegerRange(0, 20, 1);
        spinner.setSelectedIndex(0);
        callerMenu.addChild(spinner);
        
        /**
         * Part used to test out the card.
         */
        Window cardTest = new Window(main.getScreen(), "cardTestWin",
                new Vector2f(285, callerMenu.getHeight()), new Vector2f(150, 50));
        cardTest.removeAllChildren();
        cardTest.setIgnoreMouse(true);
        callerMenu.addChild(cardTest);
        
        ButtonAdapter cast = new ButtonAdapter(main.getScreen(), "castCardButton", new Vector2f(10, 10), new Vector2f(130, 30)) {
            @Override
            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                super.onButtonMouseLeftUp(evt, toggled);
                
            }
        };
        cast.setText("Cast test !");
        cardTest.addChild(cast);
        
    }
    // <editor-fold defaultstate="collapsed" desc="Menu Selection">
    private void loadElementalMenu() {
        Menu elementalMenu = new Menu(main.getScreen(), "generatorEAttributMenu", new Vector2f(0, 30), false) {
            @Override
            public void onMenuItemClicked(int index, Object value, boolean isToggled) {
                ElementalAttribut eAttribut = (ElementalAttribut) value;
                main.getScreen().getElementById("generatorEAttributButton").setText(eAttribut.name());
                main.getScreen().getElementById("cardPropertiesHover").setColorMap("Textures/Cards/"+eAttribut.name()+".png");
                
            }
        };
        ElementalAttribut[] eAttributList = ElementalAttribut.values();
        for (ElementalAttribut eAttribut : eAttributList) {
            elementalMenu.addMenuItem(eAttribut.name(), eAttribut, null);
        }
        main.getScreen().addElement(elementalMenu);
    }
    
    private void loadSubTypeMenu() {
        Menu subType = new Menu(main.getScreen(), "generatorCardTypeMenu", new Vector2f(0, 30), false) {
            @Override
            public void onMenuItemClicked(int index, Object value, boolean isToggled) {
                CardType cardType = (CardType) value;
                Element genButton = main.getScreen().getElementById("generatorCardTypeButton");
                if(!genButton.getText().equals(cardType.name())){
                    genButton.setText(cardType.name());
                    //TODO: change the hover depending on the card type
                    hover.setType(cardType);
                    openCardTypeSubMenu(cardType);
                }
            }
        };
        CardType[] cardSubTypeList = CardType.values();
        for (CardType s : cardSubTypeList) {
            subType.addMenuItem(s.name(), s, null);
        }
        main.getScreen().addElement(subType);
    }

    // <editor-fold defaultstate="collapsed" desc="Open CardTypeSubMenu">
    /**
     * Open a menu where card properties can be tweaked,
     * it depend on the subMenu loaded, who depend on the cardType.
     */
    public void openCardTypeSubMenu(CardType cardType) {
        Window subMenu;
        if(main.getScreen().getElementById("subMenu") != null){
            subMenu = (Window) main.getScreen().getElementById("subMenu");
            subMenu.removeAllChildren();
        } else {
            Element win = main.getScreen().getElementById("geneneratorImgPreview");
            subMenu = new Window(main.getScreen(), "subMenu", new Vector2f(
                    win.getAbsoluteX(), 0), new Vector2f(
                    main.getScreen().getWidth()-(win.getAbsoluteX()+win.getWidth()+10),
                    245));
            subMenu.removeAllChildren();
            mainWin.getElementsAsMap().get("cardGeneratorW").addChild(subMenu);
        }
        switch(cardType) {
            case ABILITY:
                openSubAbilityMenu(subMenu);
                break;
            case EQUIPEMENT:
                /**
                 * @todo: Equipement subMenu
                 */ 
                break;
            case SPELL:
                /*
                 * @todo: Spell subMenu
                 */ 
                break;
            case TRAP:
                /**
                 * @todo: Trap subMenu
                 */ 
                break;
            case SUMMON:
                openSubUnitMenu(subMenu);
                break;
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Used SubMenu">
    /**
     * Menu used to tweak ability properties.
     */
    private void openSubAbilityMenu(Window subMenu) {

        /**
         * Part used to show/set how many power have the ability.
         * damage - segmentCost - activationRange - FXUsed - hitCollision - isCastOnSelf
         */
        Label damageLabel = new Label(main.getScreen(), "powerLabel", new Vector2f(10,10), new Vector2f(58,30));
        damageLabel.setText("Power : ");
        subMenu.addChild(damageLabel);

        ButtonAdapter damageButton = new ButtonAdapter(main.getScreen(), "powerButton", 
                new Vector2f(damageLabel.getWidth(), 2), new Vector2f(45,30)){
            @Override
            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                super.onButtonMouseLeftUp(evt, toggled);
                TextField damageField = new TextField(screen, Vector2f.ZERO, new Vector2f(45, 30)){
                    @Override
                    public void onKeyRelease(KeyInputEvent evt) {
                        super.onKeyRelease(evt);
                        if(evt.getKeyCode() == KeyInput.KEY_RETURN 
                                && getText() != null && getText().length() != 0){
                            screen.getElementById("powerButton").setText(getText());
                            getElementParent().removeChild(this);
                        }
                    }
                };
                damageField.setType(TextField.Type.NUMERIC);
                addChild(damageField);
                damageField.setText(getText());
            }
        };
        damageButton.setText("0");
        damageLabel.addChild(damageButton);

        /**
         * Part used to show/set the segmentCost needed for the unit
         * to cast the ability.
         */
        Label unitCastCostLabel = new Label(main.getScreen(), "unitCastCostLabel", new Vector2f(10,40), new Vector2f(160,30));
        unitCastCostLabel.setText("Unit Segment Used : ");
        subMenu.addChild(unitCastCostLabel);

        Spinner unitCastCostSpinner = new Spinner(main.getScreen(), "unitCastCostSlider",
                new Vector2f(10, unitCastCostLabel.getDimensions().y), Element.Orientation.HORIZONTAL, true) {

            @Override
            public void onChange(int selectedIndex, String value) {
//                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };
        unitCastCostSpinner.setStepIntegerRange(0, 20, 1);
        unitCastCostLabel.addChild(unitCastCostSpinner);

        /**
         * @todo Part used to set the Activation range of the ability.
         */
    }

    /**
     * @todo Menu used to tweak unit properties.
     * 
     */
    private void openSubUnitMenu(Window subMenu) {
//                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    // </editor-fold>
    // </editor-fold>
    
    private void OpenFactionMenu() {
        Menu faction = new Menu(main.getScreen(), "generatorFactionMenu", new Vector2f(0, 30), false) {
            @Override
            public void onMenuItemClicked(int index, Object value, boolean isToggled) {
                Faction f = (Faction) value;
                main.getScreen().getElementById("generatorFactionButton").setText(f.name());
                hover.setFaction(f);
            }
        };
        Faction[] factionList = Faction.values();
        for (Faction f : factionList) {
            faction.addMenuItem(f.name(), f, null);
        }
        main.getScreen().addElement(faction);
    }

    private void openLoadingMenu() {
        /**
         * Some variable to get files name.
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
        Menu categoryAll = new Menu(main.getScreen(), "categoryAll", new Vector2f(0, 0), false) {
            @Override
            public void onMenuItemClicked(int index, Object value, boolean isToggled) {
                loadCardPropertiesFromFile(value.toString());
            }
        };
        
        File[] fList = folder.listFiles();
        for (File f : fList) {
            if (f.isDirectory()) {
                String[] subF = f.list(filter);
                for (String fi : subF) {
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
        Menu categoryAbility = new Menu(main.getScreen(), "categoryAbility", new Vector2f(0, 0), false) {
            @Override
            public void onMenuItemClicked(int index, Object value, boolean isToggled) {
                loadCardPropertiesFromFile(value.toString());
            }
        };
        
        String[] abilityList = folder.list(filter);
        for (String s : abilityList) {
            int index = s.lastIndexOf('.');
            categoryAbility.addMenuItem(s.substring(0, index), s.substring(0, index), null);
        }
        main.getScreen().addElement(categoryAbility);
        
        /**
         * Menu listing all saved unit card.
         */
        folder = new File(System.getProperty("user.dir") + "/assets/Data/CardData/Units");
        Menu categoryUnit = new Menu(main.getScreen(), "categoryUnit", new Vector2f(0, 0), false) {
            @Override
            public void onMenuItemClicked(int index, Object value, boolean isToggled) {
                loadCardPropertiesFromFile(value.toString());
            }
        };
        
        String[] unitList = folder.list(filter);
        for (String s : unitList) {
            int index = s.lastIndexOf('.');
            categoryUnit.addMenuItem(s.substring(0, index), s.substring(0, index), null);
        }
        main.getScreen().addElement(categoryUnit);
        
        /**
         * Root menu.
         */
        final Menu loadCategory = new Menu(main.getScreen(), "loadCategory", new Vector2f(0, 0), false) {
            @Override
            public void onMenuItemClicked(int index, Object value, boolean isToggled) {
            }
        };
        loadCategory.addMenuItem("All", null, categoryAll);
        loadCategory.addMenuItem("Unit", null, categoryUnit);
        loadCategory.addMenuItem("Ability", null, categoryAbility);
        
        main.getScreen().addElement(loadCategory);
    }
    
    private void loadCardPropertiesFromFile(String cardName) {
        System.out.println(cardName);
        EntityLoader loader = new EntityLoader();
        CardProperties load = loader.loadCardProperties(cardName);
        if(load != null){
            /**
             * Load the card cost.
             */
            Spinner cost = (Spinner) main.getScreen().getElementById("generatorCostSpinner");
            cost.setSelectedIndex(load.getPlayCost());
            /**
             * Load the element Attribut.
             */
            main.getScreen().getElementById("generatorEAttributButton").setText(load.getElement().name());
            main.getScreen().getElementById("cardPropertiesHover").setColorMap("Textures/Cards/"+load.getElement().name()+".png");
            /**
             * Load the Faction Attribut.
             */
            main.getScreen().getElementById("generatorFactionButton").setText(load.getFaction().name());
            hover.setFaction(load.getFaction());
            /**
             * Load the description Text.
             */
            main.getScreen().getElementById("generatorDescriptionButton").setText(load.getDescription());
            /**
             * Load the card type.
             */
            Element genButton = main.getScreen().getElementById("generatorCardTypeButton");
            if(!genButton.getText().equals(load.getCardSubType().name())){
                genButton.setText(load.getCardSubType().name());
                hover.setType(load.getCardSubType());
                openCardTypeSubMenu(load.getCardSubType());
            }
        }
    }
    
    @Override
    public void cleanup() {
        super.cleanup();
        main.getScreen().removeElement(mainWin);
        String[] toRemove = new String[]{"categoryAll", "categoryAbility", "categoryUnit", 
            "loadCategory", "generatorCardTypeMenu", "generatorFactionMenu"};
        for (String s : toRemove) {
            if (main.getScreen().getElementsAsMap().containsKey(s)) {
                main.getScreen().removeElement(main.getScreen().getElementById(s));
            }
        }
        cleanupTestCard();
        entityData.removeEntity(tuxDoll);
        mainWin = null;
    }
    
    private void cleanupTestCard(){
        CardRenderSystem cardRender = main.getStateManager().getState(CardRenderSystem.class);
        if(cardRender != null){
            Object[] cards = cardRender.getCardsKeyset().toArray();
            if (cards.length != 0) {
                EntityData ed = main.getStateManager().getState(EntityDataAppState.class).getEntityData();
                for (Object o : cards) {
                    EntityId id = (EntityId) o;
                    ed.removeEntity(id);
                }
            }
        }
    }
}
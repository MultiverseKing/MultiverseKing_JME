/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gamestate.Editor;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import entitysystem.EntityDataAppState;
import entitysystem.card.CardEntityRenderSystem;
import entitysystem.card.CardRenderComponent;
import hexsystem.HexTile;
import hexsystem.MapData;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import kingofmultiverse.MultiverseMain;
import utility.attribut.ElementalAttribut;
import tonegod.gui.controls.buttons.Button;
import tonegod.gui.controls.buttons.ButtonAdapter;
import tonegod.gui.controls.buttons.RadioButtonGroup;
import tonegod.gui.controls.windows.Window;
import utility.HexCoordinate;

/**
 * 
 * @author roah
 */
class EditorGUI extends AbstractAppState {

    private final MapData mapData;
    private MultiverseMain main;
    private HexCoordinate currentTilePosition;
    private RadioButtonGroup tilePButtonGroup;
    
    private Window eWin;
//    private Window mainWin;

    EditorGUI(MapData mapData) {
        this.mapData = mapData;
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app); //To change body of generated methods, choose Tools | Templates.
        main = (MultiverseMain) app;

        Window mainWin = new Window(main.getScreen(), "EditorMain", new Vector2f(15f, 15f), new Vector2f(130, 40 * 5));
        mainWin.setWindowTitle("Main Windows");
        mainWin.setMinDimensions(new Vector2f(130, 130));
        mainWin.setIsResizable(false);
        mainWin.getDragBar().setIsMovable(false);
        main.getScreen().addElement(mainWin);

        /**
         * Button used to change the current map elemental attribut.
         */
        Button mapElement = new ButtonAdapter(main.getScreen(), "mapElement", new Vector2f(15, 40)){
            @Override
            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                super.onButtonMouseLeftUp(evt, toggled);
                if(eWin == null){
                    elementalWindow();
                }
            }
        };
        mapElement.setText("Change Map Elements");
        mainWin.addChild(mapElement);

        /**
         * Button used to save the map in a folder/file of his name.
         */
        Button save = new ButtonAdapter(main.getScreen(), "save", new Vector2f(15, 40 * 2)){
            @Override
            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                super.onButtonMouseLeftUp(evt, toggled);
                try {
                    mapData.saveMap();
                } catch (IOException ex) {
                    Logger.getLogger(EditorGUI.class.getName()).log(Level.SEVERE, "Couldn't save the map.", ex);
                }
            }
        };
        save.setText("Save");
        mainWin.addChild(save);

        /**
         * Button to load a predifinned map.
         * @todo add a context menu where you will be able to set the name of the map to load.
         */
        Button load = new ButtonAdapter(main.getScreen(), "load", new Vector2f(15, 40 * 3)){
            @Override
            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                super.onButtonMouseLeftUp(evt, toggled);
                mapData.loadMap("IceLand");
            }
        };
        load.setText("Load");
        mainWin.addChild(load);
        
        /**
         * Load a predefined void map from a File(same as the starting one).
         */
        Button reset = new ButtonAdapter(main.getScreen(), "reset", new Vector2f(15, 40 * 4)){
            @Override
            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                super.onButtonMouseLeftUp(evt, toggled);
                mapData.loadMap("Reset");
            }
        };
        reset.setText("Reset");
        mainWin.addChild(reset);

        /**
         * Card system initialisation.
         * Add a card when adding the Card system.
         */
        Window cardButtonWin = new Window(main.getScreen(), "cardMain", new Vector2f(15f, 25f+40f*5), new Vector2f(180, 40));
        cardButtonWin.getDragBar().setIsVisible(false);
        main.getScreen().addElement(cardButtonWin);
        
        Button cardEditor = new ButtonAdapter(main.getScreen(), "cardEditor", new Vector2f(15, 10), new Vector2f(150, 30)){
            private boolean activeCardEditor = false;
            
            @Override
            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                super.onButtonMouseLeftUp(evt, toggled);
                if(!activeCardEditor){
                    //to change, testing purpose
                    CardEntityRenderSystem cardSystem = app.getStateManager().getState(CardEntityRenderSystem.class);
                    if(cardSystem.gotCardsIsEmpty()){
                        EntityData ed = app.getStateManager().getState(EntityDataAppState.class).getEntityData();
                        EntityId cardId = ed.createEntity();
                        ed.setComponent(cardId, new CardRenderComponent("Cendrea"));
                    }
                    
                    main.getScreen().getElementById("cardEditor").setText("Card Editor: ON");
                    main.getScreen().getElementById("addRemoveCard").show();
                    activeCardEditor = !activeCardEditor;
                } else {
                    //@todo put the card system to pause
                    main.getScreen().getElementById("cardEditor").setText("Card Editor: OFF");
                    main.getScreen().getElementById("addRemoveCard").hide();
                    activeCardEditor = !activeCardEditor;
                }
            }
        };
        cardEditor.setText("Card Editor: OFF");
        cardButtonWin.addChild(cardEditor);
        
        Window addRemoveCard = new Window(main.getScreen(), "addRemoveCard", new Vector2f(15f, 55f+40f*6), new Vector2f(180, 20));
        addRemoveCard.getDragBar().setIsVisible(false);
        addRemoveCard.setIgnoreMouse(true);
        main.getScreen().addElement(addRemoveCard);
        
        Button addCard = new ButtonAdapter(main.getScreen(), "addCard", new Vector2f(15, 10), new Vector2f(75, 30)){
            @Override
            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                super.onButtonMouseLeftUp(evt, toggled);
                EntityData ed = app.getStateManager().getState(EntityDataAppState.class).getEntityData();
                EntityId cardId = ed.createEntity();
                ed.setComponent(cardId, new CardRenderComponent("Cendrea"));
            }
        };
        addCard.setText("Add");
        addRemoveCard.addChild(addCard);
        
        Button removeCard = new ButtonAdapter(main.getScreen(), "removeCard", new Vector2f(100, 10), new Vector2f(75, 30)){
            @Override
            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                super.onButtonMouseLeftUp(evt, toggled);
                EntityData ed = app.getStateManager().getState(EntityDataAppState.class).getEntityData();
                Object[] cards = app.getStateManager().getState(CardEntityRenderSystem.class).getCardsKeyset().toArray();
                EntityId id = (EntityId) cards[FastMath.nextRandomInt(0, cards.length-1)];
                ed.removeComponent(id, CardRenderComponent.class);
            }
        };
        removeCard.setText("Del");
        addRemoveCard.addChild(removeCard);
        addRemoveCard.hide();
    }

    /**
     * Context menu used to let you chose the element for map to change to.
     */
    public final void elementalWindow() {
        eWin =  new Window(main.getScreen(), "EWindows",
                new Vector2f((main.getScreen().getWidth() / 2) - 175, (main.getScreen().getHeight() / 2) - 100));
        eWin.setWindowTitle("Elemental Windows");
        RadioButtonGroup elementG = new RadioButtonGroup(main.getScreen(), "EButtonGroup") {
            @Override
            public void onSelect(int index, Button value) {
                if (index != ElementalAttribut.getSize()) {
                    changeMapElement(ElementalAttribut.convert((byte) index));
                } else {
                    main.getScreen().removeElement(main.getScreen().getElementById("EWindows"));
                    eWin = null;
                }
            }

            private void changeMapElement(ElementalAttribut eAttribut) {
//                main.getStateManager().getState(HexMap.class).changeZoneElement(eAttribut);
                mapData.setMapElement(eAttribut);
            }
        };
        for (int i = 0; i < ElementalAttribut.getSize(); i++) {
            ButtonAdapter button = new ButtonAdapter(main.getScreen(), "EButton+" + ElementalAttribut.convert((byte) i), new Vector2f(15, 40 + (40 * i)));
            button.setText(ElementalAttribut.convert((byte) i).toString());
            elementG.addButton(button);
        }
        Button closeButton = new ButtonAdapter(main.getScreen(), "EClose", new Vector2f(15, 40 + (40 * ElementalAttribut.getSize())));
        closeButton.setText("CLOSE");
        elementG.addButton(closeButton);
        elementG.setDisplayElement(eWin);

        main.getScreen().addElement(eWin);
    }

    /**
     * Method used to open a window related to the selected hex.
     * @param tile selected one.
     */
    void openHexPropertiesWin(HexCoordinate tile) {
        currentTilePosition = tile;
        if (main.getScreen().getElementById("tileP") != null) {
            tilePButtonGroup.setSelected(mapData.getTile(tile).getElement().ordinal());
        } else {
            tilePropertiesWin();
        }
    }

    /**
     * Context menu used to show the tile properties.
     */
    private void tilePropertiesWin() {
        Window tileWin = new Window(main.getScreen(), "tileP", new Vector2f(main.getScreen().getWidth() - 170, 20), new Vector2f(155f, 40 + (40 * (ElementalAttribut.getSize() + 1))));
        tileWin.setWindowTitle("Tile Properties");
        tilePButtonGroup = new RadioButtonGroup(main.getScreen(), "tilePButtonGroup") {
            @Override
            public void onSelect(int index, Button value) {
                if (index < ElementalAttribut.getSize()) {
                    mapData.setTile(currentTilePosition, new HexTile(ElementalAttribut.convert((byte) index), (byte) mapData.getTile(currentTilePosition).getHeight()));
                } else if (index == ElementalAttribut.getSize()) {
                    main.getScreen().removeElement(main.getScreen().getElementById("tileP"));
                }
            }
        };
        for (int i = 0; i < ElementalAttribut.getSize(); i++) {
            ButtonAdapter button = new ButtonAdapter(main.getScreen(), "TButton+" + ElementalAttribut.convert((byte) i), new Vector2f(15, 40 + (40 * i)));
            button.setText(ElementalAttribut.convert((byte) i).toString());
            tilePButtonGroup.addButton(button);
        }
        Button closeButton = new ButtonAdapter(main.getScreen(), "TClose", new Vector2f(15, 40 + (40 * ElementalAttribut.getSize())));
        closeButton.setText("CLOSE");
        tilePButtonGroup.addButton(closeButton);

        Button upButton = new ButtonAdapter(main.getScreen(), "UP", new Vector2f(120, 40), new Vector2f(25, 50)) {
            @Override
            public void onButtonMouseLeftDown(MouseButtonEvent evt, boolean toggled) {
                super.onButtonMouseLeftDown(evt, toggled); //To change body of generated methods, choose Tools | Templates.
                mapData.setTileHeight(currentTilePosition, (byte) (mapData.getTile(currentTilePosition).getHeight() + 1));
            }
        };
        closeButton.setText("UP");
        tileWin.addChild(upButton);

        Button downButton = new ButtonAdapter(main.getScreen(), "Down", new Vector2f(120, (20 + (40 * ElementalAttribut.getSize()))), new Vector2f(25, 50)) {
            @Override
            public void onButtonMouseLeftDown(MouseButtonEvent evt, boolean toggled) {
                super.onButtonMouseLeftDown(evt, toggled); //To change body of generated methods, choose Tools | Templates.
                mapData.setTileHeight(currentTilePosition, (byte) (mapData.getTile(currentTilePosition).getHeight() - 1));
            }
        };
        closeButton.setText("CLOSE");
        tileWin.addChild(downButton);

        tilePButtonGroup.setDisplayElement(tileWin); // null adds the button list to the screen layer
        tilePButtonGroup.setSelected(mapData.getTile(currentTilePosition).getElement().ordinal());

        tileWin.setIsResizable(false);
        tileWin.getDragBar().setIsMovable(false);
        
        main.getScreen().addElement(tileWin);
    }
    
}
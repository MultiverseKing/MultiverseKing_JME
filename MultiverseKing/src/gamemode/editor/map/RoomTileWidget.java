package gamemode.editor.map;

import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import com.jme3.renderer.Camera;
import gamemode.gui.AnimatedButton;
import gamemode.gui.CameraTrackWindow;
import java.util.ArrayList;
import tonegod.gui.core.Element;
import tonegod.gui.core.Screen;
import utility.HexCoordinate;

/**
 * Widget menu allowing modification on one tiles.
 * @author roah
 */
class RoomTileWidget extends CameraTrackWindow {

    private ArrayList<AnimatedButton> animatedButton = new ArrayList<AnimatedButton>();
    private ElementalWidgetMenu eWin = null;
    private RoomEditorSystem system;
    private Element eAttributIco;
    private HexCoordinate selectedTilePosition;

    
    RoomTileWidget(Screen screen, Camera camera, RoomEditorSystem system, HexCoordinate tilePos) {
        super(screen, camera);
        super.screenElement = new Element(screen, "tileWidgetMenu", Vector2f.ZERO,
                new Vector2f(150, 150), Vector4f.ZERO, "Textures/Icons/EditorMap/rouage.png");
        this.system = system;
        this.selectedTilePosition = tilePos;

        populateRightMenu();
        screenElement.scale(0.7f);
    }

    private void populateRightMenu() {
        Element rightMenu = new Element(screen, "tileWidgetRightMenu", new Vector2f(95, 10),
                new Vector2f(85, 120), Vector4f.ZERO, "Textures/Icons/EditorMap/rightMenu.png");

        animatedButton.add(new AnimatedButton(screen, "rouageRightMenuTop",
                new Vector2f(14, 7), new Vector2f(62, 62), "Textures/Icons/EditorMap/rouageRight.png", 1.5f, true) {
            @Override
            public void MouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                setElementalMenuIcon();
            }
        });
        animatedButton.add(new AnimatedButton(screen, "rouageRightMenuBottom",
                new Vector2f(7, 65), new Vector2f(48, 48), "Textures/Icons/EditorMap/rouageRight.png", 1.5f));

        for (AnimatedButton b : animatedButton) {
            rightMenu.addChild(b);
        }
        loadEAttributIcon(rightMenu);
        loadAssetIcon(rightMenu);
        screenElement.addChild(rightMenu);
    }

    private void loadEAttributIcon(Element menu) {
        eAttributIco = new Element(screen, "tileWidgetEAttributIco", new Vector2f(20, 10),
                new Vector2f(50, 50), Vector4f.ZERO, "Textures/Icons/EAttributs/"+ system.getTileEAttribut(selectedTilePosition).name().toLowerCase() +".png");
//        eAttributIco.scale(0.20f);
        eAttributIco.setIgnoreMouse(true);
        menu.addChild(eAttributIco);
    }
        
    private void loadAssetIcon(Element menu) {
        Element assetIco = new Element(screen, "tileWidgetAssetIco", new Vector2f(16, -45),
                new Vector2f(150, 150), Vector4f.ZERO, "Textures/Icons/EditorMap/closeChest.png");
        assetIco.scale(0.20f);
        assetIco.setIgnoreMouse(true);
        menu.addChild(assetIco);
    }

    private void setElementalMenuIcon() {
        if (eWin == null) {
            eWin = new ElementalWidgetMenu(screen, camera, system, this, new Vector2f(100, 45), new Vector2f(150, 50), "Textures/Icons/EditorMap/emptyMenu.png");
            eWin.show(inspectedSpatialPosition, system.getTileEAttribut(selectedTilePosition));
        } else if (eWin.isVisible()) {
            eWin.hide();
        } else {
            eWin.show(inspectedSpatialPosition, system.getTileEAttribut(selectedTilePosition));
        }
        screen.updateZOrder(screenElement);
    }

    @Override
    public void show(HexCoordinate tilePos) {
        this.selectedTilePosition = tilePos;
        super.show(selectedTilePosition);
        updateIcon();
        if(eWin != null && eWin.isVisible()){
            eWin.hide();
        }
    }
    
    @Override
    public void hide() {
        super.hide();
        if(eWin != null){
            eWin.hide();
        }
    }
    
    void updateIcon(){
        eAttributIco.setColorMap("Textures/Icons/EAttributs/"+ system.getTileEAttribut(selectedTilePosition).name().toLowerCase() +".png");
    }
    
    @Override
    public void update(float tpf) {
        super.update(tpf);
        if (eWin != null && screen.getElementById(eWin.getUID()) != null && eWin.isVisible()) {
            eWin.update(tpf);
        }
        if (!animatedButton.isEmpty()) {
            for (AnimatedButton b : animatedButton) {
                b.update(tpf);
            }
        }
    }
    
    HexCoordinate getSelectedTilePosition(){
        return selectedTilePosition;
    }
    
    
    
//    private void genPropertiesWindow() {
//        tileWin = new Window(screen, "tilePropertiesWin",
//                new Vector2f(0, -getChildElementById("CloseRootButtonWin").getPosition().y
//                + 25),
//                new Vector2f(155f, 40 + (40 * (ElementalAttribut.getSize()))));
//        tileWin.setWindowTitle("    Tile Properties");
//        tileWin.setIsResizable(false);
//        tileWin.setMinDimensions(Vector2f.ZERO);
////        tileWin.getDragBar().setIsMovable(false);
//
//        tilePButtonGroup = new RadioButtonGroup(screen, "tilePButtonGroup") {
//            @Override
//            public void onSelect(int index, Button value) {
//                if (index < ElementalAttribut.getSize()) {
//                    ((MapEditorSystem) system).updateTileProperties(ElementalAttribut.convert((byte) index));
//                }
//            }
//        };
//        /**
//         * Button used to change the element of a tile.
//         */
//        for (int i = 0; i < ElementalAttribut.getSize(); i++) {
//            ButtonAdapter button = new ButtonAdapter(screen, "tilePropertiesWinButton+" + ElementalAttribut.convert((byte) i),
//                    new Vector2f(10, 40 + (40 * i)));
//            button.setText(ElementalAttribut.convert((byte) i).toString());
//            tilePButtonGroup.addButton(button);
//        }
////        Button closeButton = new ButtonAdapter(screen, "tilePropertiesWinClose", new Vector2f(10, 40 + (40 * ElementalAttribut.getSize())));
////        closeButton.setText("CLOSE");
////        tilePButtonGroup.addButton(closeButton);
//
//        /**
//         * Button used to move up a selected tile.
//         */
//        Button upButton = new ButtonAdapter(screen, "UP", new Vector2f(120, 40), new Vector2f(25, 50)) {
//            @Override
//            public void onButtonMouseLeftDown(MouseButtonEvent evt, boolean toggled) {
//                super.onButtonMouseLeftDown(evt, toggled); //To change body of generated methods, choose Tools | Templates.
//                ((MapEditorSystem) system).updateTileProperties(1);
//            }
//        };
////        upButton.setText("UP");
//        tileWin.addChild(upButton);
//
//        /**
//         * Button used to move down a selected tile.
//         */
//        Button downButton = new ButtonAdapter(screen, "Down",
//                new Vector2f(120, (20 + (40 * (ElementalAttribut.getSize() - 1)))), new Vector2f(25, 50)) {
//            @Override
//            public void onButtonMouseLeftDown(MouseButtonEvent evt, boolean toggled) {
//                super.onButtonMouseLeftDown(evt, toggled); //To change body of generated methods, choose Tools | Templates.
//                ((MapEditorSystem) system).updateTileProperties(-1);
//            }
//        };
////        downButton.setText("DOWN");
//        tileWin.addChild(downButton);
//
//        tilePButtonGroup.setDisplayElement(tileWin);
//        addChild(tileWin);
//    }

    @Override
    public void removeFromScreen() {
        super.removeFromScreen();
        if(eWin != null){
            eWin.removeFromScreen();
        }
    }
}

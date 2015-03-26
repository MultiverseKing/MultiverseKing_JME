package gui.deprecated;

import gui.deprecated.WidgetSubMenu;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import com.jme3.renderer.Camera;
import core.HexMapSystem;
import gui.deprecated.control.AnimatedButton;
import gui.deprecated.control.CameraTrackWindow;
import java.util.ArrayList;
import org.hexgridapi.core.HexTile;
import org.hexgridapi.utility.HexCoordinate;
import tonegod.gui.controls.buttons.ButtonAdapter;
import tonegod.gui.core.Element;
import tonegod.gui.core.Screen;

/**
 * Widget menu allowing modification on one tiles.
 *
 * @author roah
 */
public class TileWidgetMenu extends CameraTrackWindow {

    private ArrayList<AnimatedButton> animatedButton = new ArrayList<>();
    private ArrayList<WidgetSubMenu> widgetMenu = new ArrayList<>(2);
    private WidgetSubMenu textureSelectionMenu = null;
    private HexMapSystem system;
    private Element textureIcons;

    public TileWidgetMenu(Screen screen, Camera camera, HexMapSystem system, HexCoordinate tilePos) {
        super(screen, camera);
        super.screenElement = new Element(screen, "TileWidgetMenu", new Vector2f(),
                new Vector2f(150, 150), Vector4f.ZERO, "Textures/Icons/Widget/rouage.png");
        this.system = system;
        super.inspectedPosition = tilePos;

        populateMenu();
        screenElement.scale(0.7f);
    }

    private void populateMenu() {
        /**
         * Button used to open tile properties.
         *
         * @todo : add to multiverse Editor.
         */
//        Element rightBtnBackground = new Element(screen, "tileWidgetRightBtnBackground", new Vector2f(145, 60),
//                new Vector2f(62, 62), Vector4f.ZERO, "Textures/Icons/MapWidget/EmptyCircle.png");
//        AnimatedButton rightBtn = new AnimatedButton(screen, "rouageRightAnimBtn",
//                new Vector2f((rightBtnBackground.getDimensions().x - 48) / 2, (rightBtnBackground.getDimensions().y - 48) / 2),
//                new Vector2f(48, 48), "Textures/Icons/MapWidget/rouageRight.png", 1.5f) {
//            @Override
//            public void MouseLeftUp(MouseButtonEvent evt, boolean toggled) {
//                system.openEventMenu(inspectedSpatialPosition);
//            }
//        };
//        rightBtnBackground.addChild(rightBtn);
//        animatedButton.add(rightBtn);
//        loadPropertiesIcon(rightBtnBackground);
//        screenElement.addChild(rightBtnBackground);
        /**
         * Button used to load asset on the current tiles.
         *
         * @todo ; add in multiverse Editor.
         */
//        Element botBtnBackground = new Element(screen, "tileWidgeBotBtnBackground", new Vector2f(100, 70),
//                new Vector2f(62, 62), Vector4f.ZERO, "Textures/Icons/MapWidget/EmptyCircle.png");
//        AnimatedButton botBtn = new AnimatedButton(screen, "rouageBotAnimBtn",
//                new Vector2f((botBtnBackground.getDimensions().x - 48) / 2, (botBtnBackground.getDimensions().y - 48) / 2),
//                new Vector2f(48, 48), "Textures/Icons/MapWidget/rouageRight.png", 1.5f) {
//            @Override
//            public void MouseLeftUp(MouseButtonEvent evt, boolean toggled) {
//                system.openAssetMenu(inspectedSpatialPosition);
//            }
//        };
//        botBtnBackground.addChild(botBtn);
//        animatedButton.add(botBtn);
//        loadAssetIcon(botBtnBackground);
//        screenElement.addChild(botBtnBackground);
        /**
         * Button used change the texture on the current tile.
         */
        Element textureBtnBackground = new Element(screen, screenElement.getUID() + ":TextureBtnBackground", new Vector2f(105, 20),
                new Vector2f(80, 80), Vector4f.ZERO, "Textures/Icons/Widget/EmptyCircle.png");
        AnimatedButton textureBtnAnim = new AnimatedButton(screen, screenElement.getUID() + ":TextureBtnAnim",
                new Vector2f((textureBtnBackground.getDimensions().x - 62) / 2, (textureBtnBackground.getDimensions().y - 62) / 2),
                new Vector2f(62, 62), "Textures/Icons/Widget/rouageRight.png", 1.5f, true) {
            @Override
            public void MouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                openTextureSubMenu();
            }
        };
        textureBtnBackground.setIgnoreMouse(true);
        textureBtnBackground.addChild(textureBtnAnim);
        animatedButton.add(textureBtnAnim);
        loadTextureIcon(textureBtnBackground);
        screenElement.addChild(textureBtnBackground);

        /**
         * Button used to move up or down the selected tile.
         */
        Element holder = new Element(screen, screenElement.getUID() + ":btnHolder", new Vector2f(0, 20), offset, Vector4f.ZERO, null);
        holder.setAsContainerOnly();

        ButtonAdapter upBtn = new ButtonAdapter(screen, screenElement.getUID() + ":UpButton", new Vector2f(),
                new Vector2f(45, 45), Vector4f.ZERO, "Textures/Icons/Widget/arrow.png") {
            @Override
            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                system.setTilePropertiesUp();
            }
        };
        holder.addChild(upBtn);

        ButtonAdapter downBtn = new ButtonAdapter(screen, screenElement.getUID() + ":DownButton", new Vector2f(45, -5),
                new Vector2f(45, 45), Vector4f.ZERO, "Textures/Icons/Widget/arrow.png") {
            @Override
            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                system.setTilePropertiesDown();
            }
        };
        downBtn.setLocalRotation(downBtn.getLocalRotation().fromAngles(0, 0, 180 * FastMath.DEG_TO_RAD));
        holder.addChild(downBtn);
        screenElement.addChild(holder);
    }

    private void loadTextureIcon(Element parent) {
        String key = system.getTileTextureKey();
        String icoPath = "Textures/Icons/TileTextures/" + key + ".png";

        textureIcons = new Element(screen, screenElement.getUID() + ":TexturesIco", new Vector2f(15, 15),
                new Vector2f(50, 50), Vector4f.ZERO, icoPath);
        textureIcons.setIgnoreMouse(true);
        parent.addChild(textureIcons);
    }

    private void loadPropertiesIcon(Element parent) {
        Element properties = new Element(screen, screenElement.getUID() + ":PropertiesIco", new Vector2f(10, 5),
                new Vector2f(45, 45), Vector4f.ZERO, "Textures/Icons/Widget/configKey.png");
        properties.setIgnoreMouse(true);
        parent.addChild(properties);
    }

    private void loadAssetIcon(Element parent) {
        Element assetIco = new Element(screen, screenElement.getUID() + ":AssetIco",
                new Vector2f((parent.getDimensions().x - 32) / 2, (parent.getDimensions().y - 32) / 2),
                new Vector2f(32, 32), Vector4f.ZERO, "Textures/Icons/Widget/closeChest.png");
        assetIco.setIgnoreMouse(true);
        parent.addChild(assetIco);
    }

    private void openTextureSubMenu() {
        if (textureSelectionMenu == null) {
            textureSelectionMenu = new WidgetSubMenu(screen, camera, system, this, new Vector2f(100, 35), new Vector2f(150, 50), "Textures/Icons/Widget/emptyMenu.png");
            if (inspectedSpatialHeight != Integer.MIN_VALUE) {
                textureSelectionMenu.show(inspectedPosition, system.getTileTextureKey(), inspectedSpatialHeight);
            } else {
                textureSelectionMenu.show(inspectedPosition, system.getTileTextureKey());
            }
        } else if (textureSelectionMenu.isVisible()) {
            textureSelectionMenu.hide();
        } else {
            if (inspectedSpatialHeight != Integer.MIN_VALUE) {
                textureSelectionMenu.show(inspectedPosition, system.getTileTextureKey(), inspectedSpatialHeight);
            } else {
                textureSelectionMenu.show(inspectedPosition, system.getTileTextureKey());
            }
        }
        screen.updateZOrder(screenElement);
    }

    @Override
    public void show(HexCoordinate pos, int height) {
        super.show(pos, height);
//        updateGhostTileBtn(false);
    }

    @Override
    public void show(HexCoordinate tilePos) {
        super.show(tilePos);
        updateIcon();
        updateGhostTileBtn(true);
        if (textureSelectionMenu != null && textureSelectionMenu.isVisible()) {
            textureSelectionMenu.hide();
        }
    }

    /**
     * @todo add btn to delete tile
     * @param isGhost 
     */
    private void updateGhostTileBtn(boolean isGhost) {
        Element btn = screenElement.getChildElementById(screenElement.getUID() + ":GhostBtnBackground");
        if (isGhost && btn == null) {
            btn = new Element(screen, screenElement.getUID() + ":GhostBtnBackground", new Vector2f(46, 24),
                    new Vector2f(80, 80), Vector4f.ZERO, "Textures/Icons/Widget/EmptyCircle.png");
            AnimatedButton textureBtnAnim = new AnimatedButton(screen, screenElement.getUID() + ":GhostBtnAnim",
                    new Vector2f((btn.getDimensions().x - 62) / 2, (btn.getDimensions().y - 62) / 2),
                    new Vector2f(62, 62), "Textures/Icons/Widget/rouageRight.png", 1.5f, true) {
                @Override
                public void MouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                    system.setNewTile();
                    screen.getElementById(screenElement.getUID() + ":GhostBtnBackground").hide();
                }
            };
            btn.setIgnoreMouse(true);
            btn.addChild(textureBtnAnim);
            animatedButton.add(textureBtnAnim);

            String icoPath = "Textures/Icons/Widget/insertTile.png";
            Element insertIco = new Element(screen, screenElement.getUID() + ":insertTileIco", new Vector2f(20, 15),
                    new Vector2f(40, 50), Vector4f.ZERO, icoPath);
            insertIco.setIgnoreMouse(true);
            btn.addChild(insertIco);

            screenElement.addChild(btn);
            btn.scale(0.75f);
        } else if (isGhost && btn != null){
            btn.show();
        } else if (!isGhost && btn != null){
            btn.hide();
        }
//        if (isGhost && !btn.getIsVisible()) {
//            btn.show();
//        } else if (isGhost) {
//            btn.hide();
//        }
    }

    @Override
    public void hide() {
        super.hide();
        if (textureSelectionMenu != null) {
            textureSelectionMenu.hide();
        }
    }

    public void updateIcon() {
        String key = system.getTileTextureKey();
        textureIcons.setColorMap("Textures/Icons/TileTextures/" + key + ".png");
        updateGhostTileBtn(system.getTileTextureValue() < 0 ? true : false);
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        if (textureSelectionMenu != null && textureSelectionMenu.isVisible()) {
            textureSelectionMenu.update(tpf);
        }
        if (!animatedButton.isEmpty()) {
            for (AnimatedButton b : animatedButton) {
                b.update(tpf);
            }
        }
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
        if (textureSelectionMenu != null) {
            textureSelectionMenu.removeFromScreen();
        }
    }
}

package editor.area;

import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import com.jme3.renderer.Camera;
import gui.AnimatedButton;
import gui.CameraTrackWindow;
import java.util.ArrayList;
import org.hexgridapi.utility.HexCoordinate;
import tonegod.gui.controls.buttons.ButtonAdapter;
import tonegod.gui.core.Element;
import tonegod.gui.core.Screen;
import org.multiversekingesapi.utility.ElementalAttribut;

/**
 * Widget menu allowing modification on one tiles.
 *
 * @author roah
 */
class AreaTileWidget extends CameraTrackWindow {

    private ArrayList<AnimatedButton> animatedButton = new ArrayList<>();
    private ElementalWidgetMenu eWin = null;
    private AreaEditorSystem system;
    private Element eAttributIco;

    AreaTileWidget(Screen screen, Camera camera, AreaEditorSystem system, HexCoordinate tilePos) {
        super(screen, camera);
        super.screenElement = new Element(screen, "tileWidgetMenu", new Vector2f(),
                new Vector2f(150, 150), Vector4f.ZERO, "Textures/Icons/MapWidget/rouage.png");
        this.system = system;
        super.inspectedSpatialPosition = tilePos;

        populateMenu();
        screenElement.scale(0.7f);
    }

    private void populateMenu() {
        /**
         * Button used to open tile properties.
         */
        Element rightBtnBackground = new Element(screen, "tileWidgetRightBtnBackground", new Vector2f(145, 60),
                new Vector2f(62, 62), Vector4f.ZERO, "Textures/Icons/MapWidget/EmptyCircle.png");
        AnimatedButton rightBtn = new AnimatedButton(screen, "rouageRightAnimBtn",
                new Vector2f((rightBtnBackground.getDimensions().x - 48) / 2, (rightBtnBackground.getDimensions().y - 48) / 2),
                new Vector2f(48, 48), "Textures/Icons/MapWidget/rouageRight.png", 1.5f) {
            @Override
            public void MouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                system.openEventMenu(inspectedSpatialPosition);
            }
        };
        rightBtnBackground.addChild(rightBtn);
        animatedButton.add(rightBtn);
        loadPropertiesIcon(rightBtnBackground);
        screenElement.addChild(rightBtnBackground);

        /**
         * Button used to load asset on the current tiles.
         */
        Element botBtnBackground = new Element(screen, "tileWidgeBotBtnBackground", new Vector2f(100, 70),
                new Vector2f(62, 62), Vector4f.ZERO, "Textures/Icons/MapWidget/EmptyCircle.png");
        AnimatedButton botBtn = new AnimatedButton(screen, "rouageBotAnimBtn",
                new Vector2f((botBtnBackground.getDimensions().x - 48) / 2, (botBtnBackground.getDimensions().y - 48) / 2),
                new Vector2f(48, 48), "Textures/Icons/MapWidget/rouageRight.png", 1.5f) {
            @Override
            public void MouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                system.openAssetMenu(inspectedSpatialPosition);
            }
        };
        botBtnBackground.addChild(botBtn);
        animatedButton.add(botBtn);
        loadAssetIcon(botBtnBackground);
        screenElement.addChild(botBtnBackground);

        /**
         * Button used to try another element on the current tile.
         */
        Element topBtnBackground = new Element(screen, "tileWidgeTopBtnBackground", new Vector2f(105, 10),
                new Vector2f(80, 80), Vector4f.ZERO, "Textures/Icons/MapWidget/EmptyCircle.png");
        AnimatedButton topBtn = new AnimatedButton(screen, "rouageTopAnimBtn",
                new Vector2f((topBtnBackground.getDimensions().x - 62) / 2, (topBtnBackground.getDimensions().y - 62) / 2),
                new Vector2f(62, 62), "Textures/Icons/MapWidget/rouageRight.png", 1.5f, true) {
            @Override
            public void MouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                setElementalMenuIcon();
            }
        };
        topBtnBackground.addChild(topBtn);
        animatedButton.add(topBtn);
        loadEAttributIcon(topBtnBackground);
        screenElement.addChild(topBtnBackground);

        /**
         * Button used to move up or down the selected tile.
         */
        Element holder = new Element(screen, screenElement.getUID() + "btnHolder", new Vector2f(0, 10), offset, Vector4f.ZERO, null);
        holder.setAsContainerOnly();

        ButtonAdapter upBtn = new ButtonAdapter(screen, screenElement.getUID() + "UpButton", new Vector2f(),
                new Vector2f(45, 45), Vector4f.ZERO, "Textures/Icons/MapWidget/arrow.png") {
            @Override
            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                system.setTileProperties(inspectedSpatialPosition, (byte) 1);
            }
        };
        holder.addChild(upBtn);

        ButtonAdapter downBtn = new ButtonAdapter(screen, screenElement.getUID() + "DownButton", new Vector2f(45, -5),
                new Vector2f(45, 45), Vector4f.ZERO, "Textures/Icons/MapWidget/arrow.png") {
            @Override
            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                system.setTileProperties(inspectedSpatialPosition, (byte) -1);
            }
        };
        downBtn.setLocalRotation(downBtn.getLocalRotation().fromAngles(0, 0, 180 * FastMath.DEG_TO_RAD));
        holder.addChild(downBtn);
        screenElement.addChild(holder);
    }

    private void loadEAttributIcon(Element parent) {
        ElementalAttribut eAttribut = system.getTileEAttribut(inspectedSpatialPosition);
        String icoPath = "Textures/Icons/EAttributs/" + (eAttribut == null ? "EMPTY_TEXTURE_KEY" : eAttribut.name()) + ".png";

        eAttributIco = new Element(screen, "tileWidgetEAttributIco", new Vector2f(15, 15),
                new Vector2f(50, 50), Vector4f.ZERO, icoPath);
        eAttributIco.setIgnoreMouse(true);
        parent.addChild(eAttributIco);
    }

    private void loadPropertiesIcon(Element parent) {
        Element properties = new Element(screen, "tileWidgetPropertiesIco", new Vector2f(10, 5),
                new Vector2f(45, 45), Vector4f.ZERO, "Textures/Icons/MapWidget/configKey.png");
        properties.setIgnoreMouse(true);
        parent.addChild(properties);
    }

    private void loadAssetIcon(Element parent) {
        Element assetIco = new Element(screen, "tileWidgetAssetIco",
                new Vector2f((parent.getDimensions().x - 32) / 2, (parent.getDimensions().y - 32) / 2),
                new Vector2f(32, 32), Vector4f.ZERO, "Textures/Icons/MapWidget/closeChest.png");
        assetIco.setIgnoreMouse(true);
        parent.addChild(assetIco);
    }

    private void setElementalMenuIcon() {
        if (eWin == null) {
            eWin = new ElementalWidgetMenu(screen, camera, system, this, new Vector2f(100, 45), new Vector2f(150, 50), "Textures/Icons/MapWidget/emptyMenu.png");
            if (inspectedSpatialHeight != Integer.MIN_VALUE) {
                eWin.show(inspectedSpatialPosition, system.getTileEAttribut(inspectedSpatialPosition), inspectedSpatialHeight);
            } else {
                eWin.show(inspectedSpatialPosition, system.getTileEAttribut(inspectedSpatialPosition));
            }
        } else if (eWin.isVisible()) {
            eWin.hide();
        } else {
            if (inspectedSpatialHeight != Integer.MIN_VALUE) {
                eWin.show(inspectedSpatialPosition, system.getTileEAttribut(inspectedSpatialPosition), inspectedSpatialHeight);
            } else {
                eWin.show(inspectedSpatialPosition, system.getTileEAttribut(inspectedSpatialPosition));
            }
        }
        screen.updateZOrder(screenElement);
    }

    @Override
    public void show(HexCoordinate tilePos) {
        super.show(tilePos);
        updateIcon();
        if (eWin != null && eWin.isVisible()) {
            eWin.hide();
        }
    }

    @Override
    public void hide() {
        super.hide();
        if (eWin != null) {
            eWin.hide();
        }
    }

    void updateIcon() {
        ElementalAttribut eAttribut = system.getTileEAttribut(inspectedSpatialPosition);
        eAttributIco.setColorMap("Textures/Icons/EAttributs/" + (eAttribut == null ? "EMPTY_TEXTURE_KEY" : eAttribut.name()) + ".png");
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        if (eWin != null && eWin.isVisible()) {
            eWin.update(tpf);
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
        if (eWin != null) {
            eWin.removeFromScreen();
        }
    }
}

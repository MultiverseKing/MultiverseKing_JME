package gamemode.editor.map;

import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import com.jme3.renderer.Camera;
import gamemode.gui.CameraTrackWindow;
import java.io.File;
import tonegod.gui.controls.buttons.ButtonAdapter;
import tonegod.gui.core.Element;
import tonegod.gui.core.ElementManager;
import utility.ElementalAttribut;
import utility.HexCoordinate;

/**
 * Menu showing all invalable element to change the hex to.
 *
 * @author roah
 */
class ElementalWidgetMenu extends CameraTrackWindow {

    private final MapEditorSystem system;
    private final CameraTrackWindow elementParent;
    private Element eIconContainer = null;
    private ElementalAttribut ignoredEAttribut;

    ElementalWidgetMenu(ElementManager screen, Camera camera, MapEditorSystem system, CameraTrackWindow parent, Vector2f position, Vector2f dimensions, String defaultImg) {
        super(screen, camera, position);
        this.system = system;
        this.elementParent = parent;
        screenElement = new Element(screen, "ElementalWidgetMenu", position, dimensions, Vector4f.ZERO, defaultImg);
        screenElement.setIgnoreMouse(true);
    }

    private void buildElementalIcon() {
        if (eIconContainer == null) {
            eIconContainer = new Element(screen, "eAttributIconContainer", Vector2f.ZERO, Vector2f.ZERO, Vector4f.ZERO, null);
            eIconContainer.setAsContainerOnly();
            screenElement.addChild(eIconContainer);
        }
        byte j = 0;
        for (int i = 0; i < ElementalAttribut.values().length; i++) {
            ElementalAttribut e = ElementalAttribut.convert(i);
            if (e.equals(ignoredEAttribut) || e.equals(ElementalAttribut.NULL)) {
                j++;
            } else {
                String eName = e.name().toLowerCase();
                File f = new File(System.getProperty("user.dir") + "/assets/Textures/HexField/" + eName.toUpperCase() + ".png");
                if (!f.exists()) {
                    j++;
                } else {
                    ButtonAdapter ico = new ButtonAdapter(screen, eName + "Icon", new Vector2f(28 * (i - j) + 23, 10),
                            new Vector2f(28, 28), Vector4f.ZERO, "Textures/Icons/EAttributs/" + eName + ".png") {
                        @Override
                        public void onButtonMouseLeftDown(MouseButtonEvent evt, boolean toggled) {
                            super.onButtonMouseLeftDown(evt, toggled);
                            screen.updateZOrder(elementParent.getScreenElement());
                        }

                        @Override
                        public void onButtonMouseRightDown(MouseButtonEvent evt, boolean toggled) {
                            super.onButtonMouseRightDown(evt, toggled);
                            screen.updateZOrder(elementParent.getScreenElement());
                        }

                        @Override
                        public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                            super.onButtonMouseLeftUp(evt, toggled);
                            buttonTrigger(this.getUID());
                            screen.updateZOrder(elementParent.getScreenElement());
                        }

                        @Override
                        public void onButtonMouseRightUp(MouseButtonEvent evt, boolean toggled) {
                            super.onButtonMouseRightUp(evt, toggled);
                            screen.updateZOrder(elementParent.getScreenElement());
                        }
                    };
                    eIconContainer.addChild(ico);
                }
            }
        }
    }

    private void updateElementalIcon(ElementalAttribut currentEAttribut) {
        Element ico = eIconContainer.getChildElementById(currentEAttribut.name().toLowerCase() + "Icon");
        eIconContainer.removeChild(ico);
        ico.setColorMap("Textures/Icons/EAttributs/" + ignoredEAttribut.name().toLowerCase() + ".png");
        ico.setUID(ignoredEAttribut.name().toLowerCase() + "Icon");
        eIconContainer.addChild(ico);
        ignoredEAttribut = currentEAttribut;
    }

    void show(HexCoordinate hexPosition, ElementalAttribut currentEAttribut) {
        if (eIconContainer == null) {
            ignoredEAttribut = currentEAttribut;
            buildElementalIcon();
        } else if (ignoredEAttribut != currentEAttribut) {
            updateElementalIcon(currentEAttribut);
        }
        show(hexPosition);
    }

    private void destroyElementalIcon() {
        for (Element b : eIconContainer.getElementsAsMap().values()) {
            screen.removeElement(b);
        }
    }

    private void buttonTrigger(String button) {
        String element = button.split("Icon")[0];
        system.setTileProperties(((RoomTileWidget) elementParent).getSelectedTilePosition(),
                ElementalAttribut.valueOf(element.toUpperCase()));
        updateElementalIcon(ElementalAttribut.valueOf(element.toUpperCase()));
        ((RoomTileWidget) elementParent).updateIcon();
        hide();
    }
}

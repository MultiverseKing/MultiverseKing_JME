package editor.area;

import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetKey;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import com.jme3.renderer.Camera;
import com.jme3.texture.Image;
import gui.CameraTrackWindow;
import java.io.File;
import org.hexgridapi.utility.ElementalAttribut;
import org.hexgridapi.utility.HexCoordinate;
import tonegod.gui.controls.buttons.ButtonAdapter;
import tonegod.gui.core.Element;
import tonegod.gui.core.ElementManager;

/**
 * Menu showing all invalable element to change the hex to.
 *
 * @author roah
 */
class ElementalWidgetMenu extends CameraTrackWindow {

    private final AreaEditorSystem system;
    private final CameraTrackWindow elementParent;
    private Element eIconContainer = null;
    private String ignoredEAttribut;

    ElementalWidgetMenu(ElementManager screen, Camera camera, AreaEditorSystem system, CameraTrackWindow parent, Vector2f position, Vector2f dimensions, String defaultImg) {
        super(screen, camera, position);
        this.system = system;
        this.elementParent = parent;
        screenElement = new Element(screen, "ElementalWidgetMenu", position, dimensions, Vector4f.ZERO, defaultImg);
        screenElement.setIgnoreMouse(true);
    }

    private void buildElementalIcon() {
        if (eIconContainer == null) {
            eIconContainer = new Element(screen, "eAttributIconContainer", new Vector2f(), new Vector2f(), Vector4f.ZERO, null);
            eIconContainer.setAsContainerOnly();
            screenElement.addChild(eIconContainer);
        }
        byte j = 0;
        int i = 0;
        while (i < ElementalAttribut.values().length + 1) {
            ElementalAttribut e = ElementalAttribut.convert(i);
            if (e == null && ignoredEAttribut.equals("null") || e != null && e.toString().equals(ignoredEAttribut)) {
                j++;
            } else {
                String eName = e == null ? "null" : e.name();
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
            i++;
        }
        screenElement.setDimensions(30 * (i - j) + 30, screenElement.getDimensions().y);
    }

    private void updateElementalIcon(String currentEAttribut) {
        Element ico = eIconContainer.getChildElementById(currentEAttribut + "Icon");
        eIconContainer.removeChild(ico);
        ico.setColorMap("Textures/Icons/EAttributs/" + ignoredEAttribut + ".png");
        ico.setUID(ignoredEAttribut + "Icon");
        eIconContainer.addChild(ico);
        ignoredEAttribut = currentEAttribut;
    }

    void show(HexCoordinate hexPosition, ElementalAttribut currentEAttribut, int height) {
        inspectedSpatialHeight = height;
        show(hexPosition, currentEAttribut, true);
    }

    void show(HexCoordinate hexPosition, ElementalAttribut currentEAttribut) {
        show(hexPosition, currentEAttribut, false);
    }

    void show(HexCoordinate hexPosition, ElementalAttribut currentEAttribut, boolean hasHeight) {
        if (!hasHeight) {
            inspectedSpatialHeight = Integer.MIN_VALUE;
        }
        if (eIconContainer == null) {
            ignoredEAttribut = currentEAttribut == null ? "null" : currentEAttribut.toString();
            buildElementalIcon();
        } else if (currentEAttribut != null && !ignoredEAttribut.equals(currentEAttribut.toString())) {
            updateElementalIcon(currentEAttribut.toString());
        } else if (currentEAttribut == null && !ignoredEAttribut.equals("null")) {
            updateElementalIcon("null");
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
        system.setTileProperties(((AreaTileWidget) elementParent).getInspectedSpatialPosition(), element);
        updateElementalIcon(element);
        ((AreaTileWidget) elementParent).updateIcon();
        hide();
    }

    void setInspectedSpatialHeight(int height) {
        inspectedSpatialHeight = height;
    }
}

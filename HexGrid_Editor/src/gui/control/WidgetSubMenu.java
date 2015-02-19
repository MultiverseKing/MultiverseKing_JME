package gui.control;

import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import com.jme3.renderer.Camera;
import core.EditorMainSystem;
import gui.CameraTrackWindow;
import org.hexgridapi.utility.HexCoordinate;
import tonegod.gui.controls.buttons.ButtonAdapter;
import tonegod.gui.core.Element;
import tonegod.gui.core.ElementManager;

/**
 * Menu showing all invalable element to change the hex to.
 *
 * @author roah
 */
class WidgetSubMenu extends CameraTrackWindow {

    private final EditorMainSystem system;
    private final CameraTrackWindow elementParent;
    private Element iconContainer = null;
    private String ignoredIcon;

    WidgetSubMenu(ElementManager screen, Camera camera, EditorMainSystem system, CameraTrackWindow parent, Vector2f position, Vector2f dimensions, String defaultImg) {
        super(screen, camera, position);
        this.system = system;
        this.elementParent = parent;
        screenElement = new Element(screen, "WidgetSubMenu", position, dimensions, Vector4f.ZERO, defaultImg);
        screenElement.setIgnoreMouse(true);
    }

    private void buildTextureIcon() {
        if (iconContainer == null) {
            iconContainer = new Element(screen, screenElement.getUID() + ":IconContainer", new Vector2f(), new Vector2f(), Vector4f.ZERO, null);
            iconContainer.setAsContainerOnly();
            screenElement.addChild(iconContainer);
        }
        byte j = 0;
        byte i = 0;
        String[] textureKeys = system.getTextureKeys().toArray(new String[system.getTextureKeys().size()]);
        while (i < textureKeys.length + 1) {
            String key;
            if (i < textureKeys.length) {
                key = textureKeys[i];
            } else {
                key = "EMPTY_TEXTURE_KEY";
            }
            if (ignoredIcon.equals(key)) {
                j++;
            } else {
                ButtonAdapter ico = new ButtonAdapter(screen, screenElement.getUID() + ":" + key + "Icon", new Vector2f(28 * (i - j) + 23, 10),
                        new Vector2f(28, 28), Vector4f.ZERO, "Textures/Icons/TileTextures/" + key + ".png") {
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
                iconContainer.addChild(ico);
            }
            i++;
        }
        screenElement.setDimensions(30 * (i - j) + 30, screenElement.getDimensions().y);
    }

    private void updateTextureIcon(String textureKey) {
        Element icon = iconContainer.getChildElementById(screenElement.getUID() + ":" + textureKey + "Icon");
        iconContainer.removeChild(icon);
        icon.setColorMap("Textures/Icons/TileTextures/" + ignoredIcon + ".png");
        icon.setUID(screenElement.getUID() + ":" + ignoredIcon + "Icon");
        iconContainer.addChild(icon);
        ignoredIcon = textureKey;
    }

    void show(HexCoordinate hexPosition, String textureKey, int height) {
        inspectedSpatialHeight = height;
        show(hexPosition, textureKey, true);
    }

    void show(HexCoordinate hexPosition, String textureKey) {
        show(hexPosition, textureKey, false);
    }

    void show(HexCoordinate hexPosition, String textureKey, boolean hasHeight) {
        if (!hasHeight) {
            inspectedSpatialHeight = Integer.MIN_VALUE;
        }
        if (iconContainer == null) {
            ignoredIcon = textureKey;
            buildTextureIcon();
        } else if (!ignoredIcon.equals(textureKey)) {
            updateTextureIcon(textureKey);
        }
        show(hexPosition);
    }

    private void destroyElementalIcon() {
        for (Element b : iconContainer.getElementsAsMap().values()) {
            screen.removeElement(b);
        }
    }

    private void buttonTrigger(String button) {
        String textureKey = button.split(":")[1].split("Icon")[0];
        system.setTileProperties(((TileWidgetMenu) elementParent).getInspectedSpatialPosition(), textureKey);
        updateTextureIcon(textureKey);
//        ((TileWidgetMenu) elementParent).updateIcon();
        hide();
    }

    void setInspectedSpatialHeight(int height) {
        inspectedSpatialHeight = height;
    }
}

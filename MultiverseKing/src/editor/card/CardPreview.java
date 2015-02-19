package editor.card;

import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import entitysystem.attribut.Rarity;
import entitysystem.card.CardProperties;
import entitysystem.card.Hover;
import entitysystem.render.RenderComponent.RenderType;
import java.io.File;
import java.io.FilenameFilter;
import tonegod.gui.controls.buttons.ButtonAdapter;
import tonegod.gui.controls.menuing.Menu;
import tonegod.gui.core.Element;
import tonegod.gui.core.Screen;
import tonegod.gui.effects.Effect;
import utility.ElementalAttribut;

/**
 *
 * @author roah
 */
public class CardPreview {

    private final Hover hover;
    private final ButtonAdapter preview;
    private String visual;

    Element getPreview() {
        return preview;
    }

    CardPreview(Screen screen, Element parent, CardProperties cardProperties) {
        /**
         * Window used to show a preview of the card.
         */
        this.visual = cardProperties.getVisual();
        preview = new ButtonAdapter(screen, "geneneratorImgPreview", new Vector2f(parent.getDimensions().x, 0),
                new Vector2f(140, 200), new Vector4f(), "Textures/Cards/Artworks/" + cardProperties.getVisual() + ".png") {
            @Override
            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                super.onButtonMouseLeftUp(evt, toggled);
                if (!((Screen) screen).getElementsAsMap().containsKey("loadImgMenu")) {
                    loadImgMenu();
                }
                Menu loadImgMenu = (Menu) ((Screen) screen).getElementById("loadImgMenu");
                loadImgMenu.showMenu(null, getAbsoluteX() + getDimensions().x, getAbsoluteY());
            }

            private void loadImgMenu() {
                Menu imgMenu = new Menu(screen, "loadImgMenu", new Vector2f(), false) {
                    @Override
                    public void onMenuItemClicked(int index, Object value, boolean isToggled) {
                        preview.setColorMap("Textures/Cards/Artworks/" + value);
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
        hover = new Hover(screen, new Vector2f(), preview.getDimensions());
        hover.setProperties(cardProperties);
        preview.addChild(hover);

        parent.addChild(preview);
    }

    void switchEAttribut(ElementalAttribut eAttribut) {
        hover.setEAttribut(eAttribut);
    }

    void switchSubType(RenderType subType) {
        hover.setSubType(subType);
    }

    void switchCost(int cost) {
        hover.setCastCost(cost);
    }

    void switchName(String name) {
        hover.setCardName(name);
    }

    void switchImg(String visual) {
        this.visual = visual;
        preview.setColorMap("Textures/Cards/Artworks/" + visual + ".png");
    }

    public String getVisual() {
        return visual;
    }

    void switchRarity(Rarity rarity) {
        hover.setRarity(rarity);
    }
}

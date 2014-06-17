/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gamemode.editor.cardgui;

import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import entitysystem.attribut.Faction;
import entitysystem.attribut.Rarity;
import entitysystem.attribut.SubType;
import entitysystem.card.CardProperties;
import entitysystem.card.Hover;
import java.io.File;
import java.io.FilenameFilter;
import tonegod.gui.controls.buttons.ButtonAdapter;
import tonegod.gui.controls.menuing.Menu;
import tonegod.gui.effects.Effect;
import utility.ElementalAttribut;

/**
 *
 * @author roah
 */
public class CardPreview {

    private void genPreview() {
        /**
         * Window used to show a preview of the card.
         */
        ButtonAdapter preview = new ButtonAdapter(main.getScreen(), "geneneratorImgPreview", new Vector2f(genMenu.getWidth(), 0),
                new Vector2f(140, 200), new Vector4f(), "Textures/Cards/Artworks/undefined.png") {
            @Override
            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                super.onButtonMouseLeftUp(evt, toggled);
                if (!main.getScreen().getElementsAsMap().containsKey("loadImgMenu")) {
                    loadImgMenu();
                }
                Menu loadImgMenu = (Menu) main.getScreen().getElementById("loadImgMenu");
                loadImgMenu.showMenu(null, getAbsoluteX() + getDimensions().x, getAbsoluteY());
            }

            private void loadImgMenu() {
                Menu imgMenu = new Menu(screen, "loadImgMenu", new Vector2f(), false) {
                    @Override
                    public void onMenuItemClicked(int index, Object value, boolean isToggled) {
                        screen.getElementById("geneneratorImgPreview").setColorMap("Textures/Cards/Artworks/" + value);
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
        CardProperties cardProperties = new CardProperties("TuxDoll", 0, Faction.NEUTRAL, SubType.SUMMON,
                Rarity.COMMON, ElementalAttribut.NULL, "This is a Testing unit");
        hover.setProperties(cardProperties);
        preview.addChild(hover);
        genMenu.addChild(preview);
    }
}

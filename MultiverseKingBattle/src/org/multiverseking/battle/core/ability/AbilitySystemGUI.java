package org.multiverseking.battle.core.ability;

import com.jme3.math.Vector2f;
import com.simsilica.es.Entity;
import com.simsilica.es.EntitySet;
import org.multiverseking.ability.AbilityComponent;
import org.multiverseking.core.utility.EntitySystemAppState;
import org.multiverseking.battle.core.focus.MainFocusComponent;
import org.multiverseking.field.component.SpeedComponent;
import tonegod.gui.controls.buttons.ButtonAdapter;
import tonegod.gui.core.Element;
import tonegod.gui.core.Screen;

/**
 *
 * @author roah
 */
public class AbilitySystemGUI extends EntitySystemAppState {

    private Screen screen;
    private Element skill;
    private Element passif;

    public AbilitySystemGUI(Screen screen) {
        this.screen = screen;
    }

    @Override
    protected EntitySet initialiseSystem() {
        genCharacterMenu();
        return entityData.getEntities(MainFocusComponent.class, SpeedComponent.class, AbilityComponent.class);
    }

    @Override
    protected void updateSystem(float tpf) {
    }

    @Override
    protected void addEntity(Entity e) {
    }

    @Override
    protected void updateEntity(Entity e) {
    }

    @Override
    protected void removeEntity(Entity e) {
    }

    @Override
    protected void cleanupSystem() {
    }

    private void genCharacterMenu() {
        Vector2f size = new Vector2f(245, 60);
        Vector2f pos = new Vector2f(screen.getWidth() / 2 - size.x / 2, screen.getHeight() - size.y);
        skill = new Element(screen, "skillBar", pos, size,
                screen.getStyle("Window").getVector4f("resizeBorders"),
                screen.getStyle("Window").getString("defaultImg"));
        skill.removeAllChildren();
        size = new Vector2f(50, 50);
        pos = new Vector2f(0, 5);
        for (int i = 0; i < 4; i++) {
            pos.x = (size.x * i) + (5 * (i + 1)) + 10;
            ButtonAdapter btn = new ButtonAdapter(screen, "skill_" + i, pos, size);
            btn.setText(String.valueOf(i));
            skill.addChild(btn);
        }
//        screen.addElement(skill);

        ButtonAdapter btn = new ButtonAdapter(screen, "Passif", new Vector2f(5, 5), size);
        size = new Vector2f(60, 60);
        pos = new Vector2f(-size.x, 0);
        passif = new Element(screen, "skillPassifBar", pos, size,
                screen.getStyle("Window").getVector4f("resizeBorders"),
                screen.getStyle("Window").getString("defaultImg"));
        passif.addChild(btn);
        skill.addChild(passif);
    }
}

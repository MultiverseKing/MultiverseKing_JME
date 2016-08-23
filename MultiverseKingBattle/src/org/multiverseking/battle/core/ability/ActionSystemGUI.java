package org.multiverseking.battle.core.ability;

import com.jme3.math.Vector2f;
import com.simsilica.es.Entity;
import com.simsilica.es.EntitySet;
import org.multiverseking.ability.ActionAbilityComponent;
import org.multiverseking.core.utility.EntitySystemAppState;
import org.multiverseking.battle.core.focus.MainFocusComponent;
import org.multiverseking.battle.gui.BattleGUI;
import org.multiverseking.core.utility.MultiverseCoreGUI;
import org.multiverseking.field.position.component.SpeedComponent;
import tonegod.gui.controls.buttons.ButtonAdapter;
import tonegod.gui.core.Element;
import tonegod.gui.core.Screen;

/**
 *
 * @author roah
 */
public class ActionSystemGUI extends EntitySystemAppState {

    private Screen screen;
    private Element skill;
    private Element passif;
    private final int atbCount = 6;
    private ActionGauge actionGauge;
    private BattleGUI mainHUD;

    @Override
    protected EntitySet initialiseSystem() {

        this.screen = ((MultiverseCoreGUI) app).getScreen();
        this.mainHUD = app.getStateManager().getState(BattleGUI.class);

        actionGauge = new ActionGauge(screen, mainHUD.getFilePath(), atbCount);
        
        mainHUD.getHolder().addChild(actionGauge.getGauge());
        mainHUD.getHolder().addChild(genSkillMenu());

        return entityData.getEntities(MainFocusComponent.class, SpeedComponent.class, ActionAbilityComponent.class);
    }

    private Element genSkillMenu() {
        Vector2f size = new Vector2f(245, 60);
        Vector2f pos = new Vector2f(120, 5);
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

        size = new Vector2f(30, 30);
        ButtonAdapter btn = new ButtonAdapter(screen, "Passif", new Vector2f(5, 5), size);
        size = new Vector2f(40, 40);
        pos = new Vector2f(-size.x, size.y /3);
        passif = new Element(screen, "skillPassifBar", pos, size,
                screen.getStyle("Window").getVector4f("resizeBorders"),
                screen.getStyle("Window").getString("defaultImg"));
        passif.addChild(btn);
        skill.addChild(passif);
        return skill;
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
}

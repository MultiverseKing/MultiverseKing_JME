package org.multiverseking.battle.battleSystem;

import com.simsilica.es.Entity;
import com.simsilica.es.EntitySet;
import org.multiverseking.battle.battleSystem.component.FocusComponent;
import org.multiverseking.utility.system.EntitySystemAppState;
import org.multiverseking.utility.system.MultiverseCoreGUI;
import tonegod.gui.core.Screen;

/**
 *
 * @author roah
 */
public class BattleGUIRender extends EntitySystemAppState {
    
    private CharacterHUD1 characterHUD;
    private Screen screen;

    @Override
    protected EntitySet initialiseSystem() {
        screen = ((MultiverseCoreGUI) app).getScreen();
        characterHUD = new CharacterHUD1(screen);
        screen.addElement(characterHUD.getHUDElement());
        return entityData.getEntities(FocusComponent.class);
    }

    @Override
    protected void updateSystem(float tpf) {
        characterHUD.update(tpf);
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

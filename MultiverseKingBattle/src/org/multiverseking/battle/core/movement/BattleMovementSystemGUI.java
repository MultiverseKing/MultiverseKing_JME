/*
 * Copyright (C) 2016 roah
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.multiverseking.battle.core.movement;

import com.jme3.math.Vector2f;
import com.simsilica.es.Entity;
import com.simsilica.es.EntitySet;
import org.multiverseking.battle.core.focus.MainFocusComponent;
import org.multiverseking.battle.gui.BattleGUI;
import org.multiverseking.core.utility.EntitySystemAppState;
import org.multiverseking.core.utility.MultiverseCoreGUI;
import tonegod.gui.core.Screen;

/**
 *
 * @author roah
 */
public class BattleMovementSystemGUI extends EntitySystemAppState {

    private Screen screen;
    private BattleGUI mainHUD;
    private StaminaGauge staminaJauge;

    @Override
    protected EntitySet initialiseSystem() {

        this.screen = ((MultiverseCoreGUI) app).getScreen();
        this.mainHUD = app.getStateManager().getState(BattleGUI.class);

        staminaJauge = new StaminaGauge(screen, mainHUD.getFilePath(), new Vector2f(-75, 90));
        mainHUD.getHolder().addChild(staminaJauge.getGauge());
        staminaJauge.show(false);

        return entityData.getEntities(MainFocusComponent.class, StaminaComponent.class);
    }

    @Override
    protected void updateSystem(float tpf) {
    }

    @Override
    protected void addEntity(Entity e) {
        staminaJauge.update(e.get(StaminaComponent.class).getValue());
    }

    @Override
    protected void updateEntity(Entity e) {
        staminaJauge.update(e.get(StaminaComponent.class).getValue());
    }

    @Override
    protected void removeEntity(Entity e) {
    }

    @Override
    protected void cleanupSystem() {
    }

}

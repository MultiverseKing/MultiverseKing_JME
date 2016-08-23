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

import com.jme3.input.controls.ActionListener;
import com.jme3.math.Ray;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import java.util.HashMap;
import org.hexgridapi.core.coordinate.HexCoordinate;
import org.hexgridapi.core.mousepicking.GridMouseControlAppState;
import org.hexgridapi.events.MouseInputEvent;
import org.hexgridapi.events.MouseInputEvent.MouseInputEventType;
import org.hexgridapi.events.MouseRayListener;
import org.multiverseking.battle.core.focus.MainSelectionSystem;
import org.multiverseking.battle.core.focus.MainFocusComponent;
import org.multiverseking.core.utility.EntitySystemAppState;
import org.multiverseking.field.position.component.HexPositionComponent;
import org.multiverseking.field.position.component.MoveToComponent;

/**
 *
 * @author roah
 */
public class BattleMovementSystem extends EntitySystemAppState {

    private final float speed = 100 / 1.0f;
    private MainSelectionSystem focusSystem;
    private HashMap<EntityId, HexCoordinate> list = new HashMap<>();
    private boolean selectingMovePosition = false;
    private GridMouseControlAppState mouseControl;

    @Override
    protected EntitySet initialiseSystem() {
        app.getStateManager().attach(new BattleMovementSystemGUI());
        focusSystem = app.getStateManager().getState(MainSelectionSystem.class);
        app.getStateManager().getState(GridMouseControlAppState.class).register(inputListener);
        registerCharacterKey();
        mouseControl = app.getStateManager().getState(GridMouseControlAppState.class);
        return entityData.getEntities(StaminaComponent.class);
    }

    @Override
    protected void updateSystem(float tpf) {
        if (!list.isEmpty()) {
            list.keySet().stream().forEach((id) -> {
                StaminaComponent stamina = entities.getEntity(id).get(StaminaComponent.class);
                float newValue = stamina.getValue() + tpf * speed;
                if (newValue > 100) {
                    entityData.setComponent(id, stamina.clone(100));
                } else if (newValue < 100) {
                    entityData.setComponent(id, stamina.clone(newValue));
                }
            });
        }
    }

    @Override
    protected void addEntity(Entity e) {
    }

    @Override
    protected void updateEntity(Entity e) {
        if (e.get(StaminaComponent.class).getValue() >= 100) {
            entityData.setComponent(e.getId(), new StaminaComponent(0));
            entityData.setComponent(e.getId(), new MoveToComponent(list.get(e.getId())));
            list.remove(e.getId());
        }
    }

    @Override
    protected void removeEntity(Entity e) {
    }

    @Override
    protected void cleanupSystem() {
        app.getInputManager().removeListener(keyListeners);
        app.getStateManager().getState(GridMouseControlAppState.class).unregister(inputListener);
    }

    private void registerCharacterKey() {
        app.getInputManager().addListener(keyListeners, new String[]{
            "move", MouseInputEvent.MouseInputEventType.RMB.toString(),
            MouseInputEvent.MouseInputEventType.LMB.toString()});
    }

    private MouseRayListener inputListener = new MouseRayListener() {
        @Override
        public MouseInputEvent MouseRayInputAction(MouseInputEventType mouseInputType, Ray ray) {
            return null;
        }

        @Override
        public void onMouseAction(MouseInputEvent event) {
            if (selectingMovePosition && event.getType().equals(MouseInputEventType.LMB)) {
//                mouseControl.setCursorPulseMode(inputListener);
//                app.getStateManager().getState(GridMouseControlAppState.class).setCursorPulseMode(inputListener);
//                HexCoordinate pos = app.getStateManager().getState(GridMouseControlAppState.class).getSelectionControl().getSelectedPos();
//                EntitySet entitySet = entityData.getEntities(MainFocusComponent.class, StaminaComponent.class);
//                entitySet.stream().forEach((e) -> {
//                    list.put(e.getId(), pos);
//                });
                selectingMovePosition = false;
            } else if (selectingMovePosition && event.getType().equals(MouseInputEventType.RMB)) {
                EntitySet entitySet = entityData.getEntities(MainFocusComponent.class, HexPositionComponent.class);
                mouseControl.getSelectionControl().setSelected(
                        entitySet.toArray(new Entity[0])[0]
                                .get(HexPositionComponent.class).getPosition());
                selectingMovePosition = false;
            }
        }
    };

    private final ActionListener keyListeners = (String name, boolean isPressed, float tpf) -> {
        if (!isPressed) {
            if (!selectingMovePosition && name.equals("move") ) {
                selectingMovePosition = app.getStateManager()
                        .getState(GridMouseControlAppState.class)
                        .setCursorPulseMode(inputListener);
            } else if (selectingMovePosition) {
                mouseControl.setCursorPulseMode(inputListener);
            }
        }
    };

}

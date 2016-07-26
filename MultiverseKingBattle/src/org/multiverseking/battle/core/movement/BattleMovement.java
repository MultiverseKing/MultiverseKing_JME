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
import com.simsilica.es.Entity;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import java.util.HashMap;
import org.hexgridapi.core.coordinate.HexCoordinate;
import org.hexgridapi.core.mousepicking.GridMouseControlAppState;
import org.hexgridapi.events.MouseInputEvent;
import org.hexgridapi.events.TileInputListener;
import org.multiverseking.battle.core.focus.MainSelectionSystem;
import org.multiverseking.battle.core.focus.MainFocusComponent;
import org.multiverseking.core.utility.EntitySystemAppState;
import org.multiverseking.field.position.MoveToComponent;
import org.multiverseking.render.RenderComponent;

/**
 *
 * @author roah
 */
public class BattleMovement extends EntitySystemAppState {

    private MainSelectionSystem focusSystem;
    private HashMap<EntityId, HexCoordinate> list = new HashMap<>();
    private boolean selectingMovePosition = false;

    @Override
    protected EntitySet initialiseSystem() {
        focusSystem = app.getStateManager().getState(MainSelectionSystem.class);
        app.getStateManager().getState(GridMouseControlAppState.class).register(tileInputListener);
        registerCharacterKey();
        return entityData.getEntities(StaminaComponent.class, RenderComponent.class);
    }

    private int speed = 30;

    @Override
    protected void updateSystem(float tpf) {
        if(!list.isEmpty()) {
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
//        AbstractRender.RenderType renderType = e.get(RenderComponent.class).getRenderType();
//        if (renderType.equals(AbstractRender.RenderType.Titan)) {
//            if (currentFocus != null) {
//                entityData.removeComponent(currentFocus, MainFocusComponent.class);
//            }
//            currentFocus = e.getId();
//        }
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
//        if (e.getId().equals(currentFocus)) {
//            currentFocus = null;
//        }
    }

    @Override
    protected void cleanupSystem() {
        app.getInputManager().removeListener(keyListeners);
        app.getStateManager().getState(GridMouseControlAppState.class).unregister(tileInputListener);
        focusSystem.lockMouseFocus(tileInputListener, false);
    }

    private void registerCharacterKey() {
        app.getInputManager().addListener(keyListeners, new String[]{
            "move", MouseInputEvent.MouseInputEventType.RMB.toString()});
    }

    private final ActionListener keyListeners = new ActionListener() {
        @Override
        public void onAction(String name, boolean isPressed, float tpf) {
            if (!isPressed) {
                if (name.equals("move") && !selectingMovePosition
                        || selectingMovePosition && name.equals(MouseInputEvent.MouseInputEventType.RMB.toString())) {
                    selectingMovePosition = !selectingMovePosition;
                    focusSystem.lockMouseFocus(tileInputListener, selectingMovePosition);
                }
            }
        }
    };

    private TileInputListener tileInputListener = new TileInputListener() {
        @Override
        public void onMouseAction(MouseInputEvent event) {
            EntitySet set = entityData.getEntities(MainFocusComponent.class, StaminaComponent.class);
            set.stream().forEach((e) -> {
                if (selectingMovePosition
                        && event.getType().equals(MouseInputEvent.MouseInputEventType.LMB)) {
                    list.put(e.getId(), event.getPosition());
                    selectingMovePosition = false;
                    focusSystem.lockMouseFocus(tileInputListener, selectingMovePosition);
                }
            });
//            if (currentFocus != null && selectingMovePosition
//                    && event.getType().equals(MouseInputEvent.MouseInputEventType.LMB)) {
//                list.put(currentFocus, event.getPosition());
////                entities.getEntity(currentFocus).set(new MoveToComponent(event.getPosition()));
//                selectingMovePosition = false;
//                focusSystem.lockMouseFocus(tileInputListener, selectingMovePosition);
//            }
        }
    };

}

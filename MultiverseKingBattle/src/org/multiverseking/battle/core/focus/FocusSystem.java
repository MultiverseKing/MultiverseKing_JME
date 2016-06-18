/*
 * Copyright (C) 2015 roah
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
package org.multiverseking.battle.core.focus;

import com.jme3.collision.CollisionResults;
import com.jme3.input.controls.ActionListener;
import com.jme3.math.Ray;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import org.hexgridapi.core.AbstractHexGridAppState;
import org.hexgridapi.core.camera.RTSCamera;
import org.hexgridapi.core.coordinate.HexCoordinate;
import org.hexgridapi.core.data.MapData;
import org.hexgridapi.core.mousepicking.GridMouseControlAppState;
import org.hexgridapi.events.MouseInputEvent;
import org.hexgridapi.events.MouseRayListener;
import org.hexgridapi.events.TileInputListener;
import org.multiverseking.battle.core.BattleSystemTest;
import org.multiverseking.core.utility.EntitySystemAppState;
import org.multiverseking.field.position.HexPositionComponent;
import org.multiverseking.render.AbstractRender;
import org.multiverseking.render.RenderComponent;
import org.multiverseking.render.RenderSystem;
import org.slf4j.LoggerFactory;

/**
 * Handle the focus system during battle.
 * @author roah
 */
public class FocusSystem extends EntitySystemAppState implements MouseRayListener {
    private RenderSystem renderSystem;
    private MapData mapData;
    private BattleSystemTest battleSystem;
    private Entity selectedEntity = null; // currently selected object/unit using the mouse
    private EntityId currentFocus = null; // can be a summoned unit
    private EntityId[] mainUnitsID;
    private Integer selectedMainUnit = 0; // always a titan or the core
    private boolean countDown = false;
    private RTSCamera camera;
    private float currentTimerCountDown = 0;
    private TileInputListener mouseFocusLocker;

    @Override
    protected EntitySet initialiseSystem() {
        camera = app.getStateManager().getState(RTSCamera.class);
        renderSystem = app.getStateManager().getState(RenderSystem.class);
        mapData = app.getStateManager().getState(AbstractHexGridAppState.class).getMapData();
        app.getStateManager().getState(GridMouseControlAppState.class).register(this);
        battleSystem = app.getStateManager().getState(BattleSystemTest.class);
        mainUnitsID = battleSystem.getMainUnitsID();
        
        // Register input Listeners
        app.getInputManager().addListener(keyListeners,
                new String[]{"char_0", "char_1", "char_2", "char_3"});
//        cursor = new CharacterFocusCursor(app, systemNode);
        return entityData.getEntities(RenderComponent.class, HexPositionComponent.class);
    }
    
    private final ActionListener keyListeners = new ActionListener() {
        @Override
        public void onAction(String name, boolean isPressed, float tpf) {
            if (!isPressed) {
                Integer tmp = Integer.valueOf(name.split("_")[1]);
                if (countDown && selectedMainUnit.equals(tmp)) {
                    camera.setCenter(renderSystem.getSpatial(
                            mainUnitsID[selectedMainUnit]).getLocalTranslation());
                    countDownStop();
                } else {
                    selectedMainUnit = tmp;
                    countDownReset();
                }
                setFocusTo(mainUnitsID[selectedMainUnit]);
            }
        }
    };

    /**
     * Countdown is used for doubleTap input
     * (pressing the key twice center the view on the character).
     */
    @Override
    protected void updateSystem(float tpf) {
        if (countDown) {
            currentTimerCountDown += tpf;
            if (currentTimerCountDown >= 2) {
                countDownStop();
            }
        }
    }

    private void countDownReset() {
        countDownStop();
        countDown = true;
    }

    private void countDownStop() {
        currentTimerCountDown = 0;
        countDown = false;
    }
    
    private void setFocusTo(EntityId id) {
        // @todo
//        Spatial model = app.getAssetManager().loadModel("org/multiverseking/assets/Interface/Battle/cursor/FX_cursor.j3o");
//        renderSystem.getSubSystemNode(battleSystem).attachChild(model);
//        model.setLocalTranslation(entityData.getComponent(id, HexPositionComponent.class).getPosition().toWorldPosition());
        entityData.setComponent(id, new MainFocusComponent());
        currentFocus = id;
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
    /**
     * Implements the Ray listeners to get the collision on character 
     * before the collision on the grid is process.
     * {@inheritDoc}
     */
    @Override
    public MouseInputEvent MouseRayInputAction(MouseInputEvent.MouseInputEventType mouseInputType, Ray ray) {
        if (entities.isEmpty() || mouseFocusLocker != null) {
            return null;
        }
        if (mouseInputType.equals(MouseInputEvent.MouseInputEventType.LMB)) {
            CollisionResults results = new CollisionResults();
            ((Node) renderSystem.getSubSystemNode(battleSystem)).collideWith(ray, results);
            if (results.size() > 0) {
                for (Entity e : entities) {
                    Spatial s = results.getClosestCollision().getGeometry().getParent();
                    do {
                        if(s != null) {
                            if (s.getName().equals(renderSystem.getSpatialName(e.getId()))) {
                                HexCoordinate pos = entityData.getComponent(e.getId(), HexPositionComponent.class).getPosition();
                                selectedEntity = e;
                                return new MouseInputEvent(MouseInputEvent.MouseInputEventType.LMB, pos, 
                                        mapData.getTile(pos).getHeight(), ray, results.getClosestCollision());
                            } else {
                                s = s.getParent();
                            }
                        } else {
                            break;
                        }
                    } while (true);
                }
            }
        }
        return null;
    }

    @Override
    public void onMouseAction(MouseInputEvent event) {
        if (mouseFocusLocker == null 
                || event.getType().equals(MouseInputEvent.MouseInputEventType.LMB)) {
            // Used when the spatial is not selected directly.
            Entity e = selectedEntity == null ? getEntity(event.getPosition()) : selectedEntity;
            if (e != null) {
                setFocusTo(e.getId());
            }
        }
    }

    private Entity getEntity(HexCoordinate coord) {
        for (Entity e : entities) {
            if (!e.get(RenderComponent.class).getRenderType().equals(AbstractRender.RenderType.Debug)) {
                HexPositionComponent posComp = entityData.getComponent(e.getId(), HexPositionComponent.class);
                if (posComp != null && posComp.getPosition().equals(coord)) {
                    return e;
                }
            }
        }
        return null;
    }
    
    public boolean lockMouseFocus(TileInputListener listeners, boolean isLock) {
        if(mouseFocusLocker != null && !mouseFocusLocker.equals(listeners)) {
            LoggerFactory.getLogger(this.getClass()).info("Ray Listeners already locked by {}", mouseFocusLocker);
            return false;
        } else if (mouseFocusLocker != null && !isLock) {
            mouseFocusLocker = null;
            return true;
        } else {
            mouseFocusLocker = listeners;
            return true;
        }
    }

    @Override
    protected void cleanupSystem() {
        app.getStateManager().getState(GridMouseControlAppState.class).unregister(this);
    }
}

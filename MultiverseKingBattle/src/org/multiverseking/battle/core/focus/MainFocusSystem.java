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
package org.multiverseking.battle.core.focus;

import com.simsilica.es.Entity;
import com.simsilica.es.EntityComponent;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import org.hexgridapi.core.coordinate.HexCoordinate;
import org.multiverseking.core.utility.EntitySystemAppState;
import org.multiverseking.core.utility.SubSystem;
import org.multiverseking.field.position.HexPositionComponent;
import org.multiverseking.render.AbstractRender;
import org.multiverseking.render.RenderComponent;
import org.multiverseking.render.RenderSystem;
import org.multiverseking.utility.MoveOnYControl;

/**
 * Ensure that only one character can be selected on the same time.
 * @author roah
 */
public class MainFocusSystem extends EntitySystemAppState implements SubSystem {
    
    private EntityId currentFocus = null;   // Current entity having the MainFocusComponent
    private EntityId cursor;

    @Override
    protected EntitySet initialiseSystem() {
        app.getStateManager().getState(RenderSystem.class).registerSubSystem(this);
        cursor = entityData.createEntity();
        entityData.setComponents(cursor, new RenderComponent("cursor", 
                AbstractRender.RenderType.Utility, this, false, new MoveOnYControl()),
                new HexPositionComponent(new HexCoordinate()));
        return entityData.getEntities(MainFocusComponent.class, HexPositionComponent.class);
    }

    @Override
    protected void updateSystem(float tpf) {
    }

    @Override
    protected void addEntity(Entity e) {
        if(currentFocus != null && currentFocus != e.getId()) {
            entityData.removeComponent(currentFocus, MainFocusComponent.class);
        }
        currentFocus = e.getId();
//        entityData.setComponent(cursor, new HexPositionComponent(e.get(HexPositionComponent.class).getPosition()));
        EntityComponent comp = entityData.getComponent(cursor, 
                RenderComponent.class).setParent(currentFocus, true);
        entityData.setComponent(cursor, comp);
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

    @Override
    public void rootSystemIsRemoved() {
    }
    
}

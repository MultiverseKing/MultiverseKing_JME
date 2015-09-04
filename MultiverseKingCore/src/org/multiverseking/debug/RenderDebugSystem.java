package org.multiverseking.debug;

import com.simsilica.es.Entity;
import com.simsilica.es.EntitySet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hexgridapi.core.coordinate.HexCoordinate;
import org.multiverseking.field.component.AreaEventComponent;
import org.multiverseking.field.component.AreaEventComponent.Event;
import org.multiverseking.field.position.HexPositionComponent;
import org.multiverseking.render.AbstractRender.RenderType;
import org.multiverseking.render.RenderComponent;
import org.multiverseking.render.RenderSystem;
import org.multiverseking.utility.system.EntitySystemAppState;
import org.multiverseking.utility.system.SubSystem;

/**
 *
 * @author roah
 */
public class RenderDebugSystem extends EntitySystemAppState implements SubSystem {

    @Override
    protected EntitySet initialiseSystem() {
        app.getStateManager().getState(RenderSystem.class).registerSubSystem(this, true);
        return entityData.getEntities(AreaEventComponent.class);
    }

    @Override
    protected void updateSystem(float tpf) {
    }

    @Override
    protected void addEntity(Entity e) {
        initializeRender(e, true);
    }

    @Override
    protected void updateEntity(Entity e) {
        RenderComponent render = entityData.getComponent(e.getId(), RenderComponent.class);
        if (render != null) {
            if (e.get(AreaEventComponent.class).getEvents()
                    .contains(AreaEventComponent.Event.Start)
                    && render.getName().equals("T_Shape")
                    || !e.get(AreaEventComponent.class).getEvents()
                    .contains(AreaEventComponent.Event.Start)
                    && !render.getName().equals("T_Shape")) {
                initializeRender(e, true);
            }
        } else {
            initializeRender(e, true);
        }
    }

    @Override
    protected void removeEntity(Entity e) {
        entityData.removeComponent(e.getId(), RenderComponent.class);
        entityData.removeComponent(e.getId(), HexPositionComponent.class);
    }

    private void initializeRender(Entity e, boolean isVisible) {
        RenderComponent render;
        if (e.get(AreaEventComponent.class).getEvents().contains(Event.Start)) {
//            render = new RenderComponent("S_Shape", RenderType.Debug, this, isVisible);
            render = new RenderComponent("S_Shape", RenderType.Debug, this, isVisible);
        } else {
            render = new RenderComponent("T_Shape", RenderType.Debug, this, isVisible);
        }
        e.set(render);
        e.set(new HexPositionComponent(e.get(AreaEventComponent.class).getPosition()));
    }

    public void showDebug(boolean show, HexCoordinate position, SubSystem system) {
        for (Entity e : entities) {
            if (e.get(AreaEventComponent.class).getPosition().equals(position)) {
                RenderComponent render = entityData.getComponent(e.getId(), RenderComponent.class);
                if (show && render != null) {
                    e.set(render.cloneAndShow());
                } else if (!show && render != null) {
                    e.set(render.cloneAndHide());
                } else if (render == null) {
                    initializeRender(e, show);
                }
                return;
            }
        }
        Logger.getGlobal().log(Level.WARNING, "{0} : No event at the specifiated "
                + "position : pos({1}).", new Object[]{getClass().getName(), position});
    }

    @Override
    public void setEnabled(boolean enabled) {
        for (Entity e : entities) {
            RenderComponent comp = entityData.getComponent(e.getId(), RenderComponent.class);
            entityData.setComponent(e.getId(), enabled ? comp.cloneAndShow() : comp.cloneAndHide());
        }
        super.setEnabled(enabled);
    }

    @Override
    protected void cleanupSystem() {
        for (Entity e : entities) {
            removeEntity(e);
        }
        if (app.getStateManager().getState(RenderSystem.class) != null) {
            app.getStateManager().getState(RenderSystem.class).removeSubSystem(this, true);
        }
    }

    @Override
    public void rootSystemIsRemoved() {
        app.getStateManager().detach(this);
    }
}

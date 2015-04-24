package org.multiversekingesapi.render;

import com.jme3.scene.Node;
import com.simsilica.es.Entity;
import com.simsilica.es.EntitySet;
import org.multiversekingesapi.EntitySystemAppState;
import org.multiversekingesapi.SubSystem;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hexgridapi.utility.HexCoordinate;
import org.multiversekingesapi.field.component.AreaEventComponent;
import org.multiversekingesapi.field.component.AreaEventComponent.Event;
import org.multiversekingesapi.field.position.HexPositionComponent;
import org.multiversekingesapi.render.AbstractRender.RenderType;

/**
 *
 * @author roah
 */
public class AreaEventRenderDebugSystem extends EntitySystemAppState implements SubSystem {

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
                    && render.getName().equals("Trigger_Shape")
                    || !e.get(AreaEventComponent.class).getEvents()
                    .contains(AreaEventComponent.Event.Start)
                    && !render.getName().equals("Trigger_Shape")) {
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
            render = new RenderComponent("Start_Shape", RenderType.Debug, this, isVisible);
        } else {
            render = new RenderComponent("Trigger_Shape", RenderType.Debug, this, isVisible);
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

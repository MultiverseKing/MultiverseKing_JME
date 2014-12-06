package editor.area;

import com.jme3.scene.Node;
import com.simsilica.es.Entity;
import com.simsilica.es.EntitySet;
import entitysystem.EntitySystemAppState;
import entitysystem.SubSystem;
import entitysystem.field.position.HexPositionComponent;
import entitysystem.render.RenderComponent;
import entitysystem.render.RenderComponent.Type;
import entitysystem.render.RenderSystem;
import hexsystem.area.AreaEventComponent;
import hexsystem.area.AreaEventComponent.Event;

/**
 *
 * @author roah
 */
public class AreaEventRenderDebugSystem extends EntitySystemAppState implements SubSystem{

    private Node eventNode;
    
    @Override
    protected EntitySet initialiseSystem() {
        eventNode = app.getStateManager().getState(RenderSystem.class).registerSubSystem(this, true);
        return entityData.getEntities(AreaEventComponent.class, HexPositionComponent.class);
    }

    @Override
    protected void updateSystem(float tpf) {
    }

    @Override
    protected void addEntity(Entity e) {
        if(e.get(AreaEventComponent.class).getEvent().contains(Event.Start)){
            entityData.setComponent(e.getId(), new RenderComponent("Start_Shape", Type.Debug, this));
        } else {
            entityData.setComponent(e.getId(), new RenderComponent("Trigger_Shape", Type.Debug, this));
        }
    }

    @Override
    protected void updateEntity(Entity e) {
    }

    @Override
    protected void removeEntity(Entity e) {
        entityData.removeComponent(e.getId(), RenderComponent.class);
    }

    @Override
    protected void cleanupSystem() {
        for(Entity e : entities){
            removeEntity(e);
        }
        if(app.getStateManager().getState(RenderSystem.class) != null){
            app.getStateManager().getState(RenderSystem.class).removeSubSystem(this, true);
        }
    }

    public void removeSubSystem() {
    }

    public String getSubSystemName() {
        return getClass().getName();
    }
    
}

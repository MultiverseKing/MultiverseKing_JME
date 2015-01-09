package editor.area;

import com.jme3.scene.Node;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import entitysystem.EntitySystemAppState;
import entitysystem.SubSystem;
import entitysystem.field.position.HexPositionComponent;
import entitysystem.render.RenderComponent;
import entitysystem.render.RenderComponent.RenderType;
import entitysystem.render.RenderSystem;
import hexsystem.area.AreaEventComponent;
import hexsystem.area.AreaEventComponent.Event;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hexgridapi.utility.HexCoordinate;

/**
 *
 * @author roah
 */
public class AreaEventRenderDebugSystem extends EntitySystemAppState implements SubSystem{

    private Node eventNode;
    private HashMap<HexCoordinate, EntityId> event = new HashMap<>();
    
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
        if(!event.containsKey(e.get(HexPositionComponent.class).getPosition())){
            event.put(e.get(HexPositionComponent.class).getPosition(), e.getId());
        }
        if(e.get(AreaEventComponent.class).getEvent().contains(Event.Start)){
            entityData.setComponent(e.getId(), new RenderComponent("Start_Shape", RenderType.Debug, this));
        } else {
            entityData.setComponent(e.getId(), new RenderComponent("Trigger_Shape", RenderType.Debug, this));
        }
    }

    @Override
    protected void updateEntity(Entity e) {
    }
    

    public void showDebug(boolean show, HexCoordinate position, SubSystem system) {
        if(entityData.getComponent(event.get(position), RenderComponent.class) == null){
            return;
        }
        if(show && event.containsKey(position)){
            entityData.setComponent(event.get(position), entityData.getComponent(event.get(position), RenderComponent.class).cloneAndHide());
        } else if (!show && event.containsKey(position)){
            entityData.setComponent(event.get(position), entityData.getComponent(event.get(position), RenderComponent.class).cloneAndShow());
        } else {
            Logger.getGlobal().log(Level.WARNING, "{0} : No event at the specifiated position : pos({1}).", new Object[]{getClass().getName(), position});
        }
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

    @Override
    public void removeSubSystem() {
    }

    @Override
    public String getSubSystemName() {
        return getClass().getName();
    }
    
}

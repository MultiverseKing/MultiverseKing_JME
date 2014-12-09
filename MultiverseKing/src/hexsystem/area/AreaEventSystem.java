package hexsystem.area;

import com.simsilica.es.Entity;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import entitysystem.EntitySystemAppState;
import entitysystem.field.position.HexPositionComponent;
import hexsystem.area.AreaEventComponent.Event;
import java.util.ArrayList;
import java.util.HashMap;
import org.hexgridapi.utility.HexCoordinate;
import org.hexgridapi.utility.Rotation;

/**
 * Handle all event in the area, as teleportation, starting point etc...
 *
 * @author roah
 */
public class AreaEventSystem extends EntitySystemAppState {

    private HashMap<HexCoordinate, EntityId> eventPosition = new HashMap<>();
    private HexCoordinate startPosition;

    @Override
    protected EntitySet initialiseSystem() {
        return entityData.getEntities(AreaEventComponent.class, HexPositionComponent.class);
    }

    public boolean hasStartPosition() {
        return startPosition == null ? false : true;
    }

    public HexCoordinate getStartPosition() {
        return startPosition.duplicate();
    }

    @Override
    protected void updateSystem(float tpf) {
    }

    @Override
    protected void addEntity(Entity e) {
        register(e);
    }

    @Override
    protected void updateEntity(Entity e) {
        register(e);
    }

    private void register(Entity e) {
        if (startPosition == null && e.get(AreaEventComponent.class).getEvent().contains(Event.Start)) {
            startPosition = e.get(HexPositionComponent.class).getPosition();
        }
        eventPosition.put(e.get(HexPositionComponent.class).getPosition(), e.getId());
    }

    @Override
    protected void removeEntity(Entity e) {
        eventPosition.remove(e.get(HexPositionComponent.class).getPosition());
    }

    @Override
    protected void cleanupSystem() {
        eventPosition.clear();
    }

    public AreaEventComponent getValue(HexCoordinate inspectedTilePos) {
        if (eventPosition.containsKey(inspectedTilePos)) {
            return entities.getEntity(eventPosition.get(inspectedTilePos)).get(AreaEventComponent.class);
        }
        return null;
    }

    public void updateEvent(HexCoordinate inspectedTilePos, AreaEventComponent.Event event, boolean add) {
        if (event.equals(Event.Start) && startPosition != null) {
            EntityId id = eventPosition.get(startPosition);
            AreaEventComponent comp = entityData.getComponent(id, AreaEventComponent.class).cloneAndRemove(event);
            if (comp.getEvent().isEmpty()) {
                entityData.removeComponent(eventPosition.get(startPosition), AreaEventComponent.class);
            } else {
                entityData.setComponent(eventPosition.get(startPosition), comp);
            }
            startPosition = inspectedTilePos;
        }
        EntityId id = eventPosition.get(inspectedTilePos);
        if(add){
            if (id != null) {
                entityData.setComponent(id, entityData.getComponent(id, AreaEventComponent.class).cloneAndAdd(event));
            } else {
                id = entityData.createEntity();
                entityData.setComponents(id, new AreaEventComponent(event), new HexPositionComponent(inspectedTilePos, Rotation.A));
            }
        } else {
            if (id != null && entityData.getComponent(id, AreaEventComponent.class) != null) {
                AreaEventComponent comp = entityData.getComponent(id, AreaEventComponent.class).cloneAndRemove(event);
                if(comp.getEvent().isEmpty()){
                    entityData.removeEntity(id);
                } else {
                    entityData.setComponent(id, comp);
                }
            } else {
                id = entityData.createEntity();
                entityData.setComponents(id, new AreaEventComponent(event), new HexPositionComponent(inspectedTilePos, Rotation.A));
            }
        }
    }

    public void updateEvents(HexCoordinate inspectedTilePos, ArrayList<Event> currentEvent) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void clearAllCurrentEvent() {
        for(EntityId id : eventPosition.values()){
            entityData.removeComponent(id, AreaEventComponent.class);
        }
        startPosition = null;
    }
}

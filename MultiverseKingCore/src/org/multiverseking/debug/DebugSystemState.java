package org.multiverseking.debug;

import com.simsilica.es.Entity;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import org.hexgridapi.core.coordinate.HexCoordinate;
import org.multiverseking.field.component.AreaEventComponent;
import org.multiverseking.field.component.AreaEventComponent.Event;
import org.multiverseking.core.utility.EntitySystemAppState;

/**
 * Handle all event in the area, as teleportation, starting point etc...
 *
 * @author roah
 */
public class DebugSystemState extends EntitySystemAppState {

    private HexCoordinate startPosition;

    @Override
    protected EntitySet initialiseSystem() {
        return entityData.getEntities(AreaEventComponent.class);
    }

    public HexCoordinate getStartPosition() {
        return startPosition != null ? startPosition.clone() : null;
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
        if (startPosition == null && e.get(AreaEventComponent.class).getEvents()
                .contains(Event.Start)) {
            startPosition = e.get(AreaEventComponent.class).getPosition();
        }
    }

    @Override
    protected void removeEntity(Entity e) {
    }

    public AreaEventComponent getValue(HexCoordinate inspectedTilePos) {
        for (Entity e : entities) {
            if (e.get(AreaEventComponent.class).getPosition().equals(inspectedTilePos)) {
                return e.get(AreaEventComponent.class);
            }
        }
        return null;
    }

    public void updateEvent(HexCoordinate inspectedTilePos, AreaEventComponent.Event event, boolean add) {
        Entity entity = null;
        for (Entity e : entities) {
            if (e.get(AreaEventComponent.class).getPosition().equals(inspectedTilePos)) {
                entity = e;
            }
        }
        if (add) {
            if (event.equals(Event.Start) && startPosition != null) {
                if (startPosition.equals(inspectedTilePos)) {
                    return;
                }
                for (Entity e : entities) {
                    if (e.get(AreaEventComponent.class).getPosition().equals(startPosition)) {
                        AreaEventComponent comp = e.get(AreaEventComponent.class)
                                .cloneAndRemove(event);
                        if (comp == null) {
                            entityData.removeComponent(e.getId(), AreaEventComponent.class);
                        } else {
                            e.set(comp);
//                            entityData.setComponent(e.getId(), comp);
                        }
                        startPosition = inspectedTilePos;
                        break;
                    }
                }
            } else if (event.equals(Event.Start)) {
                startPosition = inspectedTilePos;
            }
            if (entity != null) {
                entity.set(entity.get(AreaEventComponent.class).cloneAndAdd(event));
//                entityData.setComponent(entity.getId(), entity.get(
//                        AreaEventComponent.class).cloneAndAdd(event));
            } else {
                EntityId id = entityData.createEntity();
                entityData.setComponent(id, new AreaEventComponent(inspectedTilePos, event));
            }
        } else {
            if (entity != null) {
                AreaEventComponent comp = entity.get(AreaEventComponent.class).cloneAndRemove(event);
                if (comp == null) {
                    entityData.removeComponent(entity.getId(), AreaEventComponent.class);
                } else {
                    entity.set(comp);
//                    entityData.setComponent(entity.getId(), comp);
                }
            }
            if (event.equals(Event.Start) && startPosition != null) {
                startPosition = null;
            }
        }
    }

    @Override
    protected void cleanupSystem() {
    }
}

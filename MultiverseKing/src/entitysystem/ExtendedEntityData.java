package entitysystem;

import com.simsilica.es.Entity;
import com.simsilica.es.EntityComponent;
import com.simsilica.es.EntityId;
import com.simsilica.es.base.DefaultEntityData;
import com.simsilica.es.base.EntityIdGenerator;
import entitysystem.position.HexPositionComponent;
import hexsystem.MapData;
import utility.HexCoordinate;

/**
 * Bridge between hexSystem and entitySystem.
 *
 * @todo all method have not been fully checked to see if they update tile
 * Walkable value in mapData.
 * @author roah
 */
public class ExtendedEntityData extends DefaultEntityData {

    private final MapData mapData;

    /**
     * Hex system Data handler.
     * @return 
     */
    public MapData getMapData() {
        return mapData;
    }

    /**
     * Data handler for Hex system and for the Entity system 
     * (ES set to default param).
     *
     * @param mapData hex system data handler.
     */
    public ExtendedEntityData(MapData mapData) {
        this.mapData = mapData;
    }

    /**
     * Data handler for Entity system and Hex system
     *
     * @param mapData hex system data handler.
     * @param idGenerator Entity Id algo generator to use, not sure about... :§
     */
    public ExtendedEntityData(MapData mapData, EntityIdGenerator idGenerator) {
        super(idGenerator);
        this.mapData = mapData;
    }

    /**
     * Add a component from the specifiate entity.
     *
     * @param entityId to add component.
     * @param component to add.
     */
    @Override
    public void setComponent(EntityId entityId, EntityComponent component) {
        if (component instanceof HexPositionComponent) {
            System.out.println("Set Component - 002");
            HexPositionComponent newComp = (HexPositionComponent) component;
            HexPositionComponent oldComp = getComponent(entityId, HexPositionComponent.class);
            if (oldComp != null) {
                mapData.setTileIsWalkable(oldComp.getPosition(), true);
            }
            mapData.setTileIsWalkable(newComp.getPosition(), false);
        }
        super.setComponent(entityId, component);
    }

    /**
     * Remove a component from the specifiate entity.
     *
     * @param entityId entity to add component.
     * @param type of component to remove.
     * @return true if removed.
     */
    @Override
    public boolean removeComponent(EntityId entityId, Class type) {
        if (type.equals(HexPositionComponent.class)) {
            HexCoordinate pos = getComponent(entityId, HexPositionComponent.class).getPosition();

            System.out.println("remove Component - 003b");
            mapData.setTileIsWalkable(pos, true);
        }

        return super.removeComponent(entityId, type);
    }

    /**
     * Replace an entity component by a new one, keep track of the oldOne.
     *
     * @param e entity concerned.
     * @param oldValue old Component.
     * @param newValue new Component.
     */
    @Override
    protected void replace(Entity e, EntityComponent oldValue, EntityComponent newValue) {
        super.replace(e, oldValue, newValue);
        HexPositionComponent oldComp = (HexPositionComponent) oldValue;
        HexPositionComponent newComp = (HexPositionComponent) newValue;
        if (oldComp != null && newComp != null) {
            System.out.println("replace Component - 004");
            mapData.setTileIsWalkable(oldComp.getPosition(), true);
            mapData.setTileIsWalkable(newComp.getPosition(), false);
        }
    }

    /**
     * Add multiple components to the specifiate entity.
     *
     * @param entityId entity to add component.
     * @param components components to add.
     */
    @Override
    public void setComponents(EntityId entityId, EntityComponent... components) {
        super.setComponents(entityId, components);
//        for(EntityComponent e : components){
//            if(e instanceof HexPositionComponent){
//                System.out.println("SetComponents - 005");
//                HexPositionComponent comp = (HexPositionComponent) e;
//                mapData.setTileIsWalkable(comp.getPosition(), false);
//            }
//        }
    }
}
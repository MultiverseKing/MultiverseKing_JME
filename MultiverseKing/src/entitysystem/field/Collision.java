package entitysystem.field;

import java.util.ArrayList;
import java.util.HashMap;
import utility.HexCoordinate;

/**
 *
 * @author roah
 */
public class Collision {

    private final HashMap<Byte, CollisionData> collisionLayer;

    public Collision() {
        collisionLayer = new HashMap<Byte, CollisionData>();
    }

    public Collision(HashMap<Byte, CollisionData> collisionLayer) {
        this.collisionLayer = collisionLayer;
    }

    /**
     * Add collision to the spacifiate Layer.
     *
     * @param layer to add in.
     * @param collisionData data belong to that layer.
     * @return false if the layer already exist.
     */
    public boolean addLayer(byte layer, CollisionData collisionData) {
        if (collisionLayer.containsKey(layer)) {
            return false;
        }
        collisionLayer.put(layer, collisionData);
        return true;
    }

    /**
     * Remove a collision layer.
     *
     * @return true if removed correctly.
     */
    public boolean removeLayer(byte layer) {
        if (collisionLayer.remove(layer) != null) {
            return true;
        }
        return false;
    }

    public CollisionData getCollisionLayer(byte layer) {
        return collisionLayer.get(layer);
    }

    public Byte[] getLayers() {
        return (Byte[]) collisionLayer.keySet().toArray();
    }

    public class CollisionData {

        private final byte areaRange;
        private final ArrayList<HexCoordinate> coord;

        public CollisionData(byte areaRange, ArrayList<HexCoordinate> coord) {
            this.areaRange = areaRange;
            this.coord = coord;
        }

        public byte getAreaRange() {
            return areaRange;
        }

        public ArrayList<HexCoordinate> getCoord() {
            return coord;
        }
    }
}

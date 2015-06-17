package org.multiverseking.field;

import java.util.ArrayList;
import java.util.HashMap;
import org.hexgridapi.core.geometry.builder.coordinate.HexCoordinate;

/**
 *
 * @author roah
 */
public class Collision {

    private final HashMap<Byte, CollisionData> collisionLayer;

    public Collision() {
        collisionLayer = new HashMap<>();
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
    public final boolean addLayer(byte layer, CollisionData collisionData) {
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
        return collisionLayer.keySet().toArray(new Byte[collisionLayer.keySet().size()]);
    }

    public class CollisionData {

        private byte areaRadius;
        private ArrayList<HexCoordinate> coord = new ArrayList<HexCoordinate>();

        public CollisionData(byte areaRange) {
            this.areaRadius = areaRange;
        }

        public CollisionData(byte areaRange, ArrayList<HexCoordinate> coord) {
            this.areaRadius = areaRange;
            this.coord = coord;
        }

        public byte getAreaRadius() {
            return areaRadius;
        }

        public ArrayList<HexCoordinate> getCoord() {
            return coord;
        }

        public void setAreaRadius(byte radius) {
            this.areaRadius = radius;
        }

        public void addPosition(HexCoordinate pos) {
            coord.add(pos);
        }

        public void removePosition(HexCoordinate pos) {
            coord.remove(pos);
        }
    }
}

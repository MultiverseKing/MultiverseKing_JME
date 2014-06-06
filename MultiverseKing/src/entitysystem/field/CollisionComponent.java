package entitysystem.field;

import com.simsilica.es.PersistentComponent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import utility.HexCoordinate;
import utility.Vector2Int;

/**
 *
 * @author roah
 */
public class CollisionComponent implements PersistentComponent {
    /**
     * Collision layer of the entity and collision size.
     */
    private final HashMap<Byte, ArrayList> collision;

    /**
     * Create a new collision component for a 1 Hex size unit defined layer.
     */
    public CollisionComponent(Byte layer){
        collision = new HashMap<Byte, ArrayList>(2);
        collision.put(layer, new ArrayList<Vector2Int>());
        collision.get(layer).add(new HexCoordinate(HexCoordinate.AXIAL, Vector2Int.ZERO));
    }
    
    public CollisionComponent(HashMap<Byte, ArrayList> collision) {
        this.collision = collision;
    }
    
    public Byte[] getUsedLayers(){
        Iterator<Byte> set = collision.keySet().iterator();
        Byte[] result = new Byte[collision.keySet().size()];
        for(int i = 0; i < result.length; i++){
            result[i] = set.next();
        }
        return result;
    }
    
    public ArrayList<HexCoordinate> getCollisionOnLayer(Byte layer){
        return collision.get(layer);
    }
}

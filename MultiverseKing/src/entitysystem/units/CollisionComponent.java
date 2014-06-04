package entitysystem.units;

import com.simsilica.es.PersistentComponent;
import java.util.ArrayList;
import java.util.HashMap;
import kingofmultiverse.MultiverseMain;
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
    private final HashMap<HexCoordinate, Byte> collision;

    /**
     * Create a new collision component for a 1 Hex size unit defined layer.
     */
    public CollisionComponent(Byte layer){
        collision = new HashMap<HexCoordinate, Byte>();
        collision.put(new HexCoordinate(HexCoordinate.AXIAL, Vector2Int.ZERO), layer);
    }
    
    public CollisionComponent(HashMap<HexCoordinate, Byte> collision) {
        this.collision = collision;
    }
    
    public ArrayList<Byte> getAllCollisionLayer(){
        ArrayList<Byte> result = new ArrayList<Byte>();
        for(Byte layer : collision.values()){
            result.add(layer);
        }
        return result;
    }
    
    public ArrayList<HexCoordinate> getCollisionOnLayer(Byte layer){
        return MultiverseMain.getKeysByValue(collision, layer);
    }
}

package entitysystem.units;

import entitysystem.ExtendedComponent;
import java.util.ArrayList;
import java.util.HashMap;
import kingofmultiverse.MultiverseMain;
import utility.HexCoordinate;

/**
 *
 * @author roah
 */
public class CollisionComponent implements ExtendedComponent {
    /**
     * Collision layer of the entity and collision size.
     */
    private final HashMap<HexCoordinate, Byte> collision;

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

    public ExtendedComponent clone() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}

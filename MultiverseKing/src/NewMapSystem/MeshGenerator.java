package NewMapSystem;

import com.jme3.scene.Spatial;
import utility.Vector2Int;

/**
 *
 * @author Eike Foede
 */
public class MeshGenerator {
    private int chunkSizeX;
    private int chunkSizeY;

    public MeshGenerator(int chunkSizeX, int chunkSizeY) {
        this.chunkSizeX = chunkSizeX;
        this.chunkSizeY = chunkSizeY;
    }
    
    public Spatial generateChunk(HexTile[][] tiles, Vector2Int from){
        return null;
    }
}

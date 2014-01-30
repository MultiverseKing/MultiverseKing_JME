package NewMapSystem;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.util.HashMap;
import utility.Vector2Int;

/**
 *
 * @author Eike Foede
 */
public class MapSpatialAppState extends AbstractAppState implements TileChangeListener {

    private final int chunkSizeX = 3;
    private final int chunkSizeY = 3;
    private Node mapNode;
    private HashMap<Vector2Int, Spatial> chunks = new HashMap<Vector2Int, Spatial>();
    private MapData mapData;
    private MeshGenerator meshGen = new MeshGenerator(chunkSizeX, chunkSizeY);

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        mapData = stateManager.getState(MapData.class);
        HexTile[][] tiles = mapData.getAllTiles();
        for (int x = 0; x < tiles.length; x += chunkSizeX) {
            for (int y = 0; y < tiles.length; y += chunkSizeY) {
                Vector2Int pos = getChunkForPosition(x, y);
                Spatial spat = meshGen.generateChunk(tiles, pos);
                chunks.put(pos, spat);
                mapNode.attachChild(spat);
            }
        }
    }

    private Vector2Int getChunkForPosition(int x, int y) {
        return new Vector2Int(x / chunkSizeX, y / chunkSizeY);
    }

    public void tileChange(TileChangeEvent event) {
        Vector2Int chunkPosition = getChunkForPosition(event.getX(), event.getY());
        Spatial oldSpat = chunks.get(chunkPosition);
        mapNode.detachChild(oldSpat);
        Spatial newSpat = meshGen.generateChunk(mapData.getAllTiles(), chunkPosition);
        mapNode.attachChild(newSpat);
        chunks.put(chunkPosition, newSpat);
    }
}

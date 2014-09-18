package hexsystem;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import hexsystem.chunksystem.ChunkControl;
import hexsystem.events.ChunkChangeEvent;
import hexsystem.events.ChunkChangeListener;
import hexsystem.events.TileChangeEvent;
import hexsystem.events.TileChangeListener;
import java.util.HashMap;
import java.util.Iterator;
import utility.Vector2Int;

/**
 * Take care of updating the geometry accordingly to the data (room grid).
 * @author roah
 */
public class HexSystemAppState extends AbstractAppState implements ChunkChangeListener, TileChangeListener {

    private HashMap chunkNode = new HashMap<String, Node>();
    /**
     * Main application.
     */
    protected final SimpleApplication main;
    /**
     * Tiles data manager.
     */
    protected final MapData mapData;
    /**
     * Node containing all Tile related geometry.
     */
    protected final Node mapNode;

    public MapData getMapData() {
        return mapData;
    }

    /**
     * Settup the base param for the hexMap, create a new mapNode to hold the
     * hexMap.
     *
     * @param main
     * @param mapData
     */
    public HexSystemAppState(SimpleApplication main, MapData mapData) {
        this.main = main;
        this.mapData = mapData;
        mapNode = new Node("hexMapNode");
    }

    /**
     * Load the shader used by the hexMap to render all tile.
     *
     * @param stateManager
     * @param app
     */
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        mapData.registerChunkChangeListener(this);
        mapData.registerTileChangeListener(this);

        main.getRootNode().attachChild(mapNode);
        /**
         * This double the Vertice count.
         */
        mapNode.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
    }

    /**
     * Make change to chunk according to the event.
     *
     * @param event contain information of the last chunk event.
     */
    public void chunkUpdate(ChunkChangeEvent event) {
        if (!event.purge() && event.getChunkPos() == Vector2Int.INFINITY) {
            for (Iterator it = chunkNode.values().iterator(); it.hasNext();) {
                Node chunk = (Node) it.next();
                chunk.getControl(ChunkControl.class).update();
            }
        } else if (event.purge() && event.getChunkPos() == null) {
            mapNode.detachAllChildren();
            chunkNode.clear();
        } else {
            insertNewChunk(event.getChunkPos());
        }
    }

    private void insertNewChunk(Vector2Int pos) {
        Node chunk = new Node(pos.toString());
        chunkNode.put(pos.toString(), chunk);
        chunk.setLocalTranslation(mapData.getChunkWorldPosition(pos));
        chunk.addControl(new ChunkControl(mapData, main.getAssetManager(), mapData.getMapElement()));
        mapNode.attachChild(chunk);
    }

    /**
     * Make change to tile according to the event.
     *
     * @param event contain information on the last tile event.
     */
    public void tileChange(TileChangeEvent event) {
        if (event.getNewTile().getElement() != event.getOldTile().getElement()
                || event.getNewTile().getHeight() != event.getOldTile().getHeight()) {
            mapNode.getChild(event.getChunkPos().toString()).getControl(ChunkControl.class).update();
        }

    }
}

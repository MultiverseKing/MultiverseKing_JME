package org.hexgridapi.core;

import org.hexgridapi.core.mesh.MeshParameter;
import org.hexgridapi.core.control.AreaRangeControl;
import org.hexgridapi.core.control.ChunkControl;
import com.jme3.asset.AssetManager;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import org.hexgridapi.events.TileChangeListener;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import org.hexgridapi.events.TileChangeEvent;
import org.hexgridapi.utility.HexCoordinate;
import org.hexgridapi.utility.Vector2Int;

/**
 * @todo update for chunkChange listener
 * @author roah
 */
public class HexGrid {

    /**
     * Parameter used to generate the grid mesh.
     */
    protected final MeshParameter meshParam;
    /**
     * Node containing all Tiles.
     */
    protected MapData mapData;
    protected final Node gridNode = new Node("HexGridNode");
    protected boolean debugMode;
    protected HashMap chunksNodes = new HashMap<Vector2Int, Node>();
    protected Node areaRangeNode;
    protected AssetManager assetManager;
    /**
     * Listeners.
     */
    private final TileChangeListener tileChangeListener = new TileChangeListener() {
        public final void onTileChange(TileChangeEvent... events) {
            if (events.length > 1) {
                HashSet<Vector2Int> updatedChunk = new HashSet<Vector2Int>();
                for (int i = 0; i < events.length; i++) {
                    Vector2Int pos = events[i].getTilePos().getCorrespondingChunk();
                    if (chunksNodes.containsKey(pos)) {
                        if (updatedChunk.add(pos)) {
                            //updateChunk
                            updateChunk(pos);
                        }
                    } else if (updatedChunk.add(pos)) {
                        //add chunk
                        addChunk(pos);
                    }
                }
            } else {
                updateChunk(events[0]);
            }
        }
    };

    public HexGrid(MapData mapData, AssetManager assetManager, Node rootNode) {
        this(mapData, assetManager, rootNode, false);
    }

    public HexGrid(MapData mapData, AssetManager assetManager, Node rootNode, boolean debugMode) {
        this.assetManager = assetManager;
        this.meshParam = new MeshParameter(mapData, -5);
        this.debugMode = debugMode;
        this.mapData = mapData;
        rootNode.attachChild(gridNode);
        mapData.registerTileChangeListener(tileChangeListener);
    }

    public final Node getGridNode() {
        return gridNode;
    }

    public final Set<Vector2Int> getChunksNodes() {
        return Collections.unmodifiableSet(chunksNodes.keySet());
    }

    /**
     * Make change to tile according to the event.
     *
     * @param events contain information on the last tile event.
     */
    private void updateChunk(TileChangeEvent event) {
        if (event.getTilePos() != null) {
            Vector2Int chunkPos = event.getTilePos().getCorrespondingChunk();
            if (!chunksNodes.containsKey(chunkPos)) {
                addChunk(chunkPos);
            } else if (!(event.getNewTile() == null && event.getOldTile() == null)) {
//                System.err.println("Not implemented : Add or Delete Tile from chunk");
                updateChunk(chunkPos);
            } else {
                System.err.println("old && new tile is null, an error have occurs, this will be ignored.");
            }
        } else {
            /**
             * Update the whole Map
             */
        }
    }

    private void updateChunk(Vector2Int chunkPos) {
        ((Node) chunksNodes.get(chunkPos)).getControl(ChunkControl.class).update();

        if (((Node) chunksNodes.get(chunkPos)).getControl(ChunkControl.class).isEmpty()) {
            removeChunk(chunkPos);
        } else {
            updatedChunk(((Node) chunksNodes.get(chunkPos)).getControl(ChunkControl.class));
        }
    }

    private void addChunk(Vector2Int chunkPos) {
        Node chunk = new Node(chunkPos.toString());
        chunksNodes.put(chunkPos, chunk);
        chunk.addControl(new ChunkControl(meshParam, assetManager, false, debugMode, chunkPos));
        chunk.setLocalTranslation(getChunkWorldPosition(chunkPos));
        gridNode.attachChild(chunk);
        insertedChunk(chunk.getControl(ChunkControl.class));
    }

    private void removeChunk(Vector2Int chunkPos) {
        ((Node) chunksNodes.get(chunkPos)).removeFromParent();
        chunksNodes.remove(chunkPos);
        removedChunk(chunkPos);
    }

    public final void showAreaRange(HexCoordinate centerPosition, int radius, ColorRGBA color) {
        if (areaRangeNode == null && radius > 0) {
            areaRangeNode = new Node("areaRangeNode");
            AreaRangeControl control = new AreaRangeControl(meshParam, assetManager, debugMode, centerPosition, radius, color);
            areaRangeNode.addControl(control);


        } else if (areaRangeNode != null && radius > 0) {
            areaRangeNode.getControl(AreaRangeControl.class).update(centerPosition, radius, color);
        } else if (areaRangeNode != null && radius <= 0) {
            gridNode.detachChild(areaRangeNode);
            return;

        }
        if (!gridNode.hasChild(areaRangeNode)) {
            gridNode.attachChild(areaRangeNode);
        }
        Vector3f pos = centerPosition.add(-radius + ((centerPosition.getAsOffset().y & 1) == 0 && (radius & 1) != 0 ? -1 : 0), -radius).convertToWorldPosition();
        areaRangeNode.setLocalTranslation(pos.x, pos.y + 0.1f, pos.z);
    }

    protected void insertedChunk(ChunkControl control) {
    }

    protected void updatedChunk(ChunkControl control) {
    }

    protected void removedChunk(Vector2Int pos) {
    }

    /**
     * Convert chunk position in hexMap to world unit.
     *
     * @param chunkPosition
     * @hint world unit position is the same than the node containing the chunk.
     * @return chunk world unit position.
     */
    public static Vector3f getChunkWorldPosition(Vector2Int chunkPosition) {
        return new Vector3f((chunkPosition.x * HexSetting.CHUNK_SIZE) * HexSetting.HEX_WIDTH, 0,
                (chunkPosition.y * HexSetting.CHUNK_SIZE) * (float) (HexSetting.HEX_RADIUS * 1.5));
    }

    /**
     * Convert local tile position (position inside a chunk) to global tile
     * position (inside the world grid).
     *
     * @param tilePos offset local tile position (position inside the chunk)
     * @param chunkPos position of the chunk
     * @return global tile position in offset.
     */
    public static Vector2Int getTileFromChunk(Vector2Int tilePos, Vector2Int chunkPos) {
        return new Vector2Int(chunkPos.x * HexSetting.CHUNK_SIZE + tilePos.x,
                chunkPos.y * HexSetting.CHUNK_SIZE + tilePos.y);
    }

    public void cleanup() {
        mapData.removeTileChangeListener(tileChangeListener);
    }
}

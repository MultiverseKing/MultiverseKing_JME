package org.hexgridapi.core;

import org.hexgridapi.core.mesh.MeshParameter;
import org.hexgridapi.core.control.AreaRangeControl;
import org.hexgridapi.core.control.ChunkControl;
import com.jme3.asset.AssetManager;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import org.hexgridapi.events.TileChangeListener;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import org.hexgridapi.events.TileChangeEvent;
import org.hexgridapi.utility.HexCoordinate;
import org.hexgridapi.utility.HexCoordinate.Coordinate;
import org.hexgridapi.utility.Vector2Int;

/**
 * @todo update for chunkChange listener
 * @author roah
 */
public class HexGridManager {

    /**
     * Parameter used to generate the grid mesh.
     */
    protected final MeshParameter meshParam;
    /**
     * Node containing all Tiles.
     */
    protected Node gridNode;
    protected Node tileSelectionNode;//= new Node("tileSelectionNode");
    protected boolean debugMode;
    protected HashMap chunksNodes = new HashMap<Vector2Int, Node>();
    protected Node areaRangeNode;
    protected AssetManager assetManager;
    protected MapData mapData;
    /**
     * Inner Class.
     */
    private final TileChangeListener tileChangeListener = new TileChangeListener() {
        public final void tileChange(TileChangeEvent... events) {
            if (events.length > 1) {
                HashSet<Vector2Int> updatedChunk = new HashSet<Vector2Int>();
                for (int i = 0; i < events.length; i++) {
                    Vector2Int pos = getChunkGridPosition(events[i].getTilePos());
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

    public HexGridManager(MapData mapData) {
        this(mapData, new Node("HexGridNode"), false);
    }

    public HexGridManager(MapData mapData, boolean debugMode) {
        this(mapData, new Node("HexGridNode"), debugMode);
    }

    public HexGridManager(MapData mapData, Node gridNode, boolean debugMode) {
        this.assetManager = mapData.getAssetManager();
        this.gridNode = gridNode;
        this.meshParam = new MeshParameter(mapData);
        this.debugMode = debugMode;
        this.mapData = mapData;
        mapData.registerTileChangeListener(tileChangeListener);
    }

    public MapData getMapData() {
        return mapData;
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
            Vector2Int chunkPos = getChunkGridPosition(event.getTilePos());
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
     * Return the tile position inside his corresponding chunk.
     *
     * @param tilePos tile hexMap position to convert.
     * @return tile chunk position or null.
     */
    public final HexCoordinate getTilePosInChunk(HexCoordinate tilePos) {
        Vector2Int chunk = getChunkGridPosition(tilePos);
        Vector2Int tileOffset = tilePos.getAsOffset();
        return new HexCoordinate(Coordinate.OFFSET,
                (int) (FastMath.abs(tileOffset.x) - FastMath.abs(chunk.x) * HexSetting.CHUNK_SIZE),
                (int) (FastMath.abs(tileOffset.y) - FastMath.abs(chunk.y) * HexSetting.CHUNK_SIZE));
    }

    /**
     * Return the chunk who hold the specifiated tile.
     *
     * @param tilePos hexMap coordinate of the tile.
     * @return Position of the chunk in mapData if exist, else null.
     */
    public static Vector2Int getChunkGridPosition(HexCoordinate tilePos) {
        Vector2Int tileOffset = tilePos.getAsOffset();
        int x = ((int) FastMath.abs(tileOffset.x) + (tileOffset.x < 0 ? -1 : 0)) / HexSetting.CHUNK_SIZE;
        int y = ((int) FastMath.abs(tileOffset.y) + (tileOffset.y < 0 ? -1 : 0)) / HexSetting.CHUNK_SIZE;
        Vector2Int result = new Vector2Int(tileOffset.x < 0 ? (x + 1) * -1 : x, tileOffset.y < 0 ? (y + 1) * -1 : y);
        return result;
    }

    /**
     * Convert chunk position in hexMap to world unit.
     *
     * @param position
     * @hint chunk world unit position is the same than the chunk node.
     * @return chunk world unit position.
     */
    public static Vector3f getChunkWorldPosition(Vector2Int position) {
        return new Vector3f((position.x * HexSetting.CHUNK_SIZE) * HexSetting.HEX_WIDTH, 0,
                (position.y * HexSetting.CHUNK_SIZE) * (float) (HexSetting.HEX_RADIUS * 1.5));
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

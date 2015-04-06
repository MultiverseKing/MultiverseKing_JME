package org.hexgridapi.core;

import com.jme3.app.Application;
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
import org.hexgridapi.core.control.GhostControl;
import org.hexgridapi.core.mapgenerator.ProceduralGenerator;
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
     * Node Containing all hex related data.
     */
    protected final Node gridNode = new Node("HexGridNode");
    /**
     * Node containing all Tiles.
     */
    protected final Node tileNode = new Node("HexTileNode");
    protected MapData mapData;
    private GhostControl ghostControl;
    protected HashMap chunksNodes = new HashMap<Vector2Int, Node>();
    protected Node areaRangeNode;
    protected ProceduralGenerator mapGenerator;
    protected AssetManager assetManager;

    public HexGrid(MapData mapData, AssetManager assetManager, Node rootNode) {
        this.assetManager = assetManager;
        this.meshParam = new MeshParameter(mapData);
        this.mapData = mapData;
        gridNode.attachChild(tileNode);
        rootNode.attachChild(gridNode);
//        tileNode.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        mapData.registerTileChangeListener(tileChangeListener);
    }

    protected void initialiseGhostGrid(Application app) {
        Node node = new Node("GhostNode");
        ghostControl = new GhostControl(app, meshParam, mapData.getMode(), new Vector2Int(), this);
        tileNode.attachChild(node);
        node.addControl(ghostControl);
    }
    
    /**
     * @return the node containing all the API Node. (include tileNode)
     */
    public final Node getGridNode() {
        return gridNode;
    }

    /**
     * @return the node containing all tile.
     */
    public final Node getTileNode() {
        return tileNode;
    }

    public final Set<Vector2Int> getChunksNodes() {
        return Collections.unmodifiableSet(chunksNodes.keySet());
    }

    /**
     * Unstable
     *
     * @see org.hexgridapi.core.MapData#setSeed(int)
     * @return
     */
    public int generateNewSeed() {
        int seed = ProceduralGenerator.generateSeed();
        mapData.setSeed(seed);
        return seed;
    }
    private final TileChangeListener tileChangeListener = new TileChangeListener() {
        public final void onTileChange(TileChangeEvent... events) {
            if (events.length > 1) {
                HashSet<Vector2Int> updatedChunk = new HashSet<Vector2Int>();
                for (int i = 0; i < events.length; i++) {
                    Vector2Int pos = events[i].getTilePos().getCorrespondingChunk();
                    if (updatedChunk.add(pos)) {
                        //updateChunk
                        updateChunk(events[i]);
//                        if (chunksNodes.containsKey(pos)) {
//                        } else {
//                            //add chunk
//                            addChunk(pos);
//                        }
                    }
                }
            } else {
                updateChunk(events[0]);
            }
        }
    };

    /**
     * Make change to tile according to the event.
     *
     * @param events contain information on the last tile event.
     */
    private void updateChunk(TileChangeEvent event) {
        Vector2Int chunkPos = event.getTilePos().getCorrespondingChunk();
        if (!chunksNodes.containsKey(chunkPos)) {
            addChunk(chunkPos);
            ghostControl.updateCulling();
//        } else if (!(event.getNewTile() == null && event.getOldTile() == null)) {
//            updateChunk(chunkPos);
        } else {
//            System.err.println("old && new tile is null, an error have occurs, this will be ignored.");
//            Used when forcing a tile to be ignored even by the procedural generator
            updateChunk(chunkPos);
        }
    }

    private void updateChunk(Vector2Int chunkPos) {
        ((Node) chunksNodes.get(chunkPos)).getControl(ChunkControl.class).update();
//        if (((Node) chunksNodes.get(chunkPos)).getControl(ChunkControl.class).isEmpty()) {
        if (!mapData.contain(chunkPos)) {
            removeChunk(chunkPos);
        } else {
            updatedChunk(((Node) chunksNodes.get(chunkPos)).getControl(ChunkControl.class));
        }
    }

    private void addChunk(Vector2Int chunkPos) {
        Node chunk = new Node(chunkPos.toString());
        chunksNodes.put(chunkPos, chunk);
        chunk.addControl(new ChunkControl(meshParam, assetManager, mapData.getMode(), chunkPos, false));
        chunk.setLocalTranslation(getChunkWorldPosition(chunkPos));
        tileNode.attachChild(chunk);
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
            AreaRangeControl control = new AreaRangeControl(meshParam, assetManager, mapData.getMode(), centerPosition, radius, color);
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
    public static Vector2Int getInitialChunkTile(Vector2Int chunkPos) {
        return new Vector2Int(chunkPos.x * HexSetting.CHUNK_SIZE,
                chunkPos.y * HexSetting.CHUNK_SIZE);
    }

    public void cleanup() {
        mapData.removeTileChangeListener(tileChangeListener);
    }
    
    public enum MatType {
        DEFAULT,
        TOON;
    }
}

package org.hexgridapi.base;

import com.jme3.asset.AssetManager;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import org.hexgridapi.events.ChunkChangeEvent;
import org.hexgridapi.events.ChunkChangeListener;
import org.hexgridapi.events.TileChangeEvent;
import org.hexgridapi.events.TileChangeListener;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import org.hexgridapi.utility.ArrowDebugShape;
import org.hexgridapi.utility.HexCoordinate;
import org.hexgridapi.utility.Vector2Int;

/**
 *
 * @author roah
 */
public class HexGridManager implements ChunkChangeListener, TileChangeListener {

    /**
     * Parameter used to generate the grid mesh.
     */
    private final MeshParameter meshParam;
    /**
     * Node containing all Tiles.
     */
    protected Node gridNode;
    protected boolean debugMode;
    protected HashMap chunksNodes = new HashMap<Vector2Int, Node>();
    protected Node areaRangeNode;
    protected AssetManager assetManager;
    protected String texturePath;

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
        if (debugMode) {
            this.debugMode = debugMode;
            ArrowDebugShape debugShape = new ArrowDebugShape(assetManager, gridNode, new HexCoordinate(HexCoordinate.OFFSET, 0, 0).convertToWorldPositionYAsFloor());
        }
    }

    public final Node getGridNode() {
        return gridNode;
    }

    public final Set<Vector2Int> getChunksNodes() {
        return Collections.unmodifiableSet(chunksNodes.keySet());
    }

    /**
     * Make change to chunk according to the event.
     *
     * @param event contain information of the last chunk event.
     */
    public final void chunkUpdate(ChunkChangeEvent event) {
        if (!event.delete() && event.getChunkPos() == null) {
            for (Iterator it = chunksNodes.values().iterator(); it.hasNext();) {
                Node chunk = (Node) it.next();
                chunk.getControl(ChunkControl.class).update();
            }
        } else if (event.delete() && event.getChunkPos() == null) {
            gridNode.detachAllChildren();
            chunksNodes.clear();
        } else {
            insertNewChunk(event.getChunkPos());
        }
    }

    private void insertNewChunk(Vector2Int pos) {
        Node chunk = new Node(pos.toString());
        chunksNodes.put(pos, chunk);
        chunk.setLocalTranslation(MapData.getChunkWorldPosition(pos));
        chunk.addControl(new ChunkControl(meshParam, assetManager, texturePath));
        gridNode.attachChild(chunk);
    }

    /**
     * Make change to tile according to the event.
     *
     * @param event contain information on the last tile event.
     */
    public final void tileChange(TileChangeEvent event) {
        if (event.getNewTile().getTextureKey() != event.getOldTile().getTextureKey()
                || event.getNewTile().getHeight() != event.getOldTile().getHeight()) {
            gridNode.getChild(event.getChunkPos().toString()).getControl(ChunkControl.class).update();
        }
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
        Vector3f pos = centerPosition.add(-radius+ ((centerPosition.getAsOffset().y&1) == 0 && (radius&1) != 0 ? -1 : 0), -radius).convertToWorldPosition();
        areaRangeNode.setLocalTranslation(pos.x, pos.y + 0.1f, pos.z);
    }
}

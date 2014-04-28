/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gamestate;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.material.Material;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.texture.TextureArray;
import hexsystem.HexTile;
import hexsystem.MapData;
import hexsystem.chunksystem.ChunkControl;
import hexsystem.chunksystem.MeshManager;
import hexsystem.events.ChunkChangeEvent;
import hexsystem.events.ChunkChangeListener;
import hexsystem.events.TileChangeEvent;
import hexsystem.events.TileChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import utility.Vector2Int;
import utility.attribut.ElementalAttribut;

/**
 *
 * @author roah
 */
public class HexMapAppState extends AbstractAppState implements ChunkChangeListener, TileChangeListener {

    private HashMap chunkNode = new HashMap<String, Node>();
    /**
     * Mesh generator.
     */
    protected final MeshManager meshManager;
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
    /**
     *
     */
    protected Material hexMat;

    /**
     * Settup the base param for the hexMap, create a new mapNode to hold the hexMap.
     * @param main
     * @param mapData
     */
    public HexMapAppState(SimpleApplication main, MapData mapData) {
        this.main = main;
        this.mapData = mapData;
        this.meshManager = new MeshManager(mapData.getHexSettings());
        mapNode = new Node("mapNode");
    }

    /**
     * Load the shader used by the hexMap to render all tile.
     * @todo AddAllChunks method should be cleaned, it didn't follow the main pattern.
     * @param stateManager
     * @param app
     */
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app); //To change body of generated methods, choose Tools | Templates.
        this.hexMat = new Material(main.getAssetManager(), "MatDefs/UnshadedArray.j3md");
        mapData.registerChunkChangeListener(this);
        mapData.registerTileChangeListener(this);
//        this.hexMat = main.getAssetManager().loadMaterial("Materials/newMaterial.j3m");
        main.getRootNode().attachChild(mapNode);
        mapNode.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        addAllElement();
        addAllChunks();
    }

    /**
     *
     */
    protected void addAllElement() {
        List<Image> hexImages = new ArrayList<Image>();
        for (ElementalAttribut e : ElementalAttribut.values()) {
            Texture text = (Texture) main.getAssetManager().loadTexture("Textures/Test/" + e.name() + "Center.png");
            hexImages.add(text.getImage());
        }
        TextureArray hexText = new TextureArray(hexImages);
        hexText.setWrap(Texture.WrapMode.Repeat);
        hexMat.setTexture("ColorMap", hexText);
        hexMat.getAdditionalRenderState().setAlphaTest(true);
    }

    /**
     * Make change to chunk according to the event.
     * @param event contain information of the last chunk event.
     */
    public void chunkUpdate(ChunkChangeEvent event) {
        if (!event.purge() && event.getChunkPos() == Vector2Int.INFINITY) {
            for (Iterator it = chunkNode.values().iterator(); it.hasNext();) {
                Node chunk = (Node) it.next();
                chunk.getControl(ChunkControl.class).updateChunk();
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
        chunk.addControl(new ChunkControl(mapData, meshManager, hexMat, mapData.getMapElement()));
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
            mapNode.getChild(event.getChunkPos().toString()).getControl(ChunkControl.class).updateTile(event.getTilePos());
        }

    }

    private void addAllChunks() {
        Set<Entry<Vector2Int, HexTile[][]>> chunks = mapData.getAllChunks();
        for (Entry<Vector2Int, HexTile[][]> e : chunks) {
            insertNewChunk(e.getKey());
        }
    }
}

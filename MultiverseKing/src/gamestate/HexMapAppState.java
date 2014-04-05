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
     *
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
     *
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
//        hexMat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        hexMat.getAdditionalRenderState().setAlphaTest(true);
//        hexMat.getAdditionalRenderState().setAlphaFallOff(0.1f);
//        hexMat.getAdditionalRenderState().setWireframe(true);
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
                chunk.getControl(ChunkControl.class).updateChunk(Vector2Int.INFINITY);
            }
        } else if (event.purge() && event.getChunkPos() == null) {
            mapNode.detachAllChildren();
            chunkNode.clear();
        } else {
            Node chunk = new Node(event.getChunkPos().toString());
            chunkNode.put(event.getChunkPos().toString(), chunk);
            chunk.setLocalTranslation(mapData.getChunkWorldPosition(event.getChunkPos()));
            chunk.addControl(new ChunkControl(mapData, meshManager, hexMat, mapData.getMapElement()));
            mapNode.attachChild(chunk);
        }
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
}

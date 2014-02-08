package hexsystem;

import hexsystem.events.TileChangeListener;
import hexsystem.events.TileChangeEvent;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.sun.org.apache.xalan.internal.xsltc.runtime.Hashtable;
import kingofmultiverse.MultiverseMain;
import utility.Vector2Int;
import utility.attribut.ElementalAttribut;

/**
 *
 * @author Eike Foede
 */
public class MapSpatialAppState extends AbstractAppState implements TileChangeListener {

    MultiverseMain main;
    private Node mapNode;
    private MapData mapData;
    private Geometry[][] tiles;
    Hashtable hexMat = new Hashtable();
    MeshManagerV3 meshManager;
    
    
    /**
     * To got a gap between Hex Change tileSize during initialize and set it to 1.
     */
    private float tileSize;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.main = (MultiverseMain) app;
        mapData = stateManager.getState(MapData.class);
        mapData.registerTileChangeListener(this);
        HexTile[][] tileData = mapData.getAllTiles();
        tiles = new Geometry[tileData.length][tileData[0].length];

        mapNode = new Node("MapNode");
        ((SimpleApplication) app).getRootNode().attachChild(mapNode);

        meshManager = new MeshManagerV3();
        Mesh hexagon = meshManager.generateChunk(new Vector2Int(10, 10));
        hexagon.setPointSize(4.0f);
//        hexagon.setMode(Mesh.Mode.Points);
        Geometry geom = new Geometry(25 + "|" + 25, hexagon);
        geom.setMaterial(hexMat.get(mapData.));
        mapNode.attachChild(geom);
//        geom.setCullHint(Spatial.CullHint.Never);
    }

    private void addMaterials() {
        Material mat = main.getAssetManager().loadMaterial("Materials/hexMat.j3m");
        Texture2D tex = (Texture2D) main.getAssetManager().loadTexture("Textures/Test/" + e.name() + "Center.png");
        tex.setWrap(Texture.WrapMode.Repeat);
//        g.getMaterial().getParam("Diffuse").setValue(tex);
//        mat.getAdditionalRenderState().setWireframe(true); //needed for debug on MeshManager
        mat.setTexture("ColorMap", tex);
        hexMat.put(e.name(), mat);
    }

    private Texture2D getTextureForTile(HexTile tile) {
        return materials[tile.getHexElement().ordinal()];
    }

    public void tileChange(TileChangeEvent event) {
        HexTile oldTile = event.getOldTile();
        HexTile newTile = event.getNewTile();
        if (newTile.getHexElement() != oldTile.getHexElement()) {
//            tiles[event.getX()][event.getY()].setMaterial(getMaterialForTile(event.getNewTile()));
//            tiles[event.getX()][event.getY()].setMesh(meshManager.getHeightedTile(event.getNewTile().getHeight()));
        } else if (newTile.getHeight() != oldTile.getHeight()) {
            //TODO: Regenerate mesh
            System.out.println("Work");
//            tiles[event.getX()][event.getY()].setMesh(meshManager.getHeightedTile(event.getNewTile().getHeight()));
        }
    }

    public Vector3f getSpatialPositionForTile(int x, int y) {
        float gridX = x * tileSize * 2 + (y % 2 == 1 ? tileSize : 0);
        float gridY = tileSize * FastMath.sqrt(3) * y;
        return new Vector3f(gridX, 0, gridY);
    }

    public Vector2Int getTilePositionForCoordinate(Vector3f coord) {
        int gridY = Math.round((coord.z / tileSize) / FastMath.sqrt(3));
        int gridX = Math.round((coord.x - (gridY % 2 == 1 ? tileSize : 0)) / 2 / tileSize);
        return new Vector2Int(gridX, gridY);
    }
}

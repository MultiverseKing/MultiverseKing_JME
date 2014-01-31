package hexsystem;

import hexsystem.events.TileChangeListener;
import hexsystem.events.TileChangeEvent;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import utility.Vector2Int;
import utility.attribut.ElementalAttribut;

/**
 *
 * @author Eike Foede
 */
public class MapSpatialAppState extends AbstractAppState implements TileChangeListener {

    private Node mapNode;
    private MapData mapData;
    private Geometry[][] tiles;
    Material[] materials;
    private float tileSize = 0.85f; //Set to 1 for gaps between tiles

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        setupMaterials(app.getAssetManager());
        mapData = stateManager.getState(MapData.class);
        mapData.registerTileChangeListener(this);
        HexTile[][] tileData = mapData.getAllTiles();
        tiles = new Geometry[tileData.length][tileData[0].length];

        mapNode = new Node("MapNode");
        ((SimpleApplication) app).getRootNode().attachChild(mapNode);


        //TODO: Create mesh in a nicer way
        MeshManager meshManager = new MeshManager();
        Mesh hexagon = meshManager.generateTile();
        for (int x = 0; x < tileData.length; x++) {
            for (int y = 0; y < tileData.length; y++) {

                Geometry geom = new Geometry(x + "|" + y, hexagon);
                //TODO: Set Position and scale correctly
//                geom.setLocalScale(tileSize);

                geom.setLocalTranslation(getSpatialPositionForTile(x, y));

                geom.setMaterial(getMaterialForTile(tileData[x][y]));
                tiles[x][y] = geom;
                mapNode.attachChild(geom);
            }
        }
    }

    private void setupMaterials(AssetManager assetManager) {
        materials = new Material[ElementalAttribut.getSize()];
        int i = 0;
        for (ElementalAttribut e : ElementalAttribut.values()) { //ElementalAttribut.SIZE do the same
            Material mat = assetManager.loadMaterial("Materials/hexMat.j3m");
            Texture2D tex = (Texture2D) assetManager.loadTexture("Textures/Test/" + e.name() + "Center.png");
            tex.setWrap(Texture.WrapMode.Repeat);
//            g.getMaterial().getParam("Diffuse").setValue(tex);
            mat.setTexture("ColorMap", tex);
            materials[i++] = mat;
        }
    }

    private Material getMaterialForTile(HexTile tile) {
        return materials[tile.getHexElement().ordinal()];
    }

    public void tileChange(TileChangeEvent event) {
        tiles[event.getX()][event.getY()].setMaterial(getMaterialForTile(event.getNewTile()));
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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hexsystem.chunksystem;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import hexsystem.HexSettings;
import hexsystem.MeshManager;
import utility.Vector2Int;
import utility.attribut.ElementalAttribut;

/**
 *
 * @author roah
 */
class ChunkSpatial {
    private static final int subChunkSize = 8; //a subchunk contain 16*16 tiles
    private final Geometry[][] geo;

    
    ChunkSpatial(HexSettings hexSettings, MeshManager meshManager, AssetManager assetManager, ElementalAttribut eAttribut, Node rootChunk) {
        geo = new Geometry[hexSettings.getCHUNK_SIZE()/subChunkSize][hexSettings.getCHUNK_SIZE()/subChunkSize];
        Mesh tileMesh = meshManager.generateMergedTile(new Vector2Int(subChunkSize, subChunkSize));
        Material material = getMaterial(assetManager, eAttribut);        
        
        for (int x = 0; x < hexSettings.getCHUNK_SIZE()/subChunkSize; x++) {
            for (int y = 0; y < hexSettings.getCHUNK_SIZE()/subChunkSize; y++) {
                geo[x][y] = new Geometry(x*subChunkSize+">"+(x+subChunkSize)+"|"+y*subChunkSize+">"+(y+subChunkSize), tileMesh);
                geo[x][y].setLocalTranslation(getSubChunkPosition(x, y, hexSettings));
                geo[x][y].setMaterial(material);
                rootChunk.attachChild(geo[x][y]);
            }
        }
    }

    public void setEnabled(boolean enabled) {
        CullHint culling = CullHint.Inherit;
        if(!enabled){
            culling = Spatial.CullHint.Always;
        }
        for (int x = 0; x < geo.length; x++) {
            for (int y = 0; y < geo[x].length; y++) {
                geo[x][y].setCullHint(culling);
            }
        }
    }
    //@todo update the spatial
    
    private static Vector3f getSubChunkPosition(int x, int y, HexSettings hexSettings) {
        float posX = (x*subChunkSize) * hexSettings.getHEX_WIDTH() + (hexSettings.getHEX_WIDTH()/2);
        float posY = 0;
        float posZ = (y*subChunkSize) * (float)(hexSettings.getHEX_RADIUS()*1.5);
        
        return new Vector3f(posX, posY, posZ);
    }

    private static Material getMaterial(AssetManager assetManager, ElementalAttribut eAttribut) {
        Material mat = assetManager.loadMaterial("Materials/hexMat.j3m");
        Texture2D text = (Texture2D) assetManager.loadTexture("Textures/Test/"+ eAttribut.name() +"Center.png");
        text.setWrap(Texture.WrapMode.Repeat);
        mat.setTexture("ColorMap", text);
//        mat.getAdditionalRenderState().setWireframe(true); //needed for debug on MeshManager
        return mat;
    }
}

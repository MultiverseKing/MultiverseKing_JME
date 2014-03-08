/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hexsystem.loader;

import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetLoader;
import com.jme3.asset.AssetLocator;
import com.jme3.asset.AssetManager;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;
import com.jme3.export.binary.BinaryImporter;
import hexsystem.HexTile;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import utility.Vector2Int;
import utility.attribut.ElementalAttribut;

/**
 *
 * @author roah
 */
public class ChunkDataLoader implements Savable, AssetLoader, AssetLocator {

    private String rootPath;
    private HexTile[][] tiles;
    private Vector2Int chunkPos;

    public ChunkDataLoader() {
    }

    public Vector2Int getChunkPos() {
        return chunkPos;
    }
    
    public HexTile[][] getTile(){
        return tiles;
    }
    
    public String getRootPath() {
        return rootPath;
    }

    public void setChunk(HexTile[][] tiles, Vector2Int chunkPos) {
        this.tiles = tiles;
        this.chunkPos = chunkPos;
    }

    public void write(JmeExporter ex) throws IOException {
        OutputCapsule capsule = ex.getCapsule(this);
        capsule.write(tiles, "tiles", null);
        capsule.write(chunkPos, "chunkPos", null);
    }

    public void read(JmeImporter im) throws IOException {
        InputCapsule capsule = im.getCapsule(this);
        tiles = (HexTile[][]) capsule.readSavableArray2D("tiles", null);
        chunkPos = (Vector2Int) capsule.readSavable("chunkPos", null);
    }

    public Object load(AssetInfo assetInfo) throws IOException {
        BinaryImporter importer = BinaryImporter.getInstance();
        return importer.load(assetInfo.openStream());
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    public AssetInfo locate(AssetManager manager, AssetKey key) {
        return new AssetInfo(manager, key) {
            @Override
            public InputStream openStream() {
                InputStream is = null;
                try {
                    is = new FileInputStream(new File(key.getName()));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                } finally {
                    try {
                        if (is != null) {
                            is.close();
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    return null;
                }
            }
        };
    }
}

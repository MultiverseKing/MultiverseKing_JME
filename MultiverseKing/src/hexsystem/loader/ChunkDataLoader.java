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

/**
 * 
 * @author roah
 */
public class ChunkDataLoader implements Savable, AssetLoader, AssetLocator {

    private String rootPath;
    private HexTile[][] tiles;

    public ChunkDataLoader() {
    }
    
    public HexTile[][] getTiles(){
        return tiles;
    }
    
    public String getRootPath() {
        return rootPath;
    }

    public void setChunk(HexTile[][] tiles) {
        this.tiles = tiles;
    }

    public void write(JmeExporter ex) throws IOException {
        OutputCapsule capsule = ex.getCapsule(this);
        capsule.write(tiles, "tiles", null);
    }

    public void read(JmeImporter im) throws IOException {
        InputCapsule capsule = im.getCapsule(this);
        Savable[][] t = capsule.readSavableArray2D("tiles", null);
        tiles = new HexTile[t.length][t.length];
        for(int y = 0; y < t.length; y++){
            for(int x = 0; x < t[y].length; x++){
                HexTile tile = (HexTile) t[x][y];
                tiles[x][y] = new HexTile(tile.getElement(), tile.getHeight());
            }
        }
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

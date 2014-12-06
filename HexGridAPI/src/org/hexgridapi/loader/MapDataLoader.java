package org.hexgridapi.loader;

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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import org.hexgridapi.utility.Vector2Int;

/**
 *
 * @author roah
 */
public class MapDataLoader implements Savable, AssetLoader, AssetLocator {

    private String rootPath;
    private ArrayList<Vector2Int> chunkPos = new ArrayList<Vector2Int>();
    
    public MapDataLoader() {
    }
    
    public void setChunkPos(ArrayList<Vector2Int> chunkPos) {
        this.chunkPos = chunkPos;
    }
    
    public ArrayList<Vector2Int> getChunkPos() {
        return chunkPos;
    }
    
    public void write(JmeExporter ex) throws IOException {
        OutputCapsule capsule = ex.getCapsule(this);
        capsule.writeSavableArrayList(chunkPos, "chunkPos", new ArrayList<Vector2Int>());
    }

    /**
     *
     * @param im
     * @throws IOException
     */
    public void read(JmeImporter im) throws IOException {
        InputCapsule capsule = im.getCapsule(this);
        chunkPos = capsule.readSavableArrayList("chunkPos", new ArrayList<Vector2Int>());
    }
    
    public Object load(AssetInfo assetInfo) throws IOException {
        BinaryImporter importer = BinaryImporter.getInstance();
        return importer.load(assetInfo.openStream());
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    /**
     *
     * @param manager
     * @param key
     * @return
     */
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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hexsystem;

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
import utility.Vector2Int;
import utility.attribut.ElementalAttribut;

/**
 *
 * @author roah
 */
public class MapDataLoader implements Savable, AssetLoader, AssetLocator {

    private ArrayList<HexTile[][]> tiles = new ArrayList<HexTile[][]>();
    private ArrayList<Vector2Int> chunkPos = new ArrayList<Vector2Int>();
    private String mapName;
    private ElementalAttribut mapElement;
    private String rootPath;

    public String getRootPath() {
        return rootPath;
    }

    public MapDataLoader() {
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String name) {
        this.mapName = name;
    }

    void addChunk(HexTile[][] tiles, Vector2Int chunkPos) {
        this.tiles.add(tiles);
        this.chunkPos.add(chunkPos);
    }

    public void write(JmeExporter ex) throws IOException {
        OutputCapsule capsule = ex.getCapsule(this);
        capsule.write(chunkPos.size(), "chunkCount", chunkPos.size());
        for (int i = 0; i < chunkPos.size(); i++) {
            capsule.write(tiles.get(i), "tiles" + i, null);
            capsule.write(chunkPos.get(i), "chunkPos" + i, null);
        }
        capsule.write(mapName, "mapName", "null");
        capsule.write(mapElement, "mapElement", ElementalAttribut.ICE);
    }

    public void read(JmeImporter im) throws IOException {
        InputCapsule capsule = im.getCapsule(this);
        int chunkCount = 0;
        capsule.readInt("chunkCount", chunkCount);
        for (int i = 0; i < chunkCount; i++) {
            tiles.add((HexTile[][]) capsule.readSavableArray2D("tiles" + i, null));
            chunkPos.add((Vector2Int) capsule.readSavable("chunkPos" + i, null));
        }
        mapName = capsule.readString("mapName", "null");
        mapElement = capsule.readEnum("mapElement", ElementalAttribut.class, ElementalAttribut.ICE);
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

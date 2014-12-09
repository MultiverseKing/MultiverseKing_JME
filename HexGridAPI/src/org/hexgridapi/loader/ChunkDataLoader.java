package org.hexgridapi.loader;

import org.hexgridapi.base.HexTile;
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

/**
 *
 * @author roah
 */
public class ChunkDataLoader implements Savable, AssetLoader, AssetLocator {

    private String rootPath;
    private HexTile[][] tiles;

    /**
     * internal use. (do not use)
     */
    public ChunkDataLoader() {
    }

    /**
     * Tile data loaded.
     *
     * @return
     */
    public HexTile[][] getTiles() {
        return tiles;
    }

    /**
     * path used to load data.
     *
     * @return
     */
    public String getRootPath() {
        return rootPath;
    }

    /**
     * tile belong to the chunk to save.
     *
     * @param tiles
     */
    public void setChunk(HexTile[][] tiles) {
        this.tiles = tiles;
    }

    /**
     * Convert this object data to a file.
     *
     * @param ex exporter to use.
     * @throws IOException
     */
    public void write(JmeExporter ex) throws IOException {
        OutputCapsule capsule = ex.getCapsule(this);
        capsule.write(tiles, "tiles", null);
    }

    /**
     * Convert data from file to this object.
     *
     * @param im importer to use.
     * @throws IOException
     */
    public void read(JmeImporter im) throws IOException {
        InputCapsule capsule = im.getCapsule(this);
        Savable[][] sTile = capsule.readSavableArray2D("tiles", null);
        tiles = new HexTile[sTile.length][sTile.length];
        for (int y = 0; y < sTile.length; y++) {
            for (int x = 0; x < sTile[y].length; x++) {
                HexTile tile = (HexTile) sTile[x][y];
                tiles[x][y] = new HexTile(tile.getHeight(), tile.getTextureKey());
            }
        }
    }

    /**
     * Load the file using the binary importer.
     *
     * @param assetInfo
     * @return
     * @throws IOException
     */
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
//                    e.printStackTrace();
                    return null;
                } catch (IOException e) {
//                    e.printStackTrace();
                    return null;
                } finally {
                    try {
                        if (is != null) {
                            is.close();
                        }
                    } catch (IOException ex) {
//                        ex.printStackTrace();
                    }
                    return null;
                }
            }
        };
    }
}

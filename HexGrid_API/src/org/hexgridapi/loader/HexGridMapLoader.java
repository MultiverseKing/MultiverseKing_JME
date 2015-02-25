/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hexgridapi.loader;

import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetManager;
import com.jme3.export.binary.BinaryExporter;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hexgridapi.core.MapData;
import org.hexgridapi.utility.Vector2Int;

/**
 *
 * @author roah
 */
public class HexGridMapLoader {
    private final AssetManager assetManager;

    public HexGridMapLoader(AssetManager assetManager) {
        this.assetManager = assetManager;
    }
    
    /**
     * Call/Update all registered Chunk listener with the last chunk event.
     *
     * @param cce Last chunk event.
     */
//    private void updateChunk(ChunkChangeEvent cce) {
//        for (ChunkChangeListener l : chunkListeners) {
//            l.chunkUpdate(cce);
//        }
//    }
    /**
     * Save the current map in a folder of the same name of the map.
     *
     *
     * @param mapName "RESET" && "TEMP" cannot be used for a map name since they
     * are already be used internaly.
     * @todo
     */
    public boolean saveArea(String mapName) {
        if (mapName == null || mapName.toUpperCase(Locale.ENGLISH).equalsIgnoreCase("TEMP")) {
            Logger.getLogger(MapData.class.getName()).log(Level.WARNING, "Invalid Path name");
            return false;
        }
//        this.mapName = mapName;
        try {
            if (saveChunk(null)) {
                String userHome = System.getProperty("user.dir") + "/assets";
                BinaryExporter exporter = BinaryExporter.getInstance();
                org.hexgridapi.loader.MapDataLoader mdLoader = new org.hexgridapi.loader.MapDataLoader();

//                mdLoader.setChunkPos(chunkPos);

                File file = new File(userHome + "/Data/MapData/" + mapName + "/" + mapName + ".map");
                exporter.save(mdLoader, file);
                return true;
            } else {
                return false;
            }
        } catch (IOException ex) {
            Logger.getLogger(MapData.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    /**
     * Save the selected chunk, two main use as :
     * - When saving the map
     * - When removing data from the memory.
     *
     * @param position the chunk to save.
     * @throws IOException
     * @todo
     */
    private boolean saveChunk(Vector2Int position) throws IOException {
        String userHome = System.getProperty("user.dir") + "/assets";

        BinaryExporter exporter = BinaryExporter.getInstance();
        ChunkDataLoader cdLoader = new ChunkDataLoader();

        if (position == null) {
//            for (Vector2Int pos : chunkPos) {
//                Path file = Paths.get(userHome + "/Data/MapData/" + mapName + "/" + pos.toString() + ".chk");
//                HexTile[][] tiles = getChunkTiles(pos);
//                if (tiles == null) {
//                    Path f = Paths.get(userHome + "/Data/MapData/Temp/" + pos.toString() + ".chk");
//                    if (f.toFile().exists() && !f.toFile().isDirectory()) {
//                        CopyOption[] options = new CopyOption[]{
//                            StandardCopyOption.REPLACE_EXISTING,
//                            StandardCopyOption.COPY_ATTRIBUTES
//                        };
//                        Files.copy(f, file, options);
//                    } else {
//                        Logger.getLogger(MapData.class.getName()).log(Level.WARNING,
//                                "userHome + \"/Data/MapData/\" + mapName + \"/\" + pos.toString() \n"
//                                + "                                + \".chk\" + \" can't be saved, data missing.\"");
//                        return false;
//                    }
//                } else {
//                    cdLoader.setChunk(tiles);
//                    exporter.save(cdLoader, file.toFile());
//                }
//            }
            return true;
        } else {
            File file = new File(userHome + "/Data/MapData/Temp/" + position.toString() + ".chk");
//            cdLoader.setChunk(getChunkTiles(position));
            exporter.save(cdLoader, file);
            return true;
        }
    }

    /**
     * Load a map and all the corresponding chunk and return true if done
     * properly, return false otherwise.
     *
     * @param name of the map to load.
     * @return false if not located
     * @todo
     */
    public boolean loadArea(String name) {
        File file = new File(System.getProperty("user.dir") + "/assets/Data/MapData/" + name + "/" + name + ".map");
        if (file.isDirectory() || !file.exists()) {
            Logger.getLogger(MapData.class.getName()).log(Level.WARNING, null, new IOException(name + " can't be found."));
            return false;
        }
        org.hexgridapi.loader.MapDataLoader mdLoader = (org.hexgridapi.loader.MapDataLoader) assetManager.loadAsset("/Data/MapData/" + name + "/" + name + ".map");
//        Cleanup();
//        mapName = name;
//        chunkPos = mdLoader.getChunkPos();
//        for (byte i = 0; i < chunkPos.size(); i++) {
//            loadChunk(chunkPos.get(i), mapName);
//            updateTile(new ChunkChangeEvent(chunkPos.get(i)));
//        }
        return true;
    }

    /**
     * load the chunk from a specifiate folder, internal use.
     *
     * @todo: All Tile data should only be loaded to create the map and not be
     * added into chunkData. (not keeping track of tile data in the memory)
     * @todo
     */
    private void loadChunk(Vector2Int position, String folder) {
        String chunkPath;
        if (folder == null) {
            chunkPath = "/Data/MapData/Temp/" + position.toString() + ".chk";
        } else {
            chunkPath = "/Data/MapData/" + folder + "/" + position.toString() + ".chk";
        }
        File file = new File(System.getProperty("user.dir") + "/assets" + chunkPath);
        if (file.exists() && !file.isDirectory()) {
            ChunkDataLoader cdLoaded = (ChunkDataLoader) assetManager.loadAsset(new AssetKey(chunkPath));
//            chunkData.add(position, cdLoaded.getTiles());
        } else {
            System.err.println(chunkPath + " can't be load, missing data.");
        }
    }

}

package org.multiversekingesapi.procedural;

import com.jme3.math.FastMath;
import java.util.HashMap;
import libnoiseforjava.NoiseGen;
import libnoiseforjava.exception.ExceptionInvalidParam;
import libnoiseforjava.module.Perlin;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hexgridapi.core.HexGrid;
import org.hexgridapi.core.HexSetting;
import org.hexgridapi.core.data.procedural.ProceduralChunkData;
import org.hexgridapi.core.data.procedural.ProceduralHexGrid;
import org.hexgridapi.utility.Vector2Int;

/**
 *
 * @author roah
 */
public final class ProceduralContent {
    /**
     * Module used for the generation.
     */
    private Perlin perlin = new Perlin();
    
    /**
     * 
     * @param proceduralHexGrid used for consistency.
     * @param octave 1 <==> 6
     * @param frequency 1 <=> 16
     * @param persistence 0.0 <==> 1.0
     */
    public ProceduralContent(ProceduralHexGrid proceduralHexGrid, int octave, double frequency, double persistence) {
        try {
            perlin.setOctaveCount(octave);
        } catch (ExceptionInvalidParam e){
            Logger.getLogger(getClass().getName()).log(Level.WARNING, null, e);
        }
        perlin.setFrequency(frequency);
        perlin.setPersistence(persistence);
        perlin.setNoiseQuality(NoiseGen.NoiseQuality.QUALITY_FAST);
        perlin.setSeed(proceduralHexGrid.getSeed());
    }

    /**
     * @return the currently used seed.
     */
    public int getSeed() {
        return perlin.getSeed();
    }

    /**
     * Generate a range of data following the current seed for the specifiated
     * param and chunk.
     * Do not use (0 & 1) as param value, these are used internaly.
     *
     * @param chunkPos where to generate the data for.
     * @param paramList any integer number (except 0 && 1)
     * @return all the data generated for the specifiated param and chunk
     * @deprecated 
     */
//    public ProceduralChunkData getChunkValue(Vector2Int chunkPos, int[] paramList) {
//        Vector2Int chunkInitTile = HexGrid.getInitialChunkTile(chunkPos).toOffset();
//        HashMap<Integer, HashMap<Vector2Int, Float>> data = new HashMap<>();
//
//        for (int paramValue : paramList) {
//            for (int x = 0; x < HexSetting.CHUNK_SIZE; x++) {
//                for (int y = 0; y < HexSetting.CHUNK_SIZE; y++) {
//                    if (!data.containsKey(paramValue)) {
//                        data.put(paramValue, new HashMap<Vector2Int, Float>());
//                    }
//                    data.get(paramValue).put( new Vector2Int(x, y), 
//                            getCustom(x + chunkInitTile.x, x + chunkInitTile.x, paramValue));
//                }
//            }
//        }
//        return new ProceduralChunkData(data);
//    }

    /**
     * @deprecated each project should use his own impl
     */
    private float getCustom(int x, int y, int param) {
        return (float) NoiseGen.MakeInt32Range(getTileValue(x, y, param));
    }

    /**
     *
     * @param param 0 == height <=> 1 == textureKey
     * @return
     */
    private double getTileValue(int posX, int posY, int param) {
        return FastMath.abs((float) perlin.getValue(posX * 0.1, posY * 0.1, param));
    }
}

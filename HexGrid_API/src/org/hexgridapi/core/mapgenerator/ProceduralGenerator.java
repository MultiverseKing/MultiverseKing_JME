package org.hexgridapi.core.mapgenerator;

import com.jme3.math.FastMath;
import java.util.Random;
import libnoiseforjava.NoiseGen;
import libnoiseforjava.module.Perlin;
import org.hexgridapi.core.HexGrid;
import org.hexgridapi.core.HexSetting;
import org.hexgridapi.core.HexTile;
import org.hexgridapi.utility.HexCoordinate;
import org.hexgridapi.utility.Vector2Int;

/**
 *
 * @author roah
 */
public final class ProceduralGenerator {

    private static Random GENERATOR = new Random(System.currentTimeMillis());

    public static int generateSeed() {
        return (int) (100000000 + GENERATOR.nextDouble() * 900000000);
    }
    /**
     * Used to know how many chunk to generate.
     */
    private Perlin perlin = new Perlin();
    /**
     * Used to know the minimum/maximum Heigth when generating the map.
     */
    private int heightMin = 3; // is always count as < 0 (ex: 2 mean -2)
    private int heightMax = 12; // is always count as > 0
    private int textureCount;

    public ProceduralGenerator(int seed, int textureCount) {
        this(seed, null, null, textureCount);
    }

    /**
     * /!\ if the seed change all value change be aware to take care of
     * updating.
     *
     * @param heightMin must be lesser or equal to 0
     * @param heightMax must be greater than 0
     */
    public ProceduralGenerator(int seed, Integer heightMin, Integer heightMax, int textureCount) {
        if (validateSeed(seed)) {
            if (heightMin != null) {
                this.heightMin = (int) (heightMin <= 0 ? FastMath.abs(heightMin) : heightMin);
            }
            if (heightMax != null && heightMax > 0) {
                this.heightMax = heightMax;
            }
            this.textureCount = textureCount;
//            perlin.setFrequency(2.0);
//            perlin.setPersistence(0.5);
            perlin.setNoiseQuality(NoiseGen.NoiseQuality.QUALITY_BEST);
            perlin.setSeed(seed);
        }
    }

    private boolean validateSeed(int seed) {
        String s = String.valueOf(seed);
        if (s.toCharArray().length == 9 && s.matches("\\d{9}")) {
            return true;
        }
        return false;
    }

    public int getSeed() {
        return perlin.getSeed();
    }

    public void setSeed(int seed) {
        if (validateSeed(seed)) {
            perlin.setSeed(seed);
        }
    }

    /**
     * @todo incomplete
     * @param posX
     * @param posY
     * @return
     */
    public ProceduralChunk getChunkValue(int posX, int posY) {
        Vector2Int chunkInitTile = HexGrid.getInitialChunkTile(new Vector2Int(posY, posY));
        ProceduralChunk chunk = new ProceduralChunk(HexSetting.CHUNK_SIZE);

        for (int x = 0; x < HexSetting.CHUNK_SIZE; x++) {
            for (int y = 0; y < HexSetting.CHUNK_SIZE; y++) {
                HexCoordinate tilePos = new HexCoordinate(
                        HexCoordinate.Coordinate.OFFSET, x + chunkInitTile.x, y + chunkInitTile.y);
                chunk.add(tilePos, getTileValue(tilePos));
            }
        }
        return chunk;
    }

    /**
     * @todo
     * @param tilePos
     * @return 
     */
    public HexTile getTileValue(HexCoordinate tilePos) {
        int height = getHeight(tilePos.getAsOffset().x, tilePos.getAsOffset().y);
        if(height <= 0){
            return null;
        }
        return new HexTile(height,0);
//                getTexture(tilePos.getAsOffset().x, tilePos.getAsOffset().y));
    }

    /**
     *
     * @param param 0 == height <=> 1 == textureKey
     * @return
     */
    private double getTileValue(int posX, int posY, int param) {
        return FastMath.abs((float) perlin.getValue(posX*0.01, posY*0.01, param));
    }

    /**
     * @todo wip
     * @param generatedValue
     * @return
     */
    private int getHeight(int x, int y) {
        return (int) NoiseGen.MakeInt32Range((getTileValue(x, y, 0) * (heightMax + heightMin)) - heightMin);
    }

    private int getTexture(int x, int y) {
        int result = (int) NoiseGen.MakeInt32Range((getTileValue(x, y, 1) * (textureCount + 1) - 1));
        if (result == -1) {
            result = -2;
        }
        return result;
    }
}

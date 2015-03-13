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
public final class MapGenerator {

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
    private int heightMin = -5;
    private int heightMax = 15;

    public MapGenerator(int seed) {
        this(seed, null, null);
    }

    /**
     * /!\ if the seed change all value change be aware to take care of
     * updating.
     *
     * @param heightMin must be lesser or equal to 0
     * @param heightMax must be greater than 0
     */
    public MapGenerator(int seed, Integer heightMin, Integer heightMax) {
        if (validateSeed(seed)) {
            if (heightMin != null && heightMin <= 0) {
                this.heightMin = heightMin;
            }
            if (heightMax != null && heightMax > 0) {
                this.heightMax = heightMax;
            }
            perlin.setNoiseQuality(NoiseGen.NoiseQuality.QUALITY_FAST);
            perlin.setSeed(seed);
        }
    }

    private boolean validateSeed(int seed) {
        String s = String.valueOf(seed);
        if(s.toCharArray().length == 9 && s.matches("\\d{9}")){
            System.out.println("seed validated");
            return true;
        }
        return false;
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
                chunk.add(tilePos, new HexTile(
                        convertToHeight(getTileValue(tilePos.getAsOffset().x, tilePos.getAsOffset().y))));
            }
        }
        return chunk;
    }

    private double getTileValue(int posX, int posY) {
        return perlin.getValue(posX, posY, 0);
    }

    /**
     * @todo incomplete
     * @param generatedValue
     * @return 
     */
    private int convertToHeight(double generatedValue) {
        return (int) (generatedValue * (heightMax + FastMath.abs(heightMin)));
    }
}

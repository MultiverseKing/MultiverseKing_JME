package org.hexgridapi.core.mapgenerator;

import java.util.Random;

/**
 *
 * @author roah
 */
public final class MapGenerator {
    
    private MapGenerator() {
        generateNewSeed();
    }


    private static class Holder {

        private static final MapGenerator instance = new MapGenerator();
    }

    public static MapGenerator getInstance() {
        return Holder.instance;
    }
    
    private static Random generator = new Random(System.currentTimeMillis());
    private static int mapSeed;
    
    public int generateNewSeed(){
        mapSeed = (int) (100000000 + generator.nextDouble() * 900000000);
        return mapSeed;
    }
    
    public int getCurrentSeed(){
        return mapSeed;
    }
    
    public void generateMap(int value) {
        System.err.println(value);
    }
}

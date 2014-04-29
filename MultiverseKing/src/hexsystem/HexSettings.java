/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hexsystem;

import com.jme3.math.FastMath;

/**
 * Chunk size should not be changed during runtime without changing the way the loader work.
 * @author roah
 */
public final class HexSettings {
    private final float HEX_RADIUS = 1;
    private final float HEX_WIDTH;
    private final int CHUNK_SIZE = 32; //must be power of two
    private final float FLOOR_HEIGHT = 1f;
    private final byte GROUND_HEIGHT = 0;
    private final byte CHUNK_DATA_LIMIT = 4;

    /**
     * Parameters to use when generating the map.
     * @param HEX_RADIUS 
     * @param CHUNK_SIZE Must be power of two.
     */
    public HexSettings() {
        HEX_WIDTH = FastMath.sqrt(3) * HEX_RADIUS;
    }

    public float getHEX_RADIUS() {
        return HEX_RADIUS;
    }

    public float getHEX_WIDTH() {
        return HEX_WIDTH;
    }

    /**
     * @return number of hex contain in a chunk.
     */
    public int getCHUNK_SIZE() {
        return CHUNK_SIZE;
    }

    /**
     * @return WU distance between two hex of different height.
     */
    public float getFloorHeight() {
        return FLOOR_HEIGHT;
    }
    
    /**
     * Offset value used to put the ground,
     * to have underground without having to handle negative value, mainly help with the meshManager and how geometry is handle, generated.
     * @return Ground Offset.
     */
    public byte getGROUND_HEIGHT() {
        return GROUND_HEIGHT;
    }
    
    /**
     * Used by the OldChunkData to know how many chunk to keep in memory before start deleting.
     * @return 
     */
    public byte getCHUNK_DATA_LIMIT() {
        return CHUNK_DATA_LIMIT;
    }
}

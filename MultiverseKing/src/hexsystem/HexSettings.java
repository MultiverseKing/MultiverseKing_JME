package hexsystem;

import com.jme3.math.FastMath;

/**
 * @todo Chunk size change during runtime.
 * @todo Map load from any chunk Size. (saved map can only work with the
 * chunkSize they have been created) loader work.
 *
 * @author roah
 */
public final class HexSettings {

    private final float HEX_RADIUS = 1;
    private final float HEX_WIDTH;
    private final int CHUNK_SIZE = 16; //must be power of two
    private final float FLOOR_HEIGHT = 1f;
    private final byte GROUND_HEIGHT = 10;
    private final byte CHUNK_DATA_LIMIT = 4;

    /**
     * Parameters to use when generating the map.
     *
     */
    public HexSettings() {
        HEX_WIDTH = FastMath.sqrt(3) * HEX_RADIUS;
    }

    /**
     *
     * @return
     */
    public float getHEX_RADIUS() {
        return HEX_RADIUS;
    }

    /**
     *
     * @return
     */
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
     * Offset value used to put the ground, to have underground without having
     * to handle negative value, mainly help with the meshManager and how
     * geometry is handle, generated.
     *
     * @return Ground Offset.
     */
    public byte getGROUND_HEIGHT() {
        return GROUND_HEIGHT;
    }

    /**
     * Used by the OldChunkData to know how many chunk to keep in memory before
     * start deleting.
     *
     * @return
     */
    public byte getCHUNK_DATA_LIMIT() {
        return CHUNK_DATA_LIMIT;
    }
}

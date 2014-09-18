package hexsystem;

import com.jme3.math.FastMath;

/**
 * Contain parameters used to generate the room grid.
 * @todo Chunk size change during runtime.
 * @todo Map load from any chunk Size. (saved map can only work with the
 * chunkSize they have been created) loader work.
 *
 * @author roah
 */
public final class HexSettings {

    /**
     * Parameters used when generating the map. Radius to use when generating
     * hex.
     */
    public static final float HEX_RADIUS = 1;
    /**
     * Parameters used when generating the map. Width of a generated hex.
     */
    public static final float HEX_WIDTH = FastMath.sqrt(3) * HEX_RADIUS;
    /**
     * Parameters used when generating the map. Number of hex contain in a
     * chunk.
     */
    public static final int CHUNK_SIZE = 16; //must be power of two
    /**
     * Parameters used when generating the map. WU distance between two hex of
     * different height.
     */
    public static final float FLOOR_OFFSET = 1f;
    /**
     * Parameters used when generating the map. Offset value used to put the
     * ground, to have underground without having to handle negative value,
     * mainly help with the meshManager and how geometry is handle, generated.
     */
    public static final byte GROUND_HEIGHT = 10;
    /**
     * Used to know how many chunk to keep in memory before start deleting.
     * Unused for the time being.
     */
    public static final byte CHUNK_DATA_LIMIT = 4;
}

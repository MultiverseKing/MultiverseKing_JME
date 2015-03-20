package org.hexgridapi.core;

import com.jme3.math.FastMath;

/**
 * Contain parameters used to generate the room grid.
 *
 * @todo Chunk size change during runtime.
 * @todo Map load from any chunk Size. (saved map can only work with the
 * chunkSize they have been created) loader work.
 *
 * @author roah
 */
public final class HexSetting {

    /**
     * Radius to use when generating hex.
     */
    public final static float HEX_RADIUS = 1;
    /**
     * Width of a generated hex.
     */
    public final static float HEX_WIDTH = FastMath.sqrt(3) * 1; //FastMath.sqrt(3) * HEX_RADIUS;
    /**
     * Number of hex contain in a chunk.
     */
    public final static int CHUNK_SIZE = 12; //must be multiple of two
    /**
     * The initial depth given to any generated chunk.
     */
    public final static int CHUNK_DEPTH = -5; //must lesser than 0
    /**
     * WU distance between two hex of different height.
     */
    public final static float FLOOR_OFFSET = 1f;
    /**
     * Offset value used to put the ground, to have underground without having
     * to handle negative value, mainly help with the meshManager and how
     * geometry is handle, generated.
     */
    public final static int GROUND_HEIGHT = 5;
    /**
     * Used to know how many chunk to keep in memory before purging it.
     * Unused for the time being.
     */
    public final static int CHUNK_DATA_LIMIT = 4;
}

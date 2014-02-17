/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hexsystem;

import com.jme3.math.FastMath;

/**
 *
 * @author roah
 */
public final class HexSettings {
    private final float HEX_RADIUS = 1;
    private final float HEX_WIDTH;
    private final int CHUNK_SIZE = 32; //must be power of two

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

    public int getCHUNK_SIZE() {
        return CHUNK_SIZE;
    }
}

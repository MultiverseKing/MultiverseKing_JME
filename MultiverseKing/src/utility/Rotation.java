package utility;

import com.jme3.math.Quaternion;

/**
 *
 * @author roah
 */
public enum Rotation {

    /**
     * Equivalent to X+.
     */
    A(new Quaternion(new Quaternion(new float[]{0, 1.5f, 0}))),
    /**
     * Equivalent to X+ Y+.
     */
    B(new Quaternion(new Quaternion(new float[]{0, .5f, 0}))),
    /**
     * Equivalent to X- Y+.
     */
    C(new Quaternion(new Quaternion(new float[]{0, .5f, 0}))),
    /**
     * Equivalent to X-.
     */
    D(new Quaternion(new Quaternion(new float[]{0, -1.5f, 0}))),
    /**
     * Equivalent to X- Y-.
     */
    E(new Quaternion(new Quaternion(new float[]{0, -2.5f, 0}))),
    /**
     * Equivalent to X+ Y-.
     */
    F(new Quaternion(new Quaternion(new float[]{0, 2.5f, 0})));

    private Quaternion rotation;

    private Rotation(Quaternion value) {
        this.rotation = value;
    }
    
    /**
     * Convert a direction as a Quaternion.
     *
     * @return Quaternion corresponding to that direction.
     */
    public Quaternion toQuaternion(){
        return rotation;
    }
}

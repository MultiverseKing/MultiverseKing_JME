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
    A,
    /**
     * Equivalent to X+ Y+.
     */
    B,
    /**
     * Equivalent to X- Y+.
     */
    C,
    /**
     * Equivalent to X-.
     */
    D,
    /**
     * Equivalent to X- Y-.
     */
    E,
    /**
     * Equivalent to X+ Y-.
     */
    F;

    /**
     * Convert a direction as a Quaternion.
     *
     * @param value Rotation to convert.
     * @return Quaternion corresponding to that direction.
     */
    public static Quaternion getQuaternion(Rotation value) {
        switch (value) {
            case A:
                return new Quaternion(new Quaternion(new float[]{0, 1.5f, 0}));
            case B:
                return new Quaternion(new Quaternion(new float[]{0, .5f, 0}));
            case C:
                return new Quaternion(new Quaternion(new float[]{0, -.5f, 0}));
            case D:
                return new Quaternion(new Quaternion(new float[]{0, -1.5f, 0}));
            case E:
                return new Quaternion(new Quaternion(new float[]{0, -2.5f, 0}));
            case F:
                return new Quaternion(new Quaternion(new float[]{0, 2.5f, 0}));
            default:
                return null;
        }
    }
}

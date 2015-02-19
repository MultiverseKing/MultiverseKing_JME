package org.hexgridapi.utility;

import com.jme3.math.Vector3f;

/**
 * We don't like to use 3 int or Vector3f for some variable, fully subjective
 * thing, nothing more.
 *
 * @author roah, Eike Foede
 */
public class Vector3Int {

    public static final Vector3Int ZERO = new Vector3Int(0, 0, 0);
    public int x;
    public int y;
    public int z;

    /**
     * Convert the vector2Int to string, formated as : x|y|z.
     *
     * @return string "x|y|z".
     */
    @Override
    public String toString() {
        return Integer.toString(this.x) + "|" + Integer.toString(this.y) + "|" + Integer.toString(this.z);
    }

    /**
     *
     */
    public Vector3Int() {
        this(0, 0, 0);
    }

    /**
     *
     * @param value
     */
    public Vector3Int(Vector3f value) {
        this((int) value.x, (int) value.y, (int) value.z);
    }

    /**
     *
     * @param x
     * @param y
     * @param z
     */
    public Vector3Int(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + this.x;
        hash = 97 * hash + this.y;
        hash = 97 * hash + this.z;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Vector3Int other = (Vector3Int) obj;
        if (this.x != other.x) {
            return false;
        }
        if (this.y != other.y) {
            return false;
        }
        if (this.z != other.z) {
            return false;
        }
        return true;
    }
}

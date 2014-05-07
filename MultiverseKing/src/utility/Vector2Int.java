package utility;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;
import com.jme3.math.Vector2f;
import java.io.IOException;

/**
 * I don't like to use 2 int or Vector2f for some variable, fully subjective
 * thing nothing more.
 *
 * @author roah
 */
public class Vector2Int implements Savable {

    /**
     *
     */
    public static final int X = 0;
    /**
     *
     */
    public static final int Y = 1;
    /**
     *
     */
    public static final Vector2Int ZERO = new Vector2Int(0, 0);
    /**
     *
     */
    public static final Vector2Int INFINITY = new Vector2Int(Integer.MAX_VALUE, Integer.MAX_VALUE);
    /**
     *
     */
    public static final Vector2Int NEG_INFINITY = new Vector2Int(Integer.MIN_VALUE, Integer.MIN_VALUE);
    /**
     *
     */
    public int x;
    /**
     *
     */
    public int y;

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + this.x;
        hash = 31 * hash + this.y;
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
        final Vector2Int other = (Vector2Int) obj;
        if (this.x != other.x) {
            return false;
        }
        if (this.y != other.y) {
            return false;
        }
        return true;
    }

    /**
     * Convert the vector2Int to string, formated as : x|y.
     *
     * @return string "x|y".
     */
    @Override
    public String toString() {
        return Integer.toString(this.x) + "|" + Integer.toString(this.y);
    }

    /**
     *
     */
    public Vector2Int() {
        this.x = 0;
        this.y = 0;
    }

    /**
     *
     * @param input
     */
    public Vector2Int(String input) {
        String[] strArray = input.split("\\|");
        this.x = Integer.parseInt(strArray[0]);
        this.y = Integer.parseInt(strArray[1]);
    }

    /**
     *
     * @param value
     */
    public Vector2Int(Vector2f value) {
        this((int) value.x, (int) value.y);
    }

    /**
     *
     * @param x
     * @param y
     */
    public Vector2Int(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     *
     * @param value
     */
    public Vector2Int(Vector2Int value) {
        this.x = value.x;
        this.y = value.y;
    }

    /**
     *
     * @param ex
     * @throws IOException
     */
    public void write(JmeExporter ex) throws IOException {
        OutputCapsule capsule = ex.getCapsule(this);
        capsule.write(this.x, "x", x);
        capsule.write(this.y, "y", y);
    }

    /**
     *
     * @param im
     * @throws IOException
     */
    public void read(JmeImporter im) throws IOException {
        InputCapsule capsule = im.getCapsule(this);
        capsule.readInt("x", this.x);
        capsule.readInt("y", this.y);
    }

    /**
     *
     * @param i
     * @return
     */
    public Vector2Int multiply(int i) {
        return new Vector2Int(x * i, y * i);
    }

    /**
     *
     * @param value
     * @return
     */
    public Vector2Int add(Vector2Int value) {
        return new Vector2Int(x + value.x, y + value.y);
    }
}

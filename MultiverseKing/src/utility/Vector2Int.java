/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
 * @todo implement equals && hashCode Override
 */
public class Vector2Int implements Savable {

    public static final Vector2Int ZERO = new Vector2Int(0, 0);
    public static final Vector2Int INFINITY = new Vector2Int(Integer.MAX_VALUE, Integer.MAX_VALUE);
    public static final Vector2Int NEG_INFINITY = new Vector2Int(Integer.MAX_VALUE, Integer.MAX_VALUE);
    public int x;
    public int y;

    /**
     * Convert the vector2Int to string, formated as : x|y.
     *
     * @return string "x|y".
     */
    @Override
    public String toString() {
        return Integer.toString(this.x) + "|" + Integer.toString(this.y);
    }

    public Vector2Int(String input) {
        String[] strArray = input.split("\\|");
        this.x = Integer.parseInt(strArray[0]);
        this.y = Integer.parseInt(strArray[1]);
    }

    public Vector2Int(Vector2f value) {
        this((int) value.x, (int) value.y);
    }

    public Vector2Int(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public Vector2Int(Vector2Int value){
        this.x = value.x;
        this.y = value.y;
    }

    public void write(JmeExporter ex) throws IOException {
        OutputCapsule capsule = ex.getCapsule(this);
        capsule.write(this.x, "x", x);
        capsule.write(this.y, "y", y);
    }

    public void read(JmeImporter im) throws IOException {
        InputCapsule capsule = im.getCapsule(this);
        capsule.readInt("x", this.x);
        capsule.readInt("y", this.y);
    }

    public Vector2Int multiply(int i) {
        return new Vector2Int(x*i, y*i);
    }
}

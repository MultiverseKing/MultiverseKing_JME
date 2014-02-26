/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utility;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

/**
 * I don't like to use 2 int or Vector2f for some variable, fully subjective thing nothing more.
 * @author roah
 * @todo implement equals && hashCode Override 
 */
public class Vector3Int {
    public static final Vector3Int ZERO = new Vector3Int(0,0,0);
    public int x;
    public int y;
    public int z;

    /**
     * Convert the vector2Int to string, formated as : x|y.
     * @return string "x|y".
     */
    @Override
    public String toString() {
        return Integer.toString(this.x)+"|"+Integer.toString(this.y);
    }
    
    public Vector3Int(){
        this(0,0,0);
    }

    public Vector3Int(Vector3f value){
        this((int)value.x, (int)value.y, (int)value.z);
    }

    public Vector3Int(int x, int y, int z){
        this.x = x;
        this.y = y;
        this.z = z;
    }
}

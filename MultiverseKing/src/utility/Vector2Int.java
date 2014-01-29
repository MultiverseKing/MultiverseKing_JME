/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utility;

import com.jme3.math.Vector2f;

/**
 *
 * @author roah
 */
public class Vector2Int {
    public int x;
    public int y;

    public Vector2Int(){
        this(0,0);
    }
    
    public Vector2Int(Vector2f value){
        this((int)value.x, (int)value.y);
    }
    
    public Vector2Int(int x, int y){
        this.x = x;
        this.y = y;
    }
}

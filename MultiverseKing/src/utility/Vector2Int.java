/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utility;

import com.jme3.math.Vector2f;

/**
 * I don't like to use 2 int or Vector2f for some variable, fully subjective thing nothing more.
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

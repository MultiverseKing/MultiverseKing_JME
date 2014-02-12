/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utility;

import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author Roah with the help of :
 * ArtemisArt => http://artemis.art.free.fr/ 
 * && http://www.redblobgames.com
 * 
 * This Class is only used as a converter system so we can simplifie algorithm.
 */
public final class Coordinate {
    
    public final class Cube {
        /**
         * Cube position in Grid.
         */
        public final int x;
        public final int y;
        public final int z;
        
        public Cube (int x, int y, int z){
            this.x = x;
            this.y = y;
            this.z = z;
        }
        // convert cube to axial
        public Axial toAxial(){
            return new Axial(x, z);
        }
        // convert cube to odd-r offset
        public Offset toOffset(){            
            return new Offset(x + (z - (z&1)) / 2, z);
        }
    }
    
    public final class Offset {
        /**
         * Offset position in odd-r Grid. Currently generated Grid.
         * q == x
         * r == z
         */
        public final int q;
        public final int r;
        /**
         * @param q x position of the tile in hexMap in Offset coordinate.
         * @param r z position of the tile in hexMap in Offset coordinate.
         */
        public Offset(int q, int r) {
            this.q = q;
            this.r = r;
        }
        // convert even-r offset to cube
        public Cube toCube(){
            return new Cube(q - (r - (r&1)) / 2, -(q - (r - (r&1)) / 2)-r, r);
        }
        public Axial toAxial() {
            return new Axial(q - (r - (r&1)) / 2, r);
        }
    }

    public final class Axial {
        /**
         * Axial position in Grid.
         * q == x
         * r == z
         */
        public final int q;
        public final int r;
        /**
         * @param q x position of the tile in hexMap in Axial coordinate.
         * @param r z position of the tile in hexMap in Axial coordinate.
         */
        public Axial(int q, int r) {
            this.q = q;
            this.r = r;
        }
        
        // convert axial to cube
        public Cube toCube(){
            return new Cube(q, -q-r, r);
        }
        public Offset toOffset (){
            return new Offset (q + (r - (r & 1)) / 2, r);
        }
    }
}

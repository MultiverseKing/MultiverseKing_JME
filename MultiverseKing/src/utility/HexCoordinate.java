/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utility;

import com.jme3.math.Vector2f;

/**
 *
 * @author Roah with the help of : ArtemisArt => http://artemis.art.free.fr/ &&
 * http://www.redblobgames.com
 *
 * This Class is only used as a converter system so we can simplifie algorithm.
 */
public final class HexCoordinate {

    public final class Cube {

        public final int x;
        public final int y;
        public final int z;

        /**
         * Create new Cube coordinate.
         */
        public Cube(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
        
        /**
         * Convert Cube coordinate to Axial coordinate.
         * @return Axial coordinate.
         */
        public Axial toAxial() {
            return new Axial(x, z);
        }
        
        /**
         * Convert Cube coordinate to Odd-R Offset coordinate.
         * @return Odd-R Offset coordinate.
         */
        public Offset toOffset() {
            return new Offset(x + (z - (z & 1)) / 2, z);
        }

        @Override
        public String toString() {
            return x+"|"+y+"|"+z;
        }
        
    }

    public final class Offset {


        /**
         * Offset position in odd-r Grid. Currently generated Grid. q == x
         */
        public final int q;
        /**
         * Offset position in Odd-R Grid. Currently generated Grid. r == z (or Y)
         */
        public final int r;

        /**
         * Create new Odd-R Offset grid position, Currently generated Grid.
         * @param q = x position of the tile in hexMap in Offset coordinate.
         * @param r = z (or y) position of the tile in hexMap in Offset coordinate.
         */
        public Offset(int q, int r) {
            this.q = q;
            this.r = r;
        }

        public Offset(Vector2Int value) {
            this(value.x, value.y);
        }

        /**
         * Convert Odd-R Offset to Cube coordinate.
         * @return Cube coordinate.
         */
        public Cube toCube() {
            return new Cube(q - (r - (r & 1)) / 2, -(q - (r - (r & 1)) / 2) - r, r);
        }

        /**
         * Convert Odd-R Offset to Axial coordinate.
         * @return Axial coordinate.
         */
        public Axial toAxial() {
            return new Axial(q - (r - (r & 1)) / 2, r);
        }
        
        @Override
        public String toString() {
            return q+"|"+r;
        }

        public Vector2Int toVector2Int() {
            return new Vector2Int(this.q, this.r);
        }

        public boolean equalsCoord(Offset tilePos) {
            if(tilePos.q == this.q && tilePos.r == this.r){
                return true;
            } else {
                return false;
            }
        }
    }

    public final class Axial {

        /**
         * Axial position in Grid. q == x
         */
        public final int q;
        /**
         * Axial position in Grid. r == z (or Y)
         */
        public final int r;

        /**
         * Create new Axial coordinate.
         * @param q x position of the tile in hexMap in Axial coordinate.
         * @param r z position of the tile in hexMap in Axial coordinate.
         */
        public Axial(int q, int r) {
            this.q = q;
            this.r = r;
        }

        /**
         * Convert Axial coordinate to Cube coordinate.
         * @return Axial coordinate.
         */
        public Cube toCube() {
            return new Cube(q, -q - r, r);
        }

        /**
         * Convert Axial coordinate to Odd-R Offset coordinate.
         * @return Odd-R Offset coordinate.
         */
        public Offset toOffset() {
            return new Offset(q + (r - (r & 1)) / 2, r);
        }
        
        /**
         * @return faf
         */
        @Override
        public String toString() {
            return q+"|"+r;
        }
    }
}

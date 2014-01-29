/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utility;

/**
 *
 * @author Perier Emmanuel with the help of :
 * Mathis Chenuet => http://artemis.art.free.fr/ 
 * && http://www.redblobgames.com
 * 
 * This Class is only used as a converter system so we can simplifie algorith
 * we use.
 */
public final class Coordinate {
        public final class Offset {
            /**
             * Offset position of the Tile inside hexTileGroup.
             * q == x
             * r == z
             */
            public final int q, r;
            /**
             * @param q x position of the tile in hexMap in Offset coordinate.
             * @param r z position of the tile in hexMap in Offset coordinate.
             */
            public Offset(int q, int r) {
                this.q = q;
                this.r = r;
            }
            
            public Axial toAxial() {
                return new Axial (q - (r -(r & 1)) / 2, r);
            }
        }
        
        public final class Axial {
            /**
             * Axial position of the Tile inside hexTileGroup.
             * q == x
             * r == z
             */
            public final int q, r;
            /**
             * @param q x position of the tile in hexMap in Axial coordinate.
             * @param r z position of the tile in hexMap in Axial coordinate.
             */
            public Axial(int q, int r) {
                this.q = q;
                this.r = r;
            }
            public Offset toOffset() {
                return new Offset (q + (r -(r & 1)) / 2, r);
            }
        }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hexsystem;

import hexsystem.HexMapManager;
import com.jme3.scene.Spatial;

/**
 *
 * @author roah
 */
class HexCursor {
    private Spatial cursor;

    public Spatial getCursor() {return this.cursor;}
        
    HexCursor(HexMapManager hexMapManager) {
//        cursor = hexMapManager.getChunkManager().generateHexCursor(new Integer(1), new Integer(1));
//        cursor.setLocalTranslation(0f, 0.5f, 0f);                
    }
}
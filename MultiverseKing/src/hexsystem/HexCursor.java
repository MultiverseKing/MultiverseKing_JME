/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hexsystem;

import com.jme3.scene.Spatial;

/**
 * @todo Still not functional but called on another script so it will be there for some time.
 * @todo Making the cursor animated, using particule system or SimpleSprite Shader from shaderBlow could do it properly.
 * @author roah
 */
class HexCursor {
    private Spatial cursor;

    public Spatial getCursor() {return this.cursor;}
        
    HexCursor(HexMap hexMapManager) {
//        cursor = hexMapManager.getChunkManager().generateHexCursor(new Integer(1), new Integer(1));
//        cursor.setLocalTranslation(0f, 0.5f, 0f);                
    }
}
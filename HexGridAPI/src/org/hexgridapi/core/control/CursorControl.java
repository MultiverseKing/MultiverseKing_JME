package org.hexgridapi.core.control;

import com.jme3.app.Application;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import org.hexgridapi.core.HexSetting;
import org.hexgridapi.utility.HexCoordinate;

/**
 *
 * @author roah
 */
public class CursorControl {

    private Spatial cursor;
    private final float cursorOffset = -0.15f;           //Got an offset issue with hex_void_anim.png this will solve it temporary

    public CursorControl(Application app) {
        cursor = app.getAssetManager().loadModel("Models/animPlane.j3o");
        Material animShader = app.getAssetManager().loadMaterial("Materials/animatedTexture.j3m");
        animShader.setInt("Speed", 16);
        cursor.setMaterial(animShader);
        ((Node) app.getViewPort().getScenes().get(0)).attachChild(cursor);
        cursor.setCullHint(Spatial.CullHint.Always);
    }

    public void setHeight(int height) {
        cursor.setLocalTranslation(cursor.getLocalTranslation().x, height * HexSetting.FLOOR_OFFSET + 0.1f, cursor.getLocalTranslation().z);
    }

    public void setPosition(HexCoordinate tilePos, int height) {
//        if(enable <= 0){
        initCursor();
        Vector3f pos = tilePos.toWorldPosition();
        //        cursor.setLocalTranslation(pos.x, (tile != null ? tile.getHeight() * HexSetting.FLOOR_OFFSET : HexSetting.GROUND_HEIGHT * HexSetting.FLOOR_OFFSET)
        //                + ((tilePos.toOffset().y & 1) == 0 ? 0.01f : 0.02f), pos.z + cursorOffset);
        cursor.setLocalTranslation(pos.x, height * HexSetting.FLOOR_OFFSET
                + ((tilePos.toOffset().y & 1) == 0 ? 0.01f : 0.02f), pos.z + cursorOffset);
        /**
         * The cursor real position is not updated on pulseMode.
         */
//        if (listenerPulseIndex == -1) {
//            lastHexPos = tilePos;
//        }
//        } else {
//            enable--;
//        }
    }

    public boolean clear() {
        return cursor.removeFromParent();
        //Remove offset and set it to zero if hex_void_anim.png is not used
//        float z = mapData.getTile(new HexCoordinate(HexCoordinate.OFFSET, 0, 0)).getHeight() * HexSettings.FLOOR_OFFSET + 0.01f;
//        cursor.setLocalTranslation(new Vector3f(0f, z + 0.01f, cursorOffset));
    }

    private void initCursor() {
        cursor.setCullHint(Spatial.CullHint.Inherit);
    }
}

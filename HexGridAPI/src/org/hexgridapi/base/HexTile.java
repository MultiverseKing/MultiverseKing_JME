package org.hexgridapi.base;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;
import java.io.IOException;

/**
 * tile data used in room grid.
 *
 * @author Eike Foede, Roah
 */
public class HexTile implements Savable {

    protected byte height;
    protected byte textureKey;
    
    public HexTile() {
        this.height = 0;
        this.textureKey = 0;
    }

    public HexTile(byte height) {
        this.height = height;
        this.textureKey = 0;
    }

    public HexTile(byte height, byte textureKey) {
        this.height = height;
        this.textureKey = textureKey;
    }
    
    public byte getHeight() {
        return height;
    }

    public byte getTextureKey() {
        return textureKey;
    }
    
    public void write(JmeExporter ex) throws IOException {
        OutputCapsule capsule = ex.getCapsule(this);
        capsule.write(height, "height", 0);
        capsule.write(textureKey, "textureKey", 0);
    }

    protected void extendedWrite(OutputCapsule capsule) throws IOException {
    }
    
    public void read(JmeImporter im) throws IOException {
        InputCapsule capsule = im.getCapsule(this);
//        capsule.readByte("height", height);
//        capsule.readByte("element", element);
        height = (byte) capsule.readByte("height", (byte)0);
        textureKey = (byte) capsule.readByte("textureKey", (byte)0);
    }

    protected void extendedread(InputCapsule capsule) throws IOException {
    }

    /**
     * Returns a clone of this tile with changed height param.
     */
    public HexTile cloneChangedHeight(byte height) {
        return new HexTile(height, textureKey);
    }
    /**
     * Returns a clone of this tile with changed texture param.
     */
    public HexTile cloneChangedTextureKey(byte textureKey) {
        return new HexTile(height, textureKey);
    }
}

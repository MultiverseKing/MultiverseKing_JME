package hexsystem;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;
import java.io.IOException;
import utility.attribut.ElementalAttribut;

/**
 *
 * @author Eike Foede, Roah
 */
public class HexTile implements Savable {

    private byte element;
    private int height;
    private boolean walkable;

    public HexTile() {
    }

    public HexTile(ElementalAttribut eAttribut) {
        this.element = (byte) eAttribut.ordinal();
        this.height = 0;
        this.walkable = true;
    }

    public HexTile(ElementalAttribut hexElement, int height) {
        this.element = (byte) hexElement.ordinal();
        this.height = height;
        this.walkable = true;
    }

    public HexTile(ElementalAttribut hexElement, int height, boolean walkable) {
        this.element = (byte) hexElement.ordinal();
        this.height = height;
        this.walkable = walkable;
    }

    public ElementalAttribut getElement() {
        return ElementalAttribut.convert(element);
    }

    public int getHeight() {
        return height;
    }

    public boolean getWalkable() {
        return walkable;
    }

    public void write(JmeExporter ex) throws IOException {
        OutputCapsule capsule = ex.getCapsule(this);
        capsule.write(height, "height", 0);
        capsule.write(element, "element", 0);
        capsule.write(walkable, "walkable", true);
//        System.out.println(height + " "+element);
    }

    public void read(JmeImporter im) throws IOException {
        InputCapsule capsule = im.getCapsule(this);
//        capsule.readByte("height", height);
//        capsule.readByte("element", element);
        height = (byte) capsule.readInt("height", 0);
        element = (byte) capsule.readInt("element", ElementalAttribut.NULL.ordinal());
        walkable = (boolean) capsule.readBoolean("walkable", true);
    }

    /**
     * Returns a clone of this tile with changed Element param.
     *
     * @param element ElementalAttribut
     * @return 
     */
    public HexTile cloneChangedElement(ElementalAttribut element) {
        return new HexTile(element, height, walkable);
    }

    /**
     * Returns a clone of this tile with changed height param.
     *
     * @param height
     * @return
     */
    public HexTile cloneChangedHeight(int height) {
        return new HexTile(ElementalAttribut.convert(element), height, walkable);
    }
    
    /**
     * Returns a clone of this tile with changed walkable param.
     *
     * @param walkable
     * @return
     */
    public HexTile cloneChangedWalkable(boolean walkable) {
        return new HexTile(ElementalAttribut.convert(element), height, walkable);
    }
}

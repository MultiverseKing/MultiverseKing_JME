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

    public HexTile(){
    }
    
    public HexTile(ElementalAttribut eAttribut) {
        this.element = (byte) eAttribut.ordinal();
        this.height = 0;
    }

    public HexTile(ElementalAttribut hexElement, int height) {
        this.element = (byte) hexElement.ordinal();
        this.height = height;
    }

    public ElementalAttribut getElement() {
        return ElementalAttribut.convert(element);
    }

    public int getHeight() {
        return height;
    }



    public void write(JmeExporter ex) throws IOException {
        OutputCapsule capsule = ex.getCapsule(this);
        capsule.write(height, "height", 0);
        capsule.write(element, "element", 0);
//        System.out.println(height + " "+element);
    }

    public void read(JmeImporter im) throws IOException {
        InputCapsule capsule = im.getCapsule(this);
//        capsule.readByte("height", height);
//        capsule.readByte("element", element);
        height = (byte) capsule.readInt("height", height);
        element = (byte) capsule.readInt("element", element);
//        System.out.println(height + " "+element);
    }

    /**
     * Returns a clone of this tile with changed Element
     *
     * @param element
     * @return
     */
    public HexTile cloneChangedElement(ElementalAttribut element) {
        return new HexTile(element, (height));
    }
        /**
     * Returns a clone of this tile with changed height
     *
     * @param height
     * @return
     */
    public HexTile cloneChangedHeight(int height) {
        return new HexTile(ElementalAttribut.convert(element),(height));
    }
}

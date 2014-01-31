package hexsystem;

import utility.Coordinate;
import utility.attribut.ElementalAttribut;

/**
 *
 * @author Eike Foede
 */
public class HexTile {

    private final ElementalAttribut hexElement;
    private final float height;

    public HexTile(ElementalAttribut eAttribut) {
        this.hexElement = eAttribut;
        this.height = 0;
    }

    public HexTile(ElementalAttribut hexElement, float height) {
        this.hexElement = hexElement;
        this.height = height;
    }

    public ElementalAttribut getHexElement() {
        return hexElement;
    }

    public float getHeight() {
        return height;
    }

    /**
     * Returns a clone of this tile with changed height
     *
     * @param height
     * @return
     */
    public HexTile cloneChangedHeight(float height) {
        return new HexTile(hexElement, height);
    }
}

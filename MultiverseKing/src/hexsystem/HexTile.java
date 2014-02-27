package hexsystem;

import utility.attribut.ElementalAttribut;

/**
 *
 * @author Eike Foede, Roah
 */
public class HexTile {

    private final byte hexElement;
    private final byte height;

    public HexTile(ElementalAttribut eAttribut) {
        this.hexElement = (byte) eAttribut.ordinal();
        this.height = 0;
    }

    public HexTile(ElementalAttribut hexElement, byte height) {
        this.hexElement = (byte)hexElement.ordinal();
        this.height = height;
    }

    public ElementalAttribut getHexElement() {
        return ElementalAttribut.convert(hexElement);
    }

    public int getHeight() {
        return height;
    }

    /**
     * Returns a clone of this tile with changed height
     *
     * @param height
     * @return
     */
    public HexTile cloneChangedHeight(byte height) {
        return new HexTile(ElementalAttribut.convert(hexElement), height);
    }
}

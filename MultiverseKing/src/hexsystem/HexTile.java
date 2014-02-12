package hexsystem;

import utility.attribut.ElementalAttribut;

/**
 *
 * @author Eike Foede, Roah
 */
public class HexTile {

    private ElementalAttribut hexElement;
    private int height;

    public HexTile(ElementalAttribut eAttribut) {
        this.hexElement = eAttribut;
        this.height = 0;
    }

    public HexTile(ElementalAttribut hexElement, int height) {
        this.hexElement = hexElement;
        this.height = height;
    }

    public ElementalAttribut getHexElement() {
        return hexElement;
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
    public HexTile cloneChangedHeight(int height) {
        return new HexTile(hexElement, height);
    }
}

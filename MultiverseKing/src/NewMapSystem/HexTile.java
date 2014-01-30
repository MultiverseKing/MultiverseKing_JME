package NewMapSystem;

import utility.Coordinate;
import utility.attribut.ElementalAttribut;

/**
 *
 * @author Eike Foede
 */
public class HexTile {

    private ElementalAttribut hexElement;

    public HexTile(ElementalAttribut eAttribut) {
        this.hexElement = eAttribut;
    }

    public ElementalAttribut getHexElement() {
        return hexElement;
    }
    
}

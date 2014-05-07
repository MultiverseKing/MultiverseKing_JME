/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utility.attribut;

/**
 *
 * @author roah
 */
public enum ElementalAttribut {

    EARTH,
    NATURE,
    ICE,
    SHOCK,
    NULL;
    private static final byte SIZE = (byte) ElementalAttribut.values().length;

    public static int getSize() {
        return SIZE;
    }

    /**
     * Convert a number to ElementalAttribut, 
     * if there is no convertion for the input number, the ElementalAttribut will be set to NULL,
     * default == ElementalAttribut.NULL;
     * int 0 == ElementalAttribut.NULL;
     * @param x number to convert.
     * @return converted int.
     */
    public static ElementalAttribut convert(int x) {
        ElementalAttribut result = null;
        switch (x) {
            case 0:
                result = ElementalAttribut.EARTH;
                break;
            case 1:
                result = ElementalAttribut.NATURE;
                break;
            case 2:
                result = ElementalAttribut.ICE;
                break;
            case 3:
                result = ElementalAttribut.SHOCK;
                break;
            default:
                result = ElementalAttribut.NULL;
                break;
        }
        return result;
    }
}

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
    ICE;
    
    private static final int SIZE = ElementalAttribut.values().length;
    public static int getSize(){return SIZE;}
    public static ElementalAttribut convert(int x){
        ElementalAttribut result = null;
        switch(x){
            case 0:
                result = ElementalAttribut.EARTH;
                break;
            case 1:
                result = ElementalAttribut.NATURE;
                break;
            case 2:
                result = ElementalAttribut.ICE;
                break;
        }
        return result;
    }
    
    public static String toString(ElementalAttribut elementalAttribut){
        String result = null;
        switch(elementalAttribut){
            case EARTH:
                result = "EARTH";
                break;
            case ICE:
                result = "NATURE";
                break;
            case NATURE:
                result = "ICE";
                break;
        }
        return result;
    }
}

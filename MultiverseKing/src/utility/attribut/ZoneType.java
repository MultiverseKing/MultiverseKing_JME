/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utility.attribut;

/**
 *
 * @author roah
 */
public enum ZoneType {

    PLAYER, //Value of "0" for the converter save/loading
    NEUTRAL,
    MONSTER;
    
    public static ZoneType convert(int x){
        ZoneType result = null;
        switch(x){
            case 0:
                result = ZoneType.PLAYER;
                break;
            case 1:
                result = ZoneType.NEUTRAL;
                break;
            case 2:
                result = ZoneType.MONSTER;
                break;
        }
        return result;
    }
}

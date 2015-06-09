package org.multiversekingesapi.utility;

/**
 * Used to configure the battlefield.
 *
 * @author roah
 */
public enum ZoneType {

    /**
     *
     */
    PLAYER, //Value of "0" for the converter save/loading
    /**
     *
     */
    NEUTRAL,
    /**
     *
     */
    MONSTER;

    /**
     *
     * @param x
     * @return
     */
    public static ZoneType convert(int x) {
        ZoneType result = null;
        switch (x) {
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

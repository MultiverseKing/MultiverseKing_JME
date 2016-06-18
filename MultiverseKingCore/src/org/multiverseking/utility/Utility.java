package org.multiverseking.utility;

import java.util.ArrayList;
import java.util.Map;

/**
 * Commodity class.
 * 
 * @author roah
 */
public class Utility {
    

    /**
     * Return the first founded key.
     *
     * @param map
     * @param value
     * @return the key associated to a key in a map.
     */
    public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (value.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * Return a list of key associated to a value.
     *
     * @param map
     * @param value
     * @return multiple key attached to a value.
     */
    public static <T, E> ArrayList<T> getKeysByValue(Map<T, E> map, E value) {
        ArrayList<T> keyList = new ArrayList<T>();
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (value.equals(entry.getValue())) {
                keyList.add(entry.getKey());
            }
        }
        if (keyList.isEmpty()) {
            return null;
        } else {
            return keyList;
        }
    }
}

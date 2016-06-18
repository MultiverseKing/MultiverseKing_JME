package org.multiverseking.battle.battleSystem.ability;

import java.util.ArrayList;
import java.util.HashMap;
import org.multiverseking.utility.Utility;

/**
 *
 * @author roah
 */
public class AbilityManager {

    private static final float segmentSize = 1.0f;
    private float currentLoadTime = 0;
    private float unitSpeed;
    /**
     * Byte[0] == segment needed for activation 
     * Byte[1] == current segment loaded
     */
    private HashMap<String, Byte[]> abilitySegment = new HashMap<>(4, 0.8f);

    AbilityManager(String abilityName, byte segmentNeeded, float unitSpeed) {
        this.abilitySegment.put(abilityName, new Byte[]{segmentNeeded, 0});
        this.unitSpeed = unitSpeed;
    }

    public ArrayList<String> update(float tpf) {
        System.out.println("tpf = " + tpf);
        currentLoadTime += tpf * unitSpeed;
        System.out.println("currentLoadTime = " + currentLoadTime);
        ArrayList<String> toActivate = new ArrayList<>(4);
        if (currentLoadTime >= segmentSize) {
            for (Byte[] segment : abilitySegment.values()) {
                segment[1]++;
                if (segment[1] >= segment[0]) {
                    toActivate.add(Utility.getKeyByValue(abilitySegment, segment));
                    segment[1] = 0;
                }
            }
            currentLoadTime = 0;
            return toActivate;
        } else {
            return null;
        }
    }
}

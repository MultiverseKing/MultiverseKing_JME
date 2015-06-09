package org.multiversekingesapi.loader;

import org.multiversekingesapi.render.AbstractRender.RenderType;
import org.json.simple.JSONObject;
import org.multiversekingesapi.field.component.ATBBurstComponent;
import org.multiversekingesapi.field.component.InfluenceComponent;
import org.multiversekingesapi.field.component.WeaponSlotsComponent;

/**
 *
 * @author roah
 * @deprecated use j3o
 */
public class TitanLoader extends UnitLoader {

    private final InitialTitanStatsComponent initialStatsComponent;

    TitanLoader(JSONObject data, EntityLoader eLoader) {
        super((JSONObject) data.get(RenderType.Unit.toString() + "Stats"), eLoader);
        JSONObject titanData = (JSONObject) data.get(RenderType.Titan.toString() + "Stats");

        Number influenceRange = (Number) titanData.get("influenceRange");
        Number atbBurst = (Number) titanData.get("atbBurst");
        Number weaponSlots = (Number) titanData.get("weaponSlots");

        initialStatsComponent = new InitialTitanStatsComponent(
                influenceRange.byteValue(),
                atbBurst.byteValue(),
                weaponSlots.byteValue(),
                super.getInitialStatsComponent());
    }

    @Override
    public InitialTitanStatsComponent getInitialStatsComponent() {
        return initialStatsComponent;
    }

    public class InitialTitanStatsComponent extends InitialUnitStatsComponent {

        private final byte influenceRange;
        private final byte maxAtbBurst;
        private final byte weaponSlots;

        private InitialTitanStatsComponent(byte influenceRange, byte maxAtbBurst, byte weaponSlots, InitialUnitStatsComponent stats) {
            super(stats);
            this.influenceRange = influenceRange;
            this.maxAtbBurst = maxAtbBurst;
            this.weaponSlots = weaponSlots;
        }

        /**
         * Range of the titan where he can Corrupt tiles arround him.
         */
        public byte getInfluenceRange() {
            return influenceRange;
        }

        public InfluenceComponent getInfluenceComponent() {
            return new InfluenceComponent(influenceRange);
        }

        /**
         * Battle Burst gauge size of the titan.
         */
        public byte getMaxAtbBurst() {
            return maxAtbBurst;
        }

        public ATBBurstComponent getATBBurstComponent() {
            return new ATBBurstComponent(maxAtbBurst);
        }

        /**
         * Weapon the titan can hold during battle.
         */
        public byte getWeaponSlots() {
            return weaponSlots;
        }

        public WeaponSlotsComponent getWeaponSlotsComponent() {
            return new WeaponSlotsComponent(weaponSlots);
        }
    }
}
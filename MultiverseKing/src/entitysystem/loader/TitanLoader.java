package entitysystem.loader;

import entitysystem.field.WeaponSlotsComponent;
import entitysystem.field.EnergyComponent;
import entitysystem.field.ATBBurstComponent;
import entitysystem.field.InfluenceComponent;
import entitysystem.render.RenderComponent.RenderType;
import org.json.simple.JSONObject;

/**
 *
 * @author roah
 */
public class TitanLoader extends UnitLoader {

    private final InitialTitanStatsComponent initialStatsComponent;

    TitanLoader(JSONObject data, EntityLoader eLoader) {
        super((JSONObject) data.get(RenderType.Unit.toString() + "Stats"), eLoader);
        JSONObject titanData = (JSONObject) data.get(RenderType.Unit.toString() + "Stats");

        Number influenceRange = (Number) titanData.get("influenceRange");
        Number atbBurst = (Number) titanData.get("atbBurst");
        Number energy = (Number) titanData.get("energy");
        Number weaponSlots = (Number) titanData.get("weaponSlots");

        initialStatsComponent = new InitialTitanStatsComponent(
                influenceRange.byteValue(),
                atbBurst.byteValue(),
                energy.intValue(),
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
        private final int maxEnergy;
        private final byte weaponSlots;

        private InitialTitanStatsComponent(byte influenceRange, byte maxAtbBurst, int maxEnergy, byte weaponSlots, InitialUnitStatsComponent stats) {
            super(stats);
            this.influenceRange = influenceRange;
            this.maxAtbBurst = maxAtbBurst;
            this.maxEnergy = maxEnergy;
            this.weaponSlots = weaponSlots;
        }

        public byte getInfluenceRange() {
            return influenceRange;
        }

        public InfluenceComponent getInfluenceComponent() {
            return new InfluenceComponent(influenceRange);
        }

        public byte getMaxAtbBurst() {
            return maxAtbBurst;
        }

        public ATBBurstComponent getATBBurstComponent() {
            return new ATBBurstComponent(maxAtbBurst);
        }

        public int getMaxEnergy() {
            return maxEnergy;
        }

        public EnergyComponent getEnergyComponent() {
            return new EnergyComponent(maxEnergy);
        }

        public byte getWeaponSlots() {
            return weaponSlots;
        }

        public WeaponSlotsComponent getWeaponSlotsComponent() {
            return new WeaponSlotsComponent(weaponSlots);
        }
    }
}
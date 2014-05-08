package entitysytem.Units;

/**
 * Entity Titan Stats.
 *
 * @author roah
 */
public class TitanStatsComponent {

    /**
     * How many equipements (weapon) slots this Titan have.
     */
    private byte equipementSlotsCount;
    /**
     * How many reduction damage the unit got.
     */
    private int armor;

    public TitanStatsComponent(byte equipementSlotsCount, int armor) {
        this.equipementSlotsCount = equipementSlotsCount;
        this.armor = armor;
    }

    public byte getEquipementSlotsCount() {
        return equipementSlotsCount;
    }

    /**
     * Damage reduction without equipement.
     *
     * @return
     */
    public int getArmor() {
        return armor;
    }
}

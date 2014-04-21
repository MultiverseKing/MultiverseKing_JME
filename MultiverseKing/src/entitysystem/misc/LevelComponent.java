package entitysystem.misc;

import com.simsilica.es.PersistentComponent;

/**
 * Can be used outside the card system, not specialy belong to it.
 * @author roah
 */
public class LevelComponent implements PersistentComponent {
    private byte level;

    /**
     * Create a new level component to work with the leveling system.
     * @param level to start with.
     */
    public LevelComponent(byte level) {
        this.level = level;
    }

    /**
     * @return the level of this component/entity.
     */
    public byte getLevel() {
        return level;
    }    
}

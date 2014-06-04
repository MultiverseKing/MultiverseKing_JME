package entitysystem.render;

import com.simsilica.es.PersistentComponent;

/**
 *
 * @author roah
 */
public class VFXComponent implements PersistentComponent {
    private final String abilityName;

    public VFXComponent(String abilityName) {
        this.abilityName = abilityName;
    }

    public String getAbilityName() {
        return abilityName;
    }
}

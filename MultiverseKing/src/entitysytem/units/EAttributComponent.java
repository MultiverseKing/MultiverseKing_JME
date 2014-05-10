package entitysytem.units;

import com.simsilica.es.PersistentComponent;
import utility.ElementalAttribut;

/**
 * Core Element of the entity.
 *
 * @author roah
 */
public class EAttributComponent implements PersistentComponent {

    private final ElementalAttribut eAttribut;

    /**
     * Core Element of this entity.
     *
     * @param eAttribut
     */
    public EAttributComponent(ElementalAttribut eAttribut) {
        this.eAttribut = eAttribut;
    }

    /**
     * Core element of this entity.
     *
     * @return
     */
    public ElementalAttribut geteAttribut() {
        return eAttribut;
    }
}

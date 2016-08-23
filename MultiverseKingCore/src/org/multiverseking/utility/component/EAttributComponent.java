package org.multiverseking.utility.component;

import com.simsilica.es.EntityComponent;
import org.multiverseking.utility.ElementalAttribut;

/**
 * Core Element of the entity.
 *
 * @author roah
 */
public class EAttributComponent implements EntityComponent {

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

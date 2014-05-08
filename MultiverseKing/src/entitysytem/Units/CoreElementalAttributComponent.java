package entitysytem.Units;

import utility.ElementalAttribut;

/**
 * Core Element of the entity.
 *
 * @author roah
 */
public class CoreElementalAttributComponent {

    ElementalAttribut eAttribut;

    /**
     * Core Element of this entity.
     *
     * @param eAttribut
     */
    public CoreElementalAttributComponent(ElementalAttribut eAttribut) {
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

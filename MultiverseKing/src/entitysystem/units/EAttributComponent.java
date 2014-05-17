package entitysystem.units;

import entitysystem.ExtendedComponent;
import utility.ElementalAttribut;

/**
 * Core Element of the entity.
 *
 * @author roah
 */
public class EAttributComponent implements ExtendedComponent {

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

    @Override
    public ExtendedComponent clone() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}

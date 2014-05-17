package entitysystem.render;

import entitysystem.ExtendedComponent;

/**
 *
 * @author roah
 */
public class VFXComponent implements ExtendedComponent {
    private final String abilityName;

    public VFXComponent(String abilityName) {
        this.abilityName = abilityName;
    }

    public String getAbilityName() {
        return abilityName;
    }

    @Override
    public ExtendedComponent clone() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}

package entitysytem.units;

/**
 *
 * @author roah
 */
public class AbilityComponent {
    
    /**
     * Name used to load the ability.
     */
    private String name;

    public AbilityComponent(String name) {
        this.name = name;
    }

    /**
     * Name of the ability.
     * @return 
     */
    public String getName() {
        return name;
    }
}

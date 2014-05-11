package entitysystem;

import com.simsilica.es.EntityComponent;

/**
 *
 * @author roah
 */
public interface ExtendedComponent extends EntityComponent {
    /**
     * Create a clone of this component.
     * @return the cloned component.
     */
    public ExtendedComponent clone();
}

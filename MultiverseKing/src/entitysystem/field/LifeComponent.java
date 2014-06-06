package entitysystem.field;

import com.simsilica.es.PersistentComponent;

/**
 *
 * @author roah
 */
public class LifeComponent implements PersistentComponent {

    private final int life;

    public LifeComponent(int life) {
        this.life = life;
    }

    /**
     * How many life point the unit have.
     *
     * @return
     */
    public int getLife() {
        return life;
    }
}

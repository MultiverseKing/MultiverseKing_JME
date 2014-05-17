package entitysystem.units;

import entitysystem.ExtendedComponent;

/**
 *
 * @author roah
 */
public class LifeComponent implements ExtendedComponent {

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

    @Override
    public ExtendedComponent clone() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}

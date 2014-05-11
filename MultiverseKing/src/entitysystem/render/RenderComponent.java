package entitysystem.render;

import entitysystem.ExtendedComponent;

/**
 * TODO: Comments Used by the card system and the render system.
 *
 * @author Eike Foede, roah
 */
public class RenderComponent implements ExtendedComponent {

    private String name;

    public RenderComponent(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public ExtendedComponent clone() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}

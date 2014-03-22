package entitysystem.render;

import com.simsilica.es.PersistentComponent;

/**
 *TODO: Comments
 * @author Eike Foede
 */
public class RenderComponent implements PersistentComponent {

    private String modelName;

    public RenderComponent() {
    }

    public RenderComponent(String modelName) {
        this.modelName = modelName;
    }

    public String getModelName() {
        return modelName;
    }
}

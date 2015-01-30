package test;

import com.jme3.app.SimpleApplication;
import java.util.logging.Level;
import kingofmultiverse.GameParameter;

/**
 *
 * @author roah
 */
public class GhostGridTest extends SimpleApplication {
    
    
    public static void main(String[] args) {
        GhostGridTest app = new GhostGridTest();
        java.util.logging.Logger.getLogger("").setLevel(Level.WARNING);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        GameParameter param = new GameParameter(this, false);
        
        
    }
}

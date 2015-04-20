package org.hexgridapi.core.appstate;

import com.jme3.app.SimpleApplication;
import org.hexgridapi.core.DefaultParam;
import org.hexgridapi.core.RTSCamera;

/**
 *
 * @author roah
 */
public class HexGridDefaultApp extends SimpleApplication {

    private RTSCamera rtsCam;

    public RTSCamera getRtsCam() {
        return rtsCam;
    }

    @Override
    public void simpleInitApp() {
        super.inputManager.clearMappings();
        rtsCam = new DefaultParam(this, false).getCam();
    }
    
}

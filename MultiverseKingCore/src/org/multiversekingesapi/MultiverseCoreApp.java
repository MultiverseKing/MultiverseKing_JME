package org.multiversekingesapi;

import com.jme3.app.SimpleApplication;

/**
 *
 * @author roah
 */
public class MultiverseCoreApp extends SimpleApplication {

    private RTSCamera rtsCam;

    public RTSCamera getRtsCam() {
        return rtsCam;
    }

    @Override
    public void simpleInitApp() {
        rtsCam = new DefaultParam(this, false).getCam();
    }
}

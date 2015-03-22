/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.shadow.DirectionalLightShadowFilter;
import org.hexgridapi.utility.ArrowDebugShape;

/**
 *
 * @author roah
 */
public class DefaultParam {
    private RTSCamera rtsCam;

    public DefaultParam(SimpleApplication app, boolean debug) {
        app.setPauseOnLostFocus(false);
        lightSettup(app);
        cameraSettup(app);
        if(debug){
            initDebug(app);
        }
    }

    private void lightSettup(SimpleApplication app) {
        /**
         * A white, directional light source.
         */
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection((new Vector3f(-0.5f, -0.5f, -0.5f)).normalizeLocal());
        sun.setColor(ColorRGBA.White);
//        sun.setColor(new ColorRGBA(200/255, 200/255, 200/255, 1));
//        sun.setColor(ColorRGBA.Blue);
        app.getRootNode().addLight(sun);

        /* this shadow needs a directional light */
        FilterPostProcessor fpp = new FilterPostProcessor(app.getAssetManager());
        DirectionalLightShadowFilter dlsf = new DirectionalLightShadowFilter(app.getAssetManager(), 1024, 2);
        dlsf.setLight(sun);
        fpp.addFilter(dlsf);
        app.getViewPort().addProcessor(fpp);

        /* Drop shadows */
//        final int SHADOWMAP_SIZE = 1024;
//        DirectionalLightShadowRenderer dlsr = new DirectionalLightShadowRenderer(app.getAssetManager(), SHADOWMAP_SIZE, 3);
//        dlsr.setLight(sun);
//        app.getViewPort().addProcessor(dlsr);

        /**
         * A white ambient light source.
         */
        AmbientLight ambient = new AmbientLight();
//        ambient.setColor(ColorRGBA.White);
        ambient.setColor(new ColorRGBA(230 / 255, 230 / 255, 230 / 255, 1));
        app.getRootNode().addLight(ambient);
    }

    private void cameraSettup(SimpleApplication app) {
        app.getFlyByCamera().setEnabled(false);
        rtsCam = new RTSCamera(RTSCamera.UpVector.Y_UP);//, "AZERTY");
//        rtsCam.setCenter(new Vector3f(8, 15f, 8));
        rtsCam.setCenter(new Vector3f(8, 17, 8));
        rtsCam.setRot(120);
        app.getStateManager().attach(rtsCam);
    }
    
    private void initDebug(SimpleApplication app) {
        ArrowDebugShape arrowShape = new ArrowDebugShape(app.getAssetManager(), app.getRootNode(), new Vector3f(0f, 0f, 0f));
    }
    
    RTSCamera getCam() {
        return rtsCam;
    }
}

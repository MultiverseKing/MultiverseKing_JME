/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package editor.debug;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import java.io.IOException;

/**
 *
 * @author roah
 */
public class MoveOnYControl extends AbstractControl {

    private int factor = 1;
    private float initialPosition;
    //Any local variables should be encapsulated by getters/setters so they
    //appear in the SDK properties window and can be edited.
    //Right-click a local variable to encapsulate it with getters and setters.

    @Override
    protected void controlUpdate(float tpf) {
        spatial.setLocalTranslation(spatial.getLocalTranslation().x, spatial.getLocalTranslation().y + (tpf * factor) * 0.5f, spatial.getLocalTranslation().z);
        if (spatial.getLocalTranslation().y > initialPosition+0.3f) {
            factor = -1;
        } else if (spatial.getLocalTranslation().y < initialPosition-0.2f) {
            factor = 1;
        }
    }

    @Override
    public void setSpatial(Spatial spatial) {
        initialPosition = spatial.getLocalTranslation().y;
        super.setSpatial(spatial);
    }

    
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //Only needed for rendering-related operations,
        //not called when spatial is culled.
    }

    @Override
    public Control cloneForSpatial(Spatial spatial) {
        MoveOnYControl control = new MoveOnYControl();
        //TODO: copy parameters to new Control
        return control;
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        super.read(im);
        InputCapsule in = im.getCapsule(this);
        //TODO: load properties of this Control, e.g.
        //this.value = in.readFloat("name", defaultValue);
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        super.write(ex);
        OutputCapsule out = ex.getCapsule(this);
        //TODO: save properties of this Control, e.g.
        //out.write(this.value, "name", defaultValue);
    }
}

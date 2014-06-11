package entitysystem.tonegod;

import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import tonegod.gui.core.Element;

/**
 *
 * @author roah
 */
public class ToneSpatialControl extends AbstractControl {
    private ToneNode toneNode;
    
    public ToneSpatialControl(ToneNode toneNode) {
        this.toneNode = toneNode;
    }    

    @Override
    public void setSpatial(Spatial spatial) {
        spatial.getParent().attachChild(toneNode);
        toneNode.attachChild(spatial);
        super.setSpatial(spatial);
    }

    @Override
    protected void controlUpdate(float tpf) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Element getElement() {
        return toneNode.getElement();
    }

    public void setElement(Element element) {
        this.toneNode.setElement(element);
    }
}
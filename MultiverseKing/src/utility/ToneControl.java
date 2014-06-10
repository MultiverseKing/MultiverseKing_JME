package utility;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import tonegod.gui.controls.menuing.Menu;
import tonegod.gui.listeners.MouseButtonListener;
import tonegod.gui.listeners.MouseFocusListener;
 
/**
*
* @author roah
*/
public abstract class ToneControl extends AbstractControl implements MouseFocusListener, MouseButtonListener {
    
    private Menu menu;
    
    public ToneControl(Menu menu) {
        this.menu = menu;
    }

    @Override
    protected void controlUpdate(float tpf) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }
  }
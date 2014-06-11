package entitysystem.tonegod;

import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.scene.Spatial;
import com.jme3.scene.Node;
import tonegod.gui.core.Element;
import tonegod.gui.listeners.MouseButtonListener;
import tonegod.gui.listeners.MouseFocusListener;

/**
 *
 * @author t0neg0d
 */
public abstract class ToneNode extends Node implements MouseFocusListener, MouseButtonListener {
    protected Element element;

    public ToneNode(Element element) {
        this.element = element;
    }

    public final void setElement(Element element) {
        this.element = element;
    }

    public final Element getElement() {
        return this.element;
    }
    
    /**
     * Same as get Child but didn't take The node into account.
     * @param i needed child
     * @return 
     */
    public final Spatial getAsset(int i){
        return ((Node)getChild(0)).getChild(0);
    }

    public void onGetFocus(MouseMotionEvent evt) {
        evt.setConsumed();
    }

    public void onLoseFocus(MouseMotionEvent evt) {
        evt.setConsumed();
    }

    public void onMouseLeftPressed(MouseButtonEvent evt) {
        evt.setConsumed();
    }

    public void onMouseLeftReleased(MouseButtonEvent evt) {
        evt.setConsumed();
    }

    public void onMouseRightPressed(MouseButtonEvent evt) {
        evt.setConsumed();
    }

    public void onMouseRightReleased(MouseButtonEvent evt) {
        evt.setConsumed();
    }
}

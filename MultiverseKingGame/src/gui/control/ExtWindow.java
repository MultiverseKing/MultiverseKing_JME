/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.control;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import tonegod.gui.controls.windows.Window;
import tonegod.gui.core.ElementManager;

/**
 *
 * @author roah
 */
public class ExtWindow extends Window {

    public ExtWindow(ElementManager screen) {
        super(screen);
    }

    public ExtWindow(ElementManager screen, Vector2f position) {
        super(screen, position);
    }

    public ExtWindow(ElementManager screen, Vector2f position, Vector2f dimensions) {
        super(screen, position, dimensions);
    }

    public ExtWindow(ElementManager screen, Vector2f position, Vector2f dimensions, Vector4f resizeBorders, String defaultImg) {
        super(screen, position, dimensions, resizeBorders, defaultImg);
    }

    public ExtWindow(ElementManager screen, String UID, Vector2f position) {
        super(screen, UID, position);
    }

    public ExtWindow(ElementManager screen, String UID, Vector2f position, Vector2f dimensions) {
        super(screen, UID, position, dimensions);
    }

    public ExtWindow(ElementManager screen, String UID, Vector2f position, Vector2f dimensions, Vector4f resizeBorders, String defaultImg) {
        super(screen, UID, position, dimensions, resizeBorders, defaultImg);
    }
    
    public void setIsCollapse(){
        collapse.onButtonMouseLeftUp(null, isEnabled);
    }
}

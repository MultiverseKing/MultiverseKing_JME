package gamemode.gui;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import tonegod.gui.controls.menuing.Menu;
import tonegod.gui.core.Element;
import tonegod.gui.core.ElementManager;
import utility.HexCoordinate;

/**
 *
 * @author roah
 */
public abstract class CameraTrackWindow {

    protected final ElementManager screen;
    protected final Camera camera;
    protected Vector2f offset;
    protected HexCoordinate inspectedSpatialPosition;
    protected Element screenElement;

    public CameraTrackWindow(ElementManager screen, Camera camera) {
        this(screen, camera, Vector2f.ZERO);
    }
    public CameraTrackWindow(ElementManager screen, Camera camera, Vector2f offset) {
        this.screen = screen;
        this.camera = camera;
        this.offset = offset;
    }
    
    public void update(float tpf) {
        if (screen.getElementById(screenElement.getUID()) != null && screenElement.getIsVisible()) {
            Vector3f value = camera.getScreenCoordinates(inspectedSpatialPosition.convertToWorldPositionYAsFloor());
            if (value.x > 0 && value.x < screen.getWidth()*0.9f
                    && value.y > 0 && value.y < screen.getHeight()*0.9f) {
                screenElement.setPosition(new Vector2f(value.x+offset.x, value.y+offset.y));
            } else {
                hide();
            }
        }
    }
    
    public void show(HexCoordinate pos){
        if(screen.getElementById(screenElement.getUID()) == null){
            screen.addElement(screenElement);
        }
        inspectedSpatialPosition = pos;
        Vector3f value = camera.getScreenCoordinates(pos.convertToWorldPositionYAsFloor());
        if(screenElement instanceof Menu){
            ((Menu)screenElement).showMenu(null, value.x, value.y);
        } else {
            screenElement.setPosition(value.x, value.y);
            screenElement.show();
        }
    }
    
    public boolean isVisible(){
        return screenElement.getIsVisible();
    }
    
    public void hide(){
        if(screenElement instanceof Menu){
            ((Menu)screenElement).hideMenu();
        } else {
            screenElement.hide();
        }
    }
    
    public String getUID(){
        return screenElement.getUID();
    }
    
    public Element getScreenElement(){
        return screenElement;
    }
}

package utility;

import com.jme3.app.SimpleApplication;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

/**
 *
 * @author roah
 */
public class MouseRay {
    
    public MouseRay() {
    }

    /**
     * Convert the 2d screen coordinates of the mouse to 3D world coordinates,
     * then cast a ray from it. Got his own class cause it could be used for
     * more then the mouse left clic.
     *
     * @param app
     * @return
     */
    public Ray get3DRay(SimpleApplication app) {
        Vector2f click2d = app.getInputManager().getCursorPosition();
        Vector3f click3d = app.getCamera().getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 0f).clone();
        Vector3f dir = app.getCamera().getWorldCoordinates(
                new Vector2f(click2d.x, click2d.y), 1f).subtractLocal(click3d).normalizeLocal();
        Ray ray = new Ray(click3d, dir);
        return ray;
    }
}

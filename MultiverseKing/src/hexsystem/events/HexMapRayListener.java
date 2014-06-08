package hexsystem.events;

import com.jme3.math.Ray;

/**
 * This is called before HexMapInputListener.
 * @author roah
 */
public interface HexMapRayListener extends HexMapInputListener {
    public HexMapInputEvent leftRayInputAction(Ray ray);
    public HexMapInputEvent rightRayInputAction(Ray ray);
}

package org.hexgridapi.events;

import com.jme3.math.Ray;

/**
 * This is called before MouseInputListener.
 * @author roah
 */
public interface MouseRayListener extends MouseInputListener {
    MouseInputEvent leftRayInputAction(Ray ray);
    MouseInputEvent rightRayInputAction(Ray ray);
}

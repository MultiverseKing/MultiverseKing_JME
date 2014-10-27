package org.hexgridapi.events;

import com.jme3.math.Ray;

/**
 * This is called before MouseInputListener.
 * @author roah
 */
public interface MouseRayListener extends MouseInputListener {
    public MouseInputEvent leftRayInputAction(Ray ray);
    public MouseInputEvent rightRayInputAction(Ray ray);
}

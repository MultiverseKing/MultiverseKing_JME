package org.hexgridapi.events;

import com.jme3.math.Ray;
import org.hexgridapi.events.MouseInputEvent.MouseInputEventType;

/**
 * This is called before MouseInputListener.
 *
 * @author roah
 */
public interface MouseRayListener extends TileInputListener {

    MouseInputEvent MouseRayInputAction(MouseInputEventType mouseInputType, Ray ray);
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hexsystem.events;

import com.jme3.collision.CollisionResults;
import utility.HexCoordinate;

/**
 *
 * @author roah
 */
public class HexMapInputEvent {

    private final HexCoordinate eventPosition;
    private final CollisionResults rayResults;

    public HexMapInputEvent(HexCoordinate eventPosition, CollisionResults rayResults) {
        this.eventPosition = eventPosition;
        this.rayResults = rayResults;
    }

    public HexCoordinate getEventPosition() {
        return eventPosition;
    }

    public CollisionResults getRayResults() {
        return rayResults;
    }
}

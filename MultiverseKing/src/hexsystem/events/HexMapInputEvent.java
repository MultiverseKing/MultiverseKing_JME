/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hexsystem.events;

import com.jme3.math.Ray;
import utility.HexCoordinate;

/**
 *
 * @author roah
 */
public class HexMapInputEvent {

    private final HexCoordinate eventPosition;
    private final Ray lastUsedRay;

    public HexMapInputEvent(HexCoordinate eventPosition, Ray usedRay) {
        this.eventPosition = eventPosition;
        this.lastUsedRay = usedRay;
    }
    
    public HexCoordinate getEventPosition() {
        return eventPosition;
    }

    public Ray getLastUsedRay() {
        return lastUsedRay;
    }
}

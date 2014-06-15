package hexsystem.events;

import com.jme3.collision.CollisionResult;
import com.jme3.math.Ray;
import utility.HexCoordinate;

/**
 *
 * @author roah
 */
public class HexMapInputEvent {

    private final HexCoordinate eventPosition;
    private final Ray lastUsedRay;
    private final CollisionResult collisionResult;

    public HexMapInputEvent(HexCoordinate eventPosition, Ray usedRay, CollisionResult collisionResult) {
        this.eventPosition = eventPosition;
        this.lastUsedRay = usedRay;
        this.collisionResult = collisionResult;
    }
    
    public HexCoordinate getEventPosition() {
        return eventPosition;
    }

    public Ray getLastUsedRay() {
        return lastUsedRay;
    }

    public CollisionResult getCollisionResult() {
        return collisionResult;
    }
}

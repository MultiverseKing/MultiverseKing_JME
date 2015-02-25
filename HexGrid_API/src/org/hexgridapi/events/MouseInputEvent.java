package org.hexgridapi.events;

import com.jme3.collision.CollisionResult;
import com.jme3.math.Ray;
import org.hexgridapi.utility.HexCoordinate;

/**
 *
 * @author roah
 */
public class MouseInputEvent {

    protected final HexCoordinate eventPosition;
    protected final Ray lastUsedRay;
    protected final CollisionResult collisionResult;
    protected final MouseInputEventType eventType;

    public MouseInputEvent(MouseInputEventType eventType, MouseInputEvent event) {
        this(eventType, event.getEventPosition(), event.getLastUsedRay(), event.getCollisionResult());
    }

    public MouseInputEvent(MouseInputEventType eventType, HexCoordinate eventPosition, Ray usedRay, CollisionResult collisionResult) {
        this.eventPosition = eventPosition;
        this.lastUsedRay = usedRay;
        this.collisionResult = collisionResult;
        this.eventType = eventType;
    }

    public MouseInputEventType getEventType(){
        return eventType;
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

    public enum MouseInputEventType {

        PULSE,
        RMB,
        LMB,
        MMB;
    }
}

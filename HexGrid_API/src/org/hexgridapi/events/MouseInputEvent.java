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
    protected final Integer eventHeight;
    protected final Ray lastUsedRay;
    protected final CollisionResult collisionResult;
    protected final MouseInputEventType eventType;

    public MouseInputEvent(MouseInputEvent event, MouseInputEventType eventType, Integer eventHeight) {
        this(eventType, event.getPosition(), eventHeight, event.getLastUsedRay(), event.getCollisionResult());
    }
        
    public MouseInputEvent(MouseInputEvent event, Integer eventHeight) {
        this(event.getType(), event.getPosition(), eventHeight, event.getLastUsedRay(), event.getCollisionResult());
    }
    
    public MouseInputEvent(MouseInputEvent event, MouseInputEventType eventType) {
        this(eventType, event.getPosition(), event.getHeight(), event.getLastUsedRay(), event.getCollisionResult());
    }

    public MouseInputEvent(MouseInputEventType eventType, HexCoordinate eventPosition, Integer eventHeight, Ray usedRay, CollisionResult collisionResult) {
        this.eventPosition = eventPosition;
        this.lastUsedRay = usedRay;
        this.collisionResult = collisionResult;
        this.eventType = eventType;
        this.eventHeight = eventHeight;
    }

    public MouseInputEventType getType(){
        return eventType;
    }
    
    public HexCoordinate getPosition() {
        return eventPosition;
    }

    public int getHeight() {
        return eventHeight;
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

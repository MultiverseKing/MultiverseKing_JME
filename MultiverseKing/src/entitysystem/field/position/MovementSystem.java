package entitysystem.field.position;

import com.simsilica.es.Entity;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import entitysystem.EntitySystemAppState;
import entitysystem.attribut.Animation;
import entitysystem.render.AnimationComponent;
import entitysystem.render.utility.Curve;
import entitysystem.render.utility.Curve.CurveType;
import hexsystem.area.MapDataAppState;
import org.hexgridapi.pathfinding.Astar;
import org.hexgridapi.pathfinding.Pathfinder;
import java.util.HashMap;
import java.util.List;
import org.hexgridapi.utility.HexCoordinate;
import org.hexgridapi.utility.Rotation;

/**
 * Todo : behavior when unit got an obstacle appearing when moving (stop it and
 * remove the component or put the move on pause until the obstacle is gone (2nd
 * idea best)).
 *
 * @author Eike Foede, roah
 */
public class MovementSystem extends EntitySystemAppState {

    private final float GLOBAL_SPEED = 5f;
    private Pathfinder pathfinder = new Astar();
    private HashMap<EntityId, Movement> movements;

    /**
     *
     * @return
     */
    @Override
    protected EntitySet initialiseSystem() {
        pathfinder.setMapData(app.getStateManager().getState(MapDataAppState.class).getMapData());
        movements = new HashMap<EntityId, Movement>();
        return entityData.getEntities(MovementComponent.class, HexPositionComponent.class, MoveToComponent.class);
    }

    /**
     *
     * @param tpf
     */
    @Override
    protected void updateSystem(float tpf) {
        for (Entity e : entities) {
            Movement movement = movements.get(e.getId());
            float speed = GLOBAL_SPEED / e.get(MovementComponent.class).getMoveSpeed();
            if (movement.actualPosition == 0 && movement.distanceMoved == 0f) {
                startMoveEntity(e, movement, speed);
            } else if (movement.actualPosition > movement.path.size()) {
                entityData.removeComponent(e.getId(), MoveToComponent.class);//goal is reached
                Rotation dir = movement.path.get(movement.path.size() - 2).getDirection(movement.goal);
                entityData.setComponent(e.getId(), e.get(HexPositionComponent.class).cloneWithoutCurve(movement.goal, dir));
//                movement.distanceMoved = 0;
                if (entityData.getComponent(e.getId(), AnimationComponent.class) != null) {
                    entityData.setComponent(e.getId(), new AnimationComponent(Animation.IDLE));
                }
            } else if (movement.distanceMoved >= speed) {
//                moveEntity(e, movement, speed);
                movement.actualPosition++;
                movement.distanceMoved = 0;
            }
            movement.distanceMoved += tpf;
        }
    }

    private void startMoveEntity(Entity e, Movement movement, float speed) {
        movement.actualPosition++;
        /**
         * Update the rotation.
         */
//        Rotation dir = movement.path.get(movement.actualPosition-1).getDirection(movement.path.get(movement.actualPosition));
//        if (!e.get(HexPositionComponent.class).getRotation().equals(dir)) {
//            e.set(e.get(HexPositionComponent.class).clone(dir));
//        }
        /**
         * Update the animation.
         */
        if (entityData.getComponent(e.getId(), AnimationComponent.class) != null) {
            entityData.setComponent(e.getId(), new AnimationComponent(Animation.WALK));
        }
        /**
         * We set a new position for the entity, with a specifiate curve so the
         * render will not teleport it.
         */
        Curve curve = new Curve(CurveType.LINEAR, speed, movement.path);
        e.set(e.get(HexPositionComponent.class).movement(curve));
    }

    /**
     *
     * @param e
     */
    @Override
    protected void addEntity(Entity e) {
        HexPositionComponent pos = e.get(HexPositionComponent.class);
        MoveToComponent moveTo = e.get(MoveToComponent.class);
        Movement movement = new Movement();
        movement.path = pathfinder.getPath(pos.getPosition(), moveTo.getPosition());
        if (movement.path == null) {
            System.out.println("No Path found!");
            entityData.removeComponent(e.getId(), MoveToComponent.class);
            return;
        }
        movement.goal = moveTo.getPosition();
        movements.put(e.getId(), movement);
    }

    /**
     *
     * @param e
     */
    @Override
    protected void updateEntity(Entity e) {
        MoveToComponent moveTo = e.get(MoveToComponent.class);
        if (!moveTo.getPosition().equals(movements.get(e.getId()).goal)) {
            addEntity(e);
        }
    }

    /**
     *
     * @param e
     */
    @Override
    protected void removeEntity(Entity e) {
        movements.remove(e.getId());
    }

    @Override
    protected void cleanupSystem() {
    }

    private class Movement {

        float distanceMoved = 0;//Distance already moved since last tile
        int actualPosition = 0; //index of point in list
        List<HexCoordinate> path;
        HexCoordinate goal;
    }
}

package entitysystem.field.position;

import com.simsilica.es.Entity;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import entitysystem.EntitySystemAppState;
import entitysystem.attribut.Animation;
import entitysystem.render.AnimationComponent;
import entitysystem.render.utility.Curve;
import entitysystem.render.utility.Curve.CurveType;
import hexsystem.HexSystemAppState;
import hexsystem.pathfinding.Astar;
import hexsystem.pathfinding.Pathfinder;
import java.util.HashMap;
import java.util.List;
import utility.HexCoordinate;
import utility.Rotation;

/**
 * Todo : behavior when unit got an obstacle appearing when moving (stop it and
 * remove the component or put the move on pause until the obstacle is gone (2nd
 * idea best)).
 *
 * @author Eike Foede, roah
 */
public class MovementSystem extends EntitySystemAppState {

    private Pathfinder pathfinder = new Astar();
    private HashMap<EntityId, Movement> movements;
    /**
     *
     * @return
     */
    @Override
    protected EntitySet initialiseSystem() {
        pathfinder.setMapData(app.getStateManager().getState(HexSystemAppState.class).getMapData());
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
            float speed = e.get(MovementComponent.class).getMoveSpeed();
            if (movement.distanceMoved >= speed){
                moveEntity(e, movement, speed);
                movement.distanceMoved = 0;
            } else if(movement.actualPosition == 0){
                moveEntity(e, movement, speed);
            }
            movement.distanceMoved += tpf;
        }
    }
    
    private void moveEntity(Entity e, Movement movement, float speed){
        movement.actualPosition++;
        if(movement.path.size() > movement.actualPosition){
            /**
             * Update the rotation.
             */
            Rotation dir = movement.path.get(movement.actualPosition-1).getDirection(movement.path.get(movement.actualPosition));
            if (!e.get(HexPositionComponent.class).getRotation().equals(dir)) {
                e.set(e.get(HexPositionComponent.class).clone(dir));
            }
            /**
             * We set a new position for the entity, with a specifiate curve so the render will not teleport it.
             * @todo: Interpolate the rotation.
             */
            
            Curve curve = new Curve(CurveType.LINEAR, speed);
            e.set(e.get(HexPositionComponent.class).interpolateTo(curve, movement.path.get(movement.actualPosition)));

            /**
             * Update the animation.
             */
            if(entityData.getComponent(e.getId(), AnimationComponent.class) != null){
                entityData.setComponent(e.getId(), new AnimationComponent(Animation.WALK));
            }
        } else {
            entityData.removeComponent(e.getId(), MoveToComponent.class);//goal is reached
            movement.distanceMoved = 0;
            if(entityData.getComponent(e.getId(), AnimationComponent.class) != null){
                entityData.setComponent(e.getId(), new AnimationComponent(Animation.IDLE));
            }
        }
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
        HexPositionComponent hexPos = e.get(HexPositionComponent.class);
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
        if(e.get(HexPositionComponent.class) != null){
            entityData.setComponent(e.getId(), e.get(HexPositionComponent.class).cloneWithoutCurve());
        }
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

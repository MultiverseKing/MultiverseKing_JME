package entitysystem.movement;

import com.simsilica.es.Entity;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import entitysystem.EntitySystemAppState;
import entitysystem.position.HexPositionComponent;
import gamestate.HexSystemAppState;
import hexsystem.MapData;
import hexsystem.pathfinding.Astar;
import hexsystem.pathfinding.Pathfinder;
import java.util.HashMap;
import java.util.List;
import utility.HexCoordinate;
import utility.Rotation;
import utility.Vector3Int;

/**
 * Todo : behavior when unit got an obstacle appearing when moving 
 * (stop it and remove the component 
 * or put the move on pause until the obstacle is gone (2nd idea best)).
 * 
 * @author Eike Foede, roah
 */
public class MovementSystem extends EntitySystemAppState {

    private Pathfinder pathfinder = new Astar();
    private HashMap<EntityId, Movement> movements;
//    private float secondsPerStep = 1.5f;

    /**
     *
     * @return
     */
    @Override
    protected EntitySet initialiseSystem() {
        pathfinder.setMapData(app.getStateManager().getState(HexSystemAppState.class).getMapData());
        movements = new HashMap<EntityId, Movement>();
        return entityData.getEntities(MovementStatsComponent.class, HexPositionComponent.class, MoveToComponent.class);
    }

    /**
     *
     * @param tpf
     */
    @Override
    protected void updateSystem(float tpf) {
        for (Entity e : entities) {
            Movement movement = movements.get(e.getId());
            float speed = e.get(MovementStatsComponent.class).getMoveSpeed();
            movement.distanceMoved += tpf;
            while (movement.distanceMoved > speed) {
                movement.distanceMoved -= speed;
                if (movement.actualPosition + 1 < movement.path.size()) {
                    Rotation dir = getDirection(movement.path.get(movement.actualPosition).getAsCubic(), 
                            movement.path.get(movement.actualPosition + 1).getAsCubic());
                    if (!e.get(HexPositionComponent.class).getRotation().equals(dir)) {
                        e.set(e.get(HexPositionComponent.class).clone(dir));
                    }
                }
                e.set(e.get(HexPositionComponent.class).clone(movement.path.get(movement.actualPosition)));
                movement.actualPosition++;
                if (movement.actualPosition == movement.path.size()) {
                    entityData.removeComponent(e.getId(), MoveToComponent.class);//goal is reached
                    movement.distanceMoved = 0;
                }
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

    /**
     *
     */
    @Override
    protected void cleanupSystem() {
    }

    private Rotation getDirection(Vector3Int currentPos, Vector3Int nextPos) {
        Vector3Int result = new Vector3Int(currentPos.x - nextPos.x, currentPos.y - nextPos.y, currentPos.z - nextPos.z);
        if (result.z == 0 && result.x > 0) {
            return Rotation.D;
        } else if (result.z == 0 && result.x < 0) {
            return Rotation.A;
        } else if (result.y == 0 && result.x > 0) {
            return Rotation.C;
        } else if (result.y == 0 && result.x < 0) {
            return Rotation.F;
        } else if (result.x == 0 && result.y > 0) {
            return Rotation.B;
        } else if (result.x == 0 && result.y < 0) {
            return Rotation.E;
        }
        return null;
    }

    private class Movement {

        float distanceMoved = 0;//Distance already moved since last tile
        int actualPosition = 1; //index of point in list
        List<HexCoordinate> path;
        HexCoordinate goal;
    }
}

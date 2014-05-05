package entitysystem.movement;

import com.simsilica.es.Entity;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import entitysystem.EntitySystemAppState;
import entitysystem.position.HexPositionComponent;
import entitysystem.position.RotationComponent;
import hexsystem.pathfinding.Astar;
import hexsystem.pathfinding.Pathfinder;
import java.util.HashMap;
import java.util.List;
import utility.HexCoordinate;
import utility.Rotation;
import utility.Vector3Int;

/**
 *
 * @author Eike Foede
 */
public class MovementSystem extends EntitySystemAppState {

    private Pathfinder pathfinder = new Astar();
    private HashMap<EntityId, Movement> movements;
    private float secondsPerStep = 1.5f;

    @Override
    protected EntitySet initialiseSystem() {
        pathfinder.setMapData(mapData);
        movements = new HashMap<EntityId, Movement>();
        return entityData.getEntities(HexPositionComponent.class, MoveToComponent.class, RotationComponent.class);
    }

    @Override
    protected void updateSystem(float tpf) {
        for (Entity e : entities) {
            Movement movement = movements.get(e.getId());
            movement.distanceMoved += tpf;
            while (movement.distanceMoved > secondsPerStep) {
                movement.distanceMoved -= secondsPerStep;
                if(movement.actualPosition+1 < movement.path.size()){
                    Rotation dir = getDirection(movement.path.get(movement.actualPosition).getAsCubic(), movement.path.get(movement.actualPosition+1).getAsCubic());
                    if(!e.get(RotationComponent.class).getRotation().equals(dir)){
                        e.set(new RotationComponent(dir));
                    }
                }
                e.set(new HexPositionComponent(movement.path.get(movement.actualPosition)));
                movement.actualPosition++;
                if (movement.actualPosition == movement.path.size()) {
                    entityData.removeComponent(e.getId(), MoveToComponent.class);//goal is reached
                    movement.distanceMoved = 0;
                }
            }
        }
    }

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

    @Override
    protected void updateEntity(Entity e) {
        MoveToComponent moveTo = e.get(MoveToComponent.class);
        if (!moveTo.getPosition().equals(movements.get(e.getId()).goal)) {
            addEntity(e);
        }
    }

    @Override
    protected void removeEntity(Entity e) {
        movements.remove(e.getId());
    }

    @Override
    protected void cleanupSystem() {
    }

    private Rotation getDirection(Vector3Int currentPos, Vector3Int nextPos) {
        Vector3Int result = new Vector3Int(currentPos.x - nextPos.x, currentPos.y - nextPos.y, currentPos.z - nextPos.z);
        if(result.z == 0 && result.x > 0){
            return Rotation.D;
        } else if(result.z == 0 && result.x < 0){
            return Rotation.A;
        } else if(result.y == 0 && result.x > 0){
            return Rotation.C;
        } else if(result.y == 0 && result.x < 0){
            return Rotation.F;
        } else if(result.x == 0 && result.y > 0){
            return Rotation.B;
        } else if(result.x == 0 && result.y < 0){
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

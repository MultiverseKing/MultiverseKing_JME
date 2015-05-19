package org.multiversekingesapi.field.exploration;

import com.jme3.cinematic.MotionPath;
import com.jme3.cinematic.MotionPathListener;
import com.jme3.cinematic.events.MotionEvent;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import java.util.HashMap;
import org.multiversekingesapi.EntitySystemAppState;
import org.multiversekingesapi.render.animation.Animation;
import org.multiversekingesapi.render.animation.AnimationComponent;
import org.hexgridapi.pathfinding.Astar;
import org.hexgridapi.pathfinding.Pathfinder;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hexgridapi.core.data.MapData;
import org.hexgridapi.core.appstate.MapDataAppState;
import org.hexgridapi.utility.HexCoordinate;
import org.hexgridapi.utility.Rotation;
import org.multiversekingesapi.SubSystem;
import org.multiversekingesapi.field.position.HexPositionComponent;
import org.multiversekingesapi.field.position.HexPositionSystem;
import org.multiversekingesapi.field.position.MoveToComponent;
import org.multiversekingesapi.loader.EntityLoader;
import org.multiversekingesapi.render.RenderComponent;
import org.multiversekingesapi.render.RenderSystem;

/**
 * Todo : behavior when unit got an obstacle appearing when moving (stop it and
 * remove the component).
 * todo : behavior when unit is moving and a tile change.
 *
 * @author Eike Foede, roah
 */
public class HexMovementSystem extends EntitySystemAppState implements SubSystem {

    private MapData mapData;
    private RenderSystem renderSystem;
    private HexPositionSystem hexPositionsystem;
    private Pathfinder pathfinder = new Astar();
    private HashMap<EntityId, HexCoordinate> movementUpdateGoal = new HashMap<>();
    private HashMap<EntityId, HexCoordinate> movementGoal = new HashMap<>();

    @Override
    protected EntitySet initialiseSystem() {
        mapData = app.getStateManager().getState(MapDataAppState.class).getMapData();
        pathfinder.setMapData(mapData);
        renderSystem = app.getStateManager().getState(RenderSystem.class);
        renderSystem.registerSubSystem(this, true);
        hexPositionsystem = app.getStateManager().getState(HexPositionSystem.class);
        return entityData.getEntities(HexPositionComponent.class, MoveToComponent.class, RenderComponent.class);
    }

    @Override
    protected void updateSystem(float tpf) {
    }

    @Override
    protected void addEntity(Entity e) {
        if (!movementGoal.containsKey(e.getId())) {
            if (!initialiseControl(e, getPathfinderPath(e.get(HexPositionComponent.class).getPosition(),
                    e.get(MoveToComponent.class).getPosition()))) {
                entityData.removeComponent(e.getId(), MoveToComponent.class);
            } else {
                movementGoal.put(e.getId(), e.get(MoveToComponent.class).getPosition());
                hexPositionsystem.registerEntityToSubSystem(e.getId());
            }
        }
    }

    private List<HexCoordinate> getPathfinderPath(HexCoordinate from, HexCoordinate to) {
        List<HexCoordinate> path = pathfinder.getPath(from, to);
        if (path == null) {
            Logger.getGlobal().log(Level.WARNING, "{0} : No path found by the Pathfinder !", getClass().getName());
        }
        return path;
    }

    /**
     * @return true if the control is initialised correctly.
     */
    private boolean initialiseControl(Entity e, List<HexCoordinate> path) {
        if (path != null) {
            float ms = new EntityLoader(app).loadTitanStats(
                    e.get(RenderComponent.class).getName())
                    .getInitialStatsComponent().getMoveSpeed();

            MotionPath motionPath = addPathListener(e.getId(), buildPath(null, path));
            MotionEvent motionControl = new MotionEvent(renderSystem.getSpatial(e.getId()),
                    motionPath, ms * path.size());
            motionControl.setDirectionType(MotionEvent.Direction.Path);
            motionControl.play();
            entityData.setComponent(e.getId(), new AnimationComponent(Animation.WALK));
            return true;
        }
        return false;
    }

    @Override
    protected void updateEntity(Entity e) {
        MoveToComponent moveTo = e.get(MoveToComponent.class);
        /**
         * We check if the goal of the control is the same than the one of the
         * component.
         */
        if (!moveTo.getPosition().equals(movementGoal.get(e.getId()))) {
            movementUpdateGoal.put(e.getId(), moveTo.getPosition());
//            movementGoal.put(e.getId(), moveTo.getPosition());
//            /**
//             * We build a new path starting from the next waypoint.
//             */
//            List<HexCoordinate> newPath = getPathfinderPath(
//                    new HexCoordinate(motionControl.getPath().getWayPoint(
//                    motionControl.getCurrentWayPoint()+1)), moveTo.getPosition());
//            if(newPath == null){
//                return;
//            }
//            movementUpdateQueue.put(e.getId(), newPath);
//            Movement movement = movements.get(e.getId());
//            movement.path = pathfinder.getPath(movement.path.get(movement.actualPosition-1), moveTo.getPosition());
//            movement.goal = moveTo.getPosition();
//            movement.actualPosition = 0;
//            Curve curve =
//                    movements.get(e.getId()).curve = new Curve(CurveType.LINEAR, GLOBAL_SPEED / e.get(MovementComponent.class).getMoveSpeed(), movement.path);
//            e.set(e.get(HexPositionComponent.class).movement(curve));
        }
    }

    @Override
    protected void removeEntity(Entity e) {
//        System.err.println(renderSystem.getSpatial(e.getId()).getName() + " is removed.");
        movementGoal.remove(e.getId());
        Spatial s = renderSystem.getSpatial(e.getId());
        if (s != null) {
            MotionEvent control = s.getControl(MotionEvent.class);
            if (control != null) {
                control.getPath().disableDebugShape();
                s.removeControl(control);
            }
        }
//        if (movementUpdateGoal.containsKey(e.getId())
//                && buildNewPath(e.getId(), control, null)) {
//            entityData.setComponent(e.getId(), new MoveToComponent(movementUpdateGoal.get(e.getId())));
//            return;
//        }
        hexPositionsystem.removeEntityFromSubSystem(e.getId());
        entityData.setComponent(e.getId(), new AnimationComponent(Animation.IDLE));
    }

    @Override
    protected void cleanupSystem() {
        renderSystem.removeSubSystem(this, true);
    }

    @Override
    public void rootSystemIsRemoved() {
        app.getStateManager().detach(this); //todo disable all currently running movement
    }

    private MotionPath buildPath(Vector3f initPosition, List<HexCoordinate> wayPoint) {
        MotionPath path = new MotionPath();
        if (initPosition != null) {
            path.addWayPoint(initPosition);
        }
        for (int i = 0; i < wayPoint.size(); i++) {
            HexCoordinate pos = wayPoint.get(i);
            path.addWayPoint(pos.toWorldPosition(mapData.getTile(pos).getHeight()));
        }
        path.enableDebugShape(app.getAssetManager(), renderSystem.getSubSystemNode(this));
        return path;
    }

    private MotionPath addPathListener(final EntityId id, MotionPath path) {
        path.addListener(new MotionPathListener() {
            @Override
            public void onWayPointReach(MotionEvent control, int wayPointIndex) {
                if (control.getPath().getNbWayPoints() == wayPointIndex + 1) {
//                    System.err.println(control.getSpatial().getName() + " has finished moving. ");
                    entityData.removeComponent(id, MoveToComponent.class);
                } else {
//                    System.err.println(control.getSpatial().getName() + " has reached way point " + wayPointIndex);
                }
                updatePosition(id, control);
                if (movementUpdateGoal.containsKey(id)) {
//                    System.err.println("Start a new movement path.");
                    buildNewPath(id, control, this);
                } else if (control.getPath().getNbWayPoints() == wayPointIndex + 1) {
//                    System.err.println("All movement has been done.");
                    entityData.removeComponent(id, MoveToComponent.class);
                }
            }
        });
        return path;
    }

    /**
     * @return true if a new path have been build.
     */
    private boolean buildNewPath(EntityId id, MotionEvent motionControl, MotionPathListener listeners) {
        boolean result = false;
        /**
         * We build a new path starting from the current waypoint.
         */
        List<HexCoordinate> newPath = getPathfinderPath(
                new HexCoordinate(motionControl.getPath().getWayPoint(motionControl.getCurrentWayPoint() + 1)),
                movementUpdateGoal.get(id));
        if (newPath != null) {
            motionControl.stop();
            motionControl.getPath().disableDebugShape();
            MotionPath newMotion = buildPath(motionControl.getSpatial().getLocalTranslation(), newPath);
            motionControl.setPath(newMotion);
            float ms = new EntityLoader(app).loadTitanStats(
                    entityData.getComponent(id, RenderComponent.class).getName())
                    .getInitialStatsComponent().getMoveSpeed();
            float addedDist = motionControl.getSpatial().getLocalTranslation().distance(
                    newPath.get(0).toWorldPosition(mapData.getTile(newPath.get(0)).getHeight()));
            float baseDist = newPath.get(0).toWorldPosition(mapData.getTile(newPath.get(0)).getHeight())
                    .distance(newPath.get(1).toWorldPosition(mapData.getTile(newPath.get(1)).getHeight()));
            motionControl.setInitialDuration(ms * (newPath.size() + 1));// + (ms / baseDist * addedDist));
            if (listeners != null) {
                newMotion.addListener(listeners);
            } else {
                addPathListener(id, newMotion);
            }
            movementGoal.put(id, movementUpdateGoal.get(id));
            motionControl.play();
            result = true;
        } else if (movementGoal.containsKey(id)) {
            entityData.setComponent(id, new MoveToComponent(movementGoal.get(id)));
        }
        movementUpdateGoal.remove(id);
        return result;
    }

    private void updatePosition(EntityId id, MotionEvent control) {
        HexCoordinate newPos = new HexCoordinate(control.getPath()
                .getWayPoint(control.getPath().getNbWayPoints() - 1));
        Rotation rot = new HexCoordinate(control.getPath()
                .getWayPoint(control.getPath().getNbWayPoints() - 2)).getDirection(newPos);
        entityData.setComponent(id, new HexPositionComponent(newPos, rot));
    }
}

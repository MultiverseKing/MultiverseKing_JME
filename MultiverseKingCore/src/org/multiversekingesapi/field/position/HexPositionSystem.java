package org.multiversekingesapi.field.position;

import com.jme3.cinematic.MotionPath;
import com.jme3.cinematic.events.MotionEvent;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.simsilica.es.Entity;
import com.simsilica.es.EntitySet;
import org.hexgridapi.core.HexSetting;
import org.hexgridapi.core.MapData;
import org.hexgridapi.core.appstate.MapDataAppState;
import org.hexgridapi.events.TileChangeEvent;
import org.hexgridapi.events.TileChangeListener;
import org.hexgridapi.utility.HexCoordinate;
import org.multiversekingesapi.EntitySystemAppState;
import org.multiversekingesapi.SubSystem;
import org.multiversekingesapi.render.RenderComponent;
import org.multiversekingesapi.render.RenderSystem;
import org.multiversekingesapi.render.utility.Curve;

/**
 *
 * @author roah
 */
public class HexPositionSystem extends EntitySystemAppState implements SubSystem, TileChangeListener {

    private RenderSystem renderSystem;
    private MapData mapData;

    @Override
    protected EntitySet initialiseSystem() {
        renderSystem = app.getStateManager().getState(RenderSystem.class);
        mapData = app.getStateManager().getState(MapDataAppState.class).getMapData();
        mapData.registerTileChangeListener(this);
        app.getStateManager().getState(RenderSystem.class).registerSubSystem(this);
        return entityData.getEntities(RenderComponent.class, HexPositionComponent.class);
    }

    @Override
    protected void updateSystem(float tpf) {
    }

    @Override
    protected void addEntity(Entity e) {
        updateSpatialTransform(e);
    }

    @Override
    protected void updateEntity(Entity e) {
        updateSpatialTransform(e);
    }

    @Override
    protected void removeEntity(Entity e) {
        if(e.get(RenderComponent.class) == null){
            entityData.removeComponent(e.getId(), HexPositionComponent.class);
        } else if (e.get(HexPositionComponent.class) != null) {
            entityData.removeComponent(e.getId(), HexPositionComponent.class);
        }
    }

    @Override
    protected void cleanupSystem() {
        app.getStateManager().getState(RenderSystem.class).removeSubSystem(this, false);
        mapData.removeTileChangeListener(this);
    }

    @Override
    public void rootSystemIsRemoved() {
        app.getStateManager().detach(this);
    }

    private void updateSpatialTransform(Entity e) {
        Spatial s = renderSystem.getSpatial(e.getId());
        if (e.get(HexPositionComponent.class).getCurve() != null && renderSystem.getControl(e.getId(), MotionEvent.class) == null) {
            Curve curve = e.get(HexPositionComponent.class).getCurve();
            final MotionPath path = new MotionPath();
            path.addWayPoint(s.getLocalTranslation());
            for (int i = 1; i < curve.getWaypoints().size(); i++) {
                HexCoordinate pos = curve.getWaypoints().get(i);
                path.addWayPoint(pos.convertToWorldPosition(mapData.getTile(pos).getHeight()));
            }
            path.enableDebugShape(app.getAssetManager(), renderSystem.getRenderNode());
            MotionEvent motionControl = new MotionEvent(s, path, curve.getSpeed() * (curve.getWaypoints().size()));
            motionControl.setDirectionType(MotionEvent.Direction.Path);
            motionControl.play();
        } else if (e.get(HexPositionComponent.class).getCurve() != null && s.getControl(MotionEvent.class) != null) {
//            MotionEvent motionControl = s.getControl(MotionEvent.class);
//            motionControl.getPath().disableDebugShape();
////            motionControl.getPath().setPathSplineType(Spline.SplineType.Linear);
//            motionControl.getPath().removeWayPoint(motionControl.getPath().getNbWayPoints() - 2);
//            motionControl.getPath().addWayPoint(targetPos);
//            motionControl.getPath().enableDebugShape(app.getAssetManager(), renderSystemNode);
//            motionControl.play();
        } else {
            if (s.getControl(MotionEvent.class) != null) {
                MotionEvent motionControl = s.getControl(MotionEvent.class);
                motionControl.getPath().disableDebugShape();
                s.removeControl(MotionEvent.class);
            }
            Vector3f pos;
            if (mapData.getTile(e.get(HexPositionComponent.class).getPosition()) == null) {
                pos = e.get(HexPositionComponent.class).getPosition().convertToWorldPosition();
            } else {
                pos = e.get(HexPositionComponent.class).getPosition()
                        .convertToWorldPosition(mapData.getTile(
                        e.get(HexPositionComponent.class).getPosition()).getHeight());
            }
//            pos.y += 0.1f;
            s.setLocalTranslation(pos);
            s.setLocalRotation(e.get(HexPositionComponent.class).getRotation().toQuaternion());
        }
    }

    @Override
    public void onTileChange(TileChangeEvent[] events) {
        for (Entity e : entities) {
            HexCoordinate entityPos = e.get(HexPositionComponent.class).getPosition();
            for (int i = 0; i < events.length; i++) {
                if (entityPos.equals(events[i].getTilePos())) {
                    Spatial s = renderSystem.getSpatial(e.getId());
                    float posY = events[i].getNewTile().getHeight()
                            * HexSetting.FLOOR_OFFSET + 0.1f;
                    s.setLocalTranslation(s.getLocalTranslation().x, posY, s.getLocalTranslation().z);
                    break;
                }
            }
        }
    }
}

package entitysystem.render;

import entitysystem.utility.SubSystem;
import entitysystem.render.utility.SpatialInitializer;
import com.jme3.animation.AnimControl;
import com.jme3.cinematic.MotionPath;
import com.jme3.cinematic.events.MotionEvent;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import entitysystem.EntitySystemAppState;
import entitysystem.field.position.HexPositionComponent;
import entitysystem.render.utility.Curve;
import hexsystem.HexSystemAppState;
import hexsystem.MapData;
import hexsystem.events.TileChangeEvent;
import hexsystem.events.TileChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Handle the render of all entities.
 * HexGrid got his own render system.
 * @author Eike Foede, roah
 */

/*
 * Just use HexPositions, the rare cases where Spatial Positions would be used
 * should be handled seperately. Else there could be double data for the
 * position with inconsistencies between the different data.
 */
public class RenderSystem extends EntitySystemAppState implements TileChangeListener {

    private final Node renderSystemNode = new Node("RenderSystemNode");
    private HashMap<EntityId, Spatial> spatials = new HashMap<EntityId, Spatial>();
    private ArrayList<SubSystem> subSystem = new ArrayList<SubSystem>(2);
    private ArrayList<Node> subSystemNode = new ArrayList<Node>(2);
    private SpatialInitializer spatialInitializer;
    private MapData mapData;

    /**
     * Return the Animation Spatial control of an entity, if any.
     *
     * @param id of the entity.
     * @return null if no anim control.
     */
    public AnimControl getAnimControl(EntityId id) {
        if (spatials.containsKey(id)) {
            return spatials.get(id).getControl(AnimControl.class);
        } else {
            return null;
        }
    }

    public String getSpatialName(EntityId id) {
        return spatials.get(id).getName();
    }

    // <editor-fold defaultstate="collapsed" desc="SubSystem Method">
    public void registerSubSystem(SubSystem system) {
        subSystem.add(system);
    }
    
    public boolean removeSubSystem(SubSystem system) {
        if(subSystem.remove(system)){
            removeSubSystemNode(system);
            return true;
        }
        return false;
    }

    public Node addSubSystemNode(SubSystem system) {
        for(Node n : subSystemNode){
            if(n.getName().equals(system.getSubSystemName())){
                return n;
            }
        }
        Node node = new Node(system.getSubSystemName());
        subSystemNode.add(node);
        renderSystemNode.attachChild(node);
        return node;
    }
    
    public boolean removeSubSystemNode(SubSystem system){
        Node node = null;
        for(Node n : subSystemNode){
            if(n.getName().equals(system.getSubSystemName())){
                node = n;
            }
        }
        if(node != null && subSystemNode.remove(node)){
            node.detachAllChildren();
            renderSystemNode.detachChild(node);
            return true;
        }
        return false;
    }

    public boolean addSpatialToSubSystem(EntityId id, Node systemNode) {
        if (spatials.get(id) != null) {
            ((Node) renderSystemNode.getChild(systemNode.getName())).attachChild(spatials.get(id));
            return true;
        }
        return false;
    }

    public void removeSpatialFromSubSystem(EntityId id) {
        if (spatials.get(id) != null) {
            renderSystemNode.attachChild(spatials.get(id));
        }
    }

    // </editor-fold>
    
    @Override
    protected EntitySet initialiseSystem() {
        app.getRootNode().attachChild(renderSystemNode);
        renderSystemNode.setShadowMode(RenderQueue.ShadowMode.CastAndReceive); //<< diseable this to remove the shadow.
        spatialInitializer = new SpatialInitializer(app.getAssetManager());
        mapData = app.getStateManager().getState(HexSystemAppState.class).getMapData();
        mapData.registerTileChangeListener(this);
        return entityData.getEntities(RenderComponent.class, HexPositionComponent.class);
    }

    @Override
    protected void updateSystem(float tpf) {
    }

    @Override
    public void addEntity(Entity e) {
        addSpatial(e);
        updateSpatialTransform(e.getId());
    }

    private Spatial getSpatial(Entity e) {
        RenderComponent renderComp = e.get(RenderComponent.class);
        Spatial s = spatialInitializer.initialize(renderComp.getName());
        s.setName(renderComp.getName() + e.getId().toString());
        return s;
    }

    private void addSpatial(Entity e) {
        Spatial s = getSpatial(e);
        spatials.put(e.getId(), s);
        renderSystemNode.attachChild(s);
    }

    private void switchSpatial(Entity e) {
        Spatial s = getSpatial(e);
        Node parent = spatials.get(e.getId()).getParent();
        spatials.get(e.getId()).removeFromParent();
        spatials.put(e.getId(), s);
        parent.attachChild(s);
    }

    @Override
    protected void updateEntity(Entity e) {
        /**
         * Update the spatial if it need to.
         */
        Spatial s = spatials.get(e.getId());
        String eName = (String) (e.get(RenderComponent.class).getName() + e.getId().toString());
        if (s == null) {
            addSpatial(e);
        } else if (!eName.equals(s.getName())) {
            switchSpatial(e);
        }
        updateSpatialTransform(e.getId());
    }

    private void updateSpatialTransform(EntityId id) {
        HexPositionComponent positionComp = entities.getEntity(id).get(HexPositionComponent.class);
        Spatial s = spatials.get(id);
        if (positionComp.getCurve() != null && s.getControl(MotionEvent.class) == null) {
            Curve curve = entities.getEntity(id).get(HexPositionComponent.class).getCurve();
            final MotionPath path = new MotionPath();
            path.addWayPoint(spatials.get(id).getLocalTranslation());
            for(int i = 1; i < curve.getWaypoints().size(); i++){
                path.addWayPoint(mapData.convertTileToWorldPosition(curve.getWaypoints().get(i)));
            }
            path.enableDebugShape(app.getAssetManager(), renderSystemNode);
            MotionEvent motionControl = new MotionEvent(s, path, curve.getSpeed()* (curve.getWaypoints().size()));
            motionControl.setDirectionType(MotionEvent.Direction.Path);
            motionControl.play();
        } else if (positionComp.getCurve() != null && s.getControl(MotionEvent.class) != null) {
//            MotionEvent motionControl = s.getControl(MotionEvent.class);
//            motionControl.getPath().disableDebugShape();
////            motionControl.getPath().setPathSplineType(Spline.SplineType.Linear);
//            motionControl.getPath().removeWayPoint(motionControl.getPath().getNbWayPoints() - 2);
//            motionControl.getPath().addWayPoint(targetPos);
//            motionControl.getPath().enableDebugShape(app.getAssetManager(), renderSystemNode);
//            motionControl.play();
        } else {
            if(s.getControl(MotionEvent.class) != null){
                MotionEvent motionControl = s.getControl(MotionEvent.class);
                motionControl.getPath().disableDebugShape();
                s.removeControl(MotionEvent.class);
            }
            s.setLocalTranslation(mapData.convertTileToWorldPosition(positionComp.getPosition()));
            s.setLocalRotation(positionComp.getRotation().toQuaternion());
        }
    }

    @Override
    public void removeEntity(Entity e) {
        if (e.get(RenderComponent.class) == null) {
            removeSpatial(e.getId());
        } else if (e.get(HexPositionComponent.class) == null) {
            entityData.removeComponent(e.getId(), RenderComponent.class);
        }
    }

    private void removeSpatial(EntityId id) {
        Spatial s = spatials.get(id);
        if (s.getControl(MotionEvent.class) != null) {
            s.getControl(MotionEvent.class).getPath().disableDebugShape();
        }
        if (renderSystemNode.detachChild(s) == -1) {
            for (Node n : subSystemNode) {
                if (n.detachChild(s) != -1) {
                    break;
                }
            }
        }
        spatials.remove(id);
    }
    
    @Override
    protected void cleanupSystem() {
        for (SubSystem s : subSystem) {
            s.removeSubSystem();
        }
        spatials.clear();
        renderSystemNode.removeFromParent();
    }
    
    public void tileChange(TileChangeEvent event) {
        Set<EntityId> key = spatials.keySet();
        for (EntityId id : key) {
            if (entityData.getComponent(id, HexPositionComponent.class).getPosition().equals(event.getTilePos())) {
                Vector3f currentLoc = spatials.get(id).getLocalTranslation();
                spatials.get(id).setLocalTranslation(currentLoc.x, event.getNewTile().getHeight(), currentLoc.z);
            }
        }
    }
}

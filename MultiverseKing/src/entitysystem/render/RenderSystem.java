package entitysystem.render;

import entitysystem.SubSystem;
import entitysystem.render.utility.SpatialInitializer;
import com.jme3.animation.AnimControl;
import com.jme3.cinematic.MotionPath;
import com.jme3.cinematic.events.MotionEvent;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Ray;
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
import hexsystem.area.MapDataAppState;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import org.hexgridapi.core.HexSetting;
import org.hexgridapi.core.MapData;
import org.hexgridapi.events.TileChangeEvent;
import org.hexgridapi.events.TileChangeListener;
import org.hexgridapi.utility.HexCoordinate;

/**
 * Handle the render of all entities. HexGrid got his own render system.
 *
 * @author Eike Foede, roah
 */

/*
 * Just use HexPositions, the rare cases where Spatial Positions would be used
 * should be handled seperately. Else there could be double data for the
 * position with inconsistencies between the different data.
 */
public class RenderSystem extends EntitySystemAppState implements TileChangeListener {

    private final Node renderSystemNode = new Node("RenderSystemNode");
    private HashMap<EntityId, Spatial> spatials = new HashMap<>();
    private ArrayList<SubSystem> subSystems = new ArrayList<>(2);
    private ArrayList<Node> subSystemNodes = new ArrayList<>(2);
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
            Spatial s = spatials.get(id);
            
            AnimControl ctrl = s.getControl(AnimControl.class);
            if(ctrl == null && s instanceof Node && !((Node)s).getChildren().isEmpty()){
                for(Spatial child : ((Node)s).getChildren()){
                    ctrl = child.getControl(AnimControl.class);
                    if(ctrl != null){
                        return ctrl;
                    }
                }
            } else {
                return ctrl;
            }
        }
        return null;
    }

    public String getSpatialName(EntityId id) {
        return spatials.get(id).getName();
    }

    public String getRenderNodeName() {
        return renderSystemNode.getName();
    }

    // <editor-fold defaultstate="collapsed" desc="SubSystem Method">
    /**
     * Register a System to work with entity Spatial.
     */
    public void registerSubSystem(SubSystem system) {
        registerSubSystem(system, false);
    }

    /**
     * Register a system to work with spatial having his own node settup.
     */
    public Node registerSubSystem(SubSystem system, boolean addNode) {
        subSystems.add(system);
        if (addNode) {
            return addSubSystemNode(system);
        }
        return null;
    }

    public void removeSubSystem(SubSystem system, boolean clear) {
        if (subSystems.remove(system)) {
            removeSubSystemNode(system, clear);
        }
    }

    public Node addSubSystemNode(SubSystem system) {
        for (Node n : subSystemNodes) {
            if (n.getName().equals(system.getSubSystemName())) {
                return n;
            }
        }
        Node node = new Node(system.getSubSystemName());
        subSystemNodes.add(node);
        renderSystemNode.attachChild(node);
        return node;
    }

    public void removeSubSystemNode(SubSystem system, boolean clear) {
        Node node = null;
        for (Node n : subSystemNodes) {
            if (n.getName().equals(system.getSubSystemName())) {
                node = n;
                break;
            }
        }
        if (node != null && subSystemNodes.remove(node)) {
            if (!clear) {
                for (Spatial s : node.getChildren()) {
                    if (spatials.containsValue(s)) {
                        renderSystemNode.attachChild(s);
                    }
                }
            }
            renderSystemNode.detachChild(node);
        }
    }

    private void addSpatialToSubSystem(EntityId id, SubSystem system) {
        if (spatials.get(id) != null) {
            Node subSystemNode = (Node) renderSystemNode.getChild(system.getSubSystemName());
            if (!subSystemNode.hasChild(spatials.get(id))) {
                subSystemNode.attachChild(spatials.get(id));
            }
        }
    }

    private void addSpatialToRenderNode(EntityId id) {
        if (spatials.get(id) != null) {
            renderSystemNode.attachChild(spatials.get(id));
        }
    }

    public CollisionResults subSystemCollideWith(SubSystem system, Ray ray) {
        if(subSystems.contains(system)){
            CollisionResults result = new CollisionResults();
            ((Node) renderSystemNode.getChild(system.getSubSystemName())).collideWith(ray, result);
            return result;
        }
        return null;
    }
    // </editor-fold>

    @Override
    protected EntitySet initialiseSystem() {
        app.getRootNode().attachChild(renderSystemNode);
        renderSystemNode.setShadowMode(RenderQueue.ShadowMode.Cast); //<< diseable this to remove the shadow.
        spatialInitializer = new SpatialInitializer(app.getAssetManager());
        mapData = app.getStateManager().getState(MapDataAppState.class).getMapData();
        mapData.registerTileChangeListener(this);
        return entityData.getEntities(RenderComponent.class, HexPositionComponent.class);
    }

    @Override
    protected void updateSystem(float tpf) {
    }

    @Override
    public void addEntity(Entity e) {
        addSpatial(e);
        if(e.get(RenderComponent.class).isVisible()){
            updateSpatialTransform(e.getId());
            if (e.get(RenderComponent.class).getSystem() != null) {
                addSpatialToSubSystem(e.getId(), e.get(RenderComponent.class).getSystem());
            }
        }
    }

    private Spatial initialiseSpatial(Entity e) {
        RenderComponent renderComp = e.get(RenderComponent.class);
        Spatial s = spatialInitializer.initialize(renderComp.getName(), renderComp.getRenderType());
        s.setName(renderComp.getName() + renderComp.getRenderType() + e.getId().toString());
        if(renderComp.getRenderType().equals(RenderComponent.RenderType.Debug)){
            s.setShadowMode(RenderQueue.ShadowMode.Cast);
        } else {
            s.setShadowMode(RenderQueue.ShadowMode.Inherit);
        }
        return s;
    }

    private void addSpatial(Entity e) {
        Spatial s = initialiseSpatial(e);
        spatials.put(e.getId(), s);
        if(e.get(RenderComponent.class).isVisible()){
            renderSystemNode.attachChild(s);
        }
    }

    private void switchSpatial(Entity e) {
        Spatial s = initialiseSpatial(e);
        if(e.get(RenderComponent.class).isVisible()){
            Node parent = spatials.get(e.getId()).getParent();
            spatials.get(e.getId()).removeFromParent();
            parent.attachChild(s);
        } else {
            spatials.get(e.getId()).removeFromParent();
        }
        spatials.put(e.getId(), s);
    }

    @Override
    protected void updateEntity(Entity e) {
        /**
         * Update the spatial if it need to.
         */
        Spatial s = spatials.get(e.getId());
        String eName = (String) (e.get(RenderComponent.class).getName() + e.get(RenderComponent.class).getRenderType() + e.getId().toString());
        if (s == null) {
            addSpatial(e);
        } else if (!eName.equals(s.getName())) {
            switchSpatial(e);
        }
        if(e.get(RenderComponent.class).isVisible()){
            if (e.get(RenderComponent.class).getSystem() != null) {
                addSpatialToSubSystem(e.getId(), e.get(RenderComponent.class).getSystem());
            } else {
                addSpatialToRenderNode(e.getId());
            }
            updateSpatialTransform(e.getId());
        } else if(spatials.get(e.getId()).getParent() != null){
            spatials.get(e.getId()).removeFromParent();
        }
    }

    private void updateSpatialTransform(EntityId id) {
        HexPositionComponent positionComp = entities.getEntity(id).get(HexPositionComponent.class);
        Spatial s = spatials.get(id);
        if (positionComp.getCurve() != null && s.getControl(MotionEvent.class) == null) {
            Curve curve = entities.getEntity(id).get(HexPositionComponent.class).getCurve();
            final MotionPath path = new MotionPath();
            path.addWayPoint(spatials.get(id).getLocalTranslation());
            for (int i = 1; i < curve.getWaypoints().size(); i++) {
                HexCoordinate pos = curve.getWaypoints().get(i);
                path.addWayPoint(pos.convertToWorldPosition(mapData.getTile(pos).getHeight()));
            }
            path.enableDebugShape(app.getAssetManager(), renderSystemNode);
            MotionEvent motionControl = new MotionEvent(s, path, curve.getSpeed() * (curve.getWaypoints().size()));
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
            if (s.getControl(MotionEvent.class) != null) {
                MotionEvent motionControl = s.getControl(MotionEvent.class);
                motionControl.getPath().disableDebugShape();
                s.removeControl(MotionEvent.class);
            }
            Vector3f pos = positionComp.getPosition().convertToWorldPosition(mapData.getTile(positionComp.getPosition()).getHeight());
//            pos.y += 0.1f;
            s.setLocalTranslation(pos);
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
            for (Node n : subSystemNodes) {
                if (n.detachChild(s) != -1) {
                    break;
                }
            }
        }
        spatials.remove(id);
    }

    @Override
    protected void cleanupSystem() {
        for (SubSystem s : subSystems) {
            s.removeSubSystem();
        }
        spatials.clear();
        renderSystemNode.removeFromParent();
    }

    @Override
    public void tileChange(TileChangeEvent event) {
        Set<EntityId> key = spatials.keySet();
        for (EntityId id : key) {
            if (entityData.getComponent(id, HexPositionComponent.class).getPosition().equals(event.getTilePos())) {
                Vector3f currentLoc = spatials.get(id).getLocalTranslation();
                spatials.get(id).setLocalTranslation(currentLoc.x, event.getNewTile().getHeight()
                    * HexSetting.FLOOR_OFFSET + 0.1f, currentLoc.z);
            }
        }
    }
}

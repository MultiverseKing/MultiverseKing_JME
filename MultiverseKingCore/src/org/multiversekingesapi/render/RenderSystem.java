package org.multiversekingesapi.render;

import org.multiversekingesapi.SubSystem;
import org.multiversekingesapi.render.utility.SpatialInitializer;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Ray;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import org.multiversekingesapi.EntitySystemAppState;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.multiversekingesapi.field.position.HexPositionComponent;

/**
 * Handle the render of all entities, spatial loading parenting etc...
 * /!\ Does not handle the positioning of entity.
 * HexGrid got his own render system.
 *
 * @author Eike Foede, roah
 */
public class RenderSystem extends EntitySystemAppState {

    private final Node renderSystemNode = new Node("RenderSystemNode");
    private HashMap<EntityId, Spatial> spatials = new HashMap<>();
    private ArrayList<SubSystem> subSystems = new ArrayList<>(3);
    private SpatialInitializer spatialInitializer;

    // <editor-fold defaultstate="collapsed" desc="Overrided Method">
    @Override
    protected EntitySet initialiseSystem() {
        app.getRootNode().attachChild(renderSystemNode);
        renderSystemNode.setShadowMode(RenderQueue.ShadowMode.Cast); //<< diseable this to remove the shadow.
        spatialInitializer = new SpatialInitializer(app.getAssetManager());
        return entityData.getEntities(RenderComponent.class, HexPositionComponent.class);
    }

    @Override
    protected void updateSystem(float tpf) {
    }

    @Override
    public void addEntity(Entity e) {
        if (addSpatial(e)) {
            if (e.get(RenderComponent.class).getSubSystem() != null) {
                addSpatialToSubSystem(e.getId(), e.get(RenderComponent.class).getSubSystem());
            }
        }
    }

    @Override
    protected void updateEntity(Entity e) {
        /**
         * Update the spatial if it need to.
         */
        Spatial s = spatials.get(e.getId());
        String eName = e.get(RenderComponent.class).getName() + e.get(RenderComponent.class).getRenderType() + e.getId().toString();
        if (s == null) {
            addSpatial(e);
        } else if (!eName.equals(s.getName())) {
            switchSpatial(e);
        }
        if (spatials.get(e.getId()).getParent() == null
                && e.get(RenderComponent.class).isVisible()) {
            if (e.get(RenderComponent.class).getSubSystem() != null) {
                addSpatialToSubSystem(e.getId(), e.get(RenderComponent.class).getSubSystem());
            } else {
                addSpatialToRenderNode(e.getId());
            }
        } else if (spatials.get(e.getId()).getParent() != null
                && !e.get(RenderComponent.class).isVisible()) {
            spatials.get(e.getId()).removeFromParent();
        }
    }

    @Override
    public void removeEntity(Entity e) {
        spatials.get(e.getId()).removeFromParent();
        spatials.remove(e.getId());
    }

    @Override
    protected void cleanupSystem() {
        for (SubSystem s : subSystems) {
            s.rootSystemIsRemoved();
        }
        spatials.clear();
        renderSystemNode.removeFromParent();
    }
    // </editor-fold>

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
    public void registerSubSystem(SubSystem subSystem, boolean addNode) {
        Node n = null;
        if (subSystems.contains(subSystem)) {
            n = (Node) renderSystemNode.getChild(subSystem.getClass().getName());
        } else {
            subSystems.add(subSystem);
        }
        if (addNode && n == null) {
            n = new Node(subSystem.getClass().getName());
            renderSystemNode.attachChild(n);
            for (Entity e : entities) {
                if (e.get(RenderComponent.class).getSubSystem().equals(subSystem)) {
                    n.attachChild(spatials.get(e.getId()));
                }
            }
        } else if (!addNode && n != null) {
            renderSystemNode.detachChild(n);
        }
    }

    public void removeSubSystem(SubSystem subSystem, boolean clear) {
        if (subSystems.remove(subSystem)) {
            Node n = (Node) renderSystemNode.getChild(subSystem.getClass().getName());
            if (n != null && !clear) {
                for (Spatial s : n.getChildren()) {
                    if (spatials.containsValue(s)) {
                        renderSystemNode.attachChild(s);
                    }
                }
            }
            renderSystemNode.detachChild(n);
        } else {
            Logger.getGlobal().log(Level.WARNING, "{0} : Trying to remove a system not initialized : {1} ",
                    new Object[]{getClass().getName(), subSystem.getClass().getName()});

        }
    }

    /**
     * @deprecated
     */
    public CollisionResults subSystemCollideWith(SubSystem subSystem, Ray ray) {
        if (subSystems.contains(subSystem)) {
            CollisionResults result = new CollisionResults();
            ((Node) renderSystemNode.getChild(subSystem.getClass().getName())).collideWith(ray, result);
            return result;
        }
        return null;
    }
    // </editor-fold>

    private Spatial initialiseSpatial(Entity e) {
        RenderComponent renderComp = e.get(RenderComponent.class);
        Spatial s = spatialInitializer.initialize(renderComp.getName(), renderComp.getRenderType());
        s.setName(renderComp.getName() + renderComp.getRenderType() + e.getId().toString());
        if (renderComp.getRenderType().equals(RenderComponent.RenderType.Debug)) {
            s.setShadowMode(RenderQueue.ShadowMode.Cast);
        } else {
            s.setShadowMode(RenderQueue.ShadowMode.Inherit);
        }
        return s;
    }

    /**
     * @return true if the spatial is added to the render Node.
     * (aka : is visible)
     */
    private boolean addSpatial(Entity e) {
        Spatial s = initialiseSpatial(e);
        spatials.put(e.getId(), s);
        if (e.get(RenderComponent.class).isVisible()) {
            renderSystemNode.attachChild(s);
            return true;
        }
        return false;
    }

    private void switchSpatial(Entity e) {
        Spatial newSpatial = initialiseSpatial(e);
        if (e.get(RenderComponent.class).isVisible()) {
            Node parent = spatials.get(e.getId()).getParent();
            parent.attachChild(newSpatial);
        }
        spatials.get(e.getId()).removeFromParent();
        spatials.put(e.getId(), newSpatial);
    }

    private void addSpatialToSubSystem(EntityId id, SubSystem system) {
        if (spatials.get(id) != null) {
            Node subSystemNode = (Node) renderSystemNode.getChild(system.getClass().getName());
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

    /**
     * Return the Animation Spatial control of an entity, if any.
     *
     * @param id of the entity.
     * @return null if no anim control.
     */
    public <T extends Control> T getControl(EntityId id, Class<T> controlType) {
        if (spatials.containsKey(id)) {
            Spatial s = spatials.get(id);
            T ctrl = s.getControl(controlType);
            if (ctrl == null && s instanceof Node && !((Node) s).getChildren().isEmpty()) {
                for (Spatial child : ((Node) s).getChildren()) {
                    ctrl = child.getControl(controlType);
                    if (ctrl != null) {
                        return ctrl;
                    }
                }
            }
            return ctrl;
        }
        return null;
    }

    public String getRenderNodeName() {
        return renderSystemNode.getName();
    }

    public Node getRenderNode() {
        return renderSystemNode;
    }

    public String getSpatialName(EntityId id) {
        return spatials.get(id).getName();
    }

    public Spatial getSpatial(EntityId id) {
        return spatials.get(id);
    }
}

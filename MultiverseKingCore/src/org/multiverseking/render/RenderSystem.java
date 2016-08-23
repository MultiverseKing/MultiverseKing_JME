package org.multiverseking.render;

import com.jme3.collision.CollisionResults;
import com.jme3.math.Ray;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import java.util.ArrayList;
import java.util.HashMap;
import org.multiverseking.core.utility.EntitySystemAppState;
import org.multiverseking.core.utility.RootSystem;
import org.multiverseking.core.utility.SubSystem;
import org.multiverseking.render.utility.SpatialInitializer;
import org.slf4j.LoggerFactory;

/**
 * Handle the render of all entities, spatial loading parenting etc... /!\ Does
 * not handle the positioning of entity. HexGrid got his own render system.
 *
 * @author Eike Foede, roah
 */
public class RenderSystem extends EntitySystemAppState implements RootSystem {

    private final Node renderSystemNode = new Node("RenderSystemNode");
    private HashMap<EntityId, Spatial> spatials = new HashMap<>();
    private ArrayList<SubSystem> subSystems = new ArrayList<>(3);
    private SpatialInitializer spatialInitializer;

    // <editor-fold defaultstate="collapsed" desc="Entity System">
    @Override
    protected EntitySet initialiseSystem() {
        app.getRootNode().attachChild(renderSystemNode);
        renderSystemNode.setShadowMode(RenderQueue.ShadowMode.Cast); //<< diseable this to remove the shadow. -50%fps...
        spatialInitializer = new SpatialInitializer(app.getAssetManager(), "Models");

        return entityData.getEntities(RenderComponent.class);
    }

    @Override
    protected void updateSystem(float tpf) {
    }

    @Override
    public void addEntity(Entity e) {        
        spatials.put(e.getId(), initialiseSpatial(e));
        Spatial parent = spatials.get(e.get(RenderComponent.class).getParent());
        if (parent != null) {
            ((Node) parent).attachChild(spatials.get(e.getId()));
        } else if (getSubSystemNode(e.get(RenderComponent.class).getSubSystem()) != null) {
            addSpatialToSubSystem(e.getId(), e.get(RenderComponent.class).getSubSystem());
        } else {
            renderSystemNode.attachChild(spatials.get(e.getId()));
        }

        Control[] control = e.get(RenderComponent.class).getControl();
        if (control != null) {
            Spatial s = spatials.get(e.getId());
            for (Control c : control) {
                s.addControl(c);
            }
        }

        if (!e.get(RenderComponent.class).isVisible()) {
            spatials.get(e.getId()).setCullHint(Spatial.CullHint.Always);
        }

    }

    @Override
    protected void updateEntity(Entity e) {
        Spatial s = spatials.get(e.getId());

        /**
         * Update the spatial geometry if it need to.
         */
        String eName = computeSpatialName(e);
        if (!eName.equals(s.getName())) {
            switchSpatial(e);
        }

        /**
         * Update the spatial parenting
         */
        RenderComponent renderComp = e.get(RenderComponent.class);
        Spatial renderCompParent = spatials.get(renderComp.getParent());
        if (renderCompParent != null && !s.getParent().getName().equals(renderCompParent.getName())) {
            ((Node) renderCompParent).attachChild(spatials.get(e.getId()));
        } else if (renderCompParent != null && s.getParent().getName().equals(renderCompParent.getName())) {
            // If the parent hasn't change do nothing
        } else if (e.get(RenderComponent.class).getSubSystem() != null
                && getSubSystemNode(e.get(RenderComponent.class).getSubSystem()) != null) {
            // If the subSystem Node is currently processed.
            if (getSubSystemNode(e.get(RenderComponent.class)
                    .getSubSystem()).getName().equals(s.getParent().getName())) {
                // If the SubSystem is the same than the parent do nothing
            } else {
                // If the spatial parent is not the subSystem node
                addSpatialToSubSystem(e.getId(), e.get(RenderComponent.class).getSubSystem());
            }
        } else {
            renderSystemNode.attachChild(spatials.get(e.getId()));
        }

//        if (s.getParent() == null && entityRender.getParent() != null
//                && !spatials.containsKey(entityRender.getParent())) {
//            // We attach the spatial to his parent if currently processed
//            ((Node) spatials.get(entityRender.getParent())).attachChild(s);
//        } else if ((s.getParent() == null
//                // If the spatial is not attach to the scene  
//                // We attach the Spatial to his corresponding system
//                // Else it is added to the render Node
//                || (entityRender.getParent() != null
//                && !spatials.containsKey(entityRender.getParent())))
//                && !addSpatialToSubSystem(e.getId(), entityRender.getSubSystem())) {
//            // If the spatial is attach to the scene but the parenting has been change
//            // and the new parent is not currently processed we add it to the 
//            // corresponding System else on his the render Node
//            addSpatialToRenderNode(e.getId());
//        } else if () {
//            // we check if the parent id is not currently processed
//        } else if ( && !computeSpatialName((Entity) entityData.getEntity(
//                entityRender.getParent(), RenderComponent.class))
//                .equals(s.getParent().getName())) {
//            // if the current parent Spatial Id is not the same than render component Parent Spatial ID
//        } else {
//        }
        /**
         * Update the spatial visibility if it need to.
         */
        if (renderComp.isVisible()) {
            s.setCullHint(Spatial.CullHint.Inherit);
        } else {
            s.setCullHint(Spatial.CullHint.Always);
        }
    }

    private String computeSpatialName(Entity e) {
        return e.get(RenderComponent.class).getName() + e.get(RenderComponent.class).getRenderType() + e.getId().toString();
    }

    @Override
    public void removeEntity(Entity e) {
        spatials.get(e.getId()).removeFromParent();
        spatials.remove(e.getId());
    }

    @Override
    public void cleanupSubSystem() {
        for (SubSystem s : subSystems) {
            s.rootSystemIsRemoved();
        }
    }

    @Override
    protected void cleanupSystem() {
        cleanupSubSystem();
        spatials.clear();
        renderSystemNode.removeFromParent();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="SubSystem Method">
    /**
     * Register a System to work with entity Spatial. (No node is added for that
     * system)
     */
    @Override
    public void registerSubSystem(SubSystem system) {
        registerSubSystem(system, false);
    }

    /**
     * Register a system to work with spatial having his own node settup.
     *
     * @param subSystem The system to register.
     * @param addNode Set it to true if the system require to do some operation
     * on his own.
     * @return A generated node for the system to work with if requested else
     * null.
     */
    public Node registerSubSystem(SubSystem subSystem, boolean addNode) {
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
                if (e.get(RenderComponent.class).getSubSystem() != null
                        && e.get(RenderComponent.class).getSubSystem().equals(subSystem)) {
                    n.attachChild(spatials.get(e.getId()));
                }
            }
        } else if (addNode && n != null && !n.getName().equals(subSystem.getClass().getName())) {
            n.setName(subSystem.getClass().getName());
        } else if (!addNode && n != null) {
            renderSystemNode.detachChild(n);
        }
        return n;
    }

    /**
     * Remove a subSystem ratached to this system. Clearing all spatial that
     * belong to that subSystem.
     *
     * @param subSystem the subSystem to remove.
     */
    @Override
    public void removeSubSystem(SubSystem subSystem) {
        removeSubSystem(subSystem, true);
    }

    /**
     * Remove a subSystem ratached to this system.
     *
     * @param subSystem the subSystem to remove.
     * @param clear does all spatial belong to that subSystem have to be deleted
     * ?
     */
    public void removeSubSystem(SubSystem subSystem, boolean clear) {
        if (subSystems.remove(subSystem)) {
            Node n = (Node) renderSystemNode.getChild(subSystem.getClass().getName());
            if (n != null && !clear) {
                for (Spatial s : n.getChildren()) {
                    if (spatials.containsValue(s)) {
                        renderSystemNode.attachChild(s);
                    }
                }
            } else if (n != null) {
                renderSystemNode.detachChild(n);
            }
        } else {
            LoggerFactory.getLogger(this.getClass()).warn(
                    "Trying to remove a subSystem not referenced : {} ",
                    new Object[]{subSystem.getClass().getName()});
        }
    }

    public Node getSubSystemNode(SubSystem subSystem) {
        if (subSystems.contains(subSystem)) {
            Node n = (Node) renderSystemNode.getChild(subSystem.getClass().getName());
            if (n != null) {
                return n;
            } else {
                LoggerFactory.getLogger(this.getClass()).info(
                        "No Node referenced for : {} ",
                        new Object[]{subSystem.getClass().getName()});
            }
        } else {
                LoggerFactory.getLogger(this.getClass()).warn(
                        "Trying to get a Node for a subSystem not referenced : {1} ",
                    new Object[]{subSystem.getClass().getName()});
        }
        return null;
    }

    /**
     * @deprecated use {@link #getSubSystemNode(org.multiverseking.SubSystem) }
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
        s.setName(computeSpatialName(e));
        if (renderComp.getRenderType().equals(RenderComponent.RenderType.DEBUG)) {
            s.setShadowMode(RenderQueue.ShadowMode.Cast);
        } else {
            s.setShadowMode(RenderQueue.ShadowMode.Inherit);
        }
        return s;
    }

    private void switchSpatial(Entity e) {
        Spatial newSpatial = initialiseSpatial(e);
        spatials.get(e.getId()).getParent().attachChild(newSpatial);
        if (!e.get(RenderComponent.class).isVisible()) {

        }
        spatials.get(e.getId()).removeFromParent();
        spatials.put(e.getId(), newSpatial);
    }

    private boolean addSpatialToSubSystem(EntityId id, SubSystem system) {
        if (system == null) {
            return false;
        }
        Node subSystemNode = (Node) renderSystemNode.getChild(system.getClass().getName());
        subSystemNode.attachChild(spatials.get(id));
        return true;
    }

    private void addSpatialToRenderNode(EntityId id) {
        if (spatials.get(id) != null) {
            renderSystemNode.attachChild(spatials.get(id));
        }
    }

    /**
     * Return the Spatial control of an entity, if any.
     *
     * @param id of the entity.
     * @param control the control to add.
     * @return false if there is no spatial for that entity.
     */
    public boolean setControl(EntityId id, Control control) {
        if (spatials.containsKey(id)) {
            spatials.get(id).addControl(control);
            return true;
        }
        return false;
    }

    /**
     * Return the control of the define entity spatial if any.
     *
     * @param id of the entity.
     * @return null if no control is found.
     */
    public <T extends Control> T getControl(EntityId id, Class<T> controlType) {
        if (spatials.containsKey(id)) {
            Spatial s = spatials.get(id);
            return searchControl(s, controlType);
        }
        return null;
    }

    private <T extends Control> T searchControl(Spatial s, Class<T> controlType) {
        T ctrl = s.getControl(controlType);
        if (ctrl == null && s instanceof Node && !((Node) s).getChildren().isEmpty()) {
            for (Spatial child : ((Node) s).getChildren()) {
                ctrl = searchControl(child, controlType);
//                ctrl = child.getControl(controlType);
                if (ctrl != null) {
                    return ctrl;
                }
            }
        }
        return ctrl;
    }

    public String getRenderNodeName() {
        return renderSystemNode.getName();
    }

    public String getSpatialName(EntityId id) {
        return spatials.get(id).getName();
    }

    public Spatial getSpatial(EntityId id) {
        return spatials.get(id);
    }
}

package entitysystem.field;

import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Ray;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import entitysystem.EntitySystemAppState;
import entitysystem.field.position.HexPositionComponent;
import entitysystem.render.EntityRenderSystem;
import entitysystem.render.EntityRenderComponent;
import hexsystem.HexMapMouseInput;
import hexsystem.events.HexMapInputEvent;
import hexsystem.events.HexMapInputListener;
import hexsystem.events.HexMapRayListener;
import java.util.ArrayList;

/**
 * How the player can interact with element on the field. This system need the
 * EntityRenderSystem && HexMapMouseInput to work or it can't work properly
 * since it need raycast and raycast need object to be rendered on the field to
 * collide with.
 *
 * @author roah
 */
public class EntityFieldSystem extends EntitySystemAppState implements HexMapRayListener {

    /**
     * Contain all Element the player can interact with on the Field.
     */
    private ArrayList<EntityId> elements = new ArrayList<EntityId>();
    private Node renderSystemNode;
    private Spatial rayDebug;
    private HexMapMouseInput mouseInput;

    @Override
    protected EntitySet initialiseSystem() {
        if (app.getStateManager().getState(EntityRenderSystem.class) == null) {
            app.getStateManager().attach(new EntityRenderSystem());
        }
        if (app.getStateManager().getState(HexMapMouseInput.class) == null) {
            app.getStateManager().attach(new HexMapMouseInput());
        }

        mouseInput = app.getStateManager().getState(HexMapMouseInput.class);
        renderSystemNode = app.getStateManager().getState(EntityRenderSystem.class).getNode();
        rayDebug = mouseInput.getRayDebug();
        mouseInput.registerRayInputListener(this);
        return entityData.getEntities(EntityRenderComponent.class, EntityFieldComponent.class);
    }

    @Override
    protected void updateSystem(float tpf) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void addEntity(Entity e) {
        elements.add(e.getId());
    }

    @Override
    protected void updateEntity(Entity e) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void removeEntity(Entity e) {
        elements.remove(e.getId());
    }

    public HexMapInputEvent leftRayInputAction(Ray ray) {
        return null;
    }

    public HexMapInputEvent rightRayInputAction(Ray ray) {
        CollisionResults results = new CollisionResults();
//        Ray ray = event.getLastUsedRay();
//        renderSystemNode.collideWith(event.getLastUsedRay(), results);
        app.getRootNode().getChild("RenderSystemNode").collideWith(ray, results);
        if (results.size() != 0) {
            if (results.size() > 0) {
                CollisionResult closest = results.getClosestCollision();

                for (EntityId id : elements) {
                    if (closest.getGeometry().getParent().getName().equals(id.toString())) {
                        rayDebug.setLocalTranslation(closest.getContactPoint());
                        app.getRootNode().attachChild(rayDebug);
                        openElementAction(id);
                        return new HexMapInputEvent(entityData.getComponent(id,
                                HexPositionComponent.class).getPosition(), ray);
                    }
                }
            }
        }
        return null;
    }

    public void leftMouseActionResult(HexMapInputEvent event) {
    }

    public void rightMouseActionResult(HexMapInputEvent event) {
        for (EntityId id : elements) {
            HexPositionComponent posComp = entityData.getComponent(id, HexPositionComponent.class);
            if (posComp != null && posComp.getPosition().equals(event.getEventPosition())) {
                openElementAction(id);
                return;
            }
        }
    }

    private void openElementAction(EntityId id) {
        /**
         * TODO
         */
    }

    @Override
    protected void cleanupSystem() {
        elements.clear();
    }
}
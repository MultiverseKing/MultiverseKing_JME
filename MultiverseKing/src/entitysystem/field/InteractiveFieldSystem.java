package entitysystem.field;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import entitysystem.EntitySystemAppState;
import entitysystem.render.RenderSystem;
import hexsystem.HexMapMouseInput;
import hexsystem.events.HexMapInputEvent;
import java.util.HashMap;
import kingofmultiverse.MultiverseMain;
import kingofmultiverse.RTSCamera;
import tonegod.gui.controls.menuing.Menu;
import tonegod.gui.core.Screen;
import entitysystem.tonegod.ToneControl;
import hexsystem.events.HexMapInputListener;
import java.util.logging.Logger;

/**
 * How the player can interact with element on the field. This system need the
 * EntityRenderSystem && HexMapMouseInput to work or it can't work properly
 * since it need raycast and raycast need object to be rendered on the field to
 * collide with.
 *
 * @todo Show a popup menu when an error occur and have been catch internaly.
 * @author roah
 */
public class InteractiveFieldSystem extends EntitySystemAppState implements HexMapInputListener {

    /**
     * Map containing everything the player can interact with on the Field.
     */
    private HashMap<EntityId, ToneControl> controls = new HashMap<EntityId, ToneControl>();
    private Screen screen;
    private Camera cam;
    private RenderSystem renderSystem = null;

    @Override
    protected EntitySet initialiseSystem() {
        if (app.getStateManager().getState(RenderSystem.class) == null
                || app.getStateManager().getState(HexMapMouseInput.class) == null) {
            Logger.getLogger(InteractiveFieldSystem.class.getName()).warning(
                    "This System need RenderSystem and HexMapMouseInputSystem to work, it is removed.");
            app.getStateManager().detach(this);
            return null;
        }
        screen = ((MultiverseMain) app).getScreen();
        screen.setUse3DSceneSupport(true);
        cam = app.getStateManager().getState(RTSCamera.class).getCamera();
        renderSystem = app.getStateManager().getState(RenderSystem.class);
        app.getStateManager().getState(HexMapMouseInput.class).registerTileInputListener(this);
        return entityData.getEntities(FieldGUIComponent.class);
    }

    @Override
    protected void updateSystem(float tpf) {
        
    }

    @Override
    protected void addEntity(Entity e) {
        FieldGUIComponent.EntityType field = e.get(FieldGUIComponent.class).getEntityType();
        ToneControl control = new ToneControl(e.getId(), this, screen, field, cam);
        renderSystem.addControl(e.getId(), control);
        controls.put(e.getId(), control);
    }

    @Override
    protected void updateEntity(Entity e) {
        /**
         * We check if the spatial is on the screen. If not we remove the
         * Component from this entity.
         */
        if (controls.get(e.getId()).getSpatial() != null) {
            /**
             * We check if the menu associated with the spatial is the same the
             * current, if not we change it.
             */
            FieldGUIComponent.EntityType field = e.get(FieldGUIComponent.class).getEntityType();
            if(!field.equals(controls.get(e.getId()).getField())){
                controls.get(e.getId()).updateMenuElement(field);
            }
        } else {
            entityData.removeComponent(e.getId(), FieldGUIComponent.class);
        }
    }

    @Override
    protected void removeEntity(Entity e) {
        controls.remove(e.getId());
    }

    public void leftMouseActionResult(HexMapInputEvent event) {
    }

    public void rightMouseActionResult(HexMapInputEvent event) {
//        for (EntityId id : controls) {
//            HexPositionComponent posComp = entityData.getComponent(id, HexPositionComponent.class);
//            if (posComp != null && posComp.getPosition().equals(event.getEventPosition())) {
//                if (actionUnitMenu != null && actionUnitMenu.getUID().equals(id.toString())) {
//                    return;
//                }
//                openEntityActionMenu(id);
//                return;
//            }
//        }
    }
    
    public void move(EntityId id, FieldGUIComponent.EntityType field){
        
    }
    
    @Override
    protected void cleanupSystem() {
        controls.clear();
        screen.setUse3DSceneSupport(false);
    }
}
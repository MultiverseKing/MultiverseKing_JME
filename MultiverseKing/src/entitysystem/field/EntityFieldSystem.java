package entitysystem.field;

import com.jme3.input.controls.ActionListener;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import entitysystem.EntitySystemAppState;
import entitysystem.field.position.HexPositionComponent;
import entitysystem.render.RenderSystem;
import hexsystem.HexMapMouseInput;
import hexsystem.events.HexMapInputEvent;
import hexsystem.events.HexMapRayListener;
import java.util.HashMap;
import kingofmultiverse.MultiverseMain;
import kingofmultiverse.RTSCamera;
import tonegod.gui.controls.menuing.Menu;
import tonegod.gui.core.Screen;
import utility.ToneControl;
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
public class EntityFieldSystem extends EntitySystemAppState implements HexMapRayListener {

    /**
     * Contain all Element the player can interact with on the Field.
     */
    private HashMap<EntityId, ToneControl> controls = new HashMap<EntityId, ToneControl>();
//    private Node renderSystemNode;
    private Spatial rayDebug;
    private HexMapMouseInput mouseInput = null;
    private Screen screen;
    private Menu titanMenu;
    private Menu unitMenu;
    private Menu environmentMenu;
    private Camera cam;
    private Vector3f camPos;
    private Vector3f unitPosition;
    private RenderSystem renderSystem = null;

    @Override
    protected EntitySet initialiseSystem() {
        if (app.getStateManager().getState(RenderSystem.class) == null
                || app.getStateManager().getState(HexMapMouseInput.class) == null) {
            Logger.getLogger(EntityFieldSystem.class.getName()).warning(
                    "This System need RenderSystem and HexMapMouseInputSystem to work, it is removed.");
            return null;
        }
        renderSystem = app.getStateManager().getState(RenderSystem.class);
        mouseInput = app.getStateManager().getState(HexMapMouseInput.class);
        mouseInput.registerRayInputListener(this);
        screen = ((MultiverseMain) app).getScreen();
        screen.setUse3DSceneSupport(true);
        rayDebug = mouseInput.getRayDebug();
        cam = app.getStateManager().getState(RTSCamera.class).getCamera();

//        renderSystemNode = renderSystem.getNode();
//        layoutGUI();
//        app.getInputManager().addListener(inputListener, "Cancel");
        return entityData.getEntities(FieldGUIComponent.class);
    }

    @Override
    protected void updateSystem(float tpf) {
//        if (actionUnitMenu != null) {
//            Vector3f value = cam.getScreenCoordinates(unitPosition);
//            if (value.x > 0 && value.x < screen.getWidth()
//                    && value.y > 0 && value.y < screen.getHeight()) {
//                actionUnitMenu.setPosition(new Vector2f(value.x, value.y));
//            }
//        }
    }

    @Override
    protected void addEntity(Entity e) {
        controls.put(e.getId(), getControl(e));
    }

    @Override
    protected void updateEntity(Entity e) {
        /**
         * We check if the spatial is on the screen.
         * If not we remove the Component from this entity.
         */
        if(controls.get(e.getId()).getSpatial() != null) {
            Menu menu = getRenderMenu(e.get(FieldGUIComponent.class).getEntityType());
            /**
             * We check if the menu associated with the spatial is the same the current,
             * if not we change it.
             */
            if (!menu.equals(controls.get(e.getId()).getMenu())) {
                controls.get(e.getId()).setMenu(menu);
            }
        } else {
            entityData.removeComponent(e.getId(), FieldGUIComponent.class);
        }
    }

    @Override
    protected void removeEntity(Entity e) {
        controls.remove(e.getId());
    }

    private Menu getRenderMenu(FieldGUIComponent.EntityType entityType) {
        switch (entityType) {
            case NULL:
                return null;
            case ENVIRONMENT:
                return environmentMenu;
            case TITAN:
                return titanMenu;
            case UNIT:
                return unitMenu;
            default:
                throw new UnsupportedOperationException(entityType.name()
                        + " type not currently supported in : " + this.getClass().toString());
        }
    }

    private ToneControl getControl(e) {
        if (renderSystem != null) {
        }
    }

    private ToneControl getControl(final Menu menu) {
        ToneControl toneControl = new ToneControl(menu) {
            public void onGetFocus(MouseMotionEvent evt) {

                ((Geometry) ((Node) spatial).getChild(0)).getMaterial().setColor("Color", ColorRGBA.Yellow);
                evt.setConsumed();
            }

            public void onLoseFocus(MouseMotionEvent evt) {
                ((Geometry) ((Node) spatial).getChild(0)).getMaterial().setColor("Color", ColorRGBA.Blue);
                evt.setConsumed();
            }

            public void onMouseLeftPressed(MouseButtonEvent evt) {
            }

            public void onMouseLeftReleased(MouseButtonEvent evt) {
            }

            public void onMouseRightPressed(MouseButtonEvent evt) {
            }

            public void onMouseRightReleased(MouseButtonEvent evt) {
                menu.showMenu(null, screen.getMouseXY().x, screen.getMouseXY().y - menu.getHeight());
                evt.setConsumed();
            }
        };
        return toneControl;
    }

    /**
     * Handle that in the RenderSystem not there.
     *
     * @param ray
     * @return
     */
    public HexMapInputEvent leftRayInputAction(Ray ray) {
        return null;
    }

    public HexMapInputEvent rightRayInputAction(Ray ray) {
//        CollisionResults results = new CollisionResults();
////        Ray ray = event.getLastUsedRay();
////        renderSystemNode.collideWith(event.getLastUsedRay(), results);
//        renderSystemNode.collideWith(ray, results);
//        if (results.size() != 0) {
//            if (results.size() > 0) {
//                CollisionResult closest = results.getClosestCollision();
//
//                for (EntityId id : elements) {
//                    if(actionUnitMenu != null && actionUnitMenu.getUID().equals(id.toString())){
//                        return null;
//                    }
//                    if (closest.getGeometry().getParent().getName().equals(id.toString())) {
//                        rayDebug.setLocalTranslation(closest.getContactPoint());
//                        app.getRootNode().attachChild(rayDebug);
//                        openEntityActionMenu(id);
//                        return new HexMapInputEvent(entityData.getComponent(id,
//                                HexPositionComponent.class).getPosition(), ray);
//                    }
//                }
//            }
//        }
        return null;
    }

    public void leftMouseActionResult(HexMapInputEvent event) {
    }

    public void rightMouseActionResult(HexMapInputEvent event) {
        for (EntityId id : controls) {
            HexPositionComponent posComp = entityData.getComponent(id, HexPositionComponent.class);
            if (posComp != null && posComp.getPosition().equals(event.getEventPosition())) {
                if (actionUnitMenu != null && actionUnitMenu.getUID().equals(id.toString())) {
                    return;
                }
                openEntityActionMenu(id);
                return;
            }
        }
    }
    private ActionListener inputListener = new ActionListener() {
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (name.equals("ActionMenu") && !keyPressed) {
                /**
                 * Remove action menu
                 */
//                screen.removeElement(screen.getElementById("actionUnitMenu"));
//                app.getInputManager().removeListener(this);
            }
        }
    };

    private void openEntityActionMenu(EntityId id) {
//        actionUnitMenu.showMenu(null, screen.getMouseXY().getX(), screen.getMouseXY().getY()-actionUnitMenu.getHeight());
//        unitPosition = entityData.getComponent(id, HexPositionComponent.class).getPosition().convertToWorldPosition();
//        layoutGUI(id);
    }

    private void layoutGUI(EntityId id) {
        Box box = new Box(1, 1, 1);
        Sphere sphere = new Sphere(12, 12, 1);

        Material mat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Blue);

        Node n = getNewGUINode(renderSystemNode.getChild(id.toString()), mat.clone(), menu);
        n.setName(id.toString());

        renderSystemNode.attachChild(n);
    }

    private void initializeMenus() {
        titanMenu = new Menu(screen, Vector2f.ZERO, false) {
            @Override
            public void onMenuItemClicked(int index, Object value, boolean isToggled) {
            }
        };
        titanMenu.addMenuItem("Contextual titanMenu Menu Item 1", 0, null);
        titanMenu.addMenuItem("Contextual titanMenu Menu Item 1", 1, null);
        titanMenu.addMenuItem("Contextual titanMenu Menu Item 1", 2, null);
        titanMenu.addMenuItem("Contextual titanMenu Menu Item 1", 3, null);
        screen.addElement(titanMenu);

        unitMenu = new Menu(screen, Vector2f.ZERO, false) {
            @Override
            public void onMenuItemClicked(int index, Object value, boolean isToggled) {
            }
        };
        unitMenu.addMenuItem("Contextual unitMenu Menu Item 1", 0, null);
        unitMenu.addMenuItem("Contextual unitMenu Menu Item 2", 1, null);
        unitMenu.addMenuItem("Contextual unitMenu Menu Item 3", 2, null);
        unitMenu.addMenuItem("Contextual unitMenu Menu Item 4", 3, null);
        screen.addElement(unitMenu);

        environmentMenu = new Menu(screen, Vector2f.ZERO, false) {
            @Override
            public void onMenuItemClicked(int index, Object value, boolean isToggled) {
            }
        };
        environmentMenu.addMenuItem("Contextual environmentMenu Menu Item 1", 0, null);
        environmentMenu.addMenuItem("Contextual environmentMenu Menu Item 2", 1, null);
        environmentMenu.addMenuItem("Contextual environmentMenu Menu Item 3", 2, null);
        environmentMenu.addMenuItem("Contextual environmentMenu Menu Item 4", 3, null);
        screen.addElement(environmentMenu);
    }

    @Override
    protected void cleanupSystem() {
        controls.clear();
        screen.setUse3DSceneSupport(false);
    }
}
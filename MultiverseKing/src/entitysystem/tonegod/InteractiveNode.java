package entitysystem.tonegod;

import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.material.Material;
import com.jme3.math.Vector2f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.simsilica.es.EntityId;
import entitysystem.render.RenderGUIComponent;
import static entitysystem.render.RenderGUIComponent.EntityType.ENVIRONMENT;
import static entitysystem.render.RenderGUIComponent.EntityType.TITAN;
import static entitysystem.render.RenderGUIComponent.EntityType.UNIT;
import entitysystem.render.RenderSubSystemFieldGUI;
import java.util.HashMap;
import tonegod.gui.controls.menuing.Menu;
import tonegod.gui.core.ElementManager;
import tonegod.gui.core.Screen;
import tonegod.gui.effects.Effect;
import tonegod.gui.framework.animation.Interpolation;
import tonegod.gui.framework.core.util.GameTimer;
import tonegod.gui.listeners.MouseButtonListener;
import tonegod.gui.listeners.MouseFocusListener;
import tonegod.gui.style.StyleManager;

/**
 *
 * @author t0neg0d, roah
 */
public class InteractiveNode extends Node {

//    private final HashMap<EntityId, Spatial> entitiesSpatial = new HashMap<EntityId, Spatial>();
    private final HashMap<EntityId, RenderGUIComponent.EntityType> entitiesType = new HashMap<EntityId, RenderGUIComponent.EntityType>();
    private EntityId selectedId = null;
    private final RenderSubSystemFieldGUI iSystem;
    private final ElementManager screen;
    private Material defMat, hlMat;
    private GameTimer timer;
    private String toolTipText;
    private String icon;
    private Effect fx;
    private boolean xDir = true;
    private boolean zDir = false;
    private float xAmount = 0.5f;
    private float zAmount = 0.5f;
    private boolean isInScene = true;
    private Menu titanMenu = null;
    private Menu unitMenu = null;
    private Menu environmentMenu = null;

    public InteractiveNode(ElementManager screen, RenderSubSystemFieldGUI iSystem) {
        this.screen = screen;
        this.iSystem = iSystem;
        initTimer();
    }

    public void addEntity(EntityId id, RenderGUIComponent.EntityType type) {
        entitiesType.put(id, type);
    }

    public int removeEntity(EntityId id) {
        entitiesType.remove(id);
        return 0;
    }

    public RenderGUIComponent.EntityType getEntityField(EntityId id) {
        return entitiesType.get(id);
    }
    
    public void updateMenuElement(EntityId id, RenderGUIComponent.EntityType type){
        entitiesType.put(id, type);
    }

    public Screen getScreen() {
        return (Screen) screen;
    }

    private void initTimer() {
        timer = new GameTimer(0.5f) {
            @Override
            public void onComplete(float time) {
                xDir = !xDir;
                zDir = !zDir;
            }

            @Override
            public void timerUpdateHook(float tpf) {
                scaleSpatial(this.getPercentComplete());
            }
        };
        timer.setAutoRestart(true);
        timer.setInterpolation(Interpolation.bounceOut);
    }

    public void scaleSpatial(float percent) {
        float percentX = (xDir) ? percent : 1f - percent;
        float percentZ = (zDir) ? percent : 1f - percent;
        if (timer.getRunCount() == 0) {
            setLocalScale(1 + (xAmount * percentX), 1, 1);
        } else {
            setLocalScale(1 + (xAmount * percentX), 1 + (zAmount * percentZ), 1);
        }
    }

    public void setDefaultMaterial(Material mat) {
        this.defMat = mat;
        setMaterial(defMat);
    }

    public void setHighlightMaterial(Material mat) {
        this.hlMat = mat;
    }

    public Material getHighlightMaterial() {
        return hlMat;
    }

    public void setToolTipText(String text) {
        this.toolTipText = text;
    }

    public String getToolTipText() {
        return this.toolTipText;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getIcon() {
        return this.icon;
    }

    public void setIsInScene(boolean isInScene) {
        this.isInScene = isInScene;
    }

    public boolean getIsInScene() {
        return isInScene;
    }

    public void onGetFocus(MouseMotionEvent evt) {
//        if (!screen.getAnimManager().hasGameTimer(timer)) {
//            timer.resetRunCount();
//            timer.reset(false);
//            timer.setAutoRestart(true);
//            xDir = true;
//            zDir = false;
//            screen.getAnimManager().addGameTimer(timer);
//        }
//        setMaterial(hlMat);
        ((Screen) screen).setForcedCursor(StyleManager.CursorType.HAND);
        if (screen.getUseToolTips() && toolTipText != null) {
            ((Screen) screen).setForcedToolTip(toolTipText);
        }
    }

    public void onLoseFocus(MouseMotionEvent evt) {
//        timer.setAutoRestart(false);
//        timer.endGameTimer();
//        setLocalScale(1);
//        setMaterial(defMat);
        ((Screen) screen).releaseForcedCursor();
        ((Screen) screen).releaseForcedToolTip();
    }

    public final void onMouseLeftPressed(MouseButtonEvent evt) {
//        onSpatialLeftMouseDown(evt);
        
        screen.getLastCollision();
        System.out.println(screen.getLastCollision().getContactPoint());
        Geometry geo = screen.getLastCollision().getGeometry();
        Object id = geo.getUserData("id");
        selectedId = new EntityId(Long.decode(id.toString()));
    }

    public final void onMouseLeftReleased(MouseButtonEvent evt) {
//        onSpatialLeftMouseUp(evt);
    }

    public final void onMouseRightPressed(MouseButtonEvent evt) {
//        onSpatialRightMouseDown(evt);
    }

    public final void onMouseRightReleased(MouseButtonEvent evt) {
//        onSpatialRightMouseUp(evt);
    }

//    public abstract void onSpatialRightMouseDown(MouseButtonEvent evt);
//    public abstract void onSpatialRightMouseUp(MouseButtonEvent evt);
//    public abstract void onSpatialLeftMouseDown(MouseButtonEvent evt);
//    public abstract void onSpatialLeftMouseUp(MouseButtonEvent evt);
    // <editor-fold defaultstate="collapsed" desc="Menu Method">
    private Menu getRenderMenu(RenderGUIComponent.EntityType field) {
        switch (field) {
            case ENVIRONMENT:
                if (environmentMenu == null) {
                    initEnvironmentMenu();
                }
                return environmentMenu;
            case TITAN:
                if (titanMenu == null) {
                    initTitanMenu();
                }
                return titanMenu;
            case UNIT:
                if (unitMenu == null) {
                    initUnitMenu();
                }
                return unitMenu;
            default:
                throw new UnsupportedOperationException(field.name() + "Is not a supported type.");
        }
    }

    private void initTitanMenu() {
        titanMenu = new Menu(screen, Vector2f.ZERO, false) {
            @Override
            public void onMenuItemClicked(int index, Object value, boolean isToggled) {
                switch (index) {
                    case 0:
//                        eSystem.move(entityId, field);
                }
            }
        };
        titanMenu.addMenuItem(" Move        ", 0, null);
        titanMenu.addMenuItem(" Ability     ", 1, null);
        titanMenu.addMenuItem(" Inventory   ", 2, null);
        titanMenu.addMenuItem(" Stats       ", 3, null);
        screen.addElement(titanMenu);
    }

    private void initUnitMenu() {
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
    }

    private void initEnvironmentMenu() {
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
    // </editor-fold>
}
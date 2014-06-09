package entitysystem.render;

import com.jme3.animation.AnimControl;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import entitysystem.EntitySystemAppState;
import entitysystem.field.position.HexPositionComponent;
import static entitysystem.render.RenderComponent.EntityType.ENVIRONMENT;
import static entitysystem.render.RenderComponent.EntityType.TITAN;
import static entitysystem.render.RenderComponent.EntityType.UNIT;
import hexsystem.HexSystemAppState;
import hexsystem.MapData;
import hexsystem.events.TileChangeEvent;
import hexsystem.events.TileChangeListener;
import java.util.HashMap;
import java.util.Set;
import kingofmultiverse.MultiverseMain;
import tonegod.gui.controls.menuing.Menu;
import tonegod.gui.core.Screen;
import utility.Rotation;
import utility.SimpleMath;

/**
 * TODO: Rotation/Orientation; Picking/Raycasting; Comments
 *
 * @author Eike Foede
 */

/*
 * Just use HexPositions, the rare cases where Spatial Positions would be used
 * should be handled seperately. Else there could be double data for the
 * position with inconsistencies between the different data.
 */
public class RenderSystem extends EntitySystemAppState implements TileChangeListener {

    private Screen screen;
    private HashMap<EntityId, Spatial> spatials = new HashMap<EntityId, Spatial>();
    private SpatialInitializer spatialInitializer;
    private Node renderSystemNode = new Node("RenderSystemNode");
    private Menu titanMenu;
    private Menu unitMenu;
    private Menu environmentMenu;
    private MapData mapData;

    /**
     * Return the Animation Spatial control of an entity, if any.
     *
     * @param id of the entity.
     * @return null if no anim control.
     */
    public AnimControl getAnimControl(EntityId id) {
        if(spatials.containsKey(id)){
            return spatials.get(id).getControl(AnimControl.class);
        } else {
            return null;
        }
    }

    @Override
    protected EntitySet initialiseSystem() {
        screen = ((MultiverseMain) app).getScreen();
        app.getRootNode().attachChild(renderSystemNode);
        spatialInitializer = new SpatialInitializer(app.getAssetManager(), screen);
//        initializeMenus();
        mapData = app.getStateManager().getState(HexSystemAppState.class).getMapData();
        mapData.registerTileChangeListener(this);
        return entityData.getEntities(RenderComponent.class, HexPositionComponent.class);
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

    /**
     *
     * @param tpf
     */
    @Override
    protected void updateSystem(float tpf) {
    }

    @Override
    public void addEntity(Entity e) {
        RenderComponent renderComp = e.get(RenderComponent.class);
        Spatial s = spatialInitializer.initialize(renderComp.getName(), getRenderMenu(renderComp.getEntityType()));
        s.setName(renderComp.getName()+e.getId().toString());
        spatials.put(e.getId(), s);
        HexPositionComponent positionComp = e.get(HexPositionComponent.class);
        s.setLocalTranslation(mapData.convertTileToWorldPosition(positionComp.getPosition()));
        s.setLocalRotation(Rotation.getQuaternion(positionComp.getRotation()));
        renderSystemNode.attachChild(s);
    }

    @Override
    protected void updateEntity(Entity e) {
        Spatial s = spatials.get(e.getId());
        String eName = (String)(e.get(RenderComponent.class).getName()+e.getId().toString());
        if (s == null) {
            s = spatialInitializer.initialize(e.get(RenderComponent.class).getName(),
                    getRenderMenu(e.get(RenderComponent.class).getEntityType()));
            s.setName(eName);
            spatials.put(e.getId(), s);
            renderSystemNode.attachChild(s);
        } else if (!eName.equals(s.getName())) {
            s = spatialInitializer.initialize(e.get(RenderComponent.class).getName(),
                    getRenderMenu(e.get(RenderComponent.class).getEntityType()));
            s.setName(eName);
            renderSystemNode.detachChild(spatials.get(e.getId()));
            spatials.put(e.getId(), s);
            renderSystemNode.attachChild(s);
        }
        HexPositionComponent positionComp = e.get(HexPositionComponent.class);
        Vector3f vect = SimpleMath.substractAbs(positionComp.getPosition().convertToWorldPosition(), spatials.get(e.getId()).getLocalTranslation());
        vect.y = 0;
//        if(mapData.convertTilePositionToSpatialPosition(positionComp.getPosition()) 
//                != mapData.convertWorldToGridPosition(Vector3f.NAN))
        if (vect.equals(Vector3f.ZERO)) {
            s.setLocalTranslation(mapData.convertTileToWorldPosition(positionComp.getPosition()));
        }
        s.setLocalRotation(Rotation.getQuaternion(positionComp.getRotation()));
    }
    
    /**
     *
     * @param e
     */
    @Override
    public void removeEntity(Entity e) {
        Spatial s = spatials.get(e.getId());
        renderSystemNode.detachChild(s);
        spatials.remove(e.getId());
    }

    private Menu getRenderMenu(RenderComponent.EntityType entityType) {
        switch (entityType) {
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

    /**
     *
     */
    @Override
    protected void cleanupSystem() {
        spatials.clear();
        renderSystemNode.removeFromParent();
    }

    /**
     *
     * @param event
     */
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

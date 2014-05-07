package entitysytem.Units;

import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import entitysystem.EntitySystemAppState;
import entitysystem.animation.AnimationComponent;
import entitysystem.card.CardRenderComponent;
import entitysystem.position.HexPositionComponent;
import entitysystem.position.RotationComponent;
import entitysystem.render.RenderComponent;
import gamestate.Editor.EditorAppState;
import hexsystem.HexTile;
import java.util.ArrayList;
import utility.HexCoordinate;
import utility.Rotation;
import utility.attribut.Animation;
import utility.attribut.CardRenderPosition;

/**
 * Handle all units on the field.
 *
 * @author roah
 */
public class UnitsSystem extends EntitySystemAppState {

    ArrayList<EntityId> units = new ArrayList<EntityId>();

    /**
     *
     * @return
     */
    @Override
    protected EntitySet initialiseSystem() {
        app.getInputManager().addMapping("deleteUnit", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        app.getInputManager().addListener(actionListener, "deleteUnit");

        return entityData.getEntities(StatsComponent.class, RenderComponent.class);
    }

    /**
     *
     * @param tpf
     */
    @Override
    protected void updateSystem(float tpf) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     * @param e
     */
    @Override
    protected void addEntity(Entity e) {
        units.add(e.getId());
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     * @param e
     */
    @Override
    protected void updateEntity(Entity e) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     * @param e
     */
    @Override
    protected void removeEntity(Entity e) {
        units.remove(e.getId());
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     */
    @Override
    protected void cleanupSystem() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     * @param castPosition
     * @param id
     * @return
     */
    public boolean canBeSummon(HexCoordinate castPosition, EntityId id) {
        HexTile tile = getMapData().getTile(castPosition);
        if (tile != null) {
            boolean summon = getMapData().getTile(castPosition).getWalkable();
            if (summon) {
                entityData.setComponents(id,
                        new HexPositionComponent(castPosition),
                        new CardRenderComponent(CardRenderPosition.FIELD),
                        new RotationComponent(Rotation.A),
                        new AnimationComponent(Animation.SUMMON),
                        new StatsComponent());
                return true;
            } else {
                return false;
            }
        }
        //tile does not exist nothing to cast.
        return false;
    }
    private ActionListener actionListener = new ActionListener() {
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (name.equals("deleteUnit") && !keyPressed) {
                HexCoordinate castPosition = app.getStateManager().getState(EditorAppState.class).pulseCursor();
                for (EntityId id : units) {
                    if (entityData.getComponent(id, HexPositionComponent.class).getPosition().equals(castPosition)) {
                        entityData.removeEntity(id);
                    }
                }
            }
        }
    };
}

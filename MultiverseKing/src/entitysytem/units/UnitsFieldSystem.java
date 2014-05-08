package entitysytem.units;

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
import entitysystem.attribut.Animation;
import entitysystem.attribut.CardRenderPosition;
import entitysystem.attribut.CardSubType;

/**
 * Handle all units on the field.
 *
 * @author roah
 */
public class UnitsFieldSystem extends EntitySystemAppState {

    ArrayList<EntityId> units = new ArrayList<EntityId>();
    ArrayList<EntityId> trap = new ArrayList<EntityId>();
    ArrayList<EntityId> persistentSpell = new ArrayList<EntityId>();

    /**
     *
     * @return
     */
    @Override
    protected EntitySet initialiseSystem() {
        app.getInputManager().addMapping("deleteUnit", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        app.getInputManager().addListener(actionListener, "deleteUnit");

        return entityData.getEntities(UnitStatsComponent.class, RenderComponent.class);
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
     * Check if the card can be casted on the defined position.
     *
     * @param castPosition position where the card is cast.
     * @param id entity this card belong to.
     * @return true if it can, false otherwise.
     */
    public boolean canBeCast(HexCoordinate castPosition, EntityId id, CardSubType subType) {
        HexTile tile = getMapData().getTile(castPosition);

        if (tile != null) {
            boolean walkable = getMapData().getTile(castPosition).getWalkable();
            switch (subType) {
                case SUMMON:
                    if (walkable) {
                        String name = entityData.getComponent(id, RenderComponent.class).getName();
                        UnitStatsComponent unitDataLoaded = entityData.getEntityLoader().loadUnitStats(name);
                        if (unitDataLoaded != null) {
                            entityData.setComponents(id,
                                    new HexPositionComponent(castPosition),
                                    new CardRenderComponent(CardRenderPosition.FIELD),
                                    new RotationComponent(Rotation.A),
                                    new AnimationComponent(Animation.SUMMON),
                                    unitDataLoaded); //Add damage component
                            return true;
                        } else {
                            System.err.println("Unit can be loaded mising data. Researched : " + name);
                            return false;
                        }
                    } else {
                        return false;
                    }
                case TRAP:
                    //todo : What kind of trap is it ? does it need a walkable tile to be put on ?
                    break;
                case SPELL:
                    //todo : instant cast even if the tile isn't walkable.
                    return true;
                default:
                    throw new UnsupportedOperationException(subType + " isn't a valid type for the field system.");
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

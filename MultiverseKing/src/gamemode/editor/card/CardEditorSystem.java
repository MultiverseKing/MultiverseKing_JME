package gamemode.editor.card;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import entitysystem.EntitySystemAppState;
import entitysystem.attribut.CardRenderPosition;
import entitysystem.card.CardRenderComponent;
import entitysystem.render.RenderComponent;
import gamemode.editor.EditorMainGui;
import hexsystem.HexSettings;
import hexsystem.HexSystemAppState;
import hexsystem.MapData;
import java.util.ArrayList;
import kingofmultiverse.MultiverseMain;
import utility.HexCoordinate;
import utility.Vector2Int;

/**
 *
 * @author roah
 */
public class CardEditorSystem extends EntitySystemAppState {

    private ArrayList<EntityId> entity = new ArrayList<EntityId>();
    private CardEditorMenu cardEditorGui;
    private MapData mapData;

    @Override
    protected EntitySet initialiseSystem() {
        mapData = app.getStateManager().getState(HexSystemAppState.class).getMapData();
        /**
         * Load the map and populate the Menu.
         */
//        if (mapData.getMapName() == null || !mapData.getMapName().equalsIgnoreCase("reset")) {
//            if (!mapData.loadMap("Reset")) {
//                Logger.getLogger(CardEditorGui.class.getName()).log(Level.WARNING, null, new IOException("Files missing."));
//                app.getStateManager().detach(this);
//                return null;
//            }
//        }
        cardEditorGui = new CardEditorMenu((MultiverseMain) app, this);
        ((MultiverseMain) app).getScreen().addElement(cardEditorGui);

        return entityData.getEntities(CardRenderComponent.class);
    }

    void genTestMap() {
        if (mapData.getAllChunkPos().isEmpty()) {
            mapData.addChunk(Vector2Int.ZERO, null);
        }

        /**
         * Move the camera to the center of the map.
         */
        Vector3f center = new HexCoordinate(HexCoordinate.OFFSET,
                new Vector2Int(HexSettings.CHUNK_SIZE / 2, HexSettings.CHUNK_SIZE / 2)).convertToWorldPosition();
        ((MultiverseMain) app).getRtsCam().setCenter(new Vector3f(center.x + 3, 15, center.z + 3));
    }

    @Override
    protected void updateSystem(float tpf) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void addEntity(Entity e) {
        entity.add(e.getId());
    }

    void addEntityCard(String name) {
        EntityId cardId = entityData.createEntity();
        entityData.setComponent(cardId, new RenderComponent(name));
        entityData.setComponent(cardId, new CardRenderComponent(CardRenderPosition.HAND, name));
        entity.add(cardId);
    }

    void removeEntityCard() {
        if (entity.size() > 1) {
            int i = FastMath.nextRandomInt(2, entity.size());
            i -= 1;
            entityData.removeEntity(entity.get(i));
            entity.remove(entity.get(i));
        }
    }

    @Override
    protected void updateEntity(Entity e) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void removeEntity(Entity e) {
        entity.remove(e.getId());
    }

    @Override
    protected void cleanupSystem() {
        ((MultiverseMain) app).getScreen().removeElement(cardEditorGui);
        for (EntityId id : entity) {
            entityData.removeEntity(id);
        }
        entity.clear();
        mapData.clearCurrent();
        app.getStateManager().attach(new EditorMainGui());
    }
}

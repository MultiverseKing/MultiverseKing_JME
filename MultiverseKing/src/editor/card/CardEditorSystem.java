package editor.card;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import entitysystem.EntitySystemAppState;
import entitysystem.attribut.CardRenderPosition;
import entitysystem.card.CardRenderComponent;
import entitysystem.render.RenderComponent;
import entitysystem.render.RenderComponent.Type;
import hexsystem.area.MapDataAppState;
import java.util.ArrayList;
import kingofmultiverse.MultiverseMain;
import org.hexgridapi.base.HexSetting;
import org.hexgridapi.base.MapData;
import org.hexgridapi.utility.HexCoordinate;
import org.hexgridapi.utility.Vector2Int;

/**
 * @todo update
 * @author roah
 */
public class CardEditorSystem extends EntitySystemAppState {

    private ArrayList<EntityId> entity = new ArrayList<>();
    private MapData mapData;

    @Override
    protected EntitySet initialiseSystem() {
        mapData = app.getStateManager().getState(MapDataAppState.class).getMapData();

        return entityData.getEntities(CardRenderComponent.class);
    }

    void genTestMap() {
        if (mapData.getAllChunkPos().isEmpty()) {
            mapData.addChunk(new Vector2Int(), null);
        }

        /**
         * Move the camera to the center of the map.
         */
        Vector3f center = new HexCoordinate(HexCoordinate.OFFSET,
                new Vector2Int(HexSetting.CHUNK_SIZE / 2, HexSetting.CHUNK_SIZE / 2)).convertToWorldPosition();
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
        entityData.setComponent(cardId, new RenderComponent(name, Type.Units));
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
        for (EntityId id : entity) {
            entityData.removeEntity(id);
        }
        entity.clear();
        mapData.Cleanup();
//        app.getStateManager().attach(new EditorMainGUI());
    }
}

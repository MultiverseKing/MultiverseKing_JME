package org.multiverseking.render.VFX;

import com.jme3.scene.Node;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import emitter.Emitter;
import emitter.loader.EmitterLoader;
import java.util.HashMap;
import org.hexgridapi.core.AbstractHexGridAppState;
import org.hexgridapi.core.data.MapData;
import org.multiverseking.core.utility.EntitySystemAppState;
import org.multiverseking.core.utility.SubSystem;
import org.multiverseking.render.RenderComponent;
import org.multiverseking.render.RenderSystem;

/**
 * System used to show all FX on the screen, work with other system, The Emitter
 * system made by toneGOd is the one used to render all VFX.
 *
 * @author roah
 */
public class VFXRenderSystem extends EntitySystemAppState implements SubSystem {

    private final HashMap<EntityId, EmitterLoader> emitters = new HashMap<>();
    private final String folderPath = "VFX/";
    private Node VFXNode = new Node("VFXNode");
    private MapData mapData;
//    private SpatialInitializer spatialInitializer;
    private RenderSystem renderSystem;

    @Override
    protected EntitySet initialiseSystem() {
        mapData = app.getStateManager().getState(AbstractHexGridAppState.class).getMapData();
        renderSystem = app.getStateManager().getState(RenderSystem.class);
        VFXNode = app.getStateManager().getState(RenderSystem.class).registerSubSystem(this, true);
//        spatialInitializer = new SpatialInitializer(app.getAssetManager(), "/VFX");

        app.getAssetManager().registerLoader(EmitterLoader.class, "em");

        return entityData.getEntities(VFXRenderComponent.class);
    }

    @Override
    protected void updateSystem(float tpf) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void addEntity(Entity e) {
        String path = folderPath + e.get(VFXRenderComponent.class).getName() + ".em";
        EmitterLoader em = (EmitterLoader) app.getAssetManager().loadAsset(path);
        for (Emitter emitter : em.getEmitters()) {
            emitter.initialize(app.getAssetManager());
            renderSystem.setControl(e.getId(), emitter);
            emitter.emitAllParticles();
        }
        emitters.put(e.getId(), em);
    }

    @Override
    protected void updateEntity(Entity e) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void removeEntity(Entity e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void cleanupSystem() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void rootSystemIsRemoved() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}

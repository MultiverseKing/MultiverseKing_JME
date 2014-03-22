package entitysystem.example;

import com.jme3.app.SimpleApplication;
import com.jme3.math.Quaternion;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.base.DefaultEntityData;
import de.tsajar.es.render.RenderSystem;
import entitysystem.EntityDataAppState;
import entitysystem.position.PositionComponent;
import entitysystem.position.RotationComponent;
import entitysystem.render.RenderComponent;
import hexsystem.MapData;
import hexsystem.MapDataAppState;
import utility.attribut.ElementalAttribut;

/**
 *
 * @author Eike Foede
 */
public class ExampleStartup extends SimpleApplication {

    @Override
    public void simpleInitApp() {
        EntityData entityData = new DefaultEntityData();
        MapData mapData = new MapData(ElementalAttribut.EARTH, assetManager);
        //Initialise data management
        stateManager.attach(new EntityDataAppState(entityData));
        stateManager.attach(new MapDataAppState(mapData));

        //Initialise render Systems
        stateManager.attach(new RenderSystem(new ExampleSpatialInitialiser(assetManager)));
        //TODO: Initialise visual representation of Map

        //TODO: Initialise functional systems
        //examples:
        //GaugeSystem
        //MovementSystem
        //AnimationSystem (maybe rather handled by RenderSystem)
        //

        //Example: Initialise new character entity.
        EntityId characterId = entityData.createEntity();
        entityData.setComponent(characterId, new PositionComponent(0, 0, 0));
        entityData.setComponent(characterId, new RotationComponent(Quaternion.DIRECTION_Z));
        entityData.setComponent(characterId, new RenderComponent("character.j3m"));
    }

    @Override
    public void simpleUpdate(float tpf) {
        //Nothing to do here that far, everything is handled by external AppStates, mainly EntitySystems
    }
}

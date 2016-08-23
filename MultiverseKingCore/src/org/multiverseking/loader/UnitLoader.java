package org.multiverseking.loader;

import com.jme3.app.SimpleApplication;
import com.simsilica.es.PersistentComponent;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.multiverseking.ability.ActionAbility;
import org.multiverseking.ability.ActionAbilityComponent;
import org.multiverseking.field.collision.CollisionComponent;
import org.multiverseking.utility.component.HealthComponent;
import org.multiverseking.field.position.component.SpeedComponent;
import org.multiverseking.field.position.component.MovementComponent;

/**
 * loader to load entity units from file, Contain InitialUnitStatsComponent to
 * avoids any use of it outside of the loading.
 *
 * @todo refactor
 * @author roah
 */
public class UnitLoader {

    protected final SimpleApplication app;
    private final CollisionComponent collisionComponent;
    private final InitialUnitStatsComponent initialUnitStatsComponent;

    UnitLoader(JSONObject data, EntityLoader eLoader) {
        app = eLoader.getApplication();
        Number healthPoint = (Number) data.get("healthPoint");
        Number atb = (Number) data.get("atb");
        Number loadSpeed = (Number) data.get("loadSpeed");
        Number moveRange = (Number) data.get("moveRange");
        Number moveSpeed = (Number) data.get("moveSpeed");

        JSONArray ability = (JSONArray) data.get("Ability");
        String[] abilityList = new String[ability.size()];
        for (int i = 0; i < ability.size(); i++) {
            abilityList[i] = ability.get(i).toString();
        }

        initialUnitStatsComponent = new InitialUnitStatsComponent(
                healthPoint.intValue(),
                atb.byteValue(),
                loadSpeed.floatValue(),
                moveRange.byteValue(),
                moveSpeed.floatValue(),
                abilityList);

        collisionComponent = new CollisionComponent(eLoader.importCollision((JSONObject) data.get("collision")));
    }

    public CollisionComponent getCollisionComponent() {
        return collisionComponent;
    }

    public InitialUnitStatsComponent getInitialStatsComponent() {
        return initialUnitStatsComponent;
    }

    public class InitialStatsComponent implements PersistentComponent {

        private final int healthPoint;

        private InitialStatsComponent(int healthPoint) {
            this.healthPoint = healthPoint;
        }

        public int getHealPoint() {
            return healthPoint;
        }

        public HealthComponent getHealthComponent() {
            return new HealthComponent(healthPoint);
        }
    }

    public class InitialUnitStatsComponent extends InitialStatsComponent {

        private final byte atbSize;
        private final float loadSpeed;
        private final byte moveRange;
        private final float moveSpeed;
        private final String[] abilityList;

        public InitialUnitStatsComponent(InitialUnitStatsComponent stats) {
            super(stats.getHealPoint());
            this.atbSize = stats.getAtbSize();
            this.loadSpeed = stats.getLoadSpeed();
            this.moveRange = stats.getMoveRange();
            this.moveSpeed = stats.getMoveSpeed();
            this.abilityList = stats.getAbilityList();
        }

        private InitialUnitStatsComponent(int healthPoint, byte maxAtb, float speed, 
                byte moveRange, float moveSpeed, String[] abilityList) {
            super(healthPoint);
            this.atbSize = maxAtb;
            this.loadSpeed = speed;
            this.moveRange = moveRange;
            this.moveSpeed = moveSpeed;
            this.abilityList = abilityList;
        }

        /**
         * @return Size of the Atb.Action gauge.
         */
        public byte getAtbSize() {
            return atbSize;
        }

        /**
         * @return Speed of the unit to load the gauge.
         */
        public float getLoadSpeed() {
            return loadSpeed;
        }

        public SpeedComponent getSpeedComponent() {
            return new SpeedComponent(loadSpeed);
        }

        /**
         * @return How far the unit can move (Movament gauge Size).
         */
        public byte getMoveRange() {
            return moveRange;
        }

        /**
         * @return How fast the unit can move from one tile to another.
         */
        public float getMoveSpeed() {
            return moveSpeed;
        }

        public MovementComponent getMovementComponent() {
            return new MovementComponent(moveRange, moveSpeed);
        }

        /**
         * @return All ability binded to this unit.
         */
        public String[] getAbilityList() {
            return abilityList;
        }

        public ActionAbilityComponent getActionAbilityComponent() {
            EntityLoader loader = new EntityLoader(app);
            ActionAbility[] list = new ActionAbility[abilityList.length];
            for (int i = 0; i < abilityList.length; i++) {
                list[i] = loader.loadActionAbility(abilityList[i]);
            }
            return new ActionAbilityComponent(list);
        }
    }
}

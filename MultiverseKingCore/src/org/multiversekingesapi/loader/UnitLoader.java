package org.multiversekingesapi.loader;

import com.jme3.app.SimpleApplication;
import com.simsilica.es.PersistentComponent;
import org.multiversekingesapi.ability.AbilityComponent;
import org.multiversekingesapi.field.component.ATBComponent;
import org.multiversekingesapi.field.component.HealthComponent;
import org.multiversekingesapi.field.position.MovementComponent;
import org.multiversekingesapi.field.component.CollisionComponent;
import org.multiversekingesapi.field.component.SpeedComponent;
import org.multiversekingesapi.render.Render.RenderType;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * loader to load entity units from file, Contain InitialUnitStatsComponent to
 * avoids any use of it outside of the loading.
 *
 * @author roah
 */
public class UnitLoader {

    private final CollisionComponent collisionComponent;
    private final InitialUnitStatsComponent initialUnitStatsComponent;
    private final SimpleApplication app;

    UnitLoader(JSONObject data, EntityLoader eLoader) {
        app = eLoader.getApplication();
        Number healthPoint = (Number) data.get("healthPoint");
        Number atb = (Number) data.get("atb");
        Number speed = (Number) data.get("speed");
        Number moveRange = (Number) data.get("moveRange");
        Number moveSpeed = (Number) data.get("moveSpeed");

        JSONArray ability = (JSONArray) data.get(RenderType.Ability.toString());
        String[] abilityList = new String[ability.size()];
        for (int i = 0; i < ability.size(); i++) {
            abilityList[i] = ability.get(i).toString();
        }

        initialUnitStatsComponent = new InitialUnitStatsComponent(
                healthPoint.intValue(),
                atb.byteValue(),
                speed.floatValue(),
                moveRange.byteValue(),
                moveSpeed.floatValue(),
                abilityList);

        collisionComponent = new CollisionComponent(eLoader.importCollision((JSONArray) data.get("collision")));
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

        private final byte maxAtb;
        private final float speed;
        private final byte moveRange;
        private final float moveSpeed;
        private final String[] abilityList;

        public InitialUnitStatsComponent(InitialUnitStatsComponent stats) {
            super(stats.getHealPoint());
            this.maxAtb = stats.getMaxAtb();
            this.speed = stats.getSpeed();
            this.moveRange = stats.getMoveRange();
            this.moveSpeed = stats.getMoveSpeed();
            this.abilityList = stats.getAbilityList();
        }

        private InitialUnitStatsComponent(int healthPoint, byte maxAtb, float speed, byte moveRange, float moveSpeed, String[] abilityList) {
            super(healthPoint);
            this.maxAtb = maxAtb;
            this.speed = speed;
            this.moveRange = moveRange;
            this.moveSpeed = moveSpeed;
            this.abilityList = abilityList;
        }

        public byte getMaxAtb() {
            return maxAtb;
        }

        public ATBComponent getATBComponent() {
            return new ATBComponent(maxAtb);
        }

        public float getSpeed() {
            return speed;
        }

        public SpeedComponent getSpeedComponent() {
            return new SpeedComponent(speed);
        }

        public byte getMoveRange() {
            return moveRange;
        }

        public float getMoveSpeed() {
            return moveSpeed;
        }

        public MovementComponent getMovementComponent() {
            return new MovementComponent(moveRange, moveSpeed);
        }

        public String[] getAbilityList() {
            return abilityList;
        }

        public AbilityComponent[] getAbilityComponent() {
            EntityLoader loader = new EntityLoader(app);
            AbilityComponent[] list = new AbilityComponent[abilityList.length];
            int i = 0;
            for (String s : abilityList) {
                if (s.equals("null")) {
                    return null;
                }
                list[i] = loader.loadAbility(s);
                i++;
            }
            return list;
        }
    }
}

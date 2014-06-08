package entitysystem.render;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.LoopMode;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import entitysystem.EntitySystemAppState;
import java.util.HashMap;
import kingofmultiverse.MultiverseMain;
import entitysystem.attribut.Animation;

/**
 * Handle how animation work when they got they cycle done or under defined
 * pattern.
 *
 * @author roah
 */
public class AnimationSystem extends EntitySystemAppState implements AnimEventListener {

    private EntityRenderSystem renderSystem;
    private HashMap<EntityId, AnimControl> animControls = new HashMap<EntityId, AnimControl>();

    @Override
    protected EntitySet initialiseSystem() {
        renderSystem = app.getStateManager().getState(EntityRenderSystem.class);

        return entityData.getEntities(AnimationComponent.class, EntityRenderComponent.class);
    }

    @Override
    protected void updateSystem(float tpf) {
//        Use onAnimCycleDone && onAnimChange
    }

    @Override
    protected void addEntity(Entity e) {
        AnimControl control = renderSystem.getAnimControl(e.getId());
        control.addListener(this);
        AnimChannel channel = control.createChannel();
        setPlay(channel, e.get(AnimationComponent.class).getAnimation());

        animControls.put(e.getId(), control);
    }

    @Override
    protected void updateEntity(Entity e) {
        Animation toPlay = e.get(AnimationComponent.class).getAnimation();
        if (animControls.containsKey(e.getId())
                && !animControls.get(e.getId()).getChannel(0).getAnimationName().equals(toPlay.toString())) {
            setPlay(animControls.get(e.getId()).getChannel(0), toPlay);
        }
    }

    @Override
    protected void removeEntity(Entity e) {
        AnimControl animControl = animControls.get(e.getId());
        animControl.clearChannels();
        animControl.clearListeners();
        animControls.remove(e.getId());
    }

    @Override
    protected void cleanupSystem() {
        for (AnimControl control : animControls.values()) {
            control.clearChannels();
            control.clearListeners();
        }
        animControls.clear();
    }

    /**
     * Activate the defined animation in the AnimationComponent, on the main
     * channel (Currently only one channel is used).
     *
     * @param channel to use for the animation.
     * @param anim animation to play.
     */
    private void setPlay(AnimChannel channel, Animation anim) {
        if (anim == Animation.SUMMON) {
            channel.setAnim(anim.toString());
            channel.setLoopMode(LoopMode.DontLoop);
        } else if (anim == Animation.IDLE || anim == Animation.WALK) {
            channel.setAnim(Animation.IDLE.toString(), 0.50f);
            channel.setLoopMode(LoopMode.Loop);
            channel.setSpeed(1f);
        }
    }

    /**
     * Called When an animation on a channel ended his life cycle.
     *
     * @param control who this animation belong.
     * @param channel channel used by the animation.
     * @param animName name of the ended animation.
     */
    public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName) {
        if (animName.equals(Animation.SUMMON.toString())) {
            EntityId id = MultiverseMain.getKeyByValue(animControls, control);
            entityData.setComponent(id, new AnimationComponent(Animation.IDLE));
        }
    }

    /**
     * Called when an animation got changed during his life cycle.
     *
     * @param control who this animation belong.
     * @param channel channel used by the animation.
     * @param animName name of the ended animation.
     * @deprecated not used on the current context.
     */
    public void onAnimChange(AnimControl control, AnimChannel channel, String animName) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}

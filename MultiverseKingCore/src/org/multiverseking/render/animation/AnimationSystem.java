package org.multiverseking.render.animation;

import org.multiverseking.SubSystem;
import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.LoopMode;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import org.multiverseking.EntitySystemAppState;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.multiverseking.render.RenderComponent;
import org.multiverseking.render.RenderSystem;
import org.multiverseking.utility.Utility;

/**
 * Handle how animation work when they got they cycle done or under defined
 * pattern.
 *
 * @todo make this a sub-system of the renderSystem
 *
 * @author roah
 */
public class AnimationSystem extends EntitySystemAppState implements AnimEventListener, SubSystem {

    private RenderSystem renderSystem;
    private HashMap<EntityId, AnimControl> animControls = new HashMap<>();

    @Override
    protected EntitySet initialiseSystem() {
        renderSystem = app.getStateManager().getState(RenderSystem.class);
        renderSystem.registerSubSystem(this);
        return entityData.getEntities(AnimationComponent.class, RenderComponent.class);
    }

    @Override
    protected void updateSystem(float tpf) {
//        Use onAnimCycleDone && onAnimChange
    }

    @Override
    protected void addEntity(Entity e) {
        AnimControl control = renderSystem.getControl(e.getId(), AnimControl.class);
        if (control == null) {
            entityData.removeComponent(e.getId(), AnimationComponent.class);
            Logger.getGlobal().log(Level.WARNING, "{0} : There is no Animation control for : {1}.",
                    new Object[]{getClass().getName(), e.get(RenderComponent.class).getName()});
            return;
        }
        control.addListener(this);
        AnimChannel channel = control.createChannel();
        setPlay(channel, e.get(AnimationComponent.class).getAnimation());

        animControls.put(e.getId(), control);
    }

    @Override
    protected void updateEntity(Entity e) {
        if (renderSystem.getControl(e.getId(), AnimControl.class) != animControls.get(e.getId())) {
            removeEntity(e);
            addEntity(e);
            return;
        }
        Animation toPlay = e.get(AnimationComponent.class).getAnimation();
        if (!animControls.get(e.getId()).getChannel(0).getAnimationName().equals(toPlay.toString())) {
            setPlay(animControls.get(e.getId()).getChannel(0), toPlay);
        }
//        if(e.get(TimeBreakComponent.class).isTimeBreak()){
//            animControls.get(e.getId()).getChannel(0).setSpeed(0);
//        } else if(!e.get(TimeBreakComponent.class).isTimeBreak() && animControls.get(e.getId()).getChannel(0).getSpeed() == 0f) {
//            animControls.get(e.getId()).getChannel(0).setSpeed(1f);
//        }
    }

    @Override
    protected void removeEntity(Entity e) {
        AnimControl animControl = animControls.get(e.getId());
        if (animControl == null) {
            return;
        }
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
        if (channel.getControl().getAnimationNames().contains(anim.name())) {
            switch (anim) {
                case IDLE:
                    channel.setAnim(anim.toString(), 0.50f);
                    channel.setLoopMode(LoopMode.Loop);
                    channel.setSpeed(1f);
                    break;
                case SUMMON:
                    channel.setAnim(anim.toString());
                    channel.setLoopMode(LoopMode.DontLoop);
                    break;
                case WALK:
                    channel.setAnim(anim.toString(), 0.50f);
                    channel.setLoopMode(LoopMode.Loop);
                    break;
                default:
                    System.err.println(anim.name() + " Is not a supported animation type.");
            }
        } else if (anim.equals(Animation.SUMMON)) {
            setPlay(channel, Animation.IDLE);
        }
    }

    /**
     * Called When an animation on a channel ended his life cycle.
     *
     * @param control who this animation belong.
     * @param channel channel used by the animation.
     * @param animName name of the ended animation.
     */
    @Override
    public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName) {
        if (animName.equals(Animation.SUMMON.toString())) {
            EntityId id = Utility.getKeyByValue(animControls, control);
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
    @Override
    public void onAnimChange(AnimControl control, AnimChannel channel, String animName) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void rootSystemIsRemoved() {
        app.getStateManager().detach(this);
    }
}

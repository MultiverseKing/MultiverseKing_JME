package org.multiversekingesapi.render.animation;

import com.simsilica.es.EntityComponent;

/**
 * Contain the currently played animation for the entity or the animation who
 * will be played (depend on the situation).
 *
 * @author roah
 */
public class AnimationComponent implements EntityComponent {

    private Animation animation;

    /**
     * Animation to play.
     *
     * @param animation
     */
    public AnimationComponent(Animation animation) {
        this.animation = animation;
    }

    /**
     * Animation to play.
     *
     * @return
     */
    public Animation getAnimation() {
        return animation;
    }
}
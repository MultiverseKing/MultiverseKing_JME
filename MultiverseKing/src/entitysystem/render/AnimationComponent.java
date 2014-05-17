package entitysystem.render;

import entitysystem.ExtendedComponent;
import entitysystem.attribut.Animation;

/**
 * Contain the currently played animation for the entity 
 * or the animation who will be played (depend on the situation).
 * @author roah
 */
public class AnimationComponent implements ExtendedComponent {

    private Animation animation;

    /**
     * Animation to play.
     * @param animation
     */
    public AnimationComponent(Animation animation) {
        this.animation = animation;
    }

    /**
     * Animation to play.
     * @return
     */
    public Animation getAnimation() {
        return animation;
    }

    @Override
    public ExtendedComponent clone() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
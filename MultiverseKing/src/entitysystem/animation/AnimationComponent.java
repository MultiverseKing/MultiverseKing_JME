/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entitysystem.animation;

import com.simsilica.es.PersistentComponent;
import utility.attribut.Animation;

/**
 *
 * @author roah
 */
public class AnimationComponent implements PersistentComponent {

    private Animation animation;

    public AnimationComponent(Animation animation) {
        this.animation = animation;
    }

    public Animation getAnimation() {
        return animation;
    }
}
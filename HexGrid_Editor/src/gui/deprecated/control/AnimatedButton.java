package gui.deprecated.control;

import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import tonegod.gui.controls.buttons.Button;
import tonegod.gui.controls.buttons.ButtonAdapter;
import tonegod.gui.core.Element;
import tonegod.gui.core.ElementManager;
import tonegod.gui.effects.Effect;

/**
 * 
 * @todo On mouse press effect
 * @author roah
 */
public abstract class AnimatedButton extends Element {

    private float currentTimer = 0;
    private float endTimer;
    private boolean animate = false;
    private boolean reverseRotation = false;
    private Button buttonImg;

    public AnimatedButton(ElementManager screen, String UID, Vector2f position, Vector2f dimensions, String defaultImg, float timer) {
        this(screen, UID, position, dimensions, defaultImg, timer, false);
    }

    public AnimatedButton(ElementManager screen, String UID, Vector2f position, Vector2f dimensions, String defaultImg, float timer, boolean reverseRotation) {
        super(screen, UID + "Hook", new Vector2f(position.x + (dimensions.x / 2), position.y + (dimensions.y / 2)),
                new Vector2f(), Vector4f.ZERO, null);
        endTimer = timer;
        this.reverseRotation = reverseRotation;
        buttonImg = new ButtonAdapter(screen, UID, new Vector2f(-dimensions.x / 2, -dimensions.y / 2), dimensions, Vector4f.ZERO, defaultImg) {
            @Override
            public void onButtonFocus(MouseMotionEvent evt) {
                if (!animate) {
                    animate = true;
                }
            }

            @Override
            public void onButtonMouseLeftDown(MouseButtonEvent evt, boolean toggled) {
                MouseLeftDown(evt, toggled);
            }

            @Override
            public void onButtonMouseRightDown(MouseButtonEvent evt, boolean toggled) {
                MouseRightDown(evt, toggled);
            }

            @Override
            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                MouseLeftUp(evt, toggled);
            }

            @Override
            public void onButtonMouseRightUp(MouseButtonEvent evt, boolean toggled) {
                MouseRightUp(evt, toggled);
            }
        };
        buttonImg.removeEffect(Effect.EffectEvent.Hover);
//        buttonImg.removeEffect(Effect.EffectEvent.Press); 
        this.addChild(buttonImg);
    }
    
    
    public void update(float tpf) {
        if (animate) {
            currentTimer += tpf;
            if (currentTimer >= endTimer) {
                if (buttonImg.getHasFocus()) {
                    currentTimer = 0;
                    updateRoll(currentTimer / endTimer);
                    return;
                }
                currentTimer = 0;
                resetRoll();
                animate = false;
            } else {
                updateRoll(currentTimer / endTimer);
            }
        }
    }

    private void updateRoll(float pass) {
        this.setLocalRotation(this.getLocalRotation().fromAngles(0, 0, 360 * FastMath.DEG_TO_RAD * (reverseRotation ? pass : 1.0f - pass)));
    }

    private void resetRoll() {
        this.setLocalRotation(this.getLocalRotation().fromAngles(0, 0, 0));
    }
    
    public void MouseLeftDown(MouseButtonEvent evt, boolean toggled) {    }
    public void MouseRightDown(MouseButtonEvent evt, boolean toggled) {    }
    public void MouseLeftUp(MouseButtonEvent evt, boolean toggled) {    }
    public void MouseRightUp(MouseButtonEvent evt, boolean toggled) {    }
}

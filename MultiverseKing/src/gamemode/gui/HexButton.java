package gamemode.gui;

import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import tonegod.gui.controls.buttons.Button;
import tonegod.gui.core.ElementManager;

/**
 *
 * @author roah
 */
public class HexButton extends Button {

    private boolean selected = false;

    public HexButton(ElementManager screen, String UID, Vector2f position, Vector2f dimensions) {
        super(screen, UID, position, dimensions, Vector4f.ZERO, "Textures/Icons/WindowGrid/hexWindow_empty.png");
    }

    @Override
    public void onButtonMouseLeftDown(MouseButtonEvent evt, boolean toggled) {
    }

    @Override
    public void onButtonMouseRightDown(MouseButtonEvent evt, boolean toggled) {
    }

    @Override
    public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
        selected = !selected;
        setColorMap("Textures/Icons/WindowGrid/hexWindow_" + (selected ? "selected" : "empty") + ".png");
        System.err.println(getUID());
    }

    @Override
    public void onButtonMouseRightUp(MouseButtonEvent evt, boolean toggled) {
    }

    @Override
    public void onButtonFocus(MouseMotionEvent evt) {
    }

    @Override
    public void onButtonLostFocus(MouseMotionEvent evt) {
    }
}

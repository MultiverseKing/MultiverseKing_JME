package gamemode.gui;

import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import tonegod.gui.controls.buttons.Button;
import tonegod.gui.core.ElementManager;
import utility.HexCoordinate;

/**
 *
 * @author roah
 */
public class HexButton extends Button {

    private boolean isSelected;
    private HexButtonListener listener;
    private final HexCoordinate coord;

    public HexButton(ElementManager screen, String UID, HexCoordinate coord, boolean isSelected, HexButtonListener listener) {
        super(screen, UID, new Vector2f(), new Vector2f(25, 25), Vector4f.ZERO, "Textures/Icons/WindowGrid/hexWindow_empty.png");
        this.isSelected = isSelected;
        this.coord = coord;
        this.listener = listener;
        setColorMap("Textures/Icons/WindowGrid/hexWindow_" + (isSelected ? "selected" : "empty") + ".png");
    }

    @Override
    public void onButtonMouseLeftDown(MouseButtonEvent evt, boolean toggled) {
    }

    @Override
    public void onButtonMouseRightDown(MouseButtonEvent evt, boolean toggled) {
    }

    @Override
    public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
        isSelected = !isSelected;
        update();
    }

    public void isSelected(boolean selected) {
        this.isSelected = selected;
        update();
    }
    
    private void update(){
        setColorMap("Textures/Icons/WindowGrid/hexWindow_" + (isSelected ? "selected" : "empty") + ".png");
        listener.onButtonTrigger(coord, isSelected);
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

package gamemode.gui;

import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import tonegod.gui.core.Element;
import tonegod.gui.core.Screen;
import utility.HexCoordinate;
import utility.Vector2Int;

/**
 *
 * @author roah
 */
public class HexGridWindow extends LayoutWindow {

    private int radius;
    
    public HexGridWindow(Screen screen, int radius, Element parent) {
        super(screen, parent, "Collision", new Vector2f(25, 25), Align.Horizontal, radius*2+1);
        this.radius = radius;
        populateWin();
    }

    private void populateWin(){
        Vector2Int pos = new Vector2Int();
        HexCoordinate center = new HexCoordinate(HexCoordinate.OFFSET,
                new Vector2Int(radius, radius));
        for (int i = 0; i < FastMath.pow(radius*2+1, 2); i++) {
            if (isInRange(center, pos, radius)) {
                elementList.put(pos.x + "|" + pos.y + "_"+radius, new HexButton(screen, pos.x + "|" + pos.y + "button"+ "_"+radius, new Vector2f(), new Vector2f(25, 25)));
            } else {
                elementList.put(pos.x + "|" + pos.y + "Space", null);
            }
            pos.x++;
            if (pos.x >= radius*2+1) {
                pos = new Vector2Int(0, pos.y + 1);
            }
        }
    }
    
    private boolean isInRange(HexCoordinate center, Vector2Int pos, int range) {
        if (center.equals(new HexCoordinate(HexCoordinate.OFFSET, pos))) {
            return true;
        }
        for (HexCoordinate c : center.getCoordinateInRange(range)) {
            if (c.equals(new HexCoordinate(HexCoordinate.OFFSET, pos))) {
                return true;
            }
        }
        return false;
    }

    public int getRadius() {
        return radius;
    }
    
    public void show(){
        showConstrainToParent(VAlign.bottom, HAlign.left, true);
    }

    public void reload(int radius) {
        this.radius = radius;
        removeFromScreen();
        updateAlign(radius*2+1);
        populateWin();
        show();
    }
}

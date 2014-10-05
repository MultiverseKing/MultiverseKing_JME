package gamemode.gui;

import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import gamemode.gui.LayoutWindow.VAlign;
import java.util.ArrayList;
import tonegod.gui.core.Element;
import tonegod.gui.core.ElementManager;
import utility.HexCoordinate;
import utility.Vector2Int;

/**
 *
 * @author roah
 */
public class HexGridWindow extends LayoutWindow {

    public HexGridWindow(ElementManager screen, int radius, Element parent) {
        super(screen, parent, "Collision", new Vector2f(25, 25), Align.Horizontal, radius);
        Vector2Int pos = Vector2Int.ZERO;
        HexCoordinate center = new HexCoordinate(HexCoordinate.OFFSET, 
                new Vector2Int(radius / 2, radius / 2));
        for (int i = 0; i < FastMath.pow(radius, 2); i++) {
            if (isInRange(center, pos, radius/2)) {
                elementList.put(pos.x + "|" + pos.y, new HexButton(screen, pos.x + "|" + pos.y + "button", Vector2f.ZERO, new Vector2f(25, 25)));
            } else {
                elementList.put(pos.x + "|" + pos.y +"Space", null);
            }
            pos.x++;
            if (pos.x >= radius) {
                pos = new Vector2Int(0, pos.y + 1);
            }
        }
    }

    private boolean isInRange(HexCoordinate center, Vector2Int pos, int range) {
        if(center.equals(new HexCoordinate(HexCoordinate.OFFSET, pos))){
            return true;
        }
        for (HexCoordinate c : center.getCoordinateInRange(range)) {
            if (c.equals(new HexCoordinate(HexCoordinate.OFFSET, pos))) {
                return true;
            }
        }
        return false;
    }

    public void show() {
        super.showConstrainToParent(VAlign.bottom, LayoutWindow.HAlign.left, true);
    }
}

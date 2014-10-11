package gamemode.gui;

import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import java.util.ArrayList;
import tonegod.gui.core.Element;
import tonegod.gui.core.Screen;
import utility.HexCoordinate;
import utility.Vector2Int;

/**
 *
 * @author roah
 */
public class HexGridWindow extends LayoutWindow implements HexButtonListener {

    private byte radius;
    private HexButtonListener listener;

    public HexGridWindow(Screen screen, byte radius, byte layer, Element parent, HexButtonListener listener) {
        this(screen, radius, layer, null, parent, listener);
    }

    public HexGridWindow(Screen screen, byte radius, byte layer, ArrayList<HexCoordinate> coordList, Element parent, HexButtonListener listener) {
        super(screen, parent, "Collision " + layer + ""+radius, new Vector2f(25, 25), Align.Horizontal, radius * 2 + 1);
        this.radius = radius;
        this.listener = listener;
        populateWin(coordList);
    }

    private void populateWin(ArrayList<HexCoordinate> coordList) {
        Vector2Int pos = new Vector2Int();
        HexCoordinate center = new HexCoordinate(HexCoordinate.OFFSET,
                new Vector2Int(radius, radius));
        for (int i = 0; i < FastMath.pow(radius * 2 + 1, 2); i++) {
            if (isInRange(center, pos, radius)) {
                elementList.put(pos.x + "|" + pos.y + "_" + radius,
                        new HexButton(screen, pos.x + "|" + pos.y + "button" + "_" + radius,
                        new HexCoordinate(HexCoordinate.OFFSET, pos), 
                        (coordList != null ? isSelected(new HexCoordinate(HexCoordinate.OFFSET, pos), coordList) : false), this));
            } else {
                elementList.put(pos.x + "|" + pos.y + "Space", null);
            }
            pos.x++;
            if (pos.x > radius * 2) {
                pos = new Vector2Int(0, pos.y + 1);
            }
        }
        showConstrainToParent(VAlign.bottom, HAlign.left, true);
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

    private boolean isSelected(HexCoordinate pos, ArrayList<HexCoordinate> coordList){
        HexCoordinate center = new HexCoordinate(HexCoordinate.OFFSET,
                new Vector2Int(radius, radius));
        for (HexCoordinate c : coordList) {
            if (c.add(center).equals(pos)) {
                return true;
            }
        }
        return false;
    }
    
    public int getRadius() {
        return radius;
    }

    public void setParent(Element parent) {
        this.parent = parent;
        getWindow().setElementParent(parent);
    }

    public void reload(byte radius, ArrayList<HexCoordinate> coordList) {
        this.radius = radius;
        removeFromScreen();
        updateAlign(radius * 2 + 1);
        populateWin(coordList);
    }

    public void onButtonTrigger(HexCoordinate pos, boolean selected) {
        HexCoordinate out = new HexCoordinate(HexCoordinate.OFFSET, pos.getAsOffset().add(new Vector2Int(-radius, -radius)));
        listener.onButtonTrigger(out, selected);
    }
}

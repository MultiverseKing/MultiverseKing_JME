/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package editor.area;

import gui.EditorWindow;
import org.hexgridapi.utility.HexCoordinate;
import tonegod.gui.core.Element;
import tonegod.gui.core.Screen;

/**
 *
 * @author roah
 */
public class TileEventMenu extends EditorWindow {

    private HexCoordinate inspectedTilePos;

    public HexCoordinate getInspectedTilePos() {
        return inspectedTilePos;
    }

    public TileEventMenu(Screen screen, Element parent, HexCoordinate inspectedTilePos) {
        super(screen, parent, inspectedTilePos + " Tile Event Menu");
        this.inspectedTilePos = inspectedTilePos;
    }

    void setInpectedTile(HexCoordinate inspectedTilePos) {
        /**
         * todo
         */
    }
    
    public void show(){
        
        super.showConstrainToParent(VAlign.bottom, HAlign.right);
    }
    
}

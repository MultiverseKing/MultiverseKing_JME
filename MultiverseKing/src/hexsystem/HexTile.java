/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hexsystem;

import utility.Coordinate.Offset;
import utility.attribut.ElementalAttribut;

/**
 * individual hex functionality.
 * @author roah
 */
public class HexTile {
    // Keep track of individual property of an hex.
    protected ElementalAttribut hexElement;

    // Internal way of working.
    private Offset index;
    public Offset getIndex() {return index;}
      
    public HexTile(Offset index, ElementalAttribut eAttribut){
      this.index = index;
      this.hexElement = eAttribut;
    }
}

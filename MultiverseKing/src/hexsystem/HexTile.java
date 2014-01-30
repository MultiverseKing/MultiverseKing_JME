/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hexsystem;

import utility.Coordinate.Offset;
import utility.attribut.ElementalAttribut;

/**
 * Keep track of individual property of an hex, individual functionality.
 * @author roah
 */
class HexTile {
   /**
    * Keep track of individual property of an hex, internal way of working.
    */
    private Offset index;
    private ElementalAttribut hexElement;
    public Offset getIndex() {return index;}
      
    public HexTile(Offset index, ElementalAttribut eAttribut){
      this.index = index;
      this.hexElement = eAttribut;
    }
}

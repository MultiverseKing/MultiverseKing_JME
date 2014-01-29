/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hexsystem;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import utility.Coordinate.Offset;
import utility.attribut.ElementalAttribut;

/**
 *
 * @author roah
 */
public class HexTile {
    // Generation et fonctionnement visuel
    protected ElementalAttribut hexElement;

    // Fonctionnement interne
    private Offset index;
    public Offset getIndex() {return index;}
      
    public HexTile(Offset index, ElementalAttribut eAttribut){
      this.index = index;
      this.hexElement = eAttribut;
    }
}

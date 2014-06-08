package entitysystem.render;

import com.jme3.scene.Node;
import hexsystem.MapData;

/**
 * System used to show all FX on the screen, work with other system, The
 * FXBuilder made by toneGOd is currently the one we will use to render FX.
 *
 * @author roah
 */
public class VFXRenderSystem {

    private Node VFXNode = new Node("VFXNode");
    private MapData mapData;
}

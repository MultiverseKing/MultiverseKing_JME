package entitysystem.field;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import entitysystem.render.SpatialInitializer;

/**
 *
 * @author Eike Foede
 */
public class CubeSpatialInitializer implements SpatialInitializer {

    private AssetManager assetManager = null;

    /**
     *
     * @param spatial
     * @return
     */
    public Spatial initialize(String spatial) {
        Material mat = new Material(assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");  // create a simple material
        mat.setColor("Color", ColorRGBA.Blue);   // set color of material to blue
        Box b = new Box(0.5f, 0.5f, 0.5f);
        Geometry g = new Geometry(spatial, b);
        g.setMaterial(mat);                   // set the cube's material

        return g;
    }

    /**
     *
     * @param am
     */
    public void setAssetManager(AssetManager am) {
        this.assetManager = am;
    }
}

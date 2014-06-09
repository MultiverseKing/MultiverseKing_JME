package entitysystem.render;

import com.jme3.asset.AssetManager;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import tonegod.gui.controls.menuing.Menu;
import tonegod.gui.core.Screen;
import utility.ToneControl;

/**
 *
 * @author roah
 */
public class SpatialInitializer {

    private final AssetManager assetManager;
    private final Screen screen;

    public SpatialInitializer(AssetManager am, Screen screen) {
        this.assetManager = am;
        this.screen = screen;
    }

    /**
     * @todo mouse picking for entity
     */
    public Spatial initialize(String name, final Menu menu) {
        //        Geometry geom = new Geometry();
        //        geom.setMesh(mesh);
        //        Geometry geo = (Geometry) obj.getChild("Material");
        Spatial asset = assetManager.loadModel("Models/Units/" + name + ".j3o");
//        List<Spatial> obj = assetNode.getChildren();

        ToneControl node = new ToneControl() {
            public void onGetFocus(MouseMotionEvent evt) {

                ((Geometry) ((Node) spatial).getChild(0)).getMaterial().setColor("Color", ColorRGBA.Yellow);
                evt.setConsumed();
            }

            public void onLoseFocus(MouseMotionEvent evt) {
                ((Geometry) ((Node) spatial).getChild(0)).getMaterial().setColor("Color", ColorRGBA.Blue);
                evt.setConsumed();
            }

            public void onMouseLeftPressed(MouseButtonEvent evt) {
            }

            public void onMouseLeftReleased(MouseButtonEvent evt) {
            }

            public void onMouseRightPressed(MouseButtonEvent evt) {
            }

            public void onMouseRightReleased(MouseButtonEvent evt) {
                menu.showMenu(null, screen.getMouseXY().x, screen.getMouseXY().y - menu.getHeight());
                evt.setConsumed();
            }

            @Override
            protected void controlUpdate(float tpf) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            protected void controlRender(RenderManager rm, ViewPort vp) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };
//        for (Spatial s : obj) {
//            node.attachChild(s);
//        }
//        node.addControl(assetNode.getControl(AnimControl.class));
//        node.addControl(assetNode.getControl(SkeletonControl.class));
//        AnimControl animControl = assetNode.getControl(AnimControl.class);
//        SkeletonControl skeletonControl = assetNode.getControl(SkeletonControl.class);

//        node.setMaterial(mat);
//        assetNode.addControl(node);
        return asset;
    }
}

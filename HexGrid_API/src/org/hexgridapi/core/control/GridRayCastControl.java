package org.hexgridapi.core.control;

import com.jme3.app.SimpleApplication;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.Arrow;
import com.jme3.scene.shape.Sphere;
import java.util.Iterator;
import org.hexgridapi.events.MouseInputEvent;
import org.hexgridapi.utility.HexCoordinate;

/**
 *
 * @author roah
 */
public class GridRayCastControl {

    private SimpleApplication app;
    private Node rayDebug;
    private ColorRGBA debugColor;

    public GridRayCastControl(SimpleApplication app, ColorRGBA debugColor) {
        this(app, debugColor, false);
    }
    public GridRayCastControl(SimpleApplication app, ColorRGBA debugColor, boolean debugArrow) {
        this.app = app;
        this.debugColor = debugColor;
        initialiseDebug(debugArrow);
    }

    private void initialiseDebug(boolean debugArrow) {

        Sphere sphere = new Sphere(30, 30, 0.2f);
        rayDebug = new Node("DebugRay");
        Geometry geo = new Geometry("SphereDebugRayCast", sphere);
        Material mark_mat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat.setColor("Color", debugColor);
        geo.setMaterial(mark_mat);
        rayDebug.attachChild(geo);
        if(debugArrow){
            attachCoordinateAxes();
        }
    }

    private void attachCoordinateAxes() {
        Vector3f pos = new Vector3f(0, 0.1f, 0);
        Arrow arrow = new Arrow(Vector3f.UNIT_X);
        arrow.setLineWidth(4); // make arrow thicker
        putShape(arrow, ColorRGBA.Red).setLocalTranslation(pos);

        arrow = new Arrow(Vector3f.UNIT_Y);
        arrow.setLineWidth(4); // make arrow thicker
        putShape(arrow, new ColorRGBA(0.2f, 1f, 0.2f, 1)).setLocalTranslation(pos);

        arrow = new Arrow(Vector3f.UNIT_Z);
        arrow.setLineWidth(4); // make arrow thicker
        putShape(arrow, new ColorRGBA(0.2f, 0.2f, 1, 1)).setLocalTranslation(pos);
    }

    private Geometry putShape(Mesh shape, ColorRGBA color) {
        Geometry g = new Geometry("coordinate axis", shape);
        Material mat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.getAdditionalRenderState().setWireframe(true);
        mat.setColor("Color", color);
        g.setMaterial(mat);
        rayDebug.attachChild(g);
        return g;
    }

    public MouseInputEvent castRay(Ray ray) {
        CollisionResults results = new CollisionResults();
        ray = ray != null ? ray : get3DRay(CastFrom.SCREEN_CENTER);

        app.getRootNode().getChild("HexGridNode").collideWith(ray, results);
        if (results.size() != 0) {
            if (results.size() > 0) {
                CollisionResult closest = results.getClosestCollision();
                setDebugPosition(closest.getContactPoint());
                HexCoordinate newPos = convertMouseCollision(results);
                return new MouseInputEvent(null, newPos, ray, closest);
            }
            return null;
        } else {
            //Error catching.
            System.out.println("null raycast");
            app.getRootNode().detachChild(rayDebug);
            return null;
        }
    }

    /**
     * Convert the 2d screen coordinates of the mouse to 3D world coordinates,
     * then cast a ray from it. Got his own class cause it could be used for
     * more then the mouse left clic.
     *
     * @param app
     * @return
     */
    public Ray get3DRay(CastFrom from) {
        Vector2f click2d;
        switch(from){
            case MOUSE:
                click2d = app.getInputManager().getCursorPosition();
                break;
            case SCREEN_CENTER:
                click2d = new Vector2f(app.getCamera().getWidth()/2, app.getCamera().getHeight() /2);
                break;
            default:
                throw new UnsupportedOperationException(from + "isn't a valid type.");
        }
        Vector3f click3d = app.getCamera().getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 0f).clone();
        Vector3f dir = app.getCamera().getWorldCoordinates(
                new Vector2f(click2d.x, click2d.y), 1f).subtractLocal(click3d).normalizeLocal();
        Ray ray = new Ray(click3d, dir);
        return ray;
    }

    private HexCoordinate convertMouseCollision(CollisionResults rayResults) {
        HexCoordinate tilePos;
        Vector3f pos;
        Iterator<CollisionResult> i = rayResults.iterator();

        pos = i.next().getContactPoint();
        tilePos = new HexCoordinate(pos);
        return tilePos;
//        do {
//            pos = i.next().getContactPoint();
//            tilePos = new HexCoordinate(pos);
//            if (mapData.getTile(tilePos) == null) {
//                break;
//            } else {
//                return tilePos;
//            }
//            /*}else if (mapData.getTile(tilePos).getHeight() 
//             * == FastMath.floor(pos.y/mapData.getHexSettings().getFloorHeight())){
//             return tilePos;
//             }*/
//        } while (i.hasNext());

//        return null;
    }

    public void setDebugPosition(Vector3f pos) {
        if (rayDebug != null) {
            if (app.getRootNode().hasChild(rayDebug)) {
                rayDebug.setLocalTranslation(pos);
            } else {
                app.getRootNode().attachChild(rayDebug);
                rayDebug.setLocalTranslation(pos);
            }
        }
    }
    
    public Spatial getDebug(){
        return rayDebug;
    }
    
    public enum CastFrom{
        MOUSE,
        SCREEN_CENTER;
    }

    public void clear() {
        rayDebug.removeFromParent();
    }
}

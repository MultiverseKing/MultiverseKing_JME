package org.hexgridapi.core.control;

import com.jme3.app.Application;
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

    private final Application app;
    private Spatial collisionObj;
    private Node rayDebug;

    /**
     * Handle the mouse picking. no debug.
     *
     * @param app application running
     * @param collisionObj the node the ray interact with
     */
    public GridRayCastControl(Application app, Spatial collisionObj) {
        this(app, collisionObj, null);
    }

    /**
     * Handle the mouse picking. taking into account the debug.
     *
     * @param app currently running app.
     * @param collisionNode the node the ray interact with
     * @param debugColor used to show where the ray collide, if null no debug
     */
    public GridRayCastControl(Application app, Spatial collisionNode, ColorRGBA debugColor) {
        this.collisionObj = collisionNode;
        this.app = app;
        if (debugColor != null) {
            initialiseDebug(debugColor);
        }
    }

    private void initialiseDebug(ColorRGBA debugColor) {
        Sphere sphere = new Sphere(30, 30, 0.2f);
        rayDebug = new Node("DebugRay");
        Geometry geo = new Geometry("SphereDebugRayCast" + debugColor, sphere);
        Material mark_mat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat.setColor("Color", debugColor);
        geo.setMaterial(mark_mat);
        rayDebug.attachChild(geo);
        attachCoordinateAxes();
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

    /**
     * Cast the defined ray on the defined collision node then return it
     * converted as Hex event.
     *
     * @param ray Can be get from {
     * @see #get3DRay(CastFrom)}
     * @return null if no collision.
     */
    public MouseInputEvent castRay(Ray ray) {
        CollisionResults results = new CollisionResults();
        ray = ray != null ? ray : get3DRay(CastFrom.SCREEN_CENTER);
        collisionObj.collideWith(ray, results);
        if (results.size() != 0) {
            for (int i = 0; i < results.size(); i++) {
                CollisionResult closest = results.getCollision(i);
                if (!closest.getGeometry().getName().contains("SphereDebugRayCast")) {
                    setDebugPosition(closest.getContactPoint());
                    HexCoordinate newPos = convertMouseCollision(results);
                    return new MouseInputEvent(null, newPos, null, ray, closest);
                }
            }
            return null;
        } else {
            //Error catching.
            System.out.println("null raycast");
            if (collisionObj instanceof Node) {
                ((Node) collisionObj).detachChild(rayDebug);
            } else if (collisionObj.getParent() != null) {
                collisionObj.getParent().detachChild(rayDebug);
            }
            return null;
        }
    }

    /**
     * Generate a new ray by converting 2d screen coordinates
     * to 3D world coordinates.
     *
     * @param from Which 2d screen coordinate as parameter
     * @return Newly generated ray from parameter
     */
    public Ray get3DRay(CastFrom from) {
        Vector2f click2d;
        switch (from) {
            case MOUSE:
                click2d = app.getInputManager().getCursorPosition();
                break;
            case SCREEN_CENTER:
                click2d = new Vector2f(app.getCamera().getWidth() / 2, app.getCamera().getHeight() / 2);
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

    public void setCollisionNode(Spatial collisionNode) {
        clearRayDebug();
        this.collisionObj = collisionNode;
    }

    private void setDebugPosition(Vector3f pos) {
        if (rayDebug != null && collisionObj != null) {
            Node parent = (Node) (collisionObj instanceof Node ? collisionObj : collisionObj.getParent());
            while (parent != null) {
                if (parent.getName().equals("HexGridNode")) {
                    if (parent.hasChild(rayDebug)) {
                        rayDebug.setLocalTranslation(pos);
                    } else {
                        parent.attachChild(rayDebug);
                        rayDebug.setLocalTranslation(pos);
                    }
                    return;
                }
                parent = parent.getParent();
            }
            throw new NullPointerException("HexGridNode cannot be found.");
        }
    }

    public Spatial getDebug() {
        return rayDebug;
    }

    public enum CastFrom {

        MOUSE,
        SCREEN_CENTER;
    }

    public void clearRayDebug() {
        rayDebug.removeFromParent();
    }
}

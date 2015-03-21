package org.hexgridapi.core.control;

import com.jme3.app.Application;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;
import java.util.Set;
import org.hexgridapi.core.HexGrid;
import org.hexgridapi.core.HexSetting;
import org.hexgridapi.core.MapData;
import org.hexgridapi.core.mesh.MeshParameter;
import org.hexgridapi.events.MouseInputEvent;
import org.hexgridapi.utility.HexCoordinate;
import org.hexgridapi.utility.Vector2Int;

/**
 *
 * @author roah
 */
public class GhostControl extends ChunkControl {

    private final Node collisionNode = new Node("GhostCollision");
    private final int radius = 2;
    private final HexGrid system;
    private final Camera cam;
    private GridRayCastControl rayControl;
    private Vector3f oldCamPosition = new Vector3f();

    public GhostControl(Application app, MeshParameter meshParam, MapData.GhostMode mode, Vector2Int pos, HexGrid system) {
        super(meshParam, app.getAssetManager(), mode, pos, false);
        if (mode.equals(MapData.GhostMode.NONE)) {
            throw new UnsupportedOperationException(mode + " isn't allowed for Ghost Control");
        }
        this.cam = app.getCamera();
        Geometry collisionPlane = new Geometry("ghostCollision", genQuad(3f));
        Material mat = assetManager.loadMaterial("Materials/ghostCollision.j3m");
        collisionPlane.setMaterial(mat);
        collisionNode.attachChild(collisionPlane);
        this.rayControl = new GridRayCastControl(app, collisionNode, ColorRGBA.Green);
        this.system = system;
    }

    private static Mesh genQuad(float radius) {
        float sizeX = HexSetting.CHUNK_SIZE * HexSetting.HEX_WIDTH;
        float sizeY = HexSetting.CHUNK_SIZE * (HexSetting.HEX_RADIUS * 1.5f) - HexSetting.HEX_RADIUS / 2;
        float posY = 0;
        Vector3f[] vertices = new Vector3f[]{
            new Vector3f(-(HexSetting.HEX_WIDTH / 2 + sizeX * radius), posY, -(HexSetting.HEX_RADIUS + sizeY * radius)), // top left

            new Vector3f(sizeX + sizeX * radius, posY, -(HexSetting.HEX_RADIUS + sizeY * radius)), // top right

            new Vector3f(-(HexSetting.HEX_WIDTH / 2 + sizeX * radius), posY, sizeY + sizeY * radius), // bot left

            new Vector3f(sizeX + sizeX * radius, posY, sizeY + sizeY * radius) // bot right
        };
        Vector2f[] texCoord = new Vector2f[]{new Vector2f(), new Vector2f(1, 0), new Vector2f(0, 1), new Vector2f(1, 1)};
        int[] index = new int[]{0, 2, 1, 1, 2, 3};

        Mesh result = new Mesh();
        result.setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
        result.setBuffer(VertexBuffer.Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoord));
        result.setBuffer(VertexBuffer.Type.Index, 3, BufferUtils.createIntBuffer(index));
        result.createCollisionData();
        result.updateBound();

        return result;
    }

    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);
        if (spatial != null && spatial instanceof Node) {
            ((Node) spatial).getParent().getParent().attachChild(collisionNode);
            genGhost();
            updatePosition();
        } else if (spatial == null) {
            // cleanup
        } else {
            throw new UnsupportedOperationException("Provided spatial must be a Node.");
        }
    }

    private void genGhost() {
        if (mode.equals(MapData.GhostMode.GHOST_PROCEDURAL)
                || mode.equals(MapData.GhostMode.PROCEDURAL)) {
            Set<Vector2Int> list = system.getChunksNodes();
            for (int x = -radius; x <= radius; x++) {
                for (int y = -radius; y <= radius; y++) {
                    if (!(x == 0 && y == 0)
                            && !list.contains(chunkPosition.add(x, y))) {
                        genProcedural(x, y);
                    }
                }
            }
        } else {
            Geometry child = (Geometry) ((Node) ((Node) spatial).getChild("TILES.0|0")).getChild(0);
            Material mat = assetManager.loadMaterial("Materials/hexMat.j3m");
            mat.setTexture("ColorMap", child.getMaterial().getTextureParam("ColorMap").getTextureValue());
            mat.setColor("Color", new ColorRGBA(0, 0, 1, 0.5f));
            child.getMaterial().setColor("Color", new ColorRGBA(0, 0, 1, 0.7f));
            for (int x = -radius; x <= radius; x++) {
                for (int y = -radius; y < radius; y++) {
                    if (!(x == 0 && y == 0)) {
                        Node tileNode = new Node("TILES." + x + "|" + y);
                        Geometry tileGeo = new Geometry("NO_TILE", child.getMesh());
                        tileGeo.setMaterial(mat);
                        tileNode.attachChild(tileGeo);
                        tileNode.setLocalTranslation(HexGrid.getChunkWorldPosition(chunkPosition.add(x, y)));
                        ((Node) spatial).attachChild(tileNode);
                        tileNode.setCullHint(Spatial.CullHint.Always);
                    }
                }
            }
        }
    }

    private void genProcedural(int x, int y) {
        Node tileNode;
        if (((Node) spatial).getChild("TILES." + x + "|" + y) != null) {
            tileNode = (Node) ((Node) spatial).getChild("TILES." + x + "|" + y);
            tileNode.detachAllChildren();
        } else {
            tileNode = new Node("TILES." + x + "|" + y);
            tileNode.setLocalTranslation(HexGrid.getChunkWorldPosition(chunkPosition.add(x, y)));
        }
        setMesh(tileNode, meshParam.getMesh(onlyGround, chunkPosition.add(x, y)));
        if (((Node) spatial).getChild("TILES." + x + "|" + y) == null) {
            ((Node) spatial).attachChild(tileNode);
        }
    }

    @Override
    protected void controlUpdate(float tpf) {
        if (spatial != null && !spatial.getCullHint().equals(Spatial.CullHint.Always)
                && !cam.getLocation().equals(oldCamPosition)) {
            MouseInputEvent event = rayControl.castRay(null);
            if (event != null) {
                HexCoordinate pos = event.getPosition();
                if (pos != null && !isInRange(pos.getCorrespondingChunk())) {
                    this.chunkPosition = pos.getCorrespondingChunk();
                    updatePosition();
                }
                oldCamPosition = cam.getLocation().clone();
            }
        }
    }

    private void updatePosition() {
        Vector3f pos = HexGrid.getChunkWorldPosition(chunkPosition);
        spatial.setLocalTranslation(pos);
        collisionNode.setLocalTranslation(pos);
        if (mode.equals(MapData.GhostMode.GHOST)) {
        } else if (mode.equals(MapData.GhostMode.GHOST_PROCEDURAL)
                || mode.equals(MapData.GhostMode.PROCEDURAL)) {
            update();
            genGhost();
        }
        updateCulling();
    }

    public void updateCulling() {
        Set<Vector2Int> list = system.getChunksNodes();
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                if (list.contains(chunkPosition.add(x, y))) {
                    if (mode.equals(MapData.GhostMode.GHOST)) {
                        ((Node) spatial).getChild("TILES." + x + "|" + y).setCullHint(Spatial.CullHint.Always);
                    } else {
                        ((Node) ((Node) spatial).getChild("TILES." + x + "|" + y)).detachAllChildren();
                    }
                } else if (mode.equals(MapData.GhostMode.GHOST)) {
                    ((Node) spatial).getChild("TILES." + x + "|" + y).setCullHint(Spatial.CullHint.Inherit);
                }
            }
        }
    }

    private boolean isInRange(Vector2Int pos) {
        for (int x = -(radius - 1); x <= (radius - 1); x++) {
            for (int y = -(radius - 1); y <= (radius - 1); y++) {
                if (pos.equals(chunkPosition.add(x, y))) {
                    return true;
                }
            }
        }
        return false;
    }

    public void show() {
        spatial.setCullHint(Spatial.CullHint.Inherit);
    }

    public void hide() {
        spatial.setCullHint(Spatial.CullHint.Always);
    }   
}
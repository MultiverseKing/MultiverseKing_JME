package org.hexgridapi.core.control;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;
import java.util.Set;
import org.hexgridapi.core.HexGrid;
import org.hexgridapi.core.HexSetting;
import org.hexgridapi.core.mesh.MeshParameter;
import org.hexgridapi.events.MouseInputEvent;
import org.hexgridapi.utility.HexCoordinate;
import org.hexgridapi.utility.Vector2Int;

/**
 *
 * @author roah
 */
public class ProceduralGhostControl extends ChunkControl {

    private final Geometry collisionPlane;
    private final GridRayCastControl rayControl;
    private final HexGrid system;

    public ProceduralGhostControl(SimpleApplication app, MeshParameter meshParam, HexGrid.GhostMode mode, Vector2Int pos, HexGrid system) {
        super(meshParam, app.getAssetManager(), mode, pos, false);
        this.collisionPlane = new Geometry("ghostCollision", genQuad(2));
        Material mat = assetManager.loadMaterial("Materials/ghostCollision.j3m");
        collisionPlane.setMaterial(mat);
        this.rayControl = new GridRayCastControl(app, app.getRootNode(), ColorRGBA.Green, true);
        this.system = system;
    }

    private static Mesh genQuad(int radius) {
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
            genGhost();
            updatePosition();
        } else if (spatial == null) {
            // cleanup
        } else {
            throw new UnsupportedOperationException("Provided spatial must be a Node.");
        }
    }

    private void genGhost() {
        ((Node) spatial).attachChild(collisionPlane);
        Geometry child = (Geometry) ((Node) spatial).getChild("NO_TILE");
        Mesh mesh = ((Geometry) ((Node) spatial).getChild("NO_TILE")).getMesh();
        Material mat = assetManager.loadMaterial("Materials/hexMat.j3m");
        mat.setTexture("ColorMap", child.getMaterial().getTextureParam("ColorMap").getTextureValue());
        mat.setColor("Color", new ColorRGBA(0, 0, 1, 0.5f));
        child.getMaterial().setColor("Color", new ColorRGBA(0, 0, 1, 0.7f));
        child.setName("NO_TILE.0|0");
        for (int x = -1; x < 2; x++) {
            for (int y = -1; y < 2; y++) {
                if (!(x == 0 && y == 0)) {
                    Geometry geo = new Geometry("NO_TILE." + x + "|" + y, mesh);
                    geo.setMaterial(mat);
                    geo.setLocalTranslation(HexGrid.getChunkWorldPosition(chunkPosition.add(x, y)));
                    ((Node) spatial).attachChild(geo);
                    geo.setCullHint(Spatial.CullHint.Always);
                }
            }
        }
    }

    @Override
    protected void controlUpdate(float tpf) {
        if (spatial != null && !spatial.getCullHint().equals(Spatial.CullHint.Always)) {
            MouseInputEvent event = rayControl.castRay(null);
            HexCoordinate pos = event.getPosition();
            if (pos != null) {
                setPosition(pos.getCorrespondingChunk());
            }
        }
    }

    private void updatePosition() {
        Vector3f pos = HexGrid.getChunkWorldPosition(chunkPosition);
        spatial.setLocalTranslation(new Vector3f(pos.x, pos.y - 0.01f, pos.z));
        updateCulling();
    }

    public void updateCulling() {
        Set<Vector2Int> list = system.getChunksNodes();
        for (int x = -1; x < 2; x++) {
            for (int y = -1; y < 2; y++) {
                if (list.contains(chunkPosition.add(x, y))) {
                    ((Node) spatial).getChild("NO_TILE." + x + "|" + y).setCullHint(Spatial.CullHint.Always);
                } else {
                    ((Node) spatial).getChild("NO_TILE." + x + "|" + y).setCullHint(Spatial.CullHint.Inherit);
                }
            }
        }
    }

    private boolean isInRange(Vector2Int pos) {
        for (int x = -1; x < 2; x++) {
            for (int y = -1; y < 2; y++) {
                if (pos.equals(chunkPosition.add(x, y))) {
                    return true;
                }
            }
        }
        return false;
    }

    private void setPosition(Vector2Int pos) {
        if (!isInRange(pos)) {
            this.chunkPosition = pos;
            updatePosition();
        }
    }

    public void show() {
        spatial.setCullHint(Spatial.CullHint.Inherit);
    }

    public void hide() {
        spatial.setCullHint(Spatial.CullHint.Always);
    }
}
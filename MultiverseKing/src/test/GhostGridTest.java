package test;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;
import java.util.logging.Level;
import kingofmultiverse.GlobalParameter;
import org.hexgridapi.core.HexSetting;

/**
 *
 * @author roah
 */
public class GhostGridTest extends SimpleApplication {

    public static void main(String[] args) {
        GhostGridTest app = new GhostGridTest();
        java.util.logging.Logger.getLogger("").setLevel(Level.WARNING);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        GlobalParameter param = new GlobalParameter(this, false);

//        genGhost();
    }

    private Mesh genQuad(float size) {
        Vector3f[] vertices = new Vector3f[]{
            new Vector3f(-1f * size, 0, -1f * size), new Vector3f(1f * size, 0, -1f * size),
            new Vector3f(-1f * size, 0, 1f * size), new Vector3f(1f * size, 0, 1f * size)};
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

    private void genGhost() {
        Geometry plane = new Geometry("plane", genQuad(14 * (HexSetting.HEX_RADIUS * 2)));
        Material mat = assetManager.loadMaterial("Materials/ghostShader.j3m");
        mat.setColor("Color", ColorRGBA.LightGray);
        plane.setMaterial(mat);
        rootNode.attachChild(plane);
    }
}

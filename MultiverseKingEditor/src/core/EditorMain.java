package core;

import hexmapeditor.gui.JHexEditorMenu;
import com.jme3.app.SimpleApplication;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.renderer.RenderManager;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeCanvasContext;
import hexmapeditor.HexMapSystem;
import hexmapeditor.gui.JHexEditorMenu.HexMenuAction;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import org.hexgridapi.core.MapData;
import org.hexgridapi.core.appstate.MapDataAppState;
import org.hexgridapi.core.appstate.MouseControlSystem;
import org.multiversekingesapi.EntityDataAppState;
import org.multiversekingesapi.field.AreaEventSystem;
import org.multiversekingesapi.field.position.HexPositionSystem;
import org.multiversekingesapi.render.AreaEventRenderDebugSystem;
import org.multiversekingesapi.render.RenderSystem;

/**
 *
 * @author normenhansen, roah
 */
public class EditorMain extends SimpleApplication {

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                AppSettings settings = new AppSettings(true);
                Dimension dim = new Dimension(1024, 768);
                settings.setWidth(dim.width);
                settings.setHeight(dim.height);
                java.util.logging.Logger.getLogger("").setLevel(Level.WARNING);

                final JFrame rootWindow = new JFrame("Hex Grid Editor");
                rootWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                rootWindow.getContentPane().setLayout(new BorderLayout());
                final EditorMain editorMain = new EditorMain(rootWindow);
                rootWindow.addComponentListener(new ComponentAdapter() {
                    @Override
                    public void componentResized(final ComponentEvent e) {
                        super.componentResized(e);
                        editorMain.enqueue(new Callable<Void>() {
                            @Override
                            public Void call() throws Exception {
                                editorMain.getCamera().resize(e.getComponent().getWidth(), e.getComponent().getHeight(), true);
                                return null;
                            }
                        });
                    }
                });
                editorMain.setSettings(settings);
                editorMain.createCanvas(); // create canvas!
                JmeCanvasContext ctx = (JmeCanvasContext) editorMain.getContext();
                ctx.setSystemListener(editorMain);
                ctx.getCanvas().setPreferredSize(dim);

                //-------------
                JMenuBar menuBar = new JMenuBar();
                JHexEditorMenu editorMenu = new JHexEditorMenu(editorMain);
                editorMenu.setAction(HexMenuAction.New);
                editorMenu.setAction(HexMenuAction.Load);
                menuBar.add(editorMenu);

                rootWindow.setJMenuBar(menuBar);
                //-------------

                rootWindow.pack();
                rootWindow.setMinimumSize(dim);
                rootWindow.setLocationRelativeTo(null);
                rootWindow.setVisible(true);

                editorMain.startCanvas();
            }
        });
    }
    private final JFrame rootWindow;
    private RTSCamera rtsCam;
    private boolean isStart = false;

    public EditorMain(JFrame rootWindow) {
        this.rootWindow = rootWindow;
    }

    public boolean isStart() {
        return isStart;
    }

    public JFrame getRootWindow() {
        return rootWindow;
    }

    public RTSCamera getRtsCam() {
        return rtsCam;
    }

    @Override
    public void simpleInitApp() {
        super.inputManager.clearMappings();
        rtsCam = new DefaultParam(this, false).getCam();
    }

    public void startAreaEditor() {
        inputManager.addMapping("Confirm", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addMapping("Cancel", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));

        MapData mapData = new MapData(new String[]{"EARTH", "ICE", "NATURE", "VOLT"}, assetManager, MapData.GhostMode.GHOST_PROCEDURAL);
        stateManager.attachAll(
                new MapDataAppState(mapData),
                new EntityDataAppState(),
                new RenderSystem(),
                new HexPositionSystem(),
                new MouseControlSystem(), 
                new HexMapSystem(mapData, assetManager, getRootNode()),
                new AreaEventSystem(),
                new AreaEventRenderDebugSystem());

        rootWindow.getContentPane().add(((JmeCanvasContext) this.getContext()).getCanvas(), BorderLayout.CENTER);
        isStart = true;
    }

    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}

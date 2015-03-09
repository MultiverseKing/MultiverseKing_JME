package test;

import com.jme3.app.SimpleApplication;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.renderer.RenderManager;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeCanvasContext;
import core.HexMapSystem;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import org.hexgridapi.core.MapData;
import org.hexgridapi.core.appstate.MapDataAppState;

/**
 * test
 *
 * @author normenhansen, Roah
 */
public class EditorMain extends SimpleApplication {
    
    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                // ... see below ...
                AppSettings settings = new AppSettings(true);
                settings.setWidth(1024);
                settings.setHeight(768);
                java.util.logging.Logger.getLogger("").setLevel(Level.WARNING);

                JFrame rootWindow = new JFrame("Hex Grid Editor");
                rootWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                rootWindow.getContentPane().setLayout(new BorderLayout());
//                JPanel rootPanel = new JPanel(new GridBagLayout());
                EditorMain editorMain = new EditorMain(rootWindow);

                editorMain.setSettings(settings);
                editorMain.createCanvas(); // create canvas!
                JmeCanvasContext ctx = (JmeCanvasContext) editorMain.getContext();
                ctx.setSystemListener(editorMain);
                Dimension dim = new Dimension(1024, 768);
                ctx.getCanvas().setPreferredSize(dim);


                //-------------
                JMenuBar menuBar = new JMenuBar();
                JMenu menu = new JMenu("Hex Editor");
                menuBar.add(menu);
                rootWindow.setJMenuBar(menuBar);
                JMenuItem item = new JMenuItem(new JHexEditor("New Map", editorMain));
                menu.add(item);
                //-------------
                
                rootWindow.getContentPane().add(ctx.getCanvas(), BorderLayout.CENTER);
                
                rootWindow.pack();
                rootWindow.setVisible(true);
                rootWindow.setLocationRelativeTo(null);

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

    public boolean isStart(){
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
        //Init general input 
        super.inputManager.clearMappings();
//        getFlyByCamera().setEnabled(false);
        rtsCam = new DefaultParam(this, false).getCam();
    }

    public void startEditor() {
        this.enqueue(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                inputManager.addMapping("Confirm", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
                inputManager.addMapping("Cancel", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
                //init the Entity && Hex System

                MapData mapData = new MapData(new String[]{"EARTH", "ICE", "NATURE", "VOLT"}, assetManager);
                stateManager.attach(new MapDataAppState(mapData));
                stateManager.attach(new HexMapSystem(mapData, assetManager, getRootNode()));

                return null;
            }
        });
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

package test;

import com.jme3.app.SimpleApplication;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.renderer.RenderManager;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeCanvasContext;
import core.HexMapSystem;
import core.gui.CustomDialog;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import javax.swing.AbstractAction;
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
                Dimension dim = new Dimension(1024, 768);
                settings.setWidth(dim.width);
                settings.setHeight(dim.height);
                java.util.logging.Logger.getLogger("").setLevel(Level.WARNING);

                final JFrame rootWindow = new JFrame("Hex Grid Editor");
                rootWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                rootWindow.getContentPane().setLayout(new BorderLayout());
                final EditorMain editorMain = new EditorMain(rootWindow);
                rootWindow.addComponentListener(new ComponentListener() {
                    @Override
                    public void componentResized(final ComponentEvent e) {
                        editorMain.enqueue(new Callable<Void>() {
                            @Override
                            public Void call() throws Exception {
                                editorMain.getCamera().resize(e.getComponent().getWidth(), e.getComponent().getHeight(), true);
                                return null;
                            }
                        });
                    }

                    @Override
                    public void componentMoved(ComponentEvent e) {
                    }

                    @Override
                    public void componentShown(ComponentEvent e) {
                    }

                    @Override
                    public void componentHidden(ComponentEvent e) {
                    }
                });

                editorMain.setSettings(settings);
                editorMain.createCanvas(); // create canvas!
                JmeCanvasContext ctx = (JmeCanvasContext) editorMain.getContext();
                ctx.setSystemListener(editorMain);
                ctx.getCanvas().setPreferredSize(dim);


                //-------------
                JMenuBar menuBar = new JMenuBar();
                JMenu menu = new JMenu("Hex Editor");
                menuBar.add(menu);
                rootWindow.setJMenuBar(menuBar);
                JMenuItem item = new JMenuItem(new JHexEditor("New Map", editorMain));
                menu.add(item);
                menu.add(new AbstractAction("Load Map") {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        CustomDialog customDialog = new CustomDialog(rootWindow, false);
                        customDialog.setLocationRelativeTo(rootWindow);
                        customDialog.setVisible(true);

                        String s = customDialog.getValidatedText();
                        if (s != null) {
                            //The text is valid.
                            System.err.println("Load Map " + s);
                        }
                    }
                });
//                item = new JMenuItem(new JHexEditor("Load Map", editorMain));
                //-------------

                rootWindow.getContentPane().add(ctx.getCanvas(), BorderLayout.CENTER);

                rootWindow.pack();
                rootWindow.setLocationRelativeTo(null);
                rootWindow.setVisible(true);
                rootWindow.setMinimumSize(dim);

                editorMain.startCanvas();
            }

            class resizeListener extends ComponentAdapter {

                @Override
                public void componentResized(ComponentEvent e) {
                    //Recalculate the variable you mentioned
                }
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
        //Init general input 
        super.inputManager.clearMappings();
//        getFlyByCamera().setEnabled(false);
        rtsCam = new DefaultParam(this, false).getCam();
    }

    public void startEditor() {
        this.enqueue(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                //init the Entity && Hex System
                inputManager.addMapping("Confirm", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
                inputManager.addMapping("Cancel", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));

                MapData mapData = new MapData(new String[]{"EARTH", "ICE", "NATURE", "VOLT"}, assetManager, MapData.GhostMode.GHOST_PROCEDURAL);
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
//         cam.setFrustumPerspective( 45.0f, (float) DisplaySystem.getDisplaySystem().getRenderer().getWidth()
//
//        / (float) DisplaySystem.getDisplaySystem().getRenderer().getHeight(), 1, 1000 );
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}

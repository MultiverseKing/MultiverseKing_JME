package test;

import com.jme3.app.SimpleApplication;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.renderer.RenderManager;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeCanvasContext;
import core.EditorSystem;
import gui.JHexEditor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import org.hexgridapi.core.MapData;
import org.hexgridapi.core.appstate.MapDataAppState;
import tonegod.gui.core.Screen;

/**
 * test
 *
 * @author normenhansen, Roah
 */
public class EditorMain extends SimpleApplication {

//    public static void main(String[] args) {
//        TestMain1 app = new TestMain1();
//        java.util.logging.Logger.getLogger("").setLevel(Level.WARNING);
//        app.start();
//    }
    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                // ... see below ...
                AppSettings settings = new AppSettings(true);
                settings.setWidth(640);
                settings.setHeight(480);
                java.util.logging.Logger.getLogger("").setLevel(Level.WARNING);

                JFrame rootWindow = new JFrame("Hex Grid Editor");
                rootWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                JPanel rootPanel = new JPanel(new GridBagLayout());
                EditorMain editorMain = new EditorMain(rootWindow, rootPanel);

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

                GridBagConstraints bagConstraints = new GridBagConstraints();
                bagConstraints.gridx = 1;
                bagConstraints.gridy = 1;
                bagConstraints.gridwidth = 1;
                bagConstraints.gridheight = 1;
                bagConstraints.weightx = 0;
                bagConstraints.weighty = 0;
                bagConstraints.insets = new Insets(5, 5, 5, 5);
                bagConstraints.anchor = GridBagConstraints.CENTER;
                bagConstraints.fill = GridBagConstraints.BOTH;
                // add the JME canvas
                bagConstraints.gridx = 0;
                bagConstraints.weightx = 1;
                bagConstraints.weighty = 1;
                rootPanel.add(ctx.getCanvas(), bagConstraints);


                rootWindow.add(rootPanel);
                rootWindow.pack();
                rootWindow.setVisible(true);
                rootWindow.setLocationRelativeTo(null);

                editorMain.startCanvas();
            }
        });
    }
    private final JFrame rootWindow;
    private final JPanel rootPanel;
    private Screen screen;
    private RTSCamera rtsCam;

    public EditorMain(JFrame rootWindow, JPanel rootPanel) {
        this.rootWindow = rootWindow;
        this.rootPanel = rootPanel;
    }

    public JFrame getRootWindow() {
        return rootWindow;
    }

    public JPanel getRootPanel() {
        return rootPanel;
    }
    
    public Screen getScreen() {
        return screen;
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
        screen = new Screen(this);
        this.enqueue(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                inputManager.addMapping("Confirm", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
                inputManager.addMapping("Cancel", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));

                //Create a new screen for tonegodGUI to work with.
                guiNode.addControl(screen);
                //init the Entity && Hex System

                MapData mapData = new MapData(new String[]{"EARTH", "ICE", "NATURE", "VOLT"}, assetManager);
                stateManager.attach(new MapDataAppState(mapData));
                stateManager.attach(new EditorSystem(mapData, assetManager, getRootNode()));

                return null;
            }
        });
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

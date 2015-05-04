package core;

import com.jme3.app.Application;
import hexmapeditor.gui.database.JDataBaseMenu;
import hexmapeditor.gui.hexmap.JHexEditorMenu;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.renderer.RenderManager;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeCanvasContext;
import gui.JPropertiesPanel;
import hexmapeditor.HexMapSystem;
import hexmapeditor.gui.JPropertiesPanelHolder;
import hexmapeditor.gui.hexmap.JHexEditorMenu.HexMenuAction;
import hexmapeditor.gui.hexmap.JHexPropertiesPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import org.hexgridapi.core.MapData;
import org.hexgridapi.core.appstate.HexGridDefaultApp;
import org.hexgridapi.core.appstate.MapDataAppState;
import org.hexgridapi.core.appstate.MouseControlSystem;

/**
 *
 * @author normenhansen, roah
 */
public class HexGridEditorMain extends HexGridDefaultApp {

    protected final JFrame rootWindow;
    private final JPropertiesPanelHolder holder = new JPropertiesPanelHolder();
    private boolean isStart = false;

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                HexGridEditorMain editorMain = new HexGridEditorMain("Hex Grid Editor");
            }
        });
    }

    public HexGridEditorMain(String windowName) {
        AppSettings initSettings = new AppSettings(true);
        Dimension dim = new Dimension(1024, 768);
        initSettings.setWidth(dim.width);
        initSettings.setHeight(dim.height);
        java.util.logging.Logger.getLogger("").setLevel(Level.WARNING);


        rootWindow = new JFrame(windowName);
        rootWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        rootWindow.addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosed(WindowEvent e) {
                stop();
            }
        });
        rootWindow.getContentPane().setLayout(new BorderLayout());

        rootWindow.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(final ComponentEvent e) {
                super.componentResized(e);
                enqueue(new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        getCamera().resize(e.getComponent().getWidth(), e.getComponent().getHeight(), true);
                        return null;
                    }
                });
            }
        });
        setSettings(initSettings);
        createCanvas(); // create canvas!
        JmeCanvasContext ctx = (JmeCanvasContext) getContext();
        ctx.setSystemListener(this);
        ctx.getCanvas().setPreferredSize(dim);

        //-------------
        JMenuBar menuBar = new JMenuBar();
        JHexEditorMenu editorMenu = new JHexEditorMenu(this);
        editorMenu.setAction(HexMenuAction.New);
        editorMenu.setAction(HexMenuAction.Load);
        menuBar.add(editorMenu);

        JDataBaseMenu dataMenu = new JDataBaseMenu(this);
        menuBar.add(dataMenu);

        rootWindow.setJMenuBar(menuBar);
        //-------------

        rootWindow.pack();
        rootWindow.setMinimumSize(dim);
        rootWindow.setLocationRelativeTo(null);
        rootWindow.setVisible(true);

        startCanvas();
    }

    public boolean isStart() {
        return isStart;
    }

    public JFrame getRootWindow() {
        return rootWindow;
    }

    public void startHexGridEditor() {
        inputManager.addMapping("Confirm", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addMapping("Cancel", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));

        MapData mapData = new MapData(new String[]{"EARTH", "ICE", "NATURE", "VOLT"}, assetManager, MapData.GhostMode.GHOST_PROCEDURAL);
        stateManager.attachAll(
                new MapDataAppState(mapData),
                new MouseControlSystem(),
                new HexMapSystem(mapData, assetManager, getRootNode()));

        rootWindow.getContentPane().add(((JmeCanvasContext) this.getContext()).getCanvas(), BorderLayout.CENTER);

        rootWindow.getContentPane().add(holder, BorderLayout.EAST);
        holder.add(new JHexPropertiesPanel(this));
        rootWindow.revalidate();

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

    public void addPropertiesPanel(JPropertiesPanel propertiesPanel) {
        holder.add(propertiesPanel);
    }
}

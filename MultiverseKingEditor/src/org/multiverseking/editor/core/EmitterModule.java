package org.multiverseking.editor.core;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.input.ChaseCamera;
import com.jme3.scene.Node;
import emitterbuilder.builder.EmitterBuilder;
import java.awt.Canvas;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.concurrent.Callable;
import org.hexgridapi.editor.utility.gui.Base3DModuleTab;
import org.hexgridapi.utility.Vector2Int;
import tonegod.gui.core.Screen;

/**
 *
 * @author roah
 */
public class EmitterModule extends Base3DModuleTab {

    private final SimpleApplication app;
    private final ChaseCamera chaseCam;
    private EmitterBuilder builder;
    private final Screen screen;
    private Canvas canvas;

    public EmitterModule(final SimpleApplication app) {
        super(app.getAssetManager().loadTexture("org/hexgridapi/assets/Textures/"
                + "Icons/Buttons/hexIconBW.png").getImage(),
                "Emitter Module", null, false);
        this.app = app;
        //@todo need to be cleanned
        app.getAssetManager().registerLocator("/home/roah/Documents/jmonkey/3.1/tonegodProjects/EmitterBuilder/assets", FileLocator.class);
        screen = new Screen(app, "tonegod/gui/style/atlasdef/style_map.gui.xml");
        screen.setUseTextureAtlas(true, "tonegod/gui/style/atlasdef/atlas.png");
//        screen.setUseCustomCursors(true);
        
        builder = new EmitterBuilder(app, screen);
        chaseCam = new ChaseCamera(app.getCamera(), builder.getRootNode(), app.getInputManager());
        app.enqueue(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                if(app.getGuiNode().getControl(Screen.class) == null)
                    app.getGuiNode().addControl(screen);
                return null;
            }
        });
    }

    @Override
    public void onContextGainFocus(final SimpleApplication app, final Canvas canvas) {
        this.canvas = canvas;
        add(canvas);
        revalidate();
//        app.enqueue(new Callable<Void>() {
//            @Override
//            public Void call() throws Exception {
//                if(app.getGuiNode().getControl(Screen.class) == null)
//                    app.getGuiNode().addControl(screen);
//                return null;
//            }
//        });
        app.getStateManager().attach(builder);
        canvas.addComponentListener(adapter);
    }

    @Override
    public void onContextLostFocus() {
        canvas.removeComponentListener(adapter);
//        screen.removeElement(builder.mainWin);
//        app.getGuiNode().removeControl(screen);
        app.getStateManager().detach(builder);
    }

    @Override
    public Node getModuleNode() {
        return builder.getRootNode();
    }
    
    ComponentAdapter adapter = new ComponentAdapter() {
        @Override
        public void componentResized(ComponentEvent e) {
            super.componentResized(e);
            final Vector2Int dim = new Vector2Int(e.getComponent().getWidth(), e.getComponent().getHeight());
            app.enqueue(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    // @todo
                    builder.mainWin.setPosition(0, screen.getHeight()-builder.mainWin.getHeight());
                    return null;
                }
            });
        }
    };
}

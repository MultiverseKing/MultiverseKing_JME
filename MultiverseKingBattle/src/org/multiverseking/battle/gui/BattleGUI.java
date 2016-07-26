                  package org.multiverseking.battle.gui;

import com.jme3.input.controls.ActionListener;
import com.simsilica.es.Entity;
import com.simsilica.es.EntitySet;
import org.multiverseking.battle.core.focus.FocusComponent;
import org.multiverseking.core.utility.EntitySystemAppState;
import org.multiverseking.core.utility.MultiverseCoreGUI;
import org.multiverseking.render.RenderComponent;
import tonegod.gui.core.Screen;

/**
 *
 * @author roah
 */
public class BattleGUI extends EntitySystemAppState {

    private Screen screen;
    private Options optionMenu;
    private HelperHUD helperHUD;

    @Override
    protected EntitySet initialiseSystem() {
        screen = ((MultiverseCoreGUI) app).getScreen();

        // Initialise the option Menu
        optionMenu = new Options(((MultiverseCoreGUI) app).getScreen(), this);
        app.getInputManager().addListener(keyListeners, "Options");

        return entityData.getEntities(FocusComponent.class, RenderComponent.class);
    }

    @Override
    protected void updateSystem(float tpf) {
    }

    @Override
    protected void addEntity(Entity e) {
//        basicHUD.setCurrent(e.get(RenderComponent.class).getName(),
//                e.get(RenderComponent.class).getRenderType());

    }

    @Override
    protected void updateEntity(Entity e) {
    }

    @Override
    protected void removeEntity(Entity e) {
    }

    @Override
    protected void cleanupSystem() {
    }

    private final ActionListener keyListeners = new ActionListener() {
        @Override
        public void onAction(String name, boolean isPressed, float tpf) {
            if (!isPressed) {
                if (optionMenu.isInitialized()) {
                    app.getStateManager().detach(optionMenu);
                } else {
                    app.getStateManager().attach(optionMenu);
                }
            }
        }
    };

    public void enableHelper() {
        if (helperHUD == null) {
            helperHUD = new HelperHUD(screen);
        } else if (helperHUD.isVisible()) {
            helperHUD.show(false);
        } else {
            helperHUD.show(true);
        }
    }
}

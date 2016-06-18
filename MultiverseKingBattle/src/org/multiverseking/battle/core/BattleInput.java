/*
 * Copyright (C) 2016 roah
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.multiverseking.battle.core;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.KeyTrigger;
import org.multiverseking.core.MultiverseCoreState;

/**
 *
 * @author roah
 */
public class BattleInput extends AbstractAppState {

    private SimpleApplication app;
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = (SimpleApplication) app;
        registerCharacterInput();
        switch (app.getStateManager().getState(MultiverseCoreState.class).getMapping()) {
            case col:
                break;
            case fr:
                break;
            default:
                // Use US input by default.
        }
    }

    /**
     * @todo fr && us input
     */
    private void registerCharacterInput() { //col input
        InputManager inputManager = app.getInputManager();
        // Character selection
        inputManager.addMapping("char_0", new KeyTrigger(KeyInput.KEY_F1));
        inputManager.addMapping("char_1", new KeyTrigger(KeyInput.KEY_F2));
        inputManager.addMapping("char_2", new KeyTrigger(KeyInput.KEY_F3));
        inputManager.addMapping("char_3", new KeyTrigger(KeyInput.KEY_F4));
        
        // Character movement
        switch (app.getStateManager().getState(MultiverseCoreState.class).getMapping()) {
            case col:
                inputManager.addMapping("move", new KeyTrigger(KeyInput.KEY_T));
                break;
            case fr:
                inputManager.addMapping("move", new KeyTrigger(KeyInput.KEY_F));
                break;
            default:
                // Use US input by default.
                inputManager.addMapping("move", new KeyTrigger(KeyInput.KEY_F));
        }
        // Character Normal Attack
        switch (app.getStateManager().getState(MultiverseCoreState.class).getMapping()) {
            case col:
                inputManager.addMapping("attack", new KeyTrigger(KeyInput.KEY_Q));
                break;
            case fr:
                inputManager.addMapping("attack", new KeyTrigger(KeyInput.KEY_A));
                break;
            default:
                // Use US input by default.
                inputManager.addMapping("attack", new KeyTrigger(KeyInput.KEY_Q));
        }
        
        // Character Normal Defence
        switch (app.getStateManager().getState(MultiverseCoreState.class).getMapping()) {
            case col:
                inputManager.addMapping("defends", new KeyTrigger(KeyInput.KEY_F));
                break;
            case fr:
                inputManager.addMapping("defends", new KeyTrigger(KeyInput.KEY_E));
                break;
            default:
                // Use US input by default.
                inputManager.addMapping("defends", new KeyTrigger(KeyInput.KEY_E));
        }
        
        // Character Skill
        inputManager.addMapping("skill_0", new KeyTrigger(KeyInput.KEY_1));
        inputManager.addMapping("skill_1", new KeyTrigger(KeyInput.KEY_2));
        inputManager.addMapping("skill_2", new KeyTrigger(KeyInput.KEY_3));
        inputManager.addMapping("skill_3", new KeyTrigger(KeyInput.KEY_4));
    }
}

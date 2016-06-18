/*
 * Copyright (C) 2015 roah
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
package org.multiverseking.core;

import com.jme3.app.state.AppStateManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import org.hexgridapi.core.camera.RTSCamera;
import org.hexgridapi.core.camera.RTSCamera.KeyMapping;
import org.multiverseking.core.utility.DependendAppState;
import org.multiverseking.debug.RenderDebugSystem;
import org.multiverseking.field.position.HexPositionSystem;
import org.multiverseking.loader.JSONLoader;
import org.multiverseking.render.RenderSystem;

/**
 *
 * @author roah
 */
public class MultiverseCoreState extends DependendAppState {

    private KeyMapping mapping;
    private Class<? extends MultiverseGameState> gameState;

    public MultiverseCoreState(KeyMapping mapping, Class<? extends MultiverseGameState> gameState) {
        super(EntityDataAppState.class, RenderSystem.class,
                HexPositionSystem.class, RenderDebugSystem.class, gameState);
        this.mapping = mapping;
        this.gameState = gameState;
    }

    public MultiverseCoreState(RTSCamera.KeyMapping mapping) {
        super(EntityDataAppState.class, RenderSystem.class,
                HexPositionSystem.class, RenderDebugSystem.class);
        this.mapping = mapping;
    }

    @Override
    public void initializeSystem(AppStateManager stateManager) {
        app.getAssetManager().registerLoader(JSONLoader.class, "json", "card");
        app.getInputManager().addMapping("Options", new KeyTrigger(KeyInput.KEY_ESCAPE));
    }

    public KeyMapping getMapping() {
        return mapping;
    }

    @Override
    public void cleanupSystem() {
    }
}

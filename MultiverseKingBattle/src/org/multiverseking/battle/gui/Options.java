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
package org.multiverseking.battle.gui;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector2f;
import org.multiverseking.battle.gui.utils.Layout;
import tonegod.gui.controls.buttons.ButtonAdapter;
import tonegod.gui.controls.windows.Panel;
import tonegod.gui.core.Screen;

/**
 *
 * @author roah
 */
public class Options extends AbstractAppState {

    private final BattleGUI battleGUI;
    private Screen screen;
    private Panel optionWin;

    public Options(Screen screen, BattleGUI battleGUI) {
        this.screen = screen;
        this.battleGUI = battleGUI;
        populateWindow();
    }

    private void populateWindow() {
        Layout.dim.set(100, 100);
        optionWin = new Panel(screen, Vector2f.ZERO);

        Layout.x = Layout.pad;
        Layout.y = Layout.pad;
        Layout.pos.set(Layout.x, Layout.y);
        Layout.dim.set(150, 25);
        ButtonAdapter helper = new ButtonAdapter(screen, Layout.pos, Layout.dim) {
            @Override
            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                battleGUI.enableHelper();
//                app.getStateManager().detach(option);
            }
        };
        helper.setText("Show Helper");
        optionWin.addChild(helper);

        Layout.y += helper.getHeight();
        Layout.pos.set(Layout.x, Layout.y);
        ButtonAdapter close = new ButtonAdapter(screen, Layout.pos, Layout.dim) {
            @Override
            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
            }
        };
        close.setText("Close");
        optionWin.addChild(close);

        optionWin.sizeToContent();

        optionWin.setIsResizable(false);
        optionWin.setIsMovable(false);
                battleGUI.enableHelper();
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        screen.addElement(optionWin);
        optionWin.centerToParent();
    }

    @Override
    public void update(float tpf) {
    }

    @Override
    public void cleanup() {
        super.cleanup();
        screen.removeElement(optionWin);
    }
}

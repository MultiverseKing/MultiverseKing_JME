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
package org.multiverseking.battle.core.gui;

import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector2f;
import java.util.ArrayList;
import java.util.Iterator;
import org.multiverseking.battle.core.gui.utils.Layout;
import org.multiverseking.core.MultiverseCoreState;
import tonegod.gui.controls.buttons.ButtonAdapter;
import tonegod.gui.controls.text.LabelElement;
import tonegod.gui.controls.windows.Panel;
import tonegod.gui.core.Element;
import tonegod.gui.core.Screen;

/**
 *
 * @author roah
 */
class HelperHUD {

    private final Screen screen;
    private Panel helperMain;
    private boolean isVisible;

    public HelperHUD(Screen screen) {
        this.screen = screen;
        initializePanel();
    }

    private void initializePanel() {

        ArrayList<String> text = getInputList();
        
        Layout.reset();
        Layout.dim.set(100, 100);
        helperMain = new Panel(screen, Vector2f.ZERO);
        Layout.x = Layout.pad;
        Layout.y = Layout.pad;
        Layout.pos.set(Layout.x, Layout.y);
        Layout.dim.set(290, 25);

        for (Iterator<String> it = text.iterator(); it.hasNext();) {
            String s = it.next();
            LabelElement label = new LabelElement(screen, Layout.pos, Layout.dim);
            label.setText(s);
            Layout.y += label.getHeight();
            Layout.pos.set(Layout.x, Layout.y);
            helperMain.addChild(label);
        }

        helperMain.sizeToContent();

        helperMain.setIsResizable(false);
        helperMain.setIsMovable(false);

        screen.addElement(helperMain);
    }
    
    public ArrayList<String> getInputList() {
        ArrayList<String> list = new ArrayList<>();
        
        switch (screen.getApplication().getStateManager().getState(MultiverseCoreState.class).getMapping()) {
            case col:
                list.add(" Camera movement : (W - A - R - S) ");
                break;
            case fr:
                list.add(" Camera movement : (Z - Q - S - D) ");
                break;
            default:
                // Use US input by default.
                list.add(" Camera movement : (W - A - S - D) ");
        }
        list.add(" Character selection A : (F1 - F2 - F3 - F4) ");
        list.add(" Character selection B : Double clic on it ");
        switch (screen.getApplication().getStateManager().getState(MultiverseCoreState.class).getMapping()) {
            case col:
                list.add(" Character movement : (T) + Left mouse ");
                break;
            case fr:
                list.add(" Character movement : (F) + Left mouse ");
                break;
            default:
                // Use US input by default.
                list.add(" Character movement : (F) + Left mouse ");
        }
        
        return list;
    }

    boolean isVisible() {
        return helperMain.getIsVisible();
    }

    void show(boolean b) {
        helperMain.setIsVisible(b);
    }
}

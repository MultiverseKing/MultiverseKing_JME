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

import com.jme3.input.controls.ActionListener;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import org.multiverseking.battle.core.BattleSystemTest;
import org.multiverseking.battle.core.focus.MainFocusComponent;
import org.multiverseking.battle.gui.options.HelperHUD;
import org.multiverseking.battle.gui.options.Options;
import org.multiverseking.core.utility.EntitySystemAppState;
import org.multiverseking.core.utility.MultiverseCoreGUI;
import tonegod.gui.core.Element;
import tonegod.gui.core.Screen;

/**
 * Root entry point for the GUI, store the option menu and Global Helper / tooltips.
 * 
 * @author roah
 */
public class BattleGUI extends EntitySystemAppState {

    private final String filePath = "Interface/Battle/HUD_V2/";
    private Element mainHolder;
    private Element portrait;
    private Screen screen;
    private Options optionMenu;
    private HelperHUD helperHUD;

    @Override
    protected EntitySet initialiseSystem() {
        this.screen = ((MultiverseCoreGUI) app).getScreen();
        Vector2f size = new Vector2f(906, 284).mult(.5f);
        mainHolder = new Element(screen, "statsHolder",
                new Vector2f(75, screen.getHeight() - 105),
                size, Vector4f.ZERO, null);
        mainHolder.setAsContainerOnly();

        initPortrait();

        screen.addElement(mainHolder);

        optionMenu = new Options(((MultiverseCoreGUI) app).getScreen(), this);
        app.getInputManager().addListener(keyListeners, "Options");
        
        return entityData.getEntities(MainFocusComponent.class);
    }

    private void initPortrait() {
        portrait = new Element(screen, "portrait", new Vector2f(-72, -48),
                new Vector2f(200, 200).mult(.75f), new Vector4f(),
                "Interface/Battle/HUDTest/Face_F.png");
//        screen.addElement(portrait);
        mainHolder.addChild(portrait);
        portrait.setZOrder(portrait.getZOrder() + screen.getZOrderStepMajor());
//        screen.updateZOrder(portrait);
    }

    private void setPortrait(String name, EntityId id) {
        EntityId[] list = app.getStateManager().getState(BattleSystemTest.class).getMainUnitsID();
        for (int i = 0; i < list.length; i++) {
            if (list[i].equals(id)) {
                name += i;
                break;
            }
        }
        portrait.setColorMap("Interface/Battle/HUDTest/" + name + ".png");
    }

//    private void initMainBlock() {
//        Indicator lifeStaminaWeapon = new Indicator(screen, Vector2f.ZERO,
//                new Vector2f(708, 139).mult(.75f), Element.Orientation.HORIZONTAL, true) {
//                    @Override
//                    public void onChange(float currentValue, float currentPercentage) {
//                    }
//                };
//        lifeStaminaWeapon.setOverlayImage(filePath + "lifeStaminaWeaponOverlay.png");
//        lifeStaminaWeapon.setBaseImage(filePath + "lifeStaminaWeapon.png");
//        lifeStaminaWeapon.setIndicatorColor(ColorRGBA.Red);
//        lifeStaminaWeapon.setAlphaMap(filePath + "lifeGaugeAlpha.png");
//        lifeStaminaWeapon.setMaxValue(100);
//        gauges_d.add(lifeStaminaWeapon);
//        mainHolder.addChild(lifeStaminaWeapon);
//
//        String[] val = new String[]{
//            "stamina.Red.HORIZONTAL."};
//        Vector2f size = new Vector2f(708, 139).mult(.75f);
//        for (int i = val.length - 1; i >= 0; i--) {
//            gauges_d.add(ATBGauge.initIndicator(screen, val[i] + filePath, Vector2f.ZERO, size, false));
//            mainHolder.addChild(gauges_d.get(gauges_d.size() - 1));
//        }
//    }
    @Override
    protected void updateSystem(float tpf) {
    }

    @Override
    protected void addEntity(Entity e) {
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

    public Element getHolder() {
        return mainHolder;
    }

    public String getFilePath() {
        return filePath;
    }
}

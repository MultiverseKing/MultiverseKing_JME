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
package org.multiverseking.battle.core.movement;

import org.multiverseking.battle.gui.ATBGauge;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import tonegod.gui.controls.extras.Indicator;
import tonegod.gui.core.Element;
import tonegod.gui.core.Screen;

/**
 *
 * @author roah
 */
public class StaminaGauge extends ATBGauge {

    public StaminaGauge(Screen screen, String filePath, Vector2f position) {

        gauge = new Indicator(screen, position, new Vector2f(300, 15),
                new Vector4f(5, 50, 50, 5), filePath + "skill.png", Element.Orientation.HORIZONTAL) {
                    @Override
                    public void onChange(float currentValue, float currentPercentage) {
                    }
                };
        ((Indicator)gauge).setIndicatorColor(ATBGauge.getColor("Blue"));
        ((Indicator)gauge).setAlphaMap(filePath + "skillAlpha.png");

        ((Indicator)gauge).setOverlayImage(filePath + "skillOverlay.png");
        ((Indicator)gauge).setBaseImage(filePath + "skill.png");

        ((Indicator)gauge).setMaxValue(100);
    }

    @Override
    public Indicator getGauge() {
        return (Indicator)gauge;
    }

    public void show(boolean show) {
        if (show) {
            gauge.show();
        } else {
            gauge.hide();
        }
    }
    
    /**
     *
     * @param value
     */
    public void update(float value) {
        if (value > 0 && value < 100) {
            show(true);
            getGauge().setCurrentValue(value);
        } else {
            show(false);
        }
    }
}

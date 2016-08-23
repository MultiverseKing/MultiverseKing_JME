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

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import org.slf4j.LoggerFactory;
import tonegod.gui.controls.extras.Indicator;
import tonegod.gui.core.Element;
import tonegod.gui.core.Screen;

/**
 *
 * @author roah
 */
public abstract class ATBGauge {
    
    protected Element gauge;
    
    public abstract Element getGauge();
    
    /**
     * The {@param param} value work as : name.color.fillingdirection.filepath.
     * for color {@link getColor(String string)}
     * 
     * @param screen Tonegod screen used.
     * @param param Parameter used to generate the jauge.
     * @param pos Position on the screen.
     * @param size Size of the jauge.
     * @param overlay Alpha image to put over the jauge.
     * @return A newly generated jauge on the desired position.
     */
    protected static Indicator initIndicator(Screen screen, String param, Vector2f pos, Vector2f size, boolean overlay) {
        String[] params = param.split("\\.");
        Indicator ind = new Indicator(screen, pos, size,
                Element.Orientation.valueOf(params[2]), overlay) {
                    @Override
                    public void onChange(float currentValue, float currentPercentage) {
                    }
                };
        ind.setIndicatorColor(getColor(params[1]));
        ind.setAlphaMap(params[3] + params[0] + "Alpha.png");
        ind.setMaxValue(100);
        return ind;
    }
    
    /**
     * Handled color :
     * <li>Blue</li>
     * <li>Black</li>
     * <li>Red</li>
     * <li>White</li>
     * <li>Orange</li>
     * <li>Cyan</li>
     * 
     * @param string Needed Color
     * @return Needed color value
     */
    protected static ColorRGBA getColor(String string) {
        switch (string) {
            case "Blue":
                return ColorRGBA.Blue;
            case "Black":
                return ColorRGBA.Black;
            case "Red":
                return ColorRGBA.Red;
            case "White":
                return ColorRGBA.White;
            case "Orange":
                return ColorRGBA.Orange;
            case "Cyan":
                return ColorRGBA.Cyan;
            default:
                try {
                    throw new NoSuchFieldException(string + " isn't a defined color.");
                } catch (NoSuchFieldException ex) {
                    LoggerFactory.getLogger(ATBGauge.class).error("The requested color could not be found : {}", string);
                } 
            return null;
        }
    }
}

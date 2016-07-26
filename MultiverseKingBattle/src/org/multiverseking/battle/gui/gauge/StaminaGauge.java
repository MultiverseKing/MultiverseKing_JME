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
package org.multiverseking.battle.gui.gauge;

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
    private Indicator holder;

    public StaminaGauge(Screen screen, String filePath, Vector2f position) {
        super(screen, filePath);
        
        holder = new Indicator(screen, position, new Vector2f(300, 15), 
                new Vector4f(5,50,50,5), filePath+"skill.png", Element.Orientation.HORIZONTAL) {
                    @Override
                    public void onChange(float currentValue, float currentPercentage) {
                    }
                };
        holder.setIndicatorColor(ATBGauge.getColor("Blue"));
        holder.setAlphaMap(filePath+"skillAlpha.png");
        
        holder.setOverlayImage(filePath + "skillOverlay.png");
        holder.setBaseImage(filePath + "skill.png");
        
        holder.setMaxValue(100);
//        screen.addElement(holder);
    }
    
    private final float speed = 15;     // Rate speed for the segment to fill up
    private float timer = 0;
    @Override
    public void update(float tpf) {
        if(holder.getIsVisible()) {
            System.err.println("run");
            timer += tpf * speed;
            if (timer > 100) {
                timer = 0;
            }
            holder.setCurrentValue(timer);
        }
    }

    @Override
    public Indicator getGauge() {
        return holder;
    }

    public void show(boolean show) {
        if(show){
            holder.show();
        } else {
            holder.hide();
        }
    }
    
}

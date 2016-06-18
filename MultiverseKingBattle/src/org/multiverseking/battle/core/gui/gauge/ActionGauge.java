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
package org.multiverseking.battle.core.gui.gauge;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import java.util.ArrayList;
import org.multiverseking.battle.core.gui.CharacterHUD;
import tonegod.gui.controls.extras.Indicator;
import tonegod.gui.core.Element;
import tonegod.gui.core.Screen;

/**
 *
 * @author roah
 */
public class ActionGauge implements AtbGauge {

    private final CharacterHUD characterHUD_d;
    private final Screen screen;
    private final String filePath;
    private final ArrayList<Indicator> segmentsList = new ArrayList<>();
    private int gaugeSize;
    private Element holder;

    public ActionGauge(CharacterHUD characterHUD_d, Screen screen, String filePath, int gaugeSize) {
        this.characterHUD_d = characterHUD_d;
        this.screen = screen;
        this.filePath = filePath;
        this.gaugeSize = gaugeSize;
        initAtb();
    }

    private void initAtb() {
        holder = new Element(screen, "atbGroup",
                new Vector2f(20, -35), new Vector2f(500, 500).mult(.75f), Vector4f.ZERO, null);
        holder.setAsContainerOnly();

        Element atbHolderA = new Element(screen, "atbHolderA", new Vector2f(12, 0),
                new Vector2f(131, 49).mult(.75f), new Vector4f(),
                filePath + "ATBHolderA_bis.png");
        Element atbHolderB = new Element(screen, "atbHolderB", new Vector2f(70, 27),
                new Vector2f(187, 13).mult(.75f), new Vector4f(),
                filePath + "ATBHolderB.png");
        Element atbHolderC = new Element(screen, "atbHolderC", new Vector2f(200, 27),
                new Vector2f(36, 13).mult(.75f), new Vector4f(),
                filePath + "ATBHolderC.png");
        holder.addChild(atbHolderA);
        holder.addChild(atbHolderB);
        holder.addChild(atbHolderC);

        for (int i = 0; i < gaugeSize; i++) {
            Indicator ind = characterHUD_d.initIndicator("atbSegment.Blue.HORIZONTAL",
                    new Vector2f(58 + ((100 + 10) * i), 10), new Vector2f(110, 15), true);
            ind.setOverlayImage(filePath + "atbSegmentOverlay.png");
            ind.setBaseImage(filePath + "atbSegment.png");
            segmentsList.add(ind);
            holder.addChild(ind);
        }
    }

    private final float speed = 15;     // Rate speed for the segment to fill up
    private float timer = 0;
    private int currentSegment = 0;     // Segment currently operated

    /**
     * @todo add events for when the gauge is fill to restart it
     * @param tpf 
     */
    @Override
    public void update(float tpf) {
        if (currentSegment != -1) { // if the gauge is not fill
            timer += tpf * 15;
            if (timer <= 100) {
                segmentsList.get(currentSegment).setCurrentValue((int) timer);
            } else if (timer > 100) {
                timer = 0;
                currentSegment++;
                if (currentSegment >= gaugeSize) {
                    currentSegment = -1;
                }
            }
        }
    }

    @Override
    public Element getGauge() {
        return holder;
    }
}

package org.multiverseking.battle.battleSystem;

import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import java.util.ArrayList;
import tonegod.gui.controls.extras.Indicator;
import tonegod.gui.core.Element;
import tonegod.gui.core.Screen;

/**
 *
 * @author roah
 */
public class CharacterHUD {

    private final Screen screen;
    private final String filePath = "org/multiverseking/assets/Interface/Battle/HUD_V1/";
    private final Element statsHolder;
    private final Element portrait;
    private final Element atb;
    private final Element burstHolder;
    private final Element skillHolder;
    private final Indicator staminaProgress;
    private final Indicator lifeGauge;
    private final ArrayList<Element> burstSegments = new ArrayList<>();
    private final ArrayList<Element> skillBtnList = new ArrayList<>();
    private float timer = 6; // max == 90
    private int burstCount = 8; // 8 max

    public CharacterHUD(Screen screen) {
        this(screen, null, 0);
    }

    public CharacterHUD(Screen screen, Vector2f position, float scale) {
        this.screen = screen;

        Vector2f size = new Vector2f(906, 284).mult(.5f);
        statsHolder = new Element(screen, "statsHolder", new Vector2f(10, screen.getHeight() - 160), size, Vector4f.ZERO, null);

        burstHolder = new Element(screen, "burstHolder", new Vector2f((burstCount * 13.75f) - 120, 0),
                new Vector2f(906, 284).mult(.5f), Vector4f.ZERO, filePath + "burstHolder.png");
        statsHolder.addChild(burstHolder);
        for (int i = 0; i < burstCount; i++) {
            burstSegments.add(new Element(screen, "burstSegment_" + i, new Vector2f(140 + (18 * i), 40),
                    new Vector2f(33, 47).mult(.5f), Vector4f.ZERO, filePath + "burstSegment.png"));
            statsHolder.addChild(burstSegments.get(i));
        }

        skillHolder = new Element(screen, "skillHolder", new Vector2f(),
                new Vector2f(906, 284).mult(.5f), Vector4f.ZERO, filePath + "skillHolder.png");
        statsHolder.addChild(skillHolder);
        for (int i = 0; i < 8; i++) {
            skillBtnList.add(new Element(screen, "skillBtn_" + i, new Vector2f(124 + (23 * i), 99),
                    new Vector2f(54, 35).mult(.5f), Vector4f.ZERO, filePath + "noSkillBtn.png"));
            statsHolder.addChild(skillBtnList.get(i));
        }

        staminaProgress = new Indicator(screen, new Vector2f(), size, Element.Orientation.VERTICAL, false) {
            @Override
            public void onChange(float currentValue, float currentPercentage) {
            }
        };
        staminaProgress.setBaseImage(filePath + "staminaHolder.png");
        staminaProgress.setIndicatorColor(ColorRGBA.Cyan);
        staminaProgress.setAlphaMap(filePath + "alphaStamina.png");
        staminaProgress.setMaxValue(100);
        statsHolder.addChild(staminaProgress);

        portrait = new Element(screen, "portraitBackGround", new Vector2f(),
                size, Vector4f.ZERO, filePath + "portraitTest.png");
        statsHolder.addChild(portrait);

        lifeGauge = new Indicator(screen, new Vector2f(), size, Element.Orientation.HORIZONTAL, false) {
            @Override
            public void onChange(float currentValue, float currentPercentage) {
            }
        };
        lifeGauge.setBaseImage(filePath + "lifeGauge.png");
        lifeGauge.setIndicatorColor(ColorRGBA.Red);
        lifeGauge.setAlphaMap(filePath + "alphaLifeGauge.png");
        lifeGauge.setMaxValue(100);
        statsHolder.addChild(lifeGauge);

        atb = new Element(screen, "atbBackGround", new Vector2f(),
                size, Vector4f.ZERO, filePath + "atb_4.png");
        statsHolder.addChild(atb);

        if (scale != 0) {
            statsHolder.scale(scale);
        }
        if (position != null) {
            statsHolder.setPosition(position);
        }
    }

    public Element getHUDElement() {
        return statsHolder;
    }

    void update(float tpf) {
        timer += tpf * 5;
        if (timer < 90) {
            staminaProgress.setCurrentValue((int) timer);
            lifeGauge.setCurrentValue((int) timer);
        } else if (timer > 110) {
            timer = 10;
        }
    }
}
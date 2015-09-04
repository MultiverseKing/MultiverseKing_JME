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
public class CharacterHUD1 {

    private final Screen screen;
    private final String filePath = "org/multiverseking/assets/Interface/Battle/HUD_V2/";
    private final Element statsHolder;
    private final ArrayList<Indicator> gauges = new ArrayList<>();
//    private final Indicator lifeStaminaWeapon;
//    private final ArrayList<Element> burstSegments = new ArrayList<>();
//    private final ArrayList<Element> skillBtnList = new ArrayList<>();
    private float timer = 6; // max == 90
    private int burstCount = 8; // 8 max
//    private final Indicator stamina;
//    private final Indicator mainWeapon;
//    private final Indicator secondaryWeapon;

    public CharacterHUD1(Screen screen) {
        this(screen, null, 0);
    }

    public CharacterHUD1(Screen screen, Vector2f position, float scale) {
        this.screen = screen;

        Vector2f size = new Vector2f(906, 284).mult(.5f);
        statsHolder = new Element(screen, "statsHolder", new Vector2f(10, screen.getHeight() - 160), size, Vector4f.ZERO, null);

        Indicator lifeStaminaWeapon = new Indicator(screen, Vector2f.ZERO, new Vector2f(708, 139).mult(.75f), Element.Orientation.HORIZONTAL, true) {
            @Override
            public void onChange(float currentValue, float currentPercentage) {
            }
        };
        lifeStaminaWeapon.setOverlayImage(filePath + "lifeStaminaWeaponOverlay.png");
        lifeStaminaWeapon.setBaseImage(filePath + "lifeStaminaWeapon.png");
        lifeStaminaWeapon.setIndicatorColor(ColorRGBA.Red);
        lifeStaminaWeapon.setAlphaMap(filePath + "lifeGaugeAlpha.png");
        lifeStaminaWeapon.setMaxValue(100);
        gauges.add(lifeStaminaWeapon);
        statsHolder.addChild(lifeStaminaWeapon);

        String[] val = new String[]{
            "stamina.Blue.HORIZONTAL",
            "mainWeapon.Blue.VERTICAL",
            "secondaryWeapon.Blue.VERTICAL"};
        for (int i = val.length - 1; i >= 0; i--) {
            gauges.add(initIndicator(val[i]));
            statsHolder.addChild(gauges.get(gauges.size() - 1));
        }

//        lifeStaminaWeapon = new Indicator(screen, Vector2f.ZERO, new Vector2f(708, 139).mult(.75f), Element.Orientation.HORIZONTAL, true) {
//            @Override
//            public void onChange(float currentValue, float currentPercentage) {
//            }
//        };
//        lifeStaminaWeapon.setOverlayImage(filePath + "lifeStaminaWeaponOverlay.png");
//        lifeStaminaWeapon.setBaseImage(filePath + "lifeStaminaWeapon.png");
//        lifeStaminaWeapon.setIndicatorColor(ColorRGBA.Red);
//        lifeStaminaWeapon.setAlphaMap(filePath + "lifeGaugeAlpha.png");
//        lifeStaminaWeapon.setMaxValue(100);
//        statsHolder.addChild(lifeStaminaWeapon);
//
//        stamina = new Indicator(screen, Vector2f.ZERO, new Vector2f(708, 139).mult(.75f), Element.Orientation.HORIZONTAL, false) {
//            @Override
//            public void onChange(float currentValue, float currentPercentage) {
//            }
//        };
//        stamina.setIndicatorColor(ColorRGBA.Blue);
//        stamina.setAlphaMap(filePath + "staminaAlpha.png");
//        stamina.setMaxValue(100);
//        statsHolder.addChild(stamina);
//
//        mainWeapon = new Indicator(screen, Vector2f.ZERO, new Vector2f(708, 139).mult(.75f), Element.Orientation.VERTICAL, false) {
//            @Override
//            public void onChange(float currentValue, float currentPercentage) {
//            }
//        };
//        mainWeapon.setIndicatorColor(ColorRGBA.Blue);
//        mainWeapon.setAlphaMap(filePath + "mainWeaponAlpha.png");
//        mainWeapon.setMaxValue(100);
//        statsHolder.addChild(mainWeapon);
//
//        secondaryWeapon = new Indicator(screen, Vector2f.ZERO, new Vector2f(708, 139).mult(.75f), Element.Orientation.VERTICAL, false) {
//            @Override
//            public void onChange(float currentValue, float currentPercentage) {
//            }
//        };
//        secondaryWeapon.setIndicatorColor(ColorRGBA.Blue);
//        secondaryWeapon.setAlphaMap(filePath + "secondaryWeaponAlpha.png");
//        secondaryWeapon.setMaxValue(100);
//        statsHolder.addChild(secondaryWeapon);
    }

    private Indicator initIndicator(String param) {
        String[] params = param.split("\\.");
        Indicator ind = new Indicator(screen, Vector2f.ZERO, new Vector2f(708, 139).mult(.75f), Element.Orientation.valueOf(params[2]), false) {
            @Override
            public void onChange(float currentValue, float currentPercentage) {
            }
        };
        ind.setIndicatorColor(getColor(params[1]));
        ind.setAlphaMap(filePath + params[0] + "Alpha.png");
        ind.setMaxValue(100);
        return ind;
    }

    private ColorRGBA getColor(String string) {
        switch(string) {
            default:
                return null;
        }
    }

    public Element getHUDElement() {
        return statsHolder;
    }

    void update(float tpf) {
        timer += tpf * 5;
        if (timer < 90) {
            for (int i = gauges.size() - 1; i >= 0; i--) {
                gauges.get(i).setCurrentValue((int) timer);
            }
//            lifeStaminaWeapon.setCurrentValue((int) timer);
//            stamina.setCurrentValue((int) timer);
//            mainWeapon.setCurrentValue((int) timer);
//            secondaryWeapon.setCurrentValue((int) timer);
        } else if (timer > 110) {
            timer = 10;
        }
    }
}
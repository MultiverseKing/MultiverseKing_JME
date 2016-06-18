package org.multiverseking.battle.battleSystem.gui;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.multiverseking.battle.battleSystem.gui.atbGauge.ActionGauge;
import org.multiverseking.battle.battleSystem.gui.atbGauge.AtbGauge;
import tonegod.gui.controls.extras.Indicator;
import tonegod.gui.core.Element;
import tonegod.gui.core.Screen;

/**
 *
 * @author roah
 */
public class CharacterHUD {

    private final Screen screen;
    private final String filePath = "org/multiverseking/assets/Interface/Battle/HUD_V2/";
    private final Element mainHolder;
    private final ArrayList<AtbGauge> gauges = new ArrayList<>();
    private final ArrayList<Indicator> gauges_d = new ArrayList<>();
    private final ArrayList<Indicator> atbSegmentList = new ArrayList<>();
    private Element portrait;
    private float timer = 6; // max == 90
    private int burstCount = 8; // 8 max
    private int atbCount = 6; // 8 max

    public CharacterHUD(Screen screen) {
        this(screen, null, 0);
    }

    public CharacterHUD(Screen screen, Vector2f position, float scale) {
        this.screen = screen;

        Vector2f size = new Vector2f(906, 284).mult(.5f);
        mainHolder = new Element(screen, "statsHolder",
                new Vector2f(75, screen.getHeight() - 105),
                size, Vector4f.ZERO, null);
        mainHolder.setAsContainerOnly();

        initMainBlock();
//        initAtb();
        gauges.add(new ActionGauge(this, screen, filePath, 6));
        gauges.stream().forEach((g) -> {
            mainHolder.addChild(g.getGauge());
        });

        initPortrait();

        screen.addElement(mainHolder);
    }

    private void initMainBlock() {
        Indicator lifeStaminaWeapon = new Indicator(screen, Vector2f.ZERO,
                new Vector2f(708, 139).mult(.75f), Element.Orientation.HORIZONTAL, true) {
                    @Override
                    public void onChange(float currentValue, float currentPercentage) {
                    }
                };
        lifeStaminaWeapon.setOverlayImage(filePath + "lifeStaminaWeaponOverlay.png");
        lifeStaminaWeapon.setBaseImage(filePath + "lifeStaminaWeapon.png");
        lifeStaminaWeapon.setIndicatorColor(ColorRGBA.Red);
        lifeStaminaWeapon.setAlphaMap(filePath + "lifeGaugeAlpha.png");
        lifeStaminaWeapon.setMaxValue(100);
        gauges_d.add(lifeStaminaWeapon);
        mainHolder.addChild(lifeStaminaWeapon);

        String[] val = new String[]{
            "stamina.Blue.HORIZONTAL",
            "mainWeapon.Blue.VERTICAL",
            "secondaryWeapon.Blue.VERTICAL"};
        Vector2f size = new Vector2f(708, 139).mult(.75f);
        for (int i = val.length - 1; i >= 0; i--) {
            gauges_d.add(initIndicator(val[i], null, size, false));
            mainHolder.addChild(gauges_d.get(gauges_d.size() - 1));
        }
    }

    // Split
    private void initAtb() {
        Element atbGroup = new Element(screen, "atbGroup",
                new Vector2f(20, -35), new Vector2f(500, 500).mult(.75f), Vector4f.ZERO, null);
        atbGroup.setAsContainerOnly();

        Element atbHolderA = new Element(screen, "atbHolderA", new Vector2f(12, 0),
                new Vector2f(131, 49).mult(.75f), new Vector4f(),
                filePath + "ATBHolderA_bis.png");
        Element atbHolderB = new Element(screen, "atbHolderB", new Vector2f(70, 27),
                new Vector2f(187, 13).mult(.75f), new Vector4f(),
                filePath + "ATBHolderB.png");
        Element atbHolderC = new Element(screen, "atbHolderC", new Vector2f(200, 27),
                new Vector2f(36, 13).mult(.75f), new Vector4f(),
                filePath + "ATBHolderC.png");
        atbGroup.addChild(atbHolderA);
        atbGroup.addChild(atbHolderB);
        atbGroup.addChild(atbHolderC);

        for (int i = 0; i < atbCount; i++) {
            Indicator ind = initIndicator("atbSegment.Blue.HORIZONTAL",
                    new Vector2f(58 + ((100 + 10) * i), 10), new Vector2f(110, 15), true);
            ind.setOverlayImage(filePath + "atbSegmentOverlay.png");
            ind.setBaseImage(filePath + "atbSegment.png");
            atbSegmentList.add(ind);
            atbGroup.addChild(ind);
        }
        mainHolder.addChild(atbGroup);
    }

    private void initPortrait() {
        portrait = new Element(screen, "portrait", new Vector2f(-72, -48),
                new Vector2f(200, 200).mult(.75f), new Vector4f(),
                filePath + "portrait.png");
//        screen.addElement(portrait);
        mainHolder.addChild(portrait);
        portrait.setZOrder(portrait.getZOrder() + screen.getZOrderStepMajor());
//        screen.updateZOrder(portrait);
    }

    public Indicator initIndicator(String param, Vector2f pos, Vector2f size, boolean overlay) {
        String[] params = param.split("\\.");
        Indicator ind = new Indicator(screen, pos != null ? pos : Vector2f.ZERO, size,
                Element.Orientation.valueOf(params[2]), overlay) {
                    @Override
                    public void onChange(float currentValue, float currentPercentage) {
                    }
                };
        ind.setIndicatorColor(getColor(params[1]));
        ind.setAlphaMap(filePath + params[0] + "Alpha.png");
        ind.setMaxValue(100);
        return ind;
    }

    public ColorRGBA getColor(String string) {
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
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                }
                return null;
        }
    }

    float atbTimer = 0;

    void update(float tpf) {
        gauges.stream().forEach((g) -> {
            g.update(tpf);
        });
        timer += tpf * 5;
        atbTimer += tpf * 15;
        if (timer < 90) {
            for (int i = gauges_d.size() - 1; i >= 0; i--) {
                gauges_d.get(i).setCurrentValue((int) timer);
            }
        } else if (timer > 110) {
            timer = 10;
        }
    }

    public Element getHolder() {
        return mainHolder;
    }
}

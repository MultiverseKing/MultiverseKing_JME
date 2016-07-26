package org.multiverseking.battle.gui;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import java.util.ArrayList;
import org.multiverseking.battle.core.BattleSystemTest;
import org.multiverseking.battle.core.movement.StaminaComponent;
import org.multiverseking.battle.core.focus.MainFocusComponent;
import org.multiverseking.battle.gui.gauge.ATBGauge;
import org.multiverseking.battle.gui.gauge.StaminaGauge;
import org.multiverseking.core.utility.EntitySystemAppState;
import org.multiverseking.core.utility.MultiverseCoreGUI;
import org.multiverseking.render.RenderComponent;
import tonegod.gui.controls.extras.Indicator;
import tonegod.gui.core.Element;
import tonegod.gui.core.Screen;

/**
 *
 * @author roah
 */
public class CharacterHUD extends EntitySystemAppState {

    private Screen screen;
    private final String filePath = "Interface/Battle/HUD_V2/";
    private final ArrayList<ATBGauge> gauges = new ArrayList<>();
    private final ArrayList<Indicator> gauges_d = new ArrayList<>();
    private final ArrayList<Indicator> atbSegmentList = new ArrayList<>();
    private Element mainHolder;
    private Element portrait;
    private float timer = 6; // max == 90
    private int burstCount = 8; // 8 max
    private int atbCount = 6; // 8 max
    private StaminaGauge staminaMove;
    private boolean showStaminaMove = false;

    @Override
    protected EntitySet initialiseSystem() {
        this.screen = ((MultiverseCoreGUI) app).getScreen();
        Vector2f size = new Vector2f(906, 284).mult(.5f);
        mainHolder = new Element(screen, "statsHolder",
                new Vector2f(75, screen.getHeight() - 105),
                size, Vector4f.ZERO, null);
        mainHolder.setAsContainerOnly();
        
//        initMainBlock();
//        gauges.add(new ActionGauge(this, screen, filePath, 6));
//        gauges.stream().forEach((g) -> {
//            mainHolder.addChild(g.getGauge());
//        });

        initPortrait();
        
        staminaMove = new StaminaGauge(screen, filePath, new Vector2f(-75, 90));
        mainHolder.addChild(staminaMove.getGauge());
        staminaMove.show(false);

        screen.addElement(mainHolder);
        
        return entityData.getEntities(MainFocusComponent.class, StaminaComponent.class, RenderComponent.class);
    }
    
    @Override
    protected void updateSystem(float tpf) {
    }

    @Override
    protected void addEntity(Entity e) {
        setPortrait(e.get(RenderComponent.class).getName(), e.getId());
        updateStaminaJauge(e.get(StaminaComponent.class).getValue());
//        float stamina = e.get(StaminaComponent.class).getValue();
//        if (stamina > 0) {
//            staminaMove.show(true);
//            staminaMove.getGauge().setCurrentValue(stamina);
//        }
    }

    @Override
    protected void updateEntity(Entity e) {
        updateStaminaJauge(e.get(StaminaComponent.class).getValue());
//        float stamina = e.get(StaminaComponent.class).getValue();
//        if (stamina > 0) {
//            staminaMove.show(true);
//            staminaMove.getGauge().setCurrentValue(stamina);
//        } if (stamina == 0 || stamina >= 100) {
//            staminaMove.show(false);
//        }
    }
    protected void updateStaminaJauge(float staminaValue) {
        if (staminaValue > 0 && staminaValue < 100) {
            staminaMove.show(true);
            staminaMove.getGauge().setCurrentValue(staminaValue);
        } else {
            staminaMove.show(false);
        }
        
    }

    @Override
    protected void removeEntity(Entity e) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void cleanupSystem() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
            "stamina.Red.HORIZONTAL."};
        Vector2f size = new Vector2f(708, 139).mult(.75f);
        for (int i = val.length - 1; i >= 0; i--) {
            gauges_d.add(ATBGauge.initIndicator(screen, val[i]+filePath, Vector2f.ZERO, size, false));
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
            Indicator ind = ATBGauge.initIndicator(screen, "atbSegment.Blue.HORIZONTAL."+filePath,
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
                "Interface/Battle/HUDTest/Face_F.png");
//        screen.addElement(portrait);
        mainHolder.addChild(portrait);
        portrait.setZOrder(portrait.getZOrder() + screen.getZOrderStepMajor());
//        screen.updateZOrder(portrait);
    }

    private void setPortrait(String name, EntityId id) {
        EntityId[] list = app.getStateManager().getState(BattleSystemTest.class).getMainUnitsID();
        for(int i = 0; i < list.length; i++) {
            if(list[i].equals(id)){
                name += i;
                break;
            }
        }
        portrait.setColorMap("Interface/Battle/HUDTest/"+name+".png");
    }

    public Element getHolder() {
        return mainHolder;
    }
}

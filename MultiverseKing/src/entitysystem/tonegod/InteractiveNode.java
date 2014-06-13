package entitysystem.tonegod;

import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.material.Material;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import entitysystem.field.FieldGUIComponent;
import static entitysystem.field.FieldGUIComponent.EntityType.ENVIRONMENT;
import static entitysystem.field.FieldGUIComponent.EntityType.TITAN;
import static entitysystem.field.FieldGUIComponent.EntityType.UNIT;
import tonegod.gui.core.Element;
import tonegod.gui.core.ElementManager;
import tonegod.gui.core.Screen;
import tonegod.gui.effects.Effect;
import tonegod.gui.framework.animation.Interpolation;
import tonegod.gui.framework.core.util.GameTimer;
import tonegod.gui.listeners.MouseButtonListener;
import tonegod.gui.listeners.MouseFocusListener;
import tonegod.gui.style.StyleManager;

/**
*
* @author t0neg0d, roah
*/
public abstract class InteractiveNode extends Node implements MouseFocusListener, MouseButtonListener {
    private final ToneControl toneControl;
    protected ElementManager screen;
    private GameTimer timer;
    private Effect fx;
    private boolean xDir = true;
    private boolean zDir = false;
    private float xAmount = 0.5f;
    private float zAmount = 0.5f;
    private Material defMat, hlMat;
    private String toolTipText;
    private String icon;
    private boolean isInScene = true;
    protected Element element;
 
    public InteractiveNode(ElementManager screen, ToneControl toneControl, Element element) {
        this.screen = screen;
        this.element = element;
        this.toneControl = toneControl;
        initTimer();
    }
 
    public Element getElement(){
        return element;
    }
    public void setElement(Element element){
        this.element = element;
    }
    
    public Screen getScreen(){
        return (Screen) screen;
    }
    
    private void initTimer() {
        timer = new GameTimer(0.5f) {
            @Override
            public void onComplete(float time) {
                xDir = !xDir;
                zDir = !zDir;
            }
            @Override
            public void timerUpdateHook(float tpf) {
                scaleSpatial(this.getPercentComplete());
            }
        };
        timer.setAutoRestart(true);
        timer.setInterpolation(Interpolation.bounceOut);
    }
 
    public void scaleSpatial(float percent) {
        float percentX = (xDir) ? percent : 1f-percent;
        float percentZ = (zDir) ? percent : 1f-percent;
        if (timer.getRunCount() == 0)
            setLocalScale(1+(xAmount*percentX),1,1);
        else
            setLocalScale(1+(xAmount*percentX),1+(zAmount*percentZ),1);
    }
 
    public void setDefaultMaterial(Material mat) {
        this.defMat = mat;
        setMaterial(defMat);
    }
    
    public void setHighlightMaterial(Material mat) {
        this.hlMat = mat;
    }
 
    public Material getHighlightMaterial() {
        return hlMat;
    }
    
    public void setToolTipText(String text) {
        this.toolTipText = text;
    }
 
    public String getToolTipText() { return this.toolTipText; }
 
    public void setIcon(String icon) {
        this.icon = icon;
    }
 
    public String getIcon() { return this.icon; }
 
    public void setIsInScene(boolean isInScene) {
        this.isInScene = isInScene;
    }
 
    public boolean getIsInScene() { return isInScene; }
 
    public void onGetFocus(MouseMotionEvent evt) {
//        if (!screen.getAnimManager().hasGameTimer(timer)) {
//            timer.resetRunCount();
//            timer.reset(false);
//            timer.setAutoRestart(true);
//            xDir = true;
//            zDir = false;
//            screen.getAnimManager().addGameTimer(timer);
//        }
//        setMaterial(hlMat);
        ((Screen)screen).setForcedCursor(StyleManager.CursorType.HAND);
        if (screen.getUseToolTips() && toolTipText != null)
            ((Screen)screen).setForcedToolTip(toolTipText);
    }
    public void onLoseFocus(MouseMotionEvent evt) {
        timer.setAutoRestart(false);
        timer.endGameTimer();
        setLocalScale(1);
        setMaterial(defMat);
        ((Screen)screen).releaseForcedCursor();
        ((Screen)screen).releaseForcedToolTip();
 
    }
    public final void onMouseLeftPressed(MouseButtonEvent evt) {
        onSpatialLeftMouseDown(evt);
    }
    public final void onMouseLeftReleased(MouseButtonEvent evt) {
        onSpatialLeftMouseUp(evt);
    }
    public final void onMouseRightPressed(MouseButtonEvent evt) {
        onSpatialRightMouseDown(evt);
    }
    public final void onMouseRightReleased(MouseButtonEvent evt) {
        onSpatialRightMouseUp(evt);
    }
 
    public abstract void onSpatialRightMouseDown(MouseButtonEvent evt);
    public abstract void onSpatialRightMouseUp(MouseButtonEvent evt);
    public abstract void onSpatialLeftMouseDown(MouseButtonEvent evt);
    public abstract void onSpatialLeftMouseUp(MouseButtonEvent evt);

}
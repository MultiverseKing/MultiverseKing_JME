package entitysystem.field.gui;

import com.jme3.math.Vector2f;
import tonegod.gui.controls.text.Label;
import tonegod.gui.controls.windows.Window;
import tonegod.gui.core.ElementManager;
import utility.Vector2Int;

/**
 *
 * @author roah
 */
public class PropertiesWindow extends Window {
    protected final GUIRenderSystem system;
    protected String windowTitle = "Properties : ";
    private int fieldCount = 0;
    
    public PropertiesWindow(ElementManager screen, String UID, GUIRenderSystem system) {
        super(screen, UID + "PropertiesWindow", new Vector2f(screen.getWidth()-285, 10), new Vector2f(275, 325));
        setIgnoreMouse(true);
        setUseCollapseButton(true);
        setUseCloseButton(true);
        getDragBar().setIgnoreMouse(true);
        setWindowTitle(windowTitle);
        this.system = system;
    }
    
    /**
     * value.x  == min, value.y == max.
     * @param labelName
     * @param value 
     */
    protected final void addMinMaxField(String labelName, Vector2Int value){
        addMinMaxField(labelName, value.x, value.y);
    }
    
    protected final void addMinMaxField(String labelName, int minValue, int maxValue){
        Label label = generateLabel(labelName);
        label.setText(label.getText() + minValue + " / " +maxValue);
        addChild(label);
    }
    
    protected final void addField(String labelName, float value){
        Label label = generateLabel(labelName);
        label.setText(label.getText()+" "+value);
        addChild(label);
    }
    
    private Label generateLabel(String labelName){
        Label label = new Label(screen, new Vector2f(10, fieldCount * 40 + 10), new Vector2f(30, labelName.length()*20));
        label.setText(labelName+" : ");
        fieldCount++;
        return label;
    }

    @Override
    public void hideWindow() {
        super.hideWindow();
        system.closeEntityPropertiesMenu();
    }
}

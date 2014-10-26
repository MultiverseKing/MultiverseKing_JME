package gui;

import com.jme3.math.Vector2f;
import com.simsilica.es.Entity;
import hexsystem.battle.BattleSystem;
import org.hexgridapi.utility.Vector2Int;
import tonegod.gui.controls.text.Label;
import tonegod.gui.controls.windows.Window;
import tonegod.gui.core.ElementManager;

/**
 *
 * @author roah
 */
public abstract class PropertiesWindow extends Window {

    protected final BattleSystem system;
    protected String windowTitle = "Properties : ";
    private int fieldCount = 0;

    public PropertiesWindow(ElementManager screen, String UID, BattleSystem system, int maxField) {
        super(screen, UID + "PropertiesWindow", new Vector2f(screen.getWidth() - 260, 10), new Vector2f(250, 30 * maxField));
        setIgnoreMouse(true);
        setUseCollapseButton(true);
        setUseCloseButton(true);
        getDragBar().setIgnoreMouse(true);
        setWindowTitle(windowTitle);
        this.system = system;
    }

    /**
     * value.x == min, value.y == max.
     *
     * @param labelName
     * @param value
     */
    protected final void addMinMaxField(String labelName, Vector2Int value) {
        addMinMaxField(labelName, value.x, value.y);
    }

    protected final void addMinMaxField(String labelName, int minValue, int maxValue) {
        Label label = generateLabel(labelName);
        label.setText(label.getText() + minValue + " / " + maxValue);
        label.setWidth(label.getText().length() * 8);
        addWindowContent(label);
    }

    protected final void addField(String labelName, float value) {
        Label label = generateLabel(labelName);
        label.setText(label.getText() + " " + value);
        label.setWidth(label.getText().length() * 8);
        addWindowContent(label);
    }

    private Label generateLabel(String labelName) {
        Label label = new Label(screen, new Vector2f(10, fieldCount * 25), new Vector2f(0, 25));
        label.setText(labelName + " : ");
        fieldCount++;
        return label;
    }

    public void show(Entity inspectedEntity) {
        if (screen.getElementById(getUID()) != null) {
            show();
        } else {
            showWindow(inspectedEntity);
            screen.addElement(this);
        }
    }

    @Override
    public void hideWindow() {
        super.hideWindow();
        system.closeEntityPropertiesMenu();
    }

    protected abstract void showWindow(Entity e);
}

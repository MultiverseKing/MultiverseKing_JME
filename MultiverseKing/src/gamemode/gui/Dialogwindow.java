package gamemode.gui;

import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.Vector2f;
import tonegod.gui.controls.windows.Window;
import tonegod.gui.core.ElementManager;

/**
 *
 * @author roah
 */
public class Dialogwindow extends EditorWindow {

    protected final DialogWindowListener listener;
    private Window popup;

    public Dialogwindow(ElementManager screen, String WindowName, DialogWindowListener listener) {
        super(screen, null, WindowName);
        this.listener = listener;
    }

    public void show() {
        addButtonList("staticField", new String[]{"Confirm", "Cancel"}, HAlign.right);
        initKeyMapping();
        screen.getApplication().getInputManager().addListener(dialogPopupListener, new String[]{"confirmDialog", "cancelDialog"});
        super.show(null, null);
    }

    public void addInputText(String labelName) {
        addTextField("Name", null, HAlign.left);
    }

    public void addButton(String labelName) {
        addButtonField(labelName, HAlign.full);
    }

    @Override
    protected void onButtonTrigger(String labelName) {
        if (labelName.equals("Confirm")) {
            listener.onDialogTrigger(getUID(), true);
        } else if (labelName.equals("Cancel")) {
            listener.onDialogTrigger(getUID(), false);
            clear();
        }
    }

    private void clear() {
        removeMapping();
        removeFromScreen();
    }

    private void initKeyMapping() {
        screen.getApplication().getInputManager().addMapping("confirmDialog", new KeyTrigger(KeyInput.KEY_RETURN));
        screen.getApplication().getInputManager().addMapping("cancelDialog", new KeyTrigger(KeyInput.KEY_ESCAPE));
    }

    private void removeMapping() {
        screen.getApplication().getInputManager().removeListener(dialogPopupListener);
        screen.getApplication().getInputManager().deleteMapping("confirmDialog");
        screen.getApplication().getInputManager().deleteMapping("cancelDialog");
    }
    private final ActionListener dialogPopupListener = new ActionListener() {
        public void onAction(String name, boolean isPressed, float tpf) {
            if (name.equals("confirmDialog") && !isPressed) {
                onButtonTrigger("Confirm");
            } else if (name.equals("cancelDialog") && !isPressed) {
                onButtonTrigger("Cancel");
            }
        }
    };

    @Override
    protected void onTextFieldInput(String UID, String input, boolean triggerOn) {
        if (triggerOn) {
            onButtonTrigger("Confirm");
        } else {
            onButtonTrigger("Cancel");
        }
    }

    public boolean isVisible() {
        if (getWindow() != null) {
            return getWindow().getIsVisible();
        } else {
            return false;
        }
    }

    public String getInput(String name) {
        return getTextField(name).getText();
    }

    public void popupBox(String message) {
        if (popup == null) {
            popup = new Window(screen, getUID() + "popupBox", new Vector2f(0, getWindow().getHeight()), new Vector2f(getWindow().getWidth(), 25));
            popup.removeAllChildren();
            getWindow().addChild(popup);
        } else if (!popup.getIsVisible()) {
            getWindow().addChild(popup);
        }
        popup.setText(message);
    }
}

package gui.deprecated.control;

import gui.deprecated.listener.DialogWindowListener;
import com.jme3.input.controls.ActionListener;
import com.jme3.math.Vector2f;
import tonegod.gui.controls.lists.Spinner;
import tonegod.gui.controls.windows.Window;
import tonegod.gui.core.Screen;

/**
 *
 * @author roah
 */
public class DialogWindow extends EditorWindow {

    protected final DialogWindowListener listener;
    private Window popup;
    private boolean enabled = true;
    
    public DialogWindow(Screen screen, String WindowName, DialogWindowListener listener) {
        super(screen, null, WindowName);
        this.listener = listener;
    }

    public final void show(boolean addCancel) {
        addButtonList("ConfirmBtn", (addCancel ? new String[]{"Confirm", "Cancel"} : new String[]{"Confirm"}), HAlign.right, ButtonType.TEXT);
        screen.getApplication().getInputManager().addListener(dialogPopupListener, 
                (addCancel ? new String[]{"confirmDialog", "cancelDialog"} : new String[]{"confirmDialog"}));
        super.show(null, null);
    }

    public void addInputText(String labelName) {
        addTextField(null, "Name", null, HAlign.left, 1);
    }

    public void addButton(String labelName) {
        addButtonField(null, labelName, HAlign.full, ButtonType.TEXT);
    }

    public void addSpinnerField(String labelName, int[] value) {
        addSpinnerField(null, labelName, value, HAlign.left);
    }

    public void addLabelField(String labelText){
        addLabelField(null, labelText, HAlign.left);
    }
    
    public void showText(String string) {
        addLabelField(null, string, HAlign.left);
    }

    @Override
    protected void onButtonTrigger(String labelName) {
        if (labelName.equals("Confirm")) {
            listener.onDialogTrigger(name, true);
        } else if (labelName.equals("Cancel")) {
            listener.onDialogTrigger(name, false);
        }
    }

    @Override
    public void onPressCloseAndHide() {
        listener.onDialogTrigger(name, false);
    }

    @Override
    public void removeFromScreen() {
        removeMapping();
        super.removeFromScreen();
    }

    private void removeMapping() {
        screen.getApplication().getInputManager().removeListener(dialogPopupListener);
    }
    private final ActionListener dialogPopupListener = new ActionListener() {
        public void onAction(String name, boolean isPressed, float tpf) {
            if (enabled) {
                if (name.equals("confirmDialog") && !isPressed) {
                    onButtonTrigger("Confirm");
                } else if (name.equals("cancelDialog") && !isPressed) {
                    onButtonTrigger("Cancel");
                }
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

    @Override
    public boolean isVisible() {
        if (getWindow() != null) {
            return getWindow().getIsVisible();
        } else {
            return false;
        }
    }

    public String getTextInput(String name) {
        return getTextField(null, name).getText();
    }

    public int getSpinnerInput(String name) {
        return getSpinnerField(null, name).getSelectedIndex();
    }

    @Override
    public void hide() {
        if (window != null && popup != null) {
            popup.getElementParent().removeChild(popup);
        }
        super.hide();
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

    public void enable(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    protected void onNumericFieldInput(Integer input) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void onSelectBoxFieldChange(Enum value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void onSpinnerChange(String sTrigger, int currentIndex, Spinner.ChangeType type) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}

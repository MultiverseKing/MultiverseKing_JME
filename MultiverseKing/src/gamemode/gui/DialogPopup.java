package gamemode.gui;

import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.Vector2f;
import tonegod.gui.core.ElementManager;

/**
 *
 * @author roah
 */
public class DialogPopup extends EditorWindow {

    private final DialogWindowListener listener;

    public DialogPopup(ElementManager screen, String WindowName, DialogWindowListener listener) {
        super(screen, null, WindowName);
        this.listener = listener;
    }

    public void show() {
        addButtonList(new String[]{"Confirm", "Cancel"}, HAlign.right);
        initKeyMapping();
        screen.getApplication().getInputManager().addListener(dialogPopupListener, new String[]{"confirmDialog", "cancelDialog"});
        super.show(new Vector2f(0, getelementListCount()), null, null);
    }

    public void addInputText(String labelName) {
        addEditableTextField("Name", null, Vector2f.ZERO);
    }

    @Override
    protected void onButtonTrigger(int index) {
        if (index == 0) {
            listener.onDialogTrigger(getUID(), true);
        } else if (index == 1) {
            listener.onDialogTrigger(getUID(), false);
        }
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
                onButtonTrigger(0);
            } else if (name.equals("cancelDialog") && !isPressed) {
                onButtonTrigger(1);
            }
        }
    };

    @Override
    protected void onTextFieldInput(String UID, String input, boolean triggerOn) {
        if(triggerOn){
            onButtonTrigger(0);
        } else {
            onButtonTrigger(1);
        }
    }

    
    
    public String getInput(String name) {
        return getTextField(name).getText();
    }
}

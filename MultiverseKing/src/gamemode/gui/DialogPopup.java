package gamemode.gui;

import com.jme3.math.Vector2f;
import gamemode.editor.EditorWindow;
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
    
    public void show(){
        addButtonList(new String[]{"Confirm", "Cancel"}, HAlign.right);
        super.show(new Vector2f(0, getelementListCount()), null, null);
    }

    @Override
    protected void onButtonTrigger(int index) {
        if(index == 0){
            listener.onDialogTrigger(getUID(), true);
        } else if (index == 1){
            listener.onDialogTrigger(getUID(), false);
        }
    }
}

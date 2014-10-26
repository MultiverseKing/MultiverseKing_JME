package hexsystem.battle;

import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector2f;
import tonegod.gui.controls.buttons.ButtonAdapter;
import tonegod.gui.controls.windows.Window;
import tonegod.gui.core.ElementManager;

/**
 *
 * @author roah
 */
public class TitanGUIList extends Window {

    private final int titanCount = 3;

    public TitanGUIList(ElementManager screen, Vector2f position) {
        super(screen, position, new Vector2f(200, 100));
        removeChild(getDragBar());
        addTitan();
    }

    private void addTitan() {
        for (int i = 0; i < titanCount; i++) {
            final int index = i;
            ButtonAdapter unit = new ButtonAdapter(screen, new Vector2f(10 + (75 * i), 10), new Vector2f(75, 75)) {
                @Override
                public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                    super.onButtonMouseLeftUp(evt, toggled);
                    setSelectedButton(index);
                }
            };
            addWindowContent(unit);
        }
    }

    public void showWindow() {
    }

    private void setSelectedButton(int index) {
        System.out.println(index);
    }
}

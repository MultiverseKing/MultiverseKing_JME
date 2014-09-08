package gamemode.editor;

import com.jme3.app.state.AbstractAppState;
import com.jme3.font.BitmapFont;
import com.jme3.font.LineWrapMode;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector2f;
import entitysystem.EntitySystemAppState;
import java.util.ArrayList;
import tonegod.gui.controls.buttons.Button;
import tonegod.gui.controls.buttons.ButtonAdapter;
import tonegod.gui.controls.lists.SelectList;
import tonegod.gui.controls.windows.Window;
import tonegod.gui.core.Element;
import tonegod.gui.core.ElementManager;

/**
 *
 * @author roah
 */
public abstract class EditorMenu extends Element {

    protected final EntitySystemAppState system;
    private ArrayList<Element> additionalElement = new ArrayList<Element>(3);
    private Window additionalFieldWindow;
    private SelectList selectList = null;

    public EditorMenu(ElementManager screen, String UID, String titleName, EntitySystemAppState system) {
        super(screen, UID, new Vector2f(15, 15), new Vector2f(130, 25),
                screen.getStyle("Window#Dragbar").getVector4f("resizeBorders"),
                screen.getStyle("Window#Dragbar").getString("defaultImg"));
        this.setTextWrap(LineWrapMode.valueOf(screen.getStyle("Window#Dragbar").getString("textWrap")));
        this.setTextAlign(BitmapFont.Align.valueOf(screen.getStyle("Window#Dragbar").getString("textAlign")));
        this.setTextVAlign(BitmapFont.VAlign.valueOf(screen.getStyle("Window#Dragbar").getString("textVAlign")));
        this.setTextPosition(20, 0);
        this.setFontColor(screen.getStyle("Window#Dragbar").getColorRGBA("fontColor"));
        this.setFontSize(screen.getStyle("Window#Dragbar").getFloat("fontSize"));
        this.setText(titleName);
        this.system = system;
    }

    /**
     * Create SubMenu List for this System.
     */
    private void generateSelectList() {
        selectList = new SelectList(screen, new Vector2f(0, getHeight())) {
            private int lastSelectedIndex = -1;

            @Override
            public void onChange() {
                if (getSelectedIndex() != lastSelectedIndex) {
                    lastSelectedIndex = getSelectedIndex();
                    onSelectedItemChange((Integer) getListItem(getSelectedIndex()).getValue());
                }
            }
        };
        addChild(selectList);
    }

    protected final void addItem(String caption, Integer value) {
        if (selectList == null) {
            generateSelectList();
        }
        selectList.addListItem(caption, value);
        selectList.setDimensions(getWidth() - getAbsoluteX() - 5, selectList.getListItems().size() * 25);
        selectList.setPosition(0, -selectList.getListItems().size() * 25);
        selectList.pack();
    }

    /**
     * Button to return back to the main menu.
     */
    protected final void populateEditor() {
        addAdditionalField("Return");
        additionalFieldWindow = new Window(screen, "ReturnButtonWin",
                new Vector2f(0, getHeight()
                + ((selectList == null ? 0 : selectList.getListItems().size()) * 25)),
                new Vector2f(getWidth() + 5, 20 * additionalElement.size() - 1 + 10));
        additionalFieldWindow.removeAllChildren();
        additionalFieldWindow.setIsResizable(false);
        additionalFieldWindow.setIsMovable(false);

        for (Element e : additionalElement) {
            additionalFieldWindow.addChild(e);
        }
        addChild(additionalFieldWindow);
    }

    protected final void addAdditionalField(String name) {
        final int value = additionalElement.size();
        Button btn = new ButtonAdapter(screen, "Additional" + name.replaceAll("\\s", "") + "Button",
                new Vector2f(7, 20 * additionalElement.size() - 1 + 5), new Vector2f(getWidth() - 10, 20)) {
            @Override
            public void onButtonMouseLeftDown(MouseButtonEvent evt, boolean toggled) {
                super.onButtonMouseLeftDown(evt, toggled);
                if (getUID().equals("AdditionalReturnButton")) {
                    additionalFieldReturnTrigger();
                } else {
                    onAdditionalFieldTrigger(value);
                }
            }
        };
        btn.setText(name);
        additionalElement.add(btn);
    }

    public AbstractAppState getSystem() {
        return system;
    }

    protected void additionalFieldReturnTrigger() {
        app.getStateManager().detach(system);
    }

    public abstract void update(float tpf);

    protected abstract void onAdditionalFieldTrigger(int value);

    protected abstract void onSelectedItemChange(int index);
}

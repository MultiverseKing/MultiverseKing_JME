package gamemode.editor;

import com.jme3.font.BitmapFont;
import com.jme3.font.LineWrapMode;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector2f;
import entitysystem.EntitySystemAppState;
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

    private final EntitySystemAppState system;
    private SelectList selectList;

    public EditorMenu(ElementManager screen, String UID, String titleName, EntitySystemAppState system) {
        super(screen, UID, new Vector2f(15, 15), new Vector2f(130, 25),
                screen.getStyle("Window#Dragbar").getVector4f("resizeBorders"),
                screen.getStyle("Window#Dragbar").getString("defaultImg"));
        this.setTextPadding(screen.getStyle("Window#Dragbar").getFloat("textPadding"));
        this.setTextWrap(LineWrapMode.valueOf(screen.getStyle("Window#Dragbar").getString("textWrap")));
        this.setTextAlign(BitmapFont.Align.valueOf(screen.getStyle("Window#Dragbar").getString("textAlign")));
        this.setTextVAlign(BitmapFont.VAlign.valueOf(screen.getStyle("Window#Dragbar").getString("textVAlign")));
        this.setTextPosition(20,0);
        this.setFontColor(screen.getStyle("Window#Dragbar").getColorRGBA("fontColor"));
        this.setFontSize(screen.getStyle("Window#Dragbar").getFloat("fontSize"));
        this.setText(titleName);
        this.system = system;
        
        /**
         * Create SubMenu List for this System.
         */
        selectList = new SelectList(screen, new Vector2f(0, getHeight())) {
            private int lastSelectedIndex = -1;
            @Override
            public void onChange() {
                if(getSelectedIndex() != lastSelectedIndex) {
                    lastSelectedIndex = getSelectedIndex();
                    onSelectedItemChange((Integer) getListItem(getSelectedIndex()).getValue());
                }
            }
        };
        addChild(selectList);
    }

    /**
     * @todo: Use EditorWindow
     */
    public void addItem(String caption, Integer value) {
        selectList.addListItem(caption, value);
        selectList.setDimensions(getWidth()-getAbsoluteX()-5, selectList.getListItems().size()*25);
        selectList.setPosition(0, -selectList.getListItems().size()*25);
        selectList.pack();
    }

    /**
     * Button to return back to the main menu.
     */
    protected final void populateReturnEditorMain() {
        Window closeButtonWin = new Window(screen, "CloseRootButtonWin",
                new Vector2f(0, getHeight() + (selectList.getListItems().size() * 25)), new Vector2f(getWidth()+5, 35));
        closeButtonWin.removeAllChildren();
        closeButtonWin.setIsResizable(false);
        closeButtonWin.setIsMovable(false);

        Button close = new ButtonAdapter(screen, "returnMain",
                new Vector2f(5, 5), new Vector2f(closeButtonWin.getWidth()-10, 25)) {
            @Override
            public void onButtonMouseLeftDown(MouseButtonEvent evt, boolean toggled) {
                super.onButtonMouseLeftDown(evt, toggled);
                app.getStateManager().detach(system);
            }
        };
        close.setText("Return Main");
        closeButtonWin.addChild(close);
        addChild(closeButtonWin);
    }

    protected abstract void onSelectedItemChange(int selectedIndex);
}

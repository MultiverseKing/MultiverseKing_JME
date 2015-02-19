package core.gui;

import com.jme3.math.Vector2f;
import core.EditorMainSystem;
import gui.EditorWindow;
import tonegod.gui.controls.buttons.ButtonAdapter;
import tonegod.gui.controls.windows.Window;
import tonegod.gui.core.Element;
import tonegod.gui.core.Screen;
import tonegod.gui.effects.Effect;

/**
 *
 * @author roah
 */
public class EditorMainGUI extends EditorWindow {

    private EditorMainSystem system;
    private String currentSelectedBtn = "0";
    private EditorTileProperties tileWindow;

    public EditorMainGUI(Screen screen, Element parent, EditorMainSystem system) {
        super(screen, parent, "HexTile Editor");
        this.system = system;
//        addButtonList("CurrentTool", "Tool", HAlign.left, new String[]{"disable", "cursorSelect"}, HAlign.left, ButtonType.IMG, 1);
        addLabelField("Data", "No data", HAlign.left);
//        InputContext iContext = InputContext.getInstance();
//        String local = iContext.getLocale().toString();
//        screen.getApplication().getInputManager().addMapping("editorTool.disable", new KeyTrigger(KeyInput.KEY_1));
//        screen.getApplication().getInputManager().addMapping("editorTool.editTile", new KeyTrigger(KeyInput.KEY_2));
        
        show();
        populateHelp();
        tileWindow = new EditorTileProperties(screen, getWindow(), system);
    }

    private void show() {
        if (parent != null) {
            showConstrainToParent(VAlign.bottom, HAlign.left);
        } else {
            show(VAlign.top, HAlign.left);
        }
        window.setUseCloseButton(false);
        window.setUseCollapseButton(true);
    }
    
    public void showCurrentSelectionCount(int count){
        Element field = getField("Data", null);
        field.setDimensions(layoutGridSize);
        field.setText("Currently Selected : " + count);
    }

    private void populateHelp() {
        Window helpBtn = new Window(screen, "helpWindow", new Vector2f(screen.getWidth() - 100, 0), new Vector2f(100, 30));
        helpBtn.setWindowTitle(" F1 for Help");
        window.addChild(helpBtn);
    }

    private void openHelpWindow() {
        Window helpWin = (Window) window.getChildElementById("helpWindow").getChildElementById("helpWindowExpand");
        if(helpWin == null) {
            Vector2f dimension = new Vector2f(354, 164);
            helpWin = new Window(screen, "helpWindowExpand", new Vector2f(-(dimension.x - window.getChildElementById("helpWindow").getDimensions().x), 0), 
                    dimension, screen.getStyle("Window").getVector4f("resizeBorders"), 
                    screen.getStyle("Window").getString("defaultImg"));
            helpWin.setWindowTitle("Help");
            helpWin.addEffect(new Effect(Effect.EffectType.SlideOut, Effect.EffectEvent.Hide, 0.5f));
            window.getChildElementById("helpWindow").addChild(helpWin);
        } else if(helpWin.getIsVisible()) {
            helpWin.hideWithEffect();
        } else {
            helpWin.show();
        }
    }

    public EditorTileProperties getTileWindow(){
        return tileWindow;
    }
    
    @Override
    protected void onButtonTrigger(String label) {
//        setSelected(label);
    }

    @Override
    protected void onTextFieldInput(String UID, String input, boolean triggerOn) {
    }

    @Override
    protected void onNumericFieldInput(Integer input) {
    }

    @Override
    protected void onSelectBoxFieldChange(Enum value) {
    }

    @Override
    protected void onSpinnerChange(String sTrigger, int currentIndex) {
    }

    @Override
    public void onPressCloseAndHide() {
    }

    private void setSelected(String selected) {
        ButtonAdapter btn;
        if (!currentSelectedBtn.equals("0")) {
            btn = (ButtonAdapter) getListButtonField("CurrentTool", EditorWindow.ButtonType.IMG, currentSelectedBtn);
            if (btn.getButtonIcon() != null) {
                btn.getButtonIcon().hide();
            }
        }
        btn = (ButtonAdapter) getListButtonField("CurrentTool", EditorWindow.ButtonType.IMG, selected);
        if (btn.getButtonIcon() != null) {
            btn.getButtonIcon().show();
        } else {
            btn.setButtonIcon(btn.getDimensions().x, btn.getDimensions().y, "Textures/Icons/Buttons/selected.png");
        }
//        if (!currentSelectedBtn.equals("0")) {
//            updateSystem(selected);
//        }
        currentSelectedBtn = selected;
    }
    
    private void updateSystem(String selected){
        switch(selected){
            case "disable":
                system.setSelectionGroup(false);
                break;
            case "cursorSelect":
                system.setSelectionGroup(true);
                break;
        }
    }
}

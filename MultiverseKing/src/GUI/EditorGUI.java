/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector2f;
import hexsystem.HexMapManager;
import kingofmultiverse.MultiverseMain;
import utility.attribut.ElementalAttribut;
import tonegod.gui.controls.buttons.Button;
import tonegod.gui.controls.buttons.ButtonAdapter;
import tonegod.gui.controls.buttons.CheckBox;
import tonegod.gui.controls.buttons.RadioButton;
import tonegod.gui.controls.buttons.RadioButtonGroup;
import tonegod.gui.controls.windows.Window;
import tonegod.gui.core.Element;

/**
 *
 * @author roah
 */
public class EditorGUI extends AbstractAppState{

    private MultiverseMain main;
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app); //To change body of generated methods, choose Tools | Templates.
        main = (MultiverseMain)app;
        
        Window win = new Window(main.getScreen(), "EditorMain", new Vector2f(15f, 15f));
        win.setWindowTitle("Main Windows");
        win.setMinDimensions(new Vector2f(130, 100));
        win.setWidth(new Float(50));
//        win.setIsMovable(false);
        win.setIgnoreMouse(true);
        main.getScreen().addElement(win);

        ButtonAdapter makeWindow = new ButtonAdapter( main.getScreen(), "MapElement", new Vector2f(15, 40) ) {
            @Override
            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                elementalWindow();
            }
        };
        makeWindow.setText("Change Map Elements");

        // Add it to out initial window
        win.addChild(makeWindow);
    }

    public final void elementalWindow() {
        Window eWin = 
                new Window(main.getScreen(), "EWindows",
                new Vector2f( (main.getScreen().getWidth()/2)-175, (main.getScreen().getHeight()/2)-100 )
        );
        eWin.setWindowTitle("Elemental Windows");
//        ButtonAdapter closeWindow = new ButtonAdapter( main.getScreen(), "Close", new Vector2f(15, 40) ) {
//            @Override
//            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
//                main.getScreen().removeElement(this.getElementParent());
//            }
//        };
//        closeWindow.setText("Close");
//        eWin.addChild(closeWindow);

        RadioButtonGroup elementG = new RadioButtonGroup(main.getScreen(), "EButtonGroup") {
            @Override
            public void onSelect(int index, Button value) {
//                System.out.println(index);
                if(index != ElementalAttribut.getSize() && index != 1){
                    changeMapElement(ElementalAttribut.toString(ElementalAttribut.convert(index)));
                } else if (index == 1){
                    System.out.println("no Implemented");
                }else {
                    main.getScreen().removeElement(main.getScreen().getElementById("EWindows"));
                }
            }

            private void changeMapElement(String eAttribut) {
                main.getStateManager().getState(HexMapManager.class).changeZoneElement(eAttribut);
            }
        };
        for(int i = 0; i < ElementalAttribut.getSize(); i++){
            ButtonAdapter button = new ButtonAdapter(main.getScreen(), "Button+"+ElementalAttribut.convert(i), new Vector2f(15, 40+(40*i)));
            button.setText(ElementalAttribut.toString(ElementalAttribut.convert(i)));
            elementG.addButton(button);
            
        }
        elementG.addButton(new ButtonAdapter( main.getScreen(), "Close", new Vector2f(15, 40+(40*ElementalAttribut.getSize()))));
//        elementG.addButton(new ButtonAdapter(main.getScreen(), ""));
//        elementG.addButton(new ButtonAdapter(main.getScreen(), new Vector2f(10,10)));
//        rbg.addButton(new CheckBox(main.getScreen(), new Vector2f(10,30)));
//        rbg.addButton(new RadioButton(main.getScreen(), new Vector2f(10,50)));
        elementG.setDisplayElement(eWin); // null adds the button list to the screen layer
        
        main.getScreen().addElement(eWin);
    }
}
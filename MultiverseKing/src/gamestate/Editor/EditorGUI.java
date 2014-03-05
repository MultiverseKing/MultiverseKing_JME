/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gamestate.Editor;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector2f;
import kingofmultiverse.MultiverseMain;
import utility.attribut.ElementalAttribut;
import tonegod.gui.controls.buttons.Button;
import tonegod.gui.controls.buttons.ButtonAdapter;
import tonegod.gui.controls.buttons.RadioButtonGroup;
import tonegod.gui.controls.windows.Window;

/**
 * @todo User should not be able to move the main windows.
 * @author roah
 */
class EditorGUI extends AbstractAppState{

    private MultiverseMain main;
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app); //To change body of generated methods, choose Tools | Templates.
        main = (MultiverseMain)app;
        
        Window win = new Window(main.getScreen(), "EditorMain", new Vector2f(15f, 15f));
        win.setWindowTitle("Main Windows");
        win.setMinDimensions(new Vector2f(130, 100));
        win.setWidth(new Float(50));
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
        RadioButtonGroup elementG = new RadioButtonGroup(main.getScreen(), "EButtonGroup") {
            @Override
            public void onSelect(int index, Button value) {
//                System.out.println(index);
                if(index != ElementalAttribut.getSize()){
                    changeMapElement(ElementalAttribut.convert((byte)index).toString());
                } else {
                    main.getScreen().removeElement(main.getScreen().getElementById("EWindows"));
                }
            }

            private void changeMapElement(String eAttribut) {
//                main.getStateManager().getState(HexMap.class).changeZoneElement(eAttribut);
            }
        };
        for(int i = 0; i < ElementalAttribut.getSize(); i++){
            ButtonAdapter button = new ButtonAdapter(main.getScreen(), "Button+"+ElementalAttribut.convert((byte)i), new Vector2f(15, 40+(40*i)));
            button.setText(ElementalAttribut.convert((byte)i).toString());
            elementG.addButton(button);
        }
        Button closeButton = new ButtonAdapter( main.getScreen(), "Close", new Vector2f(15, 40+(40*ElementalAttribut.getSize())));
        closeButton.setText("CLOSE");
        elementG.addButton(closeButton);
        elementG.setDisplayElement(eWin); // null adds the button list to the screen layer
        
        main.getScreen().addElement(eWin);
    }
}
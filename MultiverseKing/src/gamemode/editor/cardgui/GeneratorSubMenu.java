package gamemode.editor.cardgui;

import com.jme3.input.KeyInput;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector2f;
import entitysystem.attribut.SubType;
import gamemode.editor.EditorMenuWindow;
import tonegod.gui.controls.buttons.ButtonAdapter;
import tonegod.gui.controls.lists.Spinner;
import tonegod.gui.controls.text.LabelElement;
import tonegod.gui.controls.text.TextField;
import tonegod.gui.controls.windows.Window;
import tonegod.gui.core.Element;
import tonegod.gui.core.ElementManager;

/**
 *
 * @author roah
 */
public class GeneratorSubMenu extends EditorMenuWindow {
    
    public GeneratorSubMenu(ElementManager screen, Element parent, SubType type) {
        super(screen, parent, "Properties");
        switch(type){
            case ABILITY:
                /**
                 * Part used to show/set how many power have the ability. 
                 * damage - segmentCost - activationRange - FXUsed - hitCollision - isCastOnSelf
                 */
                addEditableNumericField("Power", 15);
                /**
                 * Part used to show/set the segmentCost needed for the unit to cast the
                 * ability.
                 */
                addSpinnerField("Cast Cost", new int[]{0, 20, 1, 0});
                /**
                 * @todo Part used to set the Activation range of the ability.
                 */
            case EQUIPEMENT:
            case SPELL:
            case SUMMON:
            case TRAP:
        }
    }

    @Override
    protected void onNumericFieldInput(Integer input) {
        super.onNumericFieldInput(input);
    }
    

    @Override
    protected void onSpinnerChange(String sTrigger, int currentIndex) {
        super.onSpinnerChange(sTrigger, currentIndex);
    }
    
    /**
     * Menu used to tweak ability properties.
     */
    private void openSubAbilityMenu(Window subMenu) {

        /**
         * Part used to show/set how many power have the ability. damage -
         * segmentCost - activationRange - FXUsed - hitCollision - isCastOnSelf
         */
//        LabelElement damageLabel = new LabelElement(main.getScreen(), "powerLabel", new Vector2f(10, 10), new Vector2f(58, 30));
//        damageLabel.setText("Power : ");
//        subMenu.addChild(damageLabel);
//
//        ButtonAdapter damageButton = new ButtonAdapter(main.getScreen(), "powerButton",
//                new Vector2f(damageLabel.getWidth(), 2), new Vector2f(45, 30)) {
//            @Override
//            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
//                super.onButtonMouseLeftUp(evt, toggled);
//                TextField damageField = new TextField(screen, Vector2f.ZERO, new Vector2f(45, 30)) {
//                    @Override
//                    public void onKeyRelease(KeyInputEvent evt) {
//                        super.onKeyRelease(evt);
//                        if (evt.getKeyCode() == KeyInput.KEY_RETURN
//                                && getText() != null && getText().length() != 0) {
//                            screen.getElementById("powerButton").setText(getText());
//                            getElementParent().removeChild(this);
//                        }
//                    }
//                };
//                damageField.setType(TextField.Type.NUMERIC);
//                addChild(damageField);
//                damageField.setText(getText());
//            }
//        };
//        damageButton.setText("0");
//        damageLabel.addChild(damageButton);

        /**
         * Part used to show/set the segmentCost needed for the unit to cast the
         * ability.
         */
//        LabelElement unitCastCostLabel = new LabelElement(main.getScreen(), "unitCastCostLabel", new Vector2f(10, 40), new Vector2f(160, 30));
//        unitCastCostLabel.setText("Unit Segment Used : ");
//        subMenu.addChild(unitCastCostLabel);
//
//        Spinner unitCastCostSpinner = new Spinner(main.getScreen(), "unitCastCostSlider",
//                new Vector2f(10, unitCastCostLabel.getDimensions().y), Element.Orientation.HORIZONTAL, true) {
//            @Override
//            public void onChange(int selectedIndex, String value) {
////                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//            }
//        };
//        unitCastCostSpinner.setStepIntegerRange(0, 20, 1);
//        unitCastCostLabel.addChild(unitCastCostSpinner);

        /**
         * @todo Part used to set the Activation range of the ability.
         */
    }
}

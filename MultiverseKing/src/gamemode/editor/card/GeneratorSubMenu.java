package gamemode.editor.card;

import com.jme3.math.Vector2f;
import entitysystem.attribut.SubType;
import gamemode.editor.EditorMenuWindow;
import tonegod.gui.controls.lists.Spinner;
import tonegod.gui.core.Element;
import tonegod.gui.core.ElementManager;
import tonegod.gui.core.layouts.LayoutHint.VAlign;

/**
 *
 * @author roah
 */
public class GeneratorSubMenu extends EditorMenuWindow {

    private final SubType current;

    public SubType getCurrent() {
        return current;
    }

    public GeneratorSubMenu(ElementManager screen, Element parent, SubType type) {
        super(screen, parent, "Properties");
        current = type;
        switch (type) {
            case ABILITY:
                /**
                 * Part used to show/set how many power have the ability. damage
                 * - segmentCost - activationRange - FXUsed - hitCollision -
                 * isCastOnSelf
                 */
                addEditableNumericField("Power", 15, Vector2f.ZERO);
                /**
                 * Part used to show/set the segmentCost needed for the unit to
                 * cast the ability.
                 */
                addSpinnerField("Segment Cost", new int[]{0, 20, 1, 0}, new Vector2f(0, getGridSize().y));
                Spinner spin = getSpinnerField("Segment Cost");
                spin.setPosition(spin.getPosition().x - 50, spin.getPosition().y);
                /**
                 * Part used to set the Activation range of the ability.
                 */
                addSpinnerField("Activation Range", new int[]{0, 100, 1, 0}, new Vector2f(0, getGridSize().y * 2));
                spin = getSpinnerField("Activation Range");
                spin.setPosition(spin.getPosition().x - 90, spin.getPosition().y);
                /**
                 * Part used to know if the spell is cast from the coster or the
                 * target.
                 */
                addCheckBoxField("Is Cast from Self", false, new Vector2f(30, getGridSize().y * 3.2f));
                show(getGridSize().x * 2, getGridSize().y * 5f, VAlign.center);
                break;
            case EQUIPEMENT:
                show(getGridSize().x * 2, getGridSize().y * 3.5f, VAlign.center);
                break;
            case SPELL:
                show(getGridSize().x * 2, getGridSize().y * 3.5f, VAlign.center);
                break;
            case SUMMON:
                show(getGridSize().x * 2, getGridSize().y * 3.5f, VAlign.center);
                break;
            case TRAP:
                show(getGridSize().x * 2, getGridSize().y * 3.5f, VAlign.center);
                break;
            default:
                throw new UnsupportedOperationException(type + " is not a supported type in : " + getClass().getName());
        }
//        getWindow().scale(0.9f);
    }

    @Override
    protected void onNumericFieldInput(Integer input) {
        super.onNumericFieldInput(input);
    }

    @Override
    protected void onSpinnerChange(String sTrigger, int currentIndex) {
        super.onSpinnerChange(sTrigger, currentIndex);
    }

    public void setPower(int power) {
        getNumericField("Power").setText(Integer.toString(power));
    }

    public void setUnitSegmentCost(int cost) {
        getSpinnerField("Segment Cost").setSelectedIndex(cost);
    }

    public void setActivationRange(int range) {
        getSpinnerField("Activation Range").setSelectedIndex(range);
    }

    public void setCastFromSelf(boolean bool) {
        getCheckBoxField("Is Cast from Self").setIsChecked(bool);
    }
}

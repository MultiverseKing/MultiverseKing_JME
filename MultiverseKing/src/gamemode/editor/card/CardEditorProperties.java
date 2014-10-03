package gamemode.editor.card;

import entitysystem.attribut.CardType;
import static entitysystem.attribut.CardType.ABILITY;
import static entitysystem.attribut.CardType.EQUIPEMENT;
import static entitysystem.attribut.CardType.SUMMON;
import entitysystem.card.AbilityProperties;
import gamemode.gui.EditorWindow;
import tonegod.gui.core.Element;
import tonegod.gui.core.ElementManager;
import utility.Vector2Int;

/**
 *
 * @author roah
 */
class CardEditorProperties extends EditorWindow {

    private CardType current;

    CardType getCurrent() {
        return current;
    }

    CardEditorProperties(ElementManager screen, Element parent, CardType type) {
        super(screen, parent, "Properties");
        current = type;

        populateMenu();
        showConstrainToParent(VAlign.bottom, HAlign.left);
    }
    
    private void populateMenu() {
        switch (current) {
            case ABILITY:
                /**
                 * Part used to show/set how many power have the ability.
                 * FXUsed - hitCollision
                 */
                addNumericField("Power", 15, HAlign.right);
                /**
                 * Part used to show/set the segmentCost needed for the unit to
                 * cast the ability.
                 */
                addSpinnerField("Segment Cost", new int[]{0, 20, 1, 0}, HAlign.left);
                /**
                 * Part used to set the Activation range of the ability.
                 */
                addNumericListField("Range", new Integer[]{0, 0}, HAlign.left);
                break;
            case EQUIPEMENT:
                break;
            case SUMMON:
                break;
            case TITAN:
                break;
            default:
                throw new UnsupportedOperationException(current + " is not a supported type in : " + getClass().getName());
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

    public void setPower(int power) {
        getNumericField("Power").setText(Integer.toString(power));
    }

    public void setUnitSegmentCost(int cost) {
        getSpinnerField("Segment Cost").setSelectedIndex(cost);
    }

    public void setActivationRange(Vector2Int range) {
        getNumericListField("Range", 0).setText(String.valueOf(range.x));
        getNumericListField("Range", 1).setText(String.valueOf(range.y));
    }
    
    /**
     * @todo : Collision generation.
     */
    public AbilityProperties getProperties(){
        Vector2Int range = new Vector2Int(Integer.valueOf(getNumericListField("Range", 0).getText()),
                           Integer.valueOf(getNumericListField("Range", 1).getText()));
        return new AbilityProperties(Integer.valueOf(getNumericField("Power").getText()), 
                getSpinnerField("Segment Cost").getSelectedIndex(), range, null);
    }
}

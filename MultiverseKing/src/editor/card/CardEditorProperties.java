package editor.card;

import entitysystem.card.AbilityProperties;
import entitysystem.field.Collision;
import entitysystem.render.RenderComponent.RenderType;
import gui.EditorWindow;
import org.hexgridapi.utility.Vector2Int;
import tonegod.gui.controls.lists.Spinner;
import tonegod.gui.core.Element;
import tonegod.gui.core.Screen;

/**
 *
 * @author roah
 */
class CardEditorProperties extends EditorWindow {

    private RenderType current;
    private CollisionWindow collisionWin;

    RenderType getCurrent() {
        return current;
    }

    CardEditorProperties(Screen screen, Element parent, RenderType type) {
        super(screen, parent, "Properties");
        current = type;

        populateMenu();
        showConstrainToParent(VAlign.bottom, HAlign.left);
        onButtonTrigger("Show collision");
    }

    private void populateMenu() {
        switch (current) {
            case Ability:
                /**
                 * Part used to show/set how many power have the ability. FXUsed
                 * - hitCollision
                 */
                addNumericField("Power", 15, HAlign.left);
                /**
                 * Part used to show/set the segmentCost needed for the unit to
                 * cast the ability.
                 */
                addSpinnerField("Segment Cost", new int[]{0, 20, 1, 0}, HAlign.left);
                /**
                 * Part used to set the Activation range of the ability.
                 */
                int[] spinA = new int[]{0, 20, 1, 0};
                int[] spinB = new int[]{0, 20, 1, 1};
                int[][] spinList = new int[][]{spinA, spinB};
                addSpinnerList("Cast range", spinList, HAlign.left);
                addButtonField("Show collision");
                break;
            case Core:
                break;
            case Debug:
                break;
            case Environment:
                break;
            case Equipement:
                break;
            case Titan:
                break;
            case Unit:
                break;
            default:
                throw new UnsupportedOperationException(current + " is not a supported type in : " + getClass().getName());
        }
    }

    @Override
    protected final void onButtonTrigger(String label) {
        if (current.equals(RenderType.Ability)) {
            if (label.equals("Show collision")) {
                if (collisionWin == null) {
                    collisionWin = new CollisionWindow(screen, getWindow(), (byte) 1);
                } else if (!collisionWin.isVisible()) {
                    collisionWin.setVisible();
                }
                getButtonField("Show collision", false).setText("Hide collision");
            } else if (label.equals("Hide collision") && collisionWin.isVisible()) {
                collisionWin.hide();
                getButtonField("Show collision", false).setText("Show collision");
            }
        }
    }

    @Override
    public void onPressCloseAndHide() {
//        getButtonField("Show collision", false).setText("Show collision");
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

    public void setCastRange(Vector2Int range) {
        ((Spinner) getSpinnerListField("Cast range", 0)).setSelectedIndex(range.x);
        ((Spinner) getSpinnerListField("Cast range", 1)).setSelectedIndex(range.y);
    }

    public void setHitCollision(Collision collision) {
        if (collisionWin != null) {
            collisionWin.removeFromScreen();
        }
        collisionWin = new CollisionWindow(screen, getWindow(), collision);
    }

    public AbilityProperties getProperties() {
        Vector2Int range = new Vector2Int(((Spinner) getSpinnerListField("Cast range", 0)).getSelectedIndex(),
                ((Spinner) getSpinnerListField("Cast range", 1)).getSelectedIndex());
        return new AbilityProperties(Integer.valueOf(getNumericField("Power").getText()),
                getSpinnerField("Segment Cost").getSelectedIndex(), range, collisionWin.getCollision());
    }
}

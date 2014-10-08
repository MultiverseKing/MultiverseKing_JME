package gamemode.editor.card;

import entitysystem.attribut.CardType;
import static entitysystem.attribut.CardType.ABILITY;
import static entitysystem.attribut.CardType.EQUIPEMENT;
import static entitysystem.attribut.CardType.SUMMON;
import entitysystem.card.AbilityProperties;
import gamemode.gui.DialogWindowListener;
import gamemode.gui.Dialogwindow;
import gamemode.gui.EditorWindow;
import gamemode.gui.HexGridWindow;
import tonegod.gui.core.Element;
import tonegod.gui.core.Screen;
import utility.Vector2Int;

/**
 *
 * @author roah
 */
class CardEditorProperties extends EditorWindow implements DialogWindowListener {

    private CardType current;
    private HexGridWindow hexWin;
    private Dialogwindow popup;

    CardType getCurrent() {
        return current;
    }

    CardEditorProperties(Screen screen, Element parent, CardType type) {
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
                int[][] spinList = new int[][] {spinA, spinB};
                addSpinnerList("Cast range", spinList, HAlign.left);
//                addNumericListField("Cast range", new Integer[]{0, 0}, HAlign.left);
                addButtonList("collision", new String[] {"Show Collision", "Set Radius"}, HAlign.left);
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
    protected void onButtonTrigger(String label) {
        if(label.equals("Show Collision")){
            if(hexWin == null) {
                hexWin = new HexGridWindow(screen, 2, getWindow());
                hexWin.show();
            } else if(hexWin.isVisible()){
                hexWin.hide();
            } else if(!hexWin.isVisible()){
                hexWin.setVisible();
            }
        } else if(label.equals("Set Radius")){
            if(popup == null){
                popup = new Dialogwindow(screen, "Set Area Radius", this);
                popup.addSpinnerField("Radius");
                popup.show();
            } else if(!popup.isVisible()){
                popup.setVisible();
            }
        }
    }

    public void onDialogTrigger(String dialogUID, boolean confirmOrCancel) {
        if(confirmOrCancel){
            if(hexWin == null){
                hexWin = new HexGridWindow(screen, popup.getInput("Radius"), getWindow());
                hexWin.show();
            } else if(popup.getInput("Radius") != hexWin.getRadius()) {
                hexWin.reload(popup.getInput("Radius"));
                popup.hide();
            }
        }
        popup.hide();
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
        getSpinnerListField("Cast range", 0).setText(String.valueOf(range.x));
        getSpinnerListField("Cast range", 1).setText(String.valueOf(range.x));
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

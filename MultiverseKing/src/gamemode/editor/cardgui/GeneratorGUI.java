package gamemode.editor.cardgui;

import com.jme3.math.Vector2f;
import entitysystem.attribut.Faction;
import entitysystem.attribut.SubType;
import gamemode.editor.EditorMenuWindow;
import tonegod.gui.controls.windows.Window;
import tonegod.gui.core.Element;
import tonegod.gui.core.ElementManager;
import tonegod.gui.core.Screen;
import utility.ElementalAttribut;

/**
 *
 * @author roah
 */
public final class GeneratorGUI extends EditorMenuWindow {

    private boolean initialized = false;
    
    private CardPreview cardPreview;
    
    public GeneratorGUI(ElementManager screen, Element parent) {
        super(screen, parent, "Card Generator");
        /**
         * Part used to show/set the card Name.
         */
        addEditableTextField("Name", "TuxDoll", Vector2f.ZERO);
        /**
         * Part used to show/choose the cost needed to use the card.
         */
        addSpinnerField("Cost", new int[] {0, 20, 1, 0}, new Vector2f(getGridSize().x+50, 0));
        /**
         * Part used to show/choose the card faction.
         */
        addEditableSelectionField("Faction", Faction.NEUTRAL, Faction.values(), new Vector2f(0, getGridSize().y));
        /**
         * Part used to show/choose the card Type.
         */
        addEditableSelectionField("Card Type", SubType.SUMMON, SubType.values(), new Vector2f(getGridSize().x, getGridSize().y));
        /**
         * Part used to show/choose the card Element Attribut.
         */
        addEditableSelectionField("E.Attribut", ElementalAttribut.NULL, ElementalAttribut.values(), new Vector2f(getGridSize().x*2, getGridSize().y));
        /**
         * Part used to test out card.
         */
        addButtonField("Test Card", "Cast", 1, new Vector2f(0, getGridSize().y*2+25));
        /**
         * Part used to test out card.
         */
        addButtonField("SubType Properties", "Edit", 0, new Vector2f(getGridSize().x, getGridSize().y*2+25));
        /**
         * Part used to show/set the card Description text.
         */
        addEditableTextField("Description", "This is a Testing unit", new Vector2f(0, getGridSize().y*4.5f));
        Element el = getTextField("Description");
        el.setPosition(el.getPosition().x-75, el.getPosition().y);
        el.setWidth(getGridSize().x*2);
        /**
         * 
         */
        show(getGridSize().x*3, getGridSize().y*6.5f);
        cardPreview = new CardPreview((Screen) screen, getWindow());
        initialized = true;
    }

    @Override
    protected void onSelectBoxFieldChange(Enum value) {
        if(initialized){
            if(value instanceof Faction){
                /**
                 * Change inspected Card Faction.
                 */
                cardPreview.switchFaction((Faction) value);
            } else if (value instanceof SubType){
                /**
                 * Change inspected Card SubType.
                 */
                cardPreview.switchSubType((SubType) value);
            } else if (value instanceof ElementalAttribut){
                /**
                 * Change inspected Card Element.
                 */
                cardPreview.switchEAttribut((ElementalAttribut) value);
            }
        }
    }

    @Override
    protected void onSpinnerChange(String sTrigger, int currentIndex) {
        if(sTrigger.equals("Cost")){
            /**
             * Change the hover cost value.
             */
            cardPreview.switchCost(currentIndex);
        }
    }

    @Override
    protected void onButtonTrigger(int index) {
        switch(index){
            case 0:
//                addChild(new GeneratorSubMenu(screen, this, (SubType)getFieldValue("Card Type")));
            case 1:
                /**
                 * Activate the cards.
                 */
        }
    }

    @Override
    protected void onTextFieldInput(String input) {
        cardPreview.switchName(input);
    }
    
}

package gamemode.editor.card;

import entitysystem.ability.AbilityComponent;
import entitysystem.attribut.Faction;
import entitysystem.attribut.CardType;
import static entitysystem.attribut.CardType.ABILITY;
import static entitysystem.attribut.CardType.EQUIPEMENT;
import static entitysystem.attribut.CardType.SPELL;
import static entitysystem.attribut.CardType.SUMMON;
import static entitysystem.attribut.CardType.TRAP;
import entitysystem.card.CardProperties;
import entitysystem.loader.EntityLoader;
import gamemode.gui.EditorWindow;
import tonegod.gui.controls.buttons.ButtonAdapter;
import tonegod.gui.controls.lists.SelectBox;
import tonegod.gui.controls.lists.Spinner;
import tonegod.gui.core.Element;
import tonegod.gui.core.ElementManager;
import tonegod.gui.core.Screen;
import utility.ElementalAttribut;

/**
 *
 * @author roah
 */
public final class CardEditorWindow extends EditorWindow {

    private boolean init = false;
    private CardPreview cardPreview;
    private GeneratorSubWindow currentSubMenu;

    public CardEditorWindow(ElementManager screen, Element parent) {
        super(screen, parent, "Card Edition", Align.Horizontal, 2);
        /**
         * Part used to show/set the card Name.
         */
        addEditableTextField("Name", "TuxDoll", HAlign.left);
        getTextField("Name").setMaxLength(13);
        /**
         * Part used to show/choose the cost needed to use the card.
         */
        addSpinnerField("Cost", new int[]{0, 20, 1, 0}, HAlign.left);
        /**
         * Part used to show/choose the card faction.
         */
        addEditableSelectionField("Faction", Faction.NEUTRAL, Faction.values(), HAlign.left);
        /**
         * Part used to show/choose the card Type.
         */
        addEditableSelectionField("Card Type", CardType.SUMMON, CardType.values(), HAlign.left);
        /**
         * Part used to show/choose the card Element Attribut.
         */
        addEditableSelectionField("E.Attribut", ElementalAttribut.NULL, ElementalAttribut.values(), HAlign.left);
        addSpace();
        /**
         * Part used to show/set the card Description text.
         */
        addEditableTextField("Description", "This is a Testing unit", HAlign.left, 2);
        /**
         * 
         */
        addButtonList("additionalField", new String[] {"Load", "Save", "SubType Properties", "Hide Preview"}, HAlign.full, 2);
        
        showConstrainToParent(VAlign.bottom, HAlign.left);
        cardPreview = new CardPreview((Screen) screen, getWindow());
        init = true;
    }

    @Override
    protected void onSelectBoxFieldChange(Enum value) {
        if(init){
            if (value instanceof Faction) {
                /**
                 * Change inspected Card Faction.
                 */
                cardPreview.switchFaction((Faction) value);
            } else if (value instanceof CardType) {
                /**
                 * Change inspected Card CardType.
                 */
                cardPreview.switchSubType((CardType) value);
            } else if (value instanceof ElementalAttribut) {
                /**
                 * Change inspected Card Element.
                 */
                cardPreview.switchEAttribut((ElementalAttribut) value);
            }
        }
    }

    @Override
    protected void onSpinnerChange(String sTrigger, int currentIndex) {
        if (sTrigger.equals("Cost")) {
            /**
             * Change the hover cost value.
             */
            cardPreview.switchCost(currentIndex);
        }
    }

    @Override
    protected void onButtonTrigger(String triggerName) {
        if (triggerName.equals("Load")) {
        } else if (triggerName.equals("Save")) {
        } else if (triggerName.equals("Hide Preview")) {
            cardPreview.getPreview().hide();
            getButtonListButton("additionalField", "Hide Preview").setText("Show Preview");
        } else if (triggerName.equals("Show Preview")) {
            cardPreview.getPreview().show();
            getButtonListButton("additionalField", "Hide Preview").setText("Hide Preview");
        } else if (triggerName.equals("SubType Properties")) {
        }
    }

    @Override
    protected void onTextFieldInput(String UID, String input, boolean isTrigger) {
        if(UID.equals("Name")){
            cardPreview.switchName(input);
        } else if(UID.equals("Description")){
        }
    }

    void loadProperties(String cardName, CardProperties properties, EntityLoader loader) {
        /**
         * Load the card Name.
         */
        getTextField("Name").setText(cardName);
        /**
         * Load the card cost.
         */
        Spinner cost = getSpinnerField("Cost");
        cost.setSelectedIndex(properties.getPlayCost());
        cardPreview.switchCost(properties.getPlayCost());
        /**
         * Load the element Attribut.
         */
        SelectBox box = getSelectBoxField("E.Attribut");
        box.setSelectedByValue(properties.getElement(), true);
//        cardPreview.switchEAttribut(properties.getElement());
        /**
         * Load the Faction Attribut.
         */
        box = getSelectBoxField("Faction");
        box.setSelectedByValue(properties.getFaction(), true);
//        cardPreview.switchFaction(properties.getFaction());
        /**
         * Load the description Text.
         */
        getTextField("Description").setText(properties.getDescription());
        /**
         * Load the card type.
         */
        box = getSelectBoxField("Card Type");
        box.setSelectedByValue(properties.getCardSubType(), true);
        onButtonTrigger("Edit");
        switch (properties.getCardSubType()) {
            case ABILITY:
                /**
                 * @todo: Load ability SubMenu Data.
                 */
                AbilityComponent abilityData = loader.loadAbility(cardName);
                currentSubMenu.setPower(abilityData.getPower());
                currentSubMenu.setUnitSegmentCost(abilityData.getSegment());
                currentSubMenu.setActivationRange(abilityData.getActivationRange());
                currentSubMenu.setCastFromSelf(abilityData.isCastFromSelf());
                //Todo: collisionhit
                break;
            case EQUIPEMENT:
                /**
                 * @todo: Load Equipement SubMenu Data.
                 */
                break;
            case SPELL:
                /**
                 * @todo: Load Spell SubMenu Data.
                 */
                break;
            case TRAP:
                /**
                 * @todo: Load Trap SubMenu Data.
                 */
                break;
            case SUMMON:
                /**
                 * @todo: Load Summon SubMenu Data
                 */
                break;
        }
    }
}
package gamemode.editor.card;

import com.jme3.math.Vector2f;
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
import tonegod.gui.controls.lists.SelectBox;
import tonegod.gui.controls.lists.Spinner;
import tonegod.gui.controls.text.TextField;
import tonegod.gui.core.Element;
import tonegod.gui.core.ElementManager;
import tonegod.gui.core.Screen;
import utility.ElementalAttribut;

/**
 *
 * @author roah
 */
public final class GeneratorWindow extends EditorWindow {

    private boolean initialized = false;
    private CardPreview cardPreview;
    private GeneratorSubWindow currentSubMenu;
    private final CardEditorLoaderWindow saveAndLoadGUI;

    public GeneratorWindow(ElementManager screen, Element parent) {
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
        /**
         * Part used to test out card.
         */
        addButtonField("SubType Properties", HAlign.full);
        /**
         * Part used to show/set the card Description text.
         */
        addEditableTextField("Description", "This is a Testing unit", HAlign.left, 2);
        /**
         *
         */
//        showConstrainToParent(new Vector2f(3, 6.5f), null, HAlign.right);
        show(null, null);
        cardPreview = new CardPreview((Screen) screen, getWindow());
        saveAndLoadGUI = new CardEditorLoaderWindow((Screen) screen, cardPreview.getPreview(), this);
        initialized = true;
    }

    @Override
    protected void onSelectBoxFieldChange(Enum value) {
        if (initialized) {
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
        if (triggerName.equals("Cast")) {
            /**
             * Activate the cards.
             */
        } else if (triggerName.equals("Edit")) {
//                if (currentSubMenu != null && !currentSubMenu.getCurrent().equals((CardType) getFieldValue("Card Type"))) {
//                    currentSubMenu.removeFromScreen();
//                } else if (currentSubMenu != null && currentSubMenu.getCurrent().equals((CardType) getFieldValue("Card Type"))) {
//                    return;
//                }
//                currentSubMenu = new GeneratorSubWindow(screen, cardPreview.getPreview(), (CardType) getFieldValue("Card Type"));
        }
    }

    @Override
    protected void onTextFieldInput(String UID, String input, boolean isTrigger) {
        if(UID.equals("Name")){
            cardPreview.switchName(input);
        } else if(UID.equals("Description")){
            return;
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

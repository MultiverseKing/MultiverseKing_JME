package gamemode.editor.card;

import com.jme3.math.Vector2f;
import entitysystem.ability.AbilityComponent;
import entitysystem.attribut.Faction;
import entitysystem.attribut.SubType;
import static entitysystem.attribut.SubType.ABILITY;
import static entitysystem.attribut.SubType.EQUIPEMENT;
import static entitysystem.attribut.SubType.SPELL;
import static entitysystem.attribut.SubType.SUMMON;
import static entitysystem.attribut.SubType.TRAP;
import entitysystem.card.CardProperties;
import entitysystem.loader.EntityLoader;
import gamemode.editor.EditorMenuWindow;
import tonegod.gui.controls.lists.SelectBox;
import tonegod.gui.controls.lists.Spinner;
import tonegod.gui.core.Element;
import tonegod.gui.core.ElementManager;
import tonegod.gui.core.Screen;
import tonegod.gui.core.layouts.LayoutHint.VAlign;
import utility.ElementalAttribut;

/**
 *
 * @author roah
 */
public final class GeneratorMenu extends EditorMenuWindow {

    private boolean initialized = false;
    private CardPreview cardPreview;
    private GeneratorSubMenu currentSubMenu;
    private final SaveAndLoadGUI saveAndLoadGUI;

    public GeneratorMenu(ElementManager screen, Element parent) {
        super(screen, parent, "Card Generator");
        /**
         * Part used to show/set the card Name.
         */
        addEditableTextField("Name", "TuxDoll", Vector2f.ZERO);
        /**
         * Part used to show/choose the cost needed to use the card.
         */
        addSpinnerField("Cost", new int[]{0, 20, 1, 0}, new Vector2f(getGridSize().x + 50, 0));
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
        addEditableSelectionField("E.Attribut", ElementalAttribut.NULL, ElementalAttribut.values(), new Vector2f(getGridSize().x * 2, getGridSize().y));
        /**
         * Part used to show/set the card Description text.
         */
        addEditableTextField("Description", "This is a Testing unit", new Vector2f(0, getGridSize().y * 2 + 25));
        Element el = getTextField("Description");
        el.setPosition(el.getPosition().x - 75, el.getPosition().y);
        el.setWidth(getGridSize().x * 2);
        /**
         * Part used to test out card.
         */
        addButtonField("Test Card", "Cast", 1, new Vector2f(0, getGridSize().y * 3.5f));
        /**
         * Part used to test out card.
         */
        addButtonField("SubType Properties", "Edit", 0, new Vector2f(getGridSize().x, getGridSize().y * 3.5f));
        /**
         *
         */
        show(getGridSize().x * 3, getGridSize().y * 6.5f, VAlign.center);
        cardPreview = new CardPreview((Screen) screen, getWindow());
        saveAndLoadGUI = new SaveAndLoadGUI((Screen) screen, cardPreview.getPreview(), this);
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
            } else if (value instanceof SubType) {
                /**
                 * Change inspected Card SubType.
                 */
                cardPreview.switchSubType((SubType) value);
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
    protected void onButtonTrigger(int index) {
        switch (index) {
            case 0:
                if (currentSubMenu != null && !currentSubMenu.getCurrent().equals((SubType) getFieldValue("Card Type"))) {
                    currentSubMenu.detachFromParent();
                } else if (currentSubMenu != null && currentSubMenu.getCurrent().equals((SubType) getFieldValue("Card Type"))) {
                    return;
                }
                currentSubMenu = new GeneratorSubMenu(screen, cardPreview.getPreview(), (SubType) getFieldValue("Card Type"));
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

    public void loadProperties(String cardName, CardProperties properties, EntityLoader loader) {
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
        onButtonTrigger(0);
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

package editor.card;

import com.jme3.app.SimpleApplication;
import com.jme3.math.Vector2f;
import entitysystem.ability.AbilityComponent;
import entitysystem.attribut.Rarity;
import entitysystem.card.AbilityProperties;
import entitysystem.card.CardProperties;
import entitysystem.loader.EntityLoader;
import entitysystem.loader.GameProperties;
import entitysystem.render.RenderComponent.RenderType;
import gui.DialogWindow;
import gui.DialogWindowListener;
import gui.EditorWindow;
import org.hexgridapi.utility.ElementalAttribut;
import tonegod.gui.controls.lists.SelectBox;
import tonegod.gui.controls.lists.Spinner;
import tonegod.gui.controls.menuing.Menu;
import tonegod.gui.core.Element;
import tonegod.gui.core.Screen;

/**
 *
 * @author roah
 */
public final class CardEditorWindow extends EditorWindow implements DialogWindowListener {

    private boolean init = false;
    private CardPreview cardPreview;
    private CardEditorProperties subMenu;
    private DialogWindow dialPopup;

    public CardEditorWindow(Screen screen, Element parent) {
        super(screen, parent, "Card Edition", Align.Horizontal, 2);
        CardProperties properties = new CardProperties("TuxDoll", "Undefined", 0, RenderType.Unit,
                Rarity.COMMON, ElementalAttribut.ICE, "This is a Testing unit");
        /**
         * Part used to show/set the card Name.
         */
        addTextField("Name", properties.getName(), HAlign.left);
        getTextField("Name").setMaxLength(13);
        /**
         * Part used to show/choose the cost needed to use the card.
         */
        addSpinnerField("Cost", new int[]{0, 20, 1, 0}, HAlign.left);
        /**
         * Part used to show/choose the card Type.
         */
        addSelectionField("Card Type", properties.getRenderType(), RenderType.values(), HAlign.left);
        /**
         * Part used to show/choose the card Element Attribut.
         */
        addSelectionField("E.Attribut", properties.getElement(), ElementalAttribut.values(), HAlign.left);
        /**
         * Part used to show/choose the card Rarity.
         */
        addSelectionField("Rarity", properties.getRarity(), Rarity.values(), HAlign.left);
        addSpace();
        /**
         * Part used to show/set the card Description text.
         */
        addTextField("Description", properties.getDescription(), HAlign.left, 2);
        /**
         *
         */
        addButtonList("additionalField", new String[]{"Load", "Save", "SubType Properties", "Hide Preview"}, HAlign.full, 2);

        showConstrainToParent(VAlign.bottom, HAlign.left);
        cardPreview = new CardPreview((Screen) screen, getWindow(), properties);
        init = true;
    }

    @Override
    protected void onSelectBoxFieldChange(Enum value) {
        if (init) {
            if (value instanceof RenderType) {
                /**
                 * Change inspected Card CardType.
                 */
                cardPreview.switchSubType((RenderType) value);
            } else if (value instanceof ElementalAttribut) {
                /**
                 * Change inspected Card Element.
                 */
                cardPreview.switchEAttribut((ElementalAttribut) value);
            } else if (value instanceof Rarity) {
                cardPreview.switchRarity((Rarity) value);
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
        switch (triggerName) {
            case "Load":
                if (((Screen) screen).getElementsAsMap().containsKey("loadCategory")) {
                    clearLoadingMenu();
                }
                openLoadingMenu();
                Element btn = getLabelListField("additionalField", "Load");
                Menu loadMenu = (Menu) screen.getElementById("loadCategory");
                loadMenu.showMenu(null, btn.getAbsoluteX(),
                        btn.getAbsoluteY() - loadMenu.getHeight());
                break;
            case "Save":
                saveCardProperties(false);
                break;
            case "Hide Preview":
                cardPreview.getPreview().hide();
                getLabelListField("additionalField", "Hide Preview").setText("Show Preview");
                break;
            case "Show Preview":
                cardPreview.getPreview().show();
                getLabelListField("additionalField", "Hide Preview").setText("Hide Preview");
                break;
            case "SubType Properties":
                if (subMenu == null) {
                    subMenu = new CardEditorProperties(screen, getWindow(), (RenderType) getSelectBoxField("Card Type").getSelectedListItem().getValue());
                } else if (subMenu.isVisible()) {
                    subMenu.hide();
                } else {
                    subMenu.setVisible();
                }
                break;
        }
    }

    @Override
    public void onPressCloseAndHide() {
    }

    @Override
    protected void onTextFieldInput(String UID, String input, boolean isTrigger) {
        if (UID.equals("Name")) {
            cardPreview.switchName(input);
        } else if (UID.equals("Description")) {
        }
    }

    private void loadProperties(String cardName, CardProperties properties, EntityLoader loader) {
        /**
         * Load the card Name && img.
         */
        getTextField("Name").setText(cardName);
        cardPreview.switchName(cardName);
        cardPreview.switchImg(properties.getVisual());
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
        /**
         * Load the description Text.
         */
        getTextField("Description").setText(properties.getDescription());
        /**
         * Load the card type.
         */
        box = getSelectBoxField("Card Type");
        box.setSelectedByValue(properties.getRenderType(), true);
        onButtonTrigger("Edit");
        if (subMenu == null) {
            subMenu = new CardEditorProperties(screen, getWindow(), (RenderType) getSelectBoxField("Card Type").getSelectedListItem().getValue());
        } else if (!subMenu.getCurrent().equals(properties.getRenderType())) {
            subMenu.removeFromScreen();
            subMenu = new CardEditorProperties(screen, getWindow(), (RenderType) getSelectBoxField("Card Type").getSelectedListItem().getValue());
            subMenu.setVisible();
        }
        switch (properties.getRenderType()) {
            case Ability:
                AbilityComponent abilityData = loader.loadAbility(cardName);
                subMenu.setPower(abilityData.getPower());
                subMenu.setUnitSegmentCost(abilityData.getSegment());
                subMenu.setCastRange(abilityData.getCastRange());
                subMenu.setHitCollision(abilityData.getHitCollision());
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
                throw new UnsupportedOperationException(properties.getRenderType() + " Is not currently a supported type.");
        }
    }

    private void openLoadingMenu() {
        /**
         * Some variable to get files name.
         */
        GameProperties properties = GameProperties.getInstance(screen.getApplication().getAssetManager());

        /**
         * Menu listing all saved card.
         */
        Menu categoryAll = new Menu(screen, "categoryAll", Vector2f.ZERO, false) {
            @Override
            public void onMenuItemClicked(int index, Object value, boolean isToggled) {
                loadCardPropertiesFromFile(value.toString());
            }
        };
        addMenuItem(properties, categoryAll);
        screen.addElement(categoryAll);

        /**
         * Menu listing all saved Ability card.
         */
        Menu categoryAbility = new Menu(screen, "categoryAbility", Vector2f.ZERO, false) {
            @Override
            public void onMenuItemClicked(int index, Object value, boolean isToggled) {
                loadCardPropertiesFromFile(value.toString());
            }
        };
        addMenuItem(properties, categoryAbility);
        screen.addElement(categoryAbility);

        /**
         * Menu listing all saved unit card.
         */
        Menu categorySummon = new Menu(screen, "categoryUnit", Vector2f.ZERO, false) {
            @Override
            public void onMenuItemClicked(int index, Object value, boolean isToggled) {
                loadCardPropertiesFromFile(value.toString());
            }
        };
        addMenuItem(properties, categorySummon);
        screen.addElement(categorySummon);

        /**
         * Menu listing all saved equipment card.
         */
        Menu categoryEquipement = new Menu(screen, "categoryEquipement", Vector2f.ZERO, false) {
            @Override
            public void onMenuItemClicked(int index, Object value, boolean isToggled) {
                loadCardPropertiesFromFile(value.toString());
            }
        };
        addMenuItem(properties, categoryEquipement);
        screen.addElement(categoryEquipement);

        /**
         * Menu listing all saved Titan card.
         */
        Menu categoryTitan = new Menu(screen, "categoryTitan", Vector2f.ZERO, false) {
            @Override
            public void onMenuItemClicked(int index, Object value, boolean isToggled) {
                loadCardPropertiesFromFile(value.toString());
            }
        };
        addMenuItem(properties, categoryTitan);
        screen.addElement(categoryTitan);

        /**
         * Root menu.
         */
        final Menu loadCategory = new Menu(screen, "loadCategory", Vector2f.ZERO, false) {
            @Override
            public void onMenuItemClicked(int index, Object value, boolean isToggled) {
            }
        };
        loadCategory.addMenuItem("All", null, categoryAll);
        loadCategory.addMenuItem("Unit", null, categorySummon);
        loadCategory.addMenuItem("Ability", null, categoryAbility);
        loadCategory.addMenuItem("Equipement", null, categoryEquipement);
        loadCategory.addMenuItem("Titan", null, categoryTitan);

        screen.addElement(loadCategory);
    }

    private void addMenuItem(GameProperties properties, Menu menu) {
        switch (menu.getUID()) {
            case "categoryAll":
                addAbilityItem(properties, menu);
                addEquipementItem(properties, menu);
                addUnitItem(properties, menu);
                addTitanItem(properties, menu);
                break;
            case "categoryAbility":
                addAbilityItem(properties, menu);
                break;
            case "categoryEquipement":
                addEquipementItem(properties, menu);
                break;
            case "categoryUnit":
                addUnitItem(properties, menu);
                break;
            case "categoryTitan":
                addTitanItem(properties, menu);
                break;
        }
    }

    private void addAbilityItem(GameProperties properties, Menu menu) {
        for (String s : properties.getAbilityList()) {
            menu.addMenuItem(s, s + "." + RenderType.Ability, null);
        }
    }

    private void addEquipementItem(GameProperties properties, Menu menu) {
        for (String s : properties.getEquipementList()) {
            menu.addMenuItem(s, s + "." + RenderType.Equipement, null);
        }
    }

    private void addUnitItem(GameProperties properties, Menu menu) {
        for (String s : properties.getUnitList()) {
            menu.addMenuItem(s, s + "." + RenderType.Unit, null);
        }
    }

    private void addTitanItem(GameProperties properties, Menu menu) {
        for (String s : properties.getTitanList()) {
            menu.addMenuItem(s, s + "." + RenderType.Titan, null);
        }
    }

    private void clearLoadingMenu() {
        ((Screen) screen).removeElement(screen.getElementById("loadCategory"));
        ((Screen) screen).removeElement(screen.getElementById("categoryTitan"));
        ((Screen) screen).removeElement(screen.getElementById("categoryEquipement"));
        ((Screen) screen).removeElement(screen.getElementById("categoryUnit"));
        ((Screen) screen).removeElement(screen.getElementById("categoryAbility"));
        ((Screen) screen).removeElement(screen.getElementById("categoryAll"));
    }

    private void loadCardPropertiesFromFile(String value) {
        EntityLoader loader = new EntityLoader((SimpleApplication) screen.getApplication());
        String[] cardName = value.split("\\.");
        loadProperties(cardName[0], loader.loadCardProperties(cardName[0], RenderType.valueOf(cardName[1])), loader);
    }

    private void saveCardProperties(boolean override) {
        if (subMenu != null && subMenu.isVisible()) {
            CardProperties card = new CardProperties(getTextField("Name").getText(), cardPreview.getVisual(),
                    getSpinnerField("Cost").getSelectedIndex(),
                    (RenderType) getSelectBoxField("Card Type").getSelectedListItem().getValue(),
                    (Rarity) getSelectBoxField("Rarity").getSelectedListItem().getValue(),
                    (ElementalAttribut) getSelectBoxField("E.Attribut").getSelectedListItem().getValue(),
                    getTextField("Description").getText());
            EntityLoader loader = new EntityLoader((SimpleApplication) screen.getApplication());
            switch (card.getRenderType()) {
                case Ability:
                    if (!loader.saveAbility(new AbilityProperties(card, subMenu.getProperties()), override) && !override) {
                        if (dialPopup != null) {
                            dialPopup.setVisible();
                        } else {
                            dialPopup = new DialogWindow(screen, "Override File", this);
                            dialPopup.showText(card.getName() + " already exist, override ?");
                            dialPopup.show(true);
                        }
                    }
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
                    throw new UnsupportedOperationException(card.getRenderType() + "Is not a supported type.");
            }
        } else {
            /**
             * Open dialogbox "nothing to save"
             */
        }
    }

    @Override
    public void onDialogTrigger(String dialogUID, boolean confirmOrCancel) {
        if (dialogUID.equals("Override File") && confirmOrCancel) {
            saveCardProperties(true);
        }
        dialPopup.hide();
    }
}

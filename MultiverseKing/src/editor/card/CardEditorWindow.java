package editor.card;

import com.jme3.math.Vector2f;
import entitysystem.ability.AbilityComponent;
import entitysystem.attribut.CardType;
import static entitysystem.attribut.CardType.ABILITY;
import static entitysystem.attribut.CardType.EQUIPEMENT;
import static entitysystem.attribut.CardType.SUMMON;
import entitysystem.attribut.Rarity;
import entitysystem.card.AbilityProperties;
import entitysystem.card.CardProperties;
import entitysystem.loader.EntityLoader;
import gui.DialogWindow;
import gui.DialogWindowListener;
import gui.EditorWindow;
import java.io.File;
import java.io.FilenameFilter;
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
        CardProperties properties = new CardProperties("TuxDoll", "Undefined", 0, CardType.SUMMON,
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
        addSelectionField("Card Type", properties.getCardType(), CardType.values(), HAlign.left);
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
            if (value instanceof CardType) {
                /**
                 * Change inspected Card CardType.
                 */
                cardPreview.switchSubType((CardType) value);
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
        if (triggerName.equals("Load")) {
            if (((Screen) screen).getElementsAsMap().containsKey("loadCategory")) {
                clearLoadingMenu();
            }
            openLoadingMenu();
            Element btn = getLabelListField("additionalField", "Load");
            Menu loadMenu = (Menu) screen.getElementById("loadCategory");
            loadMenu.showMenu(null, btn.getAbsoluteX(),
                    btn.getAbsoluteY() - loadMenu.getHeight());
        } else if (triggerName.equals("Save")) {
            saveCardProperties(false);
        } else if (triggerName.equals("Hide Preview")) {
            cardPreview.getPreview().hide();
            getLabelListField("additionalField", "Hide Preview").setText("Show Preview");
        } else if (triggerName.equals("Show Preview")) {
            cardPreview.getPreview().show();
            getLabelListField("additionalField", "Hide Preview").setText("Hide Preview");
        } else if (triggerName.equals("SubType Properties")) {
            if (subMenu == null) {
                subMenu = new CardEditorProperties(screen, getWindow(), (CardType) getSelectBoxField("Card Type").getSelectedListItem().getValue());
            } else if (subMenu.isVisible()) {
                subMenu.hide();
            } else {
                subMenu.setVisible();
            }
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
        box.setSelectedByValue(properties.getCardType(), true);
        onButtonTrigger("Edit");
        if (subMenu == null) {
            subMenu = new CardEditorProperties(screen, getWindow(), (CardType) getSelectBoxField("Card Type").getSelectedListItem().getValue());
        } else if (!subMenu.getCurrent().equals(properties.getCardType())) {
            subMenu.removeFromScreen();
            subMenu = new CardEditorProperties(screen, getWindow(), (CardType) getSelectBoxField("Card Type").getSelectedListItem().getValue());
            subMenu.setVisible();
        }
        switch (properties.getCardType()) {
            case ABILITY:
                AbilityComponent abilityData = loader.loadAbility(cardName);
                subMenu.setPower(abilityData.getPower());
                subMenu.setUnitSegmentCost(abilityData.getSegment());
                subMenu.setCastRange(abilityData.getCastRange());
                subMenu.setHitCollision(abilityData.getHitCollision());
                break;
            case EQUIPEMENT:
                /**
                 * @todo: Load Equipement SubMenu Data.
                 */
                break;
            case SUMMON:
                /**
                 * @todo: Load Summon SubMenu Data
                 */
                break;
            case TITAN:
                /**
                 * @todo: Load Spell SubMenu Data.
                 */
                break;
        }
    }

    private void openLoadingMenu() {
        /**
         * Some variable to get files name.
         */
        File folder;
        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return (name.endsWith(".card"));
            }
        };

        /**
         * Menu listing all saved card.
         */
//        folder = new File(System.getProperty("user.dir") + "/assets/Data/CardData/");
        folder = new File("/assets/Data/CardData/");
        Menu categoryAll = new Menu(screen, "categoryAll", new Vector2f(0, 0), false) {
            @Override
            public void onMenuItemClicked(int index, Object value, boolean isToggled) {
                loadCardPropertiesFromFile(value.toString());
            }
        };

        File[] fList = folder.listFiles();
        for (File f : fList) {
            if (f.isDirectory()) {
                String[] subF = f.list(filter);
                for (String fi : subF) {
                    int index = fi.lastIndexOf('.');
                    categoryAll.addMenuItem(fi.substring(0, index), fi.substring(0, index), null);
                }
            }
        }
        screen.addElement(categoryAll);

        /**
         * Menu listing all saved Ability card.
         */
//        folder = new File(System.getProperty("user.dir") + "/assets/Data/CardData/Ability");
        folder = new File("/assets/Data/CardData/Ability");
        Menu categoryAbility = new Menu(screen, "categoryAbility", new Vector2f(0, 0), false) {
            @Override
            public void onMenuItemClicked(int index, Object value, boolean isToggled) {
                loadCardPropertiesFromFile(value.toString());
            }
        };

        String[] abilityList = folder.list(filter);
        for (String s : abilityList) {
            int index = s.lastIndexOf('.');
            categoryAbility.addMenuItem(s.substring(0, index), s.substring(0, index), null);
        }
        screen.addElement(categoryAbility);

        /**
         * Menu listing all saved unit card.
         */
//        folder = new File(System.getProperty("user.dir") + "/assets/Data/CardData/Summon");
        folder = new File("/assets/Data/CardData/Summon");
        Menu categorySummon = new Menu(screen, "categorySummon", new Vector2f(0, 0), false) {
            @Override
            public void onMenuItemClicked(int index, Object value, boolean isToggled) {
                loadCardPropertiesFromFile(value.toString());
            }
        };

        String[] unitList = folder.list(filter);
        for (String s : unitList) {
            int index = s.lastIndexOf('.');
            categorySummon.addMenuItem(s.substring(0, index), s.substring(0, index), null);
        }
        screen.addElement(categorySummon);

        /**
         * Menu listing all saved equipment card.
         */
//        folder = new File(System.getProperty("user.dir") + "/assets/Data/CardData/Equipement");
        folder = new File("/assets/Data/CardData/Equipement");
        Menu categoryEquipement = new Menu(screen, "categoryEquipement", new Vector2f(0, 0), false) {
            @Override
            public void onMenuItemClicked(int index, Object value, boolean isToggled) {
                loadCardPropertiesFromFile(value.toString());
            }
        };

        String[] equipement = folder.list(filter);
        for (String s : equipement) {
            int index = s.lastIndexOf('.');
            categoryEquipement.addMenuItem(s.substring(0, index), s.substring(0, index), null);
        }
        screen.addElement(categoryEquipement);

        /**
         * Menu listing all saved equipment card.
         */
//        folder = new File(System.getProperty("user.dir") + "/assets/Data/CardData/Titan");
        folder = new File("/assets/Data/CardData/Titan");
        Menu categoryTitan = new Menu(screen, "categoryTitan", new Vector2f(0, 0), false) {
            @Override
            public void onMenuItemClicked(int index, Object value, boolean isToggled) {
                loadCardPropertiesFromFile(value.toString());
            }
        };

        String[] titan = folder.list(filter);
        for (String s : titan) {
            int index = s.lastIndexOf('.');
            categoryTitan.addMenuItem(s.substring(0, index), s.substring(0, index), null);
        }
        screen.addElement(categoryTitan);

        /**
         * Root menu.
         */
        final Menu loadCategory = new Menu(screen, "loadCategory", new Vector2f(0, 0), false) {
            @Override
            public void onMenuItemClicked(int index, Object value, boolean isToggled) {
            }
        };
        loadCategory.addMenuItem("All", null, categoryAll);
        loadCategory.addMenuItem("Summon", null, categorySummon);
        loadCategory.addMenuItem("Ability", null, categoryAbility);
        loadCategory.addMenuItem("Equipement", null, categoryEquipement);
        loadCategory.addMenuItem("Titan", null, categoryTitan);

        screen.addElement(loadCategory);
    }

    private void clearLoadingMenu() {
        ((Screen) screen).removeElement(screen.getElementById("loadCategory"));
        ((Screen) screen).removeElement(screen.getElementById("categoryTitan"));
        ((Screen) screen).removeElement(screen.getElementById("categoryEquipement"));
        ((Screen) screen).removeElement(screen.getElementById("categorySummon"));
        ((Screen) screen).removeElement(screen.getElementById("categoryAbility"));
        ((Screen) screen).removeElement(screen.getElementById("categoryAll"));
    }

    private void loadCardPropertiesFromFile(String cardName) {
        EntityLoader loader = new EntityLoader();
        CardProperties load = loader.loadCardProperties(cardName);
        if (load != null) {
            loadProperties(cardName, load, loader);
        }
    }

    private void saveCardProperties(boolean override) {
        if (subMenu != null && subMenu.isVisible()) {
            CardProperties card = new CardProperties(getTextField("Name").getText(), cardPreview.getVisual(),
                    getSpinnerField("Cost").getSelectedIndex(),
                    (CardType) getSelectBoxField("Card Type").getSelectedListItem().getValue(),
                    (Rarity) getSelectBoxField("Rarity").getSelectedListItem().getValue(),
                    (ElementalAttribut) getSelectBoxField("E.Attribut").getSelectedListItem().getValue(),
                    getTextField("Description").getText());
            EntityLoader loader = new EntityLoader();
            switch (card.getCardType()) {
                case ABILITY:
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
                case EQUIPEMENT:
                    break;
                case SUMMON:
                    break;
                case TITAN:
                    break;
            }
        } else {
            /**
             * Open dialogbox "nothing to save"
             */
        }
    }

    public void onDialogTrigger(String dialogUID, boolean confirmOrCancel) {
        if (dialogUID.equals("Override File") && confirmOrCancel) {
            saveCardProperties(true);
        }
        dialPopup.hide();
    }
}

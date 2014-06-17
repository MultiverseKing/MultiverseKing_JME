package gamemode.editor.cardgui;

import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector2f;
import entitysystem.ability.AbilityComponent;
import static entitysystem.attribut.SubType.ABILITY;
import static entitysystem.attribut.SubType.EQUIPEMENT;
import static entitysystem.attribut.SubType.SPELL;
import static entitysystem.attribut.SubType.SUMMON;
import static entitysystem.attribut.SubType.TRAP;
import entitysystem.card.CardProperties;
import entitysystem.loader.EntityLoader;
import java.io.File;
import java.io.FilenameFilter;
import tonegod.gui.controls.buttons.ButtonAdapter;
import tonegod.gui.controls.lists.Spinner;
import tonegod.gui.controls.menuing.Menu;
import tonegod.gui.controls.windows.Window;
import tonegod.gui.core.Element;

/**
 *
 * @author roah
 */
public class SaveAndLoadGUI {

    private void pupolate() {
        /**
         * Window used to load and save card to/from file.
         */
        Window cardLoader = new Window(main.getScreen(), "cardLoader",
                new Vector2f(55, genMenu.getHeight()), new Vector2f(225, 50));
        cardLoader.removeAllChildren();
        cardLoader.setIgnoreMouse(true);
        genMenu.addChild(cardLoader);

        ButtonAdapter load = new ButtonAdapter(main.getScreen(), "loadCardButton", new Vector2f(10, 10)) {
            @Override
            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                super.onButtonMouseLeftUp(evt, toggled);
                if (!main.getScreen().getElementsAsMap().containsKey("loadCategory")) {
                    openLoadingMenu();
                }
                Menu loadMenu = (Menu) main.getScreen().getElementById("loadCategory");
                loadMenu.showMenu(null, getAbsoluteX(), getAbsoluteY() - loadMenu.getHeight());
            }
        };
        load.setText("Load");
        cardLoader.addChild(load);

        ButtonAdapter save = new ButtonAdapter(main.getScreen(), "saveCardButton", new Vector2f(115, 10)) {
            @Override
            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                super.onButtonMouseLeftUp(evt, toggled);
                System.err.println("Not implemented");
            }
        };
        save.setText("Save");
        cardLoader.addChild(save);
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
        folder = new File(System.getProperty("user.dir") + "/assets/Data/CardData/");
        Menu categoryAll = new Menu(main.getScreen(), "categoryAll", new Vector2f(0, 0), false) {
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
        main.getScreen().addElement(categoryAll);

        /**
         * Menu listing all saved Ability card.
         */
        folder = new File(System.getProperty("user.dir") + "/assets/Data/CardData/Ability");
        Menu categoryAbility = new Menu(main.getScreen(), "categoryAbility", new Vector2f(0, 0), false) {
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
        main.getScreen().addElement(categoryAbility);

        /**
         * Menu listing all saved unit card.
         */
        folder = new File(System.getProperty("user.dir") + "/assets/Data/CardData/Units");
        Menu categoryUnit = new Menu(main.getScreen(), "categoryUnit", new Vector2f(0, 0), false) {
            @Override
            public void onMenuItemClicked(int index, Object value, boolean isToggled) {
                loadCardPropertiesFromFile(value.toString());
            }
        };

        String[] unitList = folder.list(filter);
        for (String s : unitList) {
            int index = s.lastIndexOf('.');
            categoryUnit.addMenuItem(s.substring(0, index), s.substring(0, index), null);
        }
        main.getScreen().addElement(categoryUnit);

        /**
         * Root menu.
         */
        final Menu loadCategory = new Menu(main.getScreen(), "loadCategory", new Vector2f(0, 0), false) {
            @Override
            public void onMenuItemClicked(int index, Object value, boolean isToggled) {
            }
        };
        loadCategory.addMenuItem("All", null, categoryAll);
        loadCategory.addMenuItem("Unit", null, categoryUnit);
        loadCategory.addMenuItem("Ability", null, categoryAbility);

        main.getScreen().addElement(loadCategory);
    }

    private void loadCardPropertiesFromFile(String cardName) {
        System.out.println(cardName);
        EntityLoader loader = new EntityLoader();
        CardProperties load = loader.loadCardProperties(cardName);

        if (load != null) {
            /**
             * Load the card cost.
             */
            Spinner cost = (Spinner) main.getScreen().getElementById("generatorCostSpinner");
            cost.setSelectedIndex(load.getPlayCost());
            hover.setCastCost(load.getPlayCost());
            /**
             * Load the element Attribut.
             */
            main.getScreen().getElementById("generatorEAttributButton").setText(load.getElement().name());
            main.getScreen().getElementById("cardPropertiesHover").setColorMap("Textures/Cards/" + load.getElement().name() + ".png");
            /**
             * Load the Faction Attribut.
             */
            main.getScreen().getElementById("generatorFactionButton").setText(load.getFaction().name());
            hover.setFaction(load.getFaction());
            /**
             * Load the description Text.
             */
            main.getScreen().getElementById("generatorDescriptionButton").setText(load.getDescription());
            /**
             * Load the card type.
             */
            Element genButton = main.getScreen().getElementById("generatorCardTypeButton");
            if (!genButton.getText().equals(load.getCardSubType().name())) {
                genButton.setText(load.getCardSubType().name());
                hover.setType(load.getCardSubType());
                openCardTypeSubMenu(load.getCardSubType());
            }
            switch (load.getCardSubType()) {
                case ABILITY:
                    /**
                     * @todo: Load ability SubMenu Data.
                     */
                    AbilityComponent abilityData = loader.loadAbility(cardName);
                    main.getScreen().getElementById("powerButton").setText(Integer.toString(abilityData.getPower()));
                    Spinner spin = (Spinner) main.getScreen().getElementById("unitCastCostSlider");
                    spin.setSelectedIndex(abilityData.getSegment());
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
}

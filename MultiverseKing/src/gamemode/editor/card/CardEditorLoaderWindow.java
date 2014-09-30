package gamemode.editor.card;

import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector2f;
import entitysystem.card.CardProperties;
import entitysystem.loader.EntityLoader;
import java.io.File;
import java.io.FilenameFilter;
import tonegod.gui.controls.buttons.ButtonAdapter;
import tonegod.gui.controls.menuing.Menu;
import tonegod.gui.controls.windows.Window;
import tonegod.gui.core.Element;
import tonegod.gui.core.Screen;

/**
 *
 * @author roah
 */
class CardEditorLoaderWindow {

    private final CardEditorWindow generatorGUI;
    private Window cardLoader;
    private Screen screen;

    CardEditorLoaderWindow(Screen screen, Element parent, CardEditorWindow generatorGUI) {
        this.screen = screen;
        this.generatorGUI = generatorGUI;
        /**
         * Window used to load and save card to/from file.
         */
        cardLoader = new Window(screen, "cardLoader",
                new Vector2f(0, parent.getHeight()), new Vector2f(120, 85));
        cardLoader.removeAllChildren();
        cardLoader.setIgnoreMouse(true);
        parent.addChild(cardLoader);

        ButtonAdapter load = new ButtonAdapter(screen, "loadCardButton", new Vector2f(10, 10)) {
            @Override
            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                super.onButtonMouseLeftUp(evt, toggled);
                if (!((Screen) screen).getElementsAsMap().containsKey("loadCategory")) {
                    openLoadingMenu();
                }
                Menu loadMenu = (Menu) screen.getElementById("loadCategory");
                loadMenu.showMenu(null, getAbsoluteX(), getAbsoluteY() - loadMenu.getHeight());
            }
        };
        load.setText("Load");
        cardLoader.addChild(load);

        ButtonAdapter save = new ButtonAdapter(screen, "saveCardButton", new Vector2f(10, 45)) {
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
        folder = new File(System.getProperty("user.dir") + "/assets/Data/CardData/Ability");
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
        folder = new File(System.getProperty("user.dir") + "/assets/Data/CardData/Units");
        Menu categoryUnit = new Menu(screen, "categoryUnit", new Vector2f(0, 0), false) {
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
        screen.addElement(categoryUnit);

        /**
         * Root menu.
         */
        final Menu loadCategory = new Menu(screen, "loadCategory", new Vector2f(0, 0), false) {
            @Override
            public void onMenuItemClicked(int index, Object value, boolean isToggled) {
            }
        };
        loadCategory.addMenuItem("All", null, categoryAll);
        loadCategory.addMenuItem("Unit", null, categoryUnit);
        loadCategory.addMenuItem("Ability", null, categoryAbility);

        screen.addElement(loadCategory);
    }

    private void loadCardPropertiesFromFile(String cardName) {
        EntityLoader loader = new EntityLoader();
        CardProperties load = loader.loadCardProperties(cardName);
        if (load != null) {
            generatorGUI.loadProperties(cardName, load, loader);
        }
    }
}

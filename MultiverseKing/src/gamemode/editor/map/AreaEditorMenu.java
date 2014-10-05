package gamemode.editor.map;

import gamemode.editor.EditorMenu;
import gamemode.gui.LayoutWindow;
import kingofmultiverse.MultiverseMain;
import tonegod.gui.controls.windows.Window;
import tonegod.gui.core.Element;
import tonegod.gui.core.Screen;
import utility.HexCoordinate;

/**
 *
 * @author roah
 */
final class AreaEditorMenu extends EditorMenu {

    private LayoutWindow currentWindow = null;
    private int currentValue = 10;
    private Window tileWin;
    private AreaTileWidget tileWidgetMenu;

    AreaEditorMenu(Screen screen, AreaEditorSystem mapEditorSystem, Element parent) {
        super(screen, "RoomEditorMenu", "Room Editor", mapEditorSystem, parent);
        addAdditionalField("New Room");
        addAdditionalField("Load Room");
        populateEditor();
    }

    /**
     * Used to set the needed mode.
     *
     * @param index
     */
    @Override
    protected void onSelectedItemChange(int index) {
    }

    @Override
    protected void onAdditionalFieldTrigger(int value) {
        if (value != currentValue) {
            if (currentWindow != null && screen.getElementById(currentWindow.getWindow().getUID()) != null) {
//                screen.removeElement(currentWindow.getWindow());
                currentWindow.removeFromScreen();
            }
            switch (value) {
                case 0:
                    /**
                     * Generate a new empty room following the parameter.
                     */
                    currentWindow = new MapEditorLoaderWindow(screen, this);
                    currentValue = value;
                    break;
                case 1:
                    /**
                     * Load a room from files.
                     */
                    break;
                default:
                    throw new UnsupportedOperationException("Additional Field trigger not implemented : " + value);
            }
        } else if (currentWindow.getWindow().getIsVisible()) {
            currentWindow.getWindow().hide();
        } else {
            currentWindow.getWindow().show();
        }
    }

    /**
     * Open the context menu box to modifie the tile.
     *
     * @param tile
     * @param tilePos
     */
    void setSelectedTile(HexCoordinate tilePos, Character trigguer) {
        if (trigguer.equals('L')) {
            closeWidgetMenu();
        } else if (trigguer.equals('R') && tilePos != null) {
            openWidgetMenu(tilePos);
        }
    }

    /**
     * Window related to the selected hex.
     *
     * @param coord of the selected hex
     */
    private void openWidgetMenu(HexCoordinate tilePos) {
        if (tileWidgetMenu == null) {
            tileWidgetMenu = new AreaTileWidget(((MultiverseMain) app).getScreen(), app.getCamera(), ((AreaEditorSystem) system), tilePos);
        }
        tileWidgetMenu.show(tilePos);
    }

    private void closeWidgetMenu() {
        if (tileWidgetMenu != null && tileWidgetMenu.isVisible()) {
            tileWidgetMenu.hide();
        }
    }

    @Override
    public void update(float tpf) {
        if (tileWidgetMenu != null) {
            tileWidgetMenu.update(tpf);
        }
    }

    @Override
    public void removeFromScreen() {
        super.removeFromScreen();
        if (tileWidgetMenu != null) {
            tileWidgetMenu.removeFromScreen();
        }
    }
}

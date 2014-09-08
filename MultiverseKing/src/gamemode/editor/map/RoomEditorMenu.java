package gamemode.editor.map;

import gamemode.editor.EditorMenu;
import gamemode.editor.EditorWindow;
import gamemode.editor.map.MapEditorSystem.MapEditorMode;
import kingofmultiverse.MultiverseMain;
import tonegod.gui.controls.windows.Window;
import tonegod.gui.core.Screen;
import utility.HexCoordinate;

/**
 *
 * @author roah
 */
final class RoomEditorMenu extends EditorMenu {

    private EditorWindow currentWindow = null;
    private int currentValue = 10;
    private Window tileWin;
    private RoomTileWidget tileWidgetMenu;

    RoomEditorMenu(Screen screen, MapEditorSystem mapEditorSystem) {
        super(screen, "AreaEditorMenu", "Area Editor", mapEditorSystem);
        addAdditionalField("Area Loader");
        addAdditionalField("Edit Assets");
        addAdditionalField("Test E.Attribut");
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
                currentWindow.detachFromParent();
            }
            switch (value) {
                case 0:
                    /**
                     * Open the area loader to save or load an area.
                     */
                    currentWindow = new MapEditorLoaderWindow(screen, this);
                    currentValue = value;
                    break;
                case 1:
                    /**
                     * Open the props editor.
                     */
                    break;
                case 2:
                    /**
                     * Open the E.Attribut window to test other elements on the
                     * current area.
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

    @Override
    protected void additionalFieldReturnTrigger() {
        ((MapEditorSystem) system).switchGui(MapEditorMode.NONE);
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
            tileWidgetMenu = new RoomTileWidget(((MultiverseMain) app).getScreen(), app.getCamera(), ((MapEditorSystem) system), tilePos);
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
}

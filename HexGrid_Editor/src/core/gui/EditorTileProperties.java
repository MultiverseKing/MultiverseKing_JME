package core.gui;

import com.jme3.math.Vector2f;
import core.EditorSystem;
import gui.deprecated.control.EditorWindow;
import org.hexgridapi.core.HexGrid;
import org.hexgridapi.core.appstate.MouseControlSystem;
import org.hexgridapi.core.control.TileSelectionControl;
import org.hexgridapi.events.TileSelectionListener;
import org.hexgridapi.utility.HexCoordinate;
import tonegod.gui.controls.buttons.ButtonAdapter;
import tonegod.gui.controls.lists.Spinner.ChangeType;
import tonegod.gui.core.Element;
import tonegod.gui.core.Screen;

/**
 * Option used to customize the area.
 *
 * @author roah
 */
public class EditorTileProperties extends EditorWindow {

    private final MouseControlSystem mouseSystem;
    private final EditorSystem editorSystem;
//    private HexCoordinate tilePosition;// = new HexCoordinate(HexCoordinate.OFFSET, new Vector2Int());
    private boolean currentIsGroup = false; // if inspected tile exist in MapData
    private EditorWindow textureSelectionMenu;
    private Boolean currentIsGhost;

    public EditorTileProperties(Screen screen, Element parent, MouseControlSystem mouseSystem, EditorSystem editorSystem) {
        super(screen, parent, "Tile Properties");
        this.mouseSystem = mouseSystem;
        this.editorSystem = editorSystem;
        addLabelField("data", "No data", HAlign.left);
        show();
        window.setIsCollapse();
        mouseSystem.getSelectionControl().registerTileListener(selectionListener);
    }
    private TileSelectionListener selectionListener = new TileSelectionListener() {
        @Override
        public void onTileSelectionUpdate(HexCoordinate currentSelection) {
            updateWindow();
        }
    };

    private void show() {
        if (parent != null) {
            showConstrainToParent(VAlign.bottom, HAlign.right);
        } else {
            show(VAlign.top, HAlign.right);
        }
        window.setUseCloseButton(false);
        window.setUseCollapseButton(true);
    }

    private void updateWindow() {
        boolean isGroup = mouseSystem.getSelectionControl().getSelectedList().isEmpty();
        if (currentIsGhost == null || isGroup != currentIsGroup || (editorSystem.getTile() != null ? false : true) != currentIsGhost) {
            initialiseValue();
        } else {
            updateValue();
        }
    }

    private void initialiseValue() {
        super.removeFromScreen();
        TileSelectionControl control = mouseSystem.getSelectionControl();
        if (control.getSelectedList().isEmpty()) {
            addLabelPropertieField("Coordinate", control.getSelectedPos().getAsOffset(), HAlign.left);
            addLabelPropertieField("Chunk", HexGrid.getChunkGridPosition(control.getSelectedPos()), HAlign.left);
        }
        if (!control.getSelectedList().isEmpty()) {
            addButtonField(null, "Texture", HAlign.left, editorSystem.getTextureDefault(), HAlign.left, ButtonType.IMG); // @todo
            addButtonList(null, "Height", HAlign.left, new String[]{"dec", "inc"}, HAlign.left, ButtonType.TEXT, 1);
            addButtonField(null, "Initialise/reset", HAlign.full, ButtonType.TEXT);
            addButtonField(null, "Destroy", HAlign.full, ButtonType.TEXT);
        } else if (editorSystem.getTile() != null) {
            addButtonField(null, "Texture", HAlign.left, editorSystem.getTileTextureKey(), HAlign.left, ButtonType.IMG);
            addSpinnerField("Height", "Height", new int[]{-100, 100, 1, editorSystem.getTileHeight()}, HAlign.left);
            addButtonField(null, "Destroy", HAlign.full, ButtonType.TEXT);
        } else {
            addButtonField(null, "Initialise/reset", HAlign.full, ButtonType.TEXT);
        }
        currentIsGhost = editorSystem.getTile() != null ? false : true;
        currentIsGroup = control.getSelectedList().isEmpty();
        show();
    }

    private void updateValue() {
        getField(null, "coordinate");
    }

    private void updateTexture(String label) {
        ButtonAdapter btn = getButtonField(null, "Texture", editorSystem.getTileTextureKey(), ButtonType.IMG);
        btn.setColorMap("Textures/Icons/Buttons/" + label + ".png");
        Element btnParent = btn.getElementParent();
        btnParent.removeChild(btn);
        btn.setUID(label + btn.getUID().substring(editorSystem.getTileTextureKey().length()));
        btnParent.addChild(btn);
    }

    @Override
    protected void onButtonTrigger(String label) {
        boolean updateSelection = false;
        if (label.equals("Initialise/reset")) {
            editorSystem.setNewTile();
            updateWindow();
        } else if (label.equals("Destroy")) {
            editorSystem.removeTile();
            updateWindow();
        } else if (label.equals("inc")) {
            editorSystem.setTilePropertiesUp();
        } else if (label.equals("dec")) {
            editorSystem.setTilePropertiesDown();
        } else {
            updateSelection = true;
        }
        if (updateSelection) {
            selectionMenu(label);
        }
    }

    private void selectionMenu(final String ignoredKey) {
        if (textureSelectionMenu != null) {
            textureSelectionMenu.removeFromScreen();
        }
        textureSelectionMenu = new EditorWindow(screen, getButtonField(null, "Texture", ignoredKey, ButtonType.IMG),
                "Textures", new Vector2f(spacement + (editorSystem.getTextureKeys().size() - 1) * 16, 18)) {
            @Override
            public void setVisible() {
                String[] list = new String[editorSystem.getTextureKeys().size() - 1];
                int i = 0;
                for (String s : editorSystem.getTextureKeys()) {
                    if (!s.equals(ignoredKey)) {
                        list[i] = s.toUpperCase();
                        i++;
                    }
                }
                addButtonList("texturesBtn", list, HAlign.left, ButtonType.IMG);
                showConstrainToParent(null, HAlign.right);
                this.window.removeChild(this.window.getDragBar());
                this.window.setDimensions(this.window.getDimensions().x, 23);
                this.window.getContentArea().setPosition(2, -8);
                this.window.setPosition(parent.getDimensions().x + 5, 0);
            }

            @Override
            public void onPressCloseAndHide() {
            }

            @Override
            protected void onButtonTrigger(String label) {
                updateTexture(label);
                editorSystem.setTilePropertiesTexTure(label);
                window.hide();
            }

            @Override
            protected void onTextFieldInput(String UID, String input, boolean triggerOn) {
            }

            @Override
            protected void onNumericFieldInput(Integer input) {
            }

            @Override
            protected void onSelectBoxFieldChange(Enum value) {
            }

            @Override
            protected void onSpinnerChange(String sTrigger, int currentIndex, ChangeType type) {
            }
        };
        textureSelectionMenu.setVisible();
    }

    @Override
    public void onPressCloseAndHide() {
    }

    @Override
    protected void onTextFieldInput(String UID, String input, boolean triggerOn) {
    }

    @Override
    protected void onNumericFieldInput(Integer input) {
    }

    @Override
    protected void onSelectBoxFieldChange(Enum value) {
    }

    @Override
    protected void onSpinnerChange(String sTrigger, int currentIndex, ChangeType type) {
        if (type.equals(ChangeType.INC)) {
            editorSystem.setTilePropertiesUp();
        } else {
            editorSystem.setTilePropertiesDown();
        }
    }
}

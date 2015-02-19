package core.gui;

import com.jme3.math.Vector2f;
import core.EditorMainSystem;
import gui.EditorWindow;
import org.hexgridapi.core.HexGridManager;
import org.hexgridapi.core.HexTile;
import org.hexgridapi.utility.HexCoordinate;
import tonegod.gui.controls.text.Label;
import tonegod.gui.core.Element;
import tonegod.gui.core.Screen;

/**
 * Option used to customize the area.
 *
 * @author roah
 */
public class EditorTileProperties extends EditorWindow {

    private final EditorMainSystem system;
    private HexCoordinate tilePosition;// = new HexCoordinate(HexCoordinate.OFFSET, new Vector2Int());

    public EditorTileProperties(Screen screen, Element parent, EditorMainSystem system) {
        super(screen, parent, "Tile Properties");
        this.system = system;
        addLabelField("data" ,"No data", HAlign.left);
        show();
    }

    private void show() {
        if (parent != null) {
            showConstrainToParent(VAlign.bottom, HAlign.right);
        } else {
            show(VAlign.top, HAlign.right);
        }
        window.setUseCloseButton(false);
        window.setUseCollapseButton(true);
        if (tilePosition == null) {
            window.setIsCollapse();
        }
    }

    public void updatePosition(HexCoordinate position, boolean isGhost) {
        this.tilePosition = position;
        update(isGhost);
    }

    public void updateTexture(String label) {
        system.setTileProperties(tilePosition, label);
        update(false);
    }

    public void update(boolean isGhost) {
        if (parent != null) {
            window.getElementParent().removeChild(window);
        } else {
            parent.removeChild(window);
        }
        elementList.clear();
        addLabelPropertieField("Coordinate", tilePosition.getAsOffset(), HAlign.left);
        addLabelPropertieField("Chunk", HexGridManager.getChunkGridPosition(tilePosition), HAlign.left);
        if (!isGhost) {
            addButtonField(null, "Texture", HAlign.left, system.getTileTextureKey(tilePosition), HAlign.left, ButtonType.IMG);
            addHeighField();
            addButtonField(null, "Destroy", HAlign.full, ButtonType.TEXT);
        } else {
            addButtonField(null, "Generate", HAlign.full, ButtonType.TEXT);
        }
        show();
    }

    private void addHeighField() {
        Label label = generateLabel("Height", HAlign.left, false);
        elementList.put("Height", label);
        label.addChild(generateSpinner("Height", "Height", new int[]{-100, 100, 1, system.getTileHeight(tilePosition)}));
    }

    @Override
    protected void onButtonTrigger(String label) {
        if (label.equals("Generate")) {
            system.setTileProperties(tilePosition, new HexTile((byte) 0, (byte) 0));
            updatePosition(tilePosition, false);
        } else if (label.equals("Destroy")) {
            system.removeTile(tilePosition);
            updatePosition(tilePosition, true);
        }
        String[] val = label.split("\\.");
        switch (val[0]) {
            case "Texture":
                selectionMenu(val[1]);
                break;
        }
    }

    private void selectionMenu(final String ignoredKey) {
        EditorWindow win = new EditorWindow(screen, getButtonField(null, "Texture", ignoredKey, ButtonType.IMG),
                "Textures", new Vector2f(spacement + (system.getTextureKeys().size()-1) * 16, 18)) {
            @Override
            public void setVisible() {
                String[] list = new String[system.getTextureKeys().size() - 1];
                int i = 0;
                for (String s : system.getTextureKeys()) {
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
            protected void onSpinnerChange(String sTrigger, int currentIndex) {
            }
        };
        win.setVisible();
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
    protected void onSpinnerChange(String sTrigger, int currentIndex) {
        system.setTileProperties(tilePosition, (byte) currentIndex);
    }
}

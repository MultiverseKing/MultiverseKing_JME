package core.gui;

import com.jme3.math.Vector2f;
import core.EditorMainSystem;
import gui.EditorWindow;
import org.hexgridapi.core.HexGridManager;
import org.hexgridapi.core.HexTile;
import org.hexgridapi.utility.HexCoordinate;
import tonegod.gui.controls.buttons.ButtonAdapter;
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
    private Boolean currentIsGhost = null; // if inspected tile exist in MapData
    private EditorWindow textureSelectionMenu;

    public EditorTileProperties(Screen screen, Element parent, EditorMainSystem system) {
        super(screen, parent, "Tile Properties");
        this.system = system;
        addLabelField("data", "No data", HAlign.left);
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

    public void updatePosition(HexCoordinate position, boolean isGroup, boolean isGhost) {
        this.tilePosition = position;
        if (currentIsGhost == null || currentIsGhost != isGhost) {
            this.currentIsGhost = isGhost;
            initialise(isGroup);
        } else {
            update(isGroup);
        }
        update(false);
    }

    private void updateTexture(String label) {
        ButtonAdapter btn = getButtonField(null, "Texture", system.getTileTextureKey(tilePosition), ButtonType.IMG);
        btn.setColorMap("Textures/Icons/Buttons/"+label+".png");
        Element btnParent = btn.getElementParent();
        btnParent.removeChild(btn);
        btn.setUID(label + btn.getUID().substring(system.getTileTextureKey(tilePosition).length()));
        btnParent.addChild(btn);
        system.setTileProperties(tilePosition, label);
    }

    public void update(boolean isGroup) {
    }

    private void initialise(boolean isGroup) {
        if (parent != null) {
            window.getElementParent().removeChild(window);
        } else {
            parent.removeChild(window);
        }
        elementList.clear();
        if (!isGroup) {
            addLabelPropertieField("Coordinate", tilePosition.getAsOffset(), HAlign.left);
            addLabelPropertieField("Chunk", HexGridManager.getChunkGridPosition(tilePosition), HAlign.left);
        }
        if (!currentIsGhost || isGroup) {
            addButtonField(null, "Texture", HAlign.left, system.getTileTextureKey(tilePosition), HAlign.left, ButtonType.IMG);
            addHeighField();
            addButtonField(null, "Destroy", HAlign.full, ButtonType.TEXT);
        } else if (currentIsGhost || isGroup) {
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
            updatePosition(tilePosition, false, false);
        } else if (label.equals("Destroy")) {
            system.removeTile(tilePosition);
            updatePosition(tilePosition, false, true);
        }
        selectionMenu(label);
    }

    private void selectionMenu(final String ignoredKey) {
        if (textureSelectionMenu != null) {
            textureSelectionMenu.removeFromScreen();
        }
        System.err.println("get value : " + ignoredKey);
        System.err.println(getButtonField(null, "Texture", ignoredKey, ButtonType.IMG).getUID());
        textureSelectionMenu = new EditorWindow(screen, getButtonField(null, "Texture", ignoredKey, ButtonType.IMG),
                "Textures", new Vector2f(spacement + (system.getTextureKeys().size() - 1) * 16, 18)) {
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
            protected void onSpinnerChange(String sTrigger, int currentIndex) {
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
    protected void onSpinnerChange(String sTrigger, int currentIndex) {
        system.setTileProperties(tilePosition, (byte) currentIndex);
    }
}

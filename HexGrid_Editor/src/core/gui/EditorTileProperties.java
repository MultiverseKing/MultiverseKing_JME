package core.gui;

import com.jme3.math.Vector2f;
import core.EditorMainSystem;
import gui.EditorWindow;
import org.hexgridapi.core.HexGridManager;
import org.hexgridapi.utility.HexCoordinate;
import tonegod.gui.controls.buttons.ButtonAdapter;
import tonegod.gui.core.Element;
import tonegod.gui.core.Screen;

/**
 * Option used to customize the area.
 *
 * @author roah
 */
public class EditorTileProperties extends EditorWindow {

    private final EditorMainSystem system;
//    private HexCoordinate tilePosition;// = new HexCoordinate(HexCoordinate.OFFSET, new Vector2Int());
    private Boolean currentIsGhost = null; // if inspected tile exist in MapData
    private boolean currentIsGroup = false; // if inspected tile exist in MapData
    private EditorWindow textureSelectionMenu;

    public EditorTileProperties(Screen screen, Element parent, EditorMainSystem system) {
        super(screen, parent, "Tile Properties");
        this.system = system;
        addLabelField("data", "No data", HAlign.left);
        show();
        window.setIsCollapse();
    }

    private void show() {
        if (parent != null) {
            showConstrainToParent(VAlign.bottom, HAlign.right);
        } else {
            show(VAlign.top, HAlign.right);
        }
        window.setUseCloseButton(false);
        window.setUseCollapseButton(true);
    }
    
    public void updatePosition(boolean isGhost, boolean isGroup) {
//        this.tilePosition = position;
        if (currentIsGhost == null || currentIsGhost != isGhost || isGroup != currentIsGroup) {
            this.currentIsGhost = isGhost;
            initialise(isGroup);
        } else {
            update(isGroup);
        }
        update(false);
    }

    private void updateTexture(String label) {
        ButtonAdapter btn = getButtonField(null, "Texture", system.getTileTextureKey(), ButtonType.IMG);
        btn.setColorMap("Textures/Icons/Buttons/"+label+".png");
        Element btnParent = btn.getElementParent();
        btnParent.removeChild(btn);
        btn.setUID(label + btn.getUID().substring(system.getTileTextureKey().length()));
        btnParent.addChild(btn);
        system.setTilePropertiesTexTure(label);
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
        if(isGroup) {
            addButtonField(null, "Texture", HAlign.left, system.getTileTextureKey(tilePosition), HAlign.left, ButtonType.IMG);
            addButtonList(null, "Height", HAlign.left, new String[]{"dec", "inc"}, HAlign.left, ButtonType.TEXT, 1);
        } else if (!currentIsGhost) {
            addButtonField(null, "Texture", HAlign.left, system.getTileTextureKey(tilePosition), HAlign.left, ButtonType.IMG);
            addHeighField();
            addButtonField(null, "Destroy", HAlign.full, ButtonType.TEXT);
        } else if (currentIsGhost) {
            addButtonField(null, "Generate", HAlign.full, ButtonType.TEXT);
        }
        currentIsGroup = isGroup;
        show();
    }

    /**
     * @todo
     */
    private void addHeighField() {
        addSpinnerField("Height", "Height", new int[]{-100, 100, 1, system.getTileHeight(tilePosition)}, HAlign.left);
//        Label label = generateLabel("Height", HAlign.left, false);
//        elementList.put("Height", label);
//        label.addChild(generateSpinner("Height", "Height", new int[]{-100, 100, 1, system.getTileHeight(tilePosition)}));
    }

    @Override
    protected void onButtonTrigger(String label) {
        boolean updateSelection = false;
        if (label.equals("Generate")) {
            system.setTileProperties(tilePosition);
            updatePosition(tilePosition, false, currentIsGroup);
        } else if (label.equals("Destroy")) {
            system.removeTile(tilePosition);
            updatePosition(tilePosition, true, currentIsGroup);
        } else if (label.equals("inc")) {
            system.setTilePropertiesUp(tilePosition);
        } else if (label.equals("dec")) {
            system.setTilePropertiesDown(tilePosition);
        } else {
            updateSelection = true;
        }
        if(updateSelection) {
            selectionMenu(label);
        }
    }

    private void selectionMenu(final String ignoredKey) {
        if (textureSelectionMenu != null) {
            textureSelectionMenu.removeFromScreen();
        }
        System.err.println(ignoredKey);
        textureSelectionMenu = new EditorWindow(screen, getButtonField(null, "Texture", ignoredKey, ButtonType.IMG),
                "Textures", new Vector2f(spacement + (system.getTextureKeys().size() - 1) * 16, 18)) {
            @Override
            public void setVisible() {
                String[] list = new String[system.getTextureKeys().size()-1];
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
        system.setTileProperties(tilePosition, currentIndex);
    }
}

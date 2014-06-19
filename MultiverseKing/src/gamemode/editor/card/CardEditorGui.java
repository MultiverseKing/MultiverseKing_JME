package gamemode.editor.card;

import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector2f;
import com.simsilica.es.EntityData;
import entitysystem.card.Hover;
import gamemode.editor.EditorMenu;
import gamemode.editor.EditorMenuWindow;
import hexsystem.MapData;
import kingofmultiverse.MultiverseMain;
import tonegod.gui.controls.buttons.ButtonAdapter;

/**
 * TODO : The GUI resolution should be consistant, gui scale should not depend
 * on the resolution screen, have an option to change it's resolution.
 *
 * @author Eike Foede, Roah
 */
public final class CardEditorGui extends EditorMenu {

    private final CardEditorSystem cardEditorSystem;
    private final MultiverseMain main;
    private EditorMenuWindow current;
    private EntityData entityData;
    private MapData mapData;
    private float rightMenu = 245;
    private Hover hover;
    private ButtonAdapter close;

    public CardEditorGui(MultiverseMain main, CardEditorSystem cardEditorSystem) {
        super(main.getScreen(), "CardEditorGui", "Card Editor", cardEditorSystem);
        this.main = main;
        this.cardEditorSystem = cardEditorSystem;
        addItem("Generator", 0);
        addItem("Test Card", 1);
        populateReturnEditorMain();
    }

    @Override
    protected void onSelectedItemChange(int selectedIndex) {
        switch (selectedIndex) {
            case 0:
                if (current != null && current instanceof TestCardGUI) {
                    current.detachFromParent();
                }
                current = new GeneratorGUI(screen, this);
                openClose(0);
                break;
            case 1:
                if (current != null && current instanceof GeneratorGUI) {
                    current.detachFromParent();
                }
                current = new TestCardGUI(screen, this, cardEditorSystem);
                openClose(1);
                break;
        }
    }

    private void openClose(int value) {
        if (value == 0) {
            if (close == null) {
                close = new ButtonAdapter(screen, new Vector2f(5,
                        -screen.getElementById("CloseRootButtonWin").getPosition().y + 30)) {
                    @Override
                    public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                        super.onButtonMouseLeftUp(evt, toggled);
                        if (getText().equals("CLOSE")) {
                            current.getWindow().hide();
                            setText("OPEN");
                        } else {
                            current.getWindow().show();
                            setText("CLOSE");
                        }
                    }
                };
                close.setText("CLOSE");
                addChild(close);
            } else {
                close.setPosition(close.getPosition().x, close.getPosition().y);
                close.setText("CLOSE");
                close.show();
            }

        } else {
            if(close != null){
                close.hide();
            }
        }
    }
}
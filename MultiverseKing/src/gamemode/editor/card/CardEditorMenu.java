package gamemode.editor.card;

import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector2f;
import com.simsilica.es.EntityData;
import entitysystem.card.Hover;
import gamemode.editor.EditorMenu;
import gamemode.gui.EditorWindow;
import hexsystem.MapData;
import kingofmultiverse.MultiverseMain;
import tonegod.gui.controls.buttons.ButtonAdapter;
import tonegod.gui.core.Element;

/**
 * @todo : The GUI resolution should be consistant, gui scale should not depend
 * on the resolution screen, have an option to change it's resolution.
 *
 * @author Eike Foede, Roah
 */
final class CardEditorMenu extends EditorMenu {

    private final CardEditorSystem cardEditorSystem;
    private final MultiverseMain main;
    private EditorWindow current;
    private EntityData entityData;
    private MapData mapData;
    private float rightMenu = 245;
    private Hover hover;
    private ButtonAdapter close;
    private int currentIndex;

    CardEditorMenu(MultiverseMain main, CardEditorSystem cardEditorSystem, Element parent) {
        super(main.getScreen(), "CardEditorGui", "Card Editor", cardEditorSystem, parent);
        this.main = main;
        this.cardEditorSystem = cardEditorSystem;
        addItem("Card Generator", 0);
        addItem("Asset-Gen", 1);
        addItem("FX-Builder", 2);
        addItem("Test Card", 3);
        populateEditor();
    }

    @Override
    protected void onSelectedItemChange(int index) {
        if (current != null && currentIndex != index) {
            current.removeFromScreen();
        } else if (current != null && currentIndex == index){
            return;
        }
        switch (index) {
            case 0:
                current = new CardEditorWindow(screen, this);
                openClose(0);
                break;
            case 1:
//                current = new GeneratorMenu(screen, this);
                openClose(1);
                break;
            case 2:
//                current = new GeneratorMenu(screen, this);
                openClose(1);
                break;
            case 3:
                current = new TestCardWindow(screen, this, cardEditorSystem);
                ((CardEditorSystem)system).genTestMap();
                openClose(1);
                break;
        }
        currentIndex = index;
    }

    private void openClose(int value) {
        if (value == 0) {
            if (close == null) {
                close = new ButtonAdapter(screen, new Vector2f(5,
                        -screen.getElementById("ReturnButtonWin").getPosition().y + 30)) {
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
            if (close != null && close.getIsVisible()) {
                close.hide();
            }
        }
    }

    @Override
    protected void onAdditionalFieldTrigger(int value) {
        
    }

    @Override
    public void update(float tpf) {
    }
}
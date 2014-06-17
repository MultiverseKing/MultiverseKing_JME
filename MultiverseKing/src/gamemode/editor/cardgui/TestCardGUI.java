package gamemode.editor.cardgui;

import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import entitysystem.attribut.SubType;
import entitysystem.card.CardRenderSystem;
import gamemode.editor.CardEditorSystem;
import gamemode.editor.EditorWindow;
import tonegod.gui.controls.buttons.Button;
import tonegod.gui.controls.buttons.ButtonAdapter;
import tonegod.gui.controls.windows.Window;
import tonegod.gui.core.Element;
import tonegod.gui.core.ElementManager;

/**
 *
 * @author roah
 */
public final class TestCardGUI extends EditorWindow {
    private final CardEditorSystem system;
    
    /**
     * Window used to add or remove card from the GUI.
     */
    public TestCardGUI(ElementManager screen, Element parent, CardEditorSystem system) {
        super(screen, parent, "Test Card");
        this.system = system;
        
        addButtonField("Add Card", "+1", 0);
        addButtonField("Remove Card", "-1", 1);
        
//        float offset;
//        if (mainMenu.getElementsAsMap().containsKey("CloseButtonWin")) {
//            offset = mainMenu.getElementsAsMap().get("CloseButtonWin").getWidth();
//        } else {
//            offset = 0;
//        }
//        Window addRemoveCard = new Window(main.getScreen(), "TestCardMenu",
//                new Vector2f(offset, mainMenu.getHeight()),
//                new Vector2f(180, 50));
//        addRemoveCard.removeAllChildren();
//        addRemoveCard.setIgnoreMouse(true);
//        mainMenu.addChild(addRemoveCard);

//        Button addCard = new ButtonAdapter(main.getScreen(), "addCard",
//                new Vector2f(15, 10), new Vector2f(70, 30)) {
//            @Override
//            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
//                super.onButtonMouseLeftUp(evt, toggled);
//                addEntityCard("Cendrea");
//            }
//        };
//        addCard.setText("Add");
//        addRemoveCard.addChild(addCard);

//        Button removeCard = new ButtonAdapter(main.getScreen(), "removeCard",
//                new Vector2f(94, 10), new Vector2f(70, 30)) {
//            @Override
//            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
//                super.onButtonMouseLeftUp(evt, toggled);
//                if (entity.size() > 1) {
//                    int i = 0;
//                    i = FastMath.nextRandomInt(2, entity.size());
//                    i-=1;
//                    entityData.removeEntity(entity.get(i));
//                    entity.remove(entity.get(i));
//                }
//            }
//        };
//        removeCard.setText("Del");
//        addRemoveCard.addChild(removeCard);
//
//        if (!main.getStateManager().getState(CardRenderSystem.class).gotCardInHand()) {
//            addEntityCard("Cendrea");
//        }
    }
    
    @Override
    protected void onButtonTrigger(int index) {
        switch(index){
            case 0:
                system.addEntityCard("Cendrea");
            case 1:
                system.removeEntityCard();
        }
    }
    
    
}

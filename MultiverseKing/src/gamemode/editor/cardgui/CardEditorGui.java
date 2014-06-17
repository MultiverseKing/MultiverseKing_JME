package gamemode.editor.cardgui;

import gamemode.editor.CardEditorSystem;
import com.simsilica.es.EntityData;
import entitysystem.card.Hover;
import gamemode.editor.EditorMenu;
import gamemode.editor.EditorWindow;
import hexsystem.MapData;
import kingofmultiverse.MultiverseMain;

/**
 * TODO : The GUI resolution should be consistant, gui scale should not depend
 * on the resolution screen, have an option to change it's resolution.
 *
 * @author Eike Foede, Roah
 */
public final class CardEditorGui extends EditorMenu {
    private final CardEditorSystem cardEditorSystem;
    private final MultiverseMain main;
    private EditorWindow current;
    
    private EntityData entityData;
    private MapData mapData;
    private float rightMenu = 245;
    private Hover hover;

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
        if(current != null){
            current.detachFromParent();
//            screen.removeElement(screen.getElementById(current.getUID()));
            current = null;
        } else {
            current = new GeneratorGUI(screen, this);
        }
//        switch(selectedIndex){
//            case 0:
//                current = new GeneratorGUI(screen, this);
//            case 1:
//                current = new TestCardGUI(screen, this, cardEditorSystem);
//        }
    }
    
//    public void getMenuButton(){
//        /**
//         * Button used to open the card generator/editing menu.
//         */
//        Button generator = new ButtonAdapter(main.getScreen(), "cardGeneratorB",
//                new Vector2f(15, 40)) {
//            @Override
//            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
//                super.onButtonMouseLeftUp(evt, toggled);
//                if (!rootWindow.getElementsAsMap().containsKey("cardGeneratorW")) {
//                    if (rootWindow.getElementsAsMap().containsKey("TestCardMenu")) {
//                        rootWindow.removeChild(rootWindow.getElementsAsMap().get("TestCardMenu"));
//                    }
//                    main.getStateManager().getState(CardRenderSystem.class).hideCards();
//                    generatorMenu();
//                } else {
//                    rootWindow.removeChild(rootWindow.getElementsAsMap().get("cardGeneratorW"));
//                }
//            }
//        };
//        generator.setText("Generator");
//        radioButtonGroup.addButton(generator);
//
//        /**
//         * Button used to open the card Testing menu.
//         */
//        Button testCard = new ButtonAdapter(main.getScreen(), "cardTestingB",
//                new Vector2f(15, 40 * 2)) {
//            @Override
//            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
//                super.onButtonMouseLeftUp(evt, toggled);
//                if (!rootWindow.getElementsAsMap().containsKey("TestCardMenu")) {
//                    if (rootWindow.getElementsAsMap().containsKey("cardGeneratorW")) {
//                        rootWindow.removeChild(rootWindow.getElementsAsMap().get("cardGeneratorW"));
//                    }
//                    main.getStateManager().getState(CardRenderSystem.class).showCards();
//                    openTestCardMenu();
//                } else {
//                    rootWindow.removeChild(rootWindow.getElementsAsMap().get("TestCardMenu"));
//                    main.getStateManager().getState(CardRenderSystem.class).hideCards();
//                }
//            }
//        };
//        testCard.setText("Test Card");
//        radioButtonGroup.addButton(testCard);
//    }
    
    
    
//    @Override
//    public void cleanup() {
//        String[] toRemove = new String[]{"categoryAll", "categoryAbility", "categoryUnit",
//            "loadCategory", "generatorCardTypeMenu", "generatorFactionMenu"};
//        for (String s : toRemove) {
//            if (main.getScreen().getElementsAsMap().containsKey(s)) {
//                main.getScreen().removeElement(main.getScreen().getElementById(s));
//            }
//        }
//        mainMenu.detachAllChildren();
//        cleanupEntityTest();
//    }

    
}
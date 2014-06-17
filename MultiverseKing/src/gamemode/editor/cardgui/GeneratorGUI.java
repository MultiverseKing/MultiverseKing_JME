package gamemode.editor.cardgui;

import entitysystem.attribut.Faction;
import entitysystem.attribut.SubType;
import gamemode.editor.EditorWindow;
import tonegod.gui.controls.windows.Window;
import tonegod.gui.core.Element;
import tonegod.gui.style.LayoutParser;
import tonegod.gui.core.ElementManager;
import utility.ElementalAttribut;

/**
 *
 * @author roah
 */
public final class GeneratorGUI extends EditorWindow {

    public GeneratorGUI(ElementManager screen, Element parent) {
        super(screen, parent, "Card Generator");
        /**
         * Part used to show/set the card Name.
         */
        addEditableTextField("Name", "TuxDoll");
        /**
         * Part used to show/choose the card faction.
         */
        addEditableSelectionField("Faction", Faction.NEUTRAL, Faction.values());
        /**
         * Part used to show/choose the card Type.
         */
        addEditableSelectionField("Card Type", SubType.SUMMON, SubType.values());
        /**
         * Part used to show/choose the card Element Attribut.
         */
        addEditableSelectionField("E.Attribut", ElementalAttribut.NULL, ElementalAttribut.values());
        /**
         * Part used to show/choose the cost needed to use the card.
         */
        addSpinnerField("Cost", new int[] {0, 20, 1, 0});
        /**
         * Part used to test out card.
         */
        addButtonField("SubType Properties", "Edit", 0);
        /**
         * Part used to test out card.
         */
        addButtonField("Test Card", "Cast", 1);
        /**
         * Part used to show/set the card Description text.
         */
        addEditableTextField("Description", "This is a Testing unit");
        
    }

    @Override
    protected void onSelectBoxFieldChange(Enum value) {
        if(value instanceof Faction){
            /**
             * Change inspected Card Faction.
             */
        } else if (value instanceof SubType){
            /**
             * Change inspected Card SubType.
             */
        } else if (value instanceof ElementalAttribut){
            /**
             * Change inspected Card Element.
             */
        }
    }

    @Override
    protected void onSpinnerChange(String sTrigger, int currentIndex) {
        if(sTrigger.equals("Cost")){
            /**
             * Change the hover cost value.
             */
        }
    }

    @Override
    protected void onButtonTrigger(int index) {
        switch(index){
            case 0:
                addChild(new GeneratorSubMenu(screen, this, (SubType)getFieldValue("Card Type")));
            case 1:
                /**
                 * Activate the cards.
                 */
        }
    }
    
    

    private void generatorFields() {
        /**
         * Part used to show/set the card Name.
         */
//        LabelElement nameLabel = new LabelElement(main.getScreen(), "generatorNameLabel",
//                new Vector2f(10, 5), new Vector2f(60, 35));
//        nameLabel.setText("Name : ");
//        addChild(nameLabel);

//        ButtonAdapter nameButton = new ButtonAdapter(main.getScreen(), "generatorFieldButton",
//                new Vector2f(nameLabel.getDimensions().x + 5, 12)) {
//            @Override
//            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
//                super.onButtonMouseLeftUp(evt, toggled);
//                TextField field = new TextField(main.getScreen(), "generatorNameField",
//                        new Vector2f(getElementParent().getElementsAsMap().get("generatorNameLabel").getDimensions().x + 5, 12),
//                        new Vector2f(100, 30)) {
//                    @Override
//                    public void onKeyRelease(KeyInputEvent evt) {
//                        super.onKeyRelease(evt);
//                        if (evt.getKeyCode() == KeyInput.KEY_RETURN) {
//                            screen.getElementById("generatorFieldButton").setText(getText());
//                            hover.setCardName(getText());
//                            getElementParent().removeChild(this);
//                        }
//                    }
//                };
//                field.setMaxLength(15);
//                field.setText(getText());
//                getElementParent().addChild(field);
//            }
//        };
//        nameButton.setText("TuxDoll");
//        callerMenu.addChild(nameButton);

        /**
         * Part used to show/choose the card Type.
         */
//        LabelElement subTypeLabel = new LabelElement(main.getScreen(), "generatorCardTypeLabel", new Vector2f(10, 45), new Vector2f(95, 35));
//        subTypeLabel.setText("Card Type : ");
//        callerMenu.addChild(subTypeLabel);

//        ButtonAdapter subTypeButton = new ButtonAdapter(main.getScreen(), "generatorCardTypeButton",
//                new Vector2f(subTypeLabel.getDimensions().x, 48)) {
//            @Override
//            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
//                if (!main.getScreen().getElementsAsMap().containsKey("generatorCardTypeMenu")) {
//                    loadSubTypeMenu();
//                }
//                Menu SubTypeMenu = (Menu) main.getScreen().getElementById("generatorCardTypeMenu");
//                SubTypeMenu.showMenu(null, getAbsoluteX(), getAbsoluteY() - SubTypeMenu.getHeight());
//            }
//        };
//        subTypeButton.setText(SubType.SUMMON.name());
//        callerMenu.addChild(subTypeButton);

        /**
         * Part used to show/set the card Description text.
         */
//        LabelElement descriptionLabel = new LabelElement(main.getScreen(), "generatorDescriptionLabel",
//                new Vector2f(10, 85), new Vector2f(150, 35));
//        descriptionLabel.setText("Description : ");
//        callerMenu.addChild(descriptionLabel);
//
//        ButtonAdapter descriptionButton = new ButtonAdapter(main.getScreen(), "generatorDescriptionButton",
//                new Vector2f(10, 115), new Vector2f(main.getScreen().getElementById("cardGeneratorW").getWidth() * 0.9f, 30)) {
//            @Override
//            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
//                super.onButtonMouseLeftUp(evt, toggled);
//                TextField field = new TextField(main.getScreen(), "generatorDescriptionField",
//                        new Vector2f(10, 115), new Vector2f(screen.getElementById("cardGeneratorW").getWidth() * 0.9f, 30)) {
//                    @Override
//                    public void onKeyRelease(KeyInputEvent evt) {
//                        super.onKeyRelease(evt);
//                        if (evt.getKeyCode() == KeyInput.KEY_RETURN) {
//                            screen.getElementById("generatorDescriptionButton").setText(getText());
//                            getElementParent().removeChild(this);
//                        }
//                    }
//                };
//                field.setText(getText());
//                getElementParent().addChild(field);
//            }
//        };
//        descriptionButton.setText("This is a Testing unit");
//        callerMenu.addChild(descriptionButton);

        /**
         * Part used to show/choose the card faction.
         */
//        LabelElement factionLabel = new LabelElement(main.getScreen(), "generatorFactionLabel", new Vector2f(rightMenu, 5), new Vector2f(75, 35));
//        factionLabel.setText("Faction : ");
//        callerMenu.addChild(factionLabel);
//
//        ButtonAdapter factionButton = new ButtonAdapter(main.getScreen(), "generatorFactionButton",
//                new Vector2f(rightMenu + factionLabel.getDimensions().x, 11)) {
//            @Override
//            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
//                if (!main.getScreen().getElementsAsMap().containsKey("generatorFactionMenu")) {
//                    OpenFactionMenu();
//                }
//                Menu factionMenu = (Menu) main.getScreen().getElementById("generatorFactionMenu");
//                factionMenu.showMenu(null, getAbsoluteX(), getAbsoluteY() - factionMenu.getHeight());
//            }
//        };
//        factionButton.setText(Faction.NEUTRAL.name());
//        callerMenu.addChild(factionButton);

        /**
         * Part used to show/choose the card Element Attribut.
         */
//        LabelElement elementTypeLabel = new LabelElement(main.getScreen(), "generatorEAttributLabel", new Vector2f(rightMenu, 45), new Vector2f(80, 35));
//        elementTypeLabel.setText("E.Attribut : ");
//        callerMenu.addChild(elementTypeLabel);
//
//        ButtonAdapter elementTypeButton = new ButtonAdapter(main.getScreen(), "generatorEAttributButton",
//                new Vector2f(rightMenu + elementTypeLabel.getDimensions().x + 5, 48)) {
//            @Override
//            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
//                if (!main.getScreen().getElementsAsMap().containsKey("generatorEAttributMenu")) {
//                    loadElementalMenu();
//                }
//                Menu SubTypeMenu = (Menu) main.getScreen().getElementById("generatorEAttributMenu");
//                SubTypeMenu.showMenu(null, getAbsoluteX(), getAbsoluteY() - SubTypeMenu.getHeight());
//            }
//        };
//        elementTypeButton.setText(ElementalAttribut.NULL.name());
//        callerMenu.addChild(elementTypeButton);

        /**
         * Part used to show/choose the cost needed to use the card.
         */
//        LabelElement costLabel = new LabelElement(main.getScreen(), "generatorCostLabel", new Vector2f(rightMenu, 80), new Vector2f(75, 35));
//        costLabel.setText("Cost : ");
//        callerMenu.addChild(costLabel);
//
//        Spinner spinner = new Spinner(main.getScreen(), "generatorCostSpinner",
//                new Vector2f(rightMenu + costLabel.getDimensions().x - 22, 85), Spinner.Orientation.HORIZONTAL, true) {
//            @Override
//            public void onChange(int selectedIndex, String value) {
//                hover.setCastCost(selectedIndex);
//            }
//        };
//        spinner.setStepIntegerRange(0, 20, 1);
//        spinner.setSelectedIndex(0);
//        callerMenu.addChild(spinner);

        /**
         * Part used to test out card.
         */
//        Window cardTest = new Window(main.getScreen(), "cardTestWin",
//                new Vector2f(285, callerMenu.getHeight()), new Vector2f(150, 50));
//        cardTest.removeAllChildren();
//        cardTest.setIgnoreMouse(true);
//        callerMenu.addChild(cardTest);
//
//        ButtonAdapter cast = new ButtonAdapter(main.getScreen(), "castCardButton", new Vector2f(10, 10), new Vector2f(130, 30)) {
//            @Override
//            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
//                super.onButtonMouseLeftUp(evt, toggled);
//            }
//        };
//        cast.setText("Cast test !");
//        cardTest.addChild(cast);

    }
    
    private void loadElementalMenu() {
//        Menu elementalMenu = new Menu(main.getScreen(), "generatorEAttributMenu", new Vector2f(0, 30), false) {
//            @Override
//            public void onMenuItemClicked(int index, Object value, boolean isToggled) {
//                ElementalAttribut eAttribut = (ElementalAttribut) value;
//                main.getScreen().getElementById("generatorEAttributButton").setText(eAttribut.name());
//                main.getScreen().getElementById("cardPropertiesHover").setColorMap("Textures/Cards/" + eAttribut.name() + ".png");
//
//            }
//        };
//        ElementalAttribut[] eAttributList = ElementalAttribut.values();
//        for (ElementalAttribut eAttribut : eAttributList) {
//            elementalMenu.addMenuItem(eAttribut.name(), eAttribut, null);
//        }
//        main.getScreen().addElement(elementalMenu);
    }
    
    private void openSubTypeMenu() {
//        Menu subType = new Menu(main.getScreen(), "generatorCardTypeMenu", new Vector2f(0, 30), false) {
//            @Override
//            public void onMenuItemClicked(int index, Object value, boolean isToggled) {
//                SubType cardType = (SubType) value;
//                Element genButton = main.getScreen().getElementById("generatorCardTypeButton");
//                if (!genButton.getText().equals(cardType.name())) {
//                    genButton.setText(cardType.name());
//                    //TODO: change the hover depending on the card type
//                    hover.setType(cardType);
//                    openCardTypeSubMenu(cardType);
//                }
//            }
//        };
//        SubType[] cardSubTypeList = SubType.values();
//        for (SubType s : cardSubTypeList) {
//            subType.addMenuItem(s.name(), s, null);
//        }
//        main.getScreen().addElement(subType);
    }
    private void openSubSummonMenu(Window subMenu) {
//                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    // </editor-fold>

    private void OpenFactionMenu() {
//        Menu faction = new Menu(main.getScreen(), "generatorFactionMenu", new Vector2f(0, 30), false) {
//            @Override
//            public void onMenuItemClicked(int index, Object value, boolean isToggled) {
//                Faction f = (Faction) value;
//                main.getScreen().getElementById("generatorFactionButton").setText(f.name());
//                hover.setFaction(f);
//            }
//        };
//        Faction[] factionList = Faction.values();
//        for (Faction f : factionList) {
//            faction.addMenuItem(f.name(), f, null);
//        }
//        main.getScreen().addElement(faction);
    }

    /**
     * Open a menu where card properties can be tweaked, it depend on the
     * subMenu loaded, who depend on the cardType.
     */
    public void openCardTypeSubMenu(SubType cardType) {
//        Window subMenu;
//        if (main.getScreen().getElementById("subMenu") != null) {
//            subMenu = (Window) main.getScreen().getElementById("subMenu");
//            subMenu.removeAllChildren();
//        } else {
//            Element win = main.getScreen().getElementById("geneneratorImgPreview");
//            subMenu = new Window(main.getScreen(), "subMenu", new Vector2f(
//                    win.getAbsoluteX(), 0), new Vector2f(
//                    main.getScreen().getWidth() - (win.getAbsoluteX() + win.getWidth() + 10),
//                    245));
//            subMenu.removeAllChildren();
//            mainMenu.getElementsAsMap().get("cardGeneratorW").addChild(subMenu);
//        }
//        switch (cardType) {
//            case ABILITY:
//                openSubAbilityMenu(subMenu);
//                break;
//            case EQUIPEMENT:
//                /**
//                 * @todo: Equipement subMenu
//                 */
//                break;
//            case SPELL:
//                /*
//                 * @todo: Spell subMenu
//                 */
//                break;
//            case TRAP:
//                /**
//                 * @todo: Trap subMenu
//                 */
//                break;
//            case SUMMON:
//                openSubSummonMenu(subMenu);
//                break;
//        }
    }
    
    public void getSubMenu() {
        /**
         * Window used to show card Properties.
         */
//        menu = new Window(screen, "cardGeneratorW", new Vector2f(130, 0),
//                new Vector2f(screen.getWidth() * 0.5f, 0));
//        menu.removeAllChildren();
//        menu.setIsResizable(false);
//        menu.setIsMovable(false);
    }    
}

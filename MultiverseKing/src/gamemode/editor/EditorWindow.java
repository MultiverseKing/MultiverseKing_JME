package gamemode.editor;

import com.jme3.input.KeyInput;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector2f;
import java.util.HashMap;
import tonegod.gui.controls.buttons.ButtonAdapter;
import tonegod.gui.controls.lists.SelectBox;
import tonegod.gui.controls.lists.Spinner;
import tonegod.gui.controls.text.Label;
import tonegod.gui.controls.text.TextField;
import tonegod.gui.core.Element;
import tonegod.gui.core.ElementManager;

/**
 *
 * @author roah
 */
public abstract class EditorWindow extends Element {

    private Label titleName;
    private HashMap<String, Element> elementList = new HashMap<String, Element>();
    private HashMap<String, Object> elementCurrentValue = new HashMap<String, Object>();

    private EditorWindow(ElementManager screen, String UID, Vector2f position, Vector2f dimensions) {
        super(screen, UID, position, dimensions,
                screen.getStyle("Window").getVector4f("resizeBorders"),
                screen.getStyle("Window").getString("defaultImg"));
    }

    protected EditorWindow(ElementManager screen, Element parent, String menuTitle) {
        this(screen, parent, menuTitle.replaceAll("\\s", ""), menuTitle);
    }

    protected EditorWindow(ElementManager screen, Element parent, String UID, String menuTitle) {
        this(screen, UID, new Vector2f(parent.getAbsoluteWidth(), 0), new Vector2f(250, 250));
        initialize(parent, menuTitle);
    }

    private void initialize(Element parent, String menuTitle){
        parent.addChild(this);
        titleName = new Label(screen, getUID() + "title", new Vector2f(), new Vector2f(getAbsoluteWidth(), 35));
        titleName.setText(menuTitle);
        addChild(titleName);
    }
    
    public void detachFromParent(){
        getElementParent().removeChild(this);
    }
    
    /**
     * Add a button Field to this menu.
     *
     * @param labelName
     * @param triggerName No Space && Not more than 7
     * @param index returned index when trigger
     */
    protected final void addButtonField(String labelName, String triggerName, int index) {
        if (triggerName.contains("\\s")) {
            throw new UnsupportedOperationException("No space Regex allowed in the trigger Name.");
        } else if (triggerName.toCharArray().length > 7) {
            throw new UnsupportedOperationException("Char count in the trigger Name exceeds the allowed amount.");
        }
        elementList.put(labelName.replaceAll("\\s", ""), generateLabel(labelName));
        generateButton(labelName.replaceAll("\\s", ""), triggerName, index);
    }

    /**
     * Add a spinner Element to this menu. value[0] = min value[1] = max
     * value[2] = step value[3] = current
     *
     * @param labelName
     * @param value
     */
    protected final void addSpinnerField(String labelName, int[] value) {
        elementList.put(labelName.replaceAll("\\s", ""), generateLabel(labelName));
        generateSpinner(labelName, labelName.replaceAll("\\s", ""), value);
    }

    /**
     * Add a Text Field element to this menu.
     *
     * @param labelName
     * @param baseValue
     */
    protected final void addEditableTextField(String labelName, String baseValue) {
        elementList.put(labelName.replaceAll("\\s", ""), generateLabel(labelName));
        generateTextField(labelName.replaceAll("\\s", ""), baseValue);
    }

    /**
     * Add a Text Field element to this menu, limited to number as input.
     *
     * @param labelName
     * @param baseValue
     */
    protected final void addEditableNumericField(String labelName, int baseValue) {
        elementList.put(labelName.replaceAll("\\s", ""), generateLabel(labelName));
        generateNumericField(labelName.replaceAll("\\s", ""), baseValue);
    }

    /**
     * Add a selectionList Element based on a enum to this menu.
     *
     * @param labelName
     * @param baseValue
     * @param value
     */
    protected final void addEditableSelectionField(String labelName, Enum<?> baseValue, Enum[] value) {
        elementList.put(labelName.replaceAll("\\s", ""), generateLabel(labelName));
        generateSelectBoxField(labelName.replaceAll("\\s", ""), baseValue, value);
    }

    private Label generateLabel(String name) {
        Label label = new Label(screen, getUID() + name.replaceAll("\\s", "") + "Label",
                new Vector2f(10, 5), new Vector2f(60, 35));
        label.setText(name + " : ");
        addChild(label);
        return label;
    }

    private void generateNumericField(final String labelName, int baseValue) {
        ButtonAdapter numButton = new ButtonAdapter(screen, getUID() + labelName + "TextFieldButton",
                new Vector2f(elementList.get(labelName).getDimensions().x + 5, 12)) {
            @Override
            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                super.onButtonMouseLeftUp(evt, toggled);
                TextField field = new TextField(screen, getUID() + labelName + "TextField",
                        new Vector2f(getElementParent().getElementsAsMap().get(getUID() + labelName + "Label").getDimensions().x + 5, 12),
                        new Vector2f(100, 30)) {
                    @Override
                    public void onKeyRelease(KeyInputEvent evt) {
                        super.onKeyRelease(evt);
                        if (evt.getKeyCode() == KeyInput.KEY_RETURN) {
                            elementCurrentValue.put(labelName, Integer.decode(getText()));
                            screen.getElementById(getUID() + labelName + "TextFieldButton").setText(getText());
                            /**
                             * There, the System have to be call to know a
                             * change have been done.
                             */
//                            hover.setCardName(getText()); 
                            onNumericFieldInput(Integer.decode(getText()));
                            getElementParent().removeChild(this);
                        }
                    }
                };
                field.setType(TextField.Type.NUMERIC);
                field.setMaxLength(3);
                field.setText(getText());
                getElementParent().addChild(field);
            }
        };
        numButton.setText(Integer.toString(baseValue));
        elementList.get(labelName).addChild(numButton);
    }

    private void generateTextField(final String labelName, String baseValue) {
        final String childUID = getUID() + labelName;
        ButtonAdapter textButton = new ButtonAdapter(screen, childUID + "TextFieldButton",
                new Vector2f(elementList.get(labelName).getDimensions().x + 5, 12)) {
            @Override
            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                super.onButtonMouseLeftUp(evt, toggled);
                TextField field = new TextField(screen,
                        new Vector2f(getElementParent().getDimensions().x + 5, 12),
                        new Vector2f(100, 30)) {
                    @Override
                    public void onKeyRelease(KeyInputEvent evt) {
                        super.onKeyRelease(evt);
                        if (evt.getKeyCode() == KeyInput.KEY_RETURN) {
                            elementCurrentValue.put(getElementParent().getText(), getText());
                            screen.getElementById(childUID + "TextFieldButton").setText(getText());
                            onTextFieldInput(getText());
                            getElementParent().removeChild(this);
                        }
                    }
                };
                field.setType(TextField.Type.EXCLUDE_SPECIAL);
                field.setMaxLength(15);
                field.setText(getText());
                getElementParent().addChild(field);
            }
        };
        textButton.setText(baseValue);
        elementList.get(labelName).addChild(textButton);
    }

    private void generateSelectBoxField(final String labelName, Enum<?> baseValue, Enum[] value) {
        SelectBox selectBox = new SelectBox(screen, labelName + "BoxField", new Vector2f(5, 35)) {
            @Override
            public void onChange(int selectedIndex, Object value) {
                elementCurrentValue.put(labelName, value);
                onSelectBoxFieldChange((Enum) value);
            }
        };
        for (Enum e : value) {
            selectBox.addListItem(e.toString(), e);
        }
        elementList.get(labelName).addChild(selectBox);
        selectBox.setSelectedIndex(baseValue.ordinal());
    }

    private void generateSpinner(final String sName, final String labelName, int[] value) {
        Spinner spinner = new Spinner(screen, getUID() + labelName + "Spinner",
                new Vector2f(elementList.get(labelName).getDimensions().x - 22, 85), Spinner.Orientation.HORIZONTAL, true) {
            @Override
            public void onChange(int selectedIndex, String value) {
                elementCurrentValue.put(labelName, value);
                onSpinnerChange(sName, selectedIndex);
            }
        };
        spinner.setStepIntegerRange(value[0], value[1], value[2]);
        spinner.setSelectedIndex(value[3]);
        elementList.get(labelName).addChild(spinner);
    }

    private void generateButton(String name, String triggerName, final int index) {
        ButtonAdapter button = new ButtonAdapter(screen, getUID() + name.replaceAll("\\s", "") + "Button", new Vector2f(10, 10), new Vector2f(130, 30)) {
            @Override
            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                super.onButtonMouseLeftUp(evt, toggled);
                onButtonTrigger(index);
            }
        };
        button.setText(triggerName);
        elementList.get(name.replaceAll("\\s", "")).addChild(button);
    }

    protected final Object getFieldValue(String fieldName) {
        return elementCurrentValue.get(fieldName.replaceAll("\\s", ""));
    }

//    @Override
//    public void cleanup() {
//        super.cleanup();
//        titleName.cleanup();
//        for(Element e : elementList.values()){
//            screen.removeElement(e);
//        }
//    }

    public void clear(){
        for(Element e : elementList.values()){
            screen.removeElement(e);
        }
        
    }
    
    protected void onButtonTrigger(int index) {
    }
    protected void onTextFieldInput(String input) {
    }
    protected void onNumericFieldInput(Integer input) {
    }
    protected void onSelectBoxFieldChange(Enum value) {
    }
    protected void onSpinnerChange(String sTrigger, int currentIndex) {
    }
}

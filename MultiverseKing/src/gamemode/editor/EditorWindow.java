package gamemode.editor;

import com.jme3.input.KeyInput;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector2f;
import java.util.HashMap;
import tonegod.gui.controls.buttons.ButtonAdapter;
import tonegod.gui.controls.buttons.CheckBox;
import tonegod.gui.controls.lists.SelectBox;
import tonegod.gui.controls.lists.Spinner;
import tonegod.gui.controls.text.Label;
import tonegod.gui.controls.text.TextField;
import tonegod.gui.controls.windows.Window;
import tonegod.gui.core.Element;
import tonegod.gui.core.ElementManager;
import tonegod.gui.core.layouts.LayoutHint.VAlign;

/**
 *
 * @author roah
 */
public abstract class EditorWindow {

    protected final ElementManager screen;
    private Vector2f gridSize = new Vector2f(150, 40);
    private String name;
    private Element parent;
    private Window window;
    private HashMap<String, Element> elementList = new HashMap<String, Element>();
    private HashMap<String, Object> elementCurrentValue = new HashMap<String, Object>();

    public EditorWindow(ElementManager screen, Element parent, String name) {
        this.screen = screen;
        this.name = name;
        this.parent = parent;
    }

    /**
     * Generate the window with a grid of sizeX and sizeY, position free.
     * @param sizeX grid size on X.
     * @param sizeY grid size on Y.
     * @param posX on the screen.
     * @param posY on the screen.
     */
    protected final void show(float sizeX, float sizeY, float posX, float posY) {
        sizeX *= gridSize.x;
        sizeY *= gridSize.y;
        window = new Window(screen, getUID(), new Vector2f(posX, posY), new Vector2f(sizeX, sizeY));
//        window.setWindowTitle("     " + name);
        window.setWindowTitle("   "+name);
        window.setIgnoreMouse(true);
        window.getDragBar().setIgnoreMouse(true);
        parent.addChild(window);
        for (Element e : elementList.values()) {
            Vector2f pos = new Vector2f(e.getPosition().x, e.getPosition().y + window.getDragBarHeight());
            e.setPosition(pos);
            window.addChild(e);
        }
    }

    /**
     * Generate the window with a grid of sizeX and sizeY, constraint
     * the position of the window to the parent position.
     * 
     * @param sizeX grid size on X.
     * @param sizeY grid size on Y.
     * @param align window following the parent.
     */
    protected final void show(float sizeX, float sizeY, VAlign align) {
        switch (align) {
            case center:
                show(sizeX, sizeY, parent.getDimensions().x + 5, 0);
                break;
            case bottom:
                show(sizeX, sizeY, 0, parent.getDimensions().y + 65);
                break;
            case top:
                show(sizeX, sizeY, 0, parent.getDimensions().y - 65);
                break;
            default:
                throw new UnsupportedOperationException(align+" Not implemented");
        }
    }

    public Window getWindow() {
        return window;
    }

    protected Vector2f getGridSize() {
        return gridSize;
    }

    private String getUID() {
        return name.replaceAll("\\s", "");
    }

    public final void clear() {
        for (Element e : elementList.values()) {
            screen.removeElement(e);
        }
    }

    public final void detachFromParent() {
        if (window != null && parent != null) {
            parent.removeChild(window);
        }
    }

    protected final CheckBox getCheckBoxField(String labelName) {
        return (CheckBox) elementList.get(generateUID(labelName));
    }

    protected final Element getField(String labelName) {
        String uid = generateUID(labelName);
        return elementList.get(generateUID(uid));
    }

    protected final ButtonAdapter getTextField(String labelName) {
        return (ButtonAdapter) getField(labelName).getChildElementById(getUID() + generateUID(labelName) + "TextFieldButton");
    }
    
    /**
     * Add a button Field to this menu attach to a label.
     *
     * @param labelName field name.
     * @param triggerName No Space && Not more than 7
     * @param index returned index when trigger
     * @param position on the window grid.
     */
    protected final void addButtonField(String labelName, String triggerName, int index, Vector2f position) {
        addButtonField(labelName, triggerName, index, position, Vector2f.ZERO, true);
    }
    /**
     * Add a button Field to this menu attach to a label.
     *
     * @param labelName field name.
     * @param triggerName No Space && Not more than 7
     * @param index returned index when trigger
     * @param position on the window grid.
     * @param offset value to add on top of the position.
     */
    protected final void addButtonField(String labelName, String triggerName, int index, Vector2f position, Vector2f offset) {
        addButtonField(labelName, triggerName, index, position, offset, true);
    }
    /**
     * Add a button field to this menu without label attached to it.
     * 
     * @param triggerName No Space && Not more than 7
     * @param index returned index when trigger
     * @param position on the window grid.
     * @param offset value to add on top of the position. 
     */
    protected final void addButtonField(String triggerName, int index, Vector2f position, Vector2f offset) {
        addButtonField(triggerName, triggerName, index, position, offset, false);
    }
    /**
     * Add a button field to this menu without label attached to it. (no offset)
     * 
     * @param triggerName No Space && Not more than 7
     * @param index returned index when trigger
     * @param position on the window grid.
     */
    protected final void addButtonField(String triggerName, int index, Vector2f position) {
        addButtonField(triggerName, triggerName, index, position, new Vector2f(), false);
    }
    /**
     * 
     * @param labelName field name.
     * @param triggerName No Space && Not more than 7.
     * @param index returned index when trigger.
     * @param position on the window grid.
     * @param offset value to be added on top of the gridPosition.
     */
    protected final void addButtonField(String labelName, String triggerName, int index, Vector2f position, Vector2f offset, boolean addLabel) {
        if (triggerName.contains("\\s")) {
            throw new UnsupportedOperationException("No space Regex allowed in the trigger Name.");
        } else if (triggerName.toCharArray().length > 7) {
            throw new UnsupportedOperationException("Char count in the trigger Name exceeds the allowed amount.");
        }
        String uid = generateUID(labelName);
        if(addLabel){
            elementList.put(uid, generateLabel(labelName, position, offset));
            elementList.get(uid).addChild(generateButton(uid, triggerName, index));
        } else {
            elementList.put(uid, generateButton(triggerName, index, position, offset));
        }
    }
    
    /**
     * Add a checkbox Element to this window.
     * 
     * @param labelName field name.
     * @param active is the checkbox switched on.
     * @param position on the window grid.
     */
    protected final void addCheckBoxField(String labelName, boolean active, Vector2f position) {
        addCheckBoxField(labelName, active, position, Vector2f.ZERO);
    }

    /**
     * Add a checkbox Element to this window.
     * 
     * @param labelName field name.
     * @param active is the checkbox switched on.
     * @param position on the window grid.
     * @param offset value to be added on top of the gridPosition.
     */
    protected final void addCheckBoxField(String labelName, boolean active, Vector2f position, Vector2f offset) {
        String uid = generateUID(labelName);
        CheckBox b = new CheckBox(screen, getUID() + uid + "CheckBox",
                new Vector2f(position.x + offset.x, position.y + offset.y));
        b.setLabelText(labelName);
        elementList.put(uid, b);
    }

    /**
     * Add a spinner Element to this menu. 
     * value[0] = min, value[1] = max,
     * value[2] = step, value[3] = current.
     *
     * @param labelName field name.
     * @param value first value to show.
     * @param position on the window grid.
     */
    protected final void addSpinnerField(String labelName, int[] value, Vector2f position) {
        addSpinnerField(labelName, value, position, Vector2f.ZERO);
    }

    /**
     * Add a spinner Element to this menu. 
     * value[0] = min, value[1] = max,
     * value[2] = step, value[3] = current.
     * 
     * @param labelName field name.
     * @param value first value to show.
     * @param position on the window grid.
     * @param offset value to be added on top of the gridPosition.
     */
    protected final void addSpinnerField(String labelName, int[] value, Vector2f position, Vector2f offset) {
        String uid = generateUID(labelName);
        elementList.put(uid, generateLabel(labelName, position, offset));
        generateSpinner(labelName, uid, value);
    }

    /**
     * Add a Text Field element to this menu.
     *
     * @param labelName field name.
     * @param baseValue first value to show.
     * @param position on the window grid.
     */
    protected final void addEditableTextField(String labelName, String baseValue, Vector2f position) {
        addEditableTextField(labelName, baseValue, position, Vector2f.ZERO);
    }
    /**
     * Add a Text Field element to this menu.
     *
     * @param labelName field name.
     * @param baseValue first value to show.
     * @param position on the window grid.
     * @param offset value to be added on top of the gridPosition.
     */
    protected final void addEditableTextField(String labelName, String baseValue, Vector2f position, Vector2f offset) {
        String uid = generateUID(labelName);
        elementList.put(uid, generateLabel(labelName, position, offset));
        generateTextField(uid, baseValue);
    }

    /**
     * Add a Text Field element to this menu, limited to number as input.
     *
     * @param labelName field name.
     * @param baseValue first value to show.
     * @param position on the window grid.
     */
    protected final void addEditableNumericField(String labelName, int baseValue, Vector2f position) {
        addEditableNumericField(labelName, baseValue, position, Vector2f.ZERO);
    }

    /**
     * Add a Text Field element to this menu, limited to number as input.
     *
     * @param labelName field name.
     * @param baseValue first value to show.
     * @param position on the window grid.
     * @param offset value to be added on top of the gridPosition.
     */
    protected final void addEditableNumericField(String labelName, int baseValue, Vector2f position, Vector2f offset) {
        String uid = generateUID(labelName);
        elementList.put(uid, generateLabel(labelName, position, offset));
        generateNumericField(uid, baseValue);
    }

    /**
     * Add a selectionList Element based on a enum to this menu.
     *
     * @param labelName field name.
     * @param baseValue first value to show.
     * @param value all possible value.
     * @param position on the window grid.
     */
    protected final void addEditableSelectionField(String labelName, Enum<?> baseValue, Enum[] value, Vector2f position) {
        addEditableSelectionField(labelName, baseValue, value, position, Vector2f.ZERO);
    }

    /**
     * Add a selectionList Element based on a enum to this menu.
     *
     * @param labelName field name.
     * @param baseValue first value to show.
     * @param value all possible value.
     * @param position on the window grid.
     * @param offset value to be added on top of the gridPosition.
     */
    protected final void addEditableSelectionField(String labelName, Enum<?> baseValue,
            Enum[] value, Vector2f position, Vector2f offset) {
        String uid = generateUID(labelName);
        elementList.put(uid, generateLabel(labelName, position, offset));
        generateSelectBoxField(uid, baseValue, value);
    }

    private String generateUID(String labelName) {
        return labelName.replaceAll("\\s", "");
    }

    private Label generateLabel(String labelName, Vector2f position, Vector2f offset) {
        Label label = new Label(screen, getUID() + labelName.replaceAll("\\s", "") + "Label",
                new Vector2f((getGridSize().x * position.x) + 10 + offset.x,
                (getGridSize().y * position.y) + 10 + offset.y),
                new Vector2f(labelName.toCharArray().length * 15, 35));
        label.setText(labelName + " : ");
        return label;
    }

    private void generateNumericField(final String labelUID, int baseValue) {
        final String childUID = getUID() + labelUID;
        ButtonAdapter numButton = new ButtonAdapter(screen, childUID + "NumericFieldButton",
                new Vector2f(elementList.get(labelUID).getDimensions().x + 5, 5)) {
            @Override
            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                super.onButtonMouseLeftUp(evt, toggled);
                TextField field = new TextField(screen, getUID() + labelUID + "NumericField",
                        new Vector2f(getPosition().x, getPosition().y + 5),
                        new Vector2f(getWidth(), 30)) {
                    @Override
                    public void onKeyRelease(KeyInputEvent evt) {
                        super.onKeyRelease(evt);
                        if (evt.getKeyCode() == KeyInput.KEY_RETURN) {
                            elementCurrentValue.put(labelUID, Integer.decode(getText()));
                            screen.getElementById(childUID + "NumericFieldButton").setText(getText());
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
        elementList.get(labelUID).addChild(numButton);
        elementCurrentValue.put(generateUID(labelUID), baseValue);
    }

    private void generateTextField(final String labelUID, String baseValue) {
        final String childUID = getUID() + labelUID;
        ButtonAdapter textButton = new ButtonAdapter(screen, childUID + "TextFieldButton",
                new Vector2f(elementList.get(labelUID).getDimensions().x + 5, 5)) {
            @Override
            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                super.onButtonMouseLeftUp(evt, toggled);
                TextField field = new TextField(screen,
                        new Vector2f(getPosition().x, getPosition().y + 5),
                        new Vector2f(getWidth(), 30)) {
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
                field.setMaxLength(13);
                field.setText(getText());
                getElementParent().addChild(field);
            }
        };
        textButton.setText(baseValue);
        elementList.get(labelUID).addChild(textButton);
        elementCurrentValue.put(labelUID, baseValue);
    }

    private void generateSelectBoxField(final String labelUID, Enum<?> baseValue, Enum[] value) {
        SelectBox selectBox = new SelectBox(screen, getUID() + labelUID + "BoxField", new Vector2f(5, 35)) {
            @Override
            public void onChange(int selectedIndex, Object value) {
                elementCurrentValue.put(labelUID, value);
                onSelectBoxFieldChange((Enum) value);
            }
        };
        for (Enum e : value) {
            selectBox.addListItem(e.toString(), e);
        }
        elementList.get(labelUID).addChild(selectBox);
        selectBox.setSelectedIndex(baseValue.ordinal());
        elementCurrentValue.put(labelUID, baseValue);
    }

    private void generateSpinner(final String labelName, final String labelUID, int[] value) {
        Spinner spinner = new Spinner(screen, getUID() + labelUID + "Spinner",
                new Vector2f(elementList.get(labelUID).getWidth() - 12, 7), Spinner.Orientation.HORIZONTAL, true) {
            @Override
            public void onChange(int selectedIndex, String value) {
                elementCurrentValue.put(labelUID, value);
                onSpinnerChange(labelName, selectedIndex);
            }
        };
        spinner.setStepIntegerRange(value[0], value[1], value[2]);
        spinner.setSelectedIndex(value[3]);
        elementList.get(labelUID).addChild(spinner);
        elementCurrentValue.put(labelUID, value[3]);
    }

    private ButtonAdapter generateButton(String name, String triggerName, final int index) {
        ButtonAdapter button = new ButtonAdapter(screen, getUID() + name + "Button",
                new Vector2f(5, 35), new Vector2f(130, 30)) {
            @Override
            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                super.onButtonMouseLeftUp(evt, toggled);
                onButtonTrigger(index);
            }
        };
        button.setText(triggerName);
        return button;
    }

    private ButtonAdapter generateButton(String triggerName, final int index, Vector2f position, Vector2f offset) {
        ButtonAdapter button = new ButtonAdapter(screen, getUID() + triggerName + "Button",
                new Vector2f((getGridSize().x * position.x) + 10 + offset.x,
                (getGridSize().y * position.y) + 10 + offset.y)) {
            @Override
            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                super.onButtonMouseLeftUp(evt, toggled);
                onButtonTrigger(index);
            }
        };
        button.setText(triggerName);
        return button;
    }
    
    protected final ButtonAdapter getNumericField(String labelName) {
        return (ButtonAdapter) getField(labelName).getChildElementById(getUID() + generateUID(labelName) + "NumericFieldButton");
    }

    protected final Spinner getSpinnerField(String labelName) {
        return (Spinner) getField(labelName).getChildElementById(getUID() + generateUID(labelName) + "Spinner");
    }

    protected final SelectBox getSelectBoxField(String labelName) {
        return (SelectBox) getField(labelName).getChildElementById(getUID() + generateUID(labelName) + "BoxField");
    }

    protected final Object getFieldValue(String fieldName) {
        return elementCurrentValue.get(fieldName.replaceAll("\\s", ""));
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

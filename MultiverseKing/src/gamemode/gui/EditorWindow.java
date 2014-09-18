package gamemode.gui;

import com.jme3.input.KeyInput;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
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

/**
 *
 * @author roah
 */
public abstract class EditorWindow {

    protected final ElementManager screen;
    private Vector2f gridSize = new Vector2f(230, 30);//min value X == 230
    private String name;
    private Element parent;
    private Window window;
    private HashMap<String, Element> elementList = new HashMap<String, Element>();
    private int btnListCount = 0;
    /**
     * Max element count on the selected alignment.
     */
    private int windowMaxAlignElement = 1;
    private Align windowAlignement = Align.Horizontal;

    protected int getelementListCount() {
        return elementList.size();
    }

    public EditorWindow(ElementManager screen, Element parent, String name) {
        this.screen = screen;
        this.name = name;
        this.parent = parent;
    }

    // <editor-fold defaultstate="collapsed" desc="Show Method">
    /**
     * Show the window on a free position on the screen.
     *
     * @param size
     * @param position
     */
    protected final void show(Vector2f size, Vector2f position) {
        size.x = (size.x + 1) * gridSize.x;
        size.y = (size.y + 1) * gridSize.y;
        window = new Window(screen, getUID(), new Vector2f(position.x - (size.x / 2f), position.y - (size.y / 2f)), size);
        System.err.println(window.getMinDimensions().y);
//        window.setWindowTitle("     " + name);
        window.setWindowTitle("   " + name);
        window.setIgnoreMouse(true);
        window.getDragBar().setIgnoreMouse(true);
        if (parent == null) {
            screen.addElement(window);
        } else {
            parent.addChild(window);
        }
        int row = 0;
        int column = 0;
        for (Element e : elementList.values()) {
            if(windowAlignement == Align.Horizontal){
                if(row >= windowMaxAlignElement){
                    row = 0;
                    column++;
                }
            } else if (windowAlignement == Align.Vertical) {
                if(column >= windowMaxAlignElement){
                    column = 0;
                    row++;
                }
            }
            Vector2f pos = new Vector2f(e.getPosition().x+(row*gridSize.x), e.getPosition().y + window.getDragBarHeight() +(column*gridSize.y));
            e.setPosition(pos);
            window.addChild(e);
            if(windowAlignement == Align.Horizontal){
                row++;
            } else if (windowAlignement == Align.Vertical) {
                column++;
            }
        }
    }

    /**
     * Show the window constraint to the screen anchor to the specifiate
     * alignement, Set to null on both alignement to put the window on the
     * center of the screen.
     *
     * @param size
     * @param hAlign Horizontal alignement to use
     * @param vAlign Vertical alignement to use
     */
    protected final void show(Vector2f size, VAlign vAlign, HAlign hAlign) {
        Vector2f position = new Vector2f();
        if (vAlign == null && hAlign == null) {
            position.x = screen.getWidth() / 2 - size.x / 2;
            position.y = screen.getHeight() / 2 - size.y / 2;
            show(size, position);
        } else {
            if (vAlign != null) {
                switch (vAlign) {
                    case bottom:
                        position.y = screen.getHeight();
                        break;
                    case top:
                        position.y = 0;
                        break;
                    default:
                        throw new UnsupportedOperationException(vAlign + " Not implemented");
                }
            }
            if (hAlign != null) {
                switch (hAlign) {
                    case left:
                        position.x = 0;
                        break;
                    case right:
                        position.x = screen.getWidth();
                        break;
                    default:
                        throw new UnsupportedOperationException(hAlign + " Not implemented");
                }
            }
            show(size, position);
        }
    }

    /**
     * Show the window constraint to the parent following Horizontal alignement.
     *
     * @param size
     * @param hAlign Horizontal alignement to use
     */
    protected final void showConstrainToParent(Vector2f size, HAlign hAlign) {
        showConstrainToParent(size, null, hAlign);
    }

    /**
     * Show the window constraint to the parent following Vectical alignement.
     *
     * @param size
     * @param vAlign Vectical alignement to use
     */
    protected final void showConstrainToParent(Vector2f size, VAlign vAlign) {
        showConstrainToParent(size, vAlign, null);
    }

    /**
     * Generate the window with a grid of sizeX and sizeY, constraint the
     * position of the window to the parent position.
     *
     * @param sizeX grid size on X.
     * @param sizeY grid size on Y.
     * @param align window following the parent.
     */
    protected final void showConstrainToParent(Vector2f size, VAlign vAlign, HAlign hAlign) {
        if (parent != null) {
            Vector2f position = new Vector2f();
            if (vAlign == null && hAlign == null) {
                show(size, position);
            } else {
                if (vAlign != null) {
                    switch (vAlign) {
                        case bottom:
                            position.y = parent.getDimensions().y + 65;
                            break;
                        case top:
                            position.y = parent.getDimensions().y - 65;
                            break;
                        default:
                            throw new UnsupportedOperationException(vAlign + " Not implemented");
                    }
                }
                if (hAlign != null) {
                    switch (hAlign) {
                        case left:
                            position.x = -size.x;
                            break;
                        case right:
                            position.x = parent.getDimensions().x + 5;
                            break;
                        default:
                            throw new UnsupportedOperationException(hAlign + " Not implemented");
                    }
                }
                show(size, position);
            }
        } else {
            throw new UnsupportedOperationException("There is no parent to align the element to.");
        }
    }
    // </editor-fold>

    public String getUID() {
        return generateUID(name);
    }

    public Window getWindow() {
        return window;
    }

    protected Vector2f getGridSize() {
        return gridSize;
    }

    public final void removeFromScreen() {
        if (window != null && parent != null) {
            parent.removeChild(window);
        } else if (window != null && parent == null) {
            screen.removeElement(window);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Add Method">
    /**
     * Add a button Field to this menu attach to a label. (no offset)
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
        if (addLabel) {
            elementList.put(uid, generateLabel(labelName, position, offset));
            elementList.get(uid).addChild(generateButton(uid+triggerName, index, Vector2f.ZERO, Vector2f.ZERO));
        } else {
            elementList.put(uid, generateButton(triggerName, index, position, offset));
        }
    }

    protected final void addButtonList(String[] triggersNames, HAlign hAlign) {
        String UID = "btnList" + btnListCount;
        Element holder = new Element(screen, UID, new Vector2f(0, -5), new Vector2f(), Vector4f.ZERO, null);
        holder.setAsContainerOnly();
        int i = 0;
        float posX = 0f;
        for (String triggerName : triggersNames) {
            ButtonAdapter btn = generateButton(triggerName, i, Vector2f.ZERO, Vector2f.ZERO);
            btn.setPosition(posX, btn.getPosition().y);
            posX += btn.getWidth();

            holder.addChild(btn);
            i++;
        }
        if (hAlign == HAlign.right) {
            holder.setPosition(gridSize.x - (posX + 10), holder.getPosition().y);
        }
        elementList.put(UID, holder);
        btnListCount++;
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
     * Add a spinner Element to this menu. value[0] = min, value[1] = max,
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
     * Add a spinner Element to this menu. value[0] = min, value[1] = max,
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

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Generate Method">
    private String generateUID(String labelName) {
        return labelName.replaceAll("\\s", "");
    }

    private Label generateLabel(String labelName, Vector2f position, Vector2f offset) {
        Label label = new Label(screen, getUID() + labelName.replaceAll("\\s", "") + "Label",
                new Vector2f((getGridSize().x * position.x) + 10 + offset.x,
                (getGridSize().y * position.y) + offset.y),
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
    }

    private void generateTextField(final String labelUID, String baseValue) {
        final String childUID = getUID() + labelUID;

        TextField field = new TextField(screen, childUID+"TextField",
                new Vector2f(elementList.get(labelUID).getWidth(), 10),
                new Vector2f(gridSize.x - (elementList.get(labelUID).getWidth() + 20), 20)) {
            @Override
            public void onKeyRelease(KeyInputEvent evt) {
                super.onKeyRelease(evt);
                if (evt.getKeyCode() == KeyInput.KEY_RETURN) {
                    onTextFieldInput(labelUID, getText(), true);
                } else if(evt.getKeyCode() == KeyInput.KEY_ESCAPE){
                    onTextFieldInput(labelUID, getText(), false);
                }
            }
        };
        field.setType(TextField.Type.EXCLUDE_SPECIAL);
        field.setMaxLength(13);
        if(baseValue != null){
            field.setText(baseValue);
        }

        elementList.get(labelUID).addChild(field);
    }

    private void generateSelectBoxField(final String labelUID, Enum<?> baseValue, Enum[] value) {
        SelectBox selectBox = new SelectBox(screen, getUID() + labelUID + "BoxField", new Vector2f(5, 35)) {
            @Override
            public void onChange(int selectedIndex, Object value) {
                onSelectBoxFieldChange((Enum) value);
            }
        };
        for (Enum e : value) {
            selectBox.addListItem(e.toString(), e);
        }
        elementList.get(labelUID).addChild(selectBox);
        selectBox.setSelectedIndex(baseValue.ordinal());
    }

    private void generateSpinner(final String labelName, final String labelUID, int[] value) {
        Spinner spinner = new Spinner(screen, getUID() + labelUID + "Spinner",
                new Vector2f(elementList.get(labelUID).getWidth() - 12, 7), Spinner.Orientation.HORIZONTAL, true) {
            @Override
            public void onChange(int selectedIndex, String value) {
                onSpinnerChange(labelName, selectedIndex);
            }
        };
        spinner.setStepIntegerRange(value[0], value[1], value[2]);
        spinner.setSelectedIndex(value[3]);
        elementList.get(labelUID).addChild(spinner);
    }
    
    private ButtonAdapter generateButton(String triggerName, final int index, Vector2f position, Vector2f offset) {
        ButtonAdapter button = new ButtonAdapter(screen, getUID() + triggerName + "Button",
                new Vector2f((getGridSize().x * position.x) + 10 + offset.x,
                (getGridSize().y * position.y) + 10 + offset.y),
                new Vector2f(triggerName.length() * 10, 20)) {
            @Override
            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                super.onButtonMouseLeftUp(evt, toggled);
                onButtonTrigger(index);
            }
        };
        button.setText(triggerName);
        return button;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="get Fields Method">
    protected final Element getField(String labelName) {
        return elementList.get(generateUID(labelName));
    }

    protected final CheckBox getCheckBoxField(String labelName) {
        return (CheckBox) elementList.get(generateUID(labelName));
    }

    protected final TextField getTextField(String labelName) {
        return (TextField) getField(labelName).getChildElementById(getUID() + generateUID(labelName) + "TextField");
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
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Trigger Method">
    protected void onButtonTrigger(int index) {
    }

    protected void onTextFieldInput(String UID, String input, boolean triggerOn) {
    }

    protected void onNumericFieldInput(Integer input) {
    }

    protected void onSelectBoxFieldChange(Enum value) {
    }

    protected void onSpinnerChange(String sTrigger, int currentIndex) {
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Exposed Enum">
    public enum VAlign {

        top,
        bottom;
    }

    public enum HAlign {

        left,
        right;
    }

    public enum Align {

        Horizontal,
        Vertical;
    }
    // </editor-fold>
}

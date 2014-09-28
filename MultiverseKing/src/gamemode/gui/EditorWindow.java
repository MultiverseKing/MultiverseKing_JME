package gamemode.gui;

import com.jme3.input.KeyInput;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import java.util.LinkedHashMap;
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
    private Vector2f gridSize;// = new Vector2f(230, 20);//min value X == 230
    private String name;
    private Element parent;
    private Window window;
    private LinkedHashMap<String, Element> elementList = new LinkedHashMap<String, Element>();
    private int btnListCount = 0;
    /**
     * Max element count on the selected alignment.
     */
    private int elementAlignMaxCount;
    private Align windowElementAlignement;

    protected int getelementListCount() {
        return elementList.size();
    }

    /**
     * Generate a new window with: size (230, 20), Align.Horizontal && Element
     * max count of 1.
     *
     * @param parent window
     * @param name window name
     */
    public EditorWindow(ElementManager screen, Element parent, String name) {
        this(screen, parent, name, new Vector2f(230, 20), Align.Horizontal, 1);
    }

    /**
     * Generate a new window with : Align.Horizontal && Element max count of 1.
     *
     * @param parent window
     * @param gridSize element size on X and Y.
     */
    public EditorWindow(ElementManager screen, Element parent, String name, Vector2f gridSize) {
        this(screen, parent, name, gridSize, Align.Horizontal, 1);
    }

    /**
     *
     * @param parent window
     * @param gridSize element size on X and Y
     * @param windowElementAlignement Alignement to use to position each
     * element.
     * @param elementAlignMaxCount element count on selected alignement.
     */
    public EditorWindow(ElementManager screen, Element parent, String name, Vector2f gridSize, Align windowElementAlignement, int elementAlignMaxCount) {
        this.screen = screen;
        this.name = name;
        this.parent = parent;
        this.windowElementAlignement = windowElementAlignement;
        this.elementAlignMaxCount = elementAlignMaxCount;
        if (gridSize.x < 230) {
            gridSize.x = 230;
        }
        if (gridSize.y < 20) {
            gridSize.y = 20;
        }
        this.gridSize = gridSize;
    }

    // <editor-fold defaultstate="collapsed" desc="Show Method">
    /**
     * Show the window on a free position on the screen.
     *
     * @param size
     * @param position
     */
    private void show(Vector2f size, Vector2f position) {
        size.y += 25;
//        window = new Window(screen, getUID(), new Vector2f(position.x - (size.x / 2f), position.y - (size.y / 2f)), size);
        window = new Window(screen, getUID(), position, size);
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
            if (windowElementAlignement == Align.Horizontal) {
                if (column >= elementAlignMaxCount) {
                    column = 0;
                    row++;
                }
            } else if (windowElementAlignement == Align.Vertical) {
                if (row >= elementAlignMaxCount) {
                    row = 0;
                    column++;
                }
            } else {
                throw new UnsupportedOperationException(windowElementAlignement + " : is not a supported type.");
            }
            Vector2f pos = new Vector2f(e.getPosition().x + (column * gridSize.x), e.getPosition().y + window.getDragBarHeight() + (row * gridSize.y));
            e.setPosition(pos);
            window.addChild(e);
            if (windowElementAlignement == Align.Horizontal) {
                row++;
            } else if (windowElementAlignement == Align.Vertical) {
                column++;
            } else {
                throw new UnsupportedOperationException(windowElementAlignement + " : is not a supported type.");
            }
        }
    }

    /**
     * Show the window constraint to the screen anchor to the specifiate
     * alignement, Set to null on both alignement to put the window on the
     * center of the screen.
     *
     * @param hAlign Horizontal alignement to use
     * @param vAlign Vertical alignement to use
     */
    protected final void show(VAlign vAlign, HAlign hAlign) {
        int column, row;
        if (windowElementAlignement == Align.Horizontal) {
            column = elementAlignMaxCount;
            row = (int) FastMath.ceil(elementList.size() / elementAlignMaxCount);
        } else if (windowElementAlignement == Align.Vertical) {
            row = elementAlignMaxCount;
            column = (int) FastMath.ceil(elementList.size() / elementAlignMaxCount);
        } else {
            throw new UnsupportedOperationException(windowElementAlignement + " is not a supported allignement.");
        }
        Vector2f size = new Vector2f(column * gridSize.x, (row + 1) * gridSize.y);

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
     * @param triggerName
     */
    protected final void addButtonField(String labelName) {
        addButtonField(labelName, labelName, HAlign.left, Vector2f.ZERO, true);
    }

    /**
     * Add a button Field to this menu attach to a label.
     *
     * @param labelName field name.
     * @param triggerName
     * @param offset value to add on top of the position.
     */
    protected final void addButtonField(String labelName, String triggerName, Vector2f offset) {
        addButtonField(labelName, triggerName, HAlign.left, offset, true);
    }

    /**
     * Add a button field to this menu without label attached to it.
     *
     * @param triggerName
     * @param offset value to add on top of the position.
     */
    protected final void addButtonField(String triggerName, HAlign hAlign, Vector2f offset) {
        addButtonField(triggerName, triggerName, hAlign, offset, false);
    }

    /**
     * Add a button field to this menu without label attached to it.
     *
     * @param triggerName
     * @param offset value to add on top of the position.
     */
    protected final void addButtonField(String triggerName, HAlign hAlign) {
        addButtonField(triggerName, triggerName, hAlign, Vector2f.ZERO, false);
    }

    /**
     *
     * @param labelName field name.
     * @param triggerName No Space && Not more than 7.
     * @param index returned index when trigger.
     * @param offset value to be added on top of the gridPosition.
     */
    protected final void addButtonField(String labelName, String triggerName, HAlign hAlign, Vector2f offset, boolean addLabel) {
        String uid = generateUID(labelName);
        if (addLabel) {
            elementList.put(uid, generateLabel(labelName, hAlign, offset));
            elementList.get(uid).addChild(generateButton(uid + triggerName, hAlign, Vector2f.ZERO));
        } else {
            elementList.put(uid, generateButton(triggerName, hAlign, offset));
        }
    }

    protected final void addButtonList(String[] triggersNames, HAlign hAlign) {
        String UID = name + "btnList" + btnListCount;
        Element holder = new Element(screen, UID, Vector2f.ZERO, Vector2f.ZERO, Vector4f.ZERO, null);
        holder.setAsContainerOnly();
        int i = 0;
        float posX = 0f;
        for (String triggerName : triggersNames) {
            ButtonAdapter btn = generateButton(triggerName, HAlign.left, Vector2f.ZERO);
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
     */
    protected final void addCheckBoxField(String labelName, boolean active) {
        addCheckBoxField(labelName, active, Vector2f.ZERO);
    }

    /**
     * Add a checkbox Element to this window.
     *
     * @param labelName field name.
     * @param active is the checkbox switched on.
     * @param offset value to be added on top of the gridPosition.
     */
    protected final void addCheckBoxField(String labelName, boolean active, Vector2f offset) {
        String uid = generateUID(labelName);
        CheckBox b = new CheckBox(screen, getUID() + uid + "CheckBox", Vector2f.ZERO);
        b.setLabelText(labelName);
        elementList.put(uid, b);
    }

    /**
     * Add a spinner Element to this menu. value[0] = min, value[1] = max,
     * value[2] = step, value[3] = current.
     *
     * @param labelName field name.
     * @param value first value to show.
     */
    protected final void addSpinnerField(String labelName, int[] value, HAlign hAlign) {
        addSpinnerField(labelName, value, hAlign, Vector2f.ZERO);
    }

    /**
     * Add a spinner Element to this menu. value[0] = min, value[1] = max,
     * value[2] = step, value[3] = current.
     *
     * @param labelName field name.
     * @param value first value to show.
     * @param offset value to be added on top of the gridPosition.
     */
    protected final void addSpinnerField(String labelName, int[] value, HAlign hAlign, Vector2f offset) {
        String uid = generateUID(labelName);
        elementList.put(uid, generateLabel(labelName, hAlign, offset));
        generateSpinner(labelName, uid, value);
    }

    /**
     * Add a Text Field element to this menu.
     *
     * @param labelName field name.
     * @param baseValue first value to show.
     */
    protected final void addEditableTextField(String labelName, String baseValue, HAlign hAlign) {
        addEditableTextField(labelName, baseValue, hAlign, Vector2f.ZERO);
    }

    /**
     * Add a Text Field element to this menu.
     *
     * @param labelName field name.
     * @param baseValue first value to show.
     * @param offset value to be added on top of the gridPosition.
     */
    protected final void addEditableTextField(String labelName, String baseValue, HAlign hAlign, Vector2f offset) {
        String uid = generateUID(labelName);
        elementList.put(uid, generateLabel(labelName, hAlign, offset));
        generateTextField(uid, baseValue);
    }

    /**
     * Add a Text Field element to this menu, limited to number as input.
     *
     * @param labelName field name.
     * @param baseValue first value to show.
     */
    protected final void addEditableNumericField(String labelName, int baseValue, HAlign hAlign) {
        addEditableNumericField(labelName, baseValue, hAlign, Vector2f.ZERO);
    }

    /**
     * Add a Text Field element to this menu, limited to number as input.
     *
     * @param labelName field name.
     * @param baseValue first value to show.
     * @param offset value to be added on top of the gridPosition.
     */
    protected final void addEditableNumericField(String labelName, int baseValue, HAlign hAlign, Vector2f offset) {
        String uid = generateUID(labelName);
        elementList.put(uid, generateLabel(labelName, hAlign, offset));
        generateNumericField(uid, baseValue);
    }

    /**
     * Add a selectionList Element based on a enum to this menu.
     *
     * @param labelName field name.
     * @param baseValue first value to show.
     * @param value all possible value.
     */
    protected final void addEditableSelectionField(String labelName, Enum<?> baseValue, Enum[] value, HAlign hAlign) {
        addEditableSelectionField(labelName, baseValue, value, hAlign, Vector2f.ZERO);
    }

    /**
     * Add a selectionList Element based on a enum to this menu.
     *
     * @param labelName field name.
     * @param baseValue first value to show.
     * @param value all possible value.
     * @param offset value to be added on top of the gridPosition.
     */
    protected final void addEditableSelectionField(String labelName, Enum<?> baseValue,
            Enum[] value, HAlign hAlign, Vector2f offset) {
        String uid = generateUID(labelName);
        elementList.put(uid, generateLabel(labelName, hAlign, offset));
        generateSelectBoxField(uid, baseValue, value);
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Generate Method">
    private String generateUID(String labelName) {
        return labelName.replaceAll("\\s", "");
    }

    private Label generateLabel(String labelName, HAlign hAlign, Vector2f offset) {
        if (hAlign.equals(HAlign.left)) {
            Label label = new Label(screen, getUID() + labelName.replaceAll("\\s", "") + "Label",
                    new Vector2f(10 + offset.x, offset.y),
                    new Vector2f(labelName.toCharArray().length * 15, gridSize.y));
            label.setText(labelName + " : ");
            return label;
        } else if (hAlign.equals(HAlign.right)) {
            Label label = new Label(screen, getUID() + labelName.replaceAll("\\s", "") + "Label",
                    new Vector2f(gridSize.x - 10 - offset.x - (labelName.toCharArray().length * 15), offset.y),
                    new Vector2f(labelName.toCharArray().length * 15, gridSize.y));
            label.setText(" : " + labelName);
            return label;
        } else {
            throw new UnsupportedOperationException(hAlign + " : Cannot be used with a label or isn't implemented.");
        }
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

        TextField field = new TextField(screen, childUID + "TextField",
                new Vector2f(elementList.get(labelUID).getWidth(), 4),
                new Vector2f(gridSize.x - (elementList.get(labelUID).getWidth() + 20), gridSize.y)) {
            @Override
            public void onKeyRelease(KeyInputEvent evt) {
                super.onKeyRelease(evt);
                if (evt.getKeyCode() == KeyInput.KEY_RETURN) {
                    onTextFieldInput(labelUID, getText(), true);
                } else if (evt.getKeyCode() == KeyInput.KEY_ESCAPE) {
                    onTextFieldInput(labelUID, getText(), false);
                }
            }
        };
        field.setType(TextField.Type.EXCLUDE_SPECIAL);
        field.setMaxLength(13);
        if (baseValue != null) {
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

    private ButtonAdapter generateButton(final String triggerName, HAlign hAlign, Vector2f offset) {
        ButtonAdapter button = new ButtonAdapter(screen, getUID() + triggerName + "Button",
                new Vector2f(), new Vector2f(triggerName.length() * 10, gridSize.y)) {
            @Override
            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                super.onButtonMouseLeftUp(evt, toggled);
                onButtonTrigger(getText());
            }
        };
        button.setText(triggerName);
        switch (hAlign) {
            case left:
                button.setPosition(new Vector2f(10 + offset.x, 10 + offset.y));
                return button;
            case right:
                button.setPosition(new Vector2f(gridSize.x - (10 + offset.x + button.getDimensions().x), 10 + offset.y));
                return button;
            case full:
                button.setDimensions(new Vector2f(gridSize.x - 20, button.getDimensions().y));
                button.setPosition(new Vector2f(10, 10 + offset.y));
                return button;
            default:
                throw new UnsupportedOperationException(hAlign + " isn't supported.");
        }
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
    protected void onButtonTrigger(String label) {
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
        right,
        full;
    }

    public enum Align {

        Horizontal,
        Vertical;
    }
    // </editor-fold>
}

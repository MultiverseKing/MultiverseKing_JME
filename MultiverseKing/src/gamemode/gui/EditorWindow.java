package gamemode.gui;

import com.jme3.input.KeyInput;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import java.util.ArrayList;
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
import utility.Vector2Int;

/**
 *
 * @author roah
 */
public abstract class EditorWindow {

    protected final ElementManager screen;
    private float spacement = 5;
    private Vector2f gridSize;// = new Vector2f(230, 20);//min value X == 230
    private String name;
    private Element parent;
    private Window window = null;
    private LinkedHashMap<String, Element> elementList = new LinkedHashMap<String, Element>();
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
     * Generate a new window with: size (230, 20), Align.Horizontal && Element
     * max count of 1.
     *
     * @param parent window
     * @param name window name
     */
    public EditorWindow(ElementManager screen, Element parent, String name, Align align, int alignElement) {
        this(screen, parent, name, new Vector2f(230, 20), align, alignElement);
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
     * @param position
     */
    public void show(Vector2f position) {
        Vector2f size = getWindowSize();
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
            if (e != null) {
                Vector2f pos = new Vector2f(e.getPosition().x + (column * gridSize.x),
                        + e.getPosition().y + window.getDragBarHeight() + (row * gridSize.y));
                e.setPosition(pos);
                window.addChild(e);
            }
            if (windowElementAlignement == Align.Horizontal) {
                column++;
            } else if (windowElementAlignement == Align.Vertical) {
                row++;
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
        Vector2f size = getWindowSize();
        Vector2f position = new Vector2f();
        if (vAlign == null && hAlign == null) {
            position.x = screen.getWidth() / 2 - size.x / 2;
            position.y = screen.getHeight() / 2 - size.y / 2;
            show(position);
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
            show(position);
        }
    }

    /**
     * Generate the window with a grid of sizeX and sizeY, constraint the
     * position of the window to the parent position.
     *
     * @param sizeX grid size on X.
     * @param sizeY grid size on Y.
     * @param align window following the parent.
     */
    protected final void showConstrainToParent(VAlign vAlign, HAlign hAlign) {
        Vector2f size = getWindowSize();
        if (parent != null) {
            Vector2f position = new Vector2f();
            if (vAlign == null && hAlign == null) {
                show(position);
            } else {
                if (vAlign != null) {
                    switch (vAlign) {
                        case bottom:
                            position.y = parent.getDimensions().y;
                            break;
                        case top:
                            position.y = parent.getDimensions().y - 65;
                            break;
                        case center:
                            position.y = -(size.y - parent.getDimensions().y);
                        default:
                            throw new UnsupportedOperationException(vAlign + " Not implemented");
                    }
                }
                if (hAlign != null) {
                    switch (hAlign) {
                        case left:
                            if (vAlign != null && vAlign.equals(VAlign.center)) {
                                position.x = -size.x;
                            }
                            break;
                        case right:
                            if (vAlign != null && vAlign.equals(VAlign.center)) {
                                position.x = parent.getDimensions().x;
                            } else {
                                position.x = parent.getDimensions().x - size.x;
                            }
                            break;
                        default:
                            throw new UnsupportedOperationException(hAlign + " Not implemented");
                    }
                } else {
                    position.x = parent.getDimensions().x / 2 - size.x / 2;
                }
                show(position);
            }
        } else {
            throw new UnsupportedOperationException("There is no parent to align the element to.");
        }
    }

    private Vector2f getWindowSize() {
        int column, row;
        if (windowElementAlignement == Align.Horizontal) {
            column = elementAlignMaxCount;
            row = (int) FastMath.ceil((float) elementList.size() / (float) elementAlignMaxCount);
        } else if (windowElementAlignement == Align.Vertical) {
            row = elementAlignMaxCount;
            column = (int) FastMath.ceil((float) elementList.size() / (float) elementAlignMaxCount);
        } else {
            throw new UnsupportedOperationException(windowElementAlignement + " is not a supported allignement.");
        }
        return new Vector2f(column * gridSize.x + (spacement * 2), row * gridSize.y + (3 * row + 5));
    }

    public void setVisible() {
        if (window != null) {
            window.show();
        } else {
            System.err.println(getUID() + "Can't be set to visible, window does not exist.");
        }
    }

    public void hide() {
        if (window != null) {
            window.hide();
        } else {
            System.err.println(getUID() + "Can't be hided, window does not exist.");
        }
    }

    public boolean isVisible() {
        if (window != null) {
            return window.getIsVisible();
        }
        return false;
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
     * Add an empty space into the grid.
     */
    protected final void addSpace() {
        elementList.put(generateUID(getUID() + spacement + "_" + elementList.size()), null);
    }

    /**
     * Add a button Field to this menu attach to a label. (no offset)
     */
    protected final void addButtonField(String labelName) {
        addButtonField(labelName, labelName, HAlign.full, Vector2f.ZERO, false);
    }

    /**
     * Add a button Field to this menu attach to a label.
     */
    protected final void addButtonField(String labelName, String triggerName, Vector2f offset) {
        addButtonField(labelName, triggerName, HAlign.left, offset, true);
    }

    /**
     * Add a button field to this menu without label attached to it.
     */
    protected final void addButtonField(String triggerName, HAlign hAlign, Vector2f offset) {
        addButtonField(triggerName, triggerName, hAlign, offset, false);
    }

    /**
     * Add a button field to this menu without label attached to it.
     */
    protected final void addButtonField(String triggerName, HAlign hAlign) {
        addButtonField(triggerName, triggerName, hAlign, Vector2f.ZERO, false);
    }

    /**
     * Add a button field to this menu without label attached to it.
     */
    protected final void addButtonField(String labelName, String triggerName, HAlign hAlign) {
        addButtonField(triggerName, triggerName, hAlign, Vector2f.ZERO, true);
    }

    /**
     * Add a button field to this menu.
     *
     * @param labelName field name.
     * @param triggerName No Space && Not more than 7.
     * @param hAlign Horizontal alignement to use.
     * @param offset value to be added on top of the gridPosition.
     * @param addLabel define if a label have to be added for the button.
     */
    protected final void addButtonField(String labelName, String triggerName, HAlign hAlign, Vector2f offset, boolean addLabel) {
        String uid = generateUID(labelName);
        if (addLabel) {
            if (hAlign == HAlign.full && labelName != null) {
                System.err.println("Label not allowed with HAlign.full for button. "
                        + labelName + " : labelName will be ignored for : " + triggerName + ".");
            }
            elementList.put(uid, generateLabel(labelName, hAlign, offset));
            elementList.get(uid).addChild(generateButton(uid, uid + triggerName, hAlign, Vector2f.ZERO));
        } else {
            elementList.put(uid, generateButton(triggerName, hAlign, offset));
        }
    }

    protected final void addButtonList(String listUID, String[] triggersNames, HAlign hAlign) {
        addButtonList(listUID, triggersNames, hAlign, 1);
    }

    /**
     * Add a button List.
     *
     * @param triggersNames list of button to add.
     * @param hAlign alignement of the button.
     * @param fieldGridSize gridSlot to use
     */
    protected final void addButtonList(String listUID, String[] triggersNames, HAlign hAlign, int fieldGridSize) {
        String UID = generateUID(listUID) + getUID() + "labelList";
        Element holder = new Element(screen, UID, new Vector2f(spacement, 10), Vector2f.ZERO, Vector4f.ZERO, null);
        holder.setAsContainerOnly();
        generateList(UID, holder, triggersNames, hAlign, "labelList", fieldGridSize);
    }

    /**
     * Add a Text Field element to this menu, limited to number as input.
     */
    protected final void addNumericListField(String labelName, Integer[] baseValue, HAlign hAlign) {
        String uid = generateUID(labelName) + getUID() + "numList";
        Label holder = generateLabel(labelName, hAlign, Vector2f.ZERO);
        generateList(uid, holder, baseValue, hAlign, "numList", 1);
    }

    private void generateList(String UID, Element holder, Object[] elements, HAlign hAlign, String type, int fieldGridSize) {

        int i = 0;
        float posX = 0f;
        float fillValue = 0;
        ArrayList<ButtonAdapter> btnList = new ArrayList<ButtonAdapter>();
        for (Object e : elements) {
            if (type.equals("labelList")) {
                btnList.add(generateButton(e.toString(), HAlign.left, Vector2f.ZERO));
            } else if (type.equals("numList")) {
                btnList.add(generateNumericField("numList" + i, i, hAlign));
            } else {
                throw new UnsupportedOperationException(type + " is not a supported type.");
            }
            btnList.get(btnList.size() - 1).setPosition(posX, btnList.get(btnList.size() - 1).getPosition().y);
            posX += btnList.get(btnList.size() - 1).getWidth();
            fillValue += btnList.get(btnList.size() - 1).getWidth();

            holder.addChild(btnList.get(btnList.size() - 1));
            i++;
        }
        if (hAlign == HAlign.right) {
            holder.setPosition(gridSize.x - (posX + 10), holder.getPosition().y);
        } else if (hAlign.equals(HAlign.left) || hAlign.equals(HAlign.full)) {
            i = 0;
            fillValue = (gridSize.x * fieldGridSize - fillValue - (hAlign.equals(HAlign.left) ? holder.getDimensions().x : 0)) / elements.length;
            for (ButtonAdapter btn : btnList) {
                btn.setDimensions(btn.getDimensions().x + fillValue, btn.getDimensions().y);
                if (i == 0 && hAlign.equals(HAlign.left)) {
                    btn.setPosition(btn.getPosition().x + (hAlign.equals(HAlign.left) ? holder.getDimensions().x : 0), btn.getPosition().y);
                } else if (i != 0) {
                    btn.setPosition(btn.getPosition().x + (fillValue * i)
                            + (hAlign.equals(HAlign.left) ? holder.getDimensions().x : 0), btn.getPosition().y);
                }
                i++;
            }
        }
        elementList.put(UID, holder);
        if (fieldGridSize > 1) {
            for (i = fieldGridSize; i > 1; i--) {
                addSpace();
            }
        }
    }

    /**
     * Add a checkbox Element to this window.
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
        CheckBox b = new CheckBox(screen, getUID() + uid + "CheckBox", new Vector2f(spacement + offset.x, spacement + offset.y));
        b.setLabelText(labelName);
        elementList.put(uid, b);
    }

    /**
     * Add a spinner Element to this menu. value[0] = min, value[1] = max,
     * value[2] = step, value[3] = current.
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
     * @param hAlign horizontal alignemenet to use.
     * @param offset value to be added on top of the gridPosition.
     */
    protected final void addSpinnerField(String labelName, int[] value, HAlign hAlign, Vector2f offset) {
        String uid = generateUID(labelName);
        elementList.put(uid, generateLabel(labelName, hAlign, offset));
        generateSpinner(labelName, uid, value);
    }

    /**
     * Add a Text Field element to this menu.
     */
    protected final void addTextField(String labelName, String baseValue, HAlign hAlign) {
        addTextField(labelName, baseValue, hAlign, 0, Vector2f.ZERO);
    }

    /**
     * Add a Text Field element to this menu.
     */
    protected final void addTextField(String labelName, String baseValue, HAlign hAlign, int fieldSize) {
        addTextField(labelName, baseValue, hAlign, fieldSize, Vector2f.ZERO);
    }

    /**
     * Add a Text Field element to this menu.
     */
    protected final void addTextField(String labelName, String baseValue, HAlign hAlign, Vector2f offset) {
        String uid = generateUID(labelName);
        elementList.put(uid, generateLabel(labelName, hAlign, offset));
        generateTextField(uid, baseValue, Vector2Int.UNIT_XY);
    }

    /**
     * Add a Text Field element to this menu with an extended grid slot use.
     *
     * @param labelName field name.
     * @param baseValue first value to show.
     * @param hAlign horizontal alignemenet to use.
     * @param fieldSize the grid slot taken for the field.
     * @param offset value to be added on top of the gridPosition.
     */
    protected final void addTextField(String labelName, String baseValue, HAlign hAlign, int fieldSize, Vector2f offset) {
        Vector2Int size = Vector2Int.UNIT_XY;
        if (fieldSize != 0 && fieldSize > 0) {
            int pos = (int) FastMath.ceil((float) (elementList.size() + 1) / (float) elementAlignMaxCount);
            pos = (int) ((float) (elementList.size() + 1) / (float) pos);
            if (windowElementAlignement == Align.Horizontal) {
                if (fieldSize + pos <= elementAlignMaxCount) {
                    size.x = fieldSize + pos - 1;
                } else {
                    size.y = (int) FastMath.ceil((float) (fieldSize) / (float) elementAlignMaxCount);
                    for (int i = pos; i > 1; i--) {
                        addSpace();
                    }
                    size.x = elementAlignMaxCount;
                }
            } else if (windowElementAlignement == Align.Vertical) {
                if (fieldSize + pos <= elementAlignMaxCount) {
                    size.y = fieldSize + pos - 1;
                } else {
                    size.x = (int) FastMath.ceil((float) (fieldSize + pos) / (float) elementAlignMaxCount);
                    size.y = elementAlignMaxCount - 1;
                }
            }
        }

        String uid = generateUID(labelName);
        elementList.put(uid, generateLabel(labelName, hAlign, offset));
        generateTextField(uid, baseValue, size);
    }

    /**
     * Add a Text Field element to this menu, limited to number as input.
     */
    protected final void addNumericField(String labelName, int baseValue, HAlign hAlign) {
        addNumericField(labelName, baseValue, hAlign, Vector2f.ZERO);
    }

    /**
     * Add a Text Field element to this menu, limited to number as input.
     *
     * @param labelName field name.
     * @param baseValue first value to show.
     * @param hAlign horizontal alignemenet to use.
     * @param offset value to be added on top of the gridPosition.
     */
    protected final void addNumericField(String labelName, int baseValue, HAlign hAlign, Vector2f offset) {
        String uid = generateUID(labelName);
        Label field = generateLabel(labelName, hAlign, offset);
        elementList.put(uid, field);
        field.addChild(generateNumericField(uid, baseValue, hAlign));
    }

    /**
     * Add a selectionList Element based on a enum to this menu.
     */
    protected final void addSelectionField(String labelName, Enum<?> baseValue, Enum[] value, HAlign hAlign) {
        addSelectionField(labelName, baseValue, value, hAlign, Vector2f.ZERO);
    }

    /**
     * Add a selectionList Element based on a enum to this menu.
     *
     * @param labelName field name.
     * @param baseValue first value to show.
     * @param value all possible value.
     * @param hAlign horizontal alignemenet to use.
     * @param offset value to be added on top of the gridPosition.
     */
    protected final void addSelectionField(String labelName, Enum<?> baseValue,
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
                    new Vector2f(spacement + offset.x, offset.y + (1.5f * elementList.size())),
                    new Vector2f((labelName.toCharArray().length + 3) * 8, gridSize.y));
            label.setText(labelName + " : ");
            return label;
        } else if (hAlign.equals(HAlign.right)) {
            Label label = new Label(screen, getUID() + labelName.replaceAll("\\s", "") + "Label",
                    new Vector2f(gridSize.x - (spacement + offset.x + ((labelName.toCharArray().length + 2) * 8)), offset.y + (1.5f * elementList.size())),
                    new Vector2f((labelName.toCharArray().length + 3) * 8, gridSize.y));
            label.setText(" : " + labelName);
            return label;
        } else {
            throw new UnsupportedOperationException(hAlign + " : Cannot be used with a label or isn't implemented.");
        }
    }

    private ButtonAdapter generateNumericField(final String labelUID, int baseValue, final HAlign hAlign) {
        final String childUID = getUID() + labelUID;
        ButtonAdapter numButton = new ButtonAdapter(screen, childUID + "NumericFieldButton",
                Vector2f.ZERO, new Vector2f(gridSize.x - (((labelUID.toCharArray().length + 2) * 8) + (spacement * 2)), gridSize.y)) {
            @Override
            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                super.onButtonMouseLeftUp(evt, toggled);
                TextField field = new TextField(screen, getUID() + labelUID + "NumericField",
                        new Vector2f(getPosition().x, getPosition().y + (hAlign.equals(HAlign.left) ? spacement : 8)),
                        new Vector2f(getWidth(), gridSize.y)) {
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
                field.setMaxLength(4);
                field.setText(getText());
                getElementParent().addChild(field);
            }
        };
        switch (hAlign) {
            case full:
                break;
            case left:
                numButton.setPosition(((labelUID.toCharArray().length + 2) * 8) + spacement, 4);
                break;
            case right:
                numButton.setPosition(-numButton.getDimensions().x + spacement, 4);
                break;
        }
        numButton.setText(Integer.toString(baseValue));
        return numButton;
    }

    private void generateTextField(final String labelUID, String baseValue, Vector2Int fieldSize) {
        final String childUID = getUID() + labelUID;

        TextField field = new TextField(screen, childUID + "TextField",
                new Vector2f(elementList.get(labelUID).getDimensions().x, 4),
                new Vector2f((gridSize.x * fieldSize.x) - elementList.get(labelUID).getDimensions().x - spacement, gridSize.y * fieldSize.y)) {
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
        if (baseValue != null) {
            field.setText(baseValue);
        }

        if (fieldSize.x > 1) {
            for (int j = fieldSize.x; j > 1; j--) {
                addSpace();
            }
        }
        if (fieldSize.y > 1) {
            for (int i = fieldSize.y; i > 1; i--) {
                for (int j = fieldSize.x; j > 0; j--) {
                    addSpace();
                }
            }
        }

        elementList.get(labelUID).addChild(field);
    }

    private void generateSelectBoxField(final String labelUID, Enum<?> baseValue, Enum[] value) {
        SelectBox selectBox = new SelectBox(screen, getUID() + labelUID + "BoxField",
                new Vector2f(elementList.get(labelUID).getDimensions().x, 4),
                new Vector2f(gridSize.x - elementList.get(labelUID).getDimensions().x - spacement, gridSize.y)) {
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
                new Vector2f(elementList.get(labelUID).getDimensions().x, 4),
                new Vector2f(gridSize.x - elementList.get(labelUID).getDimensions().x - spacement, gridSize.y),
                Spinner.Orientation.HORIZONTAL, true) {
            @Override
            public void onChange(int selectedIndex, String value) {
                onSpinnerChange(labelName, selectedIndex);
            }
        };
        spinner.setStepIntegerRange(value[0], value[1], value[2]);
        spinner.setSelectedIndex(value[3]);
        elementList.get(labelUID).addChild(spinner);
    }

    private ButtonAdapter generateButton(String triggerName, HAlign hAlign, Vector2f offset) {
        return generateButton(null, triggerName, hAlign, offset);
    }

    private ButtonAdapter generateButton(String labelName, final String triggerName, HAlign hAlign, Vector2f offset) {
        ButtonAdapter button = new ButtonAdapter(screen, getUID() + generateUID(triggerName) + "Button",
                Vector2f.ZERO, new Vector2f((labelName != null
                ? gridSize.x - elementList.get(labelName).getDimensions().x - spacement
                : (triggerName.toCharArray().length + 2) * 8) + spacement, gridSize.y)) {
            @Override
            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                super.onButtonMouseLeftUp(evt, toggled);
                onButtonTrigger(getText());
            }
        };
        button.setText(triggerName);
        switch (hAlign) {
            case left:
                button.setPosition(new Vector2f((labelName != null ? elementList.get(labelName).getDimensions().x - spacement : spacement + offset.x), 4 + offset.y));
                return button;
            case right:
                button.setPosition(new Vector2f(gridSize.x - (spacement + offset.x + button.getDimensions().x
                        + (labelName != null ? elementList.get(labelName).getDimensions().x : 0)), 4 + offset.y));
                return button;
            case full:
                button.setDimensions(new Vector2f(gridSize.x - spacement, button.getDimensions().y));
                button.setPosition(new Vector2f(spacement, offset.y + 5 + elementList.size()));
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

    protected final Element getLabelListField(String listUID, String buttonLabel) {
        return elementList.get(generateUID(listUID) + getUID() + "labelList").getChildElementById(getUID() + generateUID(buttonLabel) + "Button");
    }

    protected final Element getNumericListField(String listUID, int index) {
        return elementList.get(generateUID(listUID) + getUID() + "numList").getChildElementById(getUID() + "numList" + index + "NumericFieldButton");
    }

    protected final ButtonAdapter getButtonField(String labelName, boolean gotLabel) {
        if (gotLabel) {
            return (ButtonAdapter) getField(labelName).getChildElementById(getUID() + generateUID(labelName) + "Button");
        } else {
            return (ButtonAdapter) elementList.get(generateUID(labelName));
        }
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
        bottom,
        center;
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

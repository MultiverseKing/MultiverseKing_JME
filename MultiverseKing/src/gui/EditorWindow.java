package gui;

import com.jme3.input.KeyInput;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import static gui.LayoutWindow.HAlign.full;
import static gui.LayoutWindow.HAlign.left;
import static gui.LayoutWindow.HAlign.right;
import java.util.ArrayList;
import org.hexgridapi.utility.Vector2Int;
import tonegod.gui.controls.buttons.ButtonAdapter;
import tonegod.gui.controls.buttons.CheckBox;
import tonegod.gui.controls.lists.SelectBox;
import tonegod.gui.controls.lists.Spinner;
import tonegod.gui.controls.text.Label;
import tonegod.gui.controls.text.TextField;
import tonegod.gui.core.Element;
import tonegod.gui.core.Screen;

/**
 *
 * @author roah
 */
public abstract class EditorWindow extends LayoutWindow {

    // <editor-fold defaultstate="collapsed" desc="Constructor">
    public EditorWindow(Screen screen, Element parent, String name) {
        super(screen, parent, name);
    }

    public EditorWindow(Screen screen, Element parent, String name, Align align, int alignElement) {
        super(screen, parent, name, align, alignElement);
    }

    public EditorWindow(Screen screen, Element parent, String name, Vector2f gridSize) {
        super(screen, parent, name, gridSize);
    }

    public EditorWindow(Screen screen, Element parent, String name, Vector2f gridSize, Align windowElementAlignement, int elementAlignMaxCount) {
        super(screen, parent, name, gridSize, windowElementAlignement, elementAlignMaxCount);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Add Method">
    /**
     * Add an empty space into the grid.
     */
    protected final void addSpace() {
        elementList.put(generateUID(getUID() + spacement + "_" + elementList.size()), null);
    }

    /**
     * Add a button Field to this menu. HAlign.full
     */
    protected final void addButtonField(String labelName) {
        addButtonField(labelName, labelName, HAlign.full, new Vector2f(), false);
    }

    /**
     * Add a button Field to this menu attach to a label.
     */
    protected final void addButtonField(String labelName, HAlign labelHAlign, String triggerName, HAlign triggerHAlign) {
        String uid = generateUID(labelName);
        elementList.put(uid, generateLabel(labelName, labelHAlign, new Vector2f(), false));
        elementList.get(uid).addChild(generateButton(uid, triggerName, triggerHAlign, new Vector2f()));
    }

    /**
     * Add a button field to this menu without label attached to it.
     */
    protected final void addButtonField(String triggerName, HAlign hAlign) {
        addButtonField(triggerName, triggerName, hAlign, new Vector2f(), false);
    }

    /**
     * Add a button field to this menu.
     *
     * @param labelName field name.
     * @param triggerName name used for the button.
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
            elementList.put(uid, generateLabel(labelName, hAlign, offset, false));
            elementList.get(uid).addChild(generateButton(uid, uid + triggerName, hAlign, new Vector2f()));
        } else {
            elementList.put(uid, generateButton(triggerName, hAlign, offset));
        }
    }

    protected final void addButtonList(String listUID, String[] triggersNames, HAlign hAlign) {
        addButtonList(listUID, triggersNames, hAlign, 1, 0);
    }

    protected final void addButtonList(String listUID, String[] triggersNames, HAlign hAlign, int fieldGridSize) {
        addButtonList(listUID, triggersNames, hAlign, fieldGridSize, 0);
    }

    /**
     * Add a button List.
     *
     * @param triggersNames list of button to add.
     * @param hAlign alignement of the button.
     * @param fieldGridSize gridSlot to use
     */
    protected final void addButtonList(String listUID, String[] triggersNames, HAlign hAlign, int fieldGridSize, float offsetX) {
        String UID = generateUID(listUID) + "labelList";
        Element holder = new Element(screen, UID, new Vector2f(offsetX, 0), new Vector2f(), Vector4f.ZERO, null);
        holder.setAsContainerOnly();
        generateList(UID, holder, triggersNames, hAlign, "labelList", fieldGridSize);
    }

    /**
     * Add a Text Field element to this menu, limited to number as input.
     */
    protected final void addNumericListField(String labelName, Integer[] baseValue, HAlign hAlign) {
        String uid = generateUID(labelName) + "numList";
        Label holder = generateLabel(labelName, hAlign, new Vector2f(), false);
        generateList(uid, holder, baseValue, hAlign, "numList", 1);
    }

    protected final void addSpinnerList(String labelName, int[][] value, HAlign hAlign) {
        String uid = generateUID(labelName) + "spinList";
        Label holder = generateLabel(labelName, hAlign, new Vector2f(), false);
        generateList(uid, holder, value, hAlign, "spinList", 1);
    }

    private void generateList(String UID, Element holder, Object[] elements, HAlign hAlign, String type, int fieldGridSize) {

        int i = 0;
        float posX = 0f;
        float fillValue = spacement;
        ArrayList<Element> eList = new ArrayList<Element>();
        for (Object o : elements) {
            if (type.equals("labelList")) {
                eList.add(generateButton(o.toString(), HAlign.left, new Vector2f()));
            } else if (type.equals("numList")) {
                eList.add(generateNumericField(UID + i, i, hAlign));
            } else if (type.equals("spinList")) {
                eList.add(generateSpinner(UID + i, null, (int[]) o));
            } else {
                throw new UnsupportedOperationException(type + " is not a supported type.");
            }
            eList.get(eList.size() - 1).setPosition(posX, eList.get(eList.size() - 1).getPosition().y);
            posX += eList.get(eList.size() - 1).getWidth();
            fillValue += eList.get(eList.size() - 1).getWidth();

            holder.addChild(eList.get(eList.size() - 1));
            i++;
        }
        if (hAlign == HAlign.right && type.equals("numList")) {
            holder.setPosition(layoutGridSize.x - (posX + 10), holder.getPosition().y);
        } else if (hAlign == HAlign.right && type.equals("labelList")) {
            holder.setPosition(layoutGridSize.x - posX, holder.getPosition().y);
        } else if (type.equals("spinList") && fieldGridSize <= 1) {
            fillValue = (layoutGridSize.x - fillValue) / elements.length;
            i = 0;
            for (Element e : eList) {
                e.setDimensions(e.getDimensions().x + fillValue - (e.getDimensions().y * 2), e.getDimensions().y);
                e.setPosition(e.getPosition().x + fillValue * i + e.getDimensions().y, e.getPosition().y - layoutGridSize.y);
                e.getChildElementById(e.getUID() + ":btnInc").setPosition(e.getDimensions().x, 0);
                i++;
            }
            fieldGridSize++;
        } else if (hAlign.equals(HAlign.left) || hAlign.equals(HAlign.full)) {
            i = 0;
            fillValue = (layoutGridSize.x * fieldGridSize - fillValue - holder.getDimensions().x) / elements.length;
            for (Element e : eList) {
                e.setDimensions(e.getDimensions().x + fillValue, e.getDimensions().y);
                if (i == 0 && hAlign.equals(HAlign.left)) {
                    e.setPosition(e.getPosition().x + (hAlign.equals(HAlign.left) ? holder.getDimensions().x : 0), e.getPosition().y);
                } else if (i != 0) {
                    e.setPosition(e.getPosition().x + (fillValue * i)
                            + (hAlign.equals(HAlign.left) ? holder.getDimensions().x : 0), e.getPosition().y);
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
        addCheckBoxField(labelName, active, new Vector2f());
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
        addSpinnerField(labelName, value, hAlign, new Vector2f());
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
        elementList.put(uid, generateLabel(labelName, hAlign, offset, false));
        elementList.get(uid).addChild(generateSpinner(labelName, uid, value));
    }

    /**
     * Add a Text Field element to this menu.
     */
    protected final void addTextField(String labelName, String baseValue, HAlign hAlign) {
        addTextField(labelName, baseValue, hAlign, 1, new Vector2f());
    }

    /**
     * Add a Text Field element to this menu.
     */
    protected final void addTextField(String labelName, String baseValue, HAlign hAlign, int fieldSize) {
        addTextField(labelName, baseValue, hAlign, fieldSize, new Vector2f());
    }

    /**
     * Add a Text Field element to this menu.
     */
    protected final void addTextField(String labelName, String baseValue, HAlign hAlign, Vector2f offset) {
        String uid = generateUID(labelName);
        elementList.put(uid, generateLabel(labelName, hAlign, offset, false));
        generateTextField(uid, baseValue, new Vector2Int(1, 1));
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
        Vector2Int size = new Vector2Int(1, 1);
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
        elementList.put(uid, generateLabel(labelName, hAlign, offset, false));
        generateTextField(uid, baseValue, size);
    }

    /**
     * Add a Text Field element to this menu, limited to number as input.
     */
    protected final void addNumericField(String labelName, int baseValue, HAlign hAlign) {
        addNumericField(labelName, baseValue, hAlign, new Vector2f());
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
        Label field = generateLabel(labelName, hAlign, offset, false);
        elementList.put(uid, field);
        field.addChild(generateNumericField(uid, baseValue, hAlign));
    }

    /**
     * Add a selectionList Element based on a enum to this menu.
     */
    protected final void addSelectionField(String labelName, Enum<?> baseValue, Enum[] value, HAlign hAlign) {
        addSelectionField(labelName, baseValue, value, hAlign, new Vector2f());
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
        elementList.put(uid, generateLabel(labelName, hAlign, offset, false));
        generateSelectBoxField(uid, baseValue, value);
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
    protected final void addLabelField(String labelName, HAlign hAlign, Vector2f offset) {
        String uid = generateUID(labelName);
        elementList.put(uid, generateLabel(labelName, hAlign, offset, true));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Generate Method">
    private Label generateLabel(String labelName, HAlign hAlign, Vector2f offset, boolean labelOnly) {
        Label label;
        if (hAlign.equals(HAlign.left)) {
            label = new Label(screen, getUID() + labelName.replaceAll("\\s", "") + "Label",
                    new Vector2f(offset.x, offset.y),
                    new Vector2f((labelName.toCharArray().length + 3) * 8, layoutGridSize.y));
            label.setText(labelName + (labelOnly ? "" : " : "));
        } else if (hAlign.equals(HAlign.right)) {
            label = new Label(screen, getUID() + labelName.replaceAll("\\s", "") + "Label",
                    new Vector2f(layoutGridSize.x - (spacement + offset.x + ((labelName.toCharArray().length + 2) * 8)), offset.y),
                    new Vector2f((labelName.toCharArray().length + 3) * 8, layoutGridSize.y));
            label.setText((labelOnly ? "" : " : ") + labelName);
        } else {
            throw new UnsupportedOperationException(hAlign + " : Cannot be used with a label or isn't implemented.");
        }
        return label;
    }

    private ButtonAdapter generateNumericField(final String labelUID, int baseValue, final HAlign hAlign) {
        final String childUID = getUID() + labelUID;
        ButtonAdapter numButton = new ButtonAdapter(screen, childUID + "NumericFieldButton",
                new Vector2f(), new Vector2f(layoutGridSize.x - (((labelUID.toCharArray().length + 2) * 8) + (spacement * 2)), layoutGridSize.y)) {
            @Override
            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                super.onButtonMouseLeftUp(evt, toggled);
                TextField field = new TextField(screen, getUID() + labelUID + "NumericField",
                        new Vector2f(getPosition().x, getPosition().y + 8),
                        new Vector2f(getWidth(), layoutGridSize.y)) {
                    @Override
                    public void onKeyRelease(KeyInputEvent evt) {
                        super.onKeyRelease(evt);
                        if (evt.getKeyCode() == KeyInput.KEY_RETURN) {
                            switchText(getText());
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

            private void switchText(String text) {
                setText(text);
            }
        };
        switch (hAlign) {
            case full:
                break;
            case left:
                numButton.setPosition(((labelUID.toCharArray().length + 2) * 8) + spacement, 4);
                break;
            case right:
                numButton.setPosition(-numButton.getDimensions().x, 4);
                break;
        }
        numButton.setText(Integer.toString(baseValue));
        return numButton;
    }

    private void generateTextField(final String labelUID, String baseValue, Vector2Int fieldSize) {
        final String childUID = getUID() + labelUID;

        TextField field = new TextField(screen, childUID + "TextField",
                new Vector2f(elementList.get(labelUID).getDimensions().x, 4),
                new Vector2f((layoutGridSize.x * fieldSize.x) - elementList.get(labelUID).getDimensions().x - spacement, layoutGridSize.y * fieldSize.y)) {
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
                new Vector2f(layoutGridSize.x - elementList.get(labelUID).getDimensions().x - spacement, layoutGridSize.y)) {
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

    private Spinner generateSpinner(final String labelName, final String labelUID, int[] value) {
        Spinner spinner = new Spinner(screen, getUID() + (labelUID != null ? labelUID + "Spinner" : labelName),
                new Vector2f((labelUID != null ? elementList.get(labelUID).getDimensions().x : 0), 4),
                new Vector2f((labelUID != null ? layoutGridSize.x - elementList.get(labelUID).getDimensions().x - spacement : 0), layoutGridSize.y),
                Spinner.Orientation.HORIZONTAL, true) {
            @Override
            public void onChange(int selectedIndex, String value) {
                onSpinnerChange(labelName, selectedIndex);
            }
        };
        spinner.setStepIntegerRange(value[0], value[1], value[2]);
        spinner.setSelectedIndex(value[3]);
        return spinner;
    }

    private ButtonAdapter generateButton(String triggerName, HAlign hAlign, Vector2f offset) {
        return generateButton(null, triggerName, hAlign, offset);
    }

    private ButtonAdapter generateButton(final String labelName, final String triggerName, HAlign hAlign, Vector2f offset) {
        ButtonAdapter button = new ButtonAdapter(screen, getUID() + generateUID(triggerName) + "Button",
                new Vector2f(), new Vector2f((labelName != null
                ? layoutGridSize.x - elementList.get(labelName).getDimensions().x - spacement
                : (triggerName.toCharArray().length + 2) * 8) + spacement, layoutGridSize.y)) {
            @Override
            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                super.onButtonMouseLeftUp(evt, toggled);
                onButtonTrigger((labelName != null ? labelName : "")+getText());
            }
        };
        button.setText(triggerName);
        switch (hAlign) {
            case left:
                button.setPosition(new Vector2f((labelName != null ? (labelName.toCharArray().length+1) * 8 - spacement : spacement) + offset.x, 4));
                return button;
            case right:
                button.setPosition(new Vector2f(layoutGridSize.x - (spacement + offset.x + button.getDimensions().x), 4));
                return button;
            case full:
                button.setDimensions(new Vector2f(layoutGridSize.x - (labelName != null ? (labelName.toCharArray().length+1) * 8 : spacement), layoutGridSize.y));
                button.setPosition(new Vector2f((labelName != null ? (labelName.toCharArray().length+1) * 8 - spacement : 0) + offset.x, 4));
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
        return elementList.get(generateUID(listUID) + "labelList").getChildElementById(getUID() + generateUID(buttonLabel) + "Button");
    }

    protected final Element getSpinnerListField(String listUID, int index) {
        return elementList.get(generateUID(listUID) + "spinList").getChildElementById(getUID() + generateUID(listUID) + "spinList" + index);
    }

    protected final Element getNumericListField(String listUID, int index) {
        return elementList.get(generateUID(listUID) + "numList").getChildElementById(getUID() + "numList" + index + "NumericFieldButton");
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
}

package org.multiverseking.gui.control;

import com.jme3.input.KeyInput;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import static org.multiverseking.gui.control.LayoutWindow.HAlign.full;
import static org.multiverseking.gui.control.LayoutWindow.HAlign.left;
import static org.multiverseking.gui.control.LayoutWindow.HAlign.right;
import java.util.ArrayList;
import org.hexgridapi.utility.Vector2Int;
import tonegod.gui.controls.buttons.ButtonAdapter;
import tonegod.gui.controls.buttons.CheckBox;
import tonegod.gui.controls.lists.SelectBox;
import tonegod.gui.controls.lists.Spinner;
import tonegod.gui.controls.lists.Spinner.ChangeType;
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
    /**
     *
     * @param screen used to show the window.
     * @param parent if any.
     * @param name window name.
     */
    public EditorWindow(Screen screen, Element parent, String name) {
        super(screen, null, parent, name);
    }

    /**
     *
     * @param screen used to show the window.
     * @param parent if any.
     * @param name window name.
     * @param align Element alignement type to use for element position.
     * @param alignElement max element count on the selected alignement.
     */
    public EditorWindow(Screen screen, Element parent, String name, Align align, int alignElement) {
        super(screen, null, parent, name, align, alignElement);
    }

    /**
     *
     * @param screen used to show the window.
     * @param parent if any.
     * @param name window name.
     * @param align Element alignement type to use for element position.
     * @param gridSize size of an element on X and Y.
     */
    public EditorWindow(Screen screen, Element parent, String name, Vector2f gridSize) {
        super(screen, null, parent, name, gridSize);
    }

    /**
     *
     * @param screen used to show the window.
     * @param parent if any.
     * @param name window name.
     * @param gridSize size of an element on X and Y.
     * @param align Element alignement type to use for element position.
     * @param elementAlignMaxCount max element count on the selected alignement.
     */
    public EditorWindow(Screen screen, String UID, Element parent, String name, Vector2f gridSize, Align align, int elementAlignMaxCount) {
        super(screen, UID, parent, name, gridSize, align, elementAlignMaxCount);
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Add Method">
    /**
     * Add an empty space into the grid.
     */
    public final void addSpace() {
        elementList.put(generateUID(getUID() + ".space_" + elementList.size()), null);
    }

    /**
     * Add a button field to this menu without label attached to it.
     * label HAlign.left.
     *
     * @param UID unique identifier (if null UID = labelName)
     * @param triggerName button text
     * @param hAlign button alignement
     * @param bType button type
     */
    public final void addButtonField(String UID, String triggerName, HAlign hAlign, ButtonType bType) {
        addButtonField(UID, null, HAlign.left, triggerName, hAlign, bType);
    }

    /**
     * Add a button field to this menu. UID generated from labelName.
     *
     * @param UID unique identifier (if null UID = labelName)
     * @param labelName field name.
     * @param labelHAlign Horizontal alignement to use for label.
     * @param triggerName name used for the button.
     * @param triggerHAlign Horizontal alignement to use for button.
     * @param addLabel define if a label have to be added for the button.
     */
    public final void addButtonField(String UID, String labelName, HAlign labelHAlign, String triggerName, HAlign triggerHAlign, ButtonType bType) {
        UID = UID != null ? UID : generateUID(labelName != null ? labelName : triggerName);
        if (labelName != null) {
            if (labelHAlign == HAlign.full) {
                System.err.println("Label name isn't allowed with HAlign.full on button.\n "
                        + "labelName : " + labelName + " will be ignored.");
                elementList.put(UID, generateButton(null, triggerName, labelHAlign, bType));
                return;
            }
            elementList.put(UID, generateLabel(labelName, labelHAlign, false));
            elementList.get(UID).addChild(generateButton(UID, triggerName, triggerHAlign, bType));
        } else {
            elementList.put(UID, generateButton(null, triggerName, triggerHAlign, bType));
        }
    }

    /**
     * Add a list off button (button group).
     *
     * @param listUID storage UID.
     * @param triggersNames button label list.
     * @param hAlign alignement to use
     * @param bType button type
     */
    public final void addButtonList(String listUID, String[] triggersNames, HAlign hAlign, ButtonType bType) {
        addButtonList(listUID, null, hAlign, triggersNames, hAlign, bType, 1);
    }

    /**
     * Add a button List (button group).
     *
     * @param UID unique identifier
     * @param labelName if not null a label of this name is added
     * @param triggersNames list of button to add.
     * @param hAlign alignement of the button.
     * @param type type of button to use
     * @param fieldGridSize gridSlot to use
     */
    public final void addButtonList(String UID, String labelName, HAlign labelAlign, String[] triggersNames, HAlign btnAlign, ButtonType type, int fieldGridSize) {
        addList(UID, labelName, labelAlign, triggersNames, btnAlign, type, fieldGridSize, "btn");
    }

    /**
     * Add a numeric list Field, limited to number as input.
     *
     * @param UID unique identifier
     * @param labelName if not null a label of this name is added
     * @param baseValue
     * @param hAlign
     */
    public final void addNumericListField(String UID, String labelName, Integer[] baseValue, HAlign hAlign) {
        addList(UID, labelName, hAlign, baseValue, hAlign, ButtonType.TEXT, 1, "num");
    }

    /**
     * Add spinner list field.
     */
    public final void addSpinnerList(String UID, String labelName, int[][] value, HAlign hAlign) {
        addList(UID, labelName, hAlign, value, hAlign, ButtonType.TEXT, 1, "spin");
    }

    private void addList(String UID, String labelName, HAlign labelAlign, Object[] triggersNames, HAlign btnAlign, ButtonType bType, int fieldGridSize, String listType) {
        UID = UID + "." + "List";
        Element holder;
        if (labelName != null) {
            holder = generateLabel(labelName, labelAlign, false);
        } else {
            holder = new Element(screen, UID, new Vector2f(), new Vector2f(), Vector4f.ZERO, null);
            holder.setAsContainerOnly();
        }
        generateList(UID, holder, triggersNames, btnAlign, listType, bType, fieldGridSize);
    }

    /**
     * Add a checkbox Element to this window.
     *
     * @param labelName field name.
     * @param active is the checkbox switched on.
     * @param offset value to be added on top of the gridPosition.
     */
    public final void addCheckBoxField(String UID, String labelName, boolean active) {
        CheckBox b = new CheckBox(screen, (UID != null ? UID : labelName) + "." 
                + getUID() + "CheckBox", new Vector2f(2, spacement), new Vector2f(15,15));
        b.setIsChecked(active);
        b.setLabelText(labelName);
        elementList.put(UID != null ? UID : labelName, b);
    }

    /**
     * Add a spinner Element to this menu.
     * <li> value[0] = min </li>
     * <li> value[1] = max </li>
     * <li> value[2] = step </li>
     * <li> value[3] = current </li>
     *
     * @param labelName field name.
     * @param value first value to show.
     * @param hAlign horizontal alignemenet to use.
     */
    public final void addSpinnerField(String UID, String labelName, int[] value, HAlign hAlign) {
        if (labelName != null) {
            Label label = generateLabel(labelName, hAlign, false);
            elementList.put(UID != null ? UID : generateUID(labelName), label);
            label.addChild(generateSpinner(UID, labelName, value));
        } else {
            elementList.put(UID != null ? UID : generateUID(labelName), generateSpinner(UID, labelName, value));
        }
    }

    /**
     * Add a Text Field element to this menu with an extended grid slot use.
     *
     * @param UID unique identifier
     * @param labelName field name.
     * @param baseValue first value to show.
     * @param hAlign horizontal alignemenet to use.
     * @param fieldSize the grid slot taken for the field.
     */
    public final void addTextField(String UID, String labelName, String baseValue, HAlign hAlign, int fieldSize) {
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
        elementList.put(uid, generateLabel(labelName, hAlign, false));
        generateTextField(uid, baseValue, size);
    }

    /**
     * Add a Text Field element to this menu, limited to number as input.
     *
     * @param labelName field name.
     * @param baseValue first value to show.
     * @param hAlign horizontal alignemenet to use.
     */
    public final void addNumericField(String labelName, int baseValue, HAlign hAlign) {
        String uid = generateUID(labelName);
        Label field = generateLabel(labelName, hAlign, false);
        elementList.put(uid, field);
        field.addChild(generateNumericField(uid, baseValue, hAlign));
    }

    /**
     * Add a selectionList Element based on a enum to this menu.
     *
     * @param labelName field name.
     * @param baseValue first value to show.
     * @param value all possible value.
     * @param hAlign horizontal alignemenet to use.
     */
    public final void addSelectionField(String labelName, Enum<?> baseValue, Enum[] value, HAlign hAlign) {
        String uid = generateUID(labelName);
        elementList.put(uid, generateLabel(labelName, hAlign, false));
        generateSelectBoxField(uid, baseValue, value);
    }

    /**
     * Add a selectionList Element based on a enum to this menu.
     *
     * @param UID unique identifier
     * @param labelName field name.
     * @param hAlign horizontal alignemenet to use.
     */
    public final void addLabelField(String UID, String labelName, HAlign hAlign) {
        if (UID == null) {
            UID = generateUID(labelName);
        }
        elementList.put(UID, generateLabel(labelName, hAlign, true));
    }

    /**
     * Add a selectionList Element based on a enum to this menu.
     *
     * @param labelName field name.
     * @param value x as current value && y as max value.
     * @param hAlign horizontal alignemenet to use.
     */
    public final void addLabelPropertieField(String labelName, Vector2Int value, HAlign hAlign) {
        String uid = generateUID(labelName);
        elementList.put(uid, generateLabel(labelName + " : " + value.x + " / " + value.y, hAlign, true));
    }

    /**
     * Add a selectionList Element based on a enum to this menu.
     *
     * @param labelName field name.
     * @param value value to show.
     * @param hAlign horizontal alignemenet to use.
     */
    public final void addLabelPropertieField(String labelName, int value, HAlign hAlign) {
        String uid = generateUID(labelName);
        elementList.put(uid, generateLabel(labelName + " : " + value, hAlign, true));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Generate Method">
    protected final Label generateLabel(String labelName, HAlign hAlign, boolean labelOnly) {
        String UID = labelName.replaceAll("\\s", "") + "." + getUID() + "Label";
        Label label;
        if (hAlign.equals(HAlign.left)) {
            label = new Label(screen, UID, Vector2f.ZERO,
                    new Vector2f((labelName.toCharArray().length + (labelOnly ? 1 : 3)) * 8, layoutGridSize.y));
            label.setText(labelName + (labelOnly ? "" : " : "));
        } else if (hAlign.equals(HAlign.right)) {
            label = new Label(screen, UID,
                    new Vector2f(layoutGridSize.x - (spacement + ((labelName.toCharArray().length + 2) * 8)), 0),
                    new Vector2f((labelName.toCharArray().length + 3) * 8, layoutGridSize.y));
            label.setText((labelOnly ? "" : " : ") + labelName);
        } else {
            throw new UnsupportedOperationException(hAlign + " : Cannot be used with a label or isn't implemented.");
        }
        return label;
    }

    protected final ButtonAdapter generateNumericField(final String labelUID, int baseValue, final HAlign hAlign) {
        ButtonAdapter numButton = new ButtonAdapter(screen, labelUID + "." + getUID() + ".NumericFieldButton",
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

    protected final void generateTextField(final String labelUID, String baseValue, Vector2Int fieldSize) {
        TextField field = new TextField(screen, labelUID + "." + getUID() + ".TextField",
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

    protected final void generateSelectBoxField(final String labelUID, Enum<?> baseValue, Enum[] value) {
        SelectBox selectBox = new SelectBox(screen, labelUID + "." + getUID() + ".BoxField",
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

    protected final Spinner generateSpinner(final String UID, final String labelName, int[] intValue) {
        final int startIndex = (int) (FastMath.abs(intValue[0]) + FastMath.abs(intValue[1])) / 2 + intValue[3];
        Spinner spinner = new Spinner(screen, (labelName != null ? labelName : UID) + "." + getUID() + ".Spinner",
                new Vector2f((labelName != null ? elementList.get(UID != null ? UID
                : generateUID(labelName)).getDimensions().x : 0), 4),
                new Vector2f((labelName != null ? layoutGridSize.x - elementList.get(
                UID != null ? UID : generateUID(labelName)).getDimensions().x - spacement : 0), layoutGridSize.y),
                Spinner.Orientation.HORIZONTAL, true) {
            @Override
            public void onChange(int selectedIndex, String value, ChangeType type) {
                onSpinnerChange(labelName != null ? labelName : UID, selectedIndex, type);
            }
        };
        spinner.setStepIntegerRange(intValue[0], intValue[1], intValue[2]);
        spinner.setSelectedIndex(startIndex);
        return spinner;
    }

    protected final ButtonAdapter generateButton(String labelName, String triggerName, HAlign hAlign, ButtonType bType) {

        Vector2f bSize = bType.equals(ButtonType.TEXT)
                ? new Vector2f(((triggerName.toCharArray().length + 2) * 8), layoutGridSize.y)
                : new Vector2f(layoutGridSize.y, layoutGridSize.y);

        ButtonAdapter button = new ButtonAdapter(screen, generateUID(triggerName) + "." + getUID() + ".Button" + bType.toString(),
                new Vector2f(), bSize, bType.equals(ButtonType.IMG) ? Vector4f.ZERO : screen.getStyle("Window").getVector4f("resizeBorders"),
                bType.equals(ButtonType.IMG) ? "org/multiverseking/assets/Textures/Icons/Buttons/" + triggerName + ".png" : screen.getStyle("Button").getString("defaultImg")) {
            @Override
            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                super.onButtonMouseLeftUp(evt, toggled);
//                onButtonTrigger((labelName != null ? labelName + "." : "") + triggerName);
                onButtonTrigger(this.getUID().split("\\.")[0]);
            }
        };
        if (bType.equals(ButtonType.TEXT)) {
            button.setText(triggerName);
        }
        switch (hAlign) {
            case left:
//                button.setPosition(new Vector2f((labelName != null ? (labelName.toCharArray().length + 1) * 8 - spacement : spacement) + offset.x, 4));
                button.setPosition(new Vector2f((labelName != null ? elementList.get(labelName).getDimensions().x - spacement : spacement), 4));
                return button;
            case right:
                button.setPosition(new Vector2f(layoutGridSize.x - (spacement + button.getDimensions().x), 4));
                return button;
            case full:
                button.setDimensions(new Vector2f(layoutGridSize.x - (labelName != null ? (labelName.toCharArray().length + 1) * 8 : spacement), layoutGridSize.y));
                button.setPosition(new Vector2f((labelName != null ? (labelName.toCharArray().length + 1) * 8 - spacement : 0), 4));
                return button;
            default:
                throw new UnsupportedOperationException(hAlign + " isn't supported.");
        }
    }

    private void generateList(String UID, Element holder, Object[] elements, HAlign hAlign, String listType, ButtonType bType, int fieldGridSize) {
        int i = 0;
        float posX = 0f;
        float fillValue = spacement;
        ArrayList<Element> eList = new ArrayList<>();
        for (Object o : elements) {
            switch (listType) {
                case "btn":
                    eList.add(generateButton(null, o.toString(), hAlign, bType));
                    break;
                case "num":
                    eList.add(generateNumericField(UID + i, i, hAlign));
                    break;
                case "spin":
                    eList.add(generateSpinner(UID + i, null, (int[]) o));
                    break;
                default:
                    throw new UnsupportedOperationException(listType + " is not a supported type.");
            }
            eList.get(eList.size() - 1).setPosition(posX, eList.get(eList.size() - 1).getPosition().y);
            posX += eList.get(eList.size() - 1).getWidth();
            fillValue += eList.get(eList.size() - 1).getWidth();

            holder.addChild(eList.get(eList.size() - 1));
            i++;
        }
        if (hAlign == HAlign.right && listType.equals("num")) {
            holder.setPosition(layoutGridSize.x - (posX + 10), holder.getPosition().y);
        } else if (hAlign == HAlign.right && listType.equals("btn")) {
            holder.setPosition(layoutGridSize.x - posX, holder.getPosition().y);
        } else if (listType.equals("spin") && fieldGridSize <= 1) {
            fillValue = (layoutGridSize.x - fillValue) / elements.length;
            i = 0;
            for (Element e : eList) {
                e.setDimensions(e.getDimensions().x + fillValue - (e.getDimensions().y * 2), e.getDimensions().y);
                e.setPosition(e.getPosition().x + fillValue * i + e.getDimensions().y, e.getPosition().y - layoutGridSize.y);
                e.getChildElementById(e.getUID() + ":btnInc").setPosition(e.getDimensions().x, 0);
                ((Spinner) e).setSelectedIndex(((Spinner) e).getSelectedIndex());
                i++;
            }
            fieldGridSize++;
        } else if (hAlign.equals(HAlign.full)) {
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
        } else if (hAlign.equals(HAlign.left)) {
            i = 0;
            for (Element e : eList) {
                e.setPosition(holder.getDimensions().x - spacement + e.getDimensions().x * i, e.getPosition().y);
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
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="get Fields Method">
    protected final Element getField(String UID, String labelName) {
        return elementList.get(UID != null ? UID : generateUID(labelName));
    }

    protected final Element getListButtonField(String listUID, ButtonType bType, String buttonLabel) {
        return getListField(listUID, bType, buttonLabel).getChildElementById(generateUID(buttonLabel) + "." + getUID() + ".Button" + bType.toString());
    }

    protected final Element getListField(String listUID, ButtonType bType, String buttonLabel) {
        return elementList.get(generateUID(listUID) + ".List");
    }

    protected final Element getSpinnerListField(String listUID, int index) {
        return elementList.get(generateUID(listUID) + ".spinList").getChildElementById(getUID() + generateUID(listUID) + ".spinList" + index);
    }

    protected final Element getNumericListField(String listUID, int index) {
        return elementList.get(generateUID(listUID) + ".numList").getChildElementById(getUID() + ".numList" + index + "NumericFieldButton");
    }

    protected final ButtonAdapter getButtonField(String UID, String labelName, String triggerName, ButtonType bType) {
        ButtonAdapter btn;
        String btnUID = generateUID(triggerName) + "." + getUID() + ".Button" + bType.toString();
        if (UID != null) {
            btn = (ButtonAdapter) elementList.get(UID).getChildElementById(btnUID);
        } else if (labelName != null) {
            btn = (ButtonAdapter) getField(UID, labelName).getChildElementById(btnUID);
        } else {
            btn = (ButtonAdapter) getField(UID, labelName);
        }
        return btn;
    }

    protected final CheckBox getCheckBoxField(String labelName) {
        return (CheckBox) elementList.get(generateUID(labelName));
    }

    protected final TextField getTextField(String UID, String labelName) {
        return (TextField) getField(UID, labelName).getChildElementById(generateUID(labelName) + "." + getUID() + ".TextField");
    }

    protected final ButtonAdapter getNumericField(String UID, String labelName) {
        return (ButtonAdapter) getField(UID, labelName).getChildElementById(generateUID(labelName) + "." + getUID() + ".NumericFieldButton");
    }

    protected final Spinner getSpinnerField(String UID, String labelName) {
        return (Spinner) getField(UID, labelName).getChildElementById(generateUID(labelName) + "." + getUID() + ".Spinner");
    }

    protected final SelectBox getSelectBoxField(String UID, String labelName) {
        return (SelectBox) getField(UID, labelName).getChildElementById(generateUID(labelName) + "." + getUID() + ".BoxField");


    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Trigger Method">
    protected abstract void onButtonTrigger(String label);

    protected abstract void onTextFieldInput(String UID, String input, boolean triggerOn);

    protected abstract void onNumericFieldInput(Integer input);

    protected abstract void onSelectBoxFieldChange(Enum value);

    protected abstract void onSpinnerChange(String sTrigger, int currentIndex, ChangeType type);
    // </editor-fold>

    public enum ButtonType {
        IMG,
        TEXT;
    }
}

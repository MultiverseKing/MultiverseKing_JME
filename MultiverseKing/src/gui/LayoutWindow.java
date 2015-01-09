package gui;

import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import java.util.LinkedHashMap;
import tonegod.gui.controls.windows.Window;
import tonegod.gui.core.Element;
import tonegod.gui.core.Screen;

/**
 *
 * @author roah
 */
public abstract class LayoutWindow {

    private boolean isHexGrid = false;
    protected final Screen screen;
    protected final float spacement = 5;
    protected final Vector2f layoutGridSize;// = new Vector2f(230, 20);//min value X == 230
    protected final String name;
    protected Element parent;
    protected Window window = null;
    protected final LinkedHashMap<String, Element> elementList = new LinkedHashMap<>();
    /**
     * Max element count on the selected alignment.
     */
    protected Align windowElementAlignement = Align.Horizontal;
    protected int elementAlignMaxCount = 1;

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
    public LayoutWindow(Screen screen, Element parent, String name) {
        this(screen, parent, name, new Vector2f(230, 20), Align.Horizontal, 1);
    }

    /**
     * Generate a new window with: size (230, 20), Align.Horizontal && Element
     * max count of 1.
     *
     * @param parent window
     * @param name window name
     */
    public LayoutWindow(Screen screen, Element parent, String name, Align align, int elementAlignMaxCount) {
        this(screen, parent, name, new Vector2f(230, 20), align, elementAlignMaxCount);
    }

    /**
     * Generate a new window with : Align.Horizontal && Element max count of 1.
     *
     * @param parent window
     * @param layoutGridSize element size on X and Y.
     */
    public LayoutWindow(Screen screen, Element parent, String name, Vector2f layoutGridSize) {
        this(screen, parent, name, layoutGridSize, Align.Horizontal, 1);
    }

    /**
     *
     * @param parent window
     * @param layoutGridSize element size on X and Y
     * @param windowElementAlignement Alignement to use to position each
     * element.
     * @param elementAlignMaxCount element count on selected alignement.
     */
    public LayoutWindow(Screen screen, Element parent, String name, Vector2f layoutGridSize, Align windowElementAlignement, int elementAlignMaxCount) {
        this.screen = screen;
        this.name = name;
        this.parent = parent;
        this.windowElementAlignement = windowElementAlignement;
        this.elementAlignMaxCount = elementAlignMaxCount;
        this.layoutGridSize = layoutGridSize;
    }

    protected final void updateAlign(Align windowElementAlignement) {
        this.windowElementAlignement = windowElementAlignement;
    }

    protected final void updateAlign(int elementAlignMaxCount) {
        this.elementAlignMaxCount = elementAlignMaxCount;
    }

    protected final void updateAlign(Align windowElementAlignement, int elementAlignMaxCount) {
        this.elementAlignMaxCount = elementAlignMaxCount;
        this.windowElementAlignement = windowElementAlignement;
    }

    // <editor-fold defaultstate="collapsed" desc="Show Method">

    /**
     * Show the window on a free position on the screen.
     *
     * @param position
     */
    protected final void show(Vector2f position) {
        Vector2f size = getWindowSize();
        size.y += 25;
        window = new Window(screen, getUID(), position, size){

            @Override
            public void hideWindow() {
                super.hideWindow();
                onPressCloseAndHide();
            }
            
        };
        window.setUseCloseButton(true);
        
        if (isHexGrid) {
            window.setWindowTitle(name);
        } else {
            window.setWindowTitle("   " + name);
        }
        window.setIgnoreMouse(true);
        window.getDragBar().setIgnoreMouse(true);
        if (parent == null) {
            screen.addElement(window);
        } else {
            parent.addChild(window);
        }
        populate();
    }
    
    private void populate() {
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
                Vector2f pos = new Vector2f(spacement + e.getPosition().x + (column * layoutGridSize.x)
                        + (row % 2 != 0 && isHexGrid ? layoutGridSize.x / 2 : 0)
                        + ((elementAlignMaxCount - 1) / 2 % 2 != 0 && isHexGrid ? -layoutGridSize.x / 2 : 0),
                        e.getPosition().y + window.getDragBarHeight()
                        + (row * (isHexGrid ? layoutGridSize.y * 2 / 3 : layoutGridSize.y))
                        + row * 1.9f + (isHexGrid ? layoutGridSize.y * 0.7f / 3 : 0));
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
    
    protected final void show(VAlign vAlign, HAlign hAlign) {
        show(vAlign, hAlign, false);
    }

    /**
     * Show the window constraint to the screen anchor to the specifiate
     * alignement, Set to null on both alignement to put the window on the
     * center of the screen.
     *
     * @param hAlign Horizontal alignement to use
     * @param vAlign Vertical alignement to use
     */
    protected final void show(VAlign vAlign, HAlign hAlign, boolean isHexGrid) {
        this.isHexGrid = isHexGrid;
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

    protected final void showConstrainToParent(VAlign vAlign, HAlign hAlign) {
        showConstrainToParent(vAlign, hAlign, false);
    }

    /**
     * Generate the window with a grid of sizeX and sizeY, constraint the
     * position of the window to the parent position.
     *
     * @param sizeX grid size on X.
     * @param sizeY grid size on Y.
     * @param align window following the parent.
     */
    protected final void showConstrainToParent(VAlign vAlign, HAlign hAlign, boolean isHexGrid) {
        this.isHexGrid = isHexGrid;
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
                        default:
                            throw new UnsupportedOperationException(vAlign + " Not implemented");
                    }
                }
                if (hAlign != null) {
                    switch (hAlign) {
                        case left:
                            if (vAlign == null) {
                                position.x = -size.x;
                            }
                            break;
                        case right:
                            if (vAlign == null) {
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
        return new Vector2f(column * layoutGridSize.x + (spacement * 2) + (isHexGrid && elementAlignMaxCount % 2 == 0 ? layoutGridSize.x / 2 : 0),
                (row + 0.9f) * (isHexGrid ? layoutGridSize.y * 2 / 3 : layoutGridSize.y) + (isHexGrid ? row * 2f : 0));
    }

    public void setVisible() {
        if (window != null) {
            window.show();
            window.setIsVisible(true);
        } else {
            System.err.println(getUID() + "Can't be set to visible, window does not exist.");
        }
    }

    public void hide() {
        if (window != null) {
            window.hide();
            window.setIsVisible(false);
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

    public void removeFromScreen() {
        if (window != null && parent != null) {
            window.removeAllChildren();
            parent.getElementsAsMap().remove(window.getUID());
            screen.removeElement(window);
        } else if (window != null && parent == null) {
            screen.removeElement(window);
        }
        window = null;
        elementList.clear();
    }

    // </editor-fold>
    protected final String generateUID(String labelName) {
        return labelName.replaceAll("\\s", "");
    }

    public String getUID() {
        return generateUID(name);
    }

    public Window getWindow() {
        return window;
    }

    public Vector2f getLayoutGridSize() {
        return layoutGridSize;
    }
    
    public abstract void onPressCloseAndHide();

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

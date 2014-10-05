package gamemode.gui;

import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import java.util.LinkedHashMap;
import tonegod.gui.controls.windows.Window;
import tonegod.gui.core.Element;
import tonegod.gui.core.ElementManager;

/**
 *
 * @author roah
 */
public abstract class LayoutWindow {

    protected final ElementManager screen;
    protected final float spacement = 5;
    protected final Vector2f layoutGridSize;// = new Vector2f(230, 20);//min value X == 230
    private String name;
    private Element parent;
    private Window window = null;
    protected LinkedHashMap<String, Element> elementList = new LinkedHashMap<String, Element>();
    /**
     * Max element count on the selected alignment.
     */
    protected final int elementAlignMaxCount;
    protected final Align windowElementAlignement;

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
    public LayoutWindow(ElementManager screen, Element parent, String name) {
        this(screen, parent, name, new Vector2f(230, 20), Align.Horizontal, 1);
    }

    /**
     * Generate a new window with: size (230, 20), Align.Horizontal && Element
     * max count of 1.
     *
     * @param parent window
     * @param name window name
     */
    public LayoutWindow(ElementManager screen, Element parent, String name, Align align, int elementAlignMaxCount) {
        this(screen, parent, name, new Vector2f(230, 20), align, elementAlignMaxCount);
    }

    /**
     * Generate a new window with : Align.Horizontal && Element max count of 1.
     *
     * @param parent window
     * @param layoutGridSize element size on X and Y.
     */
    public LayoutWindow(ElementManager screen, Element parent, String name, Vector2f layoutGridSize) {
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
    public LayoutWindow(ElementManager screen, Element parent, String name, Vector2f layoutGridSize, Align windowElementAlignement, int elementAlignMaxCount) {
        this.screen = screen;
        this.name = name;
        this.parent = parent;
        this.windowElementAlignement = windowElementAlignement;
        this.elementAlignMaxCount = elementAlignMaxCount;
//        if (layoutGridSize.x < 230) {
//            layoutGridSize.x = 230;
//        }
//        if (layoutGridSize.y < 20) {
//            layoutGridSize.y = 20;
//        }
        this.layoutGridSize = layoutGridSize;
    }

    // <editor-fold defaultstate="collapsed" desc="Show Method">
    public final void show(Vector2f position) {
        show(position, false);
    }

    /**
     * Show the window on a free position on the screen.
     *
     * @param position
     */
    public final void show(Vector2f position, boolean isHexGrid) {
        Vector2f size = getWindowSize(isHexGrid);
        size.y += 25;
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
                Vector2f pos = new Vector2f(spacement + e.getPosition().x + (column * layoutGridSize.x) + 
                        (row % 2 != 0 && isHexGrid ? layoutGridSize.x / 2 : 0),
                        e.getPosition().y + window.getDragBarHeight() + 
                        (row * (isHexGrid ? layoutGridSize.y * 2 / 3 : layoutGridSize.y)) + 
                        (row * 1.9f) + (isHexGrid ? layoutGridSize.y * 0.8f / 3 : 0)
                );
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
        Vector2f size = getWindowSize(isHexGrid);
        Vector2f position = new Vector2f();
        if (vAlign == null && hAlign == null) {
            position.x = screen.getWidth() / 2 - size.x / 2;
            position.y = screen.getHeight() / 2 - size.y / 2;
            show(position, isHexGrid);
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
            show(position, isHexGrid);
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
        Vector2f size = getWindowSize(isHexGrid);
        if (parent != null) {
            Vector2f position = new Vector2f();
            if (vAlign == null && hAlign == null) {
                show(position, isHexGrid);
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
                show(position, isHexGrid);
            }
        } else {
            throw new UnsupportedOperationException("There is no parent to align the element to.");
        }
    }

    private Vector2f getWindowSize(boolean isHexGrid) {
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
        return new Vector2f(column * layoutGridSize.x + (spacement * 2) + (isHexGrid ? layoutGridSize.x/2 : 0),
                row * (isHexGrid ? layoutGridSize.y * 2.2f / 3 : layoutGridSize.y) + (3 * row + 5));
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

    public final void removeFromScreen() {
        if (window != null && parent != null) {
            parent.removeChild(window);
        } else if (window != null && parent == null) {
            screen.removeElement(window);
        }
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

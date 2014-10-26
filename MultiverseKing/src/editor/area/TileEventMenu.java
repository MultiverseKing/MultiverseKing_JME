package editor.area;

import com.jme3.math.Vector2f;
import gui.EditorWindow;
import hexsystem.area.AreaEventComponent;
import hexsystem.area.AreaEventComponent.Event;
import static hexsystem.area.AreaEventComponent.Event.trigger;
import hexsystem.area.AreaEventSystem;
import java.util.ArrayList;
import java.util.List;
import org.hexgridapi.utility.HexCoordinate;
import tonegod.gui.controls.buttons.ButtonAdapter;
import tonegod.gui.controls.menuing.Menu;
import tonegod.gui.core.Element;
import tonegod.gui.core.Screen;

/**
 *
 * @author roah
 */
public class TileEventMenu extends EditorWindow {

    private final AreaEventSystem system;
    private HexCoordinate inspectedTilePos;
    private Menu addNewEventMenu;
    private ArrayList<Event> currentEvent = new ArrayList<Event>();

    public HexCoordinate getInspectedTilePos() {
        return inspectedTilePos;
    }

    TileEventMenu(AreaEventSystem system, Screen screen, Element parent, HexCoordinate inspectedTilePos) {
        super(screen, parent, inspectedTilePos + " Tile Event Menu");
        this.inspectedTilePos = inspectedTilePos;
        this.system = system;
    }

    void setInpectedTile(HexCoordinate inspectedTilePos) {
        this.inspectedTilePos = inspectedTilePos;
        initializeEvents();
        reload();
        window.getDragBar().setText("   " + inspectedTilePos + " Tile Event Menu");
    }

    public void show() {
        if (window != null) {
            setVisible();
        } else {
            initialize();
            showConstrainToParent(VAlign.bottom, HAlign.right);
            window.setUseCloseButton(true);
        }
    }

    private void initialize() {
        /**
         * Menu used to add a new event.
         */
        addNewEventMenu = new Menu(screen, true) {
            @Override
            public void onMenuItemClicked(int index, Object value, boolean isToggled) {
                addNewEventMenuTrigger((Event) value);
            }
        };
        for (Event e : Event.values()) {
            addNewEventMenu.addMenuItem(e.toString(), e, null);
        }
        screen.addElement(addNewEventMenu);

        initializeEvents();
        populateMenu();
    }

    private void initializeEvents() {
        currentEvent.clear();
        AreaEventComponent comp = system.getValue(inspectedTilePos);
        if(comp != null){
            for (Event e : comp.getEvent()) {
                currentEvent.add(e);
            }
        }
    }

    private void addNewEventMenuTrigger(Event event) {
        system.addEvent(inspectedTilePos, event);
        currentEvent.add(event);
        reload();
    }

    private void reload() {
        removeAndClear();
        populateMenu();
        showConstrainToParent(VAlign.bottom, HAlign.right);
        window.setUseCloseButton(true);
    }

    /**
     * initialize data for current position if not null.
     */
    private void populateMenu() {
        addButtonField("Add new Event");
        for (Event e : currentEvent) {
            switch (e) {
                case Start:
                    addLabelField("Player Starting Position", HAlign.left, new Vector2f());
                    break;
                case trigger:
                    break;
                default:
                    throw new UnsupportedOperationException(e + " is not currently supported.");
            }
        }
    }

    @Override
    protected void onButtonTrigger(String event) {
        if (event.equals("Add new Event")) {
            ButtonAdapter btn = getButtonField("Add new Event", false);
            addNewEventMenu.showMenu(null, btn.getAbsoluteX(), btn.getAbsoluteY() - addNewEventMenu.getDimensions().y);
        }
    }
}

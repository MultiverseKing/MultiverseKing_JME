package editor.area;

import gui.EditorWindow;
import hexsystem.area.AreaEventComponent;
import hexsystem.area.AreaEventComponent.Event;
import static hexsystem.area.AreaEventComponent.Event.Trigger;
import hexsystem.area.AreaEventSystem;
import java.util.ArrayList;
import org.hexgridapi.utility.HexCoordinate;
import tonegod.gui.controls.buttons.ButtonAdapter;
import tonegod.gui.controls.menuing.Menu;
import tonegod.gui.controls.menuing.MenuItem;
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
    private ArrayList<Event> currentEvent = new ArrayList<>();

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
        window.getDragBar().setText("   " + inspectedTilePos.getAsOffset() + " Tile Event Menu");
    }

    public void show() {
        if (window != null) {
            setVisible();
        } else {
            initialize();
            showConstrainToParent(VAlign.bottom, HAlign.right);
//            window.setUseCloseButton(true);
        }
    }

    private void initialize() {
        /**
         * Menu used to add a new event.
         */
        addNewEventMenu = new Menu(screen, true) {
            @Override
            public void onMenuItemClicked(int index, Object value, boolean isToggled) {
                addEvent((Event) value);
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
        if (comp != null) {
            for (Event e : comp.getEvent()) {
                currentEvent.add(e);
            }
        }
    }

    private void addEvent(Event event) {
        if (event.equals(Event.Start) || currentEvent.size() < 2) {
            system.updateEvent(inspectedTilePos, event, true);
        }
        currentEvent.add(event);
        reload();
    }

    private void removeEvent(Event event) {
        if (event.equals(Event.Start) || currentEvent.size() < 2) {
            system.updateEvent(inspectedTilePos, event, false);
        }
        currentEvent.remove(event);
        reload();
    }

    private void reload() {
        removeFromScreen();
        populateMenu();
        showConstrainToParent(VAlign.bottom, HAlign.right);
//        window.setUseCloseButton(true);
    }

    /**
     * initialize data for current position if not null.
     */
    private void populateMenu() {
        addButtonField("Add new Event");
        for (Event e : currentEvent) {
            switch (e) {
                case Start:
                    addButtonField("Start Position", HAlign.left, "Delete", HAlign.full);
                    break;
                case Trigger:
                    addButtonField("Trigger", HAlign.left, "Delete", HAlign.full);
                    break;
                default:
                    throw new UnsupportedOperationException(e + " is not currently supported.");
            }
        }
    }

    private void updateMenu() {
        for (Event e : Event.values()) {
            if (currentEvent.contains(e)) {
                addNewEventMenu.removeMenuItem(e);
            } else if (!menuContain(e)) {
                addNewEventMenu.addMenuItem(e.toString(), e, null);
            }
        }
    }

    private boolean menuContain(Event e) {
        for (MenuItem m : addNewEventMenu.getMenuItems()) {
            if (((Event) m.getValue()).equals(e)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onButtonTrigger(String event) {
        if (event.equals("Add new Event")) {
            ButtonAdapter btn = getButtonField("Add new Event", false);
            updateMenu();
            addNewEventMenu.showMenu(null, btn.getAbsoluteX(), btn.getAbsoluteY() - addNewEventMenu.getDimensions().y);
        } else if (event.contains("Delete")) {
            if (event.contains("StartPosition")) {
                removeEvent(Event.Start);
            } else if (event.contains("Trigger")) {
                removeEvent(Event.Trigger);
            }
        }
    }

    @Override
    public void onPressCloseAndHide() {
    }

    @Override
    public void removeFromScreen() {
        super.removeFromScreen();
        system.updateEvents(inspectedTilePos, currentEvent);
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.multiversekingesapi.field.component;

import com.simsilica.es.PersistentComponent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.hexgridapi.utility.HexCoordinate;

/**
 *
 * @author roah
 */
public class AreaEventComponent implements PersistentComponent {

    private final ArrayList<Event> events;
    private final HexCoordinate position;

    public AreaEventComponent(HexCoordinate position, Event event){
        events = new ArrayList<>();
        events.add(event);
        this.position = position;
    }
    
    public AreaEventComponent(HexCoordinate position, ArrayList<Event> event) {
        this.events = event;
        this.position = position;
    }

    public List<Event> getEvents() {
        return Collections.unmodifiableList(events);
    }

    public HexCoordinate getPosition() {
        return position;
    }
    
    public AreaEventComponent cloneAndRemove(Event event) {
        events.remove(event);
        if(events.size() < 1){
            return null;
        } else {
            return clone();
        }
    }
    public AreaEventComponent cloneAndAdd(Event event) {
        events.add(event);
        return clone();
    }
    
    @Override
    public AreaEventComponent clone(){
        return new AreaEventComponent(position, events);
    }

    public enum Event {
        Start,
        Trigger,
    }
}

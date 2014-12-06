/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hexsystem.area;

import com.simsilica.es.PersistentComponent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author roah
 */
public class AreaEventComponent implements PersistentComponent {

    private final ArrayList<Event> events;

    public AreaEventComponent(Event event){
        events = new ArrayList<Event>();
        events.add(event);
    }
    
    public AreaEventComponent(ArrayList<Event> event) {
        this.events = event;
    }

    public List<Event> getEvent() {
        return Collections.unmodifiableList(events);
    }

    public AreaEventComponent cloneAndRemove(Event event) {
        events.remove(event);
        return clone();
    }
    public AreaEventComponent cloneAndAdd(Event event) {
        events.add(event);
        return clone();
    }
    
    @Override
    public AreaEventComponent clone(){
        return new AreaEventComponent(events);
    }

    public enum Event {

        Start,
        trigger,
    }
}

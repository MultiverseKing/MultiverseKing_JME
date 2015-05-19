package org.multiversekingesapi.field.component;

import com.simsilica.es.EntityComponent;

/**
 *
 * @author roah
 */
public class ATBComponent implements EntityComponent {

    private final byte current;
    private final byte max;

    public ATBComponent(byte max) {
        this.max = max;
        this.current = max;
    }
    public ATBComponent(byte current, byte max) {
        this.current = current;
        this.max = max;
    }

    public byte getCurrent() {
        return current;
    }

    public byte getMax() {
        return max;
    }
}

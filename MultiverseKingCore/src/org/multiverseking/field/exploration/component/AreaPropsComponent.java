package org.multiverseking.field.exploration.component;

import com.simsilica.es.EntityComponent;

/**
 * Component used for object on the Room field.
 * @author roah
 */
public class AreaPropsComponent implements EntityComponent {

    private final boolean isTrigger;
    private final boolean isImmune;
    private final String propsName;

    /**
     * 
     * @param isTrigger Define if the entity can be activated like a button by a
     * character.
     * @param isImmune Define if the entity respond to undirect effect as when a
     * firebold hit it.
     */
    public AreaPropsComponent(boolean isTrigger, boolean isImmune, String propsName) {
        this.isTrigger = isTrigger;
        this.isImmune = isImmune;
        this.propsName = propsName;
    }

    public AreaPropsComponent(String propsName) {
        this.isTrigger = false;
        this.isImmune = false;
        this.propsName = propsName;
    }

    public boolean isIsTrigger() {
        return isTrigger;
    }

    public boolean isIsImmune() {
        return isImmune;
    }

    public String getPropsName() {
        return propsName;
    }
}

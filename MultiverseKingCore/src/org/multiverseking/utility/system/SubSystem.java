package org.multiverseking.utility.system;

/**
 * Allow a system specifie to another system that it is dependend on it.
 * Used by the root system to tell to the other system his current state. 
 * @author roah
 */
public interface SubSystem {
    void rootSystemIsRemoved();
}
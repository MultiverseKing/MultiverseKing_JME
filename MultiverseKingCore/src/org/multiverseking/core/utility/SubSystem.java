package org.multiverseking.core.utility;

/**
 * Allow a system specifie to another system that it is dependend on it.
 * Used by the root system to tell to the other system his current state. 
 * @author roah
 * @todo seam only usefull to the render system
 */
public interface SubSystem {
    void rootSystemIsRemoved();
}
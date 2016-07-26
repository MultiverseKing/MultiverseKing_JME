package org.multiverseking.core.utility;

/**
 *
 * @author roah
 * @todo seam only usefull to the render system
 */
public interface RootSystem {
    void registerSubSystem(SubSystem subSystem);
    void removeSubSystem(SubSystem subSystem);
    void cleanupSubSystem();
}

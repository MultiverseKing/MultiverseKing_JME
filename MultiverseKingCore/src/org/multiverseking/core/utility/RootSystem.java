package org.multiverseking.core.utility;

/**
 *
 * @author roah
 */
public interface RootSystem {
    void registerSubSystem(SubSystem subSystem);
    void removeSubSystem(SubSystem subSystem);
    void cleanupSubSystem();
}

package com.gitlab.cdagaming.craftpresence.impl;

import com.gitlab.cdagaming.craftpresence.CraftPresence;

public interface Module {
    /**
     * Clears FULL Data from this Module
     */
    default void emptyData() {
        // N/A
    }

    /**
     * Clears Runtime Client Data from this Module (PARTIAL Clear)
     */
    default void clearClientData() {
        // N/A
    }

    /**
     * Module Event to Occur on each tick within the Application
     */
    default void onTick() {
        // N/A
    }

    /**
     * Synchronizes Data related to this module, if needed
     */
    default void updateData() {
        // N/A
    }

    /**
     * Updates RPC Data related to this Module
     */
    default void updatePresence() {
        // N/A
    }

    /**
     * Updates and Initializes Module Data, based on found Information
     */
    default void getAllData() {
        // N/A
    }

    default boolean isEnabled() {
        return true;
    }

    default void setEnabled(boolean state) {
        // N/A
    }

    default boolean isInUse() {
        return true;
    }

    default void setInUse(boolean state) {
        // N/A
    }

    default boolean canBeLoaded() {
        return CraftPresence.SYSTEM.HAS_LOADED && CraftPresence.SYSTEM.HAS_GAME_LOADED;
    }
}

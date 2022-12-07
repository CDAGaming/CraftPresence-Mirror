package com.gitlab.cdagaming.craftpresence.impl;

public interface Module {
    /**
     * Clears FULL Data from this Module
     */
    void emptyData();

    /**
     * Clears Runtime Client Data from this Module (PARTIAL Clear)
     */
    void clearClientData();

    /**
     * Module Event to Occur on each tick within the Application
     */
    void onTick();

    /**
     * Synchronizes Data related to this module, if needed
     */
    void updateData();

    /**
     * Updates RPC Data related to this Module
     */
    void updatePresence();

    /**
     * Updates and Initializes Module Data, based on found Information
     */
    void getAllData();

    boolean isEnabled();

    void setEnabled(boolean state);

    boolean isInUse();

    void setInUse(boolean state);
}

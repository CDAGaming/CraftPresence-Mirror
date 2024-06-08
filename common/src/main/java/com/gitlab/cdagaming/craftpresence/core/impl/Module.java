/*
 * MIT License
 *
 * Copyright (c) 2018 - 2024 CDAGaming (cstack2011@yahoo.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.gitlab.cdagaming.craftpresence.core.impl;

import com.gitlab.cdagaming.craftpresence.core.Constants;

import java.util.function.Supplier;

/**
 * Module Section defining properties to be used for Rich Presence Displays
 *
 * @author CDAGaming
 */
public interface Module {
    /**
     * Clears FULL Data from this Module
     */
    default void emptyData() {
        queueConfigScan();
        queueInternalScan();
        clearFieldData();
        clearClientData();
    }

    /**
     * Clears Field Data from this Module (PARTIAL Clear)
     */
    default void clearFieldData() {
        // N/A
    }

    /**
     * Clears Runtime Client Data from this Module (PARTIAL Clear)
     */
    default void clearClientData() {
        setInUse(false);
        clearAttributes();
    }

    /**
     * Clears Active Module Data (PARTIAL Clear)
     */
    default void clearActiveData() {
        clearClientData();
    }

    /**
     * Clears Module Attribute Data from this Module (PARTIAL Clear)
     */
    default void clearAttributes() {
        // N/A
    }

    /**
     * Module Event to Occur on each tick within the Application
     * <p>This runs prior to the {@link Module#onTick()} method
     */
    default void preTick() {
        // N/A
    }

    /**
     * Module Event to Occur on each tick within the Application
     * <p>This runs after the {@link Module#onTick()} method
     */
    default void postTick() {
        // N/A
    }

    /**
     * Module Event to Occur on each tick within the Application
     */
    default void onTick() {
        preTick();

        setEnabled(canBeEnabled());
        final boolean needsConfigUpdate = isEnabled() && !hasScannedConfig() && canFetchConfig();
        final boolean needsInternalUpdate = isEnabled() && !hasScannedInternals() && canFetchInternals();

        if (needsConfigUpdate) {
            scanConfigData();
            markConfigScanned();
        }
        if (needsInternalUpdate) {
            scanInternalData();
            markInternalsScanned();
        }

        if (isEnabled()) {
            if (canBeUsed()) {
                setInUse(true);
                updateData();
            } else if (isInUse()) {
                clearActiveData();
            }
        } else if (isInUse()) {
            emptyData();
        }

        postTick();
    }

    /**
     * Synchronizes Data related to this module, if needed
     */
    void updateData();

    /**
     * Initializes RPC Data related to this Module
     */
    void initPresence();

    /**
     * Updates RPC Data related to this Module
     */
    void updatePresence();

    /**
     * Updates and Initializes Module Data, based on found Information
     */
    void getInternalData();

    /**
     * Updates and Initializes Module Data, based on found Information
     */
    void getConfigData();

    /**
     * Synchronizes the Specified Argument as an RPC Message or an Icon Placeholder
     *
     * @param argumentName The Specified Argument to Synchronize for
     * @param condition    If specified, the extra conditions required to interpret the event
     * @param event        The data to attach to the Specified Argument
     * @param plain        Whether the expression should be parsed as a plain string
     */
    void syncArgument(final String argumentName, final Supplier<Boolean> condition, final Supplier<Object> event, final boolean plain);

    /**
     * Synchronizes the Specified Argument as an RPC Message or an Icon Placeholder
     *
     * @param argumentName The Specified Argument to Synchronize for
     * @param event        The data to attach to the Specified Argument
     * @param plain        Whether the expression should be parsed as a plain string
     */
    default void syncArgument(final String argumentName, final Supplier<Object> event, final boolean plain) {
        syncArgument(argumentName, null, event, plain);
    }

    /**
     * Synchronizes the Specified Argument as an RPC Message or an Icon Placeholder
     *
     * @param argumentName The Specified Argument to Synchronize for
     * @param condition    If specified, the extra conditions required to interpret the event
     * @param event        The data to attach to the Specified Argument
     */
    default void syncArgument(final String argumentName, final Supplier<Boolean> condition, final Supplier<Object> event) {
        syncArgument(argumentName, condition, event, false);
    }

    /**
     * Synchronizes the Specified Argument as an RPC Message or an Icon Placeholder
     *
     * @param argumentName The Specified Argument to Synchronize for
     * @param event        The data to attach to the Specified Argument
     */
    default void syncArgument(final String argumentName, final Supplier<Object> event) {
        syncArgument(argumentName, null, event);
    }

    /**
     * Retrieve a module-safe Supplier event for the specified args
     *
     * @param condition If specified, the extra conditions required to interpret the event
     * @param event     The original event to interpret
     * @return The processed event, relying on {@link Module#isInUse()} and any extra conditions
     */
    default Supplier<Object> getModuleFunction(final Supplier<Boolean> condition, final Supplier<Object> event) {
        return () -> isInUse() && (condition == null || condition.get()) && event != null ? event.get() : null;
    }

    /**
     * Retrieve a module-safe Supplier event for the specified args
     *
     * @param event The original event to interpret
     * @return The processed event, relying on {@link Module#isInUse()}
     */
    default Supplier<Object> getModuleFunction(final Supplier<Object> event) {
        return getModuleFunction(null, event);
    }

    /**
     * Display the specified module exception
     *
     * @param ex The {@link Throwable} exception to display
     */
    default void printException(final Throwable ex) {
        Constants.LOG.debugError(ex);
    }

    /**
     * Scans for applicable data related to this Module, within a new Thread.
     */
    default void scanConfigData() {
        Constants.getThreadFactory().newThread(() -> {
            try {
                this.getConfigData();
            } catch (Throwable ex) {
                printException(ex);
            }
        }).start();
    }

    /**
     * Scans for applicable data related to this Module, within a new Thread.
     */
    default void scanInternalData() {
        Constants.getThreadFactory().newThread(() -> {
            try {
                this.getInternalData();
            } catch (Throwable ex) {
                printException(ex);
            }
        }).start();
    }

    /**
     * Returns whether the module can be enabled
     *
     * @return {@link Boolean#TRUE} if this module can be enabled
     */
    default boolean canBeEnabled() {
        return true;
    }

    /**
     * Returns whether the module is currently enabled
     *
     * @return {@link Boolean#TRUE} if this module is enabled
     */
    default boolean isEnabled() {
        return true;
    }

    /**
     * Sets whether the module is currently enabled
     *
     * @param state the new enabled state
     */
    default void setEnabled(boolean state) {
        // N/A
    }

    /**
     * Returns whether the module can be used
     *
     * @return {@link Boolean#TRUE} if this module can be used
     */
    default boolean canBeUsed() {
        return true;
    }

    /**
     * Returns whether the module is currently active and in use
     *
     * @return {@link Boolean#TRUE} if this module is currently active and in use
     */
    default boolean isInUse() {
        return true;
    }

    /**
     * Sets whether the module is currently active and in use
     *
     * @param state the new inUse state
     */
    default void setInUse(boolean state) {
        // N/A
    }

    /**
     * Determines whether the module can currently be loaded
     *
     * @return {@link Boolean#TRUE} if this module can currently be loaded
     */
    default boolean canBeLoaded() {
        return Constants.HAS_GAME_LOADED;
    }

    /**
     * Determines whether we can check for data that can be accessed by the module
     *
     * @return {@link Boolean#TRUE} if this module can access scan data
     */
    default boolean canFetchInternals() {
        return true;
    }

    /**
     * Determine whether we have already scanned for internal module data
     *
     * @return {@link Boolean#TRUE} if already scanned
     */
    default boolean hasScannedInternals() {
        return true;
    }

    /**
     * Sets whether we have already scanned for internal module data
     *
     * @param state the new scan state
     */
    default void setScannedInternals(final boolean state) {
        // N/A
    }

    /**
     * Mark Internal Module Data as scanned
     */
    default void markInternalsScanned() {
        setScannedInternals(true);
    }

    /**
     * Queue a new internal scan to occur when possible
     */
    default void queueInternalScan() {
        setScannedInternals(false);
    }

    /**
     * Determines whether we can check for data that can be accessed by the module
     *
     * @return {@link Boolean#TRUE} if this module can access config data
     */
    default boolean canFetchConfig() {
        return true;
    }

    /**
     * Determine whether we have already scanned for config module data
     *
     * @return {@link Boolean#TRUE} if already scanned
     */
    default boolean hasScannedConfig() {
        return true;
    }

    /**
     * Sets whether we have already scanned for config module data
     *
     * @param state the new scan state
     */
    default void setScannedConfig(final boolean state) {
        // N/A
    }

    /**
     * Mark Config Module Data as scanned
     */
    default void markConfigScanned() {
        setScannedConfig(true);
    }

    /**
     * Queue a new config scan to occur when possible
     */
    default void queueConfigScan() {
        setScannedConfig(false);
    }
}

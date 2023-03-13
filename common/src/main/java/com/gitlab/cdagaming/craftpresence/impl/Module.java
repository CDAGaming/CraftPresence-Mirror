/*
 * MIT License
 *
 * Copyright (c) 2018 - 2023 CDAGaming (cstack2011@yahoo.com)
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

package com.gitlab.cdagaming.craftpresence.impl;

import com.gitlab.cdagaming.craftpresence.CraftPresence;
import com.gitlab.cdagaming.craftpresence.utils.FileUtils;

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
        return CraftPresence.SYSTEM.HAS_LOADED && CraftPresence.SYSTEM.HAS_GAME_LOADED;
    }

    /**
     * Determines whether {@link FileUtils} data can be accessed by the module
     *
     * @return {@link Boolean#TRUE} if this module can access {@link FileUtils} data
     */
    default boolean canFetchData() {
        return FileUtils.canScanClasses();
    }
}

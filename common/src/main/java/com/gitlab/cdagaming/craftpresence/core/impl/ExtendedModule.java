/*
 * MIT License
 *
 * Copyright (c) 2018 - 2025 CDAGaming (cstack2011@yahoo.com)
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

import com.gitlab.cdagaming.craftpresence.core.config.element.ModuleData;
import com.gitlab.cdagaming.craftpresence.core.config.element.PresenceData;

/**
 * Module Section defining properties to be used for Rich Presence Displays
 * <p>This implementation features integration with {@link ModuleData} and {@link PresenceData}
 *
 * @author CDAGaming
 */
public interface ExtendedModule extends Module {
    /**
     * Retrieve {@link ModuleData} for the specified element
     *
     * @param key The element to interpret
     * @return the module data, if found
     */
    default ModuleData getData(final String key) {
        throw new UnsupportedOperationException();
    }

    /**
     * Retrieve the default identifier for this module
     *
     * @return the default identifier
     */
    default String getDefaultId() {
        return "default";
    }

    /**
     * Retrieve the default data for this module
     *
     * @return the default module data
     */
    default ModuleData getDefaultData() {
        return getData(getDefaultId());
    }

    /**
     * Retrieve the first non-null {@link ModuleData} from the list
     *
     * @param modules The modules to interpret
     * @return the first non-null module, or the default data if none found
     */
    default ModuleData getOrDefault(final ModuleData... modules) {
        for (ModuleData module : modules) {
            if (module != null) {
                return module;
            }
        }
        return getDefaultData();
    }

    /**
     * Retrieve the first non-null {@link ModuleData} from the list
     *
     * @param keys The modules to interpret
     * @return the first non-null module, or the default data if none found
     */
    default ModuleData getOrDefault(final String... keys) {
        for (String key : keys) {
            final ModuleData data = getData(key);
            if (data != null) {
                return data;
            }
        }
        return getDefaultData();
    }

    /**
     * Retrieve {@link PresenceData} from the specified {@link ModuleData}
     *
     * @param data The {@link ModuleData} to interpret
     * @return the found {@link PresenceData}, otherwise return null
     */
    default PresenceData getPresenceData(final ModuleData data) {
        return data != null ? (PresenceData) data.getProperty("data") : null;
    }

    /**
     * Retrieve {@link PresenceData} from the specified {@link ModuleData}
     *
     * @param keys The modules to interpret
     * @return the found {@link PresenceData}, otherwise return null
     */
    default PresenceData getPresenceData(final String... keys) {
        return getPresenceData(getOrDefault(keys));
    }

    /**
     * Retrieve the override text for the specified module {@link PresenceData}
     * <p>Only applies when enabled and {@link PresenceData#useAsMain} is false
     *
     * @param data The {@link ModuleData} to interpret
     * @return the retrieved override text if possible, otherwise return null
     */
    String getOverrideText(final ModuleData data);

    /**
     * Retrieve the override text for the specified module {@link PresenceData}
     * <p>Only applies when enabled and {@link PresenceData#useAsMain} is false
     *
     * @param keys The modules to interpret
     * @return the retrieved override text if possible, otherwise return null
     */
    default String getOverrideText(final String... keys) {
        return getOverrideText(getOrDefault(keys));
    }

    /**
     * Retrieve the override text for the specified module {@link PresenceData}
     * <p>Only applies when enabled and {@link PresenceData#useAsMain} is false
     *
     * @param fallback The string to default to, if `overrideText` is null
     * @param data     The {@link ModuleData} to interpret
     * @return the retrieved override text if possible, otherwise return `fallback`
     */
    default String getResult(final String fallback, final ModuleData data) {
        final String overrideText = getOverrideText(data);
        return overrideText != null ? overrideText : fallback;
    }

    /**
     * Retrieve the override text for the specified module {@link PresenceData}
     * <p>Only applies when enabled and {@link PresenceData#useAsMain} is false
     *
     * @param fallback The string to default to, if `overrideText` is null
     * @param keys     The modules to interpret
     * @return the retrieved override text if possible, otherwise return `fallback`
     */
    default String getResult(final String fallback, final String... keys) {
        return getResult(fallback, getOrDefault(keys));
    }
}

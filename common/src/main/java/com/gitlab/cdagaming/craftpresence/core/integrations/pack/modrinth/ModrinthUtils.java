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

package com.gitlab.cdagaming.craftpresence.core.integrations.pack.modrinth;

import com.gitlab.cdagaming.craftpresence.core.integrations.pack.Pack;
import com.google.gson.JsonElement;
import io.github.cdagaming.unicore.utils.FileUtils;

import java.io.File;
import java.util.Properties;
import java.util.function.Supplier;

/**
 * Set of Utilities used to Parse Modrinth Instance Information
 *
 * @author CDAGaming
 */
public class ModrinthUtils extends Pack {
    public ModrinthUtils(final Supplier<Boolean> isEnabled) {
        super(isEnabled);
    }

    public ModrinthUtils(final boolean isEnabled) {
        super(isEnabled);
    }

    public ModrinthUtils() {
        super();
    }

    @Override
    public boolean load() {
        return findWithSystem() || findWithLegacy();
    }

    /**
     * Attempt to retrieve instance data via system properties
     *
     * @return {@link Boolean#TRUE} if data was found
     */
    private boolean findWithSystem() {
        // 2025-08-09: Utilize the Launcher's System Properties if available
        // Original Ref: https://github.com/modrinth/code/commit/f10e0f2bf16fb99c4e5256e0a512dbdf53a24495
        try {
            final Properties props = System.getProperties();
            if (props.containsKey("modrinth.profile.name")) {
                setPackData(
                        System.getProperty("modrinth.profile.name")
                );
            }
        } catch (Exception ex) {
            printException(ex);
        }
        return hasPackName();
    }

    /**
     * Attempt to retrieve instance data via file properties
     *
     * @return {@link Boolean#TRUE} if data was found
     */
    private boolean findWithLegacy() {
        // Utilize Legacy Property Route, if unable to use System Properties
        final File packLocation = new File("profile.json");

        if (packLocation.exists()) {
            try {
                setPackData(
                        FileUtils.getJsonData(packLocation, JsonElement.class)
                                .getAsJsonObject()
                                .getAsJsonObject("metadata")
                                .getAsJsonPrimitive("name")
                                .getAsString()
                );
            } catch (Exception ex) {
                printException(ex);
            }
        }
        return hasPackName();
    }
}

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

package com.gitlab.cdagaming.craftpresence.core.integrations.pack.curse;

import com.gitlab.cdagaming.craftpresence.core.integrations.pack.Pack;
import io.github.cdagaming.unicore.utils.FileUtils;

import java.io.File;
import java.util.function.Supplier;

/**
 * Set of Utilities used to Parse Curse Manifest Information
 * <p>Applies to: Twitch, Curse, and GDLauncher
 *
 * @author CDAGaming
 */
public class CurseUtils extends Pack {
    public CurseUtils(final Supplier<Boolean> isEnabled) {
        super(isEnabled);
    }

    public CurseUtils(final boolean isEnabled) {
        super(isEnabled);
    }

    public CurseUtils() {
        super();
    }

    @Override
    public boolean load() {
        File packLocation;

        // Attempt to Gain Curse Pack Info from the manifest.json file
        // This will typically work on released/exported/imported packs
        // But will fail with Custom/User-Created Packs
        // Note: This additionally works in the same way for GDLauncher packs of the same nature
        packLocation = new File("manifest.json");
        if (!packLocation.exists()) {
            // If it fails to get the information from the manifest.json
            // Attempt to read Pack info from the minecraftinstance.json file
            // As Most if not all types of Curse Packs contain this file
            packLocation = new File("minecraftinstance.json");
        }

        if (packLocation.exists()) {
            try {
                setPackData(
                        FileUtils.getJsonData(packLocation)
                                .getAsJsonObject()
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

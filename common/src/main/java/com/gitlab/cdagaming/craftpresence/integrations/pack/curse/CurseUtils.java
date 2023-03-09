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

package com.gitlab.cdagaming.craftpresence.integrations.pack.curse;

import com.gitlab.cdagaming.craftpresence.CraftPresence;
import com.gitlab.cdagaming.craftpresence.integrations.pack.Pack;
import com.gitlab.cdagaming.craftpresence.integrations.pack.curse.impl.CurseInstance;
import com.gitlab.cdagaming.craftpresence.integrations.pack.curse.impl.Manifest;
import com.gitlab.cdagaming.craftpresence.utils.CommandUtils;
import com.gitlab.cdagaming.craftpresence.utils.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Set of Utilities used to Parse Curse Manifest Information
 * <p>Applies to: Twitch, Curse, and GDLauncher
 *
 * @author CDAGaming
 */
public class CurseUtils extends Pack {
    @Override
    public boolean isEnabled() {
        return CraftPresence.CONFIG.generalSettings.detectCurseManifest;
    }

    @Override
    public boolean load() {
        try {
            // Attempt to Gain Curse Pack Info from the manifest.json file
            // This will typically work on released/exported/imported packs
            // But will fail with Custom/User-Created Packs
            // Note: This additionally works in the same way for GDLauncher packs of the same nature
            final Manifest manifest = FileUtils.getJsonData(new File("manifest.json"), Manifest.class);
            if (manifest != null) {
                setPackName(manifest.name);
            }
        } catch (Exception ex) {
            try {
                // If it fails to get the information from the manifest.json
                // Attempt to read Pack info from the minecraftinstance.json file
                // As Most if not all types of Curse Packs contain this file
                // Though it is considered a fallback due to how much it's parsing
                if (showException(ex)) {
                    ex.printStackTrace();
                }
                final CurseInstance instance = FileUtils.getJsonData(new File("minecraftinstance.json"), CurseInstance.class);
                if (instance != null) {
                    setPackName(instance.name);
                }
            } catch (Exception ex2) {
                if (showException(ex2)) {
                    ex2.printStackTrace();
                }
            }
        }
        return hasPackName();
    }
}

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

package com.gitlab.cdagaming.craftpresence.integrations.pack.technic;

import com.gitlab.cdagaming.craftpresence.CraftPresence;
import com.gitlab.cdagaming.craftpresence.core.utils.FileUtils;
import com.gitlab.cdagaming.craftpresence.integrations.pack.Pack;
import com.gitlab.cdagaming.craftpresence.utils.SystemUtils;

import java.io.File;

/**
 * Set of Utilities used to Parse Technic Launcher Pack Information
 *
 * @author CDAGaming
 */
public class TechnicUtils extends Pack {
    @Override
    public boolean isEnabled() {
        return CraftPresence.CONFIG.generalSettings.detectTechnicPack;
    }

    @Override
    public boolean load() {
        final File packLocation = new File(new File(SystemUtils.USER_DIR).getParentFile().getParentFile() + File.separator + "installedPacks");

        if (packLocation.exists()) {
            try {
                final String selected = FileUtils.getJsonData(packLocation)
                        .getAsJsonObject()
                        .getAsJsonPrimitive("selected")
                        .getAsString();

                if (SystemUtils.USER_DIR.contains(selected)) {
                    setPackData(selected);
                }
            } catch (Exception ex) {
                if (showException(ex)) {
                    ex.printStackTrace();
                }
            }
        }
        return hasPackName();
    }
}

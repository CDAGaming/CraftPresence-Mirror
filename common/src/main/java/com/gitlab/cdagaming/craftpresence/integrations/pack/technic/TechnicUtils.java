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
import com.gitlab.cdagaming.craftpresence.integrations.pack.Pack;
import com.gitlab.cdagaming.craftpresence.utils.CommandUtils;
import com.gitlab.cdagaming.craftpresence.utils.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;

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
        try {
            final File installedPacks = new File(CraftPresence.SYSTEM.USER_DIR + File.separator + ".." + File.separator + ".." + File.separator + "installedPacks");
            final TechnicPack technicPack = FileUtils.getJsonData(installedPacks, TechnicPack.class);

            if (technicPack != null) {
                if (CraftPresence.SYSTEM.USER_DIR.contains(technicPack.selected)) {
                    setPackName(technicPack.selected);
                }
            }
        } catch (Exception ex) {
            if (ex.getClass() != FileNotFoundException.class || CommandUtils.isVerboseMode()) {
                ex.printStackTrace();
            }
        }
        return hasPackName();
    }
}

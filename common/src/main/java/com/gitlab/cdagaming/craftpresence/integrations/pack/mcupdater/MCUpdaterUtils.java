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

package com.gitlab.cdagaming.craftpresence.integrations.pack.mcupdater;

import com.gitlab.cdagaming.craftpresence.CraftPresence;
import com.gitlab.cdagaming.craftpresence.integrations.pack.Pack;
import com.gitlab.cdagaming.craftpresence.utils.FileUtils;
import com.gitlab.cdagaming.craftpresence.utils.StringUtils;

import java.io.File;

/**
 * Set of Utilities used to Parse MCUpdater Instance Information
 *
 * @author CDAGaming
 */
public class MCUpdaterUtils extends Pack {
    @Override
    public boolean isEnabled() {
        return CraftPresence.CONFIG.generalSettings.detectMCUpdaterInstance;
    }

    @Override
    public boolean load() {
        MCUpdaterInstance instance;
        try {
            instance = FileUtils.getJsonData(new File("instance.json"), MCUpdaterInstance.class);
            if (instance != null && !StringUtils.isNullOrEmpty(instance.getPackName())) {
                setPackName(instance.getPackName());
            }
        } catch (Exception ex) {
            if (showException(ex)) {
                ex.printStackTrace();
            }
        }
        return hasPackName();
    }
}

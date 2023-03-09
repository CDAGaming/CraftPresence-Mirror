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

package com.gitlab.cdagaming.craftpresence.integrations.pack.multimc;

import com.gitlab.cdagaming.craftpresence.CraftPresence;
import com.gitlab.cdagaming.craftpresence.integrations.pack.Pack;
import com.gitlab.cdagaming.craftpresence.utils.StringUtils;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Set of Utilities used to Parse MultiMC Instance Information
 *
 * @author CDAGaming
 */
public class MultiMCUtils extends Pack {
    @Override
    public boolean isEnabled() {
        return CraftPresence.CONFIG.generalSettings.detectMultiMCManifest;
    }

    @Override
    public boolean load() {
        final String instanceFile = new File(CraftPresence.SYSTEM.USER_DIR).getParent() + File.separator + "instance.cfg";

        try (InputStream inputStream = Files.newInputStream(Paths.get(instanceFile))) {
            final Properties configFile = new Properties();
            configFile.load(inputStream);

            final String tempIconKey = configFile.getProperty("iconKey"), defaultIconName = "default", defaultIconKey = "infinity";
            setPackName(configFile.getProperty("name"));
            setPackIcon(!StringUtils.isNullOrEmpty(tempIconKey) && !tempIconKey.equals(defaultIconName) ? tempIconKey : defaultIconKey);
        } catch (Exception ex) {
            if (showException(ex)) {
                ex.printStackTrace();
            }
        }
        return hasPackName() && hasPackIcon();
    }
}

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
import com.gitlab.cdagaming.craftpresence.utils.SystemUtils;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Properties;

/**
 * Set of Utilities used to Parse MultiMC Instance Information
 *
 * @author CDAGaming
 */
public class MultiMCUtils extends Pack {
    public MultiMCUtils() {
        setPackType(getLauncherType());
    }

    @Override
    public boolean isEnabled() {
        return CraftPresence.CONFIG.generalSettings.detectMultiMCManifest;
    }

    @Override
    public boolean load() {
        return findWithSystem() || findWithLegacy();
    }

    @Override
    public void setPackIcon(final String packIcon) {
        super.setPackIcon(parseIcon(packIcon));
    }

    /**
     * Parses the Specified Icon Key, for MultiMC Integration
     *
     * @param original The original iconKey
     * @return The parsed iconKey
     */
    private String parseIcon(final String original) {
        final String defaultIcon = getDefaultIcon();
        if (StringUtils.isNullOrEmpty(original)) {
            return defaultIcon;
        } else {
            return !original.equals("default") ? original : defaultIcon;
        }
    }

    /**
     * Retrieve the default icon to use for this instance
     *
     * @return the default icon to use for this instance
     */
    private String getDefaultIcon() {
        if (Objects.equals(getPackType(), "prism")) {
            return "prismlauncher";
        }
        return "infinity";
    }

    /**
     * Attempt to retrieve instance data via system properties
     *
     * @return {@link Boolean#TRUE} if data was found
     */
    private boolean findWithSystem() {
        // 2023-03-10: Utilize the System Properties `multimc.instance.title` and `multimc.instance.icon` if available
        // Ref: https://github.com/MultiMC/Launcher/commit/c1ed09e74765e7e362c644685b49b77529b748af
        try {
            setPackName(System.getProperty("multimc.instance.title"));
            setPackIcon(System.getProperty("multimc.instance.icon"));
        } catch (Exception ex) {
            if (showException(ex)) {
                ex.printStackTrace();
            }
        }
        return hasPackName() && hasPackIcon();
    }

    /**
     * Attempt to retrieve instance data via config properties
     *
     * @return {@link Boolean#TRUE} if data was found
     */
    private boolean findWithLegacy() {
        // Utilize Legacy Property Route, if unable to use System Properties
        final String instanceFile = new File(SystemUtils.USER_DIR).getParent() + File.separator + "instance.cfg";
        try (InputStream inputStream = Files.newInputStream(Paths.get(instanceFile))) {
            final Properties configFile = new Properties();
            configFile.load(inputStream);

            setPackName(configFile.getProperty("name"));
            setPackIcon(configFile.getProperty("iconKey"));
        } catch (Exception ex) {
            if (showException(ex)) {
                ex.printStackTrace();
            }
        }
        return hasPackName() && hasPackIcon();
    }

    /**
     * Retrieve the launcher type that this instance belongs to
     *
     * @return the launcher type
     */
    private String getLauncherType() {
        final File prismLocation = new File(
                new File(SystemUtils.USER_DIR)
                        .getParentFile()
                        .getParentFile()
                        .getParentFile() + File.separator + "prismlauncher.cfg"
        );
        if (prismLocation.exists()) {
            return "prism";
        }
        return "multimc";
    }
}

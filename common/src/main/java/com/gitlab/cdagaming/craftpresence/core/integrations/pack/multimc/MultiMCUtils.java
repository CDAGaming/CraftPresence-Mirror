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

package com.gitlab.cdagaming.craftpresence.core.integrations.pack.multimc;

import com.gitlab.cdagaming.craftpresence.core.integrations.pack.Pack;
import io.github.cdagaming.unicore.utils.OSUtils;
import io.github.cdagaming.unicore.utils.StringUtils;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Supplier;

/**
 * Set of Utilities used to Parse MultiMC Instance Information
 *
 * @author CDAGaming
 */
public class MultiMCUtils extends Pack {
    public MultiMCUtils(final Supplier<Boolean> isEnabled) {
        super(isEnabled);
    }

    public MultiMCUtils(final boolean isEnabled) {
        super(isEnabled);
    }

    public MultiMCUtils() {
        super();
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
        // 2023-03-10: Utilize the Launcher's System Properties if available
        // Original Ref: https://github.com/MultiMC/Launcher/commit/c1ed09e74765e7e362c644685b49b77529b748af
        try {
            final Properties props = System.getProperties();
            if (props.containsKey("org.prismlauncher.instance.name")) {
                setPackData(
                        System.getProperty("org.prismlauncher.instance.name"),
                        System.getProperty("org.prismlauncher.instance.icon.id")
                );
                setPackType("prism");
            } else {
                setPackData(
                        System.getProperty("multimc.instance.title"),
                        System.getProperty("multimc.instance.icon")
                );
                setPackType("multimc");
            }
        } catch (Exception ex) {
            printException(ex);
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
        final Path instanceFile = Paths.get(new File(OSUtils.USER_DIR).getParent() + File.separator + "instance.cfg");
        if (Files.exists(instanceFile)) {
            try (InputStream inputStream = Files.newInputStream(instanceFile)) {
                final Properties configFile = new Properties();
                configFile.load(inputStream);

                setPackData(
                        configFile.getProperty("name"),
                        configFile.getProperty("iconKey")
                );
                setPackType(getLauncherType());
            } catch (Exception ex) {
                printException(ex);
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
        try {
            final File prismLocation = new File(
                    new File(OSUtils.USER_DIR)
                            .getParentFile()
                            .getParentFile()
                            .getParentFile() + File.separator + "prismlauncher.cfg"
            );
            if (prismLocation.exists()) {
                return "prism";
            }
        } catch (Exception ex) {
            printException(ex);
        }
        return "multimc";
    }
}

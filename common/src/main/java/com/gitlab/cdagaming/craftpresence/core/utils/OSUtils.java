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

package com.gitlab.cdagaming.craftpresence.core.utils;

import com.gitlab.cdagaming.craftpresence.core.Constants;

import java.io.IOException;
import java.util.List;
import java.util.Random;

public class OSUtils {
    /**
     * An instance of a random number generator, used for select parts of the mod
     */
    public static final Random RANDOM = new Random();
    /**
     * The Name of the User's Operating System
     */
    public static final String OS_NAME = System.getProperty("os.name");
    /**
     * The Architecture of the User's System
     */
    public static final String OS_ARCH = System.getProperty("os.arch");
    /**
     * The Directory the Application is running in
     */
    public static final String USER_DIR = System.getProperty("user.dir");
    /**
     * If the {@link OSUtils#OS_NAME} can be classified as LINUX
     */
    public static final boolean IS_LINUX = OS_NAME.startsWith("Linux") || OS_NAME.startsWith("LINUX");
    /**
     * If the {@link OSUtils#OS_NAME} can be classified as MAC
     */
    public static final boolean IS_MAC = OS_NAME.startsWith("Mac");
    /**
     * If the {@link OSUtils#OS_NAME} can be classified as WINDOWS
     */
    public static final boolean IS_WINDOWS = OS_NAME.startsWith("Windows");
    /**
     * If the {@link OSUtils#OS_ARCH} is 64-bit or x64
     */
    public static final boolean IS_64_BIT = OS_ARCH.contains("amd64") || OS_ARCH.contains("x86_64");

    /**
     * Attempt to browse to the specified command utilizing the OS-Specific APIs
     *
     * @param cmd The command to interpret
     * @return {@link Boolean#TRUE} upon success
     */
    public static boolean browseWithSystem(final String cmd) {
        if (IS_LINUX) {
            if (isXDG()) {
                if (runCommand("xdg-open", "%s", cmd)) {
                    return true;
                }
            }
            if (isKDE()) {
                if (runCommand("kde-open", "%s", cmd)) {
                    return true;
                }
            }
            if (isGNOME()) {
                if (runCommand("gnome-open", "%s", cmd)) {
                    return true;
                }
            }
            if (runCommand("kde-open", "%s", cmd)) {
                return true;
            }
            if (runCommand("gnome-open", "%s", cmd)) {
                return true;
            }
        }

        if (IS_MAC) {
            if (runCommand("open", "%s", cmd)) {
                return true;
            }
        }

        if (IS_WINDOWS) {
            if (runCommand("explorer", "%s", cmd)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Attempt to execute the specified command utilizing the OS-Specific APIs
     *
     * @param command The command to interpret
     * @param args    The arguments to interpret
     * @param file    The file or extra data to interpret
     * @return {@link Boolean#TRUE} upon success
     */
    private static boolean runCommand(final String command, final String args, final String file) {
        Constants.LOG.debugInfo("Trying to exec: [cmd=\"%s\", args=\"%s\", file=\"%s\"]", command, args, file);
        final String[] parts = prepareCommand(command, args, file);

        try {
            final Process p = Runtime.getRuntime().exec(parts);

            try {
                int retval = p.exitValue();
                if (retval == 0) {
                    Constants.LOG.debugError("Process ended immediately.");
                } else {
                    Constants.LOG.debugError("Process crashed.");
                }
                return false;
            } catch (IllegalThreadStateException itse) {
                Constants.LOG.debugError("Process is running.");
                return true;
            }
        } catch (IOException e) {
            Constants.LOG.debugError("Error running command.", e);
            return false;
        }
    }

    /**
     * Attempt to prepare the specified command for {@link OSUtils#runCommand(String, String, String)}
     *
     * @param command The command to interpret
     * @param args    The arguments to interpret
     * @param file    The file or extra data to interpret
     * @return {@link Boolean#TRUE} upon success
     */
    private static String[] prepareCommand(final String command, final String args, final String file) {
        final List<String> parts = StringUtils.newArrayList();
        parts.add(command);

        if (args != null) {
            for (String s : args.split(" ")) {
                s = String.format(s, file); // put in the filename thing
                parts.add(s.trim());
            }
        }

        return parts.toArray(new String[0]);
    }

    /**
     * Checks if the current session is running under the XDG session protocol.
     *
     * @return {@link Boolean#TRUE} if the session is running under XDG, false otherwise.
     */
    public static boolean isXDG() {
        final String xdgSessionId = System.getenv("XDG_SESSION_ID");
        return xdgSessionId != null && !xdgSessionId.isEmpty();
    }

    /**
     * Checks if the current desktop environment is GNOME.
     *
     * @return {@link Boolean#TRUE} if the desktop environment is GNOME, false otherwise.
     */
    public static boolean isGNOME() {
        final String gdmSession = System.getenv("GDMSESSION");
        return gdmSession != null && gdmSession.toLowerCase().contains("gnome");
    }

    /**
     * Checks if the current desktop environment is KDE.
     *
     * @return {@link Boolean#TRUE} if the desktop environment is KDE, false otherwise.
     */
    public static boolean isKDE() {
        final String gdmSession = System.getenv("GDMSESSION");
        return gdmSession != null && gdmSession.toLowerCase().contains("kde");
    }
}

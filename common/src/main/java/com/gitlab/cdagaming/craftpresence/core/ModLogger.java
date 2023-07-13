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

package com.gitlab.cdagaming.craftpresence.core;

import com.gitlab.cdagaming.craftpresence.core.utils.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Logging Manager for either Sending Info to Chat or in Logs
 *
 * @author CDAGaming
 */
public class ModLogger {
    /**
     * The Instance of the Root Logging Manager, for sending messages to logs
     */
    private final Logger logInstance;
    /**
     * Whether this Logger is operating in Debug Mode
     */
    private boolean debugMode;

    /**
     * Initializes a new Logger
     *
     * @param loggerName The name of the Logger
     * @param debug      Whether to initialize the logger in debug mode
     */
    public ModLogger(final String loggerName, final boolean debug) {
        this.logInstance = LogManager.getLogger(loggerName);
        this.debugMode = debug;
    }

    /**
     * Initializes a new Logger
     *
     * @param loggerName The name of the Logger
     */
    public ModLogger(final String loggerName) {
        this(loggerName, false);
    }

    /**
     * Get the instance of the root logging manager
     *
     * @return An instance of the root logging manager
     */
    public Logger getLogInstance() {
        return logInstance;
    }

    /**
     * Get whether this {@link ModLogger} is in Debug Mode
     *
     * @return the debug mode status
     */
    public boolean isDebugMode() {
        return debugMode;
    }

    /**
     * Set whether this {@link ModLogger} is in Debug Mode
     *
     * @param debugMode the new debug mode status
     */
    public void setDebugMode(final boolean debugMode) {
        this.debugMode = debugMode;
    }

    /**
     * Sends a Message with an ERROR Level to either Chat or Logs
     *
     * @param logMessage   The Log Message to Send
     * @param logArguments Additional Formatting Arguments
     */
    public void error(final String logMessage, Object... logArguments) {
        getLogInstance().error(
                parse(logMessage, logArguments)
        );
    }

    /**
     * Sends a Message with an WARNING Level to either Chat or Logs
     *
     * @param logMessage   The Log Message to Send
     * @param logArguments Additional Formatting Arguments
     */
    public void warn(final String logMessage, Object... logArguments) {
        getLogInstance().warn(
                parse(logMessage, logArguments)
        );
    }

    /**
     * Sends a Message with an INFO Level to either Chat or Logs
     *
     * @param logMessage   The Log Message to Send
     * @param logArguments Additional Formatting Arguments
     */
    public void info(final String logMessage, Object... logArguments) {
        getLogInstance().info(
                parse(logMessage, logArguments)
        );
    }

    /**
     * Sends a Message with an INFO Level to either Chat or Logs, if in Debug Mode
     *
     * @param logMessage   The Log Message to Send
     * @param logArguments Additional Formatting Arguments
     */
    public void debugInfo(final String logMessage, Object... logArguments) {
        if (isDebugMode()) {
            info("[DEBUG] " + logMessage, logArguments);
        }
    }

    /**
     * Sends a Message with an WARNING Level to either Chat or Logs, if in Debug Mode
     *
     * @param logMessage   The Log Message to Send
     * @param logArguments Additional Formatting Arguments
     */
    public void debugWarn(final String logMessage, Object... logArguments) {
        if (isDebugMode()) {
            warn("[DEBUG] " + logMessage, logArguments);
        }
    }

    /**
     * Sends a Message with an ERROR Level to either Chat or Logs, if in Debug Mode
     *
     * @param logMessage   The Log Message to Send
     * @param logArguments Additional Formatting Arguments
     */
    public void debugError(final String logMessage, Object... logArguments) {
        if (isDebugMode()) {
            error("[DEBUG] " + logMessage, logArguments);
        }
    }

    private String parse(final String message, Object... args) {
        return StringUtils.normalize(
                String.format(message, args)
        );
    }
}

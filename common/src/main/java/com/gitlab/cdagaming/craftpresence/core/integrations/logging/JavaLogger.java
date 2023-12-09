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

package com.gitlab.cdagaming.craftpresence.core.integrations.logging;

import com.gitlab.cdagaming.craftpresence.core.utils.FileUtils;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.logging.Logger;

/**
 * A {@link LoggingImpl} instance designed for Java's Utility Logger
 *
 * @author CDAGaming
 */
@SuppressFBWarnings("EI_EXPOSE_REP")
public class JavaLogger extends LoggingImpl {
    /**
     * The Instance of the Root Logging Manager, for sending messages to logs
     */
    private final Logger logInstance;

    public JavaLogger(final String loggerName, final boolean debug) {
        super(loggerName, debug);

        Logger logger = null;
        try {
            logger = (Logger) FileUtils.findValidClass(null, true, "net.minecraft.src.ModLoader", "ModLoader")
                    .getDeclaredMethod("getLogger").invoke(null);
            if (logger != null) {
                this.appendName = true;
            }
        } catch (Throwable ignored) {
        }
        this.logInstance = logger != null ? logger : Logger.getLogger(loggerName);
    }

    public JavaLogger(final String loggerName) {
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

    @Override
    public void error(final String logMessage, Object... logArguments) {
        getLogInstance().severe(
                parse(logMessage, logArguments)
        );
    }

    @Override
    public void warn(final String logMessage, Object... logArguments) {
        getLogInstance().warning(
                parse(logMessage, logArguments)
        );
    }

    @Override
    public void info(final String logMessage, Object... logArguments) {
        getLogInstance().info(
                parse(logMessage, logArguments)
        );
    }
}

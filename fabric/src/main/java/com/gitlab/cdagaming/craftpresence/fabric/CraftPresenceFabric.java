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

package com.gitlab.cdagaming.craftpresence.fabric;

import com.gitlab.cdagaming.craftpresence.CraftPresence;
import com.gitlab.cdagaming.craftpresence.utils.MappingUtils;
import net.fabricmc.api.ClientModInitializer;

/**
 * The Primary Application Class and Utilities
 *
 * @author CDAGaming
 */
public class CraftPresenceFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        if (MappingUtils.JAVA_SPEC < 1.8f) {
            throw new RuntimeException("Incompatible JVM!!! @MOD_NAME@ requires Java 8 or above to work properly!");
        }
        MappingUtils.setFilePath("/mappings-fabric.srg");
        new CraftPresence();
        setupIntegrations();
    }

    /**
     * Setup external integrations and attachments to the primary application
     */
    public void setupIntegrations() {
        // N/A
    }
}
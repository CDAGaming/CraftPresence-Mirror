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

package com.gitlab.cdagaming.unilib.impl;

import io.github.cdagaming.unicore.utils.StringUtils;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;

import java.util.Map;

/**
 * Utilities for Hooking {@link TranslationManager} instances to the Game Resource Manager
 *
 * @author CDAGaming
 */
public class TranslationListener implements IResourceManagerReloadListener {
    /**
     * The default instance for this module
     */
    public static final TranslationListener INSTANCE = new TranslationListener();
    /**
     * A mapping of currently loaded {@link TranslationManager} instances
     */
    private final Map<String, TranslationManager> translationManagerList = StringUtils.newHashMap();

    /**
     * Adds a module for ticking and RPC Synchronization
     *
     * @param moduleId The name of the module
     * @param instance The instance of the module
     */
    public void addModule(final String moduleId, final TranslationManager instance) {
        translationManagerList.put(moduleId, instance);
    }

    /**
     * The Event to Run on each Client Tick, if passed initialization events
     * <p>
     * Consists of Synchronizing and Updating Data as needed
     */
    public void onTick() {
        for (TranslationManager manager : translationManagerList.values()) {
            manager.onTick();
        }
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        for (TranslationManager manager : translationManagerList.values()) {
            manager.onReload();
        }
    }
}

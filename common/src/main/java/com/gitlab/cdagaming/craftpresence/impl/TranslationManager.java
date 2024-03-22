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

package com.gitlab.cdagaming.craftpresence.impl;

import com.gitlab.cdagaming.craftpresence.CraftPresence;
import io.github.cdagaming.unicore.utils.StringUtils;
import io.github.cdagaming.unicore.utils.TranslationUtils;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.util.ResourceLocation;

import java.io.InputStream;
import java.util.List;

/**
 * Utilities for Hooking a {@link TranslationUtils} instance to the Game Resource Manager
 *
 * @author CDAGaming
 */
public class TranslationManager implements IResourceManagerReloadListener {
    /**
     * The currently linked {@link TranslationUtils} instance
     */
    private final TranslationUtils instance;

    /**
     * Initializes a new manager for the {@link TranslationUtils} instance
     *
     * @param instance the {@link TranslationUtils} instance to attach to
     */
    public TranslationManager(final TranslationUtils instance) {
        this.instance = instance;
        ((SimpleReloadableResourceManager) CraftPresence.instance.getResourceManager()).registerReloadListener(this);

        getInstance().setLanguageSupplier((fallback) -> {
            final String result;
            if (CraftPresence.instance.gameSettings != null) {
                result = CraftPresence.instance.gameSettings.language;
            } else if (CraftPresence.CONFIG != null) {
                result = CraftPresence.CONFIG.accessibilitySettings.languageId;
            } else {
                result = fallback;
            }
            return result;
        });

        getInstance().setResourceSupplier((modId, assetsPath, langPath) -> {
            final List<InputStream> results = StringUtils.newArrayList();
            try {
                final List<IResource> resources = CraftPresence.instance.getResourceManager().getAllResources(new ResourceLocation(modId, langPath));
                for (IResource resource : resources) {
                    results.add(resource.getInputStream());
                }
            } catch (Exception ignored) {
            }
            return results;
        });
    }

    /**
     * Retrieve the current {@link TranslationUtils} instance we're attached to
     *
     * @return the currently attached {@link TranslationUtils} instance
     */
    public TranslationUtils getInstance() {
        return instance;
    }

    /**
     * The Event to Run on each Client Tick, if passed initialization events
     * <p>
     * Consists of Synchronizing Data, and Updating Translation Data as needed
     */
    public void onTick() {
        getInstance().onTick();
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        getInstance().syncTranslations();
    }
}

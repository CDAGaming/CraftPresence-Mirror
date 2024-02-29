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
import net.minecraft.src.StringTranslate;

import java.util.Properties;

/**
 * Utilities for Hooking a {@link TranslationUtils} instance to the Game Resource Manager
 *
 * @author CDAGaming
 */
public class TranslationManager {
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

        getInstance().setOnLanguageSync((entries) -> {
            StringTranslate stInstance = StringTranslate.getInstance();
            Properties data = (Properties) StringUtils.getField(
                    StringTranslate.class, stInstance,
                    "translateTable", "field_20164_b", "b"
            );
            data.putAll(entries);
            StringUtils.updateField(
                    StringTranslate.class, stInstance,
                    data,
                    "translateTable", "field_20164_b", "b"
            );
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
}

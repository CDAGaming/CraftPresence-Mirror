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

package com.gitlab.cdagaming.craftpresence.config.category;

import com.gitlab.cdagaming.craftpresence.ModUtils;
import com.gitlab.cdagaming.craftpresence.config.Module;
import com.gitlab.cdagaming.craftpresence.config.element.ColorData;
import com.gitlab.cdagaming.craftpresence.config.element.ColorSection;
import com.gitlab.cdagaming.craftpresence.utils.StringUtils;
import com.gitlab.cdagaming.craftpresence.utils.gui.RenderUtils;

import java.io.Serializable;

public class Accessibility extends Module implements Serializable {
    private static final long serialVersionUID = -6804925684173174749L;
    private static final Accessibility DEFAULT = new Accessibility();
    public ColorData tooltipBackgroundColor = new ColorData(
            new ColorSection(16, 0, 16, 240)
    );
    public ColorData tooltipBorderColor = new ColorData(
            new ColorSection(80, 0, 255, 80),
            new ColorSection(40, 0, 127, 80)
    );
    public ColorData guiBackgroundColor = new ColorData(
            new ColorSection(64, 64, 64, 255),
            RenderUtils.DEFAULT_GUI_BACKGROUND
    );
    public ColorData worldGuiBackgroundColor = new ColorData(
            new ColorSection(16, 16, 16, 192),
            new ColorSection(16, 16, 16, 208)
    );
    public ColorData buttonBackgroundColor = new ColorData(RenderUtils.DEFAULT_BUTTON_BACKGROUND);
    public String languageId = ModUtils.TRANSLATOR.defaultLanguageId;
    public boolean stripTranslationColors = ModUtils.IS_TEXT_COLORS_BLOCKED;
    public boolean showLoggingInChat = false;
    public boolean stripExtraGuiElements = false;
    public int configKeyCode = ModUtils.MCProtocolID > 340 ? 96 : 41;

    public Accessibility(final Accessibility other) {
        transferFrom(other);
    }

    public Accessibility() {
        // N/A
    }

    @Override
    public Accessibility getDefaults() {
        return new Accessibility(DEFAULT);
    }

    @Override
    public Accessibility copy() {
        return new Accessibility(this);
    }

    @Override
    public void transferFrom(Module target) {
        if (target instanceof Accessibility && !equals(target)) {
            final Accessibility data = (Accessibility) target;

            tooltipBackgroundColor = new ColorData(data.tooltipBackgroundColor);
            tooltipBorderColor = new ColorData(data.tooltipBorderColor);
            guiBackgroundColor = new ColorData(data.guiBackgroundColor);
            worldGuiBackgroundColor = new ColorData(data.worldGuiBackgroundColor);
            buttonBackgroundColor = new ColorData(data.buttonBackgroundColor);
            languageId = data.languageId;
            stripTranslationColors = data.stripTranslationColors;
            showLoggingInChat = data.showLoggingInChat;
            stripExtraGuiElements = data.stripExtraGuiElements;
            configKeyCode = data.configKeyCode;
        }
    }

    @Override
    public Object getProperty(final String name) {
        return StringUtils.getField(Accessibility.class, this, name);
    }

    @Override
    public void setProperty(final String name, final Object value) {
        StringUtils.updateField(Accessibility.class, this, value, name);
    }
}

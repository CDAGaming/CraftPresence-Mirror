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
import com.gitlab.cdagaming.craftpresence.impl.Pair;
import com.gitlab.cdagaming.craftpresence.utils.StringUtils;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.io.Serializable;

@SuppressFBWarnings("UCF_USELESS_CONTROL_FLOW_NEXT_LINE")
public class Accessibility extends Module implements Serializable {
    private static final long serialVersionUID = -6804925684173174749L;
    private static Accessibility DEFAULT;
    public String tooltipBackgroundColor = "0xF0100010";
    public String tooltipBorderColor = "0x505000FF";
    public String guiBackgroundColor = "minecraft:" + (ModUtils.MCProtocolID <= 61 && ModUtils.IS_LEGACY_SOFT ? (ModUtils.IS_LEGACY_ALPHA ? "/dirt.png" : "/gui/background.png") : "textures/gui/options_background.png");
    public String buttonBackgroundColor = "minecraft:" + (ModUtils.MCProtocolID <= 61 && ModUtils.IS_LEGACY_SOFT ? "/gui/gui.png" : "textures/gui/widgets.png");
    public String languageId = ModUtils.TRANSLATOR.defaultLanguageId;
    public boolean showBackgroundAsDark = true;
    public boolean stripTranslationColors = false;
    public boolean showLoggingInChat = false;
    public boolean stripExtraGuiElements = ModUtils.IS_LEGACY_HARD;
    public int configKeyCode = ModUtils.MCProtocolID > 340 ? 96 : 41;

    @Override
    public Accessibility getDefaults() {
        if (DEFAULT == null) {
            DEFAULT = new Accessibility();
        }
        return copy(DEFAULT);
    }

    @Override
    public Object getProperty(final String name) {
        return StringUtils.getField(Accessibility.class, this, name);
    }

    @Override
    public void setProperty(final String name, final Object value) {
        StringUtils.updateField(Accessibility.class, this, new Pair<>(name, value));
    }
}

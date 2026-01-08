/*
 * MIT License
 *
 * Copyright (c) 2018 - 2026 CDAGaming (cstack2011@yahoo.com)
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

package com.gitlab.cdagaming.craftpresence.core.config.category;

import com.gitlab.cdagaming.craftpresence.core.config.Module;
import com.gitlab.cdagaming.unilib.core.CoreUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class Accessibility extends Module implements Serializable {
    @Serial
    private static final long serialVersionUID = -6804925684173174749L;
    private static final Accessibility DEFAULT = new Accessibility();
    public String languageId = CoreUtils.getDefaultLanguage();
    public boolean stripTranslationColors = false;
    public boolean stripTranslationFormatting = CoreUtils.isTextFormattingBlocked();
    public boolean stripExtraGuiElements = false;
    public int configKeyCode = CoreUtils.MCBuildProtocol > 340 ? 96 : 41;

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
        if (target instanceof Accessibility data && !equals(target)) {
            languageId = data.languageId;
            stripTranslationColors = data.stripTranslationColors;
            stripTranslationFormatting = data.stripTranslationFormatting;
            stripExtraGuiElements = data.stripExtraGuiElements;
            configKeyCode = data.configKeyCode;
        }
    }

    @Override
    public Object getProperty(String name) {
        return switch (name) {
            case "languageId" -> languageId;
            case "stripTranslationColors" -> stripTranslationColors;
            case "stripTranslationFormatting" -> stripTranslationFormatting;
            case "stripExtraGuiElements" -> stripExtraGuiElements;
            case "configKeyCode" -> configKeyCode;
            default -> null;
        };
    }

    @Override
    public void setProperty(String name, Object value) {
        try {
            switch (name) {
                case "languageId":
                    languageId = (String) value;
                    break;
                case "stripTranslationColors":
                    stripTranslationColors = (Boolean) value;
                    break;
                case "stripTranslationFormatting":
                    stripTranslationFormatting = (Boolean) value;
                    break;
                case "stripExtraGuiElements":
                    stripExtraGuiElements = (Boolean) value;
                    break;
                case "configKeyCode":
                    configKeyCode = (Integer) value;
                    break;
                default:
                    break;
            }
        } catch (Throwable ex) {
            printException(ex);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof Accessibility other)) {
            return false;
        }

        return Objects.equals(other.languageId, languageId) &&
                Objects.equals(other.stripTranslationColors, stripTranslationColors) &&
                Objects.equals(other.stripTranslationFormatting, stripTranslationFormatting) &&
                Objects.equals(other.stripExtraGuiElements, stripExtraGuiElements) &&
                Objects.equals(other.configKeyCode, configKeyCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                languageId,
                stripTranslationColors, stripTranslationFormatting,
                stripExtraGuiElements,
                configKeyCode
        );
    }
}

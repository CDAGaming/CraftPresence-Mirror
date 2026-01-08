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

package com.gitlab.cdagaming.craftpresence.core.config.element;

import com.gitlab.cdagaming.craftpresence.core.config.Module;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class ModuleData extends Module implements Serializable {
    @Serial
    private static final long serialVersionUID = -5846802181463006850L;
    private static final ModuleData DEFAULT = new ModuleData();

    private String textOverride;
    private String iconOverride;
    private PresenceData data;

    public ModuleData(final String textOverride, final String iconOverride, final PresenceData data) {
        setTextOverride(textOverride);
        setIconOverride(iconOverride);
        setData(data);
    }

    public ModuleData(final ModuleData other) {
        this(other.textOverride, other.iconOverride, other.data);
    }

    public ModuleData(final String textOverride, final String iconOverride) {
        this(textOverride, iconOverride, null);
    }

    public ModuleData() {
        this(null, null);
    }

    @Override
    public ModuleData getDefaults() {
        return new ModuleData(DEFAULT);
    }

    @Override
    public ModuleData copy() {
        return new ModuleData(this);
    }

    @Override
    public void transferFrom(Module target) {
        if (target instanceof ModuleData module && !equals(target)) {
            setTextOverride(module.getTextOverride());
            setIconOverride(module.getIconOverride());
            setData(module.getData());
        }
    }

    public String getTextOverride() {
        return textOverride;
    }

    public ModuleData setTextOverride(final String textOverride) {
        this.textOverride = textOverride;
        return this;
    }

    public String getIconOverride() {
        return iconOverride;
    }

    public ModuleData setIconOverride(final String iconOverride) {
        this.iconOverride = iconOverride;
        return this;
    }

    public PresenceData getData() {
        return data != null ? new PresenceData(data) : null;
    }

    public ModuleData setData(final PresenceData data) {
        if (data != null) {
            this.data = new PresenceData(data);
        }
        return this;
    }

    @Override
    public Object getProperty(String name) {
        return switch (name) {
            case "textOverride" -> textOverride;
            case "iconOverride" -> iconOverride;
            case "data" -> data;
            default -> null;
        };
    }

    @Override
    public void setProperty(String name, Object value) {
        try {
            switch (name) {
                case "textOverride":
                    textOverride = (String) value;
                    break;
                case "iconOverride":
                    iconOverride = (String) value;
                    break;
                case "data":
                    data = (PresenceData) value;
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

        if (!(obj instanceof ModuleData other)) {
            return false;
        }

        return Objects.equals(other.textOverride, textOverride) &&
                Objects.equals(other.iconOverride, iconOverride) &&
                Objects.equals(other.data, data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                textOverride, iconOverride, data
        );
    }
}

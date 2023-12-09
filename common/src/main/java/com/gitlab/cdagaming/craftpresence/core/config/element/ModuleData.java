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

package com.gitlab.cdagaming.craftpresence.core.config.element;

import com.gitlab.cdagaming.craftpresence.core.config.Module;
import com.gitlab.cdagaming.craftpresence.core.utils.StringUtils;

import java.io.Serializable;

public class ModuleData extends Module implements Serializable {
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
        if (target instanceof ModuleData && !equals(target)) {
            final ModuleData data = (ModuleData) target;

            setTextOverride(data.getTextOverride());
            setIconOverride(data.getIconOverride());
            setData(data.getData());
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
    public Object getProperty(final String name) {
        return StringUtils.getField(ModuleData.class, this, name);
    }

    @Override
    public void setProperty(final String name, final Object value) {
        StringUtils.updateField(ModuleData.class, this, value, name);
    }
}

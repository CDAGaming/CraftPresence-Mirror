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
import com.gitlab.cdagaming.craftpresence.config.element.ModuleData;
import com.gitlab.cdagaming.craftpresence.impl.HashMapBuilder;
import com.gitlab.cdagaming.craftpresence.utils.StringUtils;

import java.io.Serializable;
import java.util.Map;

public class Dimension extends Module implements Serializable {
    private static final long serialVersionUID = 2779211521643527744L;
    private static final Dimension DEFAULT = new Dimension();
    public String fallbackDimensionIcon = "unknown";
    public Map<String, ModuleData> dimensionData = new HashMapBuilder<String, ModuleData>()
            .put("default", new ModuleData(
                    ModUtils.TRANSLATOR.translate(true, "craftpresence.defaults.dimension_messages.dimension_messages"),
                    null // Defaults to the Dimension Name if nothing is supplied
            ))
            .build();

    public Dimension(final Dimension other) {
        transferFrom(other);
    }

    public Dimension() {
        // N/A
    }

    @Override
    public Dimension getDefaults() {
        return new Dimension(DEFAULT);
    }

    @Override
    public Dimension copy() {
        return new Dimension(this);
    }

    @Override
    public void transferFrom(Module target) {
        if (target instanceof Dimension && !equals(target)) {
            final Dimension data = (Dimension) target;

            fallbackDimensionIcon = data.fallbackDimensionIcon;
            dimensionData = data.dimensionData;
        }
    }

    @Override
    public Object getProperty(final String name) {
        return StringUtils.getField(Dimension.class, this, name);
    }

    @Override
    public void setProperty(final String name, final Object value) {
        StringUtils.updateField(Dimension.class, this, value, name);
    }
}

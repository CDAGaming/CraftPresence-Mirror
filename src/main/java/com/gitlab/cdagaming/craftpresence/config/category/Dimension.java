/*
 * MIT License
 *
 * Copyright (c) 2018 - 2022 CDAGaming (cstack2011@yahoo.com)
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
import com.gitlab.cdagaming.craftpresence.impl.Tuple;
import com.gitlab.cdagaming.craftpresence.utils.StringUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Dimension extends Module implements Serializable {
    private static final long serialVersionUID = 2779211521643527744L;
    private static Dimension DEFAULT;
    public String fallbackDimensionIcon = "unknown";
    public Map<String, ModuleData> dimensionData = new HashMap<String, ModuleData>() {
        private static final long serialVersionUID = 6836644802686258275L;

        {
            put("default", new ModuleData(
                    ModUtils.TRANSLATOR.translate(true, "craftpresence.defaults.dimension_messages.dimension_messages"),
                    null // Defaults to the Dimension Name if nothing is supplied
            ));
        }
    };

    @Override
    public Dimension getDefaults() {
        if (DEFAULT == null) {
            DEFAULT = new Dimension();
        }
        return DEFAULT;
    }

    @Override
    public Object getProperty(final String name) {
        return StringUtils.lookupObject(Dimension.class, this, name);
    }

    @Override
    public void setProperty(final String name, final Object value) {
        StringUtils.updateField(Dimension.class, this, new Tuple<>(name, value, null));
    }
}

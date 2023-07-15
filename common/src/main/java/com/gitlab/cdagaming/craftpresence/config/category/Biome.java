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

import com.gitlab.cdagaming.craftpresence.core.Constants;
import com.gitlab.cdagaming.craftpresence.core.config.Module;
import com.gitlab.cdagaming.craftpresence.core.config.element.ModuleData;
import com.gitlab.cdagaming.craftpresence.core.impl.HashMapBuilder;
import com.gitlab.cdagaming.craftpresence.core.utils.StringUtils;

import java.io.Serializable;
import java.util.Map;

public class Biome extends Module implements Serializable {
    private static final long serialVersionUID = 7528869687150995557L;
    private static final Biome DEFAULT = new Biome();
    public String fallbackBiomeIcon = "unknown";
    public Map<String, ModuleData> biomeData = new HashMapBuilder<String, ModuleData>()
            .put("default", new ModuleData(
                    Constants.TRANSLATOR.translate("craftpresence.defaults.biome_messages.biome_messages"),
                    null // Defaults to the Biome Name if nothing is supplied
            ))
            .build();

    public Biome(final Biome other) {
        transferFrom(other);
    }

    public Biome() {
        // N/A
    }

    @Override
    public Biome getDefaults() {
        return new Biome(DEFAULT);
    }

    @Override
    public Biome copy() {
        return new Biome(this);
    }

    @Override
    public void transferFrom(Module target) {
        if (target instanceof Biome && !equals(target)) {
            final Biome data = (Biome) target;

            fallbackBiomeIcon = data.fallbackBiomeIcon;
            biomeData = data.biomeData;
        }
    }

    @Override
    public Object getProperty(final String name) {
        return StringUtils.getField(Biome.class, this, name);
    }

    @Override
    public void setProperty(final String name, final Object value) {
        StringUtils.updateField(Biome.class, this, value, name);
    }
}

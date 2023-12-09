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

package com.gitlab.cdagaming.craftpresence.config.category;

import com.gitlab.cdagaming.craftpresence.core.Constants;
import com.gitlab.cdagaming.craftpresence.core.config.Module;
import com.gitlab.cdagaming.craftpresence.core.config.element.ModuleData;
import com.gitlab.cdagaming.craftpresence.core.impl.HashMapBuilder;
import com.gitlab.cdagaming.craftpresence.core.utils.StringUtils;

import java.io.Serializable;
import java.util.Map;

public class Entity extends Module implements Serializable {
    private static final long serialVersionUID = -4294690176016925084L;
    private static final Entity DEFAULT = new Entity();
    public String fallbackEntityIcon = "unknown";
    public Map<String, ModuleData> targetData = new HashMapBuilder<String, ModuleData>()
            .put("default", new ModuleData(
                    Constants.TRANSLATOR.translate("craftpresence.defaults.advanced.entity_target_messages"),
                    null // Defaults to the Entity Name if nothing is supplied
            ))
            .build();
    public Map<String, ModuleData> ridingData = new HashMapBuilder<String, ModuleData>()
            .put("default", new ModuleData(
                    Constants.TRANSLATOR.translate("craftpresence.defaults.advanced.entity_riding_messages"),
                    null // Defaults to the Entity Name if nothing is supplied
            ))
            .build();

    public Entity(final Entity other) {
        transferFrom(other);
    }

    public Entity() {
        // N/A
    }

    @Override
    public Entity getDefaults() {
        return new Entity(DEFAULT);
    }

    @Override
    public Entity copy() {
        return new Entity(this);
    }

    @Override
    public void transferFrom(Module target) {
        if (target instanceof Entity && !equals(target)) {
            final Entity data = (Entity) target;

            fallbackEntityIcon = data.fallbackEntityIcon;
            targetData = data.targetData;
            ridingData = data.ridingData;
        }
    }

    @Override
    public Object getProperty(final String name) {
        return StringUtils.getField(Entity.class, this, name);
    }

    @Override
    public void setProperty(final String name, final Object value) {
        StringUtils.updateField(Entity.class, this, value, name);
    }
}

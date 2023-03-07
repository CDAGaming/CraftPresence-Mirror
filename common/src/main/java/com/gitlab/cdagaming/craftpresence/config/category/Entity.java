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
import com.gitlab.cdagaming.craftpresence.utils.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Entity extends Module implements Serializable {
    private static final long serialVersionUID = -4294690176016925084L;
    private static Entity DEFAULT;
    public String fallbackEntityIcon = "unknown";
    public Map<String, ModuleData> targetData = new HashMap<String, ModuleData>() {
        private static final long serialVersionUID = 9059756640153589401L;

        {
            put("default", new ModuleData(
                    ModUtils.TRANSLATOR.translate(true, "craftpresence.defaults.advanced.entity_target_messages"),
                    null // Defaults to the Entity Name if nothing is supplied
            ));
        }
    };
    public Map<String, ModuleData> ridingData = new HashMap<String, ModuleData>() {
        private static final long serialVersionUID = 9059756640153589401L;

        {
            put("default", new ModuleData(
                    ModUtils.TRANSLATOR.translate(true, "craftpresence.defaults.advanced.entity_riding_messages"),
                    null // Defaults to the Entity Name if nothing is supplied
            ));
        }
    };

    @Override
    public Entity getDefaults() {
        if (DEFAULT == null) {
            DEFAULT = new Entity();
        }
        return copy(DEFAULT, Entity.class);
    }

    @Override
    public Object getProperty(final String name) {
        return StringUtils.getField(Entity.class, this, name);
    }

    @Override
    public void setProperty(final String name, final Object value) {
        StringUtils.updateField(Entity.class, this, Pair.of(name, value));
    }
}

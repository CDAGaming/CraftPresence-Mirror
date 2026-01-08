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

import com.gitlab.cdagaming.craftpresence.core.Constants;
import com.gitlab.cdagaming.craftpresence.core.config.Module;
import com.gitlab.cdagaming.craftpresence.core.config.element.ModuleData;
import io.github.cdagaming.unicore.impl.HashMapBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

public class Entity extends Module implements Serializable {
    @Serial
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
        if (target instanceof Entity data && !equals(target)) {
            fallbackEntityIcon = data.fallbackEntityIcon;
            targetData.clear();
            for (Map.Entry<String, ModuleData> entry : data.targetData.entrySet()) {
                targetData.put(entry.getKey(), new ModuleData(entry.getValue()));
            }
            ridingData.clear();
            for (Map.Entry<String, ModuleData> entry : data.ridingData.entrySet()) {
                ridingData.put(entry.getKey(), new ModuleData(entry.getValue()));
            }
        }
    }

    @Override
    public Object getProperty(String name) {
        return switch (name) {
            case "fallbackEntityIcon" -> fallbackEntityIcon;
            case "targetData" -> targetData;
            case "ridingData" -> ridingData;
            default -> null;
        };
    }

    @Override
    public void setProperty(String name, Object value) {
        try {
            switch (name) {
                case "fallbackEntityIcon":
                    fallbackEntityIcon = (String) value;
                    break;
                case "targetData":
                    targetData = (Map<String, ModuleData>) value;
                    break;
                case "ridingData":
                    ridingData = (Map<String, ModuleData>) value;
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

        if (!(obj instanceof Entity other)) {
            return false;
        }

        return Objects.equals(other.fallbackEntityIcon, fallbackEntityIcon) &&
                Objects.equals(other.targetData, targetData) &&
                Objects.equals(other.ridingData, ridingData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fallbackEntityIcon, targetData, ridingData);
    }
}

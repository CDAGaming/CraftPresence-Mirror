/*
 * MIT License
 *
 * Copyright (c) 2018 - 2025 CDAGaming (cstack2011@yahoo.com)
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

public class Dimension extends Module implements Serializable {
    @Serial
    private static final long serialVersionUID = 2779211521643527744L;
    private static final Dimension DEFAULT = new Dimension();
    public String fallbackDimensionIcon = "unknown";
    public Map<String, ModuleData> dimensionData = new HashMapBuilder<String, ModuleData>()
            .put("default", new ModuleData(
                    Constants.TRANSLATOR.translate("craftpresence.defaults.dimension_messages.dimension_messages"),
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
        if (target instanceof Dimension data && !equals(target)) {
            fallbackDimensionIcon = data.fallbackDimensionIcon;
            dimensionData.clear();
            for (Map.Entry<String, ModuleData> entry : data.dimensionData.entrySet()) {
                dimensionData.put(entry.getKey(), new ModuleData(entry.getValue()));
            }
        }
    }

    @Override
    public Object getProperty(String name) {
        return switch (name) {
            case "fallbackDimensionIcon" -> fallbackDimensionIcon;
            case "dimensionData" -> dimensionData;
            default -> null;
        };
    }

    @Override
    public void setProperty(String name, Object value) {
        try {
            switch (name) {
                case "fallbackDimensionIcon":
                    fallbackDimensionIcon = (String) value;
                    break;
                case "dimensionData":
                    dimensionData = (Map<String, ModuleData>) value;
                    break;
                case "default":
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

        if (!(obj instanceof Dimension other)) {
            return false;
        }

        return Objects.equals(other.fallbackDimensionIcon, fallbackDimensionIcon) &&
                Objects.equals(other.dimensionData, dimensionData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fallbackDimensionIcon, dimensionData);
    }
}

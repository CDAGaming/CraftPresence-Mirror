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

package com.gitlab.cdagaming.craftpresence.utils;

import io.github.cdagaming.unicore.utils.StringUtils;
import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.Optional;

/**
 * Utilities for interpreting DataComponent and related Data Types
 *
 * @author CDAGaming
 */
public class ComponentUtils {
    public static DataComponentMap getComponentMap(final DataComponentHolder stack) {
        return stack != null ? stack.getComponents() : DataComponentMap.EMPTY;
    }

    public static DataComponentMap getComponentMap(final Object data) {
        if (data instanceof DataComponentHolder component) {
            return getComponentMap(component);
        }
        return DataComponentMap.EMPTY;
    }

    public static TypedDataComponent<?> getComponent(final Object data, final String path) {
        if (data instanceof DataComponentHolder component) {
            return getComponent(component, path);
        }
        return null;
    }

    public static TypedDataComponent<?> getComponent(final DataComponentHolder stack, final String path) {
        return getComponent(
                getComponentMap(stack), path
        );
    }

    public static TypedDataComponent<?> getComponent(final DataComponentMap root, final String path) {
        if (StringUtils.isNullOrEmpty(path)) {
            return null;
        } else {
            final Optional<DataComponentType<?>> type = BuiltInRegistries.DATA_COMPONENT_TYPE.getOptional(
                    ResourceUtils.parseResource(path)
            );
            return type.map(root::getTyped).orElse(null);
        }
    }

    public static Object parseComponent(final TypedDataComponent<?> component) {
        if (component == null) {
            return null;
        }
        return component.value();
    }
}

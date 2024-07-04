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

package com.gitlab.cdagaming.unilib.fabric;

import com.gitlab.cdagaming.unilib.UniLib;
import io.github.cdagaming.unicore.utils.MappingUtils;
import io.github.cdagaming.unicore.utils.OSUtils;
import com.gitlab.cdagaming.unilib.core.CoreUtils;
import net.fabricmc.loader.api.FabricLoader;

/**
 * The Primary Application Class and Utilities
 *
 * @author CDAGaming
 */
public class UniLibFabric {
    public UniLibFabric() {
        if (OSUtils.JAVA_SPEC < 1.8f) {
            throw new UnsupportedOperationException("Incompatible JVM!!! UniLib requires Java 8 or above to work properly!");
        }
        MappingUtils.setFilePath("/mappings-fabric.srg");
        CoreUtils.MOD_COUNT_SUPPLIER = () -> FabricLoader.getInstance().getAllMods().size();

        UniLib.assertLoaded();
    }
}
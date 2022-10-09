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

package com.gitlab.cdagaming.craftpresence.utils;

import com.gitlab.cdagaming.craftpresence.ModUtils;
import com.google.common.base.Stopwatch;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MappingUtils {
    private static Map<String, String> classMap = null;

    public static Map<String, String> getClassMap() {
        if (classMap == null) {
            Map<String, String> cm = new HashMap<String, String>();
            // load from /mappings.srg
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(FileUtils.getResourceAsStream(MappingUtils.class, "/mappings.srg")));
                try {
                    ModUtils.LOG.info("Loading Mappings...");
                    Stopwatch stopwatch = new Stopwatch().start();
                    for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                        String[] parts = line.split(" ");
                        if (parts[0].equals("CL:")) {
                            cm.put(parts[1], parts[2]);
                        }
                    }
                    ModUtils.LOG.info("Loaded Mappings in " + stopwatch.stop());
                } finally {
                    reader.close();
                }
            } catch (Throwable e) {
                if (ModUtils.MCProtocolID <= 340) {
                    e.printStackTrace();
                }
            }
            classMap = cm;
        }
        return classMap;
    }

    public static Set<String> getUnmappedClassesMatching(String start) {
        final Set<String> matches = new HashSet<>();
        start = start.replace(".", "/");

        for (Map.Entry<String, String> entry : getClassMap().entrySet()) {
            if (entry.getValue().startsWith(start)) {
                matches.add(entry.getKey().replace("/", "."));
            }
        }

        return matches;
    }

    public static String getClassName(Class<?> object) {
        String result = getClassMap().get(
                object.getCanonicalName().replace(".", "/")
        );
        if (result == null) {
            result = object.getSimpleName();
        } else {
            result = result.substring(result.lastIndexOf("/") + 1);
        }
        return result;
    }

    public static String getClassName(Object object) {
        return getClassName(object.getClass());
    }
}

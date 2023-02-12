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

package com.gitlab.cdagaming.craftpresence.utils;

import com.gitlab.cdagaming.craftpresence.ModUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiPredicate;

/**
 * Mapping Utilities used to convert between different Mojang Mapping Types
 *
 * @author CDAGaming, wagyourtail
 */
public class MappingUtils {
    /**
     * The Current Thread's Class Loader, used to dynamically receive data as needed
     */
    public static final ClassLoader CLASS_LOADER = Thread.currentThread().getContextClassLoader();
    /**
     * The Java Specification Version
     */
    public static final float JAVA_SPEC = Float.parseFloat(System.getProperty("java.specification.version"));
    private static Map<String, String> classMap = null;
    private static String filePath = "/mappings.srg";

    /**
     * Set the specified file path to retrieve data from
     *
     * @param filePath The new path to pull data from
     */
    public static void setFilePath(String filePath) {
        MappingUtils.filePath = filePath;
    }

    /**
     * Retrieve a mapping for class names from the Searge Data
     *
     * @return the resulting mappings
     */
    public static Map<String, String> getClassMap() {
        if (classMap == null) {
            Map<String, String> cm = new HashMap<>();
            // load from /mappings.srg
            try {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(FileUtils.getResourceAsStream(MappingUtils.class, filePath)))) {
                    ModUtils.LOG.info("Loading Mappings...");
                    long time = System.nanoTime();
                    for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                        String[] parts = line.split(" ");
                        if (parts[0].equals("CL:")) {
                            cm.put(parts[1], parts[2]);
                        }
                    }
                    ModUtils.LOG.info("Loaded Mappings in " + (System.nanoTime() - time) / 1000000 + "ms");
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
            classMap = cm;
        }
        return classMap;
    }

    /**
     * Retrieve the mapped class path for the specified argument, if present
     *
     * @param input The string to interpret
     * @return the resulting mapped class path
     */
    public static String getMappedPath(String input) {
        if (classMap.containsKey(input)) {
            return classMap.get(input).replace("/", ".");
        }
        return input;
    }

    /**
     * Retrieve a list of unmapped class names matching the specified argument
     *
     * @param start          The string to interpret
     * @param matchCondition The condition that, when satisfied, will add to the resulting list
     * @return the resulting list of unmapped class names
     */
    public static Set<String> getUnmappedClassesMatching(String start, BiPredicate<String, String> matchCondition) {
        final Set<String> matches = new HashSet<>();
        start = start.replace(".", "/");

        for (Map.Entry<String, String> entry : classMap.entrySet()) {
            if (matchCondition.test(entry.getValue(), start)) {
                matches.add(entry.getKey().replace("/", "."));
            }
        }

        return matches;
    }

    /**
     * Retrieve a list of unmapped class names matching the specified argument
     *
     * @param start The string to interpret
     * @param exact Whether to only return exact matches (using startsWith by default)
     * @return the resulting list of unmapped class names
     */
    public static Set<String> getUnmappedClassesMatching(String start, boolean exact) {
        return getUnmappedClassesMatching(start, exact ? String::equals : String::startsWith);
    }

    /**
     * Retrieve a list of unmapped class names matching the specified argument
     *
     * @param start The string to interpret
     * @return the resulting list of unmapped class names
     */
    public static Set<String> getUnmappedClassesMatching(String start) {
        return getUnmappedClassesMatching(start, false);
    }

    /**
     * Retrieve the mapped class name matching the requested object
     *
     * @param object     The class object to interpret
     * @param simpleName Whether to return the simple name of the found class
     * @return the mapped class name
     */
    public static String getClassName(Class<?> object, boolean simpleName) {
        String result = classMap.get(
                object.getName().replace(".", "/")
        );
        if (result == null) {
            result = simpleName ? object.getSimpleName() : object.getName();
        } else {
            result = simpleName ? result.substring(result.lastIndexOf("/") + 1) : result.replace("/", ".");
        }
        return result;
    }

    /**
     * Retrieve the mapped class name matching the requested object
     *
     * @param object The class object to interpret
     * @return the mapped class name
     */
    public static String getCanonicalName(Class<?> object) {
        return getClassName(object, false);
    }

    /**
     * Retrieve the mapped class name matching the requested object
     *
     * @param object The object to interpret
     * @return the mapped class name
     */
    public static String getCanonicalName(Object object) {
        return getCanonicalName(object.getClass());
    }

    /**
     * Retrieve the mapped class name matching the requested object
     *
     * @param object The class object to interpret
     * @return the mapped class name
     */
    public static String getClassName(Class<?> object) {
        return getClassName(object, true);
    }

    /**
     * Retrieve the mapped class name matching the requested object
     *
     * @param object The object to interpret
     * @return the mapped class name
     */
    public static String getClassName(Object object) {
        return getClassName(object.getClass());
    }
}

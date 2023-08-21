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

package com.gitlab.cdagaming.craftpresence.core.utils;

import com.gitlab.cdagaming.craftpresence.core.Constants;
import com.gitlab.cdagaming.craftpresence.core.impl.Pair;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;

import java.io.*;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.FileSystem;
import java.nio.file.*;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

/**
 * File Utilities for interpreting Files and Class Objects
 *
 * @author CDAGaming
 */
public class FileUtils {
    /**
     * A GSON Json Builder Instance
     */
    private static final GsonBuilder GSON_BUILDER = new GsonBuilder();
    /**
     * The list of the currently detected class names
     */
    private static final Map<String, ClassInfo> CLASS_MAP = StringUtils.newHashMap();
    /**
     * Whether the class list from {@link FileUtils#scanClasses()} is being iterated upon
     */
    private static boolean ARE_CLASSES_LOADING = false;

    /**
     * Retrieves Raw Data and Converts it into a Parsed Json Syntax
     *
     * @param data     The File to access
     * @param classObj The target class to base the output on
     * @param <T>      The Result and Class Type
     * @param args     The Command Arguments to parse
     * @return The Parsed Json as the Class Type's Syntax
     * @throws Exception If Unable to read the File
     */
    public static <T> T getJsonData(final File data, final Class<T> classObj, final Modifiers... args) throws Exception {
        return getJsonData(fileToString(data, "UTF-8"), classObj, args);
    }

    /**
     * Retrieves Raw Data and Converts it into a Parsed Json Syntax
     *
     * @param data The File to access
     * @param args The Command Arguments to parse
     * @return The Parsed Json as the Class Type's Syntax
     * @throws Exception If Unable to read the File
     */
    public static JsonElement getJsonData(final File data, final Modifiers... args) throws Exception {
        return getJsonData(data, JsonElement.class, args);
    }

    /**
     * Retrieves Raw Data and Converts it into a Parsed Json Syntax
     *
     * @param data     The json string to access
     * @param classObj The target class to base the output on
     * @param <T>      The Result and Class Type
     * @param args     The Command Arguments to parse
     * @return The Parsed Json as the Class Type's Syntax
     */
    public static <T> T getJsonData(final String data, final Class<T> classObj, final Modifiers... args) {
        final GsonBuilder builder = applyModifiers(GSON_BUILDER, args);
        return builder.create().fromJson(data, classObj);
    }

    /**
     * Retrieves Raw Data and Converts it into a Parsed Json Syntax
     *
     * @param data The json string to access
     * @param args The Command Arguments to parse
     * @return The Parsed Json as the Class Type's Syntax
     */
    public static JsonElement getJsonData(final String data, final Modifiers... args) {
        return getJsonData(data, JsonElement.class, args);
    }

    /**
     * Retrieves Raw Data and Converts it into a Parsed Json Syntax
     *
     * @param data    The File to access
     * @param typeObj The target type to base the output on
     * @param <T>     The Result and Class Type
     * @param args    The Command Arguments to parse
     * @return The Parsed Json as the Class Type's Syntax
     * @throws Exception If Unable to read the File
     */
    public static <T> T getJsonData(final File data, final Type typeObj, final Modifiers... args) throws Exception {
        return getJsonData(fileToString(data, "UTF-8"), typeObj, args);
    }

    /**
     * Retrieves Raw Data and Converts it into a Parsed Json Syntax
     *
     * @param data    The json string to access
     * @param typeObj The target type to base the output on
     * @param <T>     The Result and Class Type
     * @param args    The Command Arguments to parse
     * @return The Parsed Json as the Class Type's Syntax
     */
    public static <T> T getJsonData(final String data, final Type typeObj, final Modifiers... args) {
        final GsonBuilder builder = applyModifiers(GSON_BUILDER, args);
        return builder.create().fromJson(data, typeObj);
    }

    /**
     * Retrieves Raw Data and Converts it into a Parsed Json Syntax
     *
     * @param data     The data to access
     * @param classObj The target class to base the output on
     * @param <T>      The Result and Class Type
     * @param args     The Command Arguments to parse
     * @return The Parsed Json as the Class Type's Syntax
     */
    public static <T> T getJsonData(final T data, final Class<T> classObj, final Modifiers... args) {
        return getJsonData(data.toString(), classObj, args);
    }

    /**
     * Interpret compatible objects into Json Elements
     *
     * @param obj  The object data to access
     * @param args The Command Arguments to parse
     * @return the resulting json string
     */
    public static String toJsonData(Object obj, final Modifiers... args) {
        final GsonBuilder builder = applyModifiers(GSON_BUILDER, args);
        if (obj instanceof String || obj instanceof Reader || obj instanceof JsonReader) {
            obj = parseJson(obj);
        }
        return builder.create().toJson(obj);
    }

    /**
     * Attempt to parse the specified object into a JsonElement
     *
     * @param json the object to interpret
     * @return the processed JsonElement, if able
     */
    public static JsonElement parseJson(Object json) {
        if (json instanceof String) {
            return new JsonParser().parse((String) json);
        } else if (json instanceof Reader) {
            return new JsonParser().parse((Reader) json);
        } else if (json instanceof JsonReader) {
            return new JsonParser().parse((JsonReader) json);
        }
        return null;
    }

    /**
     * Writes Raw Json Data Objects to the specified file
     *
     * @param json     The json data to access
     * @param file     The resulting file to output to
     * @param encoding The encoding to parse the output as
     * @param args     The Command Arguments to parse
     */
    public static void writeJsonData(final Object json, final File file, final String encoding, final Modifiers... args) {
        final GsonBuilder builder = applyModifiers(GSON_BUILDER, args);

        try {
            assertFileExists(file);
        } catch (Exception ex1) {
            Constants.LOG.error("Failed to create json data @ " + file.getAbsolutePath());
            Constants.LOG.debugError(ex1);
        }

        try (Writer writer = new OutputStreamWriter(Files.newOutputStream(file.toPath()), Charset.forName(encoding))) {
            builder.create().toJson(json, writer);
        } catch (Exception ex2) {
            Constants.LOG.error("Failed to write json data @ " + file.getAbsolutePath());
            Constants.LOG.debugError(ex2);
        }
    }

    /**
     * Asserts whether a file exists and is available
     *
     * @param file The target file to interpret or create
     * @throws Exception if an exception occurs in the method
     */
    public static void assertFileExists(final File file) throws Exception {
        final File parentDir = file.getParentFile();
        final boolean parentDirPresent = file.getParentFile().exists() || file.getParentFile().mkdirs();
        final boolean fileAvailable = (file.exists() && file.isFile()) || file.createNewFile();
        if (!parentDirPresent) {
            throw new UnsupportedOperationException("Failed to setup parent directory @ " + parentDir.getAbsolutePath());
        }
        if (!fileAvailable) {
            throw new UnsupportedOperationException("Failed to setup target file (Unable to create or is not a file) @ " + file.getAbsolutePath());
        }
    }

    /**
     * Downloads a File from a {@link URL}, then stores it at the target location
     *
     * @param urlString The Download Link
     * @param file      The destination and filename to store the download as
     */
    public static void downloadFile(final String urlString, final File file) {
        try {
            Constants.LOG.info("Downloading \"%s\" to \"%s\"... (From: \"%s\")", file.getName(), file.getAbsolutePath(), urlString);
            final URL url = new URL(urlString);
            if (file.exists() && !file.delete()) {
                Constants.LOG.error("Failed to remove: " + file.getName());
            }
            copyStreamToFile(UrlUtils.getURLStream(url), file);
            Constants.LOG.info("\"%s\" has been successfully downloaded to \"%s\"! (From: \"%s\")", file.getName(), file.getAbsolutePath(), urlString);
        } catch (Exception ex) {
            Constants.LOG.error("Failed to download \"%s\" from \"%s\"", file.getName(), urlString);
            Constants.LOG.debugError(ex);
        }
    }

    /**
     * Copies bytes from an {@link InputStream} <code>source</code> to a file
     * <code>destination</code>. The directories up to <code>destination</code>
     * will be created if they don't already exist. <code>destination</code>
     * will be overwritten if it already exists.
     * The {@code source} stream is closed, if specified.
     * See {@link #copyToFile(InputStream, File)} for a method that does not close the input stream.
     *
     * @param stream the <code>InputStream</code> to copy bytes from, must not be {@code null}, will be closed if specified
     * @param file   the non-directory <code>File</code> to write bytes to
     *               (possibly overwriting), must not be {@code null}
     * @param close  whether to close the source stream, upon success
     * @throws Exception If unable to complete event (Unable to create needed directories/files, etc.)
     */
    public static void copyStreamToFile(final InputStream stream, final File file, final boolean close) throws Exception {
        // Create File and Parent Directories as needed
        assertFileExists(file);

        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = stream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
        }

        if (close) {
            stream.close();
        }
    }

    /**
     * Copies bytes from an {@link InputStream} <code>source</code> to a file
     * <code>destination</code>. The directories up to <code>destination</code>
     * will be created if they don't already exist. <code>destination</code>
     * will be overwritten if it already exists.
     * The {@code source} stream is closed upon success.
     * See {@link #copyToFile(InputStream, File)} for a method that does not close the input stream.
     *
     * @param stream the <code>InputStream</code> to copy bytes from, must not be {@code null}, will be closed upon success
     * @param file   the non-directory <code>File</code> to write bytes to
     *               (possibly overwriting), must not be {@code null}
     * @throws Exception If unable to complete event (Unable to create needed directories/files, etc.)
     */
    public static void copyStreamToFile(final InputStream stream, final File file) throws Exception {
        copyStreamToFile(stream, file, true);
    }

    /**
     * Copies bytes from an {@link InputStream} <code>source</code> to a file
     * <code>destination</code>. The directories up to <code>destination</code>
     * will be created if they don't already exist. <code>destination</code>
     * will be overwritten if it already exists.
     * The {@code source} stream remains open upon success.
     * See {@link #copyStreamToFile(InputStream, File)} for a method that does close the input stream.
     *
     * @param stream the <code>InputStream</code> to copy bytes from, must not be {@code null}, will remain open upon success
     * @param file   the non-directory <code>File</code> to write bytes to
     *               (possibly overwriting), must not be {@code null}
     * @throws Exception If unable to complete event (Unable to create needed directories/files, etc.)
     */
    public static void copyToFile(final InputStream stream, final File file) throws Exception {
        copyStreamToFile(stream, file, false);
    }

    /**
     * Attempts to convert a File's data into a readable String
     *
     * @param file     The file to access
     * @param encoding The encoding to parse the file as
     * @return The file's data as a String
     * @throws Exception If Unable to read the file
     */
    public static String fileToString(final File file, final String encoding) throws Exception {
        return fileToString(Files.newInputStream(file.toPath()), encoding);
    }

    /**
     * Attempts to convert a InputStream's data into a readable String
     *
     * @param stream   The InputStream to interpret
     * @param encoding The encoding to parse the file as
     * @return The file's data as a String
     * @throws Exception If Unable to read the file
     */
    public static String fileToString(final InputStream stream, final String encoding) throws Exception {
        return UrlUtils.readerToString(
                new BufferedReader(
                        new InputStreamReader(
                                stream, Charset.forName(encoding)
                        )
                )
        );
    }

    /**
     * Convert the specified String into an InputStream
     *
     * @param stream   The string to interpret
     * @param encoding The encoding to parse the file as
     * @return The string's data as an InputStream
     */
    public static InputStream stringToStream(final String stream, final String encoding) {
        return new ByteArrayInputStream(
                StringUtils.getBytes(stream, encoding)
        );
    }

    /**
     * Gets the File Extension of a File (Ex: txt)
     *
     * @param file The file to access
     * @return The file's extension String
     */
    public static String getFileExtension(final File file) {
        return getFileExtension(file.getName());
    }

    /**
     * Gets the File Extension of a File (Ex: txt)
     *
     * @param name The file to access
     * @return The file's extension String
     */
    public static String getFileExtension(final String name) {
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return "";
        }
        return name.substring(lastIndexOf);
    }

    /**
     * Gets the File Name without the File Extension
     *
     * @param file The file to access
     * @return the trimmed file name
     */
    public static String getFileNameWithoutExtension(final File file) {
        return getFileNameWithoutExtension(file.getName());
    }

    /**
     * Gets the File Name without the File Extension
     *
     * @param name The file to access
     * @return the trimmed file name
     */
    public static String getFileNameWithoutExtension(final String name) {
        if (name.indexOf(".") > 0) {
            return name.substring(0, name.lastIndexOf("."));
        } else {
            return name;
        }
    }

    /**
     * Retrieve the Amount of Active Mods in the instance
     *
     * @return The Mods that are active in the instance
     */
    public static int getModCount() {
        int modCount = -1;
        final Class<?> fmlLoader = FileUtils.findValidClass("net.minecraftforge.fml.common.Loader");
        final Class<?> quiltLoader = FileUtils.findValidClass("org.quiltmc.loader.api.QuiltLoader");
        final Class<?> fabricLoader = FileUtils.findValidClass("net.fabricmc.loader.api.FabricLoader");
        if (fmlLoader != null) {
            final Object loaderInstance = StringUtils.executeMethod(fmlLoader, null, null, null, "instance");
            if (loaderInstance != null) {
                final Object mods = StringUtils.executeMethod(fmlLoader, loaderInstance, null, null, "getModList");
                if (mods instanceof List<?>) {
                    modCount = ((List<?>) mods).size();
                }
            }
        } else if (quiltLoader != null) {
            final Object mods = StringUtils.executeMethod(quiltLoader, null, null, null, "getAllMods");
            if (mods instanceof List<?>) {
                modCount = ((List<?>) mods).size();
            }
        } else if (fabricLoader != null) {
            final Object loaderInstance = StringUtils.executeMethod(fabricLoader, null, null, null, "getInstance");
            if (loaderInstance != null) {
                final Object mods = StringUtils.executeMethod(fabricLoader, loaderInstance, null, null, "getAllMods");
                if (mods instanceof List<?>) {
                    modCount = ((List<?>) mods).size();
                }
            }
        }
        return modCount > 0 ? modCount : getRawModCount();
    }

    /**
     * Retrieve the Amount of Active Mods in the {@link Constants#modsDir}
     *
     * @return The Mods that are active in the directory
     */
    public static int getRawModCount() {
        // Mod is within ClassLoader if in a Dev Environment
        // and is thus automatically counted if this is the case
        int modCount = 0;
        final File[] mods = new File(Constants.modsDir).listFiles();

        if (mods != null) {
            for (File modFile : mods) {
                if (getFileExtension(modFile).equals(".jar")) {
                    modCount++;
                }
            }
        }
        return Math.max(1, modCount);
    }

    /**
     * Retrieve a List of Classes that extend or implement anything in the search list
     *
     * @param searchList     The Super Type Classes to look for
     * @param sourcePackages The root package directories to search within
     * @return The List of found class names from the search
     */
    public static Map<String, ClassInfo> getClassNamesMatchingSuperType(final List<Class<?>> searchList, final String... sourcePackages) {
        final Map<String, ClassInfo> matchingClasses = StringUtils.newHashMap();
        final List<String> sourceData = StringUtils.newArrayList(sourcePackages);

        Pair<Boolean, Map<String, ClassInfo>> subClassData = new Pair<>(false, StringUtils.newHashMap());
        for (Map.Entry<String, ClassInfo> classInfo : getClasses(sourceData).entrySet()) {
            for (Class<?> searchClass : searchList) {
                subClassData = isSubclassOf(classInfo.getValue(), searchClass, subClassData.getSecond());

                if (subClassData.getFirst()) {
                    // If superclass data was found, add the scanned classes
                    // as well as the original class
                    if (!matchingClasses.containsKey(classInfo.getKey())) {
                        matchingClasses.put(classInfo.getKey(), classInfo.getValue());
                    }

                    for (Map.Entry<String, ClassInfo> subClassInfo : subClassData.getSecond().entrySet()) {
                        if (!matchingClasses.containsKey(subClassInfo.getKey())) {
                            matchingClasses.put(subClassInfo.getKey(), subClassInfo.getValue());
                        }
                    }

                    break;
                } else {
                    // If no superclass data found, reset for next data
                    subClassData = new Pair<>(false, StringUtils.newHashMap());
                }
            }
        }

        return matchingClasses;
    }

    /**
     * Retrieves sub/super class data for the specified data
     *
     * @param originalClass  The original class to scan for the specified sub/super-class
     * @param superClass     The sub/super-class target to locate
     * @param scannedClasses The class hierarchy of scanned data
     * @return A pair with the format of isSubclassOf:scannedClasses
     */
    protected static Pair<Boolean, Map<String, ClassInfo>> isSubclassOf(final ClassInfo originalClass, final Class<?> superClass, final Map<String, ClassInfo> scannedClasses) {
        if (originalClass == null || superClass == null) {
            // Top of hierarchy, or no super class defined
            return new Pair<>(false, scannedClasses);
        } else if (originalClass.getName().equals(superClass.getName())) {
            return new Pair<>(true, scannedClasses);
        } else {
            // Attempt to see if things match with their deobfuscated names
            final String originalName = MappingUtils.getMappedPath(originalClass.getName());
            final String className = MappingUtils.getCanonicalName(originalClass);
            final String superClassName = MappingUtils.getCanonicalName(superClass);
            if (className.equals(superClassName)) {
                return new Pair<>(true, scannedClasses);
            } else {
                // try the next level up the hierarchy and add this class to scanned history.
                scannedClasses.put(originalName, originalClass);
                final Pair<Boolean, Map<String, ClassInfo>> subClassInfo = isSubclassOf(originalClass.getSuperclass(), superClass, scannedClasses);

                if (!subClassInfo.getFirst() && originalClass.getInterfaces() != null) {
                    for (final ClassInfo inter : originalClass.getInterfaces()) {
                        if (isSubclassOf(inter, superClass, scannedClasses).getFirst()) {
                            return new Pair<>(true, scannedClasses);
                        }
                    }
                }

                return new Pair<>(subClassInfo.getFirst(), scannedClasses);
            }
        }
    }

    /**
     * Retrieve a List of Classes that extend or implement anything in the search list
     *
     * @param searchTarget   The Super Type Class to look for
     * @param sourcePackages The root package directories to search within
     * @return The List of found classes from the search
     */
    public static Map<String, ClassInfo> getClassNamesMatchingSuperType(final Class<?> searchTarget, final String... sourcePackages) {
        return getClassNamesMatchingSuperType(StringUtils.newArrayList(searchTarget), sourcePackages);
    }

    /**
     * Return whether a class exists out of the specified arguments
     *
     * @param loader The {@link ClassLoader} to attempt loading with
     * @param init   Whether to initialize the class, if found
     * @param paths  The class path(s) to interpret
     * @return the valid {@link Class} or null
     */
    public static Class<?> findValidClass(final ClassLoader loader, final boolean init, final String... paths) {
        final List<String> classList = StringUtils.newArrayList(paths);
        for (String path : paths) {
            StringUtils.addEntriesNotPresent(classList, MappingUtils.getUnmappedClassesMatching(path, true));
        }

        for (String path : classList) {
            switch (path) {
                case "boolean":
                    return boolean.class;
                case "byte":
                    return byte.class;
                case "short":
                    return short.class;
                case "int":
                    return int.class;
                case "long":
                    return long.class;
                case "float":
                    return float.class;
                case "double":
                    return double.class;
                case "char":
                    return char.class;
                case "void":
                    return void.class;
                default: {
                    try {
                        if (loader == null) {
                            return Class.forName(path);
                        } else {
                            return Class.forName(path, init, loader);
                        }
                    } catch (Throwable ignored) {
                    }
                }
            }
        }
        return null;
    }

    /**
     * Return whether a class exists out of the specified arguments
     *
     * @param loader The {@link ClassLoader} to attempt loading with
     * @param paths  The class path(s) to interpret
     * @return the valid {@link Class} or null
     */
    public static Class<?> findValidClass(final ClassLoader loader, final String... paths) {
        return findValidClass(loader, false, paths);
    }

    /**
     * Return whether a class exists out of the specified arguments
     *
     * @param useClassLoader Whether to use the thread's current class loader
     * @param paths          The class path(s) to interpret
     * @return the valid {@link Class} or null
     */
    public static Class<?> findValidClass(final boolean useClassLoader, final String... paths) {
        return findValidClass(useClassLoader ? MappingUtils.CLASS_LOADER : null, paths);
    }

    /**
     * Return whether a class exists out of the specified arguments
     *
     * @param paths The class path(s) to interpret
     * @return the valid {@link Class} or null
     */
    public static Class<?> findValidClass(final String... paths) {
        return findValidClass(true, paths);
    }

    /**
     * Return whether the class list from {@link FileUtils#scanClasses()} is being iterated upon
     *
     * @return {@link Boolean#TRUE} if condition is satisfied
     */
    public static boolean canScanClasses() {
        return !ARE_CLASSES_LOADING;
    }

    /**
     * Begin a new Thread, executing {@link FileUtils#scanClasses()}
     */
    public static void detectClasses() {
        Constants.getThreadFactory().newThread(FileUtils::scanClasses).start();
    }

    /**
     * Clear the existing class list, then retrieve and cache all known classes within the Class Loader
     *
     * @return a map of all known classes
     */
    public static Map<String, ClassInfo> scanClasses() {
        if (canScanClasses()) {
            ARE_CLASSES_LOADING = true;
            CLASS_MAP.clear();

            // Attempt to get all possible classes from the JVM Class Loader
            final ClassGraph graphInfo = new ClassGraph()
                    .enableClassInfo()
                    .rejectPackages(
                            "net.java", "com.sun", "com.jcraft", "com.intellij", "jdk", "akka", "ibxm", "scala",
                            "*.mixin.*", "*.mixins.*", "*.jetty.*"
                    )
                    .disableModuleScanning();
            if (MappingUtils.JAVA_SPEC < 16) {
                // If we are below Java 16, we can just use the Thread's classloader
                // See: https://github.com/classgraph/classgraph/wiki#running-on-jdk-16
                graphInfo.overrideClassLoaders(MappingUtils.CLASS_LOADER);
            }

            try (ScanResult scanResult = graphInfo.scan()) {
                for (ClassInfo result : scanResult.getAllClasses()) {
                    final String resultName = MappingUtils.getMappedPath(result.getName());
                    if (!CLASS_MAP.containsKey(resultName) && !resultName.toLowerCase().contains("mixin")) {
                        CLASS_MAP.put(resultName, result);
                    }
                }
            } catch (Throwable ex) {
                Constants.LOG.debugError(ex);
            }

            ARE_CLASSES_LOADING = false;
        }
        return StringUtils.newHashMap(CLASS_MAP);
    }

    /**
     * Retrieve and Cache all known classes within the Class Loader
     *
     * @return a map of all known classes
     */
    public static Map<String, ClassInfo> getClassMap() {
        if (CLASS_MAP.isEmpty()) {
            return scanClasses();
        }
        return StringUtils.newHashMap(CLASS_MAP);
    }

    /**
     * Retrieve a list of all classes matching the specified lists of paths
     *
     * @param paths A nullable list of paths to be interpreted
     * @return the resulting list
     */
    public static Map<String, ClassInfo> getClasses(final List<String> paths) {
        final Map<String, ClassInfo> results = StringUtils.newHashMap();
        final Map<String, Set<String>> unmappedNames = StringUtils.newHashMap();
        for (String path : paths) {
            unmappedNames.put(path, MappingUtils.getUnmappedClassesMatching(path));
        }

        for (Map.Entry<String, ClassInfo> classInfo : getClassMap().entrySet()) {
            if (classInfo != null) {
                final String classPath = classInfo.getKey();
                boolean hasMatch = paths.isEmpty();
                // Attempt to Add Classes Matching any of the Source Packages
                for (String path : paths) {
                    final Set<String> unmapped = unmappedNames.get(path);
                    if (classPath.startsWith(path) || unmapped.contains(classPath)) {
                        hasMatch = true;
                        break;
                    }
                }

                if (hasMatch) {
                    try {
                        results.put(classPath, classInfo.getValue());
                    } catch (Throwable ignored) {
                    }
                }
            }
        }
        return results;
    }

    /**
     * Retrieve a list of files in a directory
     *
     * @param fallbackClass Alternative Class Loader to Use to Locate the Resource
     * @param pathToSearch  The File Path to search for
     * @return the list of files found, if any
     */
    public static List<String> filesInDir(final Class<?> fallbackClass, String pathToSearch) {
        final List<String> paths = StringUtils.newArrayList();
        if (!pathToSearch.endsWith("/")) {
            pathToSearch = pathToSearch + "/";
        }

        try {
            final URI uri = getResource(fallbackClass, pathToSearch).toURI();
            FileSystem fileSystem = null;
            Path myPath;
            if (uri.getScheme().equals("jar")) {
                try {
                    fileSystem = FileSystems.getFileSystem(uri);
                } catch (Exception ex) {
                    fileSystem = FileSystems.newFileSystem(uri, StringUtils.newHashMap());
                }

                myPath = fileSystem.getPath(pathToSearch);
            } else {
                myPath = Paths.get(uri);
            }

            final Stream<Path> walk = Files.walk(myPath, 1);

            try {
                final Iterator<Path> it = walk.iterator();
                it.next();

                while (it.hasNext()) {
                    paths.add(pathToSearch + it.next().getFileName());
                }
            } catch (Throwable ex) {
                if (walk != null) {
                    try {
                        walk.close();
                    } catch (Throwable ex2) {
                        ex.addSuppressed(ex2);
                    }
                }

                throw ex;
            }

            walk.close();

            if (fileSystem != null) {
                fileSystem.close();
            }
        } catch (IOException | URISyntaxException ignored) {
        }

        return paths;
    }

    /**
     * Attempts to Retrieve the Specified Resource as an InputStream
     *
     * @param fallbackClass Alternative Class Loader to Use to Locate the Resource
     * @param pathToSearch  The File Path to search for
     * @return The InputStream for the specified resource, if successful
     */
    public static InputStream getResourceAsStream(final Class<?> fallbackClass, final String pathToSearch) {
        InputStream in = null;
        boolean useFallback = false;

        try {
            in = MappingUtils.CLASS_LOADER.getResourceAsStream(pathToSearch);
        } catch (Exception ex) {
            useFallback = true;
        }

        if (useFallback || in == null) {
            in = fallbackClass.getResourceAsStream(pathToSearch);
        }
        return in;
    }

    /**
     * Attempts to Retrieve the Specified Resource
     *
     * @param fallbackClass Alternative Class Loader to Use to Locate the Resource
     * @param pathToSearch  The File Path to search for
     * @return The specified resource, if successful
     */
    public static URL getResource(final Class<?> fallbackClass, final String pathToSearch) {
        URL in = null;
        boolean useFallback = false;

        try {
            in = MappingUtils.CLASS_LOADER.getResource(pathToSearch);
        } catch (Exception ex) {
            useFallback = true;
        }

        if (useFallback || in == null) {
            in = fallbackClass.getResource(pathToSearch);
        }
        return in;
    }

    /**
     * Applies the specified {@link Modifiers} to a {@link GsonBuilder} instance
     *
     * @param instance The {@link GsonBuilder} to interpret
     * @param args     The Command Arguments to parse
     * @return The modified {@link GsonBuilder} instance
     */
    public static GsonBuilder applyModifiers(final GsonBuilder instance, final Modifiers... args) {
        for (Modifiers param : args) {
            switch (param) {
                case DISABLE_ESCAPES:
                    instance.disableHtmlEscaping();
                    break;
                case PRETTY_PRINT:
                    instance.setPrettyPrinting();
                    break;
                default:
                    break;
            }
        }
        return instance;
    }

    /**
     * Constants representing various {@link GsonBuilder} toggles,
     * such as Disabling Escape Characters and Toggling Pretty Print
     */
    public enum Modifiers {
        /**
         * Constant for the "Disable Escapes" Modifier.
         */
        DISABLE_ESCAPES,
        /**
         * Constant for the "Pretty Print" Modifier.
         */
        PRETTY_PRINT
    }
}

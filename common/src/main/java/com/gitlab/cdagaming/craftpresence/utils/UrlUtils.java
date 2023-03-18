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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;

/**
 * URL Utilities for parsing URL and relative Json Data
 *
 * @author CDAGaming
 */
public class UrlUtils {
    /**
     * The User Agent to Identify As when Accessing other URLs
     */
    private static final String USER_AGENT = ModUtils.MOD_ID + "/" + ModUtils.MCVersion;
    /**
     * The GSON Json Builder to Use while Parsing Json
     */
    private static final Gson GSON = new GsonBuilder().create();

    /**
     * Retrieve Output from a URL as a readable String
     *
     * @param url      The URL to Access
     * @param encoding The Charset Encoding to parse URL Contents in
     * @return The Output from the url as a String
     * @throws Exception If connection or Input is unable to be established
     */
    public static String getURLText(final URL url, final String encoding) throws Exception {
        return getString(getURLReader(url, encoding));
    }

    private static String getString(BufferedReader urlReader) throws Exception {
        final StringBuilder response = new StringBuilder();
        String inputLine;
        while (!StringUtils.isNullOrEmpty((inputLine = urlReader.readLine()))) {
            response.append(inputLine);
        }
        urlReader.close();
        return response.toString();
    }

    /**
     * Retrieve Output from a URL as a readable String
     *
     * @param url      The URL to Access
     * @param encoding The Charset Encoding to parse URL Contents in
     * @return The Output from the url as a String
     * @throws Exception If connection or Input is unable to be established
     */
    public static String getURLText(final String url, final String encoding) throws Exception {
        return getString(getURLReader(url, encoding));
    }

    /**
     * Retrieve a {@link BufferedReader} to read a response from a URL
     *
     * @param url      The URL to access (To be converted to a URL)
     * @param encoding The Charset Encoding to parse URL Contents in
     * @return a {@link BufferedReader} to read an output response from
     * @throws Exception If a connection is unable to be established
     */
    public static BufferedReader getURLReader(final String url, final String encoding) throws Exception {
        return getURLReader(new URL(url), encoding);
    }

    /**
     * Retrieve a {@link BufferedReader} to read a response from a URL
     *
     * @param url      The URL to access
     * @param encoding The Charset Encoding to parse URL Contents in
     * @return a {@link BufferedReader} to read an output response from
     * @throws Exception If a connection is unable to be established
     */
    public static BufferedReader getURLReader(final URL url, final String encoding) throws Exception {
        return new BufferedReader(getURLStreamReader(url, encoding));
    }

    /**
     * Retrieve an {@link InputStream} from a URL
     *
     * @param url The URL to access
     * @return an {@link InputStream} from the URL
     * @throws Exception If a connection is unable to be established
     */
    public static InputStream getURLStream(final URL url) throws Exception {
        if (MappingUtils.JAVA_SPEC < 1.8f) {
            // Java Versions below 1.8 do not supply a modern protocol_version
            // which can break certain URL requests.
            // To avoid this, TLSv1.2 is used as the protocol, which is equivalent to 1.8s default
            System.setProperty("https.protocols", "TLSv1.2");
        }
        final URLConnection connection = url.openConnection();
        connection.addRequestProperty("User-Agent", USER_AGENT);
        return (connection.getInputStream());
    }

    /**
     * Retrieve an {@link InputStreamReader} from a URL
     *
     * @param url      The URL to access
     * @param encoding The Charset Encoding to parse URL Contents in
     * @return an {@link InputStreamReader} from the URL
     * @throws Exception If a connection is unable to be established
     */
    public static InputStreamReader getURLStreamReader(final URL url, final String encoding) throws Exception {
        return new InputStreamReader(getURLStream(url), Charset.forName(encoding));
    }

    /**
     * Converts a URLs Output into Formatted Json
     *
     * @param url         The URL to access (To be converted into a URL)
     * @param targetClass The target class to base parsing on
     * @param <T>         The data type for the resulting Json
     * @return The URL's Output, as Formatted Json
     * @throws Exception If a connection is unable to be established or parsing fails
     */
    public static <T> T getJSONFromURL(String url, Class<T> targetClass) throws Exception {
        return getJSONFromURL(new URL(url), targetClass);
    }

    /**
     * Converts a URLs Output into Formatted Json
     *
     * @param url         The URL to access
     * @param targetClass The target class to base parsing on
     * @param <T>         The data type for the resulting Json
     * @return The URL's Output, as Formatted Json
     * @throws Exception If a connection is unable to be established or parsing fails
     */
    public static <T> T getJSONFromURL(URL url, Class<T> targetClass) throws Exception {
        return GSON.fromJson(getURLStreamReader(url, "UTF-8"), targetClass);
    }

    /**
     * Opens the Specified Url in a Browser, if able
     *
     * @param targetUrl The URL to Open, as a String
     * @return {@link Boolean#TRUE} upon success
     */
    public static boolean openUrl(final String targetUrl) {
        try {
            return openUrl(new URI(targetUrl));
        } catch (Exception ex) {
            if (CommandUtils.isVerboseMode()) {
                ex.printStackTrace();
            }
            return false;
        }
    }

    /**
     * Opens the Specified Url in a Browser, if able
     *
     * @param targetUrl The URL to Open, as a {@link URL}
     * @return {@link Boolean#TRUE} upon success
     */
    public static boolean openUrl(final URL targetUrl) {
        try {
            return openUrl(targetUrl.toURI());
        } catch (Exception ex) {
            if (CommandUtils.isVerboseMode()) {
                ex.printStackTrace();
            }
            return false;
        }
    }

    /**
     * Opens the Specified Url in a Browser, if able
     *
     * @param targetUrl The URL to Open, as a {@link URI}
     * @return {@link Boolean#TRUE} upon success
     */
    public static boolean openUrl(final URI targetUrl) {
        if (browseWithDesktop(targetUrl)) {
            return true;
        }
        if (SystemUtils.browseWithSystem(targetUrl.toString())) {
            return true;
        }
        ModUtils.LOG.warn("Failed to browse %s", targetUrl);
        return false;
    }

    /**
     * Attempt to browse to the specified {@link URI} utilizing the Java AWT Desktop API
     *
     * @param uri The URL to Open, as a {@link URI}
     * @return {@link Boolean#TRUE} upon success
     */
    public static boolean browseWithDesktop(final URI uri) {
        try {
            if (!java.awt.Desktop.isDesktopSupported()) {
                ModUtils.LOG.debugInfo("Platform is not supported.");
                return false;
            }

            if (!java.awt.Desktop.getDesktop().isSupported(java.awt.Desktop.Action.BROWSE)) {
                ModUtils.LOG.debugInfo("BROWSE is not supported.");
                return false;
            }

            ModUtils.LOG.debugInfo("Trying to use Desktop.getDesktop().browse() with " + uri.toString());
            java.awt.Desktop.getDesktop().browse(uri);

            return true;
        } catch (Throwable t) {
            ModUtils.LOG.error("Error using desktop browse.", t);
            return false;
        }
    }
}

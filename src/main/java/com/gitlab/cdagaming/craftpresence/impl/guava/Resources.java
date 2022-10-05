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

package com.gitlab.cdagaming.craftpresence.impl.guava;

import com.google.common.base.Charsets;
import com.google.common.io.ByteSource;
import com.google.common.io.CharSource;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.Charset;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Provides utility methods for working with resources in the classpath. Note that even though these
 * methods use {@link URL} parameters, they are usually not appropriate for HTTP or other
 * non-classpath resources.
 *
 * <p>All method parameters must be non-null unless documented otherwise.
 *
 * @author Chris Nokleberg
 * @author Ben Yu
 * @author Colin Decker
 * @since 1.0
 */
public final class Resources {
    private Resources() {
    }

    /**
     * Returns a {@link ByteSource} that reads from the given URL.
     *
     * @since 14.0
     */
    public static ByteSource asByteSource(URL url) {
        return new UrlByteSource(url);
    }

    /**
     * Returns a {@link CharSource} that reads from the given URL using the given character set.
     *
     * @since 14.0
     */
    public static CharSource asCharSource(URL url, Charset charset) {
        return asByteSource(url).asCharSource(charset);
    }

    /**
     * Reads all bytes from a URL into a byte array.
     *
     * @param url the URL to read from
     * @return a byte array containing all the bytes from the URL
     * @throws IOException if an I/O error occurs
     */
    public static byte[] toByteArray(URL url) throws IOException {
        return asByteSource(url).read();
    }

    /**
     * Reads all characters from a URL into a {@link String}, using the given character set.
     *
     * @param url     the URL to read from
     * @param charset the charset used to decode the input stream; see {@link Charsets} for helpful
     *                predefined constants
     * @return a string containing all the characters from the URL
     * @throws IOException if an I/O error occurs.
     */
    public static String toString(URL url, Charset charset) throws IOException {
        return asCharSource(url, charset).read();
    }

    /**
     * Copies all bytes from a URL to an output stream.
     *
     * @param from the URL to read from
     * @param to   the output stream
     * @throws IOException if an I/O error occurs
     */
    public static void copy(URL from, OutputStream to) throws IOException {
        asByteSource(from).copyTo(to);
    }

    /**
     * A byte source that reads from a URL using {@link URL#openStream()}.
     */
    private static final class UrlByteSource extends ByteSource {

        private final URL url;

        private UrlByteSource(URL url) {
            this.url = checkNotNull(url);
        }

        @Override
        public InputStream openStream() throws IOException {
            return url.openStream();
        }

        @Override
        public String toString() {
            return "Resources.asByteSource(" + url + ")";
        }
    }
}

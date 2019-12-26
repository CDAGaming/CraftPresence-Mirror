/*
 * junixsocket
 * <p>
 * Copyright 2009-2019 Christian Kohlschütter
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.newsclub.net.unix;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.util.Locale;

/**
 * Describes an {@link InetSocketAddress} that actually uses AF_UNIX sockets instead of AF_INET.
 * <p>
 * The ability to specify a port number is not specified by AF_UNIX sockets, but we need it
 * sometimes, for example for RMI-over-AF_UNIX.
 *
 * @author Christian Kohlschütter
 */
public final class AFUNIXSocketAddress extends InetSocketAddress {
    private static final long serialVersionUID = 1L;
    private final byte[] bytes;

    /**
     * Creates a new {@link AFUNIXSocketAddress} that points to the AF_UNIX socket specified by the
     * given file.
     *
     * @param socketFile The socket to connect to.
     * @throws IOException if the operation fails.
     */
    public AFUNIXSocketAddress(final File socketFile) throws IOException {
        this(socketFile, 0);
    }

    /**
     * Creates a new {@link AFUNIXSocketAddress} that points to the AF_UNIX socket specified by the
     * given file, assigning the given port to it.
     *
     * @param socketFile The socket to connect to.
     * @param port       The port associated with this socket, or {@code 0} when no port should be assigned.
     * @throws IOException if the operation fails.
     */
    public AFUNIXSocketAddress(final File socketFile, int port) throws IOException {
        this(socketFile.getCanonicalPath().getBytes(Charset.defaultCharset()), port);
    }

    /**
     * Creates a new {@link AFUNIXSocketAddress} that points to the AF_UNIX socket specified by the
     * given byte sequence.
     * <p>
     * NOTE: By specifying a byte array that starts with a zero byte, you indicate that the abstract
     * namespace is to be used. This feature is not available on all target platforms.
     *
     * @param socketAddress The socket address (as bytes).
     * @throws IOException if the operation fails.
     * @see AFUNIXSocketAddress#inAbstractNamespace(String)
     */
    public AFUNIXSocketAddress(final byte[] socketAddress) throws IOException {
        this(socketAddress, 0);
    }

    /**
     * Creates a new {@link AFUNIXSocketAddress} that points to the AF_UNIX socket specified by the
     * given byte sequence, assigning the given port to it.
     * <p>
     * NOTE: By specifying a byte array that starts with a zero byte, you indicate that the abstract
     * namespace is to be used. This feature is not available on all target platforms.
     *
     * @param socketAddress The socket address (as bytes).
     * @param port          The port associated with this socket, or {@code 0} when no port should be assigned.
     * @throws IOException if the operation fails.
     * @see AFUNIXSocketAddress#inAbstractNamespace(String, int)
     */
    public AFUNIXSocketAddress(final byte[] socketAddress, int port) throws IOException {
        super(InetAddress.getLoopbackAddress(), 0);
        if (port != 0) {
            NativeUnixSocket.setPort1(this, port);
        }

        if (socketAddress.length == 0) {
            throw new SocketException("Illegal address length: " + socketAddress.length);
        }

        this.bytes = socketAddress.clone();
    }

    /**
     * Convenience method to create an {@link AFUNIXSocketAddress} in the abstract namespace.
     * <p>
     * The returned socket address will use the byte representation of this identifier (using the
     * system's default character encoding), prefixed with a null byte (to indicate the abstract
     * namespace is used).
     *
     * @param name The identifier in the abstract namespace, without trailing zero or @.
     * @return The address.
     * @throws IOException if the operation fails.
     */
    public static AFUNIXSocketAddress inAbstractNamespace(String name) throws IOException {
        return inAbstractNamespace(name, 0);
    }

    /**
     * Convenience method to create an {@link AFUNIXSocketAddress} in the abstract namespace.
     * <p>
     * The returned socket address will use the byte representation of this identifier (using the
     * system's default character encoding), prefixed with a null byte (to indicate the abstract
     * namespace is used).
     *
     * @param name The identifier in the abstract namespace, without trailing zero or @.
     * @param port The port associated with this socket, or {@code 0} when no port should be assigned.
     * @return The address.
     * @throws IOException if the operation fails.
     */
    public static AFUNIXSocketAddress inAbstractNamespace(String name, int port) throws IOException {
        byte[] bytes = name.getBytes(Charset.defaultCharset());
        byte[] addr = new byte[bytes.length + 1];
        System.arraycopy(bytes, 0, addr, 1, bytes.length);
        return new AFUNIXSocketAddress(addr, port);
    }

    private static String prettyPrint(byte[] data) {
        final int dataLength = data.length;
        StringBuilder sb = new StringBuilder(dataLength + 16);
        for (byte c : data) {
            if (c >= 32 && c < 127) {
                sb.append((char) c);
            } else {
                sb.append("\\x");
                sb.append(String.format(Locale.ENGLISH, "%02x", c));
            }
        }
        return sb.toString();
    }

    byte[] getBytes() {
        return bytes; // NOPMD
    }

    @Override
    public String toString() {
        return getClass().getName() + "[port=" + getPort() + ";path=" + prettyPrint(bytes) + "]";
    }

    /**
     * Returns the path to the UNIX domain socket, as a human-readable string.
     * <p>
     * Zero-bytes are converted to '@', other non-printable bytes are converted to '.'
     *
     * @return The path.
     * @see #getPathAsBytes()
     */
    public String getPath() {
        byte[] by = getPathAsBytes();
        for (int i = 1; i < by.length; i++) {
            byte b = by[i];
            if (b == 0) {
                by[i] = '@';
            } else if (b < 32 || b == 127) {
                by[i] = '.';
            }
        }
        return new String(by, Charset.defaultCharset());
    }

    /**
     * Returns the path to the UNIX domain socket, as bytes.
     *
     * @return The path.
     * @see #getPath()
     */
    public byte[] getPathAsBytes() {
        return this.bytes.clone();
    }
}

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

import java.io.Closeable;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;

/**
 * Implementation of an AF_UNIX domain socket.
 *
 * @author Christian Kohlschütter
 */
public final class AFUNIXSocket extends Socket {
    static String loadedLibrary; // set by NativeLibraryLoader

    private static Integer capabilities = null;
    private final AFUNIXSocketFactory socketFactory;
    private final Closeables closeables = new Closeables();
    AFUNIXSocketImpl impl;
    AFUNIXSocketAddress addr;

    private AFUNIXSocket(final AFUNIXSocketImpl impl, AFUNIXSocketFactory factory)
            throws IOException {
        super(impl);
        this.socketFactory = factory;
        if (factory == null) {
            setIsCreated();
        }
    }

    /**
     * Creates a new, unbound {@link AFUNIXSocket}.
     * <p>
     * This "default" implementation is a bit "lenient" with respect to the specification.
     * <p>
     * In particular, we ignore calls to {@link Socket#getTcpNoDelay()} and
     * {@link Socket#setTcpNoDelay(boolean)}.
     *
     * @return A new, unbound socket.
     * @throws IOException if the operation fails.
     */
    public static AFUNIXSocket newInstance() throws IOException {
        return newInstance(null);
    }

    static AFUNIXSocket newInstance(AFUNIXSocketFactory factory) throws IOException {
        final AFUNIXSocketImpl impl = new AFUNIXSocketImpl.Lenient();
        AFUNIXSocket instance = new AFUNIXSocket(impl, factory);
        instance.impl = impl;
        return instance;
    }

    /**
     * Creates a new, unbound, "strict" {@link AFUNIXSocket}.
     * <p>
     * This call uses an implementation that tries to be closer to the specification than
     * {@link #newInstance()}, at least for some cases.
     *
     * @return A new, unbound socket.
     * @throws IOException if the operation fails.
     */
    public static AFUNIXSocket newStrictInstance() throws IOException {
        final AFUNIXSocketImpl impl = new AFUNIXSocketImpl();
        AFUNIXSocket instance = new AFUNIXSocket(impl, null);
        instance.impl = impl;
        return instance;
    }

    /**
     * Creates a new {@link AFUNIXSocket} and connects it to the given {@link AFUNIXSocketAddress}.
     *
     * @param addr The address to connect to.
     * @return A new, connected socket.
     * @throws IOException if the operation fails.
     */
    public static AFUNIXSocket connectTo(AFUNIXSocketAddress addr) throws IOException {
        AFUNIXSocket socket = newInstance();
        socket.connect(addr);
        return socket;
    }

    /**
     * Returns <code>true</code> iff {@link AFUNIXSocket}s are supported by the current Java VM.
     * <p>
     * To support {@link AFUNIXSocket}s, a custom JNI library must be loaded that is supplied with
     * <em>junixsocket</em>.
     *
     * @return {@code true} iff supported.
     */
    public static boolean isSupported() {
        return NativeUnixSocket.isLoaded();
    }

    /**
     * Returns the version of the junixsocket library, as a string, for debugging purposes.
     * <p>
     * NOTE: Do not rely on the format of the version identifier, use socket capabilities instead.
     *
     * @return String The version identfier, or {@code null} if it could not be determined.
     * @see #supports(AFUNIXSocketCapability)
     */
    public static String getVersion() {
        try {
            return NativeLibraryLoader.getJunixsocketVersion();
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Returns an identifier of the loaded native library, or {@code null} if the library hasn't been
     * loaded yet.
     * <p>
     * The identifier is useful mainly for debugging purposes.
     *
     * @return The identifier of the loaded junixsocket-native library, or {@code null}.
     */
    public static String getLoadedLibrary() {
        return loadedLibrary;
    }

    private static synchronized int getCapabilities() {
        if (capabilities == null) {
            if (!isSupported()) {
                capabilities = 0;
            } else {
                capabilities = NativeUnixSocket.capabilities();
            }
        }
        return capabilities;
    }

    /**
     * Checks if the current environment (system platform, native library, etc.) supports a given
     * junixsocket capability.
     *
     * @param capability The capability.
     * @return true if supported.
     */
    public static boolean supports(AFUNIXSocketCapability capability) {
        return (getCapabilities() & capability.getBitmask()) != 0;
    }

    private void setIsCreated() throws IOException {
        try {
            NativeUnixSocket.setCreated(this);
        } catch (LinkageError e) {
            throw new IOException("Couldn't load native library", e);
        }
    }

    /**
     * Binds this {@link AFUNIXSocket} to the given bindpoint. Only bindpoints of the type
     * {@link AFUNIXSocketAddress} are supported.
     */
    @Override
    public void bind(SocketAddress bindpoint) throws IOException {
        super.bind(bindpoint);
        this.addr = (AFUNIXSocketAddress) bindpoint;
    }

    @Override
    public void connect(SocketAddress endpoint) throws IOException {
        connect(endpoint, 0);
    }

    @Override
    public void connect(SocketAddress originalEndPoint, int timeout) throws IOException {
        SocketAddress endpoint = originalEndPoint;
        if (!(endpoint instanceof AFUNIXSocketAddress)) {
            if (socketFactory != null && endpoint instanceof InetSocketAddress) {
                InetSocketAddress isa = (InetSocketAddress) endpoint;

                String hostname = isa.getHostString();
                if (socketFactory.isHostnameSupported(hostname)) {
                    endpoint = socketFactory.addressFromHost(hostname, isa.getPort());
                }
            }
            if (!(endpoint instanceof AFUNIXSocketAddress)) {
                throw new IllegalArgumentException("Can only connect to endpoints of type "
                        + AFUNIXSocketAddress.class.getName() + ", got: " + endpoint);
            }
        }
        impl.connect(endpoint, timeout);
        this.addr = (AFUNIXSocketAddress) endpoint;
        NativeUnixSocket.setBound(this);
        NativeUnixSocket.setConnected(this);
    }

    @Override
    public String toString() {
        if (isConnected()) {
            return "AFUNIXSocket[fd=" + impl.getFD() + ";addr=" + addr.toString() + "]";
        }
        return "AFUNIXSocket[unconnected]";
    }

    /**
     * Retrieves the "peer credentials" for this connection.
     * <p>
     * These credentials may be useful to authenticate the other end of the socket (client or server).
     *
     * @return The peer's credentials.
     * @throws IOException If there was an error returning these credentials.
     */
    public AFUNIXSocketCredentials getPeerCredentials() throws IOException {
        if (isClosed() || !isConnected()) {
            throw new SocketException("Not connected");
        }
        return impl.getPeerCredentials();
    }

    @Override
    public boolean isClosed() {
        return super.isClosed() || (isConnected() && !impl.getFD().valid());
    }

    /**
     * Returns the size of the receive buffer for ancillary messages (in bytes).
     *
     * @return The size.
     */
    public int getAncillaryReceiveBufferSize() {
        return impl.getAncillaryReceiveBufferSize();
    }

    /**
     * Sets the size of the receive buffer for ancillary messages (in bytes).
     * <p>
     * To disable handling ancillary messages, set it to 0 (default).
     *
     * @param size The size.
     */
    public void setAncillaryReceiveBufferSize(int size) {
        impl.setAncillaryReceiveBufferSize(size);
    }

    /**
     * Ensures a minimum ancillary receive buffer size.
     *
     * @param minSize The minimum size (in bytes).
     */
    public void ensureAncillaryReceiveBufferSize(int minSize) {
        impl.ensureAncillaryReceiveBufferSize(minSize);
    }

    /**
     * Retrieves an array of incoming {@link FileDescriptor}s that were sent as ancillary messages,
     * along with a call to {@link InputStream#read()}, etc.
     * <p>
     * NOTE: Another call to this method will not return the same file descriptors again (most likely,
     * {@code null} will be returned).
     *
     * @return The file descriptors, or {@code null} if none were available.
     * @throws IOException if the operation fails.
     */
    public FileDescriptor[] getReceivedFileDescriptors() throws IOException {
        return impl.getReceivedFileDescriptors();
    }

    /**
     * Clears the queue of incoming {@link FileDescriptor}s that were sent as ancillary messages.
     */
    public void clearReceivedFileDescriptors() {
        impl.clearReceivedFileDescriptors();
    }

    /**
     * Sets a list of {@link FileDescriptor}s that should be sent as an ancillary message along with
     * the next write.
     * <p>
     * NOTE: There can only be one set of file descriptors active until the write completes.
     *
     * @param fdescs The file descriptors, or {@code null} if none.
     * @throws IOException if the operation fails.
     */
    public void setOutboundFileDescriptors(FileDescriptor... fdescs) throws IOException {
        impl.setOutboundFileDescriptors(fdescs);
    }

    @Override
    public synchronized void close() throws IOException {
        IOException superException = null;
        try {
            super.close();
        } catch (IOException e) {
            superException = e;
        }
        closeables.close(superException);
    }

    /**
     * Registers a {@link Closeable} that should be closed when this socket is closed.
     *
     * @param closeable The closeable.
     */
    public void addCloseable(Closeable closeable) {
        closeables.add(closeable);
    }

    /**
     * Unregisters a previously registered {@link Closeable}.
     *
     * @param closeable The closeable.
     */
    public void removeCloseable(Closeable closeable) {
        closeables.remove(closeable);
    }
}

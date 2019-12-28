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

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The Java-part of the {@link AFUNIXSocket} implementation.
 *
 * @author Christian Kohlschütter
 */
class AFUNIXSocketImpl extends SocketImpl {
    private static final int SHUT_RD = 0;
    private static final int SHUT_WR = 1;
    private static final int SHUT_RD_WR = 2;
    private final AFUNIXInputStream in = newInputStream();
    private final AFUNIXOutputStream out = newOutputStream();
    private final AtomicInteger pendingAccepts = new AtomicInteger(0);
    private final List<FileDescriptor[]> receivedFileDescriptors = Collections.synchronizedList(
            new LinkedList<>());
    private final Map<FileDescriptor, Integer> closeableFileDescriptors = Collections.synchronizedMap(
            new HashMap<>());
    private AFUNIXSocketAddress socketAddress;
    /**
     * We keep track of the server's inode to detect when another server connects to our address.
     */
    private long inode = -1;
    private volatile boolean closed = false;
    private volatile boolean bound = false;
    private boolean connected = false;
    private volatile boolean closedInputStream = false;
    private volatile boolean closedOutputStream = false;
    private boolean reuseAddr = true;
    private ByteBuffer ancillaryReceiveBuffer = ByteBuffer.allocateDirect(0);
    private int[] pendingFileDescriptors = null;
    private int timeout = 0;

    protected AFUNIXSocketImpl() {
        super();
        this.fd = new FileDescriptor();
        this.address = InetAddress.getLoopbackAddress();
    }

    private static int expectInteger(Object value) throws SocketException {
        try {
            return (Integer) value;
        } catch (final ClassCastException e) {
            throw (SocketException) new SocketException("Unsupported value: " + value).initCause(e);
        } catch (final NullPointerException e) {
            throw (SocketException) new SocketException("Value must not be null").initCause(e);
        }
    }

    private static int expectBoolean(Object value) throws SocketException {
        try {
            return (Boolean) value ? 1 : 0;
        } catch (final ClassCastException e) {
            throw (SocketException) new SocketException("Unsupported value: " + value).initCause(e);
        } catch (final NullPointerException e) {
            throw (SocketException) new SocketException("Value must not be null").initCause(e);
        }
    }

    protected AFUNIXInputStream newInputStream() {
        return new AFUNIXInputStream();
    }

    protected AFUNIXOutputStream newOutputStream() {
        return new AFUNIXOutputStream();
    }

    FileDescriptor getFD() {
        return fd;
    }

    // NOTE: This prevents a file descriptor leak
    // see conversation in https://github.com/kohlschutter/junixsocket/pull/29
    @Override
    protected final void finalize() {
        try {
            // prevent socket file descriptor leakage
            close();
        } catch (Throwable t) {
            // nothing that can be done here
        }

        // Also close all file descriptors that we've received over the wire
        try {
            synchronized (closeableFileDescriptors) {
                for (FileDescriptor fd : closeableFileDescriptors.keySet()) {
                    NativeUnixSocket.close(fd);
                }
            }
        } catch (Throwable t) {
            // nothing that can be done here
        }
    }

    @Override
    protected void accept(SocketImpl socket) throws IOException {
        FileDescriptor fdesc = validFdOrException();

        final AFUNIXSocketImpl si = (AFUNIXSocketImpl) socket;
        try {
            if (pendingAccepts.incrementAndGet() >= Integer.MAX_VALUE) {
                throw new SocketException("Too many pending accepts");
            } else {
                if (!bound || closed) {
                    throw new SocketException("Socket is closed");
                }

                NativeUnixSocket.accept(socketAddress.getBytes(), fdesc, si.fd, inode, this.timeout);
                if (!bound || closed) {
                    try {
                        NativeUnixSocket.shutdown(si.fd, SHUT_RD_WR);
                    } catch (Exception e) {
                        // ignore
                    }
                    try {
                        NativeUnixSocket.close(si.fd);
                    } catch (Exception e) {
                        // ignore
                    }
                    throw new SocketException("Socket is closed");
                }
            }
        } finally {
            pendingAccepts.decrementAndGet();
        }
        si.socketAddress = socketAddress;
        si.connected = true;
        si.port = socketAddress.getPort();
        si.address = socketAddress.getAddress();
    }

    @Override
    protected int available() throws IOException {
        FileDescriptor fdesc = validFdOrException();
        return NativeUnixSocket.available(fdesc);
    }

    protected void bind(SocketAddress addr) throws IOException {
        bind(addr, -1);
    }

    protected void bind(SocketAddress addr, int options) throws IOException {
        if (!(addr instanceof AFUNIXSocketAddress)) {
            throw new SocketException("Cannot bind to this type of address: " + addr.getClass());
        }

        this.socketAddress = (AFUNIXSocketAddress) addr;
        this.address = socketAddress.getAddress();

        this.inode = NativeUnixSocket.bind(socketAddress.getBytes(), fd, options);
        validFdOrException();
        bound = true;
        this.localport = socketAddress.getPort();
    }

    @Override
    @SuppressWarnings("hiding")
    protected void bind(InetAddress host, int port) throws IOException {
        throw new SocketException("Cannot bind to this type of address: " + InetAddress.class);
    }

    private void checkClose() throws IOException {
        if (closedInputStream && closedOutputStream) {
            close();
        }
    }

    /**
     * Unblock other threads that are currently waiting on accept, simply by connecting to the socket.
     */
    private void unblockAccepts() {
        while (pendingAccepts.get() > 0) {
            try {
                FileDescriptor tmpFd = new FileDescriptor();

                try {
                    NativeUnixSocket.connect(socketAddress.getBytes(), tmpFd, inode);
                } catch (IOException e) {
                    // there's nothing we can do to unlock these accepts
                    return;
                }
                try {
                    NativeUnixSocket.shutdown(tmpFd, SHUT_RD_WR);
                } catch (Exception e) {
                    // ignore
                }
                try {
                    NativeUnixSocket.close(tmpFd);
                } catch (Exception e) {
                    // ignore
                }
            } catch (Exception e) {
                // ignore
            }
        }
    }

    @Override
    protected final synchronized void close() throws IOException {
        boolean wasBound = bound;
        bound = false;

        FileDescriptor fdesc = validFd();
        if (fdesc != null) {
            NativeUnixSocket.shutdown(fdesc, SHUT_RD_WR);

            closed = true;
            if (wasBound && socketAddress != null && socketAddress.getBytes() != null && inode >= 0) {
                unblockAccepts();
            }

            NativeUnixSocket.close(fdesc);
        }
        closed = true;
    }

    @Override
    @SuppressWarnings("hiding")
    protected void connect(String host, int port) throws IOException {
        throw new SocketException("Cannot bind to this type of address: " + InetAddress.class);
    }

    @Override
    @SuppressWarnings("hiding")
    protected void connect(InetAddress address, int port) throws IOException {
        throw new SocketException("Cannot bind to this type of address: " + InetAddress.class);
    }

    @Override
    protected void connect(SocketAddress addr, int connectTimeout) throws IOException {
        if (!(addr instanceof AFUNIXSocketAddress)) {
            throw new SocketException("Cannot bind to this type of address: " + addr.getClass());
        }
        this.socketAddress = (AFUNIXSocketAddress) addr;
        NativeUnixSocket.connect(socketAddress.getBytes(), fd, -1);
        validFdOrException();
        this.address = socketAddress.getAddress();
        this.port = socketAddress.getPort();
        this.localport = 0;
        this.connected = true;
    }

    @Override
    protected void create(boolean stream) {
        // N/A
    }

    @Override
    protected InputStream getInputStream() throws IOException {
        if (!connected && !bound) {
            throw new IOException("Not connected/not bound");
        }
        validFdOrException();
        return in;
    }

    @Override
    protected OutputStream getOutputStream() throws IOException {
        if (!connected && !bound) {
            throw new IOException("Not connected/not bound");
        }
        validFdOrException();
        return out;
    }

    @Override
    protected void listen(int originalBacklog) throws IOException {
        int backlog = originalBacklog;
        FileDescriptor fdesc = validFdOrException();
        if (backlog <= 0) {
            backlog = 50;
        }
        NativeUnixSocket.listen(fdesc, backlog);
    }

    @Override
    protected void sendUrgentData(int data) throws IOException {
        FileDescriptor fdesc = validFdOrException();
        NativeUnixSocket.write(AFUNIXSocketImpl.this, fdesc, new byte[]{(byte) (data & 0xFF)}, 0, 1,
                pendingFileDescriptors);
    }

    private FileDescriptor validFdOrException() throws SocketException {
        FileDescriptor fdesc = validFd();
        if (fdesc == null) {
            throw new SocketException("Not open");
        }
        return fdesc;
    }

    private synchronized FileDescriptor validFd() {
        if (closed) {
            return null;
        }
        FileDescriptor descriptor = this.fd;
        if (descriptor != null && descriptor.valid()) {
            return descriptor;
        }
        return null;
    }

    @Override
    public String toString() {
        return super.toString() + "[fd=" + fd + "; addr=" + this.socketAddress + "; connected="
                + connected + "; bound=" + bound + "]";
    }

    @Override
    public Object getOption(int optID) throws SocketException {
        if (optID == SocketOptions.SO_REUSEADDR) {
            return reuseAddr;
        }

        FileDescriptor fdesc = validFdOrException();
        try {
            switch (optID) {
                case SocketOptions.SO_KEEPALIVE:
                case SocketOptions.TCP_NODELAY:
                    return NativeUnixSocket.getSocketOptionInt(fdesc, optID) != 0;
                case SocketOptions.SO_TIMEOUT:
                    return Math.max(timeout, Math.max(NativeUnixSocket.getSocketOptionInt(fdesc, 0x1005),
                            NativeUnixSocket.getSocketOptionInt(fdesc, 0x1006)));
                case SocketOptions.SO_LINGER:
                case SocketOptions.SO_RCVBUF:
                case SocketOptions.SO_SNDBUF:
                    return NativeUnixSocket.getSocketOptionInt(fdesc, optID);
                default:
                    throw new SocketException("Unsupported option: " + optID);
            }
        } catch (final SocketException e) {
            throw e;
        } catch (final Exception e) {
            throw (SocketException) new SocketException("Error while getting option").initCause(e);
        }
    }

    @Override
    public void setOption(int optID, Object value) throws SocketException {
        if (optID == SocketOptions.SO_REUSEADDR) {
            reuseAddr = expectBoolean(value) != 0;
            return;
        }

        FileDescriptor fdesc = validFdOrException();
        try {
            switch (optID) {
                case SocketOptions.SO_LINGER:

                    if (value instanceof Boolean) {
                        final boolean b = (Boolean) value;
                        if (b) {
                            throw new SocketException("Only accepting Boolean.FALSE here");
                        }
                        NativeUnixSocket.setSocketOptionInt(fdesc, optID, -1);
                        return;
                    }
                    NativeUnixSocket.setSocketOptionInt(fdesc, optID, expectInteger(value));
                    return;
                case SocketOptions.SO_TIMEOUT:
                    this.timeout = expectInteger(value);
                    NativeUnixSocket.setSocketOptionInt(fdesc, 0x1005, timeout);
                    NativeUnixSocket.setSocketOptionInt(fdesc, 0x1006, timeout);
                    return;
                case SocketOptions.SO_RCVBUF:
                case SocketOptions.SO_SNDBUF:
                    NativeUnixSocket.setSocketOptionInt(fdesc, optID, expectInteger(value));
                    return;
                case SocketOptions.SO_KEEPALIVE:
                case SocketOptions.TCP_NODELAY:
                    NativeUnixSocket.setSocketOptionInt(fdesc, optID, expectBoolean(value));
                    return;
                default:
                    throw new SocketException("Unsupported option: " + optID);
            }
        } catch (final SocketException e) {
            throw e;
        } catch (final Exception e) {
            throw (SocketException) new SocketException("Error while setting option").initCause(e);
        }
    }

    @Override
    protected void shutdownInput() throws IOException {
        FileDescriptor fdesc = validFd();
        if (fdesc != null) {
            NativeUnixSocket.shutdown(fdesc, SHUT_RD);
        }
    }

    @Override
    protected void shutdownOutput() throws IOException {
        FileDescriptor fdesc = validFd();
        if (fdesc != null) {
            NativeUnixSocket.shutdown(fdesc, SHUT_WR);
        }
    }

    AFUNIXSocketCredentials getPeerCredentials() throws IOException {
        return NativeUnixSocket.peerCredentials(fd, new AFUNIXSocketCredentials());
    }

    int getAncillaryReceiveBufferSize() {
        return ancillaryReceiveBuffer.capacity();
    }

    void setAncillaryReceiveBufferSize(int size) {
        this.ancillaryReceiveBuffer = ByteBuffer.allocateDirect(size);
    }

    public final void ensureAncillaryReceiveBufferSize(int minSize) {
        if (minSize <= 0) {
            return;
        }
        if (ancillaryReceiveBuffer.capacity() < minSize) {
            setAncillaryReceiveBufferSize(minSize);
        }
    }

    public final FileDescriptor[] getReceivedFileDescriptors() {
        if (receivedFileDescriptors.isEmpty()) {
            return null;
        }
        List<FileDescriptor[]> copy = new ArrayList<>(receivedFileDescriptors);
        if (copy.isEmpty()) {
            return null;
        }
        receivedFileDescriptors.removeAll(copy);
        int count = 0;
        for (FileDescriptor[] fds : copy) {
            count += fds.length;
        }
        if (count == 0) {
            return null;
        }
        FileDescriptor[] oneArray = new FileDescriptor[count];
        int offset = 0;
        for (FileDescriptor[] fds : copy) {
            System.arraycopy(fds, 0, oneArray, offset, fds.length);
            offset += fds.length;
        }
        return oneArray;
    }

    public final void clearReceivedFileDescriptors() {
        receivedFileDescriptors.clear();
    }

    // called from native code
    final void receiveFileDescriptors(int[] fds) throws IOException {
        if (fds == null || fds.length == 0) {
            return;
        }
        final int fdsLength = fds.length;
        FileDescriptor[] descriptors = new FileDescriptor[fdsLength];
        for (int i = 0; i < fdsLength; i++) {
            FileDescriptor fdesc = new FileDescriptor();
            NativeUnixSocket.initFD(fdesc, fds[i]);
            descriptors[i] = fdesc;

            closeableFileDescriptors.put(fdesc, fds[i]);

            @SuppressWarnings("resource") final Closeable cleanup = new Closeable() {

                @Override
                public void close() {
                    closeableFileDescriptors.remove(fdesc);
                }
            };
            NativeUnixSocket.attachCloseable(fdesc, cleanup);
        }

        this.receivedFileDescriptors.add(descriptors);
    }

    // called from native code, too (but only with null)
    final void setOutboundFileDescriptors(int... fds) {
        this.pendingFileDescriptors = (fds == null || fds.length == 0) ? null : fds;
    }

    public final void setOutboundFileDescriptors(FileDescriptor... fdescs) throws IOException {
        if (fdescs == null || fdescs.length == 0) {
            this.setOutboundFileDescriptors((int[]) null);
        } else {
            final int numFdescs = fdescs.length;
            final int[] fds = new int[numFdescs];
            for (int i = 0; i < numFdescs; i++) {
                FileDescriptor fdesc = fdescs[i];
                fds[i] = NativeUnixSocket.getFD(fdesc);
            }
            this.setOutboundFileDescriptors(fds);
        }
    }

    /**
     * Changes the behavior to be somewhat lenient with respect to the specification.
     * <p>
     * In particular, we ignore calls to {@link Socket#getTcpNoDelay()} and
     * {@link Socket#setTcpNoDelay(boolean)}.
     */
    static final class Lenient extends AFUNIXSocketImpl {
        @Override
        public void setOption(int optID, Object value) throws SocketException {
            try {
                super.setOption(optID, value);
            } catch (SocketException e) {
                if (optID == SocketOptions.TCP_NODELAY) {
                    return;
                }
                throw e;
            }
        }

        @Override
        public Object getOption(int optID) throws SocketException {
            try {
                return super.getOption(optID);
            } catch (SocketException e) {
                switch (optID) {
                    case SocketOptions.TCP_NODELAY:
                    case SocketOptions.SO_KEEPALIVE:
                        return false;
                    default:
                        throw e;
                }
            }
        }
    }

    private class AFUNIXInputStream extends InputStream {
        private volatile boolean streamClosed = false;
        private boolean eofReached = false;

        @Override
        public int read(byte[] buf, int off, int len) throws IOException {
            if (streamClosed) {
                throw new IOException("This InputStream has already been closed.");
            }
            FileDescriptor fdesc = validFdOrException();
            if (len == 0) {
                return 0;
            } else if (off < 0 || len < 0 || (len > buf.length - off)) {
                throw new IndexOutOfBoundsException();
            }

            return NativeUnixSocket.read(AFUNIXSocketImpl.this, fdesc, buf, off, len,
                    ancillaryReceiveBuffer);
        }

        @Override
        public int read() throws IOException {
            if (eofReached) {
                return -1;
            }
            final byte[] buf1 = new byte[1];
            final int numRead = read(buf1, 0, 1);
            if (numRead <= 0) {
                eofReached = true;
                return -1;
            } else {
                return buf1[0] & 0xFF;
            }
        }

        @Override
        public synchronized void close() throws IOException {
            streamClosed = true;
            FileDescriptor fdesc = validFd();
            if (fdesc != null) {
                NativeUnixSocket.shutdown(fdesc, SHUT_RD);
            }

            closedInputStream = true;
            checkClose();
        }

        @Override
        public int available() throws IOException {
            if (streamClosed) {
                throw new IOException("This InputStream has already been closed.");
            }
            FileDescriptor fdesc = validFdOrException();

            return NativeUnixSocket.available(fdesc);
        }
    }

    private class AFUNIXOutputStream extends OutputStream {
        private volatile boolean streamClosed = false;

        @Override
        public void write(int oneByte) throws IOException {
            final byte[] buf1 = {(byte) oneByte};
            write(buf1, 0, 1);
        }

        @Override
        public void write(byte[] buf, int originalOff, int originalLen) throws IOException {
            int off = originalOff;
            int len = originalLen;
            if (streamClosed) {
                throw new SocketException("This OutputStream has already been closed.");
            }
            if (len < 0 || off < 0 || len > buf.length - off) {
                throw new IndexOutOfBoundsException();
            }
            FileDescriptor fdesc = validFdOrException();
            int writtenTotal = 0;

            while (len > 0) {
                if (Thread.interrupted()) {
                    InterruptedIOException ex = new InterruptedIOException("Thread interrupted during write");
                    ex.bytesTransferred = writtenTotal;
                    Thread.currentThread().interrupt();
                    throw ex;
                }

                final int written = NativeUnixSocket.write(AFUNIXSocketImpl.this, fdesc, buf, off, len,
                        pendingFileDescriptors);
                if (written < 0) {
                    throw new IOException("Unspecific error while writing");
                }
                len -= written;
                off += written;
                writtenTotal += written;
            }
        }

        @Override
        public synchronized void close() throws IOException {
            if (streamClosed) {
                return;
            }
            streamClosed = true;
            FileDescriptor fdesc = validFd();
            if (fdesc != null) {
                NativeUnixSocket.shutdown(fdesc, SHUT_WR);
            }
            closedOutputStream = true;
            checkClose();
        }
    }
}

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

import java.io.IOException;
import java.net.Socket;
import java.rmi.server.RemoteServer;
import java.util.Arrays;
import java.util.UUID;

/**
 * AF_UNIX socket credentials.
 */
public final class AFUNIXSocketCredentials {
    /**
     * Special instance, indicating that there is no remote peer, but the referenced object is from
     * the same process.
     */
    public static final AFUNIXSocketCredentials SAME_PROCESS = new AFUNIXSocketCredentials();

    private long pid = -1; // NOPMD -- Set in native code
    private long uid = -1; // NOPMD -- Set in native code
    private long[] gids = null;
    private UUID uuid = null;

    AFUNIXSocketCredentials() {
    }

    /**
     * Returns the {@link AFUNIXSocketCredentials} for the currently active remote session, or
     * {@code null} if it was not possible to retrieve these credentials.
     * <p>
     * NOTE: For now, only RMI remote sessions are supported (RemoteServer sessions during a remote
     * method invocation).
     * <p>
     * If you want to retrieve the peer credentials for an RMI server, see junixsocket-rmi's
     * RemotePeerInfo.
     *
     * @return The credentials, or {@code null} if unable to retrieve.
     */
    @SuppressWarnings("resource")
    public static AFUNIXSocketCredentials remotePeerCredentials() {
        try {
            RemoteServer.getClientHost();
        } catch (Exception e) {
            return null;
        }

        Socket sock = NativeUnixSocket.currentRMISocket();
        if (!(sock instanceof AFUNIXSocket)) {
            return null;
        }
        AFUNIXSocket socket = (AFUNIXSocket) sock;

        try {
            return socket.getPeerCredentials();
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Returns the "pid" (process ID), or {@code -1} if it could not be retrieved.
     *
     * @return The pid, or -1.
     */
    public long getPid() {
        return pid;
    }

    /**
     * Returns the "uid" (user ID), or {@code -1} if it could not be retrieved.
     *
     * @return The uid, or -1.
     */
    public long getUid() {
        return uid;
    }

    /**
     * Returns the primary "gid" (group ID), or {@code -1} if it could not be retrieved.
     *
     * @return The gid, or -1.
     */
    public long getGid() {
        return gids == null ? -1 : gids.length == 0 ? -1 : gids[0];
    }

    /**
     * Returns all "gid" values (group IDs), or {@code null} if they could not be retrieved.
     *
     * @return The gids, or null.
     */
    public long[] getGids() {
        return gids == null ? null : gids.clone();
    }

    void setGids(long[] gids) {
        this.gids = gids.clone();
    }

    /**
     * Returns the process' unique identifier, or {@code null} if no such identifier could be
     * retrieved. Note that all processes run by the same Java runtime may share the same UUID.
     *
     * @return The UUID, or null.
     */
    public UUID getUUID() {
        return uuid;
    }

    void setUUID(String uuidStr) {
        this.uuid = UUID.fromString(uuidStr);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append('[');
        if (this == SAME_PROCESS) {
            sb.append("(same process)]");
            return sb.toString();
        }
        if (pid != -1) {
            sb.append("pid=").append(pid).append(";");
        }
        if (uid != -1) {
            sb.append("uid=").append(uid).append(";");
        }
        if (gids != null) {
            sb.append("gids=").append(Arrays.toString(gids)).append(";");
        }
        if (uuid != null) {
            sb.append("uuid=").append(uuid).append(";");
        }
        if (sb.charAt(sb.length() - 1) == ';') {
            sb.setLength(sb.length() - 1);
        }
        sb.append(']');
        return sb.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(gids);
        result = prime * result + (int) (pid ^ (pid >>> 32));
        result = prime * result + (int) (uid ^ (uid >>> 32));
        result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
        return result;
    }

    @Override
    @SuppressWarnings("all")
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AFUNIXSocketCredentials other = (AFUNIXSocketCredentials) obj;
        if (!Arrays.equals(gids, other.gids))
            return false;
        if (pid != other.pid)
            return false;
        if (uid != other.uid)
            return false;
        if (uuid == null) {
            if (other.uuid != null)
                return false;
        } else if (!uuid.equals(other.uuid))
            return false;
        return true;
    }
}

/*
 * MIT License
 *
 * Copyright (c) 2018 - 2025 CDAGaming (cstack2011@yahoo.com)
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

package com.gitlab.cdagaming.craftpresence.utils.server;

/**
 * A Local Instance of Server Data, from future Minecraft Versions
 *
 * @author CDAGaming
 */
public class ServerNBTStorage {
    /**
     * The IP Address associated with this Server
     */
    public final String serverIP;

    /**
     * The Port Number associated with this Server
     */
    public final int serverPort;

    /**
     * The Name associated with this Server
     */
    public final String serverName = null;

    /**
     * The MOTD associated with this Server
     */
    public final String serverMOTD = null;

    /**
     * Initializes Server Data for this Server
     *
     * @param serverIP   The IP for this Server
     * @param serverPort The Port Number for this Server
     */
    public ServerNBTStorage(final String serverIP, final int serverPort) {
        this.serverIP = serverIP;
        this.serverPort = serverPort;
    }

    /**
     * Initializes Server Data for this Server
     *
     * @param serverIP   The IP for this Server
     * @param serverPort The Port Number for this Server, as a String
     */
    public ServerNBTStorage(final String serverIP, final String serverPort) {
        this(serverIP, Integer.parseInt(serverPort));
    }
}

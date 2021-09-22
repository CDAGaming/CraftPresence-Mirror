/*
 * MIT License
 *
 * Copyright (c) 2018 - 2021 CDAGaming (cstack2011@yahoo.com)
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

package com.gitlab.cdagaming.craftpresence.utils.discord.rpc.entities.pipe;

import com.gitlab.cdagaming.craftpresence.ModUtils;
import com.gitlab.cdagaming.craftpresence.impl.WinRegistry;
import com.gitlab.cdagaming.craftpresence.utils.discord.rpc.IPCClient;
import com.gitlab.cdagaming.craftpresence.utils.discord.rpc.entities.Callback;
import com.gitlab.cdagaming.craftpresence.utils.discord.rpc.entities.Packet;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;

public class WindowsPipe extends Pipe {
    private static final Float javaSpec = Float.parseFloat(System.getProperty("java.specification.version"));
    private final int targetKey = WinRegistry.HKEY_CURRENT_USER;
    private final long targetLongKey = targetKey;
    public RandomAccessFile file;

    WindowsPipe(IPCClient ipcClient, HashMap<String, Callback> callbacks, String location) {
        super(ipcClient, callbacks);
        try {
            this.file = new RandomAccessFile(location, "rw");
        } catch (FileNotFoundException e) {
            this.file = null;
        }
    }

    @Override
    public void write(byte[] b) throws IOException {
        file.write(b);
    }

    @Override
    @SuppressWarnings("BusyWait")
    public Packet read() throws IOException, JsonParseException {
        while ((status == PipeStatus.CONNECTED || status == PipeStatus.CLOSING) && file.length() == 0) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException ignored) {
            }
        }

        if (status == PipeStatus.DISCONNECTED)
            throw new IOException("Disconnected!");

        if (status == PipeStatus.CLOSED)
            return new Packet(Packet.OpCode.CLOSE, null, ipcClient.getEncoding());

        Packet.OpCode op = Packet.OpCode.values()[Integer.reverseBytes(file.readInt())];
        int len = Integer.reverseBytes(file.readInt());
        byte[] d = new byte[len];

        file.readFully(d);

        return receive(op, d);
    }

    @Override
    public void close() throws IOException {
        if (ipcClient.isDebugMode()) {
            ModUtils.LOG.debugInfo("Closing IPC pipe...");
        }

        status = PipeStatus.CLOSING;
        send(Packet.OpCode.CLOSE, new JsonObject(), null);
        status = PipeStatus.CLOSED;
        file.close();
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public void registerApp(String applicationId, String command) {
        String javaLibraryPath = System.getProperty("java.home");
        File javaExeFile = new File(javaLibraryPath.split(";")[0] + "/bin/java.exe");
        File javawExeFile = new File(javaLibraryPath.split(";")[0] + "/bin/javaw.exe");
        String javaExePath = javaExeFile.exists() ? javaExeFile.getAbsolutePath() : javawExeFile.exists() ? javawExeFile.getAbsolutePath() : null;

        if (javaExePath == null)
            throw new RuntimeException("Unable to find java path");

        String openCommand;

        if (command != null)
            openCommand = command;
        else
            openCommand = javaExePath;

        String protocolName = "discord-" + applicationId;
        String protocolDescription = "URL:Run game " + applicationId + " protocol";
        String keyName = "Software\\Classes\\" + protocolName;
        String iconKeyName = keyName + "\\DefaultIcon";
        String commandKeyName = keyName + "\\DefaultIcon";

        try {
            if (javaSpec >= 11) {
                WinRegistry.createKey(targetLongKey, keyName);
                WinRegistry.writeStringValue(targetLongKey, keyName, "", protocolDescription);
                WinRegistry.writeStringValue(targetLongKey, keyName, "URL Protocol", "\0");

                WinRegistry.createKey(targetLongKey, iconKeyName);
                WinRegistry.writeStringValue(targetLongKey, iconKeyName, "", javaExePath);

                WinRegistry.createKey(targetLongKey, commandKeyName);
                WinRegistry.writeStringValue(targetLongKey, commandKeyName, "", openCommand);
            } else {
                WinRegistry.createKey(targetKey, keyName);
                WinRegistry.writeStringValue(targetKey, keyName, "", protocolDescription);
                WinRegistry.writeStringValue(targetKey, keyName, "URL Protocol", "\0");

                WinRegistry.createKey(targetKey, iconKeyName);
                WinRegistry.writeStringValue(targetKey, iconKeyName, "", javaExePath);

                WinRegistry.createKey(targetKey, commandKeyName);
                WinRegistry.writeStringValue(targetKey, commandKeyName, "", openCommand);
            }
        } catch (Exception | Error ex) {
            throw new RuntimeException("Unable to modify Discord registry keys", ex);
        }
    }

    @Override
    public void registerSteamGame(String applicationId, String steamId) {
        try {
            String steamPath;
            if (javaSpec >= 11) {
                steamPath = WinRegistry.readString(targetLongKey, "Software\\\\Valve\\\\Steam", "SteamExe");
            } else {
                steamPath = WinRegistry.readString(targetKey, "Software\\\\Valve\\\\Steam", "SteamExe");
            }
            if (steamPath == null)
                throw new RuntimeException("Steam exe path not found");

            steamPath = steamPath.replaceAll("/", "\\");

            String command = "\"" + steamPath + "\" steam://rungameid/" + steamId;

            this.registerApp(applicationId, command);
        } catch (Exception ex) {
            throw new RuntimeException("Unable to register Steam game", ex);
        }
    }

}

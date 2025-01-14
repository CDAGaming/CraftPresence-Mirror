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

import net.minecraft.src.ChatAllowedCharacters;
import net.minecraft.src.Packet;
import net.minecraft.src.ServerNBTStorage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ServerPinger {
    public static void pingServer(ServerNBTStorage par1ServerNBTStorage) throws IOException {
        String[] var3 = getServerAddressInfo(par1ServerNBTStorage);

        String var29 = var3[0];
        int var30 = var3.length > 1 ? parseIntWithDefault(var3[1], 25565) : 25565;
        Socket var31 = null;
        DataInputStream var7 = null;
        DataOutputStream var8 = null;

        try {
            var31 = new Socket();
            var31.setSoTimeout(3000);
            var31.setTcpNoDelay(true);
            var31.setTrafficClass(18);
            var31.connect(new InetSocketAddress(var29, var30), 3000);
            var7 = new DataInputStream(var31.getInputStream());
            var8 = new DataOutputStream(var31.getOutputStream());
            var8.write(254);
            if (var7.read() != 255) {
                throw new IOException("Bad message");
            }

            String var9 = Packet.readString(var7, 256);
            char[] var10 = var9.toCharArray();

            int var11;
            for (var11 = 0; var11 < var10.length; ++var11) {
                if (var10[var11] != 167 && ChatAllowedCharacters.allowedCharacters.indexOf(var10[var11]) < 0) {
                    var10[var11] = '?';
                }
            }

            var9 = new String(var10);
            var3 = var9.split("§");
            var9 = var3[0];
            var11 = -1;
            int var12 = -1;

            try {
                var11 = Integer.parseInt(var3[1]);
                var12 = Integer.parseInt(var3[2]);
            } catch (Exception var27) {
            }

            par1ServerNBTStorage.motd = "§7" + var9;
            if (var11 >= 0 && var12 > 0) {
                par1ServerNBTStorage.playerCount = "§7" + var11 + "§8/§7" + var12;
            } else {
                par1ServerNBTStorage.playerCount = "§8???";
            }
        } finally {
            try {
                if (var7 != null) {
                    var7.close();
                }
            } catch (Throwable var26) {
            }

            try {
                if (var8 != null) {
                    var8.close();
                }
            } catch (Throwable var25) {
            }

            try {
                if (var31 != null) {
                    var31.close();
                }
            } catch (Throwable var24) {
            }

        }

    }

    private static int parseIntWithDefault(String string, int par2) {
        try {
            return Integer.parseInt(string.trim());
        } catch (Exception var4) {
            return par2;
        }
    }

    public static String[] getServerAddressInfo(final ServerNBTStorage storage) {
        String var2 = storage.host;
        String[] var3 = var2.split(":");
        if (var2.startsWith("[")) {
            int var4 = var2.indexOf("]");
            if (var4 > 0) {
                String var5 = var2.substring(1, var4);
                String var6 = var2.substring(var4 + 1).trim();
                if (var6.startsWith(":") && var6.length() > 0) {
                    var6 = var6.substring(1);
                    var3 = new String[]{var5, var6};
                } else {
                    var3 = new String[]{var5};
                }
            }
        }

        if (var3.length > 2) {
            var3 = new String[]{var2};
        }
        return var3;
    }

    public static String getServerIP(final String[] data) {
        return data[0];
    }

    public static String getServerIP(final ServerNBTStorage par1ServerNBTStorage) {
        return getServerIP(getServerAddressInfo(par1ServerNBTStorage));
    }

    public static int getServerPort(final String[] data) {
        return data.length > 1 ? parseIntWithDefault(data[1], 25565) : 25565;
    }

    public static int getServerPort(final ServerNBTStorage par1ServerNBTStorage) {
        return getServerPort(getServerAddressInfo(par1ServerNBTStorage));
    }

    public static String getServerAddress(final String[] data) {
        return getServerIP(data) + ":" + getServerPort(data);
    }

    public static String getServerAddress(final ServerNBTStorage par1ServerNBTStorage) {
        return getServerAddress(getServerAddressInfo(par1ServerNBTStorage));
    }
}

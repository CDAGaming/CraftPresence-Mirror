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
import net.minecraft.src.ServerAddress;
import net.minecraft.src.ServerData;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ServerPinger {
    public static void pingServer(ServerData arg) throws IOException {
        ServerAddress var2 = ServerAddress.method_1264(arg.serverIP);
        Socket var3 = null;
        DataInputStream var4 = null;
        DataOutputStream var5 = null;

        try {
            var3 = new Socket();
            var3.setSoTimeout(3000);
            var3.setTcpNoDelay(true);
            var3.setTrafficClass(18);
            var3.connect(new InetSocketAddress(var2.getIP(), var2.getPort()), 3000);
            var4 = new DataInputStream(var3.getInputStream());
            var5 = new DataOutputStream(var3.getOutputStream());
            var5.write(254);
            if (var4.read() != 255) {
                throw new IOException("Bad message");
            }

            String var6 = Packet.readString(var4, 256);
            char[] var7 = var6.toCharArray();

            for (int var8 = 0; var8 < var7.length; ++var8) {
                if (var7[var8] != 167 && ChatAllowedCharacters.allowedCharacters.indexOf(var7[var8]) < 0) {
                    var7[var8] = '?';
                }
            }

            var6 = new String(var7);
            String[] var27 = var6.split("§");
            var6 = var27[0];
            int var9 = -1;
            int var10 = -1;

            try {
                var9 = Integer.parseInt(var27[1]);
                var10 = Integer.parseInt(var27[2]);
            } catch (Exception var25) {
            }

            arg.serverMOTD = "§7" + var6;
            if (var9 >= 0 && var10 > 0) {
                arg.populationInfo = "§7" + var9 + "§8/§7" + var10;
            } else {
                arg.populationInfo = "§8???";
            }
        } finally {
            try {
                if (var4 != null) {
                    var4.close();
                }
            } catch (Throwable var24) {
            }

            try {
                if (var5 != null) {
                    var5.close();
                }
            } catch (Throwable var23) {
            }

            try {
                if (var3 != null) {
                    var3.close();
                }
            } catch (Throwable var22) {
            }

        }

    }
}

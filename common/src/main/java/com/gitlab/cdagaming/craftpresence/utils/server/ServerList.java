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

import java.io.File;
import java.util.List;

import com.gitlab.cdagaming.unilib.ModUtils;
import io.github.cdagaming.unicore.utils.StringUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.src.CompressedStreamTools;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NBTTagList;
import net.minecraft.src.ServerNBTStorage;

public class ServerList {
    private final Minecraft mc;
    private final List<ServerNBTStorage> servers = StringUtils.newArrayList();

    public ServerList(Minecraft minecraft) {
        this.mc = minecraft;
        this.loadServerList();
    }

    public void loadServerList() {
        try {
            NBTTagCompound var1 = CompressedStreamTools.func_35622_a(new File(this.mc.mcDataDir, "servers.dat"));
            NBTTagList var2 = var1.getTagList("servers");
            this.servers.clear();

            for(int var3 = 0; var3 < var2.tagCount(); ++var3) {
                this.servers.add(ServerNBTStorage.func_35788_a((NBTTagCompound)var2.tagAt(var3)));
            }
        } catch (Exception var4) {
            var4.printStackTrace();
        }
    }

    public void saveServerList() {
        try {
            NBTTagList var1 = new NBTTagList();

            for(ServerNBTStorage var3 : this.servers) {
                var1.setTag(var3.func_35789_a());
            }

            NBTTagCompound var5 = new NBTTagCompound();
            var5.setTag("servers", var1);
            CompressedStreamTools.func_35621_a(var5, new File(this.mc.mcDataDir, "servers.dat"));
        } catch (Exception var4) {
            var4.printStackTrace();
        }
    }

    public ServerNBTStorage getServerData(int par1) {
        return this.servers.get(par1);
    }

    public void removeServerData(int par1) {
        this.servers.remove(par1);
    }

    public void addServerData(ServerNBTStorage par1ServerData) {
        this.servers.add(par1ServerData);
    }

    public int countServers() {
        return this.servers.size();
    }

    public void swapServers(int i, int par2) {
        ServerNBTStorage var3 = this.getServerData(i);
        this.servers.set(i, this.getServerData(par2));
        this.servers.set(par2, var3);
        this.saveServerList();
    }

    public void setServer(int i, ServerNBTStorage par2ServerData) {
        this.servers.set(i, par2ServerData);
    }

    public static void func_78852_b(ServerNBTStorage serverData) {
        ServerList var1 = new ServerList(ModUtils.getMinecraft());
        var1.loadServerList();

        for(int var2 = 0; var2 < var1.countServers(); ++var2) {
            ServerNBTStorage var3 = var1.getServerData(var2);
            if (var3.field_35795_a.equals(serverData.field_35795_a) && var3.field_35793_b.equals(serverData.field_35793_b)) {
                var1.setServer(var2, serverData);
                break;
            }
        }

        var1.saveServerList();
    }
}
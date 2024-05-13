/*
 * MIT License
 *
 * Copyright (c) 2018 - 2024 CDAGaming (cstack2011@yahoo.com)
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

package com.gitlab.cdagaming.craftpresence.config.category;

import com.gitlab.cdagaming.craftpresence.core.Constants;
import com.gitlab.cdagaming.craftpresence.core.config.Module;
import com.gitlab.cdagaming.craftpresence.core.config.element.ModuleData;
import io.github.cdagaming.unicore.impl.HashMapBuilder;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

public class Server extends Module implements Serializable {
    private static final long serialVersionUID = -3687928791637101400L;
    private static final Server DEFAULT = new Server();
    public String fallbackServerIcon = "default";
    public String fallbackServerName = Constants.TRANSLATOR.translate("craftpresence.defaults.server_messages.server_name");
    public String fallbackServerMotd = Constants.TRANSLATOR.translate("craftpresence.defaults.server_messages.server_motd");
    public Map<String, ModuleData> serverData = new HashMapBuilder<String, ModuleData>()
            .put("default", new ModuleData(
                    Constants.TRANSLATOR.translate("craftpresence.defaults.server_messages.server_messages"),
                    null // Defaults to the Server Name if nothing is supplied
            ))
            .build();

    public Server(final Server other) {
        transferFrom(other);
    }

    public Server() {
        // N/A
    }

    @Override
    public Server getDefaults() {
        return new Server(DEFAULT);
    }

    @Override
    public Server copy() {
        return new Server(this);
    }

    @Override
    public void transferFrom(Module target) {
        if (target instanceof Server && !equals(target)) {
            final Server data = (Server) target;

            fallbackServerIcon = data.fallbackServerIcon;
            fallbackServerName = data.fallbackServerName;
            fallbackServerMotd = data.fallbackServerMotd;
            serverData.clear();
            for (Map.Entry<String, ModuleData> entry : data.serverData.entrySet()) {
                serverData.put(entry.getKey(), new ModuleData(entry.getValue()));
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof Server)) {
            return false;
        }

        final Server other = (Server) obj;

        return Objects.equals(other.fallbackServerIcon, fallbackServerIcon) &&
                Objects.equals(other.fallbackServerName, fallbackServerName) &&
                Objects.equals(other.fallbackServerMotd, fallbackServerMotd) &&
                Objects.equals(other.serverData, serverData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                fallbackServerIcon, fallbackServerName, fallbackServerMotd, serverData
        );
    }
}

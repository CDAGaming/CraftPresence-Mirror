/*
 * MIT License
 *
 * Copyright (c) 2018 - 2023 CDAGaming (cstack2011@yahoo.com)
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

import com.gitlab.cdagaming.craftpresence.ModUtils;
import com.gitlab.cdagaming.craftpresence.config.Module;
import com.gitlab.cdagaming.craftpresence.config.element.ModuleData;
import com.gitlab.cdagaming.craftpresence.impl.HashMapBuilder;
import com.gitlab.cdagaming.craftpresence.utils.StringUtils;

import java.io.Serializable;
import java.util.Map;

public class Server extends Module implements Serializable {
    private static final long serialVersionUID = -3687928791637101400L;
    private static Server DEFAULT;
    public String fallbackServerIcon = "default";
    public String fallbackServerName = ModUtils.TRANSLATOR.translate(true, "craftpresence.defaults.server_messages.server_name");
    public String fallbackServerMotd = ModUtils.TRANSLATOR.translate(true, "craftpresence.defaults.server_messages.server_motd");
    public Map<String, ModuleData> serverData = new HashMapBuilder<String, ModuleData>()
            .put("default", new ModuleData(
                    ModUtils.TRANSLATOR.translate(true, "craftpresence.defaults.server_messages.server_messages"),
                    null // Defaults to the Server Name if nothing is supplied
            ))
            .build();

    @Override
    public Server getDefaults() {
        if (DEFAULT == null) {
            DEFAULT = new Server();
        }
        return copy(DEFAULT, Server.class);
    }

    @Override
    public Object getProperty(final String name) {
        return StringUtils.getField(Server.class, this, name);
    }

    @Override
    public void setProperty(final String name, final Object value) {
        StringUtils.updateField(Server.class, this, value, name);
    }
}

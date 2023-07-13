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
import com.gitlab.cdagaming.craftpresence.core.impl.HashMapBuilder;
import com.gitlab.cdagaming.craftpresence.core.utils.StringUtils;

import java.io.Serializable;
import java.util.Map;

public class Advanced extends Module implements Serializable {
    private static final long serialVersionUID = 6035241954568785784L;
    private static final Advanced DEFAULT = new Advanced();
    public boolean enablePerGui = false;
    public boolean enablePerItem = false;
    public boolean enablePerEntity = false;
    public boolean formatWords = true;
    public boolean debugMode = false;
    public boolean verboseMode = false;
    public int refreshRate = 2;
    public boolean allowPlaceholderPreviews = false;
    public Gui guiSettings = new Gui();
    public Map<String, String> itemMessages = new HashMapBuilder<String, String>()
            .put("default", ModUtils.TRANSLATOR.translate(true, "craftpresence.defaults.advanced.item_messages"))
            .build();
    public Entity entitySettings = new Entity();
    public boolean allowEndpointIcons = true;
    public String serverIconEndpoint = "https://api.mcsrvstat.us/icon/{server.address.short}";
    public String playerSkinEndpoint = "https://mc-heads.net/avatar/{getOrDefault(player.uuid.short, player.name)}";
    public boolean allowDuplicatePackets = false;
    public int maxConnectionAttempts = 10;

    public Advanced(final Advanced other) {
        transferFrom(other);
    }

    public Advanced() {
        // N/A
    }

    @Override
    public Advanced getDefaults() {
        return new Advanced(DEFAULT);
    }

    @Override
    public Advanced copy() {
        return new Advanced(this);
    }

    @Override
    public void transferFrom(Module target) {
        if (target instanceof Advanced && !equals(target)) {
            final Advanced data = (Advanced) target;

            enablePerGui = data.enablePerGui;
            enablePerItem = data.enablePerItem;
            enablePerEntity = data.enablePerEntity;
            formatWords = data.formatWords;
            debugMode = data.debugMode;
            verboseMode = data.verboseMode;
            refreshRate = data.refreshRate;
            allowPlaceholderPreviews = data.allowPlaceholderPreviews;
            guiSettings = new Gui(data.guiSettings);
            itemMessages = data.itemMessages;
            entitySettings = new Entity(data.entitySettings);
            allowEndpointIcons = data.allowEndpointIcons;
            serverIconEndpoint = data.serverIconEndpoint;
            playerSkinEndpoint = data.playerSkinEndpoint;
            allowDuplicatePackets = data.allowDuplicatePackets;
            maxConnectionAttempts = data.maxConnectionAttempts;
        }
    }

    @Override
    public Object getProperty(final String name) {
        return StringUtils.getField(Advanced.class, this, name);
    }

    @Override
    public void setProperty(final String name, final Object value) {
        StringUtils.updateField(Advanced.class, this, value, name);
    }
}

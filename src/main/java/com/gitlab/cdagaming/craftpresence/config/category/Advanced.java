/*
 * MIT License
 *
 * Copyright (c) 2018 - 2022 CDAGaming (cstack2011@yahoo.com)
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
import com.gitlab.cdagaming.craftpresence.impl.Tuple;
import com.gitlab.cdagaming.craftpresence.utils.StringUtils;
import com.google.common.collect.ImmutableMap;

import java.io.Serializable;
import java.util.Map;

public class Advanced extends Module implements Serializable {
    private static final long serialVersionUID = 6035241954568785784L;
    private static Advanced DEFAULT;

    public static Advanced getDefaults() {
        if (DEFAULT == null) {
            DEFAULT = new Advanced();
        }
        return DEFAULT;
    }

    public boolean enableCommands = true;
    public boolean enablePerGui = false;
    public boolean enablePerItem = false;
    public boolean enablePerEntity = false;
    public boolean renderTooltips = true;
    public boolean formatWords = true;
    public boolean debugMode = false;
    public boolean verboseMode = false;
    public int refreshRate = 2;
    public int roundSize = 3;
    public boolean includeExtraGuiClasses = false;
    public boolean allowPlaceholderPreviews = false;
    public boolean allowPlaceholderOperators = true;
    public Map<String, String> guiMessages = ImmutableMap.<String, String>builder()
            .put("default", ModUtils.TRANSLATOR.translate(true, "craftpresence.defaults.advanced.gui_messages"))
            .build();
    public Map<String, String> itemMessages = ImmutableMap.<String, String>builder()
            .put("default", ModUtils.TRANSLATOR.translate(true, "craftpresence.defaults.advanced.item_messages"))
            .build();
    public Map<String, String> entityTargetMessages = ImmutableMap.<String, String>builder()
            .put("default", ModUtils.TRANSLATOR.translate(true, "craftpresence.defaults.advanced.entity_target_messages"))
            .build();
    public Map<String, String> entityRidingMessages = ImmutableMap.<String, String>builder()
            .put("default", ModUtils.TRANSLATOR.translate(true, "craftpresence.defaults.advanced.entity_riding_messages"))
            .build();
    public String playerSkinEndpoint = ModUtils.TRANSLATOR.translate(true, "craftpresence.defaults.advanced.player_skin_endpoint");

    @Override
    public Object getProperty(final String name) {
        return StringUtils.lookupObject(Advanced.class, this, name);
    }

    @Override
    public void setProperty(final String name, final Object value) {
        StringUtils.updateField(Advanced.class, this, new Tuple<>(name, value, null));
    }

    @Override
    public void resetProperty(final String name) {
        setProperty(name, getDefaults().getProperty(name));
    }
}

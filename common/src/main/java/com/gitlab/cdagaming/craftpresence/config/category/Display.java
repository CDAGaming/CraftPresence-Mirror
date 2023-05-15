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
import com.gitlab.cdagaming.craftpresence.config.element.PresenceData;
import com.gitlab.cdagaming.craftpresence.impl.HashMapBuilder;
import com.gitlab.cdagaming.craftpresence.utils.StringUtils;

import java.io.Serializable;
import java.util.Map;

public class Display extends Module implements Serializable {
    private static final long serialVersionUID = -3302764075156017733L;
    private static final Display DEFAULT = new Display();
    public PresenceData presenceData = new PresenceData()
            .setGameState("{getOrDefault(server.message)} {getOrDefault(pack.name)}")
            .setDetails("{getFirst(menu.message, dimension.message)}")
            .setLargeImage("{getFirst(menu.icon, dimension.icon)}",
                    "{getFirst(menu.message, dimension.message)}")
            .setSmallImage("{getFirst(server.icon, pack.icon)}",
                    "{getOrDefault(server.message)} {getOrDefault(pack.name)}")
            .setStartTime("{data.general.time}");
    public Map<String, String> dynamicIcons = new HashMapBuilder<String, String>()
            .put("default", ModUtils.TRANSLATOR.translate("craftpresence.defaults.display.image.url"))
            .build();
    public Map<String, String> dynamicVariables = new HashMapBuilder<String, String>()
            .put("default", ModUtils.TRANSLATOR.translate("craftpresence.defaults.display.button.label"))
            .put("pack", ModUtils.TRANSLATOR.translate("craftpresence.defaults.placeholder.pack"))
            .put("players", ModUtils.TRANSLATOR.translate("craftpresence.defaults.placeholder.players"))
            .put("player_info_out", ModUtils.TRANSLATOR.translate("craftpresence.defaults.placeholder.player_info.out"))
            .put("player_info_in", ModUtils.TRANSLATOR.translate("craftpresence.defaults.placeholder.player_info.in"))
            .put("player_info_coordinate", ModUtils.TRANSLATOR.translate("craftpresence.defaults.placeholder.player_info.coordinate"))
            .put("player_info_health", ModUtils.TRANSLATOR.translate("craftpresence.defaults.placeholder.player_info.health"))
            .put("player_info_items", ModUtils.TRANSLATOR.translate("craftpresence.defaults.placeholder.player_info.items"))
            .put("world_info", ModUtils.TRANSLATOR.translate("craftpresence.defaults.placeholder.world_info"))
            .put("mods", ModUtils.TRANSLATOR.translate("craftpresence.defaults.placeholder.mods"))
            .build();

    public Display(final Display other) {
        transferFrom(other);
    }

    public Display() {
        // N/A
    }

    @Override
    public Display getDefaults() {
        final Display results = new Display(DEFAULT);
        // Hotfix: Preserve `dynamicIcons` as a cache setting
        results.dynamicIcons = dynamicIcons;
        return results;
    }

    @Override
    public Display copy() {
        return new Display(this);
    }

    @Override
    public void transferFrom(Module target) {
        if (target instanceof Display && !equals(target)) {
            final Display data = (Display) target;

            presenceData = new PresenceData(data.presenceData);
            dynamicIcons = data.dynamicIcons;
            dynamicVariables = data.dynamicVariables;
        }
    }

    @Override
    public Object getProperty(final String name) {
        return StringUtils.getField(Display.class, this, name);
    }

    @Override
    public void setProperty(final String name, final Object value) {
        StringUtils.updateField(Display.class, this, value, name);
    }
}

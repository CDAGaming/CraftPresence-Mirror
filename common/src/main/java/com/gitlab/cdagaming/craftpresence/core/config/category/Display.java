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

package com.gitlab.cdagaming.craftpresence.core.config.category;

import com.gitlab.cdagaming.craftpresence.core.Constants;
import com.gitlab.cdagaming.craftpresence.core.config.Module;
import com.gitlab.cdagaming.craftpresence.core.config.element.PresenceData;
import io.github.cdagaming.unicore.impl.HashMapBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

public class Display extends Module implements Serializable {
    @Serial
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
            .put("default", Constants.TRANSLATOR.translate("craftpresence.defaults.display.image.url"))
            .build();
    public Map<String, String> dynamicVariables = new HashMapBuilder<String, String>()
            .put("default", Constants.TRANSLATOR.translate("craftpresence.defaults.display.button.label"))
            .put("pack", Constants.TRANSLATOR.translate("craftpresence.defaults.placeholder.pack"))
            .put("players", Constants.TRANSLATOR.translate("craftpresence.defaults.placeholder.players"))
            .put("player_info_out", Constants.TRANSLATOR.translate("craftpresence.defaults.placeholder.player_info.out"))
            .put("player_info_in", Constants.TRANSLATOR.translate("craftpresence.defaults.placeholder.player_info.in"))
            .put("player_info_coordinate", Constants.TRANSLATOR.translate("craftpresence.defaults.placeholder.player_info.coordinate"))
            .put("player_info_health", Constants.TRANSLATOR.translate("craftpresence.defaults.placeholder.player_info.health"))
            .put("player_info_items", Constants.TRANSLATOR.translate("craftpresence.defaults.placeholder.player_info.items"))
            .put("world_info", Constants.TRANSLATOR.translate("craftpresence.defaults.placeholder.world_info"))
            .put("mods", Constants.TRANSLATOR.translate("craftpresence.defaults.placeholder.mods"))
            .build();

    public Display(final Display other) {
        transferFrom(other);
    }

    public Display() {
        // N/A
    }

    @Override
    public Display getDefaults() {
        return new Display(DEFAULT);
    }

    @Override
    public Display copy() {
        return new Display(this);
    }

    @Override
    public void transferFrom(Module target) {
        if (target instanceof Display data && !equals(target)) {
            presenceData = new PresenceData(data.presenceData);
            dynamicIcons.clear();
            dynamicIcons.putAll(data.dynamicIcons);
            dynamicVariables.clear();
            dynamicVariables.putAll(data.dynamicVariables);
        }
    }

    @Override
    public Object getProperty(String name) {
        return switch (name) {
            case "presenceData" -> presenceData;
            case "dynamicIcons" -> dynamicIcons;
            case "dynamicVariables" -> dynamicVariables;
            default -> null;
        };
    }

    @Override
    public void setProperty(String name, Object value) {
        try {
            switch (name) {
                case "presenceData":
                    presenceData = (PresenceData) value;
                    break;
                case "dynamicIcons":
                    dynamicIcons = (Map<String, String>) value;
                    break;
                case "dynamicVariables":
                    dynamicVariables = (Map<String, String>) value;
                    break;
                default:
                    break;
            }
        } catch (Throwable ex) {
            printException(ex);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof Display other)) {
            return false;
        }

        return Objects.equals(other.presenceData, presenceData) &&
                Objects.equals(other.dynamicIcons, dynamicIcons) &&
                Objects.equals(other.dynamicVariables, dynamicVariables);
    }

    @Override
    public int hashCode() {
        return Objects.hash(presenceData, dynamicIcons, dynamicVariables);
    }
}

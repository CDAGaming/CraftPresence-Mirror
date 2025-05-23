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

package com.gitlab.cdagaming.craftpresence.core.config.category;

import com.gitlab.cdagaming.craftpresence.core.Constants;
import com.gitlab.cdagaming.craftpresence.core.config.Module;
import com.gitlab.cdagaming.craftpresence.core.config.element.ModuleData;
import io.github.cdagaming.unicore.impl.HashMapBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

public class Gui extends Module implements Serializable {
    @Serial
    private static final long serialVersionUID = -5871047759131139250L;
    private static final Gui DEFAULT = new Gui();
    public String fallbackGuiIcon = "unknown";
    public Map<String, ModuleData> guiData = new HashMapBuilder<String, ModuleData>()
            .put("default", new ModuleData(
                    Constants.TRANSLATOR.translate("craftpresence.defaults.advanced.gui_messages"),
                    null // Defaults to the Gui Screen Name if nothing is supplied
            ))
            .build();

    public Gui(final Gui other) {
        transferFrom(other);
    }

    public Gui() {
        // N/A
    }

    public void appendReplayData() {
        if (Constants.hasReplayMod()) {
            guiData.put("GuiReplayViewer", new ModuleData(
                    Constants.TRANSLATOR.translate("craftpresence.defaults.integrations.replaymod.viewer"),
                    null
            ));
            guiData.put("GuiReplayOverlay", new ModuleData(
                    Constants.TRANSLATOR.translate("craftpresence.defaults.integrations.replaymod.editor"),
                    null
            ));
            guiData.put("GuiVideoRenderer", new ModuleData(
                    Constants.TRANSLATOR.translate("craftpresence.defaults.integrations.replaymod.renderer"),
                    null
            ));
        }
    }

    @Override
    public Gui getDefaults() {
        return new Gui(DEFAULT);
    }

    @Override
    public Gui copy() {
        return new Gui(this);
    }

    @Override
    public void transferFrom(Module target) {
        if (target instanceof Gui data && !equals(target)) {
            fallbackGuiIcon = data.fallbackGuiIcon;
            guiData.clear();
            for (Map.Entry<String, ModuleData> entry : data.guiData.entrySet()) {
                guiData.put(entry.getKey(), new ModuleData(entry.getValue()));
            }
        }
    }

    @Override
    public Object getProperty(String name) {
        return switch (name) {
            case "fallbackGuiIcon" -> fallbackGuiIcon;
            case "guiData" -> guiData;
            default -> null;
        };
    }

    @Override
    public void setProperty(String name, Object value) {
        try {
            switch (name) {
                case "fallbackGuiIcon":
                    fallbackGuiIcon = (String) value;
                    break;
                case "guiData":
                    guiData = (Map<String, ModuleData>) value;
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

        if (!(obj instanceof Gui other)) {
            return false;
        }

        return Objects.equals(other.fallbackGuiIcon, fallbackGuiIcon) &&
                Objects.equals(other.guiData, guiData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fallbackGuiIcon, guiData);
    }
}

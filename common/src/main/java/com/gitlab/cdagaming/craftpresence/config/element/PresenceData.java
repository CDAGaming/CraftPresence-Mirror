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

package com.gitlab.cdagaming.craftpresence.config.element;

import com.gitlab.cdagaming.craftpresence.ModUtils;
import com.gitlab.cdagaming.craftpresence.config.Module;
import com.gitlab.cdagaming.craftpresence.utils.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PresenceData extends Module implements Serializable {
    private static final long serialVersionUID = -7560029890988753870L;
    private static PresenceData DEFAULT;

    public boolean enabled = true;
    public boolean useAsMain = false;
    public String details = "";
    public String gameState = "";
    public String largeImageKey = "";
    public String largeImageText = "";
    public String smallImageKey = "";
    public String smallImageText = "";
    public String startTimestamp = "";
    public String endTimestamp = "";
    public Map<String, Button> buttons = new HashMap<String, Button>() {
        private static final long serialVersionUID = -1738414795267027009L;

        {
            put("default", new Button(
                    ModUtils.TRANSLATOR.translate("craftpresence.defaults.display.button.label"),
                    ModUtils.TRANSLATOR.translate("craftpresence.defaults.display.button.url")
            ));
        }
    };

    public PresenceData(final PresenceData other) {
        if (other != null) {
            enabled = other.enabled;
            useAsMain = other.useAsMain;
            setDetails(other.details);
            setGameState(other.gameState);
            setLargeImage(other.largeImageKey, other.largeImageText);
            setSmallImage(other.smallImageKey, other.smallImageText);
            setTimes(other.startTimestamp, other.endTimestamp);
            for (Map.Entry<String, Button> data : other.buttons.entrySet()) {
                addButton(data.getKey(), data.getValue());
            }
        }
    }

    public PresenceData() {
        // N/A
    }

    @Override
    public PresenceData getDefaults() {
        if (DEFAULT == null) {
            DEFAULT = new PresenceData();
        }
        return new PresenceData(DEFAULT);
    }

    public PresenceData setDetails(final String details) {
        this.details = details;
        return this;
    }

    public PresenceData setGameState(final String gameState) {
        this.gameState = gameState;
        return this;
    }

    public PresenceData setLargeImage(final String imageKey, final String imageText) {
        this.largeImageKey = imageKey;
        this.largeImageText = imageText;
        return this;
    }

    public PresenceData setSmallImage(final String imageKey, final String imageText) {
        this.smallImageKey = imageKey;
        this.smallImageText = imageText;
        return this;
    }

    public PresenceData setTimes(final String startTimestamp, final String endTimestamp) {
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
        return this;
    }

    public PresenceData setStartTime(final String timestamp) {
        return setTimes(timestamp, this.endTimestamp);
    }

    public PresenceData setEndTime(final String timestamp) {
        return setTimes(this.startTimestamp, timestamp);
    }

    public PresenceData addButton(final String name, final Button button) {
        this.buttons.put(name, button);
        return this;
    }

    public PresenceData removeButton(final String name) {
        this.buttons.remove(name);
        return this;
    }

    @Override
    public Object getProperty(final String name) {
        return StringUtils.getField(PresenceData.class, this, name);
    }

    @Override
    public void setProperty(final String name, final Object value) {
        StringUtils.updateField(PresenceData.class, this, Pair.of(name, value));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof PresenceData)) {
            return false;
        }

        PresenceData p = (PresenceData) obj;
        boolean areButtonsEqual = buttons.size() == p.buttons.size();
        if (areButtonsEqual) {
            for (Map.Entry<String, Button> entry : buttons.entrySet()) {
                final String key = entry.getKey();
                if (!p.buttons.containsKey(key) || !p.buttons.get(key).equals(entry.getValue())) {
                    areButtonsEqual = false;
                    break;
                }
            }
        }

        return Objects.equals(p.details, details) &&
                Objects.equals(p.gameState, gameState) &&
                Objects.equals(p.largeImageKey, largeImageKey) &&
                Objects.equals(p.largeImageText, largeImageText) &&
                Objects.equals(p.smallImageKey, smallImageKey) &&
                Objects.equals(p.startTimestamp, startTimestamp) &&
                Objects.equals(p.endTimestamp, endTimestamp) &&
                areButtonsEqual;
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + (details != null ? details.hashCode() : 0);
        result = 31 * result + (gameState != null ? gameState.hashCode() : 0);
        result = 31 * result + (largeImageKey != null ? largeImageKey.hashCode() : 0);
        result = 31 * result + (largeImageText != null ? largeImageText.hashCode() : 0);
        result = 31 * result + (smallImageKey != null ? smallImageKey.hashCode() : 0);
        result = 31 * result + (startTimestamp != null ? startTimestamp.hashCode() : 0);
        result = 31 * result + (endTimestamp != null ? endTimestamp.hashCode() : 0);
        result = 31 * result + (buttons != null ? buttons.hashCode() : 0);
        return result;
    }
}

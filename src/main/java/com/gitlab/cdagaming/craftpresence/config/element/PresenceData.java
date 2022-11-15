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

package com.gitlab.cdagaming.craftpresence.config.element;

import com.gitlab.cdagaming.craftpresence.ModUtils;
import com.gitlab.cdagaming.craftpresence.config.Module;
import com.gitlab.cdagaming.craftpresence.impl.Tuple;
import com.gitlab.cdagaming.craftpresence.utils.StringUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

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
    public Map<String, Button> buttons = new HashMap<String, Button>() {
        private static final long serialVersionUID = -1738414795267027009L;

        {
            put("default", new Button(
                    ModUtils.TRANSLATOR.translate("craftpresence.defaults.display.button.label"),
                    ModUtils.TRANSLATOR.translate("craftpresence.defaults.display.button.url")
            ));
        }
    };

    public PresenceData(PresenceData other) {
        if (other != null) {
            enabled = other.enabled;
            useAsMain = other.useAsMain;
            setDetails(other.details);
            setGameState(other.gameState);
            setLargeImage(other.largeImageKey, other.largeImageText);
            setSmallImage(other.smallImageKey, other.smallImageText);
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
        return DEFAULT;
    }

    public PresenceData setDetails(String details) {
        this.details = details;
        return this;
    }

    public PresenceData setGameState(String gameState) {
        this.gameState = gameState;
        return this;
    }

    public PresenceData setLargeImage(String imageKey, String imageText) {
        this.largeImageKey = imageKey;
        this.largeImageText = imageText;
        return this;
    }

    public PresenceData setSmallImage(String imageKey, String imageText) {
        this.smallImageKey = imageKey;
        this.smallImageText = imageText;
        return this;
    }

    public PresenceData addButton(String name, Button button) {
        this.buttons.put(name, button);
        return this;
    }

    public PresenceData removeButton(String name) {
        this.buttons.remove(name);
        return this;
    }

    @Override
    public Object getProperty(final String name) {
        return StringUtils.lookupObject(PresenceData.class, this, name);
    }

    @Override
    public void setProperty(final String name, final Object value) {
        StringUtils.updateField(PresenceData.class, this, new Tuple<>(name, value, null));
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
            for (String key : buttons.keySet()) {
                if (!p.buttons.containsKey(key) || !p.buttons.get(key).equals(buttons.get(key))) {
                    areButtonsEqual = false;
                    break;
                }
            }
        }

        return ((p.details == null && details == null) || (p.details != null && p.details.equals(details))) &&
                ((p.gameState == null && gameState == null) || (p.gameState != null && p.gameState.equals(gameState))) &&
                ((p.largeImageKey == null && largeImageKey == null) || (p.largeImageKey != null && p.largeImageKey.equals(largeImageKey))) &&
                ((p.largeImageText == null && largeImageText == null) || (p.largeImageText != null && p.largeImageText.equals(largeImageText))) &&
                ((p.smallImageKey == null && smallImageKey == null) || (p.smallImageKey != null && p.smallImageKey.equals(smallImageKey))) &&
                ((p.smallImageText == null && smallImageText == null) || (p.smallImageText != null && p.smallImageText.equals(smallImageText))) &&
                areButtonsEqual;
    }
}

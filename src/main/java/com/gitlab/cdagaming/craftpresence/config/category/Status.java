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
import com.gitlab.cdagaming.craftpresence.config.element.ModuleData;
import com.gitlab.cdagaming.craftpresence.impl.Tuple;
import com.gitlab.cdagaming.craftpresence.utils.StringUtils;

import java.io.Serializable;

public class Status extends Module implements Serializable {
    private static final long serialVersionUID = 3055410101315942491L;
    private static Status DEFAULT;
    public ModuleData mainMenuData = new ModuleData(
            ModUtils.TRANSLATOR.translate(true, "craftpresence.defaults.state.main_menu"),
            null
    );
    public ModuleData loadingData = new ModuleData(
            ModUtils.TRANSLATOR.translate(true, "craftpresence.defaults.state.loading"),
            null
    );
    public ModuleData lanData = new ModuleData(
            ModUtils.TRANSLATOR.translate(true, "craftpresence.defaults.state.lan"),
            null
    );
    public ModuleData singleplayerData = new ModuleData(
            ModUtils.TRANSLATOR.translate(true, "craftpresence.defaults.state.single_player"),
            null
    );
    public String packPlaceholderMessage = ModUtils.TRANSLATOR.translate(true, "craftpresence.defaults.placeholder.pack");
    public String outerPlayerPlaceholderMessage = ModUtils.TRANSLATOR.translate(true, "craftpresence.defaults.placeholder.player_info.out");
    public String innerPlayerPlaceholderMessage = ModUtils.TRANSLATOR.translate(true, "craftpresence.defaults.placeholder.player_info.in");
    public String playerCoordinatePlaceholderMessage = ModUtils.TRANSLATOR.translate(true, "craftpresence.defaults.placeholder.player_info.coordinate");
    public String playerHealthPlaceholderMessage = ModUtils.TRANSLATOR.translate(true, "craftpresence.defaults.placeholder.player_info.health");
    public String playerAmountPlaceholderMessage = ModUtils.TRANSLATOR.translate(true, "craftpresence.defaults.placeholder.players");
    public String playerItemsPlaceholderMessage = ModUtils.TRANSLATOR.translate(true, "craftpresence.defaults.placeholder.player_info.items");
    public String worldPlaceholderMessage = ModUtils.TRANSLATOR.translate(true, "craftpresence.defaults.placeholder.world_info");
    public String modsPlaceholderMessage = ModUtils.TRANSLATOR.translate(true, "craftpresence.defaults.placeholder.mods");

    @Override
    public Status getDefaults() {
        if (DEFAULT == null) {
            DEFAULT = new Status();
        }
        return DEFAULT;
    }

    @Override
    public Object getProperty(final String name) {
        return StringUtils.lookupObject(Status.class, this, name);
    }

    @Override
    public void setProperty(final String name, final Object value) {
        StringUtils.updateField(Status.class, this, new Tuple<>(name, value, null));
    }
}

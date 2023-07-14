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

import com.gitlab.cdagaming.craftpresence.core.config.Module;
import com.gitlab.cdagaming.craftpresence.core.impl.discord.PartyPrivacy;
import com.gitlab.cdagaming.craftpresence.core.utils.StringUtils;
import com.jagrosh.discordipc.entities.DiscordBuild;

import java.io.Serializable;

public class General extends Module implements Serializable {
    private static final long serialVersionUID = 1796294737844339558L;
    private static final General DEFAULT = new General();
    public boolean detectATLauncherInstance = true;
    public boolean detectCurseManifest = true;
    public boolean detectMultiMCManifest = true;
    public boolean detectMCUpdaterInstance = true;
    public boolean detectTechnicPack = true;
    public boolean detectBiomeData = true;
    public boolean detectDimensionData = true;
    public boolean detectWorldData = true;
    public String clientId = "450485984333660181";
    public String defaultIcon = "grass";
    public boolean enableJoinRequests = false;
    public int partyPrivacyLevel = PartyPrivacy.Public.ordinal();
    public int preferredClientLevel = DiscordBuild.ANY.ordinal();
    public boolean resetTimeOnInit = false;
    public boolean autoRegister = false;

    public General(final General other) {
        transferFrom(other);
    }

    public General() {
        // N/A
    }

    @Override
    public General getDefaults() {
        return new General(DEFAULT);
    }

    @Override
    public General copy() {
        return new General(this);
    }

    @Override
    public void transferFrom(Module target) {
        if (target instanceof General && !equals(target)) {
            final General data = (General) target;

            detectATLauncherInstance = data.detectATLauncherInstance;
            detectCurseManifest = data.detectCurseManifest;
            detectMultiMCManifest = data.detectMultiMCManifest;
            detectMCUpdaterInstance = data.detectMCUpdaterInstance;
            detectTechnicPack = data.detectTechnicPack;
            detectBiomeData = data.detectBiomeData;
            detectDimensionData = data.detectDimensionData;
            detectWorldData = data.detectWorldData;
            clientId = data.clientId;
            defaultIcon = data.defaultIcon;
            enableJoinRequests = data.enableJoinRequests;
            partyPrivacyLevel = data.partyPrivacyLevel;
            preferredClientLevel = data.preferredClientLevel;
            resetTimeOnInit = data.resetTimeOnInit;
            autoRegister = data.autoRegister;
        }
    }

    @Override
    public Object getProperty(final String name) {
        return StringUtils.getField(General.class, this, name);
    }

    @Override
    public void setProperty(final String name, final Object value) {
        StringUtils.updateField(General.class, this, value, name);
    }
}

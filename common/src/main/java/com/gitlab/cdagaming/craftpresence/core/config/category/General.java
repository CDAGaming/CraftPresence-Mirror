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

import com.gitlab.cdagaming.craftpresence.core.config.Module;
import com.jagrosh.discordipc.entities.DiscordBuild;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class General extends Module implements Serializable {
    @Serial
    private static final long serialVersionUID = 1796294737844339558L;
    private static final General DEFAULT = new General();
    public boolean detectATLauncherInstance = true;
    public boolean detectCurseManifest = true;
    public boolean detectMultiMCManifest = true;
    public boolean detectMCUpdaterInstance = true;
    public boolean detectTechnicPack = true;
    public boolean detectModrinthPack = true;
    public boolean detectBiomeData = true;
    public boolean detectDimensionData = true;
    public boolean detectWorldData = true;
    public String clientId = "450485984333660181";
    public String defaultIcon = "grass";
    public boolean enableJoinRequests = false;
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
        if (target instanceof General data && !equals(target)) {
            detectATLauncherInstance = data.detectATLauncherInstance;
            detectCurseManifest = data.detectCurseManifest;
            detectMultiMCManifest = data.detectMultiMCManifest;
            detectMCUpdaterInstance = data.detectMCUpdaterInstance;
            detectTechnicPack = data.detectTechnicPack;
            detectModrinthPack = data.detectModrinthPack;
            detectBiomeData = data.detectBiomeData;
            detectDimensionData = data.detectDimensionData;
            detectWorldData = data.detectWorldData;
            clientId = data.clientId;
            defaultIcon = data.defaultIcon;
            enableJoinRequests = data.enableJoinRequests;
            preferredClientLevel = data.preferredClientLevel;
            resetTimeOnInit = data.resetTimeOnInit;
            autoRegister = data.autoRegister;
        }
    }

    @Override
    public Object getProperty(String name) {
        return switch (name) {
            case "detectATLauncherInstance" -> detectATLauncherInstance;
            case "detectCurseManifest" -> detectCurseManifest;
            case "detectMultiMCManifest" -> detectMultiMCManifest;
            case "detectMCUpdaterInstance" -> detectMCUpdaterInstance;
            case "detectTechnicPack" -> detectTechnicPack;
            case "detectModrinthPack" -> detectModrinthPack;
            case "detectBiomeData" -> detectBiomeData;
            case "detectDimensionData" -> detectDimensionData;
            case "detectWorldData" -> detectWorldData;
            case "clientId" -> clientId;
            case "defaultIcon" -> defaultIcon;
            case "enableJoinRequests" -> enableJoinRequests;
            case "preferredClientLevel" -> preferredClientLevel;
            case "resetTimeOnInit" -> resetTimeOnInit;
            case "autoRegister" -> autoRegister;
            default -> null;
        };
    }

    @Override
    public void setProperty(String name, Object value) {
        try {
            switch (name) {
                case "detectATLauncherInstance":
                    detectATLauncherInstance = (Boolean) value;
                    break;
                case "detectCurseManifest":
                    detectCurseManifest = (Boolean) value;
                    break;
                case "detectMultiMCManifest":
                    detectMultiMCManifest = (Boolean) value;
                    break;
                case "detectMCUpdaterInstance":
                    detectMCUpdaterInstance = (Boolean) value;
                    break;
                case "detectTechnicPack":
                    detectTechnicPack = (Boolean) value;
                    break;
                case "detectModrinthPack":
                    detectModrinthPack = (Boolean) value;
                    break;
                case "detectBiomeData":
                    detectBiomeData = (Boolean) value;
                    break;
                case "detectDimensionData":
                    detectDimensionData = (Boolean) value;
                    break;
                case "detectWorldData":
                    detectWorldData = (Boolean) value;
                    break;
                case "clientId":
                    clientId = (String) value;
                    break;
                case "defaultIcon":
                    defaultIcon = (String) value;
                    break;
                case "enableJoinRequests":
                    enableJoinRequests = (Boolean) value;
                    break;
                case "preferredClientLevel":
                    preferredClientLevel = (Integer) value;
                    break;
                case "resetTimeOnInit":
                    resetTimeOnInit = (Boolean) value;
                    break;
                case "autoRegister":
                    autoRegister = (Boolean) value;
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

        if (!(obj instanceof General other)) {
            return false;
        }

        return Objects.equals(other.detectATLauncherInstance, detectATLauncherInstance) &&
                Objects.equals(other.detectCurseManifest, detectCurseManifest) &&
                Objects.equals(other.detectMultiMCManifest, detectMultiMCManifest) &&
                Objects.equals(other.detectMCUpdaterInstance, detectMCUpdaterInstance) &&
                Objects.equals(other.detectTechnicPack, detectTechnicPack) &&
                Objects.equals(other.detectModrinthPack, detectModrinthPack) &&
                Objects.equals(other.detectBiomeData, detectBiomeData) &&
                Objects.equals(other.detectDimensionData, detectDimensionData) &&
                Objects.equals(other.detectWorldData, detectWorldData) &&
                Objects.equals(other.clientId, clientId) &&
                Objects.equals(other.defaultIcon, defaultIcon) &&
                Objects.equals(other.enableJoinRequests, enableJoinRequests) &&
                Objects.equals(other.preferredClientLevel, preferredClientLevel) &&
                Objects.equals(other.resetTimeOnInit, resetTimeOnInit) &&
                Objects.equals(other.autoRegister, autoRegister);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                detectATLauncherInstance, detectCurseManifest,
                detectMultiMCManifest, detectMCUpdaterInstance,
                detectTechnicPack, detectModrinthPack,
                detectBiomeData, detectDimensionData, detectWorldData,
                clientId, defaultIcon, enableJoinRequests,
                preferredClientLevel,
                resetTimeOnInit, autoRegister
        );
    }
}

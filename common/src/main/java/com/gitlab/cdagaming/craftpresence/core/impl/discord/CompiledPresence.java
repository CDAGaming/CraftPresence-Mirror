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

package com.gitlab.cdagaming.craftpresence.core.impl.discord;

import com.gitlab.cdagaming.craftpresence.core.Constants;
import com.gitlab.cdagaming.craftpresence.core.integrations.discord.assets.DiscordAsset;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.cdagaming.unicore.utils.StringUtils;
import io.github.cdagaming.unicore.utils.TimeUtils;

import java.util.Map;

/**
 * A record mapping for compiled Rich Presence Data
 *
 * @param details        The Current Message tied to the current action / Details Field of the RPC
 * @param state          The Current Message tied to the Party/Game Status Field of the RPC
 * @param rawLargeImage  The Current Raw Large Image Icon being displayed in the RPC, if any
 * @param rawSmallImage  The Current Raw Small Image Icon being displayed in the RPC, if any
 * @param largeAsset     The Current Large Image Asset being displayed in the RPC, if any
 * @param smallAsset     The Current Small Image Asset being displayed in the RPC, if any
 * @param largeImageKey  The Current Large Image Icon being displayed in the RPC, if any
 * @param smallImageKey  The Current Small Image Icon being displayed in the RPC, if any
 * @param largeImageText The Current Message tied to the Large Image, if any
 * @param smallImageText The Current Message tied to the Small Image, if any
 * @param startTimestamp The Current Starting Unix Timestamp from Epoch, used for Elapsed Time
 * @param endTimestamp   The Current Ending Unix Timestamp from Epoch, used for Time Left (<=0 for null)
 * @param buttons        The current button array tied to the RPC, if any
 */
public record CompiledPresence(
        String details,
        String state,
        String rawLargeImage,
        String rawSmallImage,
        DiscordAsset largeAsset,
        DiscordAsset smallAsset,
        String largeImageKey,
        String smallImageKey,
        String largeImageText,
        String smallImageText,
        long startTimestamp,
        long endTimestamp,
        JsonArray buttons
) {
    /**
     * Calculate the time string, using the start and end timestamp
     *
     * @return the time string, either using "elapsed" or "remaining" time
     */
    public String getTimeString() {
        final boolean isElapsed = endTimestamp <= 0;

        final long seconds = TimeUtils.getDuration(
                TimeUtils.fromEpochMilli(startTimestamp),
                isElapsed ? TimeUtils.getCurrentTime() : TimeUtils.fromEpochMilli(endTimestamp)
        ).getSeconds();

        final long hours = seconds / 3600;
        final long minutes = (seconds % 3600) / 60;
        final long remainingSeconds = seconds % 60;

        final StringBuilder timeStringBuilder = new StringBuilder();
        if (hours > 0) {
            if (hours < 10) timeStringBuilder.append('0');
            timeStringBuilder.append(hours).append(':');
        }
        if (minutes < 10) timeStringBuilder.append('0');
        timeStringBuilder.append(minutes).append(':');
        if (remainingSeconds < 10) timeStringBuilder.append('0');
        timeStringBuilder.append(remainingSeconds);

        if (isElapsed) {
            timeStringBuilder.append(" elapsed");
        } else {
            timeStringBuilder.append(" left");
        }

        return timeStringBuilder.toString();
    }

    /**
     * Retrieve a pure-java mapping of the buttons array
     *
     * @return A pure-java mapping of the buttons array
     */
    public Map<String, String> getButtonData() {
        final Map<String, String> results = StringUtils.newLinkedHashMap();
        if (buttons != null) {
            for (JsonElement button : buttons) {
                try {
                    if (button.isJsonObject()) {
                        final JsonObject buttonObj = button.getAsJsonObject();
                        if (buttonObj.has("label") && buttonObj.has("url")) {
                            results.put(buttonObj.get("label").getAsString(),
                                    buttonObj.get("url").getAsString()
                            );
                        }
                    }
                } catch (Throwable ex) {
                    Constants.LOG.debugError(ex);
                }
            }
        }
        return results;
    }
}

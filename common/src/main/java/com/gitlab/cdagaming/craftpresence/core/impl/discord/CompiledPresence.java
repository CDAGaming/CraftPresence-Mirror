package com.gitlab.cdagaming.craftpresence.core.impl.discord;

import com.gitlab.cdagaming.craftpresence.core.integrations.discord.assets.DiscordAsset;
import com.google.gson.JsonArray;

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
 * @param endTimestamp   The Current Ending Unix Timestamp from Epoch (Used for time Until if combined with startTime)
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
}

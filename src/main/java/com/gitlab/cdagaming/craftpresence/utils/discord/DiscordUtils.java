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

package com.gitlab.cdagaming.craftpresence.utils.discord;

import com.gitlab.cdagaming.craftpresence.CraftPresence;
import com.gitlab.cdagaming.craftpresence.ModUtils;
import com.gitlab.cdagaming.craftpresence.impl.Pair;
import com.gitlab.cdagaming.craftpresence.impl.Tuple;
import com.gitlab.cdagaming.craftpresence.integrations.curse.CurseUtils;
import com.gitlab.cdagaming.craftpresence.integrations.mcupdater.MCUpdaterUtils;
import com.gitlab.cdagaming.craftpresence.integrations.multimc.MultiMCUtils;
import com.gitlab.cdagaming.craftpresence.integrations.technic.TechnicUtils;
import com.gitlab.cdagaming.craftpresence.utils.CommandUtils;
import com.gitlab.cdagaming.craftpresence.utils.FileUtils;
import com.gitlab.cdagaming.craftpresence.utils.StringUtils;
import com.gitlab.cdagaming.craftpresence.utils.discord.assets.DiscordAssetUtils;
import com.gitlab.cdagaming.craftpresence.utils.discord.rpc.IPCClient;
import com.gitlab.cdagaming.craftpresence.utils.discord.rpc.entities.*;
import com.gitlab.cdagaming.craftpresence.utils.discord.rpc.entities.pipe.PipeStatus;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.Map;

/**
 * Variables and Methods used to update the RPC Presence States to display within Discord
 *
 * @author CDAGaming
 */
public class DiscordUtils {
    /**
     * A Mapping of the Arguments available to use as RPC Message Placeholders
     */
    private final Map<ArgumentType, List<Pair<String, String>>> presenceData = Maps.newHashMap();
    /**
     * A Mapping of the Arguments attached to the &MODS& RPC Message placeholder
     */
    private final List<Pair<String, String>> modsArgs = Lists.newArrayList();
    /**
     * A Mapping of the Arguments attached to the &IGN& RPC Message Placeholder
     */
    private final List<Pair<String, String>> playerInfoArgs = Lists.newArrayList();
    /**
     * The Current User, tied to the Rich Presence
     */
    public User CURRENT_USER;
    /**
     * The Join Request User Data, if any
     */
    public User REQUESTER_USER;
    /**
     * The current RPC Status (Ex: ready, errored, disconnected)
     */
    public DiscordStatus STATUS = DiscordStatus.Disconnected;
    /**
     * The Current Message tied to the Party/Game Status Field of the RPC
     */
    public String GAME_STATE;
    /**
     * The Current Message tied to the current action / Details Field of the RPC
     */
    public String DETAILS;
    /**
     * The Current Small Image Icon being displayed in the RPC, if any
     */
    public String SMALL_IMAGE_KEY;
    /**
     * The Current Message tied to the Small Image, if any
     */
    public String SMALL_IMAGE_TEXT;
    /**
     * The Current Large Image Icon being displayed in the RPC, if any
     */
    public String LARGE_IMAGE_KEY;
    /**
     * The Current Message tied to the Large Image, if any
     */
    public String LARGE_IMAGE_TEXT;
    /**
     * The unique-character Client ID Number, tied to the game profile data attached to the RPC
     */
    public String CLIENT_ID;
    /**
     * Whether to register this application as an application with discord
     */
    public boolean AUTO_REGISTER;
    /**
     * The Current Starting Unix Timestamp from Epoch, used for Elapsed Time
     */
    public long START_TIMESTAMP;
    /**
     * The Party Session ID that's tied to the RPC, if any
     */
    public String PARTY_ID;
    /**
     * The Current Size of the Party Session, if in a Party
     */
    public int PARTY_SIZE;
    /**
     * The Maximum Size of the Party Session, if in a Party
     */
    public int PARTY_MAX;
    /**
     * The Privacy Level of the Party Session
     * <p>0 == Private; 1 == Public
     */
    public PartyPrivacy PARTY_PRIVACY = PartyPrivacy.Public;
    /**
     * The Current Party Join Secret Key, if in a Party
     */
    public String JOIN_SECRET;
    /**
     * The Current Ending Unix Timestamp from Epoch, used for Time Until if combined with {@link DiscordUtils#START_TIMESTAMP}
     */
    public long END_TIMESTAMP;
    /**
     * The Current Match Secret Key tied to the RPC, if any
     */
    public String MATCH_SECRET;
    /**
     * The Current Spectate Secret Key tied to the RPC, if any
     */
    public String SPECTATE_SECRET;
    /**
     * The current button array tied to the RPC, if any
     */
    public JsonArray BUTTONS = new JsonArray();
    /**
     * The Instance Code attached to the RPC, if any
     */
    public byte INSTANCE;
    /**
     * An Instance of the {@link IPCClient}, responsible for sending and receiving RPC Events
     */
    public IPCClient ipcInstance;
    /**
     * Whether Discord is currently awaiting a response to a Ask to Join or Spectate Request, if any
     */
    public boolean awaitingReply = false;

    // Generalized Argument Types
    /**
     * A Mapping of the General RPC Arguments allowed in adjusting Presence Messages
     */
    public List<Pair<String, String>> generalArgs = Lists.newArrayList();
    /**
     * A Mapping of the Last Requested Image Data
     * <p>Used to prevent sending duplicate packets and cache data for repeated images in other areas
     * <p>Format: lastAttemptedKey, lastResultingKey
     */
    private Pair<String, String> lastRequestedImageData = new Pair<>();
    /**
     * An Instance containing the Current Rich Presence Data
     * <p>Also used to prevent sending duplicate packets with the same presence data, if any
     */
    private RichPresence currentPresence;

    /**
     * Setup any Critical Methods needed for the RPC
     * <p>In this case, ensures a Thread is in place to shut down the RPC onExit
     */
    public synchronized void setup() {
        final Thread shutdownThread = new Thread("CraftPresence-ShutDown-Handler") {
            @Override
            public void run() {
                CraftPresence.closing = true;
                CraftPresence.timerObj.cancel();

                shutDown();
            }
        };
        Runtime.getRuntime().addShutdownHook(shutdownThread);
    }

    /**
     * Initializes and Synchronizes Initial Rich Presence Data
     *
     * @param updateTimestamp Whether or not to update the starting timestamp
     */
    public synchronized void init(final boolean updateTimestamp) {
        try {
            // Update Start Timestamp onInit, if needed
            if (updateTimestamp) {
                updateTimestamp();
            }

            // Create IPC Instance and Listener and Make a Connection if possible
            ipcInstance = new IPCClient(Long.parseLong(CLIENT_ID), ModUtils.IS_DEV, ModUtils.IS_VERBOSE, AUTO_REGISTER, CLIENT_ID);
            ipcInstance.setListener(new ModIPCListener());
            ipcInstance.connect();

            // Subscribe to RPC Events after Connection
            ipcInstance.subscribe(IPCClient.Event.ACTIVITY_JOIN);
            ipcInstance.subscribe(IPCClient.Event.ACTIVITY_JOIN_REQUEST);
            ipcInstance.subscribe(IPCClient.Event.ACTIVITY_SPECTATE);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // Initialize and Sync any Pre-made Arguments (And Reset Related Data)
        initArgument(ArgumentType.Text, "&MAINMENU&", "&BRAND&", "&MCVERSION&", "&IGN&", "&MODS&", "&PACK&", "&DIMENSION&", "&BIOME&", "&SERVER&", "&SCREEN&", "&TILEENTITY&", "&TARGETENTITY&", "&ATTACKINGENTITY&", "&RIDINGENTITY&");
        initArgument(ArgumentType.Image, "&DEFAULT&", "&MAINMENU&", "&PACK&", "&DIMENSION&", "&BIOME&", "&SERVER&");

        // Ensure Main Menu RPC Resets properly
        CommandUtils.isInMainMenu = false;

        // Add Any Generalized Argument Data needed
        modsArgs.add(new Pair<>("&MODCOUNT&", Integer.toString(FileUtils.getModCount())));
        playerInfoArgs.add(new Pair<>("&NAME&", ModUtils.USERNAME));

        generalArgs.add(new Pair<>("&MCVERSION&", ModUtils.TRANSLATOR.translate("craftpresence.defaults.state.mc.version", ModUtils.MCVersion)));
        generalArgs.add(new Pair<>("&BRAND&", ModUtils.BRAND));
        generalArgs.add(new Pair<>("&MODS&", StringUtils.sequentialReplaceAnyCase(CraftPresence.CONFIG.modsPlaceholderMessage, modsArgs)));
        generalArgs.add(new Pair<>("&IGN&", StringUtils.sequentialReplaceAnyCase(CraftPresence.CONFIG.outerPlayerPlaceholderMessage, playerInfoArgs)));

        for (Pair<String, String> generalArgument : generalArgs) {
            // For each General (Can be used Anywhere) Argument
            // Ensure they sync as Formatter Arguments too
            syncArgument(generalArgument.getFirst(), generalArgument.getSecond(), ArgumentType.Text);
        }

        // Sync the Default Icon Argument
        syncArgument("&DEFAULT&", CraftPresence.CONFIG.defaultIcon, ArgumentType.Image);

        syncPackArguments();
    }

    /**
     * Creates a string-based representation of the button-list, from config values
     *
     * @return the output list
     */
    public List<String> createButtonsList() {
        final List<String> result = Lists.newArrayList();
        for (String buttonElement : CraftPresence.CONFIG.buttonMessages) {
            if (!StringUtils.isNullOrEmpty(buttonElement)) {
                final String[] part = buttonElement.split(CraftPresence.CONFIG.splitCharacter);
                if (!StringUtils.isNullOrEmpty(part[0])) {
                    result.add(part[0]);
                }
            }
        }
        return result;
    }

    /**
     * Removes any invalid data from a placeholder argument
     *
     * @param input The string to interpret
     * @return The resulting output string
     */
    public String sanitizePlaceholders(String input) {
        if (StringUtils.isNullOrEmpty(input) || !CraftPresence.CONFIG.formatWords) {
            return input;
        }
        return input.replaceAll("&[^&]*&", "");
    }

    /**
     * Updates the Starting Unix Timestamp, if allowed
     */
    public void updateTimestamp() {
        if (CraftPresence.CONFIG.showTime) {
            START_TIMESTAMP = CraftPresence.SYSTEM.CURRENT_TIMESTAMP / 1000L;
        }
    }

    /**
     * Synchronizes the Specified Argument as an RPC Message or an Icon Placeholder
     *
     * @param argumentName The Specified Argument to Synchronize for
     * @param insertString The String to attach to the Specified Argument
     * @param dataType     The type the argument should be stored as
     */
    public void syncArgument(String argumentName, String insertString, ArgumentType dataType) {
        // Remove and Replace Placeholder Data, if the placeholder needs Updates
        if (!StringUtils.isNullOrEmpty(argumentName)) {
            setArgumentsFor(dataType, new Pair<>(argumentName, insertString));
        }
    }

    /**
     * Initialize the Specified Arguments as Empty Data
     *
     * @param dataType The type the argument should be stored as
     * @param args     The Arguments to Initialize
     */
    public void initArgument(ArgumentType dataType, String... args) {
        // Initialize Specified Arguments to Empty Data
        for (String argumentName : args) {
            syncArgument(argumentName, "", dataType);
        }
    }

    /**
     * Retrieve all arguments for the specified types
     *
     * @param typeList The types the arguments should be retrieved from
     * @return The found list of arguments
     */
    public List<Pair<String, String>> getArgumentsFor(final ArgumentType... typeList) {
        List<Pair<String, String>> result = Lists.newArrayList();
        for (ArgumentType type : typeList) {
            if (!presenceData.containsKey(type)) {
                presenceData.put(type, Lists.newArrayList());
            }
            result.addAll(presenceData.get(type));
        }
        return result;
    }

    /**
     * Remove any arguments following the specified formats within the selected Argument Type
     *
     * @param type The type the arguments should be retrieved from
     * @param args The string formats to interpret
     */
    public void removeArgumentsMatching(final ArgumentType type, final String... args) {
        if (presenceData.containsKey(type)) {
            final List<Pair<String, String>> list = Lists.newArrayList(presenceData.get(type));
            for (Pair<String, String> entry : presenceData.get(type)) {
                for (String format : args) {
                    if (entry.getFirst().contains(format)) {
                        list.remove(entry);
                    }
                }
            }
            setArgumentsFor(type, list);
        }
    }

    /**
     * Retrieves any arguments within the specified type that match the specified string formats
     *
     * @param type The type the arguments should be retrieved from
     * @param args The string formats to interpret
     * @return A List of the entries that satisfy the method conditions
     */
    public List<Pair<String, String>> getArgumentsMatching(final ArgumentType type, final String... args) {
        final List<Pair<String, String>> list = Lists.newArrayList();
        if (presenceData.containsKey(type)) {
            for (Pair<String, String> entry : presenceData.get(type)) {
                for (String format : args) {
                    if (entry.getFirst().contains(format)) {
                        list.add(entry);
                    }
                }
            }
        }
        return list;
    }

    /**
     * Retrieves any argument entries within the specified type that match the specified string formats
     *
     * @param type          The type the arguments should be retrieved from
     * @param formatToLower Whether to lower-cases the resulting entries
     * @param args          The string formats to interpret
     * @return A List of the entries that satisfy the method conditions
     */
    public List<String> getArgumentEntries(final ArgumentType type, final boolean formatToLower, final String... args) {
        final List<Pair<String, String>> list = getArgumentsMatching(type, args);
        final List<String> result = Lists.newArrayList();
        for (Pair<String, String> entry : list) {
            result.add(formatToLower ? entry.getFirst().toLowerCase() : entry.getFirst());
        }
        return result;
    }

    /**
     * Retrieves any argument entries within the specified type that match the specified string formats
     *
     * @param type The type the arguments should be retrieved from
     * @param args The string formats to interpret
     * @return A List of the entries that satisfy the method conditions
     */
    public List<String> getArgumentEntries(final ArgumentType type, final String... args) {
        return getArgumentEntries(type, false, args);
    }

    /**
     * Determines whether there are any matching arguments within the specified type matching the specified string formats
     *
     * @param type The type the arguments should be retrieved from
     * @param args The string formats to interpret
     * @return Whether the resulting list has any matching entries
     */
    public boolean hasArgumentsMatching(final ArgumentType type, final String... args) {
        return !getArgumentsMatching(type, args).isEmpty();
    }

    /**
     * Stores the specified argument data for the specified type
     *
     * @param type The type the arguments should be stored as
     * @param data The list of data to interpret
     */
    public void setArgumentsFor(final ArgumentType type, final List<Pair<String, String>> data) {
        presenceData.put(type, data);
    }

    /**
     * Stores the specified argument data for the specified type
     *
     * @param type The type the arguments should be stored as
     * @param data The data to interpret
     */
    public void setArgumentsFor(final ArgumentType type, final Pair<String, String> data) {
        final List<Pair<String, String>> list = getArgumentsFor(type);
        StringUtils.removeIf(list, e -> e.getFirst().equalsIgnoreCase(data.getFirst()));
        list.add(data);
        setArgumentsFor(type, list);
    }

    /**
     * Synchronizes the &PACK& Argument, based on any found Launcher Pack/Instance Data
     */
    private void syncPackArguments() {
        // Add &PACK& Placeholder to ArgumentData
        String foundPackName = "", foundPackIcon = "";

        if (ModUtils.BRAND.contains("vivecraft")) {
            CraftPresence.packFound = true;

            foundPackName = CraftPresence.CONFIG.vivecraftMessage;
            foundPackIcon = "vivecraft";
        } else if (!StringUtils.isNullOrEmpty(CurseUtils.INSTANCE_NAME)) {
            foundPackName = CurseUtils.INSTANCE_NAME;
            foundPackIcon = foundPackName;
        } else if (!StringUtils.isNullOrEmpty(MultiMCUtils.INSTANCE_NAME)) {
            foundPackName = MultiMCUtils.INSTANCE_NAME;
            foundPackIcon = MultiMCUtils.ICON_KEY;
        } else if (MCUpdaterUtils.instance != null && !StringUtils.isNullOrEmpty(MCUpdaterUtils.instance.getPackName())) {
            foundPackName = MCUpdaterUtils.instance.getPackName();
            foundPackIcon = foundPackName;
        } else if (!StringUtils.isNullOrEmpty(TechnicUtils.PACK_NAME)) {
            foundPackName = TechnicUtils.PACK_NAME;
            foundPackIcon = TechnicUtils.ICON_NAME;
        } else if (!StringUtils.isNullOrEmpty(CraftPresence.CONFIG.fallbackPackPlaceholderMessage)) {
            foundPackName = CraftPresence.CONFIG.fallbackPackPlaceholderMessage;
            foundPackIcon = foundPackName;
        }

        syncArgument("&PACK&", StringUtils.formatWord(StringUtils.replaceAnyCase(CraftPresence.CONFIG.packPlaceholderMessage, "&NAME&", !StringUtils.isNullOrEmpty(foundPackName) ? foundPackName : ""), !CraftPresence.CONFIG.formatWords), ArgumentType.Text);
        syncArgument("&PACK&", !StringUtils.isNullOrEmpty(foundPackIcon) ? StringUtils.formatAsIcon(foundPackIcon) : "", ArgumentType.Image);
    }

    /**
     * Synchronizes and Updates the Rich Presence Data, if needed and connected
     *
     * @param presence The New Presence Data to apply
     */
    public void updatePresence(final RichPresence presence) {
        if (presence != null &&
                (currentPresence == null || !presence.toJson().toString().equals(currentPresence.toJson().toString())) &&
                ipcInstance.getStatus() == PipeStatus.CONNECTED) {
            ipcInstance.sendRichPresence(presence);
            currentPresence = presence;
        }
    }

    /**
     * Attempts to lookup the specified Image, and if not existent, use the alternative String, and null if allowed
     *
     * @param evalString        The Specified Icon Key to search for from the {@link DiscordUtils#CLIENT_ID} Assets
     * @param alternativeString The Alternative Icon Key to use if unable to locate the Original Icon Key
     * @param allowNull         If allowed to return null if unable to find any matches, otherwise uses the Default Icon in Config
     * @return The found or alternative matching Icon Key
     */
    public String imageOf(final String evalString, final String alternativeString, final boolean allowNull) {
        // Ensures Assets were fully synced from the Client ID before running
        if (DiscordAssetUtils.syncCompleted) {
            if (StringUtils.isNullOrEmpty(lastRequestedImageData.getFirst()) || !lastRequestedImageData.getFirst().equalsIgnoreCase(evalString)) {
                final String defaultIcon = DiscordAssetUtils.contains(CraftPresence.CONFIG.defaultIcon) ? CraftPresence.CONFIG.defaultIcon : DiscordAssetUtils.getRandomAsset();
                lastRequestedImageData.setFirst(evalString);

                String finalKey = evalString;

                if (!DiscordAssetUtils.contains(finalKey)) {
                    ModUtils.LOG.error(ModUtils.TRANSLATOR.translate(true, "craftpresence.logger.error.discord.assets.fallback", evalString, alternativeString));
                    ModUtils.LOG.info(ModUtils.TRANSLATOR.translate(true, "craftpresence.logger.info.discord.assets.request", evalString));
                    if (DiscordAssetUtils.contains(alternativeString)) {
                        ModUtils.LOG.info(ModUtils.TRANSLATOR.translate(true, "craftpresence.logger.info.discord.assets.fallback", evalString, alternativeString));
                        finalKey = alternativeString;
                    } else {
                        if (allowNull) {
                            finalKey = "";
                        } else {
                            ModUtils.LOG.info(ModUtils.TRANSLATOR.translate(true, "craftpresence.logger.error.discord.assets.default", evalString, defaultIcon));
                            finalKey = defaultIcon;
                        }
                    }
                }

                lastRequestedImageData.setSecond(finalKey);
                return finalKey;
            } else {
                return lastRequestedImageData.getSecond();
            }
        } else {
            return "";
        }
    }

    /**
     * Clears Related Party Session Information from the RPC, and updates if needed
     *
     * @param clearRequesterData Whether to clear Ask to Join / Spectate Request Data
     * @param updateRPC          Whether to immediately update the RPC following changes
     */
    public void clearPartyData(boolean clearRequesterData, boolean updateRPC) {
        if (clearRequesterData) {
            awaitingReply = false;
            REQUESTER_USER = null;
            CraftPresence.SYSTEM.TIMER = 0;
        }
        JOIN_SECRET = null;
        PARTY_ID = null;
        PARTY_SIZE = 0;
        PARTY_MAX = 0;
        if (updateRPC) {
            updatePresence(buildRichPresence());
        }
    }

    /**
     * Clears Presence Data from the RPC, and updates if needed
     *
     * @param partyClearArgs Arguments for {@link DiscordUtils#clearPartyData(boolean, boolean)}
     */
    public void clearPresenceData(Tuple<Boolean, Boolean, Boolean> partyClearArgs) {
        GAME_STATE = "";
        DETAILS = "";
        LARGE_IMAGE_KEY = "";
        LARGE_IMAGE_TEXT = "";
        SMALL_IMAGE_KEY = "";
        SMALL_IMAGE_TEXT = "";

        if (partyClearArgs.getFirst()) {
            clearPartyData(partyClearArgs.getSecond(), partyClearArgs.getThird());
        }
    }

    /**
     * Shutdown the RPC and close related resources, as well as Clearing any remaining Runtime Client Data
     */
    public synchronized void shutDown() {
        if (CraftPresence.SYSTEM.HAS_LOADED) {
            try {
                ipcInstance.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            // Clear User Data before final clear and shutdown
            STATUS = DiscordStatus.Disconnected;
            currentPresence = null;
            // Empty RPC Data
            clearPresenceData(new Tuple<>(true, true, false));

            CURRENT_USER = null;
            lastRequestedImageData = new Pair<>();

            CraftPresence.DIMENSIONS.clearClientData();
            CraftPresence.TILE_ENTITIES.clearClientData();
            CraftPresence.ENTITIES.clearClientData();
            CraftPresence.BIOMES.clearClientData();
            CraftPresence.SERVER.clearClientData();
            CraftPresence.GUIS.clearClientData();

            CraftPresence.SYSTEM.HAS_LOADED = false;
            ModUtils.LOG.info(ModUtils.TRANSLATOR.translate("craftpresence.logger.info.shutdown"));
        }
    }

    /**
     * Builds a New Instance of {@link RichPresence} based on Queued Data
     *
     * @return A New Instance of {@link RichPresence}
     */
    public RichPresence buildRichPresence() {
        // Format Presence based on Arguments available in argumentData
        DETAILS = StringUtils.formatWord(StringUtils.sequentialReplaceAnyCase(CraftPresence.CONFIG.detailsMessage, getArgumentsFor(ArgumentType.Text)), !CraftPresence.CONFIG.formatWords, true, 1);
        GAME_STATE = StringUtils.formatWord(StringUtils.sequentialReplaceAnyCase(CraftPresence.CONFIG.gameStateMessage, getArgumentsFor(ArgumentType.Text)), !CraftPresence.CONFIG.formatWords, true, 1);

        LARGE_IMAGE_KEY = StringUtils.formatAsIcon(StringUtils.sequentialReplaceAnyCase(CraftPresence.CONFIG.largeImageKey, getArgumentsFor(ArgumentType.Image)));
        SMALL_IMAGE_KEY = StringUtils.formatAsIcon(StringUtils.sequentialReplaceAnyCase(CraftPresence.CONFIG.smallImageKey, getArgumentsFor(ArgumentType.Image)));

        LARGE_IMAGE_TEXT = StringUtils.formatWord(StringUtils.sequentialReplaceAnyCase(CraftPresence.CONFIG.largeImageMessage, getArgumentsFor(ArgumentType.Text)), !CraftPresence.CONFIG.formatWords, true, 1);
        SMALL_IMAGE_TEXT = StringUtils.formatWord(StringUtils.sequentialReplaceAnyCase(CraftPresence.CONFIG.smallImageMessage, getArgumentsFor(ArgumentType.Text)), !CraftPresence.CONFIG.formatWords, true, 1);

        // Format Buttons Array based on Config Value
        BUTTONS = new JsonArray();
        for (String buttonElement : CraftPresence.CONFIG.buttonMessages) {
            if (!StringUtils.isNullOrEmpty(buttonElement)) {
                final String[] part = buttonElement.split(CraftPresence.CONFIG.splitCharacter);
                JsonObject buttonObj = new JsonObject();
                if (!StringUtils.isNullOrEmpty(part[0]) && !part[0].equalsIgnoreCase("default") && !StringUtils.isNullOrEmpty(part[1])) {
                    String label = StringUtils.formatWord(
                            StringUtils.sequentialReplaceAnyCase(part[1], getArgumentsFor(ArgumentType.Button, ArgumentType.Text)),
                            !CraftPresence.CONFIG.formatWords, true, 1
                    );
                    String url = !StringUtils.isNullOrEmpty(part[2]) ?
                            StringUtils.formatWord(
                                    StringUtils.sequentialReplaceAnyCase(part[2], getArgumentsFor(ArgumentType.Button, ArgumentType.Text)),
                                    !CraftPresence.CONFIG.formatWords, true, 1
                            ) : "";
                    buttonObj.addProperty("label", sanitizePlaceholders(label));
                    buttonObj.addProperty("url", sanitizePlaceholders(url));
                    BUTTONS.add(buttonObj);
                }
            }
        }

        final RichPresence newRPCData = new RichPresence.Builder()
                .setState(GAME_STATE = sanitizePlaceholders(GAME_STATE))
                .setDetails(DETAILS = sanitizePlaceholders(DETAILS))
                .setStartTimestamp(START_TIMESTAMP)
                .setEndTimestamp(END_TIMESTAMP)
                .setLargeImage(LARGE_IMAGE_KEY = sanitizePlaceholders(LARGE_IMAGE_KEY),
                        LARGE_IMAGE_TEXT = sanitizePlaceholders(LARGE_IMAGE_TEXT))
                .setSmallImage(SMALL_IMAGE_KEY = sanitizePlaceholders(SMALL_IMAGE_KEY),
                        SMALL_IMAGE_TEXT = sanitizePlaceholders(SMALL_IMAGE_TEXT))
                .setParty(PARTY_ID, PARTY_SIZE, PARTY_MAX, PARTY_PRIVACY.getIndex())
                .setMatchSecret(MATCH_SECRET)
                .setJoinSecret(JOIN_SECRET)
                .setSpectateSecret(SPECTATE_SECRET)
                .setButtons(BUTTONS)
                .build();

        // Format Data to UTF_8 after Sent to RPC (RPC has it's own Encoding)
        GAME_STATE = StringUtils.getConvertedString(GAME_STATE, "UTF-8", false);
        DETAILS = StringUtils.getConvertedString(DETAILS, "UTF-8", false);

        LARGE_IMAGE_KEY = StringUtils.getConvertedString(LARGE_IMAGE_KEY, "UTF-8", false);
        SMALL_IMAGE_KEY = StringUtils.getConvertedString(SMALL_IMAGE_KEY, "UTF-8", false);

        LARGE_IMAGE_TEXT = StringUtils.getConvertedString(LARGE_IMAGE_TEXT, "UTF-8", false);
        SMALL_IMAGE_TEXT = StringUtils.getConvertedString(SMALL_IMAGE_TEXT, "UTF-8", false);

        return newRPCData;
    }
}

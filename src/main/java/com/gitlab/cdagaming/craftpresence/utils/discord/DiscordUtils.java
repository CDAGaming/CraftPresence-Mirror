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
import com.gitlab.cdagaming.craftpresence.impl.Predicate;
import com.gitlab.cdagaming.craftpresence.impl.Tuple;
import com.gitlab.cdagaming.craftpresence.impl.discord.ArgumentType;
import com.gitlab.cdagaming.craftpresence.impl.discord.DiscordStatus;
import com.gitlab.cdagaming.craftpresence.impl.discord.PartyPrivacy;
import com.gitlab.cdagaming.craftpresence.integrations.curse.CurseUtils;
import com.gitlab.cdagaming.craftpresence.integrations.mcupdater.MCUpdaterUtils;
import com.gitlab.cdagaming.craftpresence.integrations.multimc.MultiMCUtils;
import com.gitlab.cdagaming.craftpresence.integrations.technic.TechnicUtils;
import com.gitlab.cdagaming.craftpresence.utils.CommandUtils;
import com.gitlab.cdagaming.craftpresence.utils.FileUtils;
import com.gitlab.cdagaming.craftpresence.utils.StringUtils;
import com.gitlab.cdagaming.craftpresence.utils.discord.assets.DiscordAsset;
import com.gitlab.cdagaming.craftpresence.utils.discord.assets.DiscordAssetUtils;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jagrosh.discordipc.IPCClient;
import com.jagrosh.discordipc.entities.DiscordBuild;
import com.jagrosh.discordipc.entities.RichPresence;
import com.jagrosh.discordipc.entities.User;
import com.jagrosh.discordipc.entities.pipe.PipeStatus;
import org.slf4j.impl.JDK14LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Variables and Methods used to update the RPC Presence States to display within Discord
 *
 * @author CDAGaming
 */
public class DiscordUtils {
    /**
     * A list of the text modules for this application
     */
    public static final List<String> textModules = Lists.newArrayList(
            "&MAINMENU&",
            "&BRAND&", "&MCVERSION&", "&IGN&", "&MODS&", "&PACK&",
            "&DIMENSION&", "&BIOME&", "&SERVER&", "&SCREEN&",
            "&TILEENTITY&", "&TARGETENTITY&", "&RIDINGENTITY&"
    );
    /**
     * A list of the icon modules for this application
     */
    public static final List<String> iconModules = Lists.newArrayList(
            "&DEFAULT&", "&MAINMENU&", "&PACK&",
            "&DIMENSION&", "&BIOME&", "&SERVER&"
    );
    public static final Map<String, Pair<String, String>> validOperators = ImmutableMap.<String, Pair<String, String>>builder()
            .put("|", new Pair<>("\\|", "&[^&]*&[\\|]&[^&]*&"))
            .build();
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
     * A Mapping of the Arguments attached to the &PACK& RPC Message Placeholder
     */
    private final List<Pair<String, String>> packArgs = Lists.newArrayList();
    /**
     * A Mapping of the Last Requested Image Data
     * <p>Used to cache data for repeated images in other areas
     * <p>Format: evalKey, resultingKey
     */
    private final Map<String, String> cachedImageData = Maps.newHashMap();
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
     * The Current Small Image Asset being displayed in the RPC, if any
     */
    public DiscordAsset SMALL_IMAGE_ASSET;
    /**
     * The Current Small Image Icon being displayed in the RPC, if any
     */
    public String SMALL_IMAGE_KEY;
    /**
     * The Current Message tied to the Small Image, if any
     */
    public String SMALL_IMAGE_TEXT;
    /**
     * The Current Large Image Asset being displayed in the RPC, if any
     */
    public DiscordAsset LARGE_IMAGE_ASSET;
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
     * The preferred {@link DiscordBuild} to try pairing to
     */
    public DiscordBuild PREFERRED_CLIENT = DiscordBuild.ANY;
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
    /**
     * A Mapping of the General RPC Arguments allowed in adjusting Presence Messages
     */
    public List<Pair<String, String>> generalArgs = Lists.newArrayList();
    /**
     * A Mapping of the Last Requested Image Cache Data
     * <p>Used to prevent sending duplicate packets
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
     * @param debugMode       Whether to enable debug mode for this instance
     * @param verboseMode     Whether to enable verbose mode for this instance
     * @param updateTimestamp Whether to update the starting timestamp
     */
    public synchronized void init(final boolean debugMode, final boolean verboseMode, final boolean updateTimestamp) {
        try {
            // Update Start Timestamp onInit, if needed
            if (updateTimestamp) {
                updateTimestamp();
            }

            // Create IPC Instance and Listener and Make a Connection if possible
            ipcInstance = new IPCClient(Long.parseLong(CLIENT_ID), debugMode, verboseMode, AUTO_REGISTER, CLIENT_ID);
            ipcInstance.setForcedLogger(new JDK14LoggerFactory().getLogger(
                    ModUtils.LOG.getLogInstance().getName()
            ));
            ipcInstance.setListener(new ModIPCListener());
            if (PREFERRED_CLIENT != DiscordBuild.ANY) {
                ipcInstance.connect(PREFERRED_CLIENT, DiscordBuild.ANY);
            } else {
                ipcInstance.connect();
            }

            // Subscribe to RPC Events after Connection
            ipcInstance.subscribe(IPCClient.Event.ACTIVITY_JOIN);
            ipcInstance.subscribe(IPCClient.Event.ACTIVITY_JOIN_REQUEST);
            ipcInstance.subscribe(IPCClient.Event.ACTIVITY_SPECTATE);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // Initialize and Sync any Pre-made Arguments (And Reset Related Data)
        for (String moduleId : textModules) {
            initArgument(ArgumentType.Text, moduleId);
        }
        for (String moduleId : iconModules) {
            initArgument(ArgumentType.Image, moduleId);
        }

        // Ensure Main Menu RPC Resets properly
        CommandUtils.isInMainMenu = false;

        syncPlaceholders();
    }

    /**
     * Initializes and Synchronizes Initial Rich Presence Data
     *
     * @param debugMode       Whether to enable debug mode for this instance
     * @param updateTimestamp Whether to update the starting timestamp
     */
    public synchronized void init(final boolean debugMode, final boolean updateTimestamp) {
        init(debugMode, ModUtils.IS_VERBOSE, updateTimestamp);
    }

    /**
     * Initializes and Synchronizes Initial Rich Presence Data
     *
     * @param updateTimestamp Whether to update the starting timestamp
     */
    public synchronized void init(final boolean updateTimestamp) {
        init(ModUtils.IS_DEV, updateTimestamp);
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
        return !StringUtils.isNullOrEmpty(input) ? input.replaceAll("&[^&]*&", "").trim() : "";
    }

    /**
     * Parses the Argument Operators and Placeholders within a message
     *
     * @param input    The string to interpret
     * @param typeList The list of {@link ArgumentType}'s to iterate through
     * @return the parsed message
     */
    public String parseArgumentOperators(final String input, ArgumentType... typeList) {
        String result = input;
        if (CraftPresence.CONFIG.allowPlaceholderOperators) {
            for (Map.Entry<String, Pair<String, String>> rawEntry : validOperators.entrySet()) {
                final Pair<String, String> operatorEntry = rawEntry.getValue();
                final Pair<String, List<String>> matches = StringUtils.getMatches(operatorEntry.getSecond(), result);

                if (!matches.getSecond().isEmpty()) {
                    for (String match : matches.getSecond()) {
                        boolean foundMatch = false;
                        String resultMatch = match;
                        for (String splitData : match.split(operatorEntry.getFirst())) {
                            if (foundMatch) {
                                break;
                            }
                            for (ArgumentType type : typeList) {
                                if (!getRawArgumentEntries(type, false, splitData).isEmpty()) {
                                    foundMatch = true;
                                    resultMatch = splitData;
                                    break;
                                }
                            }
                        }
                        result = result.replace(match, foundMatch ? resultMatch : "");
                    }
                }
            }
        }
        return StringUtils.sequentialReplaceAnyCase(result, getArgumentsFor(typeList));
    }

    /**
     * Updates the Starting Unix Timestamp, if allowed
     */
    public void updateTimestamp() {
        if (CraftPresence.CONFIG.showTime) {
            START_TIMESTAMP = System.currentTimeMillis() / 1000L;
        }
    }

    /**
     * Synchronizes the Specified Argument as an RPC Message or an Icon Placeholder
     *
     * @param argumentName The Specified Argument to Synchronize for
     * @param insertString The String to attach to the Specified Argument
     * @param dataTypes    The type(s) the argument should be stored as
     */
    public void syncArgument(String argumentName, String insertString, ArgumentType... dataTypes) {
        for (ArgumentType dataType : dataTypes) {
            // Remove and Replace Placeholder Data, if the placeholder needs updates
            if (!StringUtils.isNullOrEmpty(argumentName)) {
                setArgumentsFor(dataType, new Pair<>(argumentName, insertString));
            }
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
     * Initialize the Specified Arguments as Empty Data
     *
     * @param args The Arguments to Initialize
     */
    public void initArgument(String... args) {
        for (ArgumentType type : ArgumentType.values()) {
            initArgument(type, args);
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
                presenceData.put(type, Lists.<Pair<String, String>>newArrayList());
            }
            StringUtils.addEntriesNotPresent(result, presenceData.get(type));
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
     * Remove any arguments following the specified formats within the selected Argument Type
     *
     * @param args The string formats to interpret
     */
    public void removeArgumentsMatching(final String... args) {
        for (ArgumentType type : ArgumentType.values()) {
            removeArgumentsMatching(type, args);
        }
    }

    /**
     * Retrieves any arguments within the specified type that match the specified string formats
     *
     * @param type             The type the arguments should be retrieved from
     * @param allowNullEntries Whether empty entry values should be interpreted
     * @param args             The string formats to interpret
     * @return A List of the entries that satisfy the method conditions
     */
    public List<Pair<String, String>> getArgumentsMatching(final ArgumentType type, final boolean allowNullEntries, final String... args) {
        final List<Pair<String, String>> list = Lists.newArrayList();
        if (presenceData.containsKey(type)) {
            for (Pair<String, String> entry : presenceData.get(type)) {
                final boolean isEntryValid = allowNullEntries || !StringUtils.isNullOrEmpty(entry.getSecond());
                for (String format : args) {
                    if (entry.getFirst().contains(format) && isEntryValid) {
                        list.add(entry);
                    }
                }
            }
        }
        return list;
    }

    /**
     * Retrieves any arguments within the specified type that match the specified string formats
     *
     * @param type The type the arguments should be retrieved from
     * @param args The string formats to interpret
     * @return A List of the entries that satisfy the method conditions
     */
    public List<Pair<String, String>> getArgumentsMatching(final ArgumentType type, final String... args) {
        return getArgumentsMatching(type, true, args);
    }

    /**
     * Retrieves any arguments within the specified type that match the specified string formats
     *
     * @param allowNullEntries Whether empty entry values should be interpreted
     * @param args             The string formats to interpret
     * @return A List of the entries that satisfy the method conditions
     */
    public List<Pair<String, String>> getArgumentsMatching(final boolean allowNullEntries, final String... args) {
        final List<Pair<String, String>> results = Lists.newArrayList();
        for (ArgumentType type : ArgumentType.values()) {
            StringUtils.addEntriesNotPresent(results, getArgumentsMatching(type, allowNullEntries, args));
        }
        return results;
    }

    /**
     * Retrieves any arguments within the specified type that match the specified string formats
     *
     * @param args The string formats to interpret
     * @return A List of the entries that satisfy the method conditions
     */
    public List<Pair<String, String>> getArgumentsMatching(final String... args) {
        return getArgumentsMatching(true, args);
    }

    /**
     * Retrieves any argument entries within the specified type that match the specified string formats
     *
     * @param type             The type the arguments should be retrieved from
     * @param formatToLower    Whether to lower-cases the resulting entries
     * @param allowNullEntries Whether empty entry values should be interpreted
     * @param args             The string formats to interpret
     * @return A List of the entries that satisfy the method conditions
     */
    public List<String> getArgumentEntries(final ArgumentType type, final boolean formatToLower, final boolean allowNullEntries, final String... args) {
        final List<Pair<String, String>> list = getArgumentsMatching(type, allowNullEntries, args);
        final List<String> result = Lists.newArrayList();
        for (Pair<String, String> entry : list) {
            result.add(formatToLower ? entry.getFirst().toLowerCase() : entry.getFirst());
        }
        return result;
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
        return getArgumentEntries(type, formatToLower, true, args);
    }

    /**
     * Retrieves any argument entries within the specified type that match the specified string formats
     *
     * @param type             The type the arguments should be retrieved from
     * @param allowNullEntries Whether empty entry values should be interpreted
     * @param args             The string formats to interpret
     * @return A List of the entries that satisfy the method conditions
     */
    public List<String> getRawArgumentEntries(final ArgumentType type, final boolean allowNullEntries, final String... args) {
        return getArgumentEntries(type, false, allowNullEntries, args);
    }

    /**
     * Retrieves any argument entries within the specified type that match the specified string formats
     *
     * @param type The type the arguments should be retrieved from
     * @param args The string formats to interpret
     * @return A List of the entries that satisfy the method conditions
     */
    public List<String> getRawArgumentEntries(final ArgumentType type, final String... args) {
        return getRawArgumentEntries(type, true, args);
    }

    /**
     * Determines whether there are any matching arguments within the specified type matching the specified string formats
     *
     * @param type             The type the arguments should be retrieved from
     * @param allowNullEntries Whether empty entry values should be interpreted
     * @param args             The string formats to interpret
     * @return Whether the resulting list has any matching entries
     */
    public boolean hasArgumentsMatching(final ArgumentType type, final boolean allowNullEntries, final String... args) {
        return !getArgumentsMatching(type, allowNullEntries, args).isEmpty();
    }

    /**
     * Determines whether there are any matching arguments within the specified type matching the specified string formats
     *
     * @param type The type the arguments should be retrieved from
     * @param args The string formats to interpret
     * @return Whether the resulting list has any matching entries
     */
    public boolean hasArgumentsMatching(final ArgumentType type, final String... args) {
        return hasArgumentsMatching(type, true, args);
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
        StringUtils.removeIf(list, new Predicate<Pair<String, String>>() {
            @Override
            public boolean test(Pair<String, String> e) {
                return e.getFirst().equalsIgnoreCase(data.getFirst());
            }
        });
        list.add(data);
        setArgumentsFor(type, list);
    }

    /**
     * Synchronizes the &PACK& Argument, based on any found Launcher Pack/Instance Data
     */
    private void syncPackArguments() {
        // Add &PACK& Placeholder to ArgumentData
        packArgs.clear();
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

        packArgs.add(new Pair<>("&NAME&", (!StringUtils.isNullOrEmpty(foundPackName) ? foundPackName : "")));

        // Add applicable args as sub-placeholders
        for (Pair<String, String> argumentData : packArgs) {
            syncArgument("&PACK:" + argumentData.getFirst().substring(1), argumentData.getSecond(), ArgumentType.Text);
        }

        syncArgument("&PACK&", StringUtils.sequentialReplaceAnyCase(CraftPresence.CONFIG.packPlaceholderMessage, packArgs), ArgumentType.Text);
        syncArgument("&PACK&", !StringUtils.isNullOrEmpty(foundPackIcon) ? StringUtils.formatAsIcon(foundPackIcon) : "", ArgumentType.Image);
    }

    /**
     * Generate a list of Arguments, depending on the Argument Types and String List
     *
     * @param allowNullEntries Whether empty entry values should be interpreted
     * @param argumentData     The data to interpret
     * @return the list of parsed arguments
     */
    public List<Pair<String, String>> generateArgumentList(final boolean allowNullEntries, final Map<ArgumentType, List<String>> argumentData) {
        final List<Pair<String, String>> results = Lists.newArrayList();
        for (Map.Entry<ArgumentType, List<String>> entry : argumentData.entrySet()) {
            StringUtils.addEntriesNotPresent(results,
                    new Predicate<Pair<String, String>>() {
                        @Override
                        public boolean test(final Pair<String, String> data) {
                            return StringUtils.filter(Lists.newArrayList(results), new Predicate<Pair<String, String>>() {
                                @Override
                                public boolean test(Pair<String, String> e) {
                                    return e.getFirst().equalsIgnoreCase(data.getFirst());
                                }
                            }).isEmpty();
                        }
                    },
                    convertToArgumentList(entry.getKey(), allowNullEntries, entry.getValue())
            );
        }
        return results;
    }

    /**
     * Generate a list of Arguments, depending on the Argument Types and String List
     *
     * @param argumentData The data to interpret
     * @return the list of parsed arguments
     */
    public List<Pair<String, String>> generateArgumentList(final Map<ArgumentType, List<String>> argumentData) {
        return generateArgumentList(true, argumentData);
    }

    /**
     * Generate a parsable display string for the argument data provided
     *
     * @param argumentFormat    The primary argument format to interpret
     * @param subArgumentFormat The secondary (or sub-prefix) argument format to interpret
     * @param addExtraData      Whether to add additional data to the string
     * @param args              The data to interpret
     * @return the parsable string
     */
    public String generateArgumentMessage(final String argumentFormat, final String subArgumentFormat, final boolean addExtraData, final List<Pair<String, String>> args) {
        final StringBuilder finalString = new StringBuilder(
                String.format("%s%s:",
                        ModUtils.TRANSLATOR.translate(
                                String.format("%s.placeholders.title", ModUtils.MOD_ID)
                        ), (!StringUtils.isNullOrEmpty(argumentFormat) ? (" (" + argumentFormat.toLowerCase() + ")") : "")
                )
        );
        if (args != null && !args.isEmpty()) {
            for (Pair<String, String> argData : args) {
                String placeholderName = argData.getFirst();
                String translationName = placeholderName;
                if (!StringUtils.isNullOrEmpty(argumentFormat)) {
                    if (!StringUtils.isNullOrEmpty(subArgumentFormat)) {
                        placeholderName = placeholderName.replaceAll(subArgumentFormat, argumentFormat.substring(0, 1));
                    }
                    translationName = (argumentFormat + "." + placeholderName).replaceAll(argumentFormat.substring(0, 1), "");
                } else {
                    translationName = translationName.replaceAll("[^a-zA-Z0-9]", "");
                }
                finalString.append(
                        String.format("\\n - %s = %s",
                                placeholderName.toLowerCase(),
                                ModUtils.TRANSLATOR.translate(
                                        String.format("%s.placeholders.%s.description",
                                                ModUtils.MOD_ID,
                                                translationName.replaceAll(":", ".")
                                        )
                                ))
                );

                if (addExtraData && !StringUtils.isNullOrEmpty(argData.getSecond())) {
                    final String tagValue = argData.getSecond();
                    finalString.append(String.format("\\n ==> %s \"%s\"",
                            ModUtils.TRANSLATOR.translate("gui.config.message.editor.preview"),
                            (tagValue.length() >= 128) ? "<...>" : tagValue
                    ));
                }
            }
        } else {
            finalString.append("\\n - N/A");
        }
        return finalString.toString();
    }

    /**
     * Generate a parsable display string for the argument data provided
     *
     * @param argumentFormat    The primary argument format to interpret
     * @param subArgumentFormat The secondary (or sub-prefix) argument format to interpret
     * @param args              The data to interpret
     * @return the parsable string
     */
    public String generateArgumentMessage(final String argumentFormat, final String subArgumentFormat, final List<Pair<String, String>> args) {
        return generateArgumentMessage(argumentFormat, subArgumentFormat, CraftPresence.CONFIG.allowPlaceholderPreviews, args);
    }

    /**
     * Generate a parsable display string for the argument data provided
     *
     * @param argumentFormat The primary argument format to interpret
     * @param args           The data to interpret
     * @return the parsable string
     */
    public String generateArgumentMessage(final String argumentFormat, final List<Pair<String, String>> args) {
        return generateArgumentMessage(argumentFormat, null, args);
    }

    /**
     * Generate a parsable display string for the argument data provided
     *
     * @param argumentFormat    The primary argument format to interpret
     * @param subArgumentFormat The secondary (or sub-prefix) argument format to interpret
     * @param args              The data to interpret
     * @return the parsable string
     */
    public String generateArgumentMessage(final String argumentFormat, final String subArgumentFormat, final Map<ArgumentType, List<String>> args) {
        return generateArgumentMessage(argumentFormat, subArgumentFormat, generateArgumentList(args));
    }

    /**
     * Generate a parsable display string for the argument data provided
     *
     * @param argumentFormat The primary argument format to interpret
     * @param args           The data to interpret
     * @return the parsable string
     */
    public String generateArgumentMessage(final String argumentFormat, final Map<ArgumentType, List<String>> args) {
        return generateArgumentMessage(argumentFormat, null, args);
    }

    /**
     * Generate a parsable display string for the argument data provided
     *
     * @param argumentFormat    The primary argument format to interpret
     * @param subArgumentFormat The secondary (or sub-prefix) argument format to interpret
     * @param type              The type the arguments should be retrieved from
     * @param allowNullEntries  Whether empty entry values should be interpreted
     * @param args              The data to interpret
     * @return the parsable string
     */
    public String generateArgumentMessage(final String argumentFormat, final String subArgumentFormat, final ArgumentType type, final boolean allowNullEntries, final String... args) {
        return generateArgumentMessage(argumentFormat, subArgumentFormat, convertToArgumentList(type, allowNullEntries, args));
    }

    /**
     * Generate a parsable display string for the argument data provided
     *
     * @param argumentFormat    The primary argument format to interpret
     * @param subArgumentFormat The secondary (or sub-prefix) argument format to interpret
     * @param type              The type the arguments should be retrieved from
     * @param args              The data to interpret
     * @return the parsable string
     */
    public String generateArgumentMessage(final String argumentFormat, final String subArgumentFormat, final ArgumentType type, final String... args) {
        return generateArgumentMessage(argumentFormat, subArgumentFormat, type, true, args);
    }

    /**
     * Generate a parsable display string for the argument data provided
     *
     * @param argumentFormat   The primary argument format to interpret
     * @param type             The type the arguments should be retrieved from
     * @param allowNullEntries Whether empty entry values should be interpreted
     * @param args             The data to interpret
     * @return the parsable string
     */
    public String generateArgumentMessage(final String argumentFormat, final ArgumentType type, final boolean allowNullEntries, final String... args) {
        return generateArgumentMessage(argumentFormat, null, type, allowNullEntries, args);
    }

    /**
     * Generate a parsable display string for the argument data provided
     *
     * @param argumentFormat The primary argument format to interpret
     * @param type           The type the arguments should be retrieved from
     * @param args           The data to interpret
     * @return the parsable string
     */
    public String generateArgumentMessage(final String argumentFormat, final ArgumentType type, final String... args) {
        return generateArgumentMessage(argumentFormat, type, true, args);
    }

    /**
     * Generate a parsable display string for the argument data provided
     *
     * @param argumentFormat    The primary argument format to interpret
     * @param subArgumentFormat The secondary (or sub-prefix) argument format to interpret
     * @param type              The type the arguments should be retrieved from
     * @param allowNullEntries  Whether empty entry values should be interpreted
     * @param args              The data to interpret
     * @return the parsable string
     */
    public String generateArgumentMessage(final String argumentFormat, final String subArgumentFormat, final ArgumentType type, final boolean allowNullEntries, final List<String> args) {
        return generateArgumentMessage(argumentFormat, subArgumentFormat, convertToArgumentList(type, allowNullEntries, args));
    }

    /**
     * Generate a parsable display string for the argument data provided
     *
     * @param argumentFormat    The primary argument format to interpret
     * @param subArgumentFormat The secondary (or sub-prefix) argument format to interpret
     * @param type              The type the arguments should be retrieved from
     * @param args              The data to interpret
     * @return the parsable string
     */
    public String generateArgumentMessage(final String argumentFormat, final String subArgumentFormat, final ArgumentType type, final List<String> args) {
        return generateArgumentMessage(argumentFormat, subArgumentFormat, type, true, args);
    }

    /**
     * Generate a parsable display string for the argument data provided
     *
     * @param argumentFormat   The primary argument format to interpret
     * @param type             The type the arguments should be retrieved from
     * @param allowNullEntries Whether empty entry values should be interpreted
     * @param args             The data to interpret
     * @return the parsable string
     */
    public String generateArgumentMessage(final String argumentFormat, final ArgumentType type, final boolean allowNullEntries, final List<String> args) {
        return generateArgumentMessage(argumentFormat, null, type, allowNullEntries, args);
    }

    /**
     * Generate a parsable display string for the argument data provided
     *
     * @param argumentFormat The primary argument format to interpret
     * @param type           The type the arguments should be retrieved from
     * @param args           The data to interpret
     * @return the parsable string
     */
    public String generateArgumentMessage(final String argumentFormat, final ArgumentType type, final List<String> args) {
        return generateArgumentMessage(argumentFormat, type, true, args);
    }

    /**
     * Convert a list of strings into a valid argument list
     *
     * @param type             The type the arguments should be retrieved from
     * @param allowNullEntries Whether empty entry values should be interpreted
     * @param args             The string formats to interpret
     * @return the resulting list of argument entries
     */
    public List<Pair<String, String>> convertToArgumentList(final ArgumentType type, final boolean allowNullEntries, final String... args) {
        final List<Pair<String, String>> result = Lists.newArrayList();
        final List<Pair<String, String>> existingArgs = getArgumentsMatching(type, allowNullEntries, args);
        for (String argumentName : args) {
            if (!existingArgs.isEmpty()) {
                for (Pair<String, String> entry : existingArgs) {
                    if (entry.getFirst().contains(argumentName)) {
                        result.add(entry);
                    }
                }
            } else {
                result.add(new Pair<>(argumentName, ""));
            }
        }
        return result;
    }

    /**
     * Convert a list of strings into a valid argument list
     *
     * @param type The type the arguments should be retrieved from
     * @param args The string formats to interpret
     * @return the resulting list of argument entries
     */
    public List<Pair<String, String>> convertToArgumentList(final ArgumentType type, final String... args) {
        return convertToArgumentList(type, true, args);
    }

    /**
     * Convert a list of strings into a valid argument list
     *
     * @param type             The type the arguments should be retrieved from
     * @param allowNullEntries Whether empty entry values should be interpreted
     * @param args             The string formats to interpret
     * @return the resulting list of argument entries
     */
    public List<Pair<String, String>> convertToArgumentList(final ArgumentType type, final boolean allowNullEntries, final List<String> args) {
        return convertToArgumentList(type, allowNullEntries, args.toArray(new String[0]));
    }

    /**
     * Convert a list of strings into a valid argument list
     *
     * @param type The type the arguments should be retrieved from
     * @param args The string formats to interpret
     * @return the resulting list of argument entries
     */
    public List<Pair<String, String>> convertToArgumentList(final ArgumentType type, final List<String> args) {
        return convertToArgumentList(type, true, args);
    }

    /**
     * Synchronizes and Updates Dynamic Placeholder data in this module
     */
    public void syncPlaceholders() {
        generalArgs.clear();
        modsArgs.clear();
        playerInfoArgs.clear();

        // Add Any Generalized Argument Data needed
        modsArgs.add(new Pair<>("&MODCOUNT&", Integer.toString(FileUtils.getModCount())));
        playerInfoArgs.add(new Pair<>("&NAME&", CraftPresence.session.inventory));

        generalArgs.add(new Pair<>("&MCVERSION&", ModUtils.TRANSLATOR.translate("craftpresence.defaults.state.mc.version", ModUtils.MCVersion)));
        generalArgs.add(new Pair<>("&BRAND&", ModUtils.BRAND));
        generalArgs.add(new Pair<>("&MODS&", StringUtils.sequentialReplaceAnyCase(CraftPresence.CONFIG.modsPlaceholderMessage, modsArgs)));
        generalArgs.add(new Pair<>("&IGN&", StringUtils.sequentialReplaceAnyCase(CraftPresence.CONFIG.outerPlayerPlaceholderMessage, playerInfoArgs)));

        for (Pair<String, String> generalArgument : generalArgs) {
            // For each General (Can be used Anywhere) Argument
            // Ensure they sync as Formatter Arguments too
            syncArgument(generalArgument.getFirst(), generalArgument.getSecond(), ArgumentType.Text);
        }

        // Add applicable args as sub-placeholders
        for (Pair<String, String> argumentData : modsArgs) {
            syncArgument("&MODS:" + argumentData.getFirst().substring(1), argumentData.getSecond(), ArgumentType.Text);
        }
        for (Pair<String, String> argumentData : playerInfoArgs) {
            syncArgument("&IGN:" + argumentData.getFirst().substring(1), argumentData.getSecond(), ArgumentType.Text);
        }

        // Sync the Default Icon Argument
        syncArgument("&DEFAULT&", CraftPresence.CONFIG.defaultIcon, ArgumentType.Image);
        syncPackArguments();
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
     * @param allowNull   If allowed to return null if unable to find any matches, otherwise uses the Default Icon in Config
     * @param showLogging Whether or not to display logging for this function
     * @param evalStrings The Specified Icon Key(s) to search for from the {@link DiscordUtils#CLIENT_ID} Assets
     * @return The found or alternative matching Icon Key
     */
    public String imageOf(final boolean allowNull, final boolean showLogging, final String... evalStrings) {
        // Ensures Assets were fully synced from the Client ID before running
        String result;
        if (DiscordAssetUtils.syncCompleted && !StringUtils.isNullOrEmpty(evalStrings[0])) {
            final String primaryKey = evalStrings[0];
            if (!cachedImageData.containsKey(primaryKey)) {
                final String defaultIcon = allowNull ? "" : (DiscordAssetUtils.contains(CraftPresence.CONFIG.defaultIcon) ? CraftPresence.CONFIG.defaultIcon : DiscordAssetUtils.getRandomAssetName());
                String finalKey = defaultIcon;
                for (int i = 0; i < evalStrings.length; ) {
                    final String evalString = evalStrings[i];
                    if (DiscordAssetUtils.contains(evalString)) {
                        if (showLogging && !evalString.equals(primaryKey)) {
                            ModUtils.LOG.info(ModUtils.TRANSLATOR.translate(true, "craftpresence.logger.info.discord.assets.fallback", primaryKey, evalString));
                        }
                        finalKey = evalString;
                        break;
                    } else {
                        if (i++ < evalStrings.length) {
                            if (showLogging) {
                                ModUtils.LOG.error(ModUtils.TRANSLATOR.translate(true, "craftpresence.logger.error.discord.assets.fallback", evalString, evalStrings[i]));
                                if (evalString.equals(primaryKey)) {
                                    ModUtils.LOG.info(ModUtils.TRANSLATOR.translate(true, "craftpresence.logger.info.discord.assets.request", evalString));
                                }
                            }
                        } else {
                            if (showLogging) {
                                ModUtils.LOG.info(ModUtils.TRANSLATOR.translate(true, "craftpresence.logger.error.discord.assets.default", primaryKey, defaultIcon));
                            }
                            finalKey = defaultIcon;
                        }
                    }
                }

                cachedImageData.put(primaryKey, finalKey);
                result = finalKey;
            } else {
                result = cachedImageData.get(primaryKey);
                if (StringUtils.isNullOrEmpty(lastRequestedImageData.getFirst()) || !lastRequestedImageData.getFirst().equals(primaryKey)) {
                    if (showLogging && !result.equals(primaryKey)) {
                        ModUtils.LOG.error(ModUtils.TRANSLATOR.translate(true, "craftpresence.logger.error.discord.assets.cached", primaryKey, result));
                        ModUtils.LOG.info(ModUtils.TRANSLATOR.translate(true, "craftpresence.logger.info.discord.assets.request", primaryKey));
                    }
                }
            }
            lastRequestedImageData.setFirst(primaryKey);
            lastRequestedImageData.setSecond(result);
        } else {
            result = "";
        }
        return result;
    }

    /**
     * Attempts to lookup the specified Image, and if not existent, use the alternative String, and null if allowed
     *
     * @param argumentName The Specified Argument to interpret
     * @param allowNull    If allowed to return null if unable to find any matches, otherwise uses the Default Icon in Config
     * @param evalStrings  The Specified Icon Key(s) to search for from the {@link DiscordUtils#CLIENT_ID} Assets
     * @return The found or alternative matching Icon Key
     */
    public String imageOf(final String argumentName, final boolean allowNull, final String... evalStrings) {
        return imageOf(allowNull, isImageInUse(argumentName) || isImageInUse(evalStrings), evalStrings);
    }

    /**
     * Attempts to lookup the specified Image, and if not existent, use the alternative String, and null if allowed
     *
     * @param allowNull   If allowed to return null if unable to find any matches, otherwise uses the Default Icon in Config
     * @param evalStrings The Specified Icon Key(s) to search for from the {@link DiscordUtils#CLIENT_ID} Assets
     * @return The found or alternative matching Icon Key
     */
    public String imageOf(final boolean allowNull, final String... evalStrings) {
        return imageOf(allowNull, true, evalStrings);
    }

    /**
     * Determine whether any of the specified strings are currently being used as an RPC image
     *
     * @param evalStrings The specified Icon Key(s) to interpret
     * @return whether any of the inputs are currently being used as an RPC image
     */
    public boolean isImageInUse(final String... evalStrings) {
        for (String evalString : evalStrings) {
            if (CraftPresence.CONFIG.largeImageKey.contains(evalString) ||
                    CraftPresence.CONFIG.smallImageKey.contains(evalString)
            ) {
                return true;
            }
        }
        return false;
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
        LARGE_IMAGE_ASSET = null;
        LARGE_IMAGE_KEY = "";
        LARGE_IMAGE_TEXT = "";
        SMALL_IMAGE_ASSET = null;
        SMALL_IMAGE_KEY = "";
        SMALL_IMAGE_TEXT = "";
        BUTTONS = new JsonArray();

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
            cachedImageData.clear();

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
        DETAILS = StringUtils.formatWord(parseArgumentOperators(CraftPresence.CONFIG.detailsMessage, ArgumentType.Text), !CraftPresence.CONFIG.formatWords, true, 1);
        GAME_STATE = StringUtils.formatWord(parseArgumentOperators(CraftPresence.CONFIG.gameStateMessage, ArgumentType.Text), !CraftPresence.CONFIG.formatWords, true, 1);

        LARGE_IMAGE_ASSET = DiscordAssetUtils.get(parseArgumentOperators(CraftPresence.CONFIG.largeImageKey, ArgumentType.Image));
        SMALL_IMAGE_ASSET = DiscordAssetUtils.get(parseArgumentOperators(CraftPresence.CONFIG.smallImageKey, ArgumentType.Image));

        LARGE_IMAGE_KEY = LARGE_IMAGE_ASSET != null ? (LARGE_IMAGE_ASSET.getType().equals(DiscordAsset.AssetType.CUSTOM) ?
                parseArgumentOperators(LARGE_IMAGE_ASSET.getUrl(), ArgumentType.Text) : LARGE_IMAGE_ASSET.getName()) : "";
        SMALL_IMAGE_KEY = SMALL_IMAGE_ASSET != null ? (SMALL_IMAGE_ASSET.getType().equals(DiscordAsset.AssetType.CUSTOM) ?
                parseArgumentOperators(SMALL_IMAGE_ASSET.getUrl(), ArgumentType.Text) : SMALL_IMAGE_ASSET.getName()) : "";

        LARGE_IMAGE_TEXT = StringUtils.formatWord(parseArgumentOperators(CraftPresence.CONFIG.largeImageMessage, ArgumentType.Text), !CraftPresence.CONFIG.formatWords, true, 1);
        SMALL_IMAGE_TEXT = StringUtils.formatWord(parseArgumentOperators(CraftPresence.CONFIG.smallImageMessage, ArgumentType.Text), !CraftPresence.CONFIG.formatWords, true, 1);

        // Format Buttons Array based on Config Value
        BUTTONS = new JsonArray();
        for (String buttonElement : CraftPresence.CONFIG.buttonMessages) {
            if (!StringUtils.isNullOrEmpty(buttonElement)) {
                final String[] part = buttonElement.split(CraftPresence.CONFIG.splitCharacter);
                JsonObject buttonObj = new JsonObject();
                if (part.length == 3 && !StringUtils.isNullOrEmpty(part[0]) && !part[0].equalsIgnoreCase("default") && !StringUtils.isNullOrEmpty(part[1])) {
                    String label = StringUtils.formatWord(
                            parseArgumentOperators(part[1], ArgumentType.Text),
                            !CraftPresence.CONFIG.formatWords, true, 1
                    );
                    String url = !StringUtils.isNullOrEmpty(part[2]) ? parseArgumentOperators(
                            part[2], ArgumentType.Text
                    ) : "";

                    label = sanitizePlaceholders(label);
                    url = sanitizePlaceholders(url);
                    if (!StringUtils.isNullOrEmpty(label) && !StringUtils.isNullOrEmpty(url)) {
                        buttonObj.addProperty("label", label);
                        buttonObj.addProperty("url", url);
                        BUTTONS.add(buttonObj);
                    }
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
                .setParty(PARTY_ID, PARTY_SIZE, PARTY_MAX, PARTY_PRIVACY.ordinal())
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

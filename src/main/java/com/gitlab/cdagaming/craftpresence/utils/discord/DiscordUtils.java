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
import com.gitlab.cdagaming.craftpresence.config.Config;
import com.gitlab.cdagaming.craftpresence.config.element.Button;
import com.gitlab.cdagaming.craftpresence.config.element.ModuleData;
import com.gitlab.cdagaming.craftpresence.config.element.PresenceData;
import com.gitlab.cdagaming.craftpresence.impl.Pair;
import com.gitlab.cdagaming.craftpresence.impl.Tuple;
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
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jagrosh.discordipc.IPCClient;
import com.jagrosh.discordipc.entities.DiscordBuild;
import com.jagrosh.discordipc.entities.RichPresence;
import com.jagrosh.discordipc.entities.User;
import com.jagrosh.discordipc.entities.pipe.PipeStatus;
import meteordevelopment.starscript.Script;
import meteordevelopment.starscript.StandardLib;
import meteordevelopment.starscript.Starscript;
import meteordevelopment.starscript.compiler.Compiler;
import meteordevelopment.starscript.compiler.Parser;
import meteordevelopment.starscript.utils.Error;
import meteordevelopment.starscript.utils.VariableReplacementTransformer;
import meteordevelopment.starscript.value.Value;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Supplier;

/**
 * Variables and Methods used to update the RPC Presence States to display within Discord
 *
 * @author CDAGaming
 */
@SuppressWarnings("unchecked")
public class DiscordUtils {
    /**
     * A mapping of the arguments that have overriden module data
     */
    private final Map<String, ModuleData> overrideData = Maps.newHashMap();
    /**
     * A Mapping of the Arguments available to use as RPC Message Placeholders
     */
    private final TreeMap<String, Supplier<Value>> placeholderData = Maps.newTreeMap();
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
     * An Instance of the {@link Starscript} engine, responsible for parsing and compining expressions
     */
    public Starscript scriptEngine = new Starscript();
    /**
     * An Instance of the {@link IPCClient}, responsible for sending and receiving RPC Events
     */
    public IPCClient ipcInstance;
    /**
     * Whether Discord is currently awaiting a response to a Ask to Join or Spectate Request, if any
     */
    public boolean awaitingReply = false;
    /**
     * When this is not null, {@link DiscordUtils#buildRichPresence(PresenceData)} will use this data instead of the generic data
     */
    public PresenceData forcedData = null;
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
            scriptEngine = new Starscript();
            StandardLib.init(scriptEngine);
            // TODO: Add some of our functions to the engine

            ipcInstance = new IPCClient(Long.parseLong(CLIENT_ID), debugMode, verboseMode, AUTO_REGISTER, CLIENT_ID);
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

        // Ensure Initial Data Resets properly
        overrideData.clear();
        placeholderData.clear();
        CommandUtils.isInMainMenu = false;
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
     * @param list The list to interpret
     * @return the output list
     */
    public List<String> createButtonsList(final Map<String, Button> list) {
        final List<String> result = Lists.newArrayList();
        for (String buttonEntry : list.keySet()) {
            if (!StringUtils.isNullOrEmpty(buttonEntry)) {
                result.add(buttonEntry);
            }
        }
        return result;
    }

    /**
     * Creates a string-based representation of the button-list, from config values
     *
     * @return the output list
     */
    public List<String> createButtonsList() {
        return createButtonsList(getPresenceData().buttons);
    }

    /**
     * Removes any invalid data from a placeholder argument
     *
     * @param input The string to interpret
     * @return The resulting output string
     */
    public String sanitizePlaceholders(final String input) {
        return StringUtils.getOrDefault(input).trim();
    }

    /**
     * Compiles and Parses the specified input, via {@link Starscript}
     *
     * @param input The input expression to interpret
     * @param plain Whether the expression should be parsed as a plain string
     * @return the supplier containing the output
     */
    public Supplier<Value> compileData(final String input, final String overrideId, final boolean plain, final Pair<String, Supplier<String>>... replacements) {
        final TreeMap<String, Supplier<Value>> placeholders = Maps.newTreeMap(placeholderData);
        String data = StringUtils.getOrDefault(input);

        if (!plain) {
            // Perform variable replacement before compilation
            final VariableReplacementTransformer transformer = new VariableReplacementTransformer();

            // Phase 1: Override System
            if (!StringUtils.isNullOrEmpty(overrideId)) {
                for (String placeholderName : placeholders.keySet()) {
                    if (!placeholderName.startsWith("overrides.")) {
                        transformer.addReplacer(placeholderName, () -> {
                            final String overrideName = "overrides." + placeholderName + "." + overrideId;
                            return placeholders.containsKey(overrideName) &&
                                    !StringUtils.isNullOrEmpty(
                                            placeholders.get(overrideName).get().toString()
                                    ) ? overrideName : placeholderName;
                        });
                    }
                }
            }
            // Phase 2: args field (Pair<String, Supplier<String>>...)
            for (Pair<String, Supplier<String>> replacement : replacements) {
                final String value = replacement.getSecond().get();
                if (placeholders.containsKey(value)) {
                    transformer.addReplacer(replacement.getFirst(), replacement.getSecond());
                } else {
                    data = data.replace(
                            replacement.getFirst(),
                            !StringUtils.isNullOrEmpty(value) ? "'" + value + "'" : "null"
                    );
                }
            }

            Parser.Result result = null;
            try {
                result = Parser.parse(data);
            } catch (Exception ignored) {
            }

            if (result == null || result.hasErrors()) {
                if (result != null) {
                    // TODO: Proper Logging
                    for (Error error : result.errors) ModUtils.LOG.error(error.toString());
                }
                return () -> Value.string("");
            }
            result.accept(transformer);

            final Script script = Compiler.compile(result);
            return () -> Value.string(new Starscript(scriptEngine).run(script).toString());
        } else {
            String finalData = data;
            return () -> Value.string(finalData);
        }
    }

    /**
     * Retrieve the output from the execution of {@link DiscordUtils#compileData(String, String, boolean, Pair[])}
     *
     * @param input The input expression to interpret
     * @param plain Whether the expression should be parsed as a plain string
     * @return the result of the supplier containing the output
     */
    public String getResult(final String input, final String overrideId, final boolean plain, final Pair<String, Supplier<String>>... replacements) {
        return compileData(input, overrideId, plain, replacements).get().toString();
    }

    /**
     * Compiles and Parses the specified input, via {@link Starscript}
     *
     * @param input The input expression to interpret
     * @return the supplier containing the output
     */
    public Supplier<Value> compileData(final String input, final String overrideId, final Pair<String, Supplier<String>>... replacements) {
        return compileData(input, overrideId, false, replacements);
    }

    /**
     * Retrieve the output from the execution of {@link DiscordUtils#compileData(String, String, Pair[])}
     *
     * @param input The input expression to interpret
     * @return the result of the supplier containing the output
     */
    public String getResult(final String input, final String overrideId, final Pair<String, Supplier<String>>... replacements) {
        return getResult(input, overrideId, false, replacements);
    }

    /**
     * Compiles and Parses the specified input, via {@link Starscript}
     *
     * @param input The input expression to interpret
     * @param plain Whether the expression should be parsed as a plain string
     * @return the supplier containing the output
     */
    public Supplier<Value> compileData(final String input, final boolean plain, final Pair<String, Supplier<String>>... replacements) {
        return compileData(input, null, plain, replacements);
    }

    /**
     * Retrieve the output from the execution of {@link DiscordUtils#compileData(String, boolean, Pair[])}
     *
     * @param input The input expression to interpret
     * @param plain Whether the expression should be parsed as a plain string
     * @return the result of the supplier containing the output
     */
    public String getResult(final String input, final boolean plain, final Pair<String, Supplier<String>>... replacements) {
        return getResult(input, null, plain, replacements);
    }

    /**
     * Compiles and Parses the specified input, via {@link Starscript}
     *
     * @param input The input expression to interpret
     * @return the supplier containing the output
     */
    public Supplier<Value> compileData(final String input, final Pair<String, Supplier<String>>... replacements) {
        return compileData(input, false, replacements);
    }

    /**
     * Retrieve the output from the execution of {@link DiscordUtils#compileData(String, Pair[])}
     *
     * @param input The input expression to interpret
     * @return the result of the supplier containing the output
     */
    public String getResult(final String input, final Pair<String, Supplier<String>>... replacements) {
        return getResult(input, false, replacements);
    }

    /**
     * Updates the Starting Unix Timestamp, if allowed
     */
    public void updateTimestamp() {
        if (CraftPresence.CONFIG.generalSettings.showTime) {
            START_TIMESTAMP = System.currentTimeMillis() / 1000L;
        }
    }

    /**
     * Sync {@link ModuleData} overrides for the specified placeholder(s)
     *
     * @param data The data to attach to the specified argument
     * @param args The Specified Arguments to Synchronize for
     */
    public void syncOverride(ModuleData data, String... args) {
        PresenceData presenceInfo = null;
        if (data != null && Config.getProperty(data, "data") != null) {
            presenceInfo = data.getData();
        }
        final boolean isPresenceOn = presenceInfo != null && presenceInfo.enabled;

        for (String argumentName : args) {
            if (!StringUtils.isNullOrEmpty(argumentName)) {
                overrideData.put(argumentName, data);

                final String prefix = "overrides." + argumentName;
                if (isPresenceOn && !presenceInfo.useAsMain) {
                    syncArgument(prefix + ".details", presenceInfo.details);
                    syncArgument(prefix + ".gameState", presenceInfo.gameState);
                    syncArgument(prefix + ".largeImageKey", presenceInfo.largeImageKey);
                    syncArgument(prefix + ".largeImageText", presenceInfo.largeImageText);
                    syncArgument(prefix + ".smallImageKey", presenceInfo.smallImageKey);
                    syncArgument(prefix + ".smallImageText", presenceInfo.smallImageText);

                    int buttonIndex = 1;
                    for (Button buttonInfo : presenceInfo.buttons.values()) {
                        final String buttonId = "button_" + buttonIndex;
                        syncArgument(prefix + "." + buttonId + ".label", buttonInfo.label);
                        syncArgument(prefix + "." + buttonId + ".url", buttonInfo.url);
                        buttonIndex++;
                    }
                } else {
                    removeArguments(prefix);
                }
            }
        }
        if (isPresenceOn && presenceInfo.useAsMain) {
            forcedData = data.getData();
        }
    }

    /**
     * Sync {@link ModuleData} overrides for the specified placeholder
     *
     * @param argumentName The Specified Argument to Synchronize for
     * @param data         The data to attach to the specified argument
     */
    public void syncOverride(String argumentName, ModuleData data) {
        syncOverride(data, argumentName);
    }

    /**
     * Remove {@link ModuleData} overrides for the specified placeholder
     *
     * @param args The Specified Argument(s) to interpret
     */
    public void clearOverride(String... args) {
        for (String argumentName : args) {
            if (!StringUtils.isNullOrEmpty(argumentName) && overrideData.containsKey(argumentName)) {
                final ModuleData oldData = overrideData.get(argumentName);
                overrideData.remove(argumentName);

                if (oldData != null && Config.getProperty(oldData, "data") != null) {
                    if (oldData.getData().equals(forcedData)) {
                        forcedData = null;
                    }
                }
            }
        }
    }

    /**
     * Synchronizes the Specified Argument as an RPC Message or an Icon Placeholder
     *
     * @param argumentName The Specified Argument to Synchronize for
     * @param insertString The String to attach to the Specified Argument
     * @param plain        Whether the expression should be parsed as a plain string
     */
    public void syncArgument(String argumentName, String insertString, final boolean plain) {
        if (!StringUtils.isNullOrEmpty(argumentName)) {
            final Supplier<Value> value = compileData(insertString, plain);
            scriptEngine.set(argumentName, value);
            placeholderData.put(argumentName, value);
        }
    }

    /**
     * Synchronizes the Specified Argument as an RPC Message or an Icon Placeholder
     *
     * @param argumentName The Specified Argument to Synchronize for
     * @param insertString The String to attach to the Specified Argument
     */
    public void syncArgument(String argumentName, String insertString) {
        syncArgument(argumentName, insertString, false);
    }

    /**
     * Initialize the Specified Arguments as Empty Data
     *
     * @param args The Arguments to Initialize
     */
    public void initArgument(String... args) {
        // Initialize Specified Arguments to Empty Data
        for (String argumentName : args) {
            syncArgument(argumentName, "");
        }
    }

    /**
     * Remove any arguments following the specified formats within the selected Argument Type
     *
     * @param args The string formats to interpret
     */
    public void removeArguments(final String... args) {
        final List<String> items = Lists.newArrayList(placeholderData.keySet());
        for (String key : items) {
            for (String format : args) {
                if (key.startsWith(format)) {
                    scriptEngine.remove(key);
                    placeholderData.remove(key);
                    break;
                }
            }
        }
    }

    /**
     * Retrieves any arguments within the specified type that match the specified string formats
     *
     * @param allowNullEntries Whether empty entry values should be interpreted
     * @param args             The string formats to interpret
     * @return A List of the entries that satisfy the method conditions
     */
    public Map<String, String> getArguments(final boolean allowNullEntries, final String... args) {
        final TreeMap<String, Supplier<Value>> items = Maps.newTreeMap(placeholderData);
        final Map<String, String> list = Maps.newTreeMap();

        for (Map.Entry<String, Supplier<Value>> entry : items.entrySet()) {
            final String item = entry.getKey();
            boolean addToList = args == null || args.length < 1 || args[0] == null;
            if (!addToList) {
                for (String argument : args) {
                    if (!StringUtils.isNullOrEmpty(argument) && item.startsWith(argument)) {
                        addToList = true;
                        break;
                    }
                }
            }
            if (addToList) {
                final String value = entry.getValue().get().toString();
                if (allowNullEntries || !StringUtils.isNullOrEmpty(value)) {
                    list.put(item, value);
                }
            }
        }
        return list;
    }

    /**
     * Retrieves any arguments within the specified type that match the specified string formats
     *
     * @param args The string formats to interpret
     * @return A List of the entries that satisfy the method conditions
     */
    public Map<String, String> getArguments(final String... args) {
        return getArguments(true, args);
    }

    /**
     * Retrieves any argument entries within the specified type that match the specified string formats
     *
     * @param formatToLower    Whether to lower-cases the resulting entries
     * @param allowNullEntries Whether empty entry values should be interpreted
     * @param args             The string formats to interpret
     * @return A List of the entries that satisfy the method conditions
     */
    public List<String> getArgumentEntries(final boolean formatToLower, final boolean allowNullEntries, final String... args) {
        final Map<String, String> list = getArguments(allowNullEntries, args);
        final List<String> result = Lists.newArrayList();
        for (String item : list.keySet()) {
            result.add(formatToLower ? item.toLowerCase() : item);
        }
        return result;
    }

    /**
     * Retrieves any argument entries within the specified type that match the specified string formats
     *
     * @param formatToLower Whether to lower-cases the resulting entries
     * @param args          The string formats to interpret
     * @return A List of the entries that satisfy the method conditions
     */
    public List<String> getArgumentEntries(final boolean formatToLower, final String... args) {
        return getArgumentEntries(formatToLower, true, args);
    }

    /**
     * Retrieves any argument entries within the specified type that match the specified string formats
     *
     * @param allowNullEntries Whether empty entry values should be interpreted
     * @param args             The string formats to interpret
     * @return A List of the entries that satisfy the method conditions
     */
    public List<String> getRawArgumentEntries(final boolean allowNullEntries, final String... args) {
        return getArgumentEntries(false, allowNullEntries, args);
    }

    /**
     * Retrieves any argument entries within the specified type that match the specified string formats
     *
     * @param args The string formats to interpret
     * @return A List of the entries that satisfy the method conditions
     */
    public List<String> getRawArgumentEntries(final String... args) {
        return getRawArgumentEntries(true, args);
    }

    /**
     * Determines whether there are any matching arguments within the specified type matching the specified string formats
     *
     * @param allowNullEntries Whether empty entry values should be interpreted
     * @param args             The string formats to interpret
     * @return Whether the resulting list has any matching entries
     */
    public boolean hasArgumentsMatching(final boolean allowNullEntries, final String... args) {
        return !getArguments(allowNullEntries, args).isEmpty();
    }

    /**
     * Determines whether there are any matching arguments within the specified type matching the specified string formats
     *
     * @param args The string formats to interpret
     * @return Whether the resulting list has any matching entries
     */
    public boolean hasArgumentsMatching(final String... args) {
        return hasArgumentsMatching(true, args);
    }

    /**
     * Generate a parsable display string for the argument data provided
     *
     * @param argumentFormat The primary argument format to interpret
     * @param addExtraData   Whether to add additional data to the string
     * @param args           The data to interpret
     * @return the parsable string
     */
    public String generateArgumentMessage(final String argumentFormat, final boolean addExtraData, final Map<String, String> args) {
        final String titleString = String.format("%s%s:",
                ModUtils.TRANSLATOR.translate(
                        String.format("%s.placeholders.title", ModUtils.MOD_ID)
                ), (!StringUtils.isNullOrEmpty(argumentFormat) ? (" (" + argumentFormat.toLowerCase() + ")") : "")
        );
        final StringBuilder placeholderString = new StringBuilder();
        if (args != null && !args.isEmpty()) {
            for (Map.Entry<String, String> argData : args.entrySet()) {
                final String placeholderName = argData.getKey();
                final String placeholderTranslation = String.format("%s.placeholders.%s.description",
                        ModUtils.MOD_ID,
                        placeholderName
                );
                String placeholderDescription = "";
                String placeholderFormat = "\\n - %s";

                if (ModUtils.TRANSLATOR.hasTranslation(placeholderTranslation)) {
                    placeholderDescription = ModUtils.TRANSLATOR.translate(placeholderTranslation);
                    placeholderFormat = "\\n - %s = %s";
                }

                placeholderString.append(
                        String.format(placeholderFormat,
                                placeholderName.toLowerCase(),
                                placeholderDescription
                        )
                );

                if (addExtraData && !StringUtils.isNullOrEmpty(argData.getValue())) {
                    final String tagValue = argData.getValue();
                    placeholderString.append(String.format("\\n ==> %s \"%s\"",
                            ModUtils.TRANSLATOR.translate("gui.config.message.editor.preview"),
                            (tagValue.length() >= 128) ? "<...>" : tagValue
                    ));
                }
            }
        }

        if (placeholderString.length() == 0) {
            placeholderString.append("\\n - N/A");
        }
        return titleString + placeholderString;
    }

    /**
     * Generate a parsable display string for the argument data provided
     *
     * @param argumentFormat The primary argument format to interpret
     * @param addExtraData   Whether to add additional data to the string
     * @return the parsable string
     */
    public String generateArgumentMessage(final String argumentFormat, final boolean addExtraData) {
        return generateArgumentMessage(argumentFormat, addExtraData, getArguments(argumentFormat));
    }

    /**
     * Generate a parsable display string for the argument data provided
     *
     * @param argumentFormat The primary argument format to interpret
     * @return the parsable string
     */
    public String generateArgumentMessage(final String argumentFormat) {
        return generateArgumentMessage(argumentFormat, CraftPresence.CONFIG.advancedSettings.allowPlaceholderPreviews);
    }

    /**
     * Generate a parsable display string for the argument data provided
     *
     * @return the parsable string
     */
    public String generateArgumentMessage() {
        return generateArgumentMessage(null);
    }

    /**
     * Synchronizes the `pack` Argument, based on any found Launcher Pack/Instance Data
     */
    private void syncPackArguments() {
        String foundPackName = "", foundPackIcon = "";

        if (!StringUtils.isNullOrEmpty(CurseUtils.INSTANCE_NAME)) {
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
        }

        if (!StringUtils.isNullOrEmpty(foundPackName)) {
            syncArgument("pack.name", foundPackName);
            syncArgument("pack.icon", imageOf("pack.icon", true, foundPackIcon));
        } else {
            removeArguments("pack");
        }
    }

    /**
     * Synchronizes and Updates Dynamic Placeholder data in this module
     */
    public void syncPlaceholders() {
        // Sync Custom Variables
        removeArguments("custom.");
        for (Map.Entry<String, String> entry : CraftPresence.CONFIG.displaySettings.dynamicVariables.entrySet()) {
            if (!entry.getKey().equals("default")) {
                syncArgument("custom." + entry.getKey(), entry.getValue());
            }
        }
        // Add Any Generalized Argument Data needed
        final String playerName = CraftPresence.session.getUsername();
        syncArgument("general.mods", Integer.toString(FileUtils.getModCount()));
        syncArgument("player.name", playerName);

        // UUID Data
        final String uniqueId = CraftPresence.session.getPlayerID();
        if (StringUtils.isValidUuid(uniqueId)) {
            syncArgument("player.uuid", StringUtils.getFromUuid(uniqueId, true));
            syncArgument("player.uuid.full", StringUtils.getFromUuid(uniqueId, false));
        }

        if (CraftPresence.CONFIG.advancedSettings.allowEndpointIcons &&
                !StringUtils.isNullOrEmpty(CraftPresence.CONFIG.advancedSettings.playerSkinEndpoint)) {
            final String playerIcon = compileData(String.format(
                    CraftPresence.CONFIG.advancedSettings.playerSkinEndpoint,
                    StringUtils.getOrDefault(uniqueId, playerName)
            )).get().toString();
            if (!CraftPresence.CONFIG.displaySettings.dynamicIcons.containsKey(playerName)) {
                CraftPresence.CONFIG.displaySettings.dynamicIcons.put(playerName, playerIcon);
                DiscordAssetUtils.syncCustomAssets();
                CraftPresence.CONFIG.save();
            }

            syncArgument("player.icon", playerName);
        }

        syncArgument("general.version", ModUtils.MCVersion);
        syncArgument("general.brand", ModUtils.BRAND);

        // Sync the Default Icon Argument
        syncArgument("general.icon", CraftPresence.CONFIG.generalSettings.defaultIcon);
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
     * Synchronizes and Updates the Rich Presence Data, if needed and connected
     */
    public void updatePresence() {
        updatePresence(buildRichPresence());
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
                final String defaultIcon = allowNull ? "" : (DiscordAssetUtils.contains(CraftPresence.CONFIG.generalSettings.defaultIcon) ? CraftPresence.CONFIG.generalSettings.defaultIcon : DiscordAssetUtils.getRandomAssetName());
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
                        i++;
                        if (i < evalStrings.length) {
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
        final PresenceData configData = getPresenceData();
        for (String evalString : evalStrings) {
            if (configData.largeImageKey.contains(evalString) ||
                    configData.smallImageKey.contains(evalString)
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
            updatePresence();
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
     * @param configData The {@link PresenceData} to be interpreted
     * @return A New Instance of {@link RichPresence}
     */
    public RichPresence buildRichPresence(final PresenceData configData) {
        // Format Presence based on Arguments available in argumentData
        DETAILS = StringUtils.formatWord(getResult(configData.details, "details"), !CraftPresence.CONFIG.advancedSettings.formatWords, true, 1);
        GAME_STATE = StringUtils.formatWord(getResult(configData.gameState, "gameState"), !CraftPresence.CONFIG.advancedSettings.formatWords, true, 1);

        LARGE_IMAGE_ASSET = DiscordAssetUtils.get(getResult(configData.largeImageKey, "largeImageKey"));
        SMALL_IMAGE_ASSET = DiscordAssetUtils.get(getResult(configData.smallImageKey, "smallImageKey"));

        LARGE_IMAGE_KEY = LARGE_IMAGE_ASSET != null ? (LARGE_IMAGE_ASSET.getType().equals(DiscordAsset.AssetType.CUSTOM) ?
                getResult(LARGE_IMAGE_ASSET.getUrl()) : LARGE_IMAGE_ASSET.getName()) : "";
        SMALL_IMAGE_KEY = SMALL_IMAGE_ASSET != null ? (SMALL_IMAGE_ASSET.getType().equals(DiscordAsset.AssetType.CUSTOM) ?
                getResult(SMALL_IMAGE_ASSET.getUrl()) : SMALL_IMAGE_ASSET.getName()) : "";

        LARGE_IMAGE_TEXT = StringUtils.formatWord(getResult(configData.largeImageText, "largeImageText"), !CraftPresence.CONFIG.advancedSettings.formatWords, true, 1);
        SMALL_IMAGE_TEXT = StringUtils.formatWord(getResult(configData.smallImageText, "smallImageText"), !CraftPresence.CONFIG.advancedSettings.formatWords, true, 1);

        // Format Buttons Array based on Config Value
        BUTTONS = new JsonArray();
        for (Map.Entry<String, Button> buttonElement : configData.buttons.entrySet()) {
            JsonObject buttonObj = new JsonObject();
            String overrideId = "button_" + (BUTTONS.size() + 1);
            if (!StringUtils.isNullOrEmpty(buttonElement.getKey()) &&
                    !buttonElement.getKey().equalsIgnoreCase("default") &&
                    !StringUtils.isNullOrEmpty(buttonElement.getValue().label)) {
                String label = StringUtils.formatWord(
                        getResult(buttonElement.getValue().label, overrideId + ".label"),
                        !CraftPresence.CONFIG.advancedSettings.formatWords, true, 1
                );
                String url = !StringUtils.isNullOrEmpty(buttonElement.getValue().url) ? getResult(
                        buttonElement.getValue().url, overrideId + ".label"
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

    /**
     * Builds a New Instance of {@link RichPresence} based on Queued Data
     *
     * @return A New Instance of {@link RichPresence}
     */
    public RichPresence buildRichPresence() {
        return buildRichPresence(getPresenceData());
    }

    /**
     * Retrieve the current {@link PresenceData} being used for {@link RichPresence} builders
     *
     * @return the currently used instance of {@link PresenceData}
     */
    public PresenceData getPresenceData() {
        return forcedData != null ? forcedData : CraftPresence.CONFIG.displaySettings.presenceData;
    }
}

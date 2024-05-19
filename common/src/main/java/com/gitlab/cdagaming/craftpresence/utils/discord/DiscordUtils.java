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

package com.gitlab.cdagaming.craftpresence.utils.discord;

import com.gitlab.cdagaming.craftpresence.CraftPresence;
import com.gitlab.cdagaming.craftpresence.ModUtils;
import com.gitlab.cdagaming.craftpresence.config.Config;
import com.gitlab.cdagaming.craftpresence.core.Constants;
import com.gitlab.cdagaming.craftpresence.core.config.element.Button;
import com.gitlab.cdagaming.craftpresence.core.config.element.PresenceData;
import com.gitlab.cdagaming.craftpresence.core.impl.discord.DiscordStatus;
import com.gitlab.cdagaming.craftpresence.core.impl.discord.PartyPrivacy;
import com.gitlab.cdagaming.craftpresence.integrations.discord.FunctionsLib;
import com.gitlab.cdagaming.craftpresence.utils.CommandUtils;
import com.gitlab.cdagaming.craftpresence.utils.discord.assets.DiscordAsset;
import com.gitlab.cdagaming.craftpresence.utils.discord.assets.DiscordAssetUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jagrosh.discordipc.IPCClient;
import com.jagrosh.discordipc.entities.DiscordBuild;
import com.jagrosh.discordipc.entities.RichPresence;
import com.jagrosh.discordipc.entities.User;
import com.jagrosh.discordipc.entities.pipe.PipeStatus;
import com.jagrosh.discordipc.exceptions.NoDiscordClientException;
import io.github.cdagaming.unicore.impl.Pair;
import io.github.cdagaming.unicore.utils.FileUtils;
import io.github.cdagaming.unicore.utils.ScheduleUtils;
import io.github.cdagaming.unicore.utils.StringUtils;
import io.github.cdagaming.unicore.utils.TimeUtils;
import org.meteordev.starscript.Script;
import org.meteordev.starscript.Section;
import org.meteordev.starscript.Starscript;
import org.meteordev.starscript.compiler.Compiler;
import org.meteordev.starscript.compiler.Expr;
import org.meteordev.starscript.compiler.Parser;
import org.meteordev.starscript.utils.Error;
import org.meteordev.starscript.utils.SFunction;
import org.meteordev.starscript.utils.VariableReplacementTransformer;
import org.meteordev.starscript.value.Value;
import org.meteordev.starscript.value.ValueMap;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Variables and Methods used to update the RPC Presence States to display within Discord
 *
 * @author CDAGaming
 */
public class DiscordUtils {
    /**
     * A Mapping of the Arguments available to use as RPC Message Placeholders
     */
    private final Map<String, Supplier<Value>> placeholderData = StringUtils.newTreeMap();
    /**
     * A Mapping of the Last Requested Image Data
     * <p>Used to cache data for repeated images in other areas
     * <p>Format: evalKey, resultingKey
     */
    private final Map<String, String> cachedImageData = StringUtils.newHashMap();
    /**
     * When this is not empty, {@link DiscordUtils#buildRichPresence(PresenceData)} will use the first applicable data entry instead of the generic data
     */
    private final Map<String, Supplier<PresenceData>> forcedData = StringUtils.newTreeMap();
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
    public DiscordStatus STATUS = DiscordStatus.Closed;
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
     * The Current Raw Small Image Icon being displayed in the RPC, if any
     */
    public String SMALL_IMAGE_RAW;
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
     * The Current Raw Large Image Icon being displayed in the RPC, if any
     */
    public String LARGE_IMAGE_RAW;
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
     * Whether to update the Starting Timestamp upon RPC Initialization
     */
    public boolean UPDATE_TIMESTAMP;
    /**
     * Whether to allow sending duplicate {@link RichPresence} states through {@link DiscordUtils#updatePresence(RichPresence)}
     */
    public boolean ALLOW_DUPLICATE_PACKETS;
    /**
     * How many attempts to make a connection to Discord, before failing
     */
    public int MAX_CONNECTION_ATTEMPTS;
    /**
     * The Current Starting Unix Timestamp from Epoch, used for Elapsed Time
     */
    public long START_TIMESTAMP;
    /**
     * The Party Session ID that's tied to the RPC, if any
     */
    public String PARTY_ID;
    /**
     * The Current Size of the Party Session, while applicable
     */
    public int PARTY_SIZE;
    /**
     * The Maximum Size of the Party Session, while applicable
     */
    public int PARTY_MAX;
    /**
     * The Privacy Level of the Party Session
     * <p>0 == Private; 1 == Public
     */
    public PartyPrivacy PARTY_PRIVACY = PartyPrivacy.Public;
    /**
     * The Current Party Join Secret Key, while applicable
     */
    public String JOIN_SECRET;
    /**
     * The Current Ending Unix Timestamp from Epoch
     * <p>
     * Used for Time Until if combined with {@link DiscordUtils#START_TIMESTAMP}
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
     * An Instance of the {@link Starscript} engine, responsible for parsing and combining expressions
     */
    public Starscript scriptEngine = new Starscript();
    /**
     * An Instance of the {@link IPCClient}, responsible for sending and receiving RPC Events
     */
    public IPCClient ipcInstance;
    /**
     * The last requested override identifier, used in {@link DiscordUtils#getOverrideText(PresenceData)}
     */
    private String overrideTarget = "";
    /**
     * An Instance containing the Current Rich Presence Data
     * <p>Also used to prevent sending duplicate packets with the same presence data, if any
     */
    private RichPresence currentPresence;
    /**
     * The duration or timestamp of the last running instance
     */
    private long lastStartTime;
    /**
     * Whether the Auto-Reconnection Thread is currently in progress
     */
    private boolean connectThreadActive = false;
    /**
     * How many attempts remain before giving up on connecting to an IPC Pipe
     */
    private int attemptsRemaining = 0;

    /**
     * Setup any Critical Methods needed for the RPC
     */
    public void setup() {
        Runtime.getRuntime().addShutdownHook(
                Constants.getThreadFactory().newThread(() -> {
                    Constants.IS_GAME_CLOSING = true;
                    FileUtils.shutdownSchedulers();
                    shutDown();
                })
        );

        // Setup Default / Static Placeholders
        syncPlaceholders();
    }

    /**
     * Initializes Critical Rich Presence Data
     *
     * @param debugMode   Whether to enable debug mode for this instance
     * @param verboseMode Whether to enable verbose mode for this instance
     */
    public void init(final boolean debugMode, final boolean verboseMode) {
        // Create IPC Instance
        ipcInstance = new IPCClient(Long.parseLong(CLIENT_ID), debugMode, verboseMode, AUTO_REGISTER, CLIENT_ID);
        ipcInstance.setListener(new ModIPCListener());
        // Initialize Discord Assets
        DiscordAssetUtils.loadAssets(CLIENT_ID, true);
        // Mark as Disconnected to trigger auto-sync
        STATUS = DiscordStatus.Disconnected;
    }

    /**
     * Initializes Critical Rich Presence Data
     *
     * @param debugMode Whether to enable debug mode for this instance
     */
    public void init(final boolean debugMode) {
        init(debugMode, CommandUtils.isVerboseMode());
    }

    /**
     * Initializes Critical Rich Presence Data
     */
    public void init() {
        init(CommandUtils.isDebugMode());
    }

    /**
     * Synchronizes Initial Rich Presence Data
     */
    public void postInit() {
        // N/A
    }

    /**
     * Attempt to Connect to the {@link IPCClient} service
     */
    private void attemptConnection() {
        try {
            final int attemptCount = (MAX_CONNECTION_ATTEMPTS - attemptsRemaining) + 1;
            Constants.LOG.info(Constants.TRANSLATOR.translate("craftpresence.logger.info.connect", attemptCount, MAX_CONNECTION_ATTEMPTS));
            if (PREFERRED_CLIENT != DiscordBuild.ANY) {
                ipcInstance.connect(PREFERRED_CLIENT, DiscordBuild.ANY);
            } else {
                ipcInstance.connect();
            }

            // Subscribe to RPC Events after Connection
            ipcInstance.subscribe(IPCClient.Event.ACTIVITY_JOIN);
            ipcInstance.subscribe(IPCClient.Event.ACTIVITY_JOIN_REQUEST);
            ipcInstance.subscribe(IPCClient.Event.ACTIVITY_SPECTATE);

            postInit();
            connectThreadActive = false;
        } catch (Exception ex) {
            if (ex.getClass() != NoDiscordClientException.class) {
                Constants.LOG.error(Constants.TRANSLATOR.translate("craftpresence.logger.error.connect"));
                Constants.LOG.debugError(ex);

                // Mark as Closed if we experience an actual Exception
                STATUS = DiscordStatus.Closed;
                connectThreadActive = false;
            }
        }
    }

    /**
     * Creates a string-based representation of the button-list, from config values
     *
     * @param list The list to interpret
     * @return the output list
     */
    public List<String> createButtonsList(final Map<String, Button> list) {
        final List<String> result = StringUtils.newArrayList();
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
     * @param input    The string to interpret
     * @param length   The required length the input must fall under
     * @param fallback The fallback string to interpret
     * @return The resulting output string
     */
    public String sanitizePlaceholders(final String input, int length, final String fallback) {
        return StringUtils.getOrDefault(
                input, fallback,
                StringUtils.NULL_OR_EMPTY.negate()
                        .and(e -> input.length() >= 2) // Discord String Length Limits
                        .and(e -> StringUtils.getBytes(e, "UTF-8").length < length) // Discord Byte Length Limits
        ).trim();
    }

    /**
     * Removes any invalid data from a placeholder argument
     *
     * @param input  The string to interpret
     * @param length The required length the input must fall under
     * @return The resulting output string
     */
    public String sanitizePlaceholders(final String input, int length) {
        return sanitizePlaceholders(input, length, "");
    }

    /**
     * Compiles and Parses the specified input, via {@link Starscript}
     *
     * @param input        The input expression to interpret
     * @param overrideId   The override identifier to interpret
     * @param plain        Whether the expression should be parsed as a plain string
     * @param replacements A mapping of additional replacements to perform
     * @return the supplier containing the output
     */
    @SafeVarargs
    public final Supplier<Value> compileData(final String input, final String overrideId, final boolean plain, final Pair<String, Supplier<String>>... replacements) {
        synchronized (placeholderData) {
            final Map<String, Supplier<Value>> placeholders = StringUtils.newTreeMap(placeholderData);
            final String data = StringUtils.getOrDefault(input);

            if (!plain) {
                final Pair<String, VariableReplacementTransformer> resultData = generateTransformer(
                        data, overrideId, placeholders, replacements
                );
                return getCompileResult(resultData != null ? resultData.getFirst() : data, null, resultData != null ? resultData.getSecond() : null);
            } else {
                return () -> Value.string(data);
            }
        }
    }

    /**
     * Generates a {@link VariableReplacementTransformer} for the specified arguments
     *
     * @param input        The original string to interpret
     * @param overrideId   The override identifier to interpret
     * @param placeholders A mapping of the placeholders currently available
     * @param replacements A mapping of additional replacements to perform
     * @return the processed string, alongside the variable transformer
     */
    @SafeVarargs
    public final Pair<String, VariableReplacementTransformer> generateTransformer(final String input, final String overrideId, final Map<String, Supplier<Value>> placeholders, final Pair<String, Supplier<String>>... replacements) {
        overrideTarget = overrideId;
        if (replacements != null) {
            final VariableReplacementTransformer transformer = new VariableReplacementTransformer();
            String data = StringUtils.getOrDefault(input);

            for (Pair<String, Supplier<String>> replacement : replacements) {
                if (replacement != null) {
                    final Supplier<String> info = replacement.getSecond();
                    if (info != null) {
                        final String value = info.get();
                        if (placeholders.containsKey(value)) {
                            transformer.addReplacer(replacement.getFirst(), info);
                        } else {
                            data = data.replace(
                                    replacement.getFirst(),
                                    !StringUtils.isNullOrEmpty(value) ? "'" + value + "'" : "null"
                            );
                        }
                    }
                }
            }
            return new Pair<>(data, transformer);
        }
        return null;
    }

    /**
     * Interpret the processed {@link Script} from parsing the specified args, and compile it
     *
     * @param data        The data or expression to be parsed
     * @param showLogging Whether to display logging for this function
     * @param output      If specified, attach the decompiled info to this {@link Appendable}
     * @param transforms  Any additional expression transformations, to be done before compiling
     * @return the processed output
     */
    public Supplier<Value> getCompileResult(final String data, final boolean showLogging, final Appendable output, Expr.Visitor... transforms) {
        Parser.Result result = null;
        try {
            result = Parser.parse(data);
        } catch (Throwable ignored) {
        }

        final String originalPrefix = Constants.TRANSLATOR.translate("gui.config.message.editor.original");
        final String messagePrefix = Constants.TRANSLATOR.translate("gui.config.message.editor.message");
        final String verbosePrefix = Constants.TRANSLATOR.translate("craftpresence.logger.error.verbose");
        if (result == null || result.hasErrors()) {
            if (result != null) {
                Constants.LOG.error(Constants.TRANSLATOR.translate("craftpresence.logger.error.parser"));
                Constants.LOG.error("%1$s \"%2$s\"", originalPrefix, data);
                Constants.LOG.error(messagePrefix);
                for (Error error : result.errors) {
                    if (output != null) {
                        try {
                            output.append(error.toString()).append('\n');
                        } catch (Exception ignored) {
                        }
                    }
                    Constants.LOG.error("\t" + error.toString());
                }
            }
            return Value::null_;
        }

        if (transforms != null) {
            for (Expr.Visitor transformer : transforms) {
                if (transformer != null) {
                    result.accept(transformer);
                }
            }
        }

        final Script script = Compiler.compile(result);
        return () -> {
            Section sect;
            try {
                sect = new Starscript(scriptEngine).run(script);
                if (output != null) {
                    script.decompile(output);
                }
            } catch (Throwable ex) {
                Constants.LOG.error(Constants.TRANSLATOR.translate("craftpresence.logger.error.compiler"));
                Constants.LOG.error("%1$s \"%2$s\"", originalPrefix, data);

                Constants.LOG.printStackTrace(ex, showLogging, messagePrefix, verbosePrefix, output);
                return Value.null_();
            }
            return !StringUtils.isNullOrEmpty(sect.toString()) ? Value.string(sect.toString()) : Value.null_();
        };
    }

    /**
     * Interpret the processed {@link Script} from parsing the specified args, and compile it
     *
     * @param data       The data or expression to be parsed
     * @param output     If specified, attach the decompiled info to this {@link Appendable}
     * @param transforms Any additional expression transformations, to be done before compiling
     * @return the processed output
     */
    public Supplier<Value> getCompileResult(final String data, final Appendable output, Expr.Visitor... transforms) {
        return getCompileResult(data, Constants.LOG.isDebugMode(), output, transforms);
    }

    /**
     * Retrieve the output from the execution of {@link DiscordUtils#compileData(String, String, boolean, Pair[])}
     *
     * @param input        The input expression to interpret
     * @param overrideId   The override identifier to interpret
     * @param plain        Whether the expression should be parsed as a plain string
     * @param replacements A mapping of additional replacements to perform
     * @return the result of the supplier containing the output
     */
    @SafeVarargs
    public final String getResult(final String input, final String overrideId, final boolean plain, final Pair<String, Supplier<String>>... replacements) {
        final Value data = compileData(input, overrideId, plain, replacements).get();
        return !data.isNull() ? data.toString() : "";
    }

    /**
     * Compiles and Parses the specified input, via {@link Starscript}
     *
     * @param input        The input expression to interpret
     * @param overrideId   The override identifier to interpret
     * @param replacements A mapping of additional replacements to perform
     * @return the supplier containing the output
     */
    @SafeVarargs
    public final Supplier<Value> compileData(final String input, final String overrideId, final Pair<String, Supplier<String>>... replacements) {
        return compileData(input, overrideId, false, replacements);
    }

    /**
     * Retrieve the output from the execution of {@link DiscordUtils#compileData(String, String, Pair[])}
     *
     * @param input        The input expression to interpret
     * @param overrideId   The override identifier to interpret
     * @param replacements A mapping of additional replacements to perform
     * @return the result of the supplier containing the output
     */
    @SafeVarargs
    public final String getResult(final String input, final String overrideId, final Pair<String, Supplier<String>>... replacements) {
        return getResult(input, overrideId, false, replacements);
    }

    /**
     * Compiles and Parses the specified input, via {@link Starscript}
     *
     * @param input        The input expression to interpret
     * @param plain        Whether the expression should be parsed as a plain string
     * @param replacements A mapping of additional replacements to perform
     * @return the supplier containing the output
     */
    @SafeVarargs
    public final Supplier<Value> compileData(final String input, final boolean plain, final Pair<String, Supplier<String>>... replacements) {
        return compileData(input, null, plain, replacements);
    }

    /**
     * Retrieve the output from the execution of {@link DiscordUtils#compileData(String, boolean, Pair[])}
     *
     * @param input        The input expression to interpret
     * @param plain        Whether the expression should be parsed as a plain string
     * @param replacements A mapping of additional replacements to perform
     * @return the result of the supplier containing the output
     */
    @SafeVarargs
    public final String getResult(final String input, final boolean plain, final Pair<String, Supplier<String>>... replacements) {
        return getResult(input, null, plain, replacements);
    }

    /**
     * Compiles and Parses the specified input, via {@link Starscript}
     *
     * @param input        The input expression to interpret
     * @param replacements A mapping of additional replacements to perform
     * @return the supplier containing the output
     */
    @SafeVarargs
    public final Supplier<Value> compileData(final String input, final Pair<String, Supplier<String>>... replacements) {
        return compileData(input, false, replacements);
    }

    /**
     * Retrieve the output from the execution of {@link DiscordUtils#compileData(String, Pair[])}
     *
     * @param input        The input expression to interpret
     * @param replacements A mapping of additional replacements to perform
     * @return the result of the supplier containing the output
     */
    @SafeVarargs
    public final String getResult(final String input, final Pair<String, Supplier<String>>... replacements) {
        return getResult(input, false, replacements);
    }

    /**
     * Compiles and Parses the specified input, via {@link Starscript}
     *
     * @param input The input expression to interpret
     * @return the supplier containing the output
     */
    public Supplier<Value> compileData(final String input) {
        return compileData(input, (Pair<String, Supplier<String>>) null);
    }

    /**
     * Retrieve the output from the execution of {@link DiscordUtils#compileData(String)}
     *
     * @param input The input expression to interpret
     * @return the result of the supplier containing the output
     */
    public String getResult(final String input) {
        return getResult(input, (Pair<String, Supplier<String>>) null);
    }

    /**
     * Updates the specified placeholder(s) with a Unix Timestamp
     *
     * @param newTimestamp The new timestamp for these arguments
     * @param args         The Specified Arguments to Synchronize for
     */
    public void syncTimestamp(final Supplier<Long> newTimestamp, final String... args) {
        for (String argumentName : args) {
            syncFunction(argumentName, () -> Long.toString(newTimestamp.get()), true);
        }
    }

    /**
     * Updates the specified placeholder(s) with a Unix Timestamp
     *
     * @param args The Specified Arguments to Synchronize for
     */
    public void syncTimestamp(final String... args) {
        final long newTimestamp = TimeUtils.toEpochMilli();
        syncTimestamp(() -> newTimestamp, args);
    }

    /**
     * Retrieve the override text for the specified {@link PresenceData}
     * <p>Only applies when enabled and {@link PresenceData#useAsMain} is false
     *
     * @param presenceData the {@link PresenceData} to interpret
     * @return the retrieved override text if possible, otherwise null
     */
    public String getOverrideText(final PresenceData presenceData) {
        if (presenceData != null && presenceData.enabled && !presenceData.useAsMain) {
            final String field = getOverrideTarget();
            final boolean isButton = field.startsWith("button_");
            Object result;
            if (isButton) {
                final String[] buttonInfo = field.split("\\.");
                result = presenceData.buttons.get(buttonInfo[0]).getProperty(buttonInfo[1]);
            } else {
                result = presenceData.getProperty(field);
            }
            return StringUtils.getOrDefault(result.toString());
        }
        return null;
    }

    /**
     * Retrieve the last requested override identifier
     *
     * @return the last requested override identifier
     */
    public String getOverrideTarget() {
        return overrideTarget;
    }

    /**
     * Add the specified info to the forced data list
     *
     * @param key     The key to store the info under
     * @param newData The {@link PresenceData} to interpret
     */
    public void addForcedData(final String key, final Supplier<PresenceData> newData) {
        forcedData.put(key, newData);
    }

    /**
     * Remove the specified elements from the forced data list
     *
     * @param args The elements to remove, if present
     */
    public void removeForcedData(final String... args) {
        for (String argumentName : args) {
            forcedData.remove(argumentName);
        }
    }

    /**
     * Synchronizes the Specified Argument as an RPC Message or an Icon Placeholder
     *
     * @param argumentName The Specified Argument to Synchronize for
     * @param data         The data to attach to the Specified Argument
     */
    public void setArgument(final String argumentName, final Supplier<Value> data) {
        synchronized (placeholderData) {
            if (!StringUtils.isNullOrEmpty(argumentName)) {
                scriptEngine.set(argumentName, data);
                placeholderData.put(argumentName, data);
            }
        }
    }

    /**
     * Synchronizes the Specified Argument as an RPC Message or an Icon Placeholder
     *
     * @param argumentName The Specified Argument to Synchronize for
     * @param data         The data to attach to the Specified Argument
     * @param plain        Whether the expression should be parsed as a plain string
     */
    public void syncFunction(final String argumentName, final Supplier<Object> data, final boolean plain) {
        if (!StringUtils.isNullOrEmpty(argumentName)) {
            setArgument(argumentName, () -> toValue(data.get(), plain));
        }
    }

    /**
     * Synchronizes the Specified Argument as an RPC Message or an Icon Placeholder
     *
     * @param argumentName The Specified Argument to Synchronize for
     * @param data         The data to attach to the Specified Argument
     */
    public void syncFunction(final String argumentName, final Supplier<Object> data) {
        syncFunction(argumentName, data, false);
    }

    /**
     * Synchronizes the Specified Argument as an RPC Message or an Icon Placeholder
     *
     * @param argumentName The Specified Argument to Synchronize for
     * @param data         The data to attach to the Specified Argument
     */
    public void syncFunction(final String argumentName, final SFunction data) {
        syncFunction(argumentName, () -> data);
    }

    /**
     * Initialize the Specified Arguments as Empty Data
     *
     * @param args The Arguments to Initialize
     */
    public void initArguments(final String... args) {
        // Initialize Specified Arguments to Empty Data
        for (String argumentName : args) {
            setArgument(argumentName, Value::null_);
        }
    }

    /**
     * Remove any arguments following the specified formats within the selected Argument Type
     *
     * @param args The string formats to interpret
     */
    public void removeArguments(final String... args) {
        synchronized (placeholderData) {
            final List<String> items = StringUtils.newArrayList(placeholderData.keySet());
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
    }

    /**
     * Refresh Dynamic Variable Data, removing any data no longer in-play
     *
     * @param oldData The old data to interpret
     * @param newData The new data to interpret
     */
    public void syncDynamicVariables(final Map<String, String> oldData, final Map<String, String> newData) {
        final boolean hasOldData = oldData != null && !oldData.isEmpty();
        if (hasOldData) {
            for (Map.Entry<String, String> entry : oldData.entrySet()) {
                if (!entry.getKey().equals("default") && !newData.containsKey(entry.getKey())) {
                    removeArguments("custom." + entry.getKey());
                }
            }
        }

        for (String entry : newData.keySet()) {
            if (!entry.equals("default") && (!hasOldData || !oldData.containsKey(entry))) {
                syncFunction(
                        "custom." + entry,
                        () -> CraftPresence.CONFIG.displaySettings.dynamicVariables.get(entry)
                );
            }
        }
    }

    /**
     * Converts a {@link Value} to its {@link Object} representation
     *
     * @param data the data to interpret
     * @return the {@link Object} representation
     */
    public Object fromValue(final Value data) {
        if (data.isNumber()) {
            return data.getNumber();
        } else if (data.isBool()) {
            return data.getBool();
        } else if (data.isMap()) {
            return data.getMap();
        } else if (data.isFunction()) {
            return data.getFunction();
        } else if (data.isString()) {
            return data.getString();
        } else {
            return data.isObject() ? data.getObject() : null;
        }
    }

    /**
     * Converts an {@link Object} to its {@link Value} representation
     *
     * @param data  the data to interpret
     * @param plain If true, when the data is a string, determines the expression should be parsed as a plain string
     * @return the {@link Value} representation
     */
    public Value toValue(final Object data, final boolean plain) {
        if (data instanceof Number) {
            return Value.number(((Number) data).doubleValue());
        } else if (data instanceof Boolean) {
            return Value.bool((Boolean) data);
        } else if (data instanceof ValueMap) {
            return Value.map((ValueMap) data);
        } else if (data instanceof SFunction) {
            return Value.function((SFunction) data);
        } else if (data instanceof String) {
            return compileData(data.toString(), plain).get();
        } else {
            return data != null ? Value.object(data) : Value.null_();
        }
    }

    /**
     * Retrieves any arguments within the specified type that match the specified string formats
     *
     * @param args The string formats to interpret
     * @return A List of the entries that satisfy the method conditions
     */
    public Map<String, Supplier<Value>> getArguments(final String... args) {
        synchronized (placeholderData) {
            final Map<String, Supplier<Value>> items = StringUtils.newTreeMap(placeholderData);
            final Map<String, Supplier<Value>> list = StringUtils.newTreeMap();

            for (Map.Entry<String, Supplier<Value>> entry : items.entrySet()) {
                final String item = entry.getKey();
                final Supplier<Value> data = entry.getValue();
                boolean addToList = args == null || args.length < 1 || args[0] == null;
                if (!addToList) {
                    for (String name : args) {
                        if (!StringUtils.isNullOrEmpty(name)) {
                            addToList = item.startsWith(name) ||
                                    (name.equalsIgnoreCase("type:all") || name.equalsIgnoreCase("all")) ||
                                    (name.startsWith("type:") && matchesType(
                                            name.replaceFirst("type:", "").toLowerCase(),
                                            data.get()
                                    ));
                        }

                        if (addToList) {
                            break;
                        }
                    }
                }
                if (addToList) {
                    list.put(item, data);
                }
            }
            return list;
        }
    }

    /**
     * Whether the specified {@link Value} matches the specified type
     *
     * @param type The {@link Value} type to interpret
     * @param data The {@link Value} to interpret
     * @return {@link Boolean#TRUE} if both the type and its condition is satisfied
     */
    public boolean matchesType(final String type, final Value data) {
        switch (type) {
            case "function":
                return data.isFunction();
            case "object":
                return data.isObject();
            case "bool":
            case "boolean":
                return data.isBool();
            case "map":
                return data.isMap();
            case "int":
            case "integer":
            case "float":
            case "double":
            case "number":
                return data.isNumber();
            case "text":
            case "string":
                return data.isString();
            case "empty":
            case "null":
                return data.isNull();
            case "any":
            case "all":
                return true;
            default:
                return false;
        }
    }

    /**
     * Retrieves the argument within the specified type that matches the specified string format
     *
     * @param key The input to interpret
     * @return The entry that satisfies the method conditions, or null
     */
    public Supplier<Value> getArgument(final String key) {
        synchronized (placeholderData) {
            return placeholderData.getOrDefault(key, Value::null_);
        }
    }

    /**
     * Retrieves any argument entries within the specified type that match the specified string formats
     *
     * @param formatToLower Whether to lower-cases the resulting entries
     * @param args          The string formats to interpret
     * @return A List of the entries that satisfy the method conditions
     */
    public List<String> getArgumentEntries(final boolean formatToLower, final String... args) {
        final Map<String, Supplier<Value>> list = getArguments(args);
        final List<String> result = StringUtils.newArrayList();
        for (String item : list.keySet()) {
            result.add(formatToLower ? item.toLowerCase() : item);
        }
        return result;
    }

    /**
     * Retrieves any argument entries within the specified type that match the specified string formats
     *
     * @param args The string formats to interpret
     * @return A List of the entries that satisfy the method conditions
     */
    public List<String> getArgumentEntries(final String... args) {
        return getArgumentEntries(false, args);
    }

    /**
     * Determines whether there are any matching arguments within the specified type matching the specified string formats
     *
     * @param args The string formats to interpret
     * @return Whether the resulting list has any matching entries
     */
    public boolean hasArgumentsMatching(final String... args) {
        return !getArguments(args).isEmpty();
    }

    /**
     * Generate a parsable display string for the argument data provided
     *
     * @param formats      The argument formats to interpret
     * @param addExtraData Whether to add additional data to the string
     * @param args         The data to interpret
     * @return the parsable string
     */
    public String generateArgumentMessage(final List<String> formats, final boolean addExtraData, final Map<String, Supplier<Value>> args) {
        final StringBuilder resultString = new StringBuilder(
                Constants.TRANSLATOR.translate(
                        String.format("%s.placeholders.notes", Constants.MOD_ID)
                )
        );
        resultString.append("\\n\\n").append(
                Constants.TRANSLATOR.translate(
                        String.format("%s.placeholders.title", Constants.MOD_ID)
                )
        );
        if (!formats.isEmpty()) {
            resultString.append(" (").append(String.join(",", formats)).append(")");
        }
        resultString.append(":");

        final StringBuilder placeholderString = new StringBuilder();
        if (args != null && !args.isEmpty()) {
            for (Map.Entry<String, Supplier<Value>> argData : args.entrySet()) {
                placeholderString.append("\\n").append(generateArgumentMessage(
                        argData.getKey(), argData.getValue(), true, addExtraData
                ));
            }
        }

        if (placeholderString.length() == 0) {
            placeholderString.append("\\n - N/A");
        }
        resultString.append(placeholderString);
        return resultString.toString();
    }

    /**
     * Generate a parsable display string for the argument data provided
     *
     * @param placeholderName The Specified Argument to interpret
     * @param suppliedInfo    The current argument info, if any
     * @param includeName     Whether to inline the description with the argument name
     * @param addExtraData    Whether to add additional data to the string
     * @param prefix          The prefix to begin each line with (Overwritten if `includeName` is true)
     * @return the parsable string
     */
    public String generateArgumentMessage(final String placeholderName, final Supplier<Value> suppliedInfo, final boolean includeName, final boolean addExtraData, final String prefix) {
        final StringBuilder placeholderString = new StringBuilder();

        final String placeholderTranslation = String.format("%s.placeholders.%s.description",
                Constants.MOD_ID,
                placeholderName
        );
        final String placeholderUsage = String.format("%s.placeholders.%s.usage",
                Constants.MOD_ID,
                placeholderName
        );
        final boolean hasDescription = Constants.TRANSLATOR.hasTranslation(placeholderTranslation);
        String start = prefix;

        if (includeName) {
            String placeholderDescription = "";
            String placeholderFormat = " - %s";

            if (hasDescription) {
                placeholderDescription = Constants.TRANSLATOR.translate(placeholderTranslation);
                placeholderFormat = " - %s = %s";
            }

            placeholderString.append(
                    String.format(placeholderFormat,
                            placeholderName,
                            placeholderDescription
                    )
            );
            start = "\\n" + prefix;
        } else if (hasDescription) {
            placeholderString.append(String.format("%s \"%s\"",
                    start + Constants.TRANSLATOR.translate("gui.config.message.editor.description"),
                    Constants.TRANSLATOR.translate(placeholderTranslation)
            ));
            start = "\\n" + prefix;
        }

        if (Constants.TRANSLATOR.hasTranslation(placeholderUsage)) {
            placeholderString.append(String.format("%s \"%s\"",
                    start + Constants.TRANSLATOR.translate("gui.config.message.editor.usage"),
                    Constants.TRANSLATOR.translate(placeholderUsage)
            ));
            start = "\\n" + prefix;
        }

        if (suppliedInfo != null && addExtraData && isDefaultPlaceholder(placeholderName.toLowerCase())) {
            final Value rawValue = suppliedInfo.get();
            final String tagValue = rawValue.toString();
            if (!rawValue.isNull() && !rawValue.isFunction() && !StringUtils.isNullOrEmpty(tagValue)) {
                placeholderString.append(String.format("%s \"%s\"",
                        start + Constants.TRANSLATOR.translate("gui.config.message.editor.preview"),
                        (tagValue.length() >= 128) ? "<...>" : tagValue
                ));
            }
        }
        return placeholderString.toString();
    }

    /**
     * Generate a parsable display string for the argument data provided
     *
     * @param placeholderName The Specified Argument to interpret
     * @param includeName     Whether to inline the description with the argument name
     * @param addExtraData    Whether to add additional data to the string
     * @param prefix          The prefix to begin each line with (Overwritten if `includeName` is true)
     * @return the parsable string
     */
    public String generateArgumentMessage(final String placeholderName, final boolean includeName, final boolean addExtraData, final String prefix) {
        return generateArgumentMessage(placeholderName, getArgument(placeholderName), includeName, addExtraData, prefix);
    }

    /**
     * Generate a parsable display string for the argument data provided
     *
     * @param placeholderName The Specified Argument to interpret
     * @param suppliedInfo    The current argument info, if any
     * @param includeName     Whether to inline the description with the argument name
     * @param addExtraData    Whether to add additional data to the string
     * @return the parsable string
     */
    public String generateArgumentMessage(final String placeholderName, final Supplier<Value> suppliedInfo, final boolean includeName, final boolean addExtraData) {
        return generateArgumentMessage(placeholderName, suppliedInfo, includeName, addExtraData, " ==> ");
    }

    /**
     * Generate a parsable display string for the argument data provided
     *
     * @param placeholderName The Specified Argument to interpret
     * @param includeName     Whether to inline the description with the argument name
     * @param addExtraData    Whether to add additional data to the string
     * @return the parsable string
     */
    public String generateArgumentMessage(final String placeholderName, final boolean includeName, final boolean addExtraData) {
        return generateArgumentMessage(placeholderName, includeName, addExtraData, " ==> ");
    }

    /**
     * Retrieve whether this placeholder is a default-supplied one
     * <p>
     * Non-default placeholders can contain dynamic data that we can't always account for.
     *
     * @param name The placeholder name to interpret
     * @return {@link Boolean#TRUE} if this is a default-supplied placeholder
     */
    public boolean isDefaultPlaceholder(final String name) {
        return !name.startsWith("custom.") &&
                !name.endsWith(".message") && !name.endsWith(".icon");
    }

    /**
     * Generate a parsable display string for the argument data provided
     *
     * @param addExtraData Whether to add additional data to the string
     * @param formats      The argument formats to interpret
     * @return the parsable string
     */
    public String generateArgumentMessage(final boolean addExtraData, final String... formats) {
        return generateArgumentMessage(Arrays.asList(formats), addExtraData, getArguments(formats));
    }

    /**
     * Generate a parsable display string for the argument data provided
     *
     * @param formats The argument formats to interpret
     * @return the parsable string
     */
    public String generateArgumentMessage(final String... formats) {
        return generateArgumentMessage(CraftPresence.CONFIG.advancedSettings.allowPlaceholderPreviews, formats);
    }

    /**
     * Synchronizes and Updates Dynamic Placeholder data in this module
     */
    public void syncPlaceholders() {
        FunctionsLib.init(this, scriptEngine);
        syncFunction("general.mods", Constants::getModCount);
        syncFunction("general.title", () -> Constants.TRANSLATOR.translate("craftpresence.defaults.state.mc.version", ModUtils.MCVersion));
        syncFunction("general.version", () -> ModUtils.MCVersion, true);
        syncFunction("general.protocol", () -> ModUtils.MCProtocolID);
        syncFunction("general.brand", () -> ModUtils.BRAND, true);

        syncFunction("data.general.version", () -> Constants.MCBuildVersion, true);
        syncFunction("data.general.protocol", () -> Constants.MCBuildProtocol);
        syncTimestamp(() -> {
            final long currentStartTime = !UPDATE_TIMESTAMP && lastStartTime > 0 ?
                    lastStartTime : TimeUtils.toEpochMilli();
            lastStartTime = currentStartTime;
            return currentStartTime;
        }, "data.general.time");

        syncFunction("_general.instance", () -> CraftPresence.instance);
        syncFunction("_general.player", () -> CraftPresence.player);
        syncFunction("_general.world", () -> CraftPresence.player != null ? CraftPresence.player.world : null);
        syncFunction("_config.instance", () -> CraftPresence.CONFIG);

        // Sync Custom Variables
        syncDynamicVariables(null, CraftPresence.CONFIG.displaySettings.dynamicVariables);

        // Add Any Generalized Argument Data needed
        syncFunction("player.name", () -> CraftPresence.username, true);

        // UUID Data
        syncFunction("player.uuid.short", () -> {
            final String uniqueId = CraftPresence.uuid;
            return StringUtils.isValidUuid(uniqueId) ? StringUtils.getFromUuid(uniqueId, true) : null;
        }, true);
        syncFunction("player.uuid.full", () -> {
            final String uniqueId = CraftPresence.uuid;
            return StringUtils.isValidUuid(uniqueId) ? StringUtils.getFromUuid(uniqueId, false) : null;
        }, true);

        syncFunction("player.icon", () -> {
            if (addEndpointIcon(
                    CraftPresence.CONFIG,
                    CraftPresence.CONFIG.advancedSettings.playerSkinEndpoint,
                    CraftPresence.username, CraftPresence.uuid
            )) {
                return CraftPresence.username;
            }
            return null;
        }, true);

        // Sync the Default Icon Argument
        syncFunction("general.icon", () -> CraftPresence.CONFIG.generalSettings.defaultIcon, true);

        CommandUtils.syncModuleArguments();
        CommandUtils.syncPackArguments();
    }

    /**
     * Add an Endpoint Icon to the Selectable Dynamic Assets
     *
     * @param config   The {@link Config} instance to interpret
     * @param endpoint The endpoint url to be interpreted
     * @param name     The name to be interpreted
     * @param key      The key to be interpreted
     * @return {@link Boolean#TRUE} if operation is allowed
     */
    public boolean addEndpointIcon(final Config config, final String endpoint, final String name, final String key) {
        final boolean canUseEndpointIcon = !config.hasChanged &&
                config.advancedSettings.allowEndpointIcons &&
                !StringUtils.isNullOrEmpty(endpoint);

        if (canUseEndpointIcon) {
            if (!config.displaySettings.dynamicIcons.containsKey(name)) {
                config.displaySettings.dynamicIcons.put(name,
                        compileData(String.format(
                                endpoint,
                                StringUtils.getOrDefault(key, name)
                        )).get().toString()
                );
                if (config == CraftPresence.CONFIG) {
                    DiscordAssetUtils.syncCustomAssets();
                    config.save();
                }
            }
        }
        return canUseEndpointIcon;
    }

    /**
     * Add an Endpoint Icon to the Selectable Dynamic Assets
     *
     * @param config   The {@link Config} instance to interpret
     * @param endpoint The endpoint url to be interpreted
     * @param name     The name or key to be interpreted
     * @return {@link Boolean#TRUE} if operation is allowed
     */
    public boolean addEndpointIcon(final Config config, final String endpoint, final String name) {
        return addEndpointIcon(config, endpoint, name, "");
    }

    /**
     * Synchronizes and Updates the Rich Presence Data, if needed and connected
     *
     * @param presence The New Presence Data to apply
     */
    public void updatePresence(final RichPresence presence) {
        if (!isConnected() && !isClosed() && !connectThreadActive) {
            Constants.getThreadFactory().newThread(
                    () -> {
                        attemptsRemaining = MAX_CONNECTION_ATTEMPTS;
                        while (!isConnected() && attemptsRemaining > 0) {
                            attemptConnection();
                            attemptsRemaining--;
                        }

                        if (attemptsRemaining <= 0) {
                            Constants.LOG.error(Constants.TRANSLATOR.translate("craftpresence.logger.error.connect"));
                            STATUS = DiscordStatus.Closed;
                        }
                    }
            ).start();
            connectThreadActive = true;
        }

        if (isConnected()) {
            boolean allowed = ALLOW_DUPLICATE_PACKETS;
            if (!allowed) {
                allowed = (currentPresence == null && presence != null) ||
                        (presence == null && currentPresence != null) ||
                        (presence != null &&
                                !presence.toJson().toString().equals(currentPresence.toJson().toString())
                        );
            }

            if (allowed) {
                ipcInstance.sendRichPresence(presence);
                currentPresence = presence;
            }
        }
    }

    /**
     * Synchronizes and Updates the Rich Presence Data, if needed and connected
     */
    public void updatePresence() {
        updatePresence(buildRichPresence());
    }

    /**
     * Attempts to locate the specified Image, and if not existent, use the alternative String, and null if allowed
     *
     * @param allowNull   If allowed to return null if unable to find any matches, otherwise uses the Default Icon in Config
     * @param showLogging Whether to display logging for this function
     * @param evalStrings The Specified Icon Key(s) to search for from the {@link DiscordUtils#CLIENT_ID} Assets
     * @return The found or alternative matching Icon Key
     */
    public String imageOf(final boolean allowNull, final boolean showLogging, final String... evalStrings) {
        // Ensures Assets were fully synced from the Client ID before running
        String result;
        if (!DiscordAssetUtils.ASSET_LIST.isEmpty() && !StringUtils.isNullOrEmpty(evalStrings[0])) {
            final String primaryKey = evalStrings[0];
            if (!cachedImageData.containsKey(primaryKey)) {
                final String defaultIcon = allowNull ? "" : StringUtils.getOrDefault(DiscordAssetUtils.getKey(CraftPresence.CONFIG.generalSettings.defaultIcon), DiscordAssetUtils.getRandomAssetName());
                String finalKey = defaultIcon;
                for (int i = 0; i < evalStrings.length; ) {
                    final String currentString = evalStrings[i];
                    final boolean isPrimaryEntry = currentString.equals(primaryKey);
                    final DiscordAsset foundAsset = DiscordAssetUtils.get(currentString);
                    if (foundAsset != null) {
                        finalKey = foundAsset.getName();
                        if (showLogging && !isPrimaryEntry) {
                            Constants.LOG.info(Constants.TRANSLATOR.translate("craftpresence.logger.info.discord.assets.fallback", primaryKey, finalKey));
                        }
                        break;
                    } else {
                        i++;
                        if (i < evalStrings.length) {
                            if (showLogging) {
                                Constants.LOG.error(Constants.TRANSLATOR.translate("craftpresence.logger.error.discord.assets.fallback", currentString, evalStrings[i]));
                                if (isPrimaryEntry) {
                                    Constants.LOG.info(Constants.TRANSLATOR.translate("craftpresence.logger.info.discord.assets.request", currentString));
                                }
                            }
                        } else {
                            if (showLogging) {
                                Constants.LOG.info(Constants.TRANSLATOR.translate("craftpresence.logger.error.discord.assets.default", primaryKey, defaultIcon));
                            }
                            finalKey = defaultIcon;
                        }
                    }
                }

                cachedImageData.put(primaryKey, finalKey);
                result = finalKey;
            } else {
                result = cachedImageData.get(primaryKey);
            }
        } else {
            result = "";
        }
        return result;
    }

    /**
     * Attempts to locate the specified Image, and if not existent, use the alternative String, and null if allowed
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
     * Attempts to locate the specified Image, and if not existent, use the alternative String, and null if allowed
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
            if (!StringUtils.isNullOrEmpty(evalString)) {
                if (configData.largeImageKey.contains(evalString) ||
                        configData.smallImageKey.contains(evalString)
                ) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Clears Related Party Session Information from the RPC, and updates if needed
     */
    public void clearPartyData() {
        respondToJoinRequest(IPCClient.ApprovalMode.DENY);

        JOIN_SECRET = null;
        PARTY_ID = null;
        PARTY_SIZE = 0;
        PARTY_MAX = 0;
    }

    /**
     * Clears all Data from the RPC Presence Fields
     */
    public void clearPresenceData() {
        GAME_STATE = "";
        DETAILS = "";
        LARGE_IMAGE_ASSET = null;
        LARGE_IMAGE_KEY = "";
        LARGE_IMAGE_TEXT = "";
        SMALL_IMAGE_ASSET = null;
        SMALL_IMAGE_KEY = "";
        SMALL_IMAGE_TEXT = "";
        BUTTONS = new JsonArray();

        clearPartyData();
    }

    /**
     * Shutdown the RPC and close related resources
     *
     * @param allowReconnects Whether to mark the {@link DiscordStatus} to allow auto-reconnections
     */
    public void shutDown(final boolean allowReconnects) {
        if (isAvailable()) {
            try {
                ipcInstance.close();
            } catch (Exception ex) {
                Constants.LOG.debugError(ex);
            }

            // Clear User Data before final clear and shutdown
            currentPresence = null;
            clearPresenceData();
            STATUS = allowReconnects ? DiscordStatus.Disconnected : DiscordStatus.Closed;

            CURRENT_USER = null;
            cachedImageData.clear();

            Constants.LOG.info(Constants.TRANSLATOR.translate("craftpresence.logger.info.shutdown"));
        }
    }

    /**
     * Shutdown the RPC and close related resources
     */
    public void shutDown() {
        shutDown(false);
    }

    /**
     * Builds a New Instance of {@link RichPresence} based on Queued Data
     *
     * @param configData The {@link PresenceData} to be interpreted
     * @return A New Instance of {@link RichPresence}
     */
    public RichPresence buildRichPresence(final PresenceData configData) {
        // Do not compile Presence while offline or in-progress of connecting
        if (!isAvailable() || !isConnected() || connectThreadActive) {
            return null;
        }

        // Format Presence based on Arguments available in argumentData
        DETAILS = StringUtils.formatWord(getResult(configData.details, "details"), !CraftPresence.CONFIG.advancedSettings.formatWords, true, 1);
        GAME_STATE = StringUtils.formatWord(getResult(configData.gameState, "gameState"), !CraftPresence.CONFIG.advancedSettings.formatWords, true, 1);

        LARGE_IMAGE_RAW = getResult(configData.largeImageKey, "largeImageKey");
        SMALL_IMAGE_RAW = getResult(configData.smallImageKey, "smallImageKey");

        LARGE_IMAGE_ASSET = DiscordAssetUtils.get(LARGE_IMAGE_RAW);
        SMALL_IMAGE_ASSET = DiscordAssetUtils.get(SMALL_IMAGE_RAW);

        LARGE_IMAGE_KEY = LARGE_IMAGE_ASSET != null ? (LARGE_IMAGE_ASSET.getType().equals(DiscordAsset.AssetType.CUSTOM) ?
                getResult(LARGE_IMAGE_ASSET.getUrl()) : LARGE_IMAGE_ASSET.getName()) : LARGE_IMAGE_RAW;
        SMALL_IMAGE_KEY = SMALL_IMAGE_ASSET != null ? (SMALL_IMAGE_ASSET.getType().equals(DiscordAsset.AssetType.CUSTOM) ?
                getResult(SMALL_IMAGE_ASSET.getUrl()) : SMALL_IMAGE_ASSET.getName()) : SMALL_IMAGE_RAW;

        LARGE_IMAGE_TEXT = StringUtils.formatWord(getResult(configData.largeImageText, "largeImageText"), !CraftPresence.CONFIG.advancedSettings.formatWords, true, 1);
        SMALL_IMAGE_TEXT = StringUtils.formatWord(getResult(configData.smallImageText, "smallImageText"), !CraftPresence.CONFIG.advancedSettings.formatWords, true, 1);

        final Pair<Boolean, Long> startData = StringUtils.getValidLong(
                getResult(configData.startTimestamp, "startTimestamp")
        );
        if (startData.getFirst()) {
            START_TIMESTAMP = startData.getSecond();
            final Pair<Boolean, Long> endData = StringUtils.getValidLong(
                    getResult(configData.endTimestamp, "endTimestamp")
            );
            END_TIMESTAMP = endData.getFirst() ? endData.getSecond() : 0;
        } else {
            START_TIMESTAMP = 0;
            END_TIMESTAMP = 0;
        }

        // Format Buttons Array based on Config Value
        BUTTONS = new JsonArray();
        if (StringUtils.isNullOrEmpty(JOIN_SECRET) && StringUtils.isNullOrEmpty(MATCH_SECRET) && StringUtils.isNullOrEmpty(SPECTATE_SECRET)) {
            // Only add Buttons if Discord is not overriding it
            for (Map.Entry<String, Button> buttonElement : configData.buttons.entrySet()) {
                final JsonObject buttonObj = new JsonObject();
                final String overrideId = buttonElement.getKey();
                final Button button = buttonElement.getValue();
                if (!StringUtils.isNullOrEmpty(overrideId) &&
                        !overrideId.equalsIgnoreCase("default") &&
                        !StringUtils.isNullOrEmpty(button.label)) {
                    String label = StringUtils.formatWord(
                            getResult(button.label, overrideId + ".label"),
                            !CraftPresence.CONFIG.advancedSettings.formatWords, true, 1
                    );
                    String url = !StringUtils.isNullOrEmpty(button.url) ? getResult(
                            button.url, overrideId + ".url"
                    ) : "";

                    label = sanitizePlaceholders(label, 32);
                    url = sanitizePlaceholders(url, 512);
                    if (!StringUtils.isNullOrEmpty(label) && !StringUtils.isNullOrEmpty(url)) {
                        buttonObj.addProperty("label", label);
                        buttonObj.addProperty("url", url);
                        BUTTONS.add(buttonObj);
                    }
                }
            }
        }

        final RichPresence newRPCData = new RichPresence.Builder()
                .setState(GAME_STATE = sanitizePlaceholders(GAME_STATE, 128))
                .setDetails(DETAILS = sanitizePlaceholders(DETAILS, 128))
                .setStartTimestamp(START_TIMESTAMP)
                .setEndTimestamp(END_TIMESTAMP)
                .setLargeImage(LARGE_IMAGE_KEY = sanitizePlaceholders(LARGE_IMAGE_KEY, 256),
                        LARGE_IMAGE_TEXT = sanitizePlaceholders(LARGE_IMAGE_TEXT, 128))
                .setSmallImage(SMALL_IMAGE_KEY = sanitizePlaceholders(SMALL_IMAGE_KEY, 256),
                        SMALL_IMAGE_TEXT = sanitizePlaceholders(SMALL_IMAGE_TEXT, 128))
                .setParty(
                        PARTY_ID = sanitizePlaceholders(PARTY_ID, 128),
                        PARTY_SIZE, PARTY_MAX,
                        PARTY_PRIVACY.ordinal()
                )
                .setMatchSecret(MATCH_SECRET = sanitizePlaceholders(MATCH_SECRET, 128))
                .setJoinSecret(JOIN_SECRET = sanitizePlaceholders(JOIN_SECRET, 128))
                .setSpectateSecret(SPECTATE_SECRET = sanitizePlaceholders(SPECTATE_SECRET, 128))
                .setButtons(BUTTONS)
                .build();

        // Format Data to UTF_8 after Sent to RPC (RPC has its own Encoding)
        GAME_STATE = StringUtils.convertString(GAME_STATE, "UTF-8", false);
        DETAILS = StringUtils.convertString(DETAILS, "UTF-8", false);

        LARGE_IMAGE_KEY = StringUtils.convertString(LARGE_IMAGE_KEY, "UTF-8", false);
        SMALL_IMAGE_KEY = StringUtils.convertString(SMALL_IMAGE_KEY, "UTF-8", false);

        LARGE_IMAGE_TEXT = StringUtils.convertString(LARGE_IMAGE_TEXT, "UTF-8", false);
        SMALL_IMAGE_TEXT = StringUtils.convertString(SMALL_IMAGE_TEXT, "UTF-8", false);

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
        synchronized (forcedData) {
            if (!forcedData.isEmpty()) {
                for (Supplier<PresenceData> data : forcedData.values()) {
                    final PresenceData presenceInfo = data.get();
                    if (presenceInfo != null && presenceInfo.enabled && presenceInfo.useAsMain) {
                        return presenceInfo;
                    }
                }
            }
        }
        return CraftPresence.CONFIG.displaySettings.presenceData;
    }

    /**
     * Perform any needed Tick events, tied to {@link ScheduleUtils#MINIMUM_REFRESH_RATE} ticks
     */
    public void onTick() {
        // Menu Tick Event
        final boolean isMenuActive = CommandUtils.getMenuState() != CommandUtils.MenuStatus.None;
        final boolean isFullyLoaded = Constants.HAS_GAME_LOADED && isAvailable();
        if (!isFullyLoaded) {
            // Ensure Loading Presence has already passed, before any other type of presence displays
            CommandUtils.setMenuState(CommandUtils.MenuStatus.Loading);
        } else if (CraftPresence.player == null) {
            CommandUtils.setMenuState(CommandUtils.MenuStatus.MainMenu);
        } else if (isMenuActive) {
            CommandUtils.clearMenuState();
        }
        // Join Request Tick Event
        if (!CraftPresence.CONFIG.hasChanged && isFullyLoaded) {
            // Processing for Join Request Systems
            if (REQUESTER_USER != null && CraftPresence.SCHEDULER.TIMER <= 0) {
                respondToJoinRequest(IPCClient.ApprovalMode.DENY);
            }
        }
    }

    /**
     * Whether the RPC Service is currently available
     *
     * @return {@link Boolean#TRUE} if condition is satisfied
     */
    public boolean isAvailable() {
        return STATUS != DiscordStatus.Disconnected && !isClosed() && STATUS != DiscordStatus.Invalid;
    }

    /**
     * Whether the RPC Service is currently uninitialized or manually closed
     *
     * @return {@link Boolean#TRUE} if condition is satisfied
     */
    public boolean isClosed() {
        return STATUS == DiscordStatus.Closed;
    }

    /**
     * Whether the RPC Service is currently connected to an IPC Pipe
     *
     * @return {@link Boolean#TRUE} if condition is satisfied
     */
    public boolean isConnected() {
        return ipcInstance.getStatus() == PipeStatus.CONNECTED;
    }

    /**
     * Respond to a Join Request with the specified {@link com.jagrosh.discordipc.IPCClient.ApprovalMode}
     *
     * @param mode the approval state
     */
    public void respondToJoinRequest(final IPCClient.ApprovalMode mode) {
        if (REQUESTER_USER != null) {
            if (STATUS == DiscordStatus.JoinRequest) {
                if (isConnected()) {
                    ipcInstance.respondToJoinRequest(REQUESTER_USER, mode);
                }
                STATUS = DiscordStatus.Ready;
            }
            CraftPresence.SCHEDULER.TIMER = 0;
            REQUESTER_USER = null;
        }
    }
}

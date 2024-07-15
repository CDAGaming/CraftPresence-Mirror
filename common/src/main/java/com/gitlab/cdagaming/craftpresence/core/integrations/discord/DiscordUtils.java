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

package com.gitlab.cdagaming.craftpresence.core.integrations.discord;

import com.gitlab.cdagaming.craftpresence.core.Constants;
import com.gitlab.cdagaming.craftpresence.core.config.Config;
import com.gitlab.cdagaming.craftpresence.core.config.element.Button;
import com.gitlab.cdagaming.craftpresence.core.config.element.PresenceData;
import com.gitlab.cdagaming.craftpresence.core.impl.discord.CompiledPresence;
import com.gitlab.cdagaming.craftpresence.core.impl.discord.DiscordStatus;
import com.gitlab.cdagaming.craftpresence.core.impl.discord.PartyPrivacy;
import com.gitlab.cdagaming.craftpresence.core.integrations.discord.assets.DiscordAsset;
import com.gitlab.cdagaming.craftpresence.core.integrations.discord.assets.DiscordAssetUtils;
import com.gitlab.cdagaming.unilib.core.CoreUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jagrosh.discordipc.IPCClient;
import com.jagrosh.discordipc.IPCListener;
import com.jagrosh.discordipc.entities.DiscordBuild;
import com.jagrosh.discordipc.entities.RichPresence;
import com.jagrosh.discordipc.entities.User;
import com.jagrosh.discordipc.entities.pipe.PipeStatus;
import com.jagrosh.discordipc.exceptions.NoDiscordClientException;
import io.github.cdagaming.unicore.impl.Pair;
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

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
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
    private final Map<String, Supplier<Value>> placeholderData = StringUtils.newConcurrentHashMap();
    /**
     * A Mapping of the Last Requested Image Data
     * <p>Used to cache data for repeated images in other areas
     * <p>Format: evalKey, resultingKey
     */
    private final Map<String, String> cachedImageData = StringUtils.newHashMap();
    /**
     * When this is not empty, {@link DiscordUtils#buildRichPresence(PresenceData)} will use the first applicable data entry instead of the generic data
     */
    private final Map<String, Supplier<PresenceData>> forcedData = StringUtils.newConcurrentMap();
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
     * The current compiled Presence Data
     */
    public CompiledPresence PRESENCE;
    /**
     * Whether placeholder previews can be shows when generating argument messages
     */
    public Supplier<Boolean> canShowPreviews = () -> false;
    /**
     * Whether to format words while applying {@link PresenceData}
     */
    public Supplier<Boolean> canFormatWords = () -> false;
    /**
     * The Supplier for the default {@link PresenceData}, if any
     */
    public Supplier<PresenceData> defaultPresence;
    /**
     * The Supplier for the default icon, if any
     */
    public Supplier<String> defaultIconSupplier;
    /**
     * The assigned instance of {@link ScheduleUtils}, used for Join Requests
     */
    public ScheduleUtils SCHEDULER;
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
     * The Current Match Secret Key tied to the RPC, if any
     */
    public String MATCH_SECRET;
    /**
     * The Current Spectate Secret Key tied to the RPC, if any
     */
    public String SPECTATE_SECRET;
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
                Constants.getThreadFactory().newThread(this::shutDown)
        );

        // Setup Default / Static Placeholders
        syncPlaceholders();
    }

    /**
     * Initializes Critical Rich Presence Data
     *
     * @param debugMode    Whether to enable debug mode for this instance
     * @param verboseMode  Whether to enable verbose mode for this instance
     * @param listener     The listener to assign for this instance
     * @param dynamicIcons The Dynamic Icons to sync, if supplied
     */
    public void init(final boolean debugMode, final boolean verboseMode, final IPCListener listener, final Map<String, String> dynamicIcons) {
        // Create IPC Instance
        ipcInstance = new IPCClient(Long.parseLong(CLIENT_ID), debugMode, verboseMode, AUTO_REGISTER, CLIENT_ID);
        if (listener != null) {
            ipcInstance.setListener(listener);
        }
        // Initialize Discord Assets
        DiscordAssetUtils.loadAssets(CLIENT_ID, true, dynamicIcons);
        // Mark as Disconnected to trigger auto-sync
        STATUS = DiscordStatus.Disconnected;
    }

    /**
     * Initializes Critical Rich Presence Data
     *
     * @param debugMode   Whether to enable debug mode for this instance
     * @param verboseMode Whether to enable verbose mode for this instance
     * @param listener    The listener to assign for this instance
     */
    public void init(final boolean debugMode, final boolean verboseMode, final IPCListener listener) {
        init(debugMode, verboseMode, listener, null);
    }

    /**
     * Initializes Critical Rich Presence Data
     *
     * @param debugMode   Whether to enable debug mode for this instance
     * @param verboseMode Whether to enable verbose mode for this instance
     */
    public void init(final boolean debugMode, final boolean verboseMode) {
        init(debugMode, verboseMode, null);
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
                final String messagePrefix = Constants.TRANSLATOR.translate("gui.config.message.editor.message");
                final String verbosePrefix = Constants.TRANSLATOR.translate("craftpresence.logger.error.verbose");

                Constants.LOG.error(Constants.TRANSLATOR.translate("craftpresence.logger.error.connect"));
                Constants.LOG.printStackTrace(ex, messagePrefix, verbosePrefix);

                // Mark as Closed if we experience an actual Exception
                STATUS = DiscordStatus.Closed;
                connectThreadActive = false;
            }
        }
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
        final String data = StringUtils.getOrDefault(input);

        if (!plain) {
            final Pair<String, VariableReplacementTransformer> resultData = generateTransformer(
                    data, overrideId, replacements
            );
            return getCompileResult(resultData != null ? resultData.getFirst() : data, null, resultData != null ? resultData.getSecond() : null);
        } else {
            return () -> Value.string(data);
        }
    }

    /**
     * Generates a {@link VariableReplacementTransformer} for the specified arguments
     *
     * @param input        The original string to interpret
     * @param overrideId   The override identifier to interpret
     * @param replacements A mapping of additional replacements to perform
     * @return the processed string, alongside the variable transformer
     */
    @SafeVarargs
    public final Pair<String, VariableReplacementTransformer> generateTransformer(final String input, final String overrideId, final Pair<String, Supplier<String>>... replacements) {
        overrideTarget = overrideId;
        if (replacements == null || replacements.length == 0) {
            return null;
        }

        final VariableReplacementTransformer transformer = new VariableReplacementTransformer();
        String data = StringUtils.getOrDefault(input);

        for (Pair<String, Supplier<String>> replacement : replacements) {
            if (replacement != null) {
                final Supplier<String> info = replacement.getSecond();
                if (info != null) {
                    final String value = info.get();
                    if (placeholderData.containsKey(value)) {
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

    /**
     * Interpret the processed {@link Script} from parsing the specified args, and compile it
     *
     * @param data          The data or expression to be parsed
     * @param showLogging   Whether to display logging for this function
     * @param verbosePrefix The logging prefix to use, if showLogging is false
     * @param output        If specified, attach the decompiled info to this {@link Appendable}
     * @param transforms    Any additional expression transformations, to be done before compiling
     * @return the processed output
     */
    public Supplier<Value> getCompileResult(final String data, final boolean showLogging, final String verbosePrefix, final Appendable output, Expr.Visitor... transforms) {
        Parser.Result result = null;
        try {
            result = Parser.parse(data);
        } catch (Throwable ignored) {
        }

        final String originalPrefix = Constants.TRANSLATOR.translate("gui.config.message.editor.original");
        final String messagePrefix = Constants.TRANSLATOR.translate("gui.config.message.editor.message");
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
        return getCompileResult(data, Constants.LOG.isDebugMode(), Constants.TRANSLATOR.translate("craftpresence.logger.error.verbose"), output, transforms);
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
            syncArgument(argumentName, () -> Long.toString(newTimestamp.get()), true);
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
     * <p>INTERNAL USAGE ONLY. See {@link ValueMap#set(String, Supplier)}
     *
     * @param source   The {@link ValueMap} to interpret
     * @param parent   The path parent, used in {@link ValueMap} traversal
     * @param name     The Specified Argument to Synchronize for
     * @param supplier The data to attach to the Specified Argument
     * @return the current {@link ValueMap} instance
     */
    private ValueMap setArgument(final ValueMap source, final String parent, final String name, final Supplier<Value> supplier) {
        final int dotI = name.indexOf('.');

        if (dotI >= 0) {
            // Split name based on the dot
            final String name1 = name.substring(0, dotI);
            final String name2 = name.substring(dotI + 1);

            // Get the map
            ValueMap map;
            final Supplier<Value> valueSupplier = source.getRaw(name1);

            if (valueSupplier == null) {
                map = new ValueMap();
                final Supplier<Value> newSupplier = () -> Value.map(map);
                source.setRaw(name1, newSupplier);
                placeholderData.put(parent + name1, newSupplier);
            } else {
                final Value value = valueSupplier.get();

                if (value.isMap()) map = value.getMap();
                else {
                    map = new ValueMap();
                    final Supplier<Value> newSupplier = () -> Value.map(map);
                    source.setRaw(name1, newSupplier);
                    placeholderData.put(parent + name1, newSupplier);
                }
            }

            // Set the supplier
            setArgument(map, parent + name1 + ".", name2, supplier);
        } else {
            source.setRaw(name, supplier);
            placeholderData.put(parent + name, supplier);
        }
        return source;
    }

    /**
     * Synchronizes the Specified Argument as an RPC Message or an Icon Placeholder
     * <p>INTERNAL USAGE ONLY. See {@link ValueMap#set(String, Supplier)}
     *
     * @param source   The {@link ValueMap} to interpret
     * @param name     The Specified Argument to Synchronize for
     * @param supplier The data to attach to the Specified Argument
     * @return the current {@link ValueMap} instance
     */
    private ValueMap setArgument(final ValueMap source, final String name, final Supplier<Value> supplier) {
        return setArgument(source, "", name, supplier);
    }

    /**
     * Synchronizes the Specified Argument as an RPC Message or an Icon Placeholder
     *
     * @param argumentName The Specified Argument to Synchronize for
     * @param data         The data to attach to the Specified Argument
     * @return the current {@link ValueMap} instance
     */
    public ValueMap setArgument(final String argumentName, final Supplier<Value> data) {
        final ValueMap globals = scriptEngine.getGlobals();
        if (!StringUtils.isNullOrEmpty(argumentName)) {
            return setArgument(globals, argumentName, data);
        }
        return globals;
    }

    /**
     * Removes the Specified Argument as an RPC Placeholder
     * <p>INTERNAL USAGE ONLY. See {@link ValueMap#remove(String)}
     *
     * @param source The {@link ValueMap} to interpret
     * @param parent The path parent, used in {@link ValueMap} traversal
     * @param name   The Specified Argument to Synchronize for
     * @return the previous value assigned, if any
     */
    private Supplier<Value> removeArgument(final ValueMap source, final String parent, final String name) {
        final int dotI = name.indexOf('.');

        if (dotI >= 0) {
            // Split name based on the dot
            final String name1 = name.substring(0, dotI);
            final String name2 = name.substring(dotI + 1);

            // Get child value
            final Supplier<Value> valueSupplier = source.getRaw(name1);
            if (valueSupplier == null) return null;
            else {
                // Make sure the child value is a map
                final Value value = valueSupplier.get();
                if (!value.isMap()) {
                    placeholderData.remove(parent + name1);
                    return source.removeRaw(name1);
                } else {
                    return removeArgument(value.getMap(), parent + name1 + ".", name2);
                }
            }
        } else {
            placeholderData.remove(parent + name);
            return source.removeRaw(name);
        }
    }

    /**
     * Remove any arguments following the specified formats
     * <p>INTERNAL USAGE ONLY. See {@link ValueMap#remove(String)}
     *
     * @param source The {@link ValueMap} to interpret
     * @param parent The path parent, used in {@link ValueMap} traversal
     * @param args   The string formats to interpret
     * @return a mapping of all removed objects, using format path:data
     */
    private Map<String, Supplier<Value>> removeArguments(final ValueMap source, final String parent, final Set<String> args) {
        if (args == null || args.isEmpty()) {
            return null;
        }

        final boolean hasParent = !StringUtils.isNullOrEmpty(parent);
        final String prefix = parent + (hasParent ? "." : "");

        final Map<String, Supplier<Value>> results = StringUtils.newHashMap();
        for (String arg : args) {
            final String path = prefix + arg;
            final Supplier<Value> supplier = getArgument(path);
            if (supplier != null) {
                final Value value = supplier.get();
                if (value.isMap()) {
                    final ValueMap newMap = value.getMap();
                    final Set<String> keys = newMap.keys();
                    if (!keys.isEmpty()) {
                        results.putAll(
                                removeArguments(newMap, path, keys)
                        );
                    }
                }
                results.put(path, removeArgument(source, prefix, arg));
            }
        }
        return results;
    }

    /**
     * Remove any arguments following the specified formats
     * <p>INTERNAL USAGE ONLY. See {@link ValueMap#remove(String)}
     *
     * @param source The {@link ValueMap} to interpret
     * @param parent The path parent, used in {@link ValueMap} traversal
     * @param args   The string formats to interpret
     * @return a mapping of all removed objects, using format path:data
     */
    private Map<String, Supplier<Value>> removeArguments(final ValueMap source, final String parent, final String... args) {
        if (args == null || args.length == 0) {
            return null;
        }
        return removeArguments(source, parent, StringUtils.newHashSet(args));
    }

    /**
     * Remove any arguments following the specified formats
     * <p>INTERNAL USAGE ONLY. See {@link ValueMap#remove(String)}
     *
     * @param source The {@link ValueMap} to interpret
     * @param args   The string formats to interpret
     * @return a mapping of all removed objects, using format path:data
     */
    private Map<String, Supplier<Value>> removeArguments(final ValueMap source, final String... args) {
        return removeArguments(source, "", args);
    }

    /**
     * Remove any arguments following the specified formats
     *
     * @param args The string formats to interpret
     * @return a mapping of all removed objects, using format path:data
     */
    public Map<String, Supplier<Value>> removeArguments(final String... args) {
        return removeArguments(scriptEngine.getGlobals(), args);
    }

    /**
     * Synchronizes the Specified Argument as an RPC Message or an Icon Placeholder
     *
     * @param argumentName The Specified Argument to Synchronize for
     * @param data         The data to attach to the Specified Argument
     * @param plain        Whether the expression should be parsed as a plain string
     * @return the current {@link ValueMap} instance
     */
    public ValueMap syncArgument(final String argumentName, final Supplier<Object> data, final boolean plain) {
        return setArgument(argumentName, () -> toValue(data.get(), plain));
    }

    /**
     * Synchronizes the Specified Argument as an RPC Message or an Icon Placeholder
     *
     * @param argumentName The Specified Argument to Synchronize for
     * @param data         The data to attach to the Specified Argument
     * @param plain        Whether the expression should be parsed as a plain string
     * @return the current {@link ValueMap} instance
     */
    public ValueMap syncArgument(final String argumentName, final Object data, final boolean plain) {
        return syncArgument(argumentName, () -> data, plain);
    }

    /**
     * Synchronizes the Specified Argument as an RPC Message or an Icon Placeholder
     *
     * @param argumentName The Specified Argument to Synchronize for
     * @param data         The data to attach to the Specified Argument
     * @return the current {@link ValueMap} instance
     */
    public ValueMap syncArgument(final String argumentName, final Supplier<Object> data) {
        return syncArgument(argumentName, data, false);
    }

    /**
     * Synchronizes the Specified Argument as an RPC Message or an Icon Placeholder
     *
     * @param argumentName The Specified Argument to Synchronize for
     * @param data         The data to attach to the Specified Argument
     * @return the current {@link ValueMap} instance
     */
    public ValueMap syncArgument(final String argumentName, final Object data) {
        return syncArgument(argumentName, data, false);
    }

    /**
     * Synchronizes the Specified Argument as an RPC Message or an Icon Placeholder
     *
     * @param argumentName The Specified Argument to Synchronize for
     * @param data         The data to attach to the Specified Argument
     * @return the current {@link ValueMap} instance
     */
    public ValueMap syncFunction(final String argumentName, final SFunction data) {
        return syncArgument(argumentName, data);
    }

    /**
     * Refresh Dynamic Variable Data, removing any data no longer in-play
     *
     * @param oldData      The old data to interpret
     * @param newData      The new data to interpret
     * @param newRetriever The data supplier to use alongside the newData
     */
    public void syncDynamicVariables(final Map<String, String> oldData, final Map<String, String> newData, final Function<String, Supplier<Object>> newRetriever) {
        final boolean hasOldData = oldData != null && !oldData.isEmpty();
        if (hasOldData) {
            for (String entry : oldData.keySet()) {
                if (!entry.equals("default") && !newData.containsKey(entry)) {
                    removeArguments("custom." + entry);
                }
            }
        }

        for (String entry : newData.keySet()) {
            if (!entry.equals("default") && (!hasOldData || !oldData.containsKey(entry))) {
                syncArgument(
                        "custom." + entry,
                        newRetriever.apply(entry)
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
        return switch (data) {
            case Number number -> Value.number(number.doubleValue());
            case Boolean b -> Value.bool(b);
            case ValueMap valueMap -> Value.map(valueMap);
            case SFunction sFunction -> Value.function(sFunction);
            case String s -> compileData(s, plain).get();
            case null, default -> data != null ? Value.object(data) : Value.null_();
        };
    }

    /**
     * Retrieves any arguments within the specified type that match the specified string formats
     *
     * @param args The string formats to interpret
     * @return A List of the entries that satisfy the method conditions
     */
    public Map<String, Supplier<Value>> getArguments(final String... args) {
        final Map<String, Supplier<Value>> list = StringUtils.newTreeMap();

        for (Map.Entry<String, Supplier<Value>> entry : placeholderData.entrySet()) {
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

    /**
     * Whether the specified {@link Value} matches the specified type
     *
     * @param type The {@link Value} type to interpret
     * @param data The {@link Value} to interpret
     * @return {@link Boolean#TRUE} if both the type and its condition is satisfied
     */
    public boolean matchesType(final String type, final Value data) {
        return switch (type) {
            case "function" -> data.isFunction();
            case "object" -> data.isObject();
            case "bool", "boolean" -> data.isBool();
            case "map" -> data.isMap();
            case "int", "integer", "float", "double", "number" -> data.isNumber();
            case "text", "string" -> data.isString();
            case "empty", "null" -> data.isNull();
            case "any", "all" -> true;
            default -> false;
        };
    }

    /**
     * Retrieves the argument within the specified type that matches the specified string format
     *
     * @param key The input to interpret
     * @return The entry that satisfies the method conditions, or null
     */
    public Supplier<Value> getArgument(final String key) {
        return placeholderData.get(key);
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
     * Generate a parsable display string for the argument data provided
     *
     * @param addExtraData Whether to add additional data to the string
     * @param args         The data to interpret
     * @param formats      The argument formats to interpret
     * @return the parsable string
     */
    public String generateArgumentMessage(final boolean addExtraData, final Map<String, Supplier<Value>> args, final String... formats) {
        final StringBuilder resultString = new StringBuilder(
                Constants.TRANSLATOR.translate(Constants.MOD_ID + ".placeholders.notes")
        );
        resultString.append("\\n\\n")
                .append(Constants.TRANSLATOR.translate(Constants.MOD_ID + ".placeholders.title"));

        final boolean hasNoFormats = formats == null || formats.length < 1 || formats[0] == null;
        if (!hasNoFormats) {
            resultString.append(" (").append(String.join(",", formats)).append(")");
        }
        resultString.append(":");

        final StringBuilder placeholderString = new StringBuilder();
        if (args != null && !args.isEmpty()) {
            for (Map.Entry<String, Supplier<Value>> argData : args.entrySet()) {
                final String argumentMessage = generateArgumentMessage(
                        argData.getKey(), argData.getValue(), true, true, addExtraData
                );
                if (!StringUtils.isNullOrEmpty(argumentMessage)) {
                    placeholderString.append("\\n").append(argumentMessage);
                }
            }
        }

        if (placeholderString.isEmpty()) {
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
     * @param requireDesc     Whether to require an argument description to return a result
     * @param includeName     Whether to inline the description with the argument name
     * @param addExtraData    Whether to add additional data to the string
     * @param prefix          The prefix to begin each line with (Overwritten if `includeName` is true)
     * @return the parsable string, or null if not found
     */
    public String generateArgumentMessage(final String placeholderName, final Supplier<Value> suppliedInfo, final boolean requireDesc, final boolean includeName, final boolean addExtraData, final String prefix) {
        final String placeholderTranslation = Constants.MOD_ID + ".placeholders." + placeholderName + ".description";
        final boolean hasDescription = Constants.TRANSLATOR.hasTranslation(placeholderTranslation);

        if (requireDesc && !hasDescription) {
            return null;
        }

        final StringBuilder placeholderString = new StringBuilder();

        final String placeholderUsage = Constants.MOD_ID + ".placeholders." + placeholderName + ".usage";
        String start = prefix;

        if (includeName) {
            String placeholderFormat = " - ";

            if (hasDescription) {
                final String placeholderDescription = Constants.TRANSLATOR.translate(placeholderTranslation);
                placeholderFormat = " - " + placeholderName + " = " + placeholderDescription;
            } else {
                placeholderFormat += placeholderName;
            }

            placeholderString.append(placeholderFormat);
            start = "\\n" + prefix;
        } else if (hasDescription) {
            placeholderString.append(start)
                    .append(Constants.TRANSLATOR.translate("gui.config.message.editor.description"))
                    .append(" \"")
                    .append(Constants.TRANSLATOR.translate(placeholderTranslation))
                    .append("\"");
            start = "\\n" + prefix;
        }

        if (Constants.TRANSLATOR.hasTranslation(placeholderUsage)) {
            placeholderString.append(start)
                    .append(Constants.TRANSLATOR.translate("gui.config.message.editor.usage"))
                    .append(" \"")
                    .append(Constants.TRANSLATOR.translate(placeholderUsage))
                    .append("\"");
            start = "\\n" + prefix;
        }

        if (suppliedInfo != null && addExtraData && isDefaultPlaceholder(placeholderName.toLowerCase())) {
            final Value rawValue = suppliedInfo.get();
            final String tagValue = rawValue.toString();
            if (!rawValue.isNull() && !rawValue.isFunction() && !StringUtils.isNullOrEmpty(tagValue)) {
                placeholderString.append(start)
                        .append(Constants.TRANSLATOR.translate("gui.config.message.editor.preview"))
                        .append(" \"")
                        .append(tagValue.length() >= 128 ? "<...>" : tagValue)
                        .append("\"");
            }
        }
        return placeholderString.toString();
    }

    /**
     * Generate a parsable display string for the argument data provided
     *
     * @param placeholderName The Specified Argument to interpret
     * @param requireDesc     Whether to require an argument description to return a result
     * @param includeName     Whether to inline the description with the argument name
     * @param addExtraData    Whether to add additional data to the string
     * @param prefix          The prefix to begin each line with (Overwritten if `includeName` is true)
     * @return the parsable string
     */
    public String generateArgumentMessage(final String placeholderName, final boolean requireDesc, final boolean includeName, final boolean addExtraData, final String prefix) {
        return generateArgumentMessage(placeholderName, getArgument(placeholderName), requireDesc, includeName, addExtraData, prefix);
    }

    /**
     * Generate a parsable display string for the argument data provided
     *
     * @param placeholderName The Specified Argument to interpret
     * @param suppliedInfo    The current argument info, if any
     * @param requireDesc     Whether to require an argument description to return a result
     * @param includeName     Whether to inline the description with the argument name
     * @param addExtraData    Whether to add additional data to the string
     * @return the parsable string
     */
    public String generateArgumentMessage(final String placeholderName, final Supplier<Value> suppliedInfo, final boolean requireDesc, final boolean includeName, final boolean addExtraData) {
        return generateArgumentMessage(placeholderName, suppliedInfo, requireDesc, includeName, addExtraData, " ==> ");
    }

    /**
     * Generate a parsable display string for the argument data provided
     *
     * @param placeholderName The Specified Argument to interpret
     * @param requireDesc     Whether to require an argument description to return a result
     * @param includeName     Whether to inline the description with the argument name
     * @param addExtraData    Whether to add additional data to the string
     * @return the parsable string
     */
    public String generateArgumentMessage(final String placeholderName, final boolean requireDesc, final boolean includeName, final boolean addExtraData) {
        return generateArgumentMessage(placeholderName, requireDesc, includeName, addExtraData, " ==> ");
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
        return generateArgumentMessage(addExtraData, getArguments(formats), formats);
    }

    /**
     * Generate a parsable display string for the argument data provided
     *
     * @param formats The argument formats to interpret
     * @return the parsable string
     */
    public String generateArgumentMessage(final String... formats) {
        return generateArgumentMessage(canShowPreviews.get(), formats);
    }

    /**
     * Synchronizes and Updates Dynamic Placeholder data in this module
     */
    public void syncPlaceholders() {
        FunctionsLib.init(this);
        syncArgument("general.mods", CoreUtils::getModCount);
        syncArgument("data.general.version", () -> CoreUtils.MCBuildVersion, true);
        syncArgument("data.general.protocol", () -> CoreUtils.MCBuildProtocol);
        syncTimestamp(() -> {
            final long currentStartTime = !UPDATE_TIMESTAMP && lastStartTime > 0 ?
                    lastStartTime : TimeUtils.toEpochMilli();
            lastStartTime = currentStartTime;
            return currentStartTime;
        }, "data.general.time");
    }

    /**
     * Add an Endpoint Icon to the Selectable Dynamic Assets
     *
     * @param config       The {@link Config} instance to interpret
     * @param endpoint     The endpoint url to be interpreted
     * @param name         The name to be interpreted
     * @param key          The key to be interpreted
     * @param filterToMain Whether the added icon will be used in runtime
     * @return {@link Boolean#TRUE} if operation is allowed
     */
    public boolean addEndpointIcon(final Config config, final String endpoint, final String name, final String key, final boolean filterToMain) {
        final boolean canUseEndpointIcon = !config.hasChanged() &&
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
                if (filterToMain) {
                    DiscordAssetUtils.syncCustomAssets(config.displaySettings.dynamicIcons);
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
     * @param name     The name to be interpreted
     * @param key      The key to be interpreted
     * @return {@link Boolean#TRUE} if operation is allowed
     */
    public boolean addEndpointIcon(final Config config, final String endpoint, final String name, final String key) {
        return addEndpointIcon(config, endpoint, name, key, false);
    }

    /**
     * Add an Endpoint Icon to the Selectable Dynamic Assets
     *
     * @param config       The {@link Config} instance to interpret
     * @param endpoint     The endpoint url to be interpreted
     * @param name         The name or key to be interpreted
     * @param filterToMain Whether the added icon will be used in runtime
     * @return {@link Boolean#TRUE} if operation is allowed
     */
    public boolean addEndpointIcon(final Config config, final String endpoint, final String name, final boolean filterToMain) {
        return addEndpointIcon(config, endpoint, name, "", filterToMain);
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
        return addEndpointIcon(config, endpoint, name, false);
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

        if (isConnected() && (ALLOW_DUPLICATE_PACKETS || !Objects.equals(presence, currentPresence))) {
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
                String defaultIcon = "";
                if (!allowNull) {
                    if (defaultIconSupplier != null) {
                        defaultIcon = DiscordAssetUtils.getKey(defaultIconSupplier.get());
                    }
                    defaultIcon = StringUtils.getOrDefault(defaultIcon, DiscordAssetUtils.getRandomAssetName());
                }
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
     * @param allowNull   If allowed to return null if unable to find any matches, otherwise uses the Default Icon in Config
     * @param evalStrings The Specified Icon Key(s) to search for from the {@link DiscordUtils#CLIENT_ID} Assets
     * @return The found or alternative matching Icon Key
     */
    public String imageOf(final boolean allowNull, final String... evalStrings) {
        return imageOf(allowNull, true, evalStrings);
    }

    /**
     * Rejects the active Join Request, if any
     */
    public void acceptJoinRequest() {
        respondToJoinRequest(IPCClient.ApprovalMode.ACCEPT);
    }

    /**
     * Rejects the active Join Request, if any
     */
    public void denyJoinRequest() {
        respondToJoinRequest(IPCClient.ApprovalMode.DENY);
    }

    /**
     * Clears Related Party Session Information from the RPC, and updates if needed
     */
    public void clearPartyData() {
        denyJoinRequest();

        JOIN_SECRET = null;
        PARTY_ID = null;
        PARTY_SIZE = 0;
        PARTY_MAX = 0;
    }

    /**
     * Clears all Data from the RPC Presence Fields
     */
    public void clearPresenceData() {
        PRESENCE = new CompiledPresence(
                "", "",
                "", "",
                null, null,
                "", "",
                "", "",
                0L, 0L,
                new JsonArray()
        );

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
            } catch (Throwable ex) {
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
     * Compiles Presence Data based on Queued Data
     *
     * @param configData The {@link PresenceData} to be interpreted
     * @param useAsMain  Whether the compiled data should be used as the main data
     * @return A copy of the {@link CompiledPresence} alongside A New Instance of {@link RichPresence}
     */
    public Pair<CompiledPresence, RichPresence> compilePresence(final PresenceData configData, final boolean useAsMain) {
        // Do not compile Presence while offline or in-progress of connecting
        if (useAsMain) {
            if (!isAvailable() || !isConnected() || connectThreadActive || configData == null) {
                return null;
            }
        }

        final boolean formatWords = canFormatWords.get();

        // Format Presence based on Arguments available in argumentData
        String details = StringUtils.formatWord(getResult(configData.details, "details"), !formatWords, true, 1);
        String state = StringUtils.formatWord(getResult(configData.gameState, "gameState"), !formatWords, true, 1);

        final String rawLargeImage = getResult(configData.largeImageKey, "largeImageKey");
        final String rawSmallImage = getResult(configData.smallImageKey, "smallImageKey");

        final DiscordAsset largeAsset = DiscordAssetUtils.get(rawLargeImage);
        final DiscordAsset smallAsset = DiscordAssetUtils.get(rawSmallImage);

        String largeImageKey = largeAsset != null ? (largeAsset.getType().equals(DiscordAsset.AssetType.CUSTOM) ?
                getResult(largeAsset.getUrl()) : largeAsset.getName()) : rawLargeImage;
        String smallImageKey = smallAsset != null ? (smallAsset.getType().equals(DiscordAsset.AssetType.CUSTOM) ?
                getResult(smallAsset.getUrl()) : smallAsset.getName()) : rawSmallImage;

        String largeImageText = StringUtils.formatWord(getResult(configData.largeImageText, "largeImageText"), !formatWords, true, 1);
        String smallImageText = StringUtils.formatWord(getResult(configData.smallImageText, "smallImageText"), !formatWords, true, 1);

        final Pair<Boolean, Long> startData = StringUtils.getValidLong(
                getResult(configData.startTimestamp, "startTimestamp")
        );
        long startTimestamp, endTimestamp;
        if (startData.getFirst()) {
            startTimestamp = startData.getSecond();
            final Pair<Boolean, Long> endData = StringUtils.getValidLong(
                    getResult(configData.endTimestamp, "endTimestamp")
            );
            endTimestamp = endData.getFirst() ? endData.getSecond() : 0;
        } else {
            startTimestamp = 0;
            endTimestamp = 0;
        }

        // Format Buttons Array based on Config Value
        final JsonArray buttons = new JsonArray();
        if (!useAsMain || (StringUtils.isNullOrEmpty(JOIN_SECRET) &&
                StringUtils.isNullOrEmpty(MATCH_SECRET) &&
                StringUtils.isNullOrEmpty(SPECTATE_SECRET)
        )) {
            // Only add Buttons if Discord is not overriding it, or not compiling for main
            for (Map.Entry<String, Button> buttonElement : configData.buttons.entrySet()) {
                final JsonObject buttonObj = new JsonObject();
                final String overrideId = buttonElement.getKey();
                final Button button = buttonElement.getValue();
                if (!StringUtils.isNullOrEmpty(overrideId) &&
                        !overrideId.equalsIgnoreCase("default") &&
                        !StringUtils.isNullOrEmpty(button.label)) {
                    String label = StringUtils.formatWord(
                            getResult(button.label, overrideId + ".label"),
                            !formatWords, true, 1
                    );
                    String url = !StringUtils.isNullOrEmpty(button.url) ? getResult(
                            button.url, overrideId + ".url"
                    ) : "";

                    label = sanitizePlaceholders(label, 32);
                    url = sanitizePlaceholders(url, 512);
                    if (!StringUtils.isNullOrEmpty(label) && !StringUtils.isNullOrEmpty(url)) {
                        buttonObj.addProperty("label", label);
                        buttonObj.addProperty("url", url);
                        buttons.add(buttonObj);
                    }
                }
            }
        }

        final RichPresence.Builder newRPCData = new RichPresence.Builder()
                .setState(state = sanitizePlaceholders(state, 128))
                .setDetails(details = sanitizePlaceholders(details, 128))
                .setStartTimestamp(startTimestamp)
                .setEndTimestamp(endTimestamp)
                .setLargeImage(largeImageKey = sanitizePlaceholders(largeImageKey, 256),
                        largeImageText = sanitizePlaceholders(largeImageText, 128))
                .setSmallImage(smallImageKey = sanitizePlaceholders(smallImageKey, 256),
                        smallImageText = sanitizePlaceholders(smallImageText, 128))
                .setButtons(buttons);

        // Format Data to UTF_8 after Sent to RPC (RPC has its own Encoding)
        state = StringUtils.convertString(state, "UTF-8", false);
        details = StringUtils.convertString(details, "UTF-8", false);

        largeImageKey = StringUtils.convertString(largeImageKey, "UTF-8", false);
        smallImageKey = StringUtils.convertString(smallImageKey, "UTF-8", false);

        largeImageText = StringUtils.convertString(largeImageText, "UTF-8", false);
        smallImageText = StringUtils.convertString(smallImageText, "UTF-8", false);

        final CompiledPresence data = new CompiledPresence(
                details, state,
                rawLargeImage, rawSmallImage,
                largeAsset, smallAsset,
                largeImageKey, smallImageKey,
                largeImageText, smallImageText,
                startTimestamp, endTimestamp,
                buttons
        );

        if (useAsMain) {
            // Apply Data to Global Fields
            PRESENCE = data;

            // Assign Secret Data to the RPC Builder
            newRPCData
                    .setParty(
                            PARTY_ID = sanitizePlaceholders(PARTY_ID, 128),
                            PARTY_SIZE, PARTY_MAX,
                            PARTY_PRIVACY.ordinal()
                    )
                    .setMatchSecret(MATCH_SECRET = sanitizePlaceholders(MATCH_SECRET, 128))
                    .setJoinSecret(JOIN_SECRET = sanitizePlaceholders(JOIN_SECRET, 128))
                    .setSpectateSecret(SPECTATE_SECRET = sanitizePlaceholders(SPECTATE_SECRET, 128));
        }

        return new Pair<>(data, newRPCData.build());
    }

    /**
     * Compiles Presence Data based on Queued Data
     *
     * @param configData The {@link PresenceData} to be interpreted
     * @return A copy of the {@link CompiledPresence} alongside A New Instance of {@link RichPresence}
     */
    public CompiledPresence compilePresence(final PresenceData configData) {
        final Pair<CompiledPresence, RichPresence> data = compilePresence(configData, false);
        return data == null ? null : data.getFirst();
    }

    /**
     * Compiles Presence Data based on Queued Data
     *
     * @return A copy of the {@link CompiledPresence} alongside A New Instance of {@link RichPresence}
     */
    public CompiledPresence compilePresence() {
        return compilePresence(getPresenceData());
    }

    /**
     * Builds a New Instance of {@link RichPresence} based on Queued Data
     *
     * @param configData The {@link PresenceData} to be interpreted
     * @return A New Instance of {@link RichPresence}
     */
    public RichPresence buildRichPresence(final PresenceData configData) {
        final Pair<CompiledPresence, RichPresence> data = compilePresence(configData, true);
        return data == null ? null : data.getSecond();
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
        if (!forcedData.isEmpty()) {
            for (Supplier<PresenceData> data : forcedData.values()) {
                final PresenceData presenceInfo = data.get();
                if (presenceInfo != null && presenceInfo.enabled && presenceInfo.useAsMain) {
                    return presenceInfo;
                }
            }
        }
        return defaultPresence != null ? defaultPresence.get() : null;
    }

    /**
     * Perform any needed Tick events, tied to {@link ScheduleUtils#MINIMUM_REFRESH_RATE} ticks
     */
    public void onTick() {
        if (isAvailable()) {
            // Processing for Join Request Systems
            if (REQUESTER_USER != null && SCHEDULER.TIMER <= 0) {
                denyJoinRequest();
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
     * Respond to a Join Request with the specified {@link IPCClient.ApprovalMode}
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
            if (SCHEDULER != null) {
                SCHEDULER.TIMER = 0;
            }
            REQUESTER_USER = null;
        }
    }
}

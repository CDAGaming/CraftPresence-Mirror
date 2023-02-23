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
import com.gitlab.cdagaming.craftpresence.integrations.discord.FunctionsLib;
import com.gitlab.cdagaming.craftpresence.integrations.mcupdater.MCUpdaterUtils;
import com.gitlab.cdagaming.craftpresence.integrations.multimc.MultiMCUtils;
import com.gitlab.cdagaming.craftpresence.integrations.technic.TechnicUtils;
import com.gitlab.cdagaming.craftpresence.utils.CommandUtils;
import com.gitlab.cdagaming.craftpresence.utils.FileUtils;
import com.gitlab.cdagaming.craftpresence.utils.StringUtils;
import com.gitlab.cdagaming.craftpresence.utils.SystemUtils;
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
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.lang3.exception.ExceptionUtils;
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
import java.util.TreeMap;
import java.util.function.Supplier;

/**
 * Variables and Methods used to update the RPC Presence States to display within Discord
 *
 * @author CDAGaming
 */
@SuppressFBWarnings("UWF_UNWRITTEN_PUBLIC_OR_PROTECTED_FIELD")
public class DiscordUtils {
    /**
     * A mapping of the arguments that have overwritten module data
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
     * Whether Discord is currently awaiting a response to an Ask to Join or Spectate Request, if any
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
     * The duration or timestamp of the last running instance
     */
    private long lastStartTime;
    /**
     * The duration or timestamp of the current running instance
     */
    private long currentStartTime;

    /**
     * Setup any Critical Methods needed for the RPC
     * <p>In this case, ensures a Thread is in place to shut down the RPC onExit
     */
    public synchronized void setup() {
        final Thread shutdownThread = new Thread("CraftPresence-ShutDown-Handler") {
            @Override
            public void run() {
                CraftPresence.SYSTEM.IS_GAME_CLOSING = true;
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
            // Create IPC Instance and Listener and Make a Connection if possible
            scriptEngine = new Starscript();
            FunctionsLib.init(scriptEngine);

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
            if (CommandUtils.isVerboseMode()) {
                ex.printStackTrace();
            }
        }

        // Ensure Initial Data Resets properly
        synchronized (placeholderData) {
            overrideData.clear();
            placeholderData.clear();
        }
        CommandUtils.isInMainMenu = false;

        // Update Start Timestamp onInit, if needed
        final long newStartTime = System.currentTimeMillis() / 1000L;
        currentStartTime = !updateTimestamp && lastStartTime > 0 ?
                lastStartTime :
                newStartTime;
        lastStartTime = newStartTime;
    }

    /**
     * Initializes and Synchronizes Initial Rich Presence Data
     *
     * @param debugMode       Whether to enable debug mode for this instance
     * @param updateTimestamp Whether to update the starting timestamp
     */
    public synchronized void init(final boolean debugMode, final boolean updateTimestamp) {
        init(debugMode, CommandUtils.isVerboseMode(), updateTimestamp);
    }

    /**
     * Initializes and Synchronizes Initial Rich Presence Data
     *
     * @param updateTimestamp Whether to update the starting timestamp
     */
    public synchronized void init(final boolean updateTimestamp) {
        init(CommandUtils.isDebugMode(), updateTimestamp);
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
     * @param input    The string to interpret
     * @param length   The required length the input must fall under
     * @param fallback The fallback string to interpret
     * @return The resulting output string
     */
    public String sanitizePlaceholders(final String input, int length, final String fallback) {
        return StringUtils.getOrDefault(
                input, fallback,
                StringUtils.NULL_OR_EMPTY.negate()
                        .and(e -> StringUtils.getBytes(e, "UTF-8").length < length)
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
            final Map<String, Supplier<Value>> placeholders = Maps.newTreeMap(placeholderData);
            final String data = StringUtils.getOrDefault(input);

            if (!plain) {
                return getCompileResult(data, null, generateTransformer(
                        data, overrideId, placeholders, replacements
                ));
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
     * @return the processed string
     */
    @SafeVarargs
    public final VariableReplacementTransformer generateTransformer(final String input, final String overrideId, final Map<String, Supplier<Value>> placeholders, final Pair<String, Supplier<String>>... replacements) {
        final VariableReplacementTransformer transformer = new VariableReplacementTransformer();
        String data = StringUtils.getOrDefault(input);

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
        if (replacements != null) {
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
        }
        return transformer;
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
        Parser.Result result = null;
        try {
            result = Parser.parse(data);
        } catch (Throwable ignored) {
        }

        final String originalPrefix = ModUtils.TRANSLATOR.translate("gui.config.message.editor.original");
        final String messagePrefix = ModUtils.TRANSLATOR.translate("gui.config.message.editor.message");
        final String verbosePrefix = ModUtils.TRANSLATOR.translate("craftpresence.logger.error.verbose");
        if (result == null || result.hasErrors()) {
            if (result != null) {
                ModUtils.LOG.error(ModUtils.TRANSLATOR.translate("craftpresence.logger.error.parser"));
                ModUtils.LOG.error("%1$s \"%2$s\"", originalPrefix, data);
                ModUtils.LOG.error(messagePrefix);
                for (Error error : result.errors) {
                    if (output != null) {
                        try {
                            output.append(error.toString()).append('\n');
                        } catch (Exception ignored) {
                        }
                    }
                    ModUtils.LOG.error("\t" + error.toString());
                }
            }
            return Value::null_;
        }

        for (Expr.Visitor transformer : transforms) {
            result.accept(transformer);
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
                ModUtils.LOG.error(ModUtils.TRANSLATOR.translate("craftpresence.logger.error.compiler"));
                ModUtils.LOG.error("%1$s \"%2$s\"", originalPrefix, data);
                final List<String> splitEx = StringUtils.splitTextByNewLine(ExceptionUtils.getStackTrace(ex));
                // Dispatch to Appendable WriteStream if possible
                if (output != null) {
                    try {
                        if (CommandUtils.isVerboseMode()) {
                            for (String line : splitEx) {
                                line = line.replace("\t", "    ");
                                output.append(line).append('\n');
                            }
                        } else {
                            output.append(splitEx.get(0)).append('\n');
                            if (splitEx.size() > 1) {
                                output.append('\n').append(verbosePrefix).append('\n');
                            }
                        }
                    } catch (Exception ignored) {
                    }
                }
                // Perform the same to Logging, so the same information is available on both ends
                if (CommandUtils.isVerboseMode()) {
                    ModUtils.LOG.error(messagePrefix);
                    ex.printStackTrace();
                } else {
                    ModUtils.LOG.error("%1$s \"%2$s\"", messagePrefix, splitEx.get(0));
                    if (splitEx.size() > 1) {
                        ModUtils.LOG.error(verbosePrefix);
                    }
                }
                return Value.null_();
            }
            return !StringUtils.isNullOrEmpty(sect.toString()) ? Value.string(sect.toString()) : Value.null_();
        };
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
     * @param args The Specified Arguments to Synchronize for
     */
    public void syncTimestamp(final String... args) {
        final long newTimestamp = System.currentTimeMillis() / 1000L;
        for (String argumentName : args) {
            syncArgument(argumentName, newTimestamp);
        }
    }

    /**
     * Sync {@link ModuleData} overrides for the specified placeholder(s)
     *
     * @param data The data to attach to the specified argument
     * @param args The Specified Arguments to Synchronize for
     */
    public void syncOverride(final ModuleData data, final String... args) {
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
     * Remove {@link ModuleData} overrides for the specified placeholder
     *
     * @param args The Specified Argument(s) to interpret
     */
    public void clearOverride(final String... args) {
        for (String argumentName : args) {
            if (!StringUtils.isNullOrEmpty(argumentName) && overrideData.containsKey(argumentName)) {
                final ModuleData oldData = overrideData.get(argumentName);
                overrideData.remove(argumentName);
                removeArguments("overrides." + argumentName);

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
     * @param data         The data to attach to the Specified Argument
     */
    public void syncArgument(final String argumentName, final Supplier<Value> data) {
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
    public void syncArgument(final String argumentName, final Object data, final boolean plain) {
        syncArgument(argumentName, () -> toValue(data, plain));
    }

    /**
     * Synchronizes the Specified Argument as an RPC Message or an Icon Placeholder
     *
     * @param argumentName The Specified Argument to Synchronize for
     * @param data         The data to attach to the Specified Argument
     */
    public void syncArgument(final String argumentName, final Object data) {
        syncArgument(argumentName, data, false);
    }

    /**
     * Initialize the Specified Arguments as Empty Data
     *
     * @param args The Arguments to Initialize
     */
    public void initArgument(final String... args) {
        // Initialize Specified Arguments to Empty Data
        for (String argumentName : args) {
            syncArgument(argumentName, Value::null_);
        }
    }

    /**
     * Remove any arguments following the specified formats within the selected Argument Type
     *
     * @param args The string formats to interpret
     */
    public void removeArguments(final String... args) {
        synchronized (placeholderData) {
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
            final Map<String, Supplier<Value>> items = Maps.newTreeMap(placeholderData);
            final Map<String, Supplier<Value>> list = Maps.newTreeMap();

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
        final List<String> result = Lists.newArrayList();
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
                ModUtils.TRANSLATOR.translate(
                        String.format("%s.placeholders.notes", ModUtils.MOD_ID)
                )
        );
        resultString.append("\\n\\n").append(
                ModUtils.TRANSLATOR.translate(
                        String.format("%s.placeholders.title", ModUtils.MOD_ID)
                )
        );
        if (!formats.isEmpty()) {
            resultString.append(" (").append(String.join(",", formats)).append(")");
        }
        resultString.append(":");

        final StringBuilder placeholderString = new StringBuilder();
        if (args != null && !args.isEmpty()) {
            for (Map.Entry<String, Supplier<Value>> argData : args.entrySet()) {
                final String placeholderName = argData.getKey();
                final String placeholderTranslation = String.format("%s.placeholders.%s.description",
                        ModUtils.MOD_ID,
                        placeholderName
                );
                final String placeholderUsage = String.format("%s.placeholders.%s.usage",
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

                if (ModUtils.TRANSLATOR.hasTranslation(placeholderUsage)) {
                    placeholderString.append(String.format("\\n ==> %s \"%s\"",
                            ModUtils.TRANSLATOR.translate("gui.config.message.editor.usage"),
                            ModUtils.TRANSLATOR.translate(placeholderUsage)
                    ));
                }

                if (addExtraData) {
                    final Supplier<Value> suppliedInfo = argData.getValue();

                    if (suppliedInfo != null) {
                        final Value rawValue = suppliedInfo.get();
                        final String tagValue = rawValue.toString();
                        if (!rawValue.isNull() && !rawValue.isFunction() && !StringUtils.isNullOrEmpty(tagValue)) {
                            placeholderString.append(String.format("\\n ==> %s \"%s\"",
                                    ModUtils.TRANSLATOR.translate("gui.config.message.editor.preview"),
                                    (tagValue.length() >= 128) ? StringUtils.TOO_LARGE : tagValue
                            ));
                        }
                    }
                }
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
            syncArgument("pack.icon", imageOf(
                    "pack.icon", true,
                    !StringUtils.isNullOrEmpty(foundPackIcon) ? StringUtils.formatAsIcon(foundPackIcon) : ""
            ));
        } else {
            removeArguments("pack");
        }
    }

    /**
     * Synchronizes and Updates Dynamic Placeholder data in this module
     */
    public void syncPlaceholders() {
        // Sync Internal Values
        syncArgument("_general.instance", CraftPresence.instance);
        syncArgument("_general.player", CraftPresence.player);
        syncArgument("_general.world", CraftPresence.player != null ? CraftPresence.player.world : null);
        syncArgument("_config.instance", CraftPresence.CONFIG);
        // Sync Custom Variables
        removeArguments("custom.");
        for (Map.Entry<String, String> entry : CraftPresence.CONFIG.displaySettings.dynamicVariables.entrySet()) {
            if (!entry.getKey().equals("default")) {
                syncArgument("custom." + entry.getKey(), entry.getValue());
            }
        }
        // Add Any Generalized Argument Data needed
        final String playerName = CraftPresence.session.getUsername();
        syncArgument("general.mods", FileUtils.getModCount());
        syncArgument("player.name", playerName);

        // UUID Data
        final String uniqueId = CraftPresence.session.getPlayerID();
        if (StringUtils.isValidUuid(uniqueId)) {
            syncArgument("player.uuid.short", StringUtils.getFromUuid(uniqueId, true));
            syncArgument("player.uuid.full", StringUtils.getFromUuid(uniqueId, false));
        }

        if (CraftPresence.CONFIG.advancedSettings.allowEndpointIcons &&
                !StringUtils.isNullOrEmpty(CraftPresence.CONFIG.advancedSettings.playerSkinEndpoint)) {
            if (!CraftPresence.CONFIG.displaySettings.dynamicIcons.containsKey(playerName)) {
                CraftPresence.CONFIG.displaySettings.dynamicIcons.put(playerName,
                        compileData(String.format(
                                CraftPresence.CONFIG.advancedSettings.playerSkinEndpoint,
                                StringUtils.getOrDefault(uniqueId, playerName)
                        )).get().toString()
                );
                DiscordAssetUtils.syncCustomAssets();
                CraftPresence.CONFIG.save();
            }

            syncArgument("player.icon", playerName);
        }

        syncArgument("general.version", ModUtils.MCVersion);
        syncArgument("general.brand", ModUtils.BRAND);

        // Sync any advanced arguments
        syncArgument("data.general.time", currentStartTime);

        // Sync the Default Icon Argument
        syncArgument("general.icon", CraftPresence.CONFIG.generalSettings.defaultIcon);
        syncPackArguments();
        syncScriptArguments();
    }

    /**
     * Synchronizes and Updates Placeholder data from the script engine in this module
     */
    public void syncScriptArguments() {
        synchronized (placeholderData) {
            final ValueMap map = scriptEngine.getGlobals();
            for (String name : map.keys()) {
                placeholderData.put(name, map.get(name));
            }
        }
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
                            ModUtils.LOG.info(ModUtils.TRANSLATOR.translate(true, "craftpresence.logger.info.discord.assets.fallback", primaryKey, finalKey));
                        }
                        break;
                    } else {
                        i++;
                        if (i < evalStrings.length) {
                            if (showLogging) {
                                ModUtils.LOG.error(ModUtils.TRANSLATOR.translate(true, "craftpresence.logger.error.discord.assets.fallback", currentString, evalStrings[i]));
                                if (isPrimaryEntry) {
                                    ModUtils.LOG.info(ModUtils.TRANSLATOR.translate(true, "craftpresence.logger.info.discord.assets.request", currentString));
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
                    if (CommandUtils.isVerboseMode() && !result.equals(primaryKey)) {
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
     * @param clearRequestData Whether to clear Ask to Join / Spectate Request Data
     * @param updateRPC        Whether to immediately update the RPC following changes
     */
    public void clearPartyData(final boolean clearRequestData, final boolean updateRPC) {
        if (clearRequestData) {
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
    public void clearPresenceData(final Tuple<Boolean, Boolean, Boolean> partyClearArgs) {
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
                if (CommandUtils.isVerboseMode()) {
                    ex.printStackTrace();
                }
            }

            // Clear User Data before final clear and shutdown
            STATUS = DiscordStatus.Disconnected;
            currentPresence = null;
            // Empty RPC Data
            clearPresenceData(new Tuple<>(true, true, false));

            CURRENT_USER = null;
            lastRequestedImageData = new Pair<>();
            cachedImageData.clear();

            CommandUtils.clearModuleData();

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
        for (Map.Entry<String, Button> buttonElement : configData.buttons.entrySet()) {
            final JsonObject buttonObj = new JsonObject();
            final String overrideId = "button_" + (BUTTONS.size() + 1);
            if (!StringUtils.isNullOrEmpty(buttonElement.getKey()) &&
                    !buttonElement.getKey().equalsIgnoreCase("default") &&
                    !StringUtils.isNullOrEmpty(buttonElement.getValue().label)) {
                String label = StringUtils.formatWord(
                        getResult(buttonElement.getValue().label, overrideId + ".label"),
                        !CraftPresence.CONFIG.advancedSettings.formatWords, true, 1
                );
                String url = !StringUtils.isNullOrEmpty(buttonElement.getValue().url) ? getResult(
                        buttonElement.getValue().url, overrideId + ".url"
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

        final RichPresence newRPCData = new RichPresence.Builder()
                .setState(GAME_STATE = sanitizePlaceholders(GAME_STATE, 128))
                .setDetails(DETAILS = sanitizePlaceholders(DETAILS, 128))
                .setStartTimestamp(START_TIMESTAMP)
                .setEndTimestamp(END_TIMESTAMP)
                .setLargeImage(LARGE_IMAGE_KEY = sanitizePlaceholders(LARGE_IMAGE_KEY, 256),
                        LARGE_IMAGE_TEXT = sanitizePlaceholders(LARGE_IMAGE_TEXT, 128))
                .setSmallImage(SMALL_IMAGE_KEY = sanitizePlaceholders(SMALL_IMAGE_KEY, 256),
                        SMALL_IMAGE_TEXT = sanitizePlaceholders(SMALL_IMAGE_TEXT, 128))
                .setParty(PARTY_ID, PARTY_SIZE, PARTY_MAX, PARTY_PRIVACY.ordinal())
                .setMatchSecret(MATCH_SECRET)
                .setJoinSecret(JOIN_SECRET)
                .setSpectateSecret(SPECTATE_SECRET)
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
        return forcedData != null ? forcedData : CraftPresence.CONFIG.displaySettings.presenceData;
    }

    /**
     * Perform any needed Tick events, tied to {@link SystemUtils#MINIMUM_REFRESH_RATE} ticks
     */
    public void onTick() {
        CommandUtils.syncModuleArguments();
        syncPlaceholders();

        // Menu Tick Event
        final boolean isMenuActive = (CommandUtils.isLoadingGame || CommandUtils.isInMainMenu);
        final boolean isFullyLoaded = CraftPresence.SYSTEM.HAS_LOADED && CraftPresence.SYSTEM.HAS_GAME_LOADED;
        if (!isFullyLoaded && !isMenuActive) {
            // Ensure Loading Presence has already passed, before any other type of presence displays
            CommandUtils.setLoadingPresence();
        } else if (CraftPresence.player == null && !CommandUtils.isInMainMenu) {
            CommandUtils.setMainMenuPresence();
        } else if (CraftPresence.player != null && isMenuActive) {
            CommandUtils.clearInitialPresence();
        }
        // Join Request Tick Event
        if (!CraftPresence.CONFIG.hasChanged && isFullyLoaded) {
            // Processing for Join Request Systems
            if (awaitingReply && CraftPresence.SYSTEM.TIMER == 0) {
                StringUtils.sendMessageToPlayer(CraftPresence.player, ModUtils.TRANSLATOR.translate("craftpresence.command.request.ignored", REQUESTER_USER.getName()));
                ipcInstance.respondToJoinRequest(REQUESTER_USER, IPCClient.ApprovalMode.DENY);
                awaitingReply = false;
                STATUS = DiscordStatus.Ready;
            } else if (!awaitingReply && REQUESTER_USER != null) {
                REQUESTER_USER = null;
                STATUS = DiscordStatus.Ready;
            }
        }

        updatePresence();
    }
}

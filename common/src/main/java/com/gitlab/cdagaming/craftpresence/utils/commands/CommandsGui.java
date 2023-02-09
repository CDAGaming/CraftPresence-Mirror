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

package com.gitlab.cdagaming.craftpresence.utils.commands;

import com.gitlab.cdagaming.craftpresence.CraftPresence;
import com.gitlab.cdagaming.craftpresence.ModUtils;
import com.gitlab.cdagaming.craftpresence.impl.discord.DiscordStatus;
import com.gitlab.cdagaming.craftpresence.utils.CommandUtils;
import com.gitlab.cdagaming.craftpresence.utils.FileUtils;
import com.gitlab.cdagaming.craftpresence.utils.StringUtils;
import com.gitlab.cdagaming.craftpresence.utils.discord.assets.DiscordAsset;
import com.gitlab.cdagaming.craftpresence.utils.discord.assets.DiscordAssetUtils;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ExtendedButtonControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ExtendedTextControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ScrollableListControl.RenderType;
import com.gitlab.cdagaming.craftpresence.utils.gui.impl.SelectorGui;
import com.gitlab.cdagaming.craftpresence.utils.gui.integrations.ExtendedScreen;
import com.google.common.collect.Lists;
import com.jagrosh.discordipc.IPCClient;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;
import org.meteordev.starscript.value.Value;

import java.io.BufferedWriter;
import java.io.File;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandsGui extends ExtendedScreen {
    private static String[] executionCommandArgs;
    public ExtendedButtonControl proceedButton;
    private ExtendedTextControl commandInput;
    private String executionString;
    private boolean blockInteractions = false;
    private String[] commandArgs, filteredCommandArgs;
    private List<String> tabCompletions = Lists.newArrayList();

    public CommandsGui(GuiScreen parentScreen) {
        super(parentScreen);
        executionString = ModUtils.TRANSLATOR.translate("craftpresence.command.usage.main");
    }

    /**
     * Synchronizes the Command Arguments into a List for further use
     *
     * @param args The Command Arguments to parse
     */
    public static void executeCommand(String... args) {
        executionCommandArgs = args;
    }

    /**
     * Retrieves a List of potential matches for an inputted string
     *
     * @param inputArgs           The inputted String
     * @param possibleCompletions The Potential Tab-Completions
     * @return A List of potential matches from inputted string
     */
    private static List<String> getListOfStringsMatchingLastWord(String[] inputArgs, Collection<?> possibleCompletions) {
        String s = inputArgs[inputArgs.length - 1];
        List<String> list = Lists.newArrayList();

        if (!possibleCompletions.isEmpty()) {
            for (Object object : possibleCompletions) {
                if (object != null) {
                    String str = String.valueOf(object);
                    if (doesStringStartWith(s, str)) {
                        list.add(str);
                    }
                }
            }
        }

        return list;
    }

    /**
     * Checks if the specified region matches the start of the string
     *
     * @param original The Original String to check against
     * @param region   The region to check for a match
     * @return {@code Boolean#TRUE} if the region matches the start of the string
     */
    private static boolean doesStringStartWith(String original, String region) {
        return region.regionMatches(true, 0, original, 0, original.length());
    }

    @Override
    public void initializeUi() {
        commandInput = addControl(
                new ExtendedTextControl(
                        getFontRenderer(),
                        115, (getScreenHeight() - 30),
                        (getScreenWidth() - 120), 20
                )
        );
        commandInput.setControlMaxLength(512);

        proceedButton = addControl(
                new ExtendedButtonControl(
                        10, (getScreenHeight() - 30),
                        100, 20,
                        "gui.config.message.button.back",
                        () -> CraftPresence.GUIS.openScreen(parentScreen)
                )
        );

        super.initializeUi();
    }

    @Override
    public void preRender() {
        final String mainTitle = ModUtils.TRANSLATOR.translate("gui.config.title.commands");

        renderString(mainTitle, (getScreenWidth() / 2f) - (getStringWidth(mainTitle) / 2f), 10, 0xFFFFFF);

        proceedButton.setControlEnabled(!blockInteractions);
        commandInput.setEnabled(!blockInteractions);

        if (!blockInteractions) {
            checkCommands();
        }
        CraftPresence.GUIS.drawMultiLineString(StringUtils.splitTextByNewLine(executionString), 25, 35, this, false);
    }

    /**
     * Executes Tab-Completion and Primary Command Logic
     */
    private void checkCommands() {
        if (!StringUtils.isNullOrEmpty(commandInput.getControlMessage()) && commandInput.getControlMessage().startsWith("/")) {
            commandArgs = commandInput.getControlMessage()
                    .replace("/", "")
                    .split(" ");
            filteredCommandArgs = commandInput.getControlMessage()
                    .replace("/", "")
                    .replace("cp", "")
                    .replace(ModUtils.MOD_ID, "")
                    .trim().split(" ");
            tabCompletions = getTabCompletions(filteredCommandArgs);
        }

        if (executionCommandArgs != null) {
            if (executionCommandArgs.length == 0 ||
                    (executionCommandArgs[0].equalsIgnoreCase("help") ||
                            executionCommandArgs[0].equalsIgnoreCase("?") ||
                            executionCommandArgs[0].equalsIgnoreCase("")
                    )
            ) {
                executionString = ModUtils.TRANSLATOR.translate("craftpresence.command.usage.main");
            } else if (!StringUtils.isNullOrEmpty(executionCommandArgs[0])) {
                if (executionCommandArgs[0].equalsIgnoreCase("request")) {
                    if (executionCommandArgs.length == 1) {
                        if (CraftPresence.CLIENT.STATUS == DiscordStatus.JoinRequest && CraftPresence.CLIENT.REQUESTER_USER != null) {
                            if (CraftPresence.CONFIG.generalSettings.enableJoinRequests) {
                                executionString = ModUtils.TRANSLATOR.translate("craftpresence.command.request.info",
                                        CraftPresence.CLIENT.REQUESTER_USER.getName(), CraftPresence.SYSTEM.TIMER
                                );
                                CraftPresence.CLIENT.awaitingReply = true;
                            } else {
                                CraftPresence.CLIENT.ipcInstance.respondToJoinRequest(CraftPresence.CLIENT.REQUESTER_USER, IPCClient.ApprovalMode.DENY);
                                CraftPresence.CLIENT.STATUS = DiscordStatus.Ready;
                                CraftPresence.SYSTEM.TIMER = 0;
                                CraftPresence.CLIENT.awaitingReply = false;
                            }
                        } else {
                            executionString = ModUtils.TRANSLATOR.translate("craftpresence.command.request.none");
                            CraftPresence.CLIENT.awaitingReply = false;
                        }
                    } else if (!StringUtils.isNullOrEmpty(executionCommandArgs[1])) {
                        if (CraftPresence.CLIENT.awaitingReply && CraftPresence.CONFIG.generalSettings.enableJoinRequests) {
                            if (executionCommandArgs[1].equalsIgnoreCase("accept")) {
                                executionString = ModUtils.TRANSLATOR.translate("craftpresence.command.request.accept",
                                        CraftPresence.CLIENT.REQUESTER_USER.getName()
                                );
                                CraftPresence.CLIENT.ipcInstance.respondToJoinRequest(CraftPresence.CLIENT.REQUESTER_USER, IPCClient.ApprovalMode.ACCEPT);
                                CraftPresence.CLIENT.STATUS = DiscordStatus.Ready;
                                CraftPresence.SYSTEM.TIMER = 0;
                                CraftPresence.CLIENT.awaitingReply = false;
                            } else if (executionCommandArgs[1].equalsIgnoreCase("deny")) {
                                executionString = ModUtils.TRANSLATOR.translate("craftpresence.command.request.denied",
                                        CraftPresence.CLIENT.REQUESTER_USER.getName()
                                );
                                CraftPresence.CLIENT.ipcInstance.respondToJoinRequest(CraftPresence.CLIENT.REQUESTER_USER, IPCClient.ApprovalMode.DENY);
                                CraftPresence.CLIENT.STATUS = DiscordStatus.Ready;
                                CraftPresence.SYSTEM.TIMER = 0;
                                CraftPresence.CLIENT.awaitingReply = false;
                            } else {
                                executionString = ModUtils.TRANSLATOR.translate("craftpresence.command.unrecognized");
                            }
                        } else {
                            executionString = ModUtils.TRANSLATOR.translate("craftpresence.command.request.none");
                        }
                    }
                } else if (executionCommandArgs[0].equalsIgnoreCase("export")) {
                    String clientId = CraftPresence.CONFIG.generalSettings.clientId;
                    boolean doFullCopy = false;
                    String urlMeta = "";

                    if (executionCommandArgs.length == 1) {
                        executionString = ModUtils.TRANSLATOR.translate("craftpresence.command.usage.export", clientId, false);
                    } else if (!StringUtils.isNullOrEmpty(executionCommandArgs[1])) {
                        if (executionCommandArgs[1].equalsIgnoreCase("assets")) {
                            if (executionCommandArgs.length >= 3 && executionCommandArgs.length <= 5) {
                                for (int i = 2; i < executionCommandArgs.length; i++) {
                                    if (StringUtils.isValidBoolean(executionCommandArgs[i])) {
                                        doFullCopy = Boolean.parseBoolean(executionCommandArgs[i]);
                                    } else if (DiscordAssetUtils.isValidId(executionCommandArgs[i])) {
                                        clientId = executionCommandArgs[i];
                                    } else {
                                        final Matcher matcher = Pattern.compile("\"(.*?)\"").matcher(commandInput.getControlMessage());
                                        if (matcher.find()) {
                                            urlMeta = matcher.group(1);
                                        }
                                    }
                                }
                            } else {
                                executionString = ModUtils.TRANSLATOR.translate("craftpresence.command.unrecognized");
                            }

                            exportAssets(clientId, doFullCopy, urlMeta);
                        } else {
                            executionString = ModUtils.TRANSLATOR.translate("craftpresence.command.unrecognized");
                        }
                    }
                } else if (executionCommandArgs[0].equalsIgnoreCase("compile")) {
                    if (executionCommandArgs.length == 1) {
                        executionString = ModUtils.TRANSLATOR.translate("craftpresence.command.usage.compile");
                    } else {
                        final Matcher matcher = Pattern.compile("\"(.*?)\"").matcher(commandInput.getControlMessage());
                        if (matcher.find()) {
                            final String contents = matcher.group(1);
                            final StringBuilder out = new StringBuilder();
                            final Supplier<Value> data = CraftPresence.CLIENT.getCompileResult(contents, out);

                            final String value = data.get().toString();
                            final int length = StringUtils.getBytes(value, "UTF-8").length;

                            executionString = ModUtils.TRANSLATOR.translate("craftpresence.command.compile",
                                    value, length, out.toString().replace("\n", "\\n")
                            );
                        } else {
                            executionString = ModUtils.TRANSLATOR.translate("craftpresence.command.unrecognized");
                        }
                    }
                } else if (executionCommandArgs[0].equalsIgnoreCase("search")) {
                    if (executionCommandArgs.length == 1) {
                        executionString = ModUtils.TRANSLATOR.translate("craftpresence.command.usage.search");
                    } else if (!StringUtils.isNullOrEmpty(executionCommandArgs[1])) {
                        final List<String> results = Lists.newArrayList(
                                CraftPresence.CLIENT.getArgumentEntries(executionCommandArgs[1])
                        );

                        if (!results.isEmpty()) {
                            CraftPresence.GUIS.openScreen(new SelectorGui(
                                    currentScreen,
                                    ModUtils.TRANSLATOR.translate("gui.config.title.selector.view.items"),
                                    results,
                                    null, null,
                                    false, false, RenderType.Placeholder.setCanRenderImage(false),
                                    null, null
                            ));
                        } else {
                            executionString = ModUtils.TRANSLATOR.translate("gui.config.message.empty.list");
                        }
                    }
                } else if (executionCommandArgs[0].equalsIgnoreCase("reload")) {
                    executionString = ModUtils.TRANSLATOR.translate("craftpresence.command.reload");
                    CommandUtils.reloadData(true);
                    executionString = ModUtils.TRANSLATOR.translate("craftpresence.command.reload.complete");
                } else if (executionCommandArgs[0].equalsIgnoreCase("shutdown")) {
                    executionString = ModUtils.TRANSLATOR.translate("craftpresence.command.shutdown.pre");
                    CraftPresence.CLIENT.shutDown();
                    executionString = ModUtils.TRANSLATOR.translate("craftpresence.command.shutdown.post");
                } else if (executionCommandArgs[0].equalsIgnoreCase("reboot")) {
                    executionString = ModUtils.TRANSLATOR.translate("craftpresence.command.reboot.pre");
                    CommandUtils.rebootRPC(true);
                    executionString = ModUtils.TRANSLATOR.translate("craftpresence.command.reboot.post");
                } else if (executionCommandArgs[0].equalsIgnoreCase("view")) {
                    if (executionCommandArgs.length == 1) {
                        executionString = ModUtils.TRANSLATOR.translate("craftpresence.command.usage.view");
                    } else if (!StringUtils.isNullOrEmpty(executionCommandArgs[1])) {
                        if (executionCommandArgs[1].equalsIgnoreCase("placeholders")) {
                            // Redirect: `/cp view placeholders` => `/cp search all`
                            executeCommand("search", "all");
                            return;
                        } else if (executionCommandArgs[1].equalsIgnoreCase("items")) {
                            if (CraftPresence.TILE_ENTITIES.enabled) {
                                CraftPresence.GUIS.openScreen(new SelectorGui(
                                        currentScreen,
                                        ModUtils.TRANSLATOR.translate("gui.config.title.selector.view.items"),
                                        CraftPresence.TILE_ENTITIES.TILE_ENTITY_NAMES,
                                        null, null,
                                        false, false, RenderType.ItemData,
                                        null, null
                                ));
                            } else {
                                executionString = ModUtils.TRANSLATOR.translate("gui.config.message.hover.access",
                                        ModUtils.TRANSLATOR.translate("gui.config.name.advanced.enable_per_item")
                                );
                            }
                        } else if (executionCommandArgs[1].equalsIgnoreCase("entities")) {
                            if (CraftPresence.ENTITIES.enabled) {
                                CraftPresence.GUIS.openScreen(new SelectorGui(
                                        currentScreen,
                                        ModUtils.TRANSLATOR.translate("gui.config.title.selector.view.entities"),
                                        CraftPresence.ENTITIES.ENTITY_NAMES,
                                        null, null,
                                        false, false, RenderType.EntityData,
                                        null, null
                                ));
                            } else {
                                executionString = ModUtils.TRANSLATOR.translate("gui.config.message.hover.access",
                                        ModUtils.TRANSLATOR.translate("gui.config.name.advanced.enable_per_entity")
                                );
                            }
                        } else if (executionCommandArgs[1].equalsIgnoreCase("servers")) {
                            if (CraftPresence.SERVER.enabled) {
                                CraftPresence.GUIS.openScreen(new SelectorGui(
                                        currentScreen,
                                        ModUtils.TRANSLATOR.translate("gui.config.title.selector.view.servers"),
                                        CraftPresence.SERVER.knownAddresses,
                                        null, null,
                                        false, false, RenderType.ServerData,
                                        null, null
                                ));
                            } else {
                                executionString = ModUtils.TRANSLATOR.translate("gui.config.message.hover.access",
                                        ModUtils.TRANSLATOR.translate("gui.config.name.general.detect_world_data")
                                );
                            }
                        } else if (executionCommandArgs[1].equalsIgnoreCase("screens")) {
                            if (CraftPresence.GUIS.enabled) {
                                CraftPresence.GUIS.openScreen(new SelectorGui(
                                        currentScreen,
                                        ModUtils.TRANSLATOR.translate("gui.config.title.selector.view.guis"),
                                        CraftPresence.GUIS.GUI_NAMES,
                                        null, null,
                                        false, false, null,
                                        null, null
                                ));
                            } else {
                                executionString = ModUtils.TRANSLATOR.translate("gui.config.message.hover.access",
                                        ModUtils.TRANSLATOR.translate("gui.config.name.advanced.enable_per_gui")
                                );
                            }
                        } else if (executionCommandArgs[1].equalsIgnoreCase("biomes")) {
                            if (CraftPresence.BIOMES.enabled) {
                                CraftPresence.GUIS.openScreen(new SelectorGui(
                                        currentScreen,
                                        ModUtils.TRANSLATOR.translate("gui.config.title.selector.view.biomes"),
                                        CraftPresence.BIOMES.BIOME_NAMES,
                                        null, null,
                                        false, false, null,
                                        null, null
                                ));
                            } else {
                                executionString = ModUtils.TRANSLATOR.translate("gui.config.message.hover.access",
                                        ModUtils.TRANSLATOR.translate("gui.config.name.general.detect_biome_data")
                                );
                            }
                        } else if (executionCommandArgs[1].equalsIgnoreCase("dimensions")) {
                            if (CraftPresence.DIMENSIONS.enabled) {
                                CraftPresence.GUIS.openScreen(new SelectorGui(
                                        currentScreen,
                                        ModUtils.TRANSLATOR.translate("gui.config.title.selector.view.dimensions"),
                                        CraftPresence.DIMENSIONS.DIMENSION_NAMES,
                                        null, null,
                                        false, false, null,
                                        null, null
                                ));
                            } else {
                                executionString = ModUtils.TRANSLATOR.translate("gui.config.message.hover.access",
                                        ModUtils.TRANSLATOR.translate("gui.config.name.general.detect_dimension_data")
                                );
                            }
                        } else if (executionCommandArgs[1].equalsIgnoreCase("currentData")) {
                            executionString = ModUtils.TRANSLATOR.translate("craftpresence.command.current_data",
                                    CraftPresence.CLIENT.CURRENT_USER.getName(),
                                    StringUtils.convertString(CraftPresence.CLIENT.DETAILS, "UTF-8", true),
                                    StringUtils.convertString(CraftPresence.CLIENT.GAME_STATE, "UTF-8", true),
                                    CraftPresence.CLIENT.START_TIMESTAMP,
                                    CraftPresence.CLIENT.CLIENT_ID,
                                    StringUtils.convertString(CraftPresence.CLIENT.LARGE_IMAGE_KEY, "UTF-8", true),
                                    StringUtils.convertString(CraftPresence.CLIENT.LARGE_IMAGE_TEXT, "UTF-8", true),
                                    StringUtils.convertString(CraftPresence.CLIENT.SMALL_IMAGE_KEY, "UTF-8", true),
                                    StringUtils.convertString(CraftPresence.CLIENT.SMALL_IMAGE_TEXT, "UTF-8", true),
                                    CraftPresence.CLIENT.PARTY_ID,
                                    CraftPresence.CLIENT.PARTY_SIZE,
                                    CraftPresence.CLIENT.PARTY_MAX,
                                    CraftPresence.CLIENT.PARTY_PRIVACY.name(),
                                    CraftPresence.CLIENT.JOIN_SECRET,
                                    CraftPresence.CLIENT.END_TIMESTAMP,
                                    CraftPresence.CLIENT.MATCH_SECRET, CraftPresence.CLIENT.SPECTATE_SECRET,
                                    CraftPresence.CLIENT.BUTTONS.toString(),
                                    CraftPresence.CLIENT.INSTANCE
                            );
                        } else if (executionCommandArgs[1].equalsIgnoreCase("assets")) {
                            if (executionCommandArgs.length == 2) {
                                executionString = ModUtils.TRANSLATOR.translate("craftpresence.command.usage.view.assets");
                            } else if (!StringUtils.isNullOrEmpty(executionCommandArgs[2])) {
                                if (executionCommandArgs[2].equalsIgnoreCase("custom")) {
                                    CraftPresence.GUIS.openScreen(new SelectorGui(
                                            currentScreen,
                                            ModUtils.TRANSLATOR.translate("gui.config.title.selector.view.assets.custom"),
                                            DiscordAssetUtils.CUSTOM_ASSET_LIST.keySet(),
                                            null, null,
                                            false, false, RenderType.CustomDiscordAsset,
                                            null, null
                                    ));
                                } else if (executionCommandArgs[2].equalsIgnoreCase("all")) {
                                    CraftPresence.GUIS.openScreen(new SelectorGui(
                                            currentScreen,
                                            ModUtils.TRANSLATOR.translate("gui.config.title.selector.view.assets.all"),
                                            DiscordAssetUtils.ASSET_LIST.keySet(),
                                            null, null,
                                            false, false, RenderType.DiscordAsset,
                                            null, null
                                    ));
                                }
                            }
                        } else {
                            executionString = ModUtils.TRANSLATOR.translate("craftpresence.command.unrecognized");
                        }
                    }
                } else {
                    executionString = ModUtils.TRANSLATOR.translate("craftpresence.command.unrecognized");
                }
            } else {
                executionString = ModUtils.TRANSLATOR.translate("craftpresence.logger.error.command");
            }
        }

        executionCommandArgs = null;
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (commandInput.isControlFocused() && commandInput.getControlMessage().startsWith("/") && commandArgs != null && commandArgs.length > 0 &&
                (commandArgs[0].equalsIgnoreCase("cp") || commandArgs[0].equalsIgnoreCase(ModUtils.MOD_ID))) {
            if (keyCode == Keyboard.KEY_TAB && !tabCompletions.isEmpty()) {
                if (commandArgs.length > 1 && (filteredCommandArgs[filteredCommandArgs.length - 1].length() > 1 ||
                        filteredCommandArgs[filteredCommandArgs.length - 1].equalsIgnoreCase("?")
                )) {
                    commandInput.setControlMessage(
                            commandInput.getControlMessage().replace(
                                    filteredCommandArgs[filteredCommandArgs.length - 1], tabCompletions.get(0)
                            )
                    );
                }
            } else if (keyCode == Keyboard.KEY_RETURN || keyCode == Keyboard.KEY_NUMPADENTER) {
                executeCommand(filteredCommandArgs);
            }
        }
        if (!blockInteractions) {
            super.keyTyped(typedChar, keyCode);
        }
    }

    /**
     * Retrieves a List of Tab Completions for the Specified Arguments
     *
     * @param args The Command Arguments to parse
     * @return The Possible Tab Completions from the specified arguments
     */
    private List<String> getTabCompletions(final String[] args) {
        final List<String> completions = Lists.newArrayList();

        if (args.length == 1) {
            completions.add("?");
            completions.add("help");
            completions.add("compile");
            completions.add("search");
            completions.add("reload");
            completions.add("request");
            completions.add("export");
            completions.add("view");
            completions.add("reboot");
            completions.add("shutdown");
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("export")) {
                completions.add("assets");
            } else if (args[0].equalsIgnoreCase("search")) {
                completions.add("type:");
                completions.add("all");
            } else if (args[0].equalsIgnoreCase("view")) {
                completions.add("placeholders");
                completions.add("currentData");
                completions.add("assets");
                completions.add("dimensions");
                completions.add("biomes");
                completions.add("items");
                completions.add("entities");
                completions.add("servers");
                completions.add("screens");
            } else if (args[0].equalsIgnoreCase("request")) {
                completions.add("accept");
                completions.add("deny");
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("view") && args[1].equalsIgnoreCase("assets")) {
                completions.add("all");
                completions.add("custom");
            }
        }
        return getListOfStringsMatchingLastWord(args, completions);
    }

    /**
     * Export the Assets belonging to another client id
     *
     * @param clientId   The client ID to export from
     * @param doFullCopy Whether to do a full copy or a text-only copy
     */
    private void exportAssets(final String clientId, final boolean doFullCopy, final String urlMeta) {
        new Thread(() -> {
            blockInteractions = true;
            final DiscordAsset[] assetList = DiscordAssetUtils.loadAssets(clientId, false);

            OutputStream outputData = null;
            OutputStreamWriter outputStream = null;
            BufferedWriter bw = null;
            boolean hasError = false;

            if (assetList != null) {
                final String filePath = ModUtils.MOD_ID + File.separator + "export" + File.separator + clientId + File.separator;
                executionString = ModUtils.TRANSLATOR.translate("craftpresence.command.export.pre", assetList.length, clientId, doFullCopy);

                if (!doFullCopy) {
                    try {
                        // Create Data Directory if non-existent
                        final File dataDir = new File(filePath + "downloads.txt");
                        if (!dataDir.getParentFile().exists() && !dataDir.getParentFile().mkdirs()) {
                            hasError = true;
                        }
                        // Create and write initial data, using the encoding of our current ipc instance (UTF-8 by default)
                        outputData = Files.newOutputStream(dataDir.toPath());
                        outputStream = new OutputStreamWriter(outputData, CraftPresence.CLIENT.ipcInstance.getEncoding());
                        bw = new BufferedWriter(outputStream);

                        bw.write("## Export Data => " + clientId);
                        bw.newLine();
                        bw.newLine();
                    } catch (Exception ex) {
                        if (ModUtils.IS_VERBOSE) {
                            ex.printStackTrace();
                        }
                        hasError = true;
                    }
                }

                for (int i = 0; i < assetList.length; i++) {
                    executionString = ModUtils.TRANSLATOR.translate("craftpresence.command.export.progress",
                            clientId, i + 1, assetList.length
                    );
                    final DiscordAsset asset = assetList[i];
                    final String assetUrl = DiscordAssetUtils.getDiscordAssetUrl(clientId, asset.getId(), false) + urlMeta;
                    final String assetName = asset.getName() + ".png";
                    if (doFullCopy) {
                        FileUtils.downloadFile(assetUrl, new File(filePath + assetName));
                    } else if (!hasError) {
                        try {
                            bw.write("* " + assetName + " => " + assetUrl);
                            bw.newLine();
                        } catch (Exception ex) {
                            if (ModUtils.IS_VERBOSE) {
                                ex.printStackTrace();
                            }
                            hasError = true;
                        }
                    } else {
                        executionString = ModUtils.TRANSLATOR.translate("craftpresence.command.export.exception", clientId);
                    }
                }

                try {
                    if (bw != null) {
                        bw.close();
                    }
                    if (outputStream != null) {
                        outputStream.close();
                    }
                    if (outputData != null) {
                        outputData.close();
                    }
                } catch (Exception ex) {
                    ModUtils.LOG.error(ModUtils.TRANSLATOR.translate("craftpresence.logger.error.data.close"));
                    if (ModUtils.IS_VERBOSE) {
                        ex.printStackTrace();
                    }
                }

                executionString = ModUtils.TRANSLATOR.translate("craftpresence.command.export.post", assetList.length, clientId, doFullCopy);
            } else {
                executionString = ModUtils.TRANSLATOR.translate("craftpresence.command.export.exception", clientId);
            }
            blockInteractions = false;
        }, "CraftPresence-Asset-Exporter").start();
    }
}

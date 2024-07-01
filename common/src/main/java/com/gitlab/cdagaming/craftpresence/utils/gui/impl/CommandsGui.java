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

package com.gitlab.cdagaming.craftpresence.utils.gui.impl;

import com.gitlab.cdagaming.craftpresence.CraftPresence;
import com.gitlab.cdagaming.craftpresence.core.Constants;
import com.gitlab.cdagaming.craftpresence.core.impl.discord.DiscordStatus;
import com.gitlab.cdagaming.craftpresence.core.integrations.discord.assets.DiscordAsset;
import com.gitlab.cdagaming.craftpresence.core.integrations.discord.assets.DiscordAssetUtils;
import com.gitlab.cdagaming.craftpresence.utils.CommandUtils;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.DynamicScrollableList;
import com.gitlab.cdagaming.unilib.utils.gui.controls.ExtendedButtonControl;
import com.gitlab.cdagaming.unilib.utils.gui.controls.ExtendedTextControl;
import com.gitlab.cdagaming.unilib.utils.gui.integrations.ExtendedScreen;
import com.gitlab.cdagaming.unilib.utils.gui.integrations.ScrollPane;
import com.gitlab.cdagaming.unilib.utils.gui.widgets.TextDisplayWidget;
import io.github.cdagaming.unicore.utils.FileUtils;
import io.github.cdagaming.unicore.utils.StringUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandsGui extends ExtendedScreen {
    private static final Pattern SURROUNDED_BY_QUOTES = Pattern.compile("\"(.*?)\"");
    public ExtendedButtonControl proceedButton, copyButton;
    private String[] executionCommandArgs;
    private ExtendedTextControl commandInput;
    private ScrollPane childFrame;
    private TextDisplayWidget previewArea;
    private String executionString, commandString = "";
    private boolean blockInteractions = false;
    private String[] commandArgs, filteredCommandArgs;
    private List<String> tabCompletions = StringUtils.newArrayList();

    public CommandsGui(String... commandArgs) {
        super();
        executionCommandArgs = commandArgs;
        executionString = Constants.TRANSLATOR.translate("craftpresence.command.usage.main");
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
        List<String> list = StringUtils.newArrayList();

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

    /**
     * Synchronizes the Command Arguments into a List for further use
     *
     * @param args The Command Arguments to parse
     */
    public void executeCommand(String... args) {
        executionCommandArgs = args;
    }

    @Override
    public void initializeUi() {
        proceedButton = addControl(
                new ExtendedButtonControl(
                        6, (getScreenHeight() - 26),
                        95, 20,
                        Constants.TRANSLATOR.translate("gui.config.message.button.back"),
                        () -> openScreen(getParent())
                )
        );
        copyButton = addControl(
                new ExtendedButtonControl(
                        6, 6,
                        95, 20,
                        Constants.TRANSLATOR.translate("gui.config.message.button.copy"),
                        () -> copyToClipboard(executionString)
                )
        );

        commandInput = addControl(
                new ExtendedTextControl(
                        getFontRenderer(),
                        proceedButton.getRight() + 4, (getScreenHeight() - 26),
                        (getScreenWidth() - 112), 20,
                        () -> commandString = commandInput.getControlMessage()
                )
        );
        commandInput.setControlMessage(commandString);

        childFrame = addControl(
                new ScrollPane(
                        0, 32,
                        getScreenWidth(), getScreenHeight() - 32
                )
        );
        previewArea = childFrame.addWidget(new TextDisplayWidget(
                0, 0,
                childFrame.getScreenWidth(),
                executionString
        ));
        super.initializeUi();
    }

    @Override
    public void preRender() {
        proceedButton.setControlEnabled(!blockInteractions);
        copyButton.setControlEnabled(!blockInteractions);
        commandInput.setEnabled(!blockInteractions);

        if (!blockInteractions) {
            checkCommands();
        }
        previewArea.setMessage(executionString);

        super.preRender();
    }

    @Override
    public void renderExtra() {
        final String mainTitle = Constants.TRANSLATOR.translate("gui.config.title.commands");

        renderScrollingString(
                mainTitle,
                30, 0,
                getScreenWidth() - 30, 32,
                0xFFFFFF
        );

        super.renderExtra();
    }

    /**
     * Executes Tab-Completion and Primary Command Logic
     */
    private void checkCommands() {
        if (!StringUtils.isNullOrEmpty(commandString) && commandString.startsWith("/")) {
            commandArgs = commandString
                    .replace("/", "")
                    .split(" ");
            filteredCommandArgs = commandString
                    .replace("/", "")
                    .replace("cp", "")
                    .replace(Constants.MOD_ID, "")
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
                executionString = Constants.TRANSLATOR.translate("craftpresence.command.usage.main");
            } else if (!StringUtils.isNullOrEmpty(executionCommandArgs[0])) {
                if (executionCommandArgs[0].equalsIgnoreCase("request")) {
                    if (CraftPresence.CLIENT.isAvailable()) {
                        if (executionCommandArgs.length == 1) {
                            if (CraftPresence.CLIENT.STATUS == DiscordStatus.JoinRequest && CraftPresence.CLIENT.REQUESTER_USER != null) {
                                executionString = Constants.TRANSLATOR.translate("craftpresence.command.request.info",
                                        CraftPresence.CLIENT.REQUESTER_USER.getEffectiveName(), CraftPresence.SCHEDULER.TIMER
                                );
                            } else {
                                executionString = Constants.TRANSLATOR.translate("craftpresence.command.request.none");
                            }
                        } else if (!StringUtils.isNullOrEmpty(executionCommandArgs[1])) {
                            if (CraftPresence.CLIENT.STATUS == DiscordStatus.JoinRequest && CraftPresence.CLIENT.REQUESTER_USER != null) {
                                if (executionCommandArgs[1].equalsIgnoreCase("accept")) {
                                    executionString = Constants.TRANSLATOR.translate("craftpresence.command.request.accept",
                                            CraftPresence.CLIENT.REQUESTER_USER.getEffectiveName()
                                    );
                                    CraftPresence.CLIENT.acceptJoinRequest();
                                } else if (executionCommandArgs[1].equalsIgnoreCase("deny")) {
                                    executionString = Constants.TRANSLATOR.translate("craftpresence.command.request.denied",
                                            CraftPresence.CLIENT.REQUESTER_USER.getEffectiveName()
                                    );
                                    CraftPresence.CLIENT.denyJoinRequest();
                                } else {
                                    executionString = Constants.TRANSLATOR.translate("craftpresence.command.unrecognized");
                                }
                            } else {
                                executionString = Constants.TRANSLATOR.translate("craftpresence.command.request.none");
                            }
                        }
                    } else {
                        executionString = Constants.TRANSLATOR.translate("craftpresence.command.offline");
                    }
                } else if (executionCommandArgs[0].equalsIgnoreCase("export")) {
                    String clientId = CraftPresence.CONFIG.generalSettings.clientId;
                    boolean doFullCopy = false;
                    String urlMeta = "";

                    if (executionCommandArgs.length == 1) {
                        executionString = Constants.TRANSLATOR.translate("craftpresence.command.usage.export", clientId, false);
                    } else if (!StringUtils.isNullOrEmpty(executionCommandArgs[1])) {
                        if (executionCommandArgs[1].equalsIgnoreCase("assets")) {
                            if (executionCommandArgs.length >= 3 && executionCommandArgs.length <= 5) {
                                for (int i = 2; i < executionCommandArgs.length; i++) {
                                    if (StringUtils.isValidBoolean(executionCommandArgs[i])) {
                                        doFullCopy = Boolean.parseBoolean(executionCommandArgs[i]);
                                    } else if (DiscordAssetUtils.isValidId(executionCommandArgs[i])) {
                                        clientId = executionCommandArgs[i];
                                    } else {
                                        final Matcher matcher = SURROUNDED_BY_QUOTES.matcher(commandString);
                                        if (matcher.find()) {
                                            urlMeta = matcher.group(1);
                                        }
                                    }
                                }
                            } else {
                                executionString = Constants.TRANSLATOR.translate("craftpresence.command.unrecognized");
                            }

                            exportAssets(clientId, doFullCopy, urlMeta);
                        } else {
                            executionString = Constants.TRANSLATOR.translate("craftpresence.command.unrecognized");
                        }
                    }
                } else if (executionCommandArgs[0].equalsIgnoreCase("compile")) {
                    if (executionCommandArgs.length == 1) {
                        executionString = Constants.TRANSLATOR.translate("craftpresence.command.usage.compile");
                    } else {
                        final Matcher matcher = SURROUNDED_BY_QUOTES.matcher(commandString);
                        if (matcher.find()) {
                            final String contents = matcher.group(1);
                            final StringBuilder out = new StringBuilder();

                            final String value = CraftPresence.CLIENT.getCompileResult(contents, true, null, out)
                                    .get().toString();
                            final int length = StringUtils.getBytes(value, "UTF-8").length;

                            executionString = Constants.TRANSLATOR.translate("craftpresence.command.compile",
                                    value, length, out.toString().replace("\n", "\\n")
                            );
                        } else {
                            executionString = Constants.TRANSLATOR.translate("craftpresence.command.unrecognized");
                        }
                    }
                } else if (executionCommandArgs[0].equalsIgnoreCase("search")) {
                    if (executionCommandArgs.length == 1) {
                        executionString = Constants.TRANSLATOR.translate("craftpresence.command.usage.search");
                    } else if (!StringUtils.isNullOrEmpty(executionCommandArgs[1])) {
                        final List<String> results = StringUtils.newArrayList(
                                CraftPresence.CLIENT.getArgumentEntries(executionCommandArgs[1])
                        );

                        if (!results.isEmpty()) {
                            openScreen(new DynamicSelectorGui(
                                    Constants.TRANSLATOR.translate("gui.config.title.selector.view.items"),
                                    results,
                                    null, null,
                                    false, false, DynamicScrollableList.RenderType.Placeholder,
                                    null, null
                            ));
                        } else {
                            executionString = Constants.TRANSLATOR.translate("gui.config.message.empty.list");
                        }
                    }
                } else if (executionCommandArgs[0].equalsIgnoreCase("reload")) {
                    executionString = Constants.TRANSLATOR.translate("craftpresence.command.reload");
                    CommandUtils.reloadData(true);
                    executionString = Constants.TRANSLATOR.translate("craftpresence.command.reload.complete");
                } else if (executionCommandArgs[0].equalsIgnoreCase("shutdown")) {
                    if (CraftPresence.CLIENT.isAvailable()) {
                        executionString = Constants.TRANSLATOR.translate("craftpresence.command.shutdown.pre");
                        CraftPresence.CLIENT.shutDown();
                        executionString = Constants.TRANSLATOR.translate("craftpresence.command.shutdown.post");
                    } else {
                        executionString = Constants.TRANSLATOR.translate("craftpresence.command.offline");
                    }
                } else if (executionCommandArgs[0].equalsIgnoreCase("reboot")) {
                    executionString = Constants.TRANSLATOR.translate("craftpresence.command.reboot.pre");
                    CommandUtils.setupRPC();
                    executionString = Constants.TRANSLATOR.translate("craftpresence.command.reboot.post");
                } else if (executionCommandArgs[0].equalsIgnoreCase("view")) {
                    if (executionCommandArgs.length == 1) {
                        executionString = Constants.TRANSLATOR.translate("craftpresence.command.usage.view");
                    } else if (!StringUtils.isNullOrEmpty(executionCommandArgs[1])) {
                        if (executionCommandArgs[1].equalsIgnoreCase("placeholders")) {
                            // Redirect: `/cp view placeholders` => `/cp search all`
                            executeCommand("search", "all");
                            return;
                        } else if (executionCommandArgs[1].equalsIgnoreCase("items")) {
                            if (CraftPresence.TILE_ENTITIES.isEnabled()) {
                                openScreen(new DynamicSelectorGui(
                                        Constants.TRANSLATOR.translate("gui.config.title.selector.view.items"),
                                        CraftPresence.TILE_ENTITIES.TILE_ENTITY_NAMES,
                                        null, null,
                                        false, false, DynamicScrollableList.RenderType.ItemData,
                                        null, null
                                ));
                            } else {
                                executionString = Constants.TRANSLATOR.translate("gui.config.message.hover.access",
                                        Constants.TRANSLATOR.translate("gui.config.name.advanced.enable_per_item")
                                );
                            }
                        } else if (executionCommandArgs[1].equalsIgnoreCase("entities")) {
                            if (CraftPresence.ENTITIES.isEnabled()) {
                                openScreen(new DynamicSelectorGui(
                                        Constants.TRANSLATOR.translate("gui.config.title.selector.view.entities"),
                                        CraftPresence.ENTITIES.ENTITY_NAMES,
                                        null, null,
                                        false, false, DynamicScrollableList.RenderType.EntityData,
                                        null, null
                                ));
                            } else {
                                executionString = Constants.TRANSLATOR.translate("gui.config.message.hover.access",
                                        Constants.TRANSLATOR.translate("gui.config.name.advanced.enable_per_entity")
                                );
                            }
                        } else if (executionCommandArgs[1].equalsIgnoreCase("servers")) {
                            if (CraftPresence.SERVER.isEnabled()) {
                                openScreen(new DynamicSelectorGui(
                                        Constants.TRANSLATOR.translate("gui.config.title.selector.view.servers"),
                                        CraftPresence.SERVER.knownAddresses,
                                        null, null,
                                        false, false, DynamicScrollableList.RenderType.ServerData,
                                        null, null
                                ));
                            } else {
                                executionString = Constants.TRANSLATOR.translate("gui.config.message.hover.access",
                                        Constants.TRANSLATOR.translate("gui.config.name.general.detect_world_data")
                                );
                            }
                        } else if (executionCommandArgs[1].equalsIgnoreCase("screens")) {
                            if (CraftPresence.GUIS.isEnabled()) {
                                openScreen(new DynamicSelectorGui(
                                        Constants.TRANSLATOR.translate("gui.config.title.selector.view.guis"),
                                        CraftPresence.GUIS.GUI_NAMES,
                                        null, null,
                                        false, false, null,
                                        null, null
                                ));
                            } else {
                                executionString = Constants.TRANSLATOR.translate("gui.config.message.hover.access",
                                        Constants.TRANSLATOR.translate("gui.config.name.advanced.enable_per_gui")
                                );
                            }
                        } else if (executionCommandArgs[1].equalsIgnoreCase("biomes")) {
                            if (CraftPresence.BIOMES.isEnabled()) {
                                openScreen(new DynamicSelectorGui(
                                        Constants.TRANSLATOR.translate("gui.config.title.selector.view.biomes"),
                                        CraftPresence.BIOMES.BIOME_NAMES,
                                        null, null,
                                        false, false, null,
                                        null, null
                                ));
                            } else {
                                executionString = Constants.TRANSLATOR.translate("gui.config.message.hover.access",
                                        Constants.TRANSLATOR.translate("gui.config.name.general.detect_biome_data")
                                );
                            }
                        } else if (executionCommandArgs[1].equalsIgnoreCase("dimensions")) {
                            if (CraftPresence.DIMENSIONS.isEnabled()) {
                                openScreen(new DynamicSelectorGui(
                                        Constants.TRANSLATOR.translate("gui.config.title.selector.view.dimensions"),
                                        CraftPresence.DIMENSIONS.DIMENSION_NAMES,
                                        null, null,
                                        false, false, null,
                                        null, null
                                ));
                            } else {
                                executionString = Constants.TRANSLATOR.translate("gui.config.message.hover.access",
                                        Constants.TRANSLATOR.translate("gui.config.name.general.detect_dimension_data")
                                );
                            }
                        } else if (executionCommandArgs[1].equalsIgnoreCase("currentData")) {
                            if (CraftPresence.CLIENT.isAvailable()) {
                                executionString = Constants.TRANSLATOR.translate("craftpresence.command.current_data",
                                        CraftPresence.CLIENT.CURRENT_USER.getEffectiveName(),
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
                            } else {
                                executionString = Constants.TRANSLATOR.translate("craftpresence.command.offline");
                            }
                        } else if (executionCommandArgs[1].equalsIgnoreCase("assets")) {
                            if (executionCommandArgs.length == 2) {
                                executionString = Constants.TRANSLATOR.translate("craftpresence.command.usage.view.assets");
                            } else if (!StringUtils.isNullOrEmpty(executionCommandArgs[2])) {
                                if (executionCommandArgs[2].equalsIgnoreCase("custom")) {
                                    openScreen(new DynamicSelectorGui(
                                            Constants.TRANSLATOR.translate("gui.config.title.selector.view.assets.custom"),
                                            DiscordAssetUtils.CUSTOM_ASSET_LIST.keySet(),
                                            null, null,
                                            false, false, DynamicScrollableList.RenderType.CustomDiscordAsset,
                                            null, null
                                    ));
                                } else if (executionCommandArgs[2].equalsIgnoreCase("all")) {
                                    openScreen(new DynamicSelectorGui(
                                            Constants.TRANSLATOR.translate("gui.config.title.selector.view.assets.all"),
                                            DiscordAssetUtils.ASSET_LIST.keySet(),
                                            null, null,
                                            false, false, DynamicScrollableList.RenderType.DiscordAsset,
                                            null, null
                                    ));
                                }
                            }
                        } else {
                            executionString = Constants.TRANSLATOR.translate("craftpresence.command.unrecognized");
                        }
                    }
                } else {
                    executionString = Constants.TRANSLATOR.translate("craftpresence.command.unrecognized");
                }
            } else {
                executionString = Constants.TRANSLATOR.translate("craftpresence.logger.error.command");
            }
        }

        executionCommandArgs = null;
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (!blockInteractions) {
            if (commandInput.isControlFocused()) {
                if (isEscapeKey(keyCode)) {
                    commandInput.setControlFocused(false);
                } else {
                    if (commandString.startsWith("/") && commandArgs != null && commandArgs.length > 0 &&
                            (commandArgs[0].equalsIgnoreCase("cp") || commandArgs[0].equalsIgnoreCase(Constants.MOD_ID))) {
                        if (keyCode == getKeyByVersion(15, 258) && !tabCompletions.isEmpty()) { // Tab Key Event
                            if (commandArgs.length > 1 && (filteredCommandArgs[filteredCommandArgs.length - 1].length() > 1 ||
                                    filteredCommandArgs[filteredCommandArgs.length - 1].equalsIgnoreCase("?")
                            )) {
                                commandString = commandString.replace(
                                        filteredCommandArgs[filteredCommandArgs.length - 1], tabCompletions.getFirst()
                                );
                                commandInput.setControlMessage(commandString);
                            }
                        } else if (keyCode == getKeyByVersion(28, 257) || keyCode == getKeyByVersion(156, 335)) { // Enter Key Event
                            executeCommand(filteredCommandArgs);
                            childFrame.resetMouseScroll();
                            childFrame.setMouseScroll(0);
                        }
                    }
                }
            }
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
        final List<String> completions = StringUtils.newArrayList();

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
     * @param urlMeta    The additional data to append to the URL
     */
    private void exportAssets(final String clientId, final boolean doFullCopy, final String urlMeta) {
        Constants.getThreadFactory().newThread(() -> {
            blockInteractions = true;
            final DiscordAsset[] assetList = DiscordAssetUtils.loadAssets(clientId, false);
            boolean hasError = false;

            if (assetList != null) {
                final String filePath = Constants.MOD_ID + File.separator + "export" + File.separator + clientId + File.separator;
                executionString = Constants.TRANSLATOR.translate("craftpresence.command.export.pre", assetList.length, clientId, doFullCopy);

                final File dataDir = new File(filePath + "downloads.txt");
                final String encoding = CraftPresence.CLIENT.isAvailable() ?
                        CraftPresence.CLIENT.ipcInstance.getEncoding() : "UTF-8";

                // Create Data Directory if non-existent
                try {
                    FileUtils.assertFileExists(dataDir);
                } catch (Throwable ex) {
                    Constants.LOG.debugError(ex);
                    hasError = true;
                }

                try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(dataDir.toPath()), encoding))) {
                    // Create and write initial data, using the encoding of our current ipc instance (UTF-8 by default)
                    bw.write("## Export Data => " + clientId);
                    bw.newLine();
                    bw.newLine();

                    for (int i = 0; i < assetList.length; i++) {
                        executionString = Constants.TRANSLATOR.translate("craftpresence.command.export.progress",
                                clientId, i + 1, assetList.length
                        );
                        final DiscordAsset asset = assetList[i];
                        final String assetUrl = DiscordAssetUtils.getDiscordAssetUrl(clientId, asset.getId()) + urlMeta;
                        final String assetName = asset.getName() + ".png";
                        if (doFullCopy) {
                            FileUtils.downloadFile(assetUrl, new File(filePath + assetName));
                        }
                        bw.write("* " + assetName + " => " + assetUrl);
                        bw.newLine();
                    }
                } catch (Throwable ex) {
                    Constants.LOG.debugError(ex);
                    hasError = true;
                }

                if (!hasError) {
                    executionString = Constants.TRANSLATOR.translate("craftpresence.command.export.post", assetList.length, clientId, doFullCopy);
                } else {
                    executionString = Constants.TRANSLATOR.translate("craftpresence.command.export.exception", clientId);
                }
            } else {
                executionString = Constants.TRANSLATOR.translate("craftpresence.command.export.exception", clientId);
            }
            blockInteractions = false;
        }).start();
    }
}

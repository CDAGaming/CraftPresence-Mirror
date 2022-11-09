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

package com.gitlab.cdagaming.craftpresence.utils.gui.impl;

import com.gitlab.cdagaming.craftpresence.CraftPresence;
import com.gitlab.cdagaming.craftpresence.ModUtils;
import com.gitlab.cdagaming.craftpresence.impl.PairConsumer;
import com.gitlab.cdagaming.craftpresence.utils.StringUtils;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ExtendedButtonControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ExtendedTextControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ScrollableListControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ScrollableListControl.RenderType;
import com.gitlab.cdagaming.craftpresence.utils.gui.integrations.ExtendedScreen;
import com.google.common.collect.Lists;
import net.minecraft.client.gui.GuiScreen;

import java.util.List;
import java.util.Set;

/**
 * The Selector Gui Screen
 */
public class SelectorGui extends ExtendedScreen {
    private final String mainTitle, attributeName, originalValue;
    private final List<String> originalList;
    private final boolean allowContinuing, allowDynamicEditing;
    private final PairConsumer<String, String> onUpdatedCallback;
    private final RenderType renderType;
    private final PairConsumer<String, GuiScreen> onAdjustDynamicEntry;
    private ExtendedButtonControl proceedButton;
    private ScrollableListControl scrollList;
    private ExtendedTextControl searchBox;
    private String searchTerm;
    private List<String> itemList;

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param parentScreen         The Parent Screen for this Instance
     * @param mainTitle            The Main Screen Title
     * @param list                 The list to interpret
     * @param currentValue         The initial value to select within the list
     * @param attributeName        The attribute name to interpret following selection
     * @param allowContinuing      Whether to allow continue button functionality
     * @param allowDynamicEditing  Whether to allow adding new entries to the list
     * @param renderType           The {@link RenderType} for the list, adjusting it's look and feel
     * @param onUpdatedCallback    The callback to trigger when continuing following selection
     * @param onAdjustDynamicEntry The callback to trigger when adjusting a dynamic entry
     */
    public SelectorGui(GuiScreen parentScreen, String mainTitle, List<String> list, String currentValue, String attributeName, boolean allowContinuing, boolean allowDynamicEditing, RenderType renderType, PairConsumer<String, String> onUpdatedCallback, PairConsumer<String, GuiScreen> onAdjustDynamicEntry) {
        super(parentScreen);
        itemList = originalList = list;
        originalValue = currentValue;
        this.mainTitle = mainTitle;
        this.attributeName = attributeName;
        this.allowContinuing = allowContinuing;
        this.allowDynamicEditing = allowDynamicEditing;
        this.renderType = renderType;
        this.onUpdatedCallback = onUpdatedCallback;
        this.onAdjustDynamicEntry = onAdjustDynamicEntry;
    }

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param parentScreen         The Parent Screen for this Instance
     * @param mainTitle            The Main Screen Title
     * @param list                 The list to interpret
     * @param currentValue         The initial value to select within the list
     * @param attributeName        The attribute name to interpret following selection
     * @param allowContinuing      Whether to allow continue button functionality
     * @param allowDynamicEditing  Whether to allow adding new entries to the list
     * @param renderType           The {@link RenderType} for the list, adjusting it's look and feel
     * @param onUpdatedCallback    The callback to trigger when continuing following selection
     * @param onAdjustDynamicEntry The callback to trigger when adjusting a dynamic entry
     */
    public SelectorGui(GuiScreen parentScreen, String mainTitle, Set<String> list, String currentValue, String attributeName, boolean allowContinuing, boolean allowDynamicEditing, RenderType renderType, PairConsumer<String, String> onUpdatedCallback, PairConsumer<String, GuiScreen> onAdjustDynamicEntry) {
        this(parentScreen, mainTitle, Lists.newArrayList(list), currentValue, attributeName, allowContinuing, allowDynamicEditing, renderType, onUpdatedCallback, onAdjustDynamicEntry);
    }

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param parentScreen         The Parent Screen for this Instance
     * @param mainTitle            The Main Screen Title
     * @param list                 The list to interpret
     * @param currentValue         The initial value to select within the list
     * @param attributeName        The attribute name to interpret following selection
     * @param allowContinuing      Whether to allow continue button functionality
     * @param allowDynamicEditing  Whether to allow adding new entries to the list
     * @param renderType           The {@link RenderType} for the list, adjusting it's look and feel
     * @param onAdjustDynamicEntry The callback to trigger when adjusting a dynamic entry
     */
    public SelectorGui(GuiScreen parentScreen, String mainTitle, List<String> list, String currentValue, String attributeName, boolean allowContinuing, boolean allowDynamicEditing, RenderType renderType, PairConsumer<String, GuiScreen> onAdjustDynamicEntry) {
        this(parentScreen, mainTitle, list, currentValue, attributeName, allowContinuing, allowDynamicEditing, renderType, null, onAdjustDynamicEntry);
    }

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param parentScreen         The Parent Screen for this Instance
     * @param mainTitle            The Main Screen Title
     * @param list                 The list to interpret
     * @param currentValue         The initial value to select within the list
     * @param attributeName        The attribute name to interpret following selection
     * @param allowContinuing      Whether to allow continue button functionality
     * @param allowDynamicEditing  Whether to allow adding new entries to the list
     * @param renderType           The {@link RenderType} for the list, adjusting it's look and feel
     * @param onAdjustDynamicEntry The callback to trigger when adjusting a dynamic entry
     */
    public SelectorGui(GuiScreen parentScreen, String mainTitle, Set<String> list, String currentValue, String attributeName, boolean allowContinuing, boolean allowDynamicEditing, RenderType renderType, PairConsumer<String, GuiScreen> onAdjustDynamicEntry) {
        this(parentScreen, mainTitle, list, currentValue, attributeName, allowContinuing, allowDynamicEditing, renderType, null, onAdjustDynamicEntry);
    }

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param parentScreen         The Parent Screen for this Instance
     * @param mainTitle            The Main Screen Title
     * @param list                 The list to interpret
     * @param currentValue         The initial value to select within the list
     * @param attributeName        The attribute name to interpret following selection
     * @param allowContinuing      Whether to allow continue button functionality
     * @param allowDynamicEditing  Whether to allow adding new entries to the list
     * @param onAdjustDynamicEntry The callback to trigger when adjusting a dynamic entry
     */
    public SelectorGui(GuiScreen parentScreen, String mainTitle, List<String> list, String currentValue, String attributeName, boolean allowContinuing, boolean allowDynamicEditing, PairConsumer<String, GuiScreen> onAdjustDynamicEntry) {
        this(parentScreen, mainTitle, list, currentValue, attributeName, allowContinuing, allowDynamicEditing, RenderType.None, onAdjustDynamicEntry);
    }

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param parentScreen         The Parent Screen for this Instance
     * @param mainTitle            The Main Screen Title
     * @param list                 The list to interpret
     * @param currentValue         The initial value to select within the list
     * @param attributeName        The attribute name to interpret following selection
     * @param allowContinuing      Whether to allow continue button functionality
     * @param allowDynamicEditing  Whether to allow adding new entries to the list
     * @param onAdjustDynamicEntry The callback to trigger when adjusting a dynamic entry
     */
    public SelectorGui(GuiScreen parentScreen, String mainTitle, Set<String> list, String currentValue, String attributeName, boolean allowContinuing, boolean allowDynamicEditing, PairConsumer<String, GuiScreen> onAdjustDynamicEntry) {
        this(parentScreen, mainTitle, list, currentValue, attributeName, allowContinuing, allowDynamicEditing, RenderType.None, onAdjustDynamicEntry);
    }

    @Override
    public void initializeUi() {
        if (itemList != null && !itemList.isEmpty()) {
            proceedButton = addControl(
                    new ExtendedButtonControl(
                            (getScreenWidth() - 100), (getScreenHeight() - 30),
                            90, 20,
                            "gui.config.message.button.back",
                            () -> {
                                if (allowContinuing && scrollList.currentValue != null) {
                                    if (originalValue != null) {
                                        if (!scrollList.currentValue.equals(originalValue)) {
                                            if (onUpdatedCallback != null) {
                                                onUpdatedCallback.accept(attributeName, scrollList.currentValue);
                                                CraftPresence.GUIS.openScreen(parentScreen);
                                            } else {
                                                CraftPresence.GUIS.openScreen(new MessageGui(parentScreen, StringUtils.splitTextByNewLine(ModUtils.TRANSLATOR.translate("gui.config.message.null"))));
                                            }
                                        } else {
                                            CraftPresence.GUIS.openScreen(parentScreen);
                                        }
                                    } else {
                                        if (allowDynamicEditing && onAdjustDynamicEntry != null) {
                                            onAdjustDynamicEntry.accept(scrollList.currentValue, parentScreen);
                                        } else {
                                            CraftPresence.GUIS.openScreen(new MessageGui(parentScreen, StringUtils.splitTextByNewLine(ModUtils.TRANSLATOR.translate("gui.config.message.null"))));
                                        }
                                    }
                                } else {
                                    CraftPresence.GUIS.openScreen(parentScreen);
                                }
                            }
                    )
            );

            scrollList = addList(
                    new ScrollableListControl(
                            mc, this,
                            getScreenWidth(), getScreenHeight(),
                            32, getScreenHeight() - 45, renderType != RenderType.None && !CraftPresence.CONFIG.accessibilitySettings.stripExtraGuiElements ? 45 : 18,
                            itemList, originalValue,
                            renderType
                    )
            );
            searchBox = addControl(
                    new ExtendedTextControl(
                            getFontRenderer(),
                            60, (getScreenHeight() - 30),
                            120, 20
                    )
            );

            if (allowDynamicEditing && onAdjustDynamicEntry != null) {
                // Adding Add New Button
                addControl(
                        new ExtendedButtonControl(
                                (getScreenWidth() - 195), (getScreenHeight() - 30),
                                90, 20,
                                "gui.config.message.button.add.new",
                                () -> onAdjustDynamicEntry.accept(null, parentScreen)
                        )
                );
            }

            super.initializeUi();
        } else {
            CraftPresence.GUIS.openScreen(new MessageGui(parentScreen, StringUtils.splitTextByNewLine(ModUtils.TRANSLATOR.translate("gui.config.message.empty.list"))));
        }
    }

    @Override
    public void preRender() {
        final List<String> modifiedList = Lists.newArrayList();

        if (!searchBox.getControlMessage().isEmpty()) {
            if (!searchBox.getControlMessage().equals(searchTerm)) {
                searchTerm = searchBox.getControlMessage();
                for (String item : originalList) {
                    if (!modifiedList.contains(item)) {
                        final List<String> entriesToCheck = Lists.newArrayList(item);
                        if (scrollList.entryAliases.containsKey(item)) {
                            entriesToCheck.add(scrollList.entryAliases.get(item));
                        }

                        for (String entry : entriesToCheck) {
                            if (entry.toLowerCase().contains(searchTerm.toLowerCase())) {
                                modifiedList.add(item);
                                break;
                            }
                        }
                    }
                }
                itemList = modifiedList;
            }
        } else {
            itemList = originalList;
        }

        if (!itemList.equals(originalList) && !itemList.contains(scrollList.currentValue)) {
            if (originalValue != null && itemList.contains(originalValue)) {
                scrollList.currentValue = originalValue;
            } else {
                scrollList.currentValue = null;
            }
        } else if (scrollList.currentValue == null && originalValue != null) {
            scrollList.currentValue = originalValue;
        }

        scrollList.setList(itemList);
        scrollList.currentHoverText.clear();

        proceedButton.setControlMessage(
                allowContinuing && scrollList.currentValue != null &&
                        ((originalValue != null && !scrollList.currentValue.equals(originalValue)) || (StringUtils.isNullOrEmpty(originalValue))) ?
                        "gui.config.message.button.continue" : "gui.config.message.button.back"
        );
    }

    @Override
    public void postRender() {
        final String searchText = ModUtils.TRANSLATOR.translate("gui.config.message.editor.search");
        final String extraText = isVerboseMode() ? ModUtils.TRANSLATOR.translate("gui.config.title.selector.extra", itemList.size(), originalList.size()) : "";
        final String displayText = mainTitle + " " + extraText;

        renderString(searchText, (30 - (getStringWidth(searchText) / 2f)), (getScreenHeight() - 25), 0xFFFFFF);
        renderString(displayText, (getScreenWidth() / 2f) - (getStringWidth(displayText) / 2f), 15, 0xFFFFFF);

        if (scrollList.currentHoverText != null && !scrollList.currentHoverText.isEmpty()) {
            CraftPresence.GUIS.drawMultiLineString(scrollList.currentHoverText, this, true);
        }
    }
}

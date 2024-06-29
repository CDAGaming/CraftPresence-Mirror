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

import com.gitlab.cdagaming.craftpresence.core.Constants;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ScrollableListControl;
import io.github.cdagaming.unicore.utils.StringUtils;
import com.gitlab.cdagaming.unilib.utils.gui.controls.ExtendedButtonControl;
import com.gitlab.cdagaming.unilib.utils.gui.controls.ExtendedTextControl;
import com.gitlab.cdagaming.unilib.utils.gui.impl.MessageGui;
import com.gitlab.cdagaming.unilib.utils.gui.integrations.ExtendedScreen;
import net.minecraft.client.gui.GuiScreen;

import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * The Selector Gui Screen
 */
public class SelectorGui extends ExtendedScreen {
    private final String mainTitle, attributeName, originalValue;
    private final List<String> originalList;
    private final boolean allowContinuing, allowDynamicEditing;
    private final BiConsumer<String, String> onUpdatedCallback;
    private final ScrollableListControl.RenderType renderType;
    private final BiConsumer<String, GuiScreen> onAdjustDynamicEntry;
    private ExtendedButtonControl proceedButton;
    private ScrollableListControl scrollList;
    private ExtendedTextControl searchBox;
    private String searchTerm;
    private List<String> itemList;
    private ScrollableListControl.IdentifierType identifierType = ScrollableListControl.IdentifierType.None;

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param mainTitle            The Main Screen Title
     * @param list                 The list to interpret
     * @param currentValue         The initial value to select within the list
     * @param attributeName        The attribute name to interpret following selection
     * @param allowContinuing      Whether to allow the "continue" button functionality
     * @param allowDynamicEditing  Whether to allow adding new entries to the list
     * @param renderType           The {@link ScrollableListControl.RenderType} for the list, adjusting its look and feel
     * @param onUpdatedCallback    The callback to trigger when continuing following selection
     * @param onAdjustDynamicEntry The callback to trigger when adjusting a dynamic entry
     */
    public SelectorGui(String mainTitle, List<String> list, String currentValue, String attributeName, boolean allowContinuing, boolean allowDynamicEditing, ScrollableListControl.RenderType renderType, BiConsumer<String, String> onUpdatedCallback, BiConsumer<String, GuiScreen> onAdjustDynamicEntry) {
        super();
        itemList = originalList = StringUtils.newArrayList(list);
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
     * @param mainTitle            The Main Screen Title
     * @param list                 The list to interpret
     * @param currentValue         The initial value to select within the list
     * @param attributeName        The attribute name to interpret following selection
     * @param allowContinuing      Whether to allow the "continue" button functionality
     * @param allowDynamicEditing  Whether to allow adding new entries to the list
     * @param renderType           The {@link ScrollableListControl.RenderType} for the list, adjusting its look and feel
     * @param onUpdatedCallback    The callback to trigger when continuing following selection
     * @param onAdjustDynamicEntry The callback to trigger when adjusting a dynamic entry
     */
    public SelectorGui(String mainTitle, Set<String> list, String currentValue, String attributeName, boolean allowContinuing, boolean allowDynamicEditing, ScrollableListControl.RenderType renderType, BiConsumer<String, String> onUpdatedCallback, BiConsumer<String, GuiScreen> onAdjustDynamicEntry) {
        this(mainTitle, StringUtils.newArrayList(list), currentValue, attributeName, allowContinuing, allowDynamicEditing, renderType, onUpdatedCallback, onAdjustDynamicEntry);
    }

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param mainTitle            The Main Screen Title
     * @param list                 The list to interpret
     * @param currentValue         The initial value to select within the list
     * @param attributeName        The attribute name to interpret following selection
     * @param allowContinuing      Whether to allow the "continue" button functionality
     * @param allowDynamicEditing  Whether to allow adding new entries to the list
     * @param renderType           The {@link ScrollableListControl.RenderType} for the list, adjusting its look and feel
     * @param onAdjustDynamicEntry The callback to trigger when adjusting a dynamic entry
     */
    public SelectorGui(String mainTitle, List<String> list, String currentValue, String attributeName, boolean allowContinuing, boolean allowDynamicEditing, ScrollableListControl.RenderType renderType, BiConsumer<String, GuiScreen> onAdjustDynamicEntry) {
        this(mainTitle, list, currentValue, attributeName, allowContinuing, allowDynamicEditing, renderType, null, onAdjustDynamicEntry);
    }

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param mainTitle            The Main Screen Title
     * @param list                 The list to interpret
     * @param currentValue         The initial value to select within the list
     * @param attributeName        The attribute name to interpret following selection
     * @param allowContinuing      Whether to allow the "continue" button functionality
     * @param allowDynamicEditing  Whether to allow adding new entries to the list
     * @param renderType           The {@link ScrollableListControl.RenderType} for the list, adjusting its look and feel
     * @param onAdjustDynamicEntry The callback to trigger when adjusting a dynamic entry
     */
    public SelectorGui(String mainTitle, Set<String> list, String currentValue, String attributeName, boolean allowContinuing, boolean allowDynamicEditing, ScrollableListControl.RenderType renderType, BiConsumer<String, GuiScreen> onAdjustDynamicEntry) {
        this(mainTitle, list, currentValue, attributeName, allowContinuing, allowDynamicEditing, renderType, null, onAdjustDynamicEntry);
    }

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param mainTitle            The Main Screen Title
     * @param list                 The list to interpret
     * @param currentValue         The initial value to select within the list
     * @param attributeName        The attribute name to interpret following selection
     * @param allowContinuing      Whether to allow the "continue" button functionality
     * @param allowDynamicEditing  Whether to allow adding new entries to the list
     * @param onAdjustDynamicEntry The callback to trigger when adjusting a dynamic entry
     */
    public SelectorGui(String mainTitle, List<String> list, String currentValue, String attributeName, boolean allowContinuing, boolean allowDynamicEditing, BiConsumer<String, GuiScreen> onAdjustDynamicEntry) {
        this(mainTitle, list, currentValue, attributeName, allowContinuing, allowDynamicEditing, ScrollableListControl.RenderType.None, onAdjustDynamicEntry);
    }

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param mainTitle            The Main Screen Title
     * @param list                 The list to interpret
     * @param currentValue         The initial value to select within the list
     * @param attributeName        The attribute name to interpret following selection
     * @param allowContinuing      Whether to allow the "continue" button functionality
     * @param allowDynamicEditing  Whether to allow adding new entries to the list
     * @param onAdjustDynamicEntry The callback to trigger when adjusting a dynamic entry
     */
    public SelectorGui(String mainTitle, Set<String> list, String currentValue, String attributeName, boolean allowContinuing, boolean allowDynamicEditing, BiConsumer<String, GuiScreen> onAdjustDynamicEntry) {
        this(mainTitle, list, currentValue, attributeName, allowContinuing, allowDynamicEditing, ScrollableListControl.RenderType.None, onAdjustDynamicEntry);
    }

    @Override
    public void initializeUi() {
        if (itemList != null && !itemList.isEmpty()) {
            scrollList = addList(
                    new ScrollableListControl(
                            getGameInstance(), this,
                            getScreenWidth(), getScreenHeight(),
                            32, getScreenHeight() - 32,
                            itemList, originalValue,
                            renderType
                    ).setIdentifierType(identifierType)
            );
            proceedButton = addControl(
                    new ExtendedButtonControl(
                            (getScreenWidth() - 101), (getScreenHeight() - 26),
                            95, 20,
                            "gui.config.message.button.back",
                            () -> {
                                if (allowContinuing && scrollList.currentValue != null) {
                                    if (originalValue != null) {
                                        if (!scrollList.currentValue.equals(originalValue)) {
                                            if (onUpdatedCallback != null) {
                                                onUpdatedCallback.accept(attributeName, scrollList.currentValue);
                                                openScreen(getParent());
                                            } else {
                                                openScreen(new MessageGui("gui.config.message.null"), getParent());
                                            }
                                        } else {
                                            openScreen(getParent());
                                        }
                                    } else {
                                        if (onAdjustDynamicEntry != null) {
                                            onAdjustDynamicEntry.accept(scrollList.currentValue, getParent());
                                        } else {
                                            openScreen(new MessageGui("gui.config.message.null"), getParent());
                                        }
                                    }
                                } else {
                                    openScreen(getParent());
                                }
                            }
                    )
            );
            int searchBoxRight = proceedButton.getLeft() - 6;

            if (allowDynamicEditing && onAdjustDynamicEntry != null) {
                // Adding Add New Button
                addControl(
                        new ExtendedButtonControl(
                                (proceedButton.getLeft() - 100), (getScreenHeight() - 26),
                                95, 20,
                                "gui.config.message.button.add.new",
                                () -> onAdjustDynamicEntry.accept(null, getParent())
                        )
                );
                searchBoxRight -= 100;
            }

            searchBox = addControl(
                    new ExtendedTextControl(
                            getFontRenderer(),
                            60, (getScreenHeight() - 26),
                            searchBoxRight - 60, 20
                    )
            );

            super.initializeUi();
        } else {
            openScreen(new MessageGui("gui.config.message.empty.list"), getParent());
        }
    }

    @Override
    public void preRender() {
        final List<String> originalItems = StringUtils.newArrayList(originalList);
        final List<String> modifiedList = StringUtils.newArrayList();

        if (!searchBox.getControlMessage().isEmpty()) {
            if (!searchBox.getControlMessage().equals(searchTerm)) {
                searchTerm = searchBox.getControlMessage();
                for (String item : originalItems) {
                    if (!modifiedList.contains(item)) {
                        final List<String> entriesToCheck = StringUtils.newArrayList(item);
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
            itemList = originalItems;
        }

        if (!itemList.equals(originalItems) && !itemList.contains(scrollList.currentValue)) {
            if (originalValue != null && itemList.contains(originalValue)) {
                scrollList.currentValue = originalValue;
            } else {
                scrollList.currentValue = null;
            }
        } else if (scrollList.currentValue == null && originalValue != null) {
            scrollList.currentValue = originalValue;
        }

        scrollList.setList(itemList);

        proceedButton.setControlMessage(
                allowContinuing && scrollList.currentValue != null &&
                        ((originalValue != null && !scrollList.currentValue.equals(originalValue)) || (StringUtils.isNullOrEmpty(originalValue))) ?
                        "gui.config.message.button.continue" : "gui.config.message.button.back"
        );

        super.preRender();
    }

    @Override
    public void renderExtra() {
        final String searchText = Constants.TRANSLATOR.translate("gui.config.message.editor.search");
        final int renderY = searchBox.getBottom() - (searchBox.getControlHeight() / 2) - (getFontHeight() / 2);

        final String extraText = isVerboseMode() ? Constants.TRANSLATOR.translate("gui.config.title.selector.extra", itemList.size(), originalList.size()) : "";
        final String displayText = mainTitle + " " + extraText;

        renderScrollingString(
                searchText,
                2, renderY,
                58, renderY + getFontHeight(),
                0xFFFFFF
        );
        renderScrollingString(
                displayText,
                30, 0,
                getScreenWidth() - 30, 32,
                0xFFFFFF
        );

        super.renderExtra();
    }

    @Override
    public void postRender() {
        if (scrollList.currentHoverText != null && !scrollList.currentHoverText.isEmpty()) {
            drawMultiLineString(scrollList.currentHoverText);
            scrollList.currentHoverText.clear();
        }

        super.postRender();
    }

    /**
     * Sets the Identifier Type to be linked to this Render Type
     *
     * @param type The {@link ScrollableListControl.IdentifierType} to interpret
     * @return the modified instance
     */
    public SelectorGui setIdentifierType(final ScrollableListControl.IdentifierType type) {
        this.identifierType = type;
        return this;
    }
}

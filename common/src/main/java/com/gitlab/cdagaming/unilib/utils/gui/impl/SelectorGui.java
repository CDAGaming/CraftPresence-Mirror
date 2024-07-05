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

package com.gitlab.cdagaming.unilib.utils.gui.impl;

import com.gitlab.cdagaming.unilib.core.CoreUtils;
import com.gitlab.cdagaming.unilib.utils.gui.controls.ExtendedButtonControl;
import com.gitlab.cdagaming.unilib.utils.gui.controls.ExtendedTextControl;
import com.gitlab.cdagaming.unilib.utils.gui.controls.ScrollableListControl;
import com.gitlab.cdagaming.unilib.utils.gui.integrations.ExtendedScreen;
import io.github.cdagaming.unicore.utils.StringUtils;
import net.minecraft.client.gui.GuiScreen;

import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * The Selector Gui Screen
 */
public class SelectorGui extends ExtendedScreen {
    private final String attributeName, originalValue;
    private final List<String> originalList;
    private final boolean allowContinuing, allowDynamicEditing;
    private final BiConsumer<String, String> onUpdatedCallback;
    private final BiConsumer<String, GuiScreen> onAdjustDynamicEntry;
    private ExtendedButtonControl proceedButton;
    private ScrollableListControl scrollList;
    private ExtendedTextControl searchBox;
    private String searchTerm;
    private List<String> itemList;

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param mainTitle            The Main Screen Title
     * @param list                 The list to interpret
     * @param currentValue         The initial value to select within the list
     * @param attributeName        The attribute name to interpret following selection
     * @param allowContinuing      Whether to allow the "continue" button functionality
     * @param allowDynamicEditing  Whether to allow adding new entries to the list
     * @param onUpdatedCallback    The callback to trigger when continuing following selection
     * @param onAdjustDynamicEntry The callback to trigger when adjusting a dynamic entry
     */
    public SelectorGui(String mainTitle, List<String> list, String currentValue, String attributeName, boolean allowContinuing, boolean allowDynamicEditing, BiConsumer<String, String> onUpdatedCallback, BiConsumer<String, GuiScreen> onAdjustDynamicEntry) {
        super(mainTitle);
        itemList = originalList = StringUtils.newArrayList(list);
        originalValue = currentValue;
        this.attributeName = attributeName;
        this.allowContinuing = allowContinuing;
        this.allowDynamicEditing = allowDynamicEditing;
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
     * @param onUpdatedCallback    The callback to trigger when continuing following selection
     * @param onAdjustDynamicEntry The callback to trigger when adjusting a dynamic entry
     */
    public SelectorGui(String mainTitle, Set<String> list, String currentValue, String attributeName, boolean allowContinuing, boolean allowDynamicEditing, BiConsumer<String, String> onUpdatedCallback, BiConsumer<String, GuiScreen> onAdjustDynamicEntry) {
        this(mainTitle, StringUtils.newArrayList(list), currentValue, attributeName, allowContinuing, allowDynamicEditing, onUpdatedCallback, onAdjustDynamicEntry);
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
        this(mainTitle, list, currentValue, attributeName, allowContinuing, allowDynamicEditing, null, onAdjustDynamicEntry);
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
        this(mainTitle, list, currentValue, attributeName, allowContinuing, allowDynamicEditing, null, onAdjustDynamicEntry);
    }

    @Override
    public void initializeUi() {
        if (getItemList() != null && !getItemList().isEmpty()) {
            appendListControl();
            appendControls();
            super.initializeUi();
        } else {
            openScreen(new MessageGui(CoreUtils.NAME, "This list is empty and cannot be displayed!\\n\\nPlease try again..."), getParent());
        }
    }

    protected String getOriginalValue() {
        return originalValue;
    }

    protected List<String> getItemList() {
        return itemList;
    }

    protected void setListControl(ScrollableListControl scrollList) {
        this.scrollList = scrollList;
    }

    protected void appendListControl() {
        setListControl(addList(
                new ScrollableListControl(
                        getGameInstance(), this,
                        getScreenWidth(), getScreenHeight(),
                        32, getScreenHeight() - 32,
                        getItemList(), getOriginalValue()
                )
        ));
    }

    protected void appendControls() {
        proceedButton = addControl(
                new ExtendedButtonControl(
                        (getScreenWidth() - 101), (getScreenHeight() - 26),
                        95, 20,
                        "Back",
                        () -> {
                            if (allowContinuing && scrollList.currentValue != null) {
                                if (getOriginalValue() != null) {
                                    if (!scrollList.currentValue.equals(getOriginalValue())) {
                                        if (onUpdatedCallback != null) {
                                            onUpdatedCallback.accept(attributeName, scrollList.currentValue);
                                            openScreen(getParent());
                                        } else {
                                            openScreen(new MessageGui(CoreUtils.NAME, "This area is not implemented just yet!\\n\\nPlease check back later..."), getParent());
                                        }
                                    } else {
                                        openScreen(getParent());
                                    }
                                } else {
                                    if (onAdjustDynamicEntry != null) {
                                        onAdjustDynamicEntry.accept(scrollList.currentValue, getParent());
                                    } else {
                                        openScreen(new MessageGui(CoreUtils.NAME, "This area is not implemented just yet!\\n\\nPlease check back later..."), getParent());
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
                            "Add New",
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
    }

    protected List<String> getFilteredList(final String searchTerm, final List<String> originalItems) {
        final List<String> modifiedList = StringUtils.newArrayList();

        for (String item : originalItems) {
            if (!modifiedList.contains(item) && item.toLowerCase().contains(searchTerm.toLowerCase())) {
                modifiedList.add(item);
                break;
            }
        }
        return modifiedList;
    }

    @Override
    public void preRender() {
        final List<String> originalItems = StringUtils.newArrayList(originalList);

        if (!searchBox.getControlMessage().isEmpty()) {
            if (!searchBox.getControlMessage().equals(searchTerm)) {
                searchTerm = searchBox.getControlMessage();
                itemList = getFilteredList(searchTerm, originalItems);
            }
        } else {
            itemList = originalItems;
        }

        if (!getItemList().equals(originalItems) && !getItemList().contains(scrollList.currentValue)) {
            if (getOriginalValue() != null && getItemList().contains(getOriginalValue())) {
                scrollList.currentValue = getOriginalValue();
            } else {
                scrollList.currentValue = null;
            }
        } else if (scrollList.currentValue == null && getOriginalValue() != null) {
            scrollList.currentValue = getOriginalValue();
        }

        scrollList.setList(getItemList());

        proceedButton.setControlMessage(
                allowContinuing && scrollList.currentValue != null &&
                        ((getOriginalValue() != null && !scrollList.currentValue.equals(getOriginalValue())) || (StringUtils.isNullOrEmpty(getOriginalValue()))) ?
                        "Continue" : "Back"
        );

        super.preRender();
    }

    @Override
    public void renderStringData() {
        super.renderStringData();

        final int renderY = searchBox.getBottom() - (searchBox.getControlHeight() / 2) - (getFontHeight() / 2);

        renderScrollingString(
                "Search:",
                2, renderY,
                58, renderY + getFontHeight(),
                0xFFFFFF
        );
    }
}

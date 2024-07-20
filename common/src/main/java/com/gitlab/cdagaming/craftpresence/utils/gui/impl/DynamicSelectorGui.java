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

import com.gitlab.cdagaming.craftpresence.utils.gui.controls.DynamicScrollableList;
import com.gitlab.cdagaming.unilib.utils.gui.impl.SelectorGui;
import io.github.cdagaming.unicore.utils.StringUtils;
import net.minecraft.client.gui.GuiScreen;

import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

public class DynamicSelectorGui extends SelectorGui {
    private final DynamicScrollableList.RenderType renderType;
    private DynamicScrollableList dynamicList;
    private DynamicScrollableList.IdentifierType identifierType = DynamicScrollableList.IdentifierType.None;

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param mainTitle            The Main Screen Title
     * @param list                 The list to interpret
     * @param currentValue         The initial value to select within the list
     * @param attributeName        The attribute name to interpret following selection
     * @param allowContinuing      Whether to allow the "continue" button functionality
     * @param allowDynamicEditing  Whether to allow adding new entries to the list
     * @param renderType           The {@link DynamicScrollableList.RenderType} for the list, adjusting its look and feel
     * @param onUpdatedCallback    The callback to trigger when continuing following selection
     * @param onAdjustDynamicEntry The callback to trigger when adjusting a dynamic entry
     */
    public DynamicSelectorGui(String mainTitle, List<String> list, String currentValue, String attributeName, boolean allowContinuing, boolean allowDynamicEditing, DynamicScrollableList.RenderType renderType, BiConsumer<String, String> onUpdatedCallback, BiConsumer<String, GuiScreen> onAdjustDynamicEntry) {
        super(mainTitle, list, currentValue, attributeName, allowContinuing, allowDynamicEditing, onUpdatedCallback, onAdjustDynamicEntry);
        this.renderType = renderType;
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
     * @param renderType           The {@link DynamicScrollableList.RenderType} for the list, adjusting its look and feel
     * @param onUpdatedCallback    The callback to trigger when continuing following selection
     * @param onAdjustDynamicEntry The callback to trigger when adjusting a dynamic entry
     */
    public DynamicSelectorGui(String mainTitle, Set<String> list, String currentValue, String attributeName, boolean allowContinuing, boolean allowDynamicEditing, DynamicScrollableList.RenderType renderType, BiConsumer<String, String> onUpdatedCallback, BiConsumer<String, GuiScreen> onAdjustDynamicEntry) {
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
     * @param renderType           The {@link DynamicScrollableList.RenderType} for the list, adjusting its look and feel
     * @param onAdjustDynamicEntry The callback to trigger when adjusting a dynamic entry
     */
    public DynamicSelectorGui(String mainTitle, List<String> list, String currentValue, String attributeName, boolean allowContinuing, boolean allowDynamicEditing, DynamicScrollableList.RenderType renderType, BiConsumer<String, GuiScreen> onAdjustDynamicEntry) {
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
     * @param renderType           The {@link DynamicScrollableList.RenderType} for the list, adjusting its look and feel
     * @param onAdjustDynamicEntry The callback to trigger when adjusting a dynamic entry
     */
    public DynamicSelectorGui(String mainTitle, Set<String> list, String currentValue, String attributeName, boolean allowContinuing, boolean allowDynamicEditing, DynamicScrollableList.RenderType renderType, BiConsumer<String, GuiScreen> onAdjustDynamicEntry) {
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
    public DynamicSelectorGui(String mainTitle, List<String> list, String currentValue, String attributeName, boolean allowContinuing, boolean allowDynamicEditing, BiConsumer<String, GuiScreen> onAdjustDynamicEntry) {
        this(mainTitle, list, currentValue, attributeName, allowContinuing, allowDynamicEditing, DynamicScrollableList.RenderType.None, onAdjustDynamicEntry);
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
    public DynamicSelectorGui(String mainTitle, Set<String> list, String currentValue, String attributeName, boolean allowContinuing, boolean allowDynamicEditing, BiConsumer<String, GuiScreen> onAdjustDynamicEntry) {
        this(mainTitle, list, currentValue, attributeName, allowContinuing, allowDynamicEditing, DynamicScrollableList.RenderType.None, onAdjustDynamicEntry);
    }

    @Override
    protected void appendListControl() {
        dynamicList = addControl(
                new DynamicScrollableList(
                        getGameInstance(), this,
                        getScreenWidth(), getScreenHeight() - 64,
                        32, getScreenHeight() - 32,
                        getItemList(), getOriginalValue(),
                        renderType
                ).setIdentifierType(identifierType)
        );
        setListControl(dynamicList);
    }

    @Override
    protected List<String> getFilteredList(String searchTerm, List<String> originalItems) {
        final List<String> modifiedList = StringUtils.newArrayList();

        for (String item : originalItems) {
            if (!modifiedList.contains(item)) {
                final List<String> entriesToCheck = StringUtils.newArrayList(item);
                if (dynamicList.getEntryAliases().containsKey(item)) {
                    entriesToCheck.add(dynamicList.getEntryAliases().get(item));
                }

                for (String entry : entriesToCheck) {
                    if (entry.toLowerCase().contains(searchTerm.toLowerCase())) {
                        modifiedList.add(item);
                        break;
                    }
                }
            }
        }
        return modifiedList;
    }

    @Override
    public void postRender() {
        if (dynamicList.currentHoverText != null && !dynamicList.currentHoverText.isEmpty()) {
            drawMultiLineString(dynamicList.currentHoverText);
            dynamicList.currentHoverText.clear();
        }

        super.postRender();
    }

    /**
     * Sets the Identifier Type to be linked to this Render Type
     *
     * @param type The {@link DynamicScrollableList.IdentifierType} to interpret
     * @return the modified instance
     */
    public DynamicSelectorGui setIdentifierType(final DynamicScrollableList.IdentifierType type) {
        this.identifierType = type;
        return this;
    }
}

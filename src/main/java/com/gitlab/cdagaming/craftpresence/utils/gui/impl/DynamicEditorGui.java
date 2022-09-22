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
import com.gitlab.cdagaming.craftpresence.impl.TupleConsumer;
import com.gitlab.cdagaming.craftpresence.utils.StringUtils;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ExtendedButtonControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ExtendedTextControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.integrations.ExtendedScreen;
import net.minecraft.client.gui.GuiScreen;

public class DynamicEditorGui extends ExtendedScreen {
    private final TupleConsumer<DynamicEditorGui, String, String> onAdjustEntry, onRemoveEntry;
    private final PairConsumer<String, DynamicEditorGui> onAdjustInit, onNewInit, onSpecificCallback, onHoverPrimaryCallback, onHoverSecondaryCallback;
    public String attributeName, primaryMessage, secondaryMessage, originalPrimaryMessage, originalSecondaryMessage, mainTitle, primaryText, secondaryText;
    public boolean isNewValue, isDefaultValue, willRenderSecondaryInput, overrideSecondaryRender = false;
    public int maxPrimaryLength = -1, maxSecondaryLength = -1;
    private ExtendedButtonControl proceedButton;
    private ExtendedTextControl primaryInput, secondaryInput;
    private String removeMessage;
    private int controlIndex;

    public DynamicEditorGui(GuiScreen parentScreen, String attributeName, PairConsumer<String, DynamicEditorGui> onNewInit, PairConsumer<String, DynamicEditorGui> onAdjustInit, TupleConsumer<DynamicEditorGui, String, String> onAdjustEntry, TupleConsumer<DynamicEditorGui, String, String> onRemoveEntry, PairConsumer<String, DynamicEditorGui> onSpecificCallback, PairConsumer<String, DynamicEditorGui> onHoverPrimaryCallback, PairConsumer<String, DynamicEditorGui> onHoverSecondaryCallback) {
        super(parentScreen);
        this.attributeName = attributeName;
        this.isNewValue = StringUtils.isNullOrEmpty(attributeName);
        this.isDefaultValue = !StringUtils.isNullOrEmpty(attributeName) && "default".equals(attributeName);

        this.onNewInit = onNewInit;
        this.onAdjustInit = onAdjustInit;
        this.onAdjustEntry = onAdjustEntry;
        this.onRemoveEntry = onRemoveEntry;
        this.onSpecificCallback = onSpecificCallback;
        this.onHoverPrimaryCallback = onHoverPrimaryCallback;
        this.onHoverSecondaryCallback = onHoverSecondaryCallback;

        this.controlIndex = 1;
    }

    public DynamicEditorGui(GuiScreen parentScreen, String attributeName, PairConsumer<String, DynamicEditorGui> onNewInit, PairConsumer<String, DynamicEditorGui> onAdjustInit, TupleConsumer<DynamicEditorGui, String, String> onAdjustEntry, TupleConsumer<DynamicEditorGui, String, String> onRemoveEntry, PairConsumer<String, DynamicEditorGui> onSpecificCallback, PairConsumer<String, DynamicEditorGui> onHoverPrimaryCallback) {
        this(parentScreen, attributeName, onNewInit, onAdjustInit, onAdjustEntry, onRemoveEntry, onSpecificCallback, onHoverPrimaryCallback, (name, screenInstance) -> {
            CraftPresence.GUIS.drawMultiLineString(StringUtils.splitTextByNewLine(ModUtils.TRANSLATOR.translate("gui.config.message.hover.value.name")), screenInstance.getMouseX(), screenInstance.getMouseY(), screenInstance.width, screenInstance.height, screenInstance.getWrapWidth(), screenInstance.getFontRenderer(), true);
        });
    }

    public DynamicEditorGui(GuiScreen parentScreen, String attributeName, PairConsumer<String, DynamicEditorGui> onNewInit, PairConsumer<String, DynamicEditorGui> onAdjustInit, TupleConsumer<DynamicEditorGui, String, String> onAdjustEntry, TupleConsumer<DynamicEditorGui, String, String> onRemoveEntry, PairConsumer<String, DynamicEditorGui> onSpecificCallback) {
        this(parentScreen, attributeName, onNewInit, onAdjustInit, onAdjustEntry, onRemoveEntry, onSpecificCallback, (name, screenInstance) -> {
            CraftPresence.GUIS.drawMultiLineString(StringUtils.splitTextByNewLine(ModUtils.TRANSLATOR.translate("gui.config.message.hover.value.message")), screenInstance.getMouseX(), screenInstance.getMouseY(), screenInstance.width, screenInstance.height, screenInstance.getWrapWidth(), screenInstance.getFontRenderer(), true);
        });
    }

    @Override
    public void initializeUi() {
        if (isNewValue) {
            mainTitle = ModUtils.TRANSLATOR.translate("gui.config.title.editor.add.new");
            if (onNewInit != null) {
                onNewInit.accept(attributeName, this);
            }
        } else {
            if (onAdjustInit != null) {
                onAdjustInit.accept(attributeName, this);
            }
        }

        this.willRenderSecondaryInput = isNewValue || overrideSecondaryRender;

        if (StringUtils.isNullOrEmpty(primaryText)) {
            primaryText = ModUtils.TRANSLATOR.translate("gui.config.message.editor.message");
        }
        if (StringUtils.isNullOrEmpty(secondaryText)) {
            secondaryText = ModUtils.TRANSLATOR.translate("gui.config.message.editor.value.name");
        }

        removeMessage = ModUtils.TRANSLATOR.translate("gui.config.message.remove", primaryText.replaceAll("[^a-zA-Z0-9]", ""));

        primaryInput = addControl(
                new ExtendedTextControl(
                        getFontRenderer(),
                        (width / 2) + 3, CraftPresence.GUIS.getButtonY(controlIndex++),
                        180, 20
                )
        );
        if (maxPrimaryLength > 0) {
            primaryInput.setMaxStringLength(maxPrimaryLength);
        }
        if (!StringUtils.isNullOrEmpty(primaryMessage)) {
            primaryInput.setText(primaryMessage);
        }

        if (onSpecificCallback != null && !isNewValue) {
            // Adding Specific Icon Button
            addControl(
                    new ExtendedButtonControl(
                            (width / 2) - 90, CraftPresence.GUIS.getButtonY(controlIndex++),
                            180, 20,
                            ModUtils.TRANSLATOR.translate("gui.config.message.button.icon.change"),
                            () -> onSpecificCallback.accept(attributeName, this)
                    )
            );
        }
        if (willRenderSecondaryInput) {
            secondaryInput = addControl(
                    new ExtendedTextControl(
                            getFontRenderer(),
                            (width / 2) + 3, CraftPresence.GUIS.getButtonY(controlIndex++),
                            180, 20
                    )
            );
            if (maxSecondaryLength > 0) {
                secondaryInput.setMaxStringLength(maxSecondaryLength);
            }
            if (!StringUtils.isNullOrEmpty(secondaryMessage)) {
                secondaryInput.setText(secondaryMessage);
            }
        }

        proceedButton = addControl(
                new ExtendedButtonControl(
                        (width / 2) - 90, (height - 30),
                        180, 20,
                        ModUtils.TRANSLATOR.translate("gui.config.message.button.back"),
                        () -> {
                            if (StringUtils.isNullOrEmpty(attributeName) && willRenderSecondaryInput && !StringUtils.isNullOrEmpty(secondaryInput.getText())) {
                                attributeName = secondaryInput.getText();
                            }
                            if (!primaryInput.getText().equals(primaryMessage) ||
                                    (willRenderSecondaryInput && !StringUtils.isNullOrEmpty(secondaryInput.getText()) && (!primaryInput.getText().equals(primaryMessage) || !secondaryInput.getText().equals(secondaryMessage))) ||
                                    (isDefaultValue && !StringUtils.isNullOrEmpty(primaryInput.getText()) && !primaryInput.getText().equals(primaryMessage))) {
                                if (onAdjustEntry != null) {
                                    onAdjustEntry.accept(this, attributeName, primaryInput.getText());
                                }
                            }
                            if (StringUtils.isNullOrEmpty(primaryInput.getText()) ||
                                    (primaryInput.getText().equalsIgnoreCase(originalPrimaryMessage) && !primaryMessage.equals(originalPrimaryMessage) && !isDefaultValue)) {
                                if (onRemoveEntry != null) {
                                    onRemoveEntry.accept(this, attributeName, primaryInput.getText());
                                }
                            }
                            CraftPresence.GUIS.openScreen(parentScreen);
                        },
                        () -> {
                            if (!proceedButton.isControlEnabled()) {
                                CraftPresence.GUIS.drawMultiLineString(
                                        StringUtils.splitTextByNewLine(
                                                ModUtils.TRANSLATOR.translate("gui.config.message.hover.empty.default")
                                        ),
                                        getMouseX(), getMouseY(),
                                        width, height,
                                        getWrapWidth(),
                                        getFontRenderer(),
                                        true
                                );
                            }
                        }
                )
        );

        super.initializeUi();
    }

    @Override
    public void preRender() {
        renderString(mainTitle, (width / 2f) - (StringUtils.getStringWidth(mainTitle) / 2f), 15, 0xFFFFFF);
        renderString(primaryText, (width / 2f) - 130, primaryInput.getControlPosY() + 5, 0xFFFFFF);
        if (willRenderSecondaryInput) {
            renderString(secondaryText, (width / 2f) - 130, secondaryInput.getControlPosY() + 5, 0xFFFFFF);
        }

        if (!isNewValue && !isDefaultValue) {
            renderString(removeMessage, (width / 2f) - (StringUtils.getStringWidth(removeMessage) / 2f), (height - 45), 0xFFFFFF);
        }

        proceedButton.setControlMessage(
                !primaryInput.getText().equals(primaryMessage) ||
                        (willRenderSecondaryInput && !StringUtils.isNullOrEmpty(secondaryInput.getText()) && (!primaryInput.getText().equals(primaryMessage) || !secondaryInput.getText().equals(secondaryMessage))) ||
                        (isDefaultValue && !StringUtils.isNullOrEmpty(primaryInput.getText()) && !primaryInput.getText().equals(primaryMessage)) ?
                        ModUtils.TRANSLATOR.translate("gui.config.message.button.continue") : ModUtils.TRANSLATOR.translate("gui.config.message.button.back")
        );

        proceedButton.setControlEnabled(!(StringUtils.isNullOrEmpty(primaryInput.getText()) && isDefaultValue));
    }

    @Override
    public void postRender() {
        final boolean isHoveringOverPrimary = CraftPresence.GUIS.isMouseOver(getMouseX(), getMouseY(), (width / 2f) - 130, primaryInput.getControlPosY() + 5, StringUtils.getStringWidth(primaryText), getFontHeight());
        final boolean isHoveringOverSecondary = willRenderSecondaryInput && CraftPresence.GUIS.isMouseOver(getMouseX(), getMouseY(), (width / 2f) - 130, secondaryInput.getControlPosY() + 5, StringUtils.getStringWidth(secondaryText), getFontHeight());
        // Hovering over Message Label
        if (isHoveringOverPrimary && onHoverPrimaryCallback != null) {
            onHoverPrimaryCallback.accept(attributeName, this);
        }

        // Hovering over Value Name Label
        if (isHoveringOverSecondary && onHoverSecondaryCallback != null) {
            onHoverSecondaryCallback.accept(attributeName, this);
        }
    }
}

/*
 * MIT License
 *
 * Copyright (c) 2018 - 2025 CDAGaming (cstack2011@yahoo.com)
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
import com.gitlab.cdagaming.craftpresence.core.config.element.ModuleData;
import com.gitlab.cdagaming.unilib.utils.gui.controls.ExtendedButtonControl;
import com.gitlab.cdagaming.unilib.utils.gui.controls.ExtendedTextControl;
import com.gitlab.cdagaming.unilib.utils.gui.integrations.ExtendedScreen;
import com.gitlab.cdagaming.unilib.utils.gui.integrations.ScrollPane;
import com.gitlab.cdagaming.unilib.utils.gui.widgets.TextWidget;
import io.github.cdagaming.unicore.utils.StringUtils;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

@SuppressWarnings("DuplicatedCode")
public class DynamicEditorGui extends ExtendedScreen {
    private final Consumer<DynamicEditorGui> onAdjustEntry, onRemoveEntry;
    private final BiConsumer<String, DynamicEditorGui> onAdjustInit, onNewInit, onHoverPrimaryCallback, onHoverSecondaryCallback, onSpecificCallback;
    public String attributeName, primaryMessage, secondaryMessage, originalPrimaryMessage, originalSecondaryMessage, primaryText, secondaryText;
    public boolean initialized = false, isNewValue, isDefaultValue = false, willRenderSecondaryInput, isModuleMode = false, hasChanged = false, overrideSecondaryRender = false, isPreliminaryData = false, isPresenceButton = false;
    public int maxPrimaryLength = -1, maxSecondaryLength = -1;
    public String resetText;
    public ModuleData defaultData, originalData, currentData;
    private ExtendedButtonControl proceedButton;
    private ExtendedTextControl primaryInput, secondaryInput;
    private TextWidget defaultIcon;

    public DynamicEditorGui(String attributeName, BiConsumer<String, DynamicEditorGui> onNewInit, BiConsumer<String, DynamicEditorGui> onAdjustInit, Consumer<DynamicEditorGui> onAdjustEntry, Consumer<DynamicEditorGui> onRemoveEntry, BiConsumer<String, DynamicEditorGui> onSpecificCallback, BiConsumer<String, DynamicEditorGui> onHoverPrimaryCallback, BiConsumer<String, DynamicEditorGui> onHoverSecondaryCallback) {
        super();
        this.attributeName = attributeName;
        this.isNewValue = StringUtils.isNullOrEmpty(attributeName);

        this.onNewInit = onNewInit;
        this.onAdjustInit = onAdjustInit;
        this.onAdjustEntry = onAdjustEntry;
        this.onRemoveEntry = onRemoveEntry;
        this.onSpecificCallback = onSpecificCallback;
        this.onHoverPrimaryCallback = onHoverPrimaryCallback;
        this.onHoverSecondaryCallback = onHoverSecondaryCallback;
    }

    public DynamicEditorGui(String attributeName, BiConsumer<String, DynamicEditorGui> onNewInit, BiConsumer<String, DynamicEditorGui> onAdjustInit, Consumer<DynamicEditorGui> onAdjustEntry, Consumer<DynamicEditorGui> onRemoveEntry, BiConsumer<String, DynamicEditorGui> onSpecificCallback, BiConsumer<String, DynamicEditorGui> onHoverPrimaryCallback) {
        this(attributeName, onNewInit, onAdjustInit, onAdjustEntry, onRemoveEntry, onSpecificCallback, onHoverPrimaryCallback, (name, screenInstance) ->
                screenInstance.drawMultiLineString(
                        StringUtils.splitTextByNewLine(
                                Constants.TRANSLATOR.translate("gui.config.message.hover.value.name")
                        )
                ));
    }

    public DynamicEditorGui(String attributeName, BiConsumer<String, DynamicEditorGui> onNewInit, BiConsumer<String, DynamicEditorGui> onAdjustInit, Consumer<DynamicEditorGui> onAdjustEntry, Consumer<DynamicEditorGui> onRemoveEntry, BiConsumer<String, DynamicEditorGui> onSpecificCallback) {
        this(attributeName, onNewInit, onAdjustInit, onAdjustEntry, onRemoveEntry, onSpecificCallback, (name, screenInstance) ->
                screenInstance.drawMultiLineString(
                        StringUtils.splitTextByNewLine(
                                Constants.TRANSLATOR.translate("gui.config.message.hover.value.message")
                        )
                ));
    }

    @Override
    public void initializeUi() {
        int controlIndex = 0;
        if (!isLoaded() && !initialized) {
            resetText = Constants.TRANSLATOR.translate("gui.config.message.button.remove");
            if (isNewValue) {
                setScreenTitle(Constants.TRANSLATOR.translate("gui.config.title.editor.add.new"));
                if (onNewInit != null) {
                    onNewInit.accept(attributeName, this);
                }
            } else {
                if (onAdjustInit != null) {
                    onAdjustInit.accept(attributeName, this);
                }
            }
            initialized = true;
        }

        final ScrollPane childFrame = addControl(
                new ScrollPane(
                        0, 32,
                        getScreenWidth(), getScreenHeight() - 64
                )
        );

        this.isModuleMode = defaultData != null || currentData != null;
        this.willRenderSecondaryInput = isNewValue || overrideSecondaryRender;

        if (StringUtils.isNullOrEmpty(primaryText)) {
            primaryText = Constants.TRANSLATOR.translate("gui.config.message.editor.message");
        }
        if (StringUtils.isNullOrEmpty(secondaryText)) {
            secondaryText = Constants.TRANSLATOR.translate("gui.config.message.editor.value.name");
        }

        if (isNewValue || isPreliminaryData) {
            if (isPreliminaryData && !StringUtils.isNullOrEmpty(attributeName)) {
                setScreenTitle(Constants.TRANSLATOR.translate("gui.config.title.editor.add.new.prefilled", attributeName));
            }
            if (isModuleMode && defaultData != null && currentData == null) {
                currentData = new ModuleData(defaultData);
            }
        } else if (!isDefaultValue) {
            // Adding Reset Button
            addControl(
                    new ExtendedButtonControl(
                            6, (getScreenHeight() - 26),
                            95, 20,
                            resetText,
                            () -> {
                                if (onRemoveEntry != null) {
                                    onRemoveEntry.accept(this);
                                }
                                openScreen(getParent());
                            }
                    )
            );
        }

        if (isModuleMode && originalData == null) {
            originalData = new ModuleData(currentData);
        }

        primaryInput = childFrame.addControl(
                new TextWidget(
                        getFontRenderer(),
                        getButtonY(controlIndex++),
                        180, 20,
                        primaryText,
                        () -> {
                            if (onHoverPrimaryCallback != null) {
                                onHoverPrimaryCallback.accept(attributeName, this);
                            }
                        }
                )
        );
        if (maxPrimaryLength > 0) {
            primaryInput.setControlMaxLength(maxPrimaryLength);
        }
        if (!StringUtils.isNullOrEmpty(primaryMessage)) {
            primaryInput.setControlMessage(primaryMessage);
        }

        if (willRenderSecondaryInput) {
            secondaryInput = childFrame.addControl(
                    new TextWidget(
                            getFontRenderer(),
                            getButtonY(controlIndex++),
                            180, 20,
                            secondaryText,
                            () -> {
                                if (onHoverSecondaryCallback != null) {
                                    onHoverSecondaryCallback.accept(attributeName, this);
                                }
                            }
                    )
            );
            if (maxSecondaryLength > 0) {
                secondaryInput.setControlMaxLength(maxSecondaryLength);
            }
            if (!StringUtils.isNullOrEmpty(secondaryMessage)) {
                secondaryInput.setControlMessage(secondaryMessage);
            }
        }

        if (onSpecificCallback != null) {
            // Adding Specific Icon Button
            defaultIcon = childFrame.addControl(
                    new TextWidget(
                            getFontRenderer(),
                            getButtonY(controlIndex++),
                            147, 20,
                            () -> {
                                isPresenceButton = false;
                                onSpecificCallback.accept(defaultIcon.getControlMessage(), this);
                            },
                            Constants.TRANSLATOR.translate("gui.config.message.editor.icon.change")
                    )
            );
            ConfigurationGui.addIconSelector(this, childFrame, () -> defaultIcon,
                    (attributeName, currentValue) -> {
                        isPresenceButton = false;
                        onSpecificCallback.accept(currentValue, this);
                    }
            );
            defaultIcon.setControlMessage(currentData.getIconOverride() != null ? currentData.getIconOverride() : "");

            // Adding Presence Editor Button
            childFrame.addControl(
                    new ExtendedButtonControl(
                            (getScreenWidth() / 2) - 90, getButtonY(controlIndex++),
                            180, 20,
                            Constants.TRANSLATOR.translate("gui.config.title.editor.presence"),
                            () -> {
                                isPresenceButton = true;
                                onSpecificCallback.accept(attributeName, this);
                            },
                            () -> drawMultiLineString(
                                    StringUtils.splitTextByNewLine(
                                            Constants.TRANSLATOR.translate("gui.config.message.hover.presence_editor")
                                    )
                            )
                    )
            );
        }

        proceedButton = addControl(
                new ExtendedButtonControl(
                        (getScreenWidth() / 2) - 90, (getScreenHeight() - 26),
                        180, 20,
                        Constants.TRANSLATOR.translate("gui.config.message.button.back"),
                        () -> {
                            if (StringUtils.isNullOrEmpty(attributeName) && willRenderSecondaryInput && !StringUtils.isNullOrEmpty(secondaryInput.getControlMessage())) {
                                attributeName = secondaryInput.getControlMessage();
                            }
                            if (isAdjusting() && onAdjustEntry != null) {
                                onAdjustEntry.accept(this);
                            }
                            openScreen(getParent());
                        },
                        () -> {
                            if (!proceedButton.isControlEnabled()) {
                                drawMultiLineString(
                                        StringUtils.splitTextByNewLine(
                                                Constants.TRANSLATOR.translate("gui.config.message.hover.empty.default")
                                        )
                                );
                            }
                        }
                )
        );

        super.initializeUi();
    }

    @Override
    public void preRender() {
        proceedButton.setControlMessage(Constants.TRANSLATOR.translate(
                isAdjusting() ?
                        "gui.config.message.button.continue" : "gui.config.message.button.back"
        ));

        proceedButton.setControlEnabled(isValidEntries());

        super.preRender();
    }

    /**
     * Retrieve the primary entry text
     *
     * @return the primary entry text
     */
    public String getPrimaryEntry() {
        return primaryInput.getControlMessage();
    }

    /**
     * Retrieve the secondary entry text
     *
     * @return the secondary entry text
     */
    public String getSecondaryEntry() {
        return willRenderSecondaryInput ? secondaryInput.getControlMessage() : attributeName;
    }

    /**
     * Whether the inputs in this screen classify as being adjusted
     *
     * @return {@link Boolean#TRUE} if we are doing an adjustment
     */
    private boolean isAdjusting() {
        final String primaryText = primaryInput != null ? getPrimaryEntry() : "";
        final boolean isPrimaryEmpty = StringUtils.isNullOrEmpty(primaryText);
        final String secondaryText = secondaryInput != null ? secondaryInput.getControlMessage() : "";
        final boolean isSecondaryEmpty = StringUtils.isNullOrEmpty(secondaryText);

        if (isModuleMode) {
            hasChanged = currentData.getData() != null && !currentData.getData().equals(originalData.getData());
            if (!hasChanged) {
                final String originalIcon = originalData.getIconOverride() != null ? originalData.getIconOverride() : "";
                final String currentIcon = currentData.getIconOverride() != null ? currentData.getIconOverride() : "";
                hasChanged = hasChanged || !currentIcon.equals(originalIcon);
            }
        }

        if (hasChanged) {
            return true;
        } else {
            if (willRenderSecondaryInput) {
                return !isSecondaryEmpty && (!primaryText.equals(primaryMessage) || !secondaryText.equals(secondaryMessage));
            } else if (isDefaultValue) {
                return !isPrimaryEmpty && !primaryText.equals(primaryMessage);
            } else {
                return !primaryText.equals(primaryMessage);
            }
        }
    }

    /**
     * Determines whether the inputs are considered valid
     *
     * @return {@link Boolean#TRUE} if inputs are valid
     */
    private boolean isValidEntries() {
        final String primaryText = primaryInput != null ? getPrimaryEntry() : "";
        final boolean isPrimaryEmpty = StringUtils.isNullOrEmpty(primaryText);

        if (isDefaultValue) {
            return !isPrimaryEmpty;
        } else {
            return true;
        }
    }
}

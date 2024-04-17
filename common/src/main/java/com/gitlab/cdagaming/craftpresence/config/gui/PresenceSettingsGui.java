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

package com.gitlab.cdagaming.craftpresence.config.gui;

import com.gitlab.cdagaming.craftpresence.CraftPresence;
import com.gitlab.cdagaming.craftpresence.config.Config;
import com.gitlab.cdagaming.craftpresence.core.Constants;
import com.gitlab.cdagaming.craftpresence.core.config.element.Button;
import com.gitlab.cdagaming.craftpresence.core.config.element.PresenceData;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.CheckBoxControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ExtendedButtonControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ScrollableListControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.impl.ConfigurationGui;
import com.gitlab.cdagaming.craftpresence.utils.gui.impl.DynamicEditorGui;
import com.gitlab.cdagaming.craftpresence.utils.gui.impl.SelectorGui;
import com.gitlab.cdagaming.craftpresence.utils.gui.widgets.TextWidget;
import io.github.cdagaming.unicore.utils.StringUtils;
import net.minecraft.client.gui.GuiScreen;

import java.util.function.Consumer;
import java.util.function.Supplier;

@SuppressWarnings("DuplicatedCode")
public class PresenceSettingsGui extends ConfigurationGui<PresenceData> {
    private final PresenceData DEFAULTS, INSTANCE, CURRENT;
    private final boolean isDefaultModule;
    private final Consumer<PresenceData> onChangedCallback;
    private final Supplier<PresenceData> syncSupplier;
    private TextWidget detailsFormat, gameStateFormat, largeImageFormat, smallImageFormat,
            smallImageKeyFormat, largeImageKeyFormat, startTimeFormat, endTimeFormat;
    private CheckBoxControl useAsMainCheckbox, enabledCheckbox;

    PresenceSettingsGui(GuiScreen parentScreen,
                        PresenceData moduleData, PresenceData defaultData,
                        Supplier<PresenceData> syncData, final boolean isDefault,
                        Consumer<PresenceData> changedCallback
    ) {
        super(parentScreen, "gui.config.title", "gui.config.title.presence_settings");
        DEFAULTS = defaultData;
        INSTANCE = moduleData.copy();
        CURRENT = moduleData;
        isDefaultModule = isDefault;
        syncSupplier = syncData;
        onChangedCallback = changedCallback;
    }

    PresenceSettingsGui(GuiScreen parentScreen,
                        PresenceData moduleData, PresenceData defaultData,
                        Supplier<PresenceData> syncData,
                        Consumer<PresenceData> changedCallback
    ) {
        this(parentScreen, moduleData, defaultData, syncData, false, changedCallback);
    }

    PresenceSettingsGui(GuiScreen parentScreen,
                        PresenceData moduleData, PresenceData defaultData,
                        Consumer<PresenceData> changedCallback
    ) {
        this(parentScreen, moduleData, defaultData, null, changedCallback);
    }

    @Override
    protected void appendControls() {
        super.appendControls();

        final int calc1 = (getScreenWidth() / 2) - 183;
        final int calc2 = (getScreenWidth() / 2) + 3;

        int controlIndex = 0;

        if (!isDefaultModule) {
            enabledCheckbox = childFrame.addControl(
                    new CheckBoxControl(
                            calc1, getButtonY(controlIndex),
                            "gui.config.name.display.enabled",
                            getInstanceData().enabled,
                            () -> getInstanceData().enabled = enabledCheckbox.isChecked(),
                            () -> drawMultiLineString(
                                    StringUtils.splitTextByNewLine(
                                            Constants.TRANSLATOR.translate("gui.config.comment.display.enabled")
                                    )
                            )
                    )
            );
            useAsMainCheckbox = childFrame.addControl(
                    new CheckBoxControl(
                            calc2, getButtonY(controlIndex),
                            "gui.config.name.display.use_as_main",
                            getInstanceData().useAsMain,
                            () -> getInstanceData().useAsMain = useAsMainCheckbox.isChecked(),
                            () -> drawMultiLineString(
                                    StringUtils.splitTextByNewLine(
                                            Constants.TRANSLATOR.translate("gui.config.comment.display.use_as_main")
                                    )
                            )
                    )
            );

            controlIndex++;
        }

        detailsFormat = childFrame.addControl(
                new TextWidget(
                        getFontRenderer(),
                        getButtonY(controlIndex++),
                        180, 20,
                        () -> getInstanceData().setDetails(detailsFormat.getControlMessage()),
                        "gui.config.name.display.details_message",
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        Constants.TRANSLATOR.translate("gui.config.message.presence.args.general",
                                                CraftPresence.CLIENT.generateArgumentMessage("general."))
                                )
                        )
                )
        );
        gameStateFormat = childFrame.addControl(
                new TextWidget(
                        getFontRenderer(),
                        getButtonY(controlIndex++),
                        180, 20,
                        () -> getInstanceData().setGameState(gameStateFormat.getControlMessage()),
                        "gui.config.name.display.game_state_message",
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        Constants.TRANSLATOR.translate("gui.config.message.presence.args.general",
                                                CraftPresence.CLIENT.generateArgumentMessage("general."))
                                )
                        )
                )
        );
        largeImageFormat = childFrame.addControl(
                new TextWidget(
                        getFontRenderer(),
                        getButtonY(controlIndex++),
                        180, 20,
                        () -> getInstanceData().largeImageText = largeImageFormat.getControlMessage(),
                        "gui.config.name.display.large_image_message",
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        Constants.TRANSLATOR.translate("gui.config.message.presence.args.general",
                                                CraftPresence.CLIENT.generateArgumentMessage("general."))
                                )
                        )
                )
        );
        smallImageFormat = childFrame.addControl(
                new TextWidget(
                        getFontRenderer(),
                        getButtonY(controlIndex++),
                        180, 20,
                        () -> getInstanceData().smallImageText = smallImageFormat.getControlMessage(),
                        "gui.config.name.display.small_image_message",
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        Constants.TRANSLATOR.translate("gui.config.message.presence.args.general",
                                                CraftPresence.CLIENT.generateArgumentMessage("general."))
                                )
                        )
                )
        );

        detailsFormat.setControlMessage(getInstanceData().details);
        gameStateFormat.setControlMessage(getInstanceData().gameState);
        largeImageFormat.setControlMessage(getInstanceData().largeImageText);
        smallImageFormat.setControlMessage(getInstanceData().smallImageText);

        smallImageKeyFormat = childFrame.addControl(
                new TextWidget(
                        getFontRenderer(),
                        getButtonY(controlIndex++),
                        147, 20,
                        () -> getInstanceData().smallImageKey = smallImageKeyFormat.getControlMessage(),
                        "gui.config.name.display.small_image_key",
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        Constants.TRANSLATOR.translate("gui.config.message.presence.args.icon",
                                                CraftPresence.CLIENT.generateArgumentMessage("general."))
                                )
                        )
                )
        );
        addIconSelector(childFrame, () -> smallImageKeyFormat,
                (attributeName, currentValue) -> getInstanceData().smallImageKey = currentValue
        );
        largeImageKeyFormat = childFrame.addControl(
                new TextWidget(
                        getFontRenderer(),
                        getButtonY(controlIndex++),
                        147, 20,
                        () -> getInstanceData().largeImageKey = largeImageKeyFormat.getControlMessage(),
                        "gui.config.name.display.large_image_key",
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        Constants.TRANSLATOR.translate("gui.config.message.presence.args.icon",
                                                CraftPresence.CLIENT.generateArgumentMessage("general."))
                                )
                        )
                )
        );
        addIconSelector(childFrame, () -> largeImageKeyFormat,
                (attributeName, currentValue) -> getInstanceData().largeImageKey = currentValue
        );

        smallImageKeyFormat.setControlMessage(getInstanceData().smallImageKey);
        largeImageKeyFormat.setControlMessage(getInstanceData().largeImageKey);

        startTimeFormat = childFrame.addControl(
                new TextWidget(
                        getFontRenderer(),
                        getButtonY(controlIndex++),
                        180, 20,
                        () -> getInstanceData().setStartTime(startTimeFormat.getControlMessage()),
                        "gui.config.name.display.start_timestamp",
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        Constants.TRANSLATOR.translate("gui.config.message.presence.args.general",
                                                CraftPresence.CLIENT.generateArgumentMessage("general."))
                                )
                        )
                )
        );
        endTimeFormat = childFrame.addControl(
                new TextWidget(
                        getFontRenderer(),
                        getButtonY(controlIndex++),
                        180, 20,
                        () -> getInstanceData().setEndTime(endTimeFormat.getControlMessage()),
                        "gui.config.name.display.end_timestamp",
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        Constants.TRANSLATOR.translate("gui.config.message.presence.args.general",
                                                CraftPresence.CLIENT.generateArgumentMessage("general."))
                                )
                        )
                )
        );

        startTimeFormat.setControlMessage(getInstanceData().startTimestamp);
        endTimeFormat.setControlMessage(getInstanceData().endTimestamp);

        // Adding Button Messages Button
        childFrame.addControl(
                new ExtendedButtonControl(
                        (getScreenWidth() / 2) - 90, getButtonY(controlIndex++),
                        180, 20,
                        "gui.config.name.display.button_messages",
                        () -> openScreen(
                                new SelectorGui(
                                        currentScreen,
                                        Constants.TRANSLATOR.translate("gui.config.title.selector.button"), CraftPresence.CLIENT.createButtonsList(getInstanceData().buttons),
                                        null, null,
                                        true, true, ScrollableListControl.RenderType.None,
                                        null,
                                        (currentValue, parentScreen) -> {
                                            // Event to occur when Setting Dynamic/Specific Data
                                            openScreen(
                                                    new DynamicEditorGui(
                                                            parentScreen, currentValue,
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when initializing new data
                                                                screenInstance.attributeName = "button_" + CraftPresence.CLIENT.createButtonsList(getInstanceData().buttons).size();
                                                                screenInstance.mainTitle = Constants.TRANSLATOR.translate("gui.config.title.editor.add.new.prefilled", screenInstance.attributeName);
                                                                screenInstance.primaryText = Constants.TRANSLATOR.translate("gui.config.message.editor.label");
                                                                screenInstance.secondaryText = Constants.TRANSLATOR.translate("gui.config.message.editor.url");
                                                                final Button defaultData = getInstanceData().buttons.get("default");
                                                                screenInstance.primaryMessage = screenInstance.originalPrimaryMessage = Config.getProperty(defaultData, "label") != null ? defaultData.label : "";
                                                                screenInstance.secondaryMessage = screenInstance.originalSecondaryMessage = Config.getProperty(defaultData, "url") != null ? defaultData.url : "";
                                                            },
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when initializing existing data
                                                                screenInstance.primaryText = Constants.TRANSLATOR.translate("gui.config.message.editor.label");
                                                                screenInstance.secondaryText = Constants.TRANSLATOR.translate("gui.config.message.editor.url");
                                                                screenInstance.overrideSecondaryRender = true;
                                                                screenInstance.mainTitle = Constants.TRANSLATOR.translate("gui.config.title.display.edit_specific_button", attributeName);
                                                                final Button defaultData = getInstanceData().buttons.get("default");
                                                                final Button currentData = getInstanceData().buttons.get(attributeName);
                                                                screenInstance.isPreliminaryData = currentData == null;
                                                                screenInstance.originalPrimaryMessage = Config.getProperty(defaultData, "label") != null ? defaultData.label : "";
                                                                screenInstance.originalSecondaryMessage = Config.getProperty(defaultData, "url") != null ? defaultData.url : "";
                                                                screenInstance.primaryMessage = Config.getProperty(currentData, "label") != null ? currentData.label : screenInstance.originalPrimaryMessage;
                                                                screenInstance.secondaryMessage = Config.getProperty(currentData, "url") != null ? currentData.url : screenInstance.originalSecondaryMessage;
                                                            },
                                                            (screenInstance, secondaryText, inputText) -> {
                                                                // Event to occur when adjusting set data
                                                                getInstanceData().addButton(screenInstance.attributeName, new Button(inputText, secondaryText));
                                                            },
                                                            (screenInstance, secondaryText, inputText) -> {
                                                                // Event to occur when removing set data
                                                                getInstanceData().removeButton(screenInstance.attributeName);
                                                            }, null,
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when Hovering over Primary Label
                                                                screenInstance.drawMultiLineString(
                                                                        StringUtils.splitTextByNewLine(
                                                                                Constants.TRANSLATOR.translate("gui.config.comment.display.button_messages")
                                                                        )
                                                                );
                                                            },
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when Hovering over Secondary Label
                                                                screenInstance.drawMultiLineString(
                                                                        StringUtils.splitTextByNewLine(
                                                                                Constants.TRANSLATOR.translate("gui.config.comment.display.button_messages")
                                                                        )
                                                                );
                                                            }
                                                    )
                                            );
                                        }
                                )
                        ),
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        Constants.TRANSLATOR.translate("gui.config.comment.display.button_messages")
                                )
                        )
                )
        );
    }

    @Override
    protected boolean allowedToReset() {
        return DEFAULTS != null;
    }

    @Override
    protected boolean allowedToSync() {
        return syncSupplier != null;
    }

    @Override
    protected PresenceData getInstanceData() {
        return INSTANCE;
    }

    @Override
    protected PresenceData getCurrentData() {
        return CURRENT;
    }

    @Override
    protected PresenceData getDefaultData() {
        return DEFAULTS;
    }

    @Override
    protected PresenceData getSyncData() {
        return syncSupplier.get();
    }

    @Override
    protected boolean setCurrentData(PresenceData data) {
        if (onChangedCallback != null && data != null && !getCurrentData().equals(data)) {
            onChangedCallback.accept(data);
            markAsChanged();
            return true;
        }
        return false;
    }
}

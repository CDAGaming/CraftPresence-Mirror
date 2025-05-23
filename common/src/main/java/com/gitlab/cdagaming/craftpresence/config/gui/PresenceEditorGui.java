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

package com.gitlab.cdagaming.craftpresence.config.gui;

import com.gitlab.cdagaming.craftpresence.CraftPresence;
import com.gitlab.cdagaming.craftpresence.core.Constants;
import com.gitlab.cdagaming.craftpresence.core.config.Config;
import com.gitlab.cdagaming.craftpresence.core.config.element.Button;
import com.gitlab.cdagaming.craftpresence.core.config.element.PresenceData;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.DynamicScrollableList;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.PresenceVisualizer;
import com.gitlab.cdagaming.craftpresence.utils.gui.impl.ConfigurationGui;
import com.gitlab.cdagaming.craftpresence.utils.gui.impl.DynamicEditorGui;
import com.gitlab.cdagaming.craftpresence.utils.gui.impl.DynamicSelectorGui;
import com.gitlab.cdagaming.unilib.utils.gui.controls.CheckBoxControl;
import com.gitlab.cdagaming.unilib.utils.gui.controls.ExtendedButtonControl;
import com.gitlab.cdagaming.unilib.utils.gui.widgets.ScrollableTextWidget;
import com.gitlab.cdagaming.unilib.utils.gui.widgets.TextWidget;
import com.jagrosh.discordipc.entities.ActivityType;
import com.jagrosh.discordipc.entities.PartyPrivacy;
import io.github.cdagaming.unicore.utils.StringUtils;

import java.util.function.Consumer;

public class PresenceEditorGui extends ConfigurationGui<PresenceData> {
    private final PresenceData DEFAULTS, INSTANCE, CURRENT;
    private final boolean isDefaultModule;
    private final Consumer<PresenceData> onChangedCallback;
    private final PresenceVisualizer visualizer;
    private TextWidget detailsFormat, gameStateFormat, largeImageFormat, smallImageFormat,
            smallImageKeyFormat, largeImageKeyFormat, startTimeFormat, endTimeFormat;
    private CheckBoxControl useAsMainCheckbox, enabledCheckbox;
    private ExtendedButtonControl activityTypeButton, partyPrivacyButton;
    private int currentActivityType = ActivityType.Playing.ordinal();
    private int currentPartyPrivacy = PartyPrivacy.Public.ordinal();

    PresenceEditorGui(PresenceData moduleData, PresenceData defaultData,
                      final boolean isDefault,
                      Consumer<PresenceData> changedCallback
    ) {
        super(
                Constants.TRANSLATOR.translate("gui.config.title"),
                Constants.TRANSLATOR.translate("gui.config.title.editor.presence")
        );
        DEFAULTS = defaultData;
        INSTANCE = moduleData.copy();
        CURRENT = moduleData;
        isDefaultModule = isDefault;
        onChangedCallback = changedCallback;
        visualizer = new PresenceVisualizer(this);
    }

    PresenceEditorGui(PresenceData moduleData, PresenceData defaultData,
                      Consumer<PresenceData> changedCallback
    ) {
        this(moduleData, defaultData, false, changedCallback);
    }

    @Override
    protected void appendControls() {
        super.appendControls();

        final int calc1 = (getScreenWidth() / 2) - 183;
        final int calc2 = (getScreenWidth() / 2) + 3;

        final int calc1Width = childFrame.getScreenWidth() - calc1;

        final String generalFieldsTitle = Constants.TRANSLATOR.translate("gui.config.message.editor.presence.general");
        final String largeImageTitle = Constants.TRANSLATOR.translate("gui.config.message.editor.presence.image.large");
        final String smallImageTitle = Constants.TRANSLATOR.translate("gui.config.message.editor.presence.image.small");
        final String extraFieldsTitle = Constants.TRANSLATOR.translate("gui.config.message.editor.presence.extra");

        int controlIndex = 0;

        // General Fields Section
        childFrame.addWidget(new ScrollableTextWidget(
                calc1, getButtonY(controlIndex++),
                calc1Width,
                generalFieldsTitle
        ));

        if (!isDefaultModule) {
            enabledCheckbox = childFrame.addControl(
                    new CheckBoxControl(
                            calc1, getButtonY(controlIndex),
                            Constants.TRANSLATOR.translate("gui.config.message.editor.presence.enabled"),
                            getInstanceData().enabled,
                            () -> getInstanceData().enabled = enabledCheckbox.isChecked(),
                            () -> drawMultiLineString(
                                    StringUtils.splitTextByNewLine(
                                            Constants.TRANSLATOR.translate("gui.config.message.hover.presence.enabled")
                                    )
                            )
                    )
            );
            useAsMainCheckbox = childFrame.addControl(
                    new CheckBoxControl(
                            calc2, getButtonY(controlIndex),
                            Constants.TRANSLATOR.translate("gui.config.message.editor.presence.use_as_main"),
                            getInstanceData().useAsMain,
                            () -> {
                                getInstanceData().useAsMain = useAsMainCheckbox.isChecked();
                                reloadUi(); // Reload for Visualizer Setup
                            },
                            () -> drawMultiLineString(
                                    StringUtils.splitTextByNewLine(
                                            Constants.TRANSLATOR.translate("gui.config.message.hover.presence.use_as_main")
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
                        Constants.TRANSLATOR.translate("gui.config.message.editor.presence.details"),
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
                        Constants.TRANSLATOR.translate("gui.config.message.editor.presence.game_state"),
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

        // Large Image Section
        childFrame.addWidget(new ScrollableTextWidget(
                calc1, getButtonY(controlIndex++),
                calc1Width,
                largeImageTitle
        ));

        largeImageFormat = childFrame.addControl(
                new TextWidget(
                        getFontRenderer(),
                        getButtonY(controlIndex++),
                        180, 20,
                        () -> getInstanceData().largeImageText = largeImageFormat.getControlMessage(),
                        Constants.TRANSLATOR.translate("gui.config.message.editor.message"),
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        Constants.TRANSLATOR.translate("gui.config.message.presence.args.general",
                                                CraftPresence.CLIENT.generateArgumentMessage("general."))
                                )
                        )
                )
        );
        largeImageKeyFormat = childFrame.addControl(
                new TextWidget(
                        getFontRenderer(),
                        getButtonY(controlIndex++),
                        147, 20,
                        () -> getInstanceData().largeImageKey = largeImageKeyFormat.getControlMessage(),
                        Constants.TRANSLATOR.translate("gui.config.message.editor.icon.change"),
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

        largeImageFormat.setControlMessage(getInstanceData().largeImageText);
        largeImageKeyFormat.setControlMessage(getInstanceData().largeImageKey);

        // Small Image Section
        childFrame.addWidget(new ScrollableTextWidget(
                calc1, getButtonY(controlIndex++),
                calc1Width,
                smallImageTitle
        ));

        smallImageFormat = childFrame.addControl(
                new TextWidget(
                        getFontRenderer(),
                        getButtonY(controlIndex++),
                        180, 20,
                        () -> getInstanceData().smallImageText = smallImageFormat.getControlMessage(),
                        Constants.TRANSLATOR.translate("gui.config.message.editor.message"),
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        Constants.TRANSLATOR.translate("gui.config.message.presence.args.general",
                                                CraftPresence.CLIENT.generateArgumentMessage("general."))
                                )
                        )
                )
        );
        smallImageKeyFormat = childFrame.addControl(
                new TextWidget(
                        getFontRenderer(),
                        getButtonY(controlIndex++),
                        147, 20,
                        () -> getInstanceData().smallImageKey = smallImageKeyFormat.getControlMessage(),
                        Constants.TRANSLATOR.translate("gui.config.message.editor.icon.change"),
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

        smallImageFormat.setControlMessage(getInstanceData().smallImageText);
        smallImageKeyFormat.setControlMessage(getInstanceData().smallImageKey);

        // Extra Fields Section
        childFrame.addWidget(new ScrollableTextWidget(
                calc1, getButtonY(controlIndex),
                calc2 - calc1,
                extraFieldsTitle
        ));

        // Adding Button Editor Button
        childFrame.addControl(
                new ExtendedButtonControl(
                        calc2, getButtonY(controlIndex),
                        180, 20,
                        Constants.TRANSLATOR.translate("gui.config.message.editor.presence.button_editor"),
                        () -> openScreen(
                                new DynamicSelectorGui(
                                        Constants.TRANSLATOR.translate("gui.config.title.selector.button"), getInstanceData().buttons.keySet(),
                                        null, null,
                                        true, true, DynamicScrollableList.RenderType.None,
                                        null,
                                        (currentValue, parentScreen) -> {
                                            // Event to occur when Setting Dynamic/Specific Data
                                            openScreen(
                                                    new DynamicEditorGui(
                                                            currentValue,
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when initializing new data
                                                                screenInstance.attributeName = "button_" + getInstanceData().buttons.size();
                                                                screenInstance.setScreenTitle(Constants.TRANSLATOR.translate("gui.config.title.editor.add.new.prefilled", screenInstance.attributeName));
                                                                screenInstance.primaryText = Constants.TRANSLATOR.translate("gui.config.message.editor.label");
                                                                screenInstance.secondaryText = Constants.TRANSLATOR.translate("gui.config.message.editor.url");
                                                                screenInstance.primaryMessage = "";
                                                                screenInstance.secondaryMessage = "";
                                                            },
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when initializing existing data
                                                                screenInstance.primaryText = Constants.TRANSLATOR.translate("gui.config.message.editor.label");
                                                                screenInstance.secondaryText = Constants.TRANSLATOR.translate("gui.config.message.editor.url");
                                                                screenInstance.overrideSecondaryRender = true;
                                                                screenInstance.setScreenTitle(Constants.TRANSLATOR.translate("gui.config.title.display.edit_specific_button", attributeName));
                                                                final Button currentData = getInstanceData().buttons.get(attributeName);
                                                                screenInstance.isPreliminaryData = currentData == null;
                                                                screenInstance.originalPrimaryMessage = "";
                                                                screenInstance.originalSecondaryMessage = "";
                                                                screenInstance.primaryMessage = Config.getProperty(currentData, "label") != null ? currentData.label : screenInstance.originalPrimaryMessage;
                                                                screenInstance.secondaryMessage = Config.getProperty(currentData, "url") != null ? currentData.url : screenInstance.originalSecondaryMessage;
                                                            },
                                                            (screenInstance) -> {
                                                                // Event to occur when adjusting set data
                                                                final String secondaryText = screenInstance.getSecondaryEntry();
                                                                final String inputText = screenInstance.getPrimaryEntry();

                                                                getInstanceData().addButton(screenInstance.attributeName, new Button(inputText, secondaryText));
                                                            },
                                                            (screenInstance) -> {
                                                                // Event to occur when removing set data
                                                                getInstanceData().removeButton(screenInstance.attributeName);
                                                            }, null,
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when Hovering over Primary Label
                                                                screenInstance.drawMultiLineString(
                                                                        StringUtils.splitTextByNewLine(
                                                                                Constants.TRANSLATOR.translate("gui.config.message.hover.presence.button.label")
                                                                        )
                                                                );
                                                            },
                                                            (attributeName, screenInstance) -> {
                                                                // Event to occur when Hovering over Secondary Label
                                                                screenInstance.drawMultiLineString(
                                                                        StringUtils.splitTextByNewLine(
                                                                                Constants.TRANSLATOR.translate("gui.config.message.hover.presence.button.url")
                                                                        )
                                                                );
                                                            }
                                                    ), parentScreen
                                            );
                                        }
                                )
                        ),
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        Constants.TRANSLATOR.translate("gui.config.message.hover.presence.button_editor")
                                )
                        )
                )
        );

        controlIndex++;

        startTimeFormat = childFrame.addControl(
                new TextWidget(
                        getFontRenderer(),
                        getButtonY(controlIndex++),
                        180, 20,
                        () -> getInstanceData().setStartTime(startTimeFormat.getControlMessage()),
                        Constants.TRANSLATOR.translate("gui.config.message.editor.presence.start_timestamp"),
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
                        Constants.TRANSLATOR.translate("gui.config.message.editor.presence.end_timestamp"),
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

        currentActivityType = getInstanceData().activityType;
        activityTypeButton = childFrame.addControl(
                new ExtendedButtonControl(
                        calc1, getButtonY(controlIndex),
                        180, 20,
                        Constants.TRANSLATOR.translate("gui.config.message.editor.presence.activity_type") + " => " + ActivityType.from(currentActivityType).name(),
                        () -> {
                            currentActivityType = (currentActivityType + 1) % ActivityType.values().length;
                            activityTypeButton.setControlMessage(
                                    Constants.TRANSLATOR.translate("gui.config.message.editor.presence.activity_type") + " => " + ActivityType.from(currentActivityType).name()
                            );
                            getInstanceData().activityType = currentActivityType;
                        },
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        Constants.TRANSLATOR.translate("gui.config.message.hover.presence.activity_type")
                                )
                        )
                )
        );

        currentPartyPrivacy = getInstanceData().partyPrivacy;
        partyPrivacyButton = childFrame.addControl(
                new ExtendedButtonControl(
                        calc2, getButtonY(controlIndex),
                        180, 20,
                        Constants.TRANSLATOR.translate("gui.config.message.editor.presence.party_privacy") + " => " + PartyPrivacy.from(currentPartyPrivacy).name(),
                        () -> {
                            currentPartyPrivacy = (currentPartyPrivacy + 1) % PartyPrivacy.values().length;
                            partyPrivacyButton.setControlMessage(
                                    Constants.TRANSLATOR.translate("gui.config.message.editor.presence.party_privacy") + " => " + PartyPrivacy.from(currentPartyPrivacy).name()
                            );
                            getInstanceData().partyPrivacy = currentPartyPrivacy;
                        },
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        Constants.TRANSLATOR.translate("gui.config.message.hover.presence.party_privacy")
                                )
                        )
                )
        );

        controlIndex++;

        visualizer.setupVisualizer(
                calc1, calc2,
                controlIndex,
                isDefaultModule || getInstanceData().useAsMain,
                childFrame,
                () -> CraftPresence.CLIENT.compilePresence(getInstanceData())
        );
    }

    @Override
    public void postRender() {
        super.postRender();
        visualizer.postRender(childFrame);
    }

    @Override
    protected boolean allowedToReset() {
        return DEFAULTS != null;
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
    protected boolean setCurrentData(PresenceData data) {
        if (onChangedCallback != null && hasChangesBetween(getCurrentData(), data)) {
            onChangedCallback.accept(data);
            return true;
        }
        return false;
    }
}

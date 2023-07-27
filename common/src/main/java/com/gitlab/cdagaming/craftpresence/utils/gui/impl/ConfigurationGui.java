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

package com.gitlab.cdagaming.craftpresence.utils.gui.impl;

import com.gitlab.cdagaming.craftpresence.config.gui.AboutGui;
import com.gitlab.cdagaming.craftpresence.core.Constants;
import com.gitlab.cdagaming.craftpresence.core.config.Module;
import com.gitlab.cdagaming.craftpresence.core.utils.StringUtils;
import com.gitlab.cdagaming.craftpresence.utils.discord.assets.DiscordAssetUtils;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ExtendedButtonControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ScrollableListControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.integrations.ExtendedScreen;
import com.gitlab.cdagaming.craftpresence.utils.gui.integrations.ScrollPane;
import com.gitlab.cdagaming.craftpresence.utils.gui.widgets.TextWidget;
import net.minecraft.client.gui.GuiScreen;

import java.util.function.BiConsumer;

public abstract class ConfigurationGui<T extends Module> extends ExtendedScreen {
    private final String title, subTitle;
    protected ScrollPane childFrame;
    protected ExtendedButtonControl resetConfigButton, syncConfigButton, proceedButton;

    public ConfigurationGui(GuiScreen parentScreen, String title, String subTitle) {
        super(parentScreen);

        this.title = title;
        this.subTitle = subTitle;
    }

    public ConfigurationGui(GuiScreen parentScreen, String title) {
        this(parentScreen, title, null);
    }

    @Override
    public void initializeUi() {
        proceedButton = addControl(
                new ExtendedButtonControl(
                        (getScreenWidth() / 2) - 90, (getScreenHeight() - 26),
                        180, 20,
                        "gui.config.message.button.back",
                        () -> {
                            applySettings();
                            openScreen(parentScreen);
                        }
                )
        );
        resetConfigButton = addControl(
                new ExtendedButtonControl(
                        6, (getScreenHeight() - 26),
                        95, 20,
                        "gui.config.message.button.reset",
                        () -> {
                            if (resetData()) {
                                reloadUi();
                            }
                        },
                        () -> {
                            if (resetConfigButton.isControlEnabled()) {
                                drawMultiLineString(
                                        StringUtils.splitTextByNewLine(
                                                Constants.TRANSLATOR.translate("gui.config.comment.button.reset.config")
                                        )
                                );
                            }
                        }
                )
        );
        syncConfigButton = addControl(
                new ExtendedButtonControl(
                        (getScreenWidth() - 101), (getScreenHeight() - 26),
                        95, 20,
                        "gui.config.message.button.sync.config",
                        () -> {
                            if (syncData()) {
                                reloadUi();
                            }
                        },
                        () -> {
                            if (syncConfigButton.isControlEnabled()) {
                                drawMultiLineString(
                                        StringUtils.splitTextByNewLine(
                                                Constants.TRANSLATOR.translate("gui.config.comment.button.sync.config")
                                        )
                                );
                            }
                        }
                )
        );

        childFrame = addControl(
                new ScrollPane(
                        0, 32,
                        getScreenWidth(), getScreenHeight() - 32
                )
        );

        appendControls();

        super.initializeUi();
    }

    @Override
    public void preRender() {
        final boolean hasMainTitle = !StringUtils.isNullOrEmpty(title);
        final boolean hasSubTitle = !StringUtils.isNullOrEmpty(subTitle);
        if (hasMainTitle) {
            final String mainTitle = Constants.TRANSLATOR.getLocalizedMessage(title);
            if (hasSubTitle) {
                final String otherTitle = Constants.TRANSLATOR.getLocalizedMessage(subTitle);

                renderCenteredString(mainTitle, getScreenWidth() / 2f, 10, 0xFFFFFF);
                renderCenteredString(otherTitle, getScreenWidth() / 2f, 20, 0xFFFFFF);
            } else {
                renderCenteredString(mainTitle, getScreenWidth() / 2f, 15, 0xFFFFFF);
            }
        }

        syncRenderStates();

        super.preRender();
    }

    protected void addIconSelector(final ExtendedScreen parent, final TextWidget textControl, final BiConsumer<String, String> onUpdatedCallback) {
        final int left = (parent.getScreenWidth() / 2) + 3; // Left; Textbox
        final int right = left + textControl.getControlWidth();
        parent.addControl(
                new ExtendedButtonControl(
                        right + 4,
                        textControl.getTop() - parent.getTop(),
                        30, 20,
                        "...",
                        () -> openScreen(
                                new SelectorGui(
                                        currentScreen,
                                        Constants.TRANSLATOR.translate("gui.config.title.selector.icon"), DiscordAssetUtils.ASSET_LIST.keySet(),
                                        textControl.getControlMessage(), null,
                                        true, false, ScrollableListControl.RenderType.DiscordAsset,
                                        onUpdatedCallback,
                                        null
                                )
                        )
                )
        );
    }

    protected boolean canProceed() {
        return true;
    }

    protected boolean canReset() {
        return false;
    }

    protected boolean allowedToReset() {
        return false;
    }

    protected boolean canSync() {
        return false;
    }

    protected boolean allowedToSync() {
        return false;
    }

    protected void syncRenderStates() {
        proceedButton.setControlEnabled(canProceed());

        resetConfigButton.setControlVisible(allowedToReset());
        resetConfigButton.setControlEnabled(allowedToReset() && canReset());
        syncConfigButton.setControlVisible(allowedToSync());
        syncConfigButton.setControlEnabled(allowedToSync() && canSync());
    }

    protected void appendControls() {
        // Added About Button
        addControl(
                new ExtendedButtonControl(
                        6, 6,
                        20, 20,
                        "?",
                        () -> openScreen(new AboutGui(currentScreen)),
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        Constants.TRANSLATOR.translate("gui.config.message.button.about")
                                )
                        )
                )
        );
        // Added Commands GUI Button
        addControl(
                new ExtendedButtonControl(
                        getScreenWidth() - 26, 6,
                        20, 20,
                        ">_",
                        () -> openScreen(new CommandsGui(currentScreen)),
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        Constants.TRANSLATOR.translate("gui.config.message.button.commands")
                                )
                        )
                )
        );
    }

    protected boolean resetData() {
        return false;
    }

    protected boolean syncData() {
        return false;
    }

    protected void applySettings() {
        // N/A
    }

    protected abstract T getOriginalData();

    protected abstract T getCurrentData();

    protected abstract boolean setCurrentData(T data);
}

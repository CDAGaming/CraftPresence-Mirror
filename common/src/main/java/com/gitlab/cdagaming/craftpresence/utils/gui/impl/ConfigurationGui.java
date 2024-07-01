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
import com.gitlab.cdagaming.craftpresence.config.gui.AboutGui;
import com.gitlab.cdagaming.craftpresence.core.Constants;
import com.gitlab.cdagaming.craftpresence.core.config.Module;
import com.gitlab.cdagaming.craftpresence.core.integrations.discord.assets.DiscordAssetUtils;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.DynamicScrollableList;
import com.gitlab.cdagaming.unilib.utils.gui.controls.ExtendedButtonControl;
import com.gitlab.cdagaming.unilib.utils.gui.integrations.ExtendedScreen;
import com.gitlab.cdagaming.unilib.utils.gui.integrations.ScrollPane;
import com.gitlab.cdagaming.unilib.utils.gui.widgets.TextWidget;
import io.github.cdagaming.unicore.utils.StringUtils;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public abstract class ConfigurationGui<T extends Module> extends ExtendedScreen {
    private final String title, subTitle;
    protected ScrollPane childFrame;
    protected ExtendedButtonControl resetConfigButton, syncConfigButton, proceedButton;

    public ConfigurationGui(String title, String subTitle) {
        super();

        this.title = title;
        this.subTitle = subTitle;
    }

    public ConfigurationGui(String title) {
        this(title, null);
    }

    public static void addIconSelector(final ExtendedScreen currentScreen, final ExtendedScreen parent, final Supplier<TextWidget> textWidget, final BiConsumer<String, String> onUpdatedCallback) {
        final TextWidget textControl = textWidget.get();
        final int left = (parent.getScreenWidth() / 2) + 3; // Left; Textbox
        final int right = left + textControl.getControlWidth();
        parent.addControl(
                new ExtendedButtonControl(
                        right + 4,
                        textControl.getTop() - parent.getTop(),
                        30, 20,
                        "...",
                        () -> currentScreen.openScreen(
                                new DynamicSelectorGui(
                                        Constants.TRANSLATOR.translate("gui.config.title.selector.icon"), DiscordAssetUtils.ASSET_LIST.keySet(),
                                        StringUtils.getOrDefault(textWidget.get().getControlMessage()), null,
                                        true, false, DynamicScrollableList.RenderType.DiscordAsset,
                                        onUpdatedCallback,
                                        null
                                )
                        )
                )
        );
    }

    @Override
    public void initializeUi() {
        proceedButton = addControl(
                new ExtendedButtonControl(
                        (getScreenWidth() / 2) - 90, (getScreenHeight() - 26),
                        180, 20,
                        Constants.TRANSLATOR.translate("gui.config.message.button.back"),
                        () -> {
                            applySettings();
                            openScreen(getParent());
                        }
                )
        );
        resetConfigButton = addControl(
                new ExtendedButtonControl(
                        6, (getScreenHeight() - 26),
                        95, 20,
                        Constants.TRANSLATOR.translate("gui.config.message.button.reset_to_default"),
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
                        Constants.TRANSLATOR.translate("gui.config.message.button.sync.config"),
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
        syncRenderStates();

        super.preRender();
    }

    @Override
    public void renderExtra() {
        final boolean hasMainTitle = !StringUtils.isNullOrEmpty(title);
        final boolean hasSubTitle = !StringUtils.isNullOrEmpty(subTitle);
        if (hasMainTitle) {
            final String mainTitle = Constants.TRANSLATOR.getLocalizedMessage(title);
            if (hasSubTitle) {
                final String otherTitle = Constants.TRANSLATOR.getLocalizedMessage(subTitle);

                renderScrollingString(
                        mainTitle,
                        30, 2,
                        getScreenWidth() - 30, 16,
                        0xFFFFFF
                );
                renderScrollingString(
                        otherTitle,
                        30, 16,
                        getScreenWidth() - 30, 30,
                        0xFFFFFF
                );
            } else {
                renderScrollingString(
                        mainTitle,
                        30, 0,
                        getScreenWidth() - 30, 32,
                        0xFFFFFF
                );
            }
        }

        super.renderExtra();
    }

    protected void addIconSelector(final ExtendedScreen parent, final Supplier<TextWidget> textWidget, final BiConsumer<String, String> onUpdatedCallback) {
        addIconSelector(this, parent, textWidget, onUpdatedCallback);
    }

    protected boolean canProceed() {
        return true;
    }

    protected boolean canReset() {
        return allowedToReset() &&
                hasChangesBetween(getInstanceData(), getDefaultData());
    }

    protected boolean allowedToReset() {
        return false;
    }

    protected boolean canSync() {
        return allowedToSync();
    }

    protected boolean allowedToSync() {
        return false;
    }

    protected void syncRenderStates() {
        proceedButton.setControlEnabled(canProceed());

        resetConfigButton.setControlVisible(allowedToReset());
        resetConfigButton.setControlEnabled(canReset());
        syncConfigButton.setControlVisible(allowedToSync());
        syncConfigButton.setControlEnabled(canSync());
    }

    protected void appendControls() {
        // Adding About Button
        addControl(
                new ExtendedButtonControl(
                        6, 6,
                        20, 20,
                        "?",
                        () -> openScreen(new AboutGui()),
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        Constants.TRANSLATOR.translate("gui.config.message.button.about")
                                )
                        )
                )
        );
        // Adding Commands GUI Button
        addControl(
                new ExtendedButtonControl(
                        getScreenWidth() - 26, 6,
                        20, 20,
                        ">_",
                        () -> openScreen(new CommandsGui()),
                        () -> drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        Constants.TRANSLATOR.translate("gui.config.message.button.commands")
                                )
                        )
                )
        );
    }

    protected boolean resetData() {
        return setInstanceData(getDefaultData());
    }

    protected boolean syncData() {
        final T data = getSyncData();
        return setCurrentData(data) && setInstanceData(data);
    }

    protected void applySettings() {
        if (setCurrentData(getInstanceData())) {
            markAsChanged();
        }
    }

    protected abstract T getInstanceData();

    protected abstract T getCurrentData();

    protected T getDefaultData() {
        return null;
    }

    protected T getSyncData() {
        return null;
    }

    protected boolean setData(T source, T target) {
        if (hasChangesBetween(source, target)) {
            source.transferFrom(target);
            return true;
        }
        return false;
    }

    protected boolean setCurrentData(T data) {
        return setData(getCurrentData(), data);
    }

    protected boolean setInstanceData(T data) {
        return setData(getInstanceData(), data);
    }

    protected boolean hasChangesBetween(T source, T target) {
        return target != null && !source.equals(target);
    }

    protected void markAsChanged() {
        CraftPresence.CONFIG.setChanged(true);
    }
}

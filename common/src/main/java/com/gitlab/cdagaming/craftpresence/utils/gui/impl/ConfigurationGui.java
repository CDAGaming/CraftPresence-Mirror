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

import com.gitlab.cdagaming.craftpresence.CraftPresence;
import com.gitlab.cdagaming.craftpresence.ModUtils;
import com.gitlab.cdagaming.craftpresence.config.Module;
import com.gitlab.cdagaming.craftpresence.config.gui.AboutGui;
import com.gitlab.cdagaming.craftpresence.utils.StringUtils;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ExtendedButtonControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.integrations.ExtendedScreen;
import com.gitlab.cdagaming.craftpresence.utils.gui.integrations.ScrollPane;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import net.minecraft.client.gui.GuiScreen;

public abstract class ConfigurationGui<T extends Module> extends ExtendedScreen {
    private final String title, subTitle;
    protected ScrollPane childFrame;
    private ExtendedButtonControl resetConfigButton, syncConfigButton, proceedButton;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
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
                        (getScreenWidth() / 2) - 90, (getScreenHeight() - 30),
                        180, 20,
                        "gui.config.message.button.back",
                        () -> {
                            applySettings();
                            CraftPresence.GUIS.openScreen(parentScreen);
                        }
                )
        );
        resetConfigButton = addControl(
                new ExtendedButtonControl(
                        10, (getScreenHeight() - 30),
                        95, 20,
                        "gui.config.message.button.reset",
                        this::resetData,
                        () -> {
                            if (resetConfigButton.isControlEnabled()) {
                                CraftPresence.GUIS.drawMultiLineString(
                                        StringUtils.splitTextByNewLine(
                                                ModUtils.TRANSLATOR.translate("gui.config.comment.button.reset.config")
                                        ), this, true
                                );
                            }
                        }
                )
        );
        syncConfigButton = addControl(
                new ExtendedButtonControl(
                        (getScreenWidth() - 105), (getScreenHeight() - 30),
                        95, 20,
                        "gui.config.message.button.sync.config",
                        this::syncData,
                        () -> {
                            if (syncConfigButton.isControlEnabled()) {
                                CraftPresence.GUIS.drawMultiLineString(
                                        StringUtils.splitTextByNewLine(
                                                ModUtils.TRANSLATOR.translate("gui.config.comment.button.sync.config")
                                        ), this, true
                                );
                            }
                        }
                )
        );

        childFrame = addControl(
                new ScrollPane(
                        0, 30,
                        getScreenWidth(), getScreenHeight() - 35
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
            final String mainTitle = StringUtils.getLocalizedMessage(title);
            if (hasSubTitle) {
                final String otherTitle = StringUtils.getLocalizedMessage(subTitle);

                renderCenteredString(mainTitle, getScreenWidth() / 2f, 10, 0xFFFFFF);
                renderCenteredString(otherTitle, getScreenWidth() / 2f, 20, 0xFFFFFF);
            } else {
                renderCenteredString(mainTitle, getScreenWidth() / 2f, 15, 0xFFFFFF);
            }
        }

        syncRenderStates();

        super.preRender();
    }

    protected boolean canProceed() {
        return true;
    }

    protected boolean canReset() {
        return false;
    }

    protected boolean canSync() {
        return false;
    }

    protected void syncRenderStates() {
        proceedButton.setControlEnabled(canProceed());
        resetConfigButton.setControlEnabled(canReset());
        syncConfigButton.setControlEnabled(canSync());
    }

    protected void appendControls() {
        // Added About Button
        addControl(
                new ExtendedButtonControl(
                        5, 5,
                        20, 20,
                        "?",
                        () -> CraftPresence.GUIS.openScreen(new AboutGui(currentScreen)),
                        () -> CraftPresence.GUIS.drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.message.button.about")
                                ), this, true
                        )
                )
        );
        // Added Commands GUI Button
        addControl(
                new ExtendedButtonControl(
                        getScreenWidth() - 27, 5,
                        20, 20,
                        ">_",
                        () -> CraftPresence.GUIS.openScreen(new CommandsGui(currentScreen)),
                        () -> CraftPresence.GUIS.drawMultiLineString(
                                StringUtils.splitTextByNewLine(
                                        ModUtils.TRANSLATOR.translate("gui.config.message.button.commands")
                                ), this, true
                        )
                )
        );
    }

    protected abstract void resetData();

    protected abstract void syncData();

    protected abstract void applySettings();

    protected abstract T getOriginalData();

    protected abstract T getCurrentData();

    protected abstract void setCurrentData(T data);
}

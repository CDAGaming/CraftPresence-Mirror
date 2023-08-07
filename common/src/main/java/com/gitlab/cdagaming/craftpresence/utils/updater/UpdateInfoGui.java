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

package com.gitlab.cdagaming.craftpresence.utils.updater;

import com.gitlab.cdagaming.craftpresence.CraftPresence;
import com.gitlab.cdagaming.craftpresence.ModUtils;
import com.gitlab.cdagaming.craftpresence.core.Constants;
import com.gitlab.cdagaming.craftpresence.core.utils.StringUtils;
import com.gitlab.cdagaming.craftpresence.core.utils.UrlUtils;
import com.gitlab.cdagaming.craftpresence.utils.gui.RenderUtils;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ExtendedButtonControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.integrations.ExtendedScreen;
import com.gitlab.cdagaming.craftpresence.utils.gui.integrations.ScrollPane;
import com.gitlab.cdagaming.craftpresence.utils.gui.widgets.TextDisplayWidget;
import net.minecraft.client.gui.GuiScreen;

import java.util.Map;

/**
 * The Update Info Gui Screen
 */
public class UpdateInfoGui extends ExtendedScreen {
    private final ModUpdaterUtils modUpdater;
    private TextDisplayWidget infoPane;
    private ExtendedButtonControl downloadButton, checkButton;

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param parentScreen The Parent Screen for this Instance
     * @param modUpdater   An instance of the {@link ModUpdaterUtils}
     */
    public UpdateInfoGui(GuiScreen parentScreen, ModUpdaterUtils modUpdater) {
        super(parentScreen);
        this.modUpdater = modUpdater;
    }

    @Override
    public void initializeUi() {
        checkButton = addControl(
                new ExtendedButtonControl(
                        (getScreenWidth() / 2) - 90, (getScreenHeight() - 26),
                        180, 20,
                        "gui.config.message.button.check_for_updates",
                        () -> {
                            resetNotes();
                            modUpdater.checkForUpdates(this::updateNotes);
                        }
                )
        );
        // Adding Back Button
        addControl(
                new ExtendedButtonControl(
                        6, (getScreenHeight() - 26),
                        95, 20,
                        "gui.config.message.button.back",
                        () -> openScreen(parentScreen)
                )
        );
        downloadButton = addControl(
                new ExtendedButtonControl(
                        (getScreenWidth() - 101), (getScreenHeight() - 26),
                        95, 20,
                        "gui.config.message.button.download",
                        () -> UrlUtils.openUrl(modUpdater.downloadUrl)
                )
        );

        // Child Frame Content
        final ScrollPane childFrame = addControl(
                new ScrollPane(
                        0, 32,
                        getScreenWidth(), getScreenHeight() - 32
                )
        );
        infoPane = childFrame.addWidget(
                new TextDisplayWidget(
                        childFrame, 0, 0,
                        getScreenWidth()
                )
        );

        updateNotes();

        super.initializeUi();
    }

    @Override
    public void preRender() {
        downloadButton.setControlEnabled(modUpdater.currentState == ModUpdaterUtils.UpdateState.OUTDATED ||
                modUpdater.currentState == ModUpdaterUtils.UpdateState.BETA_OUTDATED);

        checkButton.setControlEnabled(modUpdater.currentState != ModUpdaterUtils.UpdateState.PENDING);

        super.preRender();
    }

    @Override
    public void renderExtra() {
        final String mainTitle = Constants.TRANSLATOR.translate("gui.config.title");
        final String subTitle = Constants.TRANSLATOR.translate("gui.config.title.changes", modUpdater.currentState.getDisplayName());

        renderCenteredString(mainTitle, getScreenWidth() / 2f, 10, 0xFFFFFF);
        renderCenteredString(subTitle, getScreenWidth() / 2f, 20, 0xFFFFFF);

        super.renderExtra();
    }

    @Override
    public void postRender() {
        final String mainTitle = Constants.TRANSLATOR.translate("gui.config.title");
        final String subTitle = Constants.TRANSLATOR.translate("gui.config.title.changes", modUpdater.currentState.getDisplayName());

        // Hovering over Title Label
        if (RenderUtils.isMouseOver(getMouseX(), getMouseY(), (getScreenWidth() / 2f) - (getStringWidth(mainTitle) / 2f), 10, getStringWidth(mainTitle), getFontHeight())) {
            drawMultiLineString(
                    StringUtils.splitTextByNewLine(
                            Constants.TRANSLATOR.translate("gui.config.comment.title", Constants.VERSION_ID, CraftPresence.CONFIG._schemaVersion)
                    )
            );
        }
        // Hovering over Subtitle Label
        if (RenderUtils.isMouseOver(getMouseX(), getMouseY(), (getScreenWidth() / 2f) - (getStringWidth(subTitle) / 2f), 20, getStringWidth(subTitle), getFontHeight())) {
            drawMultiLineString(
                    StringUtils.splitTextByNewLine(
                            Constants.TRANSLATOR.translate("gui.config.comment.title.changes", ModUtils.MCVersion)
                    )
            );
        }

        super.postRender();
    }

    private void resetNotes() {
        final String notice = Constants.TRANSLATOR.translate("gui.config.message.changelog") +
                '\n' + "  " + "N/A";
        infoPane.setMessage(notice);
    }

    private void updateNotes() {
        if (!modUpdater.changelogData.isEmpty()) {
            final StringBuilder notice = new StringBuilder();
            notice.append(Constants.TRANSLATOR.translate("gui.config.message.changelog"));

            for (Map.Entry<String, String> entry : modUpdater.changelogData.entrySet()) {
                notice
                        .append('\n').append("  ").append(entry.getKey()).append(":")
                        .append('\n').append(entry.getValue())
                        .append('\n').append(' ');
            }
            infoPane.setMessage(notice.toString());
        } else {
            resetNotes();
        }
    }
}

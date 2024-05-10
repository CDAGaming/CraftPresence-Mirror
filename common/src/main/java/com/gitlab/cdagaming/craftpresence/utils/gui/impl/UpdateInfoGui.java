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
import com.gitlab.cdagaming.craftpresence.ModUtils;
import com.gitlab.cdagaming.craftpresence.core.Constants;
import com.gitlab.cdagaming.craftpresence.core.utils.ModUpdaterUtils;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ExtendedButtonControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.integrations.ExtendedScreen;
import com.gitlab.cdagaming.craftpresence.utils.gui.integrations.ScrollPane;
import com.gitlab.cdagaming.craftpresence.utils.gui.widgets.TextDisplayWidget;
import io.github.cdagaming.unicore.utils.StringUtils;
import io.github.cdagaming.unicore.utils.UrlUtils;

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
     * @param modUpdater An instance of the {@link ModUpdaterUtils}
     */
    public UpdateInfoGui(ModUpdaterUtils modUpdater) {
        super();
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
        // Adding About Button
        addControl(
                new ExtendedButtonControl(
                        6, 6,
                        20, 20,
                        "?",
                        null,
                        () -> {
                            final String mainLine = Constants.TRANSLATOR.translate("gui.config.comment.title", Constants.VERSION_ID, CraftPresence.CONFIG._schemaVersion);
                            final String subLine = Constants.TRANSLATOR.translate("gui.config.comment.title.changes", ModUtils.MCVersion);

                            drawMultiLineString(
                                    StringUtils.splitTextByNewLine(
                                            mainLine + '\n' + subLine
                                    )
                            );
                        }
                )
        );
        // Adding Back Button
        addControl(
                new ExtendedButtonControl(
                        6, (getScreenHeight() - 26),
                        95, 20,
                        "gui.config.message.button.back",
                        () -> openScreen(getParent())
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
        infoPane = childFrame.addWidget(new TextDisplayWidget(
                0, 0,
                getScreenWidth()
        ));

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

        renderScrollingString(
                mainTitle,
                30, 2,
                getScreenWidth() - 30, 16,
                0xFFFFFF
        );
        renderScrollingString(
                subTitle,
                30, 16,
                getScreenWidth() - 30, 30,
                0xFFFFFF
        );

        super.renderExtra();
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

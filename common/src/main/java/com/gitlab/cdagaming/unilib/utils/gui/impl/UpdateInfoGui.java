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

package com.gitlab.cdagaming.unilib.utils.gui.impl;

import com.gitlab.cdagaming.unilib.core.utils.ModUpdaterUtils;
import com.gitlab.cdagaming.unilib.utils.gui.controls.ExtendedButtonControl;
import com.gitlab.cdagaming.unilib.utils.gui.integrations.ExtendedScreen;
import com.gitlab.cdagaming.unilib.utils.gui.integrations.ScrollPane;
import com.gitlab.cdagaming.unilib.utils.gui.widgets.TextDisplayWidget;
import io.github.cdagaming.unicore.utils.UrlUtils;

import java.util.Map;

/**
 * The Update Info Gui Screen
 */
public class UpdateInfoGui extends ExtendedScreen {
    private final String changelogPrefix = "Changelog:";
    private final ModUpdaterUtils modUpdater;
    private TextDisplayWidget infoPane;
    private ExtendedButtonControl downloadButton, checkButton;

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param modID      The calling mod identifier
     * @param modUpdater An instance of the {@link ModUpdaterUtils}
     */
    public UpdateInfoGui(final String modID, final ModUpdaterUtils modUpdater) {
        super(modID + " - Update Info");
        this.modUpdater = modUpdater;
    }

    @Override
    public void initializeUi() {
        checkButton = addControl(
                new ExtendedButtonControl(
                        (getScreenWidth() / 2) - 90, (getScreenHeight() - 26),
                        180, 20,
                        "Check for Updates",
                        () -> {
                            updateInfo(ModUpdaterUtils.UpdateState.PENDING.getDisplayName());
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
                        "Back",
                        () -> openScreen(getParent())
                )
        );
        downloadButton = addControl(
                new ExtendedButtonControl(
                        (getScreenWidth() - 101), (getScreenHeight() - 26),
                        95, 20,
                        "Download",
                        () -> UrlUtils.openUrl(modUpdater.getDownloadUrl())
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
                childFrame.getScreenWidth()
        ));

        updateNotes();

        super.initializeUi();
    }

    @Override
    public void preRender() {
        downloadButton.setControlEnabled(modUpdater.getStatus() == ModUpdaterUtils.UpdateState.OUTDATED ||
                modUpdater.getStatus() == ModUpdaterUtils.UpdateState.BETA_OUTDATED);

        checkButton.setControlEnabled(modUpdater.getStatus() != ModUpdaterUtils.UpdateState.PENDING);

        super.preRender();
    }

    private void resetNotes() {
        final String notice = changelogPrefix +
                '\n' + "  " + "N/A";
        infoPane.setMessage(notice);
    }

    private void updateNotes() {
        if (modUpdater.hasChanges()) {
            final StringBuilder notice = new StringBuilder();
            notice.append(changelogPrefix);

            for (Map.Entry<String, String> entry : modUpdater.getChanges()) {
                notice
                        .append('\n').append("  ").append(entry.getKey()).append(":")
                        .append('\n').append(entry.getValue())
                        .append('\n').append(' ');
            }
            infoPane.setMessage(notice.toString());
        } else {
            resetNotes();
        }
        updateInfo();
    }

    private void updateInfo(final String status) {
        setScreenSubTitle(String.format("Version Check Info (State: %1$s)", status));
    }

    private void updateInfo() {
        updateInfo(modUpdater.getStatus().getDisplayName());
    }
}

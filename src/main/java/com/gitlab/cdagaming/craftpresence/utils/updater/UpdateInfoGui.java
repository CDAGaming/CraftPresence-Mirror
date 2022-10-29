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

package com.gitlab.cdagaming.craftpresence.utils.updater;

import com.gitlab.cdagaming.craftpresence.CraftPresence;
import com.gitlab.cdagaming.craftpresence.ModUtils;
import com.gitlab.cdagaming.craftpresence.impl.Tuple;
import com.gitlab.cdagaming.craftpresence.utils.StringUtils;
import com.gitlab.cdagaming.craftpresence.utils.UrlUtils;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ExtendedButtonControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.integrations.ExtendedScreen;
import net.minecraft.client.gui.GuiScreen;

import java.lang.reflect.Modifier;
import java.util.List;

public class UpdateInfoGui extends ExtendedScreen {
    private final ModUpdaterUtils modUpdater;
    private ExtendedButtonControl downloadButton, checkButton;

    public UpdateInfoGui(GuiScreen parentScreen, ModUpdaterUtils modUpdater) {
        super(parentScreen);
        this.modUpdater = modUpdater;
    }

    @Override
    public void initializeUi() {
        checkButton = addControl(
                new ExtendedButtonControl(
                        (getScreenWidth() / 2) - 90, (getScreenHeight() - 30),
                        180, 20,
                        "gui.config.message.button.checkForUpdates",
                        () ->
                                modUpdater.checkForUpdates(() -> {
                                    if (modUpdater.isInvalidVersion) {
                                        // If the Updater found our version to be an invalid one
                                        // Then replace the Version ID, Name, and Type
                                        StringUtils.updateField(ModUtils.class, null, new Tuple<>("VERSION_ID", "v" + modUpdater.targetVersion, ~Modifier.FINAL));
                                        StringUtils.updateField(ModUtils.class, null, new Tuple<>("VERSION_TYPE", modUpdater.currentState.getDisplayName(), ~Modifier.FINAL));
                                        StringUtils.updateField(ModUtils.class, null, new Tuple<>("VERSION_LABEL", modUpdater.currentState.getDisplayName(), ~Modifier.FINAL));
                                        StringUtils.updateField(ModUtils.class, null, new Tuple<>("NAME", CraftPresence.class.getSimpleName(), ~Modifier.FINAL));

                                        modUpdater.currentVersion = modUpdater.targetVersion;
                                        modUpdater.isInvalidVersion = false;
                                    }
                                })
                )
        );
        // Adding Back Button
        addControl(
                new ExtendedButtonControl(
                        10, (getScreenHeight() - 30),
                        95, 20,
                        "gui.config.message.button.back",
                        () -> CraftPresence.GUIS.openScreen(parentScreen)
                )
        );
        downloadButton = addControl(
                new ExtendedButtonControl(
                        (getScreenWidth() - 105), (getScreenHeight() - 30),
                        95, 20,
                        "gui.config.message.button.download",
                        () -> UrlUtils.openUrl(modUpdater.downloadUrl)
                )
        );

        super.initializeUi();
    }

    @Override
    public void preRender() {
        downloadButton.setControlEnabled(modUpdater.currentState == ModUpdaterUtils.UpdateState.OUTDATED ||
                modUpdater.currentState == ModUpdaterUtils.UpdateState.BETA_OUTDATED);

        checkButton.setControlEnabled(modUpdater.currentState != ModUpdaterUtils.UpdateState.PENDING);

        final String mainTitle = ModUtils.TRANSLATOR.translate("gui.config.title");
        final String subTitle = ModUtils.TRANSLATOR.translate("gui.config.title.changes", modUpdater.currentState.getDisplayName());
        final List<String> notice = StringUtils.splitTextByNewLine(ModUtils.TRANSLATOR.translate("gui.config.message.changelog", modUpdater.targetVersion, modUpdater.targetChangelogData));

        renderString(mainTitle, (getScreenWidth() / 2f) - (getStringWidth(mainTitle) / 2f), 10, 0xFFFFFF);
        renderString(subTitle, (getScreenWidth() / 2f) - (getStringWidth(subTitle) / 2f), 20, 0xFFFFFF);

        CraftPresence.GUIS.drawMultiLineString(notice, 10, 45, this, false);
    }

    @Override
    public void postRender() {
        final String mainTitle = ModUtils.TRANSLATOR.translate("gui.config.title");
        final String subTitle = ModUtils.TRANSLATOR.translate("gui.config.title.changes", modUpdater.currentState.getDisplayName());

        // Hovering over Title Label
        if (CraftPresence.GUIS.isMouseOver(getMouseX(), getMouseY(), (getScreenWidth() / 2f) - (getStringWidth(mainTitle) / 2f), 10, getStringWidth(mainTitle), getFontHeight())) {
            CraftPresence.GUIS.drawMultiLineString(
                    StringUtils.splitTextByNewLine(
                            ModUtils.TRANSLATOR.translate("gui.config.comment.title", ModUtils.VERSION_ID, CraftPresence.CONFIG._schemaVersion)
                    ), this, true
            );
        }
        // Hovering over Subtitle Label
        if (CraftPresence.GUIS.isMouseOver(getMouseX(), getMouseY(), (getScreenWidth() / 2f) - (getStringWidth(subTitle) / 2f), 20, getStringWidth(subTitle), getFontHeight())) {
            CraftPresence.GUIS.drawMultiLineString(
                    StringUtils.splitTextByNewLine(
                            ModUtils.TRANSLATOR.translate("gui.config.comment.title.changes", ModUtils.MCVersion)
                    ), this, true
            );
        }
    }
}

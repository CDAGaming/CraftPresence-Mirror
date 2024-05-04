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
import com.gitlab.cdagaming.craftpresence.ModUtils;
import com.gitlab.cdagaming.craftpresence.core.Constants;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ExtendedButtonControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.impl.UpdateInfoGui;
import com.gitlab.cdagaming.craftpresence.utils.gui.integrations.ExtendedScreen;
import io.github.cdagaming.unicore.utils.StringUtils;
import io.github.cdagaming.unicore.utils.UrlUtils;

import java.util.List;

public class AboutGui extends ExtendedScreen {
    @Override
    public void initializeUi() {
        // Adding Version Check Button
        addControl(
                new ExtendedButtonControl(
                        (getScreenWidth() / 2) - 90, (getScreenHeight() - 26),
                        180, 20,
                        "gui.config.message.button.version_info",
                        () -> openScreen(new UpdateInfoGui(ModUtils.UPDATER))
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

        // Adding View Source Button
        addControl(
                new ExtendedButtonControl(
                        (getScreenWidth() / 2) - 90, (getScreenHeight() - 51),
                        180, 20,
                        "gui.config.message.button.view_source",
                        () -> UrlUtils.openUrl(CraftPresence.CONFIG._SOURCE)
                )
        );

        // Adding Wiki Button
        addControl(
                new ExtendedButtonControl(
                        (getScreenWidth() - 101), (getScreenHeight() - 26),
                        95, 20,
                        "gui.config.message.button.wiki",
                        () -> UrlUtils.openUrl(CraftPresence.CONFIG._README)
                )
        );

        super.initializeUi();
    }

    @Override
    public void renderExtra() {
        final String mainTitle = Constants.TRANSLATOR.translate("gui.config.title.about.config");
        final List<String> notice = StringUtils.splitTextByNewLine(Constants.TRANSLATOR.translate("gui.config.message.credits"));

        renderScrollingString(
                mainTitle,
                30, 0,
                getScreenWidth() - 30, 32,
                0xFFFFFF
        );
        drawMultiLineString(
                notice,
                0, getScreenHeight() / 3,
                getScreenWidth(), -1, -1,
                true, false,
                createDefaultTooltip().putSecond(null).putThird(null)
        );

        super.renderExtra();
    }
}

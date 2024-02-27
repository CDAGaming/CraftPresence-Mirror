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

import com.gitlab.cdagaming.craftpresence.core.Constants;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ExtendedButtonControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.integrations.ExtendedScreen;
import io.github.cdagaming.unicore.utils.StringUtils;
import net.minecraft.client.gui.GuiScreen;

import java.util.List;

/**
 * The Message Gui Screen Implementation
 */
public class MessageGui extends ExtendedScreen {
    private final List<String> messageData;

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param parentScreen The Parent Screen for this Instance
     * @param messageData  The message to display for this Instance
     */
    public MessageGui(GuiScreen parentScreen, List<String> messageData) {
        super(parentScreen);
        this.messageData = StringUtils.newArrayList(messageData);
    }

    @Override
    public void initializeUi() {
        // Adding Back Button
        addControl(
                new ExtendedButtonControl(
                        (getScreenWidth() / 2) - 90, (getScreenHeight() - 26),
                        180, 20,
                        "gui.config.message.button.back",
                        () -> openScreen(parentScreen)
                )
        );

        super.initializeUi();
    }

    @Override
    public void renderExtra() {
        final String mainTitle = Constants.TRANSLATOR.translate("gui.config.title.message");

        renderScrollingString(
                mainTitle,
                30, 15,
                getScreenWidth() - 30, 15 + getFontHeight(),
                0xFFFFFF
        );
        drawMultiLineString(
                messageData,
                0, getScreenHeight() / 3,
                getScreenWidth(), -1, -1,
                true, false,
                createDefaultTooltip().putSecond(null).putThird(null)
        );

        super.renderExtra();
    }
}

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

import com.gitlab.cdagaming.unilib.utils.gui.controls.ExtendedButtonControl;
import com.gitlab.cdagaming.unilib.utils.gui.integrations.ExtendedScreen;
import io.github.cdagaming.unicore.utils.StringUtils;

import java.util.List;

/**
 * The Message Gui Screen Implementation
 */
public class MessageGui extends ExtendedScreen {
    private final String mainTitle;
    private final List<String> messageData;

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param modID       The calling mod identifier
     * @param messageData The message to display for this Instance
     */
    public MessageGui(final String modID, final List<String> messageData) {
        super();
        this.mainTitle = modID + " - Message";
        this.messageData = StringUtils.newArrayList(messageData);
    }

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param modID       The calling mod identifier
     * @param messageData The message to display for this Instance
     */
    public MessageGui(final String modID, final String messageData) {
        this(modID, StringUtils.splitTextByNewLine(
                messageData
        ));
    }

    @Override
    public void initializeUi() {
        // Adding Back Button
        addControl(
                new ExtendedButtonControl(
                        (getScreenWidth() / 2) - 90, (getScreenHeight() - 26),
                        180, 20,
                        "Back",
                        () -> openScreen(getParent())
                )
        );

        super.initializeUi();
    }

    @Override
    public void renderExtra() {
        renderScrollingString(
                mainTitle,
                30, 0,
                getScreenWidth() - 30, 32,
                0xFFFFFF
        );
        drawMultiLineString(
                messageData,
                0, getScreenHeight() / 3,
                getScreenWidth(), -1, -1,
                true, false
        );

        super.renderExtra();
    }
}

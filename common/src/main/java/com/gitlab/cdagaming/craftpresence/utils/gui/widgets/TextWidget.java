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

package com.gitlab.cdagaming.craftpresence.utils.gui.widgets;

import com.gitlab.cdagaming.craftpresence.utils.MathUtils;
import com.gitlab.cdagaming.craftpresence.utils.gui.integrations.ExtendedScreen;
import com.gitlab.cdagaming.craftpresence.utils.gui.integrations.ScrollPane;

import java.util.List;
import java.util.Objects;

public class TextWidget implements DynamicWidget {
    private ExtendedScreen parent;
    private int startX;
    private int startY;
    private int width;
    private int contentHeight;
    private String message;
    private List<String> renderLines;

    public TextWidget(final ExtendedScreen parent, final int startX, final int startY, final int width, final String message) {
        this.parent = parent;
        setControlPosX(startX);
        setControlPosY(startY);
        setControlWidth(width);
        setMessage(message);
    }

    public TextWidget(final ExtendedScreen parent, final int startX, final int startY, final int width) {
        this(parent, startX, startY, width, "");
    }

    public TextWidget(final ExtendedScreen parent, final int width, final String message) {
        this(parent, 0, 0, width, message);
    }

    public TextWidget(final ExtendedScreen parent, final int width) {
        this(parent, width, "");
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String newMessage) {
        if (!Objects.equals(newMessage, message)) {
            message = newMessage;
            renderLines = refreshContent();
        }
    }

    public List<String> getRenderLines() {
        return renderLines;
    }

    @Override
    public void draw(ExtendedScreen screen) {
        if (screen instanceof ScrollPane) {
            ((ScrollPane) screen).drawScrollString(this);
        }
    }

    @Override
    public int getControlPosX() {
        return this.startX;
    }

    @Override
    public void setControlPosX(int posX) {
        this.startX = posX;
    }

    @Override
    public int getControlPosY() {
        return this.startY;
    }

    @Override
    public void setControlPosY(int posY) {
        this.startY = posY;
    }

    @Override
    public int getControlWidth() {
        return width - startX;
    }

    @Override
    public void setControlWidth(int width) {
        this.width = width;
    }

    @Override
    public int getControlHeight() {
        return contentHeight;
    }

    @Override
    public void setControlHeight(int height) {
        throw new UnsupportedOperationException();
    }

    private List<String> refreshContent() {
        final int width = MathUtils.clamp(getControlWidth(), 0, parent.getMaxWidth());
        final List<String> content = parent.createRenderLines(
                getMessage(),
                width
        );
        contentHeight = content.size() * (parent.getFontHeight() + 1);
        return content;
    }
}

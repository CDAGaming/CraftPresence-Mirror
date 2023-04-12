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

import com.gitlab.cdagaming.craftpresence.CraftPresence;
import com.gitlab.cdagaming.craftpresence.utils.StringUtils;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ExtendedTextControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.integrations.ExtendedScreen;
import net.minecraft.client.gui.FontRenderer;

public class TextWidget extends ExtendedTextControl {
    private String title;
    private float titleX;
    private float titleY;
    /**
     * Event to Deploy when this Control is Hovered Over, if any
     */
    private Runnable onHoverEvent = null;

    public TextWidget(int componentId, FontRenderer fontRendererObj, int y, int widthIn, int heightIn, String title, Runnable onHoverEvent) {
        super(componentId, fontRendererObj, 0, y, widthIn, heightIn);
        this.title = title;
        setOnHover(onHoverEvent);
    }

    public TextWidget(int componentId, FontRenderer fontRendererObj, int y, int widthIn, int heightIn, String title) {
        this(componentId, fontRendererObj, y, widthIn, heightIn, title, null);
    }

    public TextWidget(FontRenderer fontRendererObj, int y, int widthIn, int heightIn, String title, Runnable onHoverEvent) {
        super(fontRendererObj, 0, y, widthIn, heightIn);
        this.title = title;
        setOnHover(onHoverEvent);
    }

    public TextWidget(FontRenderer fontRendererObj, int y, int widthIn, int heightIn, String title) {
        this(fontRendererObj, y, widthIn, heightIn, title, null);
    }

    public TextWidget(FontRenderer fontRendererObj, int y, int widthIn, int heightIn, Runnable keyEvent, String title, Runnable onHoverEvent) {
        super(fontRendererObj, 0, y, widthIn, heightIn, keyEvent);
        this.title = title;
        setOnHover(onHoverEvent);
    }

    public TextWidget(FontRenderer fontRendererObj, int y, int widthIn, int heightIn, Runnable keyEvent, String title) {
        this(fontRendererObj, y, widthIn, heightIn, keyEvent, title, null);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public float getTitleX() {
        return titleX;
    }

    public void setTitleX(float titleX) {
        this.titleX = titleX;
    }

    public float getTitleY() {
        return titleY;
    }

    public void setTitleY(float titleY) {
        this.titleY = titleY;
    }

    /**
     * Sets the Event to occur upon Mouse Over
     *
     * @param event The event to occur
     */
    public void setOnHover(final Runnable event) {
        onHoverEvent = event;
    }

    /**
     * Triggers the onHover event to occur
     */
    public void onHover() {
        if (onHoverEvent != null) {
            onHoverEvent.run();
        }
    }

    @Override
    public void draw(ExtendedScreen screen) {
        // Ensure correct positioning
        final int calc1 = (screen.getScreenWidth() / 2) - (getControlWidth() - 3); // Left; Title Text
        final int calc2 = (screen.getScreenWidth() / 2) + 3; // Left; Textbox
        setControlPosX(calc2);

        if (!StringUtils.isNullOrEmpty(title)) {
            final String mainTitle = StringUtils.getLocalizedMessage(title);
            setTitleX((calc1 + getControlWidth()) - (getControlWidth() / 2f));
            setTitleY(getBottom() - (getControlHeight() / 2f) - (screen.getFontHeight() / 2f));
            screen.renderCenteredString(mainTitle, getTitleX(), getTitleY(), 0xFFFFFF);
        }
    }

    @Override
    public void postDraw(ExtendedScreen screen) {
        if (!StringUtils.isNullOrEmpty(title)) {
            final String mainTitle = StringUtils.getLocalizedMessage(title);
            final int titleWidth = screen.getStringWidth(mainTitle);
            if (screen.isOverScreen() && CraftPresence.GUIS.isMouseOver(
                    screen.getMouseX(), screen.getMouseY(),
                    getTitleX() - (titleWidth / 2f), getTitleY(),
                    titleWidth, screen.getFontHeight()
            )) {
                onHover();
            }
        }
    }
}

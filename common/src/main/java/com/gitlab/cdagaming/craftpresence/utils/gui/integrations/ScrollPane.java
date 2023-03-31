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

package com.gitlab.cdagaming.craftpresence.utils.gui.integrations;

import com.gitlab.cdagaming.craftpresence.CraftPresence;
import com.gitlab.cdagaming.craftpresence.impl.Tuple;
import com.gitlab.cdagaming.craftpresence.utils.MathUtils;
import com.gitlab.cdagaming.craftpresence.utils.StringUtils;
import com.gitlab.cdagaming.craftpresence.utils.gui.widgets.TextWidget;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;

import java.awt.*;

public class ScrollPane extends ExtendedScreen {
    private static final Color NERO = StringUtils.getColorFrom(32, 32, 32);
    private static final Color NONE = StringUtils.getColorFrom(0, 0, 0, 0);
    private static final int DEFAULT_PADDING = 4;
    private int padding;
    private int amountScrolled;
    private int scrollMultiplier;
    private int initialClickY = -2;

    public ScrollPane(int startX, int startY, int width, int height, int padding) {
        super();
        setScreenX(startX);
        setScreenY(startY);
        setScreenWidth(width - startX);
        setScreenHeight(height - startY);
        setPadding(padding);
        setScroll(0);
    }

    public ScrollPane(int startX, int startY, int width, int height) {
        this(startX, startY, width, height, DEFAULT_PADDING);
    }

    public ScrollPane(int width, int height, int padding) {
        this(0, 0, width, height, padding);
    }

    public ScrollPane(int width, int height) {
        this(width, height, DEFAULT_PADDING);
    }

    @Override
    public void renderCriticalData() {
        CraftPresence.GUIS.drawBackground(
                getScreenX(), getScreenY(),
                getScreenWidth(), getScreenHeight(),
                NERO
        );

        getContentHeight();

        // Render Depth Decorations
        final Tuple<Boolean, String, ResourceLocation> backgroundData = CraftPresence.GUIS.getTextureData(CraftPresence.CONFIG.accessibilitySettings.guiBackgroundColor);
        CraftPresence.GUIS.drawTextureGradientRect(0.0D,
                getLeft(), getRight(), getTop(), getTop() + padding,
                1.0D, 1.0D, 0.0D,
                NONE,
                Color.black,
                backgroundData.getThird()
        );
        CraftPresence.GUIS.drawTextureGradientRect(0.0D,
                getLeft(), getRight(), getBottom() - padding, getBottom(),
                1.0D, 1.0D, 0.0D,
                Color.black,
                NONE,
                backgroundData.getThird()
        );

        // Render Scrollbar Elements
        final int scrollBarX = getScrollBarX();
        final int scrollBarRight = scrollBarX + getScrollBarWidth();
        final int bottom = getBottom();
        final int top = getTop();
        final int maxScroll = getMaxScroll();
        final int screenHeight = getScreenHeight();
        if (maxScroll > 0) {
            int height = screenHeight * screenHeight / getContentHeight();
            height = MathUtils.clamp(height, 32, screenHeight - (padding * 2));
            int barTop = amountScrolled * (screenHeight - height) / maxScroll + top;
            if (barTop < top) {
                barTop = top;
            }

            CraftPresence.GUIS.drawTextureGradientRect(0.0D,
                    scrollBarX, scrollBarRight, top, bottom,
                    1.0D, 1.0D, 0.0D,
                    Color.black, Color.black,
                    backgroundData.getThird()
            );

            CraftPresence.GUIS.drawTextureGradientRect(0.0D,
                    scrollBarX, scrollBarRight, barTop, barTop + height,
                    1.0D, 1.0D, 0.0D,
                    Color.gray, Color.gray,
                    backgroundData.getThird()
            );

            CraftPresence.GUIS.drawTextureGradientRect(0.0D,
                    scrollBarX, scrollBarRight - 1, barTop, barTop + height - 1,
                    1.0D, 1.0D, 0.0D,
                    Color.lightGray, Color.lightGray,
                    backgroundData.getThird()
            );
        }
    }

    @Override
    public void handleMouseInput() {
        if (isOverScreen()) {
            super.handleMouseInput();

            final int mouseX = getMouseX();
            final int mouseY = getMouseY();
            final int screenHeight = getScreenHeight();
            if (Mouse.isButtonDown(0)) {
                if (initialClickY == -1) {
                    if (CraftPresence.GUIS.isMouseOver(getMouseY(), this)) {
                        final int scrollBarX = getScrollBarX();
                        final int scrollBarRight = scrollBarX + getScrollBarWidth();
                        if (mouseX >= scrollBarX && mouseX <= scrollBarRight) {
                            scrollMultiplier = -1;
                            int maxScroll = getMaxScroll();
                            if (maxScroll < 1) {
                                maxScroll = 1;
                            }

                            int height = screenHeight * screenHeight / getContentHeight();
                            height = MathUtils.clamp(height, 32, screenHeight - (padding * 2));
                            scrollMultiplier /= (float) (screenHeight - height) / (float) maxScroll;
                        } else {
                            scrollMultiplier = 1;
                        }

                        initialClickY = mouseY;
                    } else {
                        initialClickY = -2;
                    }
                } else if (initialClickY >= 0) {
                    setScroll(amountScrolled - ((mouseY - initialClickY) * scrollMultiplier));
                    initialClickY = mouseY;
                }
            } else {
                initialClickY = -1;
            }

            int wheelDelta = getMouseScroll();
            if (wheelDelta != 0) {
                if (wheelDelta > 0) {
                    wheelDelta = -1;
                } else {
                    wheelDelta = 1;
                }
                scrollBy(wheelDelta * getHeightPerScroll());
            }
        }
    }

    public void drawScrollString(final TextWidget data) {
        drawScrollString(
                data.getRenderLines(),
                data.getControlPosX() + padding, amountScrolled,
                data.getTop() + padding,
                0xFFFFFF
        );
    }

    public void setPadding(final int newPadding) {
        padding = newPadding;
    }

    public int getHeightPerScroll() {
        return 8;
    }

    public int getScrollBarX() {
        return getRight() - getScrollBarWidth();
    }

    public int getScrollBarWidth() {
        return 6;
    }

    public void scrollBy(int amount) {
        setScroll(amountScrolled + amount);
    }

    public void setScroll(int amount) {
        amountScrolled = amount;
        bindAmountScrolled();
        initialClickY = -2;
    }

    public void bindAmountScrolled() {
        amountScrolled = MathUtils.clamp(amountScrolled, 0, getMaxScroll());
    }

    public int getMaxScroll() {
        return Math.max(0, getContentHeight() - (getScreenHeight() - padding));
    }

    @Override
    public int getMaxWidth() {
        return getScreenWidth() - padding - getScrollBarWidth();
    }
}

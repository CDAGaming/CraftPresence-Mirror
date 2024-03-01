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

package com.gitlab.cdagaming.craftpresence.utils.gui.integrations;

import com.gitlab.cdagaming.craftpresence.utils.gui.RenderUtils;
import net.minecraft.src.FontRenderer;
import net.minecraft.src.Gui;
import net.minecraft.src.GuiScreen;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class GuiTextField extends Gui {
    private static final String allowedChars = " !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_'abcdefghijklmnopqrstuvwxyz{|}~⌂ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»";
    private final FontRenderer fontRenderer;
    private final int xPos;
    private final int yPos;
    private final int width;
    private final int height;
    public String text;
    private int maxStringLength;
    private int cursorCounter;
    public boolean isFocused = false;
    public boolean isEnabled = true;
    public String arrow = "";
    public int lineScrollOffset = 0;
    public int cursorPosition = 0;
    public int selectionEnd = 0;
    private boolean isTextSelected;

    public GuiTextField(FontRenderer fontRenderer, int x, int y, int w, int h, String s) {
        this.fontRenderer = fontRenderer;
        this.xPos = x;
        this.yPos = y;
        this.width = w;
        this.height = h;
        this.setText(s);
        this.setCursorPosition(s.length());
    }

    public void setText(String s) {
        this.text = s;
    }

    public void setArrow(String s) {
        this.arrow = s;
    }

    public String getText() {
        return this.text;
    }

    public void updateCursorCounter() {
        ++this.cursorCounter;
    }

    public void writeText(String text) {
        String newText = "";
        int posend = Math.min(this.cursorPosition, this.selectionEnd);
        int selend = Math.max(this.cursorPosition, this.selectionEnd);
        int end = this.maxStringLength - this.text.length() - (posend - this.selectionEnd);
        if (!this.text.isEmpty()) {
            newText = newText + this.text.substring(0, posend);
        }

        int n;
        if (end < text.length()) {
            newText = newText + text.substring(0, end);
            n = end;
        } else {
            newText = newText + text;
            n = text.length();
        }

        if (!this.text.isEmpty() && selend < this.text.length()) {
            newText = newText + this.text.substring(selend);
        }

        this.text = newText;
        this.moveCursorBy(posend - this.selectionEnd + n);
    }

    public void deleteFromCursor(int i) {
        if (!this.text.isEmpty()) {
            if (this.selectionEnd != this.cursorPosition) {
                this.writeText("");
            } else {
                boolean charExists = i < 0;
                int pos = charExists ? this.cursorPosition + i : this.cursorPosition;
                int neg = charExists ? this.cursorPosition : this.cursorPosition + i;
                String newtext = "";
                if (pos >= 0) {
                    newtext = this.text.substring(0, pos);
                }

                if (neg < this.text.length()) {
                    newtext = newtext + this.text.substring(neg);
                }

                this.text = newtext;
                if (charExists) {
                    this.moveCursorBy(i);
                }
            }
        }
    }

    public void moveCursorBy(int i) {
        this.setCursorPosition(this.selectionEnd + i);
    }

    public void setCursorPosition(int i) {
        this.cursorPosition = i;
        int length = this.text.length();
        if (this.cursorPosition < 0) {
            this.cursorPosition = 0;
        }

        if (this.cursorPosition > length) {
            this.cursorPosition = length;
        }

        this.setSelectionPos(this.cursorPosition);
    }

    public void setSelectionPos(int i) {
        int length = this.text.length();
        if (i > length) {
            i = length;
        }

        if (i < 0) {
            i = 0;
        }

        this.selectionEnd = i;
        if (this.fontRenderer != null) {
            if (this.lineScrollOffset > length) {
                this.lineScrollOffset = length;
            }

            int max = this.width - 20;
            String sw = RenderUtils.trimStringToWidth(this.fontRenderer, this.text.substring(this.lineScrollOffset), max);
            int inoffs = sw.length() + this.lineScrollOffset;
            if (i == this.lineScrollOffset) {
                this.lineScrollOffset -= RenderUtils.trimStringToWidth(this.fontRenderer, this.text, max, true).length();
            }

            if (i > inoffs) {
                this.lineScrollOffset += i - inoffs;
            } else if (i <= this.lineScrollOffset) {
                this.lineScrollOffset -= this.lineScrollOffset - i;
            }

            if (this.lineScrollOffset < 0) {
                this.lineScrollOffset = 0;
            }

            if (this.lineScrollOffset > length) {
                this.lineScrollOffset = length;
            }
        }
    }

    public void textboxKeyTyped(char eventChar, int eventKey) {
        if (this.isEnabled && this.isFocused) {
            if (eventChar == 3) {
                ExtendedScreen.copyToClipboard(this.text);
            } else if (eventChar == 22) {
                this.writeText(GuiScreen.getClipboardString());
            } else if (eventChar == 24) {
                ExtendedScreen.copyToClipboard(this.text);
                this.writeText("");
            }

            if (eventKey == 14) {
                this.deleteFromCursor(-1);
                if (this.isTextSelected) {
                    this.text = "";
                    this.writeText("");
                    this.isTextSelected = false;
                }
            } else if (eventKey == 28) {
                this.isFocused = false;
            } else if (eventKey == 203) {
                this.moveCursorBy(-1);
                this.isTextSelected = false;
            } else if (eventKey == 205) {
                this.moveCursorBy(1);
                this.isTextSelected = false;
            } else if (eventKey == 211) {
                this.deleteFromCursor(1);
            } else {
                if (!this.text.isEmpty() && Keyboard.isKeyDown(29) && eventKey == 30) {
                    this.isTextSelected = true;
                    this.setCursorPosition(this.text.length());
                    this.setSelectionPos(0);
                }

                if (allowedChars.indexOf(eventChar) >= 0 && this.text.length() < this.maxStringLength) {
                    this.writeText("" + eventChar);
                }

                if (Keyboard.getEventKey() == 59 && this.text.length() < this.maxStringLength) {
                    this.writeText("§");
                }

                if (this.isTextSelected) {
                    if (allowedChars.indexOf(eventChar) >= 0 && this.text.length() < this.maxStringLength) {
                        this.text = "";
                        this.writeText("" + eventChar);
                        this.isTextSelected = false;
                    }

                    if (eventChar == 22) {
                        String str = GuiScreen.getClipboardString();
                        if (str == null) {
                            str = "";
                        }

                        int length = this.maxStringLength - this.text.length();
                        if (length > str.length()) {
                            length = str.length();
                        }

                        if (length > 0) {
                            this.text = "";
                            this.writeText(str.substring(0, length));
                            this.isTextSelected = false;
                        }
                    }
                }
            }
        }
    }

    public void mouseClicked(int x, int y, int button) {
        boolean state = this.isEnabled && x >= this.xPos && x < this.xPos + this.width && y >= this.yPos && y < this.yPos + this.height;
        this.setFocused(state);
        this.isTextSelected = false;

        if (this.isFocused && state && button == 0) {
            int l = x - this.xPos;
            String string = RenderUtils.trimStringToWidth(this.fontRenderer, this.text.substring(this.lineScrollOffset), this.width);
            this.setCursorPosition(RenderUtils.trimStringToWidth(this.fontRenderer, string, l).length() + this.lineScrollOffset);
        }
    }

    public void setFocused(boolean bool) {
        if (bool && !this.isFocused) {
            this.cursorCounter = 0;
        }

        this.isFocused = bool;
    }

    public void drawTextBox() {
        drawRect(this.xPos - 1, this.yPos - 1, this.xPos + this.width + 1, this.yPos + this.height + 1, -6250336);
        drawRect(this.xPos, this.yPos, this.xPos + this.width, this.yPos + this.height, -16777216);
        this.renderText(this.xPos + 4, this.yPos + (this.height - 8) / 2, this.yPos - 2 + (this.height - 8) / 2, this.yPos + (this.height + 12) / 2);
    }

    public void renderSelectedTextBG(String strOffset, int mny, int mxy) {
        GL11.glEnable(GL11.GL_COLOR_LOGIC_OP);
        GL11.glLogicOp(GL11.GL_OR_REVERSE);
        drawRect(
                2 + this.fontRenderer.getStringWidth(this.arrow) + this.xPos,
                mny,
                this.fontRenderer.getStringWidth(this.arrow + strOffset) + 6 + this.xPos,
                mxy,
                Color.BLUE.getRGB()
        );
        GL11.glDisable(GL11.GL_COLOR_LOGIC_OP);
    }

    public void renderText(int x, int y, int bgmny, int bgmxy) {
        if (this.isFocused && this.isEnabled) {
            String strOffset = RenderUtils.trimStringToWidth(this.fontRenderer, this.text.substring(this.lineScrollOffset), this.width - 8);
            int strWidth = this.fontRenderer.getStringWidth(this.arrow + strOffset);
            int selOffs = this.selectionEnd - this.lineScrollOffset;
            if (selOffs > strOffset.length()) {
                selOffs = strOffset.length();
            }

            this.drawString(
                    this.fontRenderer,
                    this.arrow + strOffset,
                    x,
                    y,
                    14737632
            );
            if (this.cursorCounter / 6 % 2 == 0) {
                if (this.cursorPosition == this.text.length()) {
                    this.drawString(
                            this.fontRenderer,
                            "_",
                            x + (strWidth > 0 ? strWidth + 2 : 0),
                            y,
                            14737632
                    );
                } else {
                    int selX = x + this.fontRenderer.getStringWidth(this.arrow + strOffset.substring(0, selOffs));
                    drawRect(
                            selX, y - 1, selX + 1, y + 1 + this.fontRenderer.FONT_HEIGHT, -3092272
                    );
                }
            }

            if (this.isTextSelected) {
                this.renderSelectedTextBG(strOffset, bgmny, bgmxy);
            }
        } else {
            this.drawString(this.fontRenderer, RenderUtils.trimStringToWidth(this.fontRenderer, this.text, this.width - 8), x, y, this.isEnabled ? 14737632 : 7368816);
        }
    }

    public void setMaxStringLength(int num) {
        this.maxStringLength = num;
    }
}

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

package com.gitlab.cdagaming.craftpresence.utils.gui.integrations;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.src.GuiButton;
import net.minecraft.src.Tessellator;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public abstract class GuiSlot {
    private final Minecraft mc;
    private final int width;
    private final int height;
    private final int top;
    private final int bottom;
    private final int right;
    private final int left;
    private final int posZ;
    private int scrollUpButtonID;
    private int scrollDownButtonID;
    private float initialClickY = -2.0F;
    private float scrollMultiplier;
    private float amountScrolled;
    private int selectedElement = -1;
    private long lastClicked = 0L;
    private boolean field_25123_p = true;

    public GuiSlot(Minecraft minecraft, int i, int j, int k, int l, int m) {
        this.mc = minecraft;
        this.width = i;
        this.height = j;
        this.top = k;
        this.bottom = l;
        this.posZ = m;
        this.left = 0;
        this.right = i;
    }

    protected abstract int getSize();

    protected abstract void elementClicked(int i, boolean bl);

    protected abstract boolean isSelected(int i);

    protected abstract int getContentHeight();

    protected abstract void drawBackground();

    protected abstract void drawSlot(int i, int j, int k, int l, Tessellator tessellator);

    public void registerScrollButtons(List list, int i, int j) {
        this.scrollUpButtonID = i;
        this.scrollDownButtonID = j;
    }

    private void bindAmountScrolled() {
        int var1 = this.getContentHeight() - (this.bottom - this.top - 4);
        if (var1 < 0) {
            var1 /= 2;
        }

        if (this.amountScrolled < 0.0F) {
            this.amountScrolled = 0.0F;
        }

        if (this.amountScrolled > (float)var1) {
            this.amountScrolled = (float)var1;
        }

    }

    public void actionPerformed(GuiButton guiButton) {
        if (guiButton.enabled) {
            if (guiButton.id == this.scrollUpButtonID) {
                this.amountScrolled -= (float)(this.posZ * 2 / 3);
                this.initialClickY = -2.0F;
                this.bindAmountScrolled();
            } else if (guiButton.id == this.scrollDownButtonID) {
                this.amountScrolled += (float)(this.posZ * 2 / 3);
                this.initialClickY = -2.0F;
                this.bindAmountScrolled();
            }

        }
    }

    public void drawScreen(int i, int j, float f) {
        this.drawBackground();
        int var4 = this.getSize();
        int var5 = this.width / 2 + 124;
        int var6 = var5 + 6;
        int var9;
        int var11;
        int var18;
        if (Mouse.isButtonDown(0)) {
            if (this.initialClickY == -1.0F) {
                if (j >= this.top && j <= this.bottom) {
                    int var7 = this.width / 2 - 110;
                    int var8 = this.width / 2 + 110;
                    var9 = (j - this.top + (int)this.amountScrolled - 2) / this.posZ;
                    if (i >= var7 && i <= var8 && var9 >= 0 && var9 < var4) {
                        boolean var10 = var9 == this.selectedElement && System.currentTimeMillis() - this.lastClicked < 250L;
                        this.elementClicked(var9, var10);
                        this.selectedElement = var9;
                        this.lastClicked = System.currentTimeMillis();
                    }

                    if (i >= var5 && i <= var6) {
                        this.scrollMultiplier = -1.0F;
                        var18 = this.getContentHeight() - (this.bottom - this.top - 4);
                        if (var18 < 1) {
                            var18 = 1;
                        }

                        var11 = (this.bottom - this.top) * (this.bottom - this.top) / this.getContentHeight();
                        if (var11 < 32) {
                            var11 = 32;
                        }

                        if (var11 > this.bottom - this.top - 8) {
                            var11 = this.bottom - this.top - 8;
                        }

                        this.scrollMultiplier /= (float)(this.bottom - this.top - var11) / (float)var18;
                    } else {
                        this.scrollMultiplier = 1.0F;
                    }

                    this.initialClickY = (float)j;
                } else {
                    this.initialClickY = -2.0F;
                }
            } else if (this.initialClickY >= 0.0F) {
                this.amountScrolled -= ((float)j - this.initialClickY) * this.scrollMultiplier;
                this.initialClickY = (float)j;
            }
        } else {
            this.initialClickY = -1.0F;
        }

        this.bindAmountScrolled();
        GL11.glDisable(2896);
        GL11.glDisable(2912);
        Tessellator var15 = Tessellator.instance;
        GL11.glBindTexture(3553, this.mc.renderEngine.getTexture("/gui/background.png"));
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        float var16 = 32.0F;
        var15.startDrawingQuads();
        var15.setColorOpaque_I(2105376);
        var15.addVertexWithUV((double)this.left, (double)this.bottom, 0.0, (double)((float)this.left / var16), (double)((float)(this.bottom + (int)this.amountScrolled) / var16));
        var15.addVertexWithUV((double)this.right, (double)this.bottom, 0.0, (double)((float)this.right / var16), (double)((float)(this.bottom + (int)this.amountScrolled) / var16));
        var15.addVertexWithUV((double)this.right, (double)this.top, 0.0, (double)((float)this.right / var16), (double)((float)(this.top + (int)this.amountScrolled) / var16));
        var15.addVertexWithUV((double)this.left, (double)this.top, 0.0, (double)((float)this.left / var16), (double)((float)(this.top + (int)this.amountScrolled) / var16));
        var15.draw();

        int var12;
        for(var9 = 0; var9 < var4; ++var9) {
            var18 = this.width / 2 - 92 - 16;
            var11 = this.top + 4 + var9 * this.posZ - (int)this.amountScrolled;
            var12 = this.posZ - 4;
            if (this.field_25123_p && this.isSelected(var9)) {
                int var13 = this.width / 2 - 110;
                int var14 = this.width / 2 + 110;
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                GL11.glDisable(3553);
                var15.startDrawingQuads();
                var15.setColorOpaque_I(8421504);
                var15.addVertexWithUV((double)var13, (double)(var11 + var12 + 2), 0.0, 0.0, 1.0);
                var15.addVertexWithUV((double)var14, (double)(var11 + var12 + 2), 0.0, 1.0, 1.0);
                var15.addVertexWithUV((double)var14, (double)(var11 - 2), 0.0, 1.0, 0.0);
                var15.addVertexWithUV((double)var13, (double)(var11 - 2), 0.0, 0.0, 0.0);
                var15.setColorOpaque_I(0);
                var15.addVertexWithUV((double)(var13 + 1), (double)(var11 + var12 + 1), 0.0, 0.0, 1.0);
                var15.addVertexWithUV((double)(var14 - 1), (double)(var11 + var12 + 1), 0.0, 1.0, 1.0);
                var15.addVertexWithUV((double)(var14 - 1), (double)(var11 - 1), 0.0, 1.0, 0.0);
                var15.addVertexWithUV((double)(var13 + 1), (double)(var11 - 1), 0.0, 0.0, 0.0);
                var15.draw();
                GL11.glEnable(3553);
            }

            this.drawSlot(var9, var18, var11, var12, var15);
        }

        byte var17 = 4;
        this.overlayBackground(0, this.top, 255, 255);
        this.overlayBackground(this.bottom, this.height, 255, 255);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3008);
        GL11.glShadeModel(7425);
        GL11.glDisable(3553);
        var15.startDrawingQuads();
        var15.setColorRGBA_I(0, 0);
        var15.addVertexWithUV((double)this.left, (double)(this.top + var17), 0.0, 0.0, 1.0);
        var15.addVertexWithUV((double)this.right, (double)(this.top + var17), 0.0, 1.0, 1.0);
        var15.setColorRGBA_I(0, 255);
        var15.addVertexWithUV((double)this.right, (double)this.top, 0.0, 1.0, 0.0);
        var15.addVertexWithUV((double)this.left, (double)this.top, 0.0, 0.0, 0.0);
        var15.draw();
        var15.startDrawingQuads();
        var15.setColorRGBA_I(0, 255);
        var15.addVertexWithUV((double)this.left, (double)this.bottom, 0.0, 0.0, 1.0);
        var15.addVertexWithUV((double)this.right, (double)this.bottom, 0.0, 1.0, 1.0);
        var15.setColorRGBA_I(0, 0);
        var15.addVertexWithUV((double)this.right, (double)(this.bottom - var17), 0.0, 1.0, 0.0);
        var15.addVertexWithUV((double)this.left, (double)(this.bottom - var17), 0.0, 0.0, 0.0);
        var15.draw();
        var18 = this.getContentHeight() - (this.bottom - this.top - 4);
        if (var18 > 0) {
            var11 = (this.bottom - this.top) * (this.bottom - this.top) / this.getContentHeight();
            if (var11 < 32) {
                var11 = 32;
            }

            if (var11 > this.bottom - this.top - 8) {
                var11 = this.bottom - this.top - 8;
            }

            var12 = (int)this.amountScrolled * (this.bottom - this.top - var11) / var18 + this.top;
            if (var12 < this.top) {
                var12 = this.top;
            }

            var15.startDrawingQuads();
            var15.setColorRGBA_I(0, 255);
            var15.addVertexWithUV((double)var5, (double)this.bottom, 0.0, 0.0, 1.0);
            var15.addVertexWithUV((double)var6, (double)this.bottom, 0.0, 1.0, 1.0);
            var15.addVertexWithUV((double)var6, (double)this.top, 0.0, 1.0, 0.0);
            var15.addVertexWithUV((double)var5, (double)this.top, 0.0, 0.0, 0.0);
            var15.draw();
            var15.startDrawingQuads();
            var15.setColorRGBA_I(8421504, 255);
            var15.addVertexWithUV((double)var5, (double)(var12 + var11), 0.0, 0.0, 1.0);
            var15.addVertexWithUV((double)var6, (double)(var12 + var11), 0.0, 1.0, 1.0);
            var15.addVertexWithUV((double)var6, (double)var12, 0.0, 1.0, 0.0);
            var15.addVertexWithUV((double)var5, (double)var12, 0.0, 0.0, 0.0);
            var15.draw();
            var15.startDrawingQuads();
            var15.setColorRGBA_I(12632256, 255);
            var15.addVertexWithUV((double)var5, (double)(var12 + var11 - 1), 0.0, 0.0, 1.0);
            var15.addVertexWithUV((double)(var6 - 1), (double)(var12 + var11 - 1), 0.0, 1.0, 1.0);
            var15.addVertexWithUV((double)(var6 - 1), (double)var12, 0.0, 1.0, 0.0);
            var15.addVertexWithUV((double)var5, (double)var12, 0.0, 0.0, 0.0);
            var15.draw();
        }

        GL11.glEnable(3553);
        GL11.glShadeModel(7424);
        GL11.glEnable(3008);
        GL11.glDisable(3042);
    }

    private void overlayBackground(int i, int j, int k, int l) {
        Tessellator var5 = Tessellator.instance;
        GL11.glBindTexture(3553, this.mc.renderEngine.getTexture("/gui/background.png"));
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        float var6 = 32.0F;
        var5.startDrawingQuads();
        var5.setColorRGBA_I(4210752, l);
        var5.addVertexWithUV(0.0, (double)j, 0.0, 0.0, (double)((float)j / var6));
        var5.addVertexWithUV((double)this.width, (double)j, 0.0, (double)((float)this.width / var6), (double)((float)j / var6));
        var5.setColorRGBA_I(4210752, k);
        var5.addVertexWithUV((double)this.width, (double)i, 0.0, (double)((float)this.width / var6), (double)((float)i / var6));
        var5.addVertexWithUV(0.0, (double)i, 0.0, 0.0, (double)((float)i / var6));
        var5.draw();
    }
}

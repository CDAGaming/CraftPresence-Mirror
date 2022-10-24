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

import net.minecraft.src.FontRenderer;
import net.minecraft.src.Gui;
import net.minecraft.src.GuiScreen;

public class GuiDisableButton extends Gui {
    private final FontRenderer field_22080_c;
    private final int field_22079_d;
    private final int field_22078_e;
    private final int field_22077_f;
    private final int field_22076_g;
    private String field_22075_h;
    private int field_22074_i;
    private int field_22073_k;
    public boolean field_22082_a = false;
    public boolean field_22081_b = true;

    public GuiDisableButton(FontRenderer fontRenderer, int i, int j, int k, int l, String string) {
        this.field_22080_c = fontRenderer;
        this.field_22079_d = i;
        this.field_22078_e = j;
        this.field_22077_f = k;
        this.field_22076_g = l;
        this.func_22068_a(string);
    }

    public void func_22068_a(String string) {
        this.field_22075_h = string;
    }

    public String func_22071_a() {
        return this.field_22075_h;
    }

    public void func_22070_b() {
        ++this.field_22073_k;
    }

    public void func_22072_a(char c, int i) {
        if (this.field_22081_b && this.field_22082_a) {
            if (c == 22) {
                String var3 = GuiScreen.func_574_c();
                if (var3 == null) {
                    var3 = "";
                }

                int var4 = 32 - this.field_22075_h.length();
                if (var4 > var3.length()) {
                    var4 = var3.length();
                }

                if (var4 > 0) {
                    this.field_22075_h = this.field_22075_h + var3.substring(0, var4);
                }
            }

            if (i == 14 && this.field_22075_h.length() > 0) {
                this.field_22075_h = this.field_22075_h.substring(0, this.field_22075_h.length() - 1);
            }

            if (" !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_'abcdefghijklmnopqrstuvwxyz{|}~⌂ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»".indexOf(c) >= 0 && (this.field_22075_h.length() < this.field_22074_i || this.field_22074_i == 0)) {
                this.field_22075_h = this.field_22075_h + c;
            }

        }
    }

    public void func_22069_a(int i, int j, int k) {
        boolean var4 = this.field_22081_b && i >= this.field_22079_d && i < this.field_22079_d + this.field_22077_f && j >= this.field_22078_e && j < this.field_22078_e + this.field_22076_g;
        if (var4 && !this.field_22082_a) {
            this.field_22073_k = 0;
        }

        this.field_22082_a = var4;
    }

    public void func_22067_c() {
        this.func_551_a(this.field_22079_d - 1, this.field_22078_e - 1, this.field_22079_d + this.field_22077_f + 1, this.field_22078_e + this.field_22076_g + 1, -6250336);
        this.func_551_a(this.field_22079_d, this.field_22078_e, this.field_22079_d + this.field_22077_f, this.field_22078_e + this.field_22076_g, -16777216);
        if (this.field_22081_b) {
            boolean var1 = this.field_22082_a && this.field_22073_k / 6 % 2 == 0;
            this.func_547_b(this.field_22080_c, this.field_22075_h + (var1 ? "_" : ""), this.field_22079_d + 4, this.field_22078_e + (this.field_22076_g - 8) / 2, 14737632);
        } else {
            this.func_547_b(this.field_22080_c, this.field_22075_h, this.field_22079_d + 4, this.field_22078_e + (this.field_22076_g - 8) / 2, 7368816);
        }

    }

    public void func_22066_a(int i) {
        this.field_22074_i = i;
    }
}

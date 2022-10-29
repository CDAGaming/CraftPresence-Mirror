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

package com.gitlab.cdagaming.craftpresence.config.category;

import com.gitlab.cdagaming.craftpresence.ModUtils;
import com.gitlab.cdagaming.craftpresence.config.Module;
import com.gitlab.cdagaming.craftpresence.impl.Pair;
import com.gitlab.cdagaming.craftpresence.impl.Tuple;
import com.gitlab.cdagaming.craftpresence.utils.StringUtils;
import com.google.common.collect.ImmutableMap;

import java.io.Serializable;
import java.util.Map;

public class Display extends Module implements Serializable {
    private static final long serialVersionUID = -3302764075156017733L;
    private static Display DEFAULT;

    public static Display getDefaults() {
        if (DEFAULT == null) {
            DEFAULT = new Display();
        }
        return DEFAULT;
    }

    public String gameStateTextFormat = "&SERVER& &PACK&";
    public String detailsTextFormat = "&MAINMENU&|&DIMENSION&";
    public String largeImageTextFormat = "&MAINMENU&|&DIMENSION&";
    public String smallImageTextFormat = "&SERVER& &PACK&";
    public String largeImageKeyFormat = "&MAINMENU&|&DIMENSION&";
    public String smallImageKeyFormat = "&SERVER&|&PACK&";
    public Map<String, Pair<String, String>> buttonMessages = ImmutableMap.<String, Pair<String, String>>builder()
            .put("default", new Pair<>(
                    ModUtils.TRANSLATOR.translate("craftpresence.defaults.display.button.label"),
                    ModUtils.TRANSLATOR.translate("craftpresence.defaults.display.button.url")
            ))
            .build();
    public Map<String, String> dynamicIcons = ImmutableMap.<String, String>builder()
            .put("default", ModUtils.TRANSLATOR.translate("craftpresence.defaults.display.image.url"))
            .build();

    @Override
    public Object getProperty(final String name) {
        return StringUtils.lookupObject(Display.class, this, name);
    }

    @Override
    public void setProperty(final String name, final Object value) {
        StringUtils.updateField(Display.class, this, new Tuple<>(name, value, null));
    }

    @Override
    public void resetProperty(final String name) {
        setProperty(name, getDefaults().getProperty(name));
    }
}

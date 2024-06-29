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

package com.gitlab.cdagaming.unilib.core.integrations.screen;

import com.gitlab.cdagaming.unilib.core.CoreUtils;
import com.gitlab.cdagaming.unilib.core.config.element.ColorData;
import com.gitlab.cdagaming.unilib.core.config.element.ColorSection;
import io.github.cdagaming.unicore.impl.Tuple;

import java.util.function.Function;

/**
 * Constant Variables and Methods used for Rendering Operations
 *
 * @author CDAGaming
 */
public class ScreenConstants {
    /**
     * The Default Tooltip Background Info
     */
    public static final ColorData DEFAULT_TOOLTIP_BACKGROUND = new ColorData(
            new ColorSection(16, 0, 16, 240)
    );
    /**
     * The Default Tooltip Border Info
     */
    public static final ColorData DEFAULT_TOOLTIP_BORDER = new ColorData(
            new ColorSection(80, 0, 255, 80),
            new ColorSection(40, 0, 127, 80)
    );
    /**
     * The Default Screen Background Info
     */
    public static final ColorData DEFAULT_SCREEN_BACKGROUND = new ColorData(
            new ColorSection(64, 64, 64, 255),
            getDefaultGUIBackground()
    );
    /**
     * The Default Alternative Screen Background Info
     */
    public static final ColorData DEFAULT_ALT_SCREEN_BACKGROUND = new ColorData(
            new ColorSection(16, 16, 16, 192),
            new ColorSection(16, 16, 16, 208)
    );
    /**
     * The Default Widget Background Resources
     */
    private static final Function<Integer, String> DEFAULT_BUTTON_BACKGROUND_SUPPLIER = (protocol) -> "minecraft:" + (CoreUtils.isLegacyHard(protocol) ? "/gui/gui.png" : "textures/gui/widgets.png");
    /**
     * The Default Widget Background Resources
     */
    private static final String DEFAULT_BUTTON_BACKGROUND = getDefaultButtonBackground(CoreUtils.MCBuildProtocol);
    /**
     * The Default Screen Background Resources
     */
    private static final Function<Integer, String> DEFAULT_GUI_BACKGROUND_SUPPLIER = (protocol) -> "minecraft:" + (CoreUtils.isLegacyHard(protocol) ? (CoreUtils.isLegacyAlpha(protocol) ? "/dirt.png" : "/gui/background.png") : "textures/gui/options_background.png");
    /**
     * The Default Screen Background Resources
     */
    private static final String DEFAULT_GUI_BACKGROUND = getDefaultGUIBackground(CoreUtils.MCBuildProtocol);
    /**
     * The default Tooltip Rendering Info
     */
    private static final Tuple<Boolean, ColorData, ColorData> DEFAULT_TOOLTIP = new Tuple<>(
            true, DEFAULT_TOOLTIP_BACKGROUND, DEFAULT_TOOLTIP_BORDER
    );
    /**
     * The tooltip Rendering Info for an Empty Background and Border
     */
    private static final Tuple<Boolean, ColorData, ColorData> EMPTY_TOOLTIP = new Tuple<>(
            true, null, null
    );

    /**
     * Retrieve The Default Widget Background Resources
     *
     * @param protocol The Protocol to Target for this operation
     * @return The Default Widget Background Resources
     */
    public static String getDefaultButtonBackground(final int protocol) {
        return DEFAULT_BUTTON_BACKGROUND_SUPPLIER.apply(protocol);
    }

    /**
     * Retrieve The Default Widget Background Resources
     *
     * @return The Default Widget Background Resources
     */
    public static String getDefaultButtonBackground() {
        return DEFAULT_BUTTON_BACKGROUND;
    }

    /**
     * Retrieve The Default Screen Background Resources
     *
     * @param protocol The Protocol to Target for this operation
     * @return The Default Screen Background Resources
     */
    public static String getDefaultGUIBackground(final int protocol) {
        return DEFAULT_GUI_BACKGROUND_SUPPLIER.apply(protocol);
    }

    /**
     * Retrieve The Default Screen Background Resources
     *
     * @return The Default Screen Background Resources
     */
    public static String getDefaultGUIBackground() {
        return DEFAULT_GUI_BACKGROUND;
    }

    /**
     * Retrieve the tooltip Rendering Info for an Empty Background and Border
     *
     * @return the tooltip Rendering Info for an Empty Background and Border
     */
    public static Tuple<Boolean, ColorData, ColorData> getEmptyTooltip() {
        return EMPTY_TOOLTIP;
    }

    /**
     * Retrieve the Default Tooltip Rendering Info
     *
     * @return the default Tooltip Rendering Info
     */
    public static Tuple<Boolean, ColorData, ColorData> getDefaultTooltip() {
        return DEFAULT_TOOLTIP;
    }

    /**
     * Sets the new default tooltip rendering info
     *
     * @param renderTooltips  Whether tooltips should be rendered
     * @param backgroundColor The background color info
     * @param borderColor     The border color info
     */
    public static void setDefaultTooltip(
            final boolean renderTooltips,
            final ColorData backgroundColor,
            final ColorData borderColor
    ) {
        getDefaultTooltip().put(renderTooltips, backgroundColor, borderColor);
    }
}

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

package com.gitlab.cdagaming.unilib.core.impl.screen;

import com.gitlab.cdagaming.unilib.core.CoreUtils;
import io.github.cdagaming.unicore.utils.StringUtils;

import java.awt.*;
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
     * The Default Screen Background Info
     */
    public static final ColorData DEFAULT_SCREEN_BACKGROUND = new ColorData(
            new ColorSection(64, 64, 64, 255),
            getDefaultGUIBackground()
    );
    /**
     * The default Tooltip Rendering Info
     */
    private static final TooltipData DEFAULT_TOOLTIP = new TooltipData(
            true, DEFAULT_TOOLTIP_BACKGROUND, DEFAULT_TOOLTIP_BORDER
    );
    /**
     * The tooltip Rendering Info for an Empty Background and Border
     */
    private static final TooltipData EMPTY_TOOLTIP = new TooltipData(
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
    public static TooltipData getEmptyTooltip() {
        return EMPTY_TOOLTIP;
    }

    /**
     * Retrieve the Default Tooltip Rendering Info
     *
     * @return the default Tooltip Rendering Info
     */
    public static TooltipData getDefaultTooltip() {
        return DEFAULT_TOOLTIP;
    }

    public record TooltipData(boolean renderTooltips, ColorData backgroundColor, ColorData borderColor) {
    }

    public record ColorData(ColorSection start, ColorSection end, String texLocation) {
        public ColorData(ColorSection start, ColorSection end) {
            this(start, end, "");
        }

        public ColorData(ColorSection start, String texLocation) {
            this(start, null, texLocation);
        }

        public ColorData(ColorSection start) {
            this(start, "");
        }

        public ColorData(Color start, Color end, String texLocation) {
            this(new ColorSection(start), end != null ? new ColorSection(end) : null, texLocation);
        }

        public ColorData(Color start, Color end) {
            this(start, end, "");
        }

        public ColorData(Color start, String texLocation) {
            this(start, null, texLocation);
        }

        public ColorData(Color start) {
            this(start, "");
        }

        public ColorData(String texLocation) {
            this(new ColorSection(), null, texLocation);
        }

        public ColorData() {
            this(new ColorSection(), null, "");
        }

        public ColorData(ColorData other) {
            this(other.start, other.end, other.texLocation);
        }

        public boolean hasEnd() {
            return end != null;
        }

        @Override
        public ColorSection end() {
            return hasEnd() ? end : start();
        }

        public Color startColor() {
            return start().color();
        }

        public Color endColor() {
            return end().color();
        }

        public boolean hasTexLocation() {
            return !StringUtils.isNullOrEmpty(texLocation);
        }

        @Override
        public String texLocation() {
            return hasTexLocation() ? texLocation : "";
        }
    }

    public record ColorSection(int red, int green, int blue, int alpha) {
        public ColorSection(Color color) {
            this(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
        }

        public ColorSection(ColorSection other) {
            this(other.red, other.green, other.blue, other.alpha);
        }

        public ColorSection() {
            this(Color.white);
        }

        public Color color() {
            return new Color(red(), green(), blue(), alpha());
        }
    }
}

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

package com.gitlab.cdagaming.craftpresence.utils.gui;

import com.gitlab.cdagaming.craftpresence.impl.Pair;
import com.gitlab.cdagaming.craftpresence.impl.Tuple;
import com.gitlab.cdagaming.craftpresence.utils.ImageUtils;
import com.gitlab.cdagaming.craftpresence.utils.StringUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.awt.*;
import java.io.File;
import java.util.List;

/**
 * Rendering Utilities used to Parse Screen Data and handle rendering tasks
 *
 * @author CDAGaming
 */
@SuppressWarnings("DuplicatedCode")
public class RenderUtils {
    /**
     * Adds a Scheduled/Queued Task to Display the Specified Gui Screen
     *
     * @param mc           The current game instance
     * @param targetScreen The target Gui Screen to display
     */
    public static void openScreen(final Minecraft mc, final GuiScreen targetScreen) {
        mc.addScheduledTask(() -> mc.displayGuiScreen(targetScreen));
    }

    /**
     * Renders a Gradient Box from the defined arguments
     *
     * @param posX            The Starting X Position to render the object
     * @param posY            The Starting Y Position to render the object
     * @param width           The full width for the object to render to
     * @param height          The full height for the object to render to
     * @param zLevel          The Z level position for the object to render at
     * @param borderColor     The starting border color for the object
     * @param borderColorEnd  The ending border color for the object
     * @param border          The full width of the border for the object
     * @param contentColor    The starting content color for the object
     * @param contentColorEnd The ending content color for the object
     */
    public static void drawGradientBox(final double posX, final double posY,
                                       final double width, final double height,
                                       final double zLevel,
                                       final Object borderColor, final Object borderColorEnd, final int border,
                                       final Object contentColor, final Object contentColorEnd) {
        final double canvasWidth = width - (border * 2);
        final double canvasHeight = height - (border * 2);

        final double canvasRight = posX + border + canvasWidth;
        final double canvasBottom = posY + border + canvasHeight;

        // Draw Borders
        if (borderColor != null) {
            // Top Left
            drawGradient(posX, posX + border, posY, canvasBottom + border, zLevel, borderColor, borderColorEnd);
            // Top Right
            drawGradient(canvasRight, canvasRight + border, posY, canvasBottom + border, zLevel, borderColor, borderColorEnd);
            // Bottom Left
            drawGradient(posX, canvasRight + border, canvasBottom, canvasBottom + border, zLevel, borderColor, borderColorEnd);
            // Bottom Right
            drawGradient(posX, canvasRight + border, posY, posY + border, zLevel, borderColor, borderColorEnd);
        }

        // Draw Content Box
        if (contentColor != null) {
            drawGradient(posX + border, canvasRight, posY + border, canvasBottom, zLevel, contentColor, contentColorEnd);
        }
    }

    /**
     * Renders a Slider Object from the defined arguments
     *
     * @param mc          The current game instance
     * @param x           The Starting X Position to render the slider
     * @param y           The Starting Y Position to render the slider
     * @param u           The U Mapping Value
     * @param v           The V Mapping Value
     * @param width       The full width for the slider to render to
     * @param height      The full height for the slider to render to
     * @param zLevel      The Z level position for the slider to render at
     * @param texLocation The game texture to render the slider as
     */
    public static void renderSlider(@Nonnull final Minecraft mc,
                                    final int x, final int y,
                                    final int u, final int v,
                                    final int width, final int height,
                                    final double zLevel,
                                    final ResourceLocation texLocation) {
        try {
            if (texLocation != null) {
                mc.getTextureManager().bindTexture(texLocation);
            }
        } catch (Exception ignored) {
            return;
        }
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        blit(x, y, zLevel, u, v, width, height);
        blit(x + 4, y, zLevel, u + 196, v, width, height);
    }

    /**
     * Renders a Button Object from the defined arguments
     *
     * @param mc          The current game instance
     * @param x           The Starting X Position to render the button
     * @param y           The Starting Y Position to render the button
     * @param width       The full width for the button to render to
     * @param height      The full height for the button to render to
     * @param hoverState  The hover state for the button
     * @param zLevel      The Z level position for the button to render at
     * @param texLocation The game texture to render the button as
     */
    public static void renderButton(@Nonnull final Minecraft mc,
                                    final int x, final int y,
                                    final int width, final int height,
                                    final int hoverState, final double zLevel,
                                    final ResourceLocation texLocation) {
        try {
            if (texLocation != null) {
                mc.getTextureManager().bindTexture(texLocation);
            }
        } catch (Exception ignored) {
        }
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_DEPTH_TEST);

        final int v = 46 + hoverState * 20;
        final int xOffset = width / 2;

        blit(x, y, zLevel, 0, v, xOffset, height);
        blit(x + xOffset, y, zLevel, 200 - xOffset, v, xOffset, height);
    }

    /**
     * Draws a Textured Rectangle, following the defined arguments
     *
     * @param mc            The current game instance
     * @param left          The Left Position of the Object
     * @param right         The Right Position of the Object
     * @param top           The Top Position of the Object
     * @param bottom        The Bottom Position of the Object
     * @param zLevel        The Z Level Position of the Object
     * @param minU          The minimum horizontal axis to render this Object by
     * @param maxU          The maximum horizontal axis to render this Object by
     * @param minV          The minimum vertical axis to render this Object by
     * @param maxV          The minimum vertical axis to render this Object by
     * @param startColorObj The starting texture RGB data to interpret
     * @param endColorObj   The starting texture RGB data to interpret
     * @param texLocation   The game texture to render the object as
     */
    public static void drawTexture(@Nonnull final Minecraft mc,
                                   final double left, final double right, final double top, final double bottom,
                                   final double zLevel,
                                   final double minU, final double maxU,
                                   final double minV, final double maxV,
                                   Object startColorObj, Object endColorObj,
                                   final ResourceLocation texLocation) {
        try {
            if (texLocation != null) {
                mc.getTextureManager().bindTexture(texLocation);
            }
        } catch (Exception ignored) {
            return;
        }

        final Pair<Color, Color> colorData = StringUtils.findColor(startColorObj, endColorObj);
        final Color startColor = colorData.getFirst();
        final Color endColor = colorData.getSecond();
        if (startColor == null || endColor == null) {
            return;
        }

        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_FOG);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        buffer.pos(left, bottom, zLevel).tex(minU, maxV).color(startColor.getRed(), startColor.getGreen(), startColor.getBlue(), startColor.getAlpha()).endVertex();
        buffer.pos(right, bottom, zLevel).tex(maxU, maxV).color(startColor.getRed(), startColor.getGreen(), startColor.getBlue(), startColor.getAlpha()).endVertex();
        buffer.pos(right, top, zLevel).tex(maxU, minV).color(endColor.getRed(), endColor.getGreen(), endColor.getBlue(), endColor.getAlpha()).endVertex();
        buffer.pos(left, top, zLevel).tex(minU, minV).color(endColor.getRed(), endColor.getGreen(), endColor.getBlue(), endColor.getAlpha()).endVertex();
        tessellator.draw();
    }

    /**
     * Draws a Textured Gradient Rectangle, following the defined arguments
     *
     * @param mc            The current game instance
     * @param left          The Left Position of the Object
     * @param right         The Right Position of the Object
     * @param top           The Top Position of the Object
     * @param bottom        The Bottom Position of the Object
     * @param zLevel        The Z Level Position of the Object
     * @param minU          The minimum horizontal axis to render this Object by
     * @param maxU          The maximum horizontal axis to render this Object by
     * @param minV          The minimum vertical axis to render this Object by
     * @param maxV          The minimum vertical axis to render this Object by
     * @param startColorObj The starting texture RGB data to interpret
     * @param endColorObj   The starting texture RGB data to interpret
     * @param texLocation   The game texture to render the object as
     */
    public static void drawTextureGradient(@Nonnull final Minecraft mc,
                                           final double left, final double right, final double top, final double bottom,
                                           final double zLevel,
                                           final double minU, final double maxU,
                                           final double minV, final double maxV,
                                           Object startColorObj, Object endColorObj,
                                           final ResourceLocation texLocation) {
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glShadeModel(GL11.GL_SMOOTH);

        drawTexture(mc,
                left, right,
                top, bottom,
                zLevel,
                minU, maxU,
                minV, maxV,
                startColorObj, endColorObj,
                texLocation
        );

        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

    /**
     * Draws a Gradient Rectangle, following the defined arguments
     *
     * @param left          The Left side length of the Object
     * @param right         The Right side length of the Object
     * @param top           The top length of the Object
     * @param bottom        The bottom length of the Object
     * @param zLevel        The Z Level Position of the Object
     * @param startColorObj The Starting Color Data
     * @param endColorObj   The Ending Color Data
     */
    public static void drawGradient(final double left, final double right, final double top, final double bottom,
                                    final double zLevel,
                                    Object startColorObj, Object endColorObj) {
        final Pair<Color, Color> colorData = StringUtils.findColor(startColorObj, endColorObj);
        final Color startColor = colorData.getFirst();
        final Color endColor = colorData.getSecond();
        if (startColor == null || endColor == null) {
            return;
        }

        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glShadeModel(GL11.GL_SMOOTH);

        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(left, bottom, zLevel).color(endColor.getRed(), endColor.getGreen(), endColor.getBlue(), endColor.getAlpha()).endVertex();
        buffer.pos(right, bottom, zLevel).color(endColor.getRed(), endColor.getGreen(), endColor.getBlue(), endColor.getAlpha()).endVertex();
        buffer.pos(right, top, zLevel).color(startColor.getRed(), startColor.getGreen(), startColor.getBlue(), startColor.getAlpha()).endVertex();
        buffer.pos(left, top, zLevel).color(startColor.getRed(), startColor.getGreen(), startColor.getBlue(), startColor.getAlpha()).endVertex();
        tessellator.draw();

        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

    public static void blit(final double xPos, final double yPos,
                            final double zLevel,
                            final double uOffset, final double vOffset,
                            final double uWidth, final double vHeight) {
        blit(xPos, yPos, zLevel, uOffset, vOffset, uWidth, vHeight, 256, 256);
    }

    public static void blit(final double xPos, final double yPos,
                            final double zLevel,
                            final double uOffset, final double vOffset,
                            final double uWidth, final double vHeight,
                            final double textureWidth, final double textureHeight) {
        innerBlit(xPos, xPos + uWidth, yPos, yPos + vHeight,
                zLevel,
                uWidth, vHeight,
                uOffset, vOffset,
                textureWidth, textureHeight
        );
    }

    public static void innerBlit(final double left, final double right, final double top, final double bottom,
                                 final double zLevel,
                                 final double uWidth, final double vHeight,
                                 final double uOffset, final double vOffset,
                                 final double textureWidth, final double textureHeight) {
        innerBlit(left, right, top, bottom,
                zLevel,
                (uOffset + 0.0D) / textureWidth, (uOffset + uWidth) / textureWidth,
                (vOffset + 0.0D) / textureHeight, (vOffset + vHeight) / textureHeight
        );
    }

    public static void innerBlit(final double left, final double right, final double top, final double bottom,
                                 final double zLevel,
                                 final double minU, final double maxU,
                                 final double minV, final double maxV) {
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(left, bottom, zLevel).tex(minU, maxV).endVertex();
        buffer.pos(right, bottom, zLevel).tex(maxU, maxV).endVertex();
        buffer.pos(right, top, zLevel).tex(maxU, minV).endVertex();
        buffer.pos(left, top, zLevel).tex(minU, minV).endVertex();
        tessellator.draw();
    }

    /**
     * Define viewable rendering boundaries, utilizing glScissor
     *
     * @param xPos   The Starting X Position of the Object
     * @param yPos   The Starting Y Position of the Object
     * @param width  The width to render the data to
     * @param height The height to render the data to
     */
    public static void drawWithin(final int xPos, final int yPos, final int width, final int height) {
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(xPos, yPos, width, height);
    }

    /**
     * Disables current rendering boundary flags, mainly glScissor
     */
    public static void drawAnywhere() {
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    /**
     * Computes the current GUI scale. Calling this method is equivalent to the following:<pre><code>
     * Minecraft mc = Minecraft.getMinecraft();
     * int scale = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight).getScaleFactor();</code></pre>
     *
     * @param mc The Minecraft Instance
     * @return the current GUI scale
     */
    public static int computeGuiScale(@Nonnull final Minecraft mc) {
        int scaleFactor = 1;

        int k = mc.gameSettings.guiScale;
        if (k == 0) {
            k = 1000;
        }

        while (scaleFactor < k && mc.displayWidth / (scaleFactor + 1) >= 320 && mc.displayHeight / (scaleFactor + 1) >= 240) {
            ++scaleFactor;
        }
        return scaleFactor;
    }

    /**
     * Retrieve color data for the specified string, if possible
     *
     * @param texture The data to interpret
     * @return a {@link Pair} with the mapping "isColorCode:data"
     */
    public static Pair<Boolean, String> getColorData(String texture) {
        final Pair<Boolean, String> result = new Pair<>(false, texture);
        if (!StringUtils.isNullOrEmpty(texture)) {
            texture = texture.trim();
        } else {
            return result;
        }

        final boolean isColorCode = StringUtils.isValidColorCode(texture);
        if (isColorCode) {
            if (texture.length() == 6) {
                texture = "#" + texture;
            } else if (texture.startsWith("0x")) {
                texture = Long.toString(Long.decode(texture).intValue());
            }
        }
        return result.put(isColorCode, texture);
    }

    /**
     * Retrieve texture data for the specified string, if possible
     *
     * @param texture The data to interpret
     * @return a {@link Tuple} with the mapping "usingExternalData:location:resource"
     */
    public static Tuple<Boolean, String, ResourceLocation> getTextureData(String texture) {
        ResourceLocation texLocation = new ResourceLocation("");
        final Tuple<Boolean, String, ResourceLocation> result = new Tuple<>(false, "", texLocation);
        if (!StringUtils.isNullOrEmpty(texture)) {
            texture = texture.trim();
        } else {
            return result;
        }

        final boolean isColorCode = StringUtils.isValidColorCode(texture);
        boolean usingExternalTexture = false;

        if (!isColorCode) {
            usingExternalTexture = ImageUtils.isExternalImage(texture);

            // Only Perform Texture Conversion Steps if not an external Url
            // As an external Url should be parsed as-is in most use cases
            //
            // Only when we are not using an external texture, would we then need
            // to convert the path to Minecraft's normal format.
            //
            // If we are using an external texture however, then we'd just make
            // a texture name from the last part of the url and retrieve the external texture
            if (!usingExternalTexture) {
                if (texture.startsWith(":")) {
                    texture = texture.substring(1);
                }

                if (texture.contains(":")) {
                    String[] splitInput = texture.split(":", 2);
                    texLocation = new ResourceLocation(splitInput[0], splitInput[1]);
                } else {
                    texLocation = new ResourceLocation(texture);
                }
            } else {
                final String formattedConvertedName = texture.replaceFirst("file://", "");
                final String[] urlBits = formattedConvertedName.trim().split("/");
                final String textureName = urlBits[urlBits.length - 1].trim();
                texLocation = ImageUtils.getTextureFromUrl(textureName, texture.toLowerCase().startsWith("file://") ? new File(formattedConvertedName) : formattedConvertedName);
            }
        }
        return result.put(usingExternalTexture, texture, texLocation);
    }

    /**
     * Renders a Specified Multi-Line String, constrained by position and dimension arguments
     *
     * @param mc           The current game instance
     * @param textToInput  The Specified Multi-Line String, split by lines into a list
     * @param posX         The starting X position to render the String
     * @param posY         The starting Y position to render the String
     * @param screenWidth  The maximum width to allow rendering to (Text will wrap if output is greater)
     * @param screenHeight The maximum height to allow rendering to (Text will wrap if output is greater)
     * @param maxTextWidth The maximum width the output can be before wrapping
     * @param fontRenderer The font renderer to use to render the String
     * @param colorInfo    Color Data in the format of [renderTooltips,backgroundColorInfo,borderColorInfo]
     */
    public static void drawMultiLineString(@Nonnull final Minecraft mc,
                                           final List<String> textToInput,
                                           final int posX, final int posY,
                                           final int screenWidth, final int screenHeight,
                                           final int maxTextWidth,
                                           final FontRenderer fontRenderer,
                                           final Tuple<Boolean, String, String> colorInfo) {
        if (colorInfo.getFirst() && !textToInput.isEmpty() && fontRenderer != null) {
            List<String> textLines = textToInput;
            int tooltipTextWidth = 0;

            for (String textLine : textLines) {
                final int textLineWidth = fontRenderer.getStringWidth(textLine);

                if (textLineWidth > tooltipTextWidth) {
                    tooltipTextWidth = textLineWidth;
                }
            }

            boolean needsWrap = false;

            int titleLinesCount = 1;
            int tooltipX = posX + 12;
            if (tooltipX + tooltipTextWidth + 4 > screenWidth) {
                tooltipX = posX - 16 - tooltipTextWidth;
                if (tooltipX < 4) // if the tooltip doesn't fit on the screen
                {
                    if (posX > screenWidth / 2) {
                        tooltipTextWidth = posX - 12 - 8;
                    } else {
                        tooltipTextWidth = screenWidth - 16 - posX;
                    }
                    needsWrap = true;
                }
            }

            if (maxTextWidth > 0 && tooltipTextWidth > maxTextWidth) {
                tooltipTextWidth = maxTextWidth;
                needsWrap = true;
            }

            if (needsWrap) {
                final List<String> wrappedTextLines = StringUtils.newArrayList();
                int wrappedTooltipWidth = 0;
                for (int i = 0; i < textLines.size(); i++) {
                    final List<String> wrappedLine = StringUtils.splitTextByNewLine(wrapFormattedStringToWidth(fontRenderer, textLines.get(i), tooltipTextWidth));
                    if (i == 0) {
                        titleLinesCount = wrappedLine.size();
                    }

                    for (String line : wrappedLine) {
                        int lineWidth = fontRenderer.getStringWidth(line);
                        if (lineWidth > wrappedTooltipWidth) {
                            wrappedTooltipWidth = lineWidth;
                        }
                        wrappedTextLines.add(line);
                    }
                }
                tooltipTextWidth = wrappedTooltipWidth;
                textLines = wrappedTextLines;

                if (posX > screenWidth / 2) {
                    tooltipX = posX - 16 - tooltipTextWidth;
                } else {
                    tooltipX = posX + 12;
                }
            }

            int tooltipY = posY - 12;
            int tooltipHeight = 8;

            if (textLines.size() > 1) {
                tooltipHeight += (textLines.size() - 1) * 10;
                if (textLines.size() > titleLinesCount) {
                    tooltipHeight += 2; // gap between title lines and next lines
                }
            }

            if (tooltipY < 4) {
                tooltipY = 4;
            } else if (tooltipY + tooltipHeight + 4 > screenHeight) {
                tooltipY = screenHeight - tooltipHeight - 4;
            }

            final String backgroundColorInfo = colorInfo.getSecond();
            final String borderColorInfo = colorInfo.getThird();
            final int zLevel = 300;

            if (!StringUtils.isNullOrEmpty(backgroundColorInfo)) {
                final Pair<Boolean, String> backgroundColorData = getColorData(backgroundColorInfo);
                if (backgroundColorData.getFirst()) {
                    final String backgroundColor = backgroundColorData.getSecond();

                    // Draw with Colors
                    drawGradient(tooltipX - 3, tooltipX + tooltipTextWidth + 3, tooltipY - 4, tooltipY - 3, zLevel, backgroundColor, backgroundColor);
                    drawGradient(tooltipX - 3, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3, tooltipY + tooltipHeight + 4, zLevel, backgroundColor, backgroundColor);
                    drawGradient(tooltipX - 3, tooltipX + tooltipTextWidth + 3, tooltipY - 3, tooltipY + tooltipHeight + 3, zLevel, backgroundColor, backgroundColor);
                    drawGradient(tooltipX - 4, tooltipX - 3, tooltipY - 3, tooltipY + tooltipHeight + 3, zLevel, backgroundColor, backgroundColor);
                    drawGradient(tooltipX + tooltipTextWidth + 3, tooltipX + tooltipTextWidth + 4, tooltipY - 3, tooltipY + tooltipHeight + 3, zLevel, backgroundColor, backgroundColor);
                } else {
                    final Tuple<Boolean, String, ResourceLocation> textureData = getTextureData(backgroundColorInfo);
                    final ResourceLocation backGroundTexture = textureData.getThird();
                    double widthDivider = 32.0D, heightDivider = 32.0D;

                    final double width = tooltipTextWidth + 4;
                    final double height = tooltipHeight + 4;
                    if (textureData.getFirst()) {
                        widthDivider = width;
                        heightDivider = height;
                    }

                    final double left = tooltipX - 4;
                    final double right = tooltipX + width;
                    final double top = tooltipY - 4;
                    final double bottom = tooltipY + tooltipHeight + 4;

                    drawTexture(mc,
                            left, right, top, bottom,
                            0.0D,
                            0.0D, width / widthDivider,
                            0.0D, height / heightDivider,
                            Color.white, Color.white,
                            backGroundTexture
                    );
                }
            }

            if (!StringUtils.isNullOrEmpty(borderColorInfo)) {
                final Pair<Boolean, String> borderColorData = getColorData(borderColorInfo);
                if (borderColorData.getFirst()) {
                    final String borderColor = borderColorData.getSecond();

                    // Draw with Colors
                    final int borderColorCode = (borderColor.startsWith("#") ? StringUtils.getColorFrom(borderColor).getRGB() : Integer.parseInt(borderColor));
                    final String borderColorEnd = Integer.toString((borderColorCode & 0xFEFEFE) >> 1 | borderColorCode & 0xFF000000);

                    drawGradient(tooltipX - 3, tooltipX - 3 + 1, tooltipY - 3 + 1, tooltipY + tooltipHeight + 3 - 1, zLevel, borderColor, borderColorEnd);
                    drawGradient(tooltipX + tooltipTextWidth + 2, tooltipX + tooltipTextWidth + 3, tooltipY - 3 + 1, tooltipY + tooltipHeight + 3 - 1, zLevel, borderColor, borderColorEnd);
                    drawGradient(tooltipX - 3, tooltipX + tooltipTextWidth + 3, tooltipY - 3, tooltipY - 3 + 1, zLevel, borderColor, borderColor);
                    drawGradient(tooltipX - 3, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 2, tooltipY + tooltipHeight + 3, zLevel, borderColorEnd, borderColorEnd);
                } else {
                    final Tuple<Boolean, String, ResourceLocation> textureData = getTextureData(borderColorInfo);
                    final ResourceLocation borderTexture = textureData.getThird();
                    final boolean usingExternalTexture = textureData.getFirst();

                    final double border = 1;
                    final double renderX = tooltipX - 3;
                    final double renderY = tooltipY - 3;
                    final double canvasRight = tooltipX + tooltipTextWidth + 2;
                    final double canvasBottom = tooltipY + tooltipHeight + 2;

                    final double primaryDivider = (usingExternalTexture ? tooltipTextWidth + 5 : 32.0D);
                    final double secondaryDivider = (usingExternalTexture ? 1 : 32.0D);

                    // Draw Borders
                    // Top Left
                    drawTexture(mc,
                            renderX, renderX + border, renderY, canvasBottom + border,
                            zLevel,
                            0.0D, border / primaryDivider,
                            0.0D, border / secondaryDivider,
                            Color.white, Color.white,
                            borderTexture
                    );
                    // Top Right
                    drawTexture(mc,
                            canvasRight, canvasRight + border, renderY, canvasBottom + border,
                            zLevel,
                            0.0D, border / primaryDivider,
                            0.0D, border / secondaryDivider,
                            Color.white, Color.white,
                            borderTexture
                    );
                    // Bottom Left
                    drawTexture(mc,
                            renderX, canvasRight + border, canvasBottom, canvasBottom + border,
                            zLevel,
                            0.0D, border / primaryDivider,
                            0.0D, border / secondaryDivider,
                            Color.white, Color.white,
                            borderTexture
                    );
                    // Right Border
                    drawTexture(mc,
                            renderX, canvasRight + border, renderY, renderY + border,
                            zLevel,
                            0.0D, border / primaryDivider,
                            0.0D, border / secondaryDivider,
                            Color.white, Color.white,
                            borderTexture
                    );
                }
            }

            for (int lineNumber = 0; lineNumber < textLines.size(); ++lineNumber) {
                final String line = textLines.get(lineNumber);
                fontRenderer.drawStringWithShadow(line, tooltipX, tooltipY, -1);

                if (lineNumber + 1 == titleLinesCount) {
                    tooltipY += 2;
                }

                tooltipY += 10;
            }
        }
    }

    /**
     * Draws a Background onto a Gui, supporting RGBA Codes, Game Textures and Hexadecimal Colors
     *
     * @param mc             The current game instance
     * @param xPos           The Starting X Position of the Object
     * @param yPos           The Starting Y Position of the Object
     * @param width          The width to render the background to
     * @param height         The height to render the background to
     * @param offset         The vertical offset to render the background to
     * @param backgroundCode The background render data to interpret
     * @param color          The background RGB data to interpret
     */
    public static void drawBackground(@Nonnull final Minecraft mc,
                                      final double xPos, final double yPos,
                                      final double width, final double height,
                                      double offset,
                                      final String backgroundCode, final Color color) {
        if (StringUtils.isValidColorCode(backgroundCode)) {
            drawGradient(xPos, xPos + width, yPos, yPos + height, 300.0F, backgroundCode, backgroundCode);
        } else {
            final Tuple<Boolean, String, ResourceLocation> textureData = getTextureData(backgroundCode);
            final ResourceLocation texLocation = textureData.getThird();

            final double widthDivider = textureData.getFirst() ? width : 32.0D;
            final double heightDivider = textureData.getFirst() ? height : 32.0D;
            offset = textureData.getFirst() ? 0.0D : offset;

            final double left = xPos;
            final double right = xPos + width;
            final double top = yPos;
            final double bottom = yPos + height;

            drawTexture(mc,
                    left, right, top, bottom,
                    0.0D,
                    left / widthDivider, right / widthDivider,
                    (top + offset) / heightDivider, (bottom + offset) / heightDivider,
                    color, color,
                    texLocation
            );
        }
    }

    /**
     * Format the specified string to conform to the specified width
     *
     * @param fontRenderer The Font Renderer Instance
     * @param stringInput  The original String to wrap
     * @param wrapWidth    The target width per line, to wrap the input around
     * @return The converted and wrapped version of the original input
     */
    public static List<String> listFormattedStringToWidth(final FontRenderer fontRenderer, final String stringInput, final int wrapWidth) {
        return StringUtils.splitTextByNewLine(wrapFormattedStringToWidth(fontRenderer, stringInput, wrapWidth));
    }

    /**
     * Wraps a String based on the specified target width per line<p>
     * Separated by newline characters, as needed
     *
     * @param fontRenderer The Font Renderer Instance
     * @param stringInput  The original String to wrap
     * @param wrapWidth    The target width per line, to wrap the input around
     * @return The converted and wrapped version of the original input
     */
    public static String wrapFormattedStringToWidth(final FontRenderer fontRenderer, final String stringInput, final int wrapWidth) {
        final int stringSizeToWidth = sizeStringToWidth(fontRenderer, stringInput, wrapWidth);

        if (stringInput.length() <= stringSizeToWidth) {
            return stringInput;
        } else {
            final String subString = stringInput.substring(0, stringSizeToWidth);
            final char currentCharacter = stringInput.charAt(stringSizeToWidth);
            final boolean flag = Character.isSpaceChar(currentCharacter) || currentCharacter == '\n';
            final String s1 = StringUtils.getFormatFromString(subString) + stringInput.substring(stringSizeToWidth + (flag ? 1 : 0));
            return subString + "\n" + wrapFormattedStringToWidth(fontRenderer, s1, wrapWidth);
        }
    }

    /**
     * Returns the Wrapped Width of a String, defined by the target wrapWidth
     *
     * @param fontRenderer The Font Renderer Instance
     * @param stringEntry  The original String to evaluate
     * @param wrapWidth    The target width to wrap within
     * @return The expected wrapped width the String should be
     */
    public static int sizeStringToWidth(final FontRenderer fontRenderer, final String stringEntry, final int wrapWidth) {
        final int stringLength = stringEntry.length();
        int charWidth = 0;
        int currentLine = 0;
        int currentIndex = -1;

        for (boolean flag = false; currentLine < stringLength; ++currentLine) {
            char currentCharacter = stringEntry.charAt(currentLine);
            String stringOfCharacter = String.valueOf(currentCharacter);

            if (currentCharacter == ' ' || currentCharacter == '\n') {
                currentIndex = currentLine;

                if (currentCharacter == '\n') {
                    break;
                }
            }

            if (currentCharacter == StringUtils.COLOR_CHAR && currentLine < stringLength - 1) {
                ++currentLine;
                currentCharacter = stringEntry.charAt(currentLine);
                stringOfCharacter = String.valueOf(currentCharacter);

                flag = stringOfCharacter.equalsIgnoreCase("l") && !(stringOfCharacter.equalsIgnoreCase("r") ||
                        StringUtils.STRIP_COLOR_PATTERN.matcher(stringOfCharacter).find());
            }

            charWidth += fontRenderer.getStringWidth(stringOfCharacter);
            if (flag) {
                ++charWidth;
            }

            if (charWidth > wrapWidth) {
                break;
            }
        }

        return currentLine != stringLength && currentIndex != -1 && currentIndex < currentLine ? currentIndex : currentLine;
    }
}
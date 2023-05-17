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

import com.gitlab.cdagaming.craftpresence.ModUtils;
import com.gitlab.cdagaming.craftpresence.config.element.ColorData;
import com.gitlab.cdagaming.craftpresence.impl.Pair;
import com.gitlab.cdagaming.craftpresence.impl.Tuple;
import com.gitlab.cdagaming.craftpresence.utils.ImageUtils;
import com.gitlab.cdagaming.craftpresence.utils.MathUtils;
import com.gitlab.cdagaming.craftpresence.utils.StringUtils;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ExtendedButtonControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ExtendedTextControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.integrations.ExtendedScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

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
     * The Default Widget Background Resources
     */
    public static final String DEFAULT_BUTTON_BACKGROUND = "minecraft:" + (ModUtils.IS_LEGACY_HARD ? "/gui/gui.png" : "textures/gui/widgets.png");
    /**
     * The Default Screen Background Resources
     */
    public static final String DEFAULT_GUI_BACKGROUND = "minecraft:" + (ModUtils.IS_LEGACY_HARD ? (ModUtils.IS_LEGACY_ALPHA ? "/dirt.png" : "/gui/background.png") : "textures/gui/options_background.png");

    /**
     * Retrieve the default Screen Textures as Texture Data
     *
     * @return the default Screen Textures
     */
    public static ResourceLocation getScreenTextures() {
        return getTextureData(DEFAULT_GUI_BACKGROUND).getThird();
    }

    /**
     * Retrieve the default Widget Textures as Texture Data
     *
     * @return the default Widget Textures
     */
    public static ResourceLocation getButtonTextures() {
        return getTextureData(DEFAULT_BUTTON_BACKGROUND).getThird();
    }

    /**
     * Determines if the Mouse is over an element, following the defined Arguments
     *
     * @param mouseX   The Mouse's Current X Position
     * @param mouseY   The Mouse's Current Y Position
     * @param topIn    The top-most boundary of the zone
     * @param bottomIn The bottom-most boundary of the zone
     * @param leftIn   The left-most boundary of the zone
     * @param rightIn  The right-most boundary of the zone
     * @return {@link Boolean#TRUE} if the Mouse Position is within the bounds of the object, and thus is over it
     */
    public static boolean isMouseWithin(final double mouseX, final double mouseY, final double topIn, final double bottomIn, final double leftIn, final double rightIn) {
        return MathUtils.isWithinValue(mouseY, topIn, bottomIn, true, true) &&
                MathUtils.isWithinValue(mouseX, leftIn, rightIn, true, true);
    }

    /**
     * Determines if the Mouse is over an element, following the defined Arguments
     *
     * @param mouseX        The Mouse's Current X Position
     * @param mouseY        The Mouse's Current Y Position
     * @param elementX      The Object's starting X Position
     * @param elementY      The Object's starting Y Position
     * @param elementWidth  The total width of the object
     * @param elementHeight The total height of the object
     * @return {@link Boolean#TRUE} if the Mouse Position is within the bounds of the object, and thus is over it
     */
    public static boolean isMouseOver(final double mouseX, final double mouseY, final double elementX, final double elementY, final double elementWidth, final double elementHeight) {
        return MathUtils.isWithinValue(mouseX, elementX, elementX + elementWidth, true, false) &&
                MathUtils.isWithinValue(mouseY, elementY, elementY + elementHeight, true, false);
    }

    /**
     * Determines if the Mouse is over an element, following the defined Arguments
     *
     * @param mouseX The Mouse's Current X Position
     * @param mouseY The Mouse's Current Y Position
     * @param button The Object to check bounds and position
     * @return {@link Boolean#TRUE} if the Mouse Position is within the bounds of the object, and thus is over it
     */
    public static boolean isMouseOver(final double mouseX, final double mouseY, final ExtendedButtonControl button) {
        return button.isControlVisible() && isMouseOver(mouseX, mouseY, button.getControlPosX(), button.getControlPosY(), button.getControlWidth() - 1, button.getControlHeight() - 1);
    }

    /**
     * Determines if the Mouse is over an element, following the defined Arguments
     *
     * @param mouseY The Mouse's Current Y Position
     * @param button The Object to check bounds and position
     * @return {@link Boolean#TRUE} if the Mouse Position is within the bounds of the object, and thus is over it
     */
    public static boolean isMouseOver(final double mouseY, final ExtendedButtonControl button) {
        return isMouseOver(0, mouseY, 0, button.getControlPosY(), 0, button.getControlHeight() - 1);
    }

    /**
     * Determines if the Mouse is over an element, following the defined Arguments
     *
     * @param mouseX      The Mouse's Current X Position
     * @param mouseY      The Mouse's Current Y Position
     * @param textControl The Object to check bounds and position
     * @return {@link Boolean#TRUE} if the Mouse Position is within the bounds of the object, and thus is over it
     */
    public static boolean isMouseOver(final double mouseX, final double mouseY, final ExtendedTextControl textControl) {
        return isMouseOver(mouseX, mouseY, textControl.getControlPosX(), textControl.getControlPosY(), textControl.getControlWidth() - 1, textControl.getControlHeight() - 1);
    }

    /**
     * Determines if the Mouse is over an element, following the defined Arguments
     *
     * @param mouseY      The Mouse's Current Y Position
     * @param textControl The Object to check bounds and position
     * @return {@link Boolean#TRUE} if the Mouse Position is within the bounds of the object, and thus is over it
     */
    public static boolean isMouseOver(final double mouseY, final ExtendedTextControl textControl) {
        return isMouseOver(0, mouseY, 0, textControl.getControlPosY(), 0, textControl.getControlHeight() - 1);
    }

    /**
     * Determines if the Mouse is over an element, following the defined Arguments
     *
     * @param mouseX The Mouse's Current X Position
     * @param mouseY The Mouse's Current Y Position
     * @param screen The Object to check bounds and position
     * @return {@link Boolean#TRUE} if the Mouse Position is within the bounds of the object, and thus is over it
     */
    public static boolean isMouseOver(final double mouseX, final double mouseY, final ExtendedScreen screen) {
        return screen.isLoaded() && isMouseOver(mouseX, mouseY, screen.getScreenX(), screen.getScreenY(), screen.getScreenWidth(), screen.getScreenHeight());
    }

    /**
     * Determines if the Mouse is over an element, following the defined Arguments
     *
     * @param mouseY The Mouse's Current Y Position
     * @param screen The Object to check bounds and position
     * @return {@link Boolean#TRUE} if the Mouse Position is within the bounds of the object, and thus is over it
     */
    public static boolean isMouseOver(final double mouseY, final ExtendedScreen screen) {
        return screen.isLoaded() && isMouseOver(0, mouseY, 0, screen.getScreenY(), 0, screen.getScreenHeight());
    }

    /**
     * Determines if the Mouse is over an element, following the defined Arguments
     *
     * @param screen The Object to check bounds and position
     * @return {@link Boolean#TRUE} if the Mouse Position is within the bounds of the object, and thus is over it
     */
    public static boolean isMouseOver(final ExtendedScreen screen) {
        return isMouseOver(screen.getMouseX(), screen.getMouseY(), screen);
    }

    /**
     * Adds a Scheduled/Queued Task to Display the Specified Gui Screen
     *
     * @param client       The current game instance
     * @param targetScreen The target Gui Screen to display
     */
    public static void openScreen(@Nonnull final Minecraft client, final GuiScreen targetScreen) {
        client.addScheduledTask(() -> client.displayGuiScreen(targetScreen));
    }

    /**
     * Renders an {@link ItemStack} to the current Screen
     *
     * @param client       The current game instance
     * @param fontRenderer The Font Renderer Instance
     * @param x            The Starting X Position of the Object
     * @param y            The Starting Y Position of the Object
     * @param stack        The {@link ItemStack} instance to interpret
     * @param scale        The Scale to render the Object at
     */
    public static void drawItemStack(@Nonnull final Minecraft client, final FontRenderer fontRenderer, final int x, final int y, final ItemStack stack, final float scale) {
        try {
            GL11.glPushMatrix();
            GL11.glScalef(scale, scale, 1.0f);
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            GL11.glEnable(GL11.GL_COLOR_MATERIAL);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            RenderHelper.enableGUIStandardItemLighting();
            client.getRenderItem().zLevel = -200.0f;

            final int xPos = Math.round(x / scale);
            final int yPos = Math.round(y / scale);
            client.getRenderItem().renderItemAndEffectIntoGUI(stack, xPos, yPos);
            client.getRenderItem().renderItemOverlays(fontRenderer, stack, xPos, yPos);

            client.getRenderItem().zLevel = 0.0f;
            RenderHelper.disableStandardItemLighting();
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glDisable(GL11.GL_COLOR_MATERIAL);
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            GL11.glPopMatrix();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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
                final Pair<Boolean, Integer> data = StringUtils.getValidInteger(texLocation);
                if (data.getFirst()) {
                    GL11.glBindTexture(GL11.GL_TEXTURE_2D, data.getSecond());
                } else {
                    mc.getTextureManager().bindTexture(texLocation);
                }
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
                final Pair<Boolean, Integer> data = StringUtils.getValidInteger(texLocation);
                if (data.getFirst()) {
                    GL11.glBindTexture(GL11.GL_TEXTURE_2D, data.getSecond());
                } else {
                    mc.getTextureManager().bindTexture(texLocation);
                }
            }
        } catch (Exception ignored) {
            return;
        }
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_DEPTH_TEST);

        final int v = 46 + hoverState * 20;
        final int xOffset = width / 2;

        blit(x, y, zLevel, 0, v, xOffset, height);
        blit(x + xOffset, y, zLevel, 200 - xOffset, v, xOffset, height);

        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_BLEND);
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
                                   final double minU, final double maxU, final double minV, final double maxV,
                                   Object startColorObj, Object endColorObj,
                                   final ResourceLocation texLocation) {
        try {
            if (texLocation != null) {
                final Pair<Boolean, Integer> data = StringUtils.getValidInteger(texLocation);
                if (data.getFirst()) {
                    GL11.glBindTexture(GL11.GL_TEXTURE_2D, data.getSecond());
                } else {
                    mc.getTextureManager().bindTexture(texLocation);
                }
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
        buffer.pos(left, bottom, zLevel).tex(minU, maxV).color(endColor.getRed(), endColor.getGreen(), endColor.getBlue(), endColor.getAlpha()).endVertex();
        buffer.pos(right, bottom, zLevel).tex(maxU, maxV).color(endColor.getRed(), endColor.getGreen(), endColor.getBlue(), endColor.getAlpha()).endVertex();
        buffer.pos(right, top, zLevel).tex(maxU, minV).color(startColor.getRed(), startColor.getGreen(), startColor.getBlue(), startColor.getAlpha()).endVertex();
        buffer.pos(left, top, zLevel).tex(minU, minV).color(startColor.getRed(), startColor.getGreen(), startColor.getBlue(), startColor.getAlpha()).endVertex();
        tessellator.draw();
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

    /**
     * Draws a textured rectangle from a region in a 256x256 texture
     *
     * @param xPos         The Starting X Position of the Object
     * @param yPos         The Starting Y Position of the Object
     * @param zLevel       The Z Level Position of the Object
     * @param u            The U Mapping Value
     * @param v            The V Mapping Value
     * @param regionWidth  The Width of the Texture Region
     * @param regionHeight The Height of the Texture Region
     */
    public static void blit(final double xPos, final double yPos,
                            final double zLevel,
                            final double u, final double v,
                            final double regionWidth, final double regionHeight) {
        blit(xPos, yPos, zLevel, u, v, regionWidth, regionHeight, 256, 256);
    }

    /**
     * Draws a textured rectangle from a region in a texture
     *
     * @param xPos          The Starting X Position of the Object
     * @param yPos          The Starting Y Position of the Object
     * @param zLevel        The Z Level Position of the Object
     * @param u             The U Mapping Value
     * @param v             The V Mapping Value
     * @param regionWidth   The Width of the Texture Region
     * @param regionHeight  The Height of the Texture Region
     * @param textureWidth  The Width of the Texture
     * @param textureHeight The Height of the Texture
     */
    public static void blit(final double xPos, final double yPos,
                            final double zLevel,
                            final double u, final double v,
                            final double regionWidth, final double regionHeight,
                            final double textureWidth, final double textureHeight) {
        innerBlit(xPos, xPos + regionWidth, yPos, yPos + regionHeight,
                zLevel,
                regionWidth, regionHeight,
                u, v,
                textureWidth, textureHeight
        );
    }

    /**
     * Draws a textured rectangle from a region in a texture
     *
     * @param left          The Left Position of the Object
     * @param right         The Right Position of the Object
     * @param top           The Top Position of the Object
     * @param bottom        The Bottom Position of the Object
     * @param zLevel        The Z Level Position of the Object
     * @param regionWidth   The Width of the Texture Region
     * @param regionHeight  The Height of the Texture Region
     * @param u             The U Mapping Value
     * @param v             The V Mapping Value
     * @param textureWidth  The Width of the Texture
     * @param textureHeight The Height of the Texture
     */
    public static void innerBlit(final double left, final double right, final double top, final double bottom,
                                 final double zLevel,
                                 final double regionWidth, final double regionHeight,
                                 final double u, final double v,
                                 final double textureWidth, final double textureHeight) {
        innerBlit(left, right, top, bottom,
                zLevel,
                (u + 0.0D) / textureWidth, (u + regionWidth) / textureWidth,
                (v + 0.0D) / textureHeight, (v + regionHeight) / textureHeight
        );
    }

    /**
     * Draws a textured rectangle from a region in a texture
     *
     * @param left   The Left Position of the Object
     * @param right  The Right Position of the Object
     * @param top    The Top Position of the Object
     * @param bottom The Bottom Position of the Object
     * @param zLevel The Z Level Position of the Object
     * @param minU   The minimum horizontal axis to render this Object by
     * @param maxU   The maximum horizontal axis to render this Object by
     * @param minV   The minimum vertical axis to render this Object by
     * @param maxV   The minimum vertical axis to render this Object by
     */
    public static void innerBlit(final double left, final double right, final double top, final double bottom,
                                 final double zLevel,
                                 final double minU, final double maxU, final double minV, final double maxV) {
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

        boolean usingExternalTexture = false;

        if (!StringUtils.isValidColorCode(texture)) {
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
     * @param fontRenderer The Font Renderer Instance
     * @param colorInfo    Color Data in the format of [renderTooltips,backgroundColorInfo,borderColorInfo]
     */
    public static void drawMultiLineString(@Nonnull final Minecraft mc,
                                           final List<String> textToInput,
                                           final int posX, final int posY,
                                           final int screenWidth, final int screenHeight,
                                           final int maxTextWidth,
                                           final FontRenderer fontRenderer,
                                           final Tuple<Boolean, ColorData, ColorData> colorInfo) {
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
                    final List<String> wrappedLine = StringUtils.splitTextByNewLine(wrapFormattedStringToWidth(fontRenderer, textLines.get(i), tooltipTextWidth), true);
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

            final ColorData backgroundColorInfo = colorInfo.getSecond();
            final ColorData borderColorInfo = colorInfo.getThird();
            final int zLevel = 300;

            final Color backgroundStart = backgroundColorInfo.getStartColor();
            final Color backgroundEnd = backgroundColorInfo.getEndColor();

            final Color borderStart = borderColorInfo.getStartColor();
            final Color borderEnd = borderColorInfo.getEndColor();

            // Render Background
            if (StringUtils.isNullOrEmpty(backgroundColorInfo.getTexLocation())) {
                // Draw with Colors
                drawGradient(tooltipX - 3, tooltipX + tooltipTextWidth + 3, tooltipY - 4, tooltipY - 3, zLevel, backgroundStart, backgroundEnd);
                drawGradient(tooltipX - 3, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3, tooltipY + tooltipHeight + 4, zLevel, backgroundStart, backgroundEnd);
                drawGradient(tooltipX - 3, tooltipX + tooltipTextWidth + 3, tooltipY - 3, tooltipY + tooltipHeight + 3, zLevel, backgroundStart, backgroundEnd);
                drawGradient(tooltipX - 4, tooltipX - 3, tooltipY - 3, tooltipY + tooltipHeight + 3, zLevel, backgroundStart, backgroundEnd);
                drawGradient(tooltipX + tooltipTextWidth + 3, tooltipX + tooltipTextWidth + 4, tooltipY - 3, tooltipY + tooltipHeight + 3, zLevel, backgroundStart, backgroundEnd);
            } else {
                final Tuple<Boolean, String, ResourceLocation> textureData = getTextureData(backgroundColorInfo.getTexLocation());
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
                        backgroundStart, backgroundEnd,
                        backGroundTexture
                );
            }

            // Render Border
            if (StringUtils.isNullOrEmpty(borderColorInfo.getTexLocation())) {
                // Draw with Colors
                drawGradient(tooltipX - 3, tooltipX - 3 + 1, tooltipY - 3 + 1, tooltipY + tooltipHeight + 3 - 1, zLevel, borderStart, borderEnd);
                drawGradient(tooltipX + tooltipTextWidth + 2, tooltipX + tooltipTextWidth + 3, tooltipY - 3 + 1, tooltipY + tooltipHeight + 3 - 1, zLevel, borderStart, borderEnd);
                drawGradient(tooltipX - 3, tooltipX + tooltipTextWidth + 3, tooltipY - 3, tooltipY - 3 + 1, zLevel, borderStart, borderStart);
                drawGradient(tooltipX - 3, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 2, tooltipY + tooltipHeight + 3, zLevel, borderEnd, borderEnd);
            } else {
                final Tuple<Boolean, String, ResourceLocation> textureData = getTextureData(borderColorInfo.getTexLocation());
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
                        borderStart, borderEnd,
                        borderTexture
                );
                // Top Right
                drawTexture(mc,
                        canvasRight, canvasRight + border, renderY, canvasBottom + border,
                        zLevel,
                        0.0D, border / primaryDivider,
                        0.0D, border / secondaryDivider,
                        borderStart, borderEnd,
                        borderTexture
                );
                // Bottom Left
                drawTexture(mc,
                        renderX, canvasRight + border, canvasBottom, canvasBottom + border,
                        zLevel,
                        0.0D, border / primaryDivider,
                        0.0D, border / secondaryDivider,
                        borderStart, borderEnd,
                        borderTexture
                );
                // Right Border
                drawTexture(mc,
                        renderX, canvasRight + border, renderY, renderY + border,
                        zLevel,
                        0.0D, border / primaryDivider,
                        0.0D, border / secondaryDivider,
                        borderStart, borderEnd,
                        borderTexture
                );
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
     * @param mc      The current game instance
     * @param left    The Left Position of the Object
     * @param right   The Right Position of the Object
     * @param top     The Top Position of the Object
     * @param bottom  The Bottom Position of the Object
     * @param offset  The vertical offset to render the background to
     */
    public static void drawBackground(@Nonnull final Minecraft mc,
                                      final double left, final double right,
                                      final double top, final double bottom,
                                      final double offset, float tintFactor,
                                      final ColorData data) {
        // Setup Colors + Tint Data
        tintFactor = Math.max(0.0f, Math.min(tintFactor, 1.0f));
        final Color startColor = StringUtils.offsetColor(data.getStartColor(), tintFactor);
        final Color endColor = StringUtils.offsetColor(data.getEndColor(), tintFactor);

        if (StringUtils.isNullOrEmpty(data.getTexLocation())) {
            drawGradient(left, right, top, bottom,
                    300.0F,
                    startColor, endColor
            );
        } else {
            final Tuple<Boolean, String, ResourceLocation> textureData = getTextureData(data.getTexLocation());
            final ResourceLocation texLocation = textureData.getThird();

            final double widthDivider = textureData.getFirst() ? (right - left) : 32.0D;
            final double heightDivider = textureData.getFirst() ? (bottom - top) : 32.0D;
            final double offsetAmount = textureData.getFirst() ? 0.0D : offset;

            drawTexture(mc,
                    left, right, top, bottom,
                    0.0D,
                    left / widthDivider, right / widthDivider,
                    (top + offsetAmount) / heightDivider, (bottom + offsetAmount) / heightDivider,
                    startColor, endColor,
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
        return StringUtils.splitTextByNewLine(wrapFormattedStringToWidth(fontRenderer, stringInput, wrapWidth), true);
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

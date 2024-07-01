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

package com.gitlab.cdagaming.unilib.utils.gui;

import com.gitlab.cdagaming.unilib.ModUtils;
import com.gitlab.cdagaming.unilib.core.CoreUtils;
import com.gitlab.cdagaming.unilib.core.impl.screen.ScissorStack;
import com.gitlab.cdagaming.unilib.core.impl.screen.ScreenConstants;
import com.gitlab.cdagaming.unilib.core.impl.screen.ScreenRectangle;
import com.gitlab.cdagaming.unilib.impl.ImageFrame;
import com.gitlab.cdagaming.unilib.utils.ImageUtils;
import com.gitlab.cdagaming.unilib.utils.ResourceUtils;
import com.gitlab.cdagaming.unilib.utils.gui.controls.ExtendedButtonControl;
import com.gitlab.cdagaming.unilib.utils.gui.controls.ExtendedTextControl;
import com.gitlab.cdagaming.unilib.utils.gui.integrations.ExtendedScreen;
import io.github.cdagaming.unicore.impl.Pair;
import io.github.cdagaming.unicore.impl.Tuple;
import io.github.cdagaming.unicore.utils.MathUtils;
import io.github.cdagaming.unicore.utils.StringUtils;
import io.github.cdagaming.unicore.utils.TimeUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Rendering Utilities used to Parse Screen Data and handle rendering tasks
 *
 * @author CDAGaming
 */
@SuppressWarnings("DuplicatedCode")
public class RenderUtils {
    /**
     * The stack of {@link ScreenRectangle} objects to manage the scissor areas for rendering.
     */
    private static final ScissorStack scissorStack = new ScissorStack();
    /**
     * The Block List for any ItemStacks that have failed to render in {@link RenderUtils#drawItemStack(Minecraft, FontRenderer, int, int, ItemStack, float)}
     */
    private static final List<ItemStack> BLOCKED_RENDER_ITEMS = StringUtils.newArrayList();
    /**
     * An active cache for all currently allocated internal Texture Object Results
     */
    private static final Map<String, Tuple<Boolean, String, ResourceLocation>> TEXTURE_CACHE = StringUtils.newHashMap();

    /**
     * Retrieve the default Screen Textures as Texture Data
     *
     * @param mc       The Minecraft Instance
     * @param protocol The Protocol to Target for this operation
     * @return the default Screen Textures
     */
    public static ResourceLocation getScreenTextures(@Nonnull final Minecraft mc, final int protocol) {
        return getTextureData(mc, ScreenConstants.getDefaultGUIBackground(protocol)).getThird();
    }

    /**
     * Retrieve the default Widget Textures as Texture Data
     *
     * @param mc       The Minecraft Instance
     * @param protocol The Protocol to Target for this operation
     * @return the default Widget Textures
     */
    public static ResourceLocation getButtonTextures(@Nonnull final Minecraft mc, final int protocol) {
        return getTextureData(mc, ScreenConstants.getDefaultButtonBackground(protocol)).getThird();
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
     * @param screen The Object to check bounds and position
     * @return {@link Boolean#TRUE} if the Mouse Position is within the bounds of the object, and thus is over it
     */
    public static boolean isMouseOver(final ExtendedScreen screen) {
        return isMouseOver(screen.getMouseX(), screen.getMouseY(), screen);
    }

    /**
     * Gets the Default/Global Font Renderer
     *
     * @return The Default/Global Font Renderer
     */
    public static FontRenderer getDefaultFontRenderer() {
        return ModUtils.INSTANCE_GETTER.get().fontRenderer;
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
     * Adds a Scheduled/Queued Task to Display the Specified Gui Screen
     *
     * @param client       The current game instance
     * @param targetScreen The target Gui Screen to display
     * @param parentScreen The parent screen instance to set, if possible
     * @param setParent    Whether to allow modifying the parent screen instance
     */
    public static void openScreen(@Nonnull final Minecraft client, final ExtendedScreen targetScreen, final GuiScreen parentScreen, final boolean setParent) {
        if (setParent) {
            targetScreen.setParent(parentScreen);
        }
        openScreen(client, targetScreen);
    }

    /**
     * Adds a Scheduled/Queued Task to Display the Specified Gui Screen
     *
     * @param client       The current game instance
     * @param targetScreen The target Gui Screen to display
     * @param parentScreen The parent screen instance to set, if possible
     */
    public static void openScreen(@Nonnull final Minecraft client, final ExtendedScreen targetScreen, final GuiScreen parentScreen) {
        openScreen(client, targetScreen, parentScreen, targetScreen.getParent() == null);
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
        if (BLOCKED_RENDER_ITEMS.contains(stack)) return;
        try {
            GlStateManager.pushMatrix();
            GlStateManager.scale(scale, scale, 1.0f);
            GlStateManager.enableRescaleNormal();
            GlStateManager.enableColorMaterial();
            GlStateManager.enableDepth();
            RenderHelper.enableGUIStandardItemLighting();

            final int xPos = Math.round(x / scale);
            final int yPos = Math.round(y / scale);
            client.getRenderItem().renderItemAndEffectIntoGUI(stack, xPos, yPos);
            client.getRenderItem().renderItemOverlays(fontRenderer, stack, xPos, yPos);

            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableDepth();
            GlStateManager.disableColorMaterial();
            GlStateManager.disableRescaleNormal();
            GlStateManager.popMatrix();
        } catch (Throwable ex) {
            CoreUtils.LOG.debugError(ex);
            if (!BLOCKED_RENDER_ITEMS.contains(stack)) {
                BLOCKED_RENDER_ITEMS.add(stack);
            }
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
     * @param borderOffset    The offset to apply to the vertical border bounds (Useful for Drop Shadows)
     * @param contentColor    The starting content color for the object
     * @param contentColorEnd The ending content color for the object
     */
    public static void drawGradientBox(final double posX, final double posY,
                                       final double width, final double height,
                                       final double zLevel,
                                       final Object borderColor, final Object borderColorEnd,
                                       final int border, final int borderOffset,
                                       final Object contentColor, final Object contentColorEnd) {
        final double canvasWidth = width - (border * 2);
        final double canvasHeight = height - (border * 2);

        final double canvasRight = posX + border + canvasWidth;
        final double canvasBottom = posY + border + canvasHeight;

        // Draw Borders
        if (borderColor != null) {
            // Top Left
            drawGradient(posX, posX + border, posY + border, canvasBottom, zLevel, borderColor, borderColorEnd);
            // Top Right
            drawGradient(canvasRight, canvasRight + border, posY + border, canvasBottom, zLevel, borderColor, borderColorEnd);
            // Bottom Left
            drawGradient(posX - borderOffset, canvasRight + border + borderOffset, canvasBottom, canvasBottom + border, zLevel, borderColorEnd, borderColorEnd);
            // Bottom Right
            drawGradient(posX - borderOffset, canvasRight + border + borderOffset, posY, posY + border, zLevel, borderColor, borderColor);
        }

        // Draw Content Box
        if (contentColor != null) {
            drawGradient(posX + border, canvasRight, posY + border, canvasBottom, zLevel, contentColor, contentColorEnd);
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
                                       final Object borderColor, final Object borderColorEnd,
                                       final int border,
                                       final Object contentColor, final Object contentColorEnd) {
        drawGradientBox(
                posX, posY,
                width, height,
                zLevel,
                borderColor, borderColorEnd,
                border, 0,
                contentColor, contentColorEnd
        );
    }

    /**
     * Renders a Button Object from the defined arguments
     *
     * @param mc          The current game instance
     * @param x           The Starting X Position to render the button
     * @param y           The Starting Y Position to render the button
     * @param startU      The Starting U Mapping Value
     * @param startV      The Starting V Mapping Value
     * @param endU        The Ending U Mapping Value
     * @param endV        The Ending V Mapping Value
     * @param width       The full width for the button to render to
     * @param height      The full height for the button to render to
     * @param zLevel      The Z level position for the button to render at
     * @param texLocation The game texture to render the button as
     */
    public static void renderButton(@Nonnull final Minecraft mc,
                                    final double x, final double y,
                                    final double startU, final double startV,
                                    final double endU, final double endV,
                                    final double width, final double height,
                                    final double zLevel,
                                    final ResourceLocation texLocation) {
        try {
            if (texLocation != null) {
                final Pair<Boolean, Integer> data = StringUtils.getValidInteger(texLocation);
                if (data.getFirst()) {
                    GlStateManager.bindTexture(data.getSecond());
                } else {
                    mc.getTextureManager().bindTexture(texLocation);
                }
            }
        } catch (Exception ignored) {
            return;
        }
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableDepth();

        blit(x, y, zLevel, startU, startV, width, height);
        blit(x + width, y, zLevel, endU, endV, width, height);

        GlStateManager.disableDepth();
        GlStateManager.disableBlend();
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
     * @param endColorObj   The ending texture RGB data to interpret
     * @param texLocation   The game texture to render the object as
     */
    public static void drawTexture(@Nonnull final Minecraft mc,
                                   final double left, final double right, final double top, final double bottom,
                                   final double zLevel,
                                   final double minU, final double maxU, final double minV, final double maxV,
                                   final Object startColorObj, final Object endColorObj,
                                   final ResourceLocation texLocation) {
        try {
            if (texLocation != null) {
                final Pair<Boolean, Integer> data = StringUtils.getValidInteger(texLocation);
                if (data.getFirst()) {
                    GlStateManager.bindTexture(data.getSecond());
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

        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);

        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        buffer.pos(left, bottom, zLevel).tex(minU, maxV).color(endColor.getRed(), endColor.getGreen(), endColor.getBlue(), endColor.getAlpha()).endVertex();
        buffer.pos(right, bottom, zLevel).tex(maxU, maxV).color(endColor.getRed(), endColor.getGreen(), endColor.getBlue(), endColor.getAlpha()).endVertex();
        buffer.pos(right, top, zLevel).tex(maxU, minV).color(startColor.getRed(), startColor.getGreen(), startColor.getBlue(), startColor.getAlpha()).endVertex();
        buffer.pos(left, top, zLevel).tex(minU, minV).color(startColor.getRed(), startColor.getGreen(), startColor.getBlue(), startColor.getAlpha()).endVertex();
        tessellator.draw();

        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.disableBlend();
        GlStateManager.disableAlpha();
    }

    /**
     * Draws a Textured Rectangle, following the defined arguments
     *
     * @param mc                   The current game instance
     * @param left                 The Left Position of the Object
     * @param right                The Right Position of the Object
     * @param top                  The Top Position of the Object
     * @param bottom               The Bottom Position of the Object
     * @param zLevel               The Z Level Position of the Object
     * @param usingExternalTexture Whether we are using a non-local/external texture
     * @param regionWidth          The Width of the Texture Region
     * @param regionHeight         The Height of the Texture Region
     * @param u                    The U Mapping Value
     * @param v                    The V Mapping Value
     * @param textureWidth         The Width of the Texture
     * @param textureHeight        The Height of the Texture
     * @param startColorObj        The starting texture RGB data to interpret
     * @param endColorObj          The ending texture RGB data to interpret
     * @param texLocation          The game texture to render the object as
     */
    public static void drawTexture(@Nonnull final Minecraft mc,
                                   final double left, final double right, final double top, final double bottom,
                                   final double zLevel, final boolean usingExternalTexture,
                                   final double regionWidth, final double regionHeight,
                                   final double u, final double v,
                                   final double textureWidth, final double textureHeight,
                                   final Object startColorObj, final Object endColorObj,
                                   final ResourceLocation texLocation) {
        drawTexture(mc,
                left, right, top, bottom,
                zLevel,
                getUVCoord(u + 0.0D, 0.0D, usingExternalTexture, textureWidth),
                getUVCoord(u + regionWidth, 1.0D, usingExternalTexture, textureWidth),
                getUVCoord(v + 0.0D, 0.0D, usingExternalTexture, textureHeight),
                getUVCoord(v + regionHeight, 1.0D, usingExternalTexture, textureHeight),
                startColorObj, endColorObj,
                texLocation
        );
    }

    /**
     * Draws a Textured Rectangle, following the defined arguments
     *
     * @param mc                   The current game instance
     * @param left                 The Left Position of the Object
     * @param right                The Right Position of the Object
     * @param top                  The Top Position of the Object
     * @param bottom               The Bottom Position of the Object
     * @param zLevel               The Z Level Position of the Object
     * @param usingExternalTexture Whether we are using a non-local/external texture
     * @param startColorObj        The starting texture RGB data to interpret
     * @param endColorObj          The ending texture RGB data to interpret
     * @param texLocation          The game texture to render the object as
     */
    public static void drawTexture(@Nonnull final Minecraft mc,
                                   final double left, final double right, final double top, final double bottom,
                                   final double zLevel, final boolean usingExternalTexture,
                                   final Object startColorObj, final Object endColorObj,
                                   final ResourceLocation texLocation) {
        drawTexture(mc,
                left, right, top, bottom,
                zLevel, usingExternalTexture,
                right - left, bottom - top,
                left, top,
                32.0D, 32.0D,
                startColorObj, endColorObj,
                texLocation
        );
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
                                    final Object startColorObj, final Object endColorObj) {
        final Pair<Color, Color> colorData = StringUtils.findColor(startColorObj, endColorObj);
        final Color startColor = colorData.getFirst();
        final Color endColor = colorData.getSecond();
        if (startColor == null || endColor == null) {
            return;
        }

        GlStateManager.disableDepth();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);

        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(left, bottom, zLevel).color(endColor.getRed(), endColor.getGreen(), endColor.getBlue(), endColor.getAlpha()).endVertex();
        buffer.pos(right, bottom, zLevel).color(endColor.getRed(), endColor.getGreen(), endColor.getBlue(), endColor.getAlpha()).endVertex();
        buffer.pos(right, top, zLevel).color(startColor.getRed(), startColor.getGreen(), startColor.getBlue(), startColor.getAlpha()).endVertex();
        buffer.pos(left, top, zLevel).color(startColor.getRed(), startColor.getGreen(), startColor.getBlue(), startColor.getAlpha()).endVertex();
        tessellator.draw();

        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.enableDepth();
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
        blit(xPos, xPos + regionWidth, yPos, yPos + regionHeight,
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
    public static void blit(final double left, final double right, final double top, final double bottom,
                            final double zLevel,
                            final double regionWidth, final double regionHeight,
                            final double u, final double v,
                            final double textureWidth, final double textureHeight) {
        innerBlit(left, right, top, bottom,
                zLevel,
                getUVCoord(u + 0.0D, textureWidth),
                getUVCoord(u + regionWidth, textureWidth),
                getUVCoord(v + 0.0D, textureHeight),
                getUVCoord(v + regionHeight, textureHeight)
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
     * @param mc     The Minecraft Instance
     * @param left   The Starting X Position of the Object
     * @param top    The Starting Y Position of the Object
     * @param right  The Right side length of the Object
     * @param bottom The bottom length of the Object
     */
    public static void enableScissor(@Nonnull final Minecraft mc, final int left, final int top, final int right, final int bottom) {
        applyScissor(mc, scissorStack.push(new ScreenRectangle(left, top, right - left, bottom - top)));
    }

    /**
     * Disables current rendering boundary flags, mainly glScissor
     *
     * @param mc The Minecraft Instance
     */
    public static void disableScissor(@Nonnull final Minecraft mc) {
        applyScissor(mc, scissorStack.pop());
    }

    /**
     * Define viewable rendering boundaries, utilizing glScissor
     *
     * @param mc        The Minecraft Instance
     * @param rectangle The Screen Area to process
     */
    private static void applyScissor(@Nonnull final Minecraft mc, final ScreenRectangle rectangle) {
        if (rectangle != null) {
            final int scale = computeGuiScale(mc);
            final int displayHeight = mc.displayHeight;
            final int renderWidth = Math.max(0, rectangle.width() * scale);
            final int renderHeight = Math.max(0, rectangle.height() * scale);
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            GL11.glScissor(
                    rectangle.getLeft() * scale,
                    displayHeight - rectangle.getBottom() * scale,
                    renderWidth, renderHeight
            );
        } else {
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
        }
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
     * @param mc      The Minecraft Instance
     * @param texture The data to interpret
     * @return a {@link Tuple} with the mapping "usingExternalData:location:resource"
     */
    public static Tuple<Boolean, String, ResourceLocation> getTextureData(@Nonnull final Minecraft mc, String texture) {
        ResourceLocation texLocation = ResourceUtils.getEmptyResource();
        final Tuple<Boolean, String, ResourceLocation> result = new Tuple<>(false, "", texLocation);
        if (!StringUtils.isNullOrEmpty(texture)) {
            texture = texture.trim();
            if (TEXTURE_CACHE.containsKey(texture)) {
                return TEXTURE_CACHE.get(texture);
            }
        } else {
            return result;
        }

        boolean usingExternalTexture = false;

        if (!StringUtils.isValidColorCode(texture)) {
            usingExternalTexture = ImageFrame.isExternalImage(texture);

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
                    texLocation = ResourceUtils.getResource(splitInput[0], splitInput[1]);
                } else {
                    texLocation = ResourceUtils.getResource(texture);
                }
            } else {
                final String formattedConvertedName = texture.replaceFirst("file://", "");
                final String[] urlBits = formattedConvertedName.trim().split("/");
                final String textureName = urlBits[urlBits.length - 1].trim();
                texLocation = ImageUtils.getTextureFromUrl(mc, textureName, texture.toLowerCase().startsWith("file://") ? new File(formattedConvertedName) : formattedConvertedName);
            }
        }
        result.put(usingExternalTexture, texture, texLocation);

        if (!usingExternalTexture) {
            TEXTURE_CACHE.put(texture, result);
        }
        return result;
    }

    /**
     * Renders a Specified Multi-Line String, constrained by position and dimension arguments
     *
     * @param mc           The current game instance
     * @param textToInput  The Specified Multi-Line String, split by lines into a list
     * @param posX         The starting X position to render the String
     * @param posY         The starting Y position to render the String
     * @param maxWidth     The maximum width to allow rendering to (Text will wrap if output is greater)
     * @param maxHeight    The maximum height to allow rendering to (Text will wrap if output is greater)
     * @param maxTextWidth The maximum width the output can be before wrapping
     * @param fontRenderer The Font Renderer Instance
     * @param isCentered   Whether to render the text in a center-styled layout (Disabled if maxWidth is not specified)
     * @param isTooltip    Whether to render this layout in a tooltip-style (Issues may occur if combined with isCentered)
     * @param colorInfo    Color Data in the format of [renderTooltips,backgroundColorInfo,borderColorInfo]
     */
    public static void drawMultiLineString(@Nonnull final Minecraft mc,
                                           final List<String> textToInput,
                                           final int posX, final int posY,
                                           final int maxWidth, final int maxHeight,
                                           final int maxTextWidth,
                                           final FontRenderer fontRenderer,
                                           final boolean isCentered,
                                           final boolean isTooltip,
                                           final ScreenConstants.TooltipData colorInfo) {
        if (colorInfo.renderTooltips() && !textToInput.isEmpty() && fontRenderer != null) {
            List<String> textLines = textToInput;
            int tooltipTextWidth = 0;

            for (String textLine : textLines) {
                final int textLineWidth = getStringWidth(fontRenderer, textLine);

                if (textLineWidth > tooltipTextWidth) {
                    tooltipTextWidth = textLineWidth;
                }
            }

            boolean needsWrap = false;
            boolean allowXAdjustments = maxWidth > 0;
            boolean allowYAdjustments = maxHeight > 0;
            boolean allowCenterAdjustments = isCentered && allowXAdjustments;

            int titleLinesCount = 1;
            int tooltipX = posX;

            if (!isTooltip && allowCenterAdjustments) {
                tooltipX = posX + 4;
                tooltipTextWidth = maxWidth - tooltipX - 4;
            } else if (isTooltip) {
                tooltipX = posX + (allowXAdjustments ? 12 : 0);
                if (allowXAdjustments && tooltipX + tooltipTextWidth + 4 > maxWidth) {
                    tooltipX = posX - 16 - tooltipTextWidth;
                    if (tooltipX < 4) // if the tooltip doesn't fit on the screen
                    {
                        if (posX > maxWidth / 2) {
                            tooltipTextWidth = posX - 12 - 8;
                        } else {
                            tooltipTextWidth = maxWidth - 16 - posX;
                        }
                        needsWrap = true;
                    }
                }

                if (maxTextWidth > 0 && tooltipTextWidth > maxTextWidth) {
                    tooltipTextWidth = maxTextWidth;
                    needsWrap = true;
                }
            }

            if (needsWrap) {
                final List<String> wrappedTextLines = StringUtils.newArrayList();
                int wrappedTooltipWidth = 0;
                for (int i = 0; i < textLines.size(); i++) {
                    final List<String> wrappedLine = listFormattedStringToWidth(fontRenderer, textLines.get(i), tooltipTextWidth);
                    if (i == 0) {
                        titleLinesCount = wrappedLine.size();
                    }

                    for (String line : wrappedLine) {
                        int lineWidth = getStringWidth(fontRenderer, line);
                        if (lineWidth > wrappedTooltipWidth) {
                            wrappedTooltipWidth = lineWidth;
                        }
                        wrappedTextLines.add(line);
                    }
                }
                tooltipTextWidth = wrappedTooltipWidth;
                textLines = wrappedTextLines;

                if (allowXAdjustments) {
                    if (posX > maxWidth / 2) {
                        tooltipX = posX - 16 - tooltipTextWidth;
                    } else {
                        tooltipX = posX + 12;
                    }
                }
            }

            int tooltipY = posY - (isTooltip && allowYAdjustments ? 12 : 0);
            int tooltipHeight = 8;
            int fontHeight = getFontHeight(fontRenderer);

            if (textLines.size() > 1) {
                tooltipHeight += (textLines.size() - 1) * (fontHeight + 1);
                if (isTooltip && textLines.size() > titleLinesCount) {
                    tooltipHeight += 2; // gap between title lines and next lines
                }
            }

            if (allowYAdjustments) {
                if (tooltipY < 4) {
                    tooltipY = 4;
                } else if (tooltipY + tooltipHeight + 4 > maxHeight) {
                    tooltipY = maxHeight - tooltipHeight - 4;
                }
            }

            final ScreenConstants.ColorData backgroundColorInfo = colorInfo.backgroundColor();
            final ScreenConstants.ColorData borderColorInfo = colorInfo.borderColor();
            final int zLevel = 300;

            // Render Background
            if (backgroundColorInfo != null) {
                final Color backgroundStart = backgroundColorInfo.startColor();
                final Color backgroundEnd = backgroundColorInfo.endColor();

                if (StringUtils.isNullOrEmpty(backgroundColorInfo.texLocation())) {
                    // Draw with Colors
                    drawGradientBox(
                            tooltipX - 4, tooltipY - 4,
                            tooltipTextWidth + 8, tooltipHeight + 8,
                            zLevel,
                            backgroundStart, backgroundEnd,
                            1, -1,
                            backgroundStart, backgroundEnd
                    );
                } else {
                    final Tuple<Boolean, String, ResourceLocation> textureData = getTextureData(mc, backgroundColorInfo.texLocation());
                    final boolean usingExternalTexture = textureData.getFirst();
                    final ResourceLocation backGroundTexture = textureData.getThird();

                    final double width = tooltipTextWidth + 4;
                    final double height = tooltipHeight + 4;

                    final double left = tooltipX - 4;
                    final double right = tooltipX + width;
                    final double top = tooltipY - 4;
                    final double bottom = tooltipY + height;

                    drawTexture(mc,
                            left, right, top, bottom,
                            0.0D, usingExternalTexture,
                            backgroundStart, backgroundEnd,
                            backGroundTexture
                    );
                }
            }

            // Render Border
            if (borderColorInfo != null) {
                final Color borderStart = borderColorInfo.startColor();
                final Color borderEnd = borderColorInfo.endColor();

                if (StringUtils.isNullOrEmpty(borderColorInfo.texLocation())) {
                    // Draw with Colors
                    drawGradientBox(
                            tooltipX - 3, tooltipY - 3,
                            tooltipTextWidth + 6, tooltipHeight + 6,
                            zLevel, borderStart, borderEnd,
                            1,
                            null, null
                    );
                } else {
                    final Tuple<Boolean, String, ResourceLocation> textureData = getTextureData(mc, borderColorInfo.texLocation());
                    final boolean usingExternalTexture = textureData.getFirst();
                    final ResourceLocation borderTexture = textureData.getThird();

                    final double border = 1;
                    final double renderX = tooltipX - 3;
                    final double renderY = tooltipY - 3;
                    final double canvasRight = tooltipX + tooltipTextWidth + 2;
                    final double canvasBottom = tooltipY + tooltipHeight + 2;

                    // Draw Borders
                    // Top Left
                    drawTexture(mc,
                            renderX, renderX + border, renderY, canvasBottom + border,
                            zLevel, usingExternalTexture,
                            borderStart, borderEnd,
                            borderTexture
                    );
                    // Top Right
                    drawTexture(mc,
                            canvasRight, canvasRight + border, renderY, canvasBottom + border,
                            zLevel, usingExternalTexture,
                            borderStart, borderEnd,
                            borderTexture
                    );
                    // Bottom Left
                    drawTexture(mc,
                            renderX, canvasRight + border, canvasBottom, canvasBottom + border,
                            zLevel, usingExternalTexture,
                            borderStart, borderEnd,
                            borderTexture
                    );
                    // Right Border
                    drawTexture(mc,
                            renderX, canvasRight + border, renderY, renderY + border,
                            zLevel, usingExternalTexture,
                            borderStart, borderEnd,
                            borderTexture
                    );
                }
            }

            for (int lineNumber = 0; lineNumber < textLines.size(); ++lineNumber) {
                final String line = textLines.get(lineNumber);
                final int lineWidth = getStringWidth(fontRenderer, line);
                final int renderX = isCentered ? (tooltipX + (tooltipTextWidth - lineWidth) / 2) : tooltipX;

                renderString(fontRenderer, line, renderX, tooltipY, -1);

                if (isTooltip && lineNumber + 1 == titleLinesCount) {
                    tooltipY += 2;
                }

                tooltipY += fontHeight + 1;
            }
        }
    }

    /**
     * Renders a String in the Screen, in the style of centered text
     *
     * @param fontRenderer The Font Renderer Instance
     * @param text         The text to render to the screen
     * @param xPos         The X position to render the text at
     * @param yPos         The Y position to render the text at
     * @param color        The color to render the text in
     */
    public static void renderCenteredString(final FontRenderer fontRenderer, final String text, final float xPos, final float yPos, final int color) {
        renderString(fontRenderer, text, xPos - (getStringWidth(fontRenderer, text) / 2f), yPos, color);
    }

    /**
     * Renders a String in the Screen, in the style of centered text
     *
     * @param fontRenderer The Font Renderer Instance
     * @param text         The text to render to the screen
     * @param xPos         The X position to render the text at
     * @param yPos         The Y position to render the text at
     * @param color        The color to render the text in
     */
    public static void renderCenteredString(final FontRenderer fontRenderer, final String text, final int xPos, final int yPos, final int color) {
        renderString(fontRenderer, text, xPos - (getStringWidth(fontRenderer, text) / 2), yPos, color);
    }

    /**
     * Renders a String in the Screen, in the style of normal text
     *
     * @param fontRenderer The Font Renderer Instance
     * @param text         The text to render to the screen
     * @param xPos         The X position to render the text at
     * @param yPos         The Y position to render the text at
     * @param color        The color to render the text in
     */
    public static void renderString(final FontRenderer fontRenderer, final String text, final float xPos, final float yPos, final int color) {
        fontRenderer.drawStringWithShadow(text, xPos, yPos, color);
    }

    /**
     * Renders a String in the Screen, in the style of normal text
     *
     * @param fontRenderer The Font Renderer Instance
     * @param text         The text to render to the screen
     * @param xPos         The X position to render the text at
     * @param yPos         The Y position to render the text at
     * @param color        The color to render the text in
     */
    public static void renderString(final FontRenderer fontRenderer, final String text, final int xPos, final int yPos, final int color) {
        renderString(fontRenderer, text, (float) xPos, (float) yPos, color);
    }

    /**
     * Get the Width of a String from the FontRenderer
     *
     * @param fontRenderer The Font Renderer Instance
     * @param string       The string to interpret
     * @return the string's width from the font renderer
     */
    public static int getStringWidth(final FontRenderer fontRenderer, final String string) {
        return fontRenderer.getStringWidth(string);
    }

    /**
     * Get the Width of a Character from the FontRenderer
     *
     * @param fontRenderer The Font Renderer Instance
     * @param string       The character to interpret
     * @return the character's width from the font renderer
     */
    private static int getCharWidth(final FontRenderer fontRenderer, final char string) {
        return fontRenderer.getCharWidth(string);
    }

    /**
     * Get the Current Font Height for this Screen
     *
     * @param fontRenderer The Font Renderer Instance
     * @return The Current Font Height for this Screen
     */
    public static int getFontHeight(final FontRenderer fontRenderer) {
        return fontRenderer.FONT_HEIGHT;
    }

    /**
     * Renders a String in the Screen, in the style of scrolling text
     *
     * @param mc           The current game instance
     * @param fontRenderer The Font Renderer Instance
     * @param message      The text to render to the screen
     * @param centerX      The center X position, used when not scrolling
     * @param minX         The minimum X position to render the text at
     * @param minY         The minimum Y position to render the text at
     * @param maxX         The maximum X position to render the text at
     * @param maxY         The maximum Y position to render the text at
     * @param color        The color to render the text in
     */
    public static void renderScrollingString(@Nonnull final Minecraft mc,
                                             final FontRenderer fontRenderer,
                                             final String message,
                                             final float centerX,
                                             final float minX, final float minY,
                                             final float maxX, final float maxY,
                                             final int color) {
        final int lineWidth = getStringWidth(fontRenderer, message);
        final float renderY = (minY + maxY - getFontHeight(fontRenderer)) / 2f + 1f;
        final float elementWidth = maxX - minX;
        if (lineWidth > elementWidth) {
            final float renderWidth = lineWidth - elementWidth;
            final double renderTime = TimeUtils.getElapsedMillis() / 1000D;
            final double renderDistance = Math.max(renderWidth * 0.5D, 3D);
            final double percentage = Math.sin((Math.PI / 2D) * Math.cos((Math.PI * 2D) * renderTime / renderDistance)) / 2D + 0.5D;
            final double offset = MathUtils.lerp(percentage, 0.0D, renderWidth);
            enableScissor(mc, (int) minX, (int) minY, (int) maxX, (int) maxY);
            renderString(fontRenderer, message, minX - (float) offset, renderY, color);
            disableScissor(mc);
        } else {
            final float renderX = MathUtils.clamp(centerX, minX + lineWidth / 2f, maxX - lineWidth / 2f);
            renderCenteredString(fontRenderer, message, renderX, renderY, color);
        }
    }

    /**
     * Renders a String in the Screen, in the style of scrolling text
     *
     * @param mc           The current game instance
     * @param fontRenderer The Font Renderer Instance
     * @param message      The text to render to the screen
     * @param centerX      The center X position, used when not scrolling
     * @param minX         The minimum X position to render the text at
     * @param minY         The minimum Y position to render the text at
     * @param maxX         The maximum X position to render the text at
     * @param maxY         The maximum Y position to render the text at
     * @param color        The color to render the text in
     */
    public static void renderScrollingString(@Nonnull final Minecraft mc,
                                             final FontRenderer fontRenderer,
                                             final String message,
                                             final int centerX,
                                             final int minX, final int minY,
                                             final int maxX, final int maxY,
                                             final int color) {
        final int lineWidth = getStringWidth(fontRenderer, message);
        final int renderY = (minY + maxY - getFontHeight(fontRenderer)) / 2 + 1;
        final int elementWidth = maxX - minX;
        if (lineWidth > elementWidth) {
            final int renderWidth = lineWidth - elementWidth;
            final double renderTime = TimeUtils.getElapsedMillis() / 1000D;
            final double renderDistance = Math.max(renderWidth * 0.5D, 3D);
            final double percentage = Math.sin((Math.PI / 2D) * Math.cos((Math.PI * 2D) * renderTime / renderDistance)) / 2D + 0.5D;
            final double offset = MathUtils.lerp(percentage, 0.0D, renderWidth);
            enableScissor(mc, minX, minY, maxX, maxY);
            renderString(fontRenderer, message, minX - (int) offset, renderY, color);
            disableScissor(mc);
        } else {
            final int renderX = MathUtils.clamp(centerX, minX + lineWidth / 2, maxX - lineWidth / 2);
            renderCenteredString(fontRenderer, message, renderX, renderY, color);
        }
    }

    /**
     * Renders a String in the Screen, in the style of scrolling text
     *
     * @param mc           The current game instance
     * @param fontRenderer The Font Renderer Instance
     * @param message      The text to render to the screen
     * @param minX         The minimum X position to render the text at
     * @param minY         The minimum Y position to render the text at
     * @param maxX         The maximum X position to render the text at
     * @param maxY         The maximum Y position to render the text at
     * @param color        The color to render the text in
     */
    public static void renderScrollingString(@Nonnull final Minecraft mc,
                                             final FontRenderer fontRenderer,
                                             final String message,
                                             final float minX, final float minY,
                                             final float maxX, final float maxY,
                                             final int color) {
        renderScrollingString(mc, fontRenderer, message, maxX - ((maxX - minX) / 2f), minX, minY, maxX, maxY, color);
    }

    /**
     * Renders a String in the Screen, in the style of scrolling text
     *
     * @param mc           The current game instance
     * @param fontRenderer The Font Renderer Instance
     * @param message      The text to render to the screen
     * @param minX         The minimum X position to render the text at
     * @param minY         The minimum Y position to render the text at
     * @param maxX         The maximum X position to render the text at
     * @param maxY         The maximum Y position to render the text at
     * @param color        The color to render the text in
     */
    public static void renderScrollingString(@Nonnull final Minecraft mc,
                                             final FontRenderer fontRenderer,
                                             final String message,
                                             final int minX, final int minY,
                                             final int maxX, final int maxY,
                                             final int color) {
        renderScrollingString(mc, fontRenderer, message, maxX - ((maxX - minX) / 2), minX, minY, maxX, maxY, color);
    }

    /**
     * Calculate the Axis coordinate with the specified info
     *
     * @param primary              The Primary Result, divided by the texture size
     * @param secondary            The Secondary Result, occurring when usingExternalTexture is {@link Boolean#TRUE}
     * @param usingExternalTexture Whether we are using a non-local/external texture
     * @param textureSize          The Texture Size to divide the result by, when usingExternalTexture is {@link Boolean#FALSE}
     * @return the calculated position
     */
    public static double getUVCoord(final double primary, final double secondary, final boolean usingExternalTexture, final double textureSize) {
        return usingExternalTexture ? secondary : getUVCoord(primary, textureSize);
    }

    /**
     * Calculate the Axis coordinate with the specified info
     *
     * @param primary     The Primary Result, divided by the texture size
     * @param textureSize The Texture Size to divide the result by
     * @return the calculated position
     */
    public static double getUVCoord(final double primary, final double textureSize) {
        return primary / textureSize;
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
    private static String wrapFormattedStringToWidth(final FontRenderer fontRenderer, final String stringInput, final int wrapWidth) {
        final int stringSizeToWidth = sizeStringToWidth(fontRenderer, stringInput, wrapWidth);

        if (stringInput.length() <= stringSizeToWidth) {
            return stringInput;
        } else {
            final String subString = stringInput.substring(0, stringSizeToWidth);
            final char currentCharacter = stringInput.charAt(stringSizeToWidth);
            final boolean flag = Character.isSpaceChar(currentCharacter) || currentCharacter == '\n';
            final String s1 = StringUtils.getFormatFromString(subString) + stringInput.substring(stringSizeToWidth + (flag ? 1 : 0));
            return subString + '\n' + wrapFormattedStringToWidth(fontRenderer, s1, wrapWidth);
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
    private static int sizeStringToWidth(final FontRenderer fontRenderer, final String stringEntry, final int wrapWidth) {
        final int stringLength = stringEntry.length();
        int charWidth = 0;
        int currentLine = 0;
        int currentIndex = -1;

        for (boolean flag = false; currentLine < stringLength; ++currentLine) {
            final char currentCharacter = stringEntry.charAt(currentLine);
            switch (currentCharacter) {
                case '\n':
                    --currentLine;
                    break;
                case ' ':
                    currentIndex = currentLine;
                default:
                    charWidth += getCharWidth(fontRenderer, currentCharacter);
                    if (flag) {
                        ++charWidth;
                    }
                    break;
                case StringUtils.COLOR_CHAR:
                    if (currentLine < stringLength - 1) {
                        final char code = stringEntry.charAt(++currentLine);
                        if (code == 'l' || code == 'L') {
                            flag = true;
                        } else if (code == 'r' || code == 'R' || StringUtils.isFormatColor(code)) {
                            flag = false;
                        }
                    }
            }

            if (currentCharacter == '\n') {
                currentIndex = ++currentLine;
                break;
            }

            if (charWidth > wrapWidth) {
                break;
            }
        }

        return currentLine != stringLength && currentIndex != -1 && currentIndex < currentLine ? currentIndex : currentLine;
    }
}

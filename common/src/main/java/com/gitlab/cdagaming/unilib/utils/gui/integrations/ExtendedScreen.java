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

package com.gitlab.cdagaming.unilib.utils.gui.integrations;

import com.gitlab.cdagaming.unilib.ModUtils;
import com.gitlab.cdagaming.unilib.core.impl.screen.ScreenConstants;
import com.gitlab.cdagaming.unilib.utils.gui.RenderUtils;
import com.gitlab.cdagaming.unilib.utils.gui.controls.ExtendedButtonControl;
import com.gitlab.cdagaming.unilib.utils.gui.controls.ExtendedTextControl;
import com.gitlab.cdagaming.unilib.utils.gui.widgets.DynamicWidget;
import io.github.cdagaming.unicore.impl.Tuple;
import io.github.cdagaming.unicore.utils.MathUtils;
import io.github.cdagaming.unicore.utils.StringUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.List;

/**
 * An Extended and Globalized Gui Screen
 *
 * @author CDAGaming
 */
public class ExtendedScreen extends GuiScreen {
    /**
     * The Default Vertical Padding between elements, used in {@link ExtendedScreen#getButtonY(int)}
     */
    private static final int DEFAULT_ELEMENT_PADDING = 5;
    /**
     * The Default Height of an element, used in {@link ExtendedScreen#getButtonY(int)}
     */
    private static final int DEFAULT_ELEMENT_HEIGHT = 20;
    /**
     * The Last Used Control ID
     */
    private static int lastIndex = 0;
    /**
     * The Current Screen Instance
     */
    private final GuiScreen currentScreen;
    /**
     * Similar to buttonList, a list of compatible controls in this Screen
     */
    private final List<Gui> extendedControls = StringUtils.newArrayList();
    /**
     * Similar to buttonList, a list of compatible widgets in this Screen
     */
    private final List<DynamicWidget> extendedWidgets = StringUtils.newArrayList();
    /**
     * Similar to buttonList, a list of compatible ScrollLists in this Screen
     */
    private final List<GuiSlot> extendedLists = StringUtils.newArrayList();
    /**
     * The Screen Title, if any
     */
    private String title;
    /**
     * The Screen subtitle, if any
     */
    private String subTitle;
    /**
     * The Parent or Past Screen
     */
    private GuiScreen parentScreen;
    /**
     * The Current Screen Phase, used to define where in the initialization it is at
     */
    private Phase currentPhase = Phase.PREINIT;

    /**
     * The Screen's Current X coordinate position
     */
    private int screenX = 0;
    /**
     * The Screen's Current Y coordinate position
     */
    private int screenY = 0;

    /**
     * The Last Ticked Mouse X Coordinate
     */
    private int lastMouseX = 0;
    /**
     * The Last Ticked Mouse Y Coordinate
     */
    private int lastMouseY = 0;
    /**
     * The Last Ticked Scroll Delta
     */
    private int lastMouseScroll = 0;
    /**
     * The Content Height of all applicable widgets
     */
    private int contentHeight;
    /**
     * Whether this Screen can be closed by normal means, true by default
     */
    private boolean canClose;
    /**
     * Whether the mouse is currently within screen bounds
     */
    private boolean isOverScreen;

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param parentScreen The Parent Screen for this Instance
     * @param title        The Screen Title, if any
     * @param subTitle     The Screen subtitle, if any
     */
    public ExtendedScreen(final GuiScreen parentScreen, final String title, final String subTitle) {
        setGameInstance(ModUtils.INSTANCE_GETTER.get());
        setParent(parentScreen);
        currentScreen = this;
        setTitle(title);
        setSubTitle(subTitle);
        setCanClose(true);
        setContentHeight(0);
        setScreenSettings();
    }

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param parentScreen The Parent Screen for this Instance
     * @param title        The Screen Title, if any
     */
    public ExtendedScreen(final GuiScreen parentScreen, final String title) {
        this(parentScreen, title, null);
    }

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param parentScreen The Parent Screen for this Instance
     */
    public ExtendedScreen(final GuiScreen parentScreen) {
        this(parentScreen, null);
    }

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param title    The Screen Title, if any
     * @param subTitle The Screen subtitle, if any
     */
    public ExtendedScreen(final String title, final String subTitle) {
        this(null, title, subTitle);
    }

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param title The Screen Title, if any
     */
    public ExtendedScreen(final String title) {
        this(title, null);
    }

    /**
     * Initialization Event for this Control, assigning defined arguments
     */
    public ExtendedScreen() {
        this((GuiScreen) null);
    }

    /**
     * Retrieves the Next Available Button ID for use in the currently open Screen
     *
     * @return The next available Button ID
     */
    public static int getNextIndex() {
        return lastIndex++;
    }

    /**
     * Resets the Button Index to 0
     * Normally used when closing a screen and no longer using the allocated ID's
     */
    public static void resetIndex() {
        lastIndex = 0;
    }

    /**
     * Copies the Specified Text to the System's Clipboard
     *
     * @param input the text to interpret
     */
    public static void copyToClipboard(final String input) {
        setClipboardString(StringUtils.normalize(input));
    }

    /**
     * Retrieve The Detected Minecraft Protocol Version
     *
     * @return The Detected Minecraft Protocol Version
     */
    public static int getProtocol() {
        return ModUtils.MCProtocolID;
    }

    /**
     * Return a KeyCode, dependent on the LWJGL version
     *
     * @param lwjgl2Key The KeyCode to return if on LWJGL2
     * @param lwjgl3Key The KeyCode to return if on LWJGL3
     * @return the processed KeyCode
     */
    public static int getKeyByVersion(final int lwjgl2Key, final int lwjgl3Key) {
        return getProtocol() > 340 ? lwjgl3Key : lwjgl2Key;
    }

    /**
     * Whether the specified key is a valid escape key
     *
     * @param keyCode The KeyCode entered, if any
     * @return {@link Boolean#TRUE} if condition is satisfied
     */
    public static boolean isEscapeKey(final int keyCode) {
        return keyCode == getKeyByVersion(1, 256);
    }

    /**
     * Controls whether repeat events are reported or not. If repeat events
     * are enabled, key down events are reported when a key is pressed and held for
     * a certain amount of time (OS dependent).
     *
     * @param enable the new enable state
     */
    public static void enableRepeatEvents(final boolean enable) {
        Keyboard.enableRepeatEvents(enable);
    }

    /**
     * Pre-Initializes this Screen
     * <p>
     * Responsible for Setting preliminary data
     */
    @Override
    public void initGui() {
        // Clear Data before Initialization
        super.initGui();
        clearData();
        enableRepeatEvents(true);

        currentPhase = Phase.INIT;
        initializeUi();
        currentPhase = Phase.READY;
    }

    /**
     * Clear the Screen Data
     */
    public void clearData() {
        if (currentPhase != Phase.PREINIT) {
            currentPhase = Phase.PREINIT;
            setContentHeight(0);

            buttonList.clear();
            extendedControls.clear();
            extendedWidgets.clear();
            extendedLists.clear();
        }
    }

    /**
     * Initializes this Screen
     * <p>
     * Responsible for setting initial Data and creating controls
     */
    public void initializeUi() {
        if (currentPhase == Phase.PREINIT) {
            initGui();
            return;
        }
        if (currentPhase == Phase.INIT) {
            resetMouseScroll();

            for (Gui extendedControl : getControls()) {
                if (extendedControl instanceof ExtendedScreen extendedScreen) {
                    extendedScreen.initializeUi();
                }
            }

            refreshContentHeight();
        }
    }

    /**
     * Event to trigger upon Window Reload
     */
    public void reloadUi() {
        onResize(getGameInstance(), getScreenWidth(), getScreenHeight());
    }

    /**
     * Event to trigger upon Window Resize
     *
     * @param mcIn The Minecraft Instance
     * @param w    The New Screen Width
     * @param h    The New Screen Height
     */
    @Override
    public void onResize(@Nonnull Minecraft mcIn, int w, int h) {
        if (isLoaded()) {
            for (Gui extendedControl : getControls()) {
                if (extendedControl instanceof ExtendedScreen extendedScreen) {
                    extendedScreen.onResize(mcIn, w, h);
                }
            }
        }
        super.onResize(mcIn, w, h);
    }

    /**
     * Adds a Compatible Button to this Screen with specified type
     *
     * @param buttonIn The Button to add to this Screen
     * @param <T>      The Button's Class Type
     * @return The added button with attached class type
     */
    @Nonnull
    @Override
    protected <T extends GuiButton> T addButton(@Nonnull T buttonIn) {
        return addControl(buttonIn);
    }

    /**
     * Adds a Compatible Control to this Screen with specified type
     *
     * @param buttonIn The Control to add to this Screen
     * @param <T>      The Control's Class Type
     * @return The added control with attached class type
     */
    @Nonnull
    public <T extends Gui> T addControl(@Nonnull T buttonIn) {
        if (buttonIn instanceof DynamicWidget widget && !extendedWidgets.contains(buttonIn)) {
            addWidget(widget);
        }
        if (buttonIn instanceof GuiButton button && !buttonList.contains(buttonIn)) {
            buttonList.add(button);
        }
        if (!extendedControls.contains(buttonIn)) {
            extendedControls.add(buttonIn);
        }
        return buttonIn;
    }

    /**
     * Adds a Compatible Scroll List to this Screen with specified type
     *
     * @param buttonIn The Scroll List to add to this Screen
     * @param <T>      The Scroll List's Class Type
     * @return The added scroll list with attached class type
     */
    @Nonnull
    public <T extends GuiSlot> T addList(@Nonnull T buttonIn) {
        if (!extendedLists.contains(buttonIn)) {
            extendedLists.add(buttonIn);
        }
        return buttonIn;
    }

    /**
     * Adds a Compatible Control to this Screen with specified type
     *
     * @param buttonIn The Control to add to this Screen
     * @param <T>      The Control's Class Type
     * @return The added control with attached class type
     */
    @Nonnull
    public <T extends DynamicWidget> T addWidget(@Nonnull T buttonIn) {
        if (!extendedWidgets.contains(buttonIn)) {
            buttonIn.setControlPosY(getTop() + buttonIn.getTop());
            extendedWidgets.add(buttonIn);
        }
        return buttonIn;
    }

    /**
     * Pre-Preliminary Render Event, executes before preRender
     * <p>
     * Primarily used for rendering critical elements before other elements
     */
    public void renderCriticalData() {
        drawBackground(
                getLeft(), getRight(),
                getTop(), getBottom(),
                getOffset(), getTintFactor(),
                getScreenBackground()
        );
    }

    /**
     * Draws a Background onto a Gui, supporting RGBA Codes, Game Textures and Hexadecimal Colors
     *
     * @param left       The Left Position of the Object
     * @param right      The Right Position of the Object
     * @param top        The Top Position of the Object
     * @param bottom     The Bottom Position of the Object
     * @param offset     The vertical offset to render the background to
     * @param tintFactor The factor at which to tint the background to
     * @param u          The U Mapping Value
     * @param v          The V Mapping Value
     * @param data       The {@link ScreenConstants.ColorData} to be used to render the background
     */
    public void drawBackground(final double left, final double right,
                               final double top, final double bottom,
                               final double offset, float tintFactor,
                               final double u, final double v,
                               final ScreenConstants.ColorData data) {
        // Setup Colors + Tint Data
        tintFactor = MathUtils.clamp(tintFactor, 0.0f, 1.0f);
        final Color startColor = StringUtils.offsetColor(data.startColor(), tintFactor);
        final Color endColor = StringUtils.offsetColor(data.endColor(), tintFactor);

        if (StringUtils.isNullOrEmpty(data.texLocation())) {
            RenderUtils.drawGradient(left, right, top, bottom,
                    300.0F,
                    startColor, endColor
            );
        } else {
            final Tuple<Boolean, String, ResourceLocation> textureData = RenderUtils.getTextureData(
                    getGameInstance(),
                    data.texLocation()
            );
            final boolean usingExternalTexture = textureData.getFirst();
            final ResourceLocation texLocation = textureData.getThird();

            drawTexture(
                    left, right, top, bottom,
                    usingExternalTexture,
                    offset,
                    u, v,
                    startColor, endColor,
                    texLocation
            );
        }
    }

    /**
     * Draws a Background onto a Gui, supporting RGBA Codes, Game Textures and Hexadecimal Colors
     *
     * @param left       The Left Position of the Object
     * @param right      The Right Position of the Object
     * @param top        The Top Position of the Object
     * @param bottom     The Bottom Position of the Object
     * @param offset     The vertical offset to render the background to
     * @param tintFactor The factor at which to tint the background to
     * @param data       The {@link ScreenConstants.ColorData} to be used to render the background
     */
    public void drawBackground(final double left, final double right,
                               final double top, final double bottom,
                               final double offset, float tintFactor,
                               final ScreenConstants.ColorData data) {
        drawBackground(
                left, right, top, bottom,
                offset, tintFactor,
                left, top,
                data
        );
    }

    /**
     * Draws a Textured Rectangle, following the defined arguments
     *
     * @param left                 The Left Position of the Object
     * @param right                The Right Position of the Object
     * @param top                  The Top Position of the Object
     * @param bottom               The Bottom Position of the Object
     * @param usingExternalTexture Whether we are using a non-local/external texture
     * @param offset               The vertical offset to render the background to
     * @param u                    The U Mapping Value
     * @param v                    The V Mapping Value
     * @param startColorObj        The starting texture RGB data to interpret
     * @param endColorObj          The ending texture RGB data to interpret
     * @param texLocation          The game texture to render the object as
     */
    public void drawTexture(final double left, final double right,
                            final double top, final double bottom,
                            final boolean usingExternalTexture, final double offset,
                            final double u, final double v,
                            final Object startColorObj, final Object endColorObj,
                            final ResourceLocation texLocation) {
        RenderUtils.drawTexture(getGameInstance(),
                left, right, top, bottom,
                0.0D, usingExternalTexture,
                right - left, bottom - top,
                u, v + offset,
                32.0D, 32.0D,
                startColorObj, endColorObj,
                texLocation
        );
    }

    /**
     * Retrieve if the rendering environment is within a world
     *
     * @return {@link Boolean#TRUE} if condition is satisfied
     */
    public boolean hasWorld() {
        return getGameInstance().world != null;
    }

    /**
     * Retrieve the Screen Title, if any
     *
     * @return the Screen title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the Screen Title
     *
     * @param title the new title for the screen
     */
    public void setTitle(final String title) {
        this.title = title;
    }

    /**
     * Retrieve the Screen subtitle, if any
     *
     * @return the Screen subtitle
     */
    public String getSubTitle() {
        return subTitle;
    }

    /**
     * Sets the Screen subtitle
     *
     * @param subTitle the new subtitle for the screen
     */
    public void setSubTitle(final String subTitle) {
        this.subTitle = subTitle;
    }

    /**
     * Retrieve the factor at which to tint the background
     *
     * @return the current tint factor
     */
    public float getTintFactor() {
        return 1.0f;
    }

    /**
     * Retrieve the default background data
     *
     * @return the default background
     */
    public ScreenConstants.ColorData getScreenBackground() {
        return hasWorld() ?
                ScreenConstants.DEFAULT_ALT_SCREEN_BACKGROUND :
                ScreenConstants.DEFAULT_SCREEN_BACKGROUND;
    }

    /**
     * Retrieve the default tooltip data
     *
     * @param isTooltip Whether this is a normal tooltip
     * @return the default tooltip
     */
    public ScreenConstants.TooltipData getTooltipData(final boolean isTooltip) {
        return isTooltip ?
                ScreenConstants.getDefaultTooltip() :
                ScreenConstants.getEmptyTooltip();
    }

    /**
     * Retrieve the default tooltip data
     *
     * @return the default tooltip
     */
    public ScreenConstants.TooltipData getTooltipData() {
        return getTooltipData(true);
    }

    /**
     * Retrieve the amount to offset the background data by
     *
     * @return the offset for the background
     */
    public double getOffset() {
        return 0.0D;
    }

    /**
     * Preliminary Render Event, executes after drawBackground and before super event
     * <p>
     * Primarily used for preliminary element setup
     */
    public void preRender() {
        for (DynamicWidget widget : getWidgets()) {
            widget.preDraw(this);
        }
    }

    /**
     * Primary Widget Render event, executes after super event and before postRender
     * <p>
     * Primarily used for rendering title data and extra elements
     */
    public void renderExtra() {
        renderStringData();
        for (DynamicWidget widget : getWidgets()) {
            widget.draw(this);
        }
    }

    /**
     * Render any String Data, such as the Screen Title or other elements
     */
    public void renderStringData() {
        renderTitles(getTitle(), getSubTitle());
    }

    /**
     * Render Screen Title Data, if any is present
     *
     * @param title    The Screen title to interpret
     * @param subTitle The Screen subtitle to interpret
     */
    public void renderTitles(final String title, final String subTitle) {
        final boolean hasMainTitle = !StringUtils.isNullOrEmpty(title);
        final boolean hasSubTitle = !StringUtils.isNullOrEmpty(subTitle);
        if (hasMainTitle) {
            if (hasSubTitle) {
                renderScrollingString(
                        title,
                        30, 2,
                        getScreenWidth() - 30, 16,
                        0xFFFFFF
                );
                renderScrollingString(
                        subTitle,
                        30, 16,
                        getScreenWidth() - 30, 30,
                        0xFFFFFF
                );
            } else {
                renderScrollingString(
                        title,
                        30, 0,
                        getScreenWidth() - 30, 32,
                        0xFFFFFF
                );
            }
        }
    }

    /**
     * Post-Render event, executes at the end of drawScreen
     * <p>
     * Primarily used for rendering hover data
     */
    public void postRender() {
        for (DynamicWidget widget : getWidgets()) {
            widget.postDraw(this);
        }
    }

    @Override
    public void drawWorldBackground(int tint) {
        renderCriticalData();
    }

    /**
     * Renders this Screen, including controls and post-Hover Events
     *
     * @param mouseX       The Event Mouse X Coordinate
     * @param mouseY       The Event Mouse Y Coordinate
     * @param partialTicks The Rendering Tick Rate
     */
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // Ensures initialization events have run first, preventing an NPE
        if (isLoaded()) {
            preRender();

            RenderUtils.enableScissor(
                    getGameInstance(),
                    getLeft(),
                    getTop(),
                    getRight(),
                    getBottom()
            );

            drawDefaultBackground();

            for (GuiSlot listControl : getLists()) {
                if (listControl.getEnabled()) {
                    listControl.drawScreen(mouseX, mouseY, partialTicks);
                }
            }

            super.drawScreen(mouseX, mouseY, partialTicks);

            for (Gui extendedControl : getControls()) {
                if (extendedControl instanceof ExtendedTextControl textField) {
                    textField.drawTextBox();
                }
            }

            renderExtra();

            RenderUtils.disableScissor(getGameInstance());

            lastMouseX = mouseX;
            lastMouseY = mouseY;
            isOverScreen = RenderUtils.isMouseOver(mouseX, mouseY, this);

            for (Gui extendedControl : getControls()) {
                if (extendedControl instanceof ExtendedScreen extendedScreen) {
                    extendedScreen.drawScreen(mouseX, mouseY, partialTicks);
                }
            }

            postRender();
        }
    }

    /**
     * Event to trigger upon Mouse Input
     */
    @Override
    public void handleMouseInput() {
        if (isLoaded()) {
            setMouseScroll(Mouse.getEventDWheel());
            for (GuiSlot listControl : getLists()) {
                listControl.handleMouseInput();
            }

            final int dw = getMouseScroll();
            if (dw != 0) {
                mouseScrolled(getMouseX(), getMouseY(), (int) (dw / 60D));
            }
            super.handleMouseInput();
        }
    }

    /**
     * Event to trigger upon Mouse Input
     *
     * @param mouseX The Event Mouse X Coordinate
     * @param mouseY The Event Mouse Y Coordinate
     * @param wheelY The Event Mouse Wheel Delta
     */
    public void mouseScrolled(int mouseX, int mouseY, int wheelY) {
        if (isLoaded()) {
            for (Gui extendedControl : getControls()) {
                if (extendedControl instanceof ExtendedScreen extendedScreen) {
                    extendedScreen.mouseScrolled(mouseX, mouseY, wheelY);
                }
            }
        }
    }

    /**
     * Event to trigger upon Button Action, including onClick Events
     *
     * @param button The Button to trigger upon
     */
    @Override
    protected void actionPerformed(@Nonnull GuiButton button) {
        if (isOverScreen()) {
            if (button instanceof ExtendedButtonControl extendedButton) {
                extendedButton.onClick();
            }
            super.actionPerformed(button);
        }
    }

    /**
     * Event to trigger upon Typing a Key
     *
     * @param typedChar The typed Character, if any
     * @param keyCode   The KeyCode entered, if any
     */
    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (isLoaded()) {
            if (isEscapeKey(keyCode) && canClose()) {
                openScreen(getParent());
                return;
            }

            for (Gui extendedControl : getControls()) {
                if (extendedControl instanceof ExtendedTextControl textField) {
                    textField.textboxKeyTyped(typedChar, keyCode);
                }
                if (extendedControl instanceof ExtendedScreen extendedScreen) {
                    extendedScreen.keyTyped(typedChar, keyCode);
                }
            }
        }
    }

    /**
     * Event to trigger upon the mouse being clicked
     *
     * @param mouseX      The Event Mouse X Coordinate
     * @param mouseY      The Event Mouse Y Coordinate
     * @param mouseButton The Event Mouse Button Clicked
     */
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (isLoaded()) {
            for (Gui extendedControl : getControls()) {
                if (extendedControl instanceof ExtendedTextControl textField) {
                    textField.mouseClicked(mouseX, mouseY, mouseButton);
                }
                if (extendedControl instanceof ExtendedScreen extendedScreen) {
                    extendedScreen.mouseClicked(mouseX, mouseY, mouseButton);
                }
            }
            super.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int mouseButton, long timeSinceLastClick) {
        if (isLoaded()) {
            for (Gui extendedControl : getControls()) {
                if (extendedControl instanceof ExtendedScreen extendedScreen) {
                    extendedScreen.mouseClickMove(mouseX, mouseY, mouseButton, timeSinceLastClick);
                }
            }
            super.mouseClickMove(mouseX, mouseY, mouseButton, timeSinceLastClick);
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        if (isLoaded()) {
            for (Gui extendedControl : getControls()) {
                if (extendedControl instanceof ExtendedScreen extendedScreen) {
                    extendedScreen.mouseReleased(mouseX, mouseY, state);
                }
            }
            super.mouseReleased(mouseX, mouseY, state);
        }
    }

    /**
     * Event to trigger on each tick
     */
    @Override
    public void updateScreen() {
        if (isLoaded()) {
            for (Gui extendedControl : getControls()) {
                if (extendedControl instanceof ExtendedTextControl textField) {
                    textField.updateCursorCounter();
                }
                if (extendedControl instanceof ExtendedScreen extendedScreen) {
                    extendedScreen.updateScreen();
                }
            }
            super.updateScreen();
        }
    }

    /**
     * Event to trigger upon exiting the Gui
     */
    @Override
    public void onGuiClosed() {
        if (isLoaded()) {
            for (Gui extendedControl : getControls()) {
                if (extendedControl instanceof ExtendedScreen extendedScreen) {
                    extendedScreen.onGuiClosed();
                }
            }
            clearData();
            resetIndex();
            enableRepeatEvents(false);
        }
    }

    /**
     * Retrieve the Game Instance attached to this Screen
     *
     * @return the current game instance
     */
    public Minecraft getGameInstance() {
        return mc;
    }

    /**
     * Sets the Game Instance attached to this Screen
     *
     * @param instance the new game instance
     */
    public void setGameInstance(final Minecraft instance) {
        this.mc = instance;
    }

    /**
     * Adds a Scheduled/Queued Task to Display the Specified Gui Screen
     *
     * @param targetScreen The target Gui Screen to display
     */
    public void openScreen(final GuiScreen targetScreen) {
        RenderUtils.openScreen(getGameInstance(), targetScreen);
    }

    /**
     * Adds a Scheduled/Queued Task to Display the Specified Gui Screen
     *
     * @param targetScreen The target Gui Screen to display
     * @param parentScreen The parent screen instance to set, if possible
     * @param setParent    Whether to allow modifying the parent screen instance
     */
    public void openScreen(final ExtendedScreen targetScreen, final GuiScreen parentScreen, final boolean setParent) {
        RenderUtils.openScreen(getGameInstance(), targetScreen, parentScreen, setParent);
    }

    /**
     * Adds a Scheduled/Queued Task to Display the Specified Gui Screen
     *
     * @param targetScreen The target Gui Screen to display
     * @param parentScreen The parent screen instance to set, if possible
     */
    public void openScreen(final ExtendedScreen targetScreen, final GuiScreen parentScreen) {
        RenderUtils.openScreen(getGameInstance(), targetScreen, parentScreen);
    }

    /**
     * Adds a Scheduled/Queued Task to Display the Specified Gui Screen
     *
     * @param targetScreen The target Gui Screen to display
     */
    public void openScreen(final ExtendedScreen targetScreen) {
        openScreen(targetScreen, getInstance());
    }

    /**
     * Computes the current GUI scale. Calling this method is equivalent to the following:<pre><code>
     * Minecraft mc = Minecraft.getMinecraft();
     * int scale = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight).getScaleFactor();</code></pre>
     *
     * @return the current GUI scale
     */
    public int computeGuiScale() {
        return RenderUtils.computeGuiScale(getGameInstance());
    }

    /**
     * Calculate the Y Value for Buttons in a Standard-Sized Gui
     *
     * @param order Current Order of buttons above it, or 1 if none
     * @return The Calculated Y Value to place the Button at
     */
    public int getButtonY(final int order) {
        int topPosition = getTop();
        if (getInstance() instanceof ScrollPane pane) {
            topPosition += pane.getPadding();
        }
        return topPosition + (DEFAULT_ELEMENT_PADDING * (order + 1)) + (DEFAULT_ELEMENT_HEIGHT * order);
    }

    /**
     * Calculate the Y Value for Buttons in a Standard-Sized Gui, with an offset
     *
     * @param order         Current Order of buttons above it, or 1 if none
     * @param offset        The offset to append the original y value by
     * @param appendByOrder Whether to append the offset by the current order index
     * @return The Calculated Y Value to place the Button at, accounting for the offset
     */
    public int getButtonY(final int order, final int offset, final boolean appendByOrder) {
        return getButtonY(order) + (offset * (appendByOrder ? order : 1));
    }

    /**
     * Calculate the Y Value for Buttons in a Standard-Sized Gui, with an offset
     *
     * @param order  Current Order of buttons above it, or 1 if none
     * @param offset The offset to append the original y value by
     * @return The Calculated Y Value to place the Button at, accounting for the offset
     */
    public int getButtonY(final int order, final int offset) {
        return getButtonY(order, offset, false);
    }

    /**
     * Renders a Specified Multi-Line String, constrained by position and dimension arguments
     *
     * @param textToInput  The Specified Multi-Line String, split by lines into a list
     * @param posX         The starting X position to render the String
     * @param posY         The starting Y position to render the String
     * @param maxWidth     The maximum width to allow rendering to (Text will wrap if output is greater)
     * @param maxHeight    The maximum height to allow rendering to (Text will wrap if output is greater)
     * @param maxTextWidth The maximum width the output can be before wrapping
     * @param isCentered   Whether to render the text in a center-styled layout (Disabled if maxWidth is not specified)
     * @param isTooltip    Whether to render this layout in a tooltip-style (Issues may occur if combined with isCentered)
     * @param colorInfo    Color Data in the format of [renderTooltips,backgroundColorInfo,borderColorInfo]
     */
    public void drawMultiLineString(final List<String> textToInput, final int posX, final int posY, final int maxWidth, final int maxHeight, final int maxTextWidth, final boolean isCentered, final boolean isTooltip, final ScreenConstants.TooltipData colorInfo) {
        RenderUtils.drawMultiLineString(
                getGameInstance(),
                textToInput,
                posX, posY,
                maxWidth, maxHeight,
                maxTextWidth,
                getFontRenderer(),
                isCentered, isTooltip, colorInfo
        );
    }

    /**
     * Renders a Specified Multi-Line String, constrained by position and dimension arguments
     *
     * @param textToInput  The Specified Multi-Line String, split by lines into a list
     * @param posX         The starting X position to render the String
     * @param posY         The starting Y position to render the String
     * @param maxWidth     The maximum width to allow rendering to (Text will wrap if output is greater)
     * @param maxHeight    The maximum height to allow rendering to (Text will wrap if output is greater)
     * @param maxTextWidth The maximum width the output can be before wrapping
     * @param isCentered   Whether to render the text in a center-styled layout (Disabled if maxWidth is not specified)
     * @param isTooltip    Whether to render this layout in a tooltip-style (Issues may occur if combined with isCentered)
     */
    public void drawMultiLineString(final List<String> textToInput, final int posX, final int posY, final int maxWidth, final int maxHeight, final int maxTextWidth, final boolean isCentered, final boolean isTooltip) {
        drawMultiLineString(
                textToInput,
                posX, posY,
                maxWidth, maxHeight,
                maxTextWidth,
                isCentered, isTooltip,
                getTooltipData(isTooltip)
        );
    }

    /**
     * Renders a Specified Multi-Line String, constrained by position and dimension arguments
     *
     * @param textToInput  The Specified Multi-Line String, split by lines into a list
     * @param posX         The starting X position to render the String
     * @param posY         The starting Y position to render the String
     * @param maxWidth     The maximum width to allow rendering to (Text will wrap if output is greater)
     * @param maxHeight    The maximum height to allow rendering to (Text will wrap if output is greater)
     * @param maxTextWidth The maximum width the output can be before wrapping
     */
    public void drawMultiLineString(final List<String> textToInput, final int posX, final int posY, final int maxWidth, final int maxHeight, final int maxTextWidth) {
        drawMultiLineString(
                textToInput,
                posX, posY,
                maxWidth, maxHeight,
                maxTextWidth,
                false, true
        );
    }

    /**
     * Renders a Specified Multi-Line String, constrained by position and dimension arguments
     *
     * @param textToInput The Specified Multi-Line String, split by lines into a list
     * @param posX        The starting X position to render the String
     * @param posY        The starting Y position to render the String
     */
    public void drawMultiLineString(final List<String> textToInput, final int posX, final int posY) {
        drawMultiLineString(textToInput, posX, posY, getScreenWidth(), getScreenHeight(), getWrapWidth());
    }

    /**
     * Renders a Specified Multi-Line String, constrained by position and dimension arguments
     *
     * @param textToInput The Specified Multi-Line String, split by lines into a list
     */
    public void drawMultiLineString(final List<String> textToInput) {
        drawMultiLineString(textToInput, getMouseX(), getMouseY());
    }

    /**
     * Renders a String in the Screen, in the style of scrolling text
     *
     * @param message The text to render to the screen
     * @param centerX The center X position, used when not scrolling
     * @param minX    The minimum X position to render the text at
     * @param minY    The minimum Y position to render the text at
     * @param maxX    The maximum X position to render the text at
     * @param maxY    The maximum Y position to render the text at
     * @param color   The color to render the text in
     */
    public void renderScrollingString(final String message,
                                      final float centerX,
                                      final float minX, final float minY,
                                      final float maxX, final float maxY,
                                      final int color) {
        RenderUtils.renderScrollingString(
                getGameInstance(), getFontRenderer(),
                message, centerX,
                minX, minY,
                maxX, maxY,
                color
        );
    }

    /**
     * Renders a String in the Screen, in the style of scrolling text
     *
     * @param message The text to render to the screen
     * @param centerX The center X position, used when not scrolling
     * @param minX    The minimum X position to render the text at
     * @param minY    The minimum Y position to render the text at
     * @param maxX    The maximum X position to render the text at
     * @param maxY    The maximum Y position to render the text at
     * @param color   The color to render the text in
     */
    public void renderScrollingString(final String message,
                                      final int centerX,
                                      final int minX, final int minY,
                                      final int maxX, final int maxY,
                                      final int color) {
        RenderUtils.renderScrollingString(
                getGameInstance(), getFontRenderer(),
                message, centerX,
                minX, minY,
                maxX, maxY,
                color
        );
    }

    /**
     * Renders a String in the Screen, in the style of scrolling text
     *
     * @param message The text to render to the screen
     * @param minX    The minimum X position to render the text at
     * @param minY    The minimum Y position to render the text at
     * @param maxX    The maximum X position to render the text at
     * @param maxY    The maximum Y position to render the text at
     * @param color   The color to render the text in
     */
    public void renderScrollingString(final String message,
                                      final float minX, final float minY,
                                      final float maxX, final float maxY,
                                      final int color) {
        renderScrollingString(
                message, maxX - ((maxX - minX) / 2f),
                minX, minY,
                maxX, maxY,
                color
        );
    }

    /**
     * Renders a String in the Screen, in the style of scrolling text
     *
     * @param message The text to render to the screen
     * @param minX    The minimum X position to render the text at
     * @param minY    The minimum Y position to render the text at
     * @param maxX    The maximum X position to render the text at
     * @param maxY    The maximum Y position to render the text at
     * @param color   The color to render the text in
     */
    public void renderScrollingString(final String message,
                                      final int minX, final int minY,
                                      final int maxX, final int maxY,
                                      final int color) {
        renderScrollingString(
                message, maxX - ((maxX - minX) / 2),
                minX, minY,
                maxX, maxY,
                color
        );
    }

    /**
     * Renders a String in the Screen, in the style of centered text
     *
     * @param text  The text to render to the screen
     * @param xPos  The X position to render the text at
     * @param yPos  The Y position to render the text at
     * @param color The color to render the text in
     */
    public void renderCenteredString(final String text, final float xPos, final float yPos, final int color) {
        RenderUtils.renderCenteredString(getFontRenderer(), text, xPos, yPos, color);
    }

    /**
     * Renders a String in the Screen, in the style of centered text
     *
     * @param text  The text to render to the screen
     * @param xPos  The X position to render the text at
     * @param yPos  The Y position to render the text at
     * @param color The color to render the text in
     */
    public void renderCenteredString(final String text, final int xPos, final int yPos, final int color) {
        RenderUtils.renderCenteredString(getFontRenderer(), text, xPos, yPos, color);
    }

    /**
     * Renders a String in the Screen, in the style of centered text
     *
     * @param text  The text to render to the screen
     * @param yPos  The Y position to render the text at
     * @param color The color to render the text in
     */
    public void renderCenteredString(final String text, final float yPos, final int color) {
        renderCenteredString(text, getScreenWidth() / 2f, yPos, color);
    }

    /**
     * Renders a String in the Screen, in the style of centered text
     *
     * @param text  The text to render to the screen
     * @param yPos  The Y position to render the text at
     * @param color The color to render the text in
     */
    public void renderCenteredString(final String text, final int yPos, final int color) {
        renderCenteredString(text, getScreenWidth() / 2, yPos, color);
    }

    /**
     * Renders a String in the Screen, in the style of normal text
     *
     * @param text  The text to render to the screen
     * @param xPos  The X position to render the text at
     * @param yPos  The Y position to render the text at
     * @param color The color to render the text in
     */
    public void renderString(final String text, final float xPos, final float yPos, final int color) {
        RenderUtils.renderString(getFontRenderer(), text, xPos, yPos, color);
    }

    /**
     * Renders a String in the Screen, in the style of normal text
     *
     * @param text  The text to render to the screen
     * @param xPos  The X position to render the text at
     * @param yPos  The Y position to render the text at
     * @param color The color to render the text in
     */
    public void renderString(final String text, final int xPos, final int yPos, final int color) {
        renderString(text, (float) xPos, (float) yPos, color);
    }

    /**
     * Get the Width of a String from the FontRenderer
     *
     * @param string The string to interpret
     * @return the string's width from the font renderer
     */
    public int getStringWidth(final String string) {
        return RenderUtils.getStringWidth(getFontRenderer(), string);
    }

    /**
     * Format a section of strings to conform to the specified width
     *
     * @param original  The text to interpret
     * @param wrapWidth The width to wrap the text to
     * @return the modified lines
     */
    public List<String> createRenderLines(final List<String> original, final int wrapWidth) {
        final List<String> data = StringUtils.newArrayList();
        for (String line : original) {
            data.addAll(
                    RenderUtils.listFormattedStringToWidth(getFontRenderer(), line, wrapWidth)
            );
        }
        return data;
    }

    /**
     * Format a section of strings to conform to the specified width
     *
     * @param original  The text to interpret
     * @param wrapWidth The width to wrap the text to
     * @return the modified lines
     */
    public List<String> createRenderLines(final String original, final int wrapWidth) {
        return createRenderLines(
                StringUtils.splitTextByNewLine(original, true),
                wrapWidth
        );
    }

    /**
     * Get the wrap width for elements to be wrapped by
     * <p>Mostly used as a helper method for wrapping String elements
     *
     * @return the wrap width for elements to be wrapped by
     */
    public int getWrapWidth() {
        return -1;
    }

    /**
     * Get the Current Mouse's X Coordinate Position
     *
     * @return The Mouse's X Coordinate Position
     */
    public int getMouseX() {
        return lastMouseX;
    }

    /**
     * Get the Current Mouse's Y Coordinate Position
     *
     * @return The Mouse's Y Coordinate Position
     */
    public int getMouseY() {
        return lastMouseY;
    }

    /**
     * Get the Current Screen's X Coordinate Position
     *
     * @return The Screen's X Coordinate Position
     */
    public int getScreenX() {
        return screenX;
    }

    /**
     * Sets the Current Screen's X Coordinate Position
     *
     * @param screenX the new X position for the screen
     */
    public void setScreenX(final int screenX) {
        this.screenX = screenX;
    }

    /**
     * Get the Current Screen's Y Coordinate Position
     *
     * @return The Screen's Y Coordinate Position
     */
    public int getScreenY() {
        return screenY;
    }

    /**
     * Sets the Current Screen's Y Coordinate Position
     *
     * @param screenY the new Y position for the screen
     */
    public void setScreenY(final int screenY) {
        this.screenY = screenY;
    }

    /**
     * Get whether the mouse is currently within screen bounds
     *
     * @return {@link Boolean#TRUE} is condition is satisfied
     */
    public boolean isOverScreen() {
        return isOverScreen;
    }

    /**
     * Get the Current Mouse's Scroll Delta
     *
     * @return The Mouse's Current Scroll Delta
     */
    public int getMouseScroll() {
        return lastMouseScroll;
    }

    /**
     * Sets the Current Mouse's Scroll Delta
     *
     * @param mouseScroll the new scroll delta
     */
    public void setMouseScroll(final int mouseScroll) {
        this.lastMouseScroll = mouseScroll;
    }

    /**
     * Resets the Current Mouse's Scroll Delta
     */
    public void resetMouseScroll() {
        setMouseScroll(0);
    }

    /**
     * Get the Maximum Viewable Screen Width
     *
     * @return the maximum view width of the screen
     */
    public int getMaxWidth() {
        return getScreenWidth();
    }

    /**
     * Applies any additional Screen Settings
     */
    public void setScreenSettings() {
        // N/A
    }

    /**
     * Get the Current Screen Width
     *
     * @return the width of the screen
     */
    public int getScreenWidth() {
        return width;
    }

    /**
     * Sets the Current Screen Width
     *
     * @param screenWidth the new screen width
     */
    public void setScreenWidth(final int screenWidth) {
        this.width = screenWidth;
    }

    /**
     * Retrieve the Content Height of all applicable widgets
     *
     * @return the Content Height of all applicable widgets
     */
    public int getContentHeight() {
        return contentHeight;
    }

    /**
     * Sets the Content Height of all applicable widgets
     *
     * @param contentHeight the Content Height of all applicable widgets
     */
    public void setContentHeight(int contentHeight) {
        this.contentHeight = contentHeight;
    }

    /**
     * Refresh the Content Height of all applicable widgets
     */
    public void refreshContentHeight() {
        setContentHeight(0);
        for (DynamicWidget widget : getWidgets()) {
            final int widgetHeight = widget.getBottom();
            if (widgetHeight > getContentHeight()) {
                setContentHeight(widgetHeight);
            }
        }
    }

    /**
     * Get the Current Screen Height
     *
     * @return the height of the screen
     */
    public int getScreenHeight() {
        return height;
    }

    /**
     * Sets the Current Screen Height
     *
     * @param screenHeight the new screen height
     */
    public void setScreenHeight(final int screenHeight) {
        this.height = screenHeight;
    }

    /**
     * Get the Current Font Renderer for this Screen
     *
     * @return The Current Font Renderer for this Screen
     */
    public FontRenderer getFontRenderer() {
        return getGameInstance().fontRenderer != null ? getGameInstance().fontRenderer : RenderUtils.getDefaultFontRenderer();
    }

    /**
     * Get the Current Font Height for this Screen
     *
     * @return The Current Font Height for this Screen
     */
    public int getFontHeight() {
        return RenderUtils.getFontHeight(getFontRenderer());
    }

    /**
     * Get the left-most coordinate for this screen
     *
     * @return The left-most coordinate for this screen
     */
    public int getLeft() {
        return getScreenX();
    }

    /**
     * Get the right-most coordinate for this screen
     *
     * @return The right-most coordinate for this screen
     */
    public int getRight() {
        return getScreenX() + getScreenWidth();
    }

    /**
     * Get the bottom-most coordinate for this screen
     *
     * @return The bottom-most coordinate for this screen
     */
    public int getBottom() {
        return getScreenY() + getScreenHeight();
    }

    /**
     * Get the top-most coordinate for this screen
     *
     * @return The top-most coordinate for this screen
     */
    public int getTop() {
        return getScreenY();
    }

    /**
     * Retrieve the current screen instance
     *
     * @return the current screen instance
     */
    public GuiScreen getInstance() {
        return currentScreen;
    }

    /**
     * Retrieve the parent or past screen instance
     *
     * @return the parent or past screen instance
     */
    public GuiScreen getParent() {
        return parentScreen;
    }

    /**
     * Sets the parent screen instance
     *
     * @param parentScreen the new parent screen
     */
    public void setParent(final GuiScreen parentScreen) {
        this.parentScreen = parentScreen;
    }

    /**
     * Retrieve whether this Screen can be closed by normal means
     *
     * @return Whether this Screen can be closed by normal means
     */
    public boolean canClose() {
        return canClose;
    }

    /**
     * Sets whether this Screen can be closed by normal means
     *
     * @param canClose the new "canClose" status
     */
    public void setCanClose(final boolean canClose) {
        this.canClose = canClose;
    }

    /**
     * Retrieve the list of compatible controls in this Screen
     *
     * @return the list of compatible controls in this Screen
     */
    public List<Gui> getControls() {
        return StringUtils.newArrayList(extendedControls);
    }

    /**
     * Retrieve the list of compatible widgets in this Screen
     *
     * @return the list of compatible widgets in this Screen
     */
    public List<DynamicWidget> getWidgets() {
        return StringUtils.newArrayList(extendedWidgets);
    }

    /**
     * Retrieve the list of compatible ScrollLists in this Screen
     *
     * @return the list of compatible ScrollLists in this Screen
     */
    public List<GuiSlot> getLists() {
        return StringUtils.newArrayList(extendedLists);
    }

    /**
     * Gets whether the Screen is fully loaded
     *
     * @return {@link Boolean#TRUE} if condition is satisfied
     */
    public boolean isLoaded() {
        return currentPhase == Phase.READY;
    }

    /**
     * Constants representing various Screen Phase statuses
     */
    public enum Phase {
        /**
         * Defines that the Screen is either unloaded, or has not finished pre-initialization
         */
        PREINIT,
        /**
         * Defines that the Screen has completed initial construction and data preparation
         */
        INIT,
        /**
         * Defines that the Screen is fully ready for use
         */
        READY
    }
}
